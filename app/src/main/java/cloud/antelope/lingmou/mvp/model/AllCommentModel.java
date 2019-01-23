package cloud.antelope.lingmou.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.lingdanet.safeguard.common.utils.SPUtils;

import javax.inject.Inject;

import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.contract.AllCommentContract;
import cloud.antelope.lingmou.mvp.model.api.Api;
import cloud.antelope.lingmou.mvp.model.api.CyResponseHandler;
import cloud.antelope.lingmou.mvp.model.api.LmResponseHandler;
import cloud.antelope.lingmou.mvp.model.api.service.CyqzService;
import cloud.antelope.lingmou.mvp.model.entity.CommentItemEntity;
import cloud.antelope.lingmou.mvp.model.entity.CommentListEntity;
import cloud.antelope.lingmou.mvp.model.entity.EmptyEntity;
import cloud.antelope.lingmou.mvp.model.entity.QueryReplyPageEntity;
import io.reactivex.Observable;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;


@ActivityScope
public class AllCommentModel extends BaseModel implements AllCommentContract.Model {
    private Gson mGson;
    private Application mApplication;

    @Inject
    public AllCommentModel(IRepositoryManager repositoryManager, Gson gson, Application application) {
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
    public Observable<EmptyEntity> addReply(String columnId, String contentId, String topReplyId,
                                            String reUserName, String reUserId, String reply) {
        CommentItemEntity request = new CommentItemEntity();
        request.setColumnId(columnId);
        request.setContentId(contentId);
        request.setTopReplyId(topReplyId);
        request.setReUserName(reUserName);
        request.setReUserId(reUserId);
        request.setReply(reply);
        request.setType(Constants.REPLY_TYPE_NEWS_COMMENT);

        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));

        return mRepositoryManager
                .obtainRetrofitService(CyqzService.class)
                .addReply(request)
                .compose(LmResponseHandler.<EmptyEntity>handleResult());
    }

    @Override
    public Observable<CommentListEntity> queryReplyPage(String columnId, String contentId, int page, int pageSize) {
        QueryReplyPageEntity request = new QueryReplyPageEntity();
        request.setOperationCenterId(SPUtils.getInstance().getString(Constants.CONFIG_OPERATION_ID));
        request.setColumnId(columnId);
        request.setContentId(contentId);
        request.setPage(page);
        request.setPageSize(pageSize);
        request.setReplyAuditState(Constants.HAS_AUDIT);
        request.setReplyAuditResult(Constants.HAS_AUDIT_PASS);

        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));

        return mRepositoryManager
                .obtainRetrofitService(CyqzService.class)
                .queryReplyPage(request)
                .compose(LmResponseHandler.<CommentListEntity>handleResult());
    }
}
