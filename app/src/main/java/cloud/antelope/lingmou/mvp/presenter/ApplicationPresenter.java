package cloud.antelope.lingmou.mvp.presenter;

import android.app.Application;

import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;
import com.lingdanet.safeguard.common.utils.NetworkUtils;
import com.lingdanet.safeguard.common.utils.Utils;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import javax.inject.Inject;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.contract.ApplicationContract;
import cloud.antelope.lingmou.mvp.model.entity.SoldierInfoEntity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;


@FragmentScope
public class ApplicationPresenter extends BasePresenter<ApplicationContract.Model, ApplicationContract.View> {
    private RxErrorHandler mErrorHandler;
    private Application mApplication;
    private ImageLoader mImageLoader;
    private AppManager mAppManager;

    @Inject
    public ApplicationPresenter(ApplicationContract.Model model, ApplicationContract.View rootView
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

    public void getSoldierInfo() {
        if (!NetworkUtils.isConnected()) {
            return;
        }
        mModel.getSoldierInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, FragmentEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<SoldierInfoEntity>(mErrorHandler) {
                    @Override
                    public void onNext(SoldierInfoEntity entity) {
                        mRootView.getSoldierInfoSuccess(entity);
                    }

                    @Override
                    public void onError(Throwable t) {
                        // super.onError(t);
                    }
                });
    }

}
