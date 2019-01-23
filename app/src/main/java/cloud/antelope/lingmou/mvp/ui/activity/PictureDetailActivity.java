package cloud.antelope.lingmou.mvp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.http.imageloader.glide.GlideArms;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.modle.LoadingDialog;
import com.lingdanet.safeguard.common.utils.Configuration;
import com.lingdanet.safeguard.common.utils.DeviceUtil;
import com.lingdanet.safeguard.common.utils.EncryptUtils;
import com.lingdanet.safeguard.common.utils.ImageUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.TimeUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.umeng.analytics.MobclickAgent;

import org.litepal.crud.DataSupport;
import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.CollectedPictureHolder;
import cloud.antelope.lingmou.app.utils.FormatUtils;
import cloud.antelope.lingmou.app.utils.LogUploadUtil;
import cloud.antelope.lingmou.app.utils.ResultHolder;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.di.component.DaggerPictureDetailComponent;
import cloud.antelope.lingmou.di.module.PictureDetailModule;
import cloud.antelope.lingmou.mvp.contract.PictureDepotDetailContract;
import cloud.antelope.lingmou.mvp.model.entity.CollectCameraEntity;
import cloud.antelope.lingmou.mvp.model.entity.CollectChangeEvent;
import cloud.antelope.lingmou.mvp.model.entity.CollectRequest;
import cloud.antelope.lingmou.mvp.model.entity.CollectionListBean;
import cloud.antelope.lingmou.mvp.model.entity.DetailCommonEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceNewEntity;
import cloud.antelope.lingmou.mvp.model.entity.LogUploadRequest;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;
import cloud.antelope.lingmou.mvp.model.entity.PictureCollectEvent;
import cloud.antelope.lingmou.mvp.model.entity.PictureDetailMenuEntity;
import cloud.antelope.lingmou.mvp.presenter.PictureDepotDetailPresenter;
import cloud.antelope.lingmou.mvp.ui.adapter.PicturePagerAdapter;
import cloud.antelope.lingmou.mvp.ui.fragment.CollectionListFragment;
import cloud.antelope.lingmou.mvp.ui.widget.HackyViewPager;
import cloud.antelope.lingmou.mvp.ui.widget.SelectDialog;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class PictureDetailActivity extends BaseActivity<PictureDepotDetailPresenter>
        implements PictureDepotDetailContract.View {

    @BindView(R.id.hack_viewpager)
    HackyViewPager mHackViewpager;
    @BindView(R.id.title_tv)
    TextView mTitleTv;
    @BindView(R.id.content_tv)
    TextView mContentTv;
    @BindView(R.id.time_tv)
    TextView mTimeTv;
    @BindView(R.id.bottom_ll)
    RelativeLayout mBottomLl;
    @BindView(R.id.back_ib)
    ImageButton mBackIb;
    @BindView(R.id.title_rl)
    RelativeLayout mTitleRl;
    @BindView(R.id.more_iv)
    ImageView mMoreIv;
    @BindView(R.id.detail_root_view)
    RelativeLayout mDetailRootView;
    @BindView(R.id.iv_collect)
    ImageView ivCollect;
    @BindView(R.id.iv_download)
    ImageView ivDownload;

    @Inject
    PicturePagerAdapter mFaceDepotAdapter;
    LoadingDialog mLoadingDialog;

    private List<DetailCommonEntity> mDetailEntities;
    private int mPosition;
    private List<PictureDetailMenuEntity> mChoicesList;

    private boolean mIsFromRecorgnize;
    private boolean mIsFromBody;
    private boolean mIsFromCar;
    private boolean isFromDepotDetail;
    private boolean isFromDepot;
    private DetailCommonEntity mCurrentDetailEntity;
    private float[] mFeature;
    private boolean fromCollect;
    private List<CollectCameraEntity> collectCameraList = new ArrayList<>();

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        DaggerPictureDetailComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .pictureDetailModule(new PictureDetailModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_picture_detail; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mTintManager.setStatusBarTintResource(R.color.white);
        mDetailEntities = getIntent().getParcelableArrayListExtra("bean");
        mPosition = getIntent().getIntExtra("position", 0);
        mIsFromRecorgnize = getIntent().getBooleanExtra("isFromRecorg", false);
        mIsFromBody = getIntent().getBooleanExtra("isFromBody", false);
        fromCollect = getIntent().getBooleanExtra("fromCollect", false);
        mIsFromCar = getIntent().getBooleanExtra("fromCar", false);
        isFromDepotDetail = getIntent().getBooleanExtra("isFromDepotDetail", false);
        isFromDepot = getIntent().getBooleanExtra("isFromDepot", false);
        mLoadingDialog = new LoadingDialog(this);
        mChoicesList = new ArrayList<>();
        mHackViewpager.setAdapter(mFaceDepotAdapter);
        mFaceDepotAdapter.setNewData(mDetailEntities);
        if (mDetailEntities != null && !mDetailEntities.isEmpty()) {
            mHackViewpager.setCurrentItem(mPosition);
            mCurrentDetailEntity = mDetailEntities.get(mPosition);
            mContentTv.setText(mCurrentDetailEntity.deviceName);
            long createTime = mCurrentDetailEntity.endTime;
            String time = TimeUtils.millis2String(createTime, "yyyy-MM-dd HH:mm:ss");
            mTimeTv.setText(time);
            if (isFromDepotDetail) {
                mTitleTv.setText("抓拍图片");
            } else {
                mTitleTv.setText(String.format(getString(R.string.page_content_title), mPosition + 1, mDetailEntities.size()));
            }
        }
        if(isFromDepot){
            ivCollect.setVisibility(View.GONE);
        }
        if (mIsFromCar) {
            ivCollect.setVisibility(View.GONE);
            mChoicesList.add(new PictureDetailMenuEntity(getString(R.string.device_detail_info), R.id.menu_device_detail_info));
            mChoicesList.add(new PictureDetailMenuEntity(getString(R.string.show_history_video), R.id.menu_show_history_video));
            mMoreIv.setVisibility(View.VISIBLE);
            LogUploadUtil.uploadLog(new LogUploadRequest(105600, 105601, String.format("查看点位【%s】%s的车辆抓拍照片", mCurrentDetailEntity.cameraName, TimeUtils.millis2String(mCurrentDetailEntity.endTime))));
        } else if (fromCollect) {
            mCurrentDetailEntity.collected = true;
            ivCollect.setImageResource(R.drawable.pic_collected);
            if (mCurrentDetailEntity.favoritesType == 2) {//人脸
                mChoicesList.add(new PictureDetailMenuEntity(getString(R.string.add_control), R.id.menu_add_control));
            }
            mMoreIv.setVisibility(mCurrentDetailEntity.favoritesType == CollectionListFragment.FACE ? View.VISIBLE : View.GONE);
        } else {
            mCurrentDetailEntity.collected = CollectedPictureHolder.urls.contains(mCurrentDetailEntity.faceImg);
            ivCollect.setImageResource(mCurrentDetailEntity.collected ? R.drawable.pic_collected : R.drawable.pic_collect);
            mChoicesList.add(new PictureDetailMenuEntity(getString(R.string.search_by_picture), R.id.menu_search_by_picture));
            mChoicesList.add(new PictureDetailMenuEntity(getString(R.string.show_history_video), R.id.menu_show_history_video));
            if (mCurrentDetailEntity.favoritesType == 2) {
                mChoicesList.add(new PictureDetailMenuEntity(getString(R.string.add_control), R.id.menu_add_control));
            }
            mChoicesList.add(new PictureDetailMenuEntity(getString(R.string.device_detail_info), R.id.menu_device_detail_info));
        }
        if (mIsFromRecorgnize) {
            mMoreIv.setVisibility(View.GONE);
            ivCollect.setVisibility(View.GONE);
            ivDownload.setVisibility(View.GONE);
        }
        initListener();

//        CollectionListRequest request = new CollectionListRequest();
//        request.setPageNo(1);
//        request.setPageSize(3000);
//        mPresenter.getCollections(request);
    }

    private void initListener() {
        mFaceDepotAdapter.setOnTapClicklistener(new PicturePagerAdapter.OnTapClicklistener() {
            @Override
            public void onTapClick() {
                //表示单击了
                if (mTitleRl.getVisibility() == View.VISIBLE) {
                    mTitleRl.setVisibility(View.GONE);
                    mBottomLl.setVisibility(View.GONE);
                    mTintManager.setStatusBarTintResource(R.color.black);
                    mDetailRootView.setBackgroundColor(getResources().getColor(R.color.black));
                } else {
                    mTitleRl.setVisibility(View.VISIBLE);
                    mBottomLl.setVisibility(View.VISIBLE);
                    mTintManager.setStatusBarTintResource(R.color.white);
                    mDetailRootView.setBackgroundColor(getResources().getColor(R.color.white));
                }
            }
        });

        mHackViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mPosition = position;
                mCurrentDetailEntity = mDetailEntities.get(position);
                mContentTv.setText(mCurrentDetailEntity.deviceName);
                long createTime = mCurrentDetailEntity.endTime;
                String time = TimeUtils.millis2String(createTime, "yyyy-MM-dd HH:mm:ss");
                mTimeTv.setText(time);
                if (!isFromDepotDetail)
                    mTitleTv.setText(String.format(getString(R.string.page_content_title), position + 1, mDetailEntities.size()));
                mCurrentDetailEntity.collected = CollectedPictureHolder.urls.contains(mCurrentDetailEntity.faceImg);
                ivCollect.setImageResource(mCurrentDetailEntity.collected ? R.drawable.pic_collected : R.drawable.pic_collect);
                if (fromCollect)
                    mMoreIv.setVisibility(mCurrentDetailEntity.favoritesType == CollectionListFragment.FACE ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mMoreIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreDialog((parent, view1, position, id) -> {
                    doMoreThing(position);
                }, mChoicesList);
            }
        });
    }

    //根据position进行操作
    private void doMoreThing(int position) {
        switch (mChoicesList.get(position).id) {
            case R.id.menu_device_detail_info://设备详情
                Intent videoIntent = new Intent(PictureDetailActivity.this, VideoDetailActivity.class);
                videoIntent.putExtra("cid", mCurrentDetailEntity.cameraId);
                startActivity(videoIntent);
                break;
            case R.id.menu_show_history_video:
                boolean hasVideoLive = SPUtils.getInstance().getBoolean(Constants.PERMISSION_HAS_VIDEO_LIVE, false);
                if (hasVideoLive) {
                    //事件视频
                    String eventStr = "";
                    if (mIsFromBody) {
                        eventStr = "body_eventVideo";
                    } else if (mIsFromCar) {
                        eventStr = "car_eventVideo";
                    } else if (fromCollect) {
                        eventStr = "collect_eventVideo";
                    } else {
                        eventStr = "face_eventVideo";
                    }

                    MobclickAgent.onEvent(PictureDetailActivity.this, eventStr);
                    Intent intent = new Intent(PictureDetailActivity.this, PlayerNewActivity.class);
                    intent.putExtra("isEvent", true);
                    intent.putExtra("cameraName", mCurrentDetailEntity.cameraName);
                    intent.putExtra("cameraSn", mCurrentDetailEntity.cameraId);
                    intent.putExtra("cameraId", Long.parseLong(mCurrentDetailEntity.cameraId));
                    intent.putExtra("captureTime", mCurrentDetailEntity.captureTime);
                    startActivity(intent);
                } else {
                    ToastUtils.showShort(R.string.hint_no_permission);
                }
                break;
            case R.id.menu_add_control://临控
                Intent intent2 = new Intent(PictureDetailActivity.this, NewDeployMissionActivity.class);
                intent2.putExtra("facePath", mCurrentDetailEntity.faceImg);
                startActivity(intent2);
                break;
            case R.id.menu_search_by_picture:
                //以图搜图
                boolean hasPermission = SPUtils.getInstance().getBoolean(Constants.PERMISSION_HAS_HYJJ, false);
                if (hasPermission) {
                    //进入以图搜图，看是人脸还是人体
                    Long endTime = System.currentTimeMillis();
                    Date date = TimeUtils.millis2Date(endTime - 3 * 86400 * 1000L);
                    // Long  startTime = endTime - 30L * 24L* 60L * 60L * 1000L;
                    Long startTime = date.getTime();
                    String vid = mCurrentDetailEntity.id;
                    if (!mIsFromBody) {
                        //进行人脸搜图
                        mPresenter.getFaceList(vid, startTime, endTime, 85);
                        LogUploadUtil.uploadLog(new LogUploadRequest(105700, 105701, String.format("以图搜图【图库】，图片URL:%s", mCurrentDetailEntity.faceImg)));
                    } else {
                        //进行人体搜图
                        mPresenter.getBodyList(vid, startTime, endTime, 85);
                        LogUploadUtil.uploadLog(new LogUploadRequest(105700, 105702, String.format("以图搜图【图库】，图片URL:%s", mCurrentDetailEntity.faceImg)));
                    }

                } else {
                    ToastUtils.showShort(R.string.hint_no_permission);
                }
                break;
        }
    }

    private void collect() {
        CollectRequest request = new CollectRequest();
        request.setFaceImgUrl(mCurrentDetailEntity.faceImg);
        request.setDeviceId(Long.valueOf(mCurrentDetailEntity.cameraId));
        request.setDeviceName(mCurrentDetailEntity.deviceName);
        request.setImageUrl(mCurrentDetailEntity.srcImg);
        request.setFavoritesType(mIsFromBody ? 3 : 2);
        request.setFaceRect(mCurrentDetailEntity.point);
        mPresenter.collect(request, mPosition);
        String fav = mIsFromBody ? "body_favoritePicture" : "face_favoritePicture";
        MobclickAgent.onEvent(PictureDetailActivity.this, fav);
    }

    private void cancelCollect() {
        CollectRequest request = new CollectRequest();
        request.setId(Long.parseLong(mDetailEntities.get(mPosition).id));
        mPresenter.collect(request, mPosition);
    }

    private void download() {
        String downStr = "";
        if (mIsFromBody) {
            downStr = "body_downPicture";
        } else if (mIsFromCar) {
            downStr = "car_downPicture";
        } else if (fromCollect) {
            downStr = "collect_downPicture";
        } else {
            downStr = "face_downPicture";
        }
        MobclickAgent.onEvent(PictureDetailActivity.this, downStr);
        GlideArms.with(this)
                .load(mCurrentDetailEntity.srcImg)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        String cachePath = Configuration.getDownloadDirectoryPath()
                                + EncryptUtils.encryptMD5ToString(mCurrentDetailEntity.faceImg)
                                + Constants.DEFAULT_IMAGE_SUFFIX;
                        ImageUtils.save(ImageUtils.drawable2Bitmap(resource), cachePath, Bitmap.CompressFormat.JPEG);
                        DeviceUtil.galleryAddMedia(cachePath);
                        ToastUtils.showShort(getString(R.string.save_image_success));
                    }
                });
    }

    private SelectDialog showMoreDialog(SelectDialog.SelectDialogListener listener, List<PictureDetailMenuEntity> entities) {
        ArrayList<String> names = new ArrayList<>();
        for (PictureDetailMenuEntity entity : entities) {
            names.add(entity.name);
        }
        SelectDialog dialog = new SelectDialog(this, R.style.transparentFrameWindowStyle,
                listener, names);
        if (!this.isFinishing()) {
            dialog.show();
        }
        return dialog;
    }

    @Override
    public void showLoading(String message) {
        mLoadingDialog.show();
    }

    @Override
    public void hideLoading() {
        mLoadingDialog.dismiss();
    }

    @Override
    public void showMessage(@NonNull String message) {
        ToastUtils.showShort(message);
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
    public Activity getActivity() {
        return this;
    }

    @Override
    public void getCollectionsSuccess(List<CollectionListBean> list) {
        if (null != list) {
            for (DetailCommonEntity entity : mDetailEntities) {
                boolean collected = false;
                for (CollectionListBean collectionListBean : list) {
                    boolean same = TextUtils.equals(collectionListBean.getFaceImgUrl(), entity.faceImg);
                    if (same) {
                        Timber.e(entity.srcImg);
                    }
                    collected |= same;
                }
                entity.collected = collected;
            }
            ivCollect.setImageResource(mDetailEntities.get(mPosition).collected ? R.drawable.pic_collected : R.drawable.pic_collect);
        }
    }


    @Override
    public void getFaceListSuccess(List<FaceNewEntity> userEntity) {
        if (userEntity != null && !userEntity.isEmpty()) {
            ResultHolder.disposeFaceEntity();
            ResultHolder.setRecogData(userEntity);
            Intent intent = new Intent(PictureDetailActivity.this, FaceRecognizeActivity.class);
            intent.putExtra("facePath", mCurrentDetailEntity.faceImg);
            intent.putExtra("faceSourcePath", mCurrentDetailEntity.srcImg);
            intent.putExtra("faceBodyRect", mCurrentDetailEntity.point);
            intent.putExtra("isFromCapture", false);
            intent.putExtra("featureList", mFeature);
            intent.putExtra("fromFaceBody", true);
            startActivity(intent);
            overridePendingTransition(0, 0);
        } else {
            ToastUtils.showShort(R.string.error_face_recog_null);
        }
    }

    @Override
    public void getFaceFail() {
        ToastUtils.showShort(R.string.error_face_recog_null);
    }

    @Override
    public void getFeatureSuccess(float[] feature) {
        mFeature = feature;
    }

    @Override
    public void getBodyListSuccess(List<FaceNewEntity> userEntity) {
        if (userEntity != null && !userEntity.isEmpty()) {
            ResultHolder.disposeFaceEntity();
            ResultHolder.setRecogData(userEntity);
            Intent intent = new Intent(PictureDetailActivity.this, FaceRecognizeActivity.class);
            intent.putExtra("facePath", mCurrentDetailEntity.faceImg);
            intent.putExtra("faceSourcePath", mCurrentDetailEntity.srcImg);
            intent.putExtra("isFromCapture", false);
            intent.putExtra("featureList", mFeature);
            intent.putExtra("fromFaceBody", true);
            intent.putExtra("isBody", true);
            startActivity(intent);
            overridePendingTransition(0, 0);
        } else {
            ToastUtils.showShort(R.string.error_face_recog_null);
        }
    }

    @Override
    public void onCollectSuccess(int position, String colletcId, boolean isCancel) {
        List<String> ids = CollectedPictureHolder.urls;
        if (isCancel) {
            showMessage("取消收藏！");
            ivCollect.setImageResource(R.drawable.pic_collect);
            mDetailEntities.get(position).collected = false;

            if (ids.contains(mDetailEntities.get(position).faceImg)) {
                ids.remove(mDetailEntities.get(position).faceImg);
            }
        } else {
            mDetailEntities.get(position).collected = true;
            mDetailEntities.get(position).id = colletcId;
            ivCollect.setImageResource(R.drawable.pic_collected);
            showMessage("收藏成功！");
            if (!ids.contains(mDetailEntities.get(position).faceImg)) {
                ids.add(mDetailEntities.get(position).faceImg);
            }

        }
        if (fromCollect) {
            EventBus.getDefault().post(new CollectChangeEvent(mCurrentDetailEntity.favoritesType));
        } else {
            EventBus.getDefault().post(new PictureCollectEvent(position, !isCancel, colletcId == null ? "" : colletcId));
        }

    }

    @OnClick({R.id.back_ib, R.id.iv_download, R.id.iv_collect})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_ib:
                finish();
                break;
            case R.id.iv_download:
                download();
                LogUploadUtil.uploadLog(new LogUploadRequest(107400, 107401, String.format("下载点位【%s】 %s的图片", mCurrentDetailEntity.cameraName, TimeUtils.millis2String(mCurrentDetailEntity.endTime))));
                break;
            case R.id.iv_collect:
                if (mCurrentDetailEntity.collected) {
                    cancelCollect();
                } else {
                    collect();
                }
                break;
        }
    }
}
