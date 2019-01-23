package cloud.antelope.lingmou.mvp.contract;

import com.jess.arms.mvp.IView;
import com.jess.arms.mvp.IModel;

import cloud.antelope.lingmou.mvp.model.entity.DeviceLoginResponseBean;
import cloud.antelope.lingmou.mvp.model.entity.EmptyEntity;
import io.reactivex.Observable;
import io.reactivex.Observer;


public interface SoloContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {

        void onLoginDeviceSuccess(DeviceLoginResponseBean entity);

        void onLoginDeviceError();
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {

        Observable<DeviceLoginResponseBean> loginDevice(int intCid, String sn, String brand, String signature);

        Observable<EmptyEntity> heart(long time);

        Observable<EmptyEntity> uploadLocation(String cid, double longitude, double lngitude);
    }
}
