package cloud.antelope.lingmou.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.di.component.DaggerMyCollectionComponent;
import cloud.antelope.lingmou.di.module.MyCollectionModule;
import cloud.antelope.lingmou.mvp.contract.MyCollectionContract;
import cloud.antelope.lingmou.mvp.presenter.MyCollectionPresenter;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class MyCollectionActivity extends BaseActivity<MyCollectionPresenter> implements MyCollectionContract.View {
    @BindView(R.id.head_left_iv)
    ImageButton headLeftIv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.head_right_iv)
    ImageView headRightIv;
    @BindView(R.id.head_right_tv)
    TextView headRightTv;
    @BindView(R.id.empty_layout)
    LinearLayout emptyLayout;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.swipe_refresh_widget)
    SwipeRefreshLayout mRefreshView;
    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerMyCollectionComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .myCollectionModule(new MyCollectionModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_my_collection; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        titleTv.setText("我的收藏");
        mRefreshView.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
    }

    @Override
    public void showLoading(String message) {

    }

    @Override
    public void hideLoading() {

    }

    @OnClick(R.id.head_left_iv)
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

}
