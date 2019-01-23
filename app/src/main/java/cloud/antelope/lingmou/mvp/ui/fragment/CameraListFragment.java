package cloud.antelope.lingmou.mvp.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.modle.PermissionDialog;
import com.lingdanet.safeguard.common.utils.LocationUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.PermissionUtils;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.di.component.DaggerCameraListComponent;
import cloud.antelope.lingmou.di.module.CameraListModule;
import cloud.antelope.lingmou.mvp.contract.CameraListContract;
import cloud.antelope.lingmou.mvp.model.entity.CameraItem;
import cloud.antelope.lingmou.mvp.model.entity.OrganizationEntity;
import cloud.antelope.lingmou.mvp.presenter.CameraListPresenter;
import cloud.antelope.lingmou.mvp.ui.activity.CameraMapActivity;
import cloud.antelope.lingmou.mvp.ui.activity.PlayerActivity;
import cloud.antelope.lingmou.mvp.ui.adapter.CameraAdapter;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class CameraListFragment extends BaseFragment<CameraListPresenter>
        implements CameraListContract.View,
        PermissionUtils.HasPermission,
        EasyPermissions.PermissionCallbacks {


    @BindView(R.id.camera_rclv)
    RecyclerView mCameraRclv;

    @Inject
    RecyclerView.LayoutManager mLayoutManager;
    @Inject
    CameraAdapter mCameraAdapter;
    @Inject
    RecyclerView.ItemAnimator mItemAnimator;
    @Inject
    RecyclerView.ItemDecoration mItemDecoration;

    private OrganizationEntity mOrganizationEntity;
    private String mOrgName;
    private CameraItem mClickCamera;
    private String mTitleName;

    public static CameraListFragment newInstance(String itemName, String orgName, OrganizationEntity orgEntity) {
        CameraListFragment cameraListFragment = new CameraListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("itemName", itemName);
        bundle.putString("orgnizationName", orgName);
        bundle.putSerializable("entity", orgEntity);
        cameraListFragment.setArguments(bundle);
        return cameraListFragment;
    }

    @Override
    public void setupFragmentComponent(AppComponent appComponent) {
        DaggerCameraListComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .cameraListModule(new CameraListModule(this, mOrgName))
                .build()
                .inject(this);
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera_list, container, false);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        mOrganizationEntity = (OrganizationEntity) arguments.getSerializable("entity");
        mOrgName = arguments.getString("orgnizationName");
        mTitleName = arguments.getString("itemName");

        mCameraRclv.setHasFixedSize(false);
        mCameraRclv.setLayoutManager(mLayoutManager);
        mCameraRclv.setItemAnimator(mItemAnimator);
        // mCameraRclv.addItemDecoration(mItemDecoration);
        mCameraRclv.setAdapter(mCameraAdapter);
        if (null != mOrganizationEntity) {
            List<CameraItem> cameraEntityList = mOrganizationEntity.getCameras();
            mCameraAdapter.setNewData(cameraEntityList);
            mCameraAdapter.setOrgName(mOrgName);
        }
        initListener();
    }

    private void initListener() {
        mCameraAdapter.setOnItemClickListener((adapter, view, position) -> {
            CameraItem cameraItem = (CameraItem) adapter.getItem(position);
            cameraItem.setOrgPath(mOrgName);
            Intent intent = new Intent(getContext(), PlayerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("data", cameraItem);
            intent.putExtras(bundle);
            startActivity(intent);

        });
        mCameraAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            mClickCamera = (CameraItem) adapter.getItem(position);
            mClickCamera.setOrgPath(mOrgName);
            checkLocationPerm();
            if (!LocationUtils.isGpsEnabled()) {
                LocationUtils.openGpsSettings();
            }

        });
    }

    @AfterPermissionGranted(PermissionUtils.RC_LOCATION_PERM)
    public void checkLocationPerm() {
        PermissionUtils.locationTask(this);
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
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        //        checkNotNull(intent);
        //        ArmsUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {

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
                final PermissionDialog dialog = new PermissionDialog(getContext());
                dialog.setTitle(getString(R.string.request_permission_tag));
                dialog.setMessage(getString(R.string.need_location_permission_tips));
                dialog.setNegativeListener(v -> {
                    dialog.dismiss();
                    ToastUtils.showShort(R.string.can_not_get_location);
                });
                dialog.show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public Activity getFragmentActivity() {
        return getActivity();
    }

}
