package com.cjt2325.cameralibrary;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.widget.ImageView;

import com.cjt2325.cameralibrary.lisenter.ErrorLisenter;
import com.cjt2325.cameralibrary.util.AngleUtil;
import com.cjt2325.cameralibrary.util.CameraParamUtil;
import com.cjt2325.cameralibrary.util.CheckPermission;
import com.cjt2325.cameralibrary.util.DeviceUtil;
import com.cjt2325.cameralibrary.util.ScreenUtils;
import com.lingdanet.safeguard.common.constant.CommonConstant;
import com.lingdanet.safeguard.common.utils.Configuration;
import com.lingdanet.safeguard.common.utils.ImageUtils;
import com.lingdanet.safeguard.common.utils.LogUtils;
import com.lingdanet.safeguard.common.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * =====================================
 * 作    者: 陈嘉桐
 * 版    本：1.0.4
 * 创建日期：2017/4/25
 * 描    述：camera操作单例
 * =====================================
 */
@SuppressWarnings("deprecation")
public enum CameraInterface {

    CAMERAINSTANCE;
    private static final String TAG = "CJT";

    private Camera mCamera;
    private Camera.Parameters mParams;
    private boolean isPreviewing = false;

    boolean isPreviewing() {
        return isPreviewing;
    }



    private int SELECTED_CAMERA = -1;
    private int CAMERA_POST_POSITION = -1;
    private int CAMERA_FRONT_POSITION = -1;

    private SurfaceHolder mHolder = null;
    private float screenProp = -1.0f;

    private boolean isRecorder = false;
    private MediaRecorder mediaRecorder;
    private String videoFileName;
    private String saveVideoPath;
    private String videoFileAbsPath;

    private ErrorLisenter errorLisenter;

    private ImageView mSwitchView;

    public void setSwitchView(ImageView mSwitchView) {
        this.mSwitchView = mSwitchView;
    }


    private int preview_width;
    private int preview_height;

    private Context mContext;

    public void setContext(Context context) {
        mContext = context;
    }

    private int angle = 0;
    private int rotation = 0;
    private boolean error = false;

    //视频质量
    private int mediaQuality = JCameraView.MEDIA_QUALITY_MIDDLE;

    private SensorManager sm = null;

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
            if (Sensor.TYPE_ACCELEROMETER != event.sensor.getType()) {
                return;
            }
            float[] values = event.values;
            angle = AngleUtil.getSensorAngle(values[0], values[1]);
            rotationAnimation();
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };


    //切换摄像头icon跟随手机角度进行旋转
    private void rotationAnimation() {
        if (mSwitchView == null) {
            return;
        }
        if (rotation != angle) {
            int start_rotaion = 0;
            int end_rotation = 0;
            switch (rotation) {
                case 0:
                    start_rotaion = 0;
                    switch (angle) {
                        case 90:
                            end_rotation = -90;
                            break;
                        case 270:
                            end_rotation = 90;
                            break;
                    }
                    break;
                case 90:
                    start_rotaion = -90;
                    switch (angle) {
                        case 0:
                            end_rotation = 0;
                            break;
                        case 180:
                            end_rotation = -180;
                            break;
                    }
                    break;
                case 180:
                    start_rotaion = 180;
                    switch (angle) {
                        case 90:
                            end_rotation = 270;
                            break;
                        case 270:
                            end_rotation = 90;
                            break;
                    }
                    break;
                case 270:
                    start_rotaion = 90;
                    switch (angle) {
                        case 0:
                            end_rotation = 0;
                            break;
                        case 180:
                            end_rotation = 180;
                            break;
                    }
                    break;
            }
            ObjectAnimator anim = ObjectAnimator.ofFloat(mSwitchView, "rotation", start_rotaion, end_rotation);
            anim.setDuration(500);
            anim.start();
            rotation = angle;
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void setSaveVideoPath(String saveVideoPath) {
        this.saveVideoPath = saveVideoPath;
        File file = new File(saveVideoPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static final int TYPE_RECORDER = 0x090;
    public static final int TYPE_CAPTURE = 0x091;
    private int nowScaleRate = 0;

    private int recordScleRate = 0;

    public void setZoom(float zoom, int type) {
        if (mCamera == null) {
            return;
        }
        if (mParams == null) {
            mParams = mCamera.getParameters();
        }
        if (!mParams.isZoomSupported() || !mParams.isSmoothZoomSupported()) {
            return;
        }
        switch (type) {
            case TYPE_RECORDER:
                //如果不是录制视频中，上滑不会缩放
                if (!isRecorder) {
                    return;
                }
                if (zoom >= 0) {
                    //每移动50个像素缩放一个级别
                    int scaleRate = (int) (zoom / 50);
                    if (scaleRate <= mParams.getMaxZoom() && scaleRate >= nowScaleRate && recordScleRate != scaleRate) {
                        mParams.setZoom(scaleRate);
                        mCamera.setParameters(mParams);
                        recordScleRate = scaleRate;
                    }
                }
                break;
            case TYPE_CAPTURE:
                if (isRecorder) {
                    return;
                }
                //每移动50个像素缩放一个级别
                int scaleRate = (int) (zoom / 50);
                if (scaleRate < mParams.getMaxZoom()) {
                    nowScaleRate += scaleRate;
                    if (nowScaleRate < 0) {
                        nowScaleRate = 0;
                    } else if (nowScaleRate > mParams.getMaxZoom()) {
                        nowScaleRate = mParams.getMaxZoom();
                    }
                    mParams.setZoom(nowScaleRate);
                    mCamera.setParameters(mParams);
                }
                LogUtils.i("CJT", "nowScaleRate = " + nowScaleRate);
                break;
        }

    }

    public void setMediaQuality(int quality) {
        this.mediaQuality = quality;
    }

    interface CamOpenOverCallback {
        void cameraHasOpened();

        void cameraSwitchSuccess();
    }

    private CameraInterface() {
        findAvailableCameras();
        SELECTED_CAMERA = CAMERA_POST_POSITION;
        saveVideoPath = "";
    }


    /**
     * open Camera
     */
    void doOpenCamera(CamOpenOverCallback callback) {
        if (!CheckPermission.isCameraUseable(SELECTED_CAMERA) && this.errorLisenter != null) {
            this.errorLisenter.onError();
            return;
        }
        if (mCamera == null) {
            openCamera(SELECTED_CAMERA);
        }
        callback.cameraHasOpened();
    }

    private void openCamera(int id) {
        try {
            this.mCamera = Camera.open(id);
        } catch (Exception var3) {
            if (this.errorLisenter != null) {
                this.errorLisenter.onError();
            }
        }

        if (Build.VERSION.SDK_INT > 17 && this.mCamera != null) {
            try {
                this.mCamera.enableShutterSound(false);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.e("CJT", "enable shutter sound faild");
            }
        }
    }

    public synchronized void switchCamera(CamOpenOverCallback callback) {
        if (SELECTED_CAMERA == CAMERA_POST_POSITION) {
            SELECTED_CAMERA = CAMERA_FRONT_POSITION;
        } else {
            SELECTED_CAMERA = CAMERA_POST_POSITION;
        }
        doStopCamera();
        mCamera = Camera.open(SELECTED_CAMERA);
        doStartPreview(mHolder, screenProp);
        callback.cameraSwitchSuccess();
    }

    /**
     * doStartPreview
     */
    void doStartPreview(SurfaceHolder holder, float screenProp) {
        if (this.screenProp < 0) {
            this.screenProp = screenProp;
        }
        if (holder == null) {
            return;
        }
        this.mHolder = holder;
        if (mCamera != null) {
            try {
                mParams = mCamera.getParameters();
                Camera.Size previewSize = CameraParamUtil.getInstance().getPreviewSize(mParams
                        .getSupportedPreviewSizes(), ScreenUtils.getScreenWidth(Utils.getContext()), screenProp);
                Camera.Size pictureSize = CameraParamUtil.getInstance().getPictureSize(mParams
                        .getSupportedPictureSizes(), ScreenUtils.getScreenWidth(Utils.getContext()), screenProp);

                mParams.setPreviewSize(previewSize.width, previewSize.height);

                preview_width = previewSize.width;
                preview_height = previewSize.height;

                mParams.setPictureSize(pictureSize.width, pictureSize.height);

                if (CameraParamUtil.getInstance().isSupportedFocusMode(
                        mParams.getSupportedFocusModes(),
                        Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                    mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                }
                if (CameraParamUtil.getInstance().isSupportedPictureFormats(mParams.getSupportedPictureFormats(),
                        ImageFormat.JPEG)) {
                    mParams.setPictureFormat(ImageFormat.JPEG);
                    mParams.setJpegQuality(100);
                }
                mCamera.setParameters(mParams);
                mParams = mCamera.getParameters();
                //SurfaceView
                mCamera.setPreviewDisplay(holder);
                mCamera.setDisplayOrientation(90);
                mCamera.startPreview();
                mCamera.cancelAutoFocus();
                isPreviewing = true;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
//                mCamera.stopPreview();
            }
        }
        LogUtils.i(TAG, "=== Start Preview ===");
    }

    /**
     * 停止预览，释放Camera
     */
    public void doStopCamera() {
        if (null != mCamera) {
            try {
                mCamera.stopPreview();
                //这句要在stopPreview后执行，不然会卡顿或者花屏
                mCamera.setPreviewDisplay(null);
                isPreviewing = false;
                mCamera.release();
                mCamera = null;
                LogUtils.i(TAG, "=== Stop Camera ===");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void doDestroyCamera() {
        errorLisenter = null;
        if (null != mCamera) {
            try {
                sm = null;
                mCamera.setPreviewCallback(null);
                mSwitchView = null;
                mCamera.stopPreview();
                //这句要在stopPreview后执行，不然会卡顿或者花屏
                mCamera.setPreviewDisplay(null);
                mHolder = null;
                isPreviewing = false;
                mCamera.release();
                mCamera = null;
                LogUtils.i(TAG, "=== Destroy Camera ===");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 拍照
     */

    void takePicture(final TakePictureCallback callback) {
        if (mCamera == null) {
            return;
        }
        final int nowAngle = (angle + 90) % 360;
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                LogUtils.d("JCameraView", "onPictureTaken start time = " + System.currentTimeMillis());
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                bitmap = ImageUtils.getScaledBitmap(bitmap, CommonConstant.MAX_IMAGE_BOUNDS);

                LogUtils.i("JCameraView", "onPictureTaken end time = " + System.currentTimeMillis());
                Matrix matrix = new Matrix();
                if (SELECTED_CAMERA == CAMERA_POST_POSITION) {
                    matrix.setRotate(nowAngle);
                } else if (SELECTED_CAMERA == CAMERA_FRONT_POSITION) {
                    matrix.setRotate(270);
                    matrix.postScale(-1, 1);
                }
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                if (callback != null) {
                    if (nowAngle == 90 || nowAngle == 270) {
                        callback.captureResult(bitmap, true);
                    } else {
                        callback.captureResult(bitmap, false);
                    }
                }
            }
        });
    }

    void startRecord(Surface surface, ErrorCallback callback) {
        LogUtils.i(TAG, "startRecord: " + System.currentTimeMillis());
        if (isRecorder) {
            return;
        }
        int nowAngle = (angle + 90) % 360;
        if (mCamera == null) {
            openCamera(SELECTED_CAMERA);
        }
        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
        }
        if (mParams == null) {
            mParams = mCamera.getParameters();
        }
        List<String> focusModes = mParams.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }
        mCamera.setParameters(mParams);
        mCamera.unlock();
        mediaRecorder.reset();
        mediaRecorder.setCamera(mCamera);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);


        Camera.Size videoSize;
        if (mParams.getSupportedVideoSizes() == null) {
            videoSize = CameraParamUtil.getInstance().getPictureSize(mParams.getSupportedPreviewSizes(),
                    ScreenUtils.getScreenWidth(mContext), screenProp);
        } else {
            videoSize = CameraParamUtil.getInstance().getPictureSize(mParams.getSupportedVideoSizes(),
                    ScreenUtils.getScreenWidth(mContext), screenProp);
        }
//        LogUtils.i(TAG, "setVideoSize    width = " + videoSize.width + "height = " + videoSize.height);
        if (videoSize.width == videoSize.height) {
            mediaRecorder.setVideoSize(preview_width, preview_height);
        } else {
            mediaRecorder.setVideoSize(videoSize.width, videoSize.height);
        }
//        mediaRecorder.setVideoSize(640, 480);
        if (SELECTED_CAMERA == CAMERA_FRONT_POSITION) {
            mediaRecorder.setOrientationHint(270);
        } else {
            mediaRecorder.setOrientationHint(nowAngle);
//            mediaRecorder.setOrientationHint(90);
        }
        if (DeviceUtil.isHuaWeiRongyao()) {
            mediaRecorder.setVideoEncodingBitRate(mediaQuality);
        } else {
            mediaRecorder.setVideoEncodingBitRate(mediaQuality);
        }
        mediaRecorder.setPreviewDisplay(surface);

        videoFileName = "video_" + System.currentTimeMillis() + ".mp4";
//        videoFileName = "video_" + System.currentTimeMillis() + ".3gp";
        if (saveVideoPath.equals("")) {
            saveVideoPath = Configuration.getVideoDirectoryPath();
        }
        videoFileAbsPath = saveVideoPath + videoFileName;
        mediaRecorder.setOutputFile(videoFileAbsPath);
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecorder = true;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            LogUtils.i("CJT", "startRecord IllegalStateException");
            if (this.errorLisenter != null) {
                this.errorLisenter.onError();
            }
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.i("CJT", "startRecord IOException");
            if (this.errorLisenter != null) {
                this.errorLisenter.onError();
            }
        } catch (RuntimeException e) {
            LogUtils.i("CJT", "startRecord RuntimeException");

        }
    }

    void stopRecord(boolean isShort, StopRecordCallback callback) {
        LogUtils.i(TAG, "stopRecord: " + System.currentTimeMillis());
        if (!isRecorder) {
            return;
        }
        if (mediaRecorder != null) {
            mediaRecorder.setOnErrorListener(null);
            mediaRecorder.setOnInfoListener(null);
            mediaRecorder.setPreviewDisplay(null);
            try {
                mediaRecorder.stop();
            } catch (RuntimeException e) {
                e.printStackTrace();
                mediaRecorder = null;
                mediaRecorder = new MediaRecorder();
                LogUtils.i("CJT", "stop RuntimeException");
            } catch (Exception e) {
                e.printStackTrace();
                mediaRecorder = null;
                mediaRecorder = new MediaRecorder();
                LogUtils.i("CJT", "stop Exception");
            } finally {
                if (mediaRecorder != null) {
                    mediaRecorder.release();
                }
                mediaRecorder = null;
                isRecorder = false;
            }
            if (isShort) {
                //delete video file
                boolean result = true;
                File file = new File(videoFileAbsPath);
                if (file.exists()) {
                    result = file.delete();
                }
                if (result) {
                    callback.recordResult(null);
                }
                return;
            }
            doStopCamera();
            callback.recordResult(videoFileAbsPath);
        }
    }

    private void findAvailableCameras() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        int cameraNum = Camera.getNumberOfCameras();
        for (int i = 0; i < cameraNum; i++) {
            Camera.getCameraInfo(i, info);
            switch (info.facing) {
                case Camera.CameraInfo.CAMERA_FACING_FRONT:
                    CAMERA_FRONT_POSITION = info.facing;
                    break;
                case Camera.CameraInfo.CAMERA_FACING_BACK:
                    CAMERA_POST_POSITION = info.facing;
                    break;
            }
        }
    }


    public void handleFocus(final Context context, final float x, final float y, final FocusCallback callback) {
        if (mCamera == null) {
            return;
        }
        final Camera.Parameters params;
        try {
            params = mCamera.getParameters();
        } catch (RuntimeException e) {
            e.printStackTrace();
            LogUtils.i(TAG, "get Camera Parameters fail");
            callback.focusSuccess();
            return;
        }
        Rect focusRect = calculateTapArea(x, y, 1f, context);
        mCamera.cancelAutoFocus();
        if (params.getMaxNumFocusAreas() > 0) {
            List<Camera.Area> focusAreas = new ArrayList<>();
            focusAreas.add(new Camera.Area(focusRect, 800));
            params.setFocusAreas(focusAreas);
        } else {
            LogUtils.i(TAG, "focus areas not supported");
            callback.focusSuccess();
            return;
        }
        final String currentFocusMode = params.getFocusMode();
        try {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            mCamera.setParameters(params);
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    if (success) {
                        Camera.Parameters params = camera.getParameters();
                        params.setFocusMode(currentFocusMode);
                        camera.setParameters(params);
                        callback.focusSuccess();
                    } else {
                        handleFocus(context, x, y, callback);
                    }
                }
            });
        } catch (Exception e) {
            LogUtils.e(TAG, "autoFocus failer");
        }
    }


    private static Rect calculateTapArea(float x, float y, float coefficient, Context context) {
        float focusAreaSize = 300;
        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();
        int centerX = (int) (x / ScreenUtils.getScreenHeight(context) * 2000 - 1000);
        int centerY = (int) (y / ScreenUtils.getScreenWidth(context) * 2000 - 1000);
        LogUtils.i("CJT", "FocusArea centerX = " + centerX + " , centerY = " + centerY);
        int left = clamp(centerX - areaSize / 2, -1000, 1000);
        int top = clamp(centerY - areaSize / 2, -1000, 1000);
        RectF rectF = new RectF(left, top, left + areaSize, top + areaSize);
        return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF
                .bottom));
    }

    private static int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

    public void setErrorLinsenter(ErrorLisenter errorLisenter) {
        this.errorLisenter = errorLisenter;
    }


    interface StopRecordCallback {
        void recordResult(String url);
    }

    interface ErrorCallback {
        void onError();
    }

    interface TakePictureCallback {
        void captureResult(Bitmap bitmap, boolean isVertical);
    }

    interface FocusCallback {
        void focusSuccess();

    }


    void registerSensorManager(Context context) {
        if (sm == null) {
            sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        }
        sm.registerListener(sensorEventListener, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager
                .SENSOR_DELAY_NORMAL);
    }

    void unregisterSensorManager(Context context) {
        if (sm == null) {
            sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        }
        sm.unregisterListener(sensorEventListener);
    }
}