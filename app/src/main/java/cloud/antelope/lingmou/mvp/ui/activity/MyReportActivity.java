package cloud.antelope.lingmou.mvp.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;

import org.litepal.crud.DataSupport;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.AttachmentUtils;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.di.component.DaggerMyReportComponent;
import cloud.antelope.lingmou.di.module.MyReportModule;
import cloud.antelope.lingmou.mvp.contract.MyReportContract;
import cloud.antelope.lingmou.mvp.model.entity.AttachmentBean;
import cloud.antelope.lingmou.mvp.model.entity.ClueItemEntity;
import cloud.antelope.lingmou.mvp.model.entity.ContentListEntity;
import cloud.antelope.lingmou.mvp.presenter.MyReportPresenter;
import cloud.antelope.lingmou.mvp.ui.adapter.ClueAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.CustomerLoadMoreView;
import cloud.antelope.lingmou.mvp.ui.widget.HorizontalItemDecoration;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class MyReportActivity extends BaseActivity<MyReportPresenter>
        implements MyReportContract.View,
        BaseQuickAdapter.RequestLoadMoreListener,
        SwipeRefreshLayout.OnRefreshListener {

    private static final int PAGE_SIZE = 10;
    private int page = 0;


    @BindView(R.id.swipe_refresh_widget)
    SwipeRefreshLayout mRefreshView;
    @BindView(R.id.clue_rv)
    RecyclerView mRecyclerView;
    // @BindView(R.id.tip_clue_tv)
    // TextView mTipClueTv;
    @BindView(R.id.empty_layout)
    LinearLayout mEmptyLayout;
    @BindView(R.id.head_left_iv)
    ImageButton mHeadLeftIv;
    @BindView(R.id.title_tv)
    TextView mTitleTv;

    @Inject
    ClueAdapter mAdapter;


    public Context mContext;

    private Handler mHandler = new Handler();

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        DaggerMyReportComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .myReportModule(new MyReportModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_my_report; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mContext = this;
        mTitleTv.setText(R.string.my_clue);
        initTheView();
        if (TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.CONFIG_OPERATION_ID))
                || TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.CONFIG_CASE_ID))
                || TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.CONFIG_CLUE_ID))) {
            // 如果发现运营中心的信息不完全，则再查询一次
            mPresenter.getCyqzConfig();
            // 此时显示数据中在加载中。。。
            // mTipClueTv.setText(R.string.tip_loading);
            // showEmpty();
        } else {
            initData();
        }
    }


    private void initData() {
        loadLocalData();
        mRefreshView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mRefreshView != null)
                    mRefreshView.setRefreshing(true);
                mRefreshView.setRefreshing(true);
                onRefresh();
            }
        }, 100);
    }

    private void initTheView() {

        mRefreshView.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        mRefreshView.setOnRefreshListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new HorizontalItemDecoration.Builder(mContext)
                .color(getResources().getColor(R.color.my_divider_bg_grey))
                .sizeResId(R.dimen.a_half_dp)
                // .marginResId(getLineMargins()[0], getLineMargins()[1])
                .build());
        mAdapter.setOnLoadMoreListener(this, mRecyclerView);
        mAdapter.setLoadMoreView(new CustomerLoadMoreView());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ClueItemEntity item = (ClueItemEntity) adapter.getItem(position);
                Intent intent = new Intent(mContext, ClueDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.CONFIG_CONTENT_ID, item.getClueId());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRefresh() {
        // mTipClueTv.setText(R.string.tip_loading);
        mAdapter.setEnableLoadMore(false);
        if (TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.CONFIG_OPERATION_ID))
                || TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.CONFIG_CASE_ID))
                || TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.CONFIG_CLUE_ID))) {
            // 如果发现运营中心的信息不完全，则再查询一次
            mPresenter.getCyqzConfig();
        } else {
            // mPresenter.getColumnLastUpdateTime(SPUtils.getInstance().getString(Constants.CONFIG_CLUE_ID));
            loadServerData();
        }
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void onGetColumnLastUpdateTimeSuccess(Long time) {
        long localLastUpdateTime = SPUtils.getInstance().getLong(Constants.CONFIG_LAST_CLUE_TIME);
        if (time != localLastUpdateTime) {
            SPUtils.getInstance().put(Constants.CONFIG_LAST_CLUE_TIME, time);
            loadServerData();
        } else {
            mAdapter.setEnableLoadMore(true);
            mRefreshView.setRefreshing(false);
        }
    }

    @Override
    public void onGetColumnLastUpdateTimeError() {
        if (page == 0 && mAdapter.getData().isEmpty()) {
            // showNoNetwork();
        }
        mRefreshView.setRefreshing(false);
        mAdapter.setEnableLoadMore(true);
    }

    /**
     * 加载服务器的数据.
     */
    private void loadServerData() {
        page = 0;
        getClueList();
    }

    /**
     * 加载本地的数据.
     */
    private void loadLocalData() {
        page = 0;
        List<ClueItemEntity> clueList = DataSupport.findAll(ClueItemEntity.class);
        if (clueList.size() <= 0) {
            // 如果加载本地数据得到的为0，则重置更新时间，确保后面会从服务器再获取一次数据
            SPUtils.getInstance().put(Constants.CONFIG_LAST_CLUE_TIME, -1L);
        } else {
            showClueData(clueList);
        }
    }

    /**
     * 获取线索列表.
     */
    private void getClueList() {
        mPresenter.getClueList(page, PAGE_SIZE);
    }


    @Override
    public void onLoadMoreRequested() {
        if ((mAdapter.getData().size() % PAGE_SIZE) != 0) {
            mAdapter.loadMoreEnd(false);
            return;
        }
        mRefreshView.setEnabled(false);
        page = mAdapter.getData().size() / PAGE_SIZE;
        getClueList();
    }

    @Override
    public void onGetClueListSuccess(ContentListEntity<ClueItemEntity> contentList) {
        mRefreshView.setRefreshing(false);
        mRefreshView.setEnabled(true);
        mAdapter.setEnableLoadMore(true);
        if (page == 0) {
            DataSupport.deleteAll(ClueItemEntity.class);
        }
        List<ClueItemEntity> clueList = contentList.getList();
        DataSupport.saveAll(clueList);
        showClueData(contentList.getList());
    }

    @Override
    public void onGetClueListError(String errMsg) {
        // mTipClueTv.setText(R.string.tip_load_fail);
        // 如果在加载第一页数据的时候出现了错误，则重置更新时间，确保下一次会重新从远程抓取数据
        if (page <= 0) {
            SPUtils.getInstance().put(Constants.CONFIG_LAST_CLUE_TIME, -1L);
        } else {
            mRefreshView.setEnabled(true);
            mAdapter.loadMoreFail();
        }
        if (TextUtils.equals(errMsg, getString(R.string.colume_not_exist))) {
            SPUtils.getInstance().remove(Constants.CONFIG_OPERATION_ID);
            SPUtils.getInstance().remove(Constants.CONFIG_CASE_ID);
            SPUtils.getInstance().remove(Constants.CONFIG_CLUE_ID);
            DataSupport.deleteAll(ClueItemEntity.class);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // 如果发现运营中心的信息不完全，则再查询一次
                    mPresenter.getCyqzConfig();
                    // mTipClueTv.setText(R.string.tip_loading);
                }
            }, 100);

        } else {
            mRefreshView.setRefreshing(false);
            mAdapter.setEnableLoadMore(true);
            ToastUtils.showShort(errMsg);
        }
    }

    @Override
    public void onGetCyqzConfigSuccess() {
        initData();
    }

    @Override
    public void onGetCyqzConfigError(String errMsg) {
        // 显示数据加载失败
        // mTipClueTv.setText(R.string.tip_load_fail);
        mRefreshView.setRefreshing(false);
        mAdapter.setEnableLoadMore(true);
    }

    public void showEmpty() {
        mRecyclerView.setVisibility(View.GONE);
        mEmptyLayout.setVisibility(View.VISIBLE);
    }

    public void showClueView() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mEmptyLayout.setVisibility(View.GONE);
    }

    private void showClueData(List<ClueItemEntity> clueList) {
        mRefreshView.setRefreshing(false);
        mRefreshView.setEnabled(true);
        mAdapter.setEnableLoadMore(true);
        if (null != clueList) {
            for (ClueItemEntity clue : clueList) {
                List<AttachmentBean> attachments = clue.getImgInfoJson();
                String videoThumb = AttachmentUtils.hasVideo(attachments);
                List<String> imgs = AttachmentUtils.getRealImgs(attachments);
                // 如果包含三张图片
                if (imgs.size() >= 3 || (!TextUtils.isEmpty(videoThumb) && imgs.size() == 2)) {
                    clue.setItemType(ClueAdapter.MUlTI_IMAGE_CLUE);
                } else if (imgs.size() <= 0 && TextUtils.isEmpty(videoThumb)) { // 如果不包含图片
                    clue.setItemType(ClueAdapter.NO_IMAGE_CLUE);
                } else {  // 剩下的是包含一张图片或两张图片的情况，这两种情况都只显示一张图片
                    clue.setItemType(ClueAdapter.ONE_IMAGE_CLUE);
                }
            }
            if (page == 0) {
                if (clueList.size() == 0) {
                    //mTipClueTv.setText(R.string.tip_no_clue);
                    showEmpty();
                    return;
                } else {
                    mAdapter.setNewData(clueList);
                    showClueView();
                }
            } else {
                mAdapter.addData(clueList);
            }
            if ((clueList.size() % PAGE_SIZE) != 0) {
                mAdapter.loadMoreEnd(false);
            } else {
                mAdapter.loadMoreComplete();
            }
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
        ArmsUtils.toastText(message);
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
}
