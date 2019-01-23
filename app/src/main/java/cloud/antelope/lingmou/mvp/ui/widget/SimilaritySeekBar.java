package cloud.antelope.lingmou.mvp.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.cjt2325.cameralibrary.util.ScreenUtils;

import cloud.antelope.lingmou.R;

public class SimilaritySeekBar extends View {
    private int DEFAULT_WITH;//默认宽
    private int DEFAULT_HEIGHT;//默认高
    private int currentValue;
    private int minValue;
    private int maxValue;
    private Paint minAndMaxValuePaint;//最大最小值文字的Paint
    private Paint valuePaint;//气泡内文字的Paint
    private Paint solidPaint;//底层默认
    private Paint coverPaint;//覆盖层
    private Paint picPaint;//图片
    private int circleHeight;
    private int height;
    private int width;

    public int getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
        invalidate();
    }

    public SimilaritySeekBar(Context context) {
        this(context, null);
    }

    public SimilaritySeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimilaritySeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray t = null;
        try {
            t = context.obtainStyledAttributes(attrs, R.styleable.SimilaritySeekBar,
                    0, defStyleAttr);
            currentValue = t.getInt(R.styleable.SimilaritySeekBar_default_value,
                    85);
            minValue = t.getInt(R.styleable.SimilaritySeekBar_min_value,
                    60);
            maxValue = t.getInt(R.styleable.SimilaritySeekBar_max_value,
                    100);
        } finally {
            if (t != null) {
                t.recycle();
            }
        }
        init();
    }

    private void init() {
        DEFAULT_WITH = ScreenUtils.getScreenWidth(getContext());
        DEFAULT_HEIGHT = getResources().getDimensionPixelSize(R.dimen.dp48);
        circleHeight = getResources().getDimensionPixelSize(R.dimen.dp16);
        minAndMaxValuePaint = new Paint();
        minAndMaxValuePaint.setAntiAlias(true);
        minAndMaxValuePaint.setColor(getResources().getColor(R.color.gray_424242));
        minAndMaxValuePaint.setTextSize(getResources().getDimension(R.dimen.sp14));
        valuePaint = new Paint();
        valuePaint.setAntiAlias(true);
        valuePaint.setColor(getResources().getColor(R.color.white));
        valuePaint.setTextSize(getResources().getDimension(R.dimen.sp14));
        solidPaint = new Paint();
        solidPaint.setAntiAlias(true);
        solidPaint.setColor(getResources().getColor(R.color.gray_eee));
        coverPaint = new Paint();
        coverPaint.setAntiAlias(true);
        coverPaint.setColor(getResources().getColor(R.color.yellow_ffc107));
        picPaint = new Paint();
        picPaint.setAntiAlias(true);
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
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        height = getMeasuredHeight()+getPaddingBottom()+getPaddingTop();
        width = getMeasuredWidth();
        String minStr = String.valueOf(minValue);
        String maxStr = String.valueOf(maxValue);
        String defaultStr = String.valueOf(currentValue);
        //画最大最小值
        canvas.drawText(minStr, getPaddingLeft(), height - getPaddingBottom() - (circleHeight - getTextHeight(minStr, minAndMaxValuePaint)) / 2, minAndMaxValuePaint);
        canvas.drawText(maxStr, width - getPaddingRight() - getTextWidth(maxStr, minAndMaxValuePaint), height - getPaddingBottom() - (circleHeight - getTextHeight(maxStr, minAndMaxValuePaint)) / 2, minAndMaxValuePaint);
        //画底层
        RectF rectF = new RectF(getPaddingLeft() + getTextWidth(minStr, minAndMaxValuePaint) + getResources().getDimensionPixelSize(R.dimen.dp16)
                , height - getPaddingBottom() - (circleHeight - getResources().getDimensionPixelSize(R.dimen.dp4)) / 2 - getResources().getDimensionPixelSize(R.dimen.dp4)
                , width - getPaddingRight() - getTextWidth(maxStr, minAndMaxValuePaint) - getResources().getDimensionPixelSize(R.dimen.dp16)
                , height - getPaddingBottom() - (circleHeight - getResources().getDimensionPixelSize(R.dimen.dp4)) / 2);
        canvas.drawRoundRect(rectF, getResources().getDimensionPixelSize(R.dimen.dp2), getResources().getDimensionPixelSize(R.dimen.dp2), solidPaint);
        //画上层
        rectF.left = rectF.left + (rectF.right - rectF.left) * (currentValue - minValue) / (maxValue - minValue);
        canvas.drawRoundRect(rectF, getResources().getDimensionPixelSize(R.dimen.dp2), getResources().getDimensionPixelSize(R.dimen.dp2), coverPaint);
        //画圆圈
        canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.slider), rectF.left - circleHeight / 2, height - getPaddingBottom() - circleHeight, picPaint);
        //画气泡
        canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pop), rectF.left - getResources().getDimensionPixelSize(R.dimen.dp32) / 2, height - getPaddingBottom() - circleHeight - getResources().getDimensionPixelSize(R.dimen.dp32), picPaint);
        //画气泡内文字
        canvas.drawText(defaultStr, rectF.left - getTextWidth(defaultStr, valuePaint) / 2, getPaddingTop() + (getResources().getDimensionPixelSize(R.dimen.dp24) + getTextHeight(defaultStr, valuePaint)) / 2, valuePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int left = getPaddingLeft() + getTextWidth(String.valueOf(minValue), minAndMaxValuePaint) + getResources().getDimensionPixelSize(R.dimen.dp16);
        int right = width - getPaddingRight() - getTextWidth(String.valueOf(maxValue), minAndMaxValuePaint) - getResources().getDimensionPixelSize(R.dimen.dp16);
        int top = height - getPaddingBottom() - circleHeight -getResources().getDimensionPixelSize(R.dimen.dp8);
        int bottom = height ;
        if ((event.getAction()==MotionEvent.ACTION_DOWN)&&(y > bottom || y < top )) return false;
        if(x<left) x=left;
        if(x>right) x=right;
        currentValue = (int) ((maxValue-minValue)*(x - left) / (right - left)+minValue);
        invalidate();
        return true;
    }

    private int getTextWidth(String text, Paint paint) {
        int iRet = 0;
        if (text != null && text.length() > 0) {
            int len = text.length();
            float[] widths = new float[len];
            paint.getTextWidths(text, widths);
            for (int j = 0; j < len; j++) {
                iRet += (int) Math.ceil(widths[j]);
            }
        }
        return iRet;

    }

    private int getTextHeight(String text, Paint paint) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.height();
    }

}
