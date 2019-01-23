package cloud.antelope.lingmou.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;
import com.lingdanet.safeguard.common.utils.SPUtils;

import javax.inject.Inject;

import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.contract.FaceRecognizeContract;
import cloud.antelope.lingmou.mvp.model.api.LmResponseHandler;
import cloud.antelope.lingmou.mvp.model.api.service.UserService;
import cloud.antelope.lingmou.mvp.model.entity.BodyRecorgBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.BodyRecorgnizeRequest;
import cloud.antelope.lingmou.mvp.model.entity.FaceNewEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceRecorgBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceRecorgnizeRequest;
import io.reactivex.Observable;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;


@ActivityScope
public class FaceRecognizeModel extends BaseModel implements FaceRecognizeContract.Model {
    private Gson mGson;
    private Application mApplication;

    @Inject
    public FaceRecognizeModel(IRepositoryManager repositoryManager, Gson gson, Application application) {
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
    public Observable<FaceRecorgBaseEntity<FaceNewEntity>> getFaceListByFeature(long startTime, long endTime, int score, Object featureKey) {
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

    @Override
    public Observable<BodyRecorgBaseEntity<FaceNewEntity>> getBodyList(Long startTime, Long endTime, int score, Object feature) {
        BodyRecorgnizeRequest recorgnizeRequest = new BodyRecorgnizeRequest();
        recorgnizeRequest.setBodyFeature(feature);
        recorgnizeRequest.setStartTime(startTime);
        recorgnizeRequest.setEndTime(endTime);
        recorgnizeRequest.setScore(score);
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .getBodyListByFeature(recorgnizeRequest)
                .compose(LmResponseHandler.handleResult());
    }
}
