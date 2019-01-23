package cloud.antelope.lingmou.app.utils;

import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * 作者：安兴亚
 * 创建日期：2017/11/20
 * 邮箱：anxingya@lingdanet.com
 * 描述：TODO
 */

public class AnimationUtils {

    /**
     * 从控件所在位置移动到控件的底部
     *
     * @return
     */
    public static TranslateAnimation moveToViewBottom(int duration, boolean fillAfter) {
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                0.0f, Animation.RELATIVE_TO_PARENT, 1.0f);
        animation.setDuration(duration);
        animation.setFillAfter(fillAfter);//动画出来控件可以点击
        return animation;
    }

    /**
     * 从控件所在位置移动到控件的底部
     *
     * @return
     */
    public static TranslateAnimation moveToViewBottom(int duration, boolean fillAfter, Animation.AnimationListener listener) {
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                0.0f, Animation.RELATIVE_TO_PARENT, 1.0f);
        animation.setDuration(duration);
        animation.setFillAfter(fillAfter);//动画出来控件可以点击
        animation.setAnimationListener(listener);
        return animation;
    }

    /**
     * 从控件的底部移动到控件所在位置
     *
     * @return
     */
    public static TranslateAnimation moveToViewLocation(int duration, boolean fillAfter) {
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                1.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
        animation.setDuration(duration);
        animation.setFillAfter(fillAfter);//动画出来控件可以点击
        return animation;
    }

    /**
     * 从控件的底部移动到控件所在位置
     *
     * @return
     */
    public static TranslateAnimation moveToViewLocation(int duration, boolean fillAfter, Animation.AnimationListener listener) {
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                1.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
        animation.setDuration(duration);
        animation.setFillAfter(fillAfter);//动画出来控件可以点击
        animation.setAnimationListener(listener);
        return animation;
    }
}
