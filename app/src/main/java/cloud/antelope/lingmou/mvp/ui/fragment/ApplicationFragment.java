package cloud.antelope.lingmou.mvp.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.modle.PermissionDialog;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.lzy.imagepicker.ImagePicker;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.PermissionUtils;
import cloud.antelope.lingmou.app.utils.loader.GlideImageLoader;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.di.component.DaggerApplicationComponent;
import cloud.antelope.lingmou.di.module.ApplicationModule;
import cloud.antelope.lingmou.mvp.contract.ApplicationContract;
import cloud.antelope.lingmou.mvp.model.entity.AppBean;
import cloud.antelope.lingmou.mvp.model.entity.SoldierInfoEntity;
import cloud.antelope.lingmou.mvp.presenter.ApplicationPresenter;
import cloud.antelope.lingmou.mvp.ui.activity.BodyDepotActivity;
import cloud.antelope.lingmou.mvp.ui.activity.ClothControlAlarmActivity;
import cloud.antelope.lingmou.mvp.ui.activity.FaceDepotActivity;
import cloud.antelope.lingmou.mvp.ui.activity.LmCaptureActivity;
import cloud.antelope.lingmou.mvp.ui.activity.NewsListActivity;
import cloud.antelope.lingmou.mvp.ui.activity.NoPermissionActivity;
import cloud.antelope.lingmou.mvp.ui.activity.ReportEditActivity;
import cloud.antelope.lingmou.mvp.ui.activity.SoloActivity;
import cloud.antelope.lingmou.mvp.ui.adapter.AppAdapter;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class ApplicationFragment extends BaseFragment<ApplicationPresenter>
        implements ApplicationContract.View,
        PermissionUtils.HasPermission,
        EasyPermissions.PermissionCallbacks {

    @BindView(R.id.root)
    LinearLayout mRoot;
    @BindView(R.id.app_rclv)
    RecyclerView mAppRclv;

    @Inject
    List<AppBean> mAppBeanList;
    @Inject
    AppAdapter mAppAdapter;
    @Inject
    RecyclerView.LayoutManager mLayoutManager;
    @Inject
    RecyclerView.ItemAnimator mItemAnimator;

    private Drawable mBianlianDrawable;
    private Drawable mBukongDrawable;
    private Drawable mLiveDrawable;
    private Drawable mRenlianDrawable;
    private Drawable mBodyDrawable;
    private Drawable mClueDrawable;
    private Drawable mNewsDrawable;

    ImagePicker mImagePicker;
    private static ApplicationFragment fragment;

    public static ApplicationFragment newInstance() {
        fragment = new ApplicationFragment();
        return fragment;
    }

    @Override
    public void setupFragmentComponent(AppComponent appComponent) {
        DaggerApplicationComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .applicationModule(new ApplicationModule(this))
                .build()
                .inject(this);
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_app_layout, container, false);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        initDrawable();
        initImagePicker();
        initAppDatas();
        mPresenter.getSoldierInfo();
        mAppRclv.setLayoutManager(mLayoutManager);
        //        mAppRclv.addItemDecoration(new DividerGridItemDecoration(getActivity()));
        mAppRclv.setItemAnimator(mItemAnimator);
        mAppRclv.setAdapter(mAppAdapter);
        initListener();
    }

    private void initListener() {
        mAppAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                AppBean appBean = (AppBean) adapter.getItem(position);
                Intent permissionIntent = new Intent(getContext(), NoPermissionActivity.class);
                int id = appBean.getId();
                switch (id) {
                    case R.id.app_bianlian:
                        //进入辨脸界面
                        boolean hasPermission = SPUtils.getInstance().getBoolean(Constants.PERMISSION_HAS_HYJJ, false);
                        if (hasPermission) {
                            startFaceRecorg();
                        } else {
                            //没有权限，进入无权限页面
                            permissionIntent.putExtra("title", getString(R.string.search_image));
                            startActivity(permissionIntent);
                        }
                        break;
                    case R.id.app_danbing:
                        // 检查摄像机的权限
                        checkAudioPerm();
                        break;
                    case R.id.app_bukong:
                        //进入到布控报警界面
                        boolean alarmListPermission = SPUtils.getInstance().getBoolean(Constants.PERMISSION_HAS_ALARM_LIST, false);
                        if (alarmListPermission) {
                            startActivity(new Intent(getContext(), ClothControlAlarmActivity.class));
                        } else {
                            //没有权限，进入无权限页面
                            permissionIntent.putExtra("title", getString(R.string.cloth_control_alarm));
                            startActivity(permissionIntent);
                        }
                        break;
                    case R.id.app_renliantuku:
                        //进入人脸图库界面
                        boolean faceListPermission = SPUtils.getInstance().getBoolean(Constants.PERMISSION_HAS_FACE, false);
                        if (faceListPermission) {
                            startActivity(new Intent(getContext(), FaceDepotActivity.class));
                        } else {
                            //没有权限，进入无权限页面
                            permissionIntent.putExtra("title", getString(R.string.face_depot_title));
                            startActivity(permissionIntent);
                        }
                        break;
                    case R.id.app_rentituku:
                        //进入人体图库
                        boolean bodyListPermission = SPUtils.getInstance().getBoolean(Constants.PERMISSION_HAS_BODY, false);
                        if (bodyListPermission) {
                            startActivity(new Intent(getContext(), BodyDepotActivity.class));
                        } else {
                            //没有权限，进入无权限页面
                            permissionIntent.putExtra("title", getString(R.string.body_depot_title));
                            startActivity(permissionIntent);
                        }
                        break;
                    case R.id.app_clue:
                        startActivity(new Intent(getFragmentActivity(), ReportEditActivity.class));
                        break;
                    case R.id.app_news:
                        startActivity(new Intent(getFragmentActivity(), NewsListActivity.class));
                        break;
                }
            }
        });
    }

    private void initDrawable() {
        mBianlianDrawable = getResources().getDrawable(R.drawable.face_eyes);
        setBounds(mBianlianDrawable);
        mBukongDrawable = getResources().getDrawable(R.drawable.app_bukong);
        setBounds(mBukongDrawable);
        mLiveDrawable = getResources().getDrawable(R.drawable.app_soldier);
        setBounds(mLiveDrawable);
        mRenlianDrawable = getResources().getDrawable(R.drawable.face_gallery);
        setBounds(mRenlianDrawable);
        mBodyDrawable = getResources().getDrawable(R.drawable.body_gallery);
        setBounds(mBodyDrawable);
        mClueDrawable = getResources().getDrawable(R.drawable.app_clue);
        setBounds(mClueDrawable);
        mNewsDrawable = getResources().getDrawable(R.drawable.app_news);
        setBounds(mNewsDrawable);
    }

    private void setBounds(Drawable drawable) {
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
    }

    private void initImagePicker() {
        mImagePicker = ImagePicker.getInstance();
        mImagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        mImagePicker.setMultiMode(false);                      //单选模式
        mImagePicker.setShowCamera(false);                      //显示拍照按钮
        mImagePicker.setCrop(true);                            //允许裁剪（单选才有效）
        mImagePicker.setSaveRectangle(true);                   //是否按矩形区域保存
        mImagePicker.setMediaLimit(1);                         //选中媒体文件数量限制
        mImagePicker.setVideoLimit(0);                         //选中视频数量限制
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    private void initAppDatas() {
        mAppBeanList.clear();
        for (int i = 0; i < 6; i++) {
            AppBean appBean = new AppBean();
            switch (i) {
                case 0:
                    appBean.setName(getString(R.string.search_image));
                    appBean.setId(R.id.app_bianlian);
                    appBean.setTopDrawable(mBianlianDrawable);
                    break;
                case 1:
                    appBean.setName(getString(R.string.cloth_control_alarm));
                    appBean.setId(R.id.app_bukong);
                    appBean.setTopDrawable(mBukongDrawable);
                    break;
                case 2:
                    appBean.setName(getString(R.string.face_depot_title));
                    appBean.setId(R.id.app_renliantuku);
                    appBean.setTopDrawable(mRenlianDrawable);
                    break;
                case 3:
                    appBean.setName(getString(R.string.body_depot_title));
                    appBean.setId(R.id.app_rentituku);
                    appBean.setTopDrawable(mBodyDrawable);
                    break;
                case 4:
                    appBean.setName(getString(R.string.submit_case));
                    appBean.setId(R.id.app_clue);
                    appBean.setTopDrawable(mClueDrawable);
                    break;
                case 5:
                    appBean.setName(getString(R.string.police_news));
                    appBean.setId(R.id.app_news);
                    appBean.setTopDrawable(mNewsDrawable);
                    break;
            }
            mAppBeanList.add(appBean);
        }
        mAppAdapter.setNewData(mAppBeanList);
    }

    public void startFace() {
        // mImagePicker.setStyle(CropImageView.Style.FACE);  //裁剪框的形状
        // Intent intent = new Intent(this.getActivity(), ImageGridActivity.class);
        // intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
        // intent.putExtra(JCameraView.TYPE_CAPTURE, JCameraView.BUTTON_STATE_ONLY_CAPTURE); // 是否是直接打开相机
        // intent.putExtra(ImageGridActivity.EXTRAS_LOAD_TYPE, ImageDataSource.LOADER_ALL_IMAGE); // 只加载图片
        // startActivityForResult(intent, REQ_IMAGE_CROP);
        startActivity(new Intent(getContext(), LmCaptureActivity.class));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // case REQ_IMAGE_CROP:
            //     if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //         ArrayList<MediaItem> mediaItems = (ArrayList<MediaItem>)
            //                 data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
            //         String faceEntityJson = data.getStringExtra("userEntity");
            //         if (mediaItems.size() <= 0) {
            //             break;
            //         }
            //         String facePath = mediaItems.get(0).path;
            //         Intent intent = new Intent(getActivity(), FaceRecognizeActivity.class);
            //         intent.putExtra("facePath", facePath);
            //         intent.putExtra("faceEntity", faceEntityJson);
            //         startActivity(intent);
            //     }
            //     break;
        }
    }

    @AfterPermissionGranted(PermissionUtils.RC_CAMERA_PERM)
    public void startFaceRecorg() {
        PermissionUtils.cameraTask(this);
    }

    @AfterPermissionGranted(PermissionUtils.RC_RECORD_AUDIO_PERM)
    public void checkAudioPerm() {
        PermissionUtils.audioTask(this);
    }

    @Override
    public void doNext(int permId) {
        if (PermissionUtils.RC_CAMERA_PERM == permId) {
            // 进入羚眸变脸
            startFace();
        } else if (PermissionUtils.RC_RECORD_AUDIO_PERM == permId) {
            //进入单兵界面
            startActivity(new Intent(getFragmentActivity(), SoloActivity.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            if (perms.contains(Manifest.permission.CAMERA)) {
                final PermissionDialog dialog = new PermissionDialog(getActivity());
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
            } else if (perms.contains(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                final PermissionDialog dialog = new PermissionDialog(getActivity());
                dialog.setTitle(getString(R.string.request_permission_tag));
                dialog.setMessage(getString(R.string.need_read_sdcard_permission_tips));
                dialog.setNegativeListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        ToastUtils.showShort(R.string.can_not_read_picture);
                    }
                });
                dialog.show();
            } else if (perms.contains(Manifest.permission.RECORD_AUDIO)) {
                final PermissionDialog dialog = new PermissionDialog(getActivity());
                dialog.setTitle(getString(R.string.request_permission_tag));
                dialog.setMessage(getString(R.string.need_audio_permission_tips));
                dialog.setNegativeListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        ToastUtils.showShort(R.string.can_not_use_audio);
                    }
                });
                dialog.show();
            }

        }
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
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {

    }

    @Override
    public Activity getFragmentActivity() {
        return this.getActivity();
    }

    @Override
    public void getSoldierInfoSuccess(SoldierInfoEntity entity) {
        if (null != entity) {
            if (null != entity.getManufacturerDeviceId()) {
                SPUtils.getInstance().put(Constants.CONFIG_SOLO_CID, entity.getManufacturerDeviceId() + "");
                //表示是有单兵
                if (mAppBeanList.size() < 7) {
                    AppBean appBean = new AppBean();
                    appBean.setName(getString(R.string.solo_broadcast));
                    appBean.setId(R.id.app_danbing);
                    appBean.setTopDrawable(mLiveDrawable);
                    mAppBeanList.add(4, appBean);
                }
                mAppAdapter.setNewData(mAppBeanList);
            }
            if (!TextUtils.isEmpty(entity.getSn())) {
                SPUtils.getInstance().put(Constants.CONFIG_SOLO_SN, entity.getSn());
            }
            if (!TextUtils.isEmpty(entity.getId())) {
                SPUtils.getInstance().put(Constants.CONFIG_SOLO_ID, entity.getId());
            }
        }
    }
}
