package cloud.antelope.lingmou.mvp.presenter;

import android.app.Application;

import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;
import com.trello.rxlifecycle2.android.ActivityEvent;

import javax.inject.Inject;

import cloud.antelope.lingmou.mvp.contract.ShareContract;
import cloud.antelope.lingmou.mvp.model.entity.EmptyEntity;
import cloud.antelope.lingmou.mvp.model.entity.ShareEntity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;


@ActivityScope
public class SharePresenter extends BasePresenter<ShareContract.Model, ShareContract.View> {
    private RxErrorHandler mErrorHandler;
    private Application mApplication;
    private AppManager mAppManager;

    @Inject
    public SharePresenter(ShareContract.Model model, ShareContract.View rootView
            , RxErrorHandler handler, Application application
            , AppManager appManager) {
        super(model, rootView);
        this.mErrorHandler = handler;
        this.mApplication = application;
        this.mAppManager = appManager;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mApplication = null;
    }

    public void share(ShareEntity shareEntity) {

        mModel.share(shareEntity)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                // .doOnSubscribe(disposable -> {
                //     mRootView.showLoading();//显示下拉刷新的进度条
                // }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                // .doFinally(() -> {
                //     mRootView.hideLoading();//隐藏下拉刷新的进度条
                // })
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<EmptyEntity>(mErrorHandler) {

                    @Override
                    public void onNext(EmptyEntity entity) {

                    }
                });
    }
}
