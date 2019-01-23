package cloud.antelope.lingmou.mvp.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.constant.CommonConstant;
import com.lingdanet.safeguard.common.modle.PermissionDialog;
import com.lingdanet.safeguard.common.utils.AppUtils;
import com.lingdanet.safeguard.common.utils.DeviceUtil;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.SizeUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.lingdanet.safeguard.common.utils.Utils;
import com.umeng.analytics.MobclickAgent;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.CollectedPictureHolder;
import cloud.antelope.lingmou.app.utils.JPushUtils;
import cloud.antelope.lingmou.app.utils.PermissionUtils;
import cloud.antelope.lingmou.app.utils.SaveUtils;
import cloud.antelope.lingmou.app.utils.ViewUtils;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.di.component.DaggerNewMainComponent;
import cloud.antelope.lingmou.di.module.AppUpdateModule;
import cloud.antelope.lingmou.di.module.CyqzConfigModule;
import cloud.antelope.lingmou.di.module.NewMainModule;
import cloud.antelope.lingmou.mvp.contract.AppUpdateContract;
import cloud.antelope.lingmou.mvp.contract.CyqzConfigContract;
import cloud.antelope.lingmou.mvp.contract.NewMainContract;
import cloud.antelope.lingmou.mvp.model.entity.CarBrandBean;
import cloud.antelope.lingmou.mvp.model.entity.CarInfo;
import cloud.antelope.lingmou.mvp.model.entity.CollectionListBean;
import cloud.antelope.lingmou.mvp.model.entity.CollectionListRequest;
import cloud.antelope.lingmou.mvp.model.entity.OperationEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraParentEntity;
import cloud.antelope.lingmou.mvp.presenter.AppUpdatePresenter;
import cloud.antelope.lingmou.mvp.presenter.CyqzConfigPresenter;
import cloud.antelope.lingmou.mvp.presenter.NewMainPresenter;
import cloud.antelope.lingmou.mvp.ui.fragment.CloudMapFragment;
import cloud.antelope.lingmou.mvp.ui.fragment.DailyFragment;
import cloud.antelope.lingmou.mvp.ui.fragment.DailyPoliceFragment;
import cloud.antelope.lingmou.mvp.ui.fragment.MineFragment;
import cloud.antelope.lingmou.mvp.ui.fragment.WorkbenchFragment;
import cn.jpush.android.api.JPushInterface;
import me.yokeyword.fragmentation.ISupportFragment;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;

import static cloud.antelope.lingmou.mvp.ui.fragment.CloudMapFragment.MESSAGE_LOAD_CAMERA_DONE;
import static com.jess.arms.utils.Preconditions.checkNotNull;


public class NewMainActivity extends BaseActivity<NewMainPresenter> implements NewMainContract.View, CyqzConfigContract.View, AppUpdateContract.View,
        EasyPermissions.PermissionCallbacks {
    private static final int DELAY_INIT_JPUSH = 0x01;
    @BindView(R.id.fl_container)
    FrameLayout flContainer;
    @BindView(R.id.tv_daily)
    TextView tvDaily;
    @BindView(R.id.tv_workbench)
    TextView tvWorkbench;
    @BindView(R.id.tv_mine)
    TextView tvMine;
    @BindView(R.id.rv_resources)
    RecyclerView rvResources;
    @BindView(R.id.include_resources)
    CoordinatorLayout clResources;
    @BindView(R.id.bottom_ll)
    LinearLayout mBottomLl;
    @BindView(R.id.ll_sheet)
    LinearLayout llSheet;
    @BindView(R.id.rl_slid)
    RelativeLayout rlSlid;
    @Inject
    CyqzConfigPresenter mCyqzConfigPresenter;
    @Inject
    AppUpdatePresenter mAppUpdatePresenter;
    private ISupportFragment[] mFragments = new ISupportFragment[3];
    private int prePosition = 0;
    private BottomSheetListener bottomSheetListener;
    private BottomSheetBehavior mBottomSheetBehavior;
    private int titleHeight;
    private boolean canSlidesUp;
    private Dialog mUpdateDialog;
    private String mDownloadUrl;
    private boolean mIsForceUpdate;
    private boolean mFirstBack = true;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerNewMainComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .newMainModule(new NewMainModule(this))
                .appUpdateModule(new AppUpdateModule(this))
                .cyqzConfigModule(new CyqzConfigModule(this))
                .build()
                .inject(this);
    }

    public RecyclerView getRvResources() {
        return rvResources;
    }

    public CoordinatorLayout getClResources() {
        return clResources;
    }

    public void setBottomSheetListener(BottomSheetListener bottomSheetListener) {
        this.bottomSheetListener = bottomSheetListener;
    }

    public BottomSheetBehavior getmBottomSheetBehavior() {
        return mBottomSheetBehavior;
    }

    public int getBottomHeight() {
        return mBottomLl.getHeight();
    }

    public void setCanSlidesUp(boolean canSlidesUp) {
        this.canSlidesUp = canSlidesUp;
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DELAY_INIT_JPUSH:
                    initJpushAlias();
                    break;
            }
        }
    };

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_new_main; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @OnClick({R.id.tv_daily, R.id.tv_workbench, R.id.tv_mine})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_daily:
                switchFragment(0);
                break;
            case R.id.tv_workbench:
                switchFragment(1);
                break;
            case R.id.tv_mine:
                switchFragment(2);
                break;
        }
    }

    private void switchFragment(int position) {
        showHideFragment(mFragments[position], mFragments[prePosition]);
        prePosition = position;
        switch (position) {
            case 0:
                tvDaily.setSelected(true);
                tvWorkbench.setSelected(false);
                tvMine.setSelected(false);
                break;
            case 1:
                tvDaily.setSelected(false);
                tvWorkbench.setSelected(true);
                tvMine.setSelected(false);
                break;
            case 2:
                tvDaily.setSelected(false);
                tvWorkbench.setSelected(false);
                tvMine.setSelected(true);
                break;
        }
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        boolean isSetAlisSuccess = SPUtils.getInstance().getBoolean(Constants.AFTER_LOGIN_SET_JPUSH_ALIAS_SUCCESS, false);
        if (!isSetAlisSuccess) {
            mHandler.sendEmptyMessageDelayed(DELAY_INIT_JPUSH, 10000);
        }
        initFragment();
        switchFragment(0);
        initBottomSheet();
        checkPerm();
        Timber.e("id: " + JPushInterface.getRegistrationID(NewMainActivity.this));
        if (TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.CONFIG_OPERATION_ID))
                || TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.CONFIG_CASE_ID))
                || TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.CONFIG_CLUE_ID))) {
            // 如果发现运营中心的信息不完全，则再查询一次
//            mCyqzConfigPresenter.getCyqzConfig();
        }

//        mPresenter.getCyqzLyToken();
        mPresenter.getAllCameras();
        mPresenter.getCollectionList(new CollectionListRequest(null, 3000, 1));
        if (getIntent().getBooleanExtra("fromLogin", false) || DataSupport.findAll(CarBrandBean.class).isEmpty()) {
            mPresenter.getCarBrandList();
        }
    }


    /**
     * 检测基本权限.
     */
    private void checkPerm() {
        // 6.0及以上检测权限
        if (DeviceUtil.getSDKVersion() >= Build.VERSION_CODES.M) {
            PermissionUtils.requestPermission(this);
        }
    }

    /**
     * 初始化极光推送的Alias.
     */
    private void initJpushAlias() {
        if (!JPushInterface.isPushStopped(Utils.getContext())) {
            String uid = SPUtils.getInstance().getString(CommonConstant.UID);
            String loginName = SPUtils.getInstance().getString(Constants.CONFIG_LAST_USER_LOGIN_NAME, "");
            String alias = uid + "_" + loginName;
            if (!TextUtils.isEmpty(uid) && !TextUtils.isEmpty(loginName)) {
                // JPushUtils.resumeJPush();
                JPushUtils.setAlias(alias);
            }
        }

    }

    private void initFragment() {
        Rect r = new Rect();
        findViewById(android.R.id.content).getWindowVisibleDisplayFrame(r);
        SPUtils.getInstance().put("mainActivityHeight", r.bottom);


        DailyFragment firstFragment = findFragment(DailyFragment.class);


        if (firstFragment == null) {
            mFragments[0] = DailyFragment.newInstance();
            mFragments[1] = WorkbenchFragment.newInstance();
            mFragments[2] = MineFragment.newInstance();

            loadMultipleRootFragment(R.id.fl_container, 0, mFragments[0], mFragments[1], mFragments[2]);
        } else {
            // 这里库已经做了Fragment恢复,所有不需要额外的处理了, 不会出现重叠问题
            // 这里我们需要拿到mFragments的引用
            mFragments[0] = firstFragment;
            mFragments[1] = findFragment(WorkbenchFragment.class);
            mFragments[2] = findFragment(MineFragment.class);
        }
    }

    private void initBottomSheet() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) clResources.getLayoutParams();
        layoutParams.setMargins(0, titleHeight, 0, 0);

        mBottomSheetBehavior = BottomSheetBehavior.from(llSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (bottomSheetListener != null) {
                    bottomSheetListener.onStateChanged(bottomSheet, newState);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (bottomSheetListener != null) {
                    bottomSheetListener.onSlide(bottomSheet, slideOffset);
                }
                if (canSlidesUp) {
                    float point = 1.0f - (titleHeight * 1.0f / (SizeUtils.getScreenHeight() - titleHeight - mBottomSheetBehavior.getPeekHeight()));
                    if (slideOffset >= point) {
                        float percent = -1 - (point - slideOffset) * 1.0f / (1 - point);
//                        llTitle.setTranslationY((int) (percent * titleHeight));
                    } else {
//                        llTitle.setTranslationY(-titleHeight);
                    }
                }
            }
        });
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

    @Override
    public void showUpdateDialog(boolean isForce, String versionDescription, String downloadUrl) {
        mUpdateDialog = ViewUtils.showUpgradeDialog(isForce, versionDescription, NewMainActivity.this,
                v -> {
                    mDownloadUrl = downloadUrl;
                    mIsForceUpdate = isForce;
                    if (!EasyPermissions.hasPermissions(Utils.getContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        EasyPermissions.requestPermissions(Utils.getContext(),
                                getString(R.string.need_read_sdcard_permission_tips),
                                PermissionUtils.RC_WRITE_SDCARD_PERM,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    } else {
                        downloadApk();
                    }

                }, v -> {
                    if (mUpdateDialog != null && mUpdateDialog.isShowing()) {
                        mUpdateDialog.dismiss();
                        mUpdateDialog = null;
                    }
                });
    }

    @Override
    public void next() {

    }

    @AfterPermissionGranted(PermissionUtils.RC_WRITE_SDCARD_PERM)
    private void downloadApk() {
        AppUtils.gotoDownloadApk(mDownloadUrl);
        mDownloadUrl = null;
        if (!mIsForceUpdate) {
            if (mUpdateDialog != null && mUpdateDialog.isShowing()) {
                mUpdateDialog.dismiss();
                mUpdateDialog = null;
            }
        } else {
//            finish();
//            System.exit(0);
        }
    }

    @Override
    public void onGetCyqzConfigSuccess(OperationEntity entity) {

    }

    @Override
    public void onGetCyqzConfigError() {

    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        ToastUtils.showShort("onPermissionsGranted");
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            if (perms.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                final PermissionDialog dialog = new PermissionDialog(this);
                dialog.setTitle(getString(R.string.request_permission_tag));
                dialog.setMessage(getString(R.string.need_read_sdcard_permission_tips));
                dialog.setNegativeListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(DELAY_INIT_JPUSH);
    }

    @Override
    public void onGetCyqzLyTokenSuccess() {

    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void getAllCamerasSuccess(OrgCameraParentEntity entity) {
        if (null != entity) {
            List<OrgCameraEntity> resultList = entity.getResultList();
            if (null != resultList && !resultList.isEmpty()) {
                DataSupport.deleteAll(OrgCameraEntity.class);
                DataSupport.saveAll(resultList);
            }
        }
        if (CloudMapFragment.getInstance() != null && CloudMapFragment.getInstance().mHandler != null)
            CloudMapFragment.getInstance().mHandler.sendEmptyMessage(MESSAGE_LOAD_CAMERA_DONE);
    }

    @Override
    public void getCollectionListSuccess(List<CollectionListBean> list) {
        if (null != list && !list.isEmpty()) {
            CollectedPictureHolder.urls.clear();
            for (CollectionListBean bean : list) {
                CollectedPictureHolder.urls.add(bean.getFaceImgUrl());
            }
        }
    }

    @Override
    public void onGetCarBrandList(CarInfo carInfo) {
        DataSupport.deleteAll(CarBrandBean.class);
        DataSupport.saveAll(carInfo.vehicleInfo.subList(2, carInfo.vehicleInfo.size()));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
                CloudMapFragment.getInstance().dismissRecyclerView();
                return false;
            }
            if (DailyPoliceFragment.getInstance().isShaixuanVisible()) {
                setBottomVisible(true);
                DailyPoliceFragment.getInstance().setShaixuanGone();
                return false;
            }

            if (mFirstBack) {
                ToastUtils.showShort(R.string.drop_out);
                mFirstBack = false;
                Timer timer = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        mFirstBack = true;
                    }
                };
                timer.schedule(task, 2000);
            } else if (!isFinishing()) {
                SaveUtils.clearPermissions();
                MobclickAgent.onKillProcess(this);
                finish();
                System.exit(0);
            }
        }
        return false;
    }

    public interface BottomSheetListener {
        void onStateChanged(View bottomSheet, int newState);

        void onSlide(View bottomSheet, float slideOffset);
    }

    public void setBottomVisible(boolean isVisible) {
        int visible = isVisible ? View.VISIBLE : View.GONE;
        mBottomLl.setVisibility(visible);
    }
}
