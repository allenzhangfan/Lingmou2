package cloud.antelope.lingmou.mvp.contract;

import com.jess.arms.mvp.IView;
import com.jess.arms.mvp.IModel;

import cloud.antelope.lingmou.mvp.model.entity.DeployDetailEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceUrlSearchBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceUrlSearchEntity;
import cloud.antelope.lingmou.mvp.model.entity.NewDeployMissionRequest;
import cloud.antelope.lingmou.mvp.model.entity.StartOrPauseDeployMissionRequest;
import io.reactivex.Observable;


public interface DeployMissionDetailContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        void showData(DeployDetailEntity entity);
        void onDeleteSuccess();
        void onModifySuccess(int type);
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {
        Observable<DeployDetailEntity> getData(String id);
        Observable<String> deleteMissin(String id);
        Observable<FaceUrlSearchBaseEntity<FaceUrlSearchEntity>> getFeature(String url);
        Observable<String> modifyDeployMission(NewDeployMissionRequest request);
        Observable<String> startOrPauseMission(StartOrPauseDeployMissionRequest request);
    }
}
