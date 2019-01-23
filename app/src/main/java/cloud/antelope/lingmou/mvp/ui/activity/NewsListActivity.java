package cloud.antelope.lingmou.mvp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.TimeUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.lingdanet.safeguard.common.utils.Utils;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.AttachmentUtils;
import cloud.antelope.lingmou.app.utils.loader.CaseBannerImageLoader;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.di.component.DaggerNewsListComponent;
import cloud.antelope.lingmou.di.module.CyqzConfigModule;
import cloud.antelope.lingmou.di.module.NewsListModule;
import cloud.antelope.lingmou.mvp.contract.CyqzConfigContract;
import cloud.antelope.lingmou.mvp.contract.NewsListContract;
import cloud.antelope.lingmou.mvp.model.entity.AttachmentBean;
import cloud.antelope.lingmou.mvp.model.entity.BannerItemEntity;
import cloud.antelope.lingmou.mvp.model.entity.ContentListEntity;
import cloud.antelope.lingmou.mvp.model.entity.NewsItemEntity;
import cloud.antelope.lingmou.mvp.model.entity.OperationEntity;
import cloud.antelope.lingmou.mvp.model.entity.VictoryItemEntity;
import cloud.antelope.lingmou.mvp.presenter.CyqzConfigPresenter;
import cloud.antelope.lingmou.mvp.presenter.NewsListPresenter;
import cloud.antelope.lingmou.mvp.ui.adapter.NewsAdapter;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class NewsListActivity extends BaseActivity<NewsListPresenter>
        implements NewsListContract.View,
        SwipeRefreshLayout.OnRefreshListener,
        BaseQuickAdapter.RequestLoadMoreListener, CyqzConfigContract.View {

    private static final int PAGE_SIZE = 10;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh_widget)
    SwipeRefreshLayout mRefreshView;
    @BindView(R.id.root)
    LinearLayout mRoot;
    @BindView(R.id.head_left_iv)
    ImageButton mHeadLeftIv;
    @BindView(R.id.title_tv)
    TextView mTitleTv;
    @BindView(R.id.no_permission_rl)
    RelativeLayout mNoPermissionRl;
    @BindView(R.id.empty_tv)
    TextView mEmptyTv;
    @BindView(R.id.empty_no_network_tv)
    TextView mEmptyNoNetworkTv;

    @Inject
    RecyclerView.LayoutManager mLayoutManager;
    @Inject
    RecyclerView.ItemAnimator mItemAnimator;
    @Inject
    RecyclerView.ItemDecoration mItemDecoration;
    @Inject
    LoadMoreView mLoadMoreView;
    @Inject
    NewsAdapter mAdapter;
    @Inject
    CyqzConfigPresenter mCyqzConfigPresenter;

    private View mHeadView;
    private Banner mCaseBanner;
    private LinearLayout mVictoryLl;
    private ViewFlipper mVictoryFlipper;
    private LayoutInflater mLayoutInflater;
    List<BannerItemEntity> mBannerList;
    List<VictoryItemEntity> mVictoryList;
    private int page = 0;

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        DaggerNewsListComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .newsListModule(new NewsListModule(this))
                .cyqzConfigModule(new CyqzConfigModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_news_list; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mTitleTv.setText(R.string.police_news);
        initView();
        /*if (TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.CONFIG_OPERATION_ID))
                || TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.CONFIG_CASE_ID))
                || TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.CONFIG_CLUE_ID))) {
            // 如果发现运营中心的信息不完全，则再查询一次
            mCyqzConfigPresenter.getCyqzConfig();
            // 此时显示数据中在加载中。。。
            // mTipCaseTv.setText(R.string.tip_loading);
            // showEmpty();
        } else {*/
            initData();
        // }
    }

    private void initData(){
        mLayoutInflater = LayoutInflater.from(this);
        initBanner();
        loadLocalData();
        mRefreshView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mRefreshView!=null)
                mRefreshView.setRefreshing(true);
                onRefresh();
            }
        }, 100);
    }

    private void initBanner() {
        mHeadView = getLayoutInflater()
                .inflate(R.layout.header_case_banner, (ViewGroup) mRecyclerView.getParent(), false);
        mCaseBanner = mHeadView.findViewById(R.id.case_banner);
        mVictoryLl = mHeadView.findViewById(R.id.victory_ll);
        mVictoryFlipper = mHeadView.findViewById(R.id.victory_ts);
        // 设置banner样式
        mCaseBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE);
        //设置图片加载器
        mCaseBanner.setImageLoader(new CaseBannerImageLoader());
        //设置banner动画效果
        mCaseBanner.setBannerAnimation(Transformer.Default);
        //设置自动轮播，默认为true
        mCaseBanner.isAutoPlay(true);
        //设置轮播时间
        mCaseBanner.setDelayTime(5000);
        //设置指示器位置（当banner模式中有指示器时）
        mCaseBanner.setIndicatorGravity(BannerConfig.CENTER);
        mCaseBanner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                Activity curActivity = getActivity();
                Intent intent = new Intent(curActivity, NewsDetailActivity.class);
                intent.putExtra(NewsDetailActivity.CASE_ITEM, mBannerList.get(position));
                curActivity.startActivity(intent);
            }
        });
        mVictoryLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入战果展示页面
                startActivity(new Intent(Utils.getContext(), VictoryShowActivity.class));
            }
        });
    }

    public void initView() {
        mRefreshView.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        mRefreshView.setOnRefreshListener(this);

        mRecyclerView.setHasFixedSize(false);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(mItemAnimator);
        mRecyclerView.addItemDecoration(mItemDecoration);

        mAdapter = new NewsAdapter(null);
        mAdapter.setOnLoadMoreListener(this, mRecyclerView);
        mAdapter.setLoadMoreView(mLoadMoreView);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                NewsItemEntity caseItem = (NewsItemEntity) adapter.getItem(position);
                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                intent.putExtra(NewsDetailActivity.CASE_ITEM, caseItem);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRefresh() {
        // mTipCaseTv.setText(R.string.tip_loading);
        mAdapter.setEnableLoadMore(false);
        if (TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.CONFIG_OPERATION_ID))
                || TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.CONFIG_CASE_ID))
                || TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.CONFIG_CLUE_ID))) {
            // 如果发现运营中心的信息不完全，则再查询一次
            mCyqzConfigPresenter.getCyqzConfig();
        } else {
            mPresenter.getColumnLastUpdateTime(SPUtils.getInstance().getString(Constants.CONFIG_CASE_ID));
            getVictoryList();
        }
    }

    private void getVictoryList() {
        mPresenter.getNewsList(page, PAGE_SIZE, "06");
    }

    @Override
    public void onGetColumnLastUpdateTimeSuccess(Long time) {
        long localLastUpdateTime = SPUtils.getInstance().getLong(Constants.CONFIG_LAST_CASE_TIME);
        // if (time != localLastUpdateTime) {
        //     SPUtils.getInstance().put(Constants.CONFIG_LAST_CASE_TIME, time);
            loadServerData();
        // } else {
        //     mAdapter.setEnableLoadMore(true);
        //     mRefreshView.setRefreshing(false);
        // }
    }

    @Override
    public void onGetColumnLastUpdateTimeError() {
        mRefreshView.setRefreshing(false);
        mAdapter.setEnableLoadMore(true);
    }

    @Override
    public void onGetNewsListSuccess(ContentListEntity<NewsItemEntity> contentList) {
        showDataView();
        mRefreshView.setRefreshing(false);
        mAdapter.setEnableLoadMore(true);
        if (0 == page) {
            DataSupport.deleteAll(NewsItemEntity.class);
        }
        List<NewsItemEntity> caseList = contentList.getList();
        DataSupport.saveAll(caseList);
        showNewsListData(caseList);
    }

    @Override
    public void onGetNewsListError(String errMsg) {
        // 如果在加载第一页数据的时候出现了错误，则重置更新时间，确保下一次会重新从远程抓取数据
        // mTipCaseTv.setText(R.string.tip_load_fail);
        // 如果在加载第一页数据的时候出现了错误，则重置更新时间，确保下一次会重新从远程抓取数据
        if (page <= 0) {
            SPUtils.getInstance().put(Constants.CONFIG_LAST_CASE_TIME, -1L);
        } else {
            mRefreshView.setEnabled(true);
            mAdapter.loadMoreFail();
        }
        if (TextUtils.equals(errMsg, Utils.getContext().getString(R.string.column_not_exist))) {
            SPUtils.getInstance().remove(Constants.CONFIG_OPERATION_ID);
            SPUtils.getInstance().remove(Constants.CONFIG_CASE_ID);
            SPUtils.getInstance().remove(Constants.CONFIG_CLUE_ID);
            DataSupport.deleteAll(BannerItemEntity.class);
            DataSupport.deleteAll(NewsItemEntity.class);
            DataSupport.deleteAll(VictoryItemEntity.class);
            mCyqzConfigPresenter.getCyqzConfig();
            // mTipCaseTv.setText(R.string.tip_loading);
        } else {
            ToastUtils.showShort(errMsg);
            mRefreshView.setRefreshing(false);
            mAdapter.setEnableLoadMore(true);
        }
    }

    @Override
    public void onGetNewsTopSuccess(ContentListEntity<BannerItemEntity> contentList) {
        if (null != mBannerList && mBannerList.size() > 0) {
            mBannerList.clear();
        }
        showDataView();
        mBannerList = contentList.getList();
        DataSupport.deleteAll(BannerItemEntity.class);
        DataSupport.deleteAll(NewsItemEntity.class);
        DataSupport.deleteAll(VictoryItemEntity.class);
        for (BannerItemEntity bannerItem : mBannerList) {
            bannerItem.save();
        }
        // resetBannerView();
        getNewsList();
    }

    @Override
    public void onGetNewsTopError(String errMsg) {
        // mTipCaseTv.setText(R.string.tip_load_fail);
        SPUtils.getInstance().put(Constants.CONFIG_LAST_CASE_TIME, -1L);
        ToastUtils.showShort(errMsg);
        mRefreshView.setRefreshing(false);
        mAdapter.setEnableLoadMore(true);
    }

    private void setFlipper() {
        if (null != mVictoryFlipper) {
            mVictoryFlipper.removeAllViews();
            if (null == mVictoryList || mVictoryList.isEmpty()) {
                mVictoryLl.setVisibility(View.GONE);
                return;
            }
            mVictoryLl.setVisibility(View.VISIBLE);
            int size = mVictoryList.size();
            for (int i = 0; i < size; i += 2) {
                if (i > 3) {
                    break;
                }
                if (null != mLayoutInflater) {
                    View view = mLayoutInflater.inflate(R.layout.flipper_item, null);
                    TextView tv1 = view.findViewById(R.id.one_tv);
                    TextView tv2 = view.findViewById(R.id.two_tv);
                    tv1.setText(TimeUtils.millis2String(mVictoryList.get(i).getOldCreateTime(), TimeUtils.FORMAT_FOUR)+"，"+mVictoryList.get(i).getTitle());
                    if (size == 1) {
                        tv2.setVisibility(View.GONE);
                    } else if (i + 1 < size && i + 1 < 4) {
                        tv2.setText(TimeUtils.millis2String(mVictoryList.get(i+1).getOldCreateTime(), TimeUtils.FORMAT_FOUR)+"，"+mVictoryList.get(i + 1).getTitle());
                    } else {
                        tv2.setVisibility(View.INVISIBLE);
                    }
                    mVictoryFlipper.addView(view);
                }
            }
            if (size > 2) {
                mVictoryFlipper.startFlipping();
            }
        }
    }

    @Override
    public void onGetVictorySuccess(List<VictoryItemEntity> contentList) {
        showDataView();
        if (null != mVictoryList) {
            mVictoryList.clear();
        }
        if (contentList != null && !contentList.isEmpty()) {
            mVictoryList.addAll(contentList);
        }
        for (VictoryItemEntity itemEntity : mVictoryList) {
            itemEntity.save();
        }
        setFlipper();
    }

    /**
     * 加载远程服务器的数据到NewsList列表.
     */
    private void loadServerData() {
        page = 0;
        mPresenter.getNewsTop();
    }

    /**
     * 加载本地数据到NewsList列表.
     */
    private void loadLocalData() {
        page = 0;
        mBannerList = DataSupport.findAll(BannerItemEntity.class);
        mVictoryList = DataSupport.findAll(VictoryItemEntity.class);
        List<NewsItemEntity> caseList = DataSupport.findAll(NewsItemEntity.class);
        // 如果加载本地数据得到的全部为0，则重置更新时间，确保后面会从服务器再获取一次数据
        if (mBannerList.size() <= 0 && caseList.size() <= 0) {
            SPUtils.getInstance().put(Constants.CONFIG_LAST_CASE_TIME, -1L);
        } else {
            showNewsListData(caseList);
        }
    }

    private void showNewsListData(List<NewsItemEntity> newsList) {
        mRefreshView.setRefreshing(false);
        mAdapter.setEnableLoadMore(true);
        if (null != newsList) {
            for (NewsItemEntity caseItem : newsList) {
                if (!TextUtils.isEmpty(caseItem.getCaseLevel())) {
                    List<AttachmentBean> imgInfoJson = caseItem.getImgInfoJson();
                    if (null != imgInfoJson) {
                        Iterator<AttachmentBean> iterator = imgInfoJson.iterator();
                        while (iterator.hasNext()) {
                            AttachmentBean next = iterator.next();
                            AttachmentBean.MetadataBean metadata = next.getMetadata();
                            if (!metadata.getMimeType().startsWith("image")) {
                                iterator.remove();
                            }
                        }
                    }
                    // 如果是重大的告警
                    if (caseItem.getCaseLevel().equals(Constants.CASE_LEVEL_IMPORT)) {
                        caseItem.setItemType(NewsItemEntity.IMPORT_CASE);
                    } else if (imgInfoJson != null && imgInfoJson.size() > 0
                            && Constants.IS_FRONT_COVER.equals(
                            imgInfoJson.get(0).getMetadata().isFrontCover())) { // 包含封面图片的非重大告警
                        caseItem.setItemType(NewsItemEntity.ONE_IMAGE_CASE);
                    } else if (AttachmentUtils.getImgsWithToken(imgInfoJson).size() >= 3) { // 如果大于三张图片
                        caseItem.setItemType(NewsItemEntity.MULTI_IMAGE_CASE);
                    } else if (imgInfoJson == null || imgInfoJson.size() == 0) { // 如果没有图片
                        caseItem.setItemType(NewsItemEntity.NO_IMAGE_CASE);
                    } else { // 如果有一张图片或者两张图片
                        caseItem.setItemType(NewsItemEntity.ONE_IMAGE_CASE);
                    }
                } else {
                    caseItem.setItemType(NewsItemEntity.NO_IMAGE_CASE);
                }
            }
            if (page == 0) {
                if (newsList.size() <= 0 && mBannerList.size() <= 0 && mVictoryList.size() <=0) {
                    // mTipCaseTv.setText(R.string.tip_no_case);
                    showEmpty();
                    return;
                } else {
                    showDataView();
                    if (mBannerList.size() > 0 || mVictoryList.size() > 0) { // 如果banner有数据
                        List<String> titles = new ArrayList<>();
                        for (BannerItemEntity bannerItem : mBannerList) {
                            titles.add(bannerItem.getTitle());
                        }
                        if (mAdapter.getHeaderLayoutCount() > 0) {
                            if (null != mCaseBanner) {
                                if (mBannerList.size() == 0) {
                                    mCaseBanner.setVisibility(View.GONE);
                                } else {
                                    mCaseBanner.update(mBannerList, titles);
                                    mCaseBanner.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            if (mBannerList.size() > 0) {
                                mCaseBanner.setVisibility(View.VISIBLE);
                                mCaseBanner.setImages(mBannerList);
                                mCaseBanner.setBannerTitles(titles);
                                mCaseBanner.start();
                            } else {
                                mCaseBanner.setVisibility(View.GONE);
                            }
                            mAdapter.addHeaderView(mHeadView);
                        }
                    } else { // 如果banner没有数据，则先移除banner
                        if (mAdapter.getHeaderLayoutCount() > 0) {
                            if (null != mCaseBanner) {
                                mCaseBanner.stopAutoPlay();
                                mVictoryFlipper.removeAllViews();
                                mAdapter.removeHeaderView(mHeadView);
                                // mCaseBanner = null;
                            }
                        }
                    }
                    // if (newsList.size() > 0) {
                        mAdapter.setNewData(newsList);
                    // }
                }
            } else {
                mRefreshView.setEnabled(true);
                mAdapter.addData(newsList);
            }
            if (newsList.size() < PAGE_SIZE) {
                mAdapter.loadMoreEnd(false);
            } else {
                mAdapter.loadMoreComplete();
            }
        }
    }

    private void showNoPermission() {
        mRecyclerView.setVisibility(View.GONE);
        mEmptyTv.setVisibility(View.GONE);
        mEmptyNoNetworkTv.setVisibility(View.GONE);
        mNoPermissionRl.setVisibility(View.VISIBLE);
    }

    private void showEmpty() {
        mRecyclerView.setVisibility(View.GONE);
        mEmptyTv.setVisibility(View.VISIBLE);
        mEmptyNoNetworkTv.setVisibility(View.GONE);
        mNoPermissionRl.setVisibility(View.GONE);
    }

    private void showDataView() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mEmptyTv.setVisibility(View.GONE);
        mEmptyNoNetworkTv.setVisibility(View.GONE);
        mNoPermissionRl.setVisibility(View.GONE);
    }

    private void showNoNetWorkView() {
        mRecyclerView.setVisibility(View.GONE);
        mEmptyTv.setVisibility(View.GONE);
        mEmptyNoNetworkTv.setVisibility(View.VISIBLE);
        mNoPermissionRl.setVisibility(View.GONE);
    }

    /**
     * 从服务器获取News数据.
     */
    private void getNewsList() {
        mPresenter.getNewsList(page, PAGE_SIZE, "01,02,03,04,05");
    }

    @Override
    public void onLoadMoreRequested() {
        if ((mAdapter.getData().size() % PAGE_SIZE) != 0) {
            mAdapter.loadMoreEnd(false);
            return;
        }
        mRefreshView.setEnabled(false);
        page = mAdapter.getData().size() / PAGE_SIZE;
        getNewsList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
        if (null != mRefreshView && mRefreshView.isRefreshing()) {
            mRefreshView.setRefreshing(false);
        }
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

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void onGetCyqzConfigSuccess(OperationEntity entity) {
        initData();
    }

    @Override
    public void onGetCyqzConfigError() {
        // mTipCaseTv.setText(R.string.tip_load_fail);
        mRefreshView.setRefreshing(false);
        mAdapter.setEnableLoadMore(true);
        showNoNetWorkView();
    }

    @OnClick(R.id.head_left_iv)
    public void onViewClicked() {
        finish();
    }
}
