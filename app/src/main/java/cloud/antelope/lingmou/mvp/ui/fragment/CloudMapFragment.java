package cloud.antelope.lingmou.mvp.ui.fragment;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.google.gson.Gson;
import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.modle.LoadingDialog;
import com.lingdanet.safeguard.common.modle.PermissionDialog;
import com.lingdanet.safeguard.common.utils.AppUtils;
import com.lingdanet.safeguard.common.utils.Configuration;
import com.lingdanet.safeguard.common.utils.DeviceUtil;
import com.lingdanet.safeguard.common.utils.LogUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.SizeUtils;
import com.lingdanet.safeguard.common.utils.SystemBarTintManager;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.lingdanet.safeguard.common.utils.Utils;
import com.umeng.analytics.MobclickAgent;

import org.litepal.crud.DataSupport;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.BarViewUtils;
import cloud.antelope.lingmou.app.utils.GisUtils;
import cloud.antelope.lingmou.app.utils.PermissionUtils;
import cloud.antelope.lingmou.app.utils.map.Cluster;
import cloud.antelope.lingmou.app.utils.map.ClusterClickListener;
import cloud.antelope.lingmou.app.utils.map.ClusterItem;
import cloud.antelope.lingmou.app.utils.map.ClusterOverlay;
import cloud.antelope.lingmou.app.utils.map.ClusterRender;
import cloud.antelope.lingmou.app.utils.map.GPSUtils;
import cloud.antelope.lingmou.app.utils.map.MapChangeListener;
import cloud.antelope.lingmou.app.utils.map.MarkerChangeListener;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.di.component.DaggerCloudMapComponent;
import cloud.antelope.lingmou.di.module.CloudMapModule;
import cloud.antelope.lingmou.mvp.contract.CloudMapContract;
import cloud.antelope.lingmou.mvp.model.entity.CameraNewStreamEntity;
import cloud.antelope.lingmou.mvp.model.entity.CollectCameraEntity;
import cloud.antelope.lingmou.mvp.model.entity.CollectChangeBean;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraParentEntity;
import cloud.antelope.lingmou.mvp.model.entity.SetKeyStoreRequest;
import cloud.antelope.lingmou.mvp.presenter.CloudMapPresenter;
import cloud.antelope.lingmou.mvp.ui.activity.DeviceListActivity;
import cloud.antelope.lingmou.mvp.ui.activity.DeviceMapActivity;
import cloud.antelope.lingmou.mvp.ui.activity.CloudSearchActivity;
import cloud.antelope.lingmou.mvp.ui.activity.PlayerNewActivity;
import cloud.antelope.lingmou.mvp.ui.activity.VideoDetailActivity;
import cloud.antelope.lingmou.mvp.ui.adapter.CameraResourceAdapter;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.support.design.widget.BottomSheetBehavior.STATE_HIDDEN;
import static cloud.antelope.lingmou.app.EventBusTags.CAMERA_STATUS_CHANGE;
import static com.jess.arms.utils.Preconditions.checkNotNull;


public class CloudMapFragment extends BaseFragment<CloudMapPresenter> implements CloudMapContract.View,
        AMap.OnMyLocationChangeListener, EasyPermissions.PermissionCallbacks,
        AMap.OnMapLoadedListener, ClusterRender,
        ClusterClickListener, AMap.OnMapClickListener,
        GisUtils.OnLocateActionListener, MarkerChangeListener,
        MapChangeListener, DeviceMapActivity.BottomSheetListener, PermissionUtils.HasPermission {

    private static final int MESSAGE_MARKER_CLUSTER = 0x01;
    private static final int MESSAGE_ANIMATE_MAP = 0x02;
    private static final int MESSAGE_NAVIGATION_BAR_CHANGE = 0x03;
    /**
     * 如果从某个指定的摄像机点击进入
     */
    private static final int MESSAGE_ANIMATE_TO_CAMERA = 0x04;
    /**
     * 如果从首页地图图标点击进入且只有一个摄像机包含位置信息
     */
    private static final int MESSAGE_ANIMATE_TO_ONE = 0x05;
    public static final int MESSAGE_LOAD_CAMERA_DONE = 0x06;

    @BindView(R.id.map)
    TextureMapView mMapView;

    @BindView(R.id.traffic_ll)
    RelativeLayout mTrafficLl;
    @BindView(R.id.locate_ll)
    LinearLayout mLocateLl;
    @BindView(R.id.zoom_in_iv)
    ImageView mZoomInIv;
    @BindView(R.id.zoom_out_iv)
    ImageView mZoomOutIv;
    @BindView(R.id.traffic_icon_iv)
    ImageView mTrafficIconIv;
    //    @BindView(R.id.camera_rv)
//    RecyclerView mRecyclerView;
    @BindView(R.id.camera_map_root)
    RelativeLayout mRoot;
    @BindView(R.id.search_tv)
    TextView mSearchTv;
    @BindView(R.id.compass_iv)
    ImageView mCompassIv;
    @BindView(R.id.zoom_ll)
    LinearLayout mZoomLl;
    @BindView(R.id.tv_tip)
    TextView tvTip;
    @BindView(R.id.triangle)
    ImageView triangle;
    @BindView(R.id.ll_search)
    LinearLayout mSearchll;
    @BindView(R.id.iv_collect)
    ImageView ivCollect;


    //    @Inject
//    CameraAddressAdapter mCameraAddressAdapter;
    @Inject
    RecyclerView.ItemDecoration mItemDecoration;
    @Inject
    RecyclerView.LayoutManager mLayoutManager;
    @Inject
    RecyclerView.ItemAnimator mItemAnimator;
    @Inject
    Map<Integer, Drawable> mBackDrawAbles;
    @Inject
    LoadingDialog mLoadingDialog;

    @Inject
    CameraResourceAdapter mCameraResourceAdapter;
    private AMap mAMap;

    private ClusterOverlay mClusterOverlay;
    private static CloudMapFragment instance;
    /**
     * 聚合的临界距离，单位是dp
     */
    private int clusterRadius = 36;
    /**
     * RecycleView的最大高度，单位是dp
     */

    private int mRecycleViewMaxHeight = 200;

    private boolean mIsTrafficOn;

    private LatLng mCurrentLatLng;
    private GisUtils mGisUtils;
    private Marker mLocationMarker;
    private Marker mSelectMarker;
    private OrgCameraEntity mSelectCamera;

    private boolean mIsFirstZoomToMax;
    private boolean mIsFirstZoomToMin;

    private boolean mIsLoadedData;

    private RecyclerView rvResources;
    private int behaviorState = STATE_HIDDEN;
    private float slideOffset;
    private boolean justShowCollectionCamera;
    /**
     * 系统状态栏的管理者
     */
    protected SystemBarTintManager mTintManager;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_LOAD_CAMERA_DONE:
                    loadingCamera = false;
//                    if (needShow) {
                    justShowCollectionCamera(false);
//                    }
                    break;
                case MESSAGE_MARKER_CLUSTER:
                    boolean fromConstructor = (boolean) msg.obj;
                    if (fromConstructor) {
                        // mHandler.sendEmptyMessageDelayed(MESSAGE_ANIMATE_MAP, 250);
                        mHandler.sendEmptyMessage(MESSAGE_ANIMATE_MAP);
                    } else {
                        dismissRecyclerView();
                    }
                    break;
                case MESSAGE_ANIMATE_MAP:
                    changeCamera(CameraUpdateFactory.zoomIn(), true, null);
                    break;
                case MESSAGE_NAVIGATION_BAR_CHANGE:
//                    changeLocateIconPosition();
                    break;
                case MESSAGE_ANIMATE_TO_CAMERA:
                    if (null != mAMap && mSelectCamera != null) {
                        mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mSelectCamera.getPosition(), 19));
                        ArrayList<OrgCameraEntity> list = new ArrayList<>();
                        list.add(mSelectCamera);
                        showRecyclerView(list);
                    }
                    break;
                case MESSAGE_ANIMATE_TO_ONE:
                    ClusterItem clusterItem = (ClusterItem) msg.obj;
                    mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(clusterItem.getPosition(), 14));
                    break;
                default:
                    break;
            }
        }
    };
    private BottomSheetBehavior bottomSheetBehavior;
    private boolean mLocateIconAnim;//图标在执行动画
    private boolean mShowLocateIcon = true;//图标是否显示
    private boolean canSlidesUp;
    private CoordinatorLayout clResources;
    private int totalHeight;
    private static CloudMapFragment fragment;
    private LatLngBounds.Builder allCameraBuilder;
    private LatLngBounds.Builder collectionCamerabuilder;
    private boolean loadingCamera;
    private boolean needShow;
    private boolean getCamerasSuccess;

    /**
     * 根据动画按钮状态，调用函数animateCamera或moveCamera来改变可视区域
     */
    private void changeCamera(CameraUpdate update, boolean animated, AMap.CancelableCallback callback) {
        if (animated) {
            mAMap.animateCamera(update, 200, callback);
        } else {
            mAMap.moveCamera(update);
        }
    }

    @Override
    public void getAllCamerasSuccess(OrgCameraParentEntity entity) {
        if (null != entity) {
            List<OrgCameraEntity> resultList = entity.getResultList();
            if (null != resultList && !resultList.isEmpty()) {
                DataSupport.deleteAll(OrgCameraEntity.class);
                DataSupport.saveAll(resultList);
            }
            getCamerasSuccess = true;
        } else {
            getCamerasSuccess = false;
        }
        showLoading("");
        mHandler.sendEmptyMessage(MESSAGE_LOAD_CAMERA_DONE);
    }

    private ContentObserver mNavigationStatusObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            // LogUtils.d("NAVIGATION_BAR_CHANGE with selfChange = " + selfChange);
            // 这里需要先等NavigationBar显示或隐藏完成之后再设置控件才会不出现bug
            if (null != mHandler) {
                mHandler.sendEmptyMessageDelayed(MESSAGE_NAVIGATION_BAR_CHANGE, 180);
            }
        }
    };
    private ArrayList<CollectCameraEntity> mCollectionCameras;
    private SetKeyStoreRequest mKeyStore;
    private SetKeyStoreRequest mCurrentRequest;

    public static CloudMapFragment newInstance() {
        fragment = new CloudMapFragment();
        return fragment;
    }

    public static CloudMapFragment getInstance() {
        return instance;
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerCloudMapComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .cloudMapModule(new CloudMapModule(this))
                .build()
                .inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cloud_map, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        instance = this;
        boolean hasSystemFeature = getContext().getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
        if (hasSystemFeature) {
            //有刘海屏，则增加margin
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mSearchll.getLayoutParams();
            layoutParams.topMargin = getContext().getResources().getDimensionPixelSize(R.dimen.dp36);
            mSearchll.setLayoutParams(layoutParams);
        }
        mMapView.onCreate(savedInstanceState);
//        mIsTrafficOn = true;
        mTintManager = new SystemBarTintManager(getActivity());
        mTintManager.setStatusBarTintEnabled(true);
        mTintManager.setStatusBarTintResource(android.R.color.transparent);
        /*mCameraAddressAdapter.setOnClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mSelectCamera = (OrgCameraEntity) adapter.getItem(position);
                dismissRecyclerView(false, (Marker) mRecyclerView.getTag(), 400);
            }
        });*/
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
        initMap();
        initView();
        initListener();

        // 如果是华为的手机，并且有导航栏，则注册导航栏显示和隐藏的监听
//        if (BarViewUtils.isHUAWEI() && BarViewUtils.hasNavBar()) {
//            if (getActivity().getContentResolver() != null) {
//                getActivity().getContentResolver().registerContentObserver(Settings.System.getUriFor
//                        (BarViewUtils.NAV_IS_MIN), true, mNavigationStatusObserver);
//            }
//        }
    }

    private void initMap() {
        if (mAMap == null) {
            mAMap = mMapView.getMap();
        }
        mAMap.setCustomMapStylePath(Configuration.getRootPath() + Constants.MAP_STYLE_FILE + "style.data");
        mAMap.setMapCustomEnable(true);
        UiSettings uiSettings = mAMap.getUiSettings();
        uiSettings.setCompassEnabled(false);
        uiSettings.setZoomControlsEnabled(false);
//        MyLocationStyle myLocationStyle = new MyLocationStyle();
//        myLocationStyle.myLocationIcon(BitmapDescriptorFactory .fromResource(R.drawable.gps_point));// 设置小蓝点的图标
//         myLocationStyle.strokeColor(getResources().getColor(R.color.yellow_ffb300));// 设置圆形的边框颜色
//         myLocationStyle.radiusFillColor(getResources().getColor(R.color.yellow_ff8f00));// 设置圆形的填充颜色
//         myLocationStyle.strokeWidth(0f);// 设置圆形的边框粗细
//        mAMap.setMyLocationStyle(myLocationStyle);
//        mAMap.setMyLocationStyle(new MyLocationStyle().myLocationIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.shape_location))));
        mGisUtils = new GisUtils(getActivity(), 3000);
        mGisUtils.setLocateListener(this);

        mAMap.setTrafficEnabled(mIsTrafficOn);// 显示实时交通状况
        mAMap.getCameraPosition();

    }

    private void initListener() {
        mAMap.setOnMapClickListener(this);
        mAMap.setOnMapLoadedListener(this);
        ((DeviceMapActivity) getActivity()).setBottomSheetListener(this);
        mCameraResourceAdapter.setOnClickListener(new CameraResourceAdapter.OnClickListener() {
            @Override
            public void onItemClick(OrgCameraEntity item) {
                boolean hasVideoLive = SPUtils.getInstance().getBoolean(Constants.PERMISSION_HAS_VIDEO_LIVE, false);
                if (hasVideoLive) {
                    gotoRealVidioActivity(item);
                } else {
                    ToastUtils.showShort(R.string.hint_no_permission);
                }
            }

            @Override
            public void onCollectionClick(OrgCameraEntity item) {
                collectionCamera(item);
            }

            @Override
            public void onSettingClick(OrgCameraEntity item) {
                boolean hasVideoLive = SPUtils.getInstance().getBoolean(Constants.PERMISSION_HAS_VIDEO_LIVE, false);
                if (hasVideoLive) {
                    gotoPlayerActivity(item);
                } else {
                    ToastUtils.showShort(R.string.hint_no_permission);
                }
            }
        });
    }

    private void gotoRealVidioActivity(OrgCameraEntity item) {
        Intent playIntent = new Intent(getActivity(), PlayerNewActivity.class);
        playIntent.putExtra("cameraId", item.getManufacturerDeviceId());
        playIntent.putExtra("cameraName", item.getDeviceName());
        playIntent.putExtra("cameraSn", item.getSn());
        playIntent.putExtra("coverUrl", item.getCoverUrl());
        startActivity(playIntent);
    }

    /**
     * 打开视频播放页面
     */
    private void gotoPlayerActivity(OrgCameraEntity item) {
        /*Intent intent = new Intent(NewMainActivity.this, PlayerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", mSelectCamera);
        intent.putExtras(bundle);
        startActivity(intent);*/
        boolean hasVideoLive = SPUtils.getInstance().getBoolean(Constants.PERMISSION_HAS_VIDEO_LIVE, false);
        if (hasVideoLive) {
            MobclickAgent.onEvent(getContext(), "device_toDeviceDetail");
            Intent intent = new Intent(getActivity(), VideoDetailActivity.class);
            intent.putExtra("cid", String.valueOf(item.getManufacturerDeviceId()));
            intent.putExtra("cameraName", item.getDeviceName());
            intent.putExtra("cameraSn", item.getSn());
            intent.putExtra("coverUrl", item.getCoverUrl());
            intent.putExtra("isFromOrgMap", true);
            startActivity(intent);
        } else {
            ToastUtils.showShort(R.string.hint_no_permission);
        }
    }

    private void initView() {
        clResources = ((DeviceMapActivity) getActivity()).getClResources();
        bottomSheetBehavior = ((DeviceMapActivity) getActivity()).getmBottomSheetBehavior();
       /* mRecyclerView.setLayoutManager(mLayoutManager);
        // mRecyclerView.setBackgroundColor(getResources().getColor(R.color.white));
        mRecyclerView.setItemAnimator(mItemAnimator);
        mRecyclerView.addItemDecoration(mItemDecoration);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setAdapter(mCameraAddressAdapter);*/
        rvResources = ((DeviceMapActivity) getActivity()).getRvResources();
        rvResources.setLayoutManager(new LinearLayoutManager(getContext()));
        rvResources.setAdapter(mCameraResourceAdapter);
    }

    @Override
    public void setData(@Nullable Object data) {
    }

    @Override
    public void showLoading(String message) {
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
    public void onSupportVisible() {
        super.onSupportVisible();
        if (!mIsLoadedData) {
           /* boolean fristIn = SPUtils.getInstance().getBoolean("firstIn" + AppUtils.getAppVersionName(), true);
            tvTip.setVisibility(fristIn ? View.VISIBLE : View.GONE);
            triangle.setVisibility(fristIn ? View.VISIBLE : View.GONE);
            if (fristIn) {
                tvTip.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tvTip.setVisibility(View.GONE);
                        triangle.setVisibility(View.GONE);
                    }
                }, 3000);
                SPUtils.getInstance().put("firstIn" + AppUtils.getAppVersionName(), false);
            }*/
            checkLocationPerm();
//            onMapLoadedInitData();
//            justShowCollectionCamera(false);
        }
        if (loadingCamera) {
            showLoading("");
        } else if (!getCamerasSuccess) {
            mPresenter.getAllCameras();
            loadingCamera = true;
            showLoading("");
        }
    }

    @AfterPermissionGranted(PermissionUtils.RC_LOCATION_PERM)
    public void checkLocationPerm() {
        PermissionUtils.locationTask(this);
    }

    @Override
    public void doNext(int permId) {
        if (PermissionUtils.RC_LOCATION_PERM == permId) {
            mGisUtils.start();
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        mGisUtils.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mGisUtils.start();
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

    @OnClick({R.id.rl_list, R.id.traffic_ll, R.id.locate_ll, R.id.rl_select,
            R.id.zoom_in_iv, R.id.zoom_out_iv, R.id.search_tv, R.id.compass_iv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_select:
                onMapClick(null);
                if (mSelectMarker != null) {
                    mSelectMarker.remove();
                    mSelectMarker = null;
                }
                if (mClusterOverlay != null) {
                    mClusterOverlay.clearAllClusters();
                    justShowCollectionCamera(!justShowCollectionCamera);
                }
                ivCollect.setImageResource(justShowCollectionCamera ? R.drawable.collected : R.drawable.un_collect);
                break;
            case R.id.compass_iv:
                float bearing = 0.0f;  // 地图默认方向
                CameraPosition cameraPosition = mAMap.getCameraPosition();
                mAMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(cameraPosition.target, cameraPosition.zoom, cameraPosition.tilt, bearing)));
                break;
            case R.id.rl_list:
                startActivity(new Intent(getActivity(), DeviceListActivity.class));
                break;
            case R.id.search_tv:
                ActivityOptions activityOptions = null;
                Intent searchIntent = new Intent(getActivity(), CloudSearchActivity.class);
                searchIntent.putExtra("type", "device");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    activityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity(), mSearchTv, "searchText");
                    startActivity(searchIntent, activityOptions.toBundle());
                } else {
                    startActivity(searchIntent);
                }

                break;
            case R.id.traffic_ll:
                if (mIsTrafficOn) {  // 如果开启了实时路况
                    mAMap.setTrafficEnabled(false);
                    mIsTrafficOn = false;
                    mTrafficIconIv.setImageResource(R.drawable.traffic_off);
                } else {  // 如果还没有开启实时路况
                    mAMap.setTrafficEnabled(true);
                    mIsTrafficOn = true;
                    mTrafficIconIv.setImageResource(R.drawable.traffic_on);
                }
                break;
            case R.id.locate_ll:
                if (null != mCurrentLatLng) {
                    mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLatLng, 10));
                } else {
                    ToastUtils.showShort("还没有获取到定位信息");
                }
                break;
            case R.id.zoom_in_iv:
                changeCamera(CameraUpdateFactory.zoomIn(), true, null);
                break;
            case R.id.zoom_out_iv:
                changeCamera(CameraUpdateFactory.zoomOut(), true, null);
                break;
            default:
                break;
        }
    }

    private void justShowCollectionCamera(boolean justShow) {
        if (loadingCamera) {
            needShow = true;
            return;
        }
        needShow = false;
        justShowCollectionCamera = justShow;
        long currentTimeMillis = System.currentTimeMillis();
        Observable.just(new Object())
                .map(new Function<Object, CloudMapFragment.ClusterBuilder>() {
                    @Override
                    public CloudMapFragment.ClusterBuilder apply(Object object) throws Exception {
                        CloudMapFragment.ClusterBuilder clusterBuilder = new CloudMapFragment.ClusterBuilder();
                        List<ClusterItem> items = new ArrayList<>();
                        List<OrgCameraEntity> cameras = DataSupport.findAll(OrgCameraEntity.class);
                        mIsLoadedData = true;
                        if (justShow) {
                            collectionCamerabuilder = new LatLngBounds.Builder();
                            String storeValue = SPUtils.getInstance().getString(Constants.COLLECT_JSON);
                            if (!TextUtils.isEmpty(storeValue)) {
                                Gson gson = new Gson();
                                SetKeyStoreRequest request = gson.fromJson(storeValue, SetKeyStoreRequest.class);

                                List<String> storeSets = request.getSets();
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
                                List<OrgCameraEntity> mOrgCameraEntities = new ArrayList<>();
                                for (CollectCameraEntity entity : collectCameraList) {
                                    // OrgCameraEntity orgCameraEntity = new OrgCameraEntity();
                                    // Cursor cursor = DataSupport.findBySQL("select * from orgcameraentity where manufacturerdeviceid = '" + entity.getCid()+"'");
                                    List<OrgCameraEntity> cursorEntities = DataSupport.where("manufacturerdeviceid = ?", entity.getCid()).find(OrgCameraEntity.class);
                                    if (cursorEntities != null && !cursorEntities.isEmpty()) {
                                        OrgCameraEntity cameraEntity = cursorEntities.get(0);
                                        mOrgCameraEntities.add(cameraEntity);
                                    }
                                }

                                if (null != mOrgCameraEntities && !mOrgCameraEntities.isEmpty()) { // 如果包含摄像机
                                    for (OrgCameraEntity item : mOrgCameraEntities) {
                                        if (null != item.getLatitude() && null != item.getLongitude()) {
                                            if (mSelectCamera == null) {
                                                if (GPSUtils.isInChina(item.getLatitude(), item.getLongitude())) {
                                                    items.add(item);
                                                    collectionCamerabuilder.include(item.getPosition());
                                                }
                                            } else {
                                                items.add(item);
                                                collectionCamerabuilder.include(item.getPosition());
                                            }
                                        }
                                    }
                                }
                            }
                            clusterBuilder.setBuilder(collectionCamerabuilder);
                        } else {
                            allCameraBuilder = new LatLngBounds.Builder();
                            if (null != cameras && !cameras.isEmpty()) { // 如果包含摄像机
                                for (OrgCameraEntity item : cameras) {
                                    if (null != item.getLatitude() && null != item.getLongitude()) {
                                        // item.setInstallAddress("武汉市洪山区关山大道332号保利国际中心10楼");
                                        // double[] latlon = GPSUtils.gps84_To_Gcj02(item.getLatitude(), item.getLongitude());
                                        // item.setLatitude(item.getLatitude());
                                        // item.setLongitude(item.getLongitude());
                                        if (mSelectCamera == null) {
                                            if (GPSUtils.isInChina(item.getLatitude(), item.getLongitude())) {
                                                items.add(item);
                                                allCameraBuilder.include(item.getPosition());
                                            }
                                        } else {
                                            items.add(item);
                                            allCameraBuilder.include(item.getPosition());
                                        }
                                    }
                                }
                            }
                            clusterBuilder.setBuilder(allCameraBuilder);
                        }
                        if (!items.isEmpty()) {
                            if (mClusterOverlay != null) {
                                mClusterOverlay.clearClusterItem();
                                mClusterOverlay.clearAllClusters();
                            }
                            mClusterOverlay = new ClusterOverlay(mAMap, items,
                                    SizeUtils.dp2px(clusterRadius),
                                    getContext(), mSelectMarker);
                        }
                        clusterBuilder.setItems(items);
                        clusterBuilder.setHasCameras(!items.isEmpty());
                        return clusterBuilder;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CloudMapFragment.ClusterBuilder>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CloudMapFragment.ClusterBuilder clusterBuilder) {
                        LogUtils.w("cxm", System.currentTimeMillis() - currentTimeMillis);
                        List<ClusterItem> items = clusterBuilder.getItems();
                        LatLngBounds.Builder builder = clusterBuilder.getBuilder();
                        if (!items.isEmpty()) {
                            // mClusterOverlay = new ClusterOverlay(mAMap, items,
                            //         SizeUtils.dp2px(clusterRadius),
                            //         getApplicationContext(), mSelectMarker);
                            mClusterOverlay.setClusterRenderer(CloudMapFragment.this);
                            mClusterOverlay.setMarkerChangeListener(CloudMapFragment.this);
                            mClusterOverlay.setOnClusterClickListener(CloudMapFragment.this);
                            mClusterOverlay.setOnMapChangeListener(CloudMapFragment.this);
                            if (null != mSelectMarker) {  // 如果有选中的摄像机，则直接放大到该摄像机的位置
                                mHandler.sendEmptyMessageDelayed(MESSAGE_ANIMATE_TO_CAMERA, 300);
                            } else {  // 如果没有选中的Marker，则缩放到包含全部摄像机
                                if (items.size() == 1) {
                                    Message message = Message.obtain();
                                    message.obj = items.get(0);
                                    message.what = MESSAGE_ANIMATE_TO_ONE;
                                    mHandler.sendMessageDelayed(message, 300);
                                    return;
                                } else {
                                    for (ClusterItem clusterItem : items) {
                                        builder.include(clusterItem.getPosition());
                                    }
                                }
                                LatLngBounds latLngBounds = builder.build();
                                mAMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, SizeUtils.dp2px(32)));
                            }
                            return;
                        } else {
                            if (mClusterOverlay != null) {
                                mClusterOverlay.clearClusterItem();
                                mClusterOverlay.clearAllClusters();
                            }
                        }
                        if (clusterBuilder.isHasCameras() && null != mCurrentLatLng) {
                            mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLatLng, 10));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        hideLoading();
                    }
                });

    }


    @Override
    public void onLocateStart() {
    }

    @Override
    public void onLocateOK(AMapLocation aMapLocation) {
        // 取出经纬度
        mCurrentLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
        // 添加Marker显示定位位置
        if (mLocationMarker == null) {
            //如果是空的添加一个新的,icon方法就是设置定位图标，可以自定义
            mLocationMarker = mAMap.addMarker(new MarkerOptions()
                    .position(mCurrentLatLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.gps_point))
                    .anchor(0.5f, 0.5f));
            // 然后可以移动到定位点,使用animateCamera就有动画效果
            // mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLatLng, 10));
        } else {
            LatLng latLng = mLocationMarker.getPosition();
            if (latLng == null || !latLng.equals(mCurrentLatLng)) {
                // 已经添加过了，修改位置即可
                mLocationMarker.setPosition(mCurrentLatLng);
            }
        }
        mGisUtils.stop();
    }

    @Override
    public void onLocateFail(int errCode) {
        checkLocationPerm();
//        mGisUtils.stop();
    }

    /**
     * 如果某一个Marker或者聚合点被点击了
     *
     * @param marker       点击的聚合点
     * @param clusterItems 聚合点包含的数据集合
     */
    @Override
    public void onClick(Marker marker, List<ClusterItem> clusterItems) {
        if (null != clusterItems) {
            // 如果点击的聚合点数量大于 1
            if (clusterItems.size() > 1) {
                // 如果mRecyclerView是显示的，则先说明上一个也是在聚合点，先将其隐藏
                if (behaviorState != STATE_HIDDEN) {
                    dismissRecyclerView();
                    // 如果有高亮的摄像机，则先将其状态还原
                    if (null != mSelectCamera && null != mSelectMarker) {
                        mSelectMarker.remove();
                    }
                }
                // 如果有高亮的摄像机，则先将其状态还原
                if (null != mSelectCamera && null != mSelectMarker) {
                    resetSelectMarker();
                }
                if (mAMap.getCameraPosition().zoom >= 19.0f) { // 如果地图现在是最大缩放级别
                    ArrayList<OrgCameraEntity> list = new ArrayList<>();
                    for (ClusterItem item : clusterItems) {
                        list.add((OrgCameraEntity) item);
                    }
                    showRecyclerView(list);
                } else {  // 将地图放大到包含聚合点中所有摄像头的级别
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (ClusterItem clusterItem : clusterItems) {
                        builder.include(clusterItem.getPosition());
                    }
                    LatLngBounds latLngBounds = builder.build();
                    mAMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, SizeUtils.dp2px(80)));
                }

            } else {
                if (null != mSelectMarker && null != mSelectCamera) {
                    // 比较新选中的Marker与之前选中状态的Marker是否是同一个，如果是同一个Marker，则返回
                    if (TextUtils.equals(mSelectCamera.getManufacturerDeviceId() + "", clusterItems.get(0).getItemId()))
                        return;
                }

                // 如果mRecyclerView是显示的，先将其隐藏
                if (behaviorState != STATE_HIDDEN) {
                    dismissRecyclerView();
                }
                rvResources.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setSelectMarker(marker, (OrgCameraEntity) clusterItems.get(0));
                    }
                }, 50);
            }
        }

    }

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
    public void onStateChanged(View bottomSheet, int newState) {
        if (!(newState == BottomSheetBehavior.STATE_DRAGGING && slideOffset == 1)) {
            changeLocateIconPosition(newState);
        }
        behaviorState = newState;
        if ((canSlidesUp && newState == BottomSheetBehavior.STATE_HIDDEN) || (!canSlidesUp && newState == BottomSheetBehavior.STATE_COLLAPSED)) {
            resetSelectMarker();
        }
        if (canSlidesUp && newState == BottomSheetBehavior.STATE_COLLAPSED) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) clResources.getLayoutParams();
//            layoutParams.setMargins(0, -getResources().getDimensionPixelSize(R.dimen.dp12), 0, 0);
            TypedArray actionbarSizeTypedArray = getContext().obtainStyledAttributes(new int[]{
                    android.R.attr.actionBarSize
            });
            float margin = getResources().getDimensionPixelOffset(R.dimen.dp52) + BarViewUtils.getStatusBarHeight();
            layoutParams.setMargins(0, (int) margin, 0, 0);
        }
    }

    @Override
    public void onSlide(View bottomSheet, float slideOffset) {
        this.slideOffset = slideOffset;
        if (behaviorState == BottomSheetBehavior.STATE_DRAGGING && slideOffset != 1) {
            changeLocateIconPosition(behaviorState);
        }
        if (canSlidesUp) {
            slideOffset = slideOffset < 0 ? 0 : slideOffset;
            if(!Double.isNaN(slideOffset))
            ((DeviceMapActivity) getActivity()).getViewOverlay().setAlpha(slideOffset);
//            float point = 1.0f - (titleHeight * 1.0f / (SizeUtils.getScreenHeight() - titleHeight - bottomSheetBehavior.getPeekHeight()));
//            if (slideOffset >= point) {
//                float percent = -1 - (point - slideOffset) * 1.0f / (1 - point);
//            } else {
//            }
        }
    }

    /**
     * 显示RecyclerView
     *
     * @param clusterItems Marker中包含的数据集合
     */
    private void showRecyclerView(List<OrgCameraEntity> clusterItems) {
        List<OrgCameraEntity> cameraItems = new ArrayList<>();
        List<Long> cameraIds = new ArrayList<>();
        for (ClusterItem item : clusterItems) {
            ((OrgCameraEntity) item).setSelect(isSelect((OrgCameraEntity) item));
            cameraItems.add((OrgCameraEntity) item);
            cameraIds.add(((OrgCameraEntity) item).getManufacturerDeviceId());
        }
        mCameraResourceAdapter.setNewData(cameraItems);
        Long[] cids = cameraIds.toArray(new Long[cameraIds.size()]);
        mPresenter.getCameraCovers(cids);
        totalHeight = getContext().getResources().getDimensionPixelSize(R.dimen.dp86) * clusterItems.size() + getContext().getResources().getDimensionPixelSize(R.dimen.dp12);
        int percent40Height = getContext().getResources().getDimensionPixelSize(R.dimen.dp86) * 3 + getContext().getResources().getDimensionPixelSize(R.dimen.dp12);
        canSlidesUp = totalHeight > percent40Height;
        ((DeviceMapActivity) getActivity()).setCanSlidesUp(canSlidesUp);
        if (!canSlidesUp) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) clResources.getLayoutParams();
            layoutParams.setMargins(0, SizeUtils.getScreenHeight() - totalHeight, 0, 0);
            bottomSheetBehavior.setPeekHeight(0);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            bottomSheetBehavior.setPeekHeight(percent40Height);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

    }

    @Subscriber(tag = CAMERA_STATUS_CHANGE)
    public void modifyList(CollectChangeBean event) {
        if (mCameraResourceAdapter.getData().size() > 0) {
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

            }

            for (OrgCameraEntity entity : mCameraResourceAdapter.getData()) {
                entity.setSelect(false);
                for (CollectCameraEntity collectCameraEntity : mCollectionCameras) {
                    if (TextUtils.equals(String.valueOf(entity.getManufacturerDeviceId()), collectCameraEntity.getCid())) {
                        entity.setSelect(true);
                    }
                }
            }
            mCameraResourceAdapter.notifyDataSetChanged();
        }
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


    /**
     * 设置选中状态的Marker
     *
     * @param marker
     * @param cameraItem
     */
    private void setSelectMarker(Marker marker, OrgCameraEntity cameraItem) {
        // 设置新选中状态Marker的数据
        mSelectMarker = marker;
        mSelectCamera = cameraItem;
        mSelectMarker.setAnchor(0.5f, 1.0f);
        switch (mSelectCamera.getDeviceType().intValue()) {
            case Constants.ORG_CAMERA_QIUJI:
                mSelectMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.dome_camera));
                break;
            case Constants.ORG_CAMERA_ZHUAPAIJI:
                mSelectMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.capture_camera));
                break;
            case Constants.ORG_CAMERA_PHONE:
                mSelectMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.mobile_camera));
                break;
            default:
                mSelectMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.box_camera));
                break;
        }
        mClusterOverlay.setSelectMarker(mSelectMarker);
        ArrayList<OrgCameraEntity> list = new ArrayList<>();
        list.add(mSelectCamera);
        showRecyclerView(list);
    }

    /**
     * 重置选中状态的Marker
     *
     * @return
     */
    private void resetSelectMarker() {
        mCameraResourceAdapter.close();
        if (mSelectCamera != null) {
            int cameraStatus = mSelectCamera.getDeviceState();
            int cameraType = mSelectCamera.getDeviceType().intValue();
            int res = getCameraRes(cameraStatus, cameraType);
            mSelectMarker.setAnchor(0.5f, 1.0f);
            mSelectMarker.setIcon(BitmapDescriptorFactory.fromResource(res));
        }
        if (mClusterOverlay != null)
            mClusterOverlay.setSelectMarker(null);
        mSelectCamera = null;
    }

    /**
     * 隐藏RecyclerView
     */
    public void dismissRecyclerView() {
        if (null != bottomSheetBehavior) {
            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                resetSelectMarker();
            }
        }
    }

    /**
     * 更新定位图标的位置
     */
    private void changeLocateIconPosition(int state) {
        RelativeLayout.LayoutParams locaLP = (RelativeLayout.LayoutParams) mLocateLl.getLayoutParams();
        RelativeLayout.LayoutParams zoomLP = (RelativeLayout.LayoutParams) mZoomLl.getLayoutParams();
        int bottomHeight = 0;
        float bottom;
        switch (state) {
            case BottomSheetBehavior.STATE_DRAGGING:
            case BottomSheetBehavior.STATE_SETTLING:
                hideOrShowLocateIcon(false);
                break;
            case BottomSheetBehavior.STATE_EXPANDED:
                if (canSlidesUp) {
                    hideOrShowLocateIcon(false);
                } else {
                    bottom = totalHeight + getContext().getResources().getDimensionPixelSize(R.dimen.dp16) - bottomHeight;
                    locaLP.setMargins(getContext().getResources().getDimensionPixelSize(R.dimen.dp8), 0, 0, (int) bottom);
                    zoomLP.setMargins(0, 0, getContext().getResources().getDimensionPixelSize(R.dimen.dp8), (int) bottom);
                    hideOrShowLocateIcon(true);
                }
                break;
            case BottomSheetBehavior.STATE_COLLAPSED:
                if (canSlidesUp) {
                    bottom = bottomSheetBehavior.getPeekHeight() + getContext().getResources().getDimensionPixelSize(R.dimen.dp16) - bottomHeight;
                    locaLP.setMargins(getContext().getResources().getDimensionPixelSize(R.dimen.dp8), 0, 0, (int) bottom);
                    zoomLP.setMargins(0, 0, getContext().getResources().getDimensionPixelSize(R.dimen.dp8), (int) bottom);
                } else {
                    bottom = getContext().getResources().getDimensionPixelSize(R.dimen.dp16);
                    locaLP.setMargins(getContext().getResources().getDimensionPixelSize(R.dimen.dp8), 0, 0, (int) bottom);
                    zoomLP.setMargins(0, 0, getContext().getResources().getDimensionPixelSize(R.dimen.dp8), (int) bottom);
                }
                hideOrShowLocateIcon(true);
                break;
            case STATE_HIDDEN:
                bottom = getContext().getResources().getDimensionPixelSize(R.dimen.dp16);
                locaLP.setMargins(getContext().getResources().getDimensionPixelSize(R.dimen.dp8), 0, 0, (int) bottom);
                zoomLP.setMargins(0, 0, getContext().getResources().getDimensionPixelSize(R.dimen.dp8), (int) bottom);
                hideOrShowLocateIcon(true);
                break;
        }
        mLocateLl.setLayoutParams(locaLP);
        mZoomLl.setLayoutParams(zoomLP);

    }

    private void hideOrShowLocateIcon(boolean show) {
        if (mShowLocateIcon == show || mLocateIconAnim) return;
        ValueAnimator animator = ValueAnimator.ofFloat(show ? 0 : 1.0f, show ? 1.0f : 0);
        animator.setDuration(100);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mLocateIconAnim = false;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mLocateIconAnim = true;
            }
        });
        animator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            if (mLocateLl != null && mZoomLl != null) {
                mLocateLl.setAlpha(value);
                mZoomLl.setAlpha(value);
            }
        });
        animator.start();
        mShowLocateIcon = show;
    }

    @Override
    public Drawable getDrawAble(int clusterNum) {
        Drawable bitmapDrawable = mBackDrawAbles.get(clusterNum);
        if (null == bitmapDrawable) {
            if (clusterNum <= 1) {
                int cameraStatus = clusterNum & 1;
                int cameraType = -(clusterNum >> 1 | 0);
                bitmapDrawable = getActivity().getResources().getDrawable(getCameraRes(cameraStatus, cameraType));
            } else if (clusterNum <= 9) {
                bitmapDrawable = getResources().getDrawable(R.drawable.conflux9);
            } else if (clusterNum <= 100) {
                bitmapDrawable = getResources().getDrawable(R.drawable.conflux100);
            } else if (clusterNum <= 200) {
                bitmapDrawable = getResources().getDrawable(R.drawable.conflux200);
            } else if (clusterNum <= 500) {
                bitmapDrawable = getResources().getDrawable(R.drawable.conflux500);
            } else {
                bitmapDrawable = getResources().getDrawable(R.drawable.conflux999);
            }
            mBackDrawAbles.put(clusterNum, bitmapDrawable);
        }
        return bitmapDrawable;
    }


    private float lastBearing = 0;
    private RotateAnimation rotateAnimation;

    private void compassRotate(float bearing) {
        bearing = 360 - bearing;
        rotateAnimation = new RotateAnimation(lastBearing, bearing, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setFillAfter(true);
        mCompassIv.startAnimation(rotateAnimation);
        lastBearing = bearing;
    }

    @Override
    public void onCameraChange(CameraPosition var1) {
        compassRotate(var1.bearing);
    }

    @Override
    public void onCameraChangeFinish(CameraPosition var1) {
        if (null != mSelectMarker) {
            mSelectMarker.destroy();
            mSelectMarker = null;
            mSelectCamera = null;
        }
        dismissRecyclerView();
        if (mAMap.getCameraPosition().zoom >= 19.0f) {
            mZoomInIv.setEnabled(false);
            if (!mIsFirstZoomToMax) {
                mIsFirstZoomToMax = true;
            }
        } else {
            mIsFirstZoomToMax = false;
            mZoomInIv.setEnabled(true);
        }
        if (mAMap.getCameraPosition().zoom <= 3.0f) {
            mZoomOutIv.setEnabled(false);
            if (!mIsFirstZoomToMin) {
                mIsFirstZoomToMin = true;
                ToastUtils.showShort(R.string.zoom_to_min);
            }
        } else {
            mIsFirstZoomToMin = false;
            mZoomOutIv.setEnabled(true);
        }
    }

    @Override
    public void onSelectMarkerChange(Marker marker) {
        Cluster cluster = (Cluster) marker.getObject();
        ClusterItem clusterItem = cluster.getClusterItems().get(0);
        int itemType = clusterItem.getItemType();
        int cameraStatus = itemType & 1;
        int cameraType = -(itemType >> 1 | 0);
        marker.setAnchor(0.5f, 1.0f);
        marker.setIcon(BitmapDescriptorFactory.fromResource(getCameraRes(cameraStatus, cameraType)));

    }

    @Override
    public void onMarkerClustered(boolean fromConstructor) {
        Message message = Message.obtain();
        message.what = MESSAGE_MARKER_CLUSTER;
        message.obj = fromConstructor;
        if (null != mHandler) {
            mHandler.sendMessage(message);
        }
    }

    @Override
    public void onMarkerInMaxZoom() {
    }

    @Override
    public void onMarkerNotInMaxZoom() {
        // 移除第一次添加的被选中Marker
        if (null != mSelectMarker) {
            mSelectMarker.remove();
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (behaviorState != STATE_HIDDEN) {
            dismissRecyclerView();
        } else {
            if (null != mSelectMarker && null != mSelectCamera) {
                resetSelectMarker();
            }
        }
    }

    @Override
    public void onMapLoaded() {
//        justShowCollectionCamera(false);
    }

    @Override
    public void onMyLocationChange(Location location) {

    }

    private void onMapLoadedInitData() {
        showLoading("");
        long currentTimeMillis = System.currentTimeMillis();
        Observable.just(new Object())
                .map(new Function<Object, CloudMapFragment.ClusterBuilder>() {
                    @Override
                    public CloudMapFragment.ClusterBuilder apply(Object object) throws Exception {
                        CloudMapFragment.ClusterBuilder clusterBuilder = new CloudMapFragment.ClusterBuilder();
                        List<ClusterItem> items = new ArrayList<>();
                        List<OrgCameraEntity> cameras = DataSupport.findAll(OrgCameraEntity.class);
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        if (null != cameras && !cameras.isEmpty()) { // 如果包含摄像机
                            mIsLoadedData = true;
                            for (OrgCameraEntity item : cameras) {
                                if (null != item.getLatitude() && null != item.getLongitude()) {
                                    // item.setInstallAddress("武汉市洪山区关山大道332号保利国际中心10楼");
                                    // double[] latlon = GPSUtils.gps84_To_Gcj02(item.getLatitude(), item.getLongitude());
                                    // item.setLatitude(item.getLatitude());
                                    // item.setLongitude(item.getLongitude());
                                    if (mSelectCamera == null) {
                                        if (GPSUtils.isInChina(item.getLatitude(), item.getLongitude())) {
                                            items.add(item);
                                            builder.include(item.getPosition());
                                        }
                                    } else {
                                        items.add(item);
                                        builder.include(item.getPosition());
                                    }
                                }
                            }
                        }

                        if (!items.isEmpty()) {
                            mClusterOverlay = new ClusterOverlay(mAMap, items,
                                    SizeUtils.dp2px(clusterRadius),
                                    getActivity(), mSelectMarker);
                        }
                        clusterBuilder.setBuilder(builder);
                        clusterBuilder.setItems(items);
                        clusterBuilder.setHasCameras(cameras.isEmpty());
                        return clusterBuilder;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CloudMapFragment.ClusterBuilder>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CloudMapFragment.ClusterBuilder clusterBuilder) {
                        LogUtils.w("cxm", System.currentTimeMillis() - currentTimeMillis);
                        List<ClusterItem> items = clusterBuilder.getItems();
                        LatLngBounds.Builder builder = clusterBuilder.getBuilder();
                        if (!items.isEmpty()) {
                            // mClusterOverlay = new ClusterOverlay(mAMap, items,
                            //         SizeUtils.dp2px(clusterRadius),
                            //         getApplicationContext(), mSelectMarker);
                            mClusterOverlay.setClusterRenderer(CloudMapFragment.this);
                            mClusterOverlay.setMarkerChangeListener(CloudMapFragment.this);
                            mClusterOverlay.setOnClusterClickListener(CloudMapFragment.this);
                            mClusterOverlay.setOnMapChangeListener(CloudMapFragment.this);
                            if (null != mSelectMarker) {  // 如果有选中的摄像机，则直接放大到该摄像机的位置
                                mHandler.sendEmptyMessageDelayed(MESSAGE_ANIMATE_TO_CAMERA, 300);
                            } else {  // 如果没有选中的Marker，则缩放到包含全部摄像机
                                if (items.size() == 1) {
                                    Message message = Message.obtain();
                                    message.obj = items.get(0);
                                    message.what = MESSAGE_ANIMATE_TO_ONE;
                                    mHandler.sendMessageDelayed(message, 300);
                                    return;
                                }
                                LatLngBounds latLngBounds = builder.build();
                                mAMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, SizeUtils.dp2px(80)));
                            }
                            return;
                        }
                        if (clusterBuilder.isHasCameras() && null != mCurrentLatLng) {
                            mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLatLng, 10));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        hideLoading();
                    }
                });
    }

    @Override
    public Activity getFragmentActivity() {
        return getActivity();
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
        for (OrgCameraEntity orgCameraEntity : mCameraResourceAdapter.getData()) {
            orgCameraEntity.setSelect(isSelect(orgCameraEntity));
        }
        mCameraResourceAdapter.notifyDataSetChanged();
        EventBus.getDefault().post(new CollectChangeBean(), CAMERA_STATUS_CHANGE);
    }

    @Override
    public void getCoversSuccess(CameraNewStreamEntity entity) {
        List<CameraNewStreamEntity.DevicesBean> devices = entity.getDevices();
        if (null != devices && !devices.isEmpty()) {
            for (CameraNewStreamEntity.DevicesBean devicesBean : devices) {
                Long cid = devicesBean.getCid();
                for (OrgCameraEntity cameraEntity : mCameraResourceAdapter.getData()) {
                    Long deviceId = cameraEntity.getManufacturerDeviceId();
                    if (cid.equals(deviceId)) {
                        cameraEntity.setCoverUrl(devicesBean.getCover_url());
                        cameraEntity.setState(devicesBean.getState());
                        cameraEntity.setPlayUrl(devicesBean.getRtmp_url());
                        break;
                    }
                }
            }
            mCameraResourceAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void getCollectionsSuccess(GetKeyStoreBaseEntity organizationEntity) {
        if (null != organizationEntity) {
            GetKeyStoreEntity userKvStroe = organizationEntity.getUserKvStroe();
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


    class ClusterBuilder implements Serializable {
        private List<ClusterItem> items;
        private LatLngBounds.Builder builder;
        private boolean hasCameras;

        public boolean isHasCameras() {
            return hasCameras;
        }

        public void setHasCameras(boolean hasCameras) {
            this.hasCameras = hasCameras;
        }

        public List<ClusterItem> getItems() {
            return items;
        }

        public void setItems(List<ClusterItem> item) {
            this.items = item;
        }

        public LatLngBounds.Builder getBuilder() {
            return builder;
        }

        public void setBuilder(LatLngBounds.Builder builder) {
            this.builder = builder;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        if (null != mHandler) {
            mHandler.removeMessages(MESSAGE_MARKER_CLUSTER);
            mHandler.removeMessages(MESSAGE_ANIMATE_MAP);
            mHandler.removeMessages(MESSAGE_ANIMATE_TO_CAMERA);
            mHandler = null;
        }
        if (null != mNavigationStatusObserver) {
            getActivity().getContentResolver().unregisterContentObserver(mNavigationStatusObserver);
            mNavigationStatusObserver = null;
        }
        if (null != mClusterOverlay) {
            mClusterOverlay.onDestroy();
            mClusterOverlay = null;
        }
        if (null != mGisUtils) {
            mGisUtils.destory();
            mGisUtils = null;
        }
        if (null != mMapView) {
            mMapView.onDestroy();
            mMapView = null;
        }
        super.onDestroyView();
    }

    /**
     * 通过摄像机的状态和类型获取对应的资源id
     *
     * @param cameraStatus 摄像机状态
     * @param cameraType   摄像机类型
     * @return 摄像机对应的资源id
     */
    private int getCameraRes(int cameraStatus, int cameraType) {
        int res;
        switch (cameraType) {
            case Constants.ORG_CAMERA_QIUJI:
                res = cameraStatus == Constants.CAMERA_ONLINE ? R.drawable.dome_camera_unselected : R.drawable.qiuji_offline;
                break;
            case Constants.ORG_CAMERA_ZHUAPAIJI:
                res = cameraStatus == Constants.CAMERA_ONLINE ? R.drawable.capture_camera_unselected : R.drawable.qiangji_offline;
                break;
            case Constants.ORG_CAMERA_PHONE:
                res = cameraStatus == Constants.CAMERA_ONLINE ? R.drawable.mobile_camera_unselected : R.drawable.phone_offline;
                break;
            case Constants.ORG_CAMERA_NORMAL:
                res = cameraStatus == Constants.CAMERA_ONLINE ? R.drawable.box_camera_unselected : R.drawable.normal_offline;
                break;
            default:
                res = cameraStatus == Constants.CAMERA_ONLINE ? R.drawable.box_camera_unselected : R.drawable.normal_offline;
                break;
        }
        return res;
    }

}
