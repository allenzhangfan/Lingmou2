package cloud.antelope.lingmou.mvp.ui.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.modle.LoadingDialog;
import com.lingdanet.safeguard.common.utils.LocationUtils;
import com.lingdanet.safeguard.common.utils.LogUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.SizeUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.umeng.analytics.MobclickAgent;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.PermissionUtils;
import cloud.antelope.lingmou.app.utils.map.ClusterItem;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.di.component.DaggerDevicesComponent;
import cloud.antelope.lingmou.di.module.DevicesModule;
import cloud.antelope.lingmou.mvp.contract.DevicesContract;
import cloud.antelope.lingmou.mvp.model.entity.CameraNewStreamEntity;
import cloud.antelope.lingmou.mvp.model.entity.CollectCameraEntity;
import cloud.antelope.lingmou.mvp.model.entity.CollectChangeBean;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrgMainEntity;
import cloud.antelope.lingmou.mvp.model.entity.SetKeyStoreRequest;
import cloud.antelope.lingmou.mvp.presenter.DevicesPresenter;
import cloud.antelope.lingmou.mvp.ui.activity.BodyDepotActivity;
import cloud.antelope.lingmou.mvp.ui.activity.CameraMapActivity;
import cloud.antelope.lingmou.mvp.ui.activity.DeviceListActivity;
import cloud.antelope.lingmou.mvp.ui.activity.DeviceShowMapActivity;
import cloud.antelope.lingmou.mvp.ui.activity.FaceDepotActivity;
import cloud.antelope.lingmou.mvp.ui.activity.PlayerNewActivity;
import cloud.antelope.lingmou.mvp.ui.activity.VideoDetailActivity;
import cloud.antelope.lingmou.mvp.ui.adapter.DeviceCameraAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.DeviceParentAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.dialog.DeviceOperationDialog;
import pub.devrel.easypermissions.AfterPermissionGranted;

import static cloud.antelope.lingmou.app.EventBusTags.CAMERA_STATUS_CHANGE;
import static com.jess.arms.utils.Preconditions.checkNotNull;


public class DevicesFragment extends BaseFragment<DevicesPresenter> implements DevicesContract.View, SwipeRefreshLayout.OnRefreshListener, PermissionUtils.HasPermission {

    @BindView(R.id.org_recyview)
    RecyclerView rvOrg;
    @BindView(R.id.camera_recyview)
    RecyclerView rvCamera;
    @BindView(R.id.ll_org_main)
    LinearLayout llOrgMain;
    @BindView(R.id.srl)
    SwipeRefreshLayout srl;
    @BindView(R.id.parent_main_hz_scrollview)
    HorizontalScrollView mHorizontalScrollView;
    @BindView(R.id.view_divider)
    View viewDivider;
    @BindView(R.id.no_permission_rl)
    RelativeLayout mNoPermissionRl;

    @Inject
    DeviceParentAdapter mDeviceParentAdapter;
    @Inject
    DeviceCameraAdapter mDeviceCameraAdapter;
    private ArrayList<OrgMainEntity> mOrgMainEntities;
    private String mOrgId;
    private String mOrgRootId = "0";
    private Drawable mLeftDrawable;
    private boolean hasData;
    LoadingDialog mLoadingDialog;
    private OrgMainEntity mCurrentClickOrgEntity;
    private String mCurrentOrgPath;
    private OrgCameraEntity mClickCamera;
    private int page = 1;
    private int pageSize = 3000;
    private static DevicesFragment instance;
    private boolean isVisible;
    private DeviceOperationDialog dialog;

    public static DevicesFragment newInstance() {
        DevicesFragment fragment = new DevicesFragment();
        return fragment;
    }

    public static DevicesFragment getInstance() {
        return instance;
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerDevicesComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .devicesModule(new DevicesModule(this))
                .build()
                .inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_devices, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        instance = this;
        mOrgId = SPUtils.getInstance().getString(Constants.CONFIG_ORGANIZATION_ID, "0");
        mOrgMainEntities = new ArrayList<>();
        mLeftDrawable = getResources().getDrawable(R.drawable.org_right_arrow);
        mLeftDrawable.setBounds(0, 0, mLeftDrawable.getMinimumWidth(), mLeftDrawable.getMinimumHeight());
        mLoadingDialog = new LoadingDialog(getActivity());

        rvOrg.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int position = parent.getChildAdapterPosition(view);
                outRect.top = getResources().getDimensionPixelSize(R.dimen.dp16);
                outRect.left = position % 2 == 0 ? getResources().getDimensionPixelSize(R.dimen.dp16) : getResources().getDimensionPixelSize(R.dimen.dp8);
                outRect.right = position % 2 == 0 ? getResources().getDimensionPixelSize(R.dimen.dp8) : getResources().getDimensionPixelSize(R.dimen.dp16);
            }
        });
        rvOrg.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rvOrg.setAdapter(mDeviceParentAdapter);

        rvCamera.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvCamera.setAdapter(mDeviceCameraAdapter);

        srl.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        srl.setOnRefreshListener(this);

        //响应点击事件
        mDeviceParentAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                OrgMainEntity newEntity = (OrgMainEntity) adapter.getItem(position);
                mCurrentClickOrgEntity = newEntity;
                List<OrgMainEntity> parentEntities = getParentIdOrgs(mCurrentClickOrgEntity.id);
                if (parentEntities.isEmpty() || mCurrentClickOrgEntity.mIsRootOrg) {
                    //空，进行Camera请求
                    mPresenter.getMainCameras(page, pageSize, new String[]{mCurrentClickOrgEntity.id});
                    mCurrentOrgPath = mCurrentClickOrgEntity.treeDesc + mCurrentClickOrgEntity.organizationName;
                } else {
                    Intent intent = new Intent(getActivity(), DeviceListActivity.class);
                    intent.putExtra("OrgMainEntity", newEntity);
                    intent.putParcelableArrayListExtra("mOrgMainEntities", mOrgMainEntities);
                    startActivity(intent);
                }

                /*OrgMainEntity newEntity = (OrgMainEntity) adapter.getItem(position);
                OrgMainEntity entity = new OrgMainEntity(newEntity);
                mCurrentClickOrgEntity = entity;
                //查看是否有子元素，没有的话，证明就是最小组织，点击获取摄像机
                List<OrgMainEntity> parentEntities = getParentIdOrgs(entity.id);
                if (parentEntities.isEmpty() || entity.mIsRootOrg) {
                    //空，进行Camera请求
                    mPresenter.getMainCameras(page, pageSize, new String[]{entity.id});
                    mCurrentOrgPath = newEntity.treeDesc + newEntity.organizationName;
                } else {
                    createAddTextView(false, entity);
                    //非空，进入下个
//                    srl.setEnabled(false);
                    if (!entity.mIsRootOrg) {
                        entity.mIsRootOrg=true;
                        entity.mAliasName=entity.organizationName *//*+ "(本部)"*//*;
                    }
                    parentEntities.add(0, entity);
                    mDeviceParentAdapter.setNewData(parentEntities);
                }*/
            }
        });
        mDeviceCameraAdapter.setOnClickListener(new DeviceCameraAdapter.OnClickListener() {
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
        mDeviceCameraAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            }
        });


        mDeviceCameraAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            mClickCamera = (OrgCameraEntity) adapter.getItem(position);
            checkLocationPerm();
            if (!LocationUtils.isGpsEnabled()) {
                LocationUtils.openGpsSettings();
            }
        });
        String storeValue = SPUtils.getInstance().getString(Constants.COLLECT_JSON);
        if (!TextUtils.isEmpty(storeValue)) {
            Gson gson = new Gson();
            SetKeyStoreRequest request = gson.fromJson(storeValue, SetKeyStoreRequest.class);
            if (null != request) {
                List<String> storeSets = request.getSets();
                mCollectionCameras = new ArrayList<>();
                if (null != storeSets) {
                    for (String str : storeSets) {
                        CollectCameraEntity entity = new CollectCameraEntity();
                        String[] group_one = str.split(":");
                        entity.setGroup(group_one[0]);
                        String[] group_two = group_one[1].split("/");
                        String cid = group_two[0];
                        entity.setCid(cid);
                        entity.setCameraName(group_two[1]);
                        if (!mCollectionCameras.contains(entity)) {
                            mCollectionCameras.add(entity);
                        }
                    }
                }
            }

        } else {
            mPresenter.getCollections();
        }
        ArrayList<OrgCameraEntity> orgCameraEntities = getArguments().getParcelableArrayList("OrgCameraEntities");
        mCurrentClickOrgEntity = getArguments().getParcelable("OrgMainEntity");
        mOrgMainEntities = getArguments().getParcelableArrayList("mOrgMainEntities");
        if (orgCameraEntities != null) {
            srl.setEnabled(false);
            createAddTextView(false, mCurrentClickOrgEntity);
            mDeviceParentAdapter.setNewData(null);
            showCameraView();
            mDeviceCameraAdapter.setOrgPath(mCurrentOrgPath);
            Collections.sort(orgCameraEntities, new Comparator<OrgCameraEntity>() {
                @Override
                public int compare(OrgCameraEntity o1, OrgCameraEntity o2) {
                    return (int) (o2.getDeviceState() - o1.getDeviceState());
                }
            });
            List<OrgCameraEntity> cameraItems = new ArrayList<>();
            List<Long> cameraIds = new ArrayList<>();
            for (ClusterItem item : orgCameraEntities) {
                ((OrgCameraEntity) item).setSelect(isSelect((OrgCameraEntity) item));
                cameraItems.add((OrgCameraEntity) item);
                cameraIds.add(((OrgCameraEntity) item).getManufacturerDeviceId());
            }
            int size = cameraIds.size();
            if (size > 0) {
                for (int i = 0; i < (size - 1) / 100 + 1; i++) {//每100条分开获取（接口不支持100以上）
                    List<Long> subList = cameraIds.subList(i * 100, (i * 100 + 100) >= size ? size : (i * 100 + 100));
                    Long[] cids = subList.toArray(new Long[subList.size()]);
                    mPresenter.getCameraCovers(cids);
                    if ((i * 100 + 100) >= size) break;
                }
            }
            mDeviceCameraAdapter.setNewData(cameraItems);
            showCameraView();
        } else {
            //查看是否有子元素，没有的话，证明就是最小组织，点击获取摄像机
            List<OrgMainEntity> parentEntities = getParentIdOrgs(mCurrentClickOrgEntity.id);
            if (parentEntities.isEmpty() || mCurrentClickOrgEntity.mIsRootOrg) {
                //空，进行Camera请求
                mPresenter.getMainCameras(page, pageSize, new String[]{mCurrentClickOrgEntity.id});
                mCurrentOrgPath = mCurrentClickOrgEntity.treeDesc + mCurrentClickOrgEntity.organizationName;

            } else {
                createAddTextView(false, mCurrentClickOrgEntity);
                //非空，进入下个
                srl.setEnabled(false);
                if (!mCurrentClickOrgEntity.mIsRootOrg) {
                    mCurrentClickOrgEntity.mIsRootOrg = true;
                    mCurrentClickOrgEntity.mAliasName = mCurrentClickOrgEntity.organizationName/* + "(本部)"*/;
                }
//                parentEntities.add(0, mCurrentClickOrgEntity);
                mDeviceParentAdapter.setNewData(parentEntities);
            }
        }

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
    public void onStop() {
        super.onStop();
    }

    @AfterPermissionGranted(PermissionUtils.RC_LOCATION_PERM)
    public void checkLocationPerm() {
        PermissionUtils.locationTask(this);
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

    @Override
    public void setData(@Nullable Object data) {

    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        isVisible = true;
//        if (!hasData) {
//            srl.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    srl.setRefreshing(true);
//                    onRefresh();
//                }
//            }, 100);
//        }
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        isVisible = false;
    }

    @Override
    public void showLoading(String msg) {
        mLoadingDialog.show();
    }

    @Override
    public void hideLoading() {
        mLoadingDialog.dismiss();
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
    public void getMainOrgsSuccess(List<OrgMainEntity> entityList) {
        hasData = true;
        //进行CreateText和List展示
//        srl.setRefreshing(false);
        mOrgMainEntities.clear();
        mOrgMainEntities.addAll(entityList);
        showOrgView();
        if (entityList != null) {
            for (OrgMainEntity entity : entityList) {
                String id = entity.id;
                if (id.equals(mOrgId)) {
                    Object parentId = entity.parentId;
                    mOrgRootId = String.valueOf(parentId);
                    break;
                }
            }
        }
        List<OrgMainEntity> rootOrgs = getParentIdOrgs(mOrgRootId);
        if (!rootOrgs.isEmpty()) {
            OrgMainEntity rootOrg = rootOrgs.get(0);
            llOrgMain.removeAllViews();
            createAddTextView(false, rootOrg);
            String rootOrgId = rootOrg.id;
            List<OrgMainEntity> orgMainEntityList = getParentIdOrgs(rootOrgId);
            for (OrgMainEntity entity : orgMainEntityList) {
                entity.level1 = true;
            }
            rootOrg.mAliasName = rootOrg.organizationName/* + "(本部)"*/;
            rootOrg.mIsRootOrg = true;
            rootOrg.level1 = true;
            orgMainEntityList.add(0, rootOrg);
            mDeviceParentAdapter.setNewData(orgMainEntityList);
        }
    }

    private void showOrgView() {
        //切换成摄像机列表
        rvOrg.setVisibility(View.VISIBLE);
        rvCamera.setVisibility(View.GONE);
    }

    //获取父id，获取所有父id为parentOrgId的子组织
    private List<OrgMainEntity> getParentIdOrgs(String parentOrgId) {
        List<OrgMainEntity> newList = new ArrayList<>();
        for (OrgMainEntity entity : mOrgMainEntities) {
            if (null != entity) {
                Object parentId = entity.parentId;
                if (parentId instanceof Double) {
                    if ((Double) parentId == Double.parseDouble(parentOrgId)) {
                        newList.add(entity);
                    }
                } else if (parentId instanceof String) {
                    if (parentOrgId.equals(parentId)) {
                        newList.add(entity);
                    }
                }
            }

        }
        return newList;
    }

    private void createAddTextView(boolean isRootClick, OrgMainEntity entity) {
        final TextView textView = new TextView(getActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.rightMargin = SizeUtils.dp2px(6);
        textView.setTextSize(12);
        textView.setTextColor(getResources().getColor(R.color.gray_666));
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(params);
        textView.setMaxLines(1);
        // textView.setEllipsize(TextUtils.TruncateAt.END);
        if (isRootClick) {
            textView.setText(entity.organizationName);
        } else {
            textView.setText(TextUtils.isEmpty(entity.mAliasName) ? entity.organizationName : entity.mAliasName);
        }
        textView.setCompoundDrawables(null, null, mLeftDrawable, null);
        textView.setCompoundDrawablePadding(SizeUtils.dp2px(6));
        textView.setTag(entity);
        llOrgMain.addView(textView);
        mHorizontalScrollView.post(new Runnable() {
            @Override
            public void run() {
                mHorizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        });
        //循环，然后让最后一个TextView变蓝
        int childCount = llOrgMain.getChildCount();
        for (int i = 0; i < childCount; i++) {
            TextView tv = (TextView) llOrgMain.getChildAt(i);
            if (i == childCount - 1) {
                //最后一个，颜色变蓝
                tv.setTextColor(getResources().getColor(R.color.yellow_ff8f00));
            } else {
                tv.setTextColor(getResources().getColor(R.color.gray_666));
            }
        }
        checkLevel();
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int childCount = llOrgMain.getChildCount();
                OrgMainEntity tagEntity = (OrgMainEntity) textView.getTag();
                for (int i = childCount - 1; i > -1; i--) {
                    TextView childTv = (TextView) llOrgMain.getChildAt(i);
                    if (null != childTv && childTv.getTag() == tagEntity) {
                        //证明是最后一个
                        if (i == childCount - 1) {
                            return;
                        } else {
                            //干掉i+1之后的所有view和fragment
                            for (int j = llOrgMain.getChildCount() - 1; j > i; j--) {
                                TextView orgTv = (TextView) llOrgMain.getChildAt(j);
                                LogUtils.i("cxm", "orgTv -- i = " + i + ",orgTv = " + orgTv);
                                if (null != orgTv) {
                                    LogUtils.i("cxm", "orgTv -- i = " + i + ",orgTv.text = " + orgTv.getText());
                                    llOrgMain.removeView(orgTv);
                                    ((TextView) (llOrgMain.getChildAt(llOrgMain.getChildCount() - 1))).setTextColor(getResources().getColor(R.color.yellow_ff8f00));
                                }
                            }
                            if (i == 0) {
                                //重置根目录下的元素
                                List<OrgMainEntity> rootOrgs = getParentIdOrgs(mOrgRootId);
                                if (!rootOrgs.isEmpty()) {
                                    OrgMainEntity rootOrg = rootOrgs.get(0);
                                    llOrgMain.removeAllViews();
                                    createAddTextView(true, rootOrg);
                                    mDeviceCameraAdapter.setNewData(null);
                                    showOrgView();
                                    String rootOrgId = rootOrg.id;
                                    List<OrgMainEntity> orgMainEntityList = getParentIdOrgs(rootOrgId);

                                    if (!entity.mIsRootOrg) {
                                        rootOrg.mAliasName = rootOrg.organizationName/* + "(本部)"*/;
                                        rootOrg.mIsRootOrg = true;
                                    }
                                    orgMainEntityList.add(0, rootOrg);
                                    mDeviceParentAdapter.setNewData(orgMainEntityList);
                                    srl.setEnabled(true);
                                }
                            } else {
                                mDeviceCameraAdapter.setNewData(null);
                                showOrgView();
                                List<OrgMainEntity> orgParentEntities = getParentIdOrgs(tagEntity.id);
                                srl.setEnabled(false);
                                if (!entity.mIsRootOrg) {
                                    entity.mAliasName = entity.organizationName/* + "(本部)"*/;
                                    entity.mIsRootOrg = true;
                                }
                                orgParentEntities.add(0, entity);
                                mDeviceParentAdapter.setNewData(orgParentEntities);
                            }
                        }
                    }
                }
                checkLevel();
            }
        });
    }

    private void checkLevel() {
        viewDivider.setVisibility(llOrgMain.getChildCount() > 1 ? View.VISIBLE : View.GONE);
//        mHorizontalScrollView.setVisibility(llOrgMain.getChildCount() > 1 ? View.VISIBLE : View.GONE);
//        ((DeviceListActivity)getActivity()).setIbCloseVisibility(llOrgMain.getChildCount() > 1);
    }

    @Override
    public void getMainOrgsFail() {
        srl.setEnabled(true);
        srl.setRefreshing(false);
    }

    @Override
    public void getMainOrgCamerasSuccess(List<OrgCameraEntity> entityList) {
        if (null == entityList || entityList.isEmpty()) {
            ToastUtils.showShort(R.string.hint_no_org_videos);
        } else {
            Intent intent = new Intent(getActivity(), DeviceListActivity.class);
            intent.putParcelableArrayListExtra("OrgCameraEntities", (ArrayList<? extends Parcelable>) entityList);
            intent.putExtra("OrgMainEntity", mCurrentClickOrgEntity);
            intent.putParcelableArrayListExtra("mOrgMainEntities", mOrgMainEntities);
            startActivity(intent);
        }
    }

    private void showCameraView() {
        rvOrg.setVisibility(View.GONE);
        rvCamera.setVisibility(View.VISIBLE);
        mNoPermissionRl.setVisibility(View.GONE);
    }

    @Override
    public void getMainOrgCameraFail() {
        mCurrentClickOrgEntity = null;
    }

    @Override
    public void getOrgNoPermission() {
        srl.setEnabled(true);
        srl.setRefreshing(false);
        showNoPermissionView();
    }

    private void showNoPermissionView() {
        rvOrg.setVisibility(View.GONE);
        rvCamera.setVisibility(View.GONE);
        mNoPermissionRl.setVisibility(View.VISIBLE);
    }

    @Override
    public void getCameraNoPermission() {
        mCurrentClickOrgEntity = null;
        ToastUtils.showShort(R.string.hint_no_permission);
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
        for (OrgCameraEntity orgCameraEntity : mDeviceCameraAdapter.getData()) {
            orgCameraEntity.setSelect(isSelect(orgCameraEntity));
        }
        mDeviceCameraAdapter.notifyDataSetChanged();
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
    public void getCollectionsSuccess(GetKeyStoreBaseEntity entity) {
        if (null != entity) {
            GetKeyStoreEntity userKvStroe = entity.getUserKvStroe();
            if (null != userKvStroe) {
                String storeValue = userKvStroe.getStoreValue();
                if (!TextUtils.isEmpty(storeValue)) {
                    Gson gson = new Gson();
                    SetKeyStoreRequest keyStore = gson.fromJson(storeValue, SetKeyStoreRequest.class);
                    List<String> storeSets = keyStore.getSets();
                    if (null == storeSets || storeSets.isEmpty()) {
                        SPUtils.getInstance().put(Constants.COLLECT_JSON, "");
                        return;
                    }
                    SPUtils.getInstance().put(Constants.COLLECT_JSON, storeValue);
                }
            }
        }
    }

    @Override
    public void getCoversSuccess(CameraNewStreamEntity entity) {
        List<CameraNewStreamEntity.DevicesBean> devices = entity.getDevices();
        if (null != devices && !devices.isEmpty()) {
            for (CameraNewStreamEntity.DevicesBean devicesBean : devices) {
                Long cid = devicesBean.getCid();
                for (OrgCameraEntity cameraEntity : mDeviceCameraAdapter.getData()) {
                    Long deviceId = cameraEntity.getManufacturerDeviceId();
                    if (cid.equals(deviceId)) {
                        cameraEntity.setCoverUrl(devicesBean.getCover_url());
                        cameraEntity.setPlayUrl(devicesBean.getRtmp_url());
                        break;
                    }
                }
            }
            mDeviceCameraAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRefresh() {
        mPresenter.getMainOrgs();
    }

    public boolean onPressBack() {
        if (null != llOrgMain && isVisible) {
            int childSize = llOrgMain.getChildCount();
            if (childSize > 1) {
                //表明点了二级结构
                TextView textView = (TextView) llOrgMain.getChildAt(childSize - 2);
                TextView currentTextView = (TextView) llOrgMain.getChildAt(childSize - 1);
                llOrgMain.removeView(currentTextView);
                textView.setTextColor(getResources().getColor(R.color.yellow_ff8f00));
                OrgMainEntity tagEntity = (OrgMainEntity) textView.getTag();
                mDeviceCameraAdapter.setNewData(null);
                showOrgView();
                List<OrgMainEntity> orgParentEntities = getParentIdOrgs(tagEntity.id);
//                srl.setEnabled(childSize == 2);
                if (!tagEntity.mIsRootOrg) {
                    tagEntity.mAliasName = tagEntity.organizationName/* + "(本部)"*/;
                    tagEntity.mIsRootOrg = true;
                }
                orgParentEntities.add(0, tagEntity);
                mDeviceParentAdapter.setNewData(orgParentEntities);
                checkLevel();
                return true;
            }
        }
        return false;
    }
}
