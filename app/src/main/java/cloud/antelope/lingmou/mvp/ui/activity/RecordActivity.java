package cloud.antelope.lingmou.mvp.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.antelope.sdk.ACMessageListener;
import com.antelope.sdk.ACResult;
import com.antelope.sdk.ACResultListener;
import com.antelope.sdk.capturer.ACShape;
import com.antelope.sdk.codec.ACCodecID;
import com.antelope.sdk.codec.ACDecodeMode;
import com.antelope.sdk.codec.ACStreamPacket;
import com.antelope.sdk.player.ACPlayer;
import com.antelope.sdk.player.ACPlayerExtra;
import com.antelope.sdk.player.ACPlayerStatus;
import com.antelope.sdk.service.ACWorkThread;
import com.antelope.sdk.streamer.ACProtocolType;
import com.antelope.sdk.streamer.ACStreamer;
import com.antelope.sdk.streamer.ACStreamerFactory;
import com.google.gson.Gson;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.service.CustomRunnable;
import cloud.antelope.lingmou.app.service.CustomRunnable2;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.di.component.DaggerRecordComponent;
import cloud.antelope.lingmou.di.module.RecordModule;
import cloud.antelope.lingmou.mvp.contract.RecordContract;
import cloud.antelope.lingmou.mvp.model.entity.CollectCameraEntity;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;
import cloud.antelope.lingmou.mvp.model.entity.RecordLyCameraBean;
import cloud.antelope.lingmou.mvp.model.entity.SetKeyStoreRequest;
import cloud.antelope.lingmou.mvp.model.entity.VideosBean;
import cloud.antelope.lingmou.mvp.presenter.RecordPresenter;
import cloud.antelope.lingmou.mvp.ui.widget.MutilColorDrawable;
import cloud.antelope.lingmou.mvp.ui.widget.MyHorizontalScrollView;
import cloud.antelope.lingmou.mvp.ui.widget.ScaleView;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.EasyPermissions;

import static cloud.antelope.lingmou.mvp.ui.activity.PlayerActivity.MAX_IMAGE_BOUNDS;
import static cloud.antelope.lingmou.mvp.ui.activity.PlayerActivity.mStoragePermission;
import static com.jess.arms.utils.Preconditions.checkNotNull;


public class RecordActivity extends BaseActivity<RecordPresenter> implements RecordContract.View,
        View.OnClickListener,
        CompoundButton.OnCheckedChangeListener,
        MyHorizontalScrollView.OnScrollByUserListener,
        EasyPermissions.PermissionCallbacks, Animation.AnimationListener {


    private static final int TYPE_BACK_CONTRAL = 0x10;
    @BindView(R.id.header)
    FrameLayout mHeader;
    @BindView(R.id.record_buttom)
    FrameLayout mFooter;
    @BindView(R.id.play_screen)
    GLSurfaceView mPlayerGLSurfaceView;
    @BindView(R.id.horizontalScale)
    ScaleView mScaleView;//刻度尺
    @BindView(R.id.scale_bg)
    ImageView mScaleBackground;  //时间轴的背景
    @BindView(R.id.my_horizontal_view)
    MyHorizontalScrollView mMyHorizontalScrollView;
    @BindView(R.id.date_rule)
    HorizontalScrollView mDateHorizontalScrollView;
    @BindView(R.id.play_type)
    TextView mPlayType;
    @BindView(R.id.time)
    TextView mTime;
    @BindView(R.id.date)
    LinearLayout mCalandar; //存储最近时间
    @BindView(R.id.key_frame)
    TextView mKeyFrame;//中间指针
    @BindView(R.id.start_cover)
    FrameLayout mStart_Cover; //进入播放界面的过度视图
    @BindView(R.id.player_restart_conver)
    FrameLayout mRestart_Conver; //重连
    @BindView(R.id.start_image)
    ImageView mConver;  //封面loading
    @BindView(R.id.restart_image)
    ImageView mRestart_Bg;//封面
    @BindView(R.id.network_tip)
    LinearLayout mNetwork_tip; //网络提示
    @BindView(R.id.speed)
    TextView mNetworkSpeed; //网络速度
    @BindView(R.id.play_source)
    TextView mCameraName;
    @BindView(R.id.empty_conver)
    TextView mEmptyConver;
    @BindView(R.id.record_time_tv)
    TextView mRecordTv;
    @BindView(R.id.record_iv)
    ImageView mRecordIv;
    @BindView(R.id.novideo_conver)
    TextView mNoEventTv;
    @BindView(R.id.record_rl)
    RelativeLayout mRecordRl;

    private LinearLayout mMenu;
    public boolean mIsSHow = false; //顶部和底部是否显示
    private boolean mIsCanClick = true;  //当滑动时间轴的时候，m顶部标题和右边菜单栏不进行动画
    private int mHalfScreen;   //屏幕一半距离
    private int mDensity;      //像素密度（整形）
    private float mDensityFloat; //像素密度 (浮点型）
    private int mPre_index = 0; //选中的日期(前一个索引）
    private int mIndex = 0;
    private PopupWindow mPopWindow, mProgress; //缩略图
    private CheckBox mForward_progress, mBack_progress; //快进和快退指示按钮
    private int mLastScrollx = 0; //上一次滑动的距离
    private TextView mProgressTextView;//显示滑动进度的TextView
    private Bundle mBundle;
    private OrgCameraEntity mBean;


    public ImageView mSmallPanel;  //小画面，小窗口

    public long mStartTimeStamp, mEndTimestamp;
    private int mTextViewWith = 60; //日期TextView默认60dp
    public boolean mIsPlaySuccess = false;

    private boolean mIsFirst = true;
    private NetWorkReceiver mNetWorkReceiver; //网路情况监听
    // PowerManager.WakeLock mWakeLock = null;
    private ImageButton mReplayIb;
    private CheckBox mPlayCutCamera;
    private CheckBox mPlayLive;
    private CheckBox mPlayVideo;
    private CheckBox mPlayAudio;
    private ImageButton mCollectVideo;
    // 照相声音播放
    MediaPlayer mMediaPlayer;

    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat df2 = new SimpleDateFormat("| MM/dd HH:mm:ss");
    private File mVideoFile;
    private boolean mCanGetLyy;

    private boolean mMenuEnable = false;
    private boolean mIsRecording = false;

    private ACPlayer mRecordPlayer;
    //------------------刻度尺相关-----------------
    private long mCurrentDayBeginTime = 0L; //某一天0点0分0秒的时间
    private long mCurrentDayEndTime = 0L;   //某一天23点59分59秒的时间
    private int mTotal; //时间轴总长度
    private final int mDayTime = 24 * 60 * 60;     //一天总时间
    private float mDistanceTime = 0.0f;    //单f位距离多少毫秒
    private int mStartScrollX = 0;
    private int mRecordStoreType = 7;  //7天云存储   30天云存储


    /**
     * 这个Handler主要用于计时 10秒内没有打开录像就关闭
     */
    private final int mReadFail = 0; //读取失败
    private final int mReadSuccess = 1; //读取成功
    private final int mCloseConver = 2;
    private boolean mStartTime = true; //计时10秒钟有没有读到数据
    private int mLimitTime = 10; //10秒

    //--player------ 静态数据方便上个页面修改--------
    private int mSampleHz = 11025;
    private int mChannelCount = 1;
    private final int mPlayerStart = 0;  //播放器开始播放最小时间
    private final int mPlayerDrop = 0; //播放器开始丢帧的最小时间
    private int mAudioType = ACCodecID.AC_CODEC_ID_AAC; //音频为AAC类型的。
    private long mLastTime = 0L; //上一次从硫化器中读取数据成功的时刻
    public boolean mIsAppearFrame = false; //GLSurface中是否出现了画面,只要第一次出现后一直都是true
    public boolean mIsOpenStreamer = false;//是否已经打开硫化器了
    private boolean mIsIsPauseGetPlayPosition = true; //是否停止播放进度的获取
    private ACStreamer mRecordStreamer;
    private boolean mIsDownloadComplete = false;
    public boolean mIsPause = true;//是否正在seek 如果正在seek的话,就先停止读数据
    private boolean mIsSeek = false; //是否在Seek

    private boolean mPlaying = true;

    private Animation mShowScaleAnimation, mHideTopScaleAnimation;     //顶部动画
    private Animation mShowRightScaleAnimation, mHideRightScaleAnimation;   //右边动画
    private Animation mShowBottomScaleAnimation, mHideBottomScaleAnimation; //底部动画

    private RecordLyCameraBean mRecordSection;  //时间片对象
    private String mRecordUrl = "topvdn://topvdn.public.cn?connectType=2&cid=537024953&protocolType=3&token=537024953_0_1492855799_db1f86adecdd91dc77a9827a1a3aed96&begin=1492790400&end=1492876799&play=1492790400";//视频播放地址
    private String mSeekStamp = "";
    private long[] mPlay = new long[]{0L};
    private long mBreakTimeStamp = 0L; //录像播放中断的时间点
    private Thread mRecordThread;//录像线程
    private ACStreamPacket mStreamPackt = null;//数据包
    public long mToltal = 0L; //当前网速
    /**
     * 设置表示位用于判断线程有没有start
     * 不管Thread.isAlive()返回的是true还是false，如果我们再次Thread.start()，就会出现 java.lang.IllegalThreadStateException
     */
    private boolean mRecordThreadStarted = false;

    private Bitmap mLandscapeMarkerBitmap;

    private boolean mIsPlayOrFail = false;
    /**
     * 播放器
     * GLSurface 渲染画面回调
     */
    private ACPlayerExtra mACPlayerExtra = new ACPlayerExtra() {
        @Override
        public void onPlayerStatus(int status) {
            //开始播放，播放成功
            if (status == ACPlayerStatus.ACPlayerStatusStartPlay) {
                mIsAppearFrame = true;
                closeConver();//关闭封面(第一次的时候）
                mIsIsPauseGetPlayPosition = false;
                setMenuEnable(true);
                setPlayLiveEnable(true);
                mIsPlaySuccess = true;
            }
            //LogUtils.e("ZG","status============================》"+status);
        }
    };
    private String mToken;
    private Long mCameraId;
    private SetKeyStoreRequest mKeyStore;
    private ArrayList<CollectCameraEntity> mCollectionCameras;
    private SetKeyStoreRequest mCurrentRequest;

    private Handler mBackHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mIsPlayOrFail = true;
        }
    };

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        DaggerRecordComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .recordModule(new RecordModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_record; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mBundle = getIntent().getExtras();
        if (mBundle != null) {
            mBean = (OrgCameraEntity) mBundle.getSerializable("data");
            mCameraId = mBean.getManufacturerDeviceId();
        }
        mMediaPlayer = MediaPlayer.create(this, R.raw.music_image_cut_);
        initTheView();
        initLivePlayer(mPlayerGLSurfaceView, Integer.parseInt("7"));
        initListener();
        showConver(true);//显示加载页面
        readRecordThread(true);
        mStartTimeStamp = TimeUtils.getPreDayBegin(0);
        mEndTimestamp = System.currentTimeMillis() / 1000l;
        mPresenter.getRecords(mCameraId, mStartTimeStamp, mEndTimestamp);
        registeretWorkReceiver();

        //判断是否已收藏
        String storeValue = SPUtils.getInstance().getString(Constants.COLLECT_JSON);
        if (!TextUtils.isEmpty(storeValue)) {
            Gson gson = new Gson();
            mKeyStore = gson.fromJson(storeValue, SetKeyStoreRequest.class);
            if (null != mKeyStore) {
                List<String> storeSets = mKeyStore.getSets();
                mCollectionCameras = new ArrayList<>();
                if (null != storeSets) {
                    for (String str : storeSets) {
                        CollectCameraEntity entity = new CollectCameraEntity();
                        String[] group_one = str.split(":");
                        entity.setGroup(group_one[0]);
                        String[] group_two = group_one[1].split("/");
                        String cid = group_two[0];
                        if ((mCameraId + "").equals(cid)) {
                            setCollected(true);
                        }
                        entity.setCid(cid);
                        entity.setCameraName(group_two[1]);
                        if (!mCollectionCameras.contains(entity)) {
                            mCollectionCameras.add(entity);
                        }
                    }
                }
            }

        } else {
            setCollected(false);
        }

        /*CameraItem item = DataSupport.where("cameraid = ?", mCameraId+"").findFirst(CameraItem.class);
        if (null != item) {
            //标明已收藏
            setCollected(true);
        } else {
            setCollected(false);
        }*/

        if (!NetworkUtils.isWifiConnected()) {
            Toast.makeText(this, getString(R.string.see_video_2_3_4), Toast.LENGTH_SHORT).show();
        }
        setMenuEnable(false);
        setPlayLiveEnable(false);
        mBackHandler.sendEmptyMessageDelayed(TYPE_BACK_CONTRAL, 2500);
    }

    private void initListener() {
        mPlayerGLSurfaceView.setOnClickListener(this);
        findViewById(R.id.play_finish).setOnClickListener(RecordActivity.this); //设置返回监听事件
        mMyHorizontalScrollView.setOnScrollByUserListener(RecordActivity.this); //设置HorizontalScrollView
        findViewById(R.id.net_work_cancle).setOnClickListener(this); //网络提示取消按钮
        findViewById(R.id.play_restart).setOnClickListener(this); //重连操作
        findViewById(R.id.play_help).setOnClickListener(this); //重连操作
    }

    private boolean mCollected;

    public void setCollected(boolean isCollect) {
        mCollected = isCollect;
        int resId = isCollect ? R.drawable.video_collected : R.drawable.video_uncollected;
        mCollectVideo.setImageResource(resId);
    }

    //注册监听网络的接收器
    private void registeretWorkReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mNetWorkReceiver = new NetWorkReceiver();
        registerReceiver(mNetWorkReceiver, filter);
    }

    public void readRecordThread(boolean read) {
        mPlaying = read;
    }

    /**
     * 这个Handler 主要用于记录10秒没有开成功就销毁掉
     */
    public Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == mReadFail) {
                mLimitTime--;
                //LogUtils.e("ZL","计时=="+mLimitTime);
                if (mLimitTime < 0) {
                    mHandler.post(closeTask);
                } else {
                    if (mStartTime) {
                        Message message = Message.obtain();
                        message.what = mReadFail;
                        mHandler.sendMessageDelayed(message, 1000);
                    }
                }
            } else {
                //停止倒计时
                mStartTime = false;
            }

            if (msg.what == mCloseConver) {
                closeConver();
            }
        }
    };


    /**
     * 显示 请稍后遮罩
     *
     * @param isShowBg 是否显示遮罩的背景
     */
    Runnable mStart_Cover_Task;

    public void showConver(final boolean isShowBg) {
        if (mStart_Cover != null) {
            if (mStart_Cover.getVisibility() != View.VISIBLE) {

                if (mStart_Cover_Task == null) {
                    mStart_Cover_Task = new Runnable() {
                        @Override
                        public void run() {
                            mStart_Cover.setVisibility(View.VISIBLE);
                            if (isShowBg) {
                                mConver.setVisibility(View.VISIBLE);
                                /*Glide.with(RecordActivity.this).load(mBean.coverUrl)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(mConver);*/
                            } else {
                                mConver.setVisibility(View.GONE);
                            }
                        }
                    };
                }
                runOnUiThread(mStart_Cover_Task);
            }
        }
    }

    //关闭任务(超过10秒还没有读取到任务）
    private Runnable closeTask = new Runnable() {
        @Override
        public void run() {
            closeConver();
            closeEmptyConver();
            showRestartConver(true);
            closeStreamer();
            setPlayLiveEnable(true);
        }
    };

    //关闭硫化器
    public void closeStreamer() {
        if (mRecordStreamer != null) {
            mIsDownloadComplete = false;
            //LogUtils.e("ZL","close 之前....");
            mRecordStreamer.close();
            //LogUtils.e("ZL","close 之后....");
            mRecordStreamer.release();
            //LogUtils.e("ZL","release 之后....");
            mIsOpenStreamer = false;
        }
    }

    /**
     * 显示 重连封面
     *
     * @param isShowBg 是否显示遮罩的背景
     */
    Runnable mRestart_Conver_Task;

    public void showRestartConver(final boolean isShowBg) {
        if (mRestart_Conver != null) {
            if (mRestart_Conver.getVisibility() == View.GONE || mRestart_Conver.getVisibility() == View.INVISIBLE) {
                if (mRestart_Conver_Task == null) {
                    mRestart_Conver_Task = new Runnable() {
                        @Override
                        public void run() {
                            if (mRestart_Conver.getVisibility() == View.GONE || mRestart_Conver.getVisibility() == View.INVISIBLE) {
                                mRestart_Conver.setVisibility(View.VISIBLE);
                                //LogUtils.e("ZL","出现了....");
                            }

                            if (isShowBg) {
                                mRestart_Bg.setVisibility(View.VISIBLE);
                                /*Glide.with(RecordActivity.this).load(mBean.coverUrl)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(mRestart_Bg);*/

                                if (mNetworkSpeed != null) {
                                    mNetworkSpeed.setText("0KB/s");
                                }

                            } else {
                                mRestart_Bg.setVisibility(View.GONE);
                            }
                        }
                    };
                }
                runOnUiThread(mRestart_Conver_Task);
            }
        }
    }

    /**
     * 移除 重连画面
     */
    Runnable mRestart_Conver_Close_Task;

    public void closeRestartConver() {
        if (mRestart_Conver != null) {
            if (mRestart_Conver.getVisibility() == View.VISIBLE) {
                if (mRestart_Conver_Close_Task == null) {
                    mRestart_Conver_Close_Task = new Runnable() {
                        @Override
                        public void run() {

                            mRestart_Conver.setVisibility(View.GONE);
                        }

                    };
                }
                runOnUiThread(mRestart_Conver_Close_Task);
            }
        }
    }

    /**
     * 移除 空白界面
     */
    Runnable mEmptyConver_Close_Task;

    public void closeEmptyConver() {
        if (mEmptyConver != null) {
            if (mEmptyConver.getVisibility() == View.VISIBLE) {
                if (mEmptyConver_Close_Task == null) {
                    mEmptyConver_Close_Task = new Runnable() {
                        @Override
                        public void run() {
                            mEmptyConver.setVisibility(View.GONE);
                        }
                    };
                }
                runOnUiThread(mEmptyConver_Close_Task);
            }
        }
    }

    public void setMenuEnable(final boolean isEnable) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null != mPlayAudio) {
                    mPlayAudio.setEnabled(isEnable);
                }
                if (null != mPlayVideo) {
                    mPlayVideo.setEnabled(isEnable);
                }
                if (null != mPlayCutCamera) {
                    mPlayCutCamera.setEnabled(isEnable);
                }
                if (null != mReplayIb) {
                    mReplayIb.setEnabled(isEnable);
                }
            }
        });
    }

    public void setPlayLiveEnable(final boolean isEnable) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null != mPlayLive) {
                    mPlayLive.setEnabled(isEnable);
                }
                if (null != mCollectVideo) {
                    mCollectVideo.setEnabled(isEnable);
                    mCollectVideo.setClickable(isEnable);
                }
            }
        });
    }


    /**
     * 移除 请稍后遮罩
     */
    Runnable mStart_Cover_Close_Task;

    public void closeConver() {
        if (mStart_Cover != null) {
            // if (mStart_Cover.getVisibility() == View.VISIBLE) {
            if (mStart_Cover_Close_Task == null) {
                mStart_Cover_Close_Task = new Runnable() {
                    @Override
                    public void run() {
                        mStart_Cover.setVisibility(View.GONE);
                        mIsPlayOrFail = true;
                        setMyHorizontalScrollViewEnable(true);
                    }
                };
            }
            runOnUiThread(mStart_Cover_Close_Task);
            // }
        }
    }


    /**
     * 创建并初始化播放器
     * 一个界面在onCreate中创建，在onDestroy中销毁
     */
    public void initLivePlayer(GLSurfaceView playerGLSurfaceView, int RecordStoreType) {
        mRecordStoreType = RecordStoreType;
        mRecordPlayer = new ACPlayer();
        if (mRecordPlayer != null) {
            mRecordPlayer.setPlaySurfaceView(playerGLSurfaceView, ACShape.AC_SHAPE_NONE);
        }

        ACPlayer.Config.Builder builder = new ACPlayer.Config.Builder();
        builder.setStartDropBufferSize(mPlayerStart)
                .setStartDropBufferSize(mPlayerDrop)
                .setAudioCodecId(mAudioType)
                .setVideoCodecId(ACCodecID.AC_CODEC_ID_H264)
                .setSampleRate(mSampleHz)
                .setChannelCount(mChannelCount);

        ACResult result = mRecordPlayer.initialize(builder.create(), mACPlayerExtra);
        // handleResult(result,getView());
        mHandler.post(mSDKPlayGrassTask);//开始获取播放进度
        mRecordPlayer.setDecodeMode(ACCodecID.AC_CODEC_ID_H264, ACDecodeMode.AC_DEC_MODE_SOFT);


        LogUtils.e("ZL", "解码类型====");
    }

    private void initTheView() {
        mDensityFloat = getResources().getDisplayMetrics().density;
        mDensity = Math.round(mDensityFloat);

        mPlayType = (TextView) findViewById(R.id.play_type);
        if (null != mPlayType) {
            mPlayType.setText(getResources().getString(R.string.record_type));
        }
        LayoutInflater inflater = LayoutInflater.from(this);
        //小窗口缩略图
        View small_window_root = inflater.inflate(R.layout.small_window, null);
        mPopWindow = new PopupWindow(small_window_root, 170 * mDensity, 100 * mDensity);
        mPopWindow.setAnimationStyle(R.style.popwindow_style);
        mSmallPanel = (ImageView) small_window_root.findViewById(R.id.small_window_panel);


        //进度缩略图
        View progress_root = inflater.inflate(R.layout.popwindow_progress, null);
        mProgress = new PopupWindow(progress_root, 250 * mDensity, 35 * mDensity);
        mForward_progress = (CheckBox) progress_root.findViewById(R.id.progress_back);
        mBack_progress = (CheckBox) progress_root.findViewById(R.id.progress_forward);
        mProgressTextView = (TextView) progress_root.findViewById(R.id.progress_time);

        //网络友好性的
        mCameraName.setText(mBean.getDeviceName());
        mNoEventTv = (TextView) findViewById(R.id.novideo_conver);

        if (mMenu == null) {  //懒加载
            ((ViewStub) findViewById(R.id.header_view_stub)).inflate();
            mMenu = (LinearLayout) findViewById(R.id.menu);
            mPlayAudio = ((CheckBox) findViewById(R.id.play_audio));
            mPlayAudio.setOnCheckedChangeListener(RecordActivity.this); //声音按钮
            mPlayVideo = (CheckBox) findViewById(R.id.play_video);
            mPlayVideo.setOnCheckedChangeListener(RecordActivity.this); //录像按钮
            mPlayCutCamera = (CheckBox) findViewById(R.id.play_camera);
            mPlayCutCamera.setOnCheckedChangeListener(RecordActivity.this); //截图按钮
            mPlayLive = (CheckBox) findViewById(R.id.play_live);
            CheckBox record = (CheckBox) findViewById(R.id.play_record);
            mReplayIb = (ImageButton) findViewById(R.id.replay_ib);
            mReplayIb.setOnClickListener(RecordActivity.this);
            mCollectVideo = (ImageButton) findViewById(R.id.collect_video);
            mCollectVideo.setVisibility(View.VISIBLE);
            mCollectVideo.setOnClickListener(this);
            record.setVisibility(View.GONE);
            mReplayIb.setVisibility(View.GONE);
            mPlayLive.setVisibility(View.VISIBLE);
            mPlayLive.setOnCheckedChangeListener(RecordActivity.this); //播放录像按钮
        }

        mHalfScreen = getResources().getDisplayMetrics().widthPixels / 2;
        //之所以这样是为了减少dp在不同设备上造成的精度损失
        int scale_margin = (SizeUtils.getScreenWidth() - getResources().getDimensionPixelSize(R.dimen.dp120)) / (12 * 5);
        //xml 里面的数据只作为参考
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mScaleBackground.getLayoutParams();
        params.width = scale_margin * 720; // 720表示刻度的数目
        params.leftMargin = mHalfScreen;
        mScaleBackground.setLayoutParams(params);

        int scale_sum_real = scale_margin * (720); //总长度(以这个为准，xml里面制作参考,避免dp在不同设备上造成的精度损失）
        initScaleBaseData(scale_sum_real); //初始化刻度尺相关的
    }


    /**
     * 初始化刻度尺基本相关的基本数据
     */
    public void initScaleBaseData(int scale_sum_real) {
        mTotal = scale_sum_real;
        mDistanceTime = 24 * 60 * 60 / (mTotal * 1.0f); // 秒/像素
        mStartScrollX = 0;
    }


    @Override
    public void showLoading(String message) {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(@NonNull String message) {
        setPlayLiveEnable(true);
        showRestartConver(!mIsAppearFrame);
        closeConver();
        setMyHorizontalScrollViewEnable(false);
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
        switch (v.getId()) {
            case R.id.play_screen:
                //点击了视频播放界面
                if (mIsCanClick) {
                    if (mIsSHow) {
                        hideTopAnimation(mHeader);
                        hideBottomAnimation(mFooter);
                        hideRightAnimation(mMenu);
                        mIsSHow = false;
                    } else {
                        showTopAnimation(mHeader);
                        showBottomAnimation(mFooter);
                        showRightAnimation(mMenu);
                        mIsSHow = true;
                    }

                    if (mIsFirst) {
                        ScrollDateHorizationView(Integer.parseInt("7"));
                        mIsFirst = false;
                    }
                }
                break;
            case R.id.play_finish:
                //返回back 界面
                if (mIsRecording) {
                    ToastUtils.showShort("正在录制视频，请稍后");
                } else if (!mIsPlayOrFail) {

                }else{
                    super.onBackPressed();
                }
                /*if (!mIsRecording) {
                    finish();
                } else {
                    ToastUtils.showShort("正在录制视频，请稍后");
                }*/
                break;
            case R.id.play_restart:
                //重连界面
                if (!NetworkUtils.isConnected()) {
                    Toast.makeText(this, "请检查你的网络", Toast.LENGTH_SHORT).show();
                    return;
                }
                showConver(true); //显示load....页面
                closeRestartConver(); //关闭点击封面
                if (null != mBean) {
                    //重连界面
                    mPresenter.getRecords(mCameraId, mStartTimeStamp, mEndTimestamp);
                }
                break;
            case R.id.text_view:
                long begin = (long) v.getTag(R.id.tag_first);
                //录像时间轴上的日期
                if (mStartTimeStamp != begin) {
                    mHandler.removeCallbacksAndMessages(null);
                    changeTime.value = df2.format(begin * 1000);
                    mHandler.post(changeTime);
                    long end = (long) v.getTag(R.id.tag_second);
                    mEndTimestamp = end;
                    mStartTimeStamp = begin;
                    mIndex = (int) v.getTag(R.id.index);

                    showConver(true); //显示load....页面
                    closeRestartConver(); //关闭点击封面
                    closeEmptyConver(); //关闭无录像问题
                    if (null != mBean) {
                        //重连界面
                        setMyHorizontalScrollViewEnable(false);
                        changeDate(mCameraId);
                    }
                }
                break;
            case R.id.collect_video:
                //点击收藏 或者取消收藏
                mMenuEnable = mPlayCutCamera.isEnabled();
                setMenuEnable(false);
                setPlayLiveEnable(false);
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
                    /*Iterator<String> groupIterator = groups.iterator();
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
                    }*/
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
                    collectCameraEntity.setCameraName(mBean.getDeviceName());
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
                break;
        }

    }

    @Override
    public void onBackPressedSupport() {
        if (mIsRecording) {
            ToastUtils.showShort("正在录制视频，请稍后");
        } else if (!mIsPlayOrFail) {

        }else{
            super.onBackPressedSupport();
        }
        /*if (!mIsRecording) {
            super.onBackPressed();
        } else {
            ToastUtils.showShort("正在录制视频，请稍后");
        }*/
    }

    //改变观看的日期
    public void changeDate(Long cid) {
        //LogUtils.e("ZL","切换日期停止线程读取="+mIsPause);
        mHandler.post(mSDKPlayGrassTask);
        mIsPause = true; //让线程先停止读取数据
        mIsIsPauseGetPlayPosition = true;//停止进度的获取
        mBreakTimeStamp = 0L;
        mPlay[0] = 0L;
        mPresenter.getRecords(cid, mStartTimeStamp, mEndTimestamp);
    }

    /**
     * 让切换时间在一个消息队列里面控制
     */
    private CustomRunnable<String> changeTime = new CustomRunnable<String>() {
        @Override
        public void run() {
            setPlayTime(value);
        }
    };

    /**
     * 日期轴滑动
     *
     * @param index 表示选中的是第几个TextView
     */
    CustomRunnable<Integer> ScrollDateHorizationView_Task;

    public void ScrollDateHorizationView(int index) {

        int scrollX = (int) (mTextViewWith * mDensityFloat * (index - 1) + mTextViewWith * mDensityFloat * 0.5);
        if (mDateHorizontalScrollView != null) {
            if (ScrollDateHorizationView_Task == null) {
                ScrollDateHorizationView_Task = new CustomRunnable<Integer>() {
                    @Override
                    public void run() {
                        mDateHorizontalScrollView.scrollTo(value, 0);
                    }
                };
            }
            ScrollDateHorizationView_Task.value = scrollX;
            mDateHorizontalScrollView.post(ScrollDateHorizationView_Task);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.play_audio:
                //声音
                if (isChecked) {
                    if (mRecordPlayer != null) {
                        mRecordPlayer.unmute();//取消静音
                    }
                } else {
                    if (mRecordPlayer != null) {
                        mRecordPlayer.mute();//静音（停止播放音频）
                    }
                }
            case R.id.play_video:
                /*if (EasyPermissions.hasPermissions(RecordActivity.this, mStoragePermission)) {
                    //录制视频
                    String photoDir = Configuration.getRootPath() + Constants.RECORD_PATH;
                    File dir = new File(photoDir);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    mVideoFile = new File(dir, System.currentTimeMillis() + "_" + mBean.getCameraNo() + ".mp4");
                    if (!mIsRecording) {
                        getPesenter().startRecord(mVideoFile.getAbsolutePath());
                        mIsRecording = true;
                        mRecordRl.setVisibility(View.VISIBLE);
                        mRecordHandler.sendEmptyMessageDelayed(1, 1000);
                        setViewEnable(false);
                    } else {
                        stopRecord(mIsOnPause);
                    }
                } else {
                    stopRecord(false);
                    ToastUtils.showShort("请前往设置打开存储权限");
                }*/
                break;
            case R.id.play_camera:
                if (EasyPermissions.hasPermissions(RecordActivity.this, mStoragePermission)) {
                    //截图
                    mMediaPlayer.start();
                    mPlayCutCamera.setEnabled(false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mPlayCutCamera.setEnabled(true);
                        }
                    }, 200);

                    //截图
                    String photoDir = Configuration.getRootPath() + Constants.SNAP_PATH;
                    File dir = new File(photoDir);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    final File picFile = new File(dir, System.currentTimeMillis() + "_" + mBean.getSn() + ".png");
                    snapShot(picFile.getAbsolutePath(), new PlayerActivity.IOnSnapListener() {
                        @Override
                        public void snapSuccess() {
                            ToastUtils.showShort("截图成功");
                            DeviceUtil.galleryAddMedia(picFile.getAbsolutePath());
                        }

                        @Override
                        public void snapFail(String msg) {
                            ToastUtils.showShort(msg);
                        }
                    });
                } else {
                    ToastUtils.showShort("请前往设置打开存储权限");
                    // EasyPermissions.requestPermissions(RecordActivity.this, getString(R.string.storage_title), PERMISSION_STORAGE, mStoragePermission);
                }
                break;
            case R.id.play_live:
                Intent intent = new Intent(this, PlayerActivity.class);
                intent.putExtras(mBundle);
                startActivity(intent);
                finish();
                break;
        }
    }


    public void snapShot(String path, PlayerActivity.IOnSnapListener listener) {
        if (!TextUtils.isEmpty(path)) {
            ACResult acResult = mRecordPlayer.snapshot(path);
            if (acResult.isResultOK()) {
                listener.snapSuccess();
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
                listener.snapFail("截图保存文件时失败");
                // listener.snapFail("errorCode:" + acResult.getCode() + ",errorCodeDes:" + acResult.getCodeDesc() + ",errorMsg:" + acResult.getErrMsg());
            }

        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    /**
     * 时间轴滑动回调
     * MyHorizontalScrollView的滚动监听事件
     *
     * @param isOnTouch false表示手指离开  true表示手指一直在时间刻度轴上
     */
    Runnable mProgress_Task, mProgress_Dismiss_Task;

    @Override
    public void onScroll(int scrollX, boolean isOnTouch) {
        if (isOnTouch) { //缩略图出现
            if (mProgress.isShowing()) {
                if (scrollX - mLastScrollx >= 1) {
                    mForward_progress.setChecked(false);
                    mBack_progress.setChecked(true);
                }

                if (mLastScrollx - scrollX >= 1) {
                    mForward_progress.setChecked(true);
                    mBack_progress.setChecked(false);
                }
                mLastScrollx = scrollX;
                  /*if(!mPopWindow.isShowing()){
                      mPopWindow.showAtLocation(mPlayerGLSurfaceView,Gravity.NO_GRAVITY,mHalfScreen-85*mDensity,getResources().getDisplayMetrics().heightPixels-(188*mDensity));
                  }*/
            } else {
                if (mProgress_Task == null) {
                    mProgress_Task = new Runnable() {
                        @Override
                        public void run() {
                            mProgress.showAtLocation(mPlayerGLSurfaceView, Gravity.NO_GRAVITY, mHalfScreen - 100 * mDensity, 90 * mDensity);
                        }
                    };
                }
                runOnUiThread(mProgress_Task);
            }
        } else {  //缩略图消失
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setMyHorizontalScrollViewEnable(false);
                }
            });
            if (mProgress_Dismiss_Task == null) {
                mProgress_Dismiss_Task = new Runnable() {
                    @Override
                    public void run() {
                        if (mProgress.isShowing()) {
                            mProgress.dismiss();
                        }
                    }
                };
            }
            runOnUiThread(mProgress_Dismiss_Task);
            //if(mPopWindow.isShowing()) {mPopWindow.dismiss();}
        }
        mIsCanClick = !isOnTouch;
        onScroll(scrollX, isOnTouch, mProgressTextView);
    }


    //MyScrollView的滑动回调
    public void onScroll(int scrollX, boolean isOnTouch, TextView Progress) {
        mIsSeek = isOnTouch;
        long mTimeStamp = getTimeStamp(scrollX);
        if (!isOnTouch) {
            closeRestartConver();
            if (null != mRecordSection && mRecordSection.getVideos() != null) {
                if (mRecordSection.getVideos().size() > 0) {
                    showConver(!mIsAppearFrame);
                    closeEmptyConver();
                }
            }
            SeekTimeStamp(mTimeStamp);
        }
        setProgressTime(mTimeStamp);

    }


    //设置滑动的时的快进或者快退的进度
    CustomRunnable mProgressTextView_task;

    public void setProgressTime(final long time) {
        if (mProgressTextView != null) {
            if (mProgressTextView_task == null) {
                mProgressTextView_task = new CustomRunnable() {
                    @Override
                    public void run() {
                        if (mProgress.isShowing()) {
                            mProgressTextView.setText(df.format(((Long) value) * 1000));
                        }
                    }
                };
            }
            mProgressTextView_task.value = time;
            runOnUiThread(mProgressTextView_task);
        }
    }

    /**
     * 设置时间轴（手指滑动设置时间轴）
     *
     * @param timeStamp 时间戳 单位：秒
     */
    CustomRunnable<Long> seek_Task;

    public void SeekTimeStamp(long timeStamp) {
        if ((timeStamp - 1) == mCurrentDayEndTime) {
            timeStamp = mCurrentDayEndTime;
        }

        if (timeStamp < mCurrentDayBeginTime || timeStamp > mCurrentDayEndTime) {
            closeConver();
            return;
        }
        if (mRecordStreamer != null) {
            if (seek_Task == null) {
                seek_Task = new CustomRunnable<Long>() {
                    @Override
                    public void run() {
                        mIsPause = true;//停止拉流
                        mIsIsPauseGetPlayPosition = true;//停止播放进度获取
                        if (mRecordPlayer != null) {
                            mRecordStreamer.seek(value * 1000, mSeekListener);
                            mRecordPlayer.clearQueueBuffer();//清空播放器缓存
                        }
                        //LogUtils.e("ZL","seek 时间戳:"+value);
                    }
                };

            }
            seek_Task.value = timeStamp;
            ACWorkThread.getInstance().executeTask(seek_Task);
        }
    }


    //时间轴 Seek 接口的监听
    private ACResultListener mSeekListener = new ACResultListener() {
        @Override
        public void onResult(ACResult acResult) {


            if (acResult.getCode() == ACResult.ACS_OK) {
                LogUtils.e("ZL", "seek 成功!!");
                mIsPause = false;
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setMyHorizontalScrollViewEnable(true);
                    }
                });
                mIsPause = true;
                mHandler.sendEmptyMessage(mCloseConver);
                LogUtils.e("ZL", "seek 失败!!===" + acResult.getCode());
                if (acResult.getCode() == -1205) {
                    showRestartConver(!mIsAppearFrame);
                    setPlayLiveEnable(true);
                }
                setNetworkSpeed(0);
            }
        }
    };

    /**
     * 根据滑动距离计算时间戳 单位:秒
     *
     * @param scrollX 滑动距离
     *                特别要注意精度损失问题
     */
    public long getTimeStamp(int scrollX) {
        return (mCurrentDayBeginTime + (long) ((scrollX - mStartScrollX) * mDistanceTime));
    }

    //---------------------------------------------------------控件动画-----------------------------------------------
    public void showTopAnimation(View view) {
        view.setVisibility(View.VISIBLE);
        if (mShowScaleAnimation == null) {
            mShowScaleAnimation = new ScaleAnimation(1, 1, 1.5f, 1f, Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_PARENT, 0.5f);
            mShowScaleAnimation.setDuration(300);
            mShowScaleAnimation.setInterpolator(new AccelerateInterpolator());
            mShowScaleAnimation.setFillAfter(false);
            mShowScaleAnimation.setAnimationListener(this);
        }
        view.startAnimation(mShowScaleAnimation);
    }

    public void hideTopAnimation(View view) {
        if (mHideTopScaleAnimation == null) {
            mHideTopScaleAnimation = new ScaleAnimation(1, 1, 1f, 1.5f, Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_PARENT, 0.5f);
            //mHideTopScaleAnimation=new TranslateAnimation(0,view.getWidth(),0,0);
            mHideTopScaleAnimation.setDuration(300);
            mHideTopScaleAnimation.setInterpolator(new AccelerateInterpolator());
            mHideTopScaleAnimation.setFillAfter(false);
            mHideTopScaleAnimation.setAnimationListener(this);
        }
        view.startAnimation(mHideTopScaleAnimation);
        view.setVisibility(View.GONE);
    }

    public void showRightAnimation(View view) {
        view.setVisibility(View.VISIBLE);
        if (mShowRightScaleAnimation == null) {
            mShowRightScaleAnimation = new ScaleAnimation(0.8f, 1f, 1f, 1f, Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_PARENT, 0.5f);
            //mShowRightScaleAnimation=new TranslateAnimation(-view.getWidth(),0,0,0);
            mShowRightScaleAnimation.setDuration(300);
            mShowRightScaleAnimation.setInterpolator(new AccelerateInterpolator());
            mShowRightScaleAnimation.setFillAfter(false);
            mShowRightScaleAnimation.setAnimationListener(this);
        }
        view.startAnimation(mShowRightScaleAnimation);
    }

    public void hideRightAnimation(View view) {
        if (mHideRightScaleAnimation == null) {
            mHideRightScaleAnimation = new ScaleAnimation(1f, 0.8f, 1f, 1f, Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_PARENT, 0.5f);
            //mHideRightScaleAnimation=new TranslateAnimation(0,view.getWidth(),0,0);
            mHideRightScaleAnimation.setDuration(300);
            mHideRightScaleAnimation.setInterpolator(new AccelerateInterpolator());
            mHideRightScaleAnimation.setFillAfter(false);
            mHideRightScaleAnimation.setAnimationListener(this);
        }
        view.startAnimation(mHideRightScaleAnimation);
        view.setVisibility(View.GONE);
    }

    public void showBottomAnimation(View view) {
        view.setVisibility(View.VISIBLE);
        view.setEnabled(true);
        if (mShowBottomScaleAnimation == null) {
            mShowBottomScaleAnimation = new ScaleAnimation(1, 1, 0.75f, 1f, Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_PARENT, 0.5f);
            mShowBottomScaleAnimation.setDuration(300);
            mShowBottomScaleAnimation.setInterpolator(new AccelerateInterpolator());
            mShowBottomScaleAnimation.setFillAfter(false);
            mShowBottomScaleAnimation.setAnimationListener(this);
        }
        view.startAnimation(mShowBottomScaleAnimation);
    }


    public void hideBottomAnimation(View view) {
        if (mHideBottomScaleAnimation == null) {
            mHideBottomScaleAnimation = new ScaleAnimation(1, 1, 1f, 0.75f, Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_PARENT, 0.5f);
            //mHideBottomScaleAnimation=new TranslateAnimation(0,0,0,view.getHeight());
            mHideBottomScaleAnimation.setDuration(300);
            mHideBottomScaleAnimation.setInterpolator(new AccelerateInterpolator());
            mHideBottomScaleAnimation.setFillAfter(false);
            mHideBottomScaleAnimation.setAnimationListener(this);
        }
        view.startAnimation(mHideBottomScaleAnimation);
        view.setVisibility(View.GONE);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }


    @Override
    public void onCameraLikeSuccess(GetKeyStoreBaseEntity entity) {
        setMenuEnable(mMenuEnable);
        setPlayLiveEnable(true);
        setCollected(!mCollected);
        if (mCollected) {
            ToastUtils.showShort(R.string.collect_success);
        } else {
            ToastUtils.showShort(R.string.uncollect_success);
        }
        Gson gson = new Gson();
        String toJson = gson.toJson(mCurrentRequest);
        SPUtils.getInstance().put(Constants.COLLECT_JSON, toJson);
        // new Handler().postDelayed(new Runnable() {
        //     @Override
        //     public void run() {
        //         EventBus.getDefault().post(new CollectChangeBean(), CAMERA_STATUS_CHANGE);
        //     }
        // }, 200);
    }

    @Override
    public void getRecordTimeSuccess(RecordLyCameraBean oldRecordSection) {

        StringBuilder sb = new StringBuilder();
        // final StringBuffer buffer = new StringBuffer();
        sb.append("topvdn://public.topvdn.cn?connectType=2&cid=");
        sb.append("<cid>");
        sb.append("&protocolType=3");
        sb.append("&token=");
        sb.append("<token>");
        sb.append("&begin=");
        sb.append(mStartTimeStamp);
        sb.append("&end=");
        sb.append("<end>");
        sb.append("&play=");
        sb.append("<play>");
        mRecordUrl = sb.toString();
        mRecordSection = null;
        if (null != oldRecordSection) {
            LogUtils.i("cxm", oldRecordSection.toString());
            Gson gson = new Gson();
            mSeekStamp = gson.toJson(oldRecordSection);
            mRecordSection = oldRecordSection;
            // long newCid = oldRecordSection.getCid();
            mRecordUrl = mRecordUrl.replace("<cid>", mCameraId + "");
            mRecordUrl = mRecordUrl.replace("<token>", mToken);
            if (Generate_Task == null) {
                Generate_Task = new Runnable() {
                    @Override
                    public void run() {
                        generateDate(mRecordStoreType);
                    }
                };
            }
            runOnUiThread(Generate_Task);
            setMyHorizontalScrollViewEnable(true);
            if (mRecordSection.getVideos() != null) {
                if (mRecordSection.getVideos().size() == 0) {
                    closeConver();
                    closeRestartConver();
                    setPlayLiveEnable(true);
                    showToastStr(R.string.error_no_videos);
                    showEmptyConver();
                    DateIndication(true);
                } else {
                    closeEmptyConver();
                    DateIndication(true);
                }
            }
            //获取当天的开始和结束时间
            getSomeDayBeginAndEndTimeStamp(mStartTimeStamp, mEndTimestamp);
            if (async_Task == null) {
                async_Task = new CustomRunnable2<Integer, Integer>() {
                    @Override
                    public void run() {

                        if (mRecordPlayer != null) {
                            mRecordPlayer.clearQueueBuffer(); //清空缓存
                        }
                        LogUtils.v("cxm", mRecordSection.getVideos().toString());
                        //描绘时间轴背景颜色
                        drawableScaleBackground(mRecordSection.getVideos());
                        closeStreamer();
                        mRecordUrl = mRecordUrl.replace("<end>", String.valueOf(mEndTimestamp));
                        mPlay[0] = mStartTimeStamp;
                        if (mRecordSection.getVideos() != null) {
                            if (mRecordSection.getVideos().size() > 0) {
                                //倒退
                                VideosBean videosBean = mRecordSection.getVideos().get(mRecordSection.getVideos().size() - 1);
                                long playTime = videosBean.getEnd() - 5 * 60;
                                long fromTime = videosBean.getBegin();
                                if (playTime <= videosBean.getBegin()) {
                                    mPlay[0] = fromTime;
                                } else {
                                    mPlay[0] = playTime;
                                }
                                ScrollToHorizationView(getScrollDistance(mPlay[0]));
                            }
                        }

                        //从中断的点继续播放
                        if (mBreakTimeStamp > mStartTimeStamp && mBreakTimeStamp < mEndTimestamp) {
                            mPlay[0] = mBreakTimeStamp;
                            LogUtils.v("cxm", "play = " + mPlay + ",mBreakTimeStamp = " + mBreakTimeStamp);
                        }

                        if (mRecordSection.getVideos() != null) {
                            if (mRecordSection.getVideos().size() > 0) {
                                mRecordUrl = mRecordUrl.replace("<play>", String.valueOf(mPlay[0]));
                                LogUtils.i("cxm", "openLiveUrl --- > mRecordUrl = " + mRecordUrl);
                                //将recordUrl写到文件
                                // FileUtils.writeFileFromString(Configuration.getRootPath() + "RecordUrl.txt", mRecordUrl, true);
                                openLiveUrl(mRecordUrl, mSeekStamp);//打开录像流媒体
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        showBottomAnimation();
                                        mIsSHow = true;
                                    }
                                }, 500);
                            }
                        }

                    }
                };
            }
            async_Task.start = mStartTimeStamp;
            async_Task.end = mEndTimestamp;
            ACWorkThread.getInstance().executeTask(async_Task);
        } else {
            setPlayLiveEnable(true);
        }
    }

    @Override
    public void getRecordToken(String token) {
        mToken = token;
    }

    @Override
    public void getRecordTimeFail() {
        setPlayLiveEnable(true);
        showRestartConver(!mIsAppearFrame);
        closeConver();
        setMyHorizontalScrollViewEnable(false);
    }

    public void showBottomAnimation() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!RecordActivity.this.isDestroyed()) {
                    showBottomAnimation(mFooter);
                    if (mIsFirst) {
                        ScrollDateHorizationView(Integer.parseInt("7"));
                        mIsFirst = false;
                    }
                }
            }
        });
    }

    /**
     * 打开硫化器相关操作
     *
     * @param playerUrl 播放地址
     * @param seekStamp 时间戳字符长串
     */
    public void openLiveUrl(String playerUrl, String seekStamp) {
        mLimitTime = 10;

        //LogUtils.e("ZL","录像播放址=="+playerUrl);

        /**
         * 没有下载完成
         * */
        mIsDownloadComplete = false;
        mStreamPackt = new ACStreamPacket();
        mRecordStreamer = ACStreamerFactory.createStreamer(ACProtocolType.AC_PROTOCOL_OSTP);
        mRecordStreamer.initialize(RecordActivity.this, new ACMessageListener() {
            @Override
            public void onMessage(int i, Object o) {

            }
        });


        if (mRecordStreamer != null) {
            //LogUtils.e("ZL","open 之前....");
            mRecordStreamer.open(playerUrl, 5000, seekStamp, new ACResultListener() {
                @Override
                public void onResult(ACResult acResult) { //打开流化器结果
                    if (acResult.getCode() == ACResult.ACS_OK) {
                        mIsPause = false;
                        mIsOpenStreamer = true;
                        startPlay(); //开始播放
                        // LogUtils.e("ZL","开流成功.....");


                        //开流倒计时
                        mStartTime = true;
                        if (mStartTime) {
                            Message message = Message.obtain();
                            message.what = mReadFail;
                            mHandler.sendMessage(message);
                        }

                    } else {
                        mIsOpenStreamer = false;
                        closeConver();//关闭loading封面
                        showRestartConver(!mIsAppearFrame);// 打开重连封面
                        setMenuEnable(false);
                        setPlayLiveEnable(true);
                    }
                }
            });
            //LogUtils.e("ZL","open 之后....");

        }
    }


    /**
     * 开始拉流播方（最核心的方法,极度容易出错，千万注意）
     * 一定要保证塞进player中的是正确的数据，不然容易出现花屏等一系列问题
     */

    public void startPlay() {
        if (mRecordThread == null) {
            mRecordThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    while (mPlaying) {

                        if (!mIsPause) {
                            // LogUtils.e("ZL","读数据....");
                            if (mRecordStreamer != null) {

                                ACResult result = null;

                                if (mRecordStreamer != null) {
                                    result = mRecordStreamer.read(mStreamPackt, 5000); //第二个参数是阅读超时单位好毫秒
                                }

                                // if (getView().mEventTimeStamp == 0) {
                                mBreakTimeStamp = mStreamPackt.timestamp / 1000;
                                // }

                                //LogUtils.e("ZL","录像读取的时间戳=="+mStreamPackt.timestamp);

                                if (result != null) {
                                    if (result.getCode() == ACResult.ACS_OK) {
                                        // LogUtils.w("ZL", "读取成功结果: 类型type=" + mStreamPackt.type + ",size=" + mStreamPackt.size + ", buffer=" + mStreamPackt.buffer);
                                        mToltal = mToltal + mStreamPackt.size;
                                        if (mStartTime) {
                                            mHandler.sendEmptyMessage(Message.obtain().what = mReadSuccess);
                                        }
                                    } else {
                                        mStreamPackt.size = 0;
                                        //连接断开了，需要重新连接
                                        // LogUtils.e("ZL", "读取失败结果:" + result.getCode());
                                        if (result.getCode() == ACResult.ACC_RECORD_READ_FAILED) { //-1205
                                            ErrorCode1205();
                                        }
                                        if (result.getCode() == ACResult.ACC_RECORD_SEEK_FAILED) {
                                            LogUtils.e("ZL", "seek Failed------>");
                                        }

                                        if (result.getCode() == -1212) {
                                            mIsDownloadComplete = true; //下载完成
                                            //录像播放完成会无限播放此方法，一定不要在里面做UI操作！！！
                                            // setNetworkSpeed(0);
                                        }
                                    }
                                }
                            }

                            if (mRecordPlayer != null) {
                                ACResult result = null;
                                if (mStreamPackt.size > 0) {
                                    //LogUtils.e("ZL","steamer="+DateTimeUtils.getSimpleDate(mStreamPackt.timestamp,"mm:ss"));
                                    result = mRecordPlayer.playFrame(mStreamPackt);
                                    if (result != null) {
                                        // LogUtils.w("ZL", "播放信息" + result.getCode());
                                    }
                                }
                                if (result != null) {
                                    int code = result.getCode();
                                    // LogUtils.w("ZL", "code ---- " + code);
                                    if (code == ACResult.ACS_INSUFFICIENT_BUFFER) {  //播放器的缓存队列塞满了,就休息100毫秒，主要是播放录像的时候！
                                        while (code == ACResult.ACS_INSUFFICIENT_BUFFER) {
                                            if (mRecordPlayer != null) {
                                                code = mRecordPlayer.playFrame(mStreamPackt).getCode();
                                            }
                                            try {
                                                Thread.sleep(100);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }

                            }

                        } else {
                            /*LogUtils.e("ZL","暂停读数据....");
                            try {
                                Thread.sleep(30);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }*/
                        }

                    }

                    LogUtils.e("ZL", "线程结束......");

                }
            });
        }

        if (!mRecordThreadStarted) {
            mRecordThread.start();
            mRecordThreadStarted = true;
            //LogUtils.e("ZL","mRecordThread start()方法调用了");
        } else {
            //LogUtils.e("ZL","mRecordThread start()已经调用了 Thread.start()");
        }
    }


    //刻画时间轴背景
    private void drawableScaleBackground(List<VideosBean> sections) {
        ArrayList<MutilColorDrawable.MutilColorEntity> colors = new ArrayList<>();
        if (sections != null) {
            if (sections.size() > 0) {
                for (VideosBean sectionData : sections) {
                    if (sectionData == null)
                        break;
                    float fromPercent = (float) (sectionData.getBegin() - mCurrentDayBeginTime) / mDayTime;
                    float endPercent = (float) (sectionData.getEnd() - mCurrentDayBeginTime) / mDayTime;
                    LogUtils.e("ZL", "fromPercent=" + fromPercent + "   endPercent" + endPercent);
                    colors.add(new MutilColorDrawable.MutilColorEntity(fromPercent * 100f, endPercent * 100f, Color.parseColor("#E16E2B")));
                }
            }
        }

        MutilColorDrawable drawable = new MutilColorDrawable(colors);
        initScaleBackground(drawable);
    }

    //设置刻度尺的背景颜色
    public void initScaleBackground(final Drawable drawable) {
        if (drawable == null || mScaleBackground == null)
            return;
        mScaleBackground.post(new Runnable() {
            @Override
            public void run() {
                mScaleBackground.setImageDrawable(drawable);
                LogUtils.e("ZL", "设置背景了");
            }
        });
    }

    /**
     * 获取当天时间轴的0点和23：59：59的时间戳
     **/
    private void getSomeDayBeginAndEndTimeStamp(long start, long end) {

        mCurrentDayBeginTime = start;
        mCurrentDayEndTime = end;

    }

    /**
     * 选中的日期 更改颜色
     *
     * @param canColor 是否能着色
     */
    Runnable DateIndication_Task;
    private CustomRunnable2 async_Task;

    public void DateIndication(boolean canColor) {
        if (canColor) {
            if (mCalandar != null) {
                if (DateIndication_Task == null) {
                    DateIndication_Task = new Runnable() {
                        @Override
                        public void run() {

                            //LogUtils.e("ZK","点亮==mPre_index"+mPre_index+"  mIndex"+mIndex);
                            if (mPre_index >= 1 && mPre_index <= mCalandar.getChildCount() - 2) {
                                ((TextView) mCalandar.getChildAt(mPre_index)).setTextColor(getResources().getColor(R.color.tab_unselect_color));
                            }

                            if (mIndex >= 1 && mIndex <= mCalandar.getChildCount() - 2) {
                                ((TextView) mCalandar.getChildAt(mIndex)).setTextColor(Color.WHITE);
                                ScrollDateHorizationView(mIndex);
                            }
                            mPre_index = mIndex;

                            //LogUtils.e("ZL","时间日期移动了....");
                        }
                    };
                }
                runOnUiThread(DateIndication_Task);
            }
        } else {
            //当天没有录像----------
            closeConver();
            closeRestartConver();
            showEmptyConver();
        }
    }

    /**
     * 显示 空白界面（当天没有录像）
     */
    Runnable mEmptyConver_Task;

    public void showEmptyConver() {
        if (mEmptyConver != null) {
            if (mEmptyConver.getVisibility() == View.INVISIBLE || mEmptyConver.getVisibility() == View.GONE) {
                if (mEmptyConver_Task == null) {
                    mEmptyConver_Task = new Runnable() {
                        @Override
                        public void run() {
                            mEmptyConver.setVisibility(View.VISIBLE);
                        }
                    };
                }
                runOnUiThread(mEmptyConver_Task);
            }
        }
    }

    /**
     * 弹出Toast
     */
    Runnable mToastTask;

    public void showToastStr(final int strId) {
        if (mToastTask == null) {
            mToastTask = new Runnable() {
                @Override
                public void run() {
                    ToastUtils.showShort(getString(strId));
                }
            };
        }
        runOnUiThread(mToastTask);
    }

    /**
     * 设置刻度尺是否能相应事件
     */
    public void setMyHorizontalScrollViewEnable(boolean enable) {
        if (mMyHorizontalScrollView != null) {
            mMyHorizontalScrollView.setEnabled(enable);
            mMyHorizontalScrollView.setCanScroll(enable);
        }
    }

    private Runnable Generate_Task;


    /**
     * 动态生成日期
     */
    public void generateDate(int RecordType) {
        //LogUtils.e("ZL","RecordType=="+RecordType);
        if (mCalandar.getChildCount() == 0) {
            //添加头
            final TextView mHeader = new TextView(RecordActivity.this);
            mHeader.setTextColor(Color.argb(0, 0, 0, 0));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mHalfScreen, LinearLayout.LayoutParams.MATCH_PARENT);
            mHeader.setLayoutParams(params);
            mCalandar.addView(mHeader);
            int gray = getResources().getColor(R.color.tab_unselect_color);
            for (int i = 1, j = RecordType; i <= RecordType; i++, j--) {
                TextView textView;
                if (i != RecordType) {
                    textView = createTextView(TimeUtils.millis2String(TimeUtils.getPreDayBegin(j - 1) * 1000, "MM/dd"), gray);
                } else {
                    textView = createTextView(TimeUtils.millis2String(TimeUtils.getPreDayBegin(j - 1) * 1000, "MM/dd"), Color.WHITE);
                    mIndex = RecordType;
                }
                //DateTimeUtils.getPreDayBegin(i)
                textView.setTag(R.id.tag_first, new Long(TimeUtils.getPreDayBegin(j - 1)));
                textView.setTag(R.id.tag_second, new Long(TimeUtils.getPreDayEnd(j - 1)));
                textView.setTag(R.id.index, new Integer(i));
                textView.setId(R.id.text_view);
                textView.setOnClickListener(RecordActivity.this);
                mCalandar.addView(textView);
            }

            //添加尾部
            TextView mFooter = new TextView(RecordActivity.this);
            mHeader.setTextColor(Color.argb(0, 0, 0, 0));
            LinearLayout.LayoutParams mfooterarams = new LinearLayout.LayoutParams(mHalfScreen, LinearLayout.LayoutParams.MATCH_PARENT);
            mFooter.setLayoutParams(mfooterarams);
            mCalandar.addView(mFooter);
        }
    }

    private TextView createTextView(String date, int color) {
        TextView textView = new TextView(RecordActivity.this);
        textView.setTextColor(color);
        textView.setText(date);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_medium));
        textView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) (mTextViewWith * mDensityFloat), LinearLayout.LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(params);
        return textView;
    }

    //实现对网路的监听(观看录像时断网了）
    class NetWorkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //网络状态变化了
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                if (NetworkUtils.isConnected()) {
                    // nowTime();//再次开启计时
                    setNetworkSpeed(0);
                } else {
                    Toast.makeText(RecordActivity.this, "请检查你的网络....", Toast.LENGTH_SHORT).show();
                    if (mIsRecording) {
                        stopRecord(false);
                    }
                    setMenuEnable(false);
                    setPlayLiveEnable(true);
                    wifiDismiss();
                }
            }

        }
    }

    /**
     * Wifi 断开了
     */
    public void wifiDismiss() {
        ErrorCode1205();
    }

    /**
     * 硫化器出现错误码-1205的时候
     */
    private void ErrorCode1205() {
        mIsPause = true; //暂停线程的读取
        //出现重连,立马清空播放器,让画面停止。
        if (mRecordPlayer != null) {
            mRecordPlayer.clearQueueBuffer();
        }
        closeConver();
        showRestartConver(!mIsAppearFrame);
        setPlayLiveEnable(true);
    }

    private int mRecordTime = 0;
    private Handler mRecordHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mRecordIv.getVisibility() == View.VISIBLE) {
                mRecordIv.setVisibility(View.INVISIBLE);
            } else {
                mRecordIv.setVisibility(View.VISIBLE);
            }
            mRecordTime += 1;
            int second = mRecordTime % 60;
            int minute = mRecordTime / 60;
            int hour = mRecordTime / 3600;
            mRecordTv.setText(TimeUtils.fillZero(hour) + ":" + TimeUtils.fillZero(minute) + ":" + TimeUtils.fillZero(second));
            mRecordHandler.sendEmptyMessageDelayed(1, 1000);
        }
    };

    private void stopRecord(boolean isOnPause) {
        mRecordPlayer.stopRecord();
        mIsRecording = false;
        mRecordRl.setVisibility(View.GONE);
        mRecordHandler.removeMessages(1);
        if (mRecordTime < 2) {
            if (!isOnPause) {
                ToastUtils.showShort("录制时间过短");
            }
            mVideoFile.delete();
        } else {
            if (!isOnPause) {
                ToastUtils.showShort("录制完成，路径为:" + mVideoFile.getAbsolutePath());
            } else {
                mPlayVideo.setChecked(true);
            }
            DeviceUtil.galleryAddMedia(mVideoFile.getAbsolutePath());
        }
        // setViewEnable(true);
        setMenuEnable(true);
        setPlayLiveEnable(true);
        mRecordTime = 0;
    }

    /**
     * 下载速度显示控件
     *
     * @param speed 下载速度 KB/s
     */
    Runnable mNetworkSpeed_Task;

    public void setNetworkSpeed(final int speed) {
        if (mNetworkSpeed != null) {
            if (mNetworkSpeed_Task == null) {
                mNetworkSpeed_Task = new Runnable() {
                    @Override
                    public void run() {
                        // mNetworkSpeed.setText(speed + "kB/s");
                    }
                };
            }
            runOnUiThread(mNetworkSpeed_Task);
        }
    }

    //获取SDK里面的录像的播放进度
    private Runnable mSDKPlayGrassTask = new Runnable() {
        @Override
        public void run() {
            if (mRecordPlayer != null) {
                if (!mIsIsPauseGetPlayPosition) {
                    long timestamp = mRecordPlayer.getPosition();
                    if (timestamp > 0) {
                        setPlayTime(df.format(timestamp));
                        int scrollx = getScrollDistance(timestamp / 1000);
                        // LogUtils.e("ZL","移动距离="+scrollx);
                        if (!mIsSeek) {
                            ScrollToHorizationView(scrollx);
                        }
                    }
                }
                mHandler.postDelayed(this, 1000);
            }
        }
    };

    //设置顶部的播放时间
    public void setPlayTime(final String time) {
        if (mTime != null) {
            if (mIsSHow) {
                mTime.setText(time);
            }
        }
    }

    /**
     * 根据时间戳计算滑动距离 单位：像素
     *
     * @param timeStamp 时间戳 单位秒
     */
    public int getScrollDistance(long timeStamp) {
        return (int) ((timeStamp - mCurrentDayBeginTime) / mDistanceTime) + mStartScrollX;
    }

    //滑动HorizationView
    public void ScrollToHorizationView(int scrollX) {
        if (mMyHorizontalScrollView != null) {
            int max = 720 * mScaleView.mScaleMargin;//能滑动的最大距离
            if (scrollX > max) {
                scrollX = max;
            }
            mMyHorizontalScrollView.scrollTo(scrollX, 0);
        }
    }

    public void removeAllHandler() {
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    public void removeAnimation() {
        if (null != mHideRightScaleAnimation) {
            mHideRightScaleAnimation.reset();
            mHideRightScaleAnimation = null;
        }
        if (null != mHideTopScaleAnimation) {
            mHideTopScaleAnimation.reset();
            mHideTopScaleAnimation = null;
        }
        if (null != mShowScaleAnimation) {
            mShowScaleAnimation.reset();
            mShowScaleAnimation = null;
        }
        if (null != mShowRightScaleAnimation) {
            mShowRightScaleAnimation.reset();
            mShowRightScaleAnimation = null;
        }
        if (null != mShowBottomScaleAnimation) {
            mShowBottomScaleAnimation.reset();
            mShowBottomScaleAnimation = null;
        }
        if (null != mHideBottomScaleAnimation) {
            mHideBottomScaleAnimation.reset();
            mHideBottomScaleAnimation = null;
        }
    }

    public void joinMainThread() {
        if (mRecordThread != null) {
            //mRecordThread.join();
            mRecordThread = null;
            mRecordThreadStarted = false;
        }
        LogUtils.e("ZL", "直播线程join....");
    }

    /**
     * 每秒钟从硫化器(Streamer)中读取的数据量
     */
    private Runnable nowTask = new Runnable() {
        @Override
        public void run() {

            if (!mIsPause) { //读取硫化器数据的线程暂停与否
                long m = mToltal;
                if (m > 1000) {
                    mNetworkSpeed.setText(m / 1000 + "kB/s");
                } else {
                    mNetworkSpeed.setText(m + "B/s");
                }
                mToltal = 0L;
            }
            if (null != mTime) {
                mTime.postDelayed(this, 1000);
            }
        }
    };

    public void closePlayer() {
        if (mRecordPlayer != null) {
            mRecordPlayer.clearQueueBuffer();
            mRecordPlayer.release();
            mIsAppearFrame = false;
            mBreakTimeStamp = 0L; //清空播放的时间点(每次退出该页面的时候）
            //            mRecordPlayer = null;
            mPlay[0] = 0l;
        }
    }

    private void nowTime() {
        if (mTime != null) {
            mTime.postDelayed(nowTask, 1000);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtils.e("ZL", "OnStart...");
        nowTime();//计时开始
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtils.e("ZL", "OnRestart....");
        mIsPause = !mIsOpenStreamer;

    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsOnPause = false;
        // if (mWakeLock != null) {
        //     mWakeLock.acquire();
        // }

        if (mRecordPlayer != null) {
            mRecordPlayer.enterForeground();  //切入前台
            if (mPlayAudio.isChecked()) {
                mRecordPlayer.unmute();
            }
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        mIsPause = true;//停止读数据

        if (mRecordPlayer != null) {
            mRecordPlayer.mute();
        }
    }

    private boolean mIsOnPause;

    @Override
    protected void onPause() {
        super.onPause();
        if (mIsRecording) {
            mIsOnPause = true;
            mPlayVideo.setChecked(true);
        }
        if (mRecordPlayer != null) {
            mRecordPlayer.enterBackground();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSeekListener = null;
        removeAllHandler();
        removeAnimation();
        mBackHandler.removeMessages(TYPE_BACK_CONTRAL);
        ACWorkThread.getInstance().executeTask(new Runnable() {
            @Override
            public void run() {
                readRecordThread(false);
                closeStreamer(); //关闭硫化器
                joinMainThread();
            }
        });
        if (mTime != null) {
            mTime.removeCallbacks(nowTask);
        }
        closePlayer();   //关闭播放器
        unregisterReceiver(mNetWorkReceiver);
        if (mMyHorizontalScrollView != null) {
            mMyHorizontalScrollView.handler.removeCallbacksAndMessages(null);
        }

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (null != mRecordHandler) {
            mRecordHandler.removeCallbacksAndMessages(null);
        }
    }
}
