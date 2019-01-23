package cloud.antelope.lingmou.mvp.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.lingdanet.safeguard.common.utils.Utils;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

import cloud.antelope.lingmou.R;

public class X5WebView extends WebView {

    @SuppressLint("SetJavaScriptEnabled")
    public X5WebView(Context context, AttributeSet attr) {
        super(context, attr);
        // this.setWebChromeClient(chromeClient);
        // WebStorage webStorage = WebStorage.getInstance();
        initWebViewSettings();
        // this.getView().setClickable(true);
        this.getView().setOverScrollMode(View.OVER_SCROLL_ALWAYS);
    }

    private void initWebViewSettings() {
        WebSettings webSetting = this.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        // webSetting.setAllowFileAccess(true);
        // webSetting.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setBlockNetworkImage(true);
        webSetting.setUseWideViewPort(true);
        // webSetting.setSupportMultipleWindows(true);
        webSetting.setLoadWithOverviewMode(true);
        // webSetting.setAppCacheEnabled(true);
        // webSetting.setDatabaseEnabled(false);
        // webSetting.setDomStorageEnabled(false);
        // webSetting.setGeolocationEnabled(true);
        // webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        // webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);

        // this.getSettingsExtension().setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);//extension
        // settings 的设计
    }

    public X5WebView(Context context) {
        this(context, null);
        setBackgroundColor(getResources().getColor(R.color.white));
    }

    /**
     * 将cookie同步到WebView
     *
     * @param url   WebView要加载的url
     * @param token 要同步的token
     * @param uid   要同步的uid
     * @return true  同步cookie成功，false 同步cookie失败
     */
    public static boolean syncCookie(String url, String token, String uid) {
        try {
            CookieSyncManager.createInstance(Utils.getContext());//创建一个cookie管理器
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.removeSessionCookie();// 移除以前的cookie
            cookieManager.removeAllCookie();
            // String cookieValue = "token=" + SPUtils.getInstance().getString(CommonConstant.TOKEN);
            cookieManager.setCookie(url, "token=" + token);//为url设置cookie
            if (!TextUtils.isEmpty(uid)) {
                cookieManager.setCookie(url, "uid=" + uid);
            }
            CookieSyncManager.getInstance().sync();//同步cookie
            String newCookie = cookieManager.getCookie(url);
            return TextUtils.isEmpty(newCookie) ? false : true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
