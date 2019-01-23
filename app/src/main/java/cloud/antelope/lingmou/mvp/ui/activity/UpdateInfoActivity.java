package cloud.antelope.lingmou.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.utils.AppUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.PackageUtil;
import cloud.antelope.lingmou.di.component.DaggerUpdateInfoComponent;
import cloud.antelope.lingmou.di.module.UpdateInfoModule;
import cloud.antelope.lingmou.mvp.contract.UpdateInfoContract;
import cloud.antelope.lingmou.mvp.presenter.UpdateInfoPresenter;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class UpdateInfoActivity extends BaseActivity<UpdateInfoPresenter> implements UpdateInfoContract.View {

    @BindView(R.id.head_left_iv)
    ImageButton headLeftIv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.head_right_iv)
    ImageView headRightIv;
    @BindView(R.id.head_right_tv)
    TextView headRightTv;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_content)
    TextView tvContent;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerUpdateInfoComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .updateInfoModule(new UpdateInfoModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_update_info; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        titleTv.setText(R.string.update_info);
        tvTitle.setText(String.format("智慧云眼 v%s主要更新", AppUtils.getAppVersionName()));
    }

    @Override
    public void showLoading(String message) {

    }

    @Override
    public void hideLoading() {

    }

    @OnClick({R.id.head_left_iv})
    public void onClick(View v) {
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

}
