package cloud.antelope.lingmou.mvp.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
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
import com.lingdanet.safeguard.common.modle.PermissionDialog;
import com.lingdanet.safeguard.common.utils.LocationUtils;
import com.lingdanet.safeguard.common.utils.LogUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;

import org.litepal.crud.DataSupport;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.PermissionUtils;
import cloud.antelope.lingmou.app.utils.gson.ListHistoryTypeAdapter;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.di.component.DaggerCollectionComponent;
import cloud.antelope.lingmou.di.module.CollectionModule;
import cloud.antelope.lingmou.mvp.contract.CollectionContract;
import cloud.antelope.lingmou.mvp.model.entity.CameraNewStreamEntity;
import cloud.antelope.lingmou.mvp.model.entity.CollectCameraEntity;
import cloud.antelope.lingmou.mvp.model.entity.CollectChangeBean;
import cloud.antelope.lingmou.mvp.model.entity.CollectChangeEvent;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreEntity;
import cloud.antelope.lingmou.mvp.model.entity.HistoryKVStoreRequest;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;
import cloud.antelope.lingmou.mvp.model.entity.SetKeyStoreRequest;
import cloud.antelope.lingmou.mvp.model.entity.VideoClickBean;
import cloud.antelope.lingmou.mvp.presenter.CollectionPresenter;
import cloud.antelope.lingmou.mvp.ui.activity.CameraMapActivity;
import cloud.antelope.lingmou.mvp.ui.activity.PlayerNewActivity;
import cloud.antelope.lingmou.mvp.ui.activity.VideoPlayActivity;
import cloud.antelope.lingmou.mvp.ui.adapter.CollectCameraNewAdapter;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static cloud.antelope.lingmou.app.EventBusTags.CAMERA_COUNT_CHANGE;
import static cloud.antelope.lingmou.app.EventBusTags.CAMERA_STATUS_CHANGE;
import static com.jess.arms.utils.Preconditions.checkNotNull;


public class CollectionFragment extends BaseFragment<CollectionPresenter>
        implements CollectionContract.View,
        SwipeRefreshLayout.OnRefreshListener,
        PermissionUtils.HasPermission,
        EasyPermissions.PermissionCallbacks,
        BaseQuickAdapter.RequestLoadMoreListener {

    private static final int TYPE_VIDEO_PLAY = 0x01;
    @BindView(R.id.collect_rclv)
    RecyclerView mCollectRclv;
    @BindView(R.id.collect_srfl)
    SwipeRefreshLayout mCollectSrfl;
    @BindView(R.id.empty_tv)
    TextView mNoResultTv;
    @BindView(R.id.empty_no_network_tv)
    TextView mEmptyNoNetworkTv;

    @Inject
    CollectCameraNewAdapter mCameraAdapter;
    @Inject
    RecyclerView.ItemAnimator mItemAnimator;
    @Inject
    RecyclerView.ItemDecoration mItemDecoration;
    @Inject
    LoadMoreView mLoadMoreView;

    private OrgCameraEntity mClickCamera;

    List<OrgCameraEntity> mOrgCameraEntities;
    List<OrgCameraEntity> mNowTotalEntities;
    List<OrgCameraEntity> mMoreCameraEntities;
    private int mLoadMoreCount = 0;
    private static int PAGE_SIZE = 12;

    private int mClickPosition = -1;

    public static CollectionFragment newInstance() {
        CollectionFragment fragment = new CollectionFragment();
        return fragment;
    }

    @Override
    public void setupFragmentComponent(AppComponent appComponent) {
        DaggerCollectionComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .collectionModule(new CollectionModule(this))
                .build()
                .inject(this);
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.collection_layout, container, false);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mCollectSrfl.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        mCollectSrfl.setOnRefreshListener(this);
        mCollectRclv.setHasFixedSize(false);

        mCollectRclv.setLayoutManager(new LinearLayoutManager(getActivity()));
//        mCollectRclv.setItemAnimator(mItemAnimator);
        // mCollectRclv.addItemDecoration(mItemDecoration);
        mCollectRclv.setAdapter(mCameraAdapter);
        mCameraAdapter.setIsFromAllVideo(true);
        mCameraAdapter.setOnLoadMoreListener(this, mCollectRclv);
        mCameraAdapter.setLoadMoreView(mLoadMoreView);
        mOrgCameraEntities = new ArrayList<>();
        mMoreCameraEntities = new ArrayList<>();
        mNowTotalEntities = new ArrayList<>();

        mCollectRclv.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (null != mCollectSrfl) {
                    mCollectSrfl.setRefreshing(true);
                    onRefresh();
                }
            }
        }, 100);
        initListener();
    }

    private void initListener() {
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
                    try {
                        OrgCameraEntity entity = (OrgCameraEntity) adapter.getItem(position);
                        Intent intent = new Intent(getContext(), VideoPlayActivity.class);
                        intent.putExtra("cameraId", entity.getManufacturerDeviceId());
                        intent.putExtra("cameraName", entity.getDeviceName());
                        intent.putExtra("cameraSn", entity.getSn());
                        mClickPosition = position;
                        startActivity(intent);
                    } catch (Exception e) {

                    }
                } else {
                    ToastUtils.showShort(R.string.hint_no_permission);
                }

            }
        });
        mCameraAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                mClickCamera = (OrgCameraEntity) adapter.getItem(position);
                checkLocationPerm();
                if (!LocationUtils.isGpsEnabled()) {
                    LocationUtils.openGpsSettings();
                }
            }
        });
    }

    /*@Subscriber(tag = CAMERA_STATUS_CHANGE)
    public void changeCollections(CollectChangeBean bean) {
        mCollectRclv.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (null != mCollectSrfl) {
                    mCollectSrfl.setRefreshing(true);
                    onRefresh();
                }
            }
        }, 100);
    }

    @Subscriber(tag = CAMERA_COUNT_CHANGE)
    public void changeCountCollections(VideoClickBean bean) {
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

   /* @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            if (requestCode == TYPE_VIDEO_PLAY) {
                if (-1 != mClickPosition) {
                    if (mNowTotalEntities.size() > 0) {
                        OrgCameraEntity entity = mNowTotalEntities.get(mClickPosition);
                        entity.setViewTimes(entity.getViewTimes() + 1L);
                        Collections.sort(mNowTotalEntities, new Comparator<OrgCameraEntity>() {
                            @Override
                            public int compare(OrgCameraEntity o1, OrgCameraEntity o2) {
                                return (int) (o2.getViewTimes() - o1.getViewTimes());
                            }
                        });
                        mCameraAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }*/

    @Override
    public void onRefresh() {
        mLoadMoreCount = 0;
        mNowTotalEntities.clear();
        mCameraAdapter.setEnableLoadMore(false);
        mPresenter.getCollections();
    }

    @Override
    public void getCollectionsSuccess(GetKeyStoreBaseEntity organizationEntity) {
        if (null != organizationEntity) {
            GetKeyStoreEntity userKvStroe = organizationEntity.getUserKvStroe();
            if (null != userKvStroe) {
                showCollections();
                String storeValue = userKvStroe.getStoreValue();
                if (!TextUtils.isEmpty(storeValue)) {
                    Gson gson = new Gson();
                    SetKeyStoreRequest keyStore = gson.fromJson(storeValue, SetKeyStoreRequest.class);
                    List<String> storeSets = keyStore.getSets();
                    if (null == storeSets || storeSets.isEmpty()) {
                        SPUtils.getInstance().put(Constants.COLLECT_JSON, "");
                        showEmpty();
                        return;
                    }
                    SPUtils.getInstance().put(Constants.COLLECT_JSON, storeValue);
                    List<CollectCameraEntity> collectCameraList = new ArrayList<>();
                    for (String str : storeSets) {
                        CollectCameraEntity entity = new CollectCameraEntity();
                        String[] group_one = str.split(":");
                        entity.setGroup(group_one[0]);
                        String[] group_two = group_one[1].split("/");
                        entity.setCid(group_two[0]);
                        entity.setCameraName(group_two[1]);
                        collectCameraList.add(entity);
                    }
                    int size = collectCameraList.size();
                    List<CollectCameraEntity> repeatIndexs = new ArrayList<>();
                    for (int i = 0; i < size; i++) {
                        CollectCameraEntity iEntity = collectCameraList.get(i);
                        String iCid = iEntity.getCid();
                        if (i == size - 1) {
                            break;
                        }
                        for (int j = i + 1; j < size; j++) {
                            CollectCameraEntity jEntity = collectCameraList.get(j);
                            if (iCid.equals(jEntity.getCid())) {
                                repeatIndexs.add(jEntity);
                            }
                        }
                    }
                    for (CollectCameraEntity indexEntity : repeatIndexs) {
                        collectCameraList.remove(indexEntity);
                    }
                    mOrgCameraEntities.clear();
                    for (CollectCameraEntity entity : collectCameraList) {
                        // OrgCameraEntity orgCameraEntity = new OrgCameraEntity();
                        // Cursor cursor = DataSupport.findBySQL("select * from orgcameraentity where manufacturerdeviceid = '" + entity.getCid()+"'");
                        List<OrgCameraEntity> cursorEntities = DataSupport.where("manufacturerdeviceid = ?", entity.getCid()).find(OrgCameraEntity.class);
                        if (cursorEntities != null && !cursorEntities.isEmpty()) {
                            OrgCameraEntity cameraEntity = cursorEntities.get(0);
                            mOrgCameraEntities.add(cameraEntity);
                        }
                        /*else {
                            OrgCameraEntity orgCameraEntity = new OrgCameraEntity();
                            orgCameraEntity.setManufacturerDeviceId(Long.parseLong(entity.getCid()));
                            orgCameraEntity.setDeviceName(entity.getCameraName());
                            orgCameraEntities.add(orgCameraEntity);
                        }*/
                    }
                    mPresenter.getHistories();
                    if (mOrgCameraEntities.isEmpty()) {
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
    public void getCollectionsFail() {
        if (null != mCollectSrfl) {
            mCollectSrfl.setRefreshing(false);
        }
        showNoNetwork();
    }

    @Override
    public void getCoversSuccess(CameraNewStreamEntity entity) {
        List<CameraNewStreamEntity.DevicesBean> devices = entity.getDevices();
        if (null != devices && !devices.isEmpty()) {
            for (CameraNewStreamEntity.DevicesBean devicesBean : devices) {
                Long cid = devicesBean.getCid();
                for (OrgCameraEntity cameraEntity : mNowTotalEntities) {
                    Long deviceId = cameraEntity.getManufacturerDeviceId();
                    if (cid.equals(deviceId)) {
                        cameraEntity.setCoverUrl(devicesBean.getCover_url());
                        cameraEntity.setPlayUrl(devicesBean.getRtmp_url());
                        break;
                    }
                }
            }
            mCameraAdapter.setNewData(mNowTotalEntities);
            // mCameraAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void getHistoriesSuccess(GetKeyStoreBaseEntity entity) {
        mCollectSrfl.setRefreshing(false);
        if (entity != null) {
            GetKeyStoreEntity storeEntity = entity.getUserKvStroe();
            if (null != storeEntity) {
                String storeValue = storeEntity.getStoreValue();
                if (!TextUtils.isEmpty(storeValue)) {
                    boolean found = false;
                    boolean exception = false;
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.registerTypeAdapter(ListHistoryTypeAdapter.TYPE,
                            ListHistoryTypeAdapter.getInstance());
                    Gson gson = gsonBuilder.create();
                    List<HistoryKVStoreRequest.History> histories = null;
                    try {
                        histories = gson.fromJson(storeValue, ListHistoryTypeAdapter.TYPE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        exception = true;
                    }
                    if (histories != null && !histories.isEmpty()) {
                        for (HistoryKVStoreRequest.History history : histories) {
                            long cameraId = history.getCameraId();
                            for (OrgCameraEntity cameraEntity : mOrgCameraEntities) {
                                Long deviceId = cameraEntity.getManufacturerDeviceId();
                                if (cameraId == deviceId) {
                                    cameraEntity.setViewTimes(history.getTimes());
                                    break;
                                }
                            }
                        }
                        Collections.sort(mOrgCameraEntities, new Comparator<OrgCameraEntity>() {
                            @Override
                            public int compare(OrgCameraEntity o1, OrgCameraEntity o2) {
                                return (int) (o2.getViewTimes() - o1.getViewTimes());
                            }
                        });
                        caculateDataEntity();
                    } else {
                        //第一次进，或者没有次数统计的时候
                        caculateDataEntity();
                    }
                } else {
                    //第一次进，或者没有次数统计的时候
                    caculateDataEntity();
                }
            } else {
                //第一次进，或者没有次数统计的时候
                caculateDataEntity();
            }
        }
    }

    private void caculateDataEntity() {
        //先判断总的数量，然后以12个一组分开
        int totalSize = mOrgCameraEntities.size();
        mMoreCameraEntities.clear();
        mCameraAdapter.setEnableLoadMore(true);
        mLoadMoreCount += 1;
        if (totalSize > PAGE_SIZE) {
            List<OrgCameraEntity> subList = mOrgCameraEntities.subList(0, PAGE_SIZE);
            mMoreCameraEntities.addAll(subList);
            mNowTotalEntities.addAll(subList);
            mCameraAdapter.loadMoreComplete();
        } else {
            mMoreCameraEntities.addAll(mOrgCameraEntities);
            mNowTotalEntities.addAll(mOrgCameraEntities);
            mCameraAdapter.loadMoreEnd(false);
        }
        if (!mMoreCameraEntities.isEmpty()) {
            //同时去获取封面图
            List<Long> cids = new ArrayList<>();
            for (OrgCameraEntity newEntity : mMoreCameraEntities) {
                cids.add(newEntity.getManufacturerDeviceId());
            }
            Long[] newCids = cids.toArray(new Long[mMoreCameraEntities.size()]);
            mPresenter.getCameraCovers(newCids);
        }
        mCameraAdapter.setNewData(mNowTotalEntities);
    }

    @Override
    public void getHistoriesFail() {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    private void showEmpty() {
        mCollectSrfl.setRefreshing(false);
        mCollectRclv.setVisibility(View.GONE);
        mNoResultTv.setVisibility(View.VISIBLE);
        mEmptyNoNetworkTv.setVisibility(View.GONE);
    }

    private void showNoNetwork() {
        mCollectRclv.setVisibility(View.GONE);
        mNoResultTv.setVisibility(View.GONE);
        mEmptyNoNetworkTv.setVisibility(View.VISIBLE);
    }

    private void showCollections() {
        mCollectRclv.setVisibility(View.VISIBLE);
        mNoResultTv.setVisibility(View.GONE);
        mEmptyNoNetworkTv.setVisibility(View.GONE);
    }


    /**
     * 此方法是让外部调用使fragment做一些操作的,比如说外部的activity想让fragment对象执行一些方法,
     * 建议在有多个需要让外界调用的方法时,统一传Message,通过what字段,来区分不同的方法,在setData
     * 方法中就可以switch做不同的操作,这样就可以用统一的入口方法做不同的事
     * <p>
     * 使用此方法时请注意调用时fragment的生命周期,如果调用此setData方法时onCreate还没执行
     * setData里却调用了presenter的方法时,是会报空的,因为dagger注入是在onCreated方法中执行的,然后才创建的presenter
     * 如果要做一些初始化操作,可以不必让外部调setData,在initData中初始化就可以了
     *
     * @param data
     */

    @Override
    public void setData(Object data) {

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
        if (null != mCollectSrfl && mCollectSrfl.isRefreshing()) {
            mCollectSrfl.setRefreshing(false);
        }
        showNoNetwork();
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {

    }

    @AfterPermissionGranted(PermissionUtils.RC_LOCATION_PERM)
    public void checkLocationPerm() {
        PermissionUtils.locationTask(this);
    }

    @Override
    public void doNext(int permId) {
        if (PermissionUtils.RC_LOCATION_PERM == permId) {
            Intent intent = new Intent(getFragmentActivity(), CameraMapActivity.class);
            intent.putExtra(Constants.SELECT_CAMERA, mClickCamera);
            startActivity(intent);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            if (perms.contains(Manifest.permission.ACCESS_FINE_LOCATION)) {
                final PermissionDialog dialog = new PermissionDialog(getActivity());
                dialog.setTitle(getString(R.string.request_permission_tag));
                dialog.setMessage(getString(R.string.need_location_permission_tips));
                dialog.setNegativeListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        ToastUtils.showShort(R.string.can_not_get_location);
                    }
                });
                dialog.show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public Activity getFragmentActivity() {
        return this.getActivity();
    }

    @Override
    public void onLoadMoreRequested() {
        int currentStart = mLoadMoreCount * PAGE_SIZE;
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
                // mNowTotalEntities.addAll(subList);
                mCameraAdapter.addData(mMoreCameraEntities);
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
                    // mPresenter.getHistories();
                }
            }
        }, 500);
        mLoadMoreCount += 1;
    }

    @Subscriber(tag = CAMERA_STATUS_CHANGE)
    public void modifyList(CollectChangeBean event) {
        onRefresh();
    }
}
