package cloud.antelope.lingmou.mvp.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.http.imageloader.glide.GlideArms;
import com.jess.arms.http.imageloader.glide.GlideRequest;
import com.jess.arms.http.imageloader.glide.ImageConfigImpl;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.modle.LoadingDialog;
import com.lingdanet.safeguard.common.modle.SweetDialog;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.SizeUtils;
import com.lingdanet.safeguard.common.utils.TimeUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.lingdanet.safeguard.common.utils.Utils;
import com.lingdanet.safeguard.common.utils.WaterMarkUtils;
import com.umeng.analytics.MobclickAgent;

import org.simple.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.EventBusTags;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.di.component.DaggerDailyPoliceDetailComponent;
import cloud.antelope.lingmou.di.module.DailyPoliceDetailModule;
import cloud.antelope.lingmou.mvp.contract.DailyPoliceDetailContract;
import cloud.antelope.lingmou.mvp.model.entity.AlarmChangeBean;
import cloud.antelope.lingmou.mvp.model.entity.DailyPoliceAlarmEntity;
import cloud.antelope.lingmou.mvp.presenter.DailyPoliceDetailPresenter;
import cloud.antelope.lingmou.mvp.ui.widget.CircleProgressNewView;

import static com.jess.arms.utils.Preconditions.checkNotNull;
import static com.lingdanet.safeguard.common.utils.WaterMarkUtils.getTextLength;

/**
 * 重点人员告警详情
 */
public class DailyPoliceDetailActivity extends BaseActivity<DailyPoliceDetailPresenter> implements DailyPoliceDetailContract.View {
    private static final int DETAIL_PERSON = 0x01;
    private static final int DETAIL_TASK = 0x02;
    @BindView(R.id.head_left_iv)
    ImageButton mHeadLeftIv;
    @BindView(R.id.title_tv)
    TextView mTitleTv;
    @BindView(R.id.head_right_iv)
    ImageView mHeadRightIv;
    @BindView(R.id.invalid_state_tv)
    TextView mInvalidStateTv;
    @BindView(R.id.valid_state_tv)
    TextView mValidStateTv;
    @BindView(R.id.valid_or_invalid_state_tv)
    TextView mValidOrInvalidStateTv;
    @BindView(R.id.valid_state_ll)
    LinearLayout mValidStateLl;
    @BindView(R.id.detail_bukong_iv)
    ImageView mDetailBukongIv;
    @BindView(R.id.detail_zhuapai_iv)
    ImageView mDetailZhuapaiIv;
    @BindView(R.id.detail_similar_progresview)
    CircleProgressNewView mDetailSimilarProgresview;
    @BindView(R.id.detail_similar_tv)
    TextView mDetailSimilarTv;
    @BindView(R.id.detail_person_type_tv)
    TextView mDetailPersonTypeTv;
    @BindView(R.id.detail_person_detail_nav_tv)
    TextView mDetailPersonDetailNavTv;
    @BindView(R.id.detail_person_name_tv)
    TextView mDetailPersonNameTv;
    @BindView(R.id.detail_person_name_ll)
    LinearLayout mDetailPersonNameLl;
    @BindView(R.id.detail_person_depot_tv)
    TextView mDetailPersonDepotTv;
    @BindView(R.id.detail_task_nav_tv)
    TextView mDetailTaskNavTv;
    @BindView(R.id.detail_task_name_tv)
    TextView mDetailTaskNameTv;
    @BindView(R.id.detail_alarm_type_tv)
    TextView mDetailAlarmTypeTv;
    @BindView(R.id.detail_task_camera_name_tv)
    TextView mDetailTaskCameraNameTv;
    @BindView(R.id.detail_task_camera_ll)
    LinearLayout mDetailTaskCameraLl;
    @BindView(R.id.detail_task_camera_address_tv)
    TextView mDetailTaskCameraAddressTv;
    @BindView(R.id.detail_task_camera_address_ll)
    LinearLayout mDetailTaskCameraAddressLl;
    @BindView(R.id.detail_task_detail_ll)
    LinearLayout mDetailTaskDetailLl;
    @BindView(R.id.detail_remark_et)
    EditText mDetailRemarkEt;
    @BindView(R.id.detail_person_depot_ll)
    LinearLayout mDetailPersonDepotLl;
    @BindView(R.id.capture_time_tv)
    TextView mCaptureTimeTv;
    @BindView(R.id.water_tag_iv)
    ImageView mWaterTagIv;
    @BindView(R.id.water_bk_iv)
    ImageView mWaterBkIv;
    @BindView(R.id.ll_control_pic)
    LinearLayout llControlPic;
    @BindView(R.id.ll_similarity)
    LinearLayout llSimilarity;
    @BindView(R.id.detail_person_detail_ll)
    LinearLayout mDetailPersonDetailLl;
    @BindView(R.id.ll_person_detail)
    LinearLayout llPersonDetail;

    private Drawable mLinkongDrawable, mKeyPersonDrawable,mSuiteDrawable, mArrowUpDrawable, mArrowDownDrawable;

    private DailyPoliceAlarmEntity mAlarmEntity;

    private ImageLoader mImageLoader;

    private int mPosition;

    private LoadingDialog mLoadingDialog;

    private boolean mIsCollect;

    private boolean mIsHasCollect;
    private boolean animating;
    boolean personFold, taskFold;
    int personFoldHeight, taskFoldHeight;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerDailyPoliceDetailComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .dailyPoliceDetailModule(new DailyPoliceDetailModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_daily_police_detail; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mDetailPersonDetailLl.measure(0, 0);
        mDetailTaskDetailLl.measure(0, 0);
        personFoldHeight = mDetailPersonDetailLl.getMeasuredHeight();
        taskFoldHeight = mDetailTaskDetailLl.getMeasuredHeight();

        mLoadingDialog = new LoadingDialog(this);
        mImageLoader = ArmsUtils.obtainAppComponentFromContext(this).imageLoader();
        mAlarmEntity = (DailyPoliceAlarmEntity) getIntent().getSerializableExtra("entity");
        mPosition = getIntent().getIntExtra("position", -1);
        mArrowDownDrawable = getResources().getDrawable(R.drawable.arrow_down);
        mArrowDownDrawable.setBounds(0, 0, mArrowDownDrawable.getMinimumWidth(), mArrowDownDrawable.getMinimumHeight());
        mArrowUpDrawable = getResources().getDrawable(R.drawable.arrow_up);
        mArrowUpDrawable.setBounds(0, 0, mArrowUpDrawable.getMinimumWidth(), mArrowUpDrawable.getMinimumHeight());
        mLinkongDrawable = getResources().getDrawable(R.drawable.shape_dot_yellow_ffc107);
        mLinkongDrawable.setBounds(0, 0, mLinkongDrawable.getMinimumWidth(), mLinkongDrawable.getMinimumHeight());
        mKeyPersonDrawable = getResources().getDrawable(R.drawable.shape_dot_red_ec407a);
        mKeyPersonDrawable.setBounds(0, 0, mKeyPersonDrawable.getMinimumWidth(), mKeyPersonDrawable.getMinimumHeight());
        mSuiteDrawable = getResources().getDrawable(R.drawable.shape_dot_green_26c6da);
        mSuiteDrawable.setBounds(0, 0, mSuiteDrawable.getMinimumWidth(), mSuiteDrawable.getMinimumHeight());
        mTitleTv.setText(R.string.alarm_detail_info);
        if(getIntent().getBooleanExtra("isEventRemind",false)){
            mTitleTv.setText(R.string.event_detail);
        }
        mHeadRightIv.setVisibility(View.VISIBLE);
        mHeadRightIv.setImageResource(R.drawable.collect_black_mark);
        Integer collectStatus = mAlarmEntity.getCollectStatus();
        if (null != collectStatus) {
            if (1 == collectStatus) {
                mIsCollect = true;
                mHeadRightIv.setImageResource(R.drawable.collected_yellow_marked);
            }
        }
        mWaterBkIv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = mWaterBkIv.getWidth();
                int height = mWaterBkIv.getHeight();
                Bitmap markerBitmap = getMarkerBitmap(width, height);
                mWaterBkIv.setImageBitmap(markerBitmap);
                mWaterBkIv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        mWaterTagIv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (101504 == mAlarmEntity.getTaskType() || 101502 == mAlarmEntity.getTaskType() || 101503 == mAlarmEntity.getTaskType())
                    return;
                int width = mWaterTagIv.getWidth();
                int height = mWaterTagIv.getHeight();
                Bitmap markerBitmap = getMarkerBitmap(width, height);
                mWaterTagIv.setImageBitmap(markerBitmap);
                mWaterTagIv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        updateAlarmInfo(mAlarmEntity);
    }

    private void updateAlarmInfo(DailyPoliceAlarmEntity alarmEntity) {
        if (null != alarmEntity) {
            Integer taskType = alarmEntity.getTaskType();
            if (alarmEntity.getFacePath() != null) {
                GlideRequest<Drawable> placeholder = GlideArms.with(this)
                        .load(alarmEntity.getFacePath())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.about_stroke_rect_gray);
                if (taskType == 101503) {
                    placeholder.into(mDetailZhuapaiIv);
                } else {
                    placeholder.centerCrop().into(mDetailZhuapaiIv);
                }

            }
            if (alarmEntity.getImageUrl() != null) {
                mImageLoader.loadImage(this, ImageConfigImpl
                        .builder()
                        .cacheStrategy(0)
                        .url(alarmEntity.getImageUrl())
                        .placeholder(R.drawable.about_stroke_rect_gray)
                        .imageView(mDetailBukongIv).build());
            }
            if (null != taskType) {
                // 101501:重点人员布控  101502:外来人员布控  101503:魅影报警  101504:一体机报警  101505:临控报警
                if (101501 == taskType) {
                    //重点人员
                    mDetailPersonTypeTv.setTextColor(getResources().getColor(R.color.red_ec407a));
                    mDetailPersonTypeTv.setCompoundDrawables(mKeyPersonDrawable, null, null, null);
                    mDetailPersonTypeTv.setBackgroundResource(R.drawable.shape_rect_key_person);
                    mDetailPersonTypeTv.setText(R.string.control_key_person);

                    mDetailAlarmTypeTv.setText(Utils.getContext().getString(R.string.control_key_person));
                } else if (101505 == taskType) {
                    mDetailPersonTypeTv.setTextColor(getResources().getColor(R.color.yellow_ffc107));
                    mDetailPersonTypeTv.setCompoundDrawables(mLinkongDrawable, null, null, null);
                    mDetailPersonTypeTv.setBackgroundResource(R.drawable.shape_rect_linkong);
                    mDetailPersonTypeTv.setText(R.string.control_person);
                    mDetailPersonDetailNavTv.setVisibility(View.GONE);
                    mDetailPersonDepotLl.setVisibility(View.GONE);
                    mDetailPersonNameLl.setVisibility(View.GONE);
                    mDetailAlarmTypeTv.setText(R.string.control_person);
                } else if (101502 == taskType) {
                    llControlPic.setVisibility(View.GONE);
                    llSimilarity.setVisibility(View.GONE);
                    llPersonDetail.setVisibility(View.GONE);
                    mDetailAlarmTypeTv.setText(R.string.outside_person);
//                    detailTaskCameraAddressLl.setVisibility(View.VISIBLE);//地址
                } else if (101504 == taskType) {
                    mDetailPersonTypeTv.setTextColor(getResources().getColor(R.color.green_26C6DA));
                    mDetailPersonTypeTv.setCompoundDrawables(mSuiteDrawable, null, null, null);
                    mDetailPersonTypeTv.setBackgroundResource(R.drawable.shape_rect_suite);
                    mDetailPersonTypeTv.setText(R.string.private_network_suite);
                    llControlPic.setVisibility(View.GONE);
//                    llSimilarity.setVisibility(View.GONE);
                    llPersonDetail.setVisibility(View.GONE);
                    mDetailAlarmTypeTv.setText(R.string.private_network_suite);
                } else if (101503 == taskType) {
                    llControlPic.setVisibility(View.GONE);
                    llSimilarity.setVisibility(View.GONE);
                    llPersonDetail.setVisibility(View.GONE);
                    mHeadRightIv.setVisibility(View.GONE);
                    mDetailAlarmTypeTv.setText(R.string.event_remind);
                }
            }
            DailyPoliceAlarmEntity.ObjectMainJsonBean objectMainJson = alarmEntity.getObjectMainJson();
            if (null != objectMainJson) {
                String name = objectMainJson.getName();
                if (!TextUtils.isEmpty(name)) {
                    char xing = name.charAt(0);
                    StringBuilder builder = new StringBuilder();
                    for (int i = 1; i < name.length(); i++) {
                        builder.append("*");
                    }
                    String nameNew = xing + builder.toString();
                    mDetailPersonNameTv.setText(nameNew);
                }
            }
            mDetailPersonDepotTv.setText(alarmEntity.getLibName());
            mDetailTaskNameTv.setText(alarmEntity.getTaskName());
            mDetailSimilarProgresview.setScore((float) alarmEntity.getSimilarity(), true);
            mDetailSimilarTv.setText(String.format(getResources().getString(R.string.face_person_score), String.valueOf((int) alarmEntity.getSimilarity())));
            mDetailTaskCameraNameTv.setText(alarmEntity.getCameraName());
            mDetailTaskCameraAddressTv.setText("");
            mCaptureTimeTv.setText(TimeUtils.millis2String(Long.parseLong(alarmEntity.getCaptureTime())));
            Integer isEffective = alarmEntity.getIsEffective();
            Integer isHandle = alarmEntity.getIsHandle();
            if (null != isHandle && 1 == isHandle) {
                if (null == isEffective) {
                    //无效
                    mValidOrInvalidStateTv.setVisibility(View.VISIBLE);
                    mInvalidStateTv.setVisibility(View.GONE);
                    mValidStateTv.setVisibility(View.GONE);
                    mValidOrInvalidStateTv.setText(R.string.invalid_alarm);
                    mValidOrInvalidStateTv.setTextColor(getResources().getColor(R.color.blue_gray_78909c));
                } else if (1 == isEffective) {
                    //有效
                    mValidOrInvalidStateTv.setVisibility(View.VISIBLE);
                    mInvalidStateTv.setVisibility(View.GONE);
                    mValidStateTv.setVisibility(View.GONE);
                    mValidOrInvalidStateTv.setText(R.string.valid_alarm);
                    mValidOrInvalidStateTv.setTextColor(getResources().getColor(R.color.green_4caf50));
                } else {
                    //无效
                    mValidOrInvalidStateTv.setVisibility(View.VISIBLE);
                    mInvalidStateTv.setVisibility(View.GONE);
                    mValidStateTv.setVisibility(View.GONE);
                    mValidOrInvalidStateTv.setText(R.string.invalid_alarm);
                    mValidOrInvalidStateTv.setTextColor(getResources().getColor(R.color.blue_gray_78909c));
                }
                mDetailRemarkEt.setEnabled(false);
                mDetailRemarkEt.setHint("");
                mDetailRemarkEt.setText(alarmEntity.getOperationDetail());
            } else {
                //未处理
                mValidOrInvalidStateTv.setVisibility(View.GONE);
                mInvalidStateTv.setVisibility(View.VISIBLE);
                mValidStateTv.setVisibility(View.VISIBLE);
            }
        }
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

    @OnClick({R.id.head_left_iv, R.id.head_right_iv, R.id.invalid_state_tv, R.id.iv_moment_video,
            R.id.valid_state_tv, R.id.detail_zhuapai_iv, R.id.detail_bukong_iv, R.id.detail_person_detail_nav_tv,
            R.id.detail_person_name_ll, R.id.detail_task_nav_tv, R.id.detail_task_camera_ll,
            R.id.detail_task_camera_address_ll})
    public void onViewClicked(View view) {
        SweetDialog sweetDialog = new SweetDialog(DailyPoliceDetailActivity.this);
        sweetDialog.setNegativeListener(null);
        switch (view.getId()) {
            case R.id.iv_moment_video:
                boolean hasVideoLive = SPUtils.getInstance().getBoolean(Constants.PERMISSION_HAS_VIDEO_LIVE, false);
                if (hasVideoLive) {
                    //进入事件视频
                    Intent intent = new Intent(DailyPoliceDetailActivity.this, PlayerNewActivity.class);
                    intent.putExtra("isEvent", true);
                    intent.putExtra("cameraName", mAlarmEntity.getCameraName());
                    intent.putExtra("cameraSn", String.valueOf(mAlarmEntity.getCameraId()));
                    intent.putExtra("cameraId", mAlarmEntity.getCameraId());
                    intent.putExtra("captureTime", Long.parseLong(mAlarmEntity.getCaptureTime()));
                    startActivity(intent);
                } else {
                    ToastUtils.showShort(R.string.hint_no_permission);
                }
                break;
            case R.id.head_left_iv:
                if (mIsHasCollect) {
                    //证明收藏了或者取消收藏了
                    Intent intent = new Intent();
                    intent.putExtra("collect", mIsCollect);
                    intent.putExtra("collect_position", mPosition);
                    setResult(RESULT_OK, intent);
                }
                finish();
                break;
            case R.id.head_right_iv:
                //收藏 或者取消收藏
                mPresenter.collectAlarmInfo(mAlarmEntity.getId());
                break;
            case R.id.invalid_state_tv:
                //标记为无效
                MobclickAgent.onEvent(DailyPoliceDetailActivity.this, "dealWithAlarm");
                sweetDialog.setTitle("确认该告警无效吗？");
                sweetDialog.show();
                sweetDialog.setPositiveListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPresenter.dealFaceAlarm(mAlarmEntity.getId(), 0, mDetailRemarkEt.getText().toString());
                    }
                });
                break;
            case R.id.valid_state_tv:
                //标记为有效
                MobclickAgent.onEvent(DailyPoliceDetailActivity.this, "dealWithAlarm");
                sweetDialog.setTitle("确认该告警有效吗？");
                sweetDialog.show();
                sweetDialog.setPositiveListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPresenter.dealFaceAlarm(mAlarmEntity.getId(), 1, mDetailRemarkEt.getText().toString());
                    }
                });
                break;
            case R.id.detail_bukong_iv:
                Intent faceBkIntent = new Intent(DailyPoliceDetailActivity.this, FaceDetailActivity.class);
                faceBkIntent.putExtra("faceUrl", mAlarmEntity.getImageUrl());
                faceBkIntent.putExtra("isControlImage", true);
                startActivity(faceBkIntent);
                break;
            case R.id.detail_zhuapai_iv:
                //进入大图展示页面
                Intent faceIntent = new Intent(DailyPoliceDetailActivity.this, FaceDetailActivity.class);
                faceIntent.putExtra("faceUrl", mAlarmEntity.getScenePath());
                faceIntent.putExtra("point", mAlarmEntity.getFaceRect());
                faceIntent.putExtra("isVideoFun", true);
                faceIntent.putExtra("captureTime", mAlarmEntity.getCaptureTime());
                faceIntent.putExtra("cameraId", String.valueOf(mAlarmEntity.getCameraId()));
                faceIntent.putExtra("cameraName", mAlarmEntity.getCameraName());
                faceIntent.putExtra("facePathUrl", mAlarmEntity.getFacePath());
                startActivity(faceIntent);
                break;
            case R.id.detail_person_detail_nav_tv:
                //收起或者展开人员详情
                /*int visibility = mDetailPersonNameLl.getVisibility();
                Drawable rightDrawable = visibility == View.VISIBLE ? mArrowUpDrawable : mArrowDownDrawable;
                mDetailPersonDetailNavTv.setCompoundDrawables(null, null, rightDrawable, null);
                int afterVisible = visibility == View.VISIBLE ? View.GONE : View.VISIBLE;
                mDetailPersonNameLl.setVisibility(afterVisible);
                mDetailPersonDepotLl.setVisibility(afterVisible);*/
                fold(DETAIL_PERSON);
                break;
            case R.id.detail_person_name_ll:
                //进入到个人详情页面
                Intent intent = new Intent(DailyPoliceDetailActivity.this, DailyPersonDetailActivity.class);
                intent.putExtra("person", mAlarmEntity);
                startActivity(intent);
                break;
            case R.id.detail_task_nav_tv:
                //任务的展开或者收起
                /*int taskVisible = mDetailTaskDetailLl.getVisibility();
                Drawable rightTaskDrawable = taskVisible == View.VISIBLE ? mArrowUpDrawable : mArrowDownDrawable;
                mDetailTaskNavTv.setCompoundDrawables(null, null, rightTaskDrawable, null);
                int afterTaskVisible = taskVisible == View.VISIBLE ? View.GONE : View.VISIBLE;
                mDetailTaskDetailLl.setVisibility(afterTaskVisible);*/
                fold(DETAIL_TASK);
                break;
            case R.id.detail_task_camera_ll:
                //进入摄像机详情页面
                Intent videoIntent = new Intent(DailyPoliceDetailActivity.this, VideoDetailActivity.class);
                videoIntent.putExtra("cid", String.valueOf(mAlarmEntity.getCameraId()));
                startActivity(videoIntent);
                break;
            case R.id.detail_task_camera_address_ll:
                //进入设备地址的地图页面
                Long latitide = mAlarmEntity.getLatitide();
                Long longitude = mAlarmEntity.getLongitude();
                if (null != latitide && null != longitude) {
                    LatLng latLng = new LatLng(latitide, longitude);
                    Intent deviceShowIntent = new Intent(DailyPoliceDetailActivity.this, DeviceShowMapActivity.class);
                    deviceShowIntent.putExtra("latlng", latLng);
                    startActivity(deviceShowIntent);
                } else {
                    ToastUtils.showShort(R.string.hint_no_device_latlong);
                }
                break;
        }
    }

    private void fold(int type) {
        if (animating) return;
        AnimatorSet set = new AnimatorSet();
        ValueAnimator alphaAnim, heightAnim;
        if (type == DETAIL_PERSON) {
            if (personFold) {
                alphaAnim = ValueAnimator.ofFloat(0, 1.0f);
                heightAnim = ValueAnimator.ofFloat(personFoldHeight, 0f);
            } else {
                alphaAnim = ValueAnimator.ofFloat(1.0f, 0);
                heightAnim = ValueAnimator.ofFloat(0, personFoldHeight);
            }
        } else {
            if (taskFold) {
                alphaAnim = ValueAnimator.ofFloat(0, 1.0f);
                heightAnim = ValueAnimator.ofFloat(taskFoldHeight, 0f);
            } else {
                alphaAnim = ValueAnimator.ofFloat(1.0f, 0);
                heightAnim = ValueAnimator.ofFloat(0, taskFoldHeight);
            }
        }
        alphaAnim.setDuration(200);
        alphaAnim.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            switch (type) {
                case DETAIL_PERSON:
                    mDetailPersonDetailLl.setAlpha(value);
                    break;
                case DETAIL_TASK:
                    mDetailTaskDetailLl.setAlpha(value);
                    break;
            }
        });
        heightAnim.setDuration(300);
        heightAnim.addUpdateListener(animation -> {
            float value = -(float) animation.getAnimatedValue();
            switch (type) {
                case DETAIL_PERSON:
                    mDetailPersonDetailLl.setPadding(0, (int) value, 0, 0);
                    break;
                case DETAIL_TASK:
                    mDetailTaskDetailLl.setPadding(0, (int) value, 0, 0);
                    break;
            }
        });
        if (type == DETAIL_PERSON) {
            if (personFold) {
                set.play(heightAnim).before(alphaAnim);
                alphaAnim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        Drawable drawable = getResources().getDrawable(R.drawable.arrow_down);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        mDetailPersonDetailNavTv.setCompoundDrawables(null, null, drawable, null);
                        personFold = !personFold;
                        animating = false;
                    }
                });
            } else {
                set.play(alphaAnim).before(heightAnim);
                heightAnim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        Drawable drawable = getResources().getDrawable(R.drawable.arrow_up);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        mDetailPersonDetailNavTv.setCompoundDrawables(null, null, drawable, null);
                        personFold = !personFold;
                        animating = false;
                    }
                });
            }
        } else if (type == DETAIL_TASK) {
            if (taskFold) {
                set.play(heightAnim).before(alphaAnim);
                alphaAnim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        Drawable drawable = getResources().getDrawable(R.drawable.arrow_down);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        mDetailTaskNavTv.setCompoundDrawables(null, null, drawable, null);
                        taskFold = !taskFold;
                        animating = false;
                    }
                });
            } else {
                set.play(alphaAnim).before(heightAnim);
                heightAnim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        Drawable drawable = getResources().getDrawable(R.drawable.arrow_up);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        mDetailTaskNavTv.setCompoundDrawables(null, null, drawable, null);
                        taskFold = !taskFold;
                        animating = false;
                    }
                });
            }
        }

        set.start();
        animating = true;
    }

    @Override
    public void dealSuccess() {
        ToastUtils.showShort("处理成功");
        Intent intent = new Intent();
        intent.putExtra("position", mPosition);
        setResult(RESULT_OK, intent);
        EventBus.getDefault().post(new AlarmChangeBean(), EventBusTags.DAILY_ALARM_MARK_CHANGED);
        finish();
    }

    @Override
    public void collectSuccess() {
        mIsHasCollect = true;
        if (mIsCollect) {
            //改为取消收藏成功
            ToastUtils.showShort(R.string.collect_cancel_success);
            mHeadRightIv.setImageResource(R.drawable.collect_black_mark);
        } else {
            ToastUtils.showShort(R.string.collect_confirm_success);
            mHeadRightIv.setImageResource(R.drawable.collected_yellow_marked);
        }
        mIsCollect = !mIsCollect;
        AlarmChangeBean bean = new AlarmChangeBean();
        bean.setPosition(mPosition);
        bean.setCollectstatus(mIsCollect ? 1 : 0);
        EventBus.getDefault().post(bean, EventBusTags.DAILY_ALARM_MARK_CHANGED);
    }

    @Override
    public void collectFail() {
        if (mIsCollect) {
            ToastUtils.showShort(R.string.collect_cancel_fail);
        } else {
            ToastUtils.showShort(R.string.collect_confirm_fail);
        }
    }

    @Override
    public void onBackPressedSupport() {
        if (mIsHasCollect) {
            //证明收藏了或者取消收藏了
            Intent intent = new Intent();
            intent.putExtra("collect", mIsCollect);
            intent.putExtra("collect_position", mPosition);
            setResult(RESULT_OK, intent);
        }
        super.onBackPressedSupport();
    }

    public Bitmap getMarkerBitmap(int width, int height) {
        String loginName = SPUtils.getInstance().getString(Constants.CONFIG_LAST_USER_REAL_NAME);
        String loginPhone = SPUtils.getInstance().getString(Constants.CONFIG_LAST_USER_LOGIN_PHONE);
        String waterText = loginName + "\n" + loginPhone;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        TextPaint paint = new TextPaint();
        paint.setFakeBoldText(true);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        //让画出的图形是实心的
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#22FFFFFF"));  // 设置字体中间实体部分的颜色
        //设置字体大小
        paint.setTextSize(SizeUtils.sp2px(18));
        canvas.rotate(-45, width / 2, width / 2);
        String[] split = waterText.split("\n");
        float startY = (width - split.length * WaterMarkUtils.getTextHeight(paint) - (split.length - 1) * 4) / 2;
        for (int i = 0; i < split.length; i++) {
            String lineText = split[i];
            float textLength = getTextLength(paint, lineText);
            float startPos;
            if (textLength < width) {
                startPos = (width - textLength) / 2;
            } else {
                startPos = 0;
            }
            canvas.drawText(lineText, startPos, startY + i * (WaterMarkUtils.getTextHeight(paint) + 4), paint);
        }
        /*float textLength = getTextLength(paint, waterText);
        float startPos;
        if (textLength < width) {
            startPos = (width - textLength) / 2;
        } else {
            startPos = 0;
        }
        canvas.drawText(waterText, startPos, height / 2, paint);
        //让画出的图形是空心的
        paint.setStyle(Paint.Style.STROKE);
        //设置画出的线的 粗细程度
        paint.setStrokeWidth(SizeUtils.sp2px(1.1f));
        paint.setColor(Color.parseColor("#1A000000")); // 设置字体镂空部分的颜色
        canvas.drawText(waterText, startPos, height / 2, paint);*/
        return bitmap;
    }

}
