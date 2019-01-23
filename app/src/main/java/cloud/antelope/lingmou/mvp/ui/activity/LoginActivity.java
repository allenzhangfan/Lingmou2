package cloud.antelope.lingmou.mvp.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.https.OkHttpUtils;
import com.lingdanet.safeguard.common.modle.LoadingDialog;
import com.lingdanet.safeguard.common.modle.PermissionDialog;
import com.lingdanet.safeguard.common.modle.SweetDialog;
import com.lingdanet.safeguard.common.utils.DeviceUtil;
import com.lingdanet.safeguard.common.utils.KeyboardUtils;
import com.lingdanet.safeguard.common.utils.NetworkUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.lingdanet.safeguard.common.utils.Utils;

import org.simple.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.PermissionUtils;
import cloud.antelope.lingmou.app.utils.SaveUtils;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.common.UrlConstant;
import cloud.antelope.lingmou.di.component.DaggerLoginComponent;
import cloud.antelope.lingmou.di.module.LoginModule;
import cloud.antelope.lingmou.mvp.contract.LoginContract;
import cloud.antelope.lingmou.mvp.model.entity.CountDownTime;
import cloud.antelope.lingmou.mvp.model.entity.UrlEntity;
import cloud.antelope.lingmou.mvp.presenter.LoginPresenter;
import cloud.antelope.lingmou.mvp.ui.widget.DashView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class LoginActivity extends BaseActivity<LoginPresenter>
        implements LoginContract.View, EasyPermissions.PermissionCallbacks {
    private static final int RESULT = 0x10;
    public static final String COUNTDOWN = "countdown";


    @Inject
    LoadingDialog mLoadingDialog;
    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.til_account)
    TextInputLayout tilAccount;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.til_psw)
    TextInputLayout tilPsw;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.tv_agreement)
    TextView tvAgreement;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ib_location)
    ImageView ibLocation;
    @BindView(R.id.tv_account_error)
    TextView tvAccountError;
    @BindView(R.id.tv_password_error)
    TextView tvPasswordError;
    @BindView(R.id.dv_account)
    DashView dvAccount;
    @BindView(R.id.dv_password)
    DashView dvPassword;

    private ArrayList<UrlEntity> mUrlEntityList;
    private UrlEntity mSelectUrlEntity;
    private int selectedIndex;
    public MyCountDownTimer countDownTimer;
    private long millisUntilFinished = 59000L;
    private String deviceId;
    private static LoginActivity instance;
    private String phoneNumber;

    public static LoginActivity getInstance() {
        return instance;
    }

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        DaggerLoginComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .loginModule(new LoginModule(this))
                .build()
                .inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SaveUtils.isInLogin = true;
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_login_1; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        instance = this;
        mUrlEntityList = new ArrayList<>();
        initErrorHint();
        // mPresenter.getBaseUrls();
        getLocalAddress(false);
        getPermission();
        if (getIntent() != null) {
            boolean showLogoutTip = getIntent().getBooleanExtra("showLogoutTip", false);
            if (showLogoutTip) {
                SweetDialog mSweetDialog=new SweetDialog(LoginActivity.this);
                mSweetDialog.setTitle(getString(R.string.logout_tip));
                mSweetDialog.setPositiveListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSweetDialog.dismiss();
                    }
                });
                mSweetDialog.setNegativeListener(null);
                mSweetDialog.setNegativeGone();
                mSweetDialog.show();
            }
        }
    }

    private void getPermission(){
        if (DeviceUtil.getSDKVersion() >= Build.VERSION_CODES.M) {
            if (!EasyPermissions.hasPermissions(Utils.getContext(),
                    Manifest.permission.READ_PHONE_STATE)) {
                EasyPermissions.requestPermissions(LoginActivity.this,
                        getString(R.string.need_permission_tips),
                        PermissionUtils.RC_PHONE_STATE_PERM,
                        Manifest.permission.READ_PHONE_STATE);
            } else {
                getDeviceId();
            }
        } else {
            getDeviceId();
        }
    }
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @AfterPermissionGranted(PermissionUtils.RC_PHONE_STATE_PERM)
    private void getDeviceId() {
        try {
            final TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (manager.getDeviceId() == null || manager.getDeviceId().equals("")) {
                if (Build.VERSION.SDK_INT >= 23) {
                    deviceId = manager.getDeviceId(0);
                }
            } else {
                deviceId = manager.getDeviceId();
            }
            SPUtils.getInstance().put(Constants.DEVICE_IMEI,deviceId);
        } catch (Exception e) {
            e.printStackTrace();
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

    private void initErrorHint() {
        etAccount.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                tvAccountError.setVisibility(View.GONE);
                dvAccount.start();
            } else {
                dvAccount.reset();
            }
        });
        etAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    tvAccountError.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etPassword.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                tvPasswordError.setVisibility(View.GONE);
                dvPassword.start();
            } else {
                dvPassword.reset();
            }

        });
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvPasswordError.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @OnClick({R.id.btn_login, R.id.ib_location})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                login(true);
                break;
            case R.id.ib_location:
                goSelectLocation();
                break;
        }

    }

    private void login(boolean showErroe) {
        String accountStr = etAccount.getText().toString().trim();
        String pwdStr = etPassword.getText().toString().trim();
        if (TextUtils.isEmpty(accountStr)) {
            if (!showErroe) return;
            etAccount.requestFocus();
            tvAccountError.setVisibility(View.VISIBLE);
            tvAccountError.setText(getString(R.string.plz_input_account_text));
            KeyboardUtils.showSoftInput(etAccount);
            return;
        } else {
            tvAccountError.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(pwdStr)) {
            if (!showErroe) return;
            etPassword.requestFocus();
            tvPasswordError.setVisibility(View.VISIBLE);
            tvPasswordError.setText(R.string.plz_input_pwd_error);
            KeyboardUtils.showSoftInput(etPassword);
            return;
        } else {
            tvPasswordError.setVisibility(View.GONE);
        }
        if (null == mSelectUrlEntity) {
            if (!showErroe) return;
//                    ToastUtils.showShort(R.string.hint_choice_local_address);
            KeyboardUtils.hideSoftInput(LoginActivity.this);
            goSelectLocation();
            return;
        }
        if(TextUtils.isEmpty(deviceId)){
            getPermission();
        }
        SPUtils.getInstance().put(Constants.URL_BASE, mSelectUrlEntity.getServerUrl());
//        SPUtils.getInstance().put(Constants.URL_BASE, "http://192.168.14.46:9091");
        SPUtils.getInstance().put(Constants.URL_EVENT_STORAGE, mSelectUrlEntity.getEventStorageUrl());
        SPUtils.getInstance().put(Constants.URL_OBJECT_STORAGE, mSelectUrlEntity.getObjectStorageUrl());
        SPUtils.getInstance().put(Constants.URL_SOLDIER_LIVE, mSelectUrlEntity.getSoldierLiveUrl());
        SPUtils.getInstance().put(Constants.URL_RECORD, mSelectUrlEntity.getRecordUrl());
        SPUtils.getInstance().put(Constants.URL_RECORD_PLAY, mSelectUrlEntity.getRecordPlayUrl());
        SPUtils.getInstance().put(Constants.URL_PLATFORM_NAME, mSelectUrlEntity.getPlatformName());
        mPresenter.login(accountStr, pwdStr, deviceId, Constants.REGISTRATION_ID);
    }

    public void goSelectLocation() {
        if (!mUrlEntityList.isEmpty()) {
            Intent intent = new Intent(this, LocationSelectActivity.class);
            intent.putParcelableArrayListExtra("UrlEntityList", mUrlEntityList);
            intent.putExtra("selectedIndex", selectedIndex);
            startActivityForResult(intent, RESULT);
        } else {
            getLocalAddress(true);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT && resultCode == RESULT_OK && data != null) {
            mSelectUrlEntity = data.getParcelableExtra("SelectUrlEntity");
            selectedIndex = data.getIntExtra("selectedIndex", -1);
            if (mSelectUrlEntity != null) {
                ibLocation.setImageResource(R.drawable.selected_location);
                SPUtils.getInstance().put("SelectUrlEntity",new Gson().toJson(mSelectUrlEntity));
            }
            login(false);
        }
    }


    private void getLocalAddress(boolean show) {
        if (show) showLoading("");
        if (!NetworkUtils.isConnected()) {
            showMessage(Utils.getContext().getResources().getString(R.string.network_disconnect));
            hideLoading();
            return;
        }

        Request request = new Request.Builder().url(UrlConstant.SERVER_BASE_URL).build();
        OkHttpUtils.getInstance().getOkHttpClient().newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        hideLoading();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        hideLoading();
                        ResponseBody responseBody = response.body();
                        String body = responseBody.string();
                        if (!TextUtils.isEmpty(body)) {
                            Gson gson = ArmsUtils.obtainAppComponentFromContext(getApplication()).gson();
                            List<UrlEntity> entities = null;
                            try {
                                entities = gson.fromJson(body, new TypeToken<List<UrlEntity>>() {
                                }.getType());
                            } catch (JsonSyntaxException e) {
                                hideLoading();
                            }
                            mUrlEntityList.clear();
                            List<UrlEntity> finalEntities = entities;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (null != finalEntities) {
                                        mUrlEntityList.addAll(finalEntities);
                                        int size = finalEntities.size();
                                        String urlBase = SPUtils.getInstance().getString(Constants.URL_BASE);
                                        selectedIndex = -1;
                                        String loacalAddress = "";
                                        for (int i = 0; i < size; i++) {
                                            UrlEntity entity = finalEntities.get(i);
                                            if (urlBase.equals(entity.getServerUrl())) {
                                                selectedIndex = i;
                                                loacalAddress = entity.getPlatformName();
                                                mSelectUrlEntity = entity;
                                                ibLocation.setImageResource(R.drawable.selected_location);
                                                break;
                                            }
                                        }
                                        if (show) {
                                            goSelectLocation();
                                        }
                                    }
                                }
                            });

                        }
                    }
                });
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
        ToastUtils.showShort(message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {
        finish();
        System.exit(0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        KeyboardUtils.hideSoftInput(this);
    }

    @Override
    protected void onDestroy() {
        SaveUtils.isInLogin = false;
        super.onDestroy();
    }


    @Override
    public void gotoMainActivity() {
        // JPushUtils.switchJpush();
        //进入到首页
        Intent intent = new Intent(this, NewMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void getUrlsSuccess(List<UrlEntity> entities) {
        mUrlEntityList.clear();
        if (null != entities) {
            mUrlEntityList.addAll(entities);
            int size = entities.size();
            String urlBase = SPUtils.getInstance().getString(Constants.URL_BASE);
            int index = -1;
            String loacalAddress = "";
            for (int i = 0; i < size; i++) {
                UrlEntity entity = entities.get(i);
                if (urlBase.equals(entity.getServerUrl())) {
                    index = i;
                    loacalAddress = entity.getPlatformName();
                    mSelectUrlEntity = entity;
                    ibLocation.setImageResource(R.drawable.selected_location);
                    break;
                }
            }
        }
    }

    @Override
    public void onSendMessageCodeSuccess(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        goMessageCodeActivity();
    }

    private void goMessageCodeActivity() {
        if (countDownTimer == null) {
            countDownTimer = new MyCountDownTimer(60000, 1000);
        }
        if (!countDownTimer.isRunning) {
            countDownTimer.start();
        }
        String accountStr = etAccount.getText().toString().trim();
        String pwdStr = etPassword.getText().toString().trim();

        Intent intent = new Intent(this, MessageCodeActivityActivity.class);
        intent.putExtra("loginName", accountStr);
        intent.putExtra("password", pwdStr);
        intent.putExtra("phoneNumber", phoneNumber);
        intent.putExtra("millisUntilFinished", millisUntilFinished);
        intent.putExtra("deviceId", deviceId);
        startActivity(intent);
    }

    public static class MyCountDownTimer extends CountDownTimer {
        public boolean isRunning;

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            LoginActivity.getInstance().millisUntilFinished = millisUntilFinished;
            isRunning = true;
            EventBus.getDefault().post(new CountDownTime(millisUntilFinished), COUNTDOWN);
        }

        @Override
        public void onFinish() {
            isRunning = false;
            EventBus.getDefault().post(new CountDownTime(0), COUNTDOWN);
        }
    }
}
