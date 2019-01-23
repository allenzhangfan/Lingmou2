package cloud.antelope.lingmou.app.utils;

import android.text.TextUtils;

import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.Utils;

import cloud.antelope.lingmou.common.Constants;
import cn.jpush.android.api.JPushInterface;

import static cloud.antelope.lingmou.app.utils.TagAliasOperatorHelper.ACTION_DELETE;
import static cloud.antelope.lingmou.app.utils.TagAliasOperatorHelper.ACTION_GET;
import static cloud.antelope.lingmou.app.utils.TagAliasOperatorHelper.ACTION_SET;
import static cloud.antelope.lingmou.app.utils.TagAliasOperatorHelper.sequence;

/**
 * 作者：安兴亚
 * 创建日期：2017/09/26
 * 邮箱：anxingya@lingdanet.com
 * 描述：TODO
 */

public class JPushUtils {

    /**
     * 根据App的设置，开启或关闭极光推送
     */
    public static void switchJpush() {
        boolean openPush = SPUtils.getInstance().getBoolean(Constants.CONFIG_MESSAGE_SWITCH, true);
        if (openPush) {
            resumeJPush();
        } else {
            stopJpush();
        }
    }

    /**
     * 强制开启极光推送
     */
    public static void resumeJPush() {
        if (JPushInterface.isPushStopped(Utils.getContext())) {
            JPushInterface.resumePush(Utils.getContext());
        }
    }

    /**
     * 强制关闭极光推送
     */
    public static void stopJpush() {
        if (!JPushInterface.isPushStopped(Utils.getContext())) {
            JPushInterface.stopPush(Utils.getContext());
        }
    }

    /**
     * 设置极光推送的别名
     */
    public static void setAlias(String alias) {
        onAliasAction(alias, ACTION_SET);
    }

    public static void deleteAlias() {
        onAliasAction("", ACTION_DELETE);
    }

    public static void getAlias() {
        onAliasAction("", ACTION_GET);
    }

    private static void onAliasAction(String alias, int action) {
        boolean isAliasAction = false;
        switch (action) {
            //设置alias
            case ACTION_SET:
                if (TextUtils.isEmpty(alias)) {
                    return;
                }
                isAliasAction = true;
                break;
            //获取alias
            case ACTION_GET:
                isAliasAction = true;
                break;
            //删除alias
            case ACTION_DELETE:
                isAliasAction = true;
                break;
            default:
                return;
        }
        TagAliasOperatorHelper.TagAliasBean tagAliasBean = new TagAliasOperatorHelper.TagAliasBean();
        tagAliasBean.action = action;
        sequence++;
        if (isAliasAction) {
            tagAliasBean.alias = alias;
        }
        tagAliasBean.isAliasAction = isAliasAction;
        TagAliasOperatorHelper.getInstance().handleAction(Utils.getContext(), sequence, tagAliasBean);
    }
}
