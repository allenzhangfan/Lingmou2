package cloud.antelope.lingmou.mvp.ui.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.utils.AppUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.lingdanet.safeguard.common.utils.Utils;

import butterknife.BindView;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.PermissionUtils;
import cloud.antelope.lingmou.app.utils.ViewUtils;
import cloud.antelope.lingmou.di.component.DaggerAboutComponent;
import cloud.antelope.lingmou.di.module.AppUpdateModule;
import cloud.antelope.lingmou.mvp.contract.AppUpdateContract;
import cloud.antelope.lingmou.mvp.presenter.AppUpdatePresenter;
import pub.devrel.easypermissions.EasyPermissions;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class AboutActivity extends BaseActivity<AppUpdatePresenter> implements AppUpdateContract.View {

    @BindView(R.id.app_version_tv)
    TextView mAppVersionTv;

    long[] mHits = new long[3];
    @BindView(R.id.about_img)
    ImageView mAboutImg;
    @BindView(R.id.call_service_btn)
    Button mCallServiceBtn;
    @BindView(R.id.law_btn)
    Button mLawBtn;
    @BindView(R.id.version_update_btn)
    Button mVersionUpdateBtn;
    @BindView(R.id.head_left_iv)
    ImageButton mHeadLeftIv;
    @BindView(R.id.title_tv)
    TextView mTitleTv;

    private Dialog mUpdateDialog;
    private String mDownloadUrl;
    private boolean mIsForceUpdate;

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        DaggerAboutComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .appUpdateModule(new AppUpdateModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_about; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mAppVersionTv.setText("V" + AppUtils.getAppVersionName());
        mTitleTv.setText(R.string.about_text);
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

    @OnClick({R.id.about_img, R.id.call_service_btn, R.id.law_btn, R.id.version_update_btn, R.id.head_left_iv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.head_left_iv:
                finish();
                break;
            case R.id.about_img:
                break;
            case R.id.call_service_btn:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + "027-87521052"));
                startActivity(intent);
                break;
            case R.id.law_btn:
                break;
            case R.id.version_update_btn:
                ToastUtils.showShort(R.string.hint_check_updating);
                mPresenter.checkUpdate(true);
                break;
        }
    }

    @Override
    public void showUpdateDialog(boolean isForce, String versionDescription, String downloadUrl) {
        mUpdateDialog = ViewUtils.showUpgradeDialog(isForce, versionDescription, Utils.getContext(),
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


    private void downloadApk() {
        if (!mIsForceUpdate) {
            if (mUpdateDialog != null && mUpdateDialog.isShowing()) {
                mUpdateDialog.dismiss();
                mUpdateDialog = null;
            }
        }
        AppUtils.gotoDownloadApk(mDownloadUrl);
        mDownloadUrl = null;
    }
    @Override
    public void next() {

    }
}
