package cloud.antelope.lingmou.mvp.ui.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.Arrays;

import cloud.antelope.lingmou.R;

public class SideBar extends View {
    private OnTouchingLetterChangedListener listener;
    public String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"};
    private int choose = -1;// 选中
    private Paint paint, bgPaint;
    private int normalColor;
    private int pressColor;
    private int textSize;
    private TextView mTextDialog;

    public void setChooseLetter(String letter) {
        this.choose = Arrays.asList(letters).indexOf(letter);
        invalidate();
    }

    public void setLetters(String[] letters) {
        this.letters = letters;
        requestLayout();
    }

    public void setOnTouchingLetterChangedListener(OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.listener = onTouchingLetterChangedListener;
    }

    public void setTextView(TextView mTextDialog) {
        this.mTextDialog = mTextDialog;
    }

    public SideBar(Context context) {
        this(context, null);
    }

    public SideBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SideBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray t = null;
        try {
            t = context.obtainStyledAttributes(attrs, R.styleable.SideBar,
                    0, defStyle);
            normalColor = t.getColor(R.styleable.SideBar_normalColor,
                    getResources().getColor(android.R.color.darker_gray));
            pressColor = t.getColor(R.styleable.SideBar_pressColor,
                    getResources().getColor(android.R.color.holo_blue_light));
            textSize = t.getDimensionPixelSize(R.styleable.SideBar_textSize,
                    getResources().getDimensionPixelSize(R.dimen.sp14));
        } finally {
            if (t != null) {
                t.recycle();
            }
        }
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(textSize);
        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int mWidth = textSize + getPaddingLeft() + getPaddingRight();
        int mHeight = (int) (letters.length * textSize * 1.5);
        if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT && getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(mWidth, mHeight);
        } else if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(mWidth, heightSize);
        } else if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(widthSize, mHeight);
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 获取焦点改变背景颜色.
        int height = getHeight();// 获取对应高度
        int width = getWidth(); // 获取对应宽度
        int singleHeight = height / letters.length;// 获取每一个字母的高度

        for (int i = 0; i < letters.length; i++) {
            paint.setColor(normalColor);
            bgPaint.setColor(Color.parseColor("#00000000"));
            // 选中的状态
            if (i == choose) {
                paint.setColor(Color.parseColor("#ffffff"));
                bgPaint.setColor(pressColor);
            }
            // x坐标等于中间-字符串宽度的一半.
            float xPos = width / 2 - paint.measureText(letters[i]) / 2;
            float yPos = singleHeight * i + singleHeight;

//            canvas.drawText(letters[i], xPos, yPos, paint);

            Rect rect = new Rect(0, singleHeight * i, width, singleHeight * (i + 1));
            canvas.drawCircle(rect.centerX(),rect.centerY(), singleHeight/2, bgPaint);
            paint.setTextAlign(Paint.Align.CENTER);
            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
            float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
            float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom
            int baseLineY = (int) (rect.centerY() - top / 2 - bottom / 2);//基线中间点的y轴计算公式
            canvas.drawText(letters[i], rect.centerX(), baseLineY, paint);
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();// 点击y坐标
        final int oldChoose = choose;
        final int c = (int) (y / getHeight() * letters.length);// 点击y坐标所占总高度的比例*letters数组的长度就等于点击letters中的个数.

        switch (action) {
            case MotionEvent.ACTION_UP:
                setBackground(new ColorDrawable(0x00000000));
//                choose = -1;//
                invalidate();
                if (mTextDialog != null) {
                    mTextDialog.setVisibility(View.INVISIBLE);
                }
                break;

            default:
                if (oldChoose != c) {
                    if (c >= 0 && c < letters.length) {
                        if (listener != null) {
                            listener.onTouchingLetterChanged(letters[c]);
                        }
                        if (mTextDialog != null) {
                            mTextDialog.setText(letters[c]);
                            mTextDialog.setVisibility(View.VISIBLE);
                        }

                        choose = c;
                        invalidate();
                    }
                }

                break;
        }
        return true;
    }

    public interface OnTouchingLetterChangedListener {
        void onTouchingLetterChanged(String s);
    }

}