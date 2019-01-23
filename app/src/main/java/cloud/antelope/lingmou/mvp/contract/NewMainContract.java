package cloud.antelope.lingmou.mvp.contract;

import android.app.Activity;

import com.jess.arms.mvp.IView;
import com.jess.arms.mvp.IModel;

import java.util.ArrayList;
import java.util.List;

import cloud.antelope.lingmou.mvp.model.entity.CarInfo;
import cloud.antelope.lingmou.mvp.model.entity.CollectionListBean;
import cloud.antelope.lingmou.mvp.model.entity.CollectionListRequest;
import cloud.antelope.lingmou.mvp.model.entity.CommonListResponse;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraParentEntity;
import cloud.antelope.lingmou.mvp.model.entity.PushEntity;
import cloud.antelope.lingmou.mvp.model.entity.TokenEntity;
import io.reactivex.Observable;


public interface NewMainContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        void onGetCyqzLyTokenSuccess();

        Activity getActivity();

        void getAllCamerasSuccess(OrgCameraParentEntity entity);

        void getCollectionListSuccess(List<CollectionListBean> list);

        void onGetCarBrandList(CarInfo carInfo);
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {
        Observable<List<PushEntity>> getPushConfig();

        Observable<TokenEntity> getCyqzLyToken();

        Observable<OrgCameraParentEntity> getAllCameras();

        Observable<CommonListResponse<CollectionListBean>> getCollectionList(CollectionListRequest request);

        Observable<CarInfo> getCarInfo();
    }
}
