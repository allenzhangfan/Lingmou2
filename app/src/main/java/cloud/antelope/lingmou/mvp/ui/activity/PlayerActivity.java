package cloud.antelope.lingmou.mvp.ui.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.antelope.sdk.ACMessageListener;
import com.antelope.sdk.ACResult;
import com.antelope.sdk.ACResultListener;
import com.antelope.sdk.capturer.ACShape;
import com.antelope.sdk.codec.ACCodecID;
import com.antelope.sdk.codec.ACStreamPacket;
import com.antelope.sdk.player.ACPlayer;
import com.antelope.sdk.player.ACPlayerExtra;
import com.antelope.sdk.player.ACPlayerStatus;
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
import com.lingdanet.safeguard.common.utils.TimeUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.lingdanet.safeguard.common.utils.Utils;
import com.lingdanet.safeguard.common.utils.WaterMarkUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.di.component.DaggerPlayerComponent;
import cloud.antelope.lingmou.di.module.PlayerModule;
import cloud.antelope.lingmou.mvp.contract.PlayerContract;
import cloud.antelope.lingmou.mvp.model.entity.CameraNewStreamEntity;
import cloud.antelope.lingmou.mvp.model.entity.CollectCameraEntity;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;
import cloud.antelope.lingmou.mvp.model.entity.PlayEntity;
import cloud.antelope.lingmou.mvp.model.entity.SetKeyStoreRequest;
import cloud.antelope.lingmou.mvp.presenter.PlayerPresenter;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.EasyPermissions;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class PlayerActivity extends BaseActivity<PlayerPresenter> implements PlayerContract.View,
        View.OnClickListener,
        CompoundButton.OnCheckedChangeListener,
        EasyPermissions.PermissionCallbacks,
        Animation.AnimationListener {

    /**
     * 图片最大边界
     */
    public static final int[] MAX_IMAGE_BOUNDS = new int[]{1920, 1080};

    public static final String mStoragePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final int TYPE_BACK_CONTRAL = 0x10;

    private FrameLayout mHeader;
    private LinearLayout mMenu;
    public GLSurfaceView mPlayerGLSurfaceView;
    private boolean mIsSHow = true; //顶部和底部是否显示
    private Bundle mBundle;
    private FrameLayout mStart_Cover; //进入播放界面的过度视图
    private FrameLayout mRestart_Conver;
    private ImageView mConver;  //封面loading
    private ImageView mRestart_Bg;//封面
    public OrgCameraEntity mBean;
    private TextView mCameraName, mTime, mNetworkSpeed;
    private SimpleDateFormat df = new SimpleDateFormat("直播 | MM/dd HH:mm:ss");
    private NetWorkReceiver mNetWorkReceiver = null;
    private LinearLayout mNetwork_tip; //网络提示
    private CheckBox mAudio;//声音
    // PowerManager.WakeLock mWakeLock = null;
    // 照相声音播放
    MediaPlayer mMediaPlayer;
    private CheckBox mPlayVideo;
    private CheckBox mPlayCutCamera;
    private CheckBox mPlayRecord;
    private ImageButton mCollectVideo;
    private RelativeLayout mRecordRl;
    private TextView mRecordTv;
    private ImageView mRecordIv;
    //    private boolean mIsCustomer;
    private File mVideoFile;
    private boolean mIsOnPause;
    private boolean mCollected;
    private String mPlayerUrl = "rtmp://rtmp4.public.topvdn.cn:1935/live/10000004";//视频播放地址
    private ACPlayer mPlayer;

    //--player------ 静态数据方便上个页面修改--------
    private int mSampleHz = 16000;
    private int mChannelCount = 1;
    private int mPlayerStart = 1000;  //播放器开始播放最小时间
    private int mPlayerDrop = 3000; //播放器开始丢帧的最小时间
    private int mAudioType = ACCodecID.AC_CODEC_ID_AAC; //音频为AAC类型的。
    private long mLastTime = 0L; //上一次从硫化器中读取数据成功的时刻
    public long mToltal = 0L; //当前网速
    public boolean mIsAppearFrame = false; //GLSurface中是否出现了画面
    public boolean mIsPause = true;
    private boolean mPlaying = true;
    private Long mCameraId;
    private ACStreamer mACStreamer;
    private ACStreamPacket mStreamPackt = null;
    public boolean mIsOpenStreamer = false;//是否已经打开硫化器了
    private GetKeyStoreEntity mKeyStoreEntity;
    private ArrayList<CollectCameraEntity> mCollectionCameras;
    private SetKeyStoreRequest mKeyStore;
    private SetKeyStoreRequest mCurrentRequest;
    /**
     * 这个Handler主要用于计时 10秒内没有打开录像就关闭
     */
    private final int mReadFail = 0; //读取失败
    private final int mReadSuccess = 1; //读取成功
    private boolean mStartTime = true; //计时10秒钟有没有读到数据
    private int mLimitTime = 10; //10秒
    private Thread mLiveThread;  //直播线程
    private Animation mShowScaleAnimation, mHideTopScaleAnimation;  //顶部动画
    private Animation mShowRightScaleAnimation, mHideRightScaleAnimation;   //右边动画
    /**
     * 设置表示位用于判断线程有没有start
     * 不管Thread.isAlive()返回的是true还是false，如果我们再次Thread.start()，就会出现 java.lang.IllegalThreadStateException
     */
    private boolean mLiveThreadStartd = false;

    private Bitmap mLandscapeMarkerBitmap;

    private boolean mIsPlayOrFail = false;

    private Handler mBackHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mIsPlayOrFail = true;
        }
    };

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == mReadFail) {
                mLimitTime--;
                LogUtils.e("ZL", "计时==" + mLimitTime);
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
                LogUtils.e("ZL", "计时==|||||| 倒计时完毕");
                // setMenuEnable(true);
                // setCollectRecordEnable(true);
                mStartTime = false;
            }
        }
    };


    //关闭任务(超过10秒还没有读取到任务）
    private Runnable closeTask = new Runnable() {
        @Override
        public void run() {
            closeConver();
            showRestartConver();
            setCollectRecordEnable(true);
            closeStreamer();
        }
    };

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        DaggerPlayerComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .playerModule(new PlayerModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_player; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mBundle = getIntent().getExtras();
        if (mBundle != null) {
            mBean = (OrgCameraEntity) mBundle.getSerializable("data");
            mCameraId = mBean.getManufacturerDeviceId();
        } else {
            LogUtils.e("cxm", "Intent 传过来的对象为空....");
        }
        mMediaPlayer = MediaPlayer.create(this, R.raw.music_image_cut_);
        initTheView();
        initLivePlayer(mPlayerGLSurfaceView, mPlayerUrl);
        initListener();
        showConver(); //显示截图封面
        registeretWorkReceiver();//注册网络监听器
        readLiveThread(true);
        // CameraItem item = DataSupport.where("cameraid = ?", mCameraId+"").findFirst(CameraItem.class);
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
                    setCollected(true);
                }
                entity.setCid(cid);
                entity.setCameraName(group_two[1]);
                if (!mCollectionCameras.contains(entity)) {
                    mCollectionCameras.add(entity);
                }
            }
        } else {
            setCollected(false);
        }
        // if (null != item) {
        //     //标明已收藏
        //     setCollected(true);
        // } else {
        //     setCollected(false);
        // }
        setMenuEnable(false);
        setCollectRecordEnable(false);
        mPresenter.getStreamUrl(mCameraId);
        if (!NetworkUtils.isWifiConnected()) {
            Toast.makeText(this, getString(R.string.see_video_2_3_4), Toast.LENGTH_SHORT).show();
        }
        mBackHandler.sendEmptyMessageDelayed(TYPE_BACK_CONTRAL, 2500);
    }

    public void setCollected(boolean isCollect) {
        mCollected = isCollect;
        int resId = isCollect ? R.drawable.video_collected : R.drawable.video_uncollected;
        mCollectVideo.setImageResource(resId);
    }

    //这个在OnCreate和OnDestroy中调用即可,保证子线程一直存在
    public void readLiveThread(boolean read) {
        mPlaying = read;
    }

    //展现loading..封面
    public void showConver() {
        if (mStart_Cover != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mStart_Cover.getVisibility() == View.GONE || mStart_Cover.getVisibility() == View.INVISIBLE) {
                        mStart_Cover.setVisibility(View.VISIBLE);
                    }
                }
            });

        }
    }

    //注册监听网络的接收器
    private void registeretWorkReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mNetWorkReceiver = new NetWorkReceiver();
        registerReceiver(mNetWorkReceiver, filter);
    }

    private void initTheView() {
        mHeader = (FrameLayout) findViewById(R.id.header);
        mPlayerGLSurfaceView = (GLSurfaceView) findViewById(R.id.play_screen);
        mStart_Cover = (FrameLayout) findViewById(R.id.start_cover);
        mRestart_Conver = (FrameLayout) findViewById(R.id.player_restart_conver);
        mConver = (ImageView) findViewById(R.id.start_image);
        mRestart_Bg = (ImageView) findViewById(R.id.restart_image);
        mCameraName = (TextView) findViewById(R.id.play_source);
        mNetwork_tip = (LinearLayout) findViewById(R.id.network_tip);
        mNetworkSpeed = (TextView) findViewById(R.id.speed);
        mCameraName.setText(mBean.getDeviceName());
        mTime = (TextView) findViewById(R.id.time);
        // mVideoQuality = (Spinner) findViewById(R.id.select_video);
        mRecordRl = (RelativeLayout) findViewById(R.id.record_rl);
        mRecordTv = (TextView) findViewById(R.id.record_time_tv);
        mRecordIv = (ImageView) findViewById(R.id.record_iv);
        if (mMenu == null) {  //懒加载
            ViewStub viewStub = (ViewStub) findViewById(R.id.header_view_stub);
            viewStub.setLayoutInflater(getLayoutInflater());
            viewStub.inflate();
            mMenu = (LinearLayout) findViewById(R.id.menu);
            mAudio = ((CheckBox) findViewById(R.id.play_audio));
            mAudio.setOnCheckedChangeListener(PlayerActivity.this); //声音按钮
            mPlayVideo = (CheckBox) findViewById(R.id.play_video);
            mPlayVideo.setOnCheckedChangeListener(PlayerActivity.this); //录像按钮
            mPlayCutCamera = (CheckBox) findViewById(R.id.play_camera);
            mPlayCutCamera.setOnCheckedChangeListener(PlayerActivity.this); //截图按钮
            mPlayRecord = (CheckBox) findViewById(R.id.play_record);
            mPlayRecord.setVisibility(View.VISIBLE);
            mPlayRecord.setOnCheckedChangeListener(PlayerActivity.this); //播放录像按钮
            mCollectVideo = (ImageButton) findViewById(R.id.collect_video);
            mCollectVideo.setOnClickListener(this);
        }
    }

    private void initListener() {
        mPlayerGLSurfaceView.setOnClickListener(this);
        findViewById(R.id.play_finish).setOnClickListener(PlayerActivity.this);  //设置返回监听事件
        findViewById(R.id.play_restart).setOnClickListener(PlayerActivity.this); //重连操作
        findViewById(R.id.play_help).setOnClickListener(PlayerActivity.this); //重连操作
        findViewById(R.id.net_work_cancle).setOnClickListener(this); //网络提示取消按钮
    }

    //初始化直播Player
    public void initLivePlayer(GLSurfaceView playerGLSurfaceView, String playerUrl) {
        mPlayerUrl = playerUrl;
        mPlayer = new ACPlayer();

        if (mPlayer != null) {
            mPlayer.setPlaySurfaceView(playerGLSurfaceView, ACShape.AC_SHAPE_NONE);
        }

        ACPlayer.Config.Builder builder = new ACPlayer.Config.Builder();
        builder.setStartDropBufferSize(mPlayerStart)
                .setStartDropBufferSize(mPlayerDrop)
                .setAudioCodecId(mAudioType)
                .setVideoCodecId(ACCodecID.AC_CODEC_ID_H264)
                .setSampleRate(mSampleHz)
                .setChannelCount(mChannelCount);
        ACResult result = mPlayer.initialize(builder.create(), mACPlayerExtra);
        handleResult(result);

    }

    private void handleResult(ACResult acResult) {
        if (acResult != null) {
            if (acResult.getCode() != ACResult.ACS_OK) {
                final StringBuffer buffer = new StringBuffer();
                buffer.append("Error Code:");
                buffer.append(acResult.getCode() + "\n");
                buffer.append("Code Description:");
                buffer.append(acResult.getCodeDesc() + "\n");
                buffer.append("Error Msg:");
                buffer.append(acResult.getErrMsg() + "\n");
                LogUtils.e("cxm", buffer.toString());
            }
        }
    }


    private ACPlayerExtra mACPlayerExtra = new ACPlayerExtra() {
        @Override
        public void onPlayerStatus(int status) {
            //开始播放，播放成功
            if (status == ACPlayerStatus.ACPlayerStatusStartPlay) {
                LogUtils.e("ZL", "mACPlayerExtra......" + ACPlayerStatus.ACPlayerStatusStartPlay);
                mIsAppearFrame = true;
                closeConver();//关闭封面(第一次的时候）
                setMenuEnable(true);
                setCollectRecordEnable(true);
            }
        }
    };


    //移除loading...封面
    public void closeConver() {
        if (mStart_Cover != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mStart_Cover.getVisibility() == View.VISIBLE) {
                        mStart_Cover.setVisibility(View.GONE);
                        mIsPlayOrFail = true;
                    }
                }
            });

        }
    }

    public void setMenuEnable(final boolean isEnable) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null != mAudio) {
                    mAudio.setEnabled(isEnable);
                }
                if (null != mPlayCutCamera) {
                    mPlayCutCamera.setEnabled(isEnable);
                }
                if (null != mPlayVideo) {
                    mPlayVideo.setEnabled(isEnable);
                }
            }
        });

    }


    public void setCollectRecordEnable(final boolean isEnable) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (null != mCollectVideo) {
                    mCollectVideo.setEnabled(isEnable);
                    mCollectVideo.setClickable(isEnable);
                }
                if (null != mPlayRecord) {
                    mPlayRecord.setEnabled(isEnable);
                }
            }
        });
    }

    @Override
    public void showLoading(String message) {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(@NonNull String message) {
        showRestartConver();
        closeConver();
        setCollectRecordEnable(true);
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

    private boolean mIsRecording = false;
    private boolean mMenuEnable = false;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_screen:
                if (mIsSHow) {
                    hideTopAnimation(mHeader);
                    hideRightAnimation(mMenu);
                    mIsSHow = false;
                } else {
                    showTopAnimation(mHeader);
                    showRightAnimation(mMenu);
                    mIsSHow = true;
                }
                break;
            case R.id.play_finish:
                //返回back 界面
                if (mIsRecording) {
                    ToastUtils.showShort("正在录制视频，请稍后");
                } else if (!mIsPlayOrFail) {

                }else{
                    finish();
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
                showConver(); //显示load....页面
                closeRestartConver(); //关闭点击封面
                if (null != mBean) {
                    mPresenter.getStreamUrl(mCameraId);
                }
                break;
            case R.id.net_work_cancle:
                //网络提示取消
                closeNetworkTip();//关闭网络提示
                break;
            case R.id.collect_video:
                //点击收藏 或者取消收藏
                mMenuEnable = mPlayCutCamera.isEnabled();
                setMenuEnable(false);
                setCollectRecordEnable(false);
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
                    /*
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


    //关闭重连画面
    public void closeRestartConver() {
        if (mRestart_Conver != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mRestart_Conver.getVisibility() == View.VISIBLE) {
                        mRestart_Conver.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.play_audio:
                //声音
                if (isChecked) {
                    if (mPlayer != null) {
                        mPlayer.unmute();//取消静音
                    }
                } else {
                    if (mPlayer != null) {
                        mPlayer.mute();//静音（停止播放音频）
                    }
                }
                break;
            case R.id.play_video:
                if (EasyPermissions.hasPermissions(PlayerActivity.this, mStoragePermission)) {
                    //录制视频
                    String photoDir = Configuration.getRootPath() + Constants.RECORD_PATH;
                    File dir = new File(photoDir);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    mVideoFile = new File(dir, System.currentTimeMillis() + "_" + mBean.getSn() + ".mp4");
                    if (!mIsRecording) {
                        mPlayer.startRecord(mVideoFile.getAbsolutePath(), 1);
                        mIsRecording = true;
                        mPlayRecord.setEnabled(false);
                        mRecordRl.setVisibility(View.VISIBLE);
                        mRecordHandler.sendEmptyMessageDelayed(1, 1000);
                        setViewEnable(false);
                    } else {
                        stopRecord(mIsOnPause);
                    }
                } else {
                    mPlayVideo.setChecked(true);
                    ToastUtils.showShort("请前往设置打开存储权限");
                    // EasyPermissions.requestPermissions(PlayerActivity.this, getString(R.string.permisson_storage), PERMISSION_STORAGE, mStoragePermission);
                }
                break;
            case R.id.play_camera:
                if (EasyPermissions.hasPermissions(PlayerActivity.this, mStoragePermission)) {
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
                    final File picFile = new File(dir, System.currentTimeMillis() + "_" + mBean.getSn() + ".jpeg");
                    snapShot(picFile.getAbsolutePath(), new IOnSnapListener() {
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
                }
                break;
            case R.id.play_record:
//                boolean hasVideoHistory = SPUtils.getInstance().getBoolean(Constants.PERMISSION_HAS_VIDEO_HISTORY, false);
//                if (hasVideoHistory) {
                    //回放
                    Intent intent = new Intent(this, RecordActivity.class);
                    intent.putExtras(mBundle);
                    startActivity(intent);
                    finish();
//                } else {
//                    ToastUtils.showShort(R.string.hint_no_permission);
//                }

                break;
        }

    }


    public interface IOnSnapListener {

        void snapSuccess();

        void snapFail(String msg);
    }

    public void snapShot(final String path, IOnSnapListener listener) {
        if (!TextUtils.isEmpty(path)) {
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


    private void stopRecord(boolean isOnPause) {
        mPlayer.stopRecord();
        mIsRecording = false;
        mPlayRecord.setEnabled(true);
        mRecordRl.setVisibility(View.GONE);
        mRecordHandler.removeMessages(1);
        if (mRecordTime < 2) {
            if (!isOnPause) {
                ToastUtils.showShort("录制时间过短");
            }
            mVideoFile.delete();
        } else {
            DeviceUtil.galleryAddMedia(mVideoFile.getAbsolutePath());
            if (!isOnPause) {
                ToastUtils.showShort("录制完成，路径为:" + mVideoFile.getAbsolutePath());
            }
        }
        setViewEnable(true);
        mRecordTime = 0;
    }

    private Runnable enableTask = new Runnable() {
        @Override
        public void run() {
            /*if (mVideoQuality.getVisibility() == View.VISIBLE) {
                mVideoQuality.setEnabled(true);
            }*/
            /*if (mAudio != null) {
                mAudio.setClickable(true);
                mAudio.setEnabled(true);
            }*/
            if (mPlayCutCamera != null) {
                mPlayCutCamera.setEnabled(true);
            }
            if (mPlayRecord != null) {
                mPlayRecord.setEnabled(true);
            }
        }
    };

    private Runnable unEnableTask = new Runnable() {
        @Override
        public void run() {
            /*if (mVideoQuality.getVisibility() == View.VISIBLE) {
                mVideoQuality.setEnabled(false);
            }*/
            /*if (mAudio != null) {
                mAudio.setClickable(false);
                mAudio.setEnabled(false);
            }*/
            if (mPlayCutCamera != null) {
                mPlayCutCamera.setEnabled(false);
            }
            if (mPlayRecord != null) {
                mPlayRecord.setEnabled(false);
            }
        }
    };


    //没有播放成功之前,禁止一些按钮不能点击
    public void setViewEnable(final boolean enable) {
        if (enable) {
            runOnUiThread(enableTask);
        } else {
            runOnUiThread(unEnableTask);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

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


    //展现重连封面
    public void showRestartConver() {
        if (mRestart_Conver != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mRestart_Conver.getVisibility() == View.GONE || mRestart_Conver.getVisibility() == View.INVISIBLE) {
                        mRestart_Conver.setVisibility(View.VISIBLE);
                    }
                    /*Glide.with(PlayerActivity.this).load(mBean.coverUrl)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.place_holder_half_black)
                            .into(mRestart_Bg);*/
                }
            });
        }
    }

    @Override
    public void getStreamSuccess(PlayEntity playEntity) {
        if (null != playEntity && !TextUtils.isEmpty(playEntity.getToken()) && playEntity.getStreamEntity().getDevices() != null && !playEntity.getStreamEntity().getDevices().isEmpty()) {
            List<CameraNewStreamEntity.DevicesBean> devices = playEntity.getStreamEntity().getDevices();
            CameraNewStreamEntity.DevicesBean devicesBean = devices.get(0);
            mPlayerUrl = devicesBean.getRtmp_url() + "live/" + playEntity.getToken();
            closeStreamer(); //关闭硫化器
            openLiveUrl();   //重新打开硫化器
        } else {
            showRestartConver();
            closeConver();
            setCollectRecordEnable(true);
        }
    }

    @Override
    public void getStreamFailed() {
        showRestartConver();
        closeConver();
        setCollectRecordEnable(true);
    }

    @Override
    public void onCameraLikeSuccess(GetKeyStoreBaseEntity entity) {
        setMenuEnable(mMenuEnable);
        setCollectRecordEnable(true);
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


    //打开直播Url
    public void openLiveUrl() {
        mLimitTime = 10;
        mStreamPackt = new ACStreamPacket();//数据包
        mACStreamer = ACStreamerFactory.createStreamer(ACProtocolType.AC_PROTOCOL_QSTP);
        ACResult re = mACStreamer.initialize(new ACMessageListener() {
            @Override
            public void onMessage(int i, Object o) {

            }
        });

        LogUtils.i("cxm", "resultCode == " + re.getCode() + ", codeDes = " + re.getCodeDesc() + ", errorMsg = " + re.getErrMsg());

        if (mACStreamer != null) {
            mACStreamer.open(mPlayerUrl, 5000, "", mACResultListener);
        }

    }

    private ACResultListener mACResultListener = new ACResultListener() {
        @Override
        public void onResult(ACResult acResult) { //打开流化器结果。
            if (acResult.getCode() == ACResult.ACS_OK) {
                LogUtils.e("ZL", "开流成功..");
                mIsPause = false; //打开硫化器成功才不暂停，读数据线程。
                mIsOpenStreamer = true;
                startPlay(); //开始播放。

                //开流倒计时
                mStartTime = true;
                if (mStartTime) {
                    Message message = Message.obtain();
                    message.what = mReadFail;
                    mHandler.sendMessage(message);
                }

                nowTime();
                if (mIsAppearFrame) {
                    LogUtils.e("ZL", "mIsAppearFrame.." + mIsAppearFrame);
                    closeConver();
                }
                // getView().setViewEnable(true);

            } else {
                mIsOpenStreamer = false;
                showRestartConver();// 打开重连封面
                closeConver();//关闭loading封面
                setCollectRecordEnable(true);
                //handleResult(acResult,getView(),"openLiveUrl");
                LogUtils.e("ZL", "打开硫化器结果===" + acResult.getCode() + " 错误描述=" + acResult.getCodeDesc() + " 错误=" + acResult.getCode());
                // getView().finishThis(2000);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mIsOnPause = false;
        nowTime(); //计时
        if (mPlayer != null) {
            mPlayer.enterForeground();
            if (mAudio.isChecked()) {
                mPlayer.unmute();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mIsRecording) {
            mIsOnPause = true;
            // stopRecord(false);
            mPlayVideo.setChecked(true);
        }
        mIsPause = true; //停止读数据
        if (mPlayer != null) {
            mPlayer.enterBackground();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mPlayer != null) {
            mPlayer.mute();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mACResultListener = null;
        mRecordHandler.removeMessages(1);
        mDelayNetWorkTipHandler.removeMessages(2);
        mBackHandler.removeMessages(TYPE_BACK_CONTRAL);
        removeAnimation();
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
        }
        readLiveThread(false);
        closeStreamer();
        joinMainThread();
        closePlayer();
        unregisterReceiver(mNetWorkReceiver); //注销网络广播监听
    }

    public void closePlayer() {
        if (mPlayer != null) {
            long startTime = System.currentTimeMillis();
            LogUtils.e("cxm", "closePlayer start-- " + startTime);
            mPlayer.clearQueueBuffer();
            mPlayer.release();
            mIsAppearFrame = false;
            LogUtils.e("cxm", "closePlayer end-- " + (System.currentTimeMillis() - startTime));
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
    }

    public void joinMainThread() {
        long startTime = System.currentTimeMillis();
        LogUtils.e("cxm", "joinMainThread start-- " + startTime);
        /*try {*/
        if (mLiveThread != null) {
            //mLiveThread.join();
            mLiveThread = null;
            mLiveThreadStartd = false;
        }
        LogUtils.e("ZL", "直播线程join....");
        /*} catch (InterruptedException e) {
            e.printStackTrace();
            LogUtils.e("ZL","直播线程加入主线程异常....");
        }*/

        if (mHandler != null) {  //移除计时任务
            mHandler.removeCallbacksAndMessages(null);
        }
        LogUtils.e("cxm", "joinMainThread end-- " + (System.currentTimeMillis() - startTime));
    }

    //播放手机直播视频
    public void startPlay() {

        if (mLiveThread == null) {

            mLiveThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (mPlaying) {

                        while (!mIsPause) {

                            // LogUtils.e("ZL","读数据.........==");
                            if (mACStreamer != null) {
                                ACResult result = mACStreamer.read(mStreamPackt, 5000); //第二个参数是阅读超时单位好毫秒
                                if (result.getCode() == ACResult.ACS_OK) {
                                    mToltal = mToltal + mStreamPackt.size;
                                    if (mStartTime) {
                                        mHandler.sendEmptyMessage(Message.obtain().what = mReadSuccess);
                                    }
                                    //------------------测量延时------------------------------------

                                    if (mLastTime != 0L) {
                                        final long delayTime = System.currentTimeMillis() - mLastTime;
                                        if (delayTime > 2000) { //2秒之内还没有读到下一帧
                                            showNetworkTip(); //网络提示
                                        } else {
                                            if (isNetworkTipVisible()) {
                                                closeNetworkTip();
                                            }
                                        }
                                    }
                                    mLastTime = System.currentTimeMillis();
                                    //------------------测量延时------------------------------------


                                } else {
                                    LogUtils.e("ZL", "读取硫化器数据结果==" + result.getCode());
                                    mStreamPackt.size = 0;
                                    //QSTP 只要有一次读取失败就表明这个链接是断开的
                                    if (result.getCode() == ACResult.ACC_QSTP_READ_FAILED) {
                                        if (mStartTime) {
                                            mHandler.sendEmptyMessage(Message.obtain().what = mReadSuccess);
                                        }
                                        mIsPause = true;
                                        showRestartConver(); //展现重连画面
                                        // getView().setViewEnable(false);
                                        setMenuEnable(false);
                                        setCollectRecordEnable(true);

                                    }
                                }
                            }

                            if (mPlayer != null && mACStreamer != null) {
                                ACResult result = null;

                                if (mStreamPackt.size > 0) {
                                    result = mPlayer.playFrame(mStreamPackt);
                                }
                                //LogUtils.e("ZL","分辨率信息 with:"+mPlayer.getMediaInfo(ACMediaInfo.AC_MEDIA_INFO_PLAYER_VIDEO_WIDTH)+"  height:"+mPlayer.getMediaInfo(ACMediaInfo.AC_MEDIA_INFO_PLAYER_VIDEO_HEIGHT));
                                if (result != null) {
                                    if (result.getCode() == ACResult.ACS_INSUFFICIENT_BUFFER) {  //播放器的缓存队列塞满了,就休息100毫秒，主要是播放录像的时候！
                                        while (mPlayer.playFrame(mStreamPackt).getCode() == ACResult.ACS_INSUFFICIENT_BUFFER) {
                                            try {
                                                Thread.sleep(100);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }

                            }
                        }


                    }

                    LogUtils.e("ZL", "线程结束......");

                }
            });
        }

        /**
         *不管Thread.isAlive()返回的是true还是false，如果我们再次Thread.start()，就会出现 java.lang.IllegalThreadStateException
         * */
        if (!mLiveThreadStartd) {
            mLiveThread.start();
            mLiveThreadStartd = true;
        }
    }

    //显示Task
    private Runnable mNetworkTip = new Runnable() {
        @Override
        public void run() {
            if (mNetwork_tip.getVisibility() == View.INVISIBLE || mNetwork_tip.getVisibility() == View.GONE) {
                mNetwork_tip.setVisibility(View.VISIBLE);
            }
        }
    };

    //显示网络提示
    public void showNetworkTip() {
        if (mNetwork_tip != null) {
            runOnUiThread(mNetworkTip);
        }
    }

    public boolean isNetworkTipVisible() {
        return mNetwork_tip != null && mNetwork_tip.getVisibility() == View.VISIBLE;
    }

    public void closeNetworkTip() {
        if (mNetwork_tip != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mNetwork_tip.getVisibility() == View.VISIBLE) {
                        mDelayNetWorkTipHandler.sendEmptyMessageDelayed(2, 2000);
                    }
                }
            });
        }
    }

    private Handler mDelayNetWorkTipHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (null != mNetwork_tip) {
                mNetwork_tip.setVisibility(View.GONE);
            }
        }
    };

    public void closeStreamer() {
        if (mACStreamer != null) {
            long startTime = System.currentTimeMillis();
            LogUtils.e("cxm", "closeStreamer start-- " + startTime);
            mACStreamer.close();
            mACStreamer.release();
            mIsOpenStreamer = false;
            //            mACStreamer = null;
            LogUtils.e("cxm", "closeStreamer end-- " + (System.currentTimeMillis() - startTime));
        }
    }


    //实现对网路的监听(观看录像时断网了）
    class NetWorkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //网络状态变化了
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                if (NetworkUtils.isConnected()) {
                    nowTime();//再次开启计时
                } else {
                    Toast.makeText(PlayerActivity.this, "请检查你的网络....", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }


    public void nowTime() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTime.postDelayed(nowTask, 1000);
            }
        });
    }


    private Runnable nowTask = new Runnable() {
        @Override
        public void run() {
            if (!mIsPause) { //读取硫化器数据的线程站厅与否
                mTime.setText(df.format(System.currentTimeMillis()));
                mNetworkSpeed.setText(String.valueOf(mToltal / 1000) + "kB/s");
                mToltal = 0L;
                mTime.postDelayed(this, 1000);
            }
        }
    };

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
            mHideRightScaleAnimation.setDuration(300);
            mHideRightScaleAnimation.setInterpolator(new AccelerateInterpolator());
            mHideRightScaleAnimation.setFillAfter(false);
            mHideRightScaleAnimation.setAnimationListener(this);
        }
        view.startAnimation(mHideRightScaleAnimation);
        view.setVisibility(View.GONE);
    }

}
