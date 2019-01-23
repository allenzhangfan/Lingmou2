package cloud.antelope.lingmou.mvp.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.utils.LogUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.gson.ListHistoryTypeAdapter;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.di.component.DaggerCloudHistoryComponent;
import cloud.antelope.lingmou.di.module.CloudHistoryModule;
import cloud.antelope.lingmou.mvp.contract.CloudHistoryContract;
import cloud.antelope.lingmou.mvp.model.entity.CameraNewStreamEntity;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreEntity;
import cloud.antelope.lingmou.mvp.model.entity.HistoryKVStoreRequest;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;
import cloud.antelope.lingmou.mvp.presenter.CloudHistoryPresenter;
import cloud.antelope.lingmou.mvp.ui.activity.VideoPlayActivity;
import cloud.antelope.lingmou.mvp.ui.adapter.CloudHistoryAdapter;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class CloudHistoryFragment extends BaseFragment<CloudHistoryPresenter>
        implements CloudHistoryContract.View,
        SwipeRefreshLayout.OnRefreshListener,
        BaseQuickAdapter.RequestLoadMoreListener {

    private static final int TYPE_HISTORY_VIDEO_PLAY = 0x02;
    @BindView(R.id.collect_rclv)
    RecyclerView mCollectRclv;
    @BindView(R.id.collect_srfl)
    SwipeRefreshLayout mCollectSrfl;
    @BindView(R.id.empty_tv)
    TextView mNoResultTv;
    @BindView(R.id.empty_no_network_tv)
    TextView mEmptyNoNetworkTv;

    @Inject
    RecyclerView.LayoutManager mLayoutManager;
    @Inject
    CloudHistoryAdapter mCameraAdapter;
    @Inject
    RecyclerView.ItemAnimator mItemAnimator;
    @Inject
    RecyclerView.ItemDecoration mItemDecoration;
    @Inject
    LoadMoreView mLoadMoreView;


    private boolean mIsFirstIn = true;
    List<OrgCameraEntity> mOrgCameraEntities;
    List<OrgCameraEntity> mMoreCameraEntities;
    List<OrgCameraEntity> mTotalCameraEntities;

    private static int PAGE_SIZE = 6;
    private int mCurrentStart = 0;
    private int mClickPosition = -1;

    public static CloudHistoryFragment newInstance() {
        CloudHistoryFragment fragment = new CloudHistoryFragment();
        return fragment;
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerCloudHistoryComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .cloudHistoryModule(new CloudHistoryModule(this))
                .build()
                .inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cloud_history, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mCollectSrfl.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        mCollectSrfl.setOnRefreshListener(this);
        mCollectRclv.setHasFixedSize(false);

        mCollectRclv.setLayoutManager(mLayoutManager);
        mCollectRclv.setItemAnimator(mItemAnimator);
        // mCollectRclv.addItemDecoration(mItemDecoration);
        mCollectRclv.setAdapter(mCameraAdapter);
        mOrgCameraEntities = new ArrayList<>();
        mMoreCameraEntities = new ArrayList<>();
        mTotalCameraEntities = new ArrayList<>();

        mCameraAdapter.setOnLoadMoreListener(this, mCollectRclv);
        mCameraAdapter.setLoadMoreView(mLoadMoreView);

        mCollectRclv.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (null != mCollectSrfl) {
                    mCollectSrfl.setRefreshing(true);
                    onRefresh();
                }
            }
        }, 100);
        mCameraAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                boolean hasVideoLive = SPUtils.getInstance().getBoolean(Constants.PERMISSION_HAS_VIDEO_LIVE, false);
                if (hasVideoLive) {
                    /*OrgCameraEntity cameraItem = (OrgCameraEntity) adapter.getItem(position);
                    Intent intent = new Intent(getContext(), PlayerActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data", cameraItem);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    */
                    OrgCameraEntity entity = (OrgCameraEntity) adapter.getItem(position);
                    if (entity == null) return;
                    Intent intent = new Intent(getContext(), VideoPlayActivity.class);
                    intent.putExtra("cameraId", entity.getManufacturerDeviceId());
                    intent.putExtra("cameraName", entity.getDeviceName());
                    intent.putExtra("cameraSn", entity.getSn());
                    mClickPosition = position;
                    startActivityForResult(intent, TYPE_HISTORY_VIDEO_PLAY);
                } else {
                    ToastUtils.showShort(R.string.hint_no_permission);
                }

            }
        });
    }

    // @Override
    // public void onResume() {
    //     super.onResume();
    //     if (mIsFirstIn) {
    //         mIsFirstIn = false;
    //     } else {
    //         mCollectRclv.postDelayed(new Runnable() {
    //             @Override
    //             public void run() {
    //                 if (null != mCollectSrfl) {
    //                     mCollectSrfl.setRefreshing(true);
    //                     onRefresh();
    //                 }
    //             }
    //         }, 100);
    //         /*List<CameraItem> cameras = DataSupport.findAll(CameraItem.class);
    //         if (null != cameras && !cameras.isEmpty()) {
    //             showHistories();
    //             Collections.sort(cameras, new CameraItemComparator());
    //             mCameraAdapter.setNewData(cameras);
    //         } else {
    //             // mCameraAdapter.setNewData(null);
    //             showEmpty();
    //         }*/
    //
    //     }
    // }

    /*@Subscriber(tag = CAMERA_COUNT_CHANGE)
    public void changeHistory(VideoClickBean bean) {
        mCollectRclv.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (null != mCollectSrfl) {
                    mCollectSrfl.setRefreshing(true);
                    onRefresh();
                }
            }
        }, 100);
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            if (requestCode == TYPE_HISTORY_VIDEO_PLAY) {
                if (-1 != mClickPosition) {
                    OrgCameraEntity entity = mTotalCameraEntities.get(mClickPosition);
                    if (null != data) {
                        long changeTime = data.getLongExtra("changeTime", -1L);
                        if (-1L != changeTime) {
                            entity.setTimeStamps(changeTime);
                        }
                    }
                    Collections.sort(mTotalCameraEntities, new Comparator<OrgCameraEntity>() {
                        @Override
                        public int compare(OrgCameraEntity o1, OrgCameraEntity o2) {
                            if (o2.getTimeStamps() > o1.getTimeStamps()) {
                                return 1;
                            } else if (o2.getTimeStamps() == o1.getTimeStamps()) {
                                return 0;
                            } else {
                                return -1;
                            }
                        }
                    });
                    mCameraAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void setData(@Nullable Object data) {

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

    }

    @Override
    public void onRefresh() {
        mCurrentStart = 0;
        mMoreCameraEntities.clear();
        mTotalCameraEntities.clear();
        mPresenter.getHistories();
    }

    @Override
    public Activity getFragmentActivity() {
        return getActivity();
    }

    @Override
    public void getHistoriesSuccess(GetKeyStoreBaseEntity entity) {
        mCollectSrfl.setRefreshing(false);
        if (entity != null) {
            GetKeyStoreEntity storeEntity = entity.getUserKvStroe();
            if (null != storeEntity) {
                String storeValue = storeEntity.getStoreValue();
                if (!TextUtils.isEmpty(storeValue)) {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.registerTypeAdapter(ListHistoryTypeAdapter.TYPE,
                            ListHistoryTypeAdapter.getInstance());
                    Gson gson = gsonBuilder.create();
                    List<HistoryKVStoreRequest.History> histories = null;
                    try {
                        histories = gson.fromJson(storeValue, ListHistoryTypeAdapter.TYPE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (histories != null && !histories.isEmpty()) {
                        showHistories();
                        List<Long> cameraIds = new ArrayList<>();
                        mOrgCameraEntities.clear();
                        for (HistoryKVStoreRequest.History history : histories) {
                            long cameraId = history.getCameraId();
                            OrgCameraEntity cameraEntity = new OrgCameraEntity();
                            cameraEntity.setManufacturerDeviceId(cameraId);
                            cameraEntity.setTimeStamps(history.getTimestamp());
                            cameraEntity.setDeviceName(history.getCameraName());
                            mOrgCameraEntities.add(cameraEntity);
                        }
                        Collections.sort(mOrgCameraEntities, new Comparator<OrgCameraEntity>() {
                            @Override
                            public int compare(OrgCameraEntity o1, OrgCameraEntity o2) {
                                if (o2.getTimeStamps() > o1.getTimeStamps()) {
                                    return 1;
                                } else if (o2.getTimeStamps() == o1.getTimeStamps()) {
                                    return 0;
                                } else {
                                    return -1;
                                }
                            }
                        });
                        //进行6个一组分配，加载更多
                        int totalSize = mOrgCameraEntities.size();
                        mMoreCameraEntities.clear();
                        if (totalSize <= PAGE_SIZE) {
                            //小于6，则全部加载
                            mMoreCameraEntities.addAll(mOrgCameraEntities);
                            mTotalCameraEntities.addAll(mOrgCameraEntities);
                        } else {
                            List<OrgCameraEntity> subList = mOrgCameraEntities.subList(0, PAGE_SIZE);
                            mMoreCameraEntities.addAll(subList);
                            mTotalCameraEntities.addAll(subList);
                        }
                        for (OrgCameraEntity cameraEntity : mMoreCameraEntities) {
                            cameraIds.add(cameraEntity.getManufacturerDeviceId());
                        }
                        mCurrentStart += 1;
                        Collections.sort(mTotalCameraEntities, new Comparator<OrgCameraEntity>() {
                            @Override
                            public int compare(OrgCameraEntity o1, OrgCameraEntity o2) {
                                if (o2.getTimeStamps() > o1.getTimeStamps()) {
                                    return 1;
                                } else if (o2.getTimeStamps() == o1.getTimeStamps()) {
                                    return 0;
                                } else {
                                    return -1;
                                }
                            }
                        });
                        mCameraAdapter.setNewData(mTotalCameraEntities);
                        Long[] cids = cameraIds.toArray(new Long[cameraIds.size()]);
                        mPresenter.getCameraCovers(cids);
                    } else {
                        showEmpty();
                    }
                } else {
                    showEmpty();
                }
            } else {
                showEmpty();
            }
        } else {
            showEmpty();
        }
    }

    @Override
    public void getHistoriesFail() {
        if (null != mCollectSrfl) {
            mCollectSrfl.setRefreshing(false);
        }
        showNoNetwork();
    }

    private void showEmpty() {
        mCollectRclv.setVisibility(View.GONE);
        mNoResultTv.setVisibility(View.VISIBLE);
        mEmptyNoNetworkTv.setVisibility(View.GONE);
    }

    private void showNoNetwork() {
        mCollectRclv.setVisibility(View.GONE);
        mNoResultTv.setVisibility(View.GONE);
        mEmptyNoNetworkTv.setVisibility(View.VISIBLE);
    }

    private void showHistories() {
        mCollectRclv.setVisibility(View.VISIBLE);
        mNoResultTv.setVisibility(View.GONE);
        mEmptyNoNetworkTv.setVisibility(View.GONE);
    }

    @Override
    public void getCoversSuccess(CameraNewStreamEntity entity) {
        List<CameraNewStreamEntity.DevicesBean> devices = entity.getDevices();
        if (null != devices && !devices.isEmpty()) {
            for (CameraNewStreamEntity.DevicesBean devicesBean : devices) {
                Long cid = devicesBean.getCid();
                for (OrgCameraEntity cameraEntity : mTotalCameraEntities) {
                    Long deviceId = cameraEntity.getManufacturerDeviceId();
                    if (cid.equals(deviceId)) {
                        cameraEntity.setCoverUrl(devicesBean.getCover_url());
                        cameraEntity.setPlayUrl(devicesBean.getRtmp_url());
                        break;
                    }
                }
            }
            mCameraAdapter.notifyDataSetChanged();
            // mCameraAdapter.setNewData(mTotalCameraEntities);
        }
    }

    @Override
    public void onLoadMoreRequested() {
        int currentStart = mCurrentStart * PAGE_SIZE;
        LogUtils.i("cxm", "onLoadMoreRequested  currentStart = " + currentStart);
        if (mCameraAdapter.getData().size() >= mOrgCameraEntities.size()) {
            mCameraAdapter.loadMoreEnd(false);
            return;
        }
        int endIndex = currentStart + PAGE_SIZE < mOrgCameraEntities.size() ? currentStart + PAGE_SIZE : mOrgCameraEntities.size();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                List<OrgCameraEntity> subList = mOrgCameraEntities.subList(currentStart, endIndex);
                mMoreCameraEntities.clear();
                mMoreCameraEntities.addAll(subList);
                mTotalCameraEntities.addAll(mMoreCameraEntities);
                mCameraAdapter.notifyDataSetChanged();
                // mCameraAdapter.addData(mMoreCameraEntities);
                if (subList.size() < PAGE_SIZE) {
                    mCameraAdapter.loadMoreEnd(false);
                } else {
                    mCameraAdapter.loadMoreComplete();
                }
                if (!mMoreCameraEntities.isEmpty()) {
                    //同时去获取封面图
                    List<Long> cids = new ArrayList<>();
                    for (OrgCameraEntity entity : mMoreCameraEntities) {
                        cids.add(entity.getManufacturerDeviceId());
                    }
                    Long[] newCids = cids.toArray(new Long[mMoreCameraEntities.size()]);
                    mPresenter.getCameraCovers(newCids);
                }
            }
        }, 500);
        mCurrentStart += 1;
    }
}
