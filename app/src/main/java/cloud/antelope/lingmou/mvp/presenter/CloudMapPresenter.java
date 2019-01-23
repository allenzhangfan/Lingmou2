package cloud.antelope.lingmou.mvp.presenter;

import android.app.Application;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;

import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;
import com.lingdanet.safeguard.common.constant.CommonConstant;
import com.lingdanet.safeguard.common.utils.NetworkUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.Utils;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import javax.inject.Inject;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.SuccessNullException;
import cloud.antelope.lingmou.app.utils.JPushUtils;
import cloud.antelope.lingmou.app.utils.SaveUtils;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.contract.CloudMapContract;
import cloud.antelope.lingmou.mvp.model.entity.CameraNewStreamEntity;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraParentEntity;
import cloud.antelope.lingmou.mvp.model.entity.SetKeyStoreRequest;
import cloud.antelope.lingmou.mvp.ui.activity.LoginActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;

import static com.jess.arms.utils.ArmsUtils.startActivity;


@FragmentScope
public class CloudMapPresenter extends BasePresenter<CloudMapContract.Model, CloudMapContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public CloudMapPresenter(CloudMapContract.Model model, CloudMapContract.View rootView) {
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

    public void getAllCameras() {
        if (!NetworkUtils.isConnected()) {
            mRootView.showMessage(Utils.getContext().getResources().getString(R.string.network_disconnect));
            mRootView.hideLoading();
            mRootView.getAllCamerasSuccess(null);
            return;
        }
        if (TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.URL_BASE))) {
            mRootView.hideLoading();
            mRootView.getAllCamerasSuccess(null);
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
        mModel.getAllCameras()
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, FragmentEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<OrgCameraParentEntity>(mErrorHandler) {
                    @Override
                    public void onNext(OrgCameraParentEntity entity) {
                        mRootView.getAllCamerasSuccess(entity);
                    }

                    @Override
                    public void onError(Throwable t) {
                        mRootView.getAllCamerasSuccess(null);
                    }
                });
    }

    public void cameraLike(String storeKey, SetKeyStoreRequest keyStoreRequest, boolean like) {
        if (!NetworkUtils.isConnected()) {
            mRootView.showMessage(Utils.getContext().getResources().getString(R.string.network_disconnect));
            return;
        }

        mModel.cameraLike(storeKey, keyStoreRequest)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(1, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                .doOnSubscribe(disposable -> {
                    mRootView.showLoading(Utils.getContext().getString(R.string.likeCamera));//显示下拉刷新的进度条
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                    if (mRootView != null)
                        mRootView.hideLoading();//隐藏下拉刷新的进度条
                })
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, FragmentEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<GetKeyStoreBaseEntity>(mErrorHandler) {

                    @Override
                    public void onNext(GetKeyStoreBaseEntity entity) {
                        mRootView.onCameraLikeSuccess(entity, like);
                    }
                });


    }

    public void getCameraCovers(Long[] cids) {
        mModel.getCameraCovers(cids)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, FragmentEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<CameraNewStreamEntity>(mErrorHandler) {
                    @Override
                    public void onNext(CameraNewStreamEntity entity) {
                        for (CameraNewStreamEntity.DevicesBean bean : entity.getDevices()) {
                            bean.setCover_url(bean.getCover_url() + "&width=256.0&height=144.0");
                        }
                        mRootView.getCoversSuccess(entity);
                    }
                });
    }

    public void getCollections() {
        if (!NetworkUtils.isConnected()) {
            mRootView.showMessage(Utils.getContext().getResources().getString(R.string.network_disconnect));
            return;
        }
        String userId = SPUtils.getInstance().getString(CommonConstant.UID);
        mModel.getCollections(userId)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(1, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                .doOnSubscribe(disposable -> {
                    mRootView.showLoading("");//显示下拉刷新的进度条
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                    if (null != mRootView) {
                        mRootView.hideLoading();//隐藏下拉刷新的进度条}
                    }
                })
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, FragmentEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<GetKeyStoreBaseEntity>(mErrorHandler) {

                    @Override
                    public void onNext(GetKeyStoreBaseEntity entity) {
                        mRootView.getCollectionsSuccess(entity);
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        if (t instanceof SuccessNullException) {
                            mRootView.getCollectionsSuccess(null);
                        }
                    }
                });

    }
}
