package cloud.antelope.lingmou.mvp.presenter;

import android.app.Activity;
import android.app.Application;
import android.text.TextUtils;

import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;
import com.lingdanet.safeguard.common.net.ApiException;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.List;

import javax.inject.Inject;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.contract.NewDeployMissionContract;
import cloud.antelope.lingmou.mvp.model.entity.FaceUrlSearchBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceUrlSearchEntity;
import cloud.antelope.lingmou.mvp.model.entity.NewDeployMissionRequest;
import cloud.antelope.lingmou.mvp.model.entity.ObjectEntity;
import cloud.antelope.lingmou.mvp.model.entity.TokenEntity;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


@ActivityScope
public class NewDeployMissionPresenter extends BasePresenter<NewDeployMissionContract.Model, NewDeployMissionContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;
    String token, picPath;

    @Inject
    public NewDeployMissionPresenter(NewDeployMissionContract.Model model, NewDeployMissionContract.View rootView) {
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

    public void create(String facePath, String fileLength, RequestBody requestBody, MultipartBody.Part part, String name, String startTime, String endTime, String alarmThreshold, String describe) {
        Observable<String> observable ;
        if (!TextUtils.isEmpty(facePath)) {
            observable = mModel.getFeature(facePath)
                    .observeOn(Schedulers.io())
                    .retryWhen(new RetryWithDelay(3, 2))
                    .flatMap((Function<FaceUrlSearchBaseEntity<FaceUrlSearchEntity>, ObservableSource<String>>) baseEntity -> {
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
                                mRootView.onUploadPictureSuccess(facePath);
                                return mModel.createDeployMission(new NewDeployMissionRequest(name, startTime, endTime, alarmThreshold, describe, facePath, feature));
                            }
                        }
                        return Observable.error(new ApiException(((Activity) mRootView).getString(R.string.error_obtain_face_feature)));
                    });
        } else {
            observable = mModel.getStorageToken()
                    .observeOn(Schedulers.io())
                    .retryWhen(new RetryWithDelay(3, 2))
                    .flatMap((Function<TokenEntity, ObservableSource<ObjectEntity>>) tokenEntity -> {
                        token = tokenEntity.getToken();
                        return mModel.uploadPic(tokenEntity.getToken(), fileLength, requestBody, part);
                    })
                    .flatMap((Function<ObjectEntity, ObservableSource<FaceUrlSearchBaseEntity<FaceUrlSearchEntity>>>) objectEntity -> {
                        String objUrl = SPUtils.getInstance().getString(Constants.URL_OBJECT_STORAGE);
                        picPath = objUrl + "/files?obj_id=" + objectEntity.getObjId() + "&access_token=" + token;
                        mRootView.onUploadPictureSuccess(picPath);
                        return mModel.getFeature(picPath);
                    })
                    .flatMap((Function<FaceUrlSearchBaseEntity<FaceUrlSearchEntity>, ObservableSource<String>>) baseEntity -> {
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
                                return mModel.createDeployMission(new NewDeployMissionRequest(name, startTime, endTime, alarmThreshold, describe, picPath, feature));
                            }
                        }
                        return Observable.error(new ApiException(((Activity) mRootView).getString(R.string.error_obtain_face_feature)));
                    });

        }
        observable.doOnSubscribe(disposable -> {
            mRootView.showLoading("");//显示下拉刷新的进度条
        })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {if(mRootView!=null)
                    mRootView.hideLoading();//隐藏下拉刷新的进度条
                })
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<String>(mErrorHandler) {
                    @Override
                    public void onNext(String id) {
                        mRootView.onCreateSuccess(id);
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        ToastUtils.showLong(t.getMessage());
                    }
                });
    }

}
