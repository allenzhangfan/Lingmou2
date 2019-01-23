package cloud.antelope.lingmou.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.lingdanet.safeguard.common.constant.CommonConstant;
import com.lingdanet.safeguard.common.utils.SPUtils;

import java.util.List;

import javax.inject.Inject;

import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.contract.MainContract;
import cloud.antelope.lingmou.mvp.model.api.Api;
import cloud.antelope.lingmou.mvp.model.api.CyResponseHandler;
import cloud.antelope.lingmou.mvp.model.api.LmResponseHandler;
import cloud.antelope.lingmou.mvp.model.api.service.CyqzService;
import cloud.antelope.lingmou.mvp.model.api.service.UserService;
import cloud.antelope.lingmou.mvp.model.entity.EmptyEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraParentEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraRequest;
import cloud.antelope.lingmou.mvp.model.entity.PushEntity;
import cloud.antelope.lingmou.mvp.model.entity.TokenEntity;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import cloud.antelope.lingmou.mvp.model.api.LmResponseHandler;
import cloud.antelope.lingmou.mvp.model.api.service.UserService;
import io.reactivex.Observable;


@ActivityScope
public class MainModel extends BaseModel implements MainContract.Model {
    private Gson mGson;
    private Application mApplication;

    @Inject
    public MainModel(IRepositoryManager repositoryManager, Gson gson, Application application) {
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
    public Observable<List<PushEntity>> getPushConfig() {

        // RetrofitUrlManager.getInstance().putDomain("getPushConfig", Api.CYQZ_BASE_URL);

        return mRepositoryManager
                .obtainRetrofitService(CyqzService.class)
                .getPushConfig(new EmptyEntity())
                .compose(LmResponseHandler.<List<PushEntity>>handleResult());
    }
/*
    @Override
    public Observable<TokenEntity> getCyqzUserToken() {
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .getCyqzUserToken()
                .compose(LmResponseHandler.<TokenEntity>handleResult())
                .flatMap(new Function<TokenEntity, ObservableSource<TokenEntity>>() {
                    @Override
                    public ObservableSource<TokenEntity> apply(TokenEntity tokenEntity) throws Exception {
                        SPUtils.getInstance().put(CommonConstant.TOKEN, tokenEntity.getToken());
                        return getCyqzLyToken();
                    }
                });
    }*/

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

    public Observable<TokenEntity> getCyqzLyToken() {
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .getCyqzLyToken()
                .compose(LmResponseHandler.<TokenEntity>handleResult());
    }
    /*public Observable uploadSolosLocations(String cameraId, double lat, double lng) {
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .uploadLocation(cameraId, lat, lng)
                .compose(LmResponseHandler.handleResult());
    }*/
}
