package cloud.antelope.lingmou.mvp.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by ChenXinming on 2018/1/20.
 * description:
 */

public class DeltaView extends View {

    private Paint mPaint;

    public DeltaView(Context context) {
        this(context, null);
    }

    public DeltaView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DeltaView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
    }

    private void initData() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.parseColor("#36A8FF"));
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.parseColor("#F2F2F2"));
        Path path = new Path();
        path.moveTo(0, 0);
        path.lineTo(getWidth(), 0);
        path.lineTo(0, getHeight());
        path.close();
        canvas.drawPath(path, mPaint);
    }
}
