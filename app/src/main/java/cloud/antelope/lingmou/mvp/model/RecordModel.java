package cloud.antelope.lingmou.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;
import com.lingdanet.safeguard.common.constant.CommonConstant;
import com.lingdanet.safeguard.common.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.common.UrlConstant;
import cloud.antelope.lingmou.mvp.contract.RecordContract;
import cloud.antelope.lingmou.mvp.model.api.LmResponseHandler;
import cloud.antelope.lingmou.mvp.model.api.service.UserService;
import cloud.antelope.lingmou.mvp.model.entity.CameraLikeRequest;
import cloud.antelope.lingmou.mvp.model.entity.CameraRecordRequest;
import cloud.antelope.lingmou.mvp.model.entity.CameraStreamRequest;
import cloud.antelope.lingmou.mvp.model.entity.EmptyEntity;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.LyTokenEntity;
import cloud.antelope.lingmou.mvp.model.entity.RecordLyCameraBean;
import cloud.antelope.lingmou.mvp.model.entity.RecordSection;
import cloud.antelope.lingmou.mvp.model.entity.SetKeyStoreRequest;
import io.reactivex.Observable;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;


@ActivityScope
public class RecordModel extends BaseModel implements RecordContract.Model {
    private Gson mGson;
    private Application mApplication;

    @Inject
    public RecordModel(IRepositoryManager repositoryManager, Gson gson, Application application) {
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
    public Observable<String> getLyyToken(Long cameraId) {
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .getCameraToken(cameraId)
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
                .compose(LmResponseHandler.handleResult());
    }

    @Override
    public Observable<RecordLyCameraBean> getRecordTimes(String cid, String token, long startTime, long endTime) {
        RetrofitUrlManager.getInstance().putDomain("ly_name", UrlConstant.LY_CAMERA_URL);
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .getRecordTimes(cid, token, startTime, endTime);

    }
}
