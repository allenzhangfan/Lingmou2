package cloud.antelope.lingmou.mvp.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.lingdanet.safeguard.common.utils.SizeUtils;

import cloud.antelope.lingmou.R;

/**
 * 作者：陈新明
 * 创建日期：2018/01/25
 * 邮箱：chenxinming@antelop.cloud
 * 描述：TODO
 */

public class CaptureAngel extends View {

    private int mDirection = 0;
    private Paint mPaint;
    private int line_width;

    public CaptureAngel(Context context) {
        this(context, null);
    }

    public CaptureAngel(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CaptureAngel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
    }

    private void initData() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(getResources().getColor(R.color.home_text_select));
        mPaint.setStrokeWidth(SizeUtils.dp2px(4));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        line_width = getWidth();
        switch (mDirection) {
            case 0:
                //左上角
                canvas.drawLine(0, 0, line_width, 0, mPaint);
                canvas.drawLine(0,0, 0, line_width, mPaint);
                break;
            case 1:
                //右上角
                canvas.drawLine(0, 0, line_width, 0, mPaint);
                canvas.drawLine(line_width,0, line_width, line_width, mPaint);
                break;
            case 2:
                //左下角
                canvas.drawLine(0, 0, 0, line_width, mPaint);
                canvas.drawLine(0,line_width, line_width, line_width, mPaint);
                break;
            case 3:
                //右下角
                canvas.drawLine(0, line_width, line_width, line_width, mPaint);
                canvas.drawLine(line_width,0, line_width, line_width, mPaint);
                break;
        }

    }

    public void setDirection(int direction) {
        mDirection = direction;
        invalidate();
    }
}
