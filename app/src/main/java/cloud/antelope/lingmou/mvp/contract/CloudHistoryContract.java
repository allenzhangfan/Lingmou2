package cloud.antelope.lingmou.mvp.contract;

import android.app.Activity;

import com.jess.arms.mvp.IView;
import com.jess.arms.mvp.IModel;

import cloud.antelope.lingmou.mvp.model.entity.CameraNewStreamEntity;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreBaseEntity;
import io.reactivex.Observable;


public interface CloudHistoryContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        Activity getFragmentActivity();

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

        void getCoversSuccess(CameraNewStreamEntity entity);
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {

        /**
         * 获取播放的历史记录，包含相机id、播放次数以及上次播放时间等
         *
         * @param userId 用户id
         * @return {@link Observable}封装的结果值
         */
        Observable<GetKeyStoreBaseEntity> getHistories(String userId);

        Observable<CameraNewStreamEntity> getCameraCovers(Long[] cids);
    }
}
