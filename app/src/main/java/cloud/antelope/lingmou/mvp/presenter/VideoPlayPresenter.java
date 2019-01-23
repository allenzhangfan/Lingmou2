package cloud.antelope.lingmou.mvp.presenter;

import android.app.Application;

import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;
import com.lingdanet.safeguard.common.constant.CommonConstant;
import com.lingdanet.safeguard.common.utils.NetworkUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.Utils;
import com.trello.rxlifecycle2.android.ActivityEvent;

import javax.inject.Inject;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.SuccessNullException;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.contract.VideoPlayContract;
import cloud.antelope.lingmou.mvp.model.entity.CameraNewStreamEntity;
import cloud.antelope.lingmou.mvp.model.entity.CollectRequest;
import cloud.antelope.lingmou.mvp.model.entity.CollectResponse;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.HistoryKVStoreRequest;
import cloud.antelope.lingmou.mvp.model.entity.ObjectEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;
import cloud.antelope.lingmou.mvp.model.entity.PlayEntity;
import cloud.antelope.lingmou.mvp.model.entity.RecordLyCameraBean;
import cloud.antelope.lingmou.mvp.model.entity.SetKeyStoreRequest;
import cloud.antelope.lingmou.mvp.model.entity.TokenEntity;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


@ActivityScope
public class VideoPlayPresenter extends BasePresenter<VideoPlayContract.Model, VideoPlayContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public VideoPlayPresenter(VideoPlayContract.Model model, VideoPlayContract.View rootView) {
        super(model, rootView);
    }
    String token, picPath;
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mImageLoader = null;
        this.mApplication = null;
    }

    public void getStreamUrl(Long cameraId) {
        if (!NetworkUtils.isConnected()) {
            mRootView.showMessage(Utils.getContext().getResources().getString(R.string.network_disconnect));
            return;
        }
        Observable.zip(mModel.getLyyToken(cameraId), mModel.getPlayerUrl(cameraId), new BiFunction<String, CameraNewStreamEntity, PlayEntity>() {
            @Override
            public PlayEntity apply(String token, CameraNewStreamEntity camerasStreamEntity) throws Exception {
                PlayEntity playEntity = new PlayEntity();
                playEntity.setToken(token);
                playEntity.setStreamEntity(camerasStreamEntity);
                return playEntity;
            }
        }).observeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(1, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                .doOnSubscribe(disposable -> {
                    mRootView.showLoading("");//显示下拉刷新的进度条
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {if(mRootView!=null)
                    mRootView.hideLoading();//隐藏下拉刷新的进度条
                })
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<PlayEntity>(mErrorHandler) {
                    @Override
                    public void onNext(PlayEntity playEntity) {
                        mRootView.getStreamSuccess(playEntity);
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        mRootView.getStreamFailed();
                    }
                });

    }

    public void cameraLike(String storeKey, SetKeyStoreRequest keyStoreRequest) {
        if (!NetworkUtils.isConnected()) {
            mRootView.showMessage(Utils.getContext().getResources().getString(R.string.network_disconnect));
            return;
        }

        mModel.cameraLike(storeKey, keyStoreRequest).subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(1, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                .doOnSubscribe(disposable -> {
                    mRootView.showLoading("");//显示下拉刷新的进度条
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {if(mRootView!=null)
                    mRootView.hideLoading();//隐藏下拉刷新的进度条
                })
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<GetKeyStoreBaseEntity>(mErrorHandler) {

                    @Override
                    public void onNext(GetKeyStoreBaseEntity entity) {
                        mRootView.onCameraLikeSuccess(entity);
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        mRootView.onCameraLikeFail();
                    }
                });
    }

    public void getRecords(Long cid, long start, long end) {
        if (!NetworkUtils.isConnected()) {
            mRootView.showMessage(Utils.getContext().getResources().getString(R.string.network_disconnect));
            return;
        }
        mModel.getLyyToken(cid)
                .observeOn(Schedulers.io())
                .flatMap(new Function<String, ObservableSource<RecordLyCameraBean>>() {
                    @Override
                    public ObservableSource<RecordLyCameraBean> apply(String token) throws Exception {
                        mRootView.getRecordToken(token);
                        return mModel.getRecordTimes(cid+"", token, start, end);
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
                .subscribe(new ErrorHandleSubscriber<RecordLyCameraBean>(mErrorHandler) {
                    @Override
                    public void onNext(RecordLyCameraBean recordSection) {
                        mRootView.getRecordTimeSuccess(recordSection);
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        mRootView.getRecordTimeFail();
                    }
                });
    }

    public void getHistories() {
        if (!NetworkUtils.isConnected()) {
            mRootView.showMessage(Utils.getContext().getResources().getString(R.string.network_disconnect));
            return;
        }
        String userId = SPUtils.getInstance().getString(CommonConstant.UID);
        mModel.getHistories(userId)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(1, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                .observeOn(AndroidSchedulers.mainThread())   // 不涉及UI操作，将回调放到后台线程处理
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<GetKeyStoreBaseEntity>(mErrorHandler) {

                    @Override
                    public void onNext(GetKeyStoreBaseEntity entity) {
                        mRootView.getHistoriesSuccess(entity);
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        if (t instanceof SuccessNullException) {
                            mRootView.getHistoriesSuccess(null);
                        } else {
                            mRootView.getHistoriesFail();
                        }
                    }
                });
    }

    public void putHistories(String storeKey, HistoryKVStoreRequest request) {
        if (!NetworkUtils.isConnected()) {
            mRootView.showMessage(Utils.getContext().getResources().getString(R.string.network_disconnect));
            request.recycle();
            return;
        }
        mModel.putHistories(storeKey, request)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(1, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<GetKeyStoreBaseEntity>(mErrorHandler) {

                    @Override
                    public void onNext(GetKeyStoreBaseEntity entity) {
                        // keep empty
                        mRootView.putHistoriesSuccess();
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                    }
                });
    }

    public void getDeviceInfo(String cid) {
        if (!NetworkUtils.isConnected()) {
            mRootView.showMessage(Utils.getContext().getResources().getString(R.string.network_disconnect));
            return;
        }
        mModel.getDeviceInfo(cid)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(1, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<OrgCameraEntity>(mErrorHandler) {

                    @Override
                    public void onNext(OrgCameraEntity entity) {
                        mRootView.getDeviceInfoSuccess(entity);
                    }
                });
    }

    public void collect(CollectRequest request, String fileLength, RequestBody requestBody, MultipartBody.Part part) {
        mModel.getStorageToken()
                .observeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .flatMap((Function<TokenEntity, ObservableSource<ObjectEntity>>) tokenEntity -> {
                    token = tokenEntity.getToken();
                    return mModel.uploadPic(tokenEntity.getToken(), fileLength, requestBody, part);
                })
                .flatMap((Function<ObjectEntity, ObservableSource<CollectResponse>>) objectEntity -> {
                    String objUrl = SPUtils.getInstance().getString(Constants.URL_OBJECT_STORAGE);
                    picPath = objUrl + "/files?obj_id=" + objectEntity.getObjId() + "&access_token=" + token;
                    request.setImageUrl(picPath);
                    request.setFaceImgUrl(picPath);
                    return mModel.collect(request);
                })
                .doOnSubscribe(disposable -> {
                    mRootView.showLoading("");//显示下拉刷新的进度条
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {if(mRootView!=null)
                    mRootView.hideLoading();//隐藏下拉刷新的进度条
                })
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<CollectResponse>(mErrorHandler) {
                    @Override
                    public void onNext(CollectResponse id) {
                        mRootView.onCollectSuccess();
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        if (t instanceof SuccessNullException) {
                            mRootView.onCollectSuccess();
                        }
                    }
                });
    }
}
