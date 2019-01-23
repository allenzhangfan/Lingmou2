package cloud.antelope.lingmou.mvp.contract;

import android.app.Activity;

import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;

import java.util.List;

import cloud.antelope.lingmou.mvp.model.entity.AlarmDepotEntity;
import cloud.antelope.lingmou.mvp.model.entity.EmptyEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceAlarmBlackEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceAlarmEntity;
import cloud.antelope.lingmou.mvp.model.entity.ListBaseBlackLibEntity;
import cloud.antelope.lingmou.mvp.model.entity.ListBaseEntity;
import io.reactivex.Observable;


public interface ClothControlAlarmContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        Activity getActivity();

        void getAlarmSuccess(ListBaseEntity<List<FaceAlarmBlackEntity>> faceAlarmEntity);

        void getAlarmError();

        void getDepots(ListBaseBlackLibEntity<List<AlarmDepotEntity>> list);

        void getNoPermission();

        void getDetailNoPermission();

        void getDetailSuccess();
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {
        Observable<ListBaseEntity<List<FaceAlarmBlackEntity>>> getFaceAlarm(Long startTimeStamp,
                                                 Long endTimeStamp, int from,
                                                 String effective, int size, String libIds,
                                                 String cameraIds, String geoAddress);

        Observable<ListBaseBlackLibEntity<List<AlarmDepotEntity>>> getDepots();

        Observable<EmptyEntity> getAlarmDetailPermission(String id);
    }
}
