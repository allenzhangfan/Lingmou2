package cloud.antelope.lingmou.mvp.presenter;

import android.app.Application;
import android.text.TextUtils;

import com.jess.arms.integration.AppManager;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.RxLifecycleUtils;
import com.lingdanet.safeguard.common.utils.LogUtils;
import com.lingdanet.safeguard.common.utils.NetworkUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.Utils;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.SuccessNullException;
import cloud.antelope.lingmou.app.utils.JPushUtils;
import cloud.antelope.lingmou.app.utils.SaveUtils;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.model.entity.LmBaseResponseEntity;
import cloud.antelope.lingmou.mvp.model.entity.LoginEntity;
import cloud.antelope.lingmou.mvp.model.entity.SoldierInfoEntity;
import cloud.antelope.lingmou.mvp.model.entity.UserInfoEntity;
import cloud.antelope.lingmou.mvp.model.entity.UserInfoPrivilegeEntity;
import cloud.antelope.lingmou.mvp.model.entity.UserModulesEntity;
import cloud.antelope.lingmou.mvp.model.entity.WebPrivilege;
import cloud.antelope.lingmou.mvp.ui.activity.LoginActivity;
import cloud.antelope.lingmou.mvp.ui.activity.MessageCodeActivityActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;

import javax.inject.Inject;

import cloud.antelope.lingmou.mvp.contract.MessageCodeActivityContract;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;


@ActivityScope
public class MessageCodeActivityPresenter extends BasePresenter<MessageCodeActivityContract.Model, MessageCodeActivityContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public MessageCodeActivityPresenter(MessageCodeActivityContract.Model model, MessageCodeActivityContract.View rootView) {
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

    public void login(String loginName,String psd, String code, String deviceId, String registrationId) {
        if (!NetworkUtils.isConnected()) {
            SPUtils.getInstance().put(Constants.HAS_LOGIN, false);
            mRootView.showMessage(Utils.getContext().getResources().getString(R.string.network_disconnect));
            return;
        }

        mModel.login(loginName, psd,code, deviceId, registrationId)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(1, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                .doOnSubscribe(disposable -> {
                    mRootView.showLoading("");//显示下拉刷新的进度条
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<LmBaseResponseEntity<LoginEntity>>(mErrorHandler) {
                    @Override
                    public void onNext(LmBaseResponseEntity<LoginEntity> loginEntityLmBaseResponseEntity) {
                        if (loginEntityLmBaseResponseEntity.isSuccess()) {
                            if (null != loginEntityLmBaseResponseEntity.result) {
                                SPUtils.getInstance().put(Constants.ZHYY_TOKEN, loginEntityLmBaseResponseEntity.result.getToken());
                                getUserInfo();

                            }
                        } else if (loginEntityLmBaseResponseEntity.isGatewayBad()) {
                            mRootView.hideLoading();
                            ((MessageCodeActivityActivity) mRootView.getActivity()).showMessage(Utils.getContext().getString(R.string.bad_gateway));
                        } else {
                            mRootView.hideLoading();
                            int code = loginEntityLmBaseResponseEntity.code;
                            ((MessageCodeActivityActivity) mRootView.getActivity()).showMessage(loginEntityLmBaseResponseEntity.message);
                            /*if (code == 50109 ) {
                                if(!TextUtils.isEmpty(loginEntityLmBaseResponseEntity.result.getPhoneNum())){
                                    sendCode(loginName,loginEntityLmBaseResponseEntity.result.getPhoneNum());
                                }
                            }*/
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        mRootView.hideLoading();
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
                .doFinally(() -> {
                    if (mRootView != null)
                        mRootView.hideLoading();//隐藏下拉刷新的进度条
                })
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<SoldierInfoEntity>(mErrorHandler) {
                    @Override
                    public void onNext(SoldierInfoEntity entity) {
                        mRootView.onLoginSuccess();
                        mRootView.getSoldierInfoSuccess(entity);
                    }

                    @Override
                    public void onError(Throwable t) {
                        mRootView.onLoginSuccess();
                        // super.onError(t);
                    }
                });
    }
    public void sendCode(String loginName,String phoneNumber){
        mModel.sendCode(loginName,phoneNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))
                .subscribe(new ErrorHandleSubscriber<Object>(mErrorHandler) {
                    @Override
                    public void onNext(Object o) {
                        mRootView.onSendMessageCodeSuccess();
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        if(t instanceof SuccessNullException){
                            mRootView.onSendMessageCodeSuccess();
                        }
                    }
                });
    }
    private void getUserInfo() {
        mModel.queryUserInfo()
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(1, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))
                .subscribe(new ErrorHandleSubscriber<UserInfoEntity>(mErrorHandler) {
                    @Override
                    public void onNext(UserInfoEntity userInfo) {
                        LogUtils.i("UserInfoEntity = " + userInfo);
                        MobclickAgent.onProfileSignIn(String.valueOf(userInfo.userInfo.id));
                        SaveUtils.saveLastLoginInfo(userInfo);
                        SaveUtils.savePrivileges(userInfo);
                        JPushUtils.resumeJPush();
                        // JPushUtils.setAlias(String.valueOf(userInfo.getId()));
                        getSoldierInfo();
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        mRootView.hideLoading();
                        SPUtils.getInstance().put(Constants.HAS_LOGIN, false);
                    }
                });
    }
}
