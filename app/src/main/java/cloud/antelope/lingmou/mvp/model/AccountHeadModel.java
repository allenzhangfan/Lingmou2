package cloud.antelope.lingmou.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;
import com.lingdanet.safeguard.common.constant.CommonConstant;
import com.lingdanet.safeguard.common.utils.SPUtils;

import javax.inject.Inject;

import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.contract.AccountHeadContract;
import cloud.antelope.lingmou.mvp.model.api.LmResponseHandler;
import cloud.antelope.lingmou.mvp.model.api.service.UserService;
import cloud.antelope.lingmou.mvp.model.entity.ObjectEntity;
import cloud.antelope.lingmou.mvp.model.entity.TokenEntity;
import cloud.antelope.lingmou.mvp.model.entity.UserUpdateEntity;
import io.reactivex.Observable;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


@ActivityScope
public class AccountHeadModel extends BaseModel implements AccountHeadContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public AccountHeadModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<TokenEntity> getStorageToken() {
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .getStorageToken()
                .compose(LmResponseHandler.handleResult());
    }

    @Override
    public Observable<ObjectEntity> uploadAvatar(String accessToken, String size, RequestBody requestBody, MultipartBody.Part part) {
        RetrofitUrlManager.getInstance().putDomain("ly_name", SPUtils.getInstance().getString(Constants.URL_OBJECT_STORAGE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .uploadAvatar(accessToken, size, requestBody, part, "0");
    }

    @Override
    public Observable<Object> updateUserInfo(String avatarUrl) {
        UserUpdateEntity updateEntity = new UserUpdateEntity();
        updateEntity.setId(SPUtils.getInstance().getString(CommonConstant.UID));
        updateEntity.setUserAvatar(avatarUrl);
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .updateUserInfo(updateEntity)
                .compose(LmResponseHandler.handleResult());
    }

}
