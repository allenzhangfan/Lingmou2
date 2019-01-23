package cloud.antelope.lingmou.mvp.contract;

import android.app.Activity;

import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;

import java.util.ArrayList;

import cloud.antelope.lingmou.mvp.model.entity.EmptyEntity;
import cloud.antelope.lingmou.mvp.model.entity.EventEntity;
import io.reactivex.Observable;
import okhttp3.MultipartBody;


public interface ReportEditContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {

        Activity getActivity();

        void onSubmitClueSuccess(EventEntity entity);

        void onSubmitClueError();

        void onUploadClueMessageSuccess();

        void onUploadClueMessageError();
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {

        Observable<EventEntity> submitClue(EventEntity message, ArrayList<MultipartBody.Part> files);

        Observable<EmptyEntity> uploadClueMessage(EventEntity eventEntity);
    }
}
