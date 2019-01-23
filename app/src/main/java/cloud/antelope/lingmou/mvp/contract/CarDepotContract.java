package cloud.antelope.lingmou.mvp.contract;

import android.app.Activity;

import com.jess.arms.mvp.IView;
import com.jess.arms.mvp.IModel;

import java.util.ArrayList;
import java.util.List;

import cloud.antelope.lingmou.mvp.model.entity.CarBrandBean;
import cloud.antelope.lingmou.mvp.model.entity.CarDepotEntity;
import cloud.antelope.lingmou.mvp.model.entity.CarDepotListRequest;
import cloud.antelope.lingmou.mvp.model.entity.CarInfo;
import cloud.antelope.lingmou.mvp.model.entity.CarListResponse;
import cloud.antelope.lingmou.mvp.model.entity.DeployListRequest;
import cloud.antelope.lingmou.mvp.model.entity.DeployResponse;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreBaseEntity;
import io.reactivex.Observable;


public interface CarDepotContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        Activity getActivity();
        void showList(List<CarDepotEntity> list);
        void showMore(List<CarDepotEntity> list);
        void onError();
        void onGetInfoSuccess(CarInfo carInfo);
        void getCollectionsSuccess(GetKeyStoreBaseEntity entity);
        void getCollectionsFail();

    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {
        Observable<CarInfo> getCarInfo();
        Observable<CarListResponse> getCarList(CarDepotListRequest request);
        Observable<GetKeyStoreBaseEntity> getCollections(String userId);
    }
}
