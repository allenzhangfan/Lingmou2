package cloud.antelope.lingmou.mvp.contract;

import com.jess.arms.mvp.IView;
import com.jess.arms.mvp.IModel;

import cloud.antelope.lingmou.mvp.model.entity.EmptyEntity;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.LyTokenEntity;
import cloud.antelope.lingmou.mvp.model.entity.RecordLyCameraBean;
import cloud.antelope.lingmou.mvp.model.entity.RecordSection;
import cloud.antelope.lingmou.mvp.model.entity.SetKeyStoreRequest;
import io.reactivex.Observable;


public interface RecordContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {

        void onCameraLikeSuccess(GetKeyStoreBaseEntity entity);

        void getRecordTimeSuccess(RecordLyCameraBean recordSection);

        void getRecordToken(String token);

        void getRecordTimeFail();
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {

        Observable<String> getLyyToken(Long cameraId);

        Observable<GetKeyStoreBaseEntity> cameraLike(String storeKey, SetKeyStoreRequest keyStoreRequest);

        Observable<RecordLyCameraBean> getRecordTimes(String cid, String token, long startTime, long endTime);
    }
}
