package cloud.antelope.lingmou.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;
import com.lingdanet.safeguard.common.constant.CommonConstant;
import com.lingdanet.safeguard.common.utils.SPUtils;

import java.util.List;

import javax.inject.Inject;

import cloud.antelope.lingmou.app.utils.gson.ListHistoryTypeAdapter;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.contract.VideoPlayContract;
import cloud.antelope.lingmou.mvp.model.api.LmResponseHandler;
import cloud.antelope.lingmou.mvp.model.api.service.UserService;
import cloud.antelope.lingmou.mvp.model.entity.CameraNewStreamEntity;
import cloud.antelope.lingmou.mvp.model.entity.CameraNewStreamRequest;
import cloud.antelope.lingmou.mvp.model.entity.CollectRequest;
import cloud.antelope.lingmou.mvp.model.entity.CollectResponse;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreRequest;
import cloud.antelope.lingmou.mvp.model.entity.HistoryKVStoreRequest;
import cloud.antelope.lingmou.mvp.model.entity.ObjectEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;
import cloud.antelope.lingmou.mvp.model.entity.RecordLyCameraBean;
import cloud.antelope.lingmou.mvp.model.entity.SetKeyStoreRequest;
import cloud.antelope.lingmou.mvp.model.entity.TokenEntity;
import io.reactivex.Observable;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


@ActivityScope
public class VideoPlayModel extends BaseModel implements VideoPlayContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public VideoPlayModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<String> getLyyToken(Long cameraId) {
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .getCameraToken(cameraId)
                .compose(LmResponseHandler.handleResult());
    }

    @Override
    public Observable<CameraNewStreamEntity> getPlayerUrl(Long cameraId) {
        Long[] ids = new Long[]{cameraId};
        CameraNewStreamRequest request = new CameraNewStreamRequest();
        request.setCids(ids);
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .getCameraNewStream(request)
                .compose(LmResponseHandler.handleResult());
    }

    @Override
    public Observable<GetKeyStoreBaseEntity> cameraLike(String storeKey, SetKeyStoreRequest keyStoreRequest) {
        String userId = SPUtils.getInstance().getString(CommonConstant.UID);
        Gson gson = new Gson();
        String toJson = gson.toJson(keyStoreRequest);
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .setUserKvStore(userId, storeKey, toJson)
                .compose(LmResponseHandler.<GetKeyStoreBaseEntity>handleResult());
    }

    @Override
    public Observable<RecordLyCameraBean> getRecordTimes(String cid, String token, long startTime, long endTime) {
        RetrofitUrlManager.getInstance().putDomain("ly_name", SPUtils.getInstance().getString(Constants.URL_RECORD));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .getRecordTimes(cid, token, startTime, endTime);
    }

    @Override
    public Observable<GetKeyStoreBaseEntity> getHistories(String userId) {
        GetKeyStoreRequest request = new GetKeyStoreRequest();
        request.setUserId(userId);
        request.setStoreKey("APP_HISTORY");
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .getUserKvStore(request)
                .compose(LmResponseHandler.handleResult());
    }

    @Override
    public Observable<GetKeyStoreBaseEntity> putHistories(String storeKey, HistoryKVStoreRequest request) {
        String userId = SPUtils.getInstance().getString(CommonConstant.UID);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(ListHistoryTypeAdapter.TYPE, ListHistoryTypeAdapter.getInstance());
        Gson gson = gsonBuilder.create();
        List<HistoryKVStoreRequest.History> histories = request.getHistories();
        String toJson = gson.toJson(histories, ListHistoryTypeAdapter.TYPE);
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        request.recycle();
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .setUserKvStore(userId, storeKey, toJson)
                .compose(LmResponseHandler.handleResult());
    }

    @Override
    public Observable<OrgCameraEntity> getDeviceInfo(String cId) {
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .getDeviceInfo(cId)
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
    public Observable<ObjectEntity> uploadPic(String accessToken, String size, RequestBody requestBody, MultipartBody.Part part) {
        RetrofitUrlManager.getInstance().putDomain("ly_name", SPUtils.getInstance().getString(Constants.URL_OBJECT_STORAGE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .uploadAvatar(accessToken, size, requestBody, part, "0");
    }

    @Override
    public Observable<CollectResponse> collect(CollectRequest request) {
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .collectPicture(request)
                .compose(LmResponseHandler.handleResult());
    }
}
