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
import com.lingdanet.safeguard.common.net.ApiException;
import com.lingdanet.safeguard.common.utils.NetworkUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.Utils;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.ArrayList;

import javax.inject.Inject;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.JPushUtils;
import cloud.antelope.lingmou.app.utils.SaveUtils;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.contract.SearchListContract;
import cloud.antelope.lingmou.mvp.model.entity.BaseListEntity;
import cloud.antelope.lingmou.mvp.model.entity.BodyDepotEntity;
import cloud.antelope.lingmou.mvp.model.entity.DailyPoliceAlarmEntity;
import cloud.antelope.lingmou.mvp.model.entity.DeviceIdListRequest;
import cloud.antelope.lingmou.mvp.model.entity.FaceDepotEntity;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.LmBaseResponseEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;
import cloud.antelope.lingmou.mvp.model.entity.SearchBean;
import cloud.antelope.lingmou.mvp.model.entity.SetKeyStoreRequest;
import cloud.antelope.lingmou.mvp.ui.activity.LoginActivity;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;

import static com.jess.arms.utils.ArmsUtils.startActivity;


@FragmentScope
public class SearchListPresenter extends BasePresenter<SearchListContract.Model, SearchListContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public SearchListPresenter(SearchListContract.Model model, SearchListContract.View rootView) {
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

    public void cameraLike(String storeKey, SetKeyStoreRequest keyStoreRequest, boolean like) {
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

        mModel.cameraLike(storeKey, keyStoreRequest).subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(1, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                .doOnSubscribe(disposable -> {
                    mRootView.showLoading(Utils.getContext().getString(R.string.likeCamera));//显示下拉刷新的进度条
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {if(mRootView!=null)
                    mRootView.hideLoading();//隐藏下拉刷新的进度条
                })
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, FragmentEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<GetKeyStoreBaseEntity>(mErrorHandler) {

                    @Override
                    public void onNext(GetKeyStoreBaseEntity entity) {
                        mRootView.onCameraLikeSuccess(entity,like);
                    }
                });


    }

    public void searchDevices(Integer deviceState, Integer deviceType, String deviceName, Integer pageNo, Integer pageSize) {
        if (!NetworkUtils.isConnected()) {
            mRootView.showMessage(Utils.getContext().getResources().getString(R.string.network_disconnect));
            return;
        }
        mModel.searchDevices(deviceState, deviceType, deviceName, pageNo, pageSize)
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
                .subscribe(new ErrorHandleSubscriber<BaseListEntity<OrgCameraEntity>>(mErrorHandler) {

                    @Override
                    public void onNext(BaseListEntity<OrgCameraEntity> listEntity) {
                        ArrayList<SearchBean> list = new ArrayList<>();
                        for(OrgCameraEntity entity:listEntity.getList()){
                            entity.setCoverUrl(entity.getCoverUrl()+"&width=336.0&height=188.0");
                        }
                        list.addAll(listEntity.getList());
                        if (pageNo == 1) {
                            mRootView.showList(list);
                        } else {
                            mRootView.showMore(list);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        mRootView.onError();
                    }
                });
    }

    public void searchAlarms(Integer pageNo, Integer pageSize,
                             String keyword, Integer taskType,
                             Integer alarmOperationType,
                             Integer alarmScope,
                             Long startTime, Long endTime) {
        if (!NetworkUtils.isConnected()) {
            mRootView.showMessage(Utils.getContext().getResources().getString(R.string.network_disconnect));
            return;
        }
        mModel.getAlarms(pageNo, pageSize, keyword, taskType, alarmOperationType, alarmScope, startTime, endTime)
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
                .subscribe(new ErrorHandleSubscriber<BaseListEntity<DailyPoliceAlarmEntity>>(mErrorHandler) {

                    @Override
                    public void onNext(BaseListEntity<DailyPoliceAlarmEntity> listEntity) {
                        ArrayList<SearchBean> list = new ArrayList<>();
                        list.addAll(listEntity.getList());
                        if (pageNo == 1) {
                            mRootView.showList(list);
                        } else {
                            mRootView.showMore(list);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        mRootView.onError();
                    }
                });
    }

    public void searchFace(String keyword, Integer pageNum, Integer pageSize,String minId,
                           String minCaptureTime, String pageType) {
        mModel.getDeviceIdList(new DeviceIdListRequest(keyword))
                .observeOn(Schedulers.io())
                .flatMap(new Function<LmBaseResponseEntity<ArrayList<String>>, ObservableSource<FaceDepotEntity>>() {
                    @Override
                    public ObservableSource<FaceDepotEntity> apply(LmBaseResponseEntity<ArrayList<String>> entity) throws Exception {
                        if (entity != null && entity.code == 200 && !entity.result.isEmpty()) {
                            return mModel.getFaceDepot(null, null, pageSize, null, null, null, entity.result, minId, minCaptureTime, pageType);
                        } else if (entity.code != 200) {
                            return observer -> {
                                observer.onError(new ApiException(entity.message));
                                observer.onComplete();
                            };
                        } else {
                            return observer -> {
                                FaceDepotEntity faceDepotEntity = new FaceDepotEntity();
                                faceDepotEntity.face = new ArrayList<>();
                                observer.onNext(faceDepotEntity);
                                observer.onComplete();
                            };
                        }
                    }
                })
                .doOnSubscribe(disposable -> {
                    mRootView.showLoading("");//显示下拉刷新的进度条
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, FragmentEvent.DESTROY))
                .doFinally(() -> {
                    if (mRootView != null)
                        mRootView.hideLoading();//隐藏下拉刷新的进度条
                })
                .subscribe(new ErrorHandleSubscriber<FaceDepotEntity>(mErrorHandler) {
                    @Override
                    public void onNext(FaceDepotEntity faceDepotEntity) {
                        ArrayList<SearchBean> list = new ArrayList<>();
                        list.addAll(faceDepotEntity.face);
                        if (pageNum == 1) {
                            mRootView.showList(list);
                        } else {
                            mRootView.showMore(list);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        mRootView.onError();
                    }
                });
    }

    public void searchBody(String keyword, Integer pageNum, Integer pageSize,String minId,
                           String minCaptureTime, String pageType) {
        mModel.getDeviceIdList(new DeviceIdListRequest(keyword))
                .observeOn(Schedulers.io())
                .flatMap(new Function<LmBaseResponseEntity<ArrayList<String>>, ObservableSource<BodyDepotEntity>>() {
                    @Override
                    public ObservableSource<BodyDepotEntity> apply(LmBaseResponseEntity<ArrayList<String>> entity) throws Exception {
                        if (entity != null && entity.code == 200 && !entity.result.isEmpty()) {
                            return mModel.getBodyDepot(pageSize, null, null, null, null, entity.result, minId, minCaptureTime, pageType);
                        } else if (entity.code != 200) {
                            return observer -> {
                                observer.onError(new ApiException(entity.message));
                                observer.onComplete();
                            };
                        } else {
                            return observer -> {
                                BodyDepotEntity bodyDepotEntity = new BodyDepotEntity();
                                bodyDepotEntity.body = new ArrayList<>();
                                observer.onNext(bodyDepotEntity);
                                observer.onComplete();
                            };
                        }
                    }
                })
                .doOnSubscribe(disposable -> {
                    mRootView.showLoading("");//显示下拉刷新的进度条
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, FragmentEvent.DESTROY))
                .doFinally(() -> {
                    if (mRootView != null)
                        mRootView.hideLoading();//隐藏下拉刷新的进度条
                })
                .subscribe(new ErrorHandleSubscriber<BodyDepotEntity>(mErrorHandler) {
                    @Override
                    public void onNext(BodyDepotEntity bodyDepotEntity) {
                        ArrayList<SearchBean> list = new ArrayList<>();
                        list.addAll(bodyDepotEntity.body);
                        if (pageNum == 1) {
                            mRootView.showList(list);
                        } else {
                            mRootView.showMore(list);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        mRootView.onError();
                    }
                });
    }
}
