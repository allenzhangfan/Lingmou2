package cloud.antelope.lingmou.mvp.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.http.imageloader.glide.ImageConfigImpl;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.modle.LoadingDialog;
import com.lingdanet.safeguard.common.utils.TimeUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.di.component.DaggerAlertDetailComponent;
import cloud.antelope.lingmou.di.module.AlertDetailModule;
import cloud.antelope.lingmou.mvp.contract.AlertDetailContract;
import cloud.antelope.lingmou.mvp.model.entity.DailyPoliceAlarmEntity;
import cloud.antelope.lingmou.mvp.model.entity.UpdateInfo;
import cloud.antelope.lingmou.mvp.presenter.AlertDetailPresenter;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class AlertDetailActivity extends BaseActivity<AlertDetailPresenter> implements AlertDetailContract.View {

    @BindView(R.id.head_left_iv)
    ImageButton headLeftIv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.head_right_iv)
    ImageView headRightIv;
    @BindView(R.id.head_right_tv)
    TextView headRightTv;
    @BindView(R.id.iv_pic)
    ImageView ivPic;
    @BindView(R.id.tv_mission_detail)
    TextView tvMissionDetail;
    @BindView(R.id.tv_mission_name)
    TextView tvMissionName;
    @BindView(R.id.tv_mission_status)
    TextView tvMissionStatus;
    @BindView(R.id.tv_capture_device)
    TextView tvCaptureDevice;
    @BindView(R.id.tv_device_address)
    TextView tvDeviceAddress;
    @BindView(R.id.tv_snapshot_time)
    TextView tvSnapshotTime;
    @BindView(R.id.ll_fold)
    LinearLayout llFold;
    @BindView(R.id.et_mission_remark)
    EditText etMissionRemark;
    @BindView(R.id.tv_invalid)
    TextView tvInvalid;
    @BindView(R.id.tv_valid)
    TextView tvValid;
    private boolean animating;
    private boolean fold;
    private int foldHeight;
    @Inject
    LoadingDialog mLoadingDialog;
    private DailyPoliceAlarmEntity item;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerAlertDetailComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .alertDetailModule(new AlertDetailModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_alert_detail; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        headRightIv.setVisibility(View.VISIBLE);
        headRightIv.setImageResource(R.drawable.collect_black_mark);
        titleTv.setText(R.string.alarm_detail_info);
        llFold.measure(0, 0);
        foldHeight = llFold.getMeasuredHeight();
        item = (DailyPoliceAlarmEntity) getIntent().getSerializableExtra("item");
        updateInfo(item);
    }

    private void updateInfo(DailyPoliceAlarmEntity entity) {
        if (null != entity) {
            ArmsUtils.obtainAppComponentFromContext(this).imageLoader().loadImage(this, ImageConfigImpl
                    .builder()
                    .cacheStrategy(0)
                    .url(entity.getFacePath())
                    .placeholder(R.drawable.about_stroke_rect_gray)
                    .imageView(ivPic).build());

            tvMissionName.setText(entity.getTaskName());

        }
    }

    @OnClick({R.id.head_left_iv, R.id.tv_mission_detail})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.head_left_iv:
                finish();
                break;
            case R.id.tv_mission_detail:
                fold();
                break;
        }
    }

    private void fold() {
        if (animating) return;
        AnimatorSet set = new AnimatorSet();
        ValueAnimator alphaAnim, heightAnim;
        if (fold) {
            alphaAnim = ValueAnimator.ofFloat(0, 1.0f);
            heightAnim = ValueAnimator.ofFloat(foldHeight, 0f);
        } else {
            alphaAnim = ValueAnimator.ofFloat(1.0f, 0);
            heightAnim = ValueAnimator.ofFloat(0, foldHeight);
        }
        alphaAnim.setDuration(200);
        alphaAnim.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            llFold.setAlpha(value);
        });
        heightAnim.setDuration(300);
        heightAnim.addUpdateListener(animation -> {
            float value = -(float) animation.getAnimatedValue();
            llFold.setPadding(0, (int) value, 0, 0);
        });
        if (fold) {
            set.play(heightAnim).before(alphaAnim);
            alphaAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    Drawable drawable = getResources().getDrawable(R.drawable.arrow_down);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    tvMissionDetail.setCompoundDrawables(null, null, drawable, null);
                    fold = !fold;
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
                    tvMissionDetail.setCompoundDrawables(null, null, drawable, null);
                    fold = !fold;
                    animating = false;
                }
            });
        }
        set.start();
        animating = true;
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

}
