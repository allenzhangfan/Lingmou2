package cloud.antelope.lingmou.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.lingdanet.safeguard.common.constant.CommonConstant;
import com.lingdanet.safeguard.common.utils.SPUtils;

import javax.inject.Inject;

import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.contract.MyReportContract;
import cloud.antelope.lingmou.mvp.model.api.Api;
import cloud.antelope.lingmou.mvp.model.api.CyResponseHandler;
import cloud.antelope.lingmou.mvp.model.api.LmResponseHandler;
import cloud.antelope.lingmou.mvp.model.api.service.CyqzService;
import cloud.antelope.lingmou.mvp.model.api.service.UserService;
import cloud.antelope.lingmou.mvp.model.entity.ClueItemEntity;
import cloud.antelope.lingmou.mvp.model.entity.ContentListEntity;
import cloud.antelope.lingmou.mvp.model.entity.ContentRequestEntity;
import cloud.antelope.lingmou.mvp.model.entity.OperationEntity;
import cloud.antelope.lingmou.mvp.model.entity.TokenEntity;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;


@ActivityScope
public class MyReportModel extends BaseModel implements MyReportContract.Model {
    private Gson mGson;
    private Application mApplication;

    @Inject
    public MyReportModel(IRepositoryManager repositoryManager, Gson gson, Application application) {
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

    /*@Override
    public Observable<TokenEntity> getCyqzUserToken() {
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .getCyqzUserToken()
                .compose(LmResponseHandler.<TokenEntity>handleResult())
                .flatMap(new Function<TokenEntity, ObservableSource<TokenEntity>>() {
                    @Override
                    public ObservableSource<TokenEntity> apply(TokenEntity tokenEntity) throws Exception {
                        SPUtils.getInstance().put(CommonConstant.TOKEN, tokenEntity.getToken());
                        return getCyqzLyToken();
                    }
                });
    }*/
/*
    public Observable<TokenEntity> getCyqzLyToken() {
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .getCyqzLyToken()
                .compose(LmResponseHandler.<TokenEntity>handleResult());
    }*/

    @Override
    public Observable<Long> getColumnLastUpdateTime(String columnId) {
        ContentRequestEntity request = new ContentRequestEntity();
        request.setColumnId(columnId);
        request.setOperationCenterId(SPUtils.getInstance().getString(Constants.CONFIG_OPERATION_ID));
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(CyqzService.class)
                .getColumnLastUpdateTime(request)
                .compose(LmResponseHandler.<Long>handleResult());
    }

    @Override
    public Observable<ContentListEntity<ClueItemEntity>> getClueList(int page, int pageSize) {
        ContentRequestEntity request = new ContentRequestEntity();
        request.setColumnId(SPUtils.getInstance().getString(Constants.CONFIG_CLUE_ID));
        request.setPage(page);
        request.setPageSize(pageSize);

        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));

        return mRepositoryManager
                .obtainRetrofitService(CyqzService.class)
                .getClueList(request)
                .compose(LmResponseHandler.<ContentListEntity<ClueItemEntity>>handleResult());
    }


    @Override
    public Observable<OperationEntity> getCyqzConfig() {
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager.obtainRetrofitService(CyqzService.class)
                .getCyqzConfig()
                .compose(LmResponseHandler.handleResult());
    }

}
