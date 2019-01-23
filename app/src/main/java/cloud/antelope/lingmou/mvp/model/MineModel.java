package cloud.antelope.lingmou.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.lingdanet.safeguard.common.utils.SPUtils;

import java.util.List;

import javax.inject.Inject;

import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.contract.DailyContract;
import cloud.antelope.lingmou.mvp.contract.MineContract;
import cloud.antelope.lingmou.mvp.model.api.LmResponseHandler;
import cloud.antelope.lingmou.mvp.model.api.service.CyqzService;
import cloud.antelope.lingmou.mvp.model.api.service.UserService;
import cloud.antelope.lingmou.mvp.model.entity.EmptyEntity;
import cloud.antelope.lingmou.mvp.model.entity.PushRequest;
import cloud.antelope.lingmou.mvp.model.entity.PushRequestList;
import cloud.antelope.lingmou.mvp.model.entity.TokenEntity;
import cloud.antelope.lingmou.mvp.model.entity.UserInfoEntity;
import io.reactivex.Observable;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;


@ActivityScope
public class MineModel extends BaseModel implements MineContract.Model {
    private Gson mGson;
    private Application mApplication;

    @Inject
    public MineModel(IRepositoryManager repositoryManager, Gson gson, Application application) {
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
    public Observable<UserInfoEntity> getUserInfo() {
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .queryUserInfo()
                .compose(LmResponseHandler.handleResult());
    }

    @Override
    public Observable<TokenEntity> getStorageToken() {
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .getStorageToken()
                .compose(LmResponseHandler.handleResult());
    }

    @Override
    public Observable<EmptyEntity> setPushConfig(List<PushRequest> list) {

        PushRequestList request = new PushRequestList();
        request.setKeyValues(list);

        // RetrofitUrlManager.getInstance().putDomain("setPushConfig", Api.CYQZ_BASE_URL);

        return mRepositoryManager
                .obtainRetrofitService(CyqzService.class)
                .setPushConfig(request)
                .compose(LmResponseHandler.<EmptyEntity>handleResult());
    }
}
