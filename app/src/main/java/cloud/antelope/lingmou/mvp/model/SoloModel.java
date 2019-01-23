package cloud.antelope.lingmou.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.lingdanet.safeguard.common.utils.SPUtils;

import javax.inject.Inject;

import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.common.UrlConstant;
import cloud.antelope.lingmou.mvp.contract.SoloContract;
import cloud.antelope.lingmou.mvp.model.api.LmResponseHandler;
import cloud.antelope.lingmou.mvp.model.api.service.UserService;
import cloud.antelope.lingmou.mvp.model.entity.DeviceLoginParamBean;
import cloud.antelope.lingmou.mvp.model.entity.DeviceLoginResponseBean;
import cloud.antelope.lingmou.mvp.model.entity.EmptyEntity;
import cloud.antelope.lingmou.mvp.model.entity.LocationRequest;
import io.reactivex.Observable;
import io.reactivex.Observer;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;


@ActivityScope
public class SoloModel extends BaseModel implements SoloContract.Model {
    private Gson mGson;
    private Application mApplication;

    @Inject
    public SoloModel(IRepositoryManager repositoryManager, Gson gson, Application application) {
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
    public Observable<DeviceLoginResponseBean> loginDevice(int intCid, String sn, String brand, String signature) {

        DeviceLoginParamBean paramBean = new DeviceLoginParamBean(intCid, sn, brand, "");
        RetrofitUrlManager.getInstance().putDomain("baseUrl", SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .loginDevice(paramBean)
                .compose(LmResponseHandler.handleResult());
    }

    @Override
    public Observable<EmptyEntity> heart(long time) {
        RetrofitUrlManager.getInstance().putDomain("baseUrl", SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .heart()
                .compose(LmResponseHandler.<EmptyEntity>handleResult());
    }

    @Override
    public Observable<EmptyEntity> uploadLocation(String cid, double longitude, double lngitude) {
        LocationRequest request = new LocationRequest();
        request.setDeviceId(cid);
        request.setLongitude(longitude);
        request.setLatitude(lngitude);
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .upLoadLocation(request)
                .compose(LmResponseHandler.handleResult());
    }
}
