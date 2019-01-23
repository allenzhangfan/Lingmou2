package cloud.antelope.lingmou.mvp.presenter;

import android.app.Application;

import com.jess.arms.integration.AppManager;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.RxLifecycleUtils;
import com.lingdanet.safeguard.common.constant.CommonConstant;
import com.lingdanet.safeguard.common.utils.NetworkUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.Utils;
import com.trello.rxlifecycle2.android.ActivityEvent;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.SuccessNullException;
import cloud.antelope.lingmou.mvp.model.entity.BaseListEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;

import javax.inject.Inject;

import cloud.antelope.lingmou.mvp.contract.RealVideosListContract;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;


@ActivityScope
public class RealVideosListPresenter extends BasePresenter<RealVideosListContract.Model, RealVideosListContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public RealVideosListPresenter(RealVideosListContract.Model model, RealVideosListContract.View rootView) {
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


    public void getCameraCovers(Integer deviceState, Integer deviceType, String deviceName, Integer pageNo, Integer pageSize) {
        if (!NetworkUtils.isConnected()) {
            mRootView.showMessage(Utils.getContext().getResources().getString(R.string.network_disconnect));
            return;
        }
        mModel.getAllCameras(deviceState, deviceType, deviceName, pageNo, pageSize)
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
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<BaseListEntity<OrgCameraEntity>>(mErrorHandler) {

                    @Override
                    public void onNext(BaseListEntity<OrgCameraEntity> listEntity) {
                        if (null != listEntity) {
                            for(OrgCameraEntity entity:listEntity.getList()){
                                entity.setCoverUrl(entity.getCoverUrl()+"&width=256.0&height=144.0");
                            }
                            mRootView.getAllCamerasSuccess(listEntity.getList());
                        } else {
                            mRootView.getAllCamerasSuccess(null);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        if (t instanceof SuccessNullException) {
                            mRootView.getAllCamerasSuccess(null);
                        } else {
                            mRootView.getAllCameraFail();
                        }
                    }
                });
    }
}
