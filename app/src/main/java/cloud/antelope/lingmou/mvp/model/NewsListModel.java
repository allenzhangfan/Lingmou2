package cloud.antelope.lingmou.mvp.model;

import android.app.Application;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.lingdanet.safeguard.common.utils.SPUtils;

import javax.inject.Inject;

import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.contract.NewsListContract;
import cloud.antelope.lingmou.mvp.model.api.Api;
import cloud.antelope.lingmou.mvp.model.api.CyResponseHandler;
import cloud.antelope.lingmou.mvp.model.api.LmResponseHandler;
import cloud.antelope.lingmou.mvp.model.api.service.CyqzService;
import cloud.antelope.lingmou.mvp.model.api.service.UserService;
import cloud.antelope.lingmou.mvp.model.entity.BannerItemEntity;
import cloud.antelope.lingmou.mvp.model.entity.ContentListEntity;
import cloud.antelope.lingmou.mvp.model.entity.ContentRequestEntity;
import cloud.antelope.lingmou.mvp.model.entity.NewsItemEntity;
import io.reactivex.Observable;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;


@ActivityScope
public class NewsListModel extends BaseModel implements NewsListContract.Model {
    private Gson mGson;
    private Application mApplication;

    @Inject
    public NewsListModel(IRepositoryManager repositoryManager, Gson gson, Application application) {
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
    public Observable<Long> getColumnLastUpdateTime(String columnId) {
        ContentRequestEntity request = new ContentRequestEntity();
        request.setColumnId(columnId);
        request.setOperationCenterId(SPUtils.getInstance().getString(Constants.CONFIG_OPERATION_ID));
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(CyqzService.class)
                .getColumnLastUpdateTime(request)
                .compose(LmResponseHandler.handleResult());
    }

    @Override
    public Observable<ContentListEntity<BannerItemEntity>> getNewsTop() {
        ContentRequestEntity request = new ContentRequestEntity();
        request.setColumnId(SPUtils.getInstance().getString(Constants.CONFIG_CASE_ID));
        request.setOperationCenterId(SPUtils.getInstance().getString(Constants.CONFIG_OPERATION_ID));
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(CyqzService.class)
                .getNewsTop(request)
                .compose(LmResponseHandler.handleResult());
    }

    @Override
    public Observable<ContentListEntity<NewsItemEntity>> getNewsList(int page, int pageSize, String caseType) {
        ContentRequestEntity request = new ContentRequestEntity();
        request.setColumnId(SPUtils.getInstance().getString(Constants.CONFIG_CASE_ID));
        request.setPage(page);
        request.setPageSize(pageSize);
        if (!TextUtils.isEmpty(caseType)) {
            request.setCaseType(caseType);
        }
        request.setOperationCenterId(SPUtils.getInstance().getString(Constants.CONFIG_OPERATION_ID));
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(CyqzService.class)
                .getNewsList(request)
                .compose(LmResponseHandler.handleResult());
    }
}
