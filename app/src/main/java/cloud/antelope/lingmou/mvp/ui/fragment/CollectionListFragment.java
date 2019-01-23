package cloud.antelope.lingmou.mvp.ui.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;

import org.litepal.crud.DataSupport;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.di.component.DaggerCollectionListComponent;
import cloud.antelope.lingmou.di.module.CollectionListModule;
import cloud.antelope.lingmou.mvp.contract.CollectionListContract;
import cloud.antelope.lingmou.mvp.model.entity.CollectChangeEvent;
import cloud.antelope.lingmou.mvp.model.entity.CollectionListBean;
import cloud.antelope.lingmou.mvp.model.entity.CollectionListRequest;
import cloud.antelope.lingmou.mvp.model.entity.DetailCommonEntity;
import cloud.antelope.lingmou.mvp.presenter.CollectionListPresenter;
import cloud.antelope.lingmou.mvp.ui.activity.PictureDetailActivity;
import cloud.antelope.lingmou.mvp.ui.adapter.CollectionBodyAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.CollectionFaceAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.CollectionSnapshotAdapter;

public class CollectionListFragment extends BaseFragment<CollectionListPresenter> implements CollectionListContract.View, SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {
    public static final int ALL = 0;
    public static final int FACE = 2;
    public static final int BODY = 3;
    public static final int VIDEO = 1;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.srl)
    SwipeRefreshLayout srl;
    @BindView(R.id.rl_empty)
    RelativeLayout rlEmpty;
    private BaseQuickAdapter mAdapter;
    @Inject
    CollectionFaceAdapter mCollectionFaceAdapter;
    @Inject
    CollectionBodyAdapter mCollectionBodyAdapter;
    @Inject
    CollectionSnapshotAdapter mCollectionSnapshotAdapter;
    @Inject
    LoadMoreView mLoadMoreView;
    private boolean hasMore;
    int pageNum = 1;
    int pageSize = 20;
    Integer statusType;// 1：截图2：人脸3：人体
    private boolean needRefresh;
    private boolean visible;

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
        needRefresh = false;
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
    public void onSupportVisible() {
        super.onSupportVisible();
        visible = true;
        if (needRefresh) {
            onRefresh();
        }
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        visible = false;
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
    public void setupFragmentComponent(AppComponent appComponent) {
        DaggerCollectionListComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .collectionListModule(new CollectionListModule(this))
                .build()
                .inject(this);
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_deploy_list, container, false);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        statusType = getArguments().getInt("statusType");
        switch (statusType) {
            case FACE:
                mAdapter = mCollectionFaceAdapter;
                rv.setLayoutManager(new GridLayoutManager(getActivity(), 2));
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
                break;
            case BODY:
                mAdapter = mCollectionBodyAdapter;
                rv.setLayoutManager(new GridLayoutManager(getActivity(), 2));
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
                break;
            case VIDEO:
                mAdapter = mCollectionSnapshotAdapter;
                rv.setLayoutManager(new LinearLayoutManager(getActivity()));
                rv.addItemDecoration(new RecyclerView.ItemDecoration() {
                    @Override
                    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                        super.getItemOffsets(outRect, view, parent, state);
                        int position = parent.getChildAdapterPosition(view);
                        outRect.top = position == mAdapter.getItemCount() - 1 ? 0 : getResources().getDimensionPixelSize(R.dimen.dp16);
                        outRect.left = getResources().getDimensionPixelSize(R.dimen.dp16);
                        outRect.right = getResources().getDimensionPixelSize(R.dimen.dp16);
                    }
                });
                break;
        }
        rv.setAdapter(mAdapter);
        mAdapter.setOnLoadMoreListener(this, rv);
        srl.setOnRefreshListener(this);
        mAdapter.setOnLoadMoreListener(this, rv);
        mAdapter.setLoadMoreView(mLoadMoreView);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                CollectionListBean item = (CollectionListBean) adapter.getItem(position);
                Intent intent = new Intent(getActivity(), PictureDetailActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("fromCollect", true);
                ArrayList<DetailCommonEntity> list = getChangeData(adapter.getData());
                if (list != null) {
                    intent.putParcelableArrayListExtra("bean", list);
                    startActivity(intent);
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

    private ArrayList<DetailCommonEntity> getChangeData(List<CollectionListBean> collectionListBeanList) {
        if (null != collectionListBeanList) {
            ArrayList<DetailCommonEntity> list = new ArrayList<>();
            for (CollectionListBean bean : collectionListBeanList) {
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
                commonEntity.collected=true;
                commonEntity.id=bean.getFeatureId();
                list.add(commonEntity);
            }
            return list;
        }
        return null;
    }

    @Override
    public void setData(Object data) {

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
    public void showMessage(String message) {

    }

    @Override
    public void launchActivity(Intent intent) {

    }

    @Override
    public void killMyself() {

    }

    @Subscriber()
    public void modifyList(CollectChangeEvent event) {
        if (statusType == null || statusType == ALL) {
            needRefresh = true;
        } else {
            needRefresh = statusType == event.getFavoritesType();
        }
    }
}
