package cloud.antelope.lingmou.mvp.ui.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class SizeChangeAppreciableViewPager extends ViewPager {
    OnSizeChangedListener onSizeChangedListener;

    public void setOnSizeChangedListener(OnSizeChangedListener onSizeChangedListener) {
        this.onSizeChangedListener = onSizeChangedListener;
    }

    public SizeChangeAppreciableViewPager(@NonNull Context context) {
        super(context);
    }

    public SizeChangeAppreciableViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (onSizeChangedListener != null && h != 0) {
            onSizeChangedListener.onSizeChanged(h, w);
        }
    }

    public interface OnSizeChangedListener {
        void onSizeChanged(int height, int width);
    }
}
