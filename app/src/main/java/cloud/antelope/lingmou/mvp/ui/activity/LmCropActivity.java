package cloud.antelope.lingmou.mvp.ui.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cjt2325.cameralibrary.util.ScreenUtils;
import com.google.gson.Gson;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.net.RequestUtils;
import com.lingdanet.safeguard.common.utils.Configuration;
import com.lingdanet.safeguard.common.utils.FileUtils;
import com.lingdanet.safeguard.common.utils.ImageUtils;
import com.lingdanet.safeguard.common.utils.SizeUtils;
import com.lingdanet.safeguard.common.utils.TimeUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.nanchen.compresshelper.CompressHelper;

import java.io.File;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.BarViewUtils;
import cloud.antelope.lingmou.app.utils.FormatUtils;
import cloud.antelope.lingmou.app.utils.ResultHolder;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.di.component.DaggerLmCropComponent;
import cloud.antelope.lingmou.di.module.LmCropModule;
import cloud.antelope.lingmou.mvp.contract.LmCropContract;
import cloud.antelope.lingmou.mvp.model.entity.EmptyEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceKeyEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceNewEntity;
import cloud.antelope.lingmou.mvp.presenter.LmCropPresenter;
import cloud.antelope.lingmou.mvp.ui.widget.LmCropImageView;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class LmCropActivity extends BaseActivity<LmCropPresenter> implements LmCropContract.View,
        LmCropImageView.OnBitmapSaveCompleteListener {

    @BindView(R.id.crop_iv)
    LmCropImageView mCropImageview;
    @BindView(R.id.recog_tv)
    TextView mRecogTv;
    @BindView(R.id.recog_iv)
    ImageView mRecogIv;
    @BindView(R.id.recog_start_iv)
    ImageView mRecogStartIv;
    @BindView(R.id.ni_img)
    ImageView mNiImg;
    @BindView(R.id.shun_img)
    ImageView mShunImg;
    @BindView(R.id.back_iv)
    ImageView mBackIv;
    @BindView(R.id.hint_tv)
    TextView mHintView;
    @BindView(R.id.bg_view)
    View mBgView;
    @BindView(R.id.crop_card_view)
    CardView mCropCardView;
    @BindView(R.id.recog_rl)
    LinearLayout mRecogRl;
    @BindView(R.id.root_crop_rl)
    RelativeLayout mRootCropRl;

    private AnimationDrawable mAnimationDrawable;
    private Bitmap mSourceBitmap;
    private boolean mIsFromCapture;
    private String mSourceImgPath;
    private String mCropImgPath;
    private boolean mIsLoading;
    private int mScreenHeight;
    private String mFeature;

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        DaggerLmCropComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .lmCropModule(new LmCropModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_lm_crop; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mIsFromCapture = getIntent().getBooleanExtra("fromCapture", false);
        if (!mIsFromCapture) {
            mTintManager.setStatusBarTintResource(R.color.yellow_ffb300);
            mSourceImgPath = getIntent().getStringExtra("img_path");
            mRecogRl.setVisibility(View.VISIBLE);
            mHintView.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams cardParams = (LinearLayout.LayoutParams) mCropCardView.getLayoutParams();
            cardParams.topMargin = SizeUtils.dp2px(15);
            mCropCardView.setLayoutParams(cardParams);

            LinearLayout.LayoutParams hintTvParams = (LinearLayout.LayoutParams) mHintView.getLayoutParams();
            hintTvParams.topMargin = SizeUtils.dp2px(30);
            mHintView.setLayoutParams(hintTvParams);

            ViewGroup.LayoutParams bgViewParams = mBgView.getLayoutParams();
            bgViewParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            mBgView.setLayoutParams(bgViewParams);
            mRootCropRl.setBackgroundColor(getResources().getColor(R.color.yellow_ffb300));
            mSourceBitmap = ImageUtils.getScaledBitmap(mSourceImgPath, new int[]{1280, 720}, true);
            mCropImageview.setImageBitmap(mSourceBitmap);
        } else {
            showAnim(180, 200);
            mSourceImgPath = getIntent().getStringExtra("desFilePath");
            mSourceBitmap = ResultHolder.getImage();
//            mCropImageview.setImageBitmap(mSourceBitmap);
            mCropImageview.setImageBitmap(ImageUtils.clip(mSourceBitmap, 0, 0, mSourceBitmap.getWidth(), mSourceBitmap.getWidth()));
        }
        Timber.e(mSourceBitmap.getHeight() + "   " + mSourceBitmap.getWidth() + "   " + SizeUtils.getScreenHeight());
        mAnimationDrawable = (AnimationDrawable) getResources().getDrawable(R.drawable.face_recog_anim);
        mRecogIv.setBackground(mAnimationDrawable);
        mCropImageview.setOnBitmapSaveCompleteListener(this);
    }

    private void showAnim(long duration, long delayTime) {
        mScreenHeight = SizeUtils.getScreenHeight();
        int offSetY = SizeUtils.dp2px(15);

        mCropCardView.animate().translationY(offSetY)
                .setDuration(duration - 50)
                .setStartDelay(50)
                .start();

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, mScreenHeight);
        valueAnimator.setDuration(duration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float height = (float) animation.getAnimatedValue();
                ViewGroup.LayoutParams params = mBgView.getLayoutParams();
                params.height = (int) height;
                mBgView.setLayoutParams(params);
                float alpha = height / mScreenHeight;
                mRecogRl.setAlpha(alpha);
                mHintView.setAlpha(alpha);
            }
        });

        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mRecogRl.setVisibility(View.VISIBLE);
                mHintView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mTintManager.setStatusBarTintResource(R.color.yellow_ffb300);
                mRootCropRl.setBackgroundColor(getResources().getColor(R.color.yellow_ffb300));
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.setStartDelay(delayTime);
        valueAnimator.start();
    }

    @Override
    public void showLoading(String message) {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(@NonNull String message) {
        ToastUtils.showShort(message);
        stopAnim();
        mCropImageview.setScanning(false);
        setShunNiVisible(View.VISIBLE);
        mRecogTv.setText(R.string.start_recognize);
        mIsLoading = false;
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


    @OnClick({R.id.recog_iv, R.id.recog_start_iv, R.id.ni_img, R.id.shun_img, R.id.back_iv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.recog_start_iv:
                mRecogStartIv.setVisibility(View.GONE);
                mRecogIv.setVisibility(View.VISIBLE);
                mRecogTv.setText(R.string.recognizing);
                if (mAnimationDrawable != null && !mAnimationDrawable.isRunning()) {
                    mAnimationDrawable.start();
                }
                mIsLoading = true;
                mCropImageview.setScanning(true);
                setShunNiVisible(View.INVISIBLE);
                mCropImageview.saveBitmapToFile(new File(getCropDirectoryPath()));
                break;
            case R.id.ni_img:
                mCropImageview.rotateImageView(-1);
                break;
            case R.id.shun_img:
                mCropImageview.rotateImageView(1);
                break;
            case R.id.back_iv:
                if (mIsLoading) {
                    ToastUtils.showShort("正在识别，请稍后...");
                } else {
                    finish();
                }
                break;
        }
    }

    @Override
    public void onBackPressedSupport() {
        if (mIsLoading) {
            ToastUtils.showShort("正在识别，请稍后...");
        } else {
            super.onBackPressedSupport();
        }
    }

    /**
     * 获取拍照存储目录
     *
     * @return 成功则返回获取到的路径，失败则返回null
     */
    public static String getCropDirectoryPath() {
        String path = Configuration.getRootPath() + Constants.CROP_PIC_PATH;
        if (FileUtils.createOrExistsDir(path)) {
            return path;
        } else {
            return null;
        }
    }

    @Override
    public void onBitmapSaveSuccess(File file) {
        Long endTime = System.currentTimeMillis();
        Date date = TimeUtils.millis2Date(endTime - 3L * 86400L * 1000L);
        // Long  startTime = endTime - 30L * 24L* 60L * 60L * 1000L;
        Long startTime = date.getTime();
        mCropImgPath = file.getAbsolutePath();
        File newFile = CompressHelper.getDefault(this).compressToFile(file);
        MultipartBody.Part part = RequestUtils.prepareFilePart("file", newFile.getAbsolutePath());
        EmptyEntity data = new EmptyEntity();
        RequestBody metadata = RequestUtils.createPartFromString(new Gson().toJson(data));
        mPresenter.uploadFile(String.valueOf(newFile.length()), metadata, part, startTime, endTime);
    }

    @Override
    public void onBitmapSaveError(File file) {
        if (mAnimationDrawable != null && mAnimationDrawable.isRunning()) {
            mAnimationDrawable.selectDrawable(0);
            mAnimationDrawable.stop();
        }
        mRecogStartIv.setVisibility(View.VISIBLE);
        mRecogIv.setVisibility(View.GONE);
        mRecogTv.setText(R.string.start_recognize);
        mCropImageview.setScanning(false);
        setShunNiVisible(View.VISIBLE);
        mIsLoading = false;
    }

    @Override
    public void onFaceSuccess(List<FaceNewEntity> userEntity) {
//        if (userEntity != null && !userEntity.isEmpty()) {
//            ResultHolder.disposeFaceEntity();
//            ResultHolder.setRecogData(userEntity);
//            Intent intent = new Intent(LmCropActivity.this, FaceRecognizeActivity.class);
//            intent.putExtra("facePath", mCropImgPath);
//            intent.putExtra("faceSourcePath", mSourceImgPath);
//            intent.putExtra("isFromCapture", mIsFromCapture);
//            intent.putExtra("feature", mFeature);
//            startActivity(intent);
//            overridePendingTransition(0, 0);
//        } else {
        ResultHolder.disposeFaceEntity();
        ResultHolder.setRecogData(userEntity);
        Intent intent = new Intent(LmCropActivity.this, FaceRecognizeActivity.class);
        intent.putExtra("facePath", mCropImgPath);
        intent.putExtra("faceSourcePath", mSourceImgPath);
        intent.putExtra("isFromCapture", mIsFromCapture);
        intent.putExtra("feature", mFeature);
        startActivity(intent);
        overridePendingTransition(0, 0);
//        }
    }

    private void successToStopAnim() {
        if (mIsLoading) {
            mIsLoading = false;
            stopAnim();
            mCropImageview.setScanning(false);
            setShunNiVisible(View.VISIBLE);
            mRecogTv.setText(R.string.start_recognize);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mIsLoading) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    successToStopAnim();
                }
            }, 200);
        }
    }

    private void stopAnim() {
        mIsLoading = false;
        if (mAnimationDrawable != null && mAnimationDrawable.isRunning()) {
            mAnimationDrawable.selectDrawable(0);
            mAnimationDrawable.stop();
        }
        mRecogStartIv.setVisibility(View.VISIBLE);
        mRecogIv.setVisibility(View.GONE);
        mRecogTv.setText(R.string.start_recognize);
    }

    @Override
    public void onFaceFail(String msg) {
        stopAnim();
        ToastUtils.showShort(msg);
        mCropImageview.setScanning(false);
        setShunNiVisible(View.VISIBLE);
        mRecogTv.setText(R.string.start_recognize);
        mIsLoading = false;
    }

    @Override
    public void onGetFeatureSuccess(String feature) {
        mFeature = feature;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mCropImageview) {
            mCropImageview.removeHanlder();
            mCropImageview.setOnBitmapSaveCompleteListener(null);
        }
        if (null != mSourceBitmap && !mSourceBitmap.isRecycled()) {
            mSourceBitmap.recycle();
            mSourceBitmap = null;
        }
        ResultHolder.disposeBitmap();
    }

    private void setShunNiVisible(int visible) {
        mNiImg.setVisibility(visible);
        mShunImg.setVisibility(visible);
        mHintView.setVisibility(visible);
    }
}
