package cloud.antelope.lingmou.mvp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.util.LruCache;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.utils.SizeUtils;
import com.lingdanet.safeguard.common.utils.SpanUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.lingdanet.safeguard.common.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.ResultHolder;
import cloud.antelope.lingmou.di.component.DaggerPersonTrackComponent;
import cloud.antelope.lingmou.di.module.PersonTrackModule;
import cloud.antelope.lingmou.mvp.contract.PersonTrackContract;
import cloud.antelope.lingmou.mvp.model.entity.DetailCommonEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceNewEntity;
import cloud.antelope.lingmou.mvp.presenter.PersonTrackPresenter;
import cloud.antelope.lingmou.mvp.ui.adapter.PersonTrackAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.PersonTrackPagerAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.AlphaTransformer;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class PersonTrackActivity extends BaseActivity<PersonTrackPresenter>
        implements PersonTrackContract.View, AMap.OnMarkerClickListener,
        AMap.OnCameraChangeListener, ViewPager.OnPageChangeListener, AMap.OnMapClickListener {

    /**
     * Marker之间的最小距离，单位是：米。
     */
    private static final float MARKER_MIN_DISTACE = 10.0f;
    private static final int RECYCLEVIEW_MAX_HEIGHT = 276;

    @BindView(R.id.head_left_iv)
    ImageButton mHeadLeftIv;
    @BindView(R.id.title_tv)
    TextView mTitleTv;
    @BindView(R.id.map_view)
    MapView mMapView;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.marker_title_tv)
    TextView mMarkerTitleTv;
    @BindView(R.id.marker_list_ll)
    LinearLayout mMarkerListLl;
    @BindView(R.id.zoom_in_iv)
    ImageView mZoomInIv;
    @BindView(R.id.zoom_out_iv)
    ImageView mZoomOutIv;
    @BindView(R.id.compass_iv)
    ImageView compassIv;

    @Inject
    List<FaceNewEntity> mFaceList;
    @Inject
    List<LatLng> mPositionList;
    @Inject
    List<String> mDeviceList;
    @Inject
    List<Marker> mMarkerList;
    @Inject
    PersonTrackPagerAdapter mPagerAdapter;
    @Inject
    PersonTrackAdapter mAdapter;
    @Inject
    RecyclerView.LayoutManager mLayoutManager;
    @Inject
    RecyclerView.ItemAnimator mItemAnimator;
    @Inject
    RecyclerView.ItemDecoration mItemDecoration;
    @Inject
    LruCache<Integer, BitmapDescriptor> mLruCache;


    private AlphaTransformer mCardAlphaTransformer;

    private AMap mAMap;
    private Marker mLastClickMarker;
    private BitmapDescriptor mSelectDescriptor;
    private BitmapDescriptor mUnselectDescriptor;

    private boolean mIsFirstZoomToMax;
    private boolean mIsFirstZoomToMin;

    private boolean mIsFirstIn = true;

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        DaggerPersonTrackComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .personTrackModule(new PersonTrackModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_person_track; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mTitleTv.setText(R.string.person_track);
        mMapView.onCreate(savedInstanceState);// 此方法必须重写

        initDescriptor();

        initMap();

        mFaceList = ResultHolder.getSelectFaceEntities();

        Collections.sort(mFaceList, new Comparator<FaceNewEntity>() {
            @Override
            public int compare(FaceNewEntity o1, FaceNewEntity o2) {
                return (int) (Long.parseLong(o1.captureTime) - Long.parseLong(o2.captureTime));
            }
        });
        mPagerAdapter.setNewData(mFaceList);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i < mFaceList.size(); i++) {
            FaceNewEntity faceNewEntity = mFaceList.get(i);
            int position = i + 1;
            faceNewEntity.position = position;
            LatLng latLng = new LatLng(Double.valueOf(faceNewEntity.latitide),
                    Double.valueOf(faceNewEntity.longitude));
            mPositionList.add(latLng);
            Marker existMarker = getMarker(latLng, mMarkerList);
            if (null == existMarker) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.anchor(0.5f, 0.5f)
                        .icon(i == 0 ? mSelectDescriptor : mUnselectDescriptor)
                        .position(latLng);
                Marker marker = mAMap.addMarker(markerOptions);
                marker.setObject(String.valueOf(position));
                mMarkerList.add(marker);
                if (i == 0) {
                    marker.setZIndex(2.0f);
                    marker.setIcon(getBitmapDes(position));
                    mLastClickMarker = marker;
                } else {
                    marker.setZIndex(1.0f);
                }
                builder.include(latLng);
            } else {
                String existPosition = (String) existMarker.getObject();
                existPosition += " " + String.valueOf(position);
                existMarker.setObject(existPosition);
            }
        }
        LatLngBounds latLngBounds = builder.build();
        mAMap.animateCamera(CameraUpdateFactory.newLatLngBoundsRect(
                latLngBounds, SizeUtils.dp2px(16), SizeUtils.dp2px(16),
                SizeUtils.dp2px(16), SizeUtils.dp2px(106)));
        PolylineOptions polylineOptions = new PolylineOptions();
        for (int j = 0; j < mPositionList.size(); j++) {
            polylineOptions.add(mPositionList.get(j));
        }
        mAMap.addPolyline(polylineOptions
                .width(SizeUtils.dp2px(3))
                .color(Color.argb(152, 54, 168, 255)));
        mAMap.getUiSettings().setCompassEnabled(false);
        mCardAlphaTransformer = new AlphaTransformer();
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setPageTransformer(false, mCardAlphaTransformer);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setPageMargin(SizeUtils.dp2px(8));
        mViewPager.addOnPageChangeListener(this);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(mItemAnimator);
        mRecyclerView.addItemDecoration(mItemDecoration);

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                FaceNewEntity item = (FaceNewEntity) adapter.getItem(position);
                int viewPagerPos = item.position;
                LatLng latLng = new LatLng(Double.valueOf(item.latitide), Double.valueOf(item.longitude));
                Marker marker = getMarker(latLng, mMarkerList);
                if (null != mLastClickMarker) {
                    mLastClickMarker.setIcon(mUnselectDescriptor);
                }
                marker.setIcon(getBitmapDes(viewPagerPos));
                mLastClickMarker = marker;
                mViewPager.setVisibility(View.VISIBLE);
                mMarkerListLl.setVisibility(View.GONE);
                mViewPager.setCurrentItem(viewPagerPos - 1);
            }
        });
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(PersonTrackActivity.this, PictureDetailActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("isFromRecorg", true);
                ArrayList<FaceNewEntity> detailEntities = (ArrayList<FaceNewEntity>) adapter.getData();
                ArrayList<DetailCommonEntity> changeData = getChangeData(detailEntities);
                if (changeData != null) {
                    intent.putParcelableArrayListExtra("bean", changeData);
                    startActivity(intent);
                }
            }
        });
        mPagerAdapter.setOnItemChildClickListener(new PersonTrackPagerAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(PagerAdapter adapter, View view, int position) {

                Intent intent = new Intent(PersonTrackActivity.this, PictureDetailActivity.class);
                intent.putExtra("position", 0);
                intent.putExtra("isFromRecorg", true);
                FaceNewEntity faceNewEntity = mFaceList.get(position);
                ArrayList<DetailCommonEntity> list = new ArrayList<>();
                DetailCommonEntity commonEntity = new DetailCommonEntity();
                commonEntity.deviceName = faceNewEntity.cameraName;
                commonEntity.endTime = Long.parseLong(faceNewEntity.captureTime);
                commonEntity.point = faceNewEntity.faceRect;
                commonEntity.srcImg = faceNewEntity.scenePath;
                list.add(commonEntity);
                intent.putParcelableArrayListExtra("bean", list);
                startActivity(intent);
//                String sceneImg = mFaceList.get(position).scenePath;
//                Intent intent = new Intent();
//                intent.setClass(PersonTrackActivity.this, FaceDetailActivity.class);
//                intent.putExtra("faceUrl", sceneImg);
//                intent.putExtra("point", mFaceList.get(position).faceRect);
//                startActivity(intent);
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    private ArrayList<DetailCommonEntity> getChangeData(ArrayList<FaceNewEntity> entities) {
        if (null != entities) {
            ArrayList<DetailCommonEntity> list = new ArrayList<>();
            for (FaceNewEntity entity : entities) {
                DetailCommonEntity commonEntity = new DetailCommonEntity();
                commonEntity.deviceName = entity.cameraName;
                commonEntity.endTime = Long.parseLong(entity.captureTime);
                commonEntity.point = entity.faceRect;
                commonEntity.srcImg = entity.scenePath;
                list.add(commonEntity);
            }
            return list;
        }
        return null;
    }

    private void initDescriptor() {
        mSelectDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.track_new_select);
        mUnselectDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.track_new_unselect);
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
        // mGisUtils.setLocateListener(this);
        // mAMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
        //     @Override
        //     public void onCameraChange(CameraPosition cameraPosition) {
        //         mAddressTV.setText(R.string.get_address);
        //     }
        //
        //     @Override
        //     public void onCameraChangeFinish(CameraPosition cameraPosition) {
        //         // 获取地图中心点坐标
        //         mSelectLoc = cameraPosition.target;
        //         // 反解析出地址
        //         getAddress(new LatLonPoint(mSelectLoc.latitude, mSelectLoc.longitude));
        //     }
        // });

        mAMap.setOnMarkerClickListener(this);
        mAMap.setOnCameraChangeListener(this);
        mAMap.setOnMapClickListener(this);
    }

    @OnClick({R.id.head_left_iv, R.id.zoom_in_iv, R.id.zoom_out_iv, R.id.compass_iv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.head_left_iv:
                finish();
                break;
            case R.id.zoom_in_iv:
                changeCamera(CameraUpdateFactory.zoomIn(), true, null);
                break;
            case R.id.zoom_out_iv:
                changeCamera(CameraUpdateFactory.zoomOut(), true, null);
                break;
            case R.id.compass_iv:
                float bearing = 0.0f;  // 地图默认方向
                CameraPosition cameraPosition = mAMap.getCameraPosition();
                mAMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(cameraPosition.target, cameraPosition.zoom, cameraPosition.tilt, bearing)));
                break;
        }
    }

    /**
     * 根据一个摄像机获取是否可以依附的Marker点，没有则返回null
     *
     * @param latLng
     * @return
     */
    private Marker getMarker(LatLng latLng, List<Marker> markers) {
        for (Marker marker : markers) {
            LatLng markerPosition = marker.getPosition();
            double distance = AMapUtils.calculateLineDistance(latLng, markerPosition);
            if (distance < MARKER_MIN_DISTACE && mAMap.getCameraPosition().zoom <= 19.0f) {
                return marker;
            }
        }
        return null;
    }

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
    public boolean onMarkerClick(Marker marker) {
        // ToastUtils.showShort("Marker被点击了");
        if (null != mLastClickMarker) {
            mLastClickMarker.setIcon(mUnselectDescriptor);
            mLastClickMarker.setZIndex(1.0f);
        }
        mViewPager.setVisibility(View.GONE);
        changeCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18.8f), true, null);
        marker.setIcon(mSelectDescriptor);
        marker.setZIndex(2.0f);
        String positionStr = (String) marker.getObject();
        String[] positions = positionStr.split(" ");
        List<FaceNewEntity> list = new ArrayList<>();
        for (String position : positions) {
            for (FaceNewEntity faceNewEntity : mFaceList) {
                if (position.equals(String.valueOf(faceNewEntity.position))) {
                    list.add(faceNewEntity);
                }
            }
        }
        ViewGroup.LayoutParams params = mRecyclerView.getLayoutParams();
        if (list.size() > 3) {
            params.height = SizeUtils.dp2px(RECYCLEVIEW_MAX_HEIGHT);
        } else {
            params.height = RecyclerView.LayoutParams.WRAP_CONTENT;
        }
        mRecyclerView.setLayoutParams(params);

        SpannableStringBuilder markerTitle =
                new SpanUtils().append("此点位共抓拍")
                        .setForegroundColor(Utils.getContext().getResources().getColor(R.color.detail_title))
                        .append(String.valueOf(list.size()))
                        .setForegroundColor(Utils.getContext().getResources().getColor(R.color.yellow_ffb300))
                        .append("张")
                        .setForegroundColor(Utils.getContext().getResources().getColor(R.color.detail_title))
                        .setFlag(SpannableString.SPAN_INCLUSIVE_EXCLUSIVE)
                        .create();
        mMarkerTitleTv.setText(markerTitle);
        mAdapter.setNewData(list);
        mMarkerListLl.setVisibility(View.VISIBLE);
        mLastClickMarker = marker;
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (mMarkerListLl.getVisibility() == View.VISIBLE) {
            mMarkerListLl.setVisibility(View.GONE);
            if (null != mLastClickMarker) {
                mLastClickMarker.setIcon(mUnselectDescriptor);
                mLastClickMarker = null;
            }
        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        compassRotate(cameraPosition.bearing);
    }

    private float lastBearing = 0;
    private RotateAnimation rotateAnimation;

    private void compassRotate(float bearing) {
        bearing = 360 - bearing;
        rotateAnimation = new RotateAnimation(lastBearing, bearing, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setFillAfter(true);
        compassIv.startAnimation(rotateAnimation);
        lastBearing = bearing;
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        if (mAMap.getCameraPosition().zoom >= 19.0f) {
            mZoomInIv.setAlpha(0.3f);
            if (!mIsFirstZoomToMax) {
                mIsFirstZoomToMax = true;
                if (!mIsFirstIn) {
                    ToastUtils.showShort(R.string.zoom_to_max);
                }
            }
        } else {
            mIsFirstZoomToMax = false;
            mZoomInIv.setAlpha(1.0f);
        }
        if (mAMap.getCameraPosition().zoom <= 3.0f) {
            mZoomOutIv.setAlpha(0.3f);
            if (!mIsFirstZoomToMin) {
                mIsFirstZoomToMin = true;
                ToastUtils.showShort(R.string.zoom_to_min);
            }
        } else {
            mIsFirstZoomToMin = false;
            mZoomOutIv.setAlpha(1.0f);
        }
        mIsFirstIn = false;
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        mMapView.onPause();
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

    @Override
    protected void onDestroy() {
        if (null != mMapView) {
            mMapView.onDestroy();
            mMapView = null;
        }
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
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        FaceNewEntity faceNewEntity = mFaceList.get(position);
        LatLng latLng = new LatLng(Double.valueOf(faceNewEntity.latitide),
                Double.valueOf(faceNewEntity.longitude));
        changeCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.8f), true, null);
        if (null != mLastClickMarker) {
            mLastClickMarker.setIcon(mUnselectDescriptor);
        }
        Marker marker = getMarker(latLng, mMarkerList);
        if (null != marker) {
            marker.setIcon(getBitmapDes(faceNewEntity.position));
        } else {
            ToastUtils.showShort("该照片位置信息获取失败");
        }
        mLastClickMarker = marker;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * 获取每个人脸的绘制样式
     */
    private BitmapDescriptor getBitmapDes(int num) {
        BitmapDescriptor bitmapDescriptor = mLruCache.get(num);
        if (bitmapDescriptor == null) {
            TextView textView = new TextView(this);
            textView.setText(String.valueOf(num));
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setPadding(0, SizeUtils.dp2px(10), 0, 0);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            lp.setMargins(0, 0, SizeUtils.dp2px(3), 0);
            textView.setLayoutParams(lp);
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            textView.setBackgroundResource(R.drawable.track_new_select);
            bitmapDescriptor = BitmapDescriptorFactory.fromView(textView);
            mLruCache.put(num, bitmapDescriptor);
        }
        return bitmapDescriptor;
    }
}
