package com.cjt2325.cameralibrary;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.VideoView;

import com.cjt2325.cameralibrary.lisenter.CaptureLisenter;
import com.cjt2325.cameralibrary.lisenter.ErrorLisenter;
import com.cjt2325.cameralibrary.lisenter.JCameraLisenter;
import com.cjt2325.cameralibrary.lisenter.ReturnLisenter;
import com.cjt2325.cameralibrary.lisenter.TypeLisenter;
import com.cjt2325.cameralibrary.util.ScreenUtils;
import com.lingdanet.safeguard.common.utils.LogUtils;
import com.lingdanet.safeguard.common.utils.SizeUtils;
import com.lingdanet.safeguard.common.utils.Utils;

import java.io.File;
import java.io.IOException;


/**
 * =====================================
 * 作    者: 陈嘉桐
 * 版    本：1.0.4
 * 创建日期：2017/4/25
 * 描    述：
 * =====================================
 */
public class JCameraView extends FrameLayout implements CameraInterface.CamOpenOverCallback, SurfaceHolder.Callback {
    private static final String TAG = "CJT";

    private static final int TYPE_PICTURE = 0x001;
    private static final int TYPE_VIDEO = 0x002;
    public static final String TYPE_CAPTURE = "type_capture";

    public static final int MEDIA_QUALITY_HIGH = 20 * 100000;
    public static final int MEDIA_QUALITY_MIDDLE = 18 * 100000;
    public static final int MEDIA_QUALITY_LOW = 12 * 100000;
    public static final int MEDIA_QUALITY_POOR = 8 * 100000;
    public static final int MEDIA_QUALITY_FUNNY = 4 * 100000;
    public static final int MEDIA_QUALITY_DESPAIR = 2 * 100000;
    public static final int MEDIA_QUALITY_SORRY = 1 * 80000;
    public static final int MEDIA_QUALITY_SORRY_YOU_ARE_GOOD_MAN = 1 * 10000;

    //只能拍照
    public static final int BUTTON_STATE_ONLY_CAPTURE = 0x101;
    //只能录像
    public static final int BUTTON_STATE_ONLY_RECORDER = 0x102;
    //两者都可以
    public static final int BUTTON_STATE_BOTH = 0x103;

    private JCameraLisenter jCameraLisenter;


    private Context mContext;
    private VideoView mVideoView;
    private ImageView mPhoto;
    private AppCompatImageView mSwitchCamera;
    private CaptureLayout mCaptureLayout;
    private FoucsView mFoucsView;
    private MediaPlayer mMediaPlayer;

    private int layout_width;
    private int fouce_size;
    private float screenProp;

    private Bitmap captureBitmap;
    private String videoUrl;
    private int type = -1;
    private boolean onlyPause = false;

    private int CAMERA_STATE = -1;
    private static final int STATE_IDLE = 0x010;
    private static final int STATE_RUNNING = 0x020;
    private static final int STATE_WAIT = 0x030;

    private boolean stopping = false;
    private boolean isBorrow = false;
    private boolean takePictureing = false;
    private boolean forbiddenSwitch = false;

    private boolean mIsLmCapture = true;

    /**
     * switch buttom param
     */
    private int iconSize = 0;
    private int iconMargin = 0;
    private int iconMarginTop = 0;
    private int iconSrc = 0;
    private int duration = 0;

    public void setFaceTopMargin(int margin) {
        iconMarginTop = SizeUtils.dp2px(margin);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mSwitchCamera.getLayoutParams();
        layoutParams.topMargin = iconMarginTop;
        mSwitchCamera.setLayoutParams(layoutParams);
        mSwitchCamera.invalidate();
    }

    public void setBackGone() {
        mCaptureLayout.setBtnCancelGone();
    }

    /**
     * constructor
     */
    public JCameraView(Context context) {
        this(context, null);
    }

    /**
     * constructor
     */
    public JCameraView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * constructor
     */
    public JCameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //get AttributeSet
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.JCameraView, defStyleAttr, 0);
        iconSize = a.getDimensionPixelSize(R.styleable.JCameraView_iconSize, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 35, getResources().getDisplayMetrics()));
        iconMargin = a.getDimensionPixelSize(R.styleable.JCameraView_iconMargin, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics()));
        iconMarginTop = a.getDimensionPixelSize(R.styleable.JCameraView_iconMarginTop, iconMargin);
        iconSrc = a.getResourceId(R.styleable.JCameraView_iconSrc, R.drawable.camera_font);
        duration = a.getInteger(R.styleable.JCameraView_duration_max, 10 * 1000);
        a.recycle();
        initData();
        initView();
    }

    private void initData() {
        WindowManager manager = (WindowManager) Utils.getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        layout_width = outMetrics.widthPixels;
        fouce_size = layout_width / 4;
        CAMERA_STATE = STATE_IDLE;
        screenProp = (float) ScreenUtils.getScreenWidth(Utils.getContext()) / (float) ScreenUtils.getScreenHeight(Utils.getContext());
    }


    private void initView() {
        setWillNotDraw(false);
        View view = LayoutInflater.from(Utils.getContext()).inflate(R.layout.camera_view, this);
//        this.setBackgroundColor(0xff000000);
        //VideoView
        mVideoView = (VideoView) view.findViewById(R.id.video_preview);
        //mPhoto
        mPhoto = (ImageView) view.findViewById(R.id.image_photo);
        mPhoto.setVisibility(INVISIBLE);
        //switchCamera
        mSwitchCamera = (AppCompatImageView) view.findViewById(R.id.image_switch);
        LayoutParams imageViewParam = new LayoutParams(iconSize + 2 * iconMargin, iconSize + 2 * iconMargin);
        imageViewParam.gravity = Gravity.RIGHT;
//        imageViewParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
//        imageViewParam.setMargins(0, iconMargin, iconMargin, 0);
//        imageViewParam.topMargin = iconMarginTop;
//        mSwitchCamera.setImageResource(iconSrc);
        mSwitchCamera.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBorrow || switching || forbiddenSwitch) {
                    return;
                }
                switching = true;
                new Thread() {
                    /**
                     * switch camera
                     */
                    @Override
                    public void run() {
                        CameraInterface.CAMERAINSTANCE.switchCamera(JCameraView.this);
                    }
                }.start();
            }
        });
        //CaptureLayout
        mCaptureLayout = (CaptureLayout) view.findViewById(R.id.capture_layout);
        mCaptureLayout.setDuration(duration);

        //mFoucsView
        mFoucsView = (FoucsView) view.findViewById(R.id.fouce_view);
        mFoucsView.setVisibility(VISIBLE);

        //START >>>>>>> captureLayout lisenter callback
        mCaptureLayout.setCaptureLisenter(new CaptureLisenter() {
            @Override
            public void takePictures() {
                if (CAMERA_STATE != STATE_IDLE || takePictureing) {
                    return;
                }
                CAMERA_STATE = STATE_RUNNING;
                takePictureing = true;
                mFoucsView.setVisibility(INVISIBLE);
                CameraInterface.CAMERAINSTANCE.takePicture(new CameraInterface.TakePictureCallback() {
                    @Override
                    public void captureResult(Bitmap bitmap, boolean isVertical) {
                        captureBitmap = bitmap;
                        CameraInterface.CAMERAINSTANCE.doStopCamera();
                        type = TYPE_PICTURE;
                        isBorrow = true;
                        CAMERA_STATE = STATE_WAIT;
                        if (isVertical) {
                            mPhoto.setScaleType(ImageView.ScaleType.FIT_XY);
                        } else {
                            mPhoto.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                        }
                        mPhoto.setImageBitmap(bitmap);
                        mPhoto.setVisibility(VISIBLE);
                        mCaptureLayout.startAlphaAnimation();
                        mCaptureLayout.startTypeBtnAnimator();
                        takePictureing = false;
                        mSwitchCamera.setVisibility(INVISIBLE);
                        CameraInterface.CAMERAINSTANCE.doOpenCamera(JCameraView.this);
                    }
                });
            }

            @Override
            public void recordShort(long time) {
                if (CAMERA_STATE != STATE_RUNNING && stopping) {
                    return;
                }
                stopping = true;
                mCaptureLayout.setTextWithAnimation("录制时间过短");
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CameraInterface.CAMERAINSTANCE.stopRecord(true, new
                                CameraInterface.StopRecordCallback() {
                                    @Override
                                    public void recordResult(String url) {
                                        LogUtils.i(TAG, "stopping ...");
                                        mCaptureLayout.isRecord(false);
                                        CAMERA_STATE = STATE_IDLE;
                                        stopping = false;
                                        isBorrow = false;
                                    }
                                });
                    }
                }, 1500 - time);
            }

            @Override
            public void recordStart() {
                if (CAMERA_STATE != STATE_IDLE && stopping) {
                    return;
                }
                mCaptureLayout.isRecord(true);
                isBorrow = true;
                CAMERA_STATE = STATE_RUNNING;
                mFoucsView.setVisibility(INVISIBLE);
                CameraInterface.CAMERAINSTANCE.startRecord(mVideoView.getHolder().getSurface(), new CameraInterface
                        .ErrorCallback() {
                    @Override
                    public void onError() {
                        LogUtils.i("CJT", "startRecorder error");
                        mCaptureLayout.isRecord(false);
                        CAMERA_STATE = STATE_WAIT;
                        stopping = false;
                        isBorrow = false;
                    }
                });
            }

            @Override
            public void recordEnd(long time) {
                CameraInterface.CAMERAINSTANCE.stopRecord(false, new CameraInterface.StopRecordCallback() {
                    @Override
                    public void recordResult(final String url) {
                        CAMERA_STATE = STATE_WAIT;
                        videoUrl = url;
                        type = TYPE_VIDEO;
                        new Thread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                            @Override
                            public void run() {
                                try {
                                    if (mMediaPlayer == null) {
                                        mMediaPlayer = new MediaPlayer();
                                    } else {
                                        mMediaPlayer.reset();
                                    }
                                    LogUtils.i("CJT", "URL = " + url);
                                    mMediaPlayer.setDataSource(url);
                                    mMediaPlayer.setSurface(mVideoView.getHolder().getSurface());
                                    mMediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                    mMediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer
                                            .OnVideoSizeChangedListener() {
                                        @Override
                                        public void
                                        onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                                            LogUtils.i("CJT", "onVideoSizeChanged: width = " + width + ", height = " + height
                                                    + ", mMediaPlayer.getVideoWidth() = " + mMediaPlayer.getVideoWidth()
                                                    + ", mMediaPlayer.getVideoHeight() = " + mMediaPlayer.getVideoHeight());
                                            updateVideoViewSize(mMediaPlayer.getVideoWidth(), mMediaPlayer
                                                    .getVideoHeight());
                                        }
                                    });
                                    mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                        @Override
                                        public void onPrepared(MediaPlayer mp) {
                                            mMediaPlayer.start();
                                        }
                                    });
                                    mMediaPlayer.setLooping(true);
                                    mMediaPlayer.prepare();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                });
            }

            @Override
            public void recordZoom(float zoom) {
                CameraInterface.CAMERAINSTANCE.setZoom(zoom, CameraInterface.TYPE_RECORDER);
            }

            @Override
            public void recordError() {
                //错误回调
                if (errorLisenter != null) {
                    errorLisenter.AudioPermissionError();
                }
            }
        });
        mCaptureLayout.setTypeLisenter(new TypeLisenter() {
            @Override
            public void cancel() {
                if (CAMERA_STATE == STATE_WAIT) {
                    if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                        mMediaPlayer.stop();
                        mMediaPlayer.release();
                        mMediaPlayer = null;
                    }
                    handlerPictureOrVideo(type, false);
                }
            }

            @Override
            public void confirm() {
                if (CAMERA_STATE == STATE_WAIT) {
                    if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                        mMediaPlayer.stop();
                        mMediaPlayer.release();
                        mMediaPlayer = null;
                    }
                    handlerPictureOrVideo(type, true);
                }
            }
        });
        mCaptureLayout.setReturnLisenter(new ReturnLisenter() {
            @Override
            public void onReturn() {
                if (jCameraLisenter != null && !takePictureing) {
                    jCameraLisenter.quit();
                }
            }
        });
        //END >>>>>>> captureLayout lisenter callback
        mVideoView.getHolder().addCallback(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        float widthSize = MeasureSpec.getSize(widthMeasureSpec);
        float heightSize = MeasureSpec.getSize(heightMeasureSpec);
        screenProp = heightSize / widthSize;
    }

    @Override
    public void cameraHasOpened() {
        CameraInterface.CAMERAINSTANCE.doStartPreview(mVideoView.getHolder(), screenProp);
    }

    private boolean switching = false;

    @Override
    public void cameraSwitchSuccess() {
        switching = false;
    }

    /**
     * start preview
     */
    public void onResume() {
        CameraInterface.CAMERAINSTANCE.registerSensorManager(Utils.getContext());
        if (mIsLmCapture) {
            CameraInterface.CAMERAINSTANCE.setSwitchView(mSwitchCamera);
        }
        if (onlyPause) {
            new Thread() {
                @Override
                public void run() {
                    CameraInterface.CAMERAINSTANCE.doOpenCamera(JCameraView.this);
                }
            }.start();
        }
        mFoucsView.setVisibility(INVISIBLE);
    }

//    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        setFocusViewWidthAnimation(getMeasuredWidth() / 2, getMeasuredHeight() / 2);
//    }


    /**
     * stop preview
     */
    public void onPause() {
        onlyPause = true;
        CameraInterface.CAMERAINSTANCE.unregisterSensorManager(Utils.getContext());
        CameraInterface.CAMERAINSTANCE.doStopCamera();
    }


    private boolean firstTouch = true;
    private float firstTouchLength = 0;
    private int zoomScale = 0;

    /**
     * handler touch focus
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (event.getPointerCount() == 1) {
                    //显示对焦指示器
                    setFocusViewWidthAnimation(event.getX(), event.getY());
                }
                if (event.getPointerCount() == 2) {
                    LogUtils.i("CJT", "ACTION_DOWN = " + 2);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 1) {
                    firstTouch = true;
                }
                if (event.getPointerCount() == 2) {
                    //第一个点
                    float point_1_X = event.getX(0);
                    float point_1_Y = event.getY(0);
                    //第二个点
                    float point_2_X = event.getX(1);
                    float point_2_Y = event.getY(1);

                    float result = (float) Math.sqrt(Math.pow(point_1_X - point_2_X, 2) + Math.pow(point_1_Y -
                            point_2_Y, 2));

                    if (firstTouch) {
                        firstTouchLength = result;
                        firstTouch = false;
                    }
                    if ((int) (result - firstTouchLength) / 50 != 0) {
                        firstTouch = true;
                        CameraInterface.CAMERAINSTANCE.setZoom(result - firstTouchLength, CameraInterface.TYPE_CAPTURE);
                    }
                    LogUtils.i("CJT", "result = " + (result - firstTouchLength));
                }
                break;
            case MotionEvent.ACTION_UP:
                firstTouch = true;
                break;
        }
        return true;
    }

    /**
     * focusview animation
     */
    private void setFocusViewWidthAnimation(float x, float y) {
        if (isBorrow) {
            return;
        }
        if (y > mCaptureLayout.getTop()) {
            return;
        }
        mFoucsView.setVisibility(VISIBLE);
        if (x < mFoucsView.getWidth() / 2) {
            x = mFoucsView.getWidth() / 2;
        }
        if (x > layout_width - mFoucsView.getWidth() / 2) {
            x = layout_width - mFoucsView.getWidth() / 2;
        }
        if (y < mFoucsView.getWidth() / 2) {
            y = mFoucsView.getWidth() / 2;
        }
        if (y > mCaptureLayout.getTop() - mFoucsView.getWidth() / 2) {
            y = mCaptureLayout.getTop() - mFoucsView.getWidth() / 2;
        }
        CameraInterface.CAMERAINSTANCE.handleFocus(Utils.getContext(), x, y, new CameraInterface.FocusCallback() {
            @Override
            public void focusSuccess() {
                mFoucsView.setVisibility(INVISIBLE);
            }
        });

        mFoucsView.setX(x - mFoucsView.getWidth() / 2);
        mFoucsView.setY(y - mFoucsView.getHeight() / 2);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mFoucsView, "scaleX", 1, 0.6f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mFoucsView, "scaleY", 1, 0.6f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mFoucsView, "alpha", 1f, 0.3f, 1f, 0.3f, 1f, 0.3f, 1f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(scaleX).with(scaleY).before(alpha);
        animSet.setDuration(400);
        animSet.start();
    }

    public void setJCameraLisenter(JCameraLisenter jCameraLisenter) {
        this.jCameraLisenter = jCameraLisenter;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void handlerPictureOrVideo(int type, boolean confirm) {
        if (jCameraLisenter == null || type == -1) {
            return;
        }
        switch (type) {
            case TYPE_PICTURE:
                mPhoto.setVisibility(INVISIBLE);
                if (confirm && captureBitmap != null) {
                    jCameraLisenter.captureSuccess(captureBitmap);
                } else {
                    if (captureBitmap != null) {
                        captureBitmap.recycle();
                    }
                    captureBitmap = null;
                }
                break;
            case TYPE_VIDEO:
                if (confirm) {
                    //回调录像成功后的URL
                    jCameraLisenter.recordSuccess(videoUrl);
                } else {
                    //删除视频
                    File file = new File(videoUrl);
                    if (file.exists()) {
                        file.delete();
                    }
                }
                mCaptureLayout.isRecord(false);
                LayoutParams videoViewParam = new LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT);
//                videoViewParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                mVideoView.setLayoutParams(videoViewParam);
                CameraInterface.CAMERAINSTANCE.doOpenCamera(JCameraView.this);
                break;
        }
        isBorrow = false;
        mSwitchCamera.setVisibility(VISIBLE);
        CAMERA_STATE = STATE_IDLE;
    }

    public void setSaveVideoPath(String path) {
        CameraInterface.CAMERAINSTANCE.setSaveVideoPath(path);
    }

    /**
     * TextureView resize
     */
    public void updateVideoViewSize(float videoWidth, float videoHeight) {
        if (videoWidth > videoHeight) {
            LayoutParams videoViewParam;
            int height = (int) ((videoHeight / videoWidth) * getWidth());
            videoViewParam = new LayoutParams(LayoutParams.MATCH_PARENT,
                    height);
            videoViewParam.gravity = Gravity.CENTER;
//            videoViewParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            mVideoView.setLayoutParams(videoViewParam);
        }
    }

    /**
     * forbidden audio
     */
    public void enableshutterSound(boolean enable) {
    }

    public void forbiddenSwitchCamera(boolean forbiddenSwitch) {
        this.forbiddenSwitch = forbiddenSwitch;
    }

    private ErrorLisenter errorLisenter;

    //启动Camera错误回调
    public void setErrorLisenter(ErrorLisenter errorLisenter) {
        this.errorLisenter = errorLisenter;
        CameraInterface.CAMERAINSTANCE.setErrorLinsenter(errorLisenter);
    }

    //设置CaptureButton功能（拍照和录像）
    public void setFeatures(int state) {
        this.mCaptureLayout.setButtonFeatures(state);
    }

    //设置录制质量
    public void setMediaQuality(int quality) {
        CameraInterface.CAMERAINSTANCE.setMediaQuality(quality);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LogUtils.i("CJT", "surfaceCreated");
        new Thread() {
            @Override
            public void run() {
                CameraInterface.CAMERAINSTANCE.doOpenCamera(JCameraView.this);
            }
        }.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        onlyPause = false;
        LogUtils.i("CJT", "surfaceDestroyed");
        CameraInterface.CAMERAINSTANCE.doDestroyCamera();
    }

    TakePictureListener mTakePictureListener;

    public interface TakePictureListener{
        void takePictureSuccess(Bitmap bitmap);
    }

    public void setTakePictureListener(TakePictureListener listener) {
        mTakePictureListener = listener;
    }

    public void takePicture() {
        if (CAMERA_STATE != STATE_IDLE || takePictureing) {
            return;
        }
        CAMERA_STATE = STATE_RUNNING;
        takePictureing = true;
        mFoucsView.setVisibility(INVISIBLE);
        CameraInterface.CAMERAINSTANCE.takePicture(new CameraInterface.TakePictureCallback() {
            @Override
            public void captureResult(Bitmap bitmap, boolean isVertical) {
                CameraInterface.CAMERAINSTANCE.doStopCamera();
                type = TYPE_PICTURE;
                isBorrow = false;
                CAMERA_STATE = STATE_IDLE;
                takePictureing = false;
                //                CameraInterface.CAMERAINSTANCE.doOpenCamera(JCameraView.this);
                mTakePictureListener.takePictureSuccess(bitmap);
            }
        });
    }


    public void switchCamera() {
        if (isBorrow || switching || forbiddenSwitch) {
            return;
        }
        switching = true;
        new Thread() {
            /**
             * switch camera
             */
            @Override
            public void run() {
                CameraInterface.CAMERAINSTANCE.switchCamera(JCameraView.this);
            }
        }.start();
    }

    public void setControlGone() {
        mCaptureLayout.setVisibility(GONE);
        mSwitchCamera.setVisibility(GONE);
        mIsLmCapture = false;
    }
}
