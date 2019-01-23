package cloud.antelope.lingmou.mvp.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.antelope.sdk.ACMediaInfo;
import com.antelope.sdk.ACMessageListener;
import com.antelope.sdk.ACResult;
import com.antelope.sdk.ACResultListener;
import com.antelope.sdk.capturer.ACAudioCapturer;
import com.antelope.sdk.capturer.ACAudioFrame;
import com.antelope.sdk.capturer.ACFrame;
import com.antelope.sdk.capturer.ACFrameAvailableListener;
import com.antelope.sdk.capturer.ACImageFormat;
import com.antelope.sdk.capturer.ACImageResolution;
import com.antelope.sdk.capturer.ACOutputSurfaceListener;
import com.antelope.sdk.capturer.ACShape;
import com.antelope.sdk.capturer.ACVideoCapturer;
import com.antelope.sdk.capturer.ACVideoFrame;
import com.antelope.sdk.codec.ACAudioEncoder;
import com.antelope.sdk.codec.ACAudioEncoderFactory;
import com.antelope.sdk.codec.ACCodecID;
import com.antelope.sdk.codec.ACEncodeMode;
import com.antelope.sdk.codec.ACPacketAvailableListener;
import com.antelope.sdk.codec.ACStreamPacket;
import com.antelope.sdk.codec.ACVideoEncoder;
import com.antelope.sdk.codec.ACVideoEncoderFactory;
import com.antelope.sdk.service.ACService;
import com.antelope.sdk.streamer.ACProtocolType;
import com.antelope.sdk.streamer.ACStreamer;
import com.antelope.sdk.streamer.ACStreamerFactory;
import com.gyf.barlibrary.ImmersionBar;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.modle.SweetDialog;
import com.lingdanet.safeguard.common.utils.LogUtils;
import com.lingdanet.safeguard.common.utils.NetworkUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.lingdanet.safeguard.common.utils.WaterMarkUtils;

import butterknife.BindView;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.AnimationUtils;
import cloud.antelope.lingmou.app.utils.GisUtils;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.di.component.DaggerSoloComponent;
import cloud.antelope.lingmou.di.module.SoloModule;
import cloud.antelope.lingmou.mvp.contract.SoloContract;
import cloud.antelope.lingmou.mvp.model.entity.DeviceLoginResponseBean;
import cloud.antelope.lingmou.mvp.presenter.SoloPresenter;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class SoloActivity extends BaseActivity<SoloPresenter> implements SoloContract.View, GisUtils.OnLocateActionListener {

    private static final int MESSAGE_START_LIVE_STREAM = 0x01;
    private static final int MESSAGE_CANCEL_LIVE_STREAM = 0x02;
    private static final int MESSAGE_STOP_LIVE_STREAM = 0x03;
    private static final int MESSAGE_SEND_BIT_RATE_PER_SEC = 0x04;
    private static final int MESSAGE_START_LIVE_STREAM_SUCCESS = 0x05;
    private static final int MESSAGE_UPDATE_CONNECTING_TEXT = 0x06;
    private static final int MESSAGE_UPDATE_CONNECTING_TEXT_TO_SUCCESS = 0x07;
    private static final int MESSAGE_NETWORK_FAIL = 0x08;
    private static final int MESSAGE_START_SERVICE_FAIL = 0x09;
    private static final int MESSAGE_OPEN_STREAMER_FAIL = 0x10;
    private static final int MESSAGE_RESET_CONNECT_TEXT = 0x11;
    private static final int MESSAGE_HEART = 0x12;
    /**
     * 视频连接按钮显示的开始，等待开始
     */
    public static final int BUTTON_START = 0x01;
    /**
     * 视频连接按钮显示的取消，正在连接中
     */
    public static final int BUTTON_CANCEL = 0x02;
    /**
     * 视频连接按钮显示结束，视频直播中
     */
    public static final int BUTTON_STOP = 0x03;


    public static final int OPEN_STREAMER_DELAY_TIME = 1000;


    @BindView(R.id.solo_root)
    RelativeLayout mRoot;
    @BindView(R.id.back_ib)
    ImageButton mBackIb;
    @BindView(R.id.control_ib)
    ImageButton mControlIb;
    @BindView(R.id.control_tv)
    TextView mControlTv;
    @BindView(R.id.connect_live_streaming_tv)
    TextView mConnectLiveStreamingTv;
    @BindView(R.id.solo_preview)
    GLSurfaceView mPreview;
    @BindView(R.id.live_streaming_logo_iv)
    ImageView mLiveStreamingLogoIv;
    @BindView(R.id.control_up_rl)
    RelativeLayout mControlUpRl;
    @BindView(R.id.control_down_rl)
    RelativeLayout mControlDownRl;
    @BindView(R.id.solo_network_iv)
    ImageView mSoloNetworkIv;


    private int mStartServiceTimeout; //开启云服务超时时间，单位 ms
    private DeviceLoginResponseBean.BroadcastBean mBroadcastBean;

    private long mStartTime;
    private int mControlState = BUTTON_START;

    //音视频采集器
    private ACVideoCapturer mVideoCapture;
    private ACAudioCapturer mAudioCapture;
    //音视频编码器
    private ACVideoEncoder mVideoEncoder;
    private ACAudioEncoder mAudioEncoder;
    //流化器
    private ACStreamer mStreamer;

    //视频相关参数
    private boolean mAutoRotate;//视频采集-是否自动旋转
    private int mOutFormat;//视频采集-输出数据格式
    private int mPreviewShape;//预览形状
    private int mCurrentCameraID;//摄像机ID
    private int mCaptureResolution;//分辨率
    private int mAVCEncoderMode;//视频编码模式 硬编/软编
    private int mAVCEncodeId;//视频编码类型 H264/H265...
    private int mVideoWidth, mVideoHeight;//分辨率宽高
    private int mGop;//I帧间隔 s
    private int mVideoBitRate;//码率 kpbs
    private int mFrameRate;//帧率 Kfps
    //音频相关参数
    private int mSampleRate;//采样率 8000/16000
    private int mAudioChannel;//通道数 1/2
    private int mBitSize;//量化位数 16
    private int mAudioResource;//mic资源
    private int mAudioBitRate;//码率
    private int mAudioEncoderMode;//音频编码模式 硬编/软编
    private int mAudioEncoderId;//音频编码类型 AAC/OPUS/G711A/G711U
    //流传输协议
    private int mACProtocol;
    private int mOpenStreamerTimeout; //开流超时时间，单位 ms

    private boolean isBroadcasting; //标识位，是否正在直播

    private boolean mHasInitMedia;
    private boolean mControlDownShow = true;
    private boolean mHomePressed = false;
    private boolean mFirstAudio = true;
    /**
     * 直播控制按钮是否正在进行动画
     */
    private boolean mIsAnimating = false;
    private boolean mIsNetworkEnabled = true;

    private int mConnectingTimes;

    private static String[] videoWhiteList = {
            "hwH60",    //荣耀6
            "hwPE",     //荣耀6 plus
            "hwH30",    //3c
            "hwHol",    //3c畅玩版
            "hwG750",   //3x
            "hw7D",      //x1
            "hwChe2",      //x1
            "OnePlus3",      //OnePlus3
    };

    public static String getDeviceModel() {
        return Build.DEVICE;
    }


    private GisUtils mGisUtils;

    public static boolean isVideoWhiteList() {
        int length = videoWhiteList.length;
        for (int i = 0; i < length; i++) {
            if (videoWhiteList[i].equals(getDeviceModel())) {
                return true;
            }
        }
        return false;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_START_LIVE_STREAM:
                    startSoloLive();
                    break;
                case MESSAGE_CANCEL_LIVE_STREAM:
                    resetState();
                    break;
                case MESSAGE_STOP_LIVE_STREAM:
                    mSoloNetworkIv.setVisibility(View.INVISIBLE);
                    mSoloNetworkIv.setImageResource(R.drawable.solo_living_icon);
                    resetState();
                    mBackIb.setVisibility(View.VISIBLE);
                    break;
                case MESSAGE_SEND_BIT_RATE_PER_SEC:
                    if (null != mStreamer) {
                        if (mIsNetworkEnabled) {
                            String UPLOAD_VIDEO_BITRATE = mStreamer.getMediaInfo(ACMediaInfo.AC_MEDIA_INFO_UPLOAD_VIDEO_BITRATE);
                            if (!TextUtils.isEmpty(UPLOAD_VIDEO_BITRATE)) {
                                float speed = Float.valueOf(UPLOAD_VIDEO_BITRATE) / 8;
                                if (speed < 30.0f && speed > 0.0f) {
                                    mSoloNetworkIv.setImageResource(R.drawable.solo_network_unstable);
                                } else if (speed <= 0.0f) {
                                    mSoloNetworkIv.setImageResource(R.drawable.solo_network_disconnet);
                                } else {
                                    mSoloNetworkIv.setImageResource(R.drawable.solo_living_icon);
                                }
                            }
                            LogUtils.i("handleMessage with UPLOAD_VIDEO_BITRATE + " + UPLOAD_VIDEO_BITRATE);
                        } else {
                            mSoloNetworkIv.setImageResource(R.drawable.solo_network_disconnet);
                        }
                        mHandler.sendEmptyMessageDelayed(MESSAGE_SEND_BIT_RATE_PER_SEC, 1000);
                    }
                    break;
                case MESSAGE_START_LIVE_STREAM_SUCCESS:
                    mControlState = BUTTON_STOP;
                    mControlTv.setText(R.string.end_solo_text);
                    mBackIb.setVisibility(View.VISIBLE);
                    mSoloNetworkIv.setVisibility(View.VISIBLE);
                    resetConnectText();
                    mControlUpRl.setClickable(false);
                    // ObjectAnimator animator = ObjectAnimator.ofFloat(mControlUpRl, "translationY", 0, mControlUpRl.getHeight());
                    // animator.setDuration(900).start();
                    mControlUpRl.startAnimation(AnimationUtils.moveToViewBottom(600, true));
                    mControlUpRl.setVisibility(View.GONE);
                    mGisUtils.start();
                    break;
                case MESSAGE_UPDATE_CONNECTING_TEXT:
                    if (mControlState == BUTTON_CANCEL) {
                        if (mConnectingTimes % 3 == 0) {
                            mConnectLiveStreamingTv.setText(R.string.solo_connecting_2_text);
                        } else if (mConnectingTimes % 3 == 1) {
                            mConnectLiveStreamingTv.setText(R.string.solo_connecting_3_text);
                        } else if (mConnectingTimes % 3 == 2) {
                            mConnectLiveStreamingTv.setText(R.string.solo_connecting_1_text);

                        }
                        mConnectingTimes++;
                        mHandler.sendEmptyMessageDelayed(MESSAGE_UPDATE_CONNECTING_TEXT, 300);
                    }
                    break;
                case MESSAGE_UPDATE_CONNECTING_TEXT_TO_SUCCESS:
                    mHandler.sendEmptyMessage(MESSAGE_HEART);
                    mConnectLiveStreamingTv.setText(R.string.solo_connecting_success_text);
                    break;
                case MESSAGE_NETWORK_FAIL:
                    mConnectLiveStreamingTv.setText(R.string.network_connect_fail);
                    break;
                case MESSAGE_START_SERVICE_FAIL:
                    mConnectLiveStreamingTv.setText(R.string.solo_start_service_fail);
                    break;
                case MESSAGE_OPEN_STREAMER_FAIL:
                    mConnectLiveStreamingTv.setText(R.string.streamer_open_fail);
                    break;
                case MESSAGE_RESET_CONNECT_TEXT:
                    resetConnectText();
                    break;
                case MESSAGE_HEART:
                     mPresenter.heart();
                     mHandler.sendEmptyMessageDelayed(MESSAGE_HEART, 1000);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        DaggerSoloComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .soloModule(new SoloModule(this))
                .build()
                .inject(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTintManager.setStatusBarTintEnabled(false);
        ImmersionBar.with(this)
                .statusBarDarkFont(false, 0.2f)
                .init();
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_solo; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        registerReceiver();
        mStartServiceTimeout = 5000;
        initData();
    }

    /**
     * 初始化音视频相关参数
     */
    private void initData() {
        mGisUtils = new GisUtils(this, 20000);
        mGisUtils.setLocateListener(this);
        //视频相关参数
        mAutoRotate = true; //视频采集-是否自动旋转
        mOutFormat = ACImageFormat.AC_IMAGE_FMT_YUV420P; //视频采集-输出数据格式，须输出编码器支持的格式进行编码
        mPreviewShape = ACShape.AC_SHAPE_NONE; //预览形状 NONE/CIRCLE/TRIANGLE...
        mCaptureResolution = ACImageResolution.R480; //分辨率,默认480 480/720P/1080P...
        mCurrentCameraID = Camera.CameraInfo.CAMERA_FACING_BACK; //摄像机ID BACK/FRONT/外接摄像头...
        // mAVCEncoderMode = ACEncodeMode.AC_ENC_MODE_HARD; //视频编码模式 HARD/SOFT
        if (isVideoWhiteList()) {  // 如果是白名单中的手机，则使用软编
            mAVCEncoderMode = ACEncodeMode.AC_ENC_MODE_SOFT; //视频编码模式 HARD/SOFT
        } else {
            // mAVCEncoderMode = ACEncodeMode.AC_ENC_MODE_HARD; //视频编码模式 HARD/SOFT
            mAVCEncoderMode = ACEncodeMode.AC_ENC_MODE_SOFT; //视频编码模式 HARD/SOFT
        }
        mAVCEncodeId = ACCodecID.AC_CODEC_ID_H264; //视频编码类型 H264/H265...
        mVideoWidth = 720; //分辨率WIDTH
        mVideoHeight = 1280; //分辨率HEIGHT
        mGop = 2; //I帧间隔 s
        mVideoBitRate = 800; //码率 KPBS
        mFrameRate = 15; //帧率 KFPS
        //音频相关参数
        mSampleRate = 8000; //采样率 8000/16000
        mAudioChannel = 1; //通道数 1/2
        mBitSize = 16; //量化位数 16
        mAudioBitRate = 16; //码率 KPBS
        mAudioResource = MediaRecorder.AudioSource.VOICE_COMMUNICATION; //mic资源
        mAudioEncoderMode = ACEncodeMode.AC_ENC_MODE_HARD; //音频编码模式 HARD/SOFT
        // mAudioEncoderMode = ACEncodeMode.AC_ENC_MODE_SOFT; //音频编码模式 HARD/SOFT
        mAudioEncoderId = ACCodecID.AC_CODEC_ID_AAC; //音频编码类型 AAC/OPUS
        //流化器相关参数
        mACProtocol = ACProtocolType.AC_PROTOCOL_QSTP; //播放协议 QSTP/QSUP/RECORD
        mOpenStreamerTimeout = 5000;

        isBroadcasting = false;
    }

    /**
     * 初始化音视频采集器，编码器以及流化器
     */
    private void initMedia() {
        //视频编码器
        mVideoEncoder = ACVideoEncoderFactory.createVideoEncoder(mAVCEncoderMode, mAVCEncodeId);
        ACResult videoEncoderInit = mVideoEncoder.initialize(mVideoWidth, mVideoHeight, mGop, mEncoderPacketListener);
        LogUtils.i("videoEncoderInit with result = " + videoEncoderInit.getCode());
        ACResult videoEncoderFrameRate = mVideoEncoder.setFrameRate(mFrameRate);//动态设置帧率
        LogUtils.i("videoEncoderFrameRate with result = " + videoEncoderFrameRate.getCode());
        ACResult videoEncoderBitRate = mVideoEncoder.setBitRate(mVideoBitRate);//动态设置码率
        LogUtils.i("videoEncoderBitRate with result = " + videoEncoderBitRate.getCode());
        //选择编码器所支持的格式，将此格式传给采集器以获取该格式数据
        int[] supportImageFormat = mVideoEncoder.getSupportedImageFormat();
        if (supportImageFormat != null && supportImageFormat.length > 0) {
            //            mOutFormat = supportImageFormat[0];
            for (int i = 0; i < supportImageFormat.length; i++) {
                if (supportImageFormat[i] != ACImageFormat.AC_IMAGE_FMT_SURFACE) {
                    mOutFormat = supportImageFormat[i];
                }
            }
        }

        //视频采集器
        mVideoCapture = new ACVideoCapturer();
        ACResult videoCaptureInit = mVideoCapture.initialize(SoloActivity.this, mAutoRotate, mCaptureFrameListener, mOutFormat);
        LogUtils.i("videoCaptureInit with result = " + videoCaptureInit.getCode());
        //如无需预览，则跳过此步骤，且不要设置GLSurfaceView控件，否则会抛异常
        ACResult videoPreviewSurfaceView = mVideoCapture.setPreviewSurfaceView(mPreview, mPreviewShape, new Object());
        LogUtils.i("videoPreviewSurfaceView with result = " + videoPreviewSurfaceView.getCode());
        //当outFormat = ACImageFormat.AC_IMAGE_FMT_SURFACE，必须设置该回调，返回编码器的surface
        //surface数据输出方式只输出编码数据，采集回调输出宽高等信息，buffer数据为空
        //这种数据输出方式兼容性较差，一般5.0以下手机都不兼容，所以要先初始化编码器，获取编码器所支持的数据格式
        mVideoCapture.setOutputSurfaceListener(new ACOutputSurfaceListener() {
            @Override
            public Surface getOutputSurface() {
                return mVideoEncoder.getInputSurface();
            }
        });
        ACResult openCamera = mVideoCapture.openCamera(mCurrentCameraID, mCaptureResolution);//打开摄像机
        LogUtils.i("openCamera with result = " + openCamera.getCode());

        //音频采集器
        mAudioCapture = new ACAudioCapturer();
        ACResult audioCaptureInit = mAudioCapture.initialize(mSampleRate, mAudioChannel, mBitSize, mCaptureFrameListener);
        LogUtils.i("audioCapture with result = " + audioCaptureInit.getCode());
        ACResult audioFrameSize = mAudioCapture.setFrameSize(640);//动态设置音频一次读取的字节数
        LogUtils.i("audioCapture with result = " + audioFrameSize.getCode());
        int audioBufferSizeInBytes = mAudioCapture.getBufferSizeInBytes();//获取音频最小缓冲区大小
        LogUtils.i("audioBufferSizeInBytes with result = " + audioBufferSizeInBytes);
        ACResult openMicrophone = mAudioCapture.openMicrophone(mAudioResource);
        LogUtils.i("openMicrophone with result = " + openMicrophone.getCode());

        //音频编码器
        mAudioEncoder = ACAudioEncoderFactory.createAudioEncoder(mAudioEncoderMode, mAudioEncoderId);
        ACResult audioEncoderInit = mAudioEncoder.initialize(mSampleRate, mAudioChannel, mBitSize, mAudioBitRate, mEncoderPacketListener);
        LogUtils.i("audioInit with result = " + audioEncoderInit.getCode());
    }

    private void openStreamer() {
        //流化器
        mStreamer = ACStreamerFactory.createStreamer(mACProtocol);
        ACResult mStreamerInit = mStreamer.initialize(this, new ACMessageListener() {
            @Override
            public void onMessage(int i, Object o) {
                //接受处理连接断开，重连，观看人数等各种消息
                if (null != mStreamer) {
                    String UPLOAD_VIDEO_BITRATE = mStreamer.getMediaInfo(ACMediaInfo.AC_MEDIA_INFO_UPLOAD_VIDEO_BITRATE);
                    String UPLOAD_VIDEO_FRAMERATE = mStreamer.getMediaInfo(ACMediaInfo.AC_MEDIA_INFO_UPLOAD_VIDEO_FRAMERATE);
                    if (null != o) {
                        LogUtils.i("BroadcastActivity", "onMessage with i = : " + i +
                                ",\no = " + o.toString() +
                                ",\nUPLOAD_VIDEO_BITRATE = " + UPLOAD_VIDEO_BITRATE +
                                ",\nUPLOAD_VIDEO_FRAMERATE = " + UPLOAD_VIDEO_FRAMERATE
                        );
                    } else {
                        LogUtils.i("BroadcastActivity", "onMessage with i = : " + i +
                                ",\nUPLOAD_VIDEO_BITRATE = " + UPLOAD_VIDEO_BITRATE +
                                ",\nUPLOAD_VIDEO_FRAMERATE = " + UPLOAD_VIDEO_FRAMERATE
                        );
                    }
                }
            }
        });
        LogUtils.i("mStreamerInit with result = " + mStreamerInit.getCode());

        if (null != mBroadcastBean && !TextUtils.isEmpty(mBroadcastBean.push_url)) {
            mStreamer.open(mBroadcastBean.push_url, mOpenStreamerTimeout, null, new ACResultListener() {
                @Override
                public void onResult(ACResult acResult) {
                    LogUtils.i("mStreamer open with result = " + acResult.getCode());
                    if (acResult.isResultOK()) {
                        // ToastUtils.showShort("open streamer successfully");
                        isBroadcasting = true;
                        if (mControlState == BUTTON_CANCEL) {
                            if (null != mHandler) {
                                mHandler.removeMessages(MESSAGE_UPDATE_CONNECTING_TEXT);
                                mHandler.sendEmptyMessage(MESSAGE_UPDATE_CONNECTING_TEXT_TO_SUCCESS);

                                mHandler.sendEmptyMessageDelayed(MESSAGE_START_LIVE_STREAM_SUCCESS, 400);
                                mHandler.sendEmptyMessageDelayed(MESSAGE_SEND_BIT_RATE_PER_SEC, 2000);
                            }
                        } else if (mControlState == BUTTON_START) {
                            if (null != mHandler) {
                                mHandler.sendEmptyMessage(MESSAGE_RESET_CONNECT_TEXT);
                                mHandler.sendEmptyMessage(MESSAGE_CANCEL_LIVE_STREAM);
                            }
                        }
                    } else {
                        if (mHandler != null) {
                            mHandler.removeMessages(MESSAGE_UPDATE_CONNECTING_TEXT);
                            mHandler.sendEmptyMessage(MESSAGE_OPEN_STREAMER_FAIL);
                            mHandler.sendEmptyMessage(MESSAGE_CANCEL_LIVE_STREAM);
                        }
                        isBroadcasting = false;
                    }
                }
            });
        } else {
            if (mHandler != null) {
                mHandler.removeMessages(MESSAGE_UPDATE_CONNECTING_TEXT);
                mHandler.sendEmptyMessage(MESSAGE_OPEN_STREAMER_FAIL);
                mHandler.sendEmptyMessage(MESSAGE_CANCEL_LIVE_STREAM);
            }
            isBroadcasting = false;
        }
    }


    /**
     * 音视频采集数据回调
     */
    ACFrameAvailableListener mCaptureFrameListener = new ACFrameAvailableListener() {
        @Override
        public void onFrameAvailable(ACFrame acFrame) {
            //如果正在直播（流化器已经打开），就开始编码发送数据，否则不处理
            if (isBroadcasting) {
                //视频数据
                if (acFrame instanceof ACVideoFrame) {
                    ACResult result = mVideoEncoder.encode((ACVideoFrame) acFrame, false);
                    LogUtils.i("mVideoEncoder encode with result = " + result.getCode());
                } else {
                    //音频数据
                    ACResult result = mAudioEncoder.encode((ACAudioFrame) acFrame);
                    LogUtils.i("mAudioEncoder encode with result = " + result.getCode());
                }
            }
        }
    };

    /**
     * 音视频编码数据回调
     */
    ACPacketAvailableListener mEncoderPacketListener = new ACPacketAvailableListener() {
        @Override
        public void onPacketAvailable(ACStreamPacket acStreamPacket) {
            if (null != mStreamer) {
                LogUtils.i("streamer type result：" + acStreamPacket.type);
                /*
                #warning 发送音频的时候，必须先发送一个128类型数据，然后在发送129，如果第一帧不是128类型，
                需要调用接口reset重置，产生一个128类型数据发送
                 */
                if (mFirstAudio) {
                    if (acStreamPacket.type == 128) {
                        mFirstAudio = false;
                    } else if (acStreamPacket.type == 129) {
                        ACResult acResult = mAudioEncoder.reset();
                        LogUtils.i("mAudioEncoder reset result：" + acResult.getCode());
                        if (acResult.isResultOK()) {
                            mFirstAudio = false;
                        } else {
                            return;
                        }
                    }
                }
                //发送编码后的数据
                ACResult result = mStreamer.write(acStreamPacket);
                LogUtils.i("streamer write result：" + result.getCode());
            }
        }
    };

    @OnClick({R.id.back_ib, R.id.control_ib, R.id.solo_preview})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_ib:
                if (mControlState == BUTTON_START) {
                    finish();
                } else if (mControlState == BUTTON_CANCEL) {
                    mHandler.sendEmptyMessage(MESSAGE_RESET_CONNECT_TEXT);
                    mHandler.sendEmptyMessage(MESSAGE_CANCEL_LIVE_STREAM);
                } else if (mControlState == BUTTON_STOP) {
                    showStopDialog(true);
                }
                break;
            case R.id.control_ib:
                if (mControlState == BUTTON_START) {
                    mControlState = BUTTON_CANCEL;
                    mStartTime = System.currentTimeMillis();
                    mControlTv.setText(R.string.cancel);
                    mControlIb.setImageResource(R.drawable.ydyy_stop_living);
                    mConnectLiveStreamingTv.setVisibility(View.VISIBLE);
                    mHandler.sendEmptyMessageDelayed(MESSAGE_UPDATE_CONNECTING_TEXT, 300);
                    if (ACService.getInstance().isOnline()) {
                        mHandler.sendEmptyMessageDelayed(MESSAGE_START_LIVE_STREAM, OPEN_STREAMER_DELAY_TIME);
                    } else {
                        loginDevice();
                    }
                } else if (mControlState == BUTTON_CANCEL) {
                    mHandler.sendEmptyMessage(MESSAGE_RESET_CONNECT_TEXT);
                    mHandler.sendEmptyMessage(MESSAGE_CANCEL_LIVE_STREAM);
                } else if (mControlState == BUTTON_STOP) {
                    showStopDialog(true);
                }
                break;
            case R.id.solo_preview:
                if (!mIsAnimating) {
                    mIsAnimating = true;
                    if (mControlDownShow) {
                        // ObjectAnimator animator = ObjectAnimator.ofFloat(mControlDownRl,
                        //         "translationY", mControlDownRl.getTranslationY(),
                        //         mControlDownRl.getHeight());
                        // animator.setDuration(400).start();
                        mControlDownRl.animate().translationYBy(mControlDownRl.getHeight()).setDuration(400)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        mControlDownShow = false;
                                        mIsAnimating = false;
                                    }
                                });
                        // mControlDownRl.startAnimation(AnimationUtils.moveToViewBottom(600, true));
                    } else {
                        // ObjectAnimator animator = ObjectAnimator.ofFloat(mControlDownRl,
                        //         "translationY", mControlDownRl.getTranslationY(),
                        //         mControlDownRl.getTranslationY() - mControlDownRl.getHeight());
                        // animator.setDuration(400).start();
                        mControlDownRl.animate().translationYBy(-mControlDownRl.getHeight()).setDuration(400)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        mControlDownShow = true;
                                        mIsAnimating = false;
                                    }
                                });
                        // mControlDownRl.startAnimation(AnimationUtils.moveToViewLocation(600, true));
                    }
                }
                break;
            default:
                break;
        }
    }

    private void loginDevice() {
        String cid = SPUtils.getInstance().getString(Constants.CONFIG_SOLO_CID);
        String sn = SPUtils.getInstance().getString(Constants.CONFIG_SOLO_SN);
        // String brand = SPUtils.getInstance().getString(Constants.CONFIG_SOLO_BRAND);
        String brand = "soldier";
        if (TextUtils.isEmpty(cid) || TextUtils.isEmpty(sn) || TextUtils.isEmpty(brand)) {
            ToastUtils.showShort(R.string.fail_to_login_device);
            return;
        }
        int intCid;
        try {
            intCid = Integer.parseInt(cid);
        } catch (NumberFormatException e) {
            ToastUtils.showShort(R.string.fail_to_login_device);
            e.printStackTrace();
            return;
        }
        mPresenter.loginDevice(intCid, sn, brand, "");
    }


    /**
     * 重置连接文本和状态
     */
    private void resetConnectText() {
        mHandler.removeMessages(MESSAGE_UPDATE_CONNECTING_TEXT);
        mConnectLiveStreamingTv.setVisibility(View.GONE);
        mConnectLiveStreamingTv.setText(R.string.solo_connecting_1_text);
        mConnectingTimes = 0;
    }

    /**
     * 重置连接状态
     */
    private void resetState() {
        mControlState = BUTTON_START;
        mHandler.removeMessages(MESSAGE_START_LIVE_STREAM);
        mHandler.removeMessages(MESSAGE_START_LIVE_STREAM_SUCCESS);
        mHandler.removeMessages(MESSAGE_SEND_BIT_RATE_PER_SEC);
        mControlTv.setText(R.string.solo_start_text);
        mControlIb.setImageResource(R.drawable.ydyy_start_living);
        if (null != mVideoCapture) {
            mVideoCapture.enterBackground();
        }
        if (null != mStreamer) {
            mStreamer.close();
            // mStreamer.release();
            mStreamer = null;
        }
        mFirstAudio = true;
    }

    private void startSoloLive() {
        mPreview.setVisibility(View.VISIBLE);
        if (!mHasInitMedia) {
            mHasInitMedia = true;
            initMedia();
        } else {
            ACResult openCamera = mVideoCapture.openCamera(mCurrentCameraID, mCaptureResolution);//打开摄像机
            LogUtils.i("openCamera with result = " + openCamera.getCode());
        }
        openStreamer();
        if (null != mVideoCapture) {
            mVideoCapture.enterForeground();
        }
        mPreview.postDelayed(new Runnable() {
            @Override
            public void run() {
                //进行水印加持
                ViewGroup rootView = findViewById(android.R.id.content);
                ImageView waterMarkView = (ImageView) LayoutInflater.from(SoloActivity.this).inflate(R.layout.layout_water_mark, null, false);
                String loginName = SPUtils.getInstance().getString(Constants.CONFIG_LAST_USER_REAL_NAME);
                String loginPhone = SPUtils.getInstance().getString(Constants.CONFIG_LAST_USER_LOGIN_PHONE);
                Bitmap mLandscapeMarkerBitmap = WaterMarkUtils.getMarkerBitmapMultiLine(loginName + "\n" + loginPhone);
                waterMarkView.setImageBitmap(mLandscapeMarkerBitmap);
                rootView.addView(waterMarkView);
            }
        }, 800);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mHomePressed) {
            mHomePressed = false;
            if (ACService.getInstance().isOnline()) {
                mHandler.sendEmptyMessage(MESSAGE_START_LIVE_STREAM);
            } else {
                loginDevice();
            }
        }
    }

    /**
     * 停止云服务
     */
    private void stopService() {
        if (ACService.getInstance().isOnline())
            ACService.getInstance().stopCloudService();
    }

    @Override
    protected void onStop() {
        mGisUtils.stop();
        stopService();
        isBroadcasting = false;
        if (null != mVideoCapture) {
            mVideoCapture.enterBackground();
            mVideoCapture.closeCamera();
        }
        if (null != mStreamer) {
            mStreamer.close();
            // mStreamer.release();
            mStreamer = null;
        }
        mFirstAudio = true;
        super.onStop();
        ViewGroup rootView = this.findViewById(android.R.id.content);
        rootView.removeViewAt(rootView.getChildCount() - 1);
    }

    @Override
    protected void onDestroy() {
        mGisUtils.destory();
        if (null != mHandler) {
            mHandler.removeMessages(MESSAGE_RESET_CONNECT_TEXT);
            mHandler.removeMessages(MESSAGE_CANCEL_LIVE_STREAM);
            mHandler.removeMessages(MESSAGE_SEND_BIT_RATE_PER_SEC);
             mHandler.removeMessages(MESSAGE_HEART);
            mHandler = null;
        }
        if (null != mStreamer) {
            // mStreamer.close();
            mStreamer.release();
            mStreamer = null;
        }
        if (null != mVideoEncoder) {
            mVideoEncoder.release();
        }
        if (null != mAudioEncoder) {
            mAudioEncoder.release();
        }
        if (null != mVideoCapture) {
            mVideoCapture.release();
        }

        if (null != mAudioCapture) {
            mAudioCapture.release();
        }
        if (null != mHomeWatcherReceiver) {
            unregisterReceiver(mHomeWatcherReceiver);
            unregisterReceiver(mNetworkChangeReceiver);
            mHomeWatcherReceiver = null;
            mNetworkChangeReceiver = null;
        }
        super.onDestroy();
    }

    private void showStopDialog(final boolean fromStopBtn) {
        final SweetDialog sweetDialog = new SweetDialog(this);
        sweetDialog.setTitle(getString(R.string.solo_exit_living_text));
        sweetDialog.setPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sweetDialog.dismiss();
                finish();
                // if (fromStopBtn) {
                //     mControlUpRl.setClickable(true);
                //     // ObjectAnimator animator = ObjectAnimator.ofFloat(mControlUpRl, "translationY", mControlUpRl.getHeight(), 0);
                //     // animator.setDuration(600).start();
                //     mControlUpRl.startAnimation(AnimationUtils.moveToViewLocation(1000, true));
                //     mControlUpRl.setVisibility(View.VISIBLE);
                //     mHandler.sendEmptyMessage(MESSAGE_STOP_LIVE_STREAM);
                // } else {
                //     finish();
                // }
            }
        });
        sweetDialog.setNegativeListener(null);
        sweetDialog.show();
    }

    @Override
    public void onBackPressedSupport() {
        if (mControlState == BUTTON_START) {
            finish();
        } else if (mControlState == BUTTON_CANCEL) {
            mHandler.sendEmptyMessage(MESSAGE_RESET_CONNECT_TEXT);
            mHandler.sendEmptyMessage(MESSAGE_CANCEL_LIVE_STREAM);
        } else if (mControlState == BUTTON_STOP) {
            showStopDialog(false);
        }
    }

    public void onHomePressed() {
        mHomePressed = true;
    }

    private HomeWatcherReceiver mHomeWatcherReceiver;
    private NetworkChangeReceiver mNetworkChangeReceiver;

    private void registerReceiver() {
        mHomeWatcherReceiver = new HomeWatcherReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mHomeWatcherReceiver, filter);

        mNetworkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkChangeReceiver, filter2);
    }

    @Override
    public void onLoginDeviceSuccess(DeviceLoginResponseBean entity) {
        mBroadcastBean = entity.data;
        if (null != mBroadcastBean && !TextUtils.isEmpty(mBroadcastBean.token)
                && !TextUtils.isEmpty(mBroadcastBean.init_string)) {
            startService();
        } else {
            if (null != mHandler) {
                mHandler.removeMessages(MESSAGE_UPDATE_CONNECTING_TEXT);
                mHandler.sendEmptyMessage(MESSAGE_NETWORK_FAIL);
                mHandler.sendEmptyMessage(MESSAGE_CANCEL_LIVE_STREAM);
            }
        }
    }

    /**
     * 开启云服务
     */
    private void startService() {
        ACService.getInstance().startCloudService(mBroadcastBean.token,
                mBroadcastBean.init_string, mStartServiceTimeout, new ACResultListener() {

                    @Override
                    public void onResult(ACResult acResult) {
                        if (acResult.isResultOK()) {
                            // ToastUtils.showShort("start cloud service successfully");
                            if (null != mHandler) {
                                long time = System.currentTimeMillis() - mStartTime;
                                if (time < OPEN_STREAMER_DELAY_TIME) {
                                    mHandler.sendEmptyMessageDelayed(MESSAGE_START_LIVE_STREAM,
                                            OPEN_STREAMER_DELAY_TIME - time);
                                } else {
                                    mHandler.sendEmptyMessage(MESSAGE_START_LIVE_STREAM);
                                }
                            }
                        } else {
                            if (null != mHandler) {
                                mHandler.removeMessages(MESSAGE_UPDATE_CONNECTING_TEXT);
                                mHandler.sendEmptyMessage(MESSAGE_START_SERVICE_FAIL);
                                mHandler.sendEmptyMessage(MESSAGE_CANCEL_LIVE_STREAM);
                            }
                        }
                    }
                }, new ACMessageListener() {

                    @Override
                    public void onMessage(int i, Object o) {
                        //云消息回调
                    }
                });
    }

    @Override
    public void onLoginDeviceError() {
        if (null != mHandler) {
            mHandler.removeMessages(MESSAGE_UPDATE_CONNECTING_TEXT);
            mHandler.sendEmptyMessage(MESSAGE_NETWORK_FAIL);
            mHandler.sendEmptyMessage(MESSAGE_CANCEL_LIVE_STREAM);
        }
    }

    @Override
    public void onLocateStart() {

    }

    @Override
    public void onLocateOK(AMapLocation location) {
        String soloId = SPUtils.getInstance().getString(Constants.CONFIG_SOLO_ID);
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        mPresenter.uploadLocation(soloId, longitude, latitude);
    }

    @Override
    public void onLocateFail(int errCode) {

    }

    public class HomeWatcherReceiver extends BroadcastReceiver {

        private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
        private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {

            String intentAction = intent.getAction();
            LogUtils.i("intentAction =" + intentAction);
            if (TextUtils.equals(intentAction, Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                LogUtils.i("reason =" + reason);
                if (TextUtils.equals(SYSTEM_DIALOG_REASON_HOME_KEY, reason)) { // 短按home键
                    onHomePressed();
                }
            } else if (TextUtils.equals(intentAction, Intent.ACTION_SCREEN_OFF)) { // 锁屏
                onHomePressed();
            }
        }

    }

    private class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 监听网络连接，包括wifi和移动数据的打开和关闭,以及连接上可用的连接都会接到监听
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                // 如果连接上了网络
                if (NetworkUtils.isConnected()) {
                    mIsNetworkEnabled = true;
                } else {
                    mIsNetworkEnabled = false;
                }
            }

        }
    }

    @Override
    public void showLoading(String message) {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ArmsUtils.toastText(message);
        onLoginDeviceError();
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


}
