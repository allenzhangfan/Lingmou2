package cloud.antelope.lingmou.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;

import javax.inject.Inject;

import cloud.antelope.lingmou.mvp.contract.ReportAddressContract;


@ActivityScope
public class ReportAddressModel extends BaseModel implements ReportAddressContract.Model {
    private Gson mGson;
    private Application mApplication;

    @Inject
    public ReportAddressModel(IRepositoryManager repositoryManager, Gson gson, Application application) {
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
