package cloud.antelope.lingmou.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.utils.SizeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.BarViewUtils;
import cloud.antelope.lingmou.di.component.DaggerDeviceMapComponent;
import cloud.antelope.lingmou.di.module.DeviceMapModule;
import cloud.antelope.lingmou.mvp.contract.DeviceMapContract;
import cloud.antelope.lingmou.mvp.presenter.DeviceMapPresenter;
import cloud.antelope.lingmou.mvp.ui.fragment.CloudMapFragment;
import cloud.antelope.lingmou.mvp.ui.fragment.DailyPoliceFragment;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class DeviceMapActivity extends BaseActivity<DeviceMapPresenter> implements DeviceMapContract.View {

    @BindView(R.id.head_left_iv)
    ImageButton headLeftIv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.head_right_iv)
    ImageView headRightIv;
    @BindView(R.id.head_right_tv)
    TextView headRightTv;
    @BindView(R.id.fl_container)
    FrameLayout flContainer;
    @BindView(R.id.rl_slid)
    RelativeLayout rlSlid;
    @BindView(R.id.rv_resources)
    RecyclerView rvResources;
    @BindView(R.id.ll_sheet)
    LinearLayout llSheet;
    @BindView(R.id.include_resources)
    CoordinatorLayout clResources;
    @BindView(R.id.view_overlay)
    View viewOverlay;
    @BindView(R.id.view_bar)
    View viewBar;

    BottomSheetListener bottomSheetListener;
    boolean canSlidesUp;
    private BottomSheetBehavior mBottomSheetBehavior;
    public void setBottomSheetListener(BottomSheetListener bottomSheetListener) {
        this.bottomSheetListener = bottomSheetListener;
    }
    public CoordinatorLayout getClResources() {
        return clResources;
    }
    public BottomSheetBehavior getmBottomSheetBehavior() {
        return mBottomSheetBehavior;
    }
    public RecyclerView getRvResources() {
        return rvResources;
    }
    public void setCanSlidesUp(boolean canSlidesUp) {
        this.canSlidesUp = canSlidesUp;
    }

    public View getViewOverlay() {
        return viewOverlay;
    }

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerDeviceMapComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .deviceMapModule(new DeviceMapModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_device_map; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        viewBar.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, BarViewUtils.getStatusBarHeight()));
        loadRootFragment(R.id.fl_container, CloudMapFragment.newInstance());
        titleTv.setText("设备地图");
        viewOverlay.setAlpha(0);
        initBottomSheet();
    }
    private void initBottomSheet() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) clResources.getLayoutParams();
        layoutParams.setMargins(0, getResources().getDimensionPixelSize(R.dimen.dp72), 0, 0);
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
    public void onBackPressedSupport() {
        if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            CloudMapFragment.getInstance().dismissRecyclerView();
            return ;
        }
        super.onBackPressedSupport();
    }

    @OnClick({R.id.head_left_iv})
    public void onClick(View view) {
        finish();
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

    public interface BottomSheetListener {
        void onStateChanged(View bottomSheet, int newState);

        void onSlide(View bottomSheet, float slideOffset);
    }
}
