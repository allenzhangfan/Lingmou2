package cloud.antelope.lingmou.mvp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.lingdanet.safeguard.common.modle.SweetDialog;
import com.lingdanet.safeguard.common.utils.ToastUtils;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.LogUploadUtil;
import cloud.antelope.lingmou.di.component.DaggerDeployListComponent;
import cloud.antelope.lingmou.di.module.DeployListModule;
import cloud.antelope.lingmou.mvp.contract.DeployListContract;
import cloud.antelope.lingmou.mvp.model.entity.DeployListRequest;
import cloud.antelope.lingmou.mvp.model.entity.DeployResponse;
import cloud.antelope.lingmou.mvp.model.entity.LogUploadRequest;
import cloud.antelope.lingmou.mvp.model.entity.ModifyDeployListEvent;
import cloud.antelope.lingmou.mvp.model.entity.SearchFragment;
import cloud.antelope.lingmou.mvp.model.entity.StartOrPauseDeployMissionRequest;
import cloud.antelope.lingmou.mvp.presenter.DeployListPresenter;
import cloud.antelope.lingmou.mvp.ui.activity.DeployMissionDetailActivity;
import cloud.antelope.lingmou.mvp.ui.adapter.DeployControlAdapter;

public class DeployListFragment extends BaseFragment<DeployListPresenter> implements DeployListContract.View, SearchFragment, SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {
    public static final int ALL = 4;
    public static final int PAUSED = 0;
    public static final int UNDERWAY = 1;
    public static final int NOT_RUN = 2;
    public static final int EXPIRED = 3;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.srl)
    SwipeRefreshLayout srl;
    @BindView(R.id.rl_empty)
    RelativeLayout rlEmpty;

    @Inject
    DeployControlAdapter mAdapter;
    @Inject
    LoadMoreView mLoadMoreView;
    private boolean hasMore;
    int pageNum = 1;
    int pageSize = 20;
    Integer statusType;//任务状态<br />0：暂停<br />1：运行中<br />2：未运行<br />3：已过期 4:全部
    boolean needRefresh;
    String keyword;
    private boolean visible;
    private boolean fromSearch;


    @Override
    public void onRefresh() {
        pageNum = 1;
        DeployListRequest request = new DeployListRequest();
        request.setPageNo(pageNum);
        request.setStatusType(statusType);
        request.setName(keyword);
        mPresenter.getList(request);
        needRefresh = false;
    }

    @Override
    public void showList(List<DeployResponse.ListBean> list) {
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
    public void showMore(List<DeployResponse.ListBean> list) {
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
        if (needRefresh) {
            onRefresh();
        }
        visible = true;
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        visible = false;
    }

    @Override
    public void onDeleteSuccess(int position) {
        LogUploadUtil.uploadLog(new LogUploadRequest(107500,107502,String.format("删除人员追踪任务【%s】",mAdapter.getData().get(position).getName())));
        mAdapter.remove(position);
    }

    @Override
    public void onModifySuccess(int position, int newStatus) {
        EventBus.getDefault().post(new ModifyDeployListEvent());
        LogUploadUtil.uploadLog(new LogUploadRequest(107500,107503,String.format("开启/暂停人员追踪任务【%s】",mAdapter.getData().get(position).getName())));
        if (statusType == ALL) {
            mAdapter.getData().get(position).setTaskStatus(newStatus);
            mAdapter.notifyItemChanged(position);
            needRefresh = false;
        }else {
            onRefresh();
        }
        ToastUtils.showShort("操作成功！");
    }

    @Override
    public void onLoadMoreRequested() {
        if (hasMore) {
            pageNum++;
            DeployListRequest request = new DeployListRequest();
            request.setPageNo(pageNum);
            request.setStatusType(statusType);
            request.setName(keyword);
            mPresenter.getList(request);
        }
    }

    @Override
    public void setupFragmentComponent(AppComponent appComponent) {
        DaggerDeployListComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .deployListModule(new DeployListModule(this))
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
        fromSearch = getArguments().getBoolean("fromSearch", false);
        if (fromSearch) {
            srl.setEnabled(false);
        }
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setAdapter(mAdapter);
        mAdapter.setOnLoadMoreListener(this, rv);
        srl.setOnRefreshListener(this);
        mAdapter.setOnLoadMoreListener(this, rv);
        mAdapter.setLoadMoreView(mLoadMoreView);
        mAdapter.setOnClickListener(new DeployControlAdapter.OnClickListener() {
            @Override
            public void onClickItem(DeployResponse.ListBean entity) {
                Intent intent = new Intent(getActivity(), DeployMissionDetailActivity.class);
                intent.putExtra("mission_id", entity.getId());
                startActivity(intent);
            }

            @Override
            public void onClickLeft(DeployResponse.ListBean entity, int position) {
                switch (entity.getTaskStatus()) {
                    case PAUSED:
                        mPresenter.startOrPauseMission(new StartOrPauseDeployMissionRequest(entity.getId(), "1"), position);
                        break;
                    case UNDERWAY:
                        mPresenter.startOrPauseMission(new StartOrPauseDeployMissionRequest(entity.getId(), "0"), position);
                        break;
                    case NOT_RUN:
                        Intent intent1 = new Intent(getActivity(), DeployMissionDetailActivity.class);
                        intent1.putExtra("mission_id", entity.getId());
                        intent1.putExtra("state", NOT_RUN);
                        startActivity(intent1);
                        break;
                    case EXPIRED:
                        Intent intent2 = new Intent(getActivity(), DeployMissionDetailActivity.class);
                        intent2.putExtra("mission_id", entity.getId());
                        intent2.putExtra("state", EXPIRED);
                        startActivity(intent2);
                        break;
                }
            }

            @Override
            public void onClickRight(DeployResponse.ListBean entity, int position) {
                SweetDialog dialog = new SweetDialog(getActivity());
                dialog.setTitle(getString(R.string.sure_delete_title));
                dialog.setPositiveListener((view) -> {
                    dialog.dismiss();
                    mPresenter.deleteMission(entity.getId(), position);
                });
                dialog.setNegativeListener((view) -> {
                    dialog.dismiss();
                });
                dialog.show();
            }
        });
        srl.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);

        if (!fromSearch) {
            srl.postDelayed(() ->
                            onRefresh()
                    , 100);
        }
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
    public void modifyList(ModifyDeployListEvent event) {
        needRefresh = true;
    }

    @Override
    public void onSearch(String keyword) {
        this.keyword = keyword;
        if (visible) {
            onRefresh();
        }
    }
}
