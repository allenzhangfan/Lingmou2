package cloud.antelope.lingmou.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.di.component.DaggerFocusedDevicesComponent;
import cloud.antelope.lingmou.di.module.FocusedDevicesModule;
import cloud.antelope.lingmou.mvp.contract.FocusedDevicesContract;
import cloud.antelope.lingmou.mvp.presenter.FocusedDevicesPresenter;
import cloud.antelope.lingmou.mvp.ui.fragment.CollectionFragment;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class FocusedDevicesActivity extends BaseActivity<FocusedDevicesPresenter> implements FocusedDevicesContract.View {

    @BindView(R.id.head_left_iv)
    ImageButton headLeftIv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.head_right_iv)
    ImageView headRightIv;
    @BindView(R.id.head_right_tv)
    TextView headRightTv;
    @BindView(R.id.fl)
    FrameLayout fl;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerFocusedDevicesComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .focusedDevicesModule(new FocusedDevicesModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_focused_devices; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        titleTv.setText("关注的设备");
        loadRootFragment(R.id.fl,new CollectionFragment());
    }

    @OnClick(R.id.head_left_iv)
    public void onClick(View view) {
        finish();
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

}
