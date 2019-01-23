package cloud.antelope.lingmou.mvp.presenter;

import android.app.Application;

import com.jess.arms.integration.AppManager;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.RxLifecycleUtils;
import com.lingdanet.safeguard.common.net.TokenAbateException;
import com.lingdanet.safeguard.common.utils.NetworkUtils;
import com.lingdanet.safeguard.common.utils.Utils;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.model.entity.BodyFeatureInfoEntity;
import cloud.antelope.lingmou.mvp.model.entity.BodyRecorgBaseEntity;
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

import javax.inject.Inject;

import cloud.antelope.lingmou.mvp.contract.FaceDepotDetailContract;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;


@ActivityScope
public class FaceDepotDetailPresenter extends BasePresenter<FaceDepotDetailContract.Model, FaceDepotDetailContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public FaceDepotDetailPresenter(FaceDepotDetailContract.Model model, FaceDepotDetailContract.View rootView) {
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
                        mRootView.getFaceListSuccess(entity.getBody());
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

    public void getCollectionList(CollectionListRequest request) {
        mModel.getCollectionList(request)
                .observeOn(Schedulers.io())
                .doOnSubscribe(disposable -> {
                    mRootView.showLoading("");//显示下拉刷新的进度条
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                    if (mRootView != null)
                        mRootView.hideLoading();//隐藏下拉刷新的进度条
                })
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<CommonListResponse<CollectionListBean>>(mErrorHandler) {
                    @Override
                    public void onNext(CommonListResponse<CollectionListBean> response) {
                        mRootView.getCollectionListSuccess(response.getList());
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                    }
                });
    }
}
