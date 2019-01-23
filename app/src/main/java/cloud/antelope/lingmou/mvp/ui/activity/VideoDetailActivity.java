package cloud.antelope.lingmou.mvp.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.modle.LoadingDialog;
import com.lingdanet.safeguard.common.utils.ToastUtils;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.di.component.DaggerVideoDetailComponent;
import cloud.antelope.lingmou.di.module.VideoDetailModule;
import cloud.antelope.lingmou.mvp.contract.VideoDetailContract;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;
import cloud.antelope.lingmou.mvp.presenter.VideoDetailPresenter;

import static com.jess.arms.utils.Preconditions.checkNotNull;

/**
 * 摄像机设备详情页面
 */
public class VideoDetailActivity extends BaseActivity<VideoDetailPresenter> implements VideoDetailContract.View, View.OnLongClickListener {

    @BindView(R.id.head_left_iv)
    ImageButton mHeadLeftIv;
    @BindView(R.id.title_tv)
    TextView mTitleTv;
    @BindView(R.id.device_name_tv)
    TextView mDeviceNameTv;
    @BindView(R.id.device_sn_tv)
    TextView mDeviceSnTv;
    @BindView(R.id.device_type_tv)
    TextView mDeviceTypeTv;
    @BindView(R.id.device_state_tv)
    TextView mDeviceStateTv;
    @BindView(R.id.device_organize_tv)
    TextView mDeviceOrganizeTv;
    @BindView(R.id.device_location_tv)
    TextView mDeviceLocationTv;
    @BindView(R.id.device_location_ll)
    LinearLayout mDeviceLocationLl;
    @BindView(R.id.device_address_type_tv)
    TextView mDeviceAddressTypeTv;
    @BindView(R.id.device_direction_tv)
    TextView mDeviceDirectionTv;
    @BindView(R.id.device_distance_tv)
    TextView mDeviceDistanceTv;
    @BindView(R.id.device_compony_tv)
    TextView mDeviceComponyTv;
    @BindView(R.id.device_phone_tv)
    TextView mDevicePhoneTv;
    @BindView(R.id.device_install_time_tv)
    TextView mDeviceInstallTimeTv;
    @BindView(R.id.real_video_tv)
    TextView mRealVideoTv;
    @BindView(R.id.history_video_tv)
    TextView mHistoryVideoTv;
    @BindView(R.id.face_depot_tv)
    TextView mFaceDepotTv;
    @BindView(R.id.body_depot_tv)
    TextView mBodyDepotTv;
    @BindView(R.id.function_ll)
    LinearLayout mFunctionLl;

    private String mCid;
    private String mCameraName;
    private String mCameraSn;
    private String mCoverUrl;
    private OrgCameraEntity mOrgCameraEntity;

    private LoadingDialog mLoadingDialog;

    private boolean mIsFromOrgMap;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerVideoDetailComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .videoDetailModule(new VideoDetailModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_video_detail; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mTitleTv.setText(R.string.device_detail_info);
        mLoadingDialog = new LoadingDialog(this);
        Parcelable serializableExtra = getIntent().getParcelableExtra("entity");
        mCid = getIntent().getStringExtra("cid");
        mCameraName = getIntent().getStringExtra("cameraName");
        mCameraSn = getIntent().getStringExtra("cameraSn");
        mCoverUrl = getIntent().getStringExtra("coverUrl");
        mIsFromOrgMap = getIntent().getBooleanExtra("isFromOrgMap", false);
//        if (mIsFromOrgMap) {
//            mFunctionLl.setVisibility(View.VISIBLE);
//        }
        mDeviceNameTv.setOnLongClickListener(this);
        mDeviceSnTv.setOnLongClickListener(this);
        if (null != serializableExtra) {
            mOrgCameraEntity = (OrgCameraEntity) serializableExtra;
            updateCameraInfo(mOrgCameraEntity);
        } else if (!TextUtils.isEmpty(mCid)) {
            mPresenter.getDeviceInfo(mCid);
        }
    }

    private void updateCameraInfo(OrgCameraEntity entity) {
        if (null != entity) {
            mCid = String.valueOf(entity.getManufacturerDeviceId());
            mOrgCameraEntity = entity;
            mDeviceNameTv.setText(entity.getDeviceName());
            mDeviceSnTv.setText(entity.getSn());

            Long deviceType = entity.getDeviceType();
            StringBuilder stringBuilder = new StringBuilder();
            if (deviceType == Constants.CAMERA_QIANG_JI) {
                stringBuilder.append("抓拍机");
            } else if (deviceType == Constants.CAMERA_QIU_JI) {
                stringBuilder.append("球机");
            } else if (deviceType == Constants.CAMERA_SHOU_JI) {
                stringBuilder.append("单兵");
            } else if (deviceType == Constants.CAMERA_NORMAL_JI) {
                stringBuilder.append("枪机");
            }
            mDeviceTypeTv.setText(stringBuilder.toString());
            String deviceState = entity.getDeviceStateReal() == 1 ? "在线" : "离线";
            mDeviceStateTv.setText(deviceState);
            mDeviceOrganizeTv.setText(entity.getOrganizationName());
            if ("未知".equals(entity.getInstallationLocationDetail())) {
                //进行反地理编码
                LatLonPoint point = new LatLonPoint(entity.getLatitude(), entity.getLongitude());
                RegeocodeQuery query = new RegeocodeQuery(point, 500, GeocodeSearch.AMAP);
                GeocodeSearch geocodeSearch = new GeocodeSearch(this);
                geocodeSearch.getFromLocationAsyn(query);
                geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
                    @Override
                    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                        if (i == 1000) {
                            RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
                            String formatAddress = regeocodeAddress.getFormatAddress();
                            mDeviceLocationTv.setText(formatAddress);
                        }
                    }

                    @Override
                    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

                    }
                });
            } else {
                mDeviceLocationTv.setText(entity.getInstallationLocationDetail());
            }
            mDeviceAddressTypeTv.setText(entity.getInstallationSiteName());
            mDeviceDirectionTv.setText(entity.getCameraOrientation());
            mDeviceDistanceTv.setText(entity.getInstallationHeigh());
            mDeviceComponyTv.setText(entity.getMaintenanceUnit());
            mDevicePhoneTv.setText(entity.getMaintenancePhone());
            mDeviceInstallTimeTv.setText(entity.getInstallationTime());
        }
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
        finish();
    }

    @OnClick({R.id.head_left_iv, R.id.device_location_ll, R.id.real_video_tv, R.id.history_video_tv,
            R.id.face_depot_tv, R.id.body_depot_tv})
    public void onViewClicked(View view) {
        Intent playIntent = new Intent(view.getContext(), PlayerNewActivity.class);
        playIntent.putExtra("cameraId", Long.parseLong(mCid));
        playIntent.putExtra("cameraName", mCameraName);
        playIntent.putExtra("cameraSn", mCameraSn);
        playIntent.putExtra("coverUrl", mCoverUrl);
        switch (view.getId()) {
            case R.id.head_left_iv:
                finish();
                break;
            case R.id.device_location_ll:
                //进入地图展示页面
                if (null != mOrgCameraEntity.getLatitude() && null != mOrgCameraEntity.getLongitude()) {
                    LatLng latLng = new LatLng(mOrgCameraEntity.getLatitude(), mOrgCameraEntity.getLongitude());
                    Intent intent = new Intent(VideoDetailActivity.this, DeviceShowMapActivity.class);
                    intent.putExtra("latlng", latLng);
                    startActivity(intent);
                } else {
                    ToastUtils.showShort(R.string.hint_no_device_latlong);
                }

                break;
            case R.id.real_video_tv:
                startActivity(playIntent);
                break;
            case R.id.history_video_tv:
                playIntent.putExtra("isHistory", true);
                startActivity(playIntent);
                break;
            case R.id.face_depot_tv:
                Intent faceIntent = new Intent(VideoDetailActivity.this, FaceDepotActivity.class);
                faceIntent.putExtra("fromDevice",true);
                faceIntent.putExtra("cameraId", Long.parseLong(mCid));
                startActivity(faceIntent);
                break;
            case R.id.body_depot_tv:
                Intent bodyIntent = new Intent(VideoDetailActivity.this, BodyDepotActivity.class);
                bodyIntent.putExtra("fromDevice",true);
                bodyIntent.putExtra("cameraId", Long.parseLong(mCid));
                startActivity(bodyIntent);
                break;
        }
    }

    @Override
    public void getDeviceInfoSuccess(OrgCameraEntity entity) {
        updateCameraInfo(entity);
    }

    @Override
    public void getDeviceInfoFail() {

    }

    @Override
    public boolean onLongClick(View v) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipData;
        switch (v.getId()) {
            case R.id.device_name_tv:
                mClipData = ClipData.newPlainText("Label", mDeviceNameTv.getText());
                cm.setPrimaryClip(mClipData);
                break;
            case R.id.device_sn_tv:
                 mClipData = ClipData.newPlainText("Label", mDeviceSnTv.getText());
                cm.setPrimaryClip(mClipData);
                break;
        }
        ToastUtils.showShort("复制成功");
        return false;
    }
}
