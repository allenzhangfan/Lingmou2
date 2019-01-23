package cloud.antelope.lingmou.mvp.presenter;

import android.app.Application;

import com.jess.arms.integration.AppManager;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.RxLifecycleUtils;
import com.lingdanet.safeguard.common.utils.NetworkUtils;
import com.lingdanet.safeguard.common.utils.Utils;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.ArrayList;
import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.NoPermissionException;
import cloud.antelope.lingmou.app.SuccessNullException;
import cloud.antelope.lingmou.mvp.model.entity.AlarmDepotEntity;
import cloud.antelope.lingmou.mvp.model.entity.EmptyEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceAlarmBlackEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceAlarmEntity;
import cloud.antelope.lingmou.mvp.model.entity.ListBaseBlackLibEntity;
import cloud.antelope.lingmou.mvp.model.entity.ListBaseEntity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;

import javax.inject.Inject;

import cloud.antelope.lingmou.mvp.contract.ClothControlAlarmContract;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;


@ActivityScope
public class ClothControlAlarmPresenter extends BasePresenter<ClothControlAlarmContract.Model, ClothControlAlarmContract.View> {
    private RxErrorHandler mErrorHandler;
    private Application mApplication;
    private ImageLoader mImageLoader;
    private AppManager mAppManager;

    @Inject
    public ClothControlAlarmPresenter(ClothControlAlarmContract.Model model, ClothControlAlarmContract.View rootView
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

    public void getFaceAlarm(Long startTimeStamp, Long endTimeStamp, int from, String effective, int size, String libIds, String cameraIds, String isOnlyAttention) {
        if (!NetworkUtils.isConnected()) {
            mRootView.showMessage(Utils.getContext().getResources().getString(R.string.network_disconnect));
            return;
        }
        mModel.getFaceAlarm(startTimeStamp, endTimeStamp, from, effective, size, libIds, cameraIds, isOnlyAttention)
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
                .subscribe(new ErrorHandleSubscriber<ListBaseEntity<List<FaceAlarmBlackEntity>>>(mErrorHandler) {
                    @Override
                    public void onNext(ListBaseEntity<List<FaceAlarmBlackEntity>> faceAlarmEntity) {
                        mRootView.getAlarmSuccess(faceAlarmEntity);
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        if (t instanceof SuccessNullException) {
                            mRootView.getAlarmSuccess(null);
                        }else if(t instanceof NoPermissionException){
                            mRootView.getNoPermission();
                        } else {
                            mRootView.getAlarmError();
                        }
                    }
                });
    }

    public void getDepots() {
        mModel.getDepots()
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(1, 2))
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))
                .subscribe(new ErrorHandleSubscriber<ListBaseBlackLibEntity<List<AlarmDepotEntity>>>(mErrorHandler) {

                    @Override
                    public void onNext(ListBaseBlackLibEntity<List<AlarmDepotEntity>> alarmDepotEntities) {
                        mRootView.getDepots(alarmDepotEntities);
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        if (t instanceof SuccessNullException) {
                            mRootView.getDepots(null);
                        }
                    }
                });
    }

    public void getDetailPermission(String id) {
        mModel.getAlarmDetailPermission(id)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(1, 2))
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))
                .subscribe(new ErrorHandleSubscriber<EmptyEntity>(mErrorHandler) {
                    @Override
                    public void onNext(EmptyEntity emptyEntity) {
                        mRootView.getDetailSuccess();
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        if (t instanceof NoPermissionException) {
                            mRootView.getDetailNoPermission();
                        }
                    }
                });
    }

}
