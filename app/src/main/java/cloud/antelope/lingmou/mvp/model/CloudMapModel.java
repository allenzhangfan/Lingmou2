package cloud.antelope.lingmou.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.lingdanet.safeguard.common.constant.CommonConstant;
import com.lingdanet.safeguard.common.utils.SPUtils;

import javax.inject.Inject;

import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.contract.CloudMapContract;
import cloud.antelope.lingmou.mvp.model.api.LmResponseHandler;
import cloud.antelope.lingmou.mvp.model.api.service.UserService;
import cloud.antelope.lingmou.mvp.model.entity.CameraNewStreamEntity;
import cloud.antelope.lingmou.mvp.model.entity.CameraNewStreamRequest;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreRequest;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraParentEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraRequest;
import cloud.antelope.lingmou.mvp.model.entity.SetKeyStoreRequest;
import io.reactivex.Observable;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;


@FragmentScope
public class CloudMapModel extends BaseModel implements CloudMapContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public CloudMapModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<OrgCameraParentEntity> getAllCameras() {
        OrgCameraRequest request = new OrgCameraRequest();
        request.setPage(1);
        request.setPageSize(10000);
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .getAllCameras(request)
                .compose(LmResponseHandler.handleResult());
    }
    @Override
    public Observable<GetKeyStoreBaseEntity> getCollections(String userId) {
        GetKeyStoreRequest request = new GetKeyStoreRequest();
        request.setUserId(userId);
        request.setStoreKey("MY_GROUP");
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .getUserKvStore(request)
                .compose(LmResponseHandler.handleResult());
    }

    @Override
    public Observable<GetKeyStoreBaseEntity> cameraLike(String storeKey, SetKeyStoreRequest keyStoreRequest) {
        String userId = SPUtils.getInstance().getString(CommonConstant.UID);
        Gson gson = new Gson();
        String toJson = gson.toJson(keyStoreRequest);
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .setUserKvStore(userId, storeKey, toJson)
                .compose(LmResponseHandler.<GetKeyStoreBaseEntity>handleResult());

    }

    @Override
    public Observable<CameraNewStreamEntity> getCameraCovers(Long[] cids) {
        CameraNewStreamRequest request = new CameraNewStreamRequest();
        request.setCids(cids);
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .getCameraNewStream(request)
                .compose(LmResponseHandler.handleResult());
    }
}
