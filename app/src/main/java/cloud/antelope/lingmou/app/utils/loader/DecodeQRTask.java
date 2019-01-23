package cloud.antelope.lingmou.app.utils.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder;

/**
 * 作者：安兴亚
 * 创建日期：2017/05/26
 * 邮箱：anxingya@lingdanet.com
 * 描述：图片二维码识别的任务
 */

public class DecodeQRTask extends AsyncTask<String, Void, String> {

    public static final String REQUEST_METHOD = "GET";
    public static final int CONNECT_TIME_OUT = 5000;
    public static final int RESPONSE_OK = 200;

    private String imgUrl;
    private OnDecodeQRListener onDecodeQRListener;

    public void setOnDecodeQRListener(OnDecodeQRListener onDecodeQRListener) {
        this.onDecodeQRListener = onDecodeQRListener;
    }

    @Override
    protected String doInBackground(String... params) {
        if (params != null && params.length > 0) {
            imgUrl = params[0];
        } else {
            return null;
        }
        return QRCodeDecoder.syncDecodeQRCode(imgUrl);
    }

    @Override
    protected void onPostExecute(String result) {
        if (!TextUtils.isEmpty(result)) {
            if (onDecodeQRListener != null) {
                onDecodeQRListener.onDecodeQRSuccess(result);
            }
        }
    }

    /**
     * 根据地址获取网络图片
     *
     * @param sUrl 图片地址
     * @return
     * @throws IOException
     */
    public Bitmap getBitmap(String sUrl) {
        try {
            URL url = new URL(sUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(REQUEST_METHOD);
            conn.setConnectTimeout(CONNECT_TIME_OUT);
            if (conn.getResponseCode() == RESPONSE_OK) {
                InputStream inputStream = conn.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface OnDecodeQRListener {
        void onDecodeQRSuccess(String result);
    }
}
