package cloud.antelope.lingmou.app.utils;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.lingdanet.safeguard.common.utils.FileUtils;
import com.lingdanet.safeguard.common.utils.ImageUtils;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 作者：安兴亚
 * 创建日期：2017/05/26
 * 邮箱：anxingya@lingdanet.com
 * 描述：保存一张图片
 */

public class SaveImageTask extends AsyncTask<String, Void, Boolean> {


    public static final String REQUEST_METHOD = "GET";
    public static final int CONNECT_TIME_OUT = 5000;
    public static final int RESPONSE_OK = 200;


    private String imgUrl;
    private String mSavedPath;
    private OnSaveImageSuccessListener mOnSaveImageSuccessListener;

    public void setOnSaveImageSuccessListener(
            OnSaveImageSuccessListener onSaveImageSuccessListener) {
        this.mOnSaveImageSuccessListener = onSaveImageSuccessListener;
    }

    public void setSavedPath(String savedPath) {
        mSavedPath = savedPath;
    }

    @Override
    protected Boolean doInBackground(String... params) {

        if (params != null && params.length > 0) {
            imgUrl = params[0];
        } else {
            return false;
        }
        try {
            URL url = new URL(imgUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(REQUEST_METHOD);
            conn.setConnectTimeout(CONNECT_TIME_OUT);
            if (conn.getResponseCode() == RESPONSE_OK) {
                if (!TextUtils.isEmpty(mSavedPath)) {
                    if (FileUtils.createOrExistsFile(mSavedPath)) {
                        return ImageUtils.save(conn.getInputStream(), mSavedPath);
                    }
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (mOnSaveImageSuccessListener != null) {
            if (result) {
                mOnSaveImageSuccessListener.onSaveSuccess(imgUrl, mSavedPath);
            } else {
                mOnSaveImageSuccessListener.onSaveFail();
            }
        }
    }

    public interface OnSaveImageSuccessListener {
        void onSaveSuccess(String imgUrl, String savedPath);

        void onSaveFail();

    }
}
