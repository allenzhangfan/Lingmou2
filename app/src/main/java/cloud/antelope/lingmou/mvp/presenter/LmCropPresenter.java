package cloud.antelope.lingmou.mvp.presenter;

import android.app.Application;
import android.text.TextUtils;

import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;
import com.lingdanet.safeguard.common.net.ApiException;
import com.lingdanet.safeguard.common.net.TokenAbateException;
import com.lingdanet.safeguard.common.utils.NetworkUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.Utils;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.List;

import javax.inject.Inject;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.LogUploadUtil;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.contract.LmCropContract;
import cloud.antelope.lingmou.mvp.model.entity.FaceNewEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceRecorgBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceUrlSearchBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceUrlSearchEntity;
import cloud.antelope.lingmou.mvp.model.entity.LogUploadRequest;
import cloud.antelope.lingmou.mvp.model.entity.ObjectEntity;
import cloud.antelope.lingmou.mvp.model.entity.TokenEntity;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


@ActivityScope
public class LmCropPresenter extends BasePresenter<LmCropContract.Model, LmCropContract.View> {
    private RxErrorHandler mErrorHandler;
    private Application mApplication;
    private ImageLoader mImageLoader;
    private AppManager mAppManager;

    @Inject
    public LmCropPresenter(LmCropContract.Model model, LmCropContract.View rootView
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

    public void uploadFile(String fileLength, RequestBody requestBody, MultipartBody.Part part, Long startTime, Long endTime) {
        if (!NetworkUtils.isConnected()) {
            mRootView.showMessage(Utils.getContext().getResources().getString(R.string.network_disconnect));
            return;
        }
        final String[] token = {""};
        mModel.getStorageToken()
                .observeOn(Schedulers.io())
                .flatMap(new Function<TokenEntity, ObservableSource<ObjectEntity>>() {
                    @Override
                    public ObservableSource<ObjectEntity> apply(TokenEntity tokenEntity) throws Exception {
                        token[0] = tokenEntity.getToken();
                        return mModel.uploadFile(token[0], fileLength, requestBody, part);
                    }
                })
                .flatMap(new Function<ObjectEntity, ObservableSource<FaceUrlSearchBaseEntity<FaceUrlSearchEntity>>>() {
                    @Override
                    public ObservableSource<FaceUrlSearchBaseEntity<FaceUrlSearchEntity>> apply(ObjectEntity objectEntity) throws Exception {
                        String objUrl = SPUtils.getInstance().getString(Constants.URL_OBJECT_STORAGE);
                        String avatarUrl = objUrl + "/files?obj_id=" + objectEntity.getObjId() + "&access_token=" + token[0];
                        LogUploadUtil.uploadLog(new LogUploadRequest(105700,105701,String.format("以图搜图【上传】，图片URL:%s",avatarUrl)));
                        return mModel.getFaceFeature(avatarUrl);
                    }
                }).flatMap(new Function<FaceUrlSearchBaseEntity<FaceUrlSearchEntity>, ObservableSource<FaceRecorgBaseEntity<FaceNewEntity>>>() {
            @Override
            public ObservableSource<FaceRecorgBaseEntity<FaceNewEntity>> apply(FaceUrlSearchBaseEntity<FaceUrlSearchEntity> baseEntity) throws Exception {
                if (null != baseEntity && null != baseEntity.getImgsList() && !baseEntity.getImgsList().isEmpty()) {
                    List<FaceUrlSearchEntity> imgsList = baseEntity.getImgsList();
                    String feature = null;
                    if (null != imgsList) {
                        for (FaceUrlSearchEntity urlSearchEntity : imgsList) {
                            if (null != urlSearchEntity) {
                                FaceUrlSearchEntity.FaceBean face = urlSearchEntity.getFace();
                                if (face != null) {
                                    feature = face.getFeature();
                                    break;
                                }
                            }
                        }
                    }
                    if (!TextUtils.isEmpty(feature)) {
                        mRootView.onGetFeatureSuccess(feature);
                        return mModel.getFaceListByFeature(startTime, endTime, 85, feature);
                    }
                }
                mRootView.onFaceFail(Utils.getContext().getString(R.string.error_obtain_face_feature));
                return Observable.error(new ApiException(""));

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
                        mRootView.onFaceSuccess(entity.getFace());
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        if (!(t instanceof TokenAbateException) && !(t instanceof ApiException)) {
                            mRootView.onFaceFail(Utils.getContext().getString(R.string.error_face_recog_null));
                        }
                    }
                });

    }


}
