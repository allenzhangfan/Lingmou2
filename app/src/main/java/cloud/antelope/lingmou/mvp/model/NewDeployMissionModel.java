package cloud.antelope.lingmou.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;
import com.lingdanet.safeguard.common.utils.SPUtils;

import javax.inject.Inject;

import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.contract.NewDeployMissionContract;
import cloud.antelope.lingmou.mvp.model.api.LmResponseHandler;
import cloud.antelope.lingmou.mvp.model.api.service.UserService;
import cloud.antelope.lingmou.mvp.model.entity.FaceUrlSearchBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceUrlSearchEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceUrlSearchRequest;
import cloud.antelope.lingmou.mvp.model.entity.NewDeployMissionRequest;
import cloud.antelope.lingmou.mvp.model.entity.ObjectEntity;
import cloud.antelope.lingmou.mvp.model.entity.TokenEntity;
import io.reactivex.Observable;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


@ActivityScope
public class NewDeployMissionModel extends BaseModel implements NewDeployMissionContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public NewDeployMissionModel(IRepositoryManager repositoryManager) {
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
    public Observable<ObjectEntity> uploadPic(String accessToken, String size, RequestBody requestBody, MultipartBody.Part part) {
        RetrofitUrlManager.getInstance().putDomain("ly_name", SPUtils.getInstance().getString(Constants.URL_OBJECT_STORAGE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .uploadAvatar(accessToken, size, requestBody, part, "0");
    }

    @Override
    public Observable<FaceUrlSearchBaseEntity<FaceUrlSearchEntity>> getFeature(String url) {
        FaceUrlSearchRequest request = new FaceUrlSearchRequest();
        request.setUrl(url);
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .getFaceFeature(request)
                .compose(LmResponseHandler.handleResult());
    }

    @Override
    public Observable<String> createDeployMission(NewDeployMissionRequest request) {
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .newDeployMission(request)
                .compose(LmResponseHandler.handleResult());
    }
}