package cloud.antelope.lingmou.mvp.model;

import android.app.Application;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;
import com.lingdanet.safeguard.common.utils.SPUtils;

import javax.inject.Inject;

import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.contract.RealVideosListContract;
import cloud.antelope.lingmou.mvp.model.api.LmResponseHandler;
import cloud.antelope.lingmou.mvp.model.api.service.UserService;
import cloud.antelope.lingmou.mvp.model.entity.BaseListEntity;
import cloud.antelope.lingmou.mvp.model.entity.CameraDevicesRequest;
import cloud.antelope.lingmou.mvp.model.entity.CameraNewStreamEntity;
import cloud.antelope.lingmou.mvp.model.entity.CameraNewStreamRequest;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;
import io.reactivex.Observable;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;


@ActivityScope
public class RealVideosListModel extends BaseModel implements RealVideosListContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public RealVideosListModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<BaseListEntity<OrgCameraEntity>> getAllCameras(Integer deviceState, Integer deviceType, String deviceName, Integer pageNo, Integer pageSize) {
        CameraDevicesRequest request = new CameraDevicesRequest();
        if (null != deviceType) {
            request.setDeviceType(deviceType);
        }
        if (!TextUtils.isEmpty(deviceName)) {
            request.setDeviceName(deviceName);
        }
        if (null != deviceState) {
            request.setDeviceState(deviceState);
        }
        request.setPage(pageNo);
        request.setPageSize(pageSize);
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .getCameraDevices(request)
                .compose(LmResponseHandler.handleResult());
    }
}
