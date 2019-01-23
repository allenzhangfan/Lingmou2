package cloud.antelope.lingmou.mvp.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cjt2325.cameralibrary.CameraInterface;
import com.cjt2325.cameralibrary.JCameraView;
import com.jess.arms.http.imageloader.glide.GlideArms;
import com.lingdanet.safeguard.common.constant.CommonConstant;
import com.lingdanet.safeguard.common.utils.Configuration;
import com.lingdanet.safeguard.common.utils.FileUtils;
import com.lingdanet.safeguard.common.utils.ImageUtils;
import com.lingdanet.safeguard.common.utils.LogUtils;
import com.lingdanet.safeguard.common.utils.SizeUtils;
import com.lingdanet.safeguard.common.utils.Utils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.BarViewUtils;
import cloud.antelope.lingmou.app.utils.LogUploadUtil;
import cloud.antelope.lingmou.app.utils.ResultHolder;
import cloud.antelope.lingmou.mvp.model.entity.LogUploadRequest;
import cloud.antelope.lingmou.mvp.ui.widget.CaptureAngel;
import timber.log.Timber;

/**
 * Created by ChenXinming on 2018/1/19.
 * description:
 */

public class LmCaptureActivity extends Activity implements JCameraView.TakePictureListener {

    @BindView(R.id.camera_view)
    JCameraView mCameraView;
    @BindView(R.id.album_btn)
    ImageView mAlbumBtn;
    @BindView(R.id.capture_btn)
    ImageButton mCaptureBtn;
    @BindView(R.id.front_btn)
    ImageButton mFrontBtn;
    @BindView(R.id.blackCover)
    View mBlackCover;
    @BindView(R.id.capture_hint_tv)
    TextView mCaptureHintTv;
    @BindView(R.id.capture_rl)
    RelativeLayout mCaptureRl;
    @BindView(R.id.capture_base_rl)
    RelativeLayout mCaptureBaseRl;
    @BindView(R.id.back_btn)
    ImageView mBackImg;
    @BindView(R.id.flash_view)
    View mFlashView;

    private boolean mIsCanClick = true;
    private Bitmap b;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_lm);
        ButterKnife.bind(this);
        CameraInterface.CAMERAINSTANCE.setContext(Utils.getContext());
        initData();
        LogUploadUtil.uploadLog(new LogUploadRequest(105700, 105700, "进入以图搜图界面"));
    }

    private void initData() {
        mCaptureBaseRl.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = mCaptureBaseRl.getWidth();
                int height = mCaptureBaseRl.getHeight();
                if (height > width) {
                    int tvHeight = height - width;
                    ViewGroup.LayoutParams layoutParams = mCaptureHintTv.getLayoutParams();
                    layoutParams.height = tvHeight;
                    mCaptureHintTv.setLayoutParams(layoutParams);
                    LogUtils.i("cxm", "width = " + mCaptureRl.getWidth() + ", height = " + mCaptureRl.getHeight());
                    for (int i = 0; i < 4; i++) {
                        CaptureAngel captureAngel = new CaptureAngel(LmCaptureActivity.this);
                        captureAngel.setDirection(i);
                        RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(50, 50);
                        switch (i) {
                            case 0:
                                rlParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                                break;
                            case 1:
                                rlParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                break;
                            case 2:
                                rlParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                                rlParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                                break;
                            case 3:
                                rlParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                rlParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                                break;
                        }
                        captureAngel.setLayoutParams(rlParams);
                        mCaptureRl.addView(captureAngel);
                    }
                }
                mCaptureBaseRl.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        initLatestAlbum();

    }

    private void initLatestAlbum() {

        Cursor cursor = getApplicationContext().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null,
                ContactsContract.Contacts.Photo._ID + " desc");
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                String mediaPath = cursor.getString(cursor
                        .getColumnIndex(MediaStore.MediaColumns.DATA));
                GlideArms.with(LmCaptureActivity.this).asBitmap().load(mediaPath).centerCrop().into(mAlbumBtn);
            }
        }
        cursor.close();
    }

    @OnClick({R.id.album_btn, R.id.capture_btn, R.id.back_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.album_btn:
                startActivity(new Intent(LmCaptureActivity.this, LmImageGridActivity.class));
                break;
            case R.id.capture_btn:
                if (mIsCanClick) {
                    mIsCanClick = false;
                    showTakePictureAnim();
                    mCameraView.takePicture();
                }
                break;
            case R.id.back_btn:
                finish();
                break;
        }
    }

    private void showTakePictureAnim() {

        ValueAnimator animator = ValueAnimator.ofFloat(0.8f, 0f);
        animator.setDuration(250);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha = (float) animation.getAnimatedValue();
                mFlashView.setAlpha(alpha);
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mFlashView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mFlashView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    @OnTouch(R.id.front_btn)
    boolean onTouchFaceing(final View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                view.setEnabled(false);
                view.setEnabled(false);
                mBlackCover.setAlpha(0.5f);
                mBlackCover.setVisibility(View.VISIBLE);
                mBlackCover.animate()
                        .alpha(0.5f)
                        .setStartDelay(0)
                        .setDuration(300)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                mCameraView.switchCamera();
                                mBlackCover.animate()
                                        .alpha(0)
                                        .setStartDelay(200)
                                        .setDuration(300)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                super.onAnimationEnd(animation);
                                                mBlackCover.setVisibility(View.GONE);
                                                view.setEnabled(true);
                                            }
                                        })
                                        .start();
                            }
                        })
                        .start();
                break;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
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
        mCameraView.setFeatures(JCameraView.BUTTON_STATE_ONLY_CAPTURE);
    }

    @Override
    protected void onResume() {
        LogUtils.i("JCameraView", "onResume");
        super.onResume();
        mCameraView.setTakePictureListener(this);
        mCameraView.setControlGone();
        mCameraView.onResume();
        mIsCanClick = true;
    }

    @Override
    protected void onPause() {
        LogUtils.i("JCameraView", "onPause");
        super.onPause();
        mCameraView.setTakePictureListener(null);
        mCameraView.onPause();
    }

    @Override
    public void takePictureSuccess(Bitmap bitmap) {
        mIsCanClick = true;
        File destFile = FileUtils.createFile(Configuration.getPictureDirectoryPath(), "IMG_", ".jpeg");
        boolean ret = ImageUtils.save(bitmap, destFile, Bitmap.CompressFormat.JPEG,
                CommonConstant.MAX_IMAGE_SIZE, false);
        galleryAddPic(LmCaptureActivity.this, destFile);
        ResultHolder.disposeBitmap();

        float scale = bitmap.getHeight() *1.0f/ SizeUtils.getScreenHeight();
        int[] location=new int[2];
        int clipX=0;
        mFlashView.getLocationInWindow(location);
        float clipWidth = mFlashView.getWidth()*scale ;//
        float clipHeight = mFlashView.getHeight()*scale ;
        if(clipWidth>=bitmap.getWidth()){
            clipWidth=bitmap.getWidth();
        }else {
            clipX= (int) ((bitmap.getWidth()-clipWidth)/2);
        }
        if(clipHeight+location[1]*scale>bitmap.getHeight()){
            clipHeight=bitmap.getHeight()-location[1]*scale;
        }
        ResultHolder.setImage(ImageUtils.clip(bitmap, clipX, (int) (location[1]*scale), (int) clipWidth, (int) clipHeight));
//        ResultHolder.setImage(bitmap);
        Intent intent = new Intent(LmCaptureActivity.this, LmCropActivity.class);
        intent.putExtra("fromCapture", true);
        intent.putExtra("desFilePath", destFile.getAbsolutePath());
        mCaptureRl.getLocationOnScreen(location);
        intent.putExtra("top", location[1]);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCameraView.setTakePictureListener(null);
    }

    /**
     * 扫描图片
     */
    public static void galleryAddPic(Context context, File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }
}
