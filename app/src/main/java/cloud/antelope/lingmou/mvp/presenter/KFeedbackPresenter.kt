package cloud.antelope.lingmou.mvp.presenter

import android.app.Application
import cloud.antelope.lingmou.R
import cloud.antelope.lingmou.mvp.contract.FeedbackContract
import cloud.antelope.lingmou.mvp.contract.KFeedbackContract
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreBaseEntity
import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.http.imageloader.ImageLoader
import com.jess.arms.integration.AppManager
import com.jess.arms.mvp.BasePresenter
import com.jess.arms.utils.RxLifecycleUtils
import com.lingdanet.safeguard.common.utils.NetworkUtils
import com.lingdanet.safeguard.common.utils.Utils
import com.trello.rxlifecycle2.android.ActivityEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import me.jessyan.rxerrorhandler.handler.RetryWithDelay
import javax.inject.Inject

@ActivityScope
class KFeedbackPresenter @Inject
constructor(model: KFeedbackContract.Model,
            rootView: KFeedbackContract.View,
            private var mErrorHandler: RxErrorHandler?,
            private var mApplication: Application?,
            private var mImageLoader: ImageLoader?,
            private var mAppManager: AppManager?) : BasePresenter<KFeedbackContract.Model, KFeedbackContract.View>(model, rootView) {

    override fun onDestroy() {
        super.onDestroy()
        this.mErrorHandler = null
        this.mAppManager = null
        this.mImageLoader = null
        this.mApplication = null
    }

    fun feedback(userId: String, phone: String, content: String) {
        if (!NetworkUtils.isConnected()) {
            mRootView.showMessage(Utils.getContext().resources.getString(R.string.network_disconnect))
            return
        }
        mModel.feedback(userId, phone, content)
                .subscribeOn(Schedulers.io())
                .retryWhen(RetryWithDelay(1, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                .doOnSubscribe { _ ->
                    mRootView.showLoading("")//显示下拉刷新的进度条
                }.subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally {
                    if (mRootView != null)
                    mRootView.hideLoading()//隐藏下拉刷新的进度条
                }
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(object : ErrorHandleSubscriber<GetKeyStoreBaseEntity>(mErrorHandler!!) {
                    override fun onNext(emptyEntity: GetKeyStoreBaseEntity) {
                        mRootView.feedbackSuccess()
                    }
                })
    }

}
