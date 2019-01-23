package cloud.antelope.lingmou.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.FragmentScope;
import com.lingdanet.safeguard.common.utils.SPUtils;

import java.util.List;

import javax.inject.Inject;

import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.contract.DailyDevicesContract;
import cloud.antelope.lingmou.mvp.model.api.LmResponseHandler;
import cloud.antelope.lingmou.mvp.model.api.service.UserService;
import cloud.antelope.lingmou.mvp.model.entity.EmptyEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraParentEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraRequest;
import cloud.antelope.lingmou.mvp.model.entity.OrgMainEntity;
import io.reactivex.Observable;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;


@FragmentScope
public class DailyDevicesModel extends BaseModel implements DailyDevicesContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public DailyDevicesModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<List<OrgMainEntity>> getMainOrgs() {
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager.obtainRetrofitService(UserService.class)
                .getMainOrgs(new EmptyEntity())
                .compose(LmResponseHandler.handleResult());
    }
    @Override
    public Observable<OrgCameraParentEntity> getMainOrgCameras(int page, int pageSize, String[] orgIds) {
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        OrgCameraRequest orgCameraRequest = new OrgCameraRequest();
        orgCameraRequest.setPage(page);
        orgCameraRequest.setPageSize(pageSize);
        orgCameraRequest.setOrgIds(orgIds);
        return mRepositoryManager.obtainRetrofitService(UserService.class)
                .getMainOrgCameras(orgCameraRequest)
                .compose(LmResponseHandler.handleResult());
    }

}