package cloud.antelope.lingmou.mvp.ui.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.google.gson.Gson;
import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.umeng.analytics.MobclickAgent;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.EventBusTags;
import cloud.antelope.lingmou.app.utils.PermissionUtils;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.di.component.DaggerSearchListComponent;
import cloud.antelope.lingmou.di.module.SearchListModule;
import cloud.antelope.lingmou.mvp.contract.SearchListContract;
import cloud.antelope.lingmou.mvp.model.entity.AlarmChangeBean;
import cloud.antelope.lingmou.mvp.model.entity.CollectCameraEntity;
import cloud.antelope.lingmou.mvp.model.entity.CollectChangeBean;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;
import cloud.antelope.lingmou.mvp.model.entity.SearchBean;
import cloud.antelope.lingmou.mvp.model.entity.SearchFragment;
import cloud.antelope.lingmou.mvp.model.entity.SetKeyStoreRequest;
import cloud.antelope.lingmou.mvp.presenter.SearchListPresenter;
import cloud.antelope.lingmou.mvp.ui.activity.BodyDepotActivity;
import cloud.antelope.lingmou.mvp.ui.activity.DeviceShowMapActivity;
import cloud.antelope.lingmou.mvp.ui.activity.FaceDepotActivity;
import cloud.antelope.lingmou.mvp.ui.activity.PlayerNewActivity;
import cloud.antelope.lingmou.mvp.ui.activity.VideoDetailActivity;
import cloud.antelope.lingmou.mvp.ui.adapter.SearchAlertAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.SearchBodyAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.SearchFaceAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.SearchVideoAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.dialog.DeviceOperationDialog;
import pub.devrel.easypermissions.AfterPermissionGranted;

import static cloud.antelope.lingmou.app.EventBusTags.CAMERA_STATUS_CHANGE;

public class SearchListFragment extends BaseFragment<SearchListPresenter> implements SearchListContract.View, SearchFragment, SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener, PermissionUtils.HasPermission {
    public static final int FACE = 0;
    public static final int BODY = 1;
    public static final int ALERT = 2;
    public static final int VIDEO = 3;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.srl)
    SwipeRefreshLayout srl;
    @BindView(R.id.rl_empty)
    RelativeLayout rlEmpty;
    @BindView(R.id.no_permission_rl)
    RelativeLayout noPermissionRl;
    @BindView(R.id.tv_no_network)
    TextView tvNoNetwork;

    @Inject
    SearchFaceAdapter mFaceAdapter;
    @Inject
    SearchBodyAdapter mBodyAdapter;
    @Inject
    SearchAlertAdapter mAlertAdapter;
    @Inject
    SearchVideoAdapter mVedioAdapter;
    @Inject
    LoadMoreView mLoadMoreView;
    private boolean hasMore;
    int pageNum = 1;
    int pageSize = 20;
    Integer statusType;
    String minId;
    String minCaptureTime;
    String pageType;
    private BaseQuickAdapter mAdapter;
    private String keyword;
    private boolean visible;
    private boolean needRefresh;
    private boolean video;
    private boolean face;
    private boolean body;
    private DeviceOperationDialog dialog;
    private OrgCameraEntity mClickCamera;
    public void setStatusType(Integer statusType) {
        this.statusType = statusType;
    }

    @Override
    public void onRefresh() {
        needRefresh = false;
        pageNum = 1;
        search();
    }

    private void search() {
        pageType = pageNum == 1 ? "0" : "2";

        switch (statusType) {
            case FACE:
                if (!face) {
                    rv.setVisibility(View.GONE);
                    noPermissionRl.setVisibility(View.VISIBLE);
                    return;
                }
                if (!mFaceAdapter.getData().isEmpty()) {
                    minId = mFaceAdapter.getData().get(mFaceAdapter.getData().size() - 1).id;
                    minCaptureTime = mFaceAdapter.getData().get(mFaceAdapter.getData().size() - 1).captureTime;
                }
                mPresenter.searchFace(keyword, pageNum, pageSize, minId, minCaptureTime, pageType);
                break;
            case BODY:
                if (!body) {
                    rv.setVisibility(View.GONE);
                    noPermissionRl.setVisibility(View.VISIBLE);
                    return;
                }
                if (!mBodyAdapter.getData().isEmpty()) {
                    minId = mBodyAdapter.getData().get(mBodyAdapter.getData().size() - 1).id;
                    minCaptureTime = mBodyAdapter.getData().get(mBodyAdapter.getData().size() - 1).captureTime;
                }
                mPresenter.searchBody(keyword, pageNum, pageSize, minId, minCaptureTime, pageType);
                break;
            case ALERT:
                if (getArguments() == null) {
                    mPresenter.searchAlarms(pageNum, pageSize, keyword, null, null, 1, null, null);
                } else {
                    mPresenter.searchAlarms(pageNum, pageSize, keyword, getArguments().getInt("taskType") == -1 ? null : getArguments().getInt("taskType"), null, 1, null, null);
                }
                break;
            case VIDEO:
                if (!video) {
                    rv.setVisibility(View.GONE);
                    noPermissionRl.setVisibility(View.VISIBLE);
                    return;
                }
                mPresenter.searchDevices(null, null, keyword, pageNum, pageSize);
                break;
        }
    }

    @Override
    public void showList(List<SearchBean> list) {
        mAdapter.setNewData(list);
        if (list == null || list.isEmpty()) {
            hasMore = false;
            tvNoNetwork.setVisibility(View.GONE);
            rlEmpty.setVisibility(View.VISIBLE);
        } else {
            rlEmpty.setVisibility(View.GONE);
            tvNoNetwork.setVisibility(View.GONE);
            hasMore = list.size() >= pageSize;
            if (!hasMore) {
                mAdapter.loadMoreEnd(false);//加载完毕
            } else {
                mAdapter.loadMoreComplete();//加载中
            }
        }
    }

    @Override
    public void showMore(List<SearchBean> list) {
        tvNoNetwork.setVisibility(View.GONE);
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
            tvNoNetwork.setVisibility(View.VISIBLE);
            rlEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCameraLikeSuccess(GetKeyStoreBaseEntity entity, boolean like) {
        Gson gson = new Gson();
        String toJson = gson.toJson(mCurrentRequest);
        SPUtils.getInstance().put(Constants.COLLECT_JSON, toJson);
        if (!like) {
            ToastUtils.showShort(R.string.uncollect_success);
        } else {
            ToastUtils.showShort(R.string.collect_success);
        }
        for (OrgCameraEntity orgCameraEntity : ((SearchVideoAdapter)mAdapter).getData()) {
            orgCameraEntity.setSelect(isSelect(orgCameraEntity));
        }
        mAdapter.notifyDataSetChanged();
        dialog.setSelected(like);
        EventBus.getDefault().post(new CollectChangeBean(), CAMERA_STATUS_CHANGE);
    }
    private boolean isSelect(OrgCameraEntity item) {
        if (null == mCollectionCameras) {
            mCollectionCameras = new ArrayList<>();
        }
        for (CollectCameraEntity entity : mCollectionCameras) {
            if (TextUtils.equals(String.valueOf(item.getManufacturerDeviceId()), entity.getCid()))
                return true;
        }
        return false;
    }
    @Override
    public void onLoadMoreRequested() {
        if (hasMore) {
            pageNum++;
            search();
        }
    }

    @Override
    public void setupFragmentComponent(AppComponent appComponent) {
        DaggerSearchListComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .searchListModule(new SearchListModule(this))
                .build()
                .inject(this);
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_list, container, false);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        SPUtils utils = SPUtils.getInstance();
        video = utils.getBoolean(Constants.PERMISSION_HAS_VIDEO_LIVE);
        face = utils.getBoolean(Constants.PERMISSION_HAS_FACE);
        body = utils.getBoolean(Constants.PERMISSION_HAS_BODY);
        setDecoration(statusType);
        mAdapter = mFaceAdapter;
        rv.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        switch (statusType) {
            case FACE:
                mAdapter = mFaceAdapter;
                rv.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                break;
            case BODY:
                mAdapter = mBodyAdapter;
                rv.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                break;
            case ALERT:
                mAdapter = mAlertAdapter;
                rv.setLayoutManager(new LinearLayoutManager(getActivity()));
                break;
            case VIDEO:
                mVedioAdapter.setOnClickListener(new SearchVideoAdapter.OnClickListener() {
                    @Override
                    public void onClickMenu(OrgCameraEntity item) {
                        boolean hasVideoLive = SPUtils.getInstance().getBoolean(Constants.PERMISSION_HAS_VIDEO_LIVE, false);
                        if (hasVideoLive) {
                            dialog = new DeviceOperationDialog(getActivity(), item.isSelect());
                            dialog.setOnItemClickListener(new DeviceOperationDialog.OnItemClickListener() {
                                @Override
                                public void onClick(int position) {
                                    switch (position) {
                                        case 0:
                                            collectionCamera(item);
                                            break;
                                        case 1:
                                            MobclickAgent.onEvent(getActivity(), "video_toFaceLibrary");
                                            Intent faceIntent = new Intent(getActivity(), FaceDepotActivity.class);
                                            faceIntent.putExtra("cameraId", item.getManufacturerDeviceId());
                                            faceIntent.putExtra("fromDevice", true);
                                            startActivity(faceIntent);
                                            break;
                                        case 2:
                                            MobclickAgent.onEvent(getActivity(), "video_toBodyLibrary");
                                            Intent bodyIntent = new Intent(getActivity(), BodyDepotActivity.class);
                                            bodyIntent.putExtra("cameraId", item.getManufacturerDeviceId());
                                            bodyIntent.putExtra("fromDevice", true);
                                            startActivity(bodyIntent);
                                            break;
                                        case 3:
                                            mClickCamera = item;
                                            checkLocationPerm();
                                            break;
                                        case 4:
                                            MobclickAgent.onEvent(getActivity(), "video_deviceDetail");
                                            Intent intent = new Intent(getActivity(), VideoDetailActivity.class);
                                            intent.putExtra("cid", String.valueOf(item.getManufacturerDeviceId()));
                                            intent.putExtra("cameraName", item.getDeviceName());
                                            intent.putExtra("cameraSn", item.getSn());
                                            intent.putExtra("isFromOrgMap", true);
                                            startActivity(intent);
                                            break;
                                    }
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                        } else {
                            ToastUtils.showShort(R.string.hint_no_permission);
                        }
                    }

                    @Override
                    public void onClickItem(OrgCameraEntity entity) {
                        //进入直播界面
                        Intent playIntent = new Intent(getActivity(), PlayerNewActivity.class);
                        playIntent.putExtra("cameraId", entity.getManufacturerDeviceId());
                        playIntent.putExtra("cameraName", entity.getDeviceName());
                        playIntent.putExtra("cameraSn", entity.getSn());
                        playIntent.putExtra("coverUrl", entity.getCoverUrl());
                        startActivity(playIntent);
                    }
                });
                mAdapter = mVedioAdapter;
                rv.setLayoutManager(new LinearLayoutManager(getActivity()));
                break;
        }
        rv.setAdapter(mAdapter);
        mAdapter.setOnLoadMoreListener(this, rv);
        srl.setOnRefreshListener(this);
        mAdapter.setOnLoadMoreListener(this, rv);
        mAdapter.setLoadMoreView(mLoadMoreView);
        srl.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        srl.setEnabled(false);
    }

    public void setDecoration(int statusType) {
        if (statusType == ALERT) {
            rv.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    super.getItemOffsets(outRect, view, parent, state);
                    outRect.top = getResources().getDimensionPixelSize(R.dimen.dp8);
                    outRect.left = getResources().getDimensionPixelSize(R.dimen.dp8);
                    outRect.right = getResources().getDimensionPixelSize(R.dimen.dp8);
                }
            });
        } else if(statusType == FACE||statusType == BODY){
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
        }
    }

    @AfterPermissionGranted(PermissionUtils.RC_LOCATION_PERM)
    public void checkLocationPerm() {
        PermissionUtils.locationTask(this);
    }

    private ArrayList<CollectCameraEntity> mCollectionCameras;
    private SetKeyStoreRequest mKeyStore;
    private SetKeyStoreRequest mCurrentRequest;
    public void collectionCamera(OrgCameraEntity orgCameraEntity) {
        String storeValue = SPUtils.getInstance().getString(Constants.COLLECT_JSON);
        if (!TextUtils.isEmpty(storeValue)) {
            Gson gson = new Gson();
            mKeyStore = gson.fromJson(storeValue, SetKeyStoreRequest.class);
        }
        if (orgCameraEntity.isSelect()) {
            //取消收藏
            if (null == mCollectionCameras) {
                mCollectionCameras = new ArrayList<>();
            }
            Iterator<CollectCameraEntity> iterator = mCollectionCameras.iterator();
            while (iterator.hasNext()) {
                CollectCameraEntity next = iterator.next();
                if ((orgCameraEntity.getManufacturerDeviceId() + "").equals(next.getCid())) {
                    //干掉这个对象
                    iterator.remove();
                }
            }
            List<String> groups;
            if (null != mKeyStore) {
                groups = mKeyStore.getGroups();
            } else {
                groups = new ArrayList<>();
            }
                    /*Iterator<String> groupIterator = groups.iterator();
                    while (groupIterator.hasNext()) {
                        String group = groupIterator.next();
                        boolean hasGroup = false;
                        for (CollectCameraEntity entity : mCollectionCameras) {
                            if (group.equals(entity.getGroup())) {
                                hasGroup = true;
                                break;
                            }
                        }
                        if (!hasGroup) {
                            groupIterator.remove();
                        }
                    }*/
            List<String> collectionCameras = new ArrayList<>();
            for (CollectCameraEntity entity : mCollectionCameras) {
                String camera = entity.getGroup() + ":" + entity.getCid() + "/" + entity.getCameraName();
                collectionCameras.add(camera);
            }
            mCurrentRequest = new SetKeyStoreRequest();
            mCurrentRequest.setGroups(groups);
            mCurrentRequest.setSets(collectionCameras);
            mPresenter.cameraLike("MY_GROUP", mCurrentRequest, false);
        } else {
            //进行收藏
            if (null == mKeyStore) {
                mKeyStore = new SetKeyStoreRequest();
                List<String> newGroups = new ArrayList<>();
                List<String> newSets = new ArrayList<>();
                mKeyStore.setGroups(newGroups);
                mKeyStore.setSets(newSets);
            }
            List<String> groups = mKeyStore.getGroups();
            Iterator<String> groupIterator = groups.iterator();
            boolean hsAppGroup = false;
            while (groupIterator.hasNext()) {
                String group = groupIterator.next();
                if ("app".equals(group)) {
                    //证明有app的分组，则直接塞入app分组内
                    hsAppGroup = true;
                }
            }
            if (!hsAppGroup) {
                groups.add("app");
            }
            if (null == mCollectionCameras) {
                mCollectionCameras = new ArrayList<>();
            }
            CollectCameraEntity collectCameraEntity = new CollectCameraEntity();
            collectCameraEntity.setCid(orgCameraEntity.getManufacturerDeviceId() + "");
            collectCameraEntity.setCameraName(orgCameraEntity.getDeviceName());
            collectCameraEntity.setGroup("app");
            mCollectionCameras.add(collectCameraEntity);

            List<String> collectionCameras = new ArrayList<>();
            for (CollectCameraEntity entity : mCollectionCameras) {
                String camera = entity.getGroup() + ":" + entity.getCid() + "/" + entity.getCameraName();
                collectionCameras.add(camera);
            }
            mCurrentRequest = new SetKeyStoreRequest();
            mCurrentRequest.setGroups(groups);
            mCurrentRequest.setSets(collectionCameras);
            mPresenter.cameraLike("MY_GROUP", mCurrentRequest, true);
            // mPresenter.cameraLike(mSelectCamera.getManufacturerDeviceId()+"", Constants.CAMERA_FAVORITE);
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
    public void onSearch(String keyword) {
        this.keyword = keyword;
        if (visible) {
            onRefresh();
        } else {
            needRefresh = true;
        }
    }

    @Subscriber(mode = ThreadMode.MAIN, tag = EventBusTags.DAILY_ALARM_MARK_CHANGED)
    public void changeAlarmPosition(AlarmChangeBean bean) {
        int position = bean.getPosition();
        int collectstatus = bean.getCollectstatus();
        mAlertAdapter.getItem(position).setCollectStatus(collectstatus);
    }

    @Override
    public void doNext(int permId) {
        if (PermissionUtils.RC_LOCATION_PERM == permId) {
            if (null != mClickCamera.getLatitude() && null != mClickCamera.getLongitude()) {
                LatLng latLng = new LatLng(mClickCamera.getLatitude(), mClickCamera.getLongitude());
                Intent intent = new Intent(getActivity(), DeviceShowMapActivity.class);
                intent.putExtra("latlng", latLng);
                startActivity(intent);
            } else {
                ToastUtils.showShort(R.string.hint_no_device_latlong);
            }
        }
    }
}
