package cloud.antelope.lingmou.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.lingdanet.safeguard.common.utils.SPUtils;

import javax.inject.Inject;

import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.contract.CollectionListContract;
import cloud.antelope.lingmou.mvp.contract.SearchListContract;
import cloud.antelope.lingmou.mvp.model.api.LmResponseHandler;
import cloud.antelope.lingmou.mvp.model.api.service.UserService;
import cloud.antelope.lingmou.mvp.model.entity.CollectionListBean;
import cloud.antelope.lingmou.mvp.model.entity.CollectionListRequest;
import cloud.antelope.lingmou.mvp.model.entity.CommonListResponse;
import cloud.antelope.lingmou.mvp.model.entity.DeployListRequest;
import cloud.antelope.lingmou.mvp.model.entity.DeployResponse;
import io.reactivex.Observable;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;


@FragmentScope
public class CollectionListModel extends BaseModel implements CollectionListContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public CollectionListModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<CommonListResponse<CollectionListBean>> getList(CollectionListRequest request) {
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .getCollectionList(request)
                .compose(LmResponseHandler.handleResult());
    }
}