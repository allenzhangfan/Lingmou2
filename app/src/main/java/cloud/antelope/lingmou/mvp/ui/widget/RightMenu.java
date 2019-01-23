package cloud.antelope.lingmou.mvp.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SearchEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.cjt2325.cameralibrary.util.ScreenUtils;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.BarViewUtils;

public class RightMenu implements View.OnTouchListener {
    private static final int ANIM_TIME = 300;
    public static final int DISMISS=0;
    public static final int SHOW=1;
    Activity activity;
    View view;
    int status;
    private boolean animShowing;
    private LinearLayout root;
    OnDismissListener onDismissListener;
    private Animation mRightIn;
    private Animation mRightOut;
    public RightMenu(Activity activity, View view) {
        this.activity = activity;
        this.view = view;
        mRightIn = AnimationUtils.loadAnimation(activity, R.anim.right_in);
        mRightOut = AnimationUtils.loadAnimation(activity, R.anim.right_out);
    }

    public int getStatus() {
        return status;
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    @Keep
    private void setPaddingLeft(float paddingLeft) {
//        view.setPadding( (int) paddingLeft,0, 0, 0);
        LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) view.getLayoutParams();
        layoutParams.setMargins((int) paddingLeft,0,0,0);
        view.setLayoutParams(layoutParams);
    }

    public void show(View below) {
        Rect rect = new Rect();
        below.getGlobalVisibleRect(rect);
        root = new Root(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setLayoutParams(new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        View lucency = new View(activity);
        lucency.setBackgroundColor(Color.argb(0, 255, 255, 255));
        lucency.setOnTouchListener(this);
        lucency.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, rect.bottom - BarViewUtils.getStatusBarHeight()));
        root.addView(lucency);

        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        view.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        view.setClickable(true);
        root.addView(view);
        root.post(new Runnable() {
            @Override
            public void run() {
                if (animShowing) return;
                view.startAnimation(mRightIn);
                mRightIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        animShowing = true;
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        animShowing = false;
                        status=SHOW;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        });

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

    public void dismiss() {
        if (animShowing) return;
        view.startAnimation(mRightOut);
        mRightOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                animShowing = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animShowing = false;
                status=DISMISS;
                if(onDismissListener!=null){
                    onDismissListener.onDismiss();
                }
                try {
                    activity.getWindowManager().removeView(root);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        dismiss();
        return false;
    }

    class Root extends LinearLayout{

        public Root(Context context) {
            super(context);
        }

        @Override
        public boolean dispatchKeyEvent(KeyEvent event) {
            if(event.getAction()==KeyEvent.ACTION_DOWN&&event.getKeyCode()==KeyEvent.KEYCODE_BACK){
                dismiss();
            }
            return super.dispatchKeyEvent(event);
        }
    }
    public interface OnDismissListener{
        void onDismiss();
    }
}
