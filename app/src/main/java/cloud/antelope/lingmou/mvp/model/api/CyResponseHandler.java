/**
 * Copyright (C) 2016 LingDaNet.Co.Ltd. All Rights Reserved.
 */
package cloud.antelope.lingmou.mvp.model.api;

import com.lingdanet.safeguard.common.net.ApiException;
import com.lingdanet.safeguard.common.net.TokenAbateException;
import com.lingdanet.safeguard.common.net.TokenInvalidException;

import cloud.antelope.lingmou.mvp.model.entity.BaseCyResponseEntity;
import cloud.antelope.lingmou.mvp.model.entity.BaseLyResponseEntity;
import cloud.antelope.lingmou.mvp.model.entity.LmBaseData;
import cloud.antelope.lingmou.mvp.model.entity.LmBaseResponseEntity;
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

public class CyResponseHandler {
    /**
     * 对结果进行预处理
     *
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<BaseCyResponseEntity<T>, T> handleResult() {

        return new ObservableTransformer<BaseCyResponseEntity<T>, T>() {
            @Override
            public Observable<T> apply(Observable<BaseCyResponseEntity<T>> upstream) {
                return upstream.flatMap(new Function<BaseCyResponseEntity<T>, ObservableSource<T>>() {
                    @Override
                    public Observable<T> apply(BaseCyResponseEntity<T> result) throws Exception {
                        if (result.success()) {
                            return createData(result.data);
                        } else if (result.tokenInvalid()) {
                            return Observable.error(new TokenInvalidException(result.message));
                        } else if (result.tokenAbate()) {
                            return Observable.error(new TokenAbateException(result.message));
                        } else {
                            return Observable.error(new ApiException(result.message));
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
