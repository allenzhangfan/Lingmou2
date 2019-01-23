package cloud.antelope.lingmou.mvp.ui.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.http.imageloader.glide.GlideArms;
import com.jess.arms.http.imageloader.glide.GlideRequest;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.modle.LoadingDialog;
import com.lingdanet.safeguard.common.utils.SizeUtils;
import com.lingdanet.safeguard.common.utils.TimeUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.FormatUtils;
import cloud.antelope.lingmou.app.utils.ResultHolder;
import cloud.antelope.lingmou.di.component.DaggerFaceRecognizeComponent;
import cloud.antelope.lingmou.di.module.FaceRecognizeModule;
import cloud.antelope.lingmou.mvp.contract.FaceRecognizeContract;
import cloud.antelope.lingmou.mvp.model.entity.DetailCommonEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceNewEntity;
import cloud.antelope.lingmou.mvp.presenter.FaceRecognizePresenter;
import cloud.antelope.lingmou.mvp.ui.adapter.FaceRecognizeAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.CircleProgressNewView;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class FaceRecognizeActivity extends BaseActivity<FaceRecognizePresenter> implements FaceRecognizeContract.View {

    private static final int TYPE_FILTER_FACE = 0x01;
    private static final int TYPE_PERSON_TRACK = 0x02;
    @BindView(R.id.person_old_iv)
    ImageView mPersonOldIv;
    @BindView(R.id.person_select_iv)
    ImageView mPersonSelectIv;
    @BindView(R.id.person_name_type_tv)
    TextView mPersonNameTypeTv;
    @BindView(R.id.person_time_tv)
    TextView mPersonTimeTv;
    @BindView(R.id.person_address_tv)
    TextView mPersonAddressTv;
    @BindView(R.id.face_rclv)
    RecyclerView mFaceRclv;
    @BindView(R.id.face_back_btn)
    ImageView mFaceBackBtn;
    @BindView(R.id.score_progress)
    CircleProgressNewView mScoreCircleView;
    @BindView(R.id.track_tv)
    ImageView mTrackTv;
    @BindView(R.id.confirm_track_tv)
    TextView mConfirmTrackTv;
    @BindView(R.id.detail_ll)
    RelativeLayout mDetailLl;
    @BindView(R.id.show_detail_ll)
    LinearLayout mShowDetailLl;
    @BindView(R.id.old_cardview)
    CardView mOldCardView;
    @BindView(R.id.mid_ll)
    LinearLayout mMidRl;
    @BindView(R.id.right_cardview)
    CardView mRightCardView;
    @BindView(R.id.recog_order_rg)
    RadioGroup mRecogOrderRg;
    @BindView(R.id.title_tv)
    TextView mTitleTv;
    @BindView(R.id.filter_iv)
    ImageView mFilterIv;
    @BindView(R.id.empty_ll)
    LinearLayout mEmptyLl;
    @BindView(R.id.gray_view)
    View mGrayView;
    @BindView(R.id.gray_split_view)
    View mGraySplitView;

    @Inject
    FaceRecognizeAdapter mFaceRecognizeAdapter;
    @Inject
    RecyclerView.LayoutManager mLayoutManager;
    @Inject
    RecyclerView.ItemDecoration mItemDecoration;
    @BindView(R.id.recog_time_rb)
    RadioButton recogTimeRb;
    @BindView(R.id.recog_score_rb)
    RadioButton recogScoreRb;
    @BindView(R.id.tv_choice)
    TextView tvChoice;
    @BindView(R.id.tv_size)
    TextView tvSize;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    @BindView(R.id.ll_bottom)
    LinearLayout llBottom;
    @BindView(R.id.tv_empty)
    TextView tvEmpty;
    @BindView(R.id.btn_change_filter)
    Button btnChangeFilter;

    private LoadingDialog mLoadingDialog;
    private List<FaceNewEntity> mFaceNewEntityList;
    private String mFacePath;
    private int mPrePosition;
    private FaceNewEntity mSelectFaceEntity;
    private String mFaceSourcePath;
    private float mDeltaViewHeight;

    private boolean mIsTrackAfter;
    private float mOldCardOffset;
    private List<FaceNewEntity> mCheckedList;
    private boolean mIsBacking;
    private boolean mIsTracking;
    private boolean mIsInited;
    private String mFeature;
    private long mStartTime = -1;
    private long mEndTime = -1;
    private float[] mFeeatureList;
    private boolean mIsFromFaceBody;
    private String mFaceBodyPoint;
    private boolean mIsBody;
    private boolean mIsEmpty;
    private int score;
    private int rangeTime = 3;

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        DaggerFaceRecognizeComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .faceRecognizeModule(new FaceRecognizeModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_face_recognize; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        /*if (mOldCardOffset == 0f) {
            int[] location = new int[2];
            mOldCardView.getLocationOnScreen(location);//获取在整个屏幕内的绝对坐标
            int screenWidth = SizeUtils.getScreenWidth();
            int halfCardWidth = mOldCardView.getWidth() / 2;
            mOldCardOffset = (screenWidth / 2) - halfCardWidth - location[0];
        }*/

    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mLoadingDialog = new LoadingDialog(this);
        mCheckedList = new ArrayList<>();
        mDeltaViewHeight = SizeUtils.dp2px(45);
        score = 85;
        int screenWidth = SizeUtils.getScreenWidth();
        int width_height = (screenWidth - SizeUtils.dp2px(54)) / 2;
        ViewGroup.LayoutParams layoutParams = mOldCardView.getLayoutParams();
        layoutParams.height = width_height;
        layoutParams.width = width_height;
        mOldCardView.setLayoutParams(layoutParams);
        ViewGroup.LayoutParams rightLayoutParas = mRightCardView.getLayoutParams();
        rightLayoutParas.height = width_height;
        rightLayoutParas.width = width_height;
        mRightCardView.setLayoutParams(rightLayoutParas);
        mOldCardOffset = (screenWidth / 2) - width_height / 2 - SizeUtils.dp2px(18);

        mFaceSourcePath = getIntent().getStringExtra("faceSourcePath");
        mFeeatureList = getIntent().getFloatArrayExtra("featureList");
        mFaceBodyPoint = getIntent().getStringExtra("faceBodyRect");
        mIsBody = getIntent().getBooleanExtra("isBody", false);
        mFaceRclv.setLayoutManager(mLayoutManager);
        mFaceRclv.addItemDecoration(mItemDecoration);
        mIsFromFaceBody = getIntent().getBooleanExtra("fromFaceBody", false);
        mFacePath = getIntent().getStringExtra("facePath");
        if (mFacePath == null) {
            mFacePath = "";
        }
        mFeature = getIntent().getStringExtra("feature");
        mFaceRecognizeAdapter.setTrackStatus(false);

        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mIsInited) {
            mFaceNewEntityList = ResultHolder.getFaceEntities();
            GlideRequest<Drawable> placeholder;
            if (mIsFromFaceBody) {
                placeholder = GlideArms.with(FaceRecognizeActivity.this).load(mFacePath).placeholder(R.drawable.about_stroke_rect_gray);
            } else {
                placeholder = GlideArms.with(FaceRecognizeActivity.this).load(new File(mFacePath)).placeholder(R.drawable.about_stroke_rect_gray);
            }
            if (mIsBody) {
                placeholder.into(mPersonOldIv);
            } else {
                placeholder.centerCrop().into(mPersonOldIv);
            }
            if (null != mFaceNewEntityList && !mFaceNewEntityList.isEmpty()) {
                FaceNewEntity firBean = mFaceNewEntityList.get(0);
                firBean.isSelect = true;
                updateSelectFace(firBean);
                mFaceRclv.setAdapter(mFaceRecognizeAdapter);
                mFaceRecognizeAdapter.setNewData(mFaceNewEntityList);
            } else {
                mFaceRecognizeAdapter.setNewData(null);
                showEmpty(true);
            }
            mIsInited = true;
        }
    }

    private void updateSelectFace(FaceNewEntity picturesBean) {
        mSelectFaceEntity = picturesBean;
        if (!TextUtils.isEmpty(picturesBean.facePath)) {
            GlideArms.with(this).load(picturesBean.facePath).placeholder(R.drawable.about_stroke_rect_gray).centerCrop().into(mPersonSelectIv);
        } else {
            GlideArms.with(this).load(picturesBean.bodyPath).placeholder(R.drawable.about_stroke_rect_gray).into(mPersonSelectIv);
        }
        //        mScoreTv.setText((int) (picturesBean.getScore()) + "%");
        // mScoreCircleView.setScore(ConfidenceUtil.getConfidence(mFeature, picturesBean.faceFeature));
        mScoreCircleView.setScore(picturesBean.score);
        mPersonNameTypeTv.setText(Html.fromHtml("<font color = \"#616161\">抓拍设备：</font><font color = \"#212121\">" + picturesBean.cameraName + "</font>"));
        long captureTime = Long.parseLong(picturesBean.captureTime);
        String captureTimeStr = TimeUtils.millis2String(captureTime, TimeUtils.FORMAT_ONE);
        mPersonTimeTv.setText(Html.fromHtml("<font color = \"#616161\">抓拍时间：</font><font color = \"#212121\">" + captureTimeStr + "</font>"));
    }

    private ArrayList<DetailCommonEntity> getChangeData(List<FaceNewEntity> entities) {
        if (null != entities) {
            ArrayList<DetailCommonEntity> list = new ArrayList<>();
            for (FaceNewEntity entity : entities) {
                DetailCommonEntity commonEntity = new DetailCommonEntity();
                commonEntity.deviceName = entity.cameraName;
                commonEntity.endTime = Long.parseLong(entity.captureTime);
                if (!TextUtils.isEmpty(entity.faceRect)) {
                    commonEntity.point = entity.faceRect;
                } else {
                    commonEntity.point = entity.bodyRect;
                }
                commonEntity.srcImg = entity.scenePath;
                list.add(commonEntity);
            }
            return list;
        }
        return null;
    }

    @OnClick({R.id.person_address_tv, R.id.person_select_iv, R.id.person_old_iv, R.id.confirm_track_tv
            , R.id.filter_iv, R.id.tv_choice, R.id.tv_confirm, R.id.btn_change_filter})
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.person_address_tv:
                String cameraId = mSelectFaceEntity.cameraId;
                intent.setClass(FaceRecognizeActivity.this, VideoDetailActivity.class);
                intent.putExtra("cid", cameraId);
                startActivity(intent);
                break;
            case R.id.person_select_iv:
                //点击进入大图界面
                if (null != mSelectFaceEntity && !mIsEmpty) {
                    String sceneImg = mSelectFaceEntity.scenePath;
                    intent.setClass(FaceRecognizeActivity.this, FaceDetailActivity.class);
                    intent.putExtra("faceUrl", sceneImg);
                    String point = TextUtils.isEmpty(mSelectFaceEntity.faceRect) ? mSelectFaceEntity.bodyRect : mSelectFaceEntity.faceRect;
                    intent.putExtra("point", point);
                    intent.putExtra("isVideoFun", true);
                    intent.putExtra("captureTime", mSelectFaceEntity.captureTime);
                    intent.putExtra("cameraId", mSelectFaceEntity.cameraId);
                    intent.putExtra("cameraName", mSelectFaceEntity.cameraName);
                    intent.putExtra("bodyPathUrl", mSelectFaceEntity.bodyPath);
                    intent.putExtra("facePathUrl", mSelectFaceEntity.facePath);
                    intent.putExtra("isBody", mIsBody);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                break;
            case R.id.person_old_iv:
                intent.setClass(FaceRecognizeActivity.this, FaceDetailActivity.class);
                if (mIsFromFaceBody) {
                    intent.putExtra("faceUrl", mFaceSourcePath);
                    intent.putExtra("point", mFaceBodyPoint);
                } else {
                    intent.putExtra("faceFileUrl", mFaceSourcePath);
                }
                intent.putExtra("uploadPic",true);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.confirm_track_tv:
            case R.id.tv_confirm:
                if (!mIsBacking) {
                    //看是否选择有人员
                    if (mCheckedList.isEmpty()) {
                        ToastUtils.showShort(R.string.hint_select_tracker_images);
                    } else {
                        ResultHolder.disposeSelectFaceEntities();
                        ResultHolder.setSelectFaceEntities(mCheckedList);
                        startActivityForResult(new Intent(FaceRecognizeActivity.this, PersonTrackActivity.class), TYPE_PERSON_TRACK);
                    }
                }
                break;
            case R.id.filter_iv:
            case R.id.btn_change_filter:
                //进入到条件筛选页面
                intent.setClass(FaceRecognizeActivity.this, FaceFilterActivity.class);
                if (mStartTime != -1) {
                    intent.putExtra("startTime", mStartTime);
                }
                if (mEndTime != -1) {
                    intent.putExtra("endTime", mEndTime);
                }
                intent.putExtra("score", score);
                intent.putExtra("rangeTime", rangeTime);
                startActivityForResult(intent, TYPE_FILTER_FACE);
                break;
            case R.id.tv_choice:
                if (TextUtils.equals(getString(R.string.select_all), tvChoice.getText().toString())) {
                    mCheckedList.clear();
                    for (FaceNewEntity entity : mFaceRecognizeAdapter.getData()) {
                        entity.mIsChecked = true;
                        mCheckedList.add(entity);
                    }
                } else {
                    mCheckedList.clear();
                    for (FaceNewEntity entity : mFaceRecognizeAdapter.getData()) {
                        entity.mIsChecked = false;
                    }
                }
                mFaceRecognizeAdapter.notifyDataSetChanged();
                notifySelectCount();
                break;
        }
    }

    private void initListener() {
        mRecogOrderRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.recog_time_rb:
                        //按照时间排序
                        Collections.sort(mFaceNewEntityList, new Comparator<FaceNewEntity>() {
                            @Override
                            public int compare(FaceNewEntity o1, FaceNewEntity o2) {
                                return Long.parseLong(o1.captureTime) <= Long.parseLong(o2.captureTime) ? 1 : -1;
                            }
                        });
                        mFaceRecognizeAdapter.setNewData(mFaceNewEntityList);
                        mFaceRclv.smoothScrollToPosition(0);
                        break;
                    case R.id.recog_score_rb:
                        //按照比分排序
                        Collections.sort(mFaceNewEntityList, new Comparator<FaceNewEntity>() {
                            @Override
                            public int compare(FaceNewEntity o1, FaceNewEntity o2) {
                                // float o1Confidence = ConfidenceUtil.getConfidence(mFeature, o1.faceFeature);
                                // float o2Confidence = ConfidenceUtil.getConfidence(mFeature, o2.faceFeature);
                                return o1.score <= o2.score ? 1 : -1;
                            }
                        });
                        mFaceRecognizeAdapter.setNewData(mFaceNewEntityList);
                        mFaceRclv.smoothScrollToPosition(0);
                        break;
                }
            }
        });

        if (null != mFaceRecognizeAdapter) {
            mFaceRecognizeAdapter.setOnItemClickListener((adapter, view, position) -> {
                if (mPrePosition == position && !mIsTrackAfter) {
                    return;
                }
                if (!mIsTrackAfter) {
                    //单点更新图片页面
                    FaceNewEntity preItem = (FaceNewEntity) adapter.getItem(mPrePosition);
                    preItem.isSelect = false;
                    mFaceRecognizeAdapter.notifyItemChanged(mPrePosition, preItem);
                    FaceNewEntity picturesBean = (FaceNewEntity) adapter.getItem(position);
                    picturesBean.isSelect = true;
                    mFaceRecognizeAdapter.notifyItemChanged(position, picturesBean);
                    mPrePosition = position;
                    updateSelectFace(picturesBean);
                } else {
                    //轨迹选择页面
                    FaceNewEntity checkItem = (FaceNewEntity) adapter.getItem(position);
                    if (TextUtils.isEmpty(checkItem.latitide) || TextUtils.isEmpty(checkItem.longitude)) {
                        ToastUtils.showShort(R.string.hint_no_lat_lng_image);
                        return;
                    }
                    if (mCheckedList.contains(checkItem)) {
                        checkItem.mIsChecked = false;
                        mCheckedList.remove(checkItem);
                    } else {
                        checkItem.mIsChecked = true;
                        mCheckedList.add(checkItem);
                    }
                    mFaceRecognizeAdapter.notifyItemChanged(position, checkItem);
                    notifySelectCount();
                }
            });

            mFaceRecognizeAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                    if (!mIsTrackAfter) {
                        Intent intent = new Intent(FaceRecognizeActivity.this, PictureDetailActivity.class);
                        intent.putExtra("position", position);
                        intent.putExtra("isFromRecorg", true);
                        List data = adapter.getData();
                        ArrayList changeData = getChangeData(data);
                        if (changeData != null) {
                            intent.putParcelableArrayListExtra("bean", changeData);
                            startActivity(intent);
                        }
                    }
                    return false;
                }
            });
        }

        mFaceBackBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (mIsTrackAfter && !mIsBacking) {
                    backAnim();
                } else if (mIsTracking) {
                    return;
                } else {
                    finish();
                }
            }
        });


        mTrackTv.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (!mIsTrackAfter && !mIsTracking) {
                    trackAnim();
                }
            }
        });

    }

    private void notifySelectCount() {
        List<FaceNewEntity> data = mFaceRecognizeAdapter.getData();
        int count = 0;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).mIsChecked) {
                count++;
            }
        }
        tvSize.setText(String.format("已选择%d张", count));
        tvChoice.setText(count == data.size() ? "取消选择" : "全选");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void trackAnim() {
        //开始进行动画效果
        /*ValueAnimator valueAnimator = ValueAnimator.ofFloat(10f, 0f);
        valueAnimator.setDuration(500);
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mIsTracking = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mShowDetailLl.setVisibility(View.GONE);
                mRecogOrderRg.setVisibility(View.VISIBLE);
                mIsTrackAfter = true;
                mFaceRecognizeAdapter.setTrackStatus(mIsTrackAfter);
                FaceNewEntity item = mFaceNewEntityList.get(mPrePosition);
                if (item != null) {
                    item.isSelect = false;
                }
                mPrePosition = 0;
                if (R.id.recog_time_rb == mRecogOrderRg.getCheckedRadioButtonId()) {
                    //如果那边默认就是时间排序的选项
                    //按照时间排序
                    Collections.sort(mFaceNewEntityList, new Comparator<FaceNewEntity>() {
                        @Override
                        public int compare(FaceNewEntity o1, FaceNewEntity o2) {
                            return Long.parseLong(o1.captureTime) <= Long.parseLong(o2.captureTime) ? 1 : -1;
                        }
                    });
                    mFaceRecognizeAdapter.setNewData(mFaceNewEntityList);
                    mFaceRclv.smoothScrollToPosition(0);
                } else {
                    mRecogOrderRg.check(R.id.recog_time_rb);
                }
                mTrackTv.setVisibility(View.GONE);
                mConfirmTrackTv.setVisibility(View.VISIBLE);
                mTitleTv.setText(R.string.person_track_query);
                mIsTracking = false;
                mFilterIv.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.start();*/

        mOldCardView.animate().translationX(mOldCardOffset).setDuration(500).setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                float alpha = 1 - value;
                mRightCardView.setAlpha(alpha);
            }
        }).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mIsTracking = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mIsTrackAfter = true;
                mRightCardView.setVisibility(View.INVISIBLE);

                mDetailLl.setVisibility(View.GONE);
                mRecogOrderRg.setVisibility(View.VISIBLE);
                mGrayView.setVisibility(View.VISIBLE);
                mGraySplitView.setVisibility(View.VISIBLE);
                mIsTrackAfter = true;
                mFaceRecognizeAdapter.setTrackStatus(mIsTrackAfter);
                FaceNewEntity item = mFaceNewEntityList.get(mPrePosition);
                if (item != null) {
                    item.isSelect = false;
                }
                mPrePosition = 0;
                if (R.id.recog_time_rb == mRecogOrderRg.getCheckedRadioButtonId()) {
                    //如果那边默认就是时间排序的选项
                    //按照时间排序
                    Collections.sort(mFaceNewEntityList, new Comparator<FaceNewEntity>() {
                        @Override
                        public int compare(FaceNewEntity o1, FaceNewEntity o2) {
                            return Long.parseLong(o1.captureTime) <= Long.parseLong(o2.captureTime) ? 1 : -1;
                        }
                    });
                    mFaceRecognizeAdapter.setNewData(mFaceNewEntityList);
                    mFaceRclv.smoothScrollToPosition(0);
                } else {
                    mRecogOrderRg.check(R.id.recog_time_rb);
                }
                mTrackTv.setVisibility(View.GONE);
//                mConfirmTrackTv.setVisibility(View.VISIBLE);
                mTitleTv.setText(R.string.person_track_query);
                mIsTracking = false;
                mFilterIv.setVisibility(View.GONE);
                llBottom.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void backAnim() {
        //开始进行动画效果
        /*ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 10f);
        valueAnimator.setDuration(500);
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mIsBacking = true;
                mRecogOrderRg.setVisibility(View.GONE);
                mFaceRecognizeAdapter.setTrackStatus(false);
                mCheckedList.clear();
                for (int i = 0; i < mFaceNewEntityList.size(); i++) {
                    mFaceNewEntityList.get(i).mIsChecked = false;
                }
                //按照比分排序
                Collections.sort(mFaceNewEntityList, new Comparator<FaceNewEntity>() {
                    @Override
                    public int compare(FaceNewEntity o1, FaceNewEntity o2) {
                        return Long.parseLong(o1.captureTime) <= Long.parseLong(o2.captureTime) ? 1 : -1;
                    }
                });
                mFaceRecognizeAdapter.setNewData(mFaceNewEntityList);
                mFaceRclv.smoothScrollToPosition(0);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mShowDetailLl.setVisibility(View.VISIBLE);
                mFilterIv.setVisibility(View.VISIBLE);
                mIsTrackAfter = false;
                FaceNewEntity faceNewEntity = mFaceRecognizeAdapter.getItem(mPrePosition);
                faceNewEntity.isSelect = true;
                mFaceRecognizeAdapter.notifyItemChanged(mPrePosition, faceNewEntity);
                updateSelectFace(faceNewEntity);
                mConfirmTrackTv.setVisibility(View.GONE);
                mTrackTv.setVisibility(View.VISIBLE);
                mTitleTv.setText(R.string.face_result);
                mIsBacking = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.start();*/

        mOldCardView.animate().translationX(0f).setDuration(500).setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                float alpha = value;
                mRightCardView.setAlpha(alpha);
            }
        }).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mDetailLl.setVisibility(View.VISIBLE);
                mRightCardView.setVisibility(View.VISIBLE);
                mIsBacking = true;
                mRecogOrderRg.setVisibility(View.GONE);
                mGrayView.setVisibility(View.GONE);
                mGraySplitView.setVisibility(View.GONE);
                mFaceRecognizeAdapter.setTrackStatus(false);
                mCheckedList.clear();
                for (int i = 0; i < mFaceNewEntityList.size(); i++) {
                    mFaceNewEntityList.get(i).mIsChecked = false;
                }
                //按照比分排序
                Collections.sort(mFaceNewEntityList, new Comparator<FaceNewEntity>() {
                    @Override
                    public int compare(FaceNewEntity o1, FaceNewEntity o2) {
                        return Long.parseLong(o1.captureTime) <= Long.parseLong(o2.captureTime) ? 1 : -1;
                    }
                });
                mFaceRecognizeAdapter.setNewData(mFaceNewEntityList);
                mFaceRclv.smoothScrollToPosition(0);

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mIsTrackAfter = false;
                mDetailLl.setVisibility(View.VISIBLE);
                mFilterIv.setVisibility(View.VISIBLE);
                mIsTrackAfter = false;
                FaceNewEntity faceNewEntity = mFaceRecognizeAdapter.getItem(mPrePosition);
                faceNewEntity.isSelect = true;
                mFaceRecognizeAdapter.notifyItemChanged(mPrePosition, faceNewEntity);
                updateSelectFace(faceNewEntity);
                mConfirmTrackTv.setVisibility(View.GONE);
                mTrackTv.setVisibility(View.VISIBLE);
                mTitleTv.setText(R.string.face_result);
                mIsBacking = false;
                llBottom.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    public void onBackPressedSupport() {
        if (mIsTrackAfter) {
            if (!mIsBacking) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    backAnim();
                } else {
                    super.onBackPressedSupport();
                }
            } else {
                return;
            }
        } else {
            if (mIsTracking) {
                return;
            } else {
                super.onBackPressedSupport();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ResultHolder.disposeFaceEntity();
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
        checkNotNull(message);
        ArmsUtils.toastText(message);
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
                if (mIsBody) {
                    if (mFeeatureList == null) {
                        mPresenter.getBodyList(mStartTime, mEndTime, score, mFeature);
                    } else {
                        mPresenter.getBodyList(mStartTime, mEndTime, score, mFeeatureList);
                    }
                } else {
                    if (mFeeatureList == null) {
                        mPresenter.getFaceListByFeature(mStartTime, mEndTime, score, mFeature);
                    } else {
                        mPresenter.getFaceListByFeature(mStartTime, mEndTime, score, mFeeatureList);
                    }
                }
            }
        }
        if (requestCode == TYPE_PERSON_TRACK) {
            onBackPressedSupport();
        }
    }

    @Override
    public void onFaceSuccess(List<FaceNewEntity> userEntity) {
        mFaceNewEntityList = userEntity;
        if (null != mFaceNewEntityList && !mFaceNewEntityList.isEmpty()) {
            showEmpty(false);
            FaceNewEntity firBean = mFaceNewEntityList.get(0);
            firBean.isSelect = true;
            updateSelectFace(firBean);
            mFaceRecognizeAdapter.setNewData(mFaceNewEntityList);
            mPersonAddressTv.setVisibility(View.VISIBLE);
        } else {
            //没有数据，则展示默认图
            mFaceRecognizeAdapter.setNewData(null);
            showEmpty(true);
        }
    }

    private void showEmpty(boolean isShowEmpty) {
        mIsEmpty = isShowEmpty;
        int emptyRes = isShowEmpty ? View.VISIBLE : View.GONE;
        int dataRes = isShowEmpty ? View.GONE : View.VISIBLE;
        mEmptyLl.setVisibility(emptyRes);
        mFaceRclv.setVisibility(dataRes);
        mTrackTv.setVisibility(dataRes);
        mDetailLl.setVisibility(View.VISIBLE);
        if (isShowEmpty) {
            mPersonNameTypeTv.setText(Html.fromHtml("<font color = \"#616161\">抓拍设备：</font><font color = \"#212121\">" + "----" + "</font>"));
            mPersonTimeTv.setText(Html.fromHtml("<font color = \"#616161\">抓拍时间：</font><font color = \"#212121\">" + "----" + "</font>"));
            mPersonSelectIv.setImageResource(R.drawable.placeholder_face_compare);
            mScoreCircleView.setScore(0f);
            mPersonAddressTv.setVisibility(View.GONE);
            mDetailLl.setVisibility(View.GONE);
            tvEmpty.setText(rangeTime > 0 ? String.format("%d内暂无数据", rangeTime) : "筛选时间内暂无数据");
        }
    }

    @Override
    public void onFaceFail() {

    }

}
