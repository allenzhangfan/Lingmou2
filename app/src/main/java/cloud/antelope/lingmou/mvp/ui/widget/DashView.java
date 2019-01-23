package cloud.antelope.lingmou.mvp.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import cloud.antelope.lingmou.R;
import timber.log.Timber;

public class DashView extends View {
    private final int DEFAULT_WITH = 500;//默认宽
    private final int DEFAULT_HEIGHT = 500;//默认高
    private Paint solidPaint;
    private Paint basePaint;
    private int solidColor;//颜色
    private int baseColor;//底色
    private float previous = 0f;//右侧进度
    private float latter = 0f;//右侧进度
    private int width;
    private int height;
    private boolean anim;

    public DashView(Context context) {
        this(context, null);
    }

    public DashView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DashView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray t = null;
        try {
            t = context.obtainStyledAttributes(attrs, R.styleable.DashView,
                    0, defStyleAttr);
            solidColor = t.getColor(R.styleable.DashView_solid_color,
                    getResources().getColor(android.R.color.holo_blue_light));
            baseColor = t.getColor(R.styleable.DashView_base_color,
                    getResources().getColor(android.R.color.darker_gray));
        } finally {
            if (t != null) {
                t.recycle();
            }
        }
        init();
    }

    private void init() {
        solidPaint = new Paint();
        solidPaint.setAntiAlias(true);
        solidPaint.setColor(solidColor);
        basePaint = new Paint();
        basePaint.setAntiAlias(true);
        basePaint.setColor(baseColor);
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

    public void start() {
        anim = true;
        ValueAnimator previousAnimator = ValueAnimator.ofFloat(0, 1f);
        previousAnimator.setDuration(400);
        previousAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        previousAnimator.addUpdateListener((animation) -> previous = (float) animation.getAnimatedValue());
        ValueAnimator latterAnimator = ValueAnimator.ofFloat(0, 0.75f, 0);
        latterAnimator.setDuration(600);
        latterAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        latterAnimator.addUpdateListener((animation) -> latter = (float) animation.getAnimatedValue());
        latterAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                anim = false;
            }
        });
        latterAnimator.setStartDelay(100);
        previousAnimator.start();
//        latterAnimator.start();
        invalidate();
    }


    public void reset() {
//        anim = true;
//        ValueAnimator previousAnimator = ValueAnimator.ofFloat(1f, 0f);
//        previousAnimator.setDuration(400);
//        previousAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
//        previousAnimator.addUpdateListener((animation) -> previous = (float) animation.getAnimatedValue());
//        previousAnimator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                super.onAnimationEnd(animation);
//                anim = false;
//            }
//        });
//        previousAnimator.start();
        previous=0;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float left = width * latter;
        float top = 0;
        float right = width * previous;
        float bottom = height;
        canvas.drawRect(left, top, width, bottom,basePaint);
        canvas.drawRect(left, top, right, bottom, solidPaint);
        if (anim) {
            invalidate();
        }
    }
}
