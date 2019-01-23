package cloud.antelope.lingmou.mvp.ui.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import cloud.antelope.lingmou.R;

public class HeartbeatView extends View {
    private final int DEFAULT_WITH = 50;//默认宽
    private final int DEFAULT_HEIGHT = 50;//默认高
    private int color;
    private int width;
    private int height;
    private Paint smallPaint;
    private Paint middlePaint;
    private Paint largePaint;
    private float middleScale;
    private float largeScale;
    private int middleAlpha;
    private int largeAlpha;
    private ValueAnimator scaleAnimator;

    public HeartbeatView(Context context) {
        this(context, null);
    }

    public HeartbeatView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeartbeatView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray t = null;
        try {
            t = context.obtainStyledAttributes(attrs, R.styleable.HeartbeatView,
                    0, defStyleAttr);
            color = t.getColor(R.styleable.HeartbeatView_theme_color,
                    getResources().getColor(android.R.color.holo_blue_dark));
        } finally {
            if (t != null) {
                t.recycle();
            }
        }
        init();
    }

    public void setColor(int color) {
        this.color =color;
        smallPaint.setColor(this.color);
        middlePaint.setColor(this.color);
        largePaint.setColor(this.color);
    }

    private void init() {
        smallPaint = new Paint();
        smallPaint.setAntiAlias(true);
        smallPaint.setColor(color);

        middlePaint = new Paint();
        middlePaint.setAntiAlias(true);
        middlePaint.setColor(color);

        largePaint = new Paint();
        largePaint.setAntiAlias(true);
        largePaint.setColor(color);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int mWidth = DEFAULT_WITH;
        int mHeight = DEFAULT_HEIGHT;
        if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT && getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(mWidth, mHeight);
        } else if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(mWidth, heightSize);
        } else if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(widthSize, mHeight);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        scaleAnimator = ValueAnimator.ofFloat(0, 1.0f);
        scaleAnimator.setDuration(1000);
        scaleAnimator.addUpdateListener((animation) -> {
                    float animatedFraction = animation.getAnimatedFraction();
                    middleScale = 1.0f + 2.0f * animatedFraction;
                    largeScale = 1.0f + 4.0f * animatedFraction;
                    if (animatedFraction >= 0.6f) {
                        middleAlpha = (int) (102 - (animatedFraction - 0.6) * 102 / 0.4);
                        largeAlpha = (int) (51 - (animatedFraction - 0.6) * 51 / 0.4);
                    } else {
                        middleAlpha = 102;
                        largeAlpha = 51;
                    }
                }
        );
        scaleAnimator.setRepeatMode(ValueAnimator.RESTART);
        scaleAnimator.setRepeatCount(ValueAnimator.INFINITE);
        scaleAnimator.start();
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        scaleAnimator.removeAllUpdateListeners();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float cX = width / 2.0f;
        float cY = height / 2.0f;
        int radius = Math.min(width, height) / 2;//直径
        middlePaint.setAlpha(middleAlpha);
        largePaint.setAlpha(largeAlpha);
        canvas.drawCircle(cX, cY, largeScale * radius / 5.0f, largePaint);
        canvas.drawCircle(cX, cY, middleScale * radius / 5.0f, middlePaint);
        canvas.drawCircle(cX, cY, radius / 5.0f, smallPaint);
        invalidate();
    }
}
