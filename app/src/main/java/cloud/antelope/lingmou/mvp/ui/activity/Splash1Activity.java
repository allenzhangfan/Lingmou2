package cloud.antelope.lingmou.mvp.ui.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.constant.CommonConstant;
import com.lingdanet.safeguard.common.https.OkHttpUtils;
import com.lingdanet.safeguard.common.modle.PermissionDialog;
import com.lingdanet.safeguard.common.utils.AppUtils;
import com.lingdanet.safeguard.common.utils.LogUtils;
import com.lingdanet.safeguard.common.utils.NetworkUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.LogUploadUtil;
import cloud.antelope.lingmou.app.utils.PermissionUtils;
import cloud.antelope.lingmou.app.utils.ViewUtils;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.common.UrlConstant;
import cloud.antelope.lingmou.di.component.DaggerSplash1Component;
import cloud.antelope.lingmou.di.module.AppUpdateModule;
import cloud.antelope.lingmou.di.module.Splash1Module;
import cloud.antelope.lingmou.mvp.contract.AppUpdateContract;
import cloud.antelope.lingmou.mvp.contract.Splash1Contract;
import cloud.antelope.lingmou.mvp.model.entity.AppBean;
import cloud.antelope.lingmou.mvp.model.entity.LogUploadRequest;
import cloud.antelope.lingmou.mvp.model.entity.SoldierInfoEntity;
import cloud.antelope.lingmou.mvp.model.entity.UpdateLastLoginTimeRequest;
import cloud.antelope.lingmou.mvp.model.entity.UrlEntity;
import cloud.antelope.lingmou.mvp.presenter.AppUpdatePresenter;
import cloud.antelope.lingmou.mvp.presenter.Splash1Presenter;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


import static com.jess.arms.utils.Preconditions.checkNotNull;
import static com.umeng.analytics.AnalyticsConfig.getLocation;


public class Splash1Activity extends BaseActivity<Splash1Presenter> implements Splash1Contract.View, AppUpdateContract.View,
        EasyPermissions.PermissionCallbacks {
    // 启动图显示延迟
    private static final int APP_START_DELAY_TIME = 1500;
    private long mStartTime;
    private Dialog mUpdateDialog;
    private String mDownloadUrl;
    private boolean mIsForceUpdate;
    @Inject
    AppUpdatePresenter mAppUpdatePresenter;
    private boolean needGotoNextUI;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerSplash1Component //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .splash1Module(new Splash1Module(this))
                .appUpdateModule(new AppUpdateModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.splash_layout; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mStartTime = System.currentTimeMillis();
        if (TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.ZHYY_TOKEN))) {
            gotoNextUI();
        } else {
            mAppUpdatePresenter.checkUpdate(false);
            mPresenter.getUserInfo();
            mPresenter.getSoldierInfo();
            mPresenter.updateLastLoginTime(new UpdateLastLoginTimeRequest(SPUtils.getInstance().getString(Constants.DEVICE_IMEI)));
        }
    }

    private void getLocalAddress() {
        Request request = new Request.Builder().url(UrlConstant.SERVER_BASE_URL).build();
        OkHttpUtils.getInstance().getOkHttpClient().newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Intent intent = new Intent();
                        intent.setClass(Splash1Activity.this, NewMainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        ResponseBody responseBody = response.body();
                        String body = responseBody.string();
                        if (!TextUtils.isEmpty(body)) {
                            Gson gson = ArmsUtils.obtainAppComponentFromContext(getApplication()).gson();
                            List<UrlEntity> entities = null;
                            try {
                                entities = gson.fromJson(body, new TypeToken<List<UrlEntity>>() {
                                }.getType());
                            } catch (JsonSyntaxException e) {
                            }
                            List<UrlEntity> finalEntities = entities;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (null != finalEntities) {
                                        String name = SPUtils.getInstance().getString(Constants.URL_PLATFORM_NAME);
                                        boolean matched = false;
                                        for (int i = 0; i < finalEntities.size(); i++) {
                                            UrlEntity entity = finalEntities.get(i);
                                            if (name.equals(entity.getPlatformName())) {
                                                SPUtils.getInstance().put(Constants.URL_BASE, entity.getServerUrl());
                                                matched = true;
                                                break;
                                            }
                                        }
                                        Intent intent = new Intent();
                                        if (!matched) {
                                            intent.setClass(Splash1Activity.this, LoginActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            intent.setClass(Splash1Activity.this, NewMainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                }
                            });

                        }
                    }
                });
    }

    /**
     * 跳转到下一个页面.
     */
    public void gotoNextUI() {
//        if (isFirstLaunch()) {
//            gotoGuidePage();
//        } else {
        LogUtils.w("cxm", "session = " + SPUtils.getInstance().getString(CommonConstant.SESSION) + ", uid = " + SPUtils.getInstance().getString(CommonConstant.UID));
        if (TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.ZHYY_TOKEN))
                || TextUtils.isEmpty(SPUtils.getInstance().getString(CommonConstant.UID))) {
            SPUtils.getInstance().put(Constants.HAS_LOGIN, false);
        }
        LogUploadUtil.uploadLog(new LogUploadRequest(103800, 103801, String.format("【%s】登录系统", SPUtils.getInstance().getString(Constants.CONFIG_LAST_USER_LOGIN_NAME))));
        gotoMainPage();
        //        }
//        }
        SPUtils.getInstance().put(Constants.VERSION_CODE, AppUtils.getAppVersionCode());
    }

    /**
     * 是否是首次启动.
     *
     * @return boolean true:是首次启动客户端，false:非首次启动客户端
     */
    public boolean isFirstLaunch() {
        return SPUtils.getInstance().getBoolean("firstIn" + AppUtils.getAppVersionName(), true);
    }

    /**
     * 跳转到主页面.
     */
    private void gotoMainPage() {
        long endTime = System.currentTimeMillis();
        int interval = (int) (endTime - mStartTime);
        if (interval < APP_START_DELAY_TIME) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startHomeActivity();
                }
            }, APP_START_DELAY_TIME - interval);
        } else {
            startHomeActivity();
        }
    }

    /**
     * 启动HomeActivity.
     */
    private void startHomeActivity() {
        Intent intent = new Intent();
        if (SPUtils.getInstance().getBoolean(Constants.HAS_LOGIN, false)) {
            getLocalAddress();
        } else {
            intent.setClass(Splash1Activity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    /**
     * 跳转到引导页.
     */
    private void gotoGuidePage() {
        long endTime = System.currentTimeMillis();
        int interval = (int) (endTime - mStartTime);
        if (interval < APP_START_DELAY_TIME) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startGuideActivity();
                }
            }, APP_START_DELAY_TIME - interval);
        } else {
            startGuideActivity();
        }
    }

    /**
     * 启动引导页.
     */
    private void startGuideActivity() {
        Intent intent = new Intent(Splash1Activity.this, GuideActivity.class);
        startActivity(intent);
        finish();
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
        mUpdateDialog = ViewUtils.showUpgradeDialog(isForce, versionDescription, Splash1Activity.this,
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
                        gotoNextUI();
                    }
                });
        Constants.SHOW_UPDATE_DIALOG = true;
    }

    @Override
    public void next() {
        gotoNextUI();
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
            needGotoNextUI = true;
        } else {
//            finish();
//            System.exit(0);
        }
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
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (needGotoNextUI) {
            gotoNextUI();
            needGotoNextUI = false;
        }
    }

    @Override
    public void getSoldierInfoSuccess(SoldierInfoEntity entity) {
        if (null != entity) {
            SPUtils.getInstance().put(Constants.PERMISSION_HAS_SOLO, false);
            if (null != entity.getManufacturerDeviceId()) {
                SPUtils.getInstance().put(Constants.CONFIG_SOLO_CID, entity.getManufacturerDeviceId() + "");
                //表示是有单兵
                SPUtils.getInstance().put(Constants.PERMISSION_HAS_SOLO, true);
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
