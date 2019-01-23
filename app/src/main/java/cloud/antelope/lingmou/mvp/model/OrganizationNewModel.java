package cloud.antelope.lingmou.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import javax.inject.Inject;

import cloud.antelope.lingmou.mvp.contract.OrganizationNewContract;
import cloud.antelope.lingmou.mvp.model.api.LmResponseHandler;
import cloud.antelope.lingmou.mvp.model.api.service.UserService;
import cloud.antelope.lingmou.mvp.model.entity.OrganizationEntity;
import io.reactivex.Observable;


@FragmentScope
public class OrganizationNewModel extends BaseModel implements OrganizationNewContract.Model {
    private Gson mGson;
    private Application mApplication;

    @Inject
    public OrganizationNewModel(IRepositoryManager repositoryManager, Gson gson, Application application) {
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
    public Observable<OrganizationEntity> getOrgCameras(String isRoot, String id, String type) {
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .getOrganizations(isRoot, id, type)
                .compose(LmResponseHandler.<OrganizationEntity>handleResult());
    }
}
