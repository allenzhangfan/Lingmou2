package cloud.antelope.lingmou.mvp.presenter;

import android.app.Application;

import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.trello.rxlifecycle2.android.ActivityEvent;

import javax.inject.Inject;

import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.contract.CyqzConfigContract;
import cloud.antelope.lingmou.mvp.model.entity.OperationEntity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;


@ActivityScope
public class CyqzConfigPresenter extends BasePresenter<CyqzConfigContract.Model, CyqzConfigContract.View> {
    private RxErrorHandler mErrorHandler;
    private Application mApplication;
    private ImageLoader mImageLoader;
    private AppManager mAppManager;

    @Inject
    public CyqzConfigPresenter(CyqzConfigContract.Model model, CyqzConfigContract.View rootView
            , RxErrorHandler handler, Application application
            , ImageLoader imageLoader, AppManager appManager) {
        super(model, rootView);
        this.mErrorHandler = handler;
        this.mApplication = application;
        this.mImageLoader = imageLoader;
        this.mAppManager = appManager;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mImageLoader = null;
        this.mApplication = null;
    }

    public void getCyqzConfig() {

        mModel.getCyqzConfig()
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                // .doOnSubscribe(disposable -> {
                //     mRootView.showLoading();//显示下拉刷新的进度条
                // }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                // .doFinally(() -> {
                //     mRootView.hideLoading();//隐藏下拉刷新的进度条
                // })
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<OperationEntity>(mErrorHandler) {

                    @Override
                    public void onNext(OperationEntity entity) {
                        SPUtils.getInstance().put(Constants.CONFIG_CASE_ID, entity.getCaseColumnId());
                        SPUtils.getInstance().put(Constants.CONFIG_CLUE_ID, entity.getTipColumnId());
                        // SPUtils.getInstance().put(Constants.CONFIG_OPERATION_ID, entity.getOperationCenterId());
                        if (null != mRootView) {
                            mRootView.onGetCyqzConfigSuccess(entity);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        if (null != mRootView) {
                            mRootView.onGetCyqzConfigError();
                        }
                    }
                });
    }
}
