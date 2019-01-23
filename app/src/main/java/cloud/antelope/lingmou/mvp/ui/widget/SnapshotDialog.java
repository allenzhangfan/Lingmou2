package cloud.antelope.lingmou.mvp.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.cjt2325.cameralibrary.util.ScreenUtils;
import com.jess.arms.http.imageloader.glide.GlideArms;
import com.lingdanet.safeguard.common.utils.SizeUtils;

import cloud.antelope.lingmou.R;
import timber.log.Timber;

public class SnapshotDialog implements View.OnClickListener {
    Activity activity;
    RelativeLayout root;
    OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    private final View viewBg;
    private final View viewWhite;
    private final ImageView ivPic, ivPic_;
    private final LinearLayout llBottom;
    private final ImageButton ibClose;
    private final RelativeLayout rlCenter;

    public SnapshotDialog(Activity activity) {
        this.activity = activity;
        root = (RelativeLayout) LayoutInflater.from(activity).inflate(R.layout.view_snapshot, null);
        viewBg = root.findViewById(R.id.view_bg);
        rlCenter = root.findViewById(R.id.rl_center);
        viewWhite = root.findViewById(R.id.view_white);
        ivPic = root.findViewById(R.id.iv_pic);
        ivPic_ = root.findViewById(R.id.iv_pic_);
        llBottom = root.findViewById(R.id.ll_bottom);
        root.findViewById(R.id.ll_collect).setOnClickListener(this);
        root.findViewById(R.id.ll_download).setOnClickListener(this);
        ibClose = root.findViewById(R.id.ib_close);
        ibClose.setOnClickListener(this);
    }

    public void show(String picPath) {
        /**
         * android:layout_width="@dimen/dp320"
         android:layout_height="@dimen/dp212"
         android:paddingBottom="@dimen/dp32"
         */
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ivPic_.getLayoutParams();
        int screenWidth = ScreenUtils.getScreenHeight(activity);
        int screenHeight = ScreenUtils.getScreenWidth(activity);
        GlideArms.with(activity).load(picPath).dontAnimate().into(ivPic_);
        GlideArms.with(activity).load(picPath).dontAnimate().into(ivPic);
        WindowManager.LayoutParams mWindowLayoutParams = new WindowManager.LayoutParams();
        mWindowLayoutParams.format = PixelFormat.TRANSLUCENT;
        mWindowLayoutParams.gravity = Gravity.BOTTOM;
        mWindowLayoutParams.x = 0;
        mWindowLayoutParams.y = 0;
        mWindowLayoutParams.alpha = 1f;
        mWindowLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mWindowLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        activity.getWindowManager().addView(root, mWindowLayoutParams);

        ValueAnimator animator1 = ValueAnimator.ofFloat(0, 1.0f);
        animator1.setDuration(500);
        animator1.addUpdateListener(animation -> {
            float fraction = animation.getAnimatedFraction();
            layoutParams.width = (int) (screenWidth * (1 - fraction) + fraction * activity.getResources().getDimensionPixelSize(R.dimen.dp320));
            layoutParams.height = (int) (screenHeight * (1 - fraction) + fraction * activity.getResources().getDimensionPixelSize(R.dimen.dp180));
            layoutParams.setMargins(0, (int) (fraction * activity.getResources().getDimensionPixelSize(R.dimen.dp82)),0 ,0);
            ivPic_.setLayoutParams(layoutParams);
        });
        animator1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ivPic_.setVisibility(View.GONE);
                ivPic.setVisibility(View.VISIBLE);
                viewWhite.setVisibility(View.VISIBLE);
                llBottom.setVisibility(View.VISIBLE);
                ibClose.setVisibility(View.VISIBLE);
            }
        });
        animator1.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_collect:
                if(onClickListener!=null){
                    onClickListener.onClickCollect();
                }
                break;
            case R.id.ll_download:
                if(onClickListener!=null){
                    onClickListener.onClickDownload();
                }
                break;
        }
        dismiss();
    }

    private void dismiss() {
        rlCenter.setVisibility(View.GONE);
        ibClose.setVisibility(View.GONE);
        ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 0);
        animator.setDuration(500);
        animator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            viewBg.setAlpha(value);
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                try {
                    activity.getWindowManager().removeView(root);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        animator.start();
    }
    public interface OnClickListener{
        void onClickCollect();
        void onClickDownload();
    }
}
