package cloud.antelope.lingmou.mvp.contract;

import android.app.Activity;

import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;

import java.util.ArrayList;
import java.util.List;

import cloud.antelope.lingmou.mvp.model.entity.BaseListEntity;
import cloud.antelope.lingmou.mvp.model.entity.BodyDepotEntity;
import cloud.antelope.lingmou.mvp.model.entity.DailyPoliceAlarmEntity;
import cloud.antelope.lingmou.mvp.model.entity.DeviceIdListRequest;
import cloud.antelope.lingmou.mvp.model.entity.FaceDepotEntity;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.LmBaseResponseEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;
import cloud.antelope.lingmou.mvp.model.entity.SearchBean;
import cloud.antelope.lingmou.mvp.model.entity.SetKeyStoreRequest;
import io.reactivex.Observable;


public interface SearchListContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        Activity getActivity();
        void showList(List<SearchBean> list);
        void showMore(List<SearchBean> list);
        void onError();
        void onCameraLikeSuccess(GetKeyStoreBaseEntity entity,boolean like);
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {
        Observable<BaseListEntity<OrgCameraEntity>> searchDevices(Integer deviceState, Integer deviceType, String deviceName, Integer pageNo, Integer pageSize);
        Observable<BaseListEntity<DailyPoliceAlarmEntity>> getAlarms(Integer pageNo, Integer pageSize, String keyword,
                                                                     Integer taskType, Integer alarmOperationType,
                                                                     Integer alarmScope, Long startTime, Long endTime);
        Observable<LmBaseResponseEntity<ArrayList<String>>> getDeviceIdList(DeviceIdListRequest request);

        Observable<FaceDepotEntity> getFaceDepot(Long tartTimeStamp, Long endTimeStamp, int size, List<String> cameraTags,
                                                 List<String> faceTags, List<String> emptyTags, List<String> cameraIds, String minId,
                                                 String minCaptureTime, String pageType);
        Observable<BodyDepotEntity> getBodyDepot(int size, Long startTime, Long endTime,
                                                 List<String> cameraTags, List<String> bodyTags, List<String> cameraIds,
                                                 String minId,
                                                 String minCaptureTime, String pageType);

        Observable<GetKeyStoreBaseEntity> cameraLike(String storeKey, SetKeyStoreRequest keyStoreRequest);
    }
}
