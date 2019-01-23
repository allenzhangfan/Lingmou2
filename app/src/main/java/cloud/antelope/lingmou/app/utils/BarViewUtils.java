/*
 * Copyright (C) 2017 LingDaNet.Co.Ltd. All Rights Reserved.
 */

package cloud.antelope.lingmou.app.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.lingdanet.safeguard.common.utils.Utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 作者：安兴亚
 * 创建日期：2017/08/16
 * 邮箱：anxingya@lingdanet.com
 * 描述：StatusBar和NavigationBar的工具类
 */

public class BarViewUtils {

    private static final String STATUS_BAR_HEIGHT_RES_NAME = "status_bar_height";
    private static final String SHOW_NAV_BAR_RES_NAME = "config_showNavigationBar";
    private static final String NAV_BAR_HEIGHT_RES_NAME = "navigation_bar_height";
    private static final String NAV_BAR_HEIGHT_LANDSCAPE_RES_NAME = "navigation_bar_height_landscape";
    public static final String NAV_IS_MIN = "navigationbar_is_min";
    private static final String MANUFACTURER_HUAWEI = "HUAWEI";

    /**
     * 添加StatusBarView到一个LinearLayout的Activity上面.
     */
    public static void addStatusBarView(Context context, ViewGroup parent, @DrawableRes int resId) {
        View view = new View(context);
        view.setBackgroundResource(resId);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                getStatusBarHeight());
        parent.addView(view, 0, params);
    }

    /**
     * 获取是否存在NavigationBar.
     */
    @TargetApi(14)
    public static boolean hasNavBar() {
        boolean hasNavigationBar = false;
        try {
            Resources rs = Utils.getContext().getResources();
            int id = rs.getIdentifier(SHOW_NAV_BAR_RES_NAME, "bool", "android");
            if (id > 0) {
                hasNavigationBar = rs.getBoolean(id);
            }
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;
    }

    /**
     * 获取某个Android尺寸资源的数值.
     *
     * @param res 资源类对象的实例
     * @param key 尺寸资源的key
     * @return 尺寸资源的数值
     */
    public static int getInternalDimensionSize(Resources res, String key) {
        int result = 0;
        int resourceId = res.getIdentifier(key, "dimen", "android");
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 获取StatusBar的高度.
     *
     * @return StatusBar的高度
     */
    public static int getStatusBarHeight() {
        return getInternalDimensionSize(Utils.getContext().getResources(),
                STATUS_BAR_HEIGHT_RES_NAME);
    }
    /**
     * 获取NavigationBar的高度.
     *
     * @return NavigationBar的高度
     */
    public static int getNavigationBarHeight() {
        Resources res = Utils.getContext().getResources();
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (hasNavBar()) {
                String key;
                if (res.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    key = NAV_BAR_HEIGHT_RES_NAME;
                } else {
                    key = NAV_BAR_HEIGHT_LANDSCAPE_RES_NAME;
                }
                return getInternalDimensionSize(res, key);
            }
        }
        return result;
    }

    /**
     * NavigationBar是否隐藏了.
     *
     * @return true 隐藏了，false 显示了
     */
    public static boolean isNavMin() {
        if (isHUAWEI() && hasNavBar()) {
            int isNavMin = Settings.System.getInt(Utils.getContext().getContentResolver(),
                    NAV_IS_MIN, 0);
            return isNavMin == 1;
        } else {
            return true;
        }
    }

    /**
     * 是否是华为.
     */
    public static boolean isHUAWEI() {
        return android.os.Build.MANUFACTURER.equals(MANUFACTURER_HUAWEI);
    }
}
