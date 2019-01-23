package cloud.antelope.lingmou.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;

import javax.inject.Inject;

import cloud.antelope.lingmou.mvp.contract.SearchCameraContract;
import cloud.antelope.lingmou.mvp.model.api.LmResponseHandler;
import cloud.antelope.lingmou.mvp.model.api.service.UserService;
import cloud.antelope.lingmou.mvp.model.entity.OrganizationEntity;
import io.reactivex.Observable;


@ActivityScope
public class SearchCameraModel extends BaseModel implements SearchCameraContract.Model {
    private Gson mGson;
    private Application mApplication;

    @Inject
    public SearchCameraModel(IRepositoryManager repositoryManager, Gson gson, Application application) {
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
    public Observable<OrganizationEntity> searchCamera(String key, String type, String count, String offset, String isOnline) {
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .searchCamera(key, type, count, offset, isOnline)
                .compose(LmResponseHandler.<OrganizationEntity>handleResult());
    }
}
