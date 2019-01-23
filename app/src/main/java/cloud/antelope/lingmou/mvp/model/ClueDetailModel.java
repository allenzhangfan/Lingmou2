package cloud.antelope.lingmou.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;
import com.lingdanet.safeguard.common.utils.SPUtils;

import javax.inject.Inject;

import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.contract.ClueDetailContract;
import cloud.antelope.lingmou.mvp.model.api.LmResponseHandler;
import cloud.antelope.lingmou.mvp.model.api.service.CyqzService;
import cloud.antelope.lingmou.mvp.model.api.service.UserService;
import cloud.antelope.lingmou.mvp.model.entity.ClueItemEntity;
import cloud.antelope.lingmou.mvp.model.entity.CommentItemEntity;
import cloud.antelope.lingmou.mvp.model.entity.ContentListEntity;
import cloud.antelope.lingmou.mvp.model.entity.ContentRequestEntity;
import cloud.antelope.lingmou.mvp.model.entity.EmptyEntity;
import io.reactivex.Observable;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;


@ActivityScope
public class ClueDetailModel extends BaseModel implements ClueDetailContract.Model {
    private Gson mGson;
    private Application mApplication;

    @Inject
    public ClueDetailModel(IRepositoryManager repositoryManager, Gson gson, Application application) {
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
    public Observable<ContentListEntity<ClueItemEntity>> getClueDetail(String columnId, String contentId) {
        ContentRequestEntity request = new ContentRequestEntity();
        request.setColumnId(columnId);
        request.setContentId(contentId);
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager.obtainRetrofitService(CyqzService.class)
                .getClueList(request)
                .compose(LmResponseHandler.handleResult());
    }

    @Override
    public Observable<EmptyEntity> addReply(String columnId, String contentId, String topReplyId, String reUserName, String reUserId, String reply) {
        CommentItemEntity request = new CommentItemEntity();
        request.setColumnId(columnId);
        request.setContentId(contentId);
        request.setTopReplyId(topReplyId);
        request.setReUserName(reUserName);
        request.setReUserId(reUserId);
        request.setReply(reply);
        request.setType(Constants.REPLY_TYPE_CLUE_REPLY);

        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .addReply(request)
                .compose(LmResponseHandler.handleResult());
    }
}
