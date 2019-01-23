package cloud.antelope.lingmou.mvp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.constant.CommonConstant;
import com.lingdanet.safeguard.common.modle.LoadingDialog;
import com.lingdanet.safeguard.common.utils.KeyboardUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.di.component.DaggerFeedbackComponent;
import cloud.antelope.lingmou.di.module.FeedbackModule;
import cloud.antelope.lingmou.mvp.contract.FeedbackContract;
import cloud.antelope.lingmou.mvp.presenter.FeedbackPresenter;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class FeedbackActivity extends BaseActivity<FeedbackPresenter> implements FeedbackContract.View {

    @BindView(R.id.phone_et)
    EditText mPhoneEt;
    @BindView(R.id.content_et)
    EditText mContentEt;
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
        DaggerFeedbackComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .feedbackModule(new FeedbackModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_feedback; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mTitleTv.setText(R.string.feedback);
        mHeadRightTv.setVisibility(View.VISIBLE);
        mHeadRightTv.setText(R.string.commit);
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
    }


    @OnClick({R.id.head_left_iv, R.id.head_right_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.head_left_iv:
                KeyboardUtils.hideSoftInput(FeedbackActivity.this);
                finish();
                break;
            case R.id.head_right_tv:
                //点击提交
                String phone = mPhoneEt.getText().toString();
                String content = mContentEt.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    ToastUtils.showShort("请填写问题描述");
                } else {
                    //提交意见
                    String uid = SPUtils.getInstance().getString(CommonConstant.UID);
                    mPresenter.feedback(uid, phone, content);
                }
                break;
        }
    }

    @Override
    public void feedbackSuccess() {
        KeyboardUtils.hideSoftInput(FeedbackActivity.this);
        ToastUtils.showShort(R.string.hint_feedback_success);
        finish();
    }

    @Override
    public Activity getActivity() {
        return this;
    }
}
