package cloud.antelope.lingmou.mvp.model;

import android.app.Application;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.lingdanet.safeguard.common.constant.CommonConstant;
import com.lingdanet.safeguard.common.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.contract.SearchListContract;
import cloud.antelope.lingmou.mvp.model.api.LmResponseHandler;
import cloud.antelope.lingmou.mvp.model.api.service.UserService;
import cloud.antelope.lingmou.mvp.model.entity.BaseListEntity;
import cloud.antelope.lingmou.mvp.model.entity.BodyDepotEntity;
import cloud.antelope.lingmou.mvp.model.entity.BodyDepotRequest;
import cloud.antelope.lingmou.mvp.model.entity.CameraDevicesRequest;
import cloud.antelope.lingmou.mvp.model.entity.DailyPoliceAlarmEntity;
import cloud.antelope.lingmou.mvp.model.entity.DeviceIdListRequest;
import cloud.antelope.lingmou.mvp.model.entity.FaceAlarmBlackRequest;
import cloud.antelope.lingmou.mvp.model.entity.FaceDepotEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceDepotRequest;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.LmBaseResponseEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;
import cloud.antelope.lingmou.mvp.model.entity.SetKeyStoreRequest;
import io.reactivex.Observable;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;


@FragmentScope
public class SearchListModel extends BaseModel implements SearchListContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public SearchListModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
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
    public Observable<BaseListEntity<OrgCameraEntity>> searchDevices(Integer deviceState, Integer deviceType, String deviceName, Integer pageNo, Integer pageSize) {
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

    @Override
    public Observable<BaseListEntity<DailyPoliceAlarmEntity>> getAlarms(Integer pageNo, Integer pageSize, String keyword,
                                                                        Integer taskType, Integer alarmOperationType,
                                                                        Integer alarmScope, Long startTime, Long endTime) {
        FaceAlarmBlackRequest request = new FaceAlarmBlackRequest();
        request.pageNo = pageNo;
        request.pageSize = pageSize;
        request.alarmScope = alarmScope;
        request.keyword = keyword;
        if (taskType != null && -1 != taskType) {
            request.taskType = taskType;
            request.taskTypeList.add(Long.valueOf(taskType));
        } else {
            request.taskTypeList.add(101501L);
            request.taskTypeList.add(101502L);
            SPUtils utils = SPUtils.getInstance();
            if (utils.getBoolean(Constants.PERMISSION_OUTSIDE_PERSON)) {
                request.taskTypeList.add(101505L);
            }
            if (utils.getBoolean(Constants.PERMISSION_PRIVATE_NETWORK_SUITE)) {
                request.taskTypeList.add(101504L);
            }
        }
        request.alarmOperationType = alarmOperationType;
        request.startTime = startTime;
        request.endTime = endTime;
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .getAlarms(request)
                .compose(LmResponseHandler.handleResult());
    }

    @Override
    public Observable<LmBaseResponseEntity<ArrayList<String>>> getDeviceIdList(DeviceIdListRequest request) {
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .getUserDeviceIdList(request)
                .compose(LmResponseHandler.handleResult());
    }

    @Override
    public Observable<FaceDepotEntity> getFaceDepot(Long tartTimeStamp, Long endTimeStamp, int size, List<String> cameraTags,
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
        request.setPageSize(size);
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .getFaceDepot(request)
                .compose(LmResponseHandler.handleResult());

    }

    @Override
    public Observable<BodyDepotEntity> getBodyDepot(int size, Long startTime, Long endTime,
                                                    List<String> cameraTags, List<String> bodyTags, List<String> cameraIds,
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
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .getBodyDepot(request)
                .compose(LmResponseHandler.handleResult());
    }
}