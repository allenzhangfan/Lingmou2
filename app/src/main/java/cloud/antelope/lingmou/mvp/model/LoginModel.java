package cloud.antelope.lingmou.mvp.model;

import android.app.Application;
import android.util.Base64;

import com.google.gson.Gson;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.lingdanet.safeguard.common.utils.EncryptUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;

import java.util.List;

import javax.inject.Inject;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.common.UrlConstant;
import cloud.antelope.lingmou.mvp.contract.LoginContract;
import cloud.antelope.lingmou.mvp.model.api.LmResponseHandler;
import cloud.antelope.lingmou.mvp.model.api.service.UserService;
import cloud.antelope.lingmou.mvp.model.entity.LmBaseResponseEntity;
import cloud.antelope.lingmou.mvp.model.entity.LoginEntity;
import cloud.antelope.lingmou.mvp.model.entity.LoginRequest;
import cloud.antelope.lingmou.mvp.model.entity.MessageCodeRequest;
import cloud.antelope.lingmou.mvp.model.entity.UrlEntity;
import cloud.antelope.lingmou.mvp.model.entity.UserInfoEntity;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.OkHttpClient;


@ActivityScope
public class LoginModel extends BaseModel implements LoginContract.Model {
    private Gson mGson;
    private Application mApplication;

    @Inject
    public LoginModel(IRepositoryManager repositoryManager, Gson gson, Application application) {
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
    public Observable<List<UrlEntity>> getBaseUrls() {
        RetrofitUrlManager.getInstance().putDomain("getBaseUrls", UrlConstant.SERVER_BASE_URL);
        return mRepositoryManager.obtainRetrofitService(UserService.class)
                .getBaseUrls();
    }

    @Override
    public Observable<LmBaseResponseEntity<LoginEntity>> login(String username, String password, String deviceId, String registrationId) {
        // 对密码进行MD5加密
        // password = EncryptUtils.encryptMD5ToString(password);
        // 需要缓存的使用方式
        // return Observable.just(mRepositoryManager
        //         .obtainRetrofitService(UserService.class)
        //         .login(username, password)
        //         .compose(LmResponseHandler.<LoginEntity>handleResult()))
        //         .flatMap(new Function<Observable<LoginEntity>, ObservableSource<LoginEntity>>() {
        //             @Override
        //             public ObservableSource<LoginEntity> apply(Observable<LoginEntity> loginEntityObservable) throws Exception {
        //                 return mRepositoryManager.obtainCacheService(CommonCache.class)
        //                         .login(loginEntityObservable
        //                                 , new DynamicKey(username)
        //                                 , new EvictDynamicKey(true))
        //                         .map(listReply -> listReply.getData());
        //             }
        //         });

        // 不需要缓存的使用方式
        String enCodePassword = Base64.encodeToString(password.getBytes(), Base64.DEFAULT);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setLoginName(username);
        loginRequest.setUserPwd(enCodePassword);
        loginRequest.setLoginType(1);
        LoginRequest.UserInfoExtVoBean userInfoExtVoBean = new LoginRequest.UserInfoExtVoBean();
        userInfoExtVoBean.setDeviceType(1);
        userInfoExtVoBean.setDeviceUniqueId(deviceId);
        userInfoExtVoBean.setRegistrationId(registrationId);
        loginRequest.setUserInfoExtVo(userInfoExtVoBean);

        // loginRequest.setUserPwd(password);
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .login(loginRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
//                .compose(LmResponseHandler.<LoginEntity>handleResult())
//                .flatMap(new Function<LoginEntity, ObservableSource<UserInfoEntity>>() {
//                    @Override
//                    public ObservableSource<UserInfoEntity> apply(LoginEntity loginEntity) throws Exception {
//                        SPUtils.getInstance().put(Constants.ZHYY_TOKEN, loginEntity.getToken());
//                        return queryUserInfo();
//                    }
//                });
    }

    @Override
    public Observable<Object> sendCode(String loginName,String phoneNumber) {
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .sendMessageCode(new MessageCodeRequest(loginName,phoneNumber))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(LmResponseHandler.handleResult());
//                .flatMap(new Function<LoginEntity, ObservableSource<UserInfoEntity>>() {
//                    @Override
//                    public ObservableSource<UserInfoEntity> apply(LoginEntity loginEntity) throws Exception {
//                        SPUtils.getInstance().put(Constants.ZHYY_TOKEN, loginEntity.getToken());
//                        return queryUserInfo();
//                    }
//                });
    }

    @Override
    public Observable<UserInfoEntity> queryUserInfo() {
        RetrofitUrlManager.getInstance().putDomain(Constants.KEY_BASE_URL, SPUtils.getInstance().getString(Constants.URL_BASE));
        return mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .queryUserInfo()
                .compose(LmResponseHandler.<UserInfoEntity>handleResult());
    }
}
