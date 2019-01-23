package cloud.antelope.lingmou.mvp.contract;

import com.jess.arms.mvp.IView;
import com.jess.arms.mvp.IModel;

import cloud.antelope.lingmou.mvp.model.entity.CameraNewStreamEntity;
import cloud.antelope.lingmou.mvp.model.entity.CollectRequest;
import cloud.antelope.lingmou.mvp.model.entity.CollectResponse;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.HistoryKVStoreRequest;
import cloud.antelope.lingmou.mvp.model.entity.ObjectEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;
import cloud.antelope.lingmou.mvp.model.entity.PlayEntity;
import cloud.antelope.lingmou.mvp.model.entity.RecordLyCameraBean;
import cloud.antelope.lingmou.mvp.model.entity.SetKeyStoreRequest;
import cloud.antelope.lingmou.mvp.model.entity.TokenEntity;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public interface VideoPlayContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        void getStreamSuccess(PlayEntity playEntity);

        void getStreamFailed();

        void onCameraLikeSuccess(GetKeyStoreBaseEntity entity);

        void onCameraLikeFail();

        void getRecordTimeFail();

        void getRecordTimeSuccess(RecordLyCameraBean recordSection);

        void getRecordToken(String token);

        /**
         * 获取历史记录成功，包含相机id、播放次数以及上次播放时间等
         *
         * @param entity key-value实体，里面包含具体值
         */
        void getHistoriesSuccess(GetKeyStoreBaseEntity entity);

        /**
         * 获取历史记录失败，目前暂时未做任何处理，预留接口
         */
        void getHistoriesFail();

        /**
         * put历史记录成功
         */
        void putHistoriesSuccess();

        void getDeviceInfoSuccess(OrgCameraEntity entity);

        void getDeviceInfoFail();

        void onCollectSuccess();
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {
        Observable<String> getLyyToken(Long cameraId);

        Observable<CameraNewStreamEntity> getPlayerUrl(Long cid);

        Observable<GetKeyStoreBaseEntity> cameraLike(String storeKey, SetKeyStoreRequest keyStoreRequest);

        Observable<RecordLyCameraBean> getRecordTimes(String cid, String token, long startTime, long endTime);

        /**
         * 获取播放的历史记录，包含相机id、播放次数以及上次播放时间等
         *
         * @param userId 用户id
         * @return {@link Observable}封装的结果值
         */
        Observable<GetKeyStoreBaseEntity> getHistories(String userId);

        /**
         * 保存播放的历史记录，包含相机id、播放次数以及上次播放时间等
         *
         * @param storeKey 存储的key值，固定为"APP_HISTORY"
         * @param request  历史记录请求
         * @return {@link Observable}封装的结果值
         */
        Observable<GetKeyStoreBaseEntity> putHistories(String storeKey, HistoryKVStoreRequest request);

        /**
         *  获取设备详细信息
         * @param cId 摄像机的CID
         * @return
         */
        Observable<OrgCameraEntity> getDeviceInfo(String cId);

        Observable<TokenEntity> getStorageToken();
        Observable<ObjectEntity> uploadPic(String accessToken, String size, RequestBody requestBody, MultipartBody.Part part);
        Observable<CollectResponse> collect(CollectRequest request);
    }
}
