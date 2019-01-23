package cloud.antelope.lingmou.mvp.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.http.imageloader.glide.GlideArms;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.utils.Configuration;
import com.lingdanet.safeguard.common.utils.DeviceUtil;
import com.lingdanet.safeguard.common.utils.EncryptUtils;
import com.lingdanet.safeguard.common.utils.ImageUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.SizeUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;

import org.simple.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.CollectedPictureHolder;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.di.component.DaggerFaceDetailComponent;
import cloud.antelope.lingmou.di.module.FaceDetailModule;
import cloud.antelope.lingmou.mvp.contract.FaceDetailContract;
import cloud.antelope.lingmou.mvp.model.entity.CollectChangeEvent;
import cloud.antelope.lingmou.mvp.model.entity.CollectRequest;
import cloud.antelope.lingmou.mvp.presenter.FaceDetailPresenter;
import cloud.antelope.lingmou.mvp.ui.widget.SelectDialog;
import uk.co.senab.photoview.PhotoView;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class FaceDetailActivity extends BaseActivity<FaceDetailPresenter> implements FaceDetailContract.View {
    @BindView(R.id.face_photoview)
    PhotoView mFacePhotoview;
    @BindView(R.id.progress_rl)
    LinearLayout mProgressRl;
    @BindView(R.id.back_iv)
    ImageView mBackIv;
    @BindView(R.id.more_iv)
    ImageView mMoreIv;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    private String mScenImg;
    //    private String mFeatureImg;
    private String mPoint;
    private Paint mPaint;

    private boolean mIsVideoFun;
    private String mCameraId;
    private String mCameraName;
    private String mCaptureTime;
    private boolean mFromBody;

    private List<String> mChoicesList;

    private String mBodyUrl;
    private String mFaceUrl;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerFaceDetailComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .faceDetailModule(new FaceDetailModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_face_detail; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mCaptureTime = getIntent().getStringExtra("captureTime");
        mCameraId = getIntent().getStringExtra("cameraId");
        mCameraName = getIntent().getStringExtra("cameraName");
        mFromBody = getIntent().getBooleanExtra("isBody", false);
        mBodyUrl = getIntent().getStringExtra("bodyPathUrl");
        mFaceUrl = getIntent().getStringExtra("facePathUrl");
        boolean isControlImage = getIntent().getBooleanExtra("isControlImage", false);
        boolean uploadPic = getIntent().getBooleanExtra("uploadPic", false);
        if(isControlImage){
            tvTitle.setText("布控图片");
        }
        if(uploadPic){
            tvTitle.setText("上传图片");
        }
        mIsVideoFun = getIntent().getBooleanExtra("isVideoFun", false);
        int visibleState = mIsVideoFun ? View.VISIBLE : View.GONE;
        mMoreIv.setVisibility(visibleState);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(SizeUtils.dp2px(2));
        String faceFeilePath = getIntent().getStringExtra("faceFileUrl");
        mChoicesList = new ArrayList<>();
        mChoicesList.add(getString(R.string.event_video));
        mChoicesList.add(getString(R.string.download_picture));
//        mChoicesList.add(getString(R.string.control_picture));
        if (!TextUtils.isEmpty(faceFeilePath)) {
            mProgressRl.setVisibility(View.GONE);
            GlideArms.with(this).load(new File(faceFeilePath)).fitCenter().into(mFacePhotoview);
        } else {
            mScenImg = getIntent().getStringExtra("faceUrl");
            mPoint = getIntent().getStringExtra("point");
            //            mFeatureImg = getIntent().getStringExtra("faceFeatureUrl");
            //        Glide.with(this).load(mFeatureImg).into(mFacePhotoview);
            if (TextUtils.isEmpty(mPoint)) {
                GlideArms.with(this).asBitmap().load(mScenImg).into(new SimpleTarget<Bitmap>() {

                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> glideAnimation) {
                        mProgressRl.setVisibility(View.GONE);
                        mFacePhotoview.setImageBitmap(resource);
                    }
                });
            } else {
                //获取坐标点
                final String[] split = mPoint.split(",");
                final int[] coorFaces = {Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3])};
                GlideArms.with(this).asBitmap().load(mScenImg).dontAnimate().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> glideAnimation) {
                        if (null != resource) {
                            //对bitmap进行操作
                            final Bitmap bitmap = Bitmap.createBitmap(resource.getWidth(), resource.getHeight(), Bitmap.Config.RGB_565);
                            bitmap.setDensity(0);
                            Canvas canvas = new Canvas(bitmap);
                            canvas.drawBitmap(resource, 0, 0, null);
                            canvas.drawRect(coorFaces[0], coorFaces[1], coorFaces[0] + coorFaces[2], coorFaces[1] + coorFaces[3], mPaint);
                            runOnUiThread(() -> {
                                mProgressRl.setVisibility(View.GONE);
                                mFacePhotoview.setImageBitmap(bitmap);
                            });
                        }
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        mProgressRl.setVisibility(View.GONE);
                        ToastUtils.showShort(R.string.fail_load_picture);
                        FaceDetailActivity.this.finish();
                    }
                });
            }
        }
    }

    @OnClick({R.id.back_iv, R.id.more_iv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                finish();
                overridePendingTransition(0, 0);
                break;
            case R.id.more_iv:
                //进入
                showMoreDialog((parent, view1, position, id) -> doMoreThing(position), mChoicesList);

                break;
        }
    }

    //根据position进行操作
    private void doMoreThing(int position) {
        switch (position) {
            case 0:
                //事件视频
                boolean hasVideoLive = SPUtils.getInstance().getBoolean(Constants.PERMISSION_HAS_VIDEO_LIVE, false);
                if (hasVideoLive) {
                    //进入事件视频
                    Intent intent = new Intent(FaceDetailActivity.this, PlayerNewActivity.class);
                    intent.putExtra("isEvent", true);
                    intent.putExtra("cameraName", mCameraName);
                    intent.putExtra("cameraSn", mCameraId);
                    intent.putExtra("cameraId", Long.parseLong(mCameraId));
                    intent.putExtra("captureTime", Long.parseLong(mCaptureTime));
                    startActivity(intent);
                } else {
                    ToastUtils.showShort(R.string.hint_no_permission);
                }
                break;
            case 1:
                //下载图片
                GlideArms.with(this)
                        .load(mScenImg)
                        .into(new SimpleTarget<Drawable>() {
                            @Override
                            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                String cachePath = Configuration.getDownloadDirectoryPath()
                                        + EncryptUtils.encryptMD5ToString(mScenImg)
                                        + Constants.DEFAULT_IMAGE_SUFFIX;
                                ImageUtils.save(ImageUtils.drawable2Bitmap(resource), cachePath, Bitmap.CompressFormat.JPEG);
                                DeviceUtil.galleryAddMedia(cachePath);
                                ToastUtils.showShort(getString(R.string.save_image_success));
                            }
                        });
                break;
            case 2:
                //收藏图片
                CollectRequest request = new CollectRequest();
                request.setFaceImgUrl(mFromBody ? mBodyUrl : mFaceUrl);
                request.setDeviceId(Long.valueOf(mCameraId));
                request.setDeviceName(mCameraName);
                request.setImageUrl(mScenImg);
                request.setFavoritesType(mFromBody ? 3 : 2);
                request.setFaceRect(mPoint);
                mPresenter.collect(request);
                break;
        }

    }

    private SelectDialog showMoreDialog(SelectDialog.SelectDialogListener listener, List<String> names) {
        SelectDialog dialog = new SelectDialog(this, R.style.transparentFrameWindowStyle,
                listener, names);
        if (!this.isFinishing()) {
            dialog.show();
        }
        return dialog;
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
    public void onBackPressedSupport() {
        super.onBackPressedSupport();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onCollectSuccess() {
        showMessage("收藏成功！");
        List<String> urls = CollectedPictureHolder.urls;
        String url=mFromBody?mBodyUrl:mFaceUrl;
        if(!urls.contains(url)){
            urls.add(url);
        }
    }
}
