package cloud.antelope.lingmou.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.utils.Configuration;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.di.component.DaggerDeviceShowMapComponent;
import cloud.antelope.lingmou.di.module.DeviceShowMapModule;
import cloud.antelope.lingmou.mvp.contract.DeviceShowMapContract;
import cloud.antelope.lingmou.mvp.presenter.DeviceShowMapPresenter;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class DeviceShowMapActivity extends BaseActivity<DeviceShowMapPresenter> implements DeviceShowMapContract.View {

    @BindView(R.id.head_left_iv)
    ImageButton mHeadLeftIv;
    @BindView(R.id.title_tv)
    TextView mTitleTv;
    @BindView(R.id.device_show_mapview)
    MapView mDeviceShowMapview;

    private AMap mAMap;
    private LatLng mDeviceLatLng;
    private Long mDevideType;
    private Marker mDeviceMarker;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerDeviceShowMapComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .deviceShowMapModule(new DeviceShowMapModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_device_show_map; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mTitleTv.setText("设备地图");
        int drawableRes = -1;
        mDeviceLatLng = getIntent().getParcelableExtra("latlng");
        mDevideType = getIntent().getLongExtra("deviceType", -1);
        if (mDevideType == Constants.CAMERA_QIANG_JI) {
            drawableRes = R.drawable.box_camera;
        } else if (mDevideType == Constants.CAMERA_QIU_JI) {
            drawableRes = R.drawable.dome_camera;
        } else if (mDevideType == Constants.CAMERA_SHOU_JI) {
            drawableRes = R.drawable.mobile_camera;
        } else if (mDevideType == Constants.CAMERA_NORMAL_JI) {
            drawableRes = R.drawable.capture_camera;
        } else {
            drawableRes = R.drawable.capture_camera;
        }
        if (null == mDeviceLatLng) {
            mDeviceLatLng = new LatLng(30.491219, 114.410045);
        }
        mAMap = mDeviceShowMapview.getMap();
        mDeviceShowMapview.onCreate(savedInstanceState);
        mAMap.setCustomMapStylePath(Configuration.getRootPath() + Constants.MAP_STYLE_FILE + "style.data");
        mAMap.setMapCustomEnable(true);
        UiSettings uiSettings = mAMap.getUiSettings();
        uiSettings.setCompassEnabled(false);
        uiSettings.setZoomControlsEnabled(false);
        mDeviceMarker = mAMap.addMarker(new MarkerOptions()
                .position(mDeviceLatLng)
                .icon(BitmapDescriptorFactory.fromResource(drawableRes))
                .anchor(0.5f, 0.5f));
        mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mDeviceLatLng, 16));
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

    @OnClick(R.id.head_left_iv)
    public void onViewClicked() {
        finish();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mDeviceShowMapview.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        mDeviceShowMapview.onPause();
        super.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mDeviceShowMapview.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        if (null != mDeviceShowMapview) {
            mDeviceShowMapview.onDestroy();
            mDeviceShowMapview = null;
        }
        super.onDestroy();
    }
}
