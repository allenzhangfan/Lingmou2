package cloud.antelope.lingmou.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.lingdanet.safeguard.common.constant.CommonConstant;
import com.lingdanet.safeguard.common.net.RequestUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.common.UrlConstant;
import cloud.antelope.lingmou.mvp.contract.ReportEditContract;
import cloud.antelope.lingmou.mvp.model.api.Api;
import cloud.antelope.lingmou.mvp.model.api.CyResponseHandler;
import cloud.antelope.lingmou.mvp.model.api.LmResponseHandler;
import cloud.antelope.lingmou.mvp.model.api.service.CyqzService;
import cloud.antelope.lingmou.mvp.model.api.service.UserService;
import cloud.antelope.lingmou.mvp.model.entity.EmptyEntity;
import cloud.antelope.lingmou.mvp.model.entity.EventEntity;
import cloud.antelope.lingmou.mvp.model.entity.TokenEntity;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


@ActivityScope
public class ReportEditModel extends BaseModel implements ReportEditContract.Model {
    private Gson mGson;
    private Application mApplication;

    @Inject
    public ReportEditModel(IRepositoryManager repositoryManager, Gson gson, Application application) {
        super(repositoryManager);
        this.mGson = gson;
        this.mApplication = application;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<EventEntity> submitClue(EventEntity message, ArrayList<MultipartBody.Part> files) {
        return getCyqzLyToken().flatMap(new Function<TokenEntity, ObservableSource<EventEntity>>() {
            @Override
            public ObservableSource<EventEntity> apply(TokenEntity tokenEntity) throws Exception {
                String token = tokenEntity.getToken();
                SPUtils.getInstance().put(Constants.CONFIG_CYQZ_LY_TOKEN, token);
                EventEntity.BodyBean body = message.getBody();
                body.setUpLoadToken(token);
                body.setToken(SPUtils.getInstance().getString(Constants.ZHYY_TOKEN));
                RequestBody messagePart = RequestUtils.createPartFromString(new Gson().toJson(message));
                RetrofitUrlManager.getInstance().putDomain("submitClue", SPUtils.getInstance().getString(Constants.URL_EVENT_STORAGE));
                return mRepositoryManager
                        .obtainRetrofitService(UserService.class)
                        .submitClue(token, messagePart, files)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        });
    }

    @Override
    public Observable<EmptyEntity> uploadClueMessage(EventEntity eventEntity) {
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(CyqzService.class)
                .uploadClueMessage(eventEntity)
                .compose(LmResponseHandler.<EmptyEntity>handleResult());
    }

    /*public Observable<TokenEntity> getCyqzUserToken() {
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .getCyqzUserToken()
                .compose(LmResponseHandler.<TokenEntity>handleResult())
                .flatMap(new Function<TokenEntity, ObservableSource<TokenEntity>>() {
                    @Override
                    public ObservableSource<TokenEntity> apply(TokenEntity tokenEntity) throws Exception {
                        SPUtils.getInstance().put(CommonConstant.TOKEN, tokenEntity.getToken());
                        return getCyqzLyToken();
                    }
                });
    }*/

    public Observable<TokenEntity> getCyqzLyToken() {
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .getCyqzLyToken()
                .compose(LmResponseHandler.<TokenEntity>handleResult());
    }
}
