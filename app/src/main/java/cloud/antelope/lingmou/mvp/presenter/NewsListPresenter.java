package cloud.antelope.lingmou.mvp.presenter;

import android.app.Application;
import android.text.TextUtils;

import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;
import com.lingdanet.safeguard.common.utils.NetworkUtils;
import com.lingdanet.safeguard.common.utils.TimeUtils;
import com.lingdanet.safeguard.common.utils.Utils;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.SuccessNullException;
import cloud.antelope.lingmou.mvp.contract.NewsListContract;
import cloud.antelope.lingmou.mvp.model.entity.BannerItemEntity;
import cloud.antelope.lingmou.mvp.model.entity.ContentListEntity;
import cloud.antelope.lingmou.mvp.model.entity.NewsItemEntity;
import cloud.antelope.lingmou.mvp.model.entity.VictoryItemEntity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;


@ActivityScope
public class NewsListPresenter extends BasePresenter<NewsListContract.Model, NewsListContract.View> {
    private RxErrorHandler mErrorHandler;
    private Application mApplication;
    private ImageLoader mImageLoader;
    private AppManager mAppManager;

    @Inject
    public NewsListPresenter(NewsListContract.Model model, NewsListContract.View rootView
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

    public void getColumnLastUpdateTime(String columnId) {
        if (!NetworkUtils.isConnected()) {
            mRootView.showMessage(Utils.getContext().getResources().getString(R.string.network_disconnect));
            return;
        }

        mModel.getColumnLastUpdateTime(columnId)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                .doOnSubscribe(disposable -> {
                    mRootView.showLoading("");//显示下拉刷新的进度条
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {if(mRootView!=null)
                    mRootView.hideLoading();//隐藏下拉刷新的进度条
                })
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

    public void getNewsList(int page, int pageSize, String caseType) {

        if (!NetworkUtils.isConnected()) {
            mRootView.onGetNewsListError(Utils.getContext()
                    .getResources().getString(R.string.network_disconnect));
            return;
        }

        mModel.getNewsList(page, pageSize, caseType)
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
                .subscribe(new ErrorHandleSubscriber<ContentListEntity<NewsItemEntity>>(mErrorHandler) {

                    @Override
                    public void onNext(ContentListEntity<NewsItemEntity> contentList) {
                        if ("06".equals(caseType) && page == 0) {
                            //表明是战果的信息
                            List<NewsItemEntity> list = contentList.getList();
                            List<VictoryItemEntity> victoryItemEntityList = new ArrayList<>();
                            if (list != null) {
                                for (NewsItemEntity itemEntity : list) {
                                    VictoryItemEntity victoryItemEntity = new VictoryItemEntity();
                                    long time = TimeUtils.string2Millis(itemEntity.getCreateTime(),new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()));
                                    victoryItemEntity.setOldCreateTime(time);
                                    victoryItemEntity.setTitle(itemEntity.getTitle());
                                    victoryItemEntityList.add(victoryItemEntity);
                                }
                            }
                            mRootView.onGetVictorySuccess(victoryItemEntityList);
                        } else {
                            mRootView.onGetNewsListSuccess(contentList);
                        }

                    }

                    @Override
                    public void onError(Throwable t) {
                        // super.onError(t);
                        mRootView.onGetNewsListError(t.getMessage());
                    }
                });
    }


    public void getNewsTop() {

        if (!NetworkUtils.isConnected()) {
            mRootView.onGetNewsTopError(Utils.getContext()
                    .getResources().getString(R.string.network_disconnect));
            return;
        }

        mModel.getNewsTop()
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
                .subscribe(new ErrorHandleSubscriber<ContentListEntity<BannerItemEntity>>(mErrorHandler) {

                    @Override
                    public void onNext(ContentListEntity<BannerItemEntity> contentList) {
                        mRootView.onGetNewsTopSuccess(contentList);
                    }

                    @Override
                    public void onError(Throwable t) {
                        // super.onError(t);
                        mRootView.onGetNewsTopError(t.getMessage());
                    }
                });
    }

}
