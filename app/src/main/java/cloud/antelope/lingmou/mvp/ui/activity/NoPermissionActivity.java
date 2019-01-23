package cloud.antelope.lingmou.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.di.component.DaggerNoPermissionComponent;
import cloud.antelope.lingmou.di.module.NoPermissionModule;
import cloud.antelope.lingmou.mvp.contract.NoPermissionContract;
import cloud.antelope.lingmou.mvp.presenter.NoPermissionPresenter;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class NoPermissionActivity extends BaseActivity<NoPermissionPresenter> implements NoPermissionContract.View {

    @BindView(R.id.head_left_iv)
    ImageButton mHeadLeftIv;
    @BindView(R.id.title_tv)
    TextView mTitleTv;
    @BindView(R.id.no_permission_rl)
    RelativeLayout mNoPermissionRl;

    private String mTitleStr;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerNoPermissionComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .noPermissionModule(new NoPermissionModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_no_permission; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mTitleStr = getIntent().getStringExtra("title");
        mTitleTv.setText(mTitleStr);
        mNoPermissionRl.setVisibility(View.VISIBLE);
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

    @OnClick(R.id.head_left_iv)
    public void onViewClicked() {
        finish();
    }
}
