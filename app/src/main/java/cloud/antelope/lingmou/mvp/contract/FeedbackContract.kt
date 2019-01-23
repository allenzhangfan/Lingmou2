package cloud.antelope.lingmou.mvp.contract

import android.app.Activity
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreBaseEntity
import com.jess.arms.mvp.IModel
import com.jess.arms.mvp.IView
import io.reactivex.Observable

interface KFeedbackContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View : IView {
        fun getActivity(): Activity
        fun feedbackSuccess()
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model : IModel {
        fun feedback(userId: String, phone: String, content: String): Observable<GetKeyStoreBaseEntity>
    }
}