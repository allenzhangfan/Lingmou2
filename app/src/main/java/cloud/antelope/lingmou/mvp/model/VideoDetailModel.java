package cloud.antelope.lingmou.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;

import javax.inject.Inject;

import cloud.antelope.lingmou.mvp.contract.VideoDetailContract;
import cloud.antelope.lingmou.mvp.model.api.LmResponseHandler;
import cloud.antelope.lingmou.mvp.model.api.service.UserService;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;
import io.reactivex.Observable;


@ActivityScope
public class VideoDetailModel extends BaseModel implements VideoDetailContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public VideoDetailModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<OrgCameraEntity> getDeviceInfo(String cId) {
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .getDeviceInfo(cId)
                .compose(LmResponseHandler.handleResult());
    }
}
