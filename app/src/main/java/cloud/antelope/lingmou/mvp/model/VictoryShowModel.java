package cloud.antelope.lingmou.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;
import com.lingdanet.safeguard.common.utils.SPUtils;

import org.apache.http.client.ResponseHandler;

import javax.inject.Inject;

import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.contract.VictoryShowContract;
import cloud.antelope.lingmou.mvp.model.api.LmResponseHandler;
import cloud.antelope.lingmou.mvp.model.api.service.CyqzService;
import cloud.antelope.lingmou.mvp.model.api.service.UserService;
import cloud.antelope.lingmou.mvp.model.entity.ContentListEntity;
import cloud.antelope.lingmou.mvp.model.entity.ContentRequestEntity;
import cloud.antelope.lingmou.mvp.model.entity.NewsItemEntity;
import io.reactivex.Observable;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;


@ActivityScope
public class VictoryShowModel extends BaseModel implements VictoryShowContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public VictoryShowModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<ContentListEntity<NewsItemEntity>> getNewsList(int page, int pageSize) {
        ContentRequestEntity request = new ContentRequestEntity();
        request.setColumnId(SPUtils.getInstance().getString(Constants.CONFIG_CASE_ID));
        request.setPage(page);
        request.setPageSize(pageSize);
        request.setCaseType("06");
        request.setOperationCenterId(SPUtils.getInstance().getString(Constants.CONFIG_OPERATION_ID));

        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(CyqzService.class)
                .getNewsList(request)
                .compose(LmResponseHandler.handleResult());
    }
}
