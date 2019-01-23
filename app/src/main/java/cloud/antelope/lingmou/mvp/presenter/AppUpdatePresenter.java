package cloud.antelope.lingmou.mvp.presenter;

import android.app.Application;

import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;
import com.lingdanet.safeguard.common.utils.AppUtils;
import com.lingdanet.safeguard.common.utils.NetworkUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.lingdanet.safeguard.common.utils.Utils;

import javax.inject.Inject;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.common.UrlConstant;
import cloud.antelope.lingmou.mvp.contract.AppUpdateContract;
import cloud.antelope.lingmou.mvp.model.entity.AppUpdateEntity;
import cloud.antelope.lingmou.mvp.model.entity.UpdateInfo;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import retrofit2.Retrofit;


@ActivityScope
public class AppUpdatePresenter extends BasePresenter<AppUpdateContract.Model, AppUpdateContract.View> {
    private RxErrorHandler mErrorHandler;
    private Application mApplication;
    private ImageLoader mImageLoader;
    private AppManager mAppManager;

    @Inject
    public AppUpdatePresenter(AppUpdateContract.Model model, AppUpdateContract.View rootView
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

    public void checkUpdate(boolean showToast) {
        if (showToast && !NetworkUtils.isConnected()) {
            mRootView.showMessage(Utils.getContext().getResources().getString(R.string.network_disconnect));
            mRootView.next();
            return;
        }
        mModel.checkUpdate()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<AppUpdateEntity>(mErrorHandler) {
                    @Override
                    public void onNext(AppUpdateEntity entity) {
                        UpdateInfo updateInfo = entity.getUpdateInfo();
                        int currentVersion = AppUtils.getAppVersionCode();
                        if (null != updateInfo) {
                            int updateVersion = updateInfo.getVersionNumber();
                            boolean forceUpdate = updateInfo.isForceUpdate();
                            String versionDescription = updateInfo.getVersionDescription();
                            String packageUrl = updateInfo.getPackageUrl();
                            int minVersion = updateInfo.getMinVersion();
                            if (updateVersion > currentVersion) {
                                if (currentVersion < minVersion) {
                                    mRootView.showUpdateDialog(true, versionDescription, packageUrl);
                                } else {
                                    mRootView.showUpdateDialog(forceUpdate, versionDescription, packageUrl);
                                }
                            } else {
                                if (showToast) {
                                    ToastUtils.showShort("您已经是最新版本了");
                                }
                                mRootView.next();
                            }
                        }else {
                            mRootView.next();
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        mRootView.next();
                    }
                });
    }

}
