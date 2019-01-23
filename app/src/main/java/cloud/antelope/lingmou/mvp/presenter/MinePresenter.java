package cloud.antelope.lingmou.mvp.presenter;

import android.app.Application;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;

import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;
import com.lingdanet.safeguard.common.utils.LogUtils;
import com.lingdanet.safeguard.common.utils.NetworkUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.Utils;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.SuccessNullException;
import cloud.antelope.lingmou.app.utils.JPushUtils;
import cloud.antelope.lingmou.app.utils.SaveUtils;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.contract.DailyContract;
import cloud.antelope.lingmou.mvp.contract.MineContract;
import cloud.antelope.lingmou.mvp.model.entity.EmptyEntity;
import cloud.antelope.lingmou.mvp.model.entity.PushRequest;
import cloud.antelope.lingmou.mvp.model.entity.TokenEntity;
import cloud.antelope.lingmou.mvp.model.entity.UserInfoEntity;
import cloud.antelope.lingmou.mvp.model.entity.UserInfoPrivilegeEntity;
import cloud.antelope.lingmou.mvp.model.entity.UserModulesEntity;
import cloud.antelope.lingmou.mvp.model.entity.WebPrivilege;
import cloud.antelope.lingmou.mvp.ui.activity.LoginActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;

import static com.jess.arms.utils.ArmsUtils.startActivity;


@ActivityScope
public class MinePresenter extends BasePresenter<MineContract.Model, MineContract.View> {
    private RxErrorHandler mErrorHandler;
    private Application mApplication;
    private ImageLoader mImageLoader;
    private AppManager mAppManager;

    @Inject
    public MinePresenter(MineContract.Model model, MineContract.View rootView
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
    public void getUserInfo() {
        if (!NetworkUtils.isConnected()) {
            mRootView.showMessage(Utils.getContext().getResources().getString(R.string.network_disconnect));
            return;
        }
        if(TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.URL_BASE))){
            if (!SaveUtils.isInLogin) {
                SaveUtils.isInLogin = true;
                JPushUtils.deleteAlias();
                SaveUtils.clear();
                Message message = new Message();
                message.what = AppManager.KILL_ALL;
                AppManager.post(message);
                //                AppStatusTracker.getInstance().exitAllActivity();
                // 退出登录时，下次不再自动登录
                Intent intent = new Intent(Utils.getContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            return;
        }
        mModel.getUserInfo()
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                .doOnSubscribe(disposable -> {
                    if (null != mRootView) {
                        mRootView.showLoading("");//显示下拉刷新的进度条
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                    if (null != mRootView) {
                        mRootView.hideLoading();//隐藏下拉刷新的进度条}
                    }
                })
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, FragmentEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<UserInfoEntity>(mErrorHandler) {

                    @Override
                    public void onNext(UserInfoEntity userInfo) {
                        SaveUtils.saveLastLoginInfo(userInfo);
                        SaveUtils.savePrivileges(userInfo);
                        mRootView.getUserInfoSuccess(userInfo);
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        if (t instanceof SuccessNullException) {
                            mRootView.getUserInfoSuccess(null);
                        }
                    }
                });
    }

    public void getStorageToken() {
        if (!NetworkUtils.isConnected()) {
            mRootView.showMessage(Utils.getContext().getResources().getString(R.string.network_disconnect));
            return;
        }
        if(TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.URL_BASE))){
            if (!SaveUtils.isInLogin) {
                SaveUtils.isInLogin = true;
                JPushUtils.deleteAlias();
                SaveUtils.clear();
                Message message = new Message();
                message.what = AppManager.KILL_ALL;
                AppManager.post(message);
                //                AppStatusTracker.getInstance().exitAllActivity();
                // 退出登录时，下次不再自动登录
                Intent intent = new Intent(Utils.getContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            return;
        }
        mModel.getStorageToken()
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .doOnSubscribe(disposable -> mRootView.showLoading(""))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, FragmentEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<TokenEntity>(mErrorHandler) {
                    @Override
                    public void onNext(TokenEntity tokenEntity) {
                        SPUtils.getInstance().put(Constants.CONFIG_LAST_LY_TOKEN, tokenEntity.getToken());
                        mRootView.getTokenSuccess();
                    }

                    @Override
                    public void onError(Throwable t) {
                        // super.onError(t);
                    }
                });
    }

    public void setPushConfig(String type) {
        if (!NetworkUtils.isConnected()) {
            mRootView.showMessage(Utils.getContext().getResources().getString(R.string.network_disconnect));
            return;
        }
        if(TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.URL_BASE))){
            if (!SaveUtils.isInLogin) {
                SaveUtils.isInLogin = true;
                JPushUtils.deleteAlias();
                SaveUtils.clear();
                Message message = new Message();
                message.what = AppManager.KILL_ALL;
                AppManager.post(message);
                //                AppStatusTracker.getInstance().exitAllActivity();
                // 退出登录时，下次不再自动登录
                Intent intent = new Intent(Utils.getContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            return;
        }
        List<PushRequest> list = new ArrayList<>();
        PushRequest request = new PushRequest();
        request.setKey(Constants.PUSH_TYPE_CLUE);
        request.setValue(type);
        list.add(request);
        mModel.setPushConfig(list)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                // .doOnSubscribe(disposable -> mRootView.showLoading())
                // .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                // .doFinally(() -> mRootView.hideLoading())
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, FragmentEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<EmptyEntity>(mErrorHandler) {
                    @Override
                    public void onNext(EmptyEntity entity) {
                    }
                });
    }
}
