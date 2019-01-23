package cloud.antelope.lingmou.mvp.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;

import cloud.antelope.lingmou.R;
import timber.log.Timber;

public class DividerItemDecoration extends RecyclerView.ItemDecoration {
    private Context context;
    private int groupDividerHeight;     // 分组分割线高度

    private int itemDividerHeight;     // 分组内item分割线高度

    private int dividerPaddingLeft;    // 分割线左间距

    private int dividerPaddingRight;   // 分割线右间距

    private Paint dividerPaint;     // 绘制分割线画笔

    private Paint textPaint;        // 绘制文字画笔

    private Paint topDividerPaint;
    private  int headerCount;

    public void setHeaderCount(int headerCount) {
        this.headerCount = headerCount;
    }

    public DividerItemDecoration(Context context, OnGroupListener listener) {
        this.context = context;
        this.listener = listener;
        groupDividerHeight = dp2Px(28);
        itemDividerHeight = 1;
        initPaint();
    }

    private void initPaint() {
        dividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dividerPaint.setColor(context.getResources().getColor(R.color.gray_f7f7f7));
        dividerPaint.setStyle(Paint.Style.FILL);


        topDividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        topDividerPaint.setColor(Color.parseColor("#9924f715"));
        topDividerPaint.setStyle(Paint.Style.FILL);


        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setColor(context.getResources().getColor(R.color.gray_212121));
        textPaint.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.sp14));

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
//        // 不是第一个 item 才设置 top
////        if (parent.getChildAdapterPosition(view) != 0) {
//        outRect.top = groupDividerHeight;
////        }
//        Log.d(TAG, "getItemOffsets: left = " + outRect.left + ",top = " + outRect.top + ",right = " + outRect.right
//                + ", bottom = " + outRect.bottom);

        int position = parent.getChildAdapterPosition(view);
        // 获取组名
       /* String groupName = getGroupName(position - headerCount);
        if (groupName == null) {
            return;
        }*/
        if (position < headerCount) {
            outRect.top = 0;
        } else if (isGroupFirst(position)) {
            outRect.top = groupDividerHeight;
        } else {
            outRect.top = 1;
        }
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(canvas, parent, state);
        // getChildCount() 获取的是当前屏幕可见 item 数量，而不是 RecyclerView 所有的 item 数量
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = parent.getChildAt(i);
            // 获取当前itemview在adapter中的索引
            int childAdapterPosition = parent.getChildAdapterPosition(childView);
            /**
             * 由于分割线是绘制在每一个 itemview 的顶部，所以分割线矩形 rect.bottom = itemview.top,
             * rect.top = itemview.top - groupDividerHeight
             */
            int bottom = childView.getTop();
            int left = parent.getPaddingLeft();
            int right = parent.getPaddingRight();
            if (isGroupFirst(childAdapterPosition)) {   // 是分组第一个，则绘制分组分割线
                int top = bottom - groupDividerHeight;
                // 绘制分组分割线矩形
                canvas.drawRect(left + dividerPaddingLeft, top,
                        childView.getWidth() - right - dividerPaddingRight, bottom, dividerPaint);
                // 绘制分组分割线中的文字
                float baseLine = (top + bottom) / 2f - (textPaint.descent() + textPaint.ascent()) / 2f;
                String name = getGroupName(childAdapterPosition - headerCount);
                if (name != null)
                    canvas.drawText(name, left + dp2Px(16),
                            baseLine, textPaint);
            } else {    // 不是分组中第一个，则绘制常规分割线
                int top = bottom - 1;
                canvas.drawRect(left + dividerPaddingLeft, top,
                        childView.getWidth() - right - dividerPaddingRight, bottom, dividerPaint);
            }
        }
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);
        if (headerCount == 0) return;
        ViewGroup firstVisibleView = (ViewGroup) parent.getChildAt(0);
        if (firstVisibleView.getChildCount() == 1) return;
        int firstVisiblePosition = parent.getChildAdapterPosition(firstVisibleView);
        String groupName = getGroupName(firstVisiblePosition - headerCount);
        int left = parent.getPaddingLeft();
        int right = firstVisibleView.getWidth() - parent.getPaddingRight();
        // 第一个itemview(firstVisibleView) 的 bottom 值小于分割线高度，分割线随着 recyclerview 滚动，
        // 分割线top固定不变，bottom=firstVisibleView.bottom
        if (firstVisibleView.getBottom() <= groupDividerHeight && isGroupFirst(firstVisiblePosition + 1)) {
            canvas.drawRect(left, 0, right, firstVisibleView.getBottom(), dividerPaint);
            float baseLine = firstVisibleView.getBottom()- groupDividerHeight/ 2f - (textPaint.descent() + textPaint.ascent()) / 2f;
            canvas.drawText(groupName, left + dp2Px(16),
                    baseLine, textPaint);
        } else if (groupName != null) {
            canvas.drawRect(left, 0, right, groupDividerHeight, dividerPaint);
            float baseLine = groupDividerHeight / 2f - (textPaint.descent() + textPaint.ascent()) / 2f;
            canvas.drawText(groupName, left + dp2Px(16), baseLine, textPaint);
        }
    }


    private OnGroupListener listener;

    public interface OnGroupListener {

        // 获取分组中第一个文字
        String getLetter(int position);
    }


    public String getGroupName(int position) {
        if (listener != null) {
            return listener.getLetter(position);
        }
        return null;
    }


    /**
     * 是否是某组中第一个item
     *
     * @param position
     * @return
     */
    private boolean isGroupFirst(int position) {
        // 第一个 itemView 肯定是新的一个分组
        if (position == 0) {
            return true;
        } else {
            String preGroupName = getGroupName(position - headerCount - 1);
            String groupName = getGroupName(position - headerCount);
            return !TextUtils.equals(preGroupName, groupName);
        }
    }


    private int dp2Px(int dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

    private int sp2Px(int spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, context.getResources().getDisplayMetrics());
    }
}
