package cloud.antelope.lingmou.mvp.ui.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.Calendar;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.FormatUtils;
import cloud.antelope.lingmou.app.utils.LogUploadUtil;
import cloud.antelope.lingmou.di.component.DaggerDeployControlComponent;
import cloud.antelope.lingmou.di.module.DeployControlModule;
import cloud.antelope.lingmou.mvp.contract.DeployControlContract;
import cloud.antelope.lingmou.mvp.model.entity.LogUploadRequest;
import cloud.antelope.lingmou.mvp.presenter.DeployControlPresenter;
import cloud.antelope.lingmou.mvp.ui.adapter.DeployPagerAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.LmDatePickDialog;
import cloud.antelope.lingmou.mvp.ui.widget.LmTimePickerDialog;

import static cloud.antelope.lingmou.mvp.ui.activity.NewDeployMissionActivity.END_TIME;
import static cloud.antelope.lingmou.mvp.ui.activity.NewDeployMissionActivity.START_TIME;
import static com.jess.arms.utils.Preconditions.checkNotNull;


public class DeployControlActivity extends BaseActivity<DeployControlPresenter> implements DeployControlContract.View {

    @BindView(R.id.head_left_iv)
    ImageButton headLeftIv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.iv_add)
    ImageView ivAdd;
    @BindView(R.id.tv_search)
    TextView tvSearch;
    @Inject
    DeployPagerAdapter mAdapter;
    @BindView(R.id.stl)
    SlidingTabLayout stl;
    @BindView(R.id.vp)
    ViewPager vp;
    private LmDatePickDialog lmDatePickDialog;


    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerDeployControlComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .deployControlModule(new DeployControlModule(this, getSupportFragmentManager()))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_deploy_control; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        if (getIntent() != null && getIntent().getBooleanExtra("fromMine", false)) {
            titleTv.setText(getText(R.string.created_task));
            ivAdd.setVisibility(View.GONE);
        }
        LogUploadUtil.uploadLog(new LogUploadRequest(107500, 107599, "进入人员追踪界面"));
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

    @OnClick({R.id.iv_add,  R.id.tv_search, R.id.head_left_iv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.head_left_iv:
                finish();
                break;
            case R.id.iv_add:
                MobclickAgent.onEvent(DeployControlActivity.this, "newLinkongTask");
                startActivity(new Intent(DeployControlActivity.this, NewDeployMissionActivity.class));
                break;
            /*case R.id.ll_filter:
                if (rightOpen) {
                    dl.closeDrawer(llRight);
                } else {
                    dl.openDrawer(llRight);
//                    tvFilter.setTextColor(getResources().getColor(R.color.yellow_ff8f00));
//                    Drawable drawable = getResources().getDrawable(R.drawable.filter_orange);
//                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//                    tvFilter.setCompoundDrawables(drawable, null, null, null);
                }
                break;*/
            case R.id.tv_search:
                ActivityOptions activityOptions = null;
                Intent intent = new Intent(DeployControlActivity.this, CloudSearchActivity.class);
                intent.putExtra("type", "deploy");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    activityOptions = ActivityOptions.makeSceneTransitionAnimation(DeployControlActivity.this, tvSearch, "searchText");
                    startActivity(intent, activityOptions.toBundle());
                } else {
                    startActivity(intent);
                }
                break;
        }
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
