package cloud.antelope.lingmou.mvp.presenter;

import android.app.Application;

import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;
import com.lingdanet.safeguard.common.constant.CommonConstant;
import com.lingdanet.safeguard.common.net.TokenAbateException;
import com.lingdanet.safeguard.common.utils.NetworkUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.Utils;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.List;

import javax.inject.Inject;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.NoPermissionException;
import cloud.antelope.lingmou.app.SuccessNullException;
import cloud.antelope.lingmou.mvp.contract.FaceDepotContract;
import cloud.antelope.lingmou.mvp.model.entity.FaceDepotEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceFeatureInfoEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceNewEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceRecorgBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreBaseEntity;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;


@ActivityScope
public class FaceDepotPresenter extends BasePresenter<FaceDepotContract.Model, FaceDepotContract.View> {
    private RxErrorHandler mErrorHandler;
    private Application mApplication;
    private ImageLoader mImageLoader;
    private AppManager mAppManager;

    @Inject
    public FaceDepotPresenter(FaceDepotContract.Model model, FaceDepotContract.View rootView
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

    public void getFaceDepot(Long tartTimeStamp, Long EndTimeStamp, int size, List<String> cameraTags,List<String> noCameraTags,
                             List<String> faceTags, List<String> emptyTags, List<String> cameraIds, String minId,
                             String minCaptureTime, String pageType) {
        if (!NetworkUtils.isConnected()) {
            mRootView.showMessage(Utils.getContext().getResources().getString(R.string.network_disconnect));
            return;
        }
        mModel.getFaceDepot(tartTimeStamp, EndTimeStamp,  size, cameraTags,noCameraTags, faceTags, emptyTags, cameraIds, minId, minCaptureTime, pageType)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(1, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                .doOnSubscribe(disposable -> {
                    mRootView.showLoading("");//显示下拉刷新的进度条
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {if(mRootView!=null)
                    mRootView.hideLoading();//隐藏下拉刷新的进度条
                })
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<FaceDepotEntity>(mErrorHandler) {
                    @Override
                    public void onNext(FaceDepotEntity faceDepotEntity) {
                        mRootView.getFaceDepotSuccess(faceDepotEntity);
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        if (t instanceof SuccessNullException) {
                            mRootView.getFaceDepotSuccess(new FaceDepotEntity());
                        }else if(t instanceof NoPermissionException){
                            mRootView.getNoPermission();
                        } else {
                            mRootView.getFaceError();
                        }
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
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
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
                        } else {
                            mRootView.getCollectionsFail();
                        }
                    }
                });

    }

}
