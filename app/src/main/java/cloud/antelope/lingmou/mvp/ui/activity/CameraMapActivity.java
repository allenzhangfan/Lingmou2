package cloud.antelope.lingmou.mvp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.utils.SizeUtils;

import butterknife.BindView;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.transition.ChangeColor;
import cloud.antelope.lingmou.app.utils.transition.ChangePosition;
import cloud.antelope.lingmou.app.utils.transition.CommentEnterTransition;
import cloud.antelope.lingmou.app.utils.transition.ShareElemEnterRevealTransition;
import cloud.antelope.lingmou.app.utils.transition.ShareElemReturnChangePosition;
import cloud.antelope.lingmou.app.utils.transition.ShareElemReturnRevealTransition;
import cloud.antelope.lingmou.di.component.DaggerCameraMapComponent;
import cloud.antelope.lingmou.di.module.CameraMapModule;
import cloud.antelope.lingmou.mvp.contract.CameraMapContract;
import cloud.antelope.lingmou.mvp.presenter.CameraMapPresenter;
import cloud.antelope.lingmou.mvp.ui.fragment.CloudMapFragment;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class CameraMapActivity extends BaseActivity<CameraMapPresenter> implements CameraMapContract.View {

    @BindView(R.id.view_bar)
    View viewBar;
    @BindView(R.id.head_left_iv)
    ImageButton headLeftIv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.head_right_iv)
    ImageView headRightIv;
    @BindView(R.id.head_right_tv)
    TextView headRightTv;
    @BindView(R.id.ll_title)
    LinearLayout llTitle;
    @BindView(R.id.fl_container)
    FrameLayout flContainer;
    @BindView(R.id.rv_resources)
    RecyclerView rvResources;
    @BindView(R.id.ll_root)
    LinearLayout llRoot;
    @BindView(R.id.ll_sheet)
    LinearLayout llSheet;

    @BindView(R.id.include_resources)
    CoordinatorLayout clResources;

    private boolean canSlidesUp;
    private int titleHeight;

    private BottomSheetListener bottomSheetListener;
    private BottomSheetBehavior mBottomSheetBehavior;

    public RecyclerView getRvResources() {
        return rvResources;
    }

    public CoordinatorLayout getClResources() {
        return clResources;
    }

    public void setBottomSheetListener(BottomSheetListener bottomSheetListener) {
        this.bottomSheetListener = bottomSheetListener;
    }

    public BottomSheetBehavior getmBottomSheetBehavior() {
        return mBottomSheetBehavior;
    }


    public void setCanSlidesUp(boolean canSlidesUp) {
        this.canSlidesUp = canSlidesUp;
    }

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        DaggerCameraMapComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .cameraMapModule(new CameraMapModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_camera_map; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        loadRootFragment(R.id.fl_container, new CloudMapFragment());
        initBottomSheet();
        titleTv.setText("设备地图");
//        enterAnim();
//        returnAnim();
    }

    /**
     * 进入动画
     */
    private void enterAnim() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(new CommentEnterTransition(this, llTitle));
            TransitionSet transitionSet = new TransitionSet();

            Transition changePos = new ChangePosition();
            changePos.setDuration(300);
            changePos.addTarget(R.id.fl_container);
            transitionSet.addTransition(changePos);

            Transition revealTransition = new ShareElemEnterRevealTransition(flContainer);
            transitionSet.addTransition(revealTransition);
            revealTransition.addTarget(R.id.fl_container);
            revealTransition.setInterpolator(new FastOutSlowInInterpolator());
            revealTransition.setDuration(300);

            ChangeColor changeColor = new ChangeColor(getResources().getColor(R.color.yellow_ffa000), getResources().getColor(R.color.white));
            changeColor.addTarget(R.id.fl_container);
            changeColor.setDuration(350);

            transitionSet.addTransition(changeColor);

            transitionSet.setDuration(900);
            getWindow().setSharedElementEnterTransition(transitionSet);
        }
    }

    /**
     * 返回动画
     */
    private void returnAnim() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TransitionSet transitionSet = new TransitionSet();

            Transition changePos = new ShareElemReturnChangePosition();
            changePos.addTarget(R.id.fl_container);
            transitionSet.addTransition(changePos);

            ChangeColor changeColor = new ChangeColor(getResources().getColor(R.color.white), getResources().getColor(R.color.yellow_ffa000));
            changeColor.addTarget(R.id.fl_container);
            transitionSet.addTransition(changeColor);


            Transition revealTransition = new ShareElemReturnRevealTransition(flContainer);
            revealTransition.addTarget(R.id.fl_container);
            transitionSet.addTransition(revealTransition);

            transitionSet.setDuration(900);
            getWindow().setSharedElementReturnTransition(transitionSet);
        }
    }

    private void initBottomSheet() {
        titleHeight = llTitle.getMeasuredHeight();
        mBottomSheetBehavior = BottomSheetBehavior.from(llSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (bottomSheetListener != null) {
                    bottomSheetListener.onStateChanged(bottomSheet, newState);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (bottomSheetListener != null) {
                    bottomSheetListener.onSlide(bottomSheet, slideOffset);
                }
                if (canSlidesUp) {
                    float point = 1.0f - (titleHeight * 1.0f / (SizeUtils.getScreenHeight() - titleHeight - mBottomSheetBehavior.getPeekHeight()));
                    if (slideOffset >= point) {
                        float percent = -1 - (point - slideOffset) * 1.0f / (1 - point);
                        llTitle.setTranslationY((int) (percent * titleHeight));
                    } else {
                        llTitle.setTranslationY(-titleHeight);
                    }
                }
            }
        });
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
    public Activity getActivity() {
        return this;
    }

    @Override
    public void onBackPressedSupport() {
        super.onBackPressedSupport();
    }

    public interface BottomSheetListener {
        void onStateChanged(View bottomSheet, int newState);

        void onSlide(View bottomSheet, float slideOffset);
    }

    @OnClick(R.id.head_left_iv)
    public void onClick(View view) {
        finish();
    }
}
