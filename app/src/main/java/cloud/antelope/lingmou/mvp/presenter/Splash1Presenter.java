package cloud.antelope.lingmou.mvp.presenter;

import android.app.Application;
import android.text.TextUtils;

import com.jess.arms.integration.AppManager;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.RxLifecycleUtils;
import com.lingdanet.safeguard.common.constant.CommonConstant;
import com.lingdanet.safeguard.common.utils.LogUtils;
import com.lingdanet.safeguard.common.utils.NetworkUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import javax.inject.Inject;

import cloud.antelope.lingmou.app.utils.JPushUtils;
import cloud.antelope.lingmou.app.utils.SaveUtils;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.contract.Splash1Contract;
import cloud.antelope.lingmou.mvp.model.entity.SoldierInfoEntity;
import cloud.antelope.lingmou.mvp.model.entity.UpdateLastLoginTimeRequest;
import cloud.antelope.lingmou.mvp.model.entity.UserInfoEntity;
import cloud.antelope.lingmou.mvp.model.entity.UserModulesEntity;
import cloud.antelope.lingmou.mvp.model.entity.WebPrivilege;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;


@ActivityScope
public class Splash1Presenter extends BasePresenter<Splash1Contract.Model, Splash1Contract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public Splash1Presenter(Splash1Contract.Model model, Splash1Contract.View rootView) {
        super(model, rootView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mImageLoader = null;
        this.mApplication = null;
    }

    public void updateLastLoginTime(UpdateLastLoginTimeRequest request) {
        if (TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.ZHYY_TOKEN))
                || TextUtils.isEmpty(SPUtils.getInstance().getString(CommonConstant.UID))
                || TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.URL_BASE))
                ) return;
        mModel.updateLastLoginTime(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<Object>(mErrorHandler) {
                    @Override
                    public void onNext(Object entity) {
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                    }
                });
    }

    public void getUserInfo() {
        if (TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.ZHYY_TOKEN))
                || TextUtils.isEmpty(SPUtils.getInstance().getString(CommonConstant.UID))
                || TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.URL_BASE))
                ) return;
        mModel.queryUserInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))
                .subscribe(new ErrorHandleSubscriber<UserInfoEntity>(mErrorHandler) {
                    @Override
                    public void onNext(UserInfoEntity userInfo) {
                        MobclickAgent.onProfileSignIn(String.valueOf(userInfo.userInfo.id));
                        SaveUtils.saveLastLoginInfo(userInfo);
                        SaveUtils.savePrivileges(userInfo);
                        // SPUtils.getInstance().put(Constants.AFTER_LOGIN_SET_JPUSH_ALIAS_SUCCESS, false);

                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                    }
                });
    }

    public void getSoldierInfo() {
        if (!NetworkUtils.isConnected()) {
            return;
        }
        mModel.getSoldierInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
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
