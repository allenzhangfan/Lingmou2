package cloud.antelope.lingmou.mvp.contract;

import android.app.Activity;

import com.jess.arms.mvp.IView;
import com.jess.arms.mvp.IModel;

import cloud.antelope.lingmou.mvp.model.entity.ClueItemEntity;
import cloud.antelope.lingmou.mvp.model.entity.ContentListEntity;
import cloud.antelope.lingmou.mvp.model.entity.EmptyEntity;
import io.reactivex.Observable;


public interface ClueDetailContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        Activity getActivity();

        void onGetClueDetailSuccess(ClueItemEntity clueItemEntity);

        void onGetClueFail();

        void onAddReplySuccess();
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {

        Observable<ContentListEntity<ClueItemEntity>> getClueDetail(String columnId, String contentId);

        Observable<EmptyEntity> addReply(String columnId, String contentId, String topReplyId, String reUserName, String reUserId, String reply);
    }
}
