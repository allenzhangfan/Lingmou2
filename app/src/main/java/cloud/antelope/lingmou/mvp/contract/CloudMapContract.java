package cloud.antelope.lingmou.mvp.contract;

import android.app.Activity;

import com.jess.arms.mvp.IView;
import com.jess.arms.mvp.IModel;

import cloud.antelope.lingmou.mvp.model.entity.CameraNewStreamEntity;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraParentEntity;
import cloud.antelope.lingmou.mvp.model.entity.SetKeyStoreRequest;
import io.reactivex.Observable;


public interface CloudMapContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        Activity getFragmentActivity();

        void onCameraLikeSuccess(GetKeyStoreBaseEntity entity,boolean like);

        void getCoversSuccess(CameraNewStreamEntity entity);

        void getCollectionsSuccess(GetKeyStoreBaseEntity entity);

        void getAllCamerasSuccess(OrgCameraParentEntity entity);
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {
        Observable<OrgCameraParentEntity> getAllCameras();
        Observable<GetKeyStoreBaseEntity> getCollections(String userId);
        Observable<GetKeyStoreBaseEntity> cameraLike(String storeKey, SetKeyStoreRequest keyStoreRequest);
        Observable<CameraNewStreamEntity> getCameraCovers(Long[] cids);
    }
}
