package cloud.antelope.lingmou.mvp.ui.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.lingdanet.safeguard.common.utils.SizeUtils;

import java.util.Timer;
import java.util.TimerTask;

import cloud.antelope.lingmou.R;
import timber.log.Timber;

public class FloatingDragView {
    public static final String TAG = "FloatingDragView";
    private FloatingDragLayout floatingDragLayout;
    private onClickListener onClickListener;
    private float cx = SizeUtils.getScreenWidth() - SizeUtils.dp2px(58);   // 中心X坐标
    private float cy = SizeUtils.getScreenHeight() - SizeUtils.dp2px(200);   // 中心Y坐标
    private float vx = 1; // X轴速度
    private float vy = 1; // Y轴速度
    private TimerTask timerTask;
    private Boolean isFluttering = false;
    private final View floatingView;


    public FloatingDragView(Context context, @LayoutRes int layoutResID) {
        // 用户布局
        View contentView = LayoutInflater.from(context).inflate(layoutResID, null);
        // 悬浮球按钮
        floatingView = LayoutInflater.from(context).inflate(R.layout.layout_floating_dragged, null);
        floatingView.setVisibility(View.GONE);
        // ViewDragHelper的ViewGroup容器
        floatingDragLayout = new FloatingDragLayout(context);
        floatingDragLayout.setFloatingDragView(this);
        floatingDragLayout.addView(contentView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        floatingDragLayout.addView(floatingView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (isFluttering) {
//                floatingDragLayout.restorePosition();
            }
            return false;
        }
    });

    public void start() {
        Timber.e("start");
        floatingView.setVisibility(View.VISIBLE);
        isFluttering = true;
    }

    public void show(int positionX, int positionY) {
        Timber.e("show");
        floatingView.setVisibility(View.VISIBLE);
        cx = positionX;
        cy = positionY;
    }

    public void show() {
        Timber.e("show");
        floatingView.setVisibility(View.VISIBLE);
    }

    public void stop() {
        isFluttering = false;
    }

    public void dismiss() {
        isFluttering = false;
        floatingView.setVisibility(View.GONE);
    }

    public void setOnClickListener(FloatingDragView.onClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public View getView() {
        return floatingDragLayout;
    }

    public View getFloatingView() {
        return floatingView;
    }

    public class FloatingDragLayout extends FrameLayout {
        ViewDragHelper dragHelper;
        FloatingDragButton floatingBtn;
        FloatingDragView floatingDragView;


        public void setFloatingDragView(FloatingDragView floatingDragView) {
            this.floatingDragView = floatingDragView;
        }

        public FloatingDragLayout(Context context) {
            this(context, null);
        }


        public FloatingDragLayout(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public FloatingDragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }

        private void init() {
            dragHelper = ViewDragHelper.create(FloatingDragLayout.this, 1.0f, new ViewDragHelper.Callback() {
                @Override
                public boolean tryCaptureView(View child, int pointerId) {
                    return child == floatingBtn;
                }

                @Override
                public int clampViewPositionVertical(View child, int top, int dy) {
                    if (top > getHeight() - child.getMeasuredHeight()) {
                        top = getHeight() - child.getMeasuredHeight();
                    } else if (top < 0) {
                        top = 0;
                    }
                    return top;
                }

                @Override
                public int clampViewPositionHorizontal(View child, int left, int dx) {
                    if (left > getWidth() - child.getMeasuredWidth()) {
                        left = getWidth() - child.getMeasuredWidth();
                    } else if (left < 0) {
                        left = 0;
                    }
                    return left;
                }

                @Override
                public int getViewVerticalDragRange(View child) {
                    return getMeasuredHeight() - child.getMeasuredHeight();
                }

                @Override
                public int getViewHorizontalDragRange(View child) {
                    return getMeasuredWidth() - child.getMeasuredWidth();
                }

                @Override
                public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                    super.onViewPositionChanged(changedView, left, top, dx, dy);
                    cx += dx;
                    cy += dy;
                }

                @Override
                public void onViewDragStateChanged(int state) {
                    super.onViewDragStateChanged(state);
                    if (state == ViewDragHelper.STATE_DRAGGING) {
                        isFluttering = false;
                    }
                }

                @Override
                public void onViewReleased(View releasedChild, float xvel, float yvel) {
                    if (releasedChild == floatingBtn) {
                        isFluttering = true;
                    }
                }
            });
        }


        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            timerTask.cancel();
        }


        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            floatingBtn = findViewById(R.id.floatingBtn);
            floatingBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickListener != null) {
                        onClickListener.onClick();
                    }
                }
            });
            floatingBtn.setmFloatingDragView(floatingDragView);
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    mHandler.sendEmptyMessage(0);
                }
            };
            new Timer().schedule(timerTask, 16, 16);
        }


        /**
         * 更新位置
         */
        public void restorePosition() {
            cx += vx;
            cy += vy;
            int left = getLeft();
            int top = getTop();
            int right = getRight();
            int bottom = getBottom();
            int buttonWidth = floatingBtn.getMeasuredWidth();
            int buttonHeight = floatingBtn.getMeasuredHeight();
            // 碰撞左右，X的速度取反
            if (cx <= left && vx < 0) {
                vx = -vx;
            } else if (cy <= top && vy < 0) {
                vy = -vy;
            } else if (cx + buttonWidth >= right && vx > 0) {
                vx = -vx;
            } else if (cy + buttonHeight >= bottom && vy > 0) {
                vy = -vy;
            }
            floatingBtn.proxyLayout((int) cx, (int) cy,
                    (int) cx + floatingBtn.getMeasuredWidth(), (int) cy + floatingBtn.getMeasuredHeight());

        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            floatingBtn.proxyLayout((int) cx, (int) cy,
                    (int) cx + floatingBtn.getMeasuredWidth(), (int) cy + floatingBtn.getMeasuredHeight());
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            return dragHelper.shouldInterceptTouchEvent(ev);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            dragHelper.processTouchEvent(event);
            return true;
        }

        @Override
        public void computeScroll() {
            if (dragHelper.continueSettling(true)) {
                invalidate();
            }
        }

    }

    /**
     * 防止用户调用requestLayout()导致闪烁
     */
    public static class FloatingDragButton extends android.support.v7.widget.AppCompatImageView {
        FloatingDragView mFloatingDragView;

        public void setmFloatingDragView(FloatingDragView mFloatingDragView) {
            this.mFloatingDragView = mFloatingDragView;
        }

        public FloatingDragButton(Context context) {
            super(context);
        }

        public FloatingDragButton(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public FloatingDragButton(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }


        @Override
        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mFloatingDragView.stop();
                    break;
                case MotionEvent.ACTION_UP:
//                    mFloatingDragView.start();
                    break;
            }
            return super.onTouchEvent(event);
        }

        @Override
        public void layout(int l, int t, int r, int b) {
//        super.layout(l, t, r, b);
        }

        public void proxyLayout(int l, int t, int r, int b) {
            super.layout(l, t, r, b);
        }
    }

    public interface onClickListener {
        void onClick();
    }

}
