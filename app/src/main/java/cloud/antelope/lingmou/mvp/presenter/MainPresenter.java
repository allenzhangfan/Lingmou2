package cloud.antelope.lingmou.mvp.presenter;

import android.app.Application;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.support.v4.app.Fragment;
import android.support.v4.app.SupportActivity;

import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;
import com.lingdanet.safeguard.common.utils.NetworkUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.List;

import cloud.antelope.lingmou.mvp.contract.MainContract;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraParentEntity;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;

import javax.inject.Inject;

import cloud.antelope.lingmou.app.utils.JPushUtils;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.model.entity.PushEntity;
import cloud.antelope.lingmou.mvp.model.entity.TokenEntity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;


@ActivityScope
public class MainPresenter extends BasePresenter<MainContract.Model, MainContract.View> {
    private RxErrorHandler mErrorHandler;
    private Application mApplication;
    private ImageLoader mImageLoader;
    private AppManager mAppManager;

    @Inject
    public MainPresenter(MainContract.Model model, MainContract.View rootView
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

    public void isReceiveMessage() {
        if (!NetworkUtils.isConnected()) {
            return;
        }

        mModel.getPushConfig()
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(1, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                // .doOnSubscribe(disposable -> {
                //     mRootView.showLoading();//显示下拉刷新的进度条
                // }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                // .doFinally(() -> {
                //     mRootView.hideLoading();//隐藏下拉刷新的进度条
                // })
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<List<PushEntity>>(mErrorHandler) {

                    @Override
                    public void onNext(List<PushEntity> pushEntities) {
                        if (null != pushEntities) {
                            for (PushEntity pushEntity : pushEntities) {
                                if (Constants.PUSH_TYPE_CLUE.equals(pushEntity.getMsgKey())) {
                                    String msgValue = pushEntity.getMsgValue();
                                    if (Constants.PUSH.equals(msgValue)) {
                                        SPUtils.getInstance().put(Constants.CONFIG_MESSAGE_SWITCH, true);
                                    } else {
                                        SPUtils.getInstance().put(Constants.CONFIG_MESSAGE_SWITCH, false);
                                    }
                                }
                            }
                        }
                        JPushUtils.switchJpush();
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        JPushUtils.switchJpush();
                    }
                });
    }

    public void getCyqzLyToken() {
        if (!NetworkUtils.isConnected()) {
            return;
        }

        mModel.getCyqzLyToken()
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
                .subscribe(new ErrorHandleSubscriber<TokenEntity>(mErrorHandler) {

                               @Override
                               public void onNext(TokenEntity tokenEntity) {
                                   SPUtils.getInstance().put(Constants.CONFIG_CYQZ_LY_TOKEN, tokenEntity.getToken());
                                   mRootView.onGetCyqzLyTokenSuccess();
                               }
                           });
    }

    public void getAllCameras() {
        if (!NetworkUtils.isConnected()) {
            return;
        }
        mModel.getAllCameras()
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                .observeOn(Schedulers.newThread())
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<OrgCameraParentEntity>(mErrorHandler) {
                    @Override
                    public void onNext(OrgCameraParentEntity entity) {
                        mRootView.getAllCamerasSuccess(entity);
                    }

                    @Override
                    public void onError(Throwable t) {

                    }
                });
    }

    /**
     * 上传经纬度
     * @param cameraId 用户信息里的cid
     * @param lat 纬度
     * @param lng 经度
     */
    /*public void uploadSolosLocations(String cameraId, double lat, double lng) {
        mModel.uploadSolosLocations(cameraId, lat, lng)
                .subscribeOn(Schedulers.io())
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<EmptyEntity>(mErrorHandler) {

                    @Override
                    public void onNext(EmptyEntity entity) {

                    }

                    @Override
                    public void onError(Throwable t) {
                        //删除掉Error
                    }
                });
    }*/
}
