package cloud.antelope.lingmou.mvp.contract;

import android.app.Activity;

import com.jess.arms.mvp.IView;
import com.jess.arms.mvp.IModel;

import cloud.antelope.lingmou.mvp.model.entity.FaceUrlSearchBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceUrlSearchEntity;
import cloud.antelope.lingmou.mvp.model.entity.NewDeployMissionRequest;
import cloud.antelope.lingmou.mvp.model.entity.ObjectEntity;
import cloud.antelope.lingmou.mvp.model.entity.TokenEntity;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public interface NewDeployMissionContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        void onCreateSuccess(String id);
        void onUploadPictureSuccess(String url);
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {
        Observable<TokenEntity> getStorageToken();
        Observable<ObjectEntity> uploadPic(String accessToken, String size, RequestBody requestBody, MultipartBody.Part part);
        Observable<FaceUrlSearchBaseEntity<FaceUrlSearchEntity>> getFeature(String url);
        Observable<String> createDeployMission(NewDeployMissionRequest request);

    }
}
