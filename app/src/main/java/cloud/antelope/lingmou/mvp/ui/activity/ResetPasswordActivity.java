package cloud.antelope.lingmou.mvp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.lingdanet.safeguard.common.constant.CommonConstant;
import com.lingdanet.safeguard.common.modle.LoadingDialog;
import com.lingdanet.safeguard.common.utils.AppStatusTracker;
import com.lingdanet.safeguard.common.utils.KeyboardUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.lingdanet.safeguard.common.utils.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.SaveUtils;
import cloud.antelope.lingmou.di.component.DaggerResetPasswordComponent;
import cloud.antelope.lingmou.di.module.ResetPasswordModule;
import cloud.antelope.lingmou.mvp.contract.ResetPasswordContract;
import cloud.antelope.lingmou.mvp.presenter.ResetPasswordPresenter;


public class ResetPasswordActivity extends BaseActivity<ResetPasswordPresenter>
        implements ResetPasswordContract.View,
        CompoundButton.OnCheckedChangeListener {


    @BindView(R.id.old_psw_et)
    EditText mOldPswEt;
    @BindView(R.id.new_psw_et)
    EditText mNewPswEt;
    @BindView(R.id.confirm_new_psw_et)
    EditText mConfirmNewPswEt;
    @BindView(R.id.show_psw_cb)
    CheckBox mShowPswCb;
    @BindView(R.id.head_left_iv)
    ImageButton mHeadLeftIv;
    @BindView(R.id.title_tv)
    TextView mTitleTv;
    @BindView(R.id.head_right_tv)
    TextView mHeadRightTv;

    @Inject
    LoadingDialog mLoadingDialog;

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        DaggerResetPasswordComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .resetPasswordModule(new ResetPasswordModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_reset_password; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mTitleTv.setText(R.string.modify_login_password);
        mHeadRightTv.setVisibility(View.VISIBLE);
        mHeadRightTv.setText(R.string.confirm);
        mShowPswCb.setOnCheckedChangeListener(this);
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

    }

    @Override
    public void killMyself() {
        finish();
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int i = buttonView.getId();
        if (i == R.id.show_psw_cb) {
            TransformationMethod method = isChecked ? HideReturnsTransformationMethod.getInstance()
                    : PasswordTransformationMethod.getInstance();
            mOldPswEt.setTransformationMethod(method);
            mOldPswEt.setSelection(mOldPswEt.getText().toString().length());
            mNewPswEt.setTransformationMethod(method);
            mNewPswEt.setSelection(mNewPswEt.getText().toString().length());
            mConfirmNewPswEt.setTransformationMethod(method);
            mConfirmNewPswEt.setSelection(mConfirmNewPswEt.getText().toString().length());
        }
    }

    @OnClick({R.id.head_left_iv, R.id.head_right_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.head_left_iv:
                KeyboardUtils.hideSoftInput(ResetPasswordActivity.this);
                finish();
                break;
            case R.id.head_right_tv:
                //点击确定，重置密码
                String oldPsw = mOldPswEt.getText().toString();
                String newPsw = mNewPswEt.getText().toString();
                String confirmPsw = mConfirmNewPswEt.getText().toString();
                if (TextUtils.isEmpty(oldPsw)) {
                    ToastUtils.showShort(R.string.error_input_old_password);
                } else if (TextUtils.isEmpty(newPsw)) {
                    ToastUtils.showShort(R.string.error_input_new_password);
                } else if (!passMatch(newPsw)) {
                    ToastUtils.showShort(R.string.error_input_length_password);
                } else if (!confirmPsw.equals(newPsw)) {
                    ToastUtils.showShort(R.string.error_psw_not_same);
                } else {
                    //调用重置接口
                    String uid = SPUtils.getInstance().getString(CommonConstant.UID);
                    mPresenter.updatePsw(uid, oldPsw, confirmPsw);
                }
                break;
        }
    }

    /**
     * @param password 密码
     * @return true：密码为6~18位以字母开头的字母,数字,_组合
     */
    public static boolean passMatch(String password) {
        Pattern p = Pattern.compile("^[a-zA-Z]\\w{5,17}$");
        Matcher m = p.matcher(password);
        while (m.find()) {
            return true;
        }
        return false;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void updateSuccess() {
        //更改密码成功，退出，进入登录界面
        ToastUtils.showShort(R.string.reset_password_success);
        SaveUtils.clear();
        AppStatusTracker.getInstance().exitAllActivity();
        // 退出登录时，下次不再自动登录
        Intent intent = new Intent(Utils.getContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        System.gc();
    }
}
