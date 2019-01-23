package cloud.antelope.lingmou.mvp.presenter;

import android.app.Application;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;
import com.lingdanet.safeguard.common.net.ApiException;
import com.lingdanet.safeguard.common.net.BadGatewayException;
import com.lingdanet.safeguard.common.net.TokenAbateException;
import com.lingdanet.safeguard.common.utils.LogUtils;
import com.lingdanet.safeguard.common.utils.NetworkUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.Utils;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.List;

import javax.inject.Inject;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.NoPermissionException;
import cloud.antelope.lingmou.app.SuccessNullException;
import cloud.antelope.lingmou.app.utils.JPushUtils;
import cloud.antelope.lingmou.app.utils.SaveUtils;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.contract.LoginContract;
import cloud.antelope.lingmou.mvp.model.entity.LmBaseResponseEntity;
import cloud.antelope.lingmou.mvp.model.entity.LoginEntity;
import cloud.antelope.lingmou.mvp.model.entity.UrlEntity;
import cloud.antelope.lingmou.mvp.model.entity.UserInfoEntity;
import cloud.antelope.lingmou.mvp.model.entity.UserInfoPrivilegeEntity;
import cloud.antelope.lingmou.mvp.model.entity.UserModulesEntity;
import cloud.antelope.lingmou.mvp.model.entity.WebPrivilege;
import cloud.antelope.lingmou.mvp.ui.activity.LoginActivity;
import io.jsonwebtoken.Jwts;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;


@ActivityScope
public class LoginPresenter extends BasePresenter<LoginContract.Model, LoginContract.View> {
    private RxErrorHandler mErrorHandler;
    private Application mApplication;
    private ImageLoader mImageLoader;
    private AppManager mAppManager;
    private String lastPhoneNumber;

    @Inject
    public LoginPresenter(LoginContract.Model model, LoginContract.View rootView
            , RxErrorHandler handler, Application application
            , ImageLoader imageLoader, AppManager appManager) {
        super(model, rootView);
        this.mErrorHandler = handler;
        this.mApplication = application;
        this.mImageLoader = imageLoader;
        this.mAppManager = appManager;
    }

    public void login(String username, String password, String deviceId, String registrationId) {
        if (!NetworkUtils.isConnected()) {
            SPUtils.getInstance().put(Constants.HAS_LOGIN, false);
            mRootView.showMessage(Utils.getContext().getResources().getString(R.string.network_disconnect));
            return;
        }

        mModel.login(username, password, deviceId, registrationId)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(1, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                .doOnSubscribe(disposable -> {
                    mRootView.showLoading("");//显示下拉刷新的进度条
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                    if (mRootView != null)
                        mRootView.hideLoading();//隐藏下拉刷新的进度条
                })
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<LmBaseResponseEntity<LoginEntity>>(mErrorHandler) {
                    @Override
                    public void onNext(LmBaseResponseEntity<LoginEntity> loginEntityLmBaseResponseEntity) {
                        if (loginEntityLmBaseResponseEntity.isSuccess()) {
                            if (null != loginEntityLmBaseResponseEntity.result) {
//                                SPUtils.getInstance().put(Constants.ZHYY_TOKEN, loginEntityLmBaseResponseEntity.result.getToken());
//                                getUserInfo();
                                if (!TextUtils.isEmpty(loginEntityLmBaseResponseEntity.result.getPhoneNum())) {
                                    LoginActivity.MyCountDownTimer countDownTimer = ((LoginActivity) mRootView.getActivity()).countDownTimer;
                                    if (countDownTimer != null && countDownTimer.isRunning && TextUtils.equals(lastPhoneNumber, loginEntityLmBaseResponseEntity.result.getPhoneNum())) {
                                        mRootView.onSendMessageCodeSuccess(lastPhoneNumber);
                                    } else {
                                        sendCode(username,loginEntityLmBaseResponseEntity.result.getPhoneNum());
                                    }
                                }
                            }
                        } else if (loginEntityLmBaseResponseEntity.isGatewayBad()) {
                            ((LoginActivity) mRootView.getActivity()).showMessage(Utils.getContext().getString(R.string.bad_gateway));
                        } else {
                            int code = loginEntityLmBaseResponseEntity.code;
                            ((LoginActivity) mRootView.getActivity()).showMessage(loginEntityLmBaseResponseEntity.message);
                            if (code == 50109) {
                                if (!TextUtils.isEmpty(loginEntityLmBaseResponseEntity.result.getPhoneNum())) {
                                    LoginActivity.MyCountDownTimer countDownTimer = ((LoginActivity) mRootView.getActivity()).countDownTimer;
                                    if (countDownTimer != null && countDownTimer.isRunning && TextUtils.equals(lastPhoneNumber, loginEntityLmBaseResponseEntity.result.getPhoneNum())) {
                                        mRootView.onSendMessageCodeSuccess(lastPhoneNumber);
                                    } else {
                                        sendCode(username,loginEntityLmBaseResponseEntity.result.getPhoneNum());
                                    }
                                }
                            }
                        }
                    }
                });

        // new BaseSubscribe<UserInfoEntity>((Activity) mView, R.string.login_ing) {
        //     @Override
        //     protected void _onNext(UserInfoEntity userInfo) {
        //         // JPushUtils.setAlias(String.valueOf(userInfo.getId()));
        //         MyApplication.getInstance().saveLastLoginInfo(userInfo);
        //         mView.onLoginSuccess(userInfo);
        //     }
        //
        //     @Override
        //     protected void _onError(String message) {
        //         SPUtils.getInstance().put(Constants.HAS_LOGIN, false);
        //         mView.onLoginError(message);
        //     }
        // });
    }

    private void sendCode(String loginName,String phoneNumber) {
        mModel.sendCode(loginName,phoneNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))
                .subscribe(new ErrorHandleSubscriber<Object>(mErrorHandler) {
                    @Override
                    public void onNext(Object o) {
                        mRootView.onSendMessageCodeSuccess(phoneNumber);
                        lastPhoneNumber = phoneNumber;
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        if (t instanceof SuccessNullException) {
                            mRootView.onSendMessageCodeSuccess(phoneNumber);
                            lastPhoneNumber = phoneNumber;
                        }
                    }
                });
    }

    private void getUserInfo() {
        mModel.queryUserInfo()
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(1, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                .doOnSubscribe(disposable -> {
                    mRootView.showLoading("");//显示下拉刷新的进度条
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                    if (mRootView != null)
                        mRootView.hideLoading();//隐藏下拉刷新的进度条
                })
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
                        mRootView.gotoMainActivity();
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        SPUtils.getInstance().put(Constants.HAS_LOGIN, false);
                    }
                });
    }

    public void getBaseUrls() {
        mModel.getBaseUrls()
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(1, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<List<UrlEntity>>(mErrorHandler) {
                    @Override
                    public void onNext(List<UrlEntity> entities) {
                        mRootView.getUrlsSuccess(entities);
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mImageLoader = null;
        this.mApplication = null;
    }

}
