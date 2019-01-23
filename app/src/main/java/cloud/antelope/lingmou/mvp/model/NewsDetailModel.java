package cloud.antelope.lingmou.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.lingdanet.safeguard.common.utils.SPUtils;

import javax.inject.Inject;

import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.contract.NewsDetailContract;
import cloud.antelope.lingmou.mvp.model.api.Api;
import cloud.antelope.lingmou.mvp.model.api.CyResponseHandler;
import cloud.antelope.lingmou.mvp.model.api.LmResponseHandler;
import cloud.antelope.lingmou.mvp.model.api.service.CyqzService;
import cloud.antelope.lingmou.mvp.model.api.service.UserService;
import cloud.antelope.lingmou.mvp.model.entity.CommentLikeEntity;
import cloud.antelope.lingmou.mvp.model.entity.ContentRecordRequest;
import cloud.antelope.lingmou.mvp.model.entity.EmptyEntity;
import cloud.antelope.lingmou.mvp.model.entity.LikeCountEntity;
import io.reactivex.Observable;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;


@ActivityScope
public class NewsDetailModel extends BaseModel implements NewsDetailContract.Model {
    private Gson mGson;
    private Application mApplication;

    @Inject
    public NewsDetailModel(IRepositoryManager repositoryManager, Gson gson, Application application) {
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
    public Observable<LikeCountEntity> getContentLike(String columnId, String contentId) {

        CommentLikeEntity request = new CommentLikeEntity();
        request.setColumnId(columnId);
        request.setContentId(contentId);
        request.setOperationCenterId(SPUtils.getInstance().getString(Constants.CONFIG_OPERATION_ID));
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));

        return mRepositoryManager
                .obtainRetrofitService(CyqzService.class)
                .getContentLike(request)
                .compose(LmResponseHandler.<LikeCountEntity>handleResult());
    }

    @Override
    public Observable<EmptyEntity> likeIt(String columnId, String contentId, String addOrRemove) {

        CommentLikeEntity request = new CommentLikeEntity();
        request.setColumnId(columnId);
        request.setContentId(contentId);
        request.setAddOrRemove(addOrRemove);

        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));

        return mRepositoryManager
                .obtainRetrofitService(CyqzService.class)
                .likeIt(request)
                .compose(LmResponseHandler.<EmptyEntity>handleResult());
    }

    @Override
    public Observable<EmptyEntity> contentRecord(String coloum, String contentId, String type) {
        ContentRecordRequest recordRequest = new ContentRecordRequest();
        recordRequest.setColumnId(coloum);
        recordRequest.setContentId(contentId);
        recordRequest.setType(type);
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager.obtainRetrofitService(UserService.class)
                .contentRecord(recordRequest)
                .compose(LmResponseHandler.handleResult());
    }
}
