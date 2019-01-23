package cloud.antelope.lingmou.mvp.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import butterknife.OnClick
import cloud.antelope.lingmou.R
import cloud.antelope.lingmou.di.component.DaggerKFeedbackComponent
import cloud.antelope.lingmou.di.module.KFeedbackModule
import cloud.antelope.lingmou.mvp.contract.KFeedbackContract
import cloud.antelope.lingmou.mvp.presenter.KFeedbackPresenter
import com.jess.arms.base.BaseActivity
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils
import com.lingdanet.safeguard.common.constant.CommonConstant
import com.lingdanet.safeguard.common.modle.LoadingDialog
import com.lingdanet.safeguard.common.utils.KeyboardUtils
import com.lingdanet.safeguard.common.utils.SPUtils
import com.lingdanet.safeguard.common.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_feedback.*
import kotlinx.android.synthetic.main.include_head_item.*
import javax.inject.Inject

class KFeedbackActivity : BaseActivity<KFeedbackPresenter>(), KFeedbackContract.View, View.OnClickListener {


    @Inject
    lateinit var mLoadingDialog: LoadingDialog

    override fun setupActivityComponent(appComponent: AppComponent) {
        DaggerKFeedbackComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .kFeedbackModule(KFeedbackModule(this@KFeedbackActivity))
                .build()
                .inject(this)
    }

    override fun initView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_feedback //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    override fun initData(savedInstanceState: Bundle?) {
        title_tv.text = getString(R.string.feedback)
        head_right_tv.visibility = View.VISIBLE
        head_right_tv.text=getString(R.string.commit)
        head_left_iv.setOnClickListener(this)
        head_right_tv.setOnClickListener(this)

    }
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.head_left_iv -> {
                KeyboardUtils.hideSoftInput(this@KFeedbackActivity)
                finish()
            }
            R.id.head_right_tv -> {
                //点击提交
                val phone = phone_et.text.toString()
                val content = content_et.text.toString()
                if (TextUtils.isEmpty(content)) {
                    ToastUtils.showShort("请填写问题描述")
                } else {
                    //提交意见
                    val uid = SPUtils.getInstance().getString(CommonConstant.UID)
                    mPresenter.feedback(uid, phone, content)
                }
            }
        }
    }

    override fun showLoading(message: String) {
        mLoadingDialog.show()
    }

    override fun hideLoading() {
        mLoadingDialog.dismiss()
    }

    override fun showMessage(message: String) {
        ToastUtils.showShort(message)
    }

    override fun launchActivity(intent: Intent) {
        checkNotNull<Intent>(intent)
        ArmsUtils.startActivity(intent)
    }

    override fun killMyself() {
        finish()
    }

    override fun feedbackSuccess() {
        KeyboardUtils.hideSoftInput(this@KFeedbackActivity)
        ToastUtils.showShort(R.string.hint_feedback_success)
        finish()
    }

    override fun getActivity(): Activity {
        return this
    }
}