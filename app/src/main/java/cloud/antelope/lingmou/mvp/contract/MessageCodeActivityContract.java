package cloud.antelope.lingmou.mvp.contract;

import android.app.Activity;

import com.jess.arms.mvp.IView;
import com.jess.arms.mvp.IModel;

import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.LmBaseResponseEntity;
import cloud.antelope.lingmou.mvp.model.entity.LoginEntity;
import cloud.antelope.lingmou.mvp.model.entity.SoldierInfoEntity;
import cloud.antelope.lingmou.mvp.model.entity.UserInfoEntity;
import io.reactivex.Observable;


public interface MessageCodeActivityContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        void onSendMessageCodeSuccess();
        void onLoginSuccess();
        Activity getActivity();
        void getSoldierInfoSuccess(SoldierInfoEntity entity);
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {
        Observable<UserInfoEntity> queryUserInfo();
        Observable<SoldierInfoEntity> getSoldierInfo();
        Observable<Object> sendCode(String loginName,String phoneNumber);
        Observable<LmBaseResponseEntity<LoginEntity>> login(String loginName, String psw, String code,String deviceId, String registrationId);
    }
}
