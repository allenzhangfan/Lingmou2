/**
 * Copyright 2017 JessYan
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cloud.antelope.lingmou.app;

import android.content.Context;
import android.content.Intent;
import android.net.ParseException;
import android.os.Message;
import android.text.TextUtils;

import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.jess.arms.integration.AppManager;
import com.lingdanet.safeguard.common.net.ApiException;
import com.lingdanet.safeguard.common.net.BadGatewayException;
import com.lingdanet.safeguard.common.net.TokenAbateException;
import com.lingdanet.safeguard.common.utils.LogUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.lingdanet.safeguard.common.utils.Utils;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.JPushUtils;
import cloud.antelope.lingmou.app.utils.SaveUtils;
import cloud.antelope.lingmou.mvp.ui.activity.LoginActivity;
import me.jessyan.rxerrorhandler.handler.listener.ResponseErrorListener;
import retrofit2.HttpException;
import timber.log.Timber;

import static com.jess.arms.utils.ArmsUtils.startActivity;

/**
 * ================================================
 * 展示 {@link ResponseErrorListener} 的用法
 * <p>
 * Created by JessYan on 04/09/2017 17:18
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public class ResponseErrorListenerImpl implements ResponseErrorListener {

    @Override
    public void handleResponseError(Context context, Throwable t) {
        Timber.tag("Catch-Error").w(t.getMessage());
        //这里不光是只能打印错误,还可以根据不同的错误作出不同的逻辑处理
        String msg = "未知错误";
        if (t instanceof SuccessNullException) {
            msg = "";
        } else if (t instanceof TokenAbateException) {
            msg = t.getMessage();
            resetAndLogin();
        } else if (t instanceof UnknownHostException) {
            msg = "服务器异常";
        } else if (t instanceof SocketTimeoutException) {
            msg = "请求网络超时";
        } else if (t instanceof ConnectException) {
            msg = "网络连接异常";
        } else if (t instanceof HttpException) {
            HttpException httpException = (HttpException) t;
//            msg = convertStatusCode(httpException);
            msg="服务器异常";
        } else if(t instanceof BadGatewayException){
            msg = t.getMessage();
        }else if (t instanceof ApiException) {
            msg = t.getMessage();
        } else if (t instanceof JsonParseException || t instanceof ParseException || t instanceof JSONException || t instanceof JsonIOException) {
            msg = "数据解析错误";
        } else if (t instanceof NoPermissionException) {
            msg = Utils.getContext().getString(R.string.hint_no_permission);
        }
        if (!TextUtils.isEmpty(msg)) {
            if (Utils.getContext().getString(R.string.token_abate).equals(msg)) {
//                ToastUtils.showLong(msg);
            } else {
                ToastUtils.showShort(msg);
            }
        }
        //            ArmsUtils.snackbarText(msg);
    }

    private String convertStatusCode(HttpException httpException) {
        String msg;
        if (httpException.code() == 500) {
            msg = "服务器发生错误";
        } else if (httpException.code() == 404) {
            msg = "请求地址不存在";
        } else if (httpException.code() == 403) {
            msg = "请求被服务器拒绝";
        } else if (httpException.code() == 307) {
            msg = "请求被重定向到其他页面";
        } else {
            msg = httpException.message();
        }
        return msg;
    }

    /**
     * 重置APP并重新登录
     */
    private void resetAndLogin() {
        synchronized (SaveUtils.class) {
            if (!SaveUtils.isInLogin) {
                SaveUtils.isInLogin = true;
                JPushUtils.deleteAlias();
                SaveUtils.clear();
                Message message = new Message();
                message.what = AppManager.KILL_ALL;
                AppManager.post(message);
                //                AppStatusTracker.getInstance().exitAllActivity();
                // 退出登录时，下次不再自动登录
                Intent intent = new Intent(Utils.getContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("showLogoutTip",true);
                startActivity(intent);
                LogUtils.w("okhttp", "resetLogin");
            }
        }
    }
}
