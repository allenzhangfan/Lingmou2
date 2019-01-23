package cloud.antelope.lingmou.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.lingdanet.safeguard.common.utils.SPUtils;

import java.util.List;

import javax.inject.Inject;

import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.contract.CollectionContract;
import cloud.antelope.lingmou.mvp.model.api.LmResponseHandler;
import cloud.antelope.lingmou.mvp.model.api.service.UserService;
import cloud.antelope.lingmou.mvp.model.entity.CameraNewStreamEntity;
import cloud.antelope.lingmou.mvp.model.entity.CameraNewStreamRequest;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreRequest;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrganizationEntity;
import io.reactivex.Observable;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;


@FragmentScope
public class CollectionModel extends BaseModel implements CollectionContract.Model {
    private Gson mGson;
    private Application mApplication;

    @Inject
    public CollectionModel(IRepositoryManager repositoryManager, Gson gson, Application application) {
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
    public Observable<CameraNewStreamEntity> getCameraCovers(Long[] cids) {
        CameraNewStreamRequest request = new CameraNewStreamRequest();
        request.setCids(cids);
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .getCameraNewStream(request)
                .compose(LmResponseHandler.handleResult());
    }

    @Override
    public Observable<GetKeyStoreBaseEntity> getHistories(String userId) {
        GetKeyStoreRequest request = new GetKeyStoreRequest();
        request.setUserId(userId);
        request.setStoreKey("APP_HISTORY");
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .getUserKvStore(request)
                .compose(LmResponseHandler.handleResult());
    }
}
