package cloud.antelope.lingmou.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;
import com.lingdanet.safeguard.common.utils.SPUtils;

import javax.inject.Inject;

import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.contract.DailyPoliceDetailContract;
import cloud.antelope.lingmou.mvp.model.api.LmResponseHandler;
import cloud.antelope.lingmou.mvp.model.api.service.UserService;
import cloud.antelope.lingmou.mvp.model.entity.CollectAlarmInfoRequest;
import cloud.antelope.lingmou.mvp.model.entity.EmptyEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceDealRequest;
import io.reactivex.Observable;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;


@ActivityScope
public class DailyPoliceDetailModel extends BaseModel implements DailyPoliceDetailContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public DailyPoliceDetailModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<EmptyEntity> deal(String vid, int effective, String common) {
        FaceDealRequest request = new FaceDealRequest();
        request.setId(vid);
        request.setIsEffective(effective);
        request.setOperationDetail(common);
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .dealFaceAlarm(request)
                .compose(LmResponseHandler.handleResult());
    }

    @Override
    public Observable<Object> collectAlarmInfo(String alarmId) {
        CollectAlarmInfoRequest request = new CollectAlarmInfoRequest();
        request.setAlarmResultId(alarmId);

        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .collectAlarmInfo(request)
                .compose(LmResponseHandler.handleResult());
    }
}
