package cloud.antelope.lingmou.app.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.Gravity;

import com.antelope.sdk.utils.CLog;
import com.lingdanet.safeguard.common.utils.AppStatusTracker;
import com.lingdanet.safeguard.common.utils.Configuration;
import com.lingdanet.safeguard.common.utils.FileUtils;
import com.lingdanet.safeguard.common.utils.LogUtils;
import com.lingdanet.safeguard.common.utils.SizeUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.lingdanet.safeguard.common.utils.Utils;
import com.mob.MobSDK;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import cloud.antelope.lingmou.BuildConfig;
import cloud.antelope.lingmou.app.utils.JPushUtils;
import cloud.antelope.lingmou.common.Constants;
import cn.jpush.android.api.JPushInterface;

/**
 * 作者：安兴亚
 * 创建日期：2017/11/02
 * 邮箱：anxingya@lingdanet.com
 * 描述：TODO
 */

public class InitializeService extends IntentService {

    private static final String ACTION_INIT_WHEN_APP_CREATE = "cloud.antelope.lingmou.service.action.INIT";

    public InitializeService() {
        super("InitializeService");
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, InitializeService.class);
        intent.setAction(ACTION_INIT_WHEN_APP_CREATE);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_INIT_WHEN_APP_CREATE.equals(action)) {
                performInit();
            }
        }
    }

    private void performInit() {
        if (BuildConfig.LOG_DEBUG) {
            CLog.setLogLevel(CLog.LOG_LEVEL_VERBOSE);
        } else {
            CLog.setLogLevel(CLog.LOG_LEVEL_ASSERT);
        }
        LogUtils.d("performInit begin:" + System.currentTimeMillis());

        CrashReport.UserStrategy strategy = initStrategy();
        // 初始化bugly
        Bugly.init(Utils.getContext(), Constants.TECENT_BUGLY_APPID, BuildConfig.LOG_DEBUG, strategy);
        // CrashReport.initCrashReport(getApplicationContext(), Constants.TECENT_BUGLY_APPID, false);

        ToastUtils.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, SizeUtils.dp2px(64.0f));
        // SDKInitializer.initialize(MyApplication.getInstance());
        // 通过代码注册你的AppKey和AppSecret
        MobSDK.init(Utils.getContext(), Constants.SHARE_SDK_APPID, Constants.SHARE_SDK_APP_KEY);

        MobclickAgent.setDebugMode(BuildConfig.LOG_DEBUG);  //打开调试模式
        MobclickAgent.setScenarioType(Utils.getContext(), MobclickAgent.EScenarioType.E_UM_NORMAL); //场景类型设置
        MobclickAgent.setCatchUncaughtExceptions(false);   // 关闭友盟的日志功能

        initLog();
        initJPush();
        showDebugDBAddressLog();

        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, null);

        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);

        // OkHttpUtils.getInstance().setCertificatePinner("*.netposa.com", "sha256/ZSLptVh3HOaBIeP7TXTfo4DL8DjvZYQzmbC/RgtZPTc=");

        LogUtils.d("performInit end:" + System.currentTimeMillis());
        try {
            InputStream inputStream = getAssets().open("style.data");
            boolean orExistsDir = FileUtils.createOrExistsDir(Configuration.getRootPath() + Constants.MAP_STYLE_FILE);
            if (orExistsDir) {
                //创建成功
                File styleFile = new File(Configuration.getRootPath() + Constants.MAP_STYLE_FILE + "style.data");
                boolean isSuccess = FileUtils.writeFileFromIS(styleFile, inputStream, false);
                LogUtils.w("cxm", "isSuccess = " + isSuccess);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    private CrashReport.UserStrategy initStrategy() {
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(Utils.getContext());
        strategy.setCrashHandleCallback(new CrashReport.CrashHandleCallback() {
            public Map onCrashHandleStart(int crashType, String errorType, String errorMessage, String errorStack) {
                LinkedHashMap map = new LinkedHashMap();
                String x5CrashInfo = com.tencent.smtt.sdk.WebView.getCrashExtraMessage(Utils.getContext());
                map.put("x5crashInfo", x5CrashInfo);
                return map;
            }

            @Override
            public byte[] onCrashHandleStart2GetExtraDatas(int crashType, String errorType, String errorMessage, String errorStack) {
                try {
                    return "Extra data.".getBytes("UTF-8");
                } catch (Exception e) {
                    return null;
                }
            }
        });
        return strategy;
    }

    /**
     * 初始化日志系统.
     */
    public static void initLog() {
        LogUtils.Builder builder = new LogUtils.Builder()
                .setLogSwitch(BuildConfig.LOG_DEBUG)// 设置log总开关，包括输出到控制台和文件，默认开
                .setConsoleSwitch(BuildConfig.LOG_DEBUG)// 设置是否输出到控制台开关，默认开
                .setGlobalTag(null)// 设置log全局标签，默认为空
                // 当全局标签不为空时，我们输出的log全部为该tag，
                // 为空时，如果传入的tag为空那就显示类名，否则显示tag
                .setLogHeadSwitch(true)// 设置log头信息开关，默认为开
                .setLog2FileSwitch(false)// 打印log时是否存到文件的开关，默认关
                .setDir("")// 当自定义路径为空时，写入应用的/cache/log/目录中
                .setBorderSwitch(true)// 输出日志是否带边框开关，默认开
                .setConsoleFilter(LogUtils.V)// log的控制台过滤器，和logcat过滤器同理，默认Verbose
                .setFileFilter(LogUtils.V);// log文件过滤器，和logcat过滤器同理，默认Verbose
        LogUtils.d(builder.toString());
    }

    /**
     * 加载JPUSH
     * 如果用户关闭消息推送则关闭JPUSH
     */
    private void initJPush() {
        JPushInterface.setDebugMode(BuildConfig.LOG_DEBUG);
        JPushInterface.init(Utils.getContext());
        JPushUtils.switchJpush();
        Constants.REGISTRATION_ID = JPushInterface.getRegistrationID(this);
    }

    /**
     * 显示Android Debug Database Library的地址.
     */
    public static void showDebugDBAddressLog() {
        if (BuildConfig.DEBUG) {
            try {
                Class<?> debugDB = Class.forName("com.amitshekhar.DebugDB");
                Method getAddressLog = debugDB.getMethod("getAddressLog");
                Object value = getAddressLog.invoke(null);
                LogUtils.d(" showDebugDBAddressLog " + (String) value);
            } catch (Exception ignore) {

            }
        }
    }

}
