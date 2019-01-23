package cloud.antelope.lingmou.mvp.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.lingdanet.safeguard.common.utils.SizeUtils;

import cloud.antelope.lingmou.R;
import timber.log.Timber;


/**
 * 刻度尺
 * create by zl
 */
public class ScaleView extends View {

    private int mMax; //最大刻度
    private int mMin; // 最小刻度

    public int mScaleMargin; //刻度间距
    private int mScaleHeight; //刻度线的高度
    private int mScaleMaxHeight; //整刻度线高度

    private int mRectWidth; //总宽度
    public int mRectHeight; //高度
    private Paint mPaint;
    public int mScreenHalf = 0;

    public ScaleView(Context context) {
        super(context);
        init(null);
    }

    public ScaleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ScaleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ScaleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {

        mScreenHalf = getContext().getResources().getDisplayMetrics().widthPixels / 2;

        // 获取自定义属性
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.ScaleView);

        mMin = ta.getInteger(R.styleable.ScaleView_lf_scale_view_min, 0);
        mMax = ta.getInteger(R.styleable.ScaleView_lf_scale_view_max, 200);
//        mScaleMargin = ta.getDimensionPixelSize(R.styleable.ScaleView_lf_scale_view_margin, 15);
        mScaleMargin = (SizeUtils.getScreenWidth() - getContext().getResources().getDimensionPixelSize(R.dimen.dp120)) / (12 * 5);
        mScaleHeight = ta.getDimensionPixelSize(R.styleable.ScaleView_lf_scale_view_height, 8);
        mScaleMaxHeight = ta.getDimensionPixelSize(R.styleable.ScaleView_lf_scale_view_max_height, 16);


        //Log.e("ZL","ScaleView mScaleMargin="+mScaleMargin);
        //Log.e("ZL","ScaleView Halfofffset="+mScaleMargin*35);
        ta.recycle();

        // 画笔
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        // 抗锯齿
        mPaint.setAntiAlias(true);
        // 设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
        mPaint.setDither(true);
        // 空心
        mPaint.setStyle(Paint.Style.STROKE);
        // 文字居中
        mPaint.setTextAlign(Paint.Align.CENTER);
        initVar();
    }


    private void initVar() {
        mRectWidth = (mMax - mMin) * mScaleMargin + mScreenHalf * 2;
        mRectHeight = getResources().getDimensionPixelSize(R.dimen.dp64);
        //setPadding(mScaleMargin,0,mScaleMargin,0);
        //设置layoutParams
        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(mRectWidth, mRectHeight);
        this.setLayoutParams(lp);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.makeMeasureSpec(mRectHeight, MeasureSpec.AT_MOST);
        int customWidth = MeasureSpec.makeMeasureSpec(mRectWidth, MeasureSpec.getMode(widthMeasureSpec));
        super.onMeasure(customWidth, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //查看整个刻度尺的范围
        /*int left=mScaleMargin*mHalfOffset;
        int right=left+720*mScaleMargin;
        mPaint.setColor(Color.RED);
        canvas.drawRect(left,0,right,mRectHeight,mPaint);
        mPaint.setColor(Color.WHITE);
        */
        onDrawScale(canvas, mPaint); //画刻度
        super.onDraw(canvas);
    }


    private void onDrawScale(Canvas canvas, Paint paint) {
        //Log.e("ZL","画刻度尺....");
        int hour = 0;
        int minute = 0;
        String hourString = "";
        String minuteString = "";
        mScreenHalf -= getContext().getResources().getDimensionPixelSize(R.dimen.dp40);
        for (int i = 0, k = mMin; i <= mMax - mMin; i++) {
            if (i % 5 == 0) { //整值
                canvas.drawLine((i) * mScaleMargin + mScreenHalf, mRectHeight - mScaleMaxHeight, (i) * mScaleMargin + mScreenHalf, mRectHeight, paint);
                //整值文字
                minute = k % 6;
                hour = k / 6;
                minute = minute * 10;
                if (hour < 10) {
                    hourString = "0" + hour;
                } else {
                    hourString = String.valueOf(hour);
                }

                if (minute == 0) {
                    minuteString = "0" + minute;
                } else {
                    minuteString = String.valueOf(minute);
                }

                //画文字
                /*if(i==0){
                    canvas.drawText(hourString+":"+minuteString,(i+2)* mScaleMargin, mRectHeight-20, paint);
                }else{*/
                paint.setTextSize(getResources().getDimension(R.dimen.sp12));
                paint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
                paint.setColor(getContext().getResources().getColor(R.color.white80));
                canvas.drawText(hourString + ":" + minuteString, i * mScaleMargin + mScreenHalf, mRectHeight - mScaleMaxHeight - 10, paint);
                /*}*/

                k += 1;
            } else {
                canvas.drawLine((i) * mScaleMargin + mScreenHalf, mRectHeight - mScaleHeight, (i) * mScaleMargin + mScreenHalf, mRectHeight, paint);
            }
        }
    }
}
