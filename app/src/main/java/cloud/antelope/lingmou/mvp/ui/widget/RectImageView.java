package cloud.antelope.lingmou.mvp.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Created by ChenXinming on 2017/11/21.
 * description: 圆角ImageView
 */

public class RectImageView extends AppCompatImageView {

    private boolean mIsSelect;
    private Paint mPaint;
    private int mUnSelectColor = Color.parseColor("#DDDDDD");
    private int mSelectedColor = Color.parseColor("#FF8F00");
    private int mUnSelectStrokeWidth = 2;
    private int mSelectedStrokeWidth = 4;
    private RectF mRectF;
    private Path mPath;

    public RectImageView(Context context) {
        this(context, null);
    }

    public RectImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData();
    }

    private void initData() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRectF = new RectF();
        mPath = new Path();
    }

    public void setIsSelect(boolean isSelect) {
        mIsSelect = isSelect;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPath.reset();
        mRectF.left = 0;
        mRectF.top = 0;
        mRectF.right = getWidth();
        mRectF.bottom = getHeight();
        mPath.addRoundRect(mRectF, 5, 5, Path.Direction.CW);
        canvas.clipPath(mPath);
        super.onDraw(canvas);
        if (null == getDrawable()) {
            return;
        }
        if (mIsSelect) {
            mPaint.reset();
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(mSelectedColor);
            mPaint.setStrokeWidth(mSelectedStrokeWidth);
        } else {
            mPaint.reset();
            mPaint.setColor(mUnSelectColor);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(mUnSelectStrokeWidth);
        }
        canvas.drawRoundRect(mRectF, 5, 5, mPaint);
    }
}
