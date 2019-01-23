package cloud.antelope.lingmou.mvp.contract;

import android.app.Activity;

import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;

import java.util.List;

import cloud.antelope.lingmou.mvp.model.entity.DeployListRequest;
import cloud.antelope.lingmou.mvp.model.entity.DeployResponse;
import cloud.antelope.lingmou.mvp.model.entity.StartOrPauseDeployMissionRequest;
import io.reactivex.Observable;


public interface DeployListContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        Activity getActivity();
        void showList(List<DeployResponse.ListBean> list);
        void showMore(List<DeployResponse.ListBean> list);
        void onError();
        void onDeleteSuccess(int position);
        void onModifySuccess(int position,int newStatus);
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {
        Observable<DeployResponse> getList(DeployListRequest request);
        Observable<String> deleteMissin(String id);
        Observable<String> startOrPauseMission(StartOrPauseDeployMissionRequest request);
    }
}