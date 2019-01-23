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
import butterknife.OnClick;
import cloud.antelope.lingmou.di.component.DaggerHistoryWatchComponent;
import cloud.antelope.lingmou.di.module.HistoryWatchModule;
import cloud.antelope.lingmou.mvp.contract.HistoryWatchContract;
import cloud.antelope.lingmou.mvp.presenter.HistoryWatchPresenter;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.ui.fragment.CloudHistoryFragment;
import cloud.antelope.lingmou.mvp.ui.fragment.CollectionFragment;


import static com.jess.arms.utils.Preconditions.checkNotNull;


public class HistoryWatchActivity extends BaseActivity<HistoryWatchPresenter> implements HistoryWatchContract.View {
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
        DaggerHistoryWatchComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .historyWatchModule(new HistoryWatchModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_history_watch; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        titleTv.setText("历史观看");
        loadRootFragment(R.id.fl,new CloudHistoryFragment());
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
