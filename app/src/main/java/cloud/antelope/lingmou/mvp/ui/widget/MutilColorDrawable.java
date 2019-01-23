package cloud.antelope.lingmou.mvp.ui.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

import com.lingdanet.safeguard.common.utils.Utils;

import java.util.List;

import cloud.antelope.lingmou.R;

/**
 * 用于描绘刻度尺的背景颜色
 * Created by zhulei on 17/4/12.
 */

public class MutilColorDrawable extends Drawable {
    private Paint mPaint; //画笔
    private List<MutilColorEntity> mColors;
    private LinearGradient mLinearGradient;

    /**
     * 前一次的开始和结束位置
     */
    private int mPre_start, mPre_end;

    public MutilColorDrawable(List<MutilColorEntity> list) {
        mColors = list;
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }


    /**
     * 因为绘制的时候,后端的时间戳可能出现30秒的时间重叠,而绘制的颜色又包含透明度,所以如果出现了重叠的话，重叠的部分颜色会加深(若果绘制的颜色是不透明的则不会有这个问题，蛋疼的UI和后端）
     * 所以进行如下的解决添加判断，但是这个前提是已经对时间戳进行了排序(好在后端进行了排序);不用saveLayer的方法是因为saveLayer会创建新的图层，会创建新的bitmap，如果
     * 时间段非常的多，可能出现OOM
     */
    @Override
    public void draw(Canvas canvas) {
        int left = getBounds().left- Utils.getContext().getResources().getDimensionPixelSize(R.dimen.dp40);
        int width = getBounds().width();
        Rect rect;

            /*//底色
            mPaint.setColor(Color.RED);
            rect = new Rect(left,getBounds().centerY() - getBounds().height() / 2,
                    width+left,getBounds().centerY() + getBounds().height() / 2);
            canvas.drawRect(rect, mPaint);
            //底色
            */
        // Log.e("ZL", "画时间轴....");
        if (mColors.size() > 0) {

            if (mLinearGradient == null) {                                                                   //#CCFF9000                  //#CCFF5000
                mLinearGradient = new LinearGradient(0, 0, 0, getBounds().height(), new int[]{Color.parseColor("#CCFFA000"),Color.parseColor("#CCFFA000")}, null, Shader.TileMode.CLAMP);
            }
            mPaint.setShader(mLinearGradient);
            for (MutilColorEntity item : mColors) {
                //mPaint.setColor(item.color);

                int start = left + (int) (width * item.fromPercent / 100f);
                int end = left + (int) (width * item.toPercent / 100f);
                //Log.e("ZL","tart=="+start+"   end=="+end);
                if (start >= mPre_end) { //没有重叠
                    rect = new Rect(start, getBounds().centerY() - getBounds().height() / 2, end, getBounds().centerY() + getBounds().height() / 2);
                    canvas.drawRect(rect, mPaint);
                    mPre_start = start;
                    mPre_end = end;
                    continue;
                }

                if (end < mPre_end) {  //将要绘制的完全在前一次之中
                    continue;
                }

                if (start <= mPre_end && end > mPre_end) { //部分重叠
                    rect = new Rect(mPre_end, getBounds().centerY() - getBounds().height() / 2, end, getBounds().centerY() + getBounds().height() / 2);
                    canvas.drawRect(rect, mPaint);
                    mPre_start = start;
                    mPre_end = end;
                    continue;
                }
            }

            mPre_start = 0;
            mPre_end = 0;
            //canvas.restore();
        } else {
            mPaint.setColor(Color.parseColor("#00000000"));
            rect = new Rect(left, getBounds().centerY() - getBounds().height() / 2, left + width, getBounds().centerY() + getBounds().height() / 2);
            canvas.drawRect(rect, mPaint);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }


    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public static class MutilColorEntity {
        public float fromPercent;
        public float toPercent;
        public int color;

        public MutilColorEntity(float fromPercent, float toPercent, int color) {
            this.fromPercent = fromPercent;
            this.toPercent = toPercent;
            this.color = color;
        }
    }
}
