package com.cjt2325.cameralibrary;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cjt2325.cameralibrary.lisenter.ErrorLisenter;
import com.cjt2325.cameralibrary.lisenter.JCameraLisenter;
import com.cjt2325.cameralibrary.util.DeviceUtil;
import com.lingdanet.safeguard.common.constant.CommonConstant;
import com.lingdanet.safeguard.common.utils.Configuration;
import com.lingdanet.safeguard.common.utils.FileUtils;
import com.lingdanet.safeguard.common.utils.ImageUtils;
import com.lingdanet.safeguard.common.utils.LogUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.lingdanet.safeguard.common.utils.Utils;

import java.io.File;

public class CaptureActivity extends AppCompatActivity {
    public static final String EXTRA_CAPTURE_ITEM = "extra_capture_item";
    public static final String EXTRA_IS_VIDEO = "extra_is_video";
    public static final String EXTRA_ALBUM = "extra_album";
    private JCameraView jCameraView;
    private String saveVideoDir;

    private TextView mAlbumTv;
    private RelativeLayout mFaceRl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        CameraInterface.CAMERAINSTANCE.setContext(Utils.getContext());
        setContentView(R.layout.activity_capture);
        mAlbumTv = (TextView) findViewById(R.id.album_tv);
        mFaceRl = (RelativeLayout) findViewById(R.id.face_rtl);
        mAlbumTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(EXTRA_ALBUM, true);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        int styleExtra = getIntent().getIntExtra("styleExtra", 0);
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        jCameraView = (JCameraView) findViewById(R.id.jcameraview);
        if (styleExtra == 1) {
            //标明是Face过来的
            mFaceRl.setVisibility(View.VISIBLE);
            jCameraView.setFaceTopMargin(50);
            jCameraView.setBackGone();
        }
        saveVideoDir = Configuration.getVideoDirectoryPath();
        //设置视频保存路径
        jCameraView.setSaveVideoPath(saveVideoDir);
        jCameraView.setMediaQuality(JCameraView.MEDIA_QUALITY_MIDDLE);

        //JCameraView监听
        jCameraView.setJCameraLisenter(new JCameraLisenter() {
            @Override
            public void captureSuccess(Bitmap bitmap) {
                //获取图片bitmap
                LogUtils.i("JCameraView", "width = " + bitmap.getWidth() + ", height = " + bitmap.getHeight());
                LogUtils.i("JCameraView", "start time = " + System.currentTimeMillis());
                File destFile = FileUtils.createFile(Configuration.getPictureDirectoryPath(), "IMG_", ".jpeg");
                boolean ret = ImageUtils.save(bitmap, destFile, Bitmap.CompressFormat.JPEG,
                        CommonConstant.MAX_IMAGE_SIZE, false);
                LogUtils.i("JCameraView", "end time = " + System.currentTimeMillis());
                LogUtils.i("JCameraView", "url = " + destFile);
                Intent intent = new Intent();
                intent.putExtra(EXTRA_CAPTURE_ITEM, ret ? destFile.getAbsolutePath() : "");
                intent.putExtra(EXTRA_IS_VIDEO, false);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void recordSuccess(String url) {
                //获取视频路径
                LogUtils.i("JCameraView", "url = " + url);
                Intent intent = new Intent();
                intent.putExtra(EXTRA_CAPTURE_ITEM, url);
                intent.putExtra(EXTRA_IS_VIDEO, true);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void quit() {
                //退出按钮
                CaptureActivity.this.finish();
            }
        });
        jCameraView.setErrorLisenter(new ErrorLisenter() {
            @Override
            public void onError() {
                //错误监听
                LogUtils.i("JCameraView", "camera error");
                Intent intent = new Intent();
                setResult(103, intent);
                finish();
            }

            @Override
            public void AudioPermissionError() {
                ToastUtils.showShort("给点录音权限可以?");
            }
        });
        LogUtils.i("JCameraView", DeviceUtil.getDeviceModel());
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtils.i("JCameraView", "onStart");
        //全屏显示
        if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
        }
        int typeCapture = getIntent().getIntExtra(JCameraView.TYPE_CAPTURE, JCameraView.BUTTON_STATE_BOTH);
        if (typeCapture == JCameraView.BUTTON_STATE_ONLY_CAPTURE) {
            jCameraView.setFeatures(JCameraView.BUTTON_STATE_ONLY_CAPTURE);
        } else if (typeCapture == JCameraView.BUTTON_STATE_BOTH) {
            jCameraView.setFeatures(JCameraView.BUTTON_STATE_BOTH);
        } else if (typeCapture == JCameraView.BUTTON_STATE_ONLY_RECORDER) {
            jCameraView.setFeatures(JCameraView.BUTTON_STATE_ONLY_RECORDER);
        }
    }


    @Override
    protected void onResume() {
        LogUtils.i("JCameraView", "onResume");
        super.onResume();
        jCameraView.onResume();
    }

    @Override
    protected void onPause() {
        LogUtils.i("JCameraView", "onPause");
        super.onPause();
        jCameraView.onPause();
    }
}

