package cloud.antelope.lingmou.mvp.contract;

import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;

import java.util.List;

import cloud.antelope.lingmou.mvp.model.entity.CameraNewStreamEntity;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraParentEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrgMainEntity;
import cloud.antelope.lingmou.mvp.model.entity.SetKeyStoreRequest;
import io.reactivex.Observable;


public interface DevicesContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        void getMainOrgsSuccess(List<OrgMainEntity> entityList);

        void getMainOrgsFail();

        void getMainOrgCamerasSuccess(List<OrgCameraEntity> entityList);

        void getMainOrgCameraFail();

        void getOrgNoPermission();

        void getCameraNoPermission();

        void onCameraLikeSuccess(GetKeyStoreBaseEntity entity,boolean like);

        void getCollectionsSuccess(GetKeyStoreBaseEntity entity);

        void getCoversSuccess(CameraNewStreamEntity entity);
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {
        Observable<List<OrgMainEntity>> getMainOrgs();

        Observable<OrgCameraParentEntity> getMainOrgCameras(int page, int pageSize, String[] orgIds);

        Observable<GetKeyStoreBaseEntity> getCollections(String userId);

        Observable<GetKeyStoreBaseEntity> cameraLike(String storeKey, SetKeyStoreRequest keyStoreRequest);

        Observable<CameraNewStreamEntity> getCameraCovers(Long[] cids);
    }
}
