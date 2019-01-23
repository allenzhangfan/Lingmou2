package cloud.antelope.lingmou.mvp.contract;

import android.app.Activity;

import com.jess.arms.mvp.IView;
import com.jess.arms.mvp.IModel;

import java.util.List;

import cloud.antelope.lingmou.mvp.model.entity.FaceDepotEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceFeatureInfoEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceNewEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceRecorgBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreBaseEntity;
import io.reactivex.Observable;


public interface FaceDepotContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        Activity getActivity();
        void getFaceDepotSuccess(FaceDepotEntity faceDepotEntity);
        void getFaceError();
        void getNoPermission();

        void getCollectionsSuccess(GetKeyStoreBaseEntity entity);

        void getCollectionsFail();

    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {
        Observable<FaceDepotEntity> getFaceDepot(Long tartTimeStamp, Long EndTimeStamp, int size, List<String> cameraTags,List<String> noCameraTags,
                                                 List<String> faceTags, List<String> emptyTags, List<String> cameraIds,String minId,
                                                 String minCaptureTime, String pageType);

        Observable<GetKeyStoreBaseEntity> getCollections(String userId);

    }
}
