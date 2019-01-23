package cloud.antelope.lingmou.mvp.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.antelope.sdk.ACMediaInfo;
import com.antelope.sdk.ACMessageListener;
import com.antelope.sdk.ACMessageType;
import com.antelope.sdk.ACResult;
import com.antelope.sdk.ACResultListener;
import com.antelope.sdk.capturer.ACFrame;
import com.antelope.sdk.capturer.ACSampleFormat;
import com.antelope.sdk.capturer.ACShape;
import com.antelope.sdk.codec.ACCodecID;
import com.antelope.sdk.codec.ACStreamPacket;
import com.antelope.sdk.player.ACPlayer;
import com.antelope.sdk.player.ACPlayerExtra;
import com.antelope.sdk.player.ACPlayerStatus;
import com.antelope.sdk.streamer.ACProtocolType;
import com.antelope.sdk.streamer.ACStreamer;
import com.antelope.sdk.streamer.ACStreamerFactory;
import com.antelope.sdk.utils.WorkThreadExecutor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.http.imageloader.glide.ImageConfigImpl;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.modle.LoadingDialog;
import com.lingdanet.safeguard.common.net.RequestUtils;
import com.lingdanet.safeguard.common.utils.Configuration;
import com.lingdanet.safeguard.common.utils.DeviceUtil;
import com.lingdanet.safeguard.common.utils.FileUtils;
import com.lingdanet.safeguard.common.utils.ImageUtils;
import com.lingdanet.safeguard.common.utils.LogUtils;
import com.lingdanet.safeguard.common.utils.NetworkUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.SizeUtils;
import com.lingdanet.safeguard.common.utils.TimeUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.lingdanet.safeguard.common.utils.Utils;
import com.lingdanet.safeguard.common.utils.WaterMarkUtils;

import org.simple.eventbus.EventBus;

import java.io.File;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.LogUploadUtil;
import cloud.antelope.lingmou.app.utils.gson.ListHistoryTypeAdapter;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.di.component.DaggerPlayerNewComponent;
import cloud.antelope.lingmou.di.module.PlayerNewModule;
import cloud.antelope.lingmou.mvp.contract.PlayerNewContract;
import cloud.antelope.lingmou.mvp.model.entity.CameraNewStreamEntity;
import cloud.antelope.lingmou.mvp.model.entity.CollectCameraEntity;
import cloud.antelope.lingmou.mvp.model.entity.CollectChangeBean;
import cloud.antelope.lingmou.mvp.model.entity.CollectRequest;
import cloud.antelope.lingmou.mvp.model.entity.EmptyEntity;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreEntity;
import cloud.antelope.lingmou.mvp.model.entity.HistoryKVStoreRequest;
import cloud.antelope.lingmou.mvp.model.entity.LogUploadRequest;
import cloud.antelope.lingmou.mvp.model.entity.NotWifiNoticeChangedEvent;
import cloud.antelope.lingmou.mvp.model.entity.PlayEntity;
import cloud.antelope.lingmou.mvp.model.entity.RecordLyCameraBean;
import cloud.antelope.lingmou.mvp.model.entity.SetKeyStoreRequest;
import cloud.antelope.lingmou.mvp.model.entity.VideoClickBean;
import cloud.antelope.lingmou.mvp.model.entity.VideosBean;
import cloud.antelope.lingmou.mvp.presenter.PlayerNewPresenter;
import cloud.antelope.lingmou.mvp.ui.widget.MutilColorDrawable;
import cloud.antelope.lingmou.mvp.ui.widget.MyHorizontalScrollView;
import cloud.antelope.lingmou.mvp.ui.widget.ScaleView;
import cloud.antelope.lingmou.mvp.ui.widget.SnapshotDialog;
import cloud.antelope.lingmou.mvp.ui.widget.dialog.VideoHistoryDialog;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;


import static cloud.antelope.lingmou.app.EventBusTags.CAMERA_COUNT_CHANGE;
import static cloud.antelope.lingmou.app.EventBusTags.CAMERA_STATUS_CHANGE;
import static com.jess.arms.utils.Preconditions.checkNotNull;


public class PlayerNewActivity extends BaseActivity<PlayerNewPresenter> implements PlayerNewContract.View, View.OnClickListener {

    static final int TOTAL_SPAN = 86400;

    static final int PLAY_TYPE_OF_LIVE = 0x01;
    static final int PLAY_TYPE_OF_RECORD = 0x02;
    static final int PLAY_TYPE_OF_EVENT = 0x03;

    private static final int TYPE_BACK_CONTRAL = 0x10;
    //播放器
    private ACPlayer mPlayer;
    //流化器
    private ACStreamer mStreamer;
    //流传输协议
    private int mACProtocol;
    //形状
    private int mShape;
    private int mStartPlayBufferSize;//最小缓冲时长
    private int mStartDropBufferSize;//最大缓冲时长
    private int mVideoCodecId;//视频解码类型
    private int mAudioCodecId;//音频解码类型
    private int mSampleSize;//音频播放采样率
    private int mChannel;//声道
    private WorkThreadExecutor mWorkThreadExecutor;

    private ACResult mResult;
    private boolean isInit = false;
    private String mPlayAddrDomain, mPlayAddr, mPlayLiveAddr, mCameraName, mCameraSn;
    private boolean mIsPlay, mHasRecord;
    private int mPlayType;
    private long mCameraId;
    //播放器UI相关
    private SurfaceView mPlayerView;
    private LinearLayout ll4gTip;
    private TextView mPlayerNameTV, mBitrateTV, mNetWorkNoticeTV;
    private ImageView mExitFullscreenIV, mCoverIV;
    private ImageView mSoundIV, mBackRecordIV, mBackLiveIV, mCollectIv, mCaptureIv;
    private RelativeLayout mRecordHorizontalLayout;
    private LinearLayout mPlayFailedLayout, mTopLayout, mSlideLl, mToolLl;
    //录像时间轴
    private MyHorizontalScrollView mTimeLineHorizontalView;

    //    private RecyclerView mDateRecyleView;
    private ScaleView mScaleView;
    private View mVideoStateView;
    private ImageView mFaceIv, mBodyIv;

    //录像时间轴滑动时时间显示
    private TextView mCurrentTimeTV;

    private TextView mFailRefreshTv;
    private PopupWindow mPopupWindow;

    //录像搜索相关
    private long mTabCurrentTime;//当前seek时间点
    private long mCurrentSelFrom, mCurrentSelTo;//开始查询起始和结束时间点
    private long mEventFrom, mEventTo;
    private Date mRecordCurrentDate;
    private String mSegment;
    private NetworkUtils.NetworkType mNetworkType;

    private boolean isShowTopAndBotton = false;
    private LoadingDialog mLoadingDialog;
    private boolean mPaused;
    private Handler mHandler;
    private ImageView mScaleBackground;
    //    private PlayerDateAdapter mPlayerDateAdapter;
    private boolean mIsPlayOrFail = false;

    private float mDistanceTime = 0.0f;    //单f位距离多少毫秒
    private int mStartScrollX = 0;
    private int mTotal; //时间轴总长度
    private boolean mIsPlayComplete;
    private String mCoverUrl;

    private Handler mBackHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mIsPlayOrFail = true;
        }
    };
    private boolean mIsRecording;
    private String mToken;
    private int mPrePosition;
    private int mHalfScreen;
    private int mLastScrollx = 0; //上一次滑动的距离
    private float mDensityFloat;
    private int mDensity;
    private CheckBox mForward_progress;
    private CheckBox mBack_progress;
    private TextView mProgressTextView;
    // 照相声音播放
    private MediaPlayer mMediaPlayer;
    private static final String mStoragePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    private ArrayList<CollectCameraEntity> mCollectionCameras;
    private SetKeyStoreRequest mKeyStore;
    private SetKeyStoreRequest mCurrentRequest;
    private boolean mCollected;
    private boolean mIsEvent;
    private boolean mIsHistory;
    List<Date> mDateList;
    List<VideosBean> mSections;

    private ImageLoader mImageLoader;
    /**
     * 图片最大边界
     */
    public static final int[] MAX_IMAGE_BOUNDS = new int[]{1920, 1080};
    private Bitmap mLandscapeMarkerBitmap;
    private String savePath;
    private VideoHistoryDialog historyDialog;
    private long downTime, upTime;
    private TimerTask downTask, upTask;
    private boolean notWifiNotice;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerPlayerNewComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .playerNewModule(new PlayerNewModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_player_new; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        //保持屏幕常亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //隐藏状态栏 方法一
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        notWifiNotice = SPUtils.getInstance().getBoolean(Constants.CONFIG_NOT_WIFI_SWITCH, false);
        mIsEvent = getIntent().getBooleanExtra("isEvent", false);
        mIsHistory = getIntent().getBooleanExtra("isHistory", false);

        mImageLoader = ArmsUtils.obtainAppComponentFromContext(this).imageLoader();
        mLoadingDialog = new LoadingDialog(this);
        mWorkThreadExecutor = new WorkThreadExecutor("PlayerNewActivity");
        mWorkThreadExecutor.start(null);
        mNetworkType = NetworkUtils.getNetworkType();
        mHandler = new Handler(Looper.getMainLooper());

        mCameraId = getIntent().getLongExtra("cameraId", 0);
        mCameraName = getIntent().getStringExtra("cameraName");
        mCameraSn = getIntent().getStringExtra("cameraSn");
        mCoverUrl = getIntent().getStringExtra("coverUrl");
        mPlayAddrDomain = SPUtils.getInstance().getString(Constants.URL_RECORD_PLAY) + "?connectType=2&cid=" + mCameraId + "&protocolType=3";
        // mPlayAddrDomain = "topvdn://public.topvdn.cn?connectType=2&cid=" + mCameraId + "&protocolType=3";
        if (mIsEvent) {
            mPlayType = PLAY_TYPE_OF_EVENT;
            long captureTime = getIntent().getLongExtra("captureTime", -1);
            if (captureTime > 10000000000L) {
                //证明是13位，毫秒
                mEventFrom = captureTime / 1000L - 15L;
            } else {
                mEventFrom = captureTime - 15L;
            }

            mEventTo = mEventFrom + 30L;
            LogUploadUtil.uploadLog(new LogUploadRequest(103900, 103902, String.format("查看点位【%s】%s到%s的录像", mCameraName, TimeUtils.millis2String(mEventFrom * 1000), TimeUtils.millis2String(mEventTo * 1000))));
        } else {
            if (mIsHistory) {
                mPlayType = PLAY_TYPE_OF_RECORD;
            } else {
                mPlayType = PLAY_TYPE_OF_LIVE;
                LogUploadUtil.uploadLog(new LogUploadRequest(103900, 103901, String.format("查看点位【%s】的实时视频", mCameraName)));
            }
        }
        mDateList = getDateList(7);
        mRecordCurrentDate = mDateList.get(mPrePosition);
        mBackHandler.sendEmptyMessageDelayed(TYPE_BACK_CONTRAL, 2000);
        if (mPlayType != PLAY_TYPE_OF_EVENT) {
            mMediaPlayer = MediaPlayer.create(this, R.raw.music_image_cut_);
            //判断是否已收藏
            String storeValue = SPUtils.getInstance().getString(Constants.COLLECT_JSON);
            if (!TextUtils.isEmpty(storeValue)) {
                Gson gson = new Gson();
                mKeyStore = gson.fromJson(storeValue, SetKeyStoreRequest.class);
                List<String> storeSets = mKeyStore.getSets();
                mCollectionCameras = new ArrayList<>();
                for (String str : storeSets) {
                    CollectCameraEntity entity = new CollectCameraEntity();
                    String[] group_one = str.split(":");
                    entity.setGroup(group_one[0]);
                    String[] group_two = group_one[1].split("/");
                    String cid = group_two[0];
                    if ((mCameraId + "").equals(cid)) {
                        mCollected = true;
                    }
                    entity.setCid(cid);
                    entity.setCameraName(group_two[1]);
                    if (!mCollectionCameras.contains(entity)) {
                        mCollectionCameras.add(entity);
                    }
                }
            } else {
                mCollected = false;
            }
            mPresenter.getHistories();
        }
        initTheView();
        initUI();
        /*boolean hasSystemFeature = getApplicationContext().getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
        if (hasSystemFeature) {
            //有刘海屏，则增加margin
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mTopLayout.getLayoutParams();
            layoutParams.leftMargin = 80;
            mTopLayout.setLayoutParams(layoutParams);
        }*/
    }

    public void setCollected(boolean isCollect) {
        mCollected = isCollect;
        int resId = isCollect ? R.drawable.cancel_follow : R.drawable.add_follow_yellow;
        mCollectIv.setImageResource(resId);
    }

    private void playLiveUI() {
        mPlayType = PLAY_TYPE_OF_LIVE;
        mSegment = null;
        mPlayAddr = mPlayLiveAddr;
        if (null != mBackRecordIV) {
            mBackRecordIV.setEnabled(mHasRecord);
            mBackRecordIV.setVisibility(View.VISIBLE);
        }
        if (null != mBackLiveIV) {
            mBackLiveIV.setVisibility(View.GONE);
        }
        if (null != mRecordHorizontalLayout) {
            mRecordHorizontalLayout.setVisibility(View.GONE);
        }
        mPlayFailedLayout.setVisibility(View.GONE);
        mNetWorkNoticeTV.setVisibility(View.GONE);
        mVideoStateView.setBackgroundResource(R.drawable.blue_dot_bg);
    }

    private void playRecordUI() {
        mPlayType = PLAY_TYPE_OF_RECORD;
        //        mPlayProgressBar.setVisibility(View.VISIBLE);
        // mProgress.show();
        if (null != mBackRecordIV) {
            mBackRecordIV.setVisibility(View.GONE);
        }
        if (null != mBackLiveIV) {
            mBackLiveIV.setVisibility(View.VISIBLE);
        }
        if (null != mRecordHorizontalLayout) {
            mRecordHorizontalLayout.setVisibility(View.VISIBLE);
        }
        mPlayFailedLayout.setVisibility(View.GONE);
        mNetWorkNoticeTV.setVisibility(View.GONE);
        mVideoStateView.setBackgroundResource(R.drawable.yellow_dot_bg);
    }

    private void playEventUI() {
        mPlayType = PLAY_TYPE_OF_EVENT;
        mToolLl.setVisibility(View.INVISIBLE);
        if (null != mRecordHorizontalLayout) {
            mRecordHorizontalLayout.setVisibility(View.GONE);
        }
        mPlayFailedLayout.setVisibility(View.GONE);
        mNetWorkNoticeTV.setVisibility(View.GONE);
        mVideoStateView.setBackgroundResource(R.drawable.yellow_dot_bg);
    }

    private void initTheView() {
        mIsPlay = false;
        mHasRecord = true;

        //顶部半透明条
        mTopLayout = findViewById(R.id.layout_top);
        mPlayerNameTV = findViewById(R.id.tv_player_name);
        mBitrateTV = findViewById(R.id.tv_bitrate);
        mExitFullscreenIV = findViewById(R.id.tv_exit_fullscreen);
        //播放器相关
        mPlayerView = findViewById(R.id.playerview);
        mPlayFailedLayout = findViewById(R.id.layout_play_failed);
        mNetWorkNoticeTV = findViewById(R.id.tv_network_notice);
        ll4gTip = findViewById(R.id.ll_4g_tip);
        ll4gTip.setOnClickListener(this);
        findViewById(R.id.tv_play_anyway).setOnClickListener(this);

        mFailRefreshTv = findViewById(R.id.fail_retry_tv);
        mCurrentTimeTV = findViewById(R.id.tv_player_status);

        mCoverIV = findViewById(R.id.cover_view);
        if (!TextUtils.isEmpty(mCoverUrl)) {
            mImageLoader.loadImage(this, ImageConfigImpl
                    .builder()
                    .cacheStrategy(0)
                    .url(mCoverUrl)
                    .imageView(mCoverIV)
                    .build());
        }
        mToolLl = findViewById(R.id.tool_ll);

        mSlideLl = findViewById(R.id.slide_ll);
        mVideoStateView = findViewById(R.id.video_status_view);

        mFaceIv = findViewById(R.id.video_face_iv);
        mBodyIv = findViewById(R.id.video_body_iv);
        mFaceIv.setOnClickListener(this);
        mBodyIv.setOnClickListener(this);

        mDensityFloat = getResources().getDisplayMetrics().density;
        mDensity = Math.round(mDensityFloat);
        //进度缩略图
        View progress_root = LayoutInflater.from(this).inflate(R.layout.popwindow_progress, null);
        mPopupWindow = new PopupWindow(progress_root, 250 * mDensity, 35 * mDensity);
        mForward_progress = progress_root.findViewById(R.id.progress_back);
        mBack_progress = progress_root.findViewById(R.id.progress_forward);
        mProgressTextView = progress_root.findViewById(R.id.progress_time);

        mPlayerNameTV.setText(mCameraName == null ? "" : mCameraName);
        mNetWorkNoticeTV.setVisibility(View.GONE);
        mPlayFailedLayout.setVisibility(View.GONE);

        mHalfScreen = getResources().getDisplayMetrics().widthPixels / 2;

        if (mPlayType == PLAY_TYPE_OF_LIVE) {
            //直播
            playLiveUI();
        } else if (mPlayType == PLAY_TYPE_OF_RECORD) {
            //录像
            playRecordUI();
        } else if (mPlayType == PLAY_TYPE_OF_EVENT) {
            //事件
            playEventUI();
        }

        //设置监听器
        mExitFullscreenIV.setOnClickListener(this);
        // mPlayFailedIV.setOnClickListener(this);
        mPlayerView.setOnClickListener(this);
        mFailRefreshTv.setOnClickListener(this);
    }

    /**
     * 根据滑动距离计算时间戳 单位:秒
     *
     * @param scrollX 滑动距离
     *                特别要注意精度损失问题
     */
    public long getTimeStamp(int scrollX) {
        return (mCurrentSelFrom / 1000 + (long) ((scrollX - mStartScrollX) * mDistanceTime));
    }

   /* private void resetItemColor(int prePosition) {
        View viewByPosition = mPlayerDateAdapter.getViewByPosition(prePosition, R.id.date_tv);
        if (null != viewByPosition) {
            viewByPosition.setSelected(false);
        }
    }*/

    private List<Date> getDateList(int day) {
        List<Date> dateList = new ArrayList<>();
        long currentTimeMillis = System.currentTimeMillis();
        for (int i = 0; i < day; i++) {
            Date date = TimeUtils.millis2Date(currentTimeMillis - i * TOTAL_SPAN * 1000);
            dateList.add(date);
        }
        return dateList;
    }

    /**
     * seek
     * 时间是 毫秒
     */
    private void seek() {
        mIsPlay = false;
        mPlayer.clearQueueBuffer();
        mStreamer.seek(mTabCurrentTime * 1000, new ACResultListener() {
            @Override
            public void onResult(final ACResult acResult) {
                LogUtils.i("PlayerActivity seek result:" + acResult.getCode());
                if (acResult.isResultOK()) {
                    LogUtils.d("beck", "tab current time:seek===" + mTabCurrentTime);
                    mPlayer.setStartPlayingTimestamp(mTabCurrentTime * 1000);
                    play();
                } else {
                    playFailed();
                }
            }
        });
    }


    private void initUI() {
        if (mPlayType == PLAY_TYPE_OF_LIVE) {
            //直播
            playLiveUI();
            if (!TextUtils.isEmpty(mPlayLiveAddr)) {
                startPlay();
            } else {
                mPresenter.getStreamUrl(mCameraId);
            }
        } else if (mPlayType == PLAY_TYPE_OF_RECORD) { //录像
            playRecordUI();
            initRecordTime(mRecordCurrentDate);
        } else if (mPlayType == PLAY_TYPE_OF_EVENT) {
            playEventUI();
            mPresenter.getRecords(mCameraId, mEventFrom, mEventTo);
        }
    }

    private void startPlay() {
        //如果当前摄像机详情为空或正在播放 不做任何处理
        if (mIsPlay || TextUtils.isEmpty(mPlayAddr)) {
            playFailed();
            return;
        }

        //隐藏播放失败提示，显示加载圈
        mPlayFailedLayout.setVisibility(View.GONE);
        //        mPlayProgressBar.setVisibility(View.VISIBLE);
        // mProgress.show();

        //        isInit = false;
        //初始化播放器和流化器
        if (!isInit) {
            isInit = initTheData();
        }
        //开流并播放
        if (isInit) {
            openStreamer();
        } else {
            //初始化失败 隐藏progressbar  显示加载失败按钮
            //            mPlayProgressBar.setVisibility(View.GONE);
            mLoadingDialog.dismiss();
            LogUtils.i("init failed");
            mPlayFailedLayout.setVisibility(View.VISIBLE);
        }
    }


    private boolean initTheData() {
        mShape = ACShape.AC_SHAPE_NONE;
        if (mPlayType == PLAY_TYPE_OF_LIVE) {
            mACProtocol = ACProtocolType.AC_PROTOCOL_QSTP;
        } else {
            mACProtocol = ACProtocolType.AC_PROTOCOL_OSTP;
        }
        if (mACProtocol == ACProtocolType.AC_PROTOCOL_QSTP) {
            mStartPlayBufferSize = 1000;
            mStartDropBufferSize = 5000;
        } else {
            mStartPlayBufferSize = 0;
            mStartDropBufferSize = 0;
        }

        mVideoCodecId = ACCodecID.AC_CODEC_ID_H264;
        mAudioCodecId = ACCodecID.AC_CODEC_ID_AAC;
        mSampleSize = 16000;

        mChannel = 1;

        mPlayer = new ACPlayer();
        //初始化
        ACPlayer.Config config = new ACPlayer.Config.Builder()
                .setStartPlayBufferSize(mStartPlayBufferSize)
                .setStartDropBufferSize(mStartDropBufferSize)
                .setVideoCodecId(mVideoCodecId)
                .setAudioCodecId(mAudioCodecId)
                .setSampleRate(mSampleSize)
                .setChannelCount(mChannel)
                .setSampleFormat(ACSampleFormat.AC_SAMPLE_FMT_S16)
                .create();

        //解码模式在初始化后可随时动态设置切换
        mResult = mPlayer.initialize(config, mPlayerExtra);
        if (!mResult.isResultOK()) {
            return false;
        }

        //设置surfaceview以及播放器形状
        mPlayer.setPlaySurfaceView(mPlayerView, mShape);
        mStreamer = ACStreamerFactory.createStreamer(mACProtocol);
        if (mStreamer == null) {
            return false;
        }
        LogUtils.i("create streamer " + mStreamer);
        mResult = mStreamer.initialize(PlayerNewActivity.this, new ACMessageListener() {
            @Override
            public void onMessage(int type, Object message) {
                switch (type) {
                    case ACMessageType.AC_MESSAGE_DISCONNECTED:
                        LogUtils.v("qstp was disconnected!");
                        break;
                    case ACMessageType.AC_MESSSAGE_RECONNECTED:
                        LogUtils.v("qstp was reconnected successfully");
                        break;
                    case ACMessageType.AC_MESSAGE_START_PLAY_TIME:
                        mPlayer.setStartPlayingTimestamp(Long.parseLong(message.toString()));
                        LogUtils.v("play time " + message.toString());
                        break;
                    default:
                        if (message != null)
                            LogUtils.v(message);
                        break;
                }
            }
        });
        if (!mResult.isResultOK()) {
            return false;
        }
        return true;
    }

    private void openStreamer() {
        if (NetworkUtils.isMobileConnected() && notWifiNotice) {
            ll4gTip.setVisibility(View.VISIBLE);
            mLoadingDialog.dismiss();
            return;
        }
        if (mPlayType == PLAY_TYPE_OF_RECORD) {
            mPlayAddr = String.format(mPlayAddrDomain + "&token=%s&begin=%s&end=%s&play=%s",
                    mToken, mCurrentSelFrom / 1000, mCurrentSelTo / 1000, mTabCurrentTime);
        } else if (mPlayType == PLAY_TYPE_OF_EVENT) {
            mPlayAddr = String.format(mPlayAddrDomain + "&token=%s&begin=%s&end=%s&play=%s",
                    mToken, mEventFrom, mEventTo, mEventFrom);
        } else {
            mPlayAddr = mPlayLiveAddr;
        }
        LogUtils.i("LL--player mplayUrl " + mPlayAddr + " segment:" + mSegment);
        //        mPlayAddr = "topvdn://public.topvdn.cn?protocolType=2&connectType=2&token=10000025_3356491776_1499599851_8f7a86313cf622981f995e549e812547";
        mStreamer.open(mPlayAddr, 5000, mSegment, new ACResultListener() {

                    @Override
                    public void onResult(final ACResult status) {
                        if (isFinishing()) return;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                LogUtils.i("openStreamer code:" + status.getCode());
                                if (status.isResultOK()) {
                                    play();
                                    if (!mIsPlayOrFail) {
                                        mIsPlayOrFail = true;
                                    }
                                    if (mPlayType == PLAY_TYPE_OF_LIVE) {
                                        nowTime();
                                    }
                                } else {
                                    isInit = false;
                                    LogUtils.i("openstreamer failed");
                                    playFailed();
                                }
                            }
                        });
                    }
                }
        );
    }

    public void nowTime() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCurrentTimeTV.postDelayed(nowTask, 1000);
            }
        });
    }


    private Runnable nowTask = new Runnable() {
        @Override
        public void run() {
            if (mIsPlay) {
                mCurrentTimeTV.setText("直播 | " + TimeUtils.millis2String(System.currentTimeMillis() - 1000l, "MM/dd " + TimeUtils.LONG_TIME_FORMAT) + "| ");
                mCurrentTimeTV.postDelayed(this, 1000);
            }
        }
    };

    private void play() {
        if (isFinishing()) return;
        mIsPlay = true;
        mIsPlayComplete = false;
        if (mPlayType == PLAY_TYPE_OF_RECORD && mTabCurrentTime != 0 && mSections != null) {
            long end = 0L;
            for (int i = 0; i < mSections.size(); i++) {
                if (mSections.get(i).getBegin() <= mTabCurrentTime && mSections.get(i).getEnd() >= mTabCurrentTime) {
                    end = mSections.get(i).getEnd();
                    break;
                }
            }
            LogUploadUtil.uploadLog(new LogUploadRequest(103900, 103902, String.format("查看点位【%s】%s到%s的录像", mCameraName, TimeUtils.millis2String(mTabCurrentTime * 1000), TimeUtils.millis2String(end * 1000))));
        }
        mWorkThreadExecutor.executeTask(new Runnable() {
            public void run() {
                int code = 0;
                while (mIsPlay) {
                    final ACStreamPacket packet = new ACStreamPacket();
                    packet.buffer = ByteBuffer.allocateDirect(1024 * 1024);
                    //读取数据
                    ACResult readResult = mStreamer.read(packet, 1000);
                    if (readResult.getCode() != code) {
                        code = readResult.getCode();
                    }

                    String videoCacheInfo = mPlayer.getMediaInfo(ACMediaInfo.AC_MEDIA_INFO_PLAYER_VIDEO_CACHE);
                    String bufferTimeInfo = mPlayer.getMediaInfo(ACMediaInfo.AC_MEDIA_INFO_PLAYER_BUFFER_TIME);
                    // LogUtils.w("cxm", "code = "+readResult.getCode()+"videoCacheInfo  = " + videoCacheInfo + ",bufferTimeInfo=" + bufferTimeInfo + ",delayTimeInfo=" + delayTimeInfo);
                    if (!mIsPlayComplete && readResult.getCode() == ACResult.ACC_OSTP_CHANNEL_CLOSE && "0".equals(videoCacheInfo) && "0".equals(bufferTimeInfo)) {
                        LogUtils.i("cxm", "video complete");
                        mIsPlayComplete = true;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showShort(R.string.media_play_complete);
                            }
                        });
                    }
                    if (readResult.getCode() == ACResult.ACC_QSTP_READ_FAILED) {
                        mIsPlay = false;
                        //读取失败表示连接已断开
                        if (mHandler != null)
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    LogUtils.i("play playfrailed");
                                    playFailed();
                                }
                            });
                    } else if (readResult.isResultOK()) {
                        ACResult result = mPlayer.playFrame(packet);
                    }

                }
                mIsPlay = false;
            }
        });
    }

    private void playFailed() {
        mIsPlay = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLoadingDialog.dismiss();
                mPlayFailedLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * 播放器扩展接口，可获取解码前/后的数据,可进行自定义操作
     */
    ACPlayerExtra mPlayerExtra = new ACPlayerExtra() {
        /**
         * 视频渲染之前和音频播放之前都会调用 （数据已解码） 自行判断区分音视频
         */
        @Override
        public ACResult onDecodePacket(ACStreamPacket packet, ACFrame frame) {
            return new ACResult(ACResult.ACS_UNIMPLEMENTED, "");
        }

        /**
         * 音视频解码前调用 packet 未解码的数据 frame 放解码后的数据
         */
        @Override
        public ACResult onProcessFrame(ACFrame frame) {
            return new ACResult(ACResult.ACS_UNIMPLEMENTED, "");
        }

        @Override
        public void onPlayerStatus(int status) {
            super.onPlayerStatus(status);
            if (isFinishing()) {
                return;
            }
            if (status == ACPlayerStatus.ACPlayerStatusStartPlay) {

                if ((mPlayType == PLAY_TYPE_OF_EVENT || mPlayType == PLAY_TYPE_OF_RECORD) && mIsPlay) {
                    LogUtils.d("beck", "start run progress runnable");
                    mHandler.post(recordProgressRunnable);
                }
                mHandler.postDelayed(bitrateRunnable, 1000);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (null != mSoundIV) {
                            mSoundIV.setSelected(true);
                        }
                        mLoadingDialog.dismiss();
                        mCoverIV.setVisibility(View.GONE);
                        mPlayFailedLayout.setVisibility(View.GONE);
                    }
                });
            }
        }
    };

    //更新码率
    Runnable bitrateRunnable = new Runnable() {
        @Override
        public void run() {
            if (mIsPlay) {
                mBitrateTV.setText(mStreamer.getMediaInfo(ACMediaInfo.AC_MEDIA_INFO_DOWNLOAD_VIDEO_BITRATE) + " KB/s");

                //网络监测
                NetworkUtils.NetworkType netType = NetworkUtils.getNetworkType();
                if (netType != mNetworkType) {
                    if (netType == NetworkUtils.NetworkType.NETWORK_NO || netType == NetworkUtils.NetworkType.NETWORK_UNKNOWN) {
                        mNetWorkNoticeTV.setText("网络连接已断开");
                    } else if (netType == NetworkUtils.NetworkType.NETWORK_WIFI) {
                        mNetWorkNoticeTV.setText("网络已切换至WiFi");
                    } else if (netType == NetworkUtils.NetworkType.NETWORK_2G || netType == NetworkUtils.NetworkType.NETWORK_3G || netType == NetworkUtils.NetworkType.NETWORK_4G) {
                        mNetWorkNoticeTV.setText("您正在使用2G/3G/4G网络观看");
                    } else {
                        mNetWorkNoticeTV.setText("网络已切换至未知网络");
                    }
                    mNetworkType = netType;
                    mNetWorkNoticeTV.setVisibility(View.VISIBLE);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mNetWorkNoticeTV.setVisibility(View.GONE);
                        }
                    }, 2000);

                    //如果是录像且当前播放帧的时间戳不是时间轴的最后一段的时间戳，且播放器缓存队列没有数据，则播放失败，否则播放完毕
                    // if (mPlayer.getMediaInfo(ACMediaInfo.AC_MEDIA_INFO_PLAYER_VIDEO_CACHE).equals("0")) {
                    //     ToastUtils.showShort(R.string.media_play_complete);
                    // }
                }
                if (mHandler != null)
                    mHandler.postDelayed(bitrateRunnable, 1000);
            }
        }
    };


    //初始化搜索起始时间
    private void initRecordTime(Date date) {
        // 初始化当前日期
        mRecordCurrentDate = date;
        String currentDayStr = TimeUtils.millis2String(date, "yyyy-MM-dd ");//传过来的日期
        String nowDayStr = TimeUtils.millis2String(System.currentTimeMillis(), "yyyy-MM-dd ");//当前日期
        mCurrentSelFrom = TimeUtils.string2Millis(currentDayStr + "00:00:00");//传过来的日期零点
        if (nowDayStr.equals(currentDayStr)) {
            //两个相等，证明是今天
            mCurrentSelTo = System.currentTimeMillis();
        } else {
            mCurrentSelTo = TimeUtils.string2Millis(currentDayStr + "23:59:59");
        }
        //当前时间的前5分钟
        /*if (mTabCurrentTime != 0) {
            String currentTimeStr = TimeUtils.millis2String(mCurrentSelTo - 60000 * 5, "HH:mm:ss");
            mTabCurrentTime = TimeUtils.string2Millis(currentDayStr + currentTimeStr);
            ToastUtils.showLong("mTabCurrentTime: "+TimeUtils.date2String(new Date(mTabCurrentTime), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));
            LogUtils.d("beck", "tab current time:init record time===" + mTabCurrentTime);
        }*/
        initSection();
    }

    //循环存储时间轴
    public void initSection() {
        // equalCount = 0;
        mPresenter.getRecords(mCameraId, mCurrentSelFrom / 1000, mCurrentSelTo / 1000);
        LogUploadUtil.uploadLog(new LogUploadRequest(103900, 103902, String.format("查看点位【%s】%s到%s的录像", mCameraName, TimeUtils.millis2String(mCurrentSelFrom), TimeUtils.millis2String(mCurrentSelTo))));
    }

    /**
     * 录像播放进度回调,每秒一次
     * 播放器返回的时间戳是 毫秒
     */
    Runnable recordProgressRunnable = new Runnable() {
        @Override
        public void run() {
            if (mIsPlay) {
                //更新录像进度条
                mTabCurrentTime = mPlayer.getPosition() / 1000;
                String recordTimeStr = TimeUtils.millis2String(mTabCurrentTime * 1000, "MM/dd HH:mm:ss");
                mCurrentTimeTV.setText("回放 | " + recordTimeStr + "| ");
                int distance = getScrollDistance(mTabCurrentTime);
                ScrollToHorizationView(distance);
                mHandler.postDelayed(recordProgressRunnable, 1000);
            }
        }
    };

    @Override
    public void showLoading(String message) {
        mLoadingDialog.show();
    }

    @Override
    public void hideLoading() {
        // mLoadingDialog.dismiss();
    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ArmsUtils.snackbarText(message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {
        finish();
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        switch (vId) {
            case R.id.tv_play_anyway:
                ll4gTip.setVisibility(View.GONE);
                notWifiNotice = false;
                SPUtils.getInstance().put(Constants.CONFIG_NOT_WIFI_SWITCH, false);
                EventBus.getDefault().post(new NotWifiNoticeChangedEvent());
                startPlay();
                break;
            case R.id.ll_menu:
                if (historyDialog == null) {
                    historyDialog = new VideoHistoryDialog(PlayerNewActivity.this);
                    historyDialog.setOnTimeConfirmListener(new VideoHistoryDialog.OnTimeConfirmListener() {
                        @Override
                        public void onTimeConfirm(Date date) {
                            mRecordCurrentDate = date;
                            stopPlay();
                            initRecordTime(mRecordCurrentDate);
                            mTabCurrentTime = date.getTime() / 1000;

                        }
                    });
                }
                historyDialog.show();
                break;
            case R.id.tv_exit_fullscreen:
                if (mIsRecording) {
                    ToastUtils.showShort("正在录制视频，请稍后");
                } else if (mIsPlayOrFail) {
                    finish();
                }
                break;
            case R.id.iv_sound:
                //静音
                if (null != mPlayer) {
                    if (mSoundIV.isSelected()) {
                        mSoundIV.setSelected(false);
                        mPlayer.mute();
                    } else {
                        mSoundIV.setSelected(true);
                        mPlayer.unmute();
                    }
                }
                break;
            case R.id.iv_back_live:
                mPlayType = PLAY_TYPE_OF_LIVE;
                mCoverIV.setVisibility(View.VISIBLE);
                mLoadingDialog.show();
                stopPlay();
                initUI();
                LogUploadUtil.uploadLog(new LogUploadRequest(103900, 103901, String.format("查看点位【%s】的实时视频", mCameraName)));
                break;
            case R.id.iv_back_record:
//                boolean hasVideoHistory = SPUtils.getInstance().getBoolean(Constants.PERMISSION_HAS_VIDEO_HISTORY, false);
//                if (hasVideoHistory) {
                stopPlay();
                initStubUI();
                playRecordUI();
                Timber.e("初始时间：" + TimeUtils.date2String(mRecordCurrentDate, new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss")));
                initRecordTime(mRecordCurrentDate);
                mCoverIV.setVisibility(View.VISIBLE);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
//                            View view = mPlayerDateAdapter.getViewByPosition(mPrePosition, R.id.date_tv);
//                            view.setSelected(true);
                    }
                });
//                } else {
//                    ToastUtils.showShort(R.string.hint_no_permission);
//                }

                break;
            case R.id.fail_retry_tv:
                mLoadingDialog.show();
                stopPlay();
                initUI();
                break;
            case R.id.playerview:
                initSideStubUI();
                setCollected(mCollected);
                showHideTopAndBotton();
                break;
            case R.id.capture_video:
                if (null != mPlayer) {
                    snapshotCapture();
                }
                break;
            case R.id.collect_video:
                collectAction();
                break;
            case R.id.video_face_iv:
                if (SPUtils.getInstance().getBoolean(Constants.PERMISSION_HAS_FACE)) {
                    Intent faceIntent = new Intent(PlayerNewActivity.this, FaceDepotActivity.class);
                    faceIntent.putExtra("fromDevice", true);
                    faceIntent.putExtra("fromVideo", true);
                    faceIntent.putExtra("cameraId", mCameraId);
                    PlayerNewActivity.this.startActivity(faceIntent);
                } else {
                    ToastUtils.showShort(getString(R.string.hint_no_permission));
                }
                break;
            case R.id.video_body_iv:
                if (SPUtils.getInstance().getBoolean(Constants.PERMISSION_HAS_BODY)) {
                    Intent bodyIntent = new Intent(PlayerNewActivity.this, BodyDepotActivity.class);
                    bodyIntent.putExtra("fromDevice", true);
                    bodyIntent.putExtra("fromVideo", true);
                    bodyIntent.putExtra("cameraId", mCameraId);
                    PlayerNewActivity.this.startActivity(bodyIntent);
                } else {
                    ToastUtils.showShort(getString(R.string.hint_no_permission));
                }
                break;
        }
    }

    private void snapshotCapture() {
//        if (!SPUtils.getInstance().getBoolean(Constants.PERMISSION_HAS_VIDEO_CAPTURE)) {
//            ToastUtils.showShort(R.string.hint_no_permission);
//        } else {
        if (EasyPermissions.hasPermissions(PlayerNewActivity.this, mStoragePermission)) {
            mMediaPlayer.start();
            mCaptureIv.setEnabled(false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mCaptureIv.setEnabled(true);
                }
            }, 200);
            //截图
//                String photoDir = Configuration.getRootPath() + Constants.SNAP_PATH;
            File dir = new File(Configuration.getCacheDirectoryPath());
            if (!dir.exists()) {
                dir.mkdirs();
            }
            final File picFile = new File(dir, System.currentTimeMillis() + "_" + mCameraSn + ".jpeg");
            snapShot(picFile.getAbsolutePath(), new IOnSnapListener() {
                @Override
                public void snapSuccess() {
                    SnapshotDialog dialog = new SnapshotDialog(PlayerNewActivity.this);
                    dialog.show(picFile.getAbsolutePath());
                    dialog.setOnClickListener(new SnapshotDialog.OnClickListener() {
                        @Override
                        public void onClickCollect() {
                            savePath = picFile.getAbsolutePath();
                            collectSnapshot();
                        }

                        @Override
                        public void onClickDownload() {
                            savePath = Configuration.getRootPath() + Constants.SNAP_PATH + picFile.getName();
                            FileUtils.copyFile(picFile.getAbsolutePath(), savePath);
                            DeviceUtil.galleryAddMedia(savePath);
                            ToastUtils.showShort(getString(R.string.save_image_success));
                        }
                    });
                }

                @Override
                public void snapFail(String msg) {

                }
            });
        } else {
            ToastUtils.showShort("请前往设置打开存储权限");
        }
//        }
    }

    private void collectSnapshot() {
        CollectRequest request = new CollectRequest();
        request.setDeviceId(mCameraId);
        request.setDeviceName(mCameraName);
        request.setFavoritesType(1);

        File file = new File(savePath);
        MultipartBody.Part part = null;
        if (FileUtils.isFileExists(file)) {
            part = RequestUtils.prepareFilePart("file", file.getAbsolutePath());
        }
        EmptyEntity data = new EmptyEntity();
        RequestBody metadata = RequestUtils.createPartFromString(new Gson().toJson(data));
        mPresenter.collect(request, String.valueOf(file.length()), metadata, part);
    }

    private void collectAction() {
        if (mCollected) {
            //取消收藏
            Iterator<CollectCameraEntity> iterator = mCollectionCameras.iterator();
            while (iterator.hasNext()) {
                CollectCameraEntity next = iterator.next();
                if ((mCameraId + "").equals(next.getCid())) {
                    //干掉这个对象
                    iterator.remove();
                }
            }
            List<String> groups;
            if (null != mKeyStore) {
                groups = mKeyStore.getGroups();
            } else {
                groups = new ArrayList<>();
            }
            Iterator<String> groupIterator = groups.iterator();
            while (groupIterator.hasNext()) {
                String group = groupIterator.next();
                boolean hasGroup = false;
                for (CollectCameraEntity entity : mCollectionCameras) {
                    if (group.equals(entity.getGroup())) {
                        hasGroup = true;
                        break;
                    }
                }
                if (!hasGroup) {
                    groupIterator.remove();
                }
            }
            List<String> collectionCameras = new ArrayList<>();
            for (CollectCameraEntity entity : mCollectionCameras) {
                String camera = entity.getGroup() + ":" + entity.getCid() + "/" + entity.getCameraName();
                collectionCameras.add(camera);
            }
            mCurrentRequest = new SetKeyStoreRequest();
            mCurrentRequest.setGroups(groups);
            mCurrentRequest.setSets(collectionCameras);
            mPresenter.cameraLike("MY_GROUP", mCurrentRequest);
        } else {
            //进行收藏
            List<String> groups;
            if (null != mKeyStore) {
                groups = mKeyStore.getGroups();
            } else {
                groups = new ArrayList<>();
            }
            Iterator<String> groupIterator = groups.iterator();
            boolean hsAppGroup = false;
            while (groupIterator.hasNext()) {
                String group = groupIterator.next();
                if ("app".equals(group)) {
                    //证明有app的分组，则直接塞入app分组内
                    hsAppGroup = true;
                }
            }
            if (!hsAppGroup) {
                groups.add("app");
            }
            if (null == mCollectionCameras) {
                mCollectionCameras = new ArrayList<>();
            }
            CollectCameraEntity collectCameraEntity = new CollectCameraEntity();
            collectCameraEntity.setCid(mCameraId + "");
            collectCameraEntity.setCameraName(mCameraName);
            collectCameraEntity.setGroup("app");
            mCollectionCameras.add(collectCameraEntity);

            List<String> collectionCameras = new ArrayList<>();
            for (CollectCameraEntity entity : mCollectionCameras) {
                String camera = entity.getGroup() + ":" + entity.getCid() + "/" + entity.getCameraName();
                collectionCameras.add(camera);
            }
            mCurrentRequest = new SetKeyStoreRequest();
            mCurrentRequest.setGroups(groups);
            mCurrentRequest.setSets(collectionCameras);
            mPresenter.cameraLike("MY_GROUP", mCurrentRequest);
        }
    }


    public interface IOnSnapListener {

        void snapSuccess();

        void snapFail(String msg);
    }

    public void snapShot(final String path, IOnSnapListener listener) {
        if (!TextUtils.isEmpty(path) && null != mPlayer) {
            ACResult acResult = mPlayer.snapshot(path);
            if (acResult.isResultOK()) {
                if (FileUtils.isFileExists(path)) {
                    ImageView view = (ImageView) LayoutInflater.from(Utils.getContext()).inflate(R.layout.layout_water_mark, null, false);
                    WaterMarkUtils.layoutView(view, MAX_IMAGE_BOUNDS[0], MAX_IMAGE_BOUNDS[1]);
                    view.setBackground(Drawable.createFromPath(path));
                    if (null == mLandscapeMarkerBitmap) {
                        String realName = SPUtils.getInstance().getString(Constants.CONFIG_LAST_USER_REAL_NAME);
                        String phoneNum = SPUtils.getInstance().getString(Constants.CONFIG_LAST_USER_LOGIN_PHONE);
                        mLandscapeMarkerBitmap = WaterMarkUtils.getLandscapeMarkerBitmapMultiLine(realName + "\n" + phoneNum, MAX_IMAGE_BOUNDS[0], MAX_IMAGE_BOUNDS[1]);
                    }
                    view.setImageBitmap(mLandscapeMarkerBitmap);
                    Bitmap bitmap = WaterMarkUtils.loadBitmapFromView(view);
                    Observable.just(bitmap).map(new Function<Bitmap, Boolean>() {
                        @Override
                        public Boolean apply(Bitmap bitmap) throws Exception {
                            return ImageUtils.save(bitmap, path, Bitmap.CompressFormat.JPEG, 300, false);
                        }
                    }).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<Boolean>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onNext(Boolean aBoolean) {

                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onComplete() {

                                }
                            });

                    listener.snapSuccess();
                } else {
                    listener.snapFail("截图保存文件时失败");
                }
            } else {
                // listener.snapFail("errorCode:" + acResult.getCode() + ",errorCodeDes:" + acResult.getCodeDesc() + ",errorMsg:" + acResult.getErrMsg());
                listener.snapFail("截图失败");
            }

        }
    }

    private void initSideStubUI() {
        mSoundIV = findViewById(R.id.iv_sound);
        //初始化相关控件
        mSoundIV.setSelected(true);
        mBackRecordIV = findViewById(R.id.iv_back_record);
        mBackLiveIV = findViewById(R.id.iv_back_live);
        mCollectIv = findViewById(R.id.collect_video);
        mCaptureIv = findViewById(R.id.capture_video);
        mSoundIV.setOnClickListener(this);
        mBackRecordIV.setOnClickListener(this);
        mBackLiveIV.setOnClickListener(this);
        mCaptureIv.setOnClickListener(this);
        mCollectIv.setOnClickListener(this);
    }

    private void initStubUI() {
        if (null == mRecordHorizontalLayout) {
            ((ViewStub) findViewById(R.id.record_view_stub)).inflate();
            findViewById(R.id.ll_left).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            downTime = System.currentTimeMillis();
                            downTask = new TimerTask() {
                                @Override
                                public void run() {
                                    onLeftPress(true);
                                }
                            };
                            new Timer().schedule(downTask, 1100);

                            if (System.currentTimeMillis() - upTime < 800) {
                                upTask.cancel();
                            }
                            break;
                        case MotionEvent.ACTION_CANCEL:
                        case MotionEvent.ACTION_UP:
                            if (System.currentTimeMillis() - downTime < 1000) {
                                downTask.cancel();
                                mTabCurrentTime -= 120 * 60;
                                ScrollToHorizationView(getScrollDistance(mTabCurrentTime));
                                stopPlay();
                                upTime = System.currentTimeMillis();
                                upTask = new TimerTask() {
                                    @Override
                                    public void run() {
                                        onTimerScroll(getScrollDistance(mTabCurrentTime), false);
                                    }
                                };
                                new Timer().schedule(upTask, 1000);
                            } else {
                                onLeftPress(false);
                            }
                            break;
                    }
                    return true;
                }
            });
            findViewById(R.id.ll_right).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            downTime = System.currentTimeMillis();
                            downTask = new TimerTask() {
                                @Override
                                public void run() {
                                    onRightPress(true);
                                }
                            };
                            new Timer().schedule(downTask, 1100);

                            if (System.currentTimeMillis() - upTime < 800) {
                                upTask.cancel();
                            }
                            break;
                        case MotionEvent.ACTION_CANCEL:
                        case MotionEvent.ACTION_UP:
                            if (System.currentTimeMillis() - downTime < 1000) {
                                downTask.cancel();
                                mTabCurrentTime += 120 * 60;
                                ScrollToHorizationView(getScrollDistance(mTabCurrentTime));
                                stopPlay();
                                upTime = System.currentTimeMillis();
                                upTask = new TimerTask() {
                                    @Override
                                    public void run() {
                                        onTimerScroll(getScrollDistance(mTabCurrentTime), false);
                                    }
                                };
                                new Timer().schedule(upTask, 1000);
                            } else {
                                onRightPress(false);
                            }
                            break;
                    }
                    return true;
                }
            });


            findViewById(R.id.ll_menu).setOnClickListener(this);
            mRecordHorizontalLayout = findViewById(R.id.record_stub_rl);
            //时间轴
            mTimeLineHorizontalView = findViewById(R.id.my_horizontal_view);
//            mDateRecyleView = findViewById(R.id.date_recy_view);
            mScaleBackground = findViewById(R.id.scale_bg);
            mScaleView = findViewById(R.id.horizontalScale);
            //初始化录像进度条
            mTimeLineHorizontalView.setCanScroll(true);
            mTimeLineHorizontalView.setSmoothScrollingEnabled(true);
            mTimeLineHorizontalView.setOnScrollByUserListener(new MyHorizontalScrollView.OnScrollByUserListener() {
                @Override
                public void onScroll(int scrollX, boolean isOnTouch) {
                    onTimerScroll(scrollX, isOnTouch);
                }
            });

//            mDateRecyleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
//            mPlayerDateAdapter = new PlayerDateAdapter();
//            mDateRecyleView.setAdapter(mPlayerDateAdapter);
//            mPlayerDateAdapter.setNewData(mDateList);
//            mPlayerDateAdapter.bindToRecyclerView(mDateRecyleView);
//            mPlayerDateAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
//                @Override
//                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                    if (mPrePosition == position) {
//                        return;
//                    }
//                    resetItemColor(mPrePosition);
//                    View itemView = adapter.getViewByPosition(position, R.id.date_tv);
//                    itemView.setSelected(true);
//                    mPrePosition = position;
//                    mRecordCurrentDate = (Date) adapter.getItem(position);
//                    stopPlay();
//                    initRecordTime(mRecordCurrentDate);
//                }
//            });

            //之所以这样是为了减少dp在不同设备上造成的精度损失
            int scale_margin = (SizeUtils.getScreenWidth() - getResources().getDimensionPixelSize(R.dimen.dp120)) / (12 * 5);
            //xml 里面的数据只作为参考
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mScaleBackground.getLayoutParams();
            params.width = scale_margin * 720; // 720表示刻度的数目
            params.leftMargin = mHalfScreen;
            mScaleBackground.setLayoutParams(params);

            int scale_sum_real = scale_margin * (720); //总长度(以这个为准，xml里面制作参考,避免dp在不同设备上造成的精度损失）
            mTotal = scale_sum_real;
            mDistanceTime = 24 * 60 * 60 / (mTotal * 1.0f); // 秒/像素
            mStartScrollX = 0;
        }

    }

    private boolean leftPress;
    private boolean rightPress;
    private Runnable left;
    private Runnable right;

    private void onTimerScroll(int scrollX, boolean isOnTouch) {
        mHandler.removeCallbacks(recordProgressRunnable);
        long mTimeStamp = getTimeStamp(scrollX);
                /*if ((mTimeStamp - 1) == mCurrentSelTo /1000) {
                    mTimeStamp = mCurrentSelTo /1000;
                }*/
        long finalMTimeStamp = mTimeStamp;
        // mTabCurrentTime = mTimeStamp;
        if (!isOnTouch) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mPopupWindow.dismiss();
                    mLoadingDialog.show();
                    mPlayFailedLayout.setVisibility(View.GONE);
                }
            });
            mIsPlay = false;

            if (mTimeStamp >= mCurrentSelTo / 1000) {
                mTabCurrentTime = mCurrentSelTo / 1000 - 300;
            } else {
                mTabCurrentTime = mTimeStamp;
            }
            LogUtils.d("beck", "tab current time:scroll by user 2===" + mTabCurrentTime);
            LogUtils.i("seek isinit:" + isInit);
            if (!isInit) {
                stopPlay();
                startPlay();
            } else {
                seek();
            }
        } else {
            if (mPopupWindow.isShowing()) {
                //seek时间显示
                if (scrollX - mLastScrollx >= 1) {
                    mForward_progress.setChecked(false);
                    mBack_progress.setChecked(true);
                }

                if (mLastScrollx - scrollX >= 1) {
                    mForward_progress.setChecked(true);
                    mBack_progress.setChecked(false);
                }
                mLastScrollx = scrollX;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressTextView.setText(TimeUtils.millis2String(finalMTimeStamp * 1000));
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPopupWindow.showAtLocation(mPlayerView, Gravity.NO_GRAVITY, mHalfScreen - 100 * mDensity, 90 * mDensity);
                    }
                });
            }
        }
    }

    private void onLeftPress(boolean leftP) {
        stopPlay();
        this.leftPress = leftP;
        if (left == null) {
            left = new Runnable() {
                @Override
                public void run() {
                    if (!leftPress) {
                        mHandler.removeCallbacks(this);
                        onTimerScroll(getScrollDistance(mTabCurrentTime), false);
                    } else {
                        mTabCurrentTime -= 30;
                        ScrollToHorizationView(getScrollDistance(mTabCurrentTime));
                        onTimerScroll(getScrollDistance(mTabCurrentTime), true);
//                        seek();
                        mHandler.postDelayed(this, 10);
                    }
                }
            };
        }
        mHandler.postDelayed(left, 10);
    }

    private void onRightPress(boolean rightP) {
        stopPlay();
        this.rightPress = rightP;
        if (right == null) {
            right = new Runnable() {
                @Override
                public void run() {
                    if (!rightPress) {
                        mHandler.removeCallbacks(this);
                        onTimerScroll(getScrollDistance(mTabCurrentTime), false);
                    } else {
                        mTabCurrentTime += 30;
                        ScrollToHorizationView(getScrollDistance(mTabCurrentTime));
                        onTimerScroll(getScrollDistance(mTabCurrentTime), true);
//                        seek();
                        mHandler.postDelayed(this, 10);
                    }
                }
            };
        }
        mHandler.postDelayed(right, 10);
    }

    private void showHideTopAndBotton() {
        isShowTopAndBotton = !isShowTopAndBotton;
        Animation topOut = AnimationUtils.loadAnimation(this, R.anim.top_out);
        Animation topIn = AnimationUtils.loadAnimation(this, R.anim.top_in);
        Animation bottonOut = AnimationUtils.loadAnimation(this, R.anim.bottom_out);
        Animation bottonIn = AnimationUtils.loadAnimation(this, R.anim.bottom_in);
        Animation rightIn = AnimationUtils.loadAnimation(this, R.anim.right_in);
        Animation rightOut = AnimationUtils.loadAnimation(this, R.anim.right_out);
        //        View decorView = getWindow().getDecorView();
        if (isShowTopAndBotton) {
            mTopLayout.setVisibility(View.VISIBLE);
            mTopLayout.startAnimation(topIn);
            if (mPlayType != PLAY_TYPE_OF_EVENT) {
                mSlideLl.setVisibility(View.VISIBLE);
                mSlideLl.startAnimation(rightIn);
            }
            if (mPlayType == PLAY_TYPE_OF_RECORD) {
                initStubUI();
                playRecordUI();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
//                        View view = mPlayerDateAdapter.getViewByPosition(mPrePosition, R.id.date_tv);
//                        view.setSelected(true);
                    }
                });
                drawableScaleBackground(mSections);
                mRecordHorizontalLayout.setVisibility(View.VISIBLE);
                mRecordHorizontalLayout.startAnimation(bottonIn);
            }

        } else {
            mTopLayout.setVisibility(View.GONE);
            mTopLayout.startAnimation(topOut);
            if (mPlayType != PLAY_TYPE_OF_EVENT) {
                mSlideLl.setVisibility(View.GONE);
                mSlideLl.startAnimation(rightOut);
            }
            if (mPlayType == PLAY_TYPE_OF_RECORD) {
                mRecordHorizontalLayout.setVisibility(View.GONE);
                mRecordHorizontalLayout.startAnimation(bottonOut);
            }
        }
    }

    private void stopPlay() {
        mIsPlay = false;
        mHandler.removeCallbacks(recordProgressRunnable);
        mHandler.removeCallbacks(bitrateRunnable);
        mHandler.removeCallbacksAndMessages(null);
        if (mPlayer != null) {
            mPlayer.clearQueueBuffer();
            mPlayer.release();
        }
        if (mStreamer != null) {
            mStreamer.close();
            mStreamer.release();
        }
        isInit = false;
    }

    @Override
    public void getStreamSuccess(PlayEntity playEntity) {
        if (mPlayType != PLAY_TYPE_OF_LIVE) return;
        if (null != playEntity && !TextUtils.isEmpty(playEntity.getToken())
                && playEntity.getStreamEntity().getDevices() != null
                && !playEntity.getStreamEntity().getDevices().isEmpty()) {
            List<CameraNewStreamEntity.DevicesBean> devices = playEntity.getStreamEntity().getDevices();
            CameraNewStreamEntity.DevicesBean devicesBean = devices.get(0);
            if (!TextUtils.isEmpty(devicesBean.getRtmp_url())) {
                mPlayLiveAddr = devicesBean.getRtmp_url() + "live/" + playEntity.getToken();
            }
            mPlayAddr = mPlayLiveAddr;
            if (TextUtils.isEmpty(mCoverUrl)) {
                mCoverUrl = devicesBean.getCover_url();
                mImageLoader.loadImage(this, ImageConfigImpl
                        .builder()
                        .cacheStrategy(0)
                        .url(mCoverUrl)
                        .imageView(mCoverIV)
                        .build());
            }
            startPlay();
        } else {
            playFailed();
        }
    }

    @Override
    public void getStreamFailed() {
        playFailed();
    }

    @Override
    public void onCameraLikeSuccess(GetKeyStoreBaseEntity entity) {
        mLoadingDialog.dismiss();
        setCollected(!mCollected);
        if (mCollected) {
            ToastUtils.showShort(R.string.collect_success);
        } else {
            ToastUtils.showShort(R.string.uncollect_success);
        }
        Gson gson = new Gson();
        String toJson = gson.toJson(mCurrentRequest);
        SPUtils.getInstance().put(Constants.COLLECT_JSON, toJson);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new CollectChangeBean(), CAMERA_STATUS_CHANGE);
            }
        }, 200);
    }

    @Override
    public void onCameraLikeFail() {
        mLoadingDialog.dismiss();
        if (mCollected) {
            ToastUtils.showShort(R.string.uncollect_fail);
        } else {
            ToastUtils.showShort(R.string.collect_fail);
        }
    }

    @Override
    public void getRecordTimeFail() {
        playFailed();
    }

    @Override
    public void getRecordTimeSuccess(RecordLyCameraBean recordVideosResponse) {
        if (mPlayType == PLAY_TYPE_OF_LIVE)
            return;
        if (recordVideosResponse != null) {
            mSegment = new Gson().toJson(recordVideosResponse);
            mSections = recordVideosResponse.getVideos();
            if (mSections.size() == 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLoadingDialog.dismiss();
                        mSections.add(new VideosBean(mCurrentSelFrom / 1000, mCurrentSelFrom / 1000));
                        if (mPlayType == PLAY_TYPE_OF_RECORD && isShowTopAndBotton) {
                            drawableScaleBackground(mSections);
                        }
                        ToastUtils.showShort("当前暂无回放哦！");
                    }
                });
                return;
            }
            //查看当天录像，从最后一段的最后五分钟开始播放
            //查看当天以前的录像，从前一个播放时间点开始播放
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mPlayType == PLAY_TYPE_OF_RECORD) {
                        if (isShowTopAndBotton) {
                            drawableScaleBackground(mSections);
                        }
                        initPlayTime(mSections);
                    } else {
                        initEventTime(mSections);
                    }
                }
            });
        }
    }

    @Override
    public void getHistoriesSuccess(GetKeyStoreBaseEntity entity) {
        if (entity != null) {
            GetKeyStoreEntity storeEntity = entity.getUserKvStroe();
            if (null != storeEntity) {
                String storeValue = storeEntity.getStoreValue();
                if (!TextUtils.isEmpty(storeValue)) {
                    boolean found = false;
                    boolean exception = false;
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.registerTypeAdapter(ListHistoryTypeAdapter.TYPE,
                            ListHistoryTypeAdapter.getInstance());
                    Gson gson = gsonBuilder.create();
                    List<HistoryKVStoreRequest.History> histories = null;
                    try {
                        histories = gson.fromJson(storeValue, ListHistoryTypeAdapter.TYPE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        exception = true;
                    }
                    if (histories != null && !histories.isEmpty()) {
                        for (HistoryKVStoreRequest.History history : histories) {
                            if (mCameraId == history.getCameraId()) {
                                history.touch();
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        if (histories == null) {
                            histories = new ArrayList<>();
                        }
                        HistoryKVStoreRequest.History history = new HistoryKVStoreRequest.History(
                                mCameraId, TextUtils.isEmpty(mCameraName) ? "UNKNOWN" : mCameraName,
                                0, 0);
                        history.touch();
                        histories.add(history);
                    }
                    if (!exception) {
                        HistoryKVStoreRequest request = HistoryKVStoreRequest.obtain();
                        request.setHistories(histories);
                        mPresenter.putHistories("APP_HISTORY", request);
                    }
                } else {
                    List<HistoryKVStoreRequest.History> histories = new ArrayList<>();
                    HistoryKVStoreRequest.History history = new HistoryKVStoreRequest.History(
                            mCameraId, TextUtils.isEmpty(mCameraName) ? "UNKNOWN" : mCameraName,
                            0, 0);
                    history.touch();
                    histories.add(history);
                    HistoryKVStoreRequest request = HistoryKVStoreRequest.obtain();
                    request.setHistories(histories);
                    mPresenter.putHistories("APP_HISTORY", request);
                }
            }
        }
    }

    @Override
    public void getHistoriesFail() {
        // 获取历史记录失败暂时未做任何处理
    }

    @Override
    public void putHistoriesSuccess() {
        EventBus.getDefault().post(new VideoClickBean(), CAMERA_COUNT_CHANGE);
    }

    /**
     * 根据时间戳计算滑动距离 单位：像素
     *
     * @param timeStamp 时间戳 单位秒
     */
    public int getScrollDistance(long timeStamp) {
        return (int) ((timeStamp - mCurrentSelFrom / 1000) / mDistanceTime) + mStartScrollX;
    }

    //滑动HorizationView
    public void ScrollToHorizationView(int scrollX) {
        if (mTimeLineHorizontalView != null) {
            int max = 720 * mScaleView.mScaleMargin;//能滑动的最大距离
            if (scrollX > max) {
                scrollX = max;
            }
            mTimeLineHorizontalView.scrollTo(scrollX, 0);
        }
    }

    private void initPlayTime(List<VideosBean> sections) {
        // 首次点击观看录像是查看当前录像，从当天0点到当前时间的时间轴查询显示，播放时间为当前时间的前5分钟
        // 显示用户存储天数的日期，如果是7天循环存储，则显示当前及前六天的日期
        // 用户选择哪天就查询哪天0点到24点的时间轴信息，播放时间为播放时间的前五分钟
        // 如果播放时间无录像，则播放下一段。如果播放时间后面没有录像了则播放上一段录像的最后五分钟
        if (sections.size() == 0) return;
        if (mTabCurrentTime == 0) {
            mTabCurrentTime = System.currentTimeMillis() / 1000 - 300;
        }
        long playTime = mTabCurrentTime / 1000;
        VideosBean section = sections.get(sections.size() - 1);
        if (playTime < section.getEnd() && playTime >= section.getBegin()) {
            mTabCurrentTime = playTime;
        } else {
            /*playTime = section.getEnd() - 300;
            if (playTime < section.getBegin()) {
                //                    mCurrentSelFrom = section.from;
                playTime = section.getBegin();
            }
            mTabCurrentTime = playTime;
            */
            if (mTabCurrentTime < section.getBegin()) {
                //                    mCurrentSelFrom = section.from;
                mTabCurrentTime = section.getBegin();
            }
        }
        ScrollToHorizationView(getScrollDistance(mTabCurrentTime));
        LogUtils.i("initPlayTime--" + TimeUtils.millis2String(mTabCurrentTime * 1000, "HH:mm:ss"));
        mPlayAddr = String.format(mPlayAddrDomain + "&token=%s&begin=%s&end=%s&play=%s", mToken,
                mCurrentSelFrom / 1000, mCurrentSelTo / 1000, mTabCurrentTime);
        mCurrentTimeTV.setText("回放 | " +
                TimeUtils.millis2String(mTabCurrentTime * 1000, "MM/dd HH:mm:ss") + " | ");
        LogUtils.d("beck", "tab current time:init play time===" + mTabCurrentTime);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                startPlay();
            }
        });
    }

    private String getFormatTime(Date date) {
        return TimeUtils.date2String(date, new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss"));
    }

    private void initEventTime(List<VideosBean> sections) {
        //事件视频，播放前后15s
        if (sections.size() == 0) return;
        VideosBean section = sections.get(sections.size() - 1);
        if (mEventFrom < section.getBegin()) {
            mEventFrom = section.getBegin();
        }
        if (mEventTo > section.getEnd()) {
            mEventTo = section.getEnd();
        }
        mPlayAddr = String.format(mPlayAddrDomain + "&token=%s&begin=%s&end=%s&play=%s", mToken,
                mEventFrom, mEventTo, mEventFrom);
        mCurrentTimeTV.setText("回放 | " +
                TimeUtils.millis2String(mEventFrom * 1000, "MM/dd HH:mm:ss") + " | ");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                startPlay();
            }
        });
    }

    @Override
    public void getRecordToken(String token) {
        mToken = token;
    }

    //刻画时间轴背景
    private void drawableScaleBackground(List<VideosBean> sections) {
        ArrayList<MutilColorDrawable.MutilColorEntity> colors = new ArrayList<>();
        if (sections != null) {
            if (sections.size() > 0) {
                for (VideosBean sectionData : sections) {
                    if (sectionData == null)
                        break;
                    float fromPercent = (float) (sectionData.getBegin() - mCurrentSelFrom / 1000) / TOTAL_SPAN;
                    float endPercent = (float) (sectionData.getEnd() - mCurrentSelFrom / 1000) / TOTAL_SPAN;
                    LogUtils.e("ZL", "fromPercent=" + fromPercent + "   endPercent" + endPercent);
                    colors.add(new MutilColorDrawable.MutilColorEntity(fromPercent * 100f, endPercent * 100f, Color.parseColor("#E16E2B")));
                }

            }
        }
        MutilColorDrawable drawable = new MutilColorDrawable(colors);
        mScaleBackground.post(new Runnable() {
            @Override
            public void run() {
                mScaleBackground.setImageDrawable(drawable);
                LogUtils.e("ZL", "设置背景了");
                onTimerScroll(getScrollDistance(mTabCurrentTime), false);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isInit && mPaused) {
            startPlay();
            mPaused = false;
        } else {
            if (mPlayer != null) {
                mLoadingDialog.show();
                mPlayer.enterForeground();
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.i("onPause");
        if (mPlayer != null)
            mPlayer.enterBackground();
        mPaused = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.i("onStop");
        mIsPlay = false;
        if (mStreamer != null) {
            mStreamer.close();
            mStreamer.release();
        }
        if (mPlayer != null) {
            mPlayer.clearQueueBuffer();
            mPlayer.release();
        }
        isInit = false;

    }

    @Override
    public void onCollectSuccess() {
        mLoadingDialog.dismiss();
        showMessage("收藏成功！");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.i("onDestroy");
        if (mPlayer != null)
            mPlayer.release();
        if (mStreamer != null)
            mStreamer.release();
        if (mWorkThreadExecutor != null) {
            mWorkThreadExecutor.removeTasksAndMessages(null);
            mWorkThreadExecutor.release();
            mWorkThreadExecutor = null;
        }
        if (mHandler != null)
            mHandler.removeCallbacksAndMessages(null);
        mBackHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onBackPressedSupport() {
        if (mIsRecording) {
            ToastUtils.showShort("正在录制视频，请稍后");
        } else if (mIsPlayOrFail) {
            super.onBackPressedSupport();
        }
    }
}
