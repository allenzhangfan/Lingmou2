package cloud.antelope.lingmou.mvp.model;

import android.app.Application;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.lingdanet.safeguard.common.utils.SPUtils;

import java.util.List;

import javax.inject.Inject;

import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.contract.BodyDepotContract;
import cloud.antelope.lingmou.mvp.model.api.LmResponseHandler;
import cloud.antelope.lingmou.mvp.model.api.service.UserService;
import cloud.antelope.lingmou.mvp.model.entity.BodyDepotEntity;
import cloud.antelope.lingmou.mvp.model.entity.BodyDepotRequest;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreRequest;
import io.reactivex.Observable;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;


@ActivityScope
public class BodyDepotModel extends BaseModel implements BodyDepotContract.Model {
    private Gson mGson;
    private Application mApplication;

    @Inject
    public BodyDepotModel(IRepositoryManager repositoryManager, Gson gson, Application application) {
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
    public Observable<BodyDepotEntity> getBodyDepot(int size, Long startTime, Long endTime,
                                                    List<String> cameraTags, List<String> noCameraTags,List<String> bodyTags, List<String> cameraIds,
                                                    String minId,
                                                    String minCaptureTime, String pageType) {
        BodyDepotRequest request = new BodyDepotRequest();
        request.setPageSize(size);
        if (null != startTime) {
            request.setStartTime(startTime);
        }
        if (null != endTime) {
            request.setEndTime(endTime);
        }
        if (null != cameraTags && !cameraTags.isEmpty()) {
            request.setCameraTags(cameraTags);
        }
        if (null != bodyTags && !bodyTags.isEmpty()) {
            request.setBodyTags(bodyTags);
        }
        if (null != cameraIds && !cameraIds.isEmpty()) {
            request.setCameraIds(cameraIds);
        }
        if (!TextUtils.isEmpty(minId)) {
            request.setMinId(minId);
        }
        if (!TextUtils.isEmpty(minCaptureTime)) {
            request.setMinCaptureTime(minCaptureTime);
        }
        if (null != pageType) {
            request.setPageType(pageType);
        }
        request.setNoCameraTags(noCameraTags);
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .getBodyDepot(request)
                .compose(LmResponseHandler.handleResult());
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
}
