package cloud.antelope.lingmou.mvp.presenter;

import android.app.Application;
import android.text.TextUtils;

import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;
import com.lingdanet.safeguard.common.utils.NetworkUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.Utils;
import com.trello.rxlifecycle2.android.ActivityEvent;

import javax.inject.Inject;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.SuccessNullException;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.contract.MyReportContract;
import cloud.antelope.lingmou.mvp.model.entity.ClueItemEntity;
import cloud.antelope.lingmou.mvp.model.entity.ContentListEntity;
import cloud.antelope.lingmou.mvp.model.entity.OperationEntity;
import cloud.antelope.lingmou.mvp.model.entity.TokenEntity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;


@ActivityScope
public class MyReportPresenter extends BasePresenter<MyReportContract.Model, MyReportContract.View> {
    private RxErrorHandler mErrorHandler;
    private Application mApplication;
    private ImageLoader mImageLoader;
    private AppManager mAppManager;

    @Inject
    public MyReportPresenter(MyReportContract.Model model, MyReportContract.View rootView
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


    public void getCyqzConfig() {
        if (!NetworkUtils.isConnected()) {
            mRootView.showMessage(Utils.getContext().getResources()
                    .getString(R.string.network_disconnect));
            return;
        }
        mModel.getCyqzConfig()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(disposable -> {
                    mRootView.showLoading("");//显示下拉刷新的进度条
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                    if (null != mRootView) {
                        mRootView.hideLoading();//隐藏下拉刷新的进度条}
                    }
                })
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .subscribe(new ErrorHandleSubscriber<OperationEntity>(mErrorHandler) {
                    @Override
                    public void onNext(OperationEntity operationEntity) {
                        SPUtils.getInstance().put(Constants.CONFIG_CASE_ID, operationEntity.getCaseColumnId());
                        SPUtils.getInstance().put(Constants.CONFIG_CLUE_ID, operationEntity.getTipColumnId());
                        SPUtils.getInstance().put(Constants.CONFIG_OPERATION_ID, operationEntity.getOperationCenterId());
                        mRootView.onGetCyqzConfigSuccess();
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        mRootView.onGetCyqzConfigError("");
                    }
                });
    }

    public void getColumnLastUpdateTime(String columnId) {
        if (!NetworkUtils.isConnected()) {
            mRootView.showMessage(Utils.getContext().getResources().getString(R.string.network_disconnect));
            return;
        }

        mModel.getColumnLastUpdateTime(columnId)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 1))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                // .doOnSubscribe(disposable -> {
                //     mRootView.showLoading();//显示下拉刷新的进度条
                // }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                // .doFinally(() -> {
                //     mRootView.hideLoading();//隐藏下拉刷新的进度条
                // })
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<Long>(mErrorHandler) {

                    @Override
                    public void onNext(Long time) {
                        mRootView.onGetColumnLastUpdateTimeSuccess(time);
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        mRootView.onGetColumnLastUpdateTimeError();
                    }
                });
    }

    public void getClueList(int page, int pageSize) {
        if (!NetworkUtils.isConnected()) {
            mRootView.showMessage(Utils.getContext().getResources().getString(R.string.network_disconnect));
            return;
        }

        mModel.getClueList(page, pageSize)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(2, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                // .doOnSubscribe(disposable -> {
                //     mRootView.showLoading();//显示下拉刷新的进度条
                // }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                // .doFinally(() -> {
                //     mRootView.hideLoading();//隐藏下拉刷新的进度条
                // })
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<ContentListEntity<ClueItemEntity>>(mErrorHandler) {

                    @Override
                    public void onNext(ContentListEntity<ClueItemEntity> contentListEntity) {
                        mRootView.onGetClueListSuccess(contentListEntity);
                    }

                    @Override
                    public void onError(Throwable t) {
                        String message = t.getMessage();
                        if (!TextUtils.isEmpty(message) && message.contains(Utils.getContext().getString(R.string.no_permission))) {
                        } else {
                            super.onError(t);
                        }
                        if (t instanceof SuccessNullException) {
                            mRootView.onGetClueListSuccess(new ContentListEntity<ClueItemEntity>());
                        } else {
                            mRootView.onGetClueListError(t.getMessage());
                        }
                    }
                });
    }
}
