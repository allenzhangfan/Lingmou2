package cloud.antelope.lingmou.mvp.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;


import com.lingdanet.safeguard.common.utils.SizeUtils;

import cloud.antelope.lingmou.R;

/**
 * Created by ChenXinming on 2017/12/28.
 * description: 自定义圆形进度条，带渐变的
 */

public class CircleProgressNewView extends View {

    private Paint mScorePaint;
    private Paint mOtherPaint;
    private Paint mCirclePaint;
    private Paint mCircleBgPaint;
    private Context mContext;

    private int mHeadColor;
    private int mEndColor;
    private int mScoreColor;
    private int mScoreSemblanceColor;
    private int mCircleWidth;

    private float mTotalScore;
    private float mCurrentScore;
    private RectF mCirclrRectf;
    private CircleHandler mCircleHandler;
    private int mScoreSize;
    private int mScoreSemblanceSize;

    public CircleProgressNewView(Context context) {
        this(context, null);
    }

    public CircleProgressNewView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public CircleProgressNewView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressNewView);
        mHeadColor = attributes.getColor(R.styleable.CircleProgressNewView_new_head_color, getResources().getColor(R.color.alarm_progress_front_new_head));
        mEndColor = attributes.getColor(R.styleable.CircleProgressNewView_new_end_color, getResources().getColor(R.color.alarm_progress_front_new_end));
        mCircleWidth = attributes.getDimensionPixelSize(R.styleable.CircleProgressNewView_new_circle_width, 20);
        mScoreSize = attributes.getDimensionPixelSize(R.styleable.CircleProgressNewView_new_score_size, 20);
        mScoreSemblanceSize = attributes.getDimensionPixelSize(R.styleable.CircleProgressNewView_new_semblance_size, 12);
        mScoreColor = attributes.getColor(R.styleable.CircleProgressNewView_new_score_color, getResources().getColor(R.color.progress_score_color));
        mScoreSemblanceColor = attributes.getColor(R.styleable.CircleProgressNewView_new_score_color, getResources().getColor(R.color.progress_score_semblance_color));
        mContext = context;
        initData();
    }

    private void initData() {
        mScorePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOtherPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCircleBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mCirclePaint.setStrokeWidth(mCircleWidth);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeJoin(Paint.Join.ROUND);
        mCirclePaint.setStrokeCap(Paint.Cap.ROUND);

        mCircleBgPaint.setColor(getResources().getColor(R.color.gray_eee));
        mCircleBgPaint.setStrokeWidth(mCircleWidth);
        mCircleBgPaint.setStyle(Paint.Style.STROKE);
        mCircleBgPaint.setStrokeCap(Paint.Cap.BUTT);

        mScorePaint.setColor(mScoreColor);
        mScorePaint.setStrokeWidth(6);
        // mScoreSize = SizeUtils.sp2px(mScoreSize);
        mScorePaint.setTextSize(mScoreSize);
        mScorePaint.setTextAlign(Paint.Align.LEFT);
        mScorePaint.setTypeface(Typeface.DEFAULT_BOLD);

        mOtherPaint.setColor(mScoreSemblanceColor);
//        mScoreSemblanceSize = SizeUtils.sp2px(mScoreSemblanceSize);
        mOtherPaint.setTextSize(mScoreSemblanceSize);
        mOtherPaint.setStrokeWidth(2);

        mCirclrRectf = new RectF();
        mCircleHandler = new CircleHandler();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        LogUtils.i("cxm", "currentScore  = " + mCurrentScore + ",mTotalScore = " + mTotalScore);
        canvas.save();
        canvas.rotate(-90, getWidth() / 2, getHeight() / 2);
        canvas.drawArc(mCirclrRectf, 0, 360f, false, mCircleBgPaint);
        canvas.drawArc(mCirclrRectf, 10f, mCurrentScore / 100f * 360f - 10f, false, mCirclePaint);
        canvas.restore();
        // Rect bounds = new Rect();
        int currentScore = (int) mCurrentScore;
        //方式一
        float textWidth = mScorePaint.measureText(currentScore+"");  //获取文字的宽度
        float x = (getMeasuredWidth() - textWidth) / 2; //x的位置
        float y = getMeasuredHeight() / 2 +
                (-mScorePaint.ascent() - (mScorePaint.descent() - mScorePaint.ascent()) / 2);

        // mScorePaint.getTextBounds(currentScore+"", 0, (currentScore+"").length(), bounds);
        // Paint.FontMetrics fontMetrics = mScorePaint.getFontMetrics();
        // int baseLine = (int) (getMeasuredHeight()  / 2  - (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.top);
        canvas.drawText(currentScore + "", x, y, mScorePaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = 0;
        int height = 0;
        /**
         * 设置宽度
         */
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        switch (specMode) {
            case MeasureSpec.EXACTLY:// 明确指定了
                width = specSize;
                break;
            case MeasureSpec.AT_MOST:// 一般为WARP_CONTENT
                width = getPaddingLeft() + getPaddingRight();
                break;
        }

        /**
         * 设置高度
         */
        specMode = MeasureSpec.getMode(heightMeasureSpec);
        specSize = MeasureSpec.getSize(heightMeasureSpec);
        switch (specMode) {
            case MeasureSpec.EXACTLY:// 明确指定了
                height = specSize;
                break;
            case MeasureSpec.AT_MOST:// 一般为WARP_CONTENT
                height = width / 10;
                break;
        }
        if (width != 0 && width < height) {
            height = width;
        }
        if (height != 0 && height < width) {
            width = height;
        }
        setMeasuredDimension(width, height);
        mCirclrRectf.left = mCircleWidth;
        mCirclrRectf.top = mCircleWidth;
        mCirclrRectf.right = width - mCircleWidth;
        mCirclrRectf.bottom = height - mCircleWidth;
//        LinearGradient linearGradient = new LinearGradient(mCircleWidth, mCircleWidth, getWidth() - mCircleWidth, getHeight() - mCircleWidth, mHeadColor, mEndColor, Shader.TileMode.CLAMP);
        SweepGradient sweepGradient = new SweepGradient(width / 2, height / 2, mHeadColor, mEndColor);
        mCirclePaint.setShader(sweepGradient);
    }

    public void setScore(float score) {
        setScore(score, true);
    }

    public void setScore(float score, boolean isAnim) {
        mTotalScore = score;
        mCurrentScore = isAnim ? 0 : score;
        invalidate();
        if (isAnim) {
            mCircleHandler.sendEmptyMessageDelayed(1, 100);
        }
    }

    private class CircleHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if ((int) mCurrentScore < (int) mTotalScore) {
                invalidate();
                mCurrentScore++;
                mCircleHandler.sendEmptyMessageDelayed(1, 10);
            } else {
                mCircleHandler.removeMessages(1);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        mCircleHandler.removeMessages(1);
        super.onDetachedFromWindow();
    }
}
