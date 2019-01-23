package cloud.antelope.lingmou.mvp.ui.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.di.component.DaggerTrackingPersonComponent;
import cloud.antelope.lingmou.di.module.TrackingPersonModule;
import cloud.antelope.lingmou.mvp.contract.TrackingPersonContract;
import cloud.antelope.lingmou.mvp.model.entity.TrackingPersonListBean;
import cloud.antelope.lingmou.mvp.model.entity.TrackingPersonRequest;
import cloud.antelope.lingmou.mvp.presenter.TrakingPersonPresenter;
import cloud.antelope.lingmou.mvp.ui.adapter.TrackingPersonAdapter;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class TrackingPersonActivity extends BaseActivity<TrakingPersonPresenter> implements TrackingPersonContract.View, SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {

    @BindView(R.id.head_left_iv)
    ImageButton headLeftIv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.head_right_iv)
    ImageView headRightIv;
    @BindView(R.id.head_right_tv)
    TextView headRightTv;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.srl)
    SwipeRefreshLayout srl;
    @BindView(R.id.rl_empty)
    RelativeLayout rlEmpty;
    @Inject
    TrackingPersonAdapter mAdapter;
    @Inject
    LoadMoreView mLoadMoreView;
    private boolean hasMore;
    int pageNum = 1;
    int pageSize = 20;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerTrackingPersonComponent
                .builder()
                .appComponent(appComponent)
                .trackingPersonModule(new TrackingPersonModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_traking_person; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        titleTv.setText(getString(R.string.tracking_people));
        rv.setLayoutManager(new GridLayoutManager(TrackingPersonActivity.this, 2));
        rv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int position = parent.getChildAdapterPosition(view);
                outRect.top = getResources().getDimensionPixelSize(R.dimen.dp8);
                outRect.left = position % 2 == 0 ? getResources().getDimensionPixelSize(R.dimen.dp8) : getResources().getDimensionPixelSize(R.dimen.dp3_5);
                outRect.right = position % 2 == 0 ? getResources().getDimensionPixelSize(R.dimen.dp3_5) : getResources().getDimensionPixelSize(R.dimen.dp8);
            }
        });
        rv.setAdapter(mAdapter);
        mAdapter.setOnLoadMoreListener(this, rv);
        srl.setOnRefreshListener(this);
        mAdapter.setOnLoadMoreListener(this, rv);
        mAdapter.setLoadMoreView(mLoadMoreView);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(TrackingPersonActivity.this, DeployMissionDetailActivity.class);
                intent.putExtra("mission_id", mAdapter.getData().get(position).getId());
                startActivity(intent);
            }
        });
        srl.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        srl.postDelayed(() ->
                        onRefresh()
                , 100);
    }

    @OnClick(R.id.head_left_iv)
    public void onClick(View view) {
        finish();
    }

    @Override
    public void onRefresh() {
        pageNum = 1;
        TrackingPersonRequest request = new TrackingPersonRequest();
        request.setPageNo(pageNum);
        mPresenter.getList(request);
//        List<CollectionListBean> list = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            CollectionListBean searchResponse = new CollectionListBean();
//            list.add(searchResponse);
//        }
//        showList(list);
    }

    @Override
    public void showList(List<TrackingPersonListBean> list) {
        mAdapter.setNewData(list);
        if (list == null || list.isEmpty()) {
            hasMore = false;
            rlEmpty.setVisibility(View.VISIBLE);
        } else {
            rlEmpty.setVisibility(View.GONE);
            hasMore = list.size() >= pageSize;
            if (!hasMore) {
                mAdapter.loadMoreEnd(false);//加载完毕
            } else {
                mAdapter.loadMoreComplete();//加载中
            }
        }
    }

    @Override
    public void showMore(List<TrackingPersonListBean> list) {
        hasMore = list.size() >= pageSize;
        mAdapter.addData(list);
        if (!hasMore) {
            mAdapter.loadMoreEnd(false);//加载完毕
        } else {
            mAdapter.loadMoreComplete();//加载中
        }
    }

    @Override
    public void onError() {
        if (pageNum > 1) {
            pageNum--;
            if (mAdapter != null) mAdapter.loadMoreFail();
        } else {

        }
    }

    @Override
    public void onLoadMoreRequested() {
        if (hasMore) {
            pageNum++;
            TrackingPersonRequest request = new TrackingPersonRequest();
            request.setPageNo(pageNum);
            mPresenter.getList(request);
        }
    }

    @Override
    public void showLoading(String message) {
        srl.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        srl.setRefreshing(false);
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
