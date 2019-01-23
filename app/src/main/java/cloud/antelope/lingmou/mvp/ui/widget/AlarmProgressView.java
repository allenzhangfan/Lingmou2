package cloud.antelope.lingmou.mvp.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.model.entity.FaceAlarmBlackEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceAlarmDetailBean;

/**
 * Created by ChenXinming on 2017/12/27.
 * description:
 */

public class AlarmProgressView extends View {

    private Paint mBackPaint;       //下面的笔
    private Paint mFrontPaint;      //上层的笔

    private int mBgColor;
    private int mFrontHeadColor;
    private int mFrontEndColor;
    private Bitmap mVeinBitmap;

    private Context mContext;
    private RectF mRectF;
    private RectF mFrontRectF;

    private RectF mOverrideRectF;
    private float mCurrentRight = 0;
    private int mTotal;
    private ProgressHandler mProgressHandler;
    private int mHeight;
    private int mWidth;
    private double mScore;

    public AlarmProgressView(Context context) {
        super(context);
    }

    public AlarmProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.AlarmProgressView);
        mBgColor = attributes.getColor(R.styleable.AlarmProgressView_bg_color, getResources().getColor(R.color.alarm_progress_bg));
        mFrontHeadColor = attributes.getColor(R.styleable.AlarmProgressView_front_head_color, getResources().getColor(R.color.alarm_progress_front_head));
        mFrontEndColor = attributes.getColor(R.styleable.AlarmProgressView_front_end_color, getResources().getColor(R.color.alarm_progress_front_end));
        Drawable drawable = attributes.getDrawable(R.styleable.AlarmProgressView_vein_src);
        mVeinBitmap = ((BitmapDrawable) drawable).getBitmap();
        attributes.recycle();
        initData();
    }

    private void initData() {
        mBackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackPaint.setColor(mBgColor);
        mFrontPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFrontRectF = new RectF(0, 0, mCurrentRight, getMeasuredHeight());
        mProgressHandler = new ProgressHandler();
    }

    private FaceAlarmBlackEntity mAlarmDetailBean;
    public void setTotalScore(FaceAlarmBlackEntity bean, double score) {
        mAlarmDetailBean = bean;
        mScore = score;
        //总的Score
    }


    private class ProgressHandler extends android.os.Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mCurrentRight < mTotal && mTotal != 0) {
                mCurrentRight++;
                mProgressHandler.sendEmptyMessageDelayed(1, 5);
                invalidate();
            } else {
                mCurrentRight = mTotal;
                mProgressHandler.removeMessages(1);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mFrontPaint.reset();
        LinearGradient linearGradient = new LinearGradient(0, 0, mCurrentRight, 0, mFrontHeadColor, mFrontEndColor, Shader.TileMode.CLAMP);
        mFrontPaint.setShader(linearGradient);
        mFrontRectF.right = mCurrentRight;
        mFrontRectF.bottom = mHeight;
//        LogUtils.i("cxm", "right = " + mFrontRectF.right + ", bottom = " + mFrontRectF.bottom);
        canvas.drawRoundRect(mRectF, 20, 20, mBackPaint);
//        canvas.drawBitmap(mVeinBitmap, 0, 0, null);
        canvas.drawRoundRect(mFrontRectF, 20, 20, mFrontPaint);
        canvas.drawBitmap(mVeinBitmap, 0, 0, null);
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

        setMeasuredDimension(width, height);
        mRectF = new RectF(0, 0, width, height);
        mHeight = height;
        mWidth = width;
        mTotal = (int) ((mScore / 100.0d) * mWidth);
        mCurrentRight = mTotal;
//        mProgressHandler.sendEmptyMessage(1);
//        LogUtils.i("cxm", "onMeasure --- total = " + mTotal);
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDetachedFromWindow() {
//        mProgressHandler.removeMessages(1);
        super.onDetachedFromWindow();
    }
}
