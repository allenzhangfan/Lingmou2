package cloud.antelope.lingmou.mvp.model;

import android.app.Application;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;
import com.lingdanet.safeguard.common.utils.SPUtils;

import org.apache.http.client.ResponseHandler;

import javax.inject.Inject;

import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.contract.CommentDetailContract;
import cloud.antelope.lingmou.mvp.model.api.LmResponseHandler;
import cloud.antelope.lingmou.mvp.model.api.service.UserService;
import cloud.antelope.lingmou.mvp.model.entity.CommentItemEntity;
import cloud.antelope.lingmou.mvp.model.entity.CommentListEntity;
import cloud.antelope.lingmou.mvp.model.entity.EmptyEntity;
import cloud.antelope.lingmou.mvp.model.entity.QueryReplyPageEntity;
import io.reactivex.Observable;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;


@ActivityScope
public class CommentDetailModel extends BaseModel implements CommentDetailContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public CommentDetailModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<EmptyEntity> addReply(String columnId, String contentId, String topReplyId, String upReplyId, String reUserName, String reUserId, String reply) {
        CommentItemEntity request = new CommentItemEntity();
        request.setColumnId(columnId);
        request.setContentId(contentId);
        request.setTopReplyId(topReplyId);

        if (!TextUtils.isEmpty(upReplyId)) {
            request.setUpReplyId(upReplyId);
        }
        if (!TextUtils.isEmpty(reUserName)) {
            request.setReUserName(reUserName);
        }
        if (!TextUtils.isEmpty(reUserId)) {
            request.setReUserId(reUserId);
        }
        request.setReply(reply);
        request.setType(Constants.REPLY_TYPE_COMMENT_REPLY);

        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .addReply(request)
                .compose(LmResponseHandler.handleResult());
    }

    @Override
    public Observable<CommentListEntity> queryReplyPage(String columnId, String contentId, String replyId) {
        QueryReplyPageEntity request = new QueryReplyPageEntity();
        request.setOperationCenterId(SPUtils.getInstance().getString(Constants.CONFIG_OPERATION_ID));
        request.setColumnId(columnId);
        request.setContentId(contentId);
        request.setReplyId(replyId);
        request.setReplyAuditState(Constants.HAS_AUDIT);
        request.setReplyAuditResult(Constants.HAS_AUDIT_PASS);
        request.setType(Constants.REPLY_TYPE_COMMENT_REPLY);

        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .queryReplyPage(request)
                .compose(LmResponseHandler.handleResult());
    }
}
