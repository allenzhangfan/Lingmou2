package cloud.antelope.lingmou.mvp.contract;

import android.app.Activity;

import com.jess.arms.mvp.IView;
import com.jess.arms.mvp.IModel;

import java.util.List;

import cloud.antelope.lingmou.mvp.model.entity.BodyDepotEntity;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreBaseEntity;
import io.reactivex.Observable;


public interface BodyDepotContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        Activity getActivity();

        void getBodyDepotSuccess(BodyDepotEntity bodyDepotEntity);

        void getBodyDepotError();

        void getNoPermission();

        void getCollectionsSuccess(GetKeyStoreBaseEntity entity);

        void getCollectionsFail();
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {
        Observable<BodyDepotEntity> getBodyDepot(int size, Long startTime, Long endTime, List<String> mCameraTags,List<String> noCameraTags,
                                                 List<String> mBodyTags, List<String> mCameraIds, String minId,
                                                 String minCaptureTime, String pageType);

        Observable<GetKeyStoreBaseEntity> getCollections(String userId);
    }
}
