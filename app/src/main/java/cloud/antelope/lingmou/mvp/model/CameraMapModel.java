package cloud.antelope.lingmou.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import javax.inject.Inject;

import cloud.antelope.lingmou.mvp.contract.CameraMapContract;


@ActivityScope
public class CameraMapModel extends BaseModel implements CameraMapContract.Model {
    private Gson mGson;
    private Application mApplication;

    @Inject
    public CameraMapModel(IRepositoryManager repositoryManager, Gson gson, Application application) {
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

}
