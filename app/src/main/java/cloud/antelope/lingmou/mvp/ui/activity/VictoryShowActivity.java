package cloud.antelope.lingmou.mvp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cloud.antelope.lingmou.di.component.DaggerVictoryShowComponent;
import cloud.antelope.lingmou.di.module.VictoryShowModule;
import cloud.antelope.lingmou.mvp.contract.VictoryShowContract;
import cloud.antelope.lingmou.mvp.model.entity.ContentListEntity;
import cloud.antelope.lingmou.mvp.model.entity.NewsItemEntity;
import cloud.antelope.lingmou.mvp.presenter.VictoryShowPresenter;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.ui.adapter.VictoryAdapter;


import static com.jess.arms.utils.Preconditions.checkNotNull;


public class VictoryShowActivity extends BaseActivity<VictoryShowPresenter>
        implements VictoryShowContract.View,
        SwipeRefreshLayout.OnRefreshListener,
        BaseQuickAdapter.RequestLoadMoreListener  {

    @BindView(R.id.head_left_iv)
    ImageButton mHeadLeftIv;
    @BindView(R.id.title_tv)
    TextView mTitleTv;
    @BindView(R.id.victory_show_rycl)
    RecyclerView mRecyclerView;
    @BindView(R.id.victory_swipe)
    SwipeRefreshLayout mRefreshView;

    @Inject
    VictoryAdapter mAdapter;
    @Inject
    RecyclerView.LayoutManager mLayoutManager;
    @Inject
    RecyclerView.ItemAnimator mItemAnimator;
    @Inject
    LoadMoreView mLoadMoreView;

    private int page = 0;
    private static final int PAGE_SIZE = 10;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerVictoryShowComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .victoryShowModule(new VictoryShowModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_victory_show; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mTitleTv.setText(R.string.victory_show);

        mRefreshView.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        mRefreshView.setOnRefreshListener(this);

        mRecyclerView.setHasFixedSize(false);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(mItemAnimator);

        mAdapter.setOnLoadMoreListener(this, mRecyclerView);
        mAdapter.setLoadMoreView(mLoadMoreView);
        mRecyclerView.setAdapter(mAdapter);
        mRefreshView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRefreshView.setRefreshing(true);
                onRefresh();
            }
        }, 100);
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

    @Override
    public void onRefresh() {
        page = 0;
        mPresenter.getNewsList(page, PAGE_SIZE);
    }

    @Override
    public Activity getActivity() {
        return this;
    }


    @Override
    public void onGetNewsListSuccess(ContentListEntity<NewsItemEntity> contentList) {
        mRefreshView.setRefreshing(false);
        mAdapter.setEnableLoadMore(true);
        if (null != contentList) {
            List<NewsItemEntity> list = contentList.getList();
            if (page == 0) {
                mAdapter.setNewData(list);
            } else {
                mRefreshView.setEnabled(true);
                mAdapter.addData(list);
            }
            if (list.size() < PAGE_SIZE) {
                mAdapter.loadMoreEnd(false);
            } else {
                mAdapter.loadMoreComplete();
            }
        }
    }

    @Override
    public void onGetNewsListError(String errMsg) {
        mRefreshView.setRefreshing(false);
    }

    @Override
    public void onLoadMoreRequested() {
        if ((mAdapter.getData().size() % PAGE_SIZE) != 0) {
            mAdapter.loadMoreEnd(false);
            return;
        }
        mRefreshView.setEnabled(false);
        page = mAdapter.getData().size() / PAGE_SIZE;
        mPresenter.getNewsList(page, PAGE_SIZE);
    }
}
