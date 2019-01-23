package cloud.antelope.lingmou.mvp.ui.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.modle.SweetDialog;
import com.lingdanet.safeguard.common.utils.TimeUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.umeng.analytics.MobclickAgent;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.di.component.DaggerEventRemindListComponent;
import cloud.antelope.lingmou.di.module.EventRemindListModule;
import cloud.antelope.lingmou.mvp.contract.EventRemindListContract;
import cloud.antelope.lingmou.mvp.model.entity.DailyPoliceAlarmEntity;
import cloud.antelope.lingmou.mvp.model.entity.DeployListRequest;
import cloud.antelope.lingmou.mvp.model.entity.DeployResponse;
import cloud.antelope.lingmou.mvp.model.entity.EventRemindEntity;
import cloud.antelope.lingmou.mvp.model.entity.ModifyDeployListEvent;
import cloud.antelope.lingmou.mvp.model.entity.StartOrPauseDeployMissionRequest;
import cloud.antelope.lingmou.mvp.presenter.EventRemindListPresenter;
import cloud.antelope.lingmou.mvp.ui.activity.AlertDetailActivity;
import cloud.antelope.lingmou.mvp.ui.activity.DailyPoliceDetailActivity;
import cloud.antelope.lingmou.mvp.ui.activity.DeployMissionDetailActivity;
import cloud.antelope.lingmou.mvp.ui.adapter.DailyPoliceAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.DeployControlAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.EventRemindListAdapter;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class EventRemindListFragment extends BaseFragment<EventRemindListPresenter> implements EventRemindListContract.View, BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    public static final int ALL = 0;
    public static final int UNSETTLED = 1;
    public static final int VALID_EVENT = 2;
    public static final int INVALID_EVENT = 3;
    private static final int ITEM_CLICK_POSITION = 0x01;
    @BindView(R.id.iv)
    ImageView iv;
    @BindView(R.id.rl_empty)
    RelativeLayout rlEmpty;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.srl)
    SwipeRefreshLayout srl;

    @Inject
    LoadMoreView mLoadMoreView;
    @Inject
    DailyPoliceAdapter mAdapter;
    private boolean hasMore;
    int pageNum = 1;
    int pageSize = 10;
    private Integer mAlarmScope = 1;
    private Integer mAlarmOperationType = -1;//5：全部<br />1：待处理<br />2：有效<br />3：无效<br />4：已处理 |
    private Long mEndTimeStamp = null;
    private Long mStartTimeStamp = null;
    Integer statusType;
    boolean needRefresh;
    String keyword;
    private boolean visible;
    private boolean fromSearch;

    public static EventRemindListFragment newInstance() {
        EventRemindListFragment fragment = new EventRemindListFragment();
        return fragment;
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerEventRemindListComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .eventRemindListModule(new EventRemindListModule(this))
                .build()
                .inject(this);
    }

    public void setTime(Long startTimeStamp, Long endTimeStamp) {
        this.mStartTimeStamp = startTimeStamp;
        this.mEndTimeStamp = endTimeStamp;
        if (visible) {
            onRefresh();
        }
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_remind_list, container, false);
    }


    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        statusType = getArguments().getInt("statusType");
        fromSearch = getArguments().getBoolean("fromSearch", false);
        if (fromSearch) {
            srl.setEnabled(false);
        }
        switch (statusType) {
            case ALL:
                mAlarmOperationType = 5;
                break;
            case UNSETTLED:
                mAlarmOperationType = 1;
                break;
            case VALID_EVENT:
                mAlarmOperationType = 2;
                break;
            case INVALID_EVENT:
                mAlarmOperationType = 3;
                break;
        }
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int position = parent.getChildLayoutPosition(view);
                outRect.bottom = position == mAdapter.getData().size() - 1 ? 0 : getResources().getDimensionPixelSize(R.dimen.dp8);
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
                MobclickAgent.onEvent(getContext(), "alarmDetail");
                DailyPoliceAlarmEntity item = (DailyPoliceAlarmEntity) adapter.getItem(position);
                Intent intent = new Intent(getActivity(), DailyPoliceDetailActivity.class);
                intent.putExtra("entity", item);
                intent.putExtra("position", position);
                intent.putExtra("isEventRemind", true);
                startActivityForResult(intent, ITEM_CLICK_POSITION);
            }
        });
        srl.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);


        mEndTimeStamp = System.currentTimeMillis();
        mStartTimeStamp = System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000L;

        if (!fromSearch) {
            srl.postDelayed(() ->
                            onRefresh()
                    , 100);
        }
    }

    @Override
    public void setData(@Nullable Object data) {

    }

    @Override
    public void onRefresh() {
        pageNum = 1;
        mPresenter.getList(pageNum, pageSize, null, 101503, mAlarmOperationType, mAlarmScope, mStartTimeStamp, mEndTimeStamp);
        needRefresh = false;
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == ITEM_CLICK_POSITION) {
                int position = data.getIntExtra("position", -1);
                if (-1 == position) {
                    //证明是收藏相关的
                    int collect_position = data.getIntExtra("collect_position", -1);
                    boolean collect = data.getBooleanExtra("collect", false);
                    if (mAlarmScope == 2) {
                        srl.setRefreshing(true);
                        onRefresh();
                    } else {
                        mAdapter.getItem(collect_position).setCollectStatus(collect ? 1 : 0);
                    }
                } else {
                    if (-1 == mAlarmOperationType) {
                        srl.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //表示是全部，则需要更新数据
                                srl.setRefreshing(true);
                                onRefresh();
                            }
                        }, 150);
                    } else {
                        mAdapter.remove(position);
                        mAdapter.notifyItemRemoved(position);
                    }
                }

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
    public void onLoadMoreRequested() {
        if (hasMore) {
            pageNum++;
            mPresenter.getList(pageNum, pageSize, null, 101503, mAlarmOperationType, mAlarmScope, mStartTimeStamp, mEndTimeStamp);
            needRefresh = false;
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
        ArmsUtils.snackbarText(message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {

    }
}
