package cloud.antelope.lingmou.mvp.ui.fragment;

import android.content.Intent;
import android.graphics.Rect;
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
import com.umeng.analytics.MobclickAgent;

import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.EventBusTags;
import cloud.antelope.lingmou.di.component.DaggerCollectedAlertListComponent;
import cloud.antelope.lingmou.di.module.CollectedAlertListModule;
import cloud.antelope.lingmou.mvp.contract.CollectedAlertListContract;
import cloud.antelope.lingmou.mvp.model.entity.AlarmChangeBean;
import cloud.antelope.lingmou.mvp.model.entity.DailyPoliceAlarmEntity;
import cloud.antelope.lingmou.mvp.presenter.CollectedAlertListPresenter;
import cloud.antelope.lingmou.mvp.ui.activity.DailyPoliceDetailActivity;
import cloud.antelope.lingmou.mvp.ui.adapter.DailyPoliceAdapter;

public class CollectedAlertListFragment extends BaseFragment<CollectedAlertListPresenter> implements CollectedAlertListContract.View, SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {
    public static final int ALL = 5;
    public static final int VALID = 2;
    public static final int INVALID = 3;
    public static final int TODO = 1;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.srl)
    SwipeRefreshLayout srl;
    @BindView(R.id.rl_empty)
    RelativeLayout rlEmpty;
    @Inject
    DailyPoliceAdapter mAdapter;
    @Inject
    LoadMoreView mLoadMoreView;
    private boolean hasMore;
    int pageNum = 1;
    int pageSize = 20;
    private Integer mAlarmScope = 2;//查询范围<br />1：全部<br />2：收藏
    private Integer mAlarmTaskType = null;//布控任务类型101501:重点人员布控101502:外来人员布控101503:魅影报警101504:一体机报警101505:临控报警
    private Integer mAlarmOperationType = null;//告警状态：5：全部1：待处理2：有效3：无效4：已处理

    @Override
    public void onRefresh() {
        pageNum = 1;
        mPresenter.getList(pageNum, pageSize, null, mAlarmTaskType, mAlarmOperationType, mAlarmScope, null, null);
    }

    @Override
    public void showList(List<DailyPoliceAlarmEntity> list) {
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
    public void showMore(List<DailyPoliceAlarmEntity> list) {
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
            mPresenter.getList(pageNum, pageSize, null, mAlarmTaskType, mAlarmOperationType, mAlarmScope, null, null);
        }
    }

    @Override
    public void setupFragmentComponent(AppComponent appComponent) {
        DaggerCollectedAlertListComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .collectedAlertListModule(new CollectedAlertListModule(this))
                .build()
                .inject(this);
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_deploy_list, container, false);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mAlarmOperationType = getArguments().getInt("statusType");
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setAdapter(mAdapter);
        rv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                if (parent.getChildAdapterPosition(view) == 0) {
                    outRect.top = getResources().getDimensionPixelSize(R.dimen.dp8);
                }
                outRect.left = getResources().getDimensionPixelSize(R.dimen.dp8);
                outRect.right = getResources().getDimensionPixelSize(R.dimen.dp8);
            }
        });
        mAdapter.setOnLoadMoreListener(this, rv);
        srl.setOnRefreshListener(this);
        mAdapter.setOnLoadMoreListener(this, rv);
        mAdapter.setLoadMoreView(mLoadMoreView);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MobclickAgent.onEvent(getContext(), "alarmDetail");
                DailyPoliceAlarmEntity item = (DailyPoliceAlarmEntity) adapter.getItem(position);
                Intent intent = new Intent(getActivity(), DailyPoliceDetailActivity.class);
                intent.putExtra("entity", item);
                intent.putExtra("position", position);
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

    @Subscriber(mode = ThreadMode.MAIN, tag = EventBusTags.DAILY_ALARM_MARK_CHANGED)
    public void changeAlarmPosition(AlarmChangeBean bean) {
        onRefresh();
    }
}
