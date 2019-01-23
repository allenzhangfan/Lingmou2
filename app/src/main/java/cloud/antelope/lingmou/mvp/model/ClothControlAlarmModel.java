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
import cloud.antelope.lingmou.mvp.contract.ClothControlAlarmContract;
import cloud.antelope.lingmou.mvp.model.api.LmResponseHandler;
import cloud.antelope.lingmou.mvp.model.api.service.UserService;
import cloud.antelope.lingmou.mvp.model.entity.AlarmDepotEntity;
import cloud.antelope.lingmou.mvp.model.entity.EmptyEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceAlarmBlackEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceAlarmEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceAlarmRequest;
import cloud.antelope.lingmou.mvp.model.entity.ListBaseBlackLibEntity;
import cloud.antelope.lingmou.mvp.model.entity.ListBaseEntity;
import io.reactivex.Observable;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;


@ActivityScope
public class ClothControlAlarmModel extends BaseModel implements ClothControlAlarmContract.Model {
    private Gson mGson;
    private Application mApplication;

    @Inject
    public ClothControlAlarmModel(IRepositoryManager repositoryManager, Gson gson, Application application) {
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
    public Observable<ListBaseEntity<List<FaceAlarmBlackEntity>>> getFaceAlarm(Long startTimeStamp,
                                                                               Long endTimeStamp, int from,
                                                                               String effective, int size, String libIds,
                                                                               String cameraIds, String geoAddress) {
        FaceAlarmRequest request = new FaceAlarmRequest();
        if (null != startTimeStamp) {
            request.setStartTime(startTimeStamp);
        }
        if (null != endTimeStamp) {
            request.setEndTime(endTimeStamp);
        }
        if (!TextUtils.isEmpty(effective)) {
            request.setAlarmOperationType(effective);
        }
        if (!TextUtils.isEmpty(libIds)) {
            request.setLibIds(libIds);
        }
        if (!TextUtils.isEmpty(cameraIds)) {
            request.setCameraIds(cameraIds);
        }
        // request.setGeoAddress(geoAddress);
        request.setSortType(1);
        request.setPage(from);
        request.setPageSize(size);
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .faceAlarmList(request)
                .compose(LmResponseHandler.handleResult());
    }

    @Override
    public Observable<ListBaseBlackLibEntity<List<AlarmDepotEntity>>> getDepots() {
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .getAlarmDepot(new EmptyEntity())
                .compose(LmResponseHandler.handleResult());
    }

    @Override
    public Observable<EmptyEntity> getAlarmDetailPermission(String id) {
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .getAlarmDetailPermission(id)
                .compose(LmResponseHandler.handleResult());
    }
}
