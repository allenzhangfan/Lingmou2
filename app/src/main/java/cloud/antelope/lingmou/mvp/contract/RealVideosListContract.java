package cloud.antelope.lingmou.mvp.contract;

import android.app.Activity;

import com.jess.arms.mvp.IView;
import com.jess.arms.mvp.IModel;

import java.util.List;

import cloud.antelope.lingmou.mvp.model.entity.BaseListEntity;
import cloud.antelope.lingmou.mvp.model.entity.CameraNewStreamEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;
import io.reactivex.Observable;


public interface RealVideosListContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        Activity getFragmentActivity();
        void getAllCamerasSuccess(List<OrgCameraEntity> list);
        void getAllCameraFail();
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {
        Observable<BaseListEntity<OrgCameraEntity>> getAllCameras(Integer deviceState, Integer deviceType,
                                                                  String deviceName, Integer pageNo, Integer pageSize);
    }
}
