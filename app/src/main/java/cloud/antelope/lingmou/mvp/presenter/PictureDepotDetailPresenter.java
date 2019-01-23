package cloud.antelope.lingmou.mvp.presenter;

import android.app.Application;
import android.text.TextUtils;

import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;
import com.lingdanet.safeguard.common.net.TokenAbateException;
import com.lingdanet.safeguard.common.utils.NetworkUtils;
import com.lingdanet.safeguard.common.utils.Utils;
import com.trello.rxlifecycle2.android.ActivityEvent;

import javax.inject.Inject;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.SuccessNullException;
import cloud.antelope.lingmou.mvp.contract.PictureDepotDetailContract;
import cloud.antelope.lingmou.mvp.model.entity.BodyFeatureInfoEntity;
import cloud.antelope.lingmou.mvp.model.entity.BodyRecorgBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.CollectRequest;
import cloud.antelope.lingmou.mvp.model.entity.CollectResponse;
import cloud.antelope.lingmou.mvp.model.entity.CollectionListBean;
import cloud.antelope.lingmou.mvp.model.entity.CollectionListRequest;
import cloud.antelope.lingmou.mvp.model.entity.CommonListResponse;
import cloud.antelope.lingmou.mvp.model.entity.FaceFeatureInfoEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceNewEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceRecorgBaseEntity;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;


@ActivityScope
public class PictureDepotDetailPresenter extends BasePresenter<PictureDepotDetailContract.Model, PictureDepotDetailContract.View> {
    private RxErrorHandler mErrorHandler;
    private Application mApplication;
    private ImageLoader mImageLoader;
    private AppManager mAppManager;

    @Inject
    public PictureDepotDetailPresenter(PictureDepotDetailContract.Model model, PictureDepotDetailContract.View rootView
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
    public void getCollections(CollectionListRequest request) {
        mModel.getList(request)
                .observeOn(Schedulers.io())
                .doOnSubscribe(disposable -> {
                    mRootView.showLoading("");//显示下拉刷新的进度条
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {if(mRootView!=null)
                    mRootView.hideLoading();//隐藏下拉刷新的进度条
                })
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<CommonListResponse<CollectionListBean>>(mErrorHandler) {
                    @Override
                    public void onNext(CommonListResponse<CollectionListBean> response) {
                        mRootView.getCollectionsSuccess(response.getList());
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        mRootView.getCollectionsSuccess(null);
                    }
                });
    }

    public void getFaceList(String vid, Long startTime, Long endTime, int score) {
        if (!NetworkUtils.isConnected()) {
            mRootView.showMessage(Utils.getContext().getResources().getString(R.string.network_disconnect));
            return;
        }
        mModel.getFaceFeatureInfo(vid)
                .observeOn(Schedulers.io())
                .flatMap(new Function<FaceFeatureInfoEntity, ObservableSource<FaceRecorgBaseEntity<FaceNewEntity>>>() {
                    @Override
                    public ObservableSource<FaceRecorgBaseEntity<FaceNewEntity>> apply(FaceFeatureInfoEntity faceFeatureInfoEntity) throws Exception {
                        float[] faceFeature = faceFeatureInfoEntity.getFaceFeature();
                        if (null != faceFeature) {
                            mRootView.getFeatureSuccess(faceFeature);
                            return mModel.getFaceList(startTime, endTime, score, faceFeature, vid);
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(disposable -> {
                    mRootView.showLoading("");//显示下拉刷新的进度条
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {if(mRootView!=null)
                    mRootView.hideLoading();//隐藏下拉刷新的进度条
                })
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<FaceRecorgBaseEntity<FaceNewEntity>>(mErrorHandler) {
                    @Override
                    public void onNext(FaceRecorgBaseEntity<FaceNewEntity> entity) {
                        mRootView.getFaceListSuccess(entity.getFace());
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        if (!(t instanceof TokenAbateException)) {
                            mRootView.getFaceFail();
                        }
                    }
                });
    }


    public void getBodyList(String vid, Long startTime, Long endTime, int score) {
        if (!NetworkUtils.isConnected()) {
            mRootView.showMessage(Utils.getContext().getResources().getString(R.string.network_disconnect));
            return;
        }
        mModel.getBodyFeatureInfo(vid)
                .observeOn(Schedulers.io())
                .flatMap(new Function<BodyFeatureInfoEntity, ObservableSource<BodyRecorgBaseEntity<FaceNewEntity>>>() {
                    @Override
                    public ObservableSource<BodyRecorgBaseEntity<FaceNewEntity>> apply(BodyFeatureInfoEntity bodyFeatureInfoEntity) throws Exception {
                        float[] bodyFeature = bodyFeatureInfoEntity.getBodyFeature();
                        if (null != bodyFeature) {
                            mRootView.getFeatureSuccess(bodyFeature);
                            return mModel.getBodyList(startTime, endTime, score, bodyFeature, vid);
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(disposable -> {
                    mRootView.showLoading("");//显示下拉刷新的进度条
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {if(mRootView!=null)
                    mRootView.hideLoading();//隐藏下拉刷新的进度条
                })
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<BodyRecorgBaseEntity<FaceNewEntity>>(mErrorHandler) {
                    @Override
                    public void onNext(BodyRecorgBaseEntity<FaceNewEntity> entity) {
                        mRootView.getBodyListSuccess(entity.getBody());
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        if (!(t instanceof TokenAbateException)) {
                            mRootView.getFaceFail();
                        }
                    }
                });
    }
    public void collect(CollectRequest request,int position){
        mModel.collect(request)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(disposable -> {
                    mRootView.showLoading("");//显示下拉刷新的进度条
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {if(mRootView!=null)
                    mRootView.hideLoading();//隐藏下拉刷新的进度条
                })
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))
                .subscribe(new ErrorHandleSubscriber<CollectResponse>(mErrorHandler) {
                    @Override
                    public void onNext(CollectResponse response) {
                        mRootView.onCollectSuccess(position,response.id,TextUtils.isEmpty(request.getDeviceName()));
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        if (t instanceof SuccessNullException) {
                            mRootView.onCollectSuccess(position,null,TextUtils.isEmpty(request.getDeviceName()));
                        }
                    }
                });
    }
}
