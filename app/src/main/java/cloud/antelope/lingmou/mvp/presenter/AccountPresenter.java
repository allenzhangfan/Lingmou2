package cloud.antelope.lingmou.mvp.presenter;

import android.app.Application;

import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;
import com.lingdanet.safeguard.common.constant.CommonConstant;
import com.lingdanet.safeguard.common.utils.Configuration;
import com.lingdanet.safeguard.common.utils.DeviceUtil;
import com.lingdanet.safeguard.common.utils.EncryptUtils;
import com.lingdanet.safeguard.common.utils.FileUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.trello.rxlifecycle2.android.ActivityEvent;

import javax.inject.Inject;

import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.contract.AccountContract;
import cloud.antelope.lingmou.mvp.model.entity.ObjectEntity;
import cloud.antelope.lingmou.mvp.model.entity.TokenEntity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


@ActivityScope
public class AccountPresenter extends BasePresenter<AccountContract.Model, AccountContract.View> {
    private RxErrorHandler mErrorHandler;
    private Application mApplication;
    private ImageLoader mImageLoader;
    private AppManager mAppManager;

    @Inject
    public AccountPresenter(AccountContract.Model model, AccountContract.View rootView
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

    public void uploadAvatar(String fileLength, RequestBody requestBody, MultipartBody.Part part, String filePath) {
        mModel.getStorageToken()
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .doOnSubscribe(disposable -> mRootView.showLoading(""))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {if(mRootView!=null) mRootView.hideLoading();})
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<TokenEntity>(mErrorHandler) {
                    @Override
                    public void onNext(TokenEntity tokenEntity) {
                        uploadToAntelope(tokenEntity.getToken(), fileLength, requestBody, part, filePath);
                    }
                });
    }

    private void uploadToAntelope(String tokenEntity, String fileLength, RequestBody requestBody, MultipartBody.Part part, String filePath) {
//        RetrofitUrlManager.getInstance().putDomain(LY_DOMAIN_NAME, LY_DOMAIN);
//        RetrofitUrlManager.getInstance().setRun(true);
        mModel.uploadAvatar(tokenEntity, fileLength, requestBody, part)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(1, 2))
                .doOnSubscribe(disposable -> mRootView.showLoading(""))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {if(mRootView!=null) mRootView.hideLoading();})
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<ObjectEntity>(mErrorHandler) {
                    @Override
                    public void onNext(ObjectEntity objectEntity) {
                        String objUrl = SPUtils.getInstance().getString(Constants.URL_OBJECT_STORAGE);
                        String avatarUrl = objUrl + "/files?obj_id=" + objectEntity.getObjId() + "&access_token=" + tokenEntity;
                        updateUserInfo(avatarUrl);
                        saveAvatar(avatarUrl, filePath);
//                        RetrofitUrlManager.getInstance().clearAllDomain();
//                        RetrofitUrlManager.getInstance().setRun(false);
                    }
                });
    }

    private void updateUserInfo(String avatarUrl) {
        mModel.updateUserInfo(avatarUrl)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .doOnSubscribe(disposable -> mRootView.showLoading(""))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {if(mRootView!=null) mRootView.hideLoading();})
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber(mErrorHandler) {
                    @Override
                    public void onNext(Object o) {
                        SPUtils.getInstance().put(Constants.CONFIG_LAST_USER_AVATAR, avatarUrl);
                        mRootView.uploadAvatarSuccess();
                    }
                });
    }


    /**
     * 保存头像到APP的头像目录.
     *
     * @param avatarUrl 头像的Url
     * @param filePath  头像的原始文件路径
     */
    private void saveAvatar(String avatarUrl, String filePath) {
        String objUrl = SPUtils.getInstance().getString(Constants.URL_OBJECT_STORAGE);
        avatarUrl = objUrl + "/files2/" + avatarUrl + "?access_token=";
        String destPath = Configuration.getAvatarDirectoryPath()
                + EncryptUtils.encryptMD5ToString(avatarUrl) + Constants.DEFAULT_IMAGE_SUFFIX;
        if (filePath.contains(CommonConstant.MAIN_PATH + CommonConstant.AVATAR_PATH)) {
            FileUtils.rename(filePath, destPath);
            DeviceUtil.galleryAddMedia(filePath);
        } else {
            FileUtils.copyFile(filePath, destPath);
        }
    }

    public void logOut() {
        mModel.logOut()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(disposable -> mRootView.showLoading(""))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {if(mRootView!=null) mRootView.hideLoading();})
                .compose(RxLifecycleUtils.bindUntilEvent(mRootView, ActivityEvent.DESTROY))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<String>(mErrorHandler) {
                    @Override
                    public void onNext(String objectEntity) {
                        mRootView.logOutSuccess();
                    }

                    @Override
                    public void onError(Throwable t) {
                        // super.onError(t);
                        mRootView.logOutFail();
                    }
                });
    }
}
