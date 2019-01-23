package cloud.antelope.lingmou.mvp.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.cjt2325.cameralibrary.util.ScreenUtils;

import cloud.antelope.lingmou.app.utils.BarViewUtils;


/**
 * Author: 陈洋
 * Time: 2018/2/8 0008 下午 3:27
 * Description:
 */
public class MenuView implements View.OnTouchListener {
    private static final int ANIMTIME=300;
    private static final int SHOW=0;
    private static final int DISMISS=1;
    private Activity activity;
    private LinearLayout root;
    private View view;
    private View opacity, lucency;
    private Rect rect;
    private OnDismissListener onDismissListener;
    private boolean animShowing;
    private int height;

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public MenuView(Activity activity, View view,int height) {
        this.activity = activity;
        this.view = view;
        this.height=height;
    }

    public void dismiss() {
        showAnim(DISMISS);
    }

    public void show(View below) {
        rect = new Rect();
        below.getGlobalVisibleRect(rect);
        root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setLayoutParams(new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        lucency = new View(activity);
        lucency.setBackgroundColor(Color.argb(0, 255, 255, 255));
        lucency.setOnTouchListener(this);
        lucency.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, rect.bottom - BarViewUtils.getStatusBarHeight()));
        root.addView(lucency);

        RootView rootView = new RootView(activity);
        rootView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        opacity = new View(activity);
        opacity.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        opacity.setBackgroundColor(Color.argb(128, 0, 0, 0));
        opacity.setOnTouchListener(this);
        rootView.addView(opacity);

        if(view.getParent()!=null){
            ((ViewGroup)view.getParent()).removeView(view);
        }
        view.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.setClickable(true);
        rootView.addView(view);
        root.addView(rootView);

        WindowManager.LayoutParams mWindowLayoutParams = new WindowManager.LayoutParams();
        mWindowLayoutParams.format = PixelFormat.TRANSLUCENT;
        mWindowLayoutParams.gravity = Gravity.BOTTOM;
        mWindowLayoutParams.x = 0;
        mWindowLayoutParams.y = 0;
        mWindowLayoutParams.alpha = 1f;
        mWindowLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mWindowLayoutParams.height = ScreenUtils.getScreenWidth(activity) - BarViewUtils.getStatusBarHeight();
        activity.getWindowManager().addView(root, mWindowLayoutParams);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        showAnim(DISMISS);
        return false;
    }

    class RootView extends FrameLayout {
        public RootView(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            view.setVisibility(VISIBLE);
            showAnim(SHOW);
        }

    }

    public void setPaddingTop(float paddingTop) {
        view.setPadding(0, (int) paddingTop, 0, 0);
    }

    private void showAnim(final int type) {
        if (animShowing) return;
        ObjectAnimator animator1 = type == SHOW ? ObjectAnimator.ofFloat(opacity, "alpha", 0, 1.0f) : ObjectAnimator.ofFloat(opacity, "alpha", 1.0f, 0);
        animator1.setDuration(ANIMTIME);
        animator1.start();
        ObjectAnimator animator2 = type == SHOW ? ObjectAnimator.ofFloat(MenuView.this, "paddingTop", -1.0f *height, 0.0f) : ObjectAnimator.ofFloat(MenuView.this, "paddingTop", 0.0f, -1.0f *height);
        animator2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                animShowing = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animShowing = false;
                if (type == 1) {
                    if (onDismissListener != null) {
                        onDismissListener.onDismiss();
                    }
                    try {
                        activity.getWindowManager().removeView(root);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        animator2.setDuration(ANIMTIME);
        animator2.start();

    }

    public interface OnDismissListener {
        void onDismiss();
    }
}
