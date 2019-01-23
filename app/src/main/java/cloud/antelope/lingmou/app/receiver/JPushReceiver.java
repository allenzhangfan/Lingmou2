package cloud.antelope.lingmou.app.receiver;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jess.arms.integration.AppManager;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.constant.CommonConstant;
import com.lingdanet.safeguard.common.utils.LogUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.JPushUtils;
import cloud.antelope.lingmou.app.utils.SaveUtils;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.model.entity.DailyPoliceAlarmEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceAlarmBlackEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceAlarmDetailBean;
import cloud.antelope.lingmou.mvp.model.entity.JpushEntity;
import cloud.antelope.lingmou.mvp.ui.activity.DailyPoliceDetailActivity;
import cloud.antelope.lingmou.mvp.ui.activity.FaceAlarmDetailActivity;
import cloud.antelope.lingmou.mvp.ui.activity.LoginActivity;
import cloud.antelope.lingmou.mvp.ui.activity.MyReportActivity;
import cn.jpush.android.api.JPushInterface;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.jess.arms.utils.ArmsUtils.startActivity;

/**
 * JPush 接受器
 * Created by liucheng on 17/2/17.
 */

public class JPushReceiver extends BroadcastReceiver {

    private static final String TAG = "JPushReceiver";

    private Context mContext;

    private NotificationManager mNotificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        Bundle bundle = intent.getExtras();
        if (null != bundle) {
            LogUtils.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

            int notificationId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            if (!SPUtils.getInstance().getBoolean(Constants.CONFIG_MESSAGE_SWITCH, true)) {
                removeNotification(context, notificationId);
                LogUtils.d(TAG, "remove notification with notificationId = " + notificationId);
                return;
            }
            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
                LogUtils.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
                Constants.REGISTRATION_ID=regId;
                //send the Registration Id to your server...

            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                LogUtils.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
                // registerEvent(bundle);
                messageReceive(bundle);

            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                LogUtils.d(TAG, "[MyReceiver] 接收到推送下来的通知");
                LogUtils.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notificationId);
                // registerEvent(bundle);

            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
                LogUtils.d(TAG, "[MyReceiver] 用户点击打开了通知");
                /*String uid = SPUtils.getInstance().getString(CommonConstant.UID, "");
                if (!TextUtils.isEmpty(uid)) {
                    String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
                    //是报警信息
                    Gson gson = ArmsUtils.obtainAppComponentFromContext(context).gson();
                    try {
                        JpushEntity jpushEntity = gson.fromJson(extra, JpushEntity.class);
                        if (null != jpushEntity) {
                            String type = jpushEntity.getType();
                            if (!TextUtils.isEmpty(type) && "zhyy_alarm".equals(type)) {
                                String content = jpushEntity.getContent();
                                if (!TextUtils.isEmpty(content)) {
                                    FaceAlarmDetailBean faceAlarmDetailBean = gson.fromJson(content, FaceAlarmDetailBean.class);
                                    if (null != faceAlarmDetailBean) {
                                        Intent intentAlarm = new Intent(Utils.getContext(), FaceAlarmDetailActivity.class);
                                        intentAlarm.putExtra("bean", faceAlarmDetailBean);
                                        intentAlarm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        mContext.startActivity(intentAlarm);
                                    }
                                }
                            }
                        }
                    } catch (JsonSyntaxException exception) {

                    }
                }*/

            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
                LogUtils.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
                //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

            } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
                boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
                LogUtils.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
            } else {
                LogUtils.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
            }
        }
    }

    private void messageReceive(Bundle bundle) {
        if (null != mNotificationManager) {
            String uid = SPUtils.getInstance().getString(CommonConstant.UID);
            if (!TextUtils.isEmpty(uid)) {
                String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
                String title=bundle.getString(JPushInterface.EXTRA_TITLE);
                //是报警信息
                Gson gson = ArmsUtils.obtainAppComponentFromContext(mContext).gson();
                try {
                    JpushEntity jpushEntity = gson.fromJson(extra, JpushEntity.class);
                    if (null != jpushEntity) {
                        String type = jpushEntity.getType();
                        if (!TextUtils.isEmpty(type)) {
                            if ("zhyy_alarm".equals(type)) {
                                String content = jpushEntity.getContent();
                                if (!TextUtils.isEmpty(content)) {
                                    DailyPoliceAlarmEntity faceAlarmDetailBean = gson.fromJson(content, DailyPoliceAlarmEntity.class);
                                    if (null != faceAlarmDetailBean) {
                                        List<Long> alarmNotifyUsers = faceAlarmDetailBean.getAlarmNotifyUsers();
                                        if (null != alarmNotifyUsers && !alarmNotifyUsers.isEmpty()) {
                                            try {
                                                if (alarmNotifyUsers.contains(Long.parseLong(uid))) {
                                                    Intent intent = new Intent();
                                                    intent.setClass(mContext, DailyPoliceDetailActivity.class);
                                                    intent.putExtra("entity", faceAlarmDetailBean);
                                                    PendingIntent pendingIntent = PendingIntent.getActivity(mContext, (int) SystemClock.uptimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                                    Notification.Builder builder = new Notification.Builder(mContext);
                                                    builder.setSmallIcon(R.mipmap.app_icon)
                                                            .setContentTitle(title)
                                                            .setContentText(title)
                                                            .setAutoCancel(true)
                                                            .setContentIntent(pendingIntent);
                                                    Notification notification = builder.build();
                                                    notification.defaults |= Notification.DEFAULT_SOUND;
                                                    notification.defaults |= Notification.DEFAULT_VIBRATE;
                                                    mNotificationManager.notify((int) SystemClock.uptimeMillis(), notification);
                                                }
                                            } catch (NumberFormatException e) {

                                            }
                                        }

                                    }
                                }
                            } else if ("cyqz_valuable".equals(type)) {
                                Intent intent = new Intent();
                                intent.setClass(mContext, MyReportActivity.class);
                                PendingIntent pendingIntent = PendingIntent.getActivity(mContext, (int) SystemClock.uptimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                Notification.Builder builder = new Notification.Builder(mContext);
                                builder.setSmallIcon(R.mipmap.app_icon)
                                        .setContentTitle("您的线索被标记为有价值！")
                                        .setContentText("您的线索被标记为有价值!")
                                        .setAutoCancel(true)
                                        .setContentIntent(pendingIntent);
                                Notification notification = builder.build();
                                notification.defaults |= Notification.DEFAULT_SOUND;
                                notification.defaults |= Notification.DEFAULT_VIBRATE;
                                mNotificationManager.notify((int) SystemClock.uptimeMillis(), notification);
                            }else if("zhyy_logout".equals(type)){
                                resetAndLogin();
                            }

                        }
                    }
                } catch (JsonSyntaxException exception) {

                }
            }
        }
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
                intent.putExtra("showLogoutTip",true);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                LogUtils.w("okhttp", "resetLogin");
            }
        }

    }
    /**
     * 移除指定的notification
     *
     * @param context
     * @param notificationId
     */
    private void removeNotification(Context context, int notificationId) {
        if (notificationId != -1) { // 同时直接将JPush弹出的notification隐藏掉
            JPushInterface.clearNotificationById(context, notificationId);
        }
    }

    /**
     * 注册推送消息事件
     *
     * @param bundle
     */
    private void registerEvent(Bundle bundle) {
        String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
        Gson gson = ArmsUtils.obtainAppComponentFromContext(mContext).gson();
        JpushEntity jpushEntity = gson.fromJson(extra, JpushEntity.class);
        if (null != jpushEntity) {
            String type = jpushEntity.getType();
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    LogUtils.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    LogUtils.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

}
