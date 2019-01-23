package cloud.antelope.lingmou.mvp.contract;

import com.jess.arms.mvp.IView;
import com.jess.arms.mvp.IModel;

import java.util.List;

import cloud.antelope.lingmou.mvp.model.entity.BodyRecorgBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceNewEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceRecorgBaseEntity;
import io.reactivex.Observable;


public interface FaceRecognizeContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        void onFaceSuccess(List<FaceNewEntity> userEntity);

        void onFaceFail();
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {
        Observable<FaceRecorgBaseEntity<FaceNewEntity>> getFaceListByFeature(long startTime, long endTime, int score, Object feature);
        Observable<BodyRecorgBaseEntity<FaceNewEntity>> getBodyList(Long startTime, Long endTime, int score, Object feature);
    }
}
