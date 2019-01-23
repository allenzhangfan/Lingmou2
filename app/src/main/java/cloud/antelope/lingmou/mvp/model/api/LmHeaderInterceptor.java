/**
 * Copyright (C) 2016 LingDaNet.Co.Ltd. All Rights Reserved.
 */
package cloud.antelope.lingmou.mvp.model.api;

import android.text.TextUtils;

import com.lingdanet.safeguard.common.constant.CommonConstant;
import com.lingdanet.safeguard.common.utils.LogUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.Set;

import cloud.antelope.lingmou.common.Constants;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 作者：赵炜
 * 创建日期：2016/9/23
 * 邮箱：zhaowei0920@lingdanet.com
 * 描述：Header 拦截器,增加token统一处理
 */

public class LmHeaderInterceptor implements Interceptor {

    private static final String TAG = "ImageHeaderInterceptor";

    private Map<String, String> headers;
    private static final String COOKIE = "Cookie";
    private static final String DATE = "Date";
    private static final String AUTHORIZATION = "Authorization";

    public LmHeaderInterceptor(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = null;
        synchronized (this) {
            Request request = chain.request();
            Request.Builder builder = request.newBuilder();
            String zhyyToken = SPUtils.getInstance().getString(Constants.ZHYY_TOKEN);
            if (headers != null) {
                LogUtils.d("LmHeaderInterceptor", zhyyToken);
                headers.put("user-agent", "Android");
                headers.put(AUTHORIZATION, zhyyToken);
                if (headers.size() > 0) {
                    Set<String> keys = headers.keySet();
                    for (String headerKey : keys) {
                        builder.addHeader(headerKey, headers.get(headerKey)).build();
                    }
                }
                response = chain.proceed(builder.build());
            } else {
                response = chain.proceed(builder.build());
            }
        }
        return response;
    }
}
