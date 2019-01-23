package cloud.antelope.lingmou.mvp.model;

import android.app.Application;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;
import com.lingdanet.safeguard.common.utils.SPUtils;

import java.util.List;

import javax.inject.Inject;

import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.common.UrlConstant;
import cloud.antelope.lingmou.mvp.contract.FaceDepotContract;
import cloud.antelope.lingmou.mvp.model.api.LmResponseHandler;
import cloud.antelope.lingmou.mvp.model.api.service.UserService;
import cloud.antelope.lingmou.mvp.model.entity.FaceDepotEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceDepotRequest;
import cloud.antelope.lingmou.mvp.model.entity.FaceFeatureInfoEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceFeatureRequest;
import cloud.antelope.lingmou.mvp.model.entity.FaceNewEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceRecorgBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceRecorgnizeRequest;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreRequest;
import io.reactivex.Observable;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;


@ActivityScope
public class FaceDepotModel extends BaseModel implements FaceDepotContract.Model {
    private Gson mGson;
    private Application mApplication;

    @Inject
    public FaceDepotModel(IRepositoryManager repositoryManager, Gson gson, Application application) {
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
    public Observable<FaceDepotEntity> getFaceDepot(Long tartTimeStamp, Long endTimeStamp, int size, List<String> cameraTags,List<String> noCameraTags,
                                                    List<String> faceTags, List<String> emptyTags, List<String> cameraIds, String minId,
                                                    String minCaptureTime, String pageType) {
        FaceDepotRequest request = new FaceDepotRequest();
        if (null != tartTimeStamp) {
            request.setStartTime(tartTimeStamp);
        }
        if (null != endTimeStamp) {
            request.setEndTime(endTimeStamp);
        }
        if (null != cameraTags && !cameraTags.isEmpty()) {
            request.setCameraTags(cameraTags);
        }
        if (null != faceTags && !faceTags.isEmpty()) {
            request.setFaceTags(faceTags);
        }
        if (null != emptyTags && !emptyTags.isEmpty()) {
            request.setEmptyTags(emptyTags);
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
        request.setPageSize(size);
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .getFaceDepot(request)
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
