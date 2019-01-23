package cloud.antelope.lingmou.mvp.contract;

import android.app.Activity;

import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;

import java.util.List;

import cloud.antelope.lingmou.mvp.model.entity.LmBaseResponseEntity;
import cloud.antelope.lingmou.mvp.model.entity.LoginEntity;
import cloud.antelope.lingmou.mvp.model.entity.UrlEntity;
import cloud.antelope.lingmou.mvp.model.entity.UserInfoEntity;
import io.reactivex.Observable;


public interface LoginContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {

        void gotoMainActivity();

        Activity getActivity();

        void getUrlsSuccess(List<UrlEntity> entities);

        void onSendMessageCodeSuccess(String phoneNumber);
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {
        Observable<List<UrlEntity>> getBaseUrls();

        Observable<LmBaseResponseEntity<LoginEntity>> login(String username, String password, String deviceId, String registrationId);

        Observable<Object> sendCode(String loginName,String phoneNumber);

        Observable<UserInfoEntity> queryUserInfo();
    }
}
