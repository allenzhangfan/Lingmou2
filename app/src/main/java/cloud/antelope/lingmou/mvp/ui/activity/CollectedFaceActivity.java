package cloud.antelope.lingmou.mvp.ui.activity;

import android.app.Activity;
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
import com.lingdanet.safeguard.common.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.di.component.DaggerCollectedFaceComponent;
import cloud.antelope.lingmou.di.module.CollectedFaceModule;
import cloud.antelope.lingmou.mvp.contract.CollectedFaceContract;
import cloud.antelope.lingmou.mvp.model.entity.CollectionListBean;
import cloud.antelope.lingmou.mvp.model.entity.CollectionListRequest;
import cloud.antelope.lingmou.mvp.model.entity.DetailCommonEntity;
import cloud.antelope.lingmou.mvp.presenter.CollectedFacePresenter;
import cloud.antelope.lingmou.mvp.ui.adapter.CollectedFaceAdapter;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class CollectedFaceActivity extends BaseActivity<CollectedFacePresenter> implements CollectedFaceContract.View, BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    @Inject
    CollectedFaceAdapter mAdapter;
    @Inject
    LoadMoreView mLoadMoreView;
    @BindView(R.id.head_left_iv)
    ImageButton headLeftIv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.head_right_iv)
    ImageView headRightIv;
    @BindView(R.id.head_right_tv)
    TextView headRightTv;
    @BindView(R.id.iv)
    ImageView iv;
    @BindView(R.id.rl_empty)
    RelativeLayout rlEmpty;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.srl)
    SwipeRefreshLayout srl;
    private boolean hasMore;
    int pageNum = 1;
    int pageSize = 20;
    Integer statusType = 2;// 1：截图2：人脸3：人体
    private boolean fromNewDeployMissionActivity;

    @Override
    public void onRefresh() {
        pageNum = 1;
        CollectionListRequest request = new CollectionListRequest();
        request.setPageNo(pageNum);
        if (statusType != null && statusType == 0) {
            statusType = null;
        }
        request.setFavoritesType(statusType);
        mPresenter.getList(request);
    }

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerCollectedFaceComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .collectedFaceModule(new CollectedFaceModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_collected_face; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        titleTv.setText("收藏的人脸");
        fromNewDeployMissionActivity = getIntent().getBooleanExtra("fromNewDeployMissionActivity", false);
        rv.setLayoutManager(new GridLayoutManager(CollectedFaceActivity.this, 2));
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
                CollectionListBean item = (CollectionListBean) adapter.getItem(position);
                if (fromNewDeployMissionActivity) {
                    Intent intent = new Intent();
                    intent.putExtra("facePath", item.getFaceImgUrl());
                    setResult(RESULT_OK, intent);
                    finish();
                }
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
    public Activity getActivity() {
        return this;
    }

    private ArrayList<DetailCommonEntity> getChangeData(CollectionListBean bean) {
        if (null != bean) {
            ArrayList<DetailCommonEntity> list = new ArrayList<>();
            DetailCommonEntity commonEntity = new DetailCommonEntity();
            commonEntity.deviceName = bean.getDeviceName();
            commonEntity.endTime = Long.parseLong(bean.getCreateTime());
            commonEntity.srcImg = bean.getImageUrl();
            commonEntity.collectId = bean.getId();
            commonEntity.cameraId = bean.getDeviceId();
            commonEntity.cameraName = bean.getDeviceName();
            commonEntity.faceImg = bean.getFaceImgUrl();
            commonEntity.captureTime = Long.parseLong(bean.getCreateTime());
            commonEntity.favoritesType = bean.getFavoritesType();
            commonEntity.point = bean.getFaceRect();
            list.add(commonEntity);
            return list;
        }
        return null;
    }

    @Override
    public void showList(List<CollectionListBean> list) {
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
    public void showMore(List<CollectionListBean> list) {
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
            CollectionListRequest request = new CollectionListRequest();
            request.setPageNo(pageNum);
            request.setFavoritesType(statusType);
            mPresenter.getList(request);
        }
    }

    @Override
    public void showLoading(String message) {
        if (srl != null)
            srl.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        if (srl != null)
            srl.setRefreshing(false);
    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ToastUtils.showShort(message);
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
