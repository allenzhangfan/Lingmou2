package cloud.antelope.lingmou.mvp.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import cloud.antelope.lingmou.R;

public class IndicatorView extends View {
    private final int defaultSingleWidth = 5;
    private final int defaultGapWidthWidth = 0;
    private int baseColor;
    private int singleWidth;//线段的长度
    private int gapWidth;//如果只有2个文字，文字中间的距离
    private int width;
    private int height;
    ViewPager mViewPager;
    private int count;
    private float left;
    private float right;
    private Paint mPaint;
    private RectF rectF;

    public IndicatorView(Context context) {
        this(context, null);
    }

    public IndicatorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray t = null;
        try {
            t = context.obtainStyledAttributes(attrs, R.styleable.IndicatorView,
                    0, defStyleAttr);
            baseColor = t.getColor(R.styleable.IndicatorView_line_color,
                    getResources().getColor(android.R.color.holo_blue_light));
            singleWidth = t.getDimensionPixelSize(R.styleable.IndicatorView_single_width, defaultSingleWidth);
            gapWidth = t.getDimensionPixelSize(R.styleable.IndicatorView_gap_width, defaultGapWidthWidth);
        } finally {
            if (t != null) {
                t.recycle();
            }
        }
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(baseColor);
    }

    public void setViewPager(ViewPager viewPager) {
        this.mViewPager = viewPager;
        if (mViewPager.getAdapter() == null) {
            throw new RuntimeException("viewPager needs a adapter!");
        }
        count = mViewPager.getAdapter().getCount();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                float segmentWidth = width * 1.0f / count;//每段的距离
                float base = (segmentWidth + gapWidth * 0.5f) * position + (segmentWidth - singleWidth - gapWidth * 0.5f) * 0.5f;//左边点的锚点
                left = base + (segmentWidth + gapWidth * 0.5f) * positionOffset * positionOffset;//左边的位置
                right = left + singleWidth + segmentWidth * 0.6f * (1 - (2 * positionOffset - 1) * (2 * positionOffset - 1));//右边的位置

//                float segmentWidth = width * 1.0f / count;//每段的距离
//                float base = segmentWidth * position + (segmentWidth - singleWidth) * 0.5f;//左边点的锚点
//                if (positionOffset <= 0.5f) {//positionOffset是滑动的比例，值从0到1
//                    right = (base + singleWidth) + (segmentWidth - singleWidth * 0.5f) * positionOffset * 2;//滑动一半页面前，（base + singleWidth）是右边点的锚点，singleWidth是线的长度
//                } else {
//                    right = (base + singleWidth) + segmentWidth - singleWidth * 0.5f + singleWidth * (positionOffset - 0.5f);//滑动一半后
//                }
//                left = base + segmentWidth * positionOffset * positionOffset;//左边的位置
                invalidate();//刷新绘画
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int mWidth = 0;
        int mHeight = 0;
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
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        rectF = new RectF(left, 0, right, height);
        canvas.drawRoundRect(rectF, height * 0.5f, height * 0.5f, mPaint);
    }
}
