package cloud.antelope.lingmou.mvp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.http.imageloader.glide.GlideArms;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.utils.SizeUtils;
import com.lingdanet.safeguard.common.utils.TimeUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.lingdanet.safeguard.common.utils.Utils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import cloud.antelope.lingmou.di.component.DaggerFaceDepotDetailComponent;
import cloud.antelope.lingmou.di.module.FaceDepotDetailModule;
import cloud.antelope.lingmou.mvp.contract.FaceDepotDetailContract;
import cloud.antelope.lingmou.mvp.model.entity.BodyDepotDetailEntity;
import cloud.antelope.lingmou.mvp.model.entity.CollectionListBean;
import cloud.antelope.lingmou.mvp.model.entity.CollectionListRequest;
import cloud.antelope.lingmou.mvp.model.entity.DetailCommonEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceDepotDetailEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceNewEntity;
import cloud.antelope.lingmou.mvp.model.entity.LogUploadRequest;
import cloud.antelope.lingmou.mvp.presenter.FaceDepotDetailPresenter;
import cloud.antelope.lingmou.mvp.ui.adapter.FaceDepotDetailAdapter;
import cloud.antelope.lingmou.mvp.ui.fragment.CollectionListFragment;
import cloud.antelope.lingmou.mvp.ui.widget.GridSpacingItemDecoration;
import cloud.antelope.lingmou.mvp.ui.widget.SpacesItemDecoration;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class FaceDepotDetailActivity extends BaseActivity<FaceDepotDetailPresenter> implements FaceDepotDetailContract.View {
    private static final int TYPE_FILTER_FACE = 0x01;
    @BindView(R.id.head_left_iv)
    ImageButton headLeftIv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.track_tv)
    ImageView trackTv;
    @BindView(R.id.filter_iv)
    ImageView filterIv;
    @BindView(R.id.iv_pic)
    ImageView ivPic;
    @BindView(R.id.cv)
    CardView cv;
    @BindView(R.id.tv_1)
    TextView tv1;
    @BindView(R.id.tv_capture_address)
    TextView tvCaptureAddress;
    @BindView(R.id.tv_2)
    TextView tv2;
    @BindView(R.id.tv_capture_time)
    TextView tvCaptureTime;
    @BindView(R.id.tv_device_detail)
    TextView tvDeviceDetail;
    @BindView(R.id.tv_recent)
    TextView tvRecent;
    @BindView(R.id.tv_similarity)
    TextView tvSimilarity;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.rl_overly)
    RelativeLayout rlOverly;
    @BindView(R.id.ll_bottom)
    LinearLayout llBottom;
    @BindView(R.id.tv_size)
    TextView tvSize;
    @BindView(R.id.tv_choice)
    TextView tvChoice;
    @BindView(R.id.rl_empty)
    RelativeLayout rlEmpty;
    @BindView(R.id.tv_empty)
    TextView tvEmpty;

    @Inject
    FaceDepotDetailAdapter mAdapter;
    private float[] mFeature;
    private FaceDepotDetailEntity faceEntity;
    private BodyDepotDetailEntity bodyEntity;
    private Drawable drawableSelected;
    private Drawable drawableUnselected;
    private boolean isChoosing;
    private int choosedSize;
    private long mStartTime = -1;
    private long mEndTime = -1;
    private int score;
    private boolean mIsFromBody;
    private int rangeTime = 3;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerFaceDepotDetailComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .faceDepotDetailModule(new FaceDepotDetailModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_face_depot_detail; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        faceEntity = getIntent().getParcelableExtra("face_Entity");
        bodyEntity = getIntent().getParcelableExtra("body_Entity");
        mIsFromBody = getIntent().getBooleanExtra("isFromBody", false);
        score = 85;
        if (mIsFromBody) {
            GlideArms.with(this).load(bodyEntity.bodyPath).into(ivPic);
            tvCaptureAddress.setText(bodyEntity.cameraName);
            tvCaptureTime.setText(TimeUtils.date2String(new Date(Long.valueOf(bodyEntity.captureTime))));
            LogUploadUtil.uploadLog(new LogUploadRequest(104200, 104205, String.format("查看点位【%s】 %s的人体抓拍照片", bodyEntity.cameraName, TimeUtils.millis2String(Long.valueOf(bodyEntity.captureTime)))));
        } else {
            GlideArms.with(this).load(faceEntity.facePath).centerCrop().into(ivPic);
            tvCaptureAddress.setText(faceEntity.cameraName);
            tvCaptureTime.setText(TimeUtils.date2String(new Date(Long.valueOf(faceEntity.captureTime))));
            LogUploadUtil.uploadLog(new LogUploadRequest(104100, 104105, String.format("查看点位【%s】 %s的人脸抓拍照片", faceEntity.cameraName, TimeUtils.millis2String(Long.valueOf(faceEntity.captureTime)))));
        }

        drawableSelected = getResources().getDrawable(R.drawable.shape_drawable_bottom_similar_picture);
        drawableSelected.setBounds(0, 0, drawableSelected.getMinimumWidth(), drawableSelected.getMinimumHeight());
        drawableUnselected = getResources().getDrawable(R.drawable.shape_transparent);
        drawableUnselected.setBounds(0, 0, drawableUnselected.getMinimumWidth(), drawableUnselected.getMinimumHeight());

        getRecent();
        rv.setLayoutManager(new GridLayoutManager(FaceDepotDetailActivity.this, 2));
        rv.setAdapter(mAdapter);
       /* rv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int position = parent.getChildLayoutPosition(view);
                outRect.top = getResources().getDimensionPixelSize(R.dimen.dp8);
                outRect.bottom = getResources().getDimensionPixelSize(R.dimen.dp8);
                outRect.left = position % 2 == 0 ? getResources().getDimensionPixelSize(R.dimen.dp16) : getResources().getDimensionPixelSize(R.dimen.dp8);
                outRect.right = position % 2 == 0 ? getResources().getDimensionPixelSize(R.dimen.dp8) : getResources().getDimensionPixelSize(R.dimen.dp16);
            }
        });*/
       rv.addItemDecoration(new SpacesItemDecoration(2, SizeUtils.dp2px(8), false));
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (isChoosing) {
                if (mAdapter.getData().get(position).mIsChecked) {
                    choosedSize--;
                } else {
                    choosedSize++;
                }
                mAdapter.getData().get(position).mIsChecked = !mAdapter.getData().get(position).mIsChecked;
                mAdapter.notifyItemChanged(position);
                notifySize();
            } else {
                Intent detailIntent = new Intent();
                detailIntent.putExtra("position", position);
                detailIntent.setClass(FaceDepotDetailActivity.this, PictureDetailActivity.class);
                detailIntent.putParcelableArrayListExtra("bean", getDetailCommonList(mAdapter.getData()));
                detailIntent.putExtra("isFromBody", mIsFromBody);
                detailIntent.putExtra("isFromDepot", true);
                startActivity(detailIntent);


               /* FaceNewEntity entity = mAdapter.getData().get(position);
                Intent intent = new Intent();
                intent.setClass(this, FaceDetailActivity.class);
                String sceneImg = entity.scenePath ;
                intent.putExtra("faceUrl", sceneImg);
                String point = mIsFromBody ? entity.bodyRect : entity.faceRect;
                intent.putExtra("point", point);
                intent.putExtra("isVideoFun", true);
                intent.putExtra("captureTime", entity.captureTime);
                intent.putExtra("cameraId", entity.cameraId);
                intent.putExtra("cameraName", entity.cameraName);
                if (mIsFromBody) {
                    intent.putExtra("bodyPathUrl", entity.bodyPath);
                } else {
                    intent.putExtra("facePathUrl", entity.facePath);
                }
                intent.putExtra("isBody", mIsFromBody);
                startActivity(intent);*/
            }
        });
        mPresenter.getCollectionList(new CollectionListRequest(null, 1, 3000));
    }

    private ArrayList<DetailCommonEntity> getDetailCommonList(List<FaceNewEntity> data) {
        ArrayList<DetailCommonEntity> list = new ArrayList<>();
        for(FaceNewEntity entity:data){
            DetailCommonEntity commonEntity = new DetailCommonEntity();
            if (mIsFromBody) {
                commonEntity.deviceName = entity.cameraName;
                commonEntity.endTime = Long.parseLong(entity.captureTime);
                commonEntity.point = entity.bodyRect;
                commonEntity.srcImg = entity.scenePath;
                commonEntity.id = entity.id;
                commonEntity.favoritesType = CollectionListFragment.BODY;
                commonEntity.cameraId = entity.cameraId;
                commonEntity.cameraName = entity.cameraName;
                commonEntity.faceImg = entity.bodyPath;
                commonEntity.captureTime = Long.parseLong(entity.captureTime);
            } else {
                commonEntity.deviceName = entity.cameraName;
                commonEntity.endTime = Long.parseLong(entity.captureTime);
                commonEntity.point = entity.faceRect;
                commonEntity.srcImg = entity.scenePath;
                commonEntity.id = entity.id;
                commonEntity.favoritesType = CollectionListFragment.FACE;
                commonEntity.cameraId = entity.cameraId;
                commonEntity.cameraName = entity.cameraName;
                commonEntity.faceImg = entity.facePath;
                commonEntity.captureTime = Long.parseLong(entity.captureTime);
            }
            list.add(commonEntity);
        }
        return list;
    }

    private void notifySize() {
        tvSize.setText(String.format("已选择%d张", choosedSize));
        tvChoice.setText(choosedSize == mAdapter.getData().size() ? "取消选择" : "全选");
    }

    private void getRecent() {
        tvRecent.setSelected(true);
        tvSimilarity.setSelected(false);
        tvRecent.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        tvSimilarity.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
        tvRecent.setCompoundDrawables(null, null, null, drawableSelected);
        tvSimilarity.setCompoundDrawables(null, null, null, drawableUnselected);

        if (mAdapter.getData().size() > 0) {
            //按照时间排序
            List<FaceNewEntity> data = mAdapter.getData();
            Collections.sort(data, new Comparator<FaceNewEntity>() {
                @Override
                public int compare(FaceNewEntity o1, FaceNewEntity o2) {
                    return Long.parseLong(o1.captureTime) <= Long.parseLong(o2.captureTime) ? 1 : -1;
                }
            });
            mAdapter.setNewData(data);
            rv.smoothScrollToPosition(0);
        } else {
            mEndTime = System.currentTimeMillis();
            Date date = TimeUtils.millis2Date(mEndTime - 3 * 24 * 60 * 60 * 1000L);
            // Long  startTime = endTime - 30L * 24L* 60L * 60L * 1000L;
            mStartTime = date.getTime();
            if (mIsFromBody) {
                mPresenter.getBodyList(bodyEntity.id, mStartTime, mEndTime, score);
            } else {
                //进行人脸搜图
                mPresenter.getFaceList(faceEntity.id, mStartTime, mEndTime, score);
            }
        }
    }

    private void getSimilarity() {
        tvSimilarity.setSelected(true);
        tvRecent.setSelected(false);
        tvSimilarity.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        tvRecent.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
        tvSimilarity.setCompoundDrawables(null, null, null, drawableSelected);
        tvRecent.setCompoundDrawables(null, null, null, drawableUnselected);

        //按照相似度排序
        List<FaceNewEntity> data = mAdapter.getData();
        Collections.sort(data, new Comparator<FaceNewEntity>() {
            @Override
            public int compare(FaceNewEntity o1, FaceNewEntity o2) {
                return o1.score <= o2.score ? 1 : -1;
            }
        });
        mAdapter.setNewData(data);
        rv.smoothScrollToPosition(0);
    }

    @OnClick({R.id.head_left_iv, R.id.tv_recent, R.id.tv_similarity, R.id.tv_device_detail, R.id.cv
            , R.id.track_tv, R.id.tv_choice, R.id.tv_confirm, R.id.filter_iv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.head_left_iv:
                onBackPressedSupport();
                break;
            case R.id.tv_recent:
                getRecent();
                break;
            case R.id.tv_similarity:
                getSimilarity();
                break;
            case R.id.tv_device_detail:
                Intent videoIntent = new Intent(FaceDepotDetailActivity.this, VideoDetailActivity.class);
                videoIntent.putExtra("cid", mIsFromBody ? bodyEntity.cameraId : faceEntity.cameraId);
                startActivity(videoIntent);
                break;
            case R.id.cv:
                Intent detailIntent = new Intent();
                detailIntent.putExtra("position", 0);
                ArrayList<DetailCommonEntity> list = new ArrayList<>();

                DetailCommonEntity commonEntity = new DetailCommonEntity();
                if (mIsFromBody) {
                    commonEntity.deviceName = bodyEntity.cameraName;
                    commonEntity.endTime = Long.parseLong(bodyEntity.captureTime);
                    commonEntity.point = bodyEntity.bodyRect;
                    commonEntity.srcImg = bodyEntity.scenePath;
                    commonEntity.collectId = bodyEntity.collectId;
                    commonEntity.id = bodyEntity.id;
                    commonEntity.favoritesType = CollectionListFragment.BODY;
                    commonEntity.cameraId = bodyEntity.cameraId;
                    commonEntity.cameraName = bodyEntity.cameraName;
                    commonEntity.faceImg = bodyEntity.bodyPath;
                    commonEntity.captureTime = Long.parseLong(bodyEntity.captureTime);
                    commonEntity.collected = "1".equals(bodyEntity.isCollect);
                } else {
                    commonEntity.deviceName = faceEntity.cameraName;
                    commonEntity.endTime = Long.parseLong(faceEntity.captureTime);
                    commonEntity.point = faceEntity.faceRect;
                    commonEntity.srcImg = faceEntity.scenePath;
                    commonEntity.collectId = faceEntity.collectId;
                    commonEntity.id = faceEntity.id;
                    commonEntity.favoritesType = CollectionListFragment.FACE;
                    commonEntity.cameraId = faceEntity.cameraId;
                    commonEntity.cameraName = faceEntity.cameraName;
                    commonEntity.faceImg = faceEntity.facePath;
                    commonEntity.captureTime = Long.parseLong(faceEntity.captureTime);
                    commonEntity.collected = "1".equals(faceEntity.isCollect);
                }

                list.add(commonEntity);
                detailIntent.setClass(FaceDepotDetailActivity.this, PictureDetailActivity.class);
                detailIntent.putParcelableArrayListExtra("bean", list);
                detailIntent.putExtra("isFromBody", mIsFromBody);
                detailIntent.putExtra("isFromDepotDetail", true);
                startActivity(detailIntent);
                break;
            case R.id.track_tv:
                trackTv.setVisibility(View.GONE);
                titleTv.setText("人员轨迹");
                isChoosing = true;
                llBottom.setVisibility(View.VISIBLE);
                for (FaceNewEntity entity : mAdapter.getData()) {
                    entity.checkable = true;
                }
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.tv_choice:
                if (TextUtils.equals(getString(R.string.select_all), tvChoice.getText().toString())) {
                    for (FaceNewEntity entity : mAdapter.getData()) {
                        entity.mIsChecked = true;
                    }
                    choosedSize = mAdapter.getData().size();
                } else {
                    for (FaceNewEntity entity : mAdapter.getData()) {
                        entity.mIsChecked = false;
                    }
                    choosedSize = 0;
                }
                mAdapter.notifyDataSetChanged();
                notifySize();
                break;
            case R.id.tv_confirm:
                if (choosedSize == 0) {
                    ToastUtils.showShort(R.string.hint_select_tracker_images);
                } else {
                    List<FaceNewEntity> mCheckedList = new ArrayList<>();
                    for (FaceNewEntity entity : mAdapter.getData()) {
                        if (entity.mIsChecked) {
                            mCheckedList.add(entity);
                        }
                    }
                    ResultHolder.disposeSelectFaceEntities();
                    ResultHolder.setSelectFaceEntities(mCheckedList);
                    startActivity(new Intent(FaceDepotDetailActivity.this, PersonTrackActivity.class));
                }
                break;
            case R.id.filter_iv:
                //进入到条件筛选页面
                Intent filterIntent = new Intent(Utils.getContext(), FaceFilterActivity.class);
                if (mStartTime != -1) {
                    filterIntent.putExtra("startTime", mStartTime);
                }
                if (mEndTime != -1) {
                    filterIntent.putExtra("endTime", mEndTime);
                }
                filterIntent.putExtra("score", score);
                filterIntent.putExtra("rangeTime", rangeTime);

                startActivityForResult(filterIntent, TYPE_FILTER_FACE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            if (requestCode == TYPE_FILTER_FACE) {
                long startTime = data.getLongExtra("startTime", -1);
                long endTime = data.getLongExtra("endTime", -1);
                int score = data.getIntExtra("score", -1);
                rangeTime = data.getIntExtra("rangeTime", -1);
                if (rangeTime != -1) {
                    mStartTime = System.currentTimeMillis() - rangeTime * 24 * 60 * 60 * 1000L;
                    mEndTime = System.currentTimeMillis();
                } else {
                    if (startTime != -1) {
                        mStartTime = startTime;
                    }
                    if (endTime != -1) {
                        mEndTime = endTime;
                    }
                }
                if (score != -1) {
                    this.score = score;
                }

                //进行重新请求
                if (mIsFromBody) {
                    mPresenter.getBodyList(bodyEntity.id, mStartTime, mEndTime, score);
                } else {
                    mPresenter.getFaceList(faceEntity.id, mStartTime, mEndTime, score);
                }
                rlOverly.setVisibility(View.VISIBLE);
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
    protected void onResume() {
        super.onResume();
        mPresenter.getCollectionList(new CollectionListRequest(null, 3000, 1));
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
    public Activity getActivity() {
        return this;
    }

    @Override
    public void getFeatureSuccess(float[] feature) {
        mFeature = feature;
    }

    @Override
    public void getCollectionListSuccess(List<CollectionListBean> list) {
        if (null != list && !list.isEmpty()) {
            CollectedPictureHolder.urls.clear();
            for (CollectionListBean bean : list) {
                CollectedPictureHolder.urls.add(bean.getFaceImgUrl());
            }
        }
    }

    @Override
    public void getFaceListSuccess(List<FaceNewEntity> userEntity) {
        rlOverly.setVisibility(View.GONE);
        if (userEntity != null && !userEntity.isEmpty()) {
            mAdapter.setNewData(userEntity);
            rlEmpty.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);
        } else {
            rlEmpty.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
            tvEmpty.setText(rangeTime > 0 ? String.format("%d内暂无相似图片", rangeTime) : "筛选时间内暂无相似图片");
        }
    }

    @Override
    public void getFaceFail() {

    }

    @Override
    public void onBackPressedSupport() {
        if (isChoosing) {
            trackTv.setVisibility(View.VISIBLE);
            titleTv.setText("详细信息");
            isChoosing = false;
            llBottom.setVisibility(View.GONE);
            for (FaceNewEntity entity : mAdapter.getData()) {
                entity.checkable = false;
            }
            mAdapter.notifyDataSetChanged();
            return;
        }
        super.onBackPressedSupport();
    }
}
