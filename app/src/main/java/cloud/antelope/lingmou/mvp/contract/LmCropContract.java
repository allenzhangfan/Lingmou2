package cloud.antelope.lingmou.mvp.contract;

import com.jess.arms.mvp.IView;
import com.jess.arms.mvp.IModel;
import cloud.antelope.lingmou.mvp.model.entity.FaceKeyEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceNewEntity;

import java.io.File;
import java.util.List;

import cloud.antelope.lingmou.mvp.model.entity.FaceRecorgBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceUrlSearchBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceUrlSearchEntity;
import cloud.antelope.lingmou.mvp.model.entity.ObjectEntity;
import cloud.antelope.lingmou.mvp.model.entity.TokenEntity;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public interface LmCropContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {

        void onFaceSuccess(List<FaceNewEntity> userEntity);

        void onFaceFail(String msg);

        void onGetFeatureSuccess(String feature);
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {

        Observable<TokenEntity> getStorageToken();

        Observable<ObjectEntity> uploadFile(String accessToken, String size, RequestBody requestBody, MultipartBody.Part part);

        Observable<FaceUrlSearchBaseEntity<FaceUrlSearchEntity>> getFaceFeature(String featureKey);

        Observable<FaceRecorgBaseEntity<FaceNewEntity>> getFaceListByFeature(Long startTime, Long endTime, int score, String feature);
    }
}
