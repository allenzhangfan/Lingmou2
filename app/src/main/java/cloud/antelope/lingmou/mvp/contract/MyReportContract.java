package cloud.antelope.lingmou.mvp.contract;

import android.app.Activity;

import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;

import cloud.antelope.lingmou.mvp.model.entity.ClueItemEntity;
import cloud.antelope.lingmou.mvp.model.entity.ContentListEntity;
import cloud.antelope.lingmou.mvp.model.entity.OperationEntity;
import cloud.antelope.lingmou.mvp.model.entity.TokenEntity;
import io.reactivex.Observable;


public interface MyReportContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {

        Activity getActivity();

        void onGetColumnLastUpdateTimeSuccess(Long time);

        void onGetColumnLastUpdateTimeError();

        void onGetClueListSuccess(ContentListEntity<ClueItemEntity> contentListEntity);

        void onGetClueListError(String errMsg);

        void onGetCyqzConfigSuccess();

        void onGetCyqzConfigError(String errMsg);
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {

        // Observable<TokenEntity> getCyqzUserToken();
        // Observable<TokenEntity> getCyqzLyToken();


        Observable<OperationEntity> getCyqzConfig();

        Observable<Long> getColumnLastUpdateTime(String columnId);

        Observable<ContentListEntity<ClueItemEntity>> getClueList(int page, int pageSize);

    }
}
