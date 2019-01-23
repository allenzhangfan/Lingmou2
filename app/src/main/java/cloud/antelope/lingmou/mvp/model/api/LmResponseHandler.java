/**
 * Copyright (C) 2016 LingDaNet.Co.Ltd. All Rights Reserved.
 */
package cloud.antelope.lingmou.mvp.model.api;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lingdanet.safeguard.common.net.ApiException;
import com.lingdanet.safeguard.common.net.BadGatewayException;
import com.lingdanet.safeguard.common.net.TokenAbateException;
import com.lingdanet.safeguard.common.utils.LogUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.Utils;

import org.json.JSONObject;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.NoPermissionException;
import cloud.antelope.lingmou.app.SuccessNullException;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.model.entity.BaseLyResponseEntity;
import cloud.antelope.lingmou.mvp.model.entity.EmptyEntity;
import cloud.antelope.lingmou.mvp.model.entity.LmBaseResponseEntity;
import io.jsonwebtoken.Jwts;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 作者：赵炜
 * 创建日期：2016/9/23
 * 邮箱：zhaowei0920@lingdanet.com
 * 描述：
 */

public class LmResponseHandler {

    private static final byte[] SECRET="3d990d2276917dfac04467df11fff26d".getBytes();
    /**
     * 对结果进行预处理
     *
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<LmBaseResponseEntity<T>, T> handleResult() {

        return new ObservableTransformer<LmBaseResponseEntity<T>, T>() {
            @Override
            public Observable<T> apply(Observable<LmBaseResponseEntity<T>> upstream) {
                return upstream.flatMap(new Function<LmBaseResponseEntity<T>, ObservableSource<T>>() {
                    @Override
                    public Observable<T> apply(LmBaseResponseEntity<T> result) throws Exception {
                        if (result.isSuccess()) {
                            if (null == result.result) {
                                return Observable.error(new SuccessNullException(""));
                            } else {
                                return createData(result.result);
                            }
                        } else if (result.isInvalidate()) {
                            String zhyyToken = SPUtils.getInstance().getString(Constants.ZHYY_TOKEN);
                            if (!TextUtils.isEmpty(zhyyToken)) {
                                Object body = Jwts.parser().setSigningKey(SECRET).parse(zhyyToken).getBody();
                                String toJson = new Gson().toJson(body);
                                JSONObject jsonObject = new JSONObject(toJson);
                                long extTime = jsonObject.optLong("ext");
                                long currentTimeMillis = System.currentTimeMillis();
                                if (currentTimeMillis < extTime) {
                                    return Observable.error(new TokenAbateException(Utils.getContext().getString(R.string.token_abate)));
                                } else {
                                    //过期
                                    return Observable.error(new TokenAbateException(Utils.getContext().getString(R.string.token_invalid)));
                                }
                            }
                            //过期
                            return Observable.error(new TokenAbateException(Utils.getContext().getString(R.string.token_invalid)));
                        } else if(result.isGatewayBad()) {
                            return Observable.error(new BadGatewayException(Utils.getContext().getString(R.string.bad_gateway)));
                        }  else if(result.isNoPermission()){
                            return  Observable.error(new NoPermissionException(""));
                        } else {
                            String message = result.message;
                            return Observable.error(new ApiException(message));
                        }
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        };

    }

    /**
     * 对结果进行预处理
     *
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<BaseLyResponseEntity, T> handleNoDataResult() {

        return new ObservableTransformer<BaseLyResponseEntity, T>() {
            @Override
            public Observable<T> apply(Observable<BaseLyResponseEntity> upstream) {
                return upstream.flatMap(new Function<BaseLyResponseEntity, ObservableSource<T>>() {
                    @Override
                    public Observable<T> apply(BaseLyResponseEntity result) throws Exception {
                        if (result.isSuccess()) {
                                return Observable.error(new SuccessNullException(result.message));
                        } else {
                            String message = result.message;
                            return Observable.error(new ApiException(message));
                        }
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        };

    }

    /**
     * 创建成功的数据
     *
     * @param data
     * @param <T>
     * @return
     */
    private static <T> Observable<T> createData(final T data) {
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> emitter) throws Exception {
                try {
                    emitter.onNext(data);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }
}
