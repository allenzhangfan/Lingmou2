package cloud.antelope.lingmou.mvp.ui.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by liucheng on 16/5/10.
 */
public class CustomViewPager extends ViewPager {

    private boolean mIsPagingEnabled = false;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.mIsPagingEnabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.mIsPagingEnabled && super.onInterceptTouchEvent(event);
    }

    public void setIsPagingEnabled(boolean b) {
        this.mIsPagingEnabled = b;
    }}
