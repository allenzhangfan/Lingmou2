package cloud.antelope.lingmou.mvp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.modle.LoadingDialog;
import com.lingdanet.safeguard.common.utils.KeyboardUtils;
import com.lingdanet.safeguard.common.utils.PhoneUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;

import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.LogUploadUtil;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.di.component.DaggerMessageCodeActivityComponent;
import cloud.antelope.lingmou.di.module.MessageCodeActivityModule;
import cloud.antelope.lingmou.mvp.contract.MessageCodeActivityContract;
import cloud.antelope.lingmou.mvp.model.entity.CountDownTime;
import cloud.antelope.lingmou.mvp.model.entity.LogUploadRequest;
import cloud.antelope.lingmou.mvp.model.entity.SoldierInfoEntity;
import cloud.antelope.lingmou.mvp.presenter.MessageCodeActivityPresenter;
import cloud.antelope.lingmou.mvp.ui.widget.InterceptableEditText;
import timber.log.Timber;

import static cloud.antelope.lingmou.mvp.ui.activity.LoginActivity.COUNTDOWN;
import static com.jess.arms.utils.Preconditions.checkNotNull;


public class MessageCodeActivityActivity extends BaseActivity<MessageCodeActivityPresenter> implements MessageCodeActivityContract.View {

    @BindView(R.id.head_left_iv)
    ImageButton headLeftIv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.head_right_iv)
    ImageView headRightIv;
    @BindView(R.id.head_right_tv)
    TextView headRightTv;
    @BindView(R.id.et_1)
    InterceptableEditText et1;
    @BindView(R.id.et_2)
    InterceptableEditText et2;
    @BindView(R.id.et_3)
    InterceptableEditText et3;
    @BindView(R.id.et_4)
    InterceptableEditText et4;
    @BindView(R.id.tv_tip)
    TextView tvTip;
    @BindView(R.id.tv_send_code)
    TextView tvSendCode;
    @Inject
    LoadingDialog mLoadingDialog;

    private EditText mFocusedEditText = et1;
    private boolean ready1, ready2, ready3, ready4;
    private String phoneNumber;
    private String loginName;
    private String psd;
    private String deviceId;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerMessageCodeActivityComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .messageCodeActivityModule(new MessageCodeActivityModule(this))
                .build()
                .inject(this);
    }

    @Subscriber(mode = ThreadMode.MAIN, tag = COUNTDOWN)
    public void onTick(CountDownTime countDownTime) {
        if (countDownTime != null) {
            setTime(countDownTime.millisUntilFinished);
        }
    }

    public void setTime(long millisUntilFinished) {
        int s = (int) (millisUntilFinished / 1000);
        if (s >= 1) {
            tvSendCode.setEnabled(false);
            tvSendCode.setText(String.format(getString(R.string.send_msg_code_remain), s));
        } else {
            tvSendCode.setEnabled(true);
            tvSendCode.setText(getString(R.string.send_msg_code_again));
        }
        tvSendCode.setVisibility(View.VISIBLE);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_message_code; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        loginName = getIntent().getStringExtra("loginName");
        psd = getIntent().getStringExtra("password");
        deviceId = getIntent().getStringExtra("deviceId");
        tvTip.setText(String.format(getString(R.string.message_code_tip), PhoneUtils.hidePhoneNum(phoneNumber)));
        long millisUntilFinished = getIntent().getLongExtra("millisUntilFinished", 60000);
        setTime(millisUntilFinished);
        et1.postDelayed(() -> {
            if (et1 != null)
                KeyboardUtils.showSoftInput(et1);
        }, 200);
        et1.addTextChangedListener(new MyTextWatcher(et1));
        et1.setOnKeyListener(new MyOnKeyListener(et1));
        et1.setOnCommitTextListener(new MyOnCommitTextListener(et1));
        et1.setOnFocusChangeListener(new MyOnFocusChangeListener(et1));
        et2.addTextChangedListener(new MyTextWatcher(et2));
        et2.setOnKeyListener(new MyOnKeyListener(et2));
        et2.setOnCommitTextListener(new MyOnCommitTextListener(et2));
        et2.setOnFocusChangeListener(new MyOnFocusChangeListener(et2));
        et3.addTextChangedListener(new MyTextWatcher(et3));
        et3.setOnKeyListener(new MyOnKeyListener(et3));
        et3.setOnCommitTextListener(new MyOnCommitTextListener(et3));
        et3.setOnFocusChangeListener(new MyOnFocusChangeListener(et3));
        et4.addTextChangedListener(new MyTextWatcher(et4));
        et4.setOnKeyListener(new MyOnKeyListener(et4));
        et4.setOnCommitTextListener(new MyOnCommitTextListener(et4));
        et4.setOnFocusChangeListener(new MyOnFocusChangeListener(et4));
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
    protected void onPause() {
        super.onPause();
        KeyboardUtils.hideSoftInput(this);
    }

    @OnClick({R.id.head_left_iv, R.id.tv_send_code})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_left_iv:
                finish();
                break;
            case R.id.tv_send_code:
                mPresenter.sendCode(loginName, phoneNumber);
                break;
        }
    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ToastUtils.showLong(message);
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
    public void onSendMessageCodeSuccess() {
        LoginActivity.MyCountDownTimer countDownTimer = LoginActivity.getInstance().countDownTimer;
        if (countDownTimer == null) {
            countDownTimer = new LoginActivity.MyCountDownTimer(60000, 1000);
        }
        if (!countDownTimer.isRunning) {
            countDownTimer.start();
        }
    }

    @Override
    public void onLoginSuccess() {
        Intent intent = new Intent(this, NewMainActivity.class);
        intent.putExtra("fromLogin",true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        LoginActivity.getInstance().countDownTimer.cancel();
        LoginActivity.getInstance().finish();
        finish();
        LogUploadUtil.uploadLog(new LogUploadRequest(103800, 103801, String.format("【%s】登录系统", SPUtils.getInstance().getString(Constants.CONFIG_LAST_USER_LOGIN_NAME))));
    }

    @Override
    public Activity getActivity() {
        return this;
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


    class MyTextWatcher implements TextWatcher {
        private EditText editText;

        public MyTextWatcher(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!TextUtils.isEmpty(s)) {
                switch (editText.getId()) {
                    case R.id.et_1:
                        ready1 = true;
                        et1.setSelected(true);
                        et2.requestFocus();
                        mFocusedEditText = et2;
                        break;
                    case R.id.et_2:
                        ready2 = true;
                        et2.setSelected(true);
                        et3.requestFocus();
                        mFocusedEditText = et3;
                        break;
                    case R.id.et_3:
                        ready3 = true;
                        et3.setSelected(true);
                        et4.requestFocus();
                        mFocusedEditText = et4;
                        break;
                    case R.id.et_4:
                        ready4 = true;
                        et4.setSelected(true);
                        et4.setSelection(et4.getText().toString().length());
                        break;
                }
            } else {
                switch (editText.getId()) {
                    case R.id.et_1:
                        ready1 = false;
                        et1.setSelected(false);
                        break;
                    case R.id.et_2:
                        ready2 = false;
                        et2.setSelected(false);
                        break;
                    case R.id.et_3:
                        ready3 = false;
                        et3.setSelected(false);
                        break;
                    case R.id.et_4:
                        ready4 = false;
                        et4.setSelected(false);
                        break;
                }
            }
            checkReady();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private void checkReady() {
        if (ready1 && ready2 && ready3 && ready4) {
            mPresenter.login(loginName, psd
                    , et1.getText().toString() + et2.getText().toString() + et3.getText().toString() + et4.getText().toString()
                    , deviceId, Constants.REGISTRATION_ID);
        }
    }

    class MyOnKeyListener implements View.OnKeyListener {
        private EditText editText;

        public MyOnKeyListener(EditText editText) {
            this.editText = editText;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (mFocusedEditText == null) return false;
            if (TextUtils.isEmpty(mFocusedEditText.getText().toString().trim())
                    && keyCode == KeyEvent.KEYCODE_DEL
                    && event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (editText.getId()) {
                    case R.id.et_4:
                        et3.requestFocus();
                        et3.setText("");
                        mFocusedEditText = et3;
                        break;
                    case R.id.et_3:
                        et2.requestFocus();
                        et2.setText("");
                        mFocusedEditText = et2;
                        break;
                    case R.id.et_2:
                        et1.requestFocus();
                        et1.setText("");
                        mFocusedEditText = et1;
                        break;
                }
            }
            return false;
        }
    }

    class MyOnCommitTextListener implements InterceptableEditText.OnCommitTextListener {
        private EditText editText;

        public MyOnCommitTextListener(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void commitText(CharSequence text) {
            if (!TextUtils.isEmpty(editText.getText().toString().trim())) {
                editText.setText(text);
                editText.setSelection(editText.getText().toString().length());
            } else {
                if (editText.getId() == R.id.et_1 && isMsgCode(text.toString())) {
                    et1.setText(String.valueOf(text.toString().charAt(0)));
                    et2.setText(String.valueOf(text.toString().charAt(1)));
                    et3.setText(String.valueOf(text.toString().charAt(2)));
                    et4.setText(String.valueOf(text.toString().charAt(3)));
                }
            }
        }
    }

    private boolean isMsgCode(String mobiles) {
        String telRegex = "\\d{4}";
        if (TextUtils.isEmpty(mobiles)) {
            return false;
        } else {
            return mobiles.matches(telRegex);
        }
    }

    class MyOnFocusChangeListener implements View.OnFocusChangeListener {
        private EditText editText;

        public MyOnFocusChangeListener(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                mFocusedEditText = editText;
                editText.setSelection(editText.getText().toString().length());
            }
        }
    }
}
