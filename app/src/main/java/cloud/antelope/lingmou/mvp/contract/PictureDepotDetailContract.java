package cloud.antelope.lingmou.mvp.contract;

import android.app.Activity;

import com.jess.arms.mvp.IView;
import com.jess.arms.mvp.IModel;

import java.util.List;

import cloud.antelope.lingmou.mvp.model.entity.BodyFeatureInfoEntity;
import cloud.antelope.lingmou.mvp.model.entity.BodyRecorgBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.CollectRequest;
import cloud.antelope.lingmou.mvp.model.entity.CollectResponse;
import cloud.antelope.lingmou.mvp.model.entity.CollectionListBean;
import cloud.antelope.lingmou.mvp.model.entity.CollectionListRequest;
import cloud.antelope.lingmou.mvp.model.entity.CommonListResponse;
import cloud.antelope.lingmou.mvp.model.entity.FaceFeatureInfoEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceNewEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceRecorgBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreBaseEntity;
import io.reactivex.Observable;


public interface PictureDepotDetailContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        Activity getActivity();

        void getFaceListSuccess(List<FaceNewEntity> userEntity);

        void getFaceFail();

        void getFeatureSuccess(float[] feature);

        void getBodyListSuccess(List<FaceNewEntity> userEntity);

        void onCollectSuccess(int position,String collectId,boolean isCancel);

        void getCollectionsSuccess(List<CollectionListBean> list);
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {
        Observable<FaceFeatureInfoEntity> getFaceFeatureInfo(String vid);

        Observable<FaceRecorgBaseEntity<FaceNewEntity>> getFaceList(Long startTime, Long endTime, int score, float[] feature, String vid);

        Observable<BodyFeatureInfoEntity> getBodyFeatureInfo(String vid);

        Observable<BodyRecorgBaseEntity<FaceNewEntity>> getBodyList(Long startTime, Long endTime, int score, float[] feature, String vid);

        Observable<CollectResponse> collect(CollectRequest request);

        Observable<CommonListResponse<CollectionListBean>> getList(CollectionListRequest request);
    }
}
