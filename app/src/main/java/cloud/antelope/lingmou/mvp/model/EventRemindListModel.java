package cloud.antelope.lingmou.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.FragmentScope;
import com.lingdanet.safeguard.common.utils.SPUtils;

import javax.inject.Inject;

import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.contract.EventRemindListContract;
import cloud.antelope.lingmou.mvp.model.api.LmResponseHandler;
import cloud.antelope.lingmou.mvp.model.api.service.UserService;
import cloud.antelope.lingmou.mvp.model.entity.BaseListEntity;
import cloud.antelope.lingmou.mvp.model.entity.DailyPoliceAlarmEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceAlarmBlackRequest;
import io.reactivex.Observable;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;


@FragmentScope
public class EventRemindListModel extends BaseModel implements EventRemindListContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public EventRemindListModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<BaseListEntity<DailyPoliceAlarmEntity>> getAlarms(Integer pageNo, Integer pageSize, String keyword,
                                                                        Integer taskType, Integer alarmOperationType,
                                                                        Integer alarmScope, Long startTime, Long endTime) {
        FaceAlarmBlackRequest request = new FaceAlarmBlackRequest();
        request.pageNo = pageNo;
        request.pageSize = pageSize;
        request.alarmScope = alarmScope;
        if (null != keyword) {
            request.keyword = keyword;
        }
        if (taskType != null && -1 != taskType) {
            request.taskTypeList.add(Long.valueOf(taskType));
        }
        if (-1 != alarmOperationType) {
            request.alarmOperationType = alarmOperationType;
        }
        if (null != startTime) {
            request.startTime = startTime;
        }
        if (null != endTime) {
            request.endTime = endTime;
        }
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .getAlarms(request)
                .compose(LmResponseHandler.handleResult());
    }
}