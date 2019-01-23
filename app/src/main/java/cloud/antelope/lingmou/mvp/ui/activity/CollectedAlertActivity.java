package cloud.antelope.lingmou.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.di.component.DaggerCollectedAlertComponent;
import cloud.antelope.lingmou.di.module.CollectedAlertModule;
import cloud.antelope.lingmou.mvp.contract.CollectedAlertContract;
import cloud.antelope.lingmou.mvp.presenter.CollectedAlertPresenter;
import cloud.antelope.lingmou.mvp.ui.adapter.CollectedAlertPagerAdapter;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class CollectedAlertActivity extends BaseActivity<CollectedAlertPresenter> implements CollectedAlertContract.View {

    @BindView(R.id.head_left_iv)
    ImageButton headLeftIv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.head_right_iv)
    ImageView headRightIv;
    @BindView(R.id.head_right_tv)
    TextView headRightTv;
    @BindView(R.id.stl)
    SlidingTabLayout stl;
    @BindView(R.id.vp)
    ViewPager vp;
    @Inject
    CollectedAlertPagerAdapter mAdapter;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerCollectedAlertComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .collectedAlertModule(new CollectedAlertModule(this, getSupportFragmentManager()))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_collected_alert; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @OnClick(R.id.head_left_iv)
    public void onClick(View view) {
        finish();
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        titleTv.setText(getString(R.string.collected_alert));
        vp.setAdapter(mAdapter);
        stl.setViewPager(vp);
        stl.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                vp.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}
