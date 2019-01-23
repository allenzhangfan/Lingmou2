package cloud.antelope.lingmou.mvp.presenter;

import android.app.Application;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jess.arms.integration.AppManager;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.RxLifecycleUtils;
import com.lingdanet.safeguard.common.utils.NetworkUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.lingdanet.safeguard.common.utils.Utils;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.JPushUtils;
import cloud.antelope.lingmou.app.utils.SaveUtils;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.model.entity.CarBrandBean;
import cloud.antelope.lingmou.mvp.model.entity.CarColorBean;
import cloud.antelope.lingmou.mvp.model.entity.CarInfo;
import cloud.antelope.lingmou.mvp.model.entity.CarTypeBean;
import cloud.antelope.lingmou.mvp.model.entity.CollectionListBean;
import cloud.antelope.lingmou.mvp.model.entity.CollectionListRequest;
import cloud.antelope.lingmou.mvp.model.entity.CommonListResponse;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraParentEntity;
import cloud.antelope.lingmou.mvp.model.entity.PlateColorBean;
import cloud.antelope.lingmou.mvp.model.entity.TokenEntity;
import cloud.antelope.lingmou.mvp.ui.activity.LoginActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;

import javax.inject.Inject;

import cloud.antelope.lingmou.mvp.contract.NewMainContract;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;

import static com.jess.arms.utils.ArmsUtils.startActivity;


@ActivityScope
public class NewMainPresenter extends BasePresenter<NewMainContract.Model, NewMainContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public NewMainPresenter(NewMainContract.Model model, NewMainContract.View rootView) {
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

    public void getCyqzLyToken() {
        if (!NetworkUtils.isConnected()) {
            mRootView.showMessage(Utils.getContext().getResources().getString(R.string.network_disconnect));
            return;
        }
        if (TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.URL_BASE))) {
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
        mModel.getCyqzLyToken()
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                // .doOnSubscribe(disposable -> {
                //     mRootView.showLoading();//显示下拉刷新的进度条
                // }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                // .doFinally(() -> {
                //     mRootView.hideLoading();//隐藏下拉刷新的进度条
                // })
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<TokenEntity>(mErrorHandler) {

                    @Override
                    public void onNext(TokenEntity tokenEntity) {
                        SPUtils.getInstance().put(Constants.CONFIG_CYQZ_LY_TOKEN, tokenEntity.getToken());
                        mRootView.onGetCyqzLyTokenSuccess();
                    }
                });
    }

    public void getAllCameras() {
        if (!NetworkUtils.isConnected()) {
            mRootView.showMessage(Utils.getContext().getResources().getString(R.string.network_disconnect));
            return;
        }
        if (TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.URL_BASE))) {
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
        mModel.getAllCameras()
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                .observeOn(Schedulers.newThread())
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<OrgCameraParentEntity>(mErrorHandler) {
                    @Override
                    public void onNext(OrgCameraParentEntity entity) {
                        mRootView.getAllCamerasSuccess(entity);
                    }

                    @Override
                    public void onError(Throwable t) {
                        mRootView.getAllCamerasSuccess(null);
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

    public void getCarBrandList(){
        mModel.getCarInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))
                .subscribe(new ErrorHandleSubscriber<CarInfo>(mErrorHandler) {
                    @Override
                    public void onNext(CarInfo info) {
                        if (info != null) {
                            for (CarBrandBean bean : info.vehicleInfo) {
                                if ("全部".equals(bean.name)) {
                                    bean.selected = true;
                                }
                                if (bean.fullSpelling != null) {
                                    bean.fullSpelling = bean.fullSpelling.toLowerCase();
                                }
                            }
                            mRootView.onGetCarBrandList(info);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        ToastUtils.showLong(t.getMessage());
                    }
                });
    }
}
