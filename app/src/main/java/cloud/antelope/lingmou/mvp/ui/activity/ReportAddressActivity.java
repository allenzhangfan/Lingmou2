package cloud.antelope.lingmou.mvp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.modle.PermissionDialog;
import com.lingdanet.safeguard.common.utils.ToastUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.GisUtils;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.di.component.DaggerReportAddressComponent;
import cloud.antelope.lingmou.di.module.ReportAddressModule;
import cloud.antelope.lingmou.mvp.contract.ReportAddressContract;
import cloud.antelope.lingmou.mvp.presenter.ReportAddressPresenter;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class ReportAddressActivity extends BaseActivity<ReportAddressPresenter>
        implements ReportAddressContract.View,
        GisUtils.OnLocateActionListener, GeocodeSearch.OnGeocodeSearchListener {


    @BindView(R.id.map_view)
    MapView mMapView;
    @BindView(R.id.clue_address_tv)
    TextView mAddressTV;
    @BindView(R.id.center_btn)
    RelativeLayout mCenterBtn;
    @BindView(R.id.head_left_iv)
    ImageButton mHeadLeftIv;
    @BindView(R.id.title_tv)
    TextView mTitleTv;
    @BindView(R.id.head_right_tv)
    TextView mHeadRightTv;
    @BindView(R.id.cetermarker_iv)
    ImageView mCetermarkerIv;

    @Inject
    GisUtils mGisUtils;
    @Inject
    GeocodeSearch mGeoCoder;

    private AMap mAMap;
    private LatLng mCurLoc;
    private LatLng mSelectLoc;
    private Marker mLocationMarker;
    // 是否是第一次定位
    private boolean mIsFirstLocate = true;

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        DaggerReportAddressComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .reportAddressModule(new ReportAddressModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_report_address; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mTitleTv.setText(R.string.report_address_text);
        mHeadRightTv.setVisibility(View.VISIBLE);
        mHeadRightTv.setText(R.string.confirm);

        mMapView.onCreate(savedInstanceState);// 此方法必须重写

        initMap();
        initListener();
    }

    private void initListener() {
        mGeoCoder.setOnGeocodeSearchListener(this);
    }

    private void initMap() {
        mAMap = mMapView.getMap();
        UiSettings uiSettings = mAMap.getUiSettings();
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(false);
        // uiSettings.setMyLocationButtonEnabled(true);
        // mAMap.setMyLocationEnabled(true);
        // mapView.removeViewAt(1);//去掉logo
        // 开启定位图层
        mGisUtils.setLocateListener(this);
        mAMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                mAddressTV.setText(R.string.get_address);
            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                // 获取地图中心点坐标
                mSelectLoc = cameraPosition.target;
                // 反解析出地址
                getAddress(new LatLonPoint(mSelectLoc.latitude, mSelectLoc.longitude));
            }
        });
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        // 启动定位
        mGisUtils.start();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        mMapView.onPause();
        if (mGisUtils != null) {
            mGisUtils.stop();
        }
        super.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @OnClick({R.id.center_btn, R.id.head_left_iv, R.id.head_right_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.head_left_iv:
                finish();
                break;
            case R.id.center_btn:
                if (mCurLoc != null) {
                    setCenterCoordinate(mCurLoc, mAMap.getMaxZoomLevel() - 4);
                }
                break;
            case R.id.head_right_tv:
                Intent intent = new Intent();
                if (mSelectLoc != null) {
                    intent.putExtra(Constants.EXTRA_ADDR, mAddressTV.getText().toString().trim());
                    intent.putExtra(Constants.EXTRA_LAT, mSelectLoc.latitude);
                    intent.putExtra(Constants.EXTRA_LON, mSelectLoc.latitude);
                }
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    /**
     * 响应逆地理编码
     */
    public void getAddress(final LatLonPoint latLonPoint) {
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        mGeoCoder.getFromLocationAsyn(query);// 设置异步逆地理编码请求
    }

    @Override
    public void onLocateStart() {

    }

    @Override
    public void onLocateOK(AMapLocation location) {
        if (location == null || mMapView == null)
            return;
        mCurLoc = new LatLng(location.getLatitude(), location.getLongitude());
        // 添加Marker显示定位位置
        if (mLocationMarker == null) {
            //如果是空的添加一个新的,icon方法就是设置定位图标，可以自定义
            mLocationMarker = mAMap.addMarker(new MarkerOptions()
                    .position(mCurLoc)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.gps_point))
                    .anchor(0.5f, 0.5f));
            // 然后可以移动到定位点,使用animateCamera就有动画效果
            // mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLatLng, 10));
        } else {
            LatLng latLng = mLocationMarker.getPosition();
            if (latLng == null || !latLng.equals(mCurLoc)) {
                // 已经添加过了，修改位置即可
                mLocationMarker.setPosition(mCurLoc);
            }
        }
        if (mIsFirstLocate) {
            mAddressTV.setText(GisUtils.getAbbrAddr(location));
            mSelectLoc = mCurLoc;
            setCenterCoordinate(mCurLoc, mAMap.getMaxZoomLevel() - 4);
            mIsFirstLocate = false;
        }
    }

    //    40.003303    116.486698
    private void setCenterCoordinate(LatLng latLng, float zoomlevel) {
        mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomlevel));
    }

    @Override
    public void onLocateFail(int errCode) {
        final PermissionDialog dialog = new PermissionDialog(this);
        dialog.setTitle(getString(R.string.request_permission_tag));
        dialog.setMessage(getString(R.string.need_camera_permission_tips));
        dialog.setNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ToastUtils.showShort(R.string.can_not_use_camera);
            }
        });
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        if (mGeoCoder != null) {
            mGeoCoder = null;
        }
        if (null != mGisUtils) {
            mGisUtils.destory();
            mGisUtils = null;
        }
        if (null != mMapView) {
            mMapView.onDestroy();
            mMapView = null;
        }
        super.onDestroy();
    }

    /**
     * 逆地理编码回调
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                mAddressTV.setText(GisUtils.getAbbrAddr(result.getRegeocodeAddress().getFormatAddress()));
                if (mSelectLoc == null) {
                    ToastUtils.showShort(R.string.get_lat_lng);
                }
            }
        }
    }

    /**
     * 地理编码查询回调
     */
    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

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

    @Override
    public Activity getActivity() {
        return this;
    }
}
