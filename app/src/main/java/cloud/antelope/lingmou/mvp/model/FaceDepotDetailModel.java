package cloud.antelope.lingmou.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;
import com.lingdanet.safeguard.common.utils.SPUtils;

import javax.inject.Inject;

import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.contract.FaceDepotDetailContract;
import cloud.antelope.lingmou.mvp.model.api.LmResponseHandler;
import cloud.antelope.lingmou.mvp.model.api.service.UserService;
import cloud.antelope.lingmou.mvp.model.entity.BodyFeatureInfoEntity;
import cloud.antelope.lingmou.mvp.model.entity.BodyRecorgBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.BodyRecorgnizeRequest;
import cloud.antelope.lingmou.mvp.model.entity.CollectionListBean;
import cloud.antelope.lingmou.mvp.model.entity.CollectionListRequest;
import cloud.antelope.lingmou.mvp.model.entity.CommonListResponse;
import cloud.antelope.lingmou.mvp.model.entity.FaceFeatureInfoEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceFeatureRequest;
import cloud.antelope.lingmou.mvp.model.entity.FaceNewEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceRecorgBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceRecorgnizeRequest;
import io.reactivex.Observable;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;


@ActivityScope
public class FaceDepotDetailModel extends BaseModel implements FaceDepotDetailContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public FaceDepotDetailModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<FaceFeatureInfoEntity> getFaceFeatureInfo(String vid) {
        FaceFeatureRequest request = new FaceFeatureRequest();
        request.setId(vid);
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .getCaptureFaceInfoById(request)
                .compose(LmResponseHandler.handleResult());
    }
    @Override
    public Observable<BodyFeatureInfoEntity> getBodyFeatureInfo(String vid) {
        FaceFeatureRequest request = new FaceFeatureRequest();
        request.setId(vid);
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .getBodyFeatruByEntity(request)
                .compose(LmResponseHandler.handleResult());
    }

    @Override
    public Observable<FaceRecorgBaseEntity<FaceNewEntity>> getFaceList(Long startTime, Long endTime, int score, float[] feature, String vid) {
        FaceRecorgnizeRequest recorgnizeRequest = new FaceRecorgnizeRequest();
        recorgnizeRequest.setFaceFeatures(feature);
        recorgnizeRequest.setStartTime(startTime);
        recorgnizeRequest.setEndTime(endTime);
        recorgnizeRequest.setScore(score);
        recorgnizeRequest.setId(vid);
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .getFaceListByFeature(recorgnizeRequest)
                .compose(LmResponseHandler.handleResult());
    }
    @Override
    public Observable<BodyRecorgBaseEntity<FaceNewEntity>> getBodyList(Long startTime, Long endTime, int score, float[] feature, String vid) {
        BodyRecorgnizeRequest recorgnizeRequest = new BodyRecorgnizeRequest();
        recorgnizeRequest.setBodyFeature(feature);
        recorgnizeRequest.setStartTime(startTime);
        recorgnizeRequest.setEndTime(endTime);
        recorgnizeRequest.setScore(score);
        recorgnizeRequest.setId(vid);
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .getBodyListByFeature(recorgnizeRequest)
                .compose(LmResponseHandler.handleResult());
    }

    @Override
    public Observable<CommonListResponse<CollectionListBean>> getCollectionList(CollectionListRequest request) {
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .getCollectionList(request)
                .compose(LmResponseHandler.handleResult());
    }
}