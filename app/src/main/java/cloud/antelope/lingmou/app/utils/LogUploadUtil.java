package cloud.antelope.lingmou.app.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.lingdanet.safeguard.common.https.OkHttpUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.TimeUtils;

import java.io.IOException;
import java.util.Timer;

import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.model.entity.LogUploadRequest;
import cloud.antelope.lingmou.mvp.model.entity.UpdateLastLoginTimeRequest;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import timber.log.Timber;

public class LogUploadUtil {
    public static void uploadLog(LogUploadRequest logUploadRequest) {
        if (TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.URL_BASE)) || TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.ZHYY_TOKEN)))
            return;
        Timber.e(logUploadRequest.toString());
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json"), new Gson().toJson(logUploadRequest));
        Request request = new Request.Builder()
                .url(SPUtils.getInstance().getString(Constants.URL_BASE) + "/api/logServer/saveLog")
                .addHeader("Authorization", SPUtils.getInstance().getString(Constants.ZHYY_TOKEN))
                .addHeader("Accept", "*/*")
                .addHeader("user-agent", "Android")
                .addHeader("Content-type", "application/json")
                .post(requestBody)
                .build();
        OkHttpUtils.getInstance().getOkHttpClient().newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                    }
                });
    }
}
