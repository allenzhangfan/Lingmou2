package cloud.antelope.lingmou.mvp.contract;

import android.app.Activity;

import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;

import cloud.antelope.lingmou.mvp.model.entity.CommentListEntity;
import cloud.antelope.lingmou.mvp.model.entity.EmptyEntity;
import io.reactivex.Observable;


public interface AllCommentContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {

        Activity getActivity();

        void onAddReplySuccess(String replyId);

        void onQueryReplyPageSuccess(CommentListEntity entity);

        void onQueryReplyPageError();
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {

        Observable<EmptyEntity> addReply(String columnId, String contentId, String topReplyId,
                                         String reUserName, String reUserId, String reply);


        Observable<CommentListEntity> queryReplyPage(String columnId, String contentId, int page, int pageSize);
    }
}
