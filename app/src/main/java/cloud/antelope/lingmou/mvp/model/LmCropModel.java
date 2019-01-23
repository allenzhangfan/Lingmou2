package cloud.antelope.lingmou.mvp.model;

import android.app.Application;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;
import com.lingdanet.safeguard.common.net.RequestUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;

import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.common.UrlConstant;
import cloud.antelope.lingmou.mvp.model.entity.FaceKeyEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceNewEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceRecorgBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceRecorgnizeRequest;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import cloud.antelope.lingmou.mvp.contract.LmCropContract;
import cloud.antelope.lingmou.mvp.model.api.LmResponseHandler;
import cloud.antelope.lingmou.mvp.model.api.service.UserService;
import cloud.antelope.lingmou.mvp.model.entity.FaceUrlSearchBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceUrlSearchEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceUrlSearchRequest;
import cloud.antelope.lingmou.mvp.model.entity.ObjectEntity;
import cloud.antelope.lingmou.mvp.model.entity.TokenEntity;
import io.reactivex.Observable;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


@ActivityScope
public class LmCropModel extends BaseModel implements LmCropContract.Model {
    private Gson mGson;
    private Application mApplication;

    @Inject
    public LmCropModel(IRepositoryManager repositoryManager, Gson gson, Application application) {
        super(repositoryManager);
        this.mGson = gson;
        this.mApplication = application;
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
    public Observable<ObjectEntity> uploadFile(String accessToken, String size, RequestBody requestBody, MultipartBody.Part part) {
        RetrofitUrlManager.getInstance().putDomain("ly_name", SPUtils.getInstance().getString(Constants.URL_OBJECT_STORAGE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .uploadAvatar(accessToken, size, requestBody, part, "0");
    }

    @Override
    public Observable<FaceUrlSearchBaseEntity<FaceUrlSearchEntity>> getFaceFeature(String url) {
        FaceUrlSearchRequest request = new FaceUrlSearchRequest();
        request.setUrl(url);
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .getFaceFeature(request)
                .compose(LmResponseHandler.handleResult());
    }

    @Override
    public Observable<FaceRecorgBaseEntity<FaceNewEntity>> getFaceListByFeature(Long startTime, Long endTime, int score, String featureKey) {
        if (!TextUtils.isEmpty(featureKey)) {
            FaceRecorgnizeRequest recorgnizeRequest = new FaceRecorgnizeRequest();
            recorgnizeRequest.setFaceFeatures(featureKey);
            recorgnizeRequest.setStartTime(startTime);
            recorgnizeRequest.setEndTime(endTime);
            recorgnizeRequest.setScore(score);
            RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
            return mRepositoryManager
                    .obtainRetrofitService(UserService.class)
                    .getFaceListByFeature(recorgnizeRequest)
                    .compose(LmResponseHandler.handleResult());
        }
        return null;
    }
}
