package cloud.antelope.lingmou.mvp.model

import android.app.Application
import android.text.TextUtils
import cloud.antelope.lingmou.common.Constants
import cloud.antelope.lingmou.mvp.contract.KFeedbackContract
import cloud.antelope.lingmou.mvp.model.api.LmResponseHandler
import cloud.antelope.lingmou.mvp.model.api.service.UserService
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreBaseEntity
import com.google.gson.Gson
import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.integration.IRepositoryManager
import com.jess.arms.mvp.BaseModel
import com.lingdanet.safeguard.common.utils.SPUtils
import io.reactivex.Observable
import me.jessyan.retrofiturlmanager.RetrofitUrlManager
import javax.inject.Inject

@ActivityScope
class KFeedbackModel
@Inject constructor(repositoryManager: IRepositoryManager, var gson: Gson?, var application: Application?)
    : BaseModel(repositoryManager), KFeedbackContract.Model {


    override fun onDestroy() {
        super.onDestroy()
        this.gson = null
        this.application = null
    }

    override fun feedback(userId: String, phone: String, content: String): Observable<GetKeyStoreBaseEntity> {
        var storeValue = content
        if (!TextUtils.isEmpty(phone)) {
            storeValue = "$phone/$content"
        }
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE))
        return mRepositoryManager
                .obtainRetrofitService<UserService>(UserService::class.java)
                .setUserKvStore(userId, "FEED_BACK", storeValue)
                .compose(LmResponseHandler.handleResult())
    }
}