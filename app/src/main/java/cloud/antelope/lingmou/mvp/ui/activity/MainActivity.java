package cloud.antelope.lingmou.mvp.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
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
import com.lingdanet.safeguard.common.utils.LogUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.SizeUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.lingdanet.safeguard.common.utils.Utils;
import com.umeng.analytics.MobclickAgent;

import org.litepal.crud.DataSupport;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.BarViewUtils;
import cloud.antelope.lingmou.app.utils.JPushUtils;
import cloud.antelope.lingmou.app.utils.PermissionUtils;
import cloud.antelope.lingmou.app.utils.SaveUtils;
import cloud.antelope.lingmou.app.utils.ViewUtils;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.di.component.DaggerMainComponent;
import cloud.antelope.lingmou.di.module.AppUpdateModule;
import cloud.antelope.lingmou.di.module.CyqzConfigModule;
import cloud.antelope.lingmou.di.module.MainModule;
import cloud.antelope.lingmou.mvp.contract.AppUpdateContract;
import cloud.antelope.lingmou.mvp.contract.CyqzConfigContract;
import cloud.antelope.lingmou.mvp.contract.MainContract;
import cloud.antelope.lingmou.mvp.model.entity.OperationEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraParentEntity;
import cloud.antelope.lingmou.mvp.presenter.AppUpdatePresenter;
import cloud.antelope.lingmou.mvp.presenter.CyqzConfigPresenter;
import cloud.antelope.lingmou.mvp.presenter.MainPresenter;
import cloud.antelope.lingmou.mvp.ui.adapter.MainPagerAdapter;
import cloud.antelope.lingmou.mvp.ui.fragment.CloudMapFragment;
import cloud.antelope.lingmou.mvp.ui.fragment.DailyPoliceFragment;
import cn.jpush.android.api.JPushInterface;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class MainActivity extends BaseActivity<MainPresenter>
        implements MainContract.View,
        CyqzConfigContract.View, AppUpdateContract.View,
        EasyPermissions.PermissionCallbacks {


    private static final int DELAY_INIT_JPUSH = 0x01;

    @BindView(R.id.home_content_ll)
    LinearLayout mHomeLayout;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.main_content)
    RelativeLayout mMainContent;

    @BindView(R.id.home_video_tv)
    TextView mHomeVideoTV;
    @BindView(R.id.home_map_tv)
    TextView mHomeMapTV;
    @BindView(R.id.home_app_tv)
    TextView mHomeAppTV;
    @BindView(R.id.home_person_tv)
    TextView mHomePersonalTV;
    @BindView(R.id.bottom_ll)
    LinearLayout mBottomLl;
    // @BindView(R.id.home_rg)
    // RadioGroup mHomeRg;
    @BindView(R.id.rv_resources)
    RecyclerView rvResources;
    @BindView(R.id.ll_title)
    LinearLayout llTitle;
    @BindView(R.id.title_tv)
    TextView tvTitle;
    @BindView(R.id.include_resources)
    CoordinatorLayout clResources;
    @BindView(R.id.head_left_iv)
    ImageView ivLeft;
    @BindView(R.id.view_bar)
    View viewBar;
    BottomSheetBehavior mBottomSheetBehavior;
    @Inject
    CyqzConfigPresenter mCyqzConfigPresenter;
    @Inject
    AppUpdatePresenter mAppUpdatePresenter;
    @Inject
    MainPagerAdapter mAdapter;
    // @Inject
    // GisUtils mGisUtils;
    private BottomSheetListener bottomSheetListener;
    private int titleHeight;
    private boolean canSlidesUp;

    public void setCanSlidesUp(boolean canSlidesUp) {
        this.canSlidesUp = canSlidesUp;
    }

    public void setBottomSheetListener(BottomSheetListener bottomSheetListener) {
        this.bottomSheetListener = bottomSheetListener;
    }

    public RecyclerView getRvResources() {
        return rvResources;
    }

    public CoordinatorLayout getClResources() {
        return clResources;
    }

    public BottomSheetBehavior getmBottomSheetBehavior() {
        return mBottomSheetBehavior;
    }

    private int mCurrentPage = 1;

    private boolean mFirstBack = true;

    private Dialog mUpdateDialog;
    private String mDownloadUrl;
    private boolean mIsForceUpdate;

    private TextView mCurrentSelectTv;

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
    public void setupActivityComponent(AppComponent appComponent) {
        DaggerMainComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .mainModule(new MainModule(this, getSupportFragmentManager()))
                .appUpdateModule(new AppUpdateModule(this))
                .cyqzConfigModule(new CyqzConfigModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_home; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        tvTitle.setText("视频资源");
        boolean isSetAlisSuccess = SPUtils.getInstance().getBoolean(Constants.AFTER_LOGIN_SET_JPUSH_ALIAS_SUCCESS, false);
        if (!isSetAlisSuccess) {
            mHandler.sendEmptyMessageDelayed(DELAY_INIT_JPUSH, 10000);
        }
        // mHomeRg.setOnCheckedChangeListener(this);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(3);
        showHomeFragment();
        initBottomSheet();
        checkPerm();
        if (TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.CONFIG_OPERATION_ID))
                || TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.CONFIG_CASE_ID))
                || TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.CONFIG_CLUE_ID))) {
            // 如果发现运营中心的信息不完全，则再查询一次
            // mCyqzConfigPresenter.getCyqzConfig();
        }

        // mPresenter.getCyqzLyToken();
        mPresenter.getAllCameras();
        // mGisUtils.setLocateListener(this);
        // mGisUtils.start();
    }

    private void initBottomSheet() {
        ivLeft.setVisibility(View.GONE);
        TypedArray actionbarSizeTypedArray = obtainStyledAttributes(new int[]{
                android.R.attr.actionBarSize
        });
        titleHeight = (int) (actionbarSizeTypedArray.getDimension(0, 0) + BarViewUtils.getStatusBarHeight());
        LinearLayout.LayoutParams barLP = (LinearLayout.LayoutParams) viewBar.getLayoutParams();
        barLP.height = BarViewUtils.getStatusBarHeight();
        viewBar.setLayoutParams(barLP);

        llTitle.setTranslationY(-titleHeight);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) clResources.getLayoutParams();
        layoutParams.setMargins(0, titleHeight, 0, 0);

        mBottomSheetBehavior = BottomSheetBehavior.from(rvResources);
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
                        llTitle.setTranslationY((int) (percent * titleHeight));
                    } else {
                        llTitle.setTranslationY(-titleHeight);
                    }
                }
            }
        });
    }

    public int getBottomHeight() {
        return mBottomLl.getHeight();
    }

    private void showHomeFragment() {
        if (mHomeVideoTV == mCurrentSelectTv) {
            return;
        }
        if (null != mCurrentSelectTv) {
            mCurrentSelectTv.setSelected(false);
        }
        mHomeVideoTV.setSelected(true);
        mCurrentSelectTv = mHomeVideoTV;
        mViewPager.setCurrentItem(0);
        mTintManager.setStatusBarTintResource(R.color.bg_app);
    }

    private void showMapFragment() {
        if (mHomeMapTV == mCurrentSelectTv) {
            return;
        }
        mCurrentSelectTv.setSelected(false);
        mHomeMapTV.setSelected(true);
        mCurrentSelectTv = mHomeMapTV;
        mViewPager.setCurrentItem(1);
        mTintManager.setStatusBarTintResource(R.color.transparent);
    }

    private void showAppFragment() {
        if (mHomeAppTV == mCurrentSelectTv) {
            return;
        }
        mCurrentSelectTv.setSelected(false);
        mHomeAppTV.setSelected(true);
        mCurrentSelectTv = mHomeAppTV;
        mViewPager.setCurrentItem(2);
        mTintManager.setStatusBarTintResource(R.color.white);
    }

    private void showPersonFragment() {
        if (mHomePersonalTV == mCurrentSelectTv) {
            return;
        }
        mCurrentSelectTv.setSelected(false);
        mHomePersonalTV.setSelected(true);
        mCurrentSelectTv = mHomePersonalTV;
        mViewPager.setCurrentItem(3);
        mTintManager.setStatusBarTintResource(R.color.white);
    }

    @Override
    public void onGetCyqzLyTokenSuccess() {
        if (SPUtils.getInstance().getBoolean(Constants.HAS_LOGIN)) {
            // mPresenter.isReceiveMessage();
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


    public Activity getActivity() {
        return this;
    }

    @Override
    public void getAllCamerasSuccess(OrgCameraParentEntity entity) {
        if (null != entity) {
            List<OrgCameraEntity> resultList = entity.getResultList();
            LogUtils.i("cxm", "AllCamera ---- " + Thread.currentThread().getName());
            if (null != resultList && !resultList.isEmpty()) {
                DataSupport.deleteAll(OrgCameraEntity.class);
                DataSupport.saveAll(resultList);
            }
        }
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

    @OnClick({R.id.home_video_tv, R.id.home_map_tv, R.id.home_app_tv, R.id.home_person_tv})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.home_video_tv:
                showHomeFragment();
                break;
            case R.id.home_map_tv:
                showMapFragment();
                break;
            case R.id.home_app_tv:
                showAppFragment();
                break;
            case R.id.home_person_tv:
                showPersonFragment();
                break;
        }
    }

    /*

     */
    /**
     * 显示应用界面
     */    /*

    @Override
    public void showAppItem() {
        mCurrentPage = 1;
        mViewPager.setCurrentItem(mCurrentPage);
    }

        */
    /**
     * 显示我的页面.
     */    /*

    private void showMyItem() {
        //        mTintManager.setStatusBarTintResource(R.drawable.my_status_bar_48);
        mCurrentPage = 2;
        mViewPager.setCurrentItem(mCurrentPage);
    }

        */

    /**
     * 显示主页警务列表信息.
     */    /*

    private void showHomeItem() {
        //        mTintManager.setStatusBarTintResource(R.drawable.bg_home_title);
        mCurrentPage = 0;
        mViewPager.setCurrentItem(mCurrentPage);
    }
    */
    @Override
    public void onGetCyqzConfigSuccess(OperationEntity entity) {

    }

    @Override
    public void onGetCyqzConfigError() {

    }

    @Override
    public void showUpdateDialog(boolean isForce, String versionDescription, String downloadUrl) {
        mUpdateDialog = ViewUtils.showUpgradeDialog(isForce, versionDescription, MainActivity.this,
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

   /* public void onLocateStart() {

    }

    @Override
    public void onLocateOK(AMapLocation location) {
        String soloCid = SPUtils.getInstance().getString(Constants.CONFIG_SOLO_CID, "");
        if (!TextUtils.isEmpty(soloCid) && null != location) {
            double[] latLan = GPSUtils.gcj02_To_Gps84(location.getLatitude(), location.getLongitude());
            //上传地理位置
            mPresenter.uploadSolosLocations(soloCid, latLan[0], latLan[1]);
        }
    }

    @Override
    public void onLocateFail(int errCode) {

    }*/

    public interface HomeBackPressInterface {
        boolean backPre(boolean isVisible);
    }

    public HomeBackPressInterface mBackInterface;

    public void setHomeBackLisntener(HomeBackPressInterface backInterface) {
        mBackInterface = backInterface;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
//                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                CloudMapFragment.getInstance().dismissRecyclerView();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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
        // if (mGisUtils != null) {
        //     mGisUtils.destory();
        //     mGisUtils = null;
        // }
        mHandler.removeMessages(DELAY_INIT_JPUSH);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //        super.onSaveInstanceState(outState);
    }

    public interface BottomSheetListener {
        void onStateChanged(View bottomSheet, int newState);

        void onSlide(View bottomSheet, float slideOffset);
    }
}
