package cloud.antelope.lingmou.mvp.presenter;

import android.app.Application;

import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;
import com.lingdanet.safeguard.common.utils.NetworkUtils;
import com.lingdanet.safeguard.common.utils.Utils;
import com.trello.rxlifecycle2.android.FragmentEvent;

import javax.inject.Inject;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.SuccessNullException;
import cloud.antelope.lingmou.mvp.contract.CollectedAlertListContract;
import cloud.antelope.lingmou.mvp.contract.DeployListContract;
import cloud.antelope.lingmou.mvp.model.entity.BaseListEntity;
import cloud.antelope.lingmou.mvp.model.entity.DailyPoliceAlarmEntity;
import cloud.antelope.lingmou.mvp.model.entity.DeployListRequest;
import cloud.antelope.lingmou.mvp.model.entity.DeployResponse;
import cloud.antelope.lingmou.mvp.model.entity.StartOrPauseDeployMissionRequest;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;

import static cloud.antelope.lingmou.mvp.ui.fragment.DeployListFragment.PAUSED;
import static cloud.antelope.lingmou.mvp.ui.fragment.DeployListFragment.UNDERWAY;


@FragmentScope
public class CollectedAlertListPresenter extends BasePresenter<CollectedAlertListContract.Model, CollectedAlertListContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public CollectedAlertListPresenter(CollectedAlertListContract.Model model, CollectedAlertListContract.View rootView) {
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

    public void getList(Integer pageNo, Integer pageSize,
                          String keyword, Integer taskType,
                          Integer alarmOperationType,
                          Integer alarmScope,
                          Long startTime, Long endTime) {
        if (!NetworkUtils.isConnected()) {
            mRootView.showMessage(Utils.getContext().getResources().getString(R.string.network_disconnect));
            return;
        }
        mModel.getList(pageNo, pageSize, keyword, taskType, alarmOperationType, alarmScope, startTime, endTime)
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
                        if (pageNo==1) {
                            mRootView.showList(listEntity.getList());
                        } else {
                            mRootView.showMore(listEntity.getList());
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        if (t instanceof SuccessNullException) {
                            mRootView.showList(null);
                        } else {
                            mRootView.onError();
                        }
                    }
                });
    }
}
