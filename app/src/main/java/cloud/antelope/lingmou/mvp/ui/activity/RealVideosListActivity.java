package cloud.antelope.lingmou.mvp.ui.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.di.component.DaggerRealVideosListComponent;
import cloud.antelope.lingmou.di.module.RealVideosListModule;
import cloud.antelope.lingmou.mvp.contract.RealVideosListContract;
import cloud.antelope.lingmou.mvp.model.entity.CameraNewStreamEntity;
import cloud.antelope.lingmou.mvp.model.entity.DailyFilterItemEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;
import cloud.antelope.lingmou.mvp.presenter.RealVideosListPresenter;
import cloud.antelope.lingmou.mvp.ui.adapter.CollectCameraNewAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.DailyFilterAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.RealVideosListAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.MenuView;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class RealVideosListActivity extends BaseActivity<RealVideosListPresenter> implements RealVideosListContract.View,
        SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {

    @BindView(R.id.head_left_iv)
    ImageButton mHeadLeftIv;
    @BindView(R.id.title_tv)
    TextView mTitleTv;
    @BindView(R.id.head_right_iv)
    ImageView mHeadRightIv;
    @BindView(R.id.device_state_tv)
    TextView mDeviceStateTv;
    @BindView(R.id.device_state_ll)
    LinearLayout mDeviceStateLl;
    @BindView(R.id.device_type_tv)
    TextView mDeviceTypeTv;
    @BindView(R.id.device_type_ll)
    LinearLayout mDeviceTypeLl;
    @BindView(R.id.real_video_rclv)
    RecyclerView mRealVideoRclv;
    @BindView(R.id.real_video_srfl)
    SwipeRefreshLayout mRealVideoSrfl;
    @BindView(R.id.empty_tv)
    TextView mEmptyTv;
    @BindView(R.id.empty_no_network_tv)
    TextView mEmptyNoNetworkTv;
    @BindView(R.id.shaixuan_ll)
    LinearLayout mShaixuanLl;
    @BindView(R.id.filter_rclv)
    RecyclerView mFilterRclv;
    @BindView(R.id.tv_search)
    TextView tvSearch;

    @Inject
    @Named("PoliceLayoutManager")
    RecyclerView.LayoutManager mLayoutManager;
    @Inject
    @Named("FilterLayoutManager")
    RecyclerView.LayoutManager mFilterLayoutManager;
    @Inject
    RealVideosListAdapter mCameraAdapter;
    @Inject
    @Named("PoliceItemAnimator")
    RecyclerView.ItemAnimator mItemAnimator;
    @Inject
    @Named("FilterItemAnimator")
    RecyclerView.ItemAnimator mFilterItemAnimator;
    @Inject
    RecyclerView.ItemDecoration mItemDecoration;
    @Inject
    LoadMoreView mLoadMoreView;
    @Inject
    DailyFilterAdapter mDailyFilterAdapter;

    private List<OrgCameraEntity> mNowTotalEntities;

    private List<DailyFilterItemEntity> mStateEntities;
    private List<DailyFilterItemEntity> mTypeEntities;

    private Drawable mFilterArrowDownYellowDrawable, mFilterArrowDownBlackDrawable,
            mFilterArrowUpYellowDrawable, mFilterArrowUpBlackDrawable;

    private Animation mTopOut;
    private Animation mTopIn;

    private int mPosition;

    private int mPreTypePosition;
    private int mPreStatePosition;
    private Integer mDeviceState;
    private Integer mDeviceType;
    private int mPageNo = 1;
    private final static int PAGE_SIZE = 12;
    private MenuView menuView;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerRealVideosListComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .realVideosListModule(new RealVideosListModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_real_videos_list; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

        mTopOut = AnimationUtils.loadAnimation(this, R.anim.top_out);
        mTopIn = AnimationUtils.loadAnimation(this, R.anim.top_in);

        mFilterArrowDownBlackDrawable = getResources().getDrawable(R.drawable.arrow_black_triangle_down);
        mFilterArrowUpBlackDrawable = getResources().getDrawable(R.drawable.arrow_black_triangle_up);
        mFilterArrowDownYellowDrawable = getResources().getDrawable(R.drawable.arrow_yellow_triangle_down);
        mFilterArrowUpYellowDrawable = getResources().getDrawable(R.drawable.arrow_yellow_triangle_up);

        mFilterArrowDownBlackDrawable.setBounds(0, 0, mFilterArrowDownBlackDrawable.getMinimumWidth(), mFilterArrowDownBlackDrawable.getMinimumHeight());
        mFilterArrowUpBlackDrawable.setBounds(0, 0, mFilterArrowUpBlackDrawable.getMinimumWidth(), mFilterArrowUpBlackDrawable.getMinimumHeight());
        mFilterArrowDownYellowDrawable.setBounds(0, 0, mFilterArrowDownYellowDrawable.getMinimumWidth(), mFilterArrowDownYellowDrawable.getMinimumHeight());
        mFilterArrowUpYellowDrawable.setBounds(0, 0, mFilterArrowUpYellowDrawable.getMinimumWidth(), mFilterArrowUpYellowDrawable.getMinimumHeight());

        mTitleTv.setText(R.string.real_time_video);
        mNowTotalEntities = new ArrayList<>();
        mRealVideoSrfl.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);

        mRealVideoSrfl.setOnRefreshListener(this);
        mRealVideoRclv.setHasFixedSize(false);

        mRealVideoRclv.setLayoutManager(mLayoutManager);
        mRealVideoRclv.setItemAnimator(mItemAnimator);
        mRealVideoRclv.setAdapter(mCameraAdapter);
        mCameraAdapter.setIsFromAllVideo(true);


        mFilterRclv.setLayoutManager(new GridLayoutManager(RealVideosListActivity.this,3));
        mFilterRclv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int position = parent.getChildLayoutPosition(view);
                outRect.left = position % 3 == 0 ? getResources().getDimensionPixelSize(R.dimen.dp16) : getResources().getDimensionPixelSize(R.dimen.dp8);
                outRect.right = position % 3 == 2 ? getResources().getDimensionPixelSize(R.dimen.dp16) : getResources().getDimensionPixelSize(R.dimen.dp8);
                outRect.top = position < 3 ? getResources().getDimensionPixelSize(R.dimen.dp16) : 0;
                outRect.bottom = getResources().getDimensionPixelSize(R.dimen.dp16);
            }
        });
        mFilterRclv.setAdapter(mDailyFilterAdapter);

        mCameraAdapter.setOnLoadMoreListener(this, mRealVideoRclv);
        mCameraAdapter.setLoadMoreView(mLoadMoreView);

        initFilterData();
        initListener();
        mFilterRclv.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRealVideoSrfl.setRefreshing(true);
                onRefresh();
            }
        }, 100);
    }

    private void initFilterData() {
        mStateEntities = new ArrayList<>();
        mTypeEntities = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            DailyFilterItemEntity entity = new DailyFilterItemEntity();
            if (i == 0) {
                entity.setSelect(true);
                entity.setName("全部");
            } else if (i == 1) {
                entity.setSelect(false);
                entity.setName("在线");
            } else {
                entity.setSelect(false);
                entity.setName("离线");
            }
            mStateEntities.add(entity);
        }

        for (int i = 0; i < 4; i++) {
            DailyFilterItemEntity entity = new DailyFilterItemEntity();
            if (i == 0) {
                entity.setSelect(true);
                entity.setName("全部");
            } else if (i == 1) {
                entity.setSelect(false);
                entity.setName("智能枪机");
            } else if (i == 2) {
                entity.setSelect(false);
                entity.setName("抓拍机");
            } else {
                entity.setSelect(false);
                entity.setName("球机");
            }
            mTypeEntities.add(entity);
        }
    }

    private void initListener() {

        mDailyFilterAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (mPosition == 0) {
                    //证明当前是第一个筛选
                    mStateEntities.get(mPreStatePosition).setSelect(false);
                    mPreStatePosition = position;
                    mStateEntities.get(position).setSelect(true);
                    showArrowDown(0);
                    switch (position) {
                        case 0:
                            mDeviceState = null;
                            break;
                        case 1:
                            mDeviceState = 1;
                            break;
                        case 2:
                            mDeviceState = 0;
                            break;
                    }
                } else if (mPosition == 1) {
                    mTypeEntities.get(mPreTypePosition).setSelect(false);
                    mPreTypePosition = position;
                    mTypeEntities.get(position).setSelect(true);
                    showArrowDown(1);
                    switch (position) {
                        case 0:
                            mDeviceType = null;
                            break;
                        case 1:
                            mDeviceType = 100604;
                            break;
                        case 2:
                            mDeviceType = 100603;
                            break;
                        case 3:
                            mDeviceType = 100602;
                            break;
                    }
                }
//                mShaixuanLl.setVisibility(View.GONE);
//                mShaixuanLl.startAnimation(mTopOut);
                menuView.dismiss();
                mRealVideoRclv.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRealVideoSrfl.setRefreshing(true);
                        onRefresh();
                    }
                }, 200);
            }
        });

        mCameraAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //进入设备竖屏播放页面
                OrgCameraEntity orgCameraEntity = (OrgCameraEntity) adapter.getItem(position);
                Intent intent = new Intent(RealVideosListActivity.this, VideoPlayActivity.class);
                intent.putExtra("cameraId", orgCameraEntity.getManufacturerDeviceId());
                intent.putExtra("cameraName", orgCameraEntity.getDeviceName());
                intent.putExtra("cameraSn", orgCameraEntity.getSn());
                intent.putExtra("stateReal", orgCameraEntity.getDeviceData());
                intent.putExtra("cover", orgCameraEntity.getCoverUrl());
                intent.putExtra("entity", orgCameraEntity);
                startActivity(intent);
            }
        });
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

    private void showArrowUp(int typePosition) {
        switch (typePosition) {
            case 0:
                //range
                if (mPreStatePosition == 0) {
                    mDeviceStateTv.setCompoundDrawables(null, null, mFilterArrowUpBlackDrawable, null);
                } else {
                    mDeviceStateTv.setCompoundDrawables(null, null, mFilterArrowUpYellowDrawable, null);
                }
                break;
            case 1:
                //type
                if (mPreTypePosition == 0) {
                    mDeviceTypeTv.setCompoundDrawables(null, null, mFilterArrowUpBlackDrawable, null);
                } else {
                    mDeviceTypeTv.setCompoundDrawables(null, null, mFilterArrowUpYellowDrawable, null);
                }
                break;
        }
    }


    private void showArrowDown(int typePosition) {
        switch (typePosition) {
            case 0:
                //type
                if (mPreStatePosition == 0) {
                    //第一个，全部，则颜色变回黑色
                    mDeviceStateTv.setText(R.string.device_state);
                    mDeviceStateTv.setTextColor(getResources().getColor(R.color.gray_212121));
                    mDeviceStateTv.setCompoundDrawables(null, null, mFilterArrowDownBlackDrawable, null);
                } else {
                    mDeviceStateTv.setText(mStateEntities.get(mPreStatePosition).getName());
                    mDeviceStateTv.setTextColor(getResources().getColor(R.color.yellow_ff8f00));
                    mDeviceStateTv.setCompoundDrawables(null, null, mFilterArrowDownYellowDrawable, null);
                }
                break;
            case 1:
                //state
                if (mPreTypePosition == 0) {
                    //第一个，全部，则颜色变回黑色
                    mDeviceTypeTv.setText(R.string.device_type);
                    mDeviceTypeTv.setTextColor(getResources().getColor(R.color.gray_212121));
                    mDeviceTypeTv.setCompoundDrawables(null, null, mFilterArrowDownBlackDrawable, null);
                } else {
                    mDeviceTypeTv.setText(mTypeEntities.get(mPreTypePosition).getName());
                    mDeviceTypeTv.setTextColor(getResources().getColor(R.color.yellow_ff8f00));
                    mDeviceTypeTv.setCompoundDrawables(null, null, mFilterArrowDownYellowDrawable, null);
                }
                break;
        }
    }

    @OnClick({R.id.head_left_iv, R.id.head_right_iv, R.id.tv_search,
            R.id.device_state_ll, R.id.device_type_ll})
    public void onViewClicked(View view) {
        int visibility = mShaixuanLl.getVisibility();
        switch (view.getId()) {
            case R.id.head_left_iv:
                finish();
                break;
            case R.id.tv_search:
            case R.id.head_right_iv:
                //进行搜索
                ActivityOptions activityOptions = null;
                Intent intent = new Intent(RealVideosListActivity.this, CloudSearchActivity.class);
                intent.putExtra("type", "device");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    activityOptions = ActivityOptions.makeSceneTransitionAnimation(RealVideosListActivity.this, tvSearch, "searchText");
                    startActivity(intent, activityOptions.toBundle());
                } else {
                    startActivity(intent);
                }
                break;
            case R.id.device_state_ll:
                //状态（在线、离线）
                mFilterRclv.setVisibility(View.VISIBLE);
                mDailyFilterAdapter.setNewData(mStateEntities);
                menuView = new MenuView(RealVideosListActivity.this, mFilterRclv, getViewHeight(mStateEntities));
                menuView.show(mDeviceStateLl);
                menuView.setOnDismissListener(new MenuView.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        showArrowDown(mPosition);
                    }
                });
                if (visibility == View.VISIBLE) {
                    if (mPosition != 0) {
                        showArrowUp(0);
                        showArrowDown(mPosition);
                    } else {
                        showArrowDown(0);
                    }
                } else {
                    showArrowUp(0);
                }
                mPosition = 0;
                /*if (visibility == View.VISIBLE) {
                    if (mPosition != 0) {
                        showArrowUp(0);
                        mDailyFilterAdapter.setNewData(mStateEntities);
                        showArrowDown(mPosition);

                    } else {
                        mShaixuanLl.startAnimation(mTopOut);
                        mShaixuanLl.setVisibility(View.GONE);
                        showArrowDown(0);
                    }
                } else {
                    mDailyFilterAdapter.setNewData(mStateEntities);
                    mShaixuanLl.setVisibility(View.VISIBLE);
                    mShaixuanLl.startAnimation(mTopIn);
                    showArrowUp(0);
                }
                mPosition = 0;*/
                break;
            case R.id.device_type_ll:
                //设备类型（抓拍机、枪机、球机、手机、门禁等）
                mFilterRclv.setVisibility(View.VISIBLE);
                mDailyFilterAdapter.setNewData(mTypeEntities);
                menuView = new MenuView(RealVideosListActivity.this, mFilterRclv, getViewHeight(mTypeEntities));
                menuView.show(mDeviceStateLl);
                menuView.setOnDismissListener(new MenuView.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        showArrowDown(mPosition);
                    }
                });
                if (visibility == View.VISIBLE) {
                    if (mPosition != 1) {
                        showArrowUp(1);
                        showArrowDown(mPosition);
                    } else {
                        showArrowDown(1);
                    }
                } else {
                    showArrowUp(1);
                }
                mPosition = 1;
               /* if (visibility == View.VISIBLE) {
                    if (mPosition != 1) {
                        showArrowUp(1);
                        mDailyFilterAdapter.setNewData(mTypeEntities);
                        showArrowDown(mPosition);

                    } else {
                        mShaixuanLl.startAnimation(mTopOut);
                        mShaixuanLl.setVisibility(View.GONE);
                        showArrowDown(1);
                    }
                } else {
                    mDailyFilterAdapter.setNewData(mTypeEntities);
                    mShaixuanLl.setVisibility(View.VISIBLE);
                    mShaixuanLl.startAnimation(mTopIn);
                    showArrowUp(1);
                }
                mPosition = 1;*/
                break;
            case R.id.shaixuan_ll:
                mShaixuanLl.startAnimation(mTopOut);
                mShaixuanLl.setVisibility(View.GONE);
                showArrowDown(mPosition);
                break;
        }
    }

    private int getViewHeight(List list) {
        return list.size() * getResources().getDimensionPixelSize(R.dimen.dp40) + list.size() - 1;
    }

    @Override
    public void onRefresh() {
        mCameraAdapter.setEnableLoadMore(false);
        mPageNo = 1;
        mPresenter.getCameraCovers(mDeviceState, mDeviceType, null, mPageNo, PAGE_SIZE);
    }

    @Override
    public Activity getFragmentActivity() {
        return this;
    }

    @Override
    public void getAllCamerasSuccess(List<OrgCameraEntity> list) {
        mRealVideoSrfl.setRefreshing(false);
        mCameraAdapter.setEnableLoadMore(true);
        if (list != null) {
            if (1 == mPageNo) {
                if (list.size() == 0) {
                    //展示无数据页面
                    mEmptyTv.setVisibility(View.VISIBLE);
                    mRealVideoRclv.setVisibility(View.GONE);
                } else {
                    //展示数据页面
                    mEmptyTv.setVisibility(View.GONE);
                    mRealVideoRclv.setVisibility(View.VISIBLE);
                }
                mCameraAdapter.setNewData(list);
                if (list.size() < PAGE_SIZE) {
                    mCameraAdapter.disableLoadMoreIfNotFullPage(mRealVideoRclv);
                }
                if (list.size() != 0) {
                    mRealVideoRclv.scrollToPosition(0);
                }
            } else {
                mRealVideoSrfl.setEnabled(true);
                mCameraAdapter.addData(list);
                if (list.size() < PAGE_SIZE) {
                    mCameraAdapter.loadMoreEnd(false);
                } else {
                    mCameraAdapter.loadMoreComplete();
                }
            }
        } else {
            if (1 == mPageNo) {
                //展示无数据页
                mEmptyTv.setVisibility(View.VISIBLE);
                mRealVideoRclv.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void getAllCameraFail() {
        if (1 == mPageNo) {
            //展示无数据页
            mEmptyTv.setVisibility(View.VISIBLE);
            mRealVideoRclv.setVisibility(View.GONE);
            mRealVideoSrfl.setRefreshing(false);
        }
    }

    @Override
    public void onLoadMoreRequested() {
        mPageNo++;
        mRealVideoSrfl.setEnabled(false);
        mPresenter.getCameraCovers(mDeviceState, mDeviceType, null, mPageNo, PAGE_SIZE);
    }
}
