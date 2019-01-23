package cloud.antelope.lingmou.mvp.ui.widget.popup;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lingdanet.safeguard.common.utils.SizeUtils;

import cloud.antelope.lingmou.R;

/**
 * @author RocXu
 * @brief 分享弹出框
 */
public class SharePopWindow implements View.OnClickListener {
    private static final String TAG = "SharePopWindow";
    private Context mContext;
    private LayoutInflater inflater;
    private int resourceID;
    private View parent;
    // 底部弹框
    private PopupWindow popupWindow;
    private RelativeLayout qqBtn, qzoneBtn, wechatBtn, wechatMomentsBtn;
    private TextView cancelBtn;
    private onPlatformClickListener listener;

    private int mPopHeight;

    public SharePopWindow(Context context, View parent, int resID) {
        this.mContext = context;
        this.resourceID = resID;
        this.parent = parent;

        mPopHeight = SizeUtils.dp2px(188);
        initView();
        startAnimation();
        initListener();
    }


    private void initView() {
        inflater = LayoutInflater.from(mContext);
        View panelView = inflater.inflate(resourceID, null);

        popupWindow = new PopupWindow(panelView, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        qqBtn = (RelativeLayout) panelView.findViewById(R.id.qq_btn);
        qzoneBtn = (RelativeLayout) panelView.findViewById(R.id.qzone_btn);
        wechatBtn = (RelativeLayout) panelView.findViewById(R.id.wechat_btn);
        wechatMomentsBtn = (RelativeLayout) panelView.findViewById(R.id.wechat_moments_btn);
        cancelBtn = (TextView) panelView.findViewById(R.id.cancel_text);
    }

    private void initListener() {
        cancelBtn.setOnClickListener(this);
        qqBtn.setOnClickListener(this);
        qzoneBtn.setOnClickListener(this);
        wechatBtn.setOnClickListener(this);
        wechatMomentsBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_text:
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                break;

            case R.id.qq_btn:
                popupWindow.dismiss();
                if (listener != null) {
                    listener.qqClick();
                }
                break;

            case R.id.qzone_btn:
                popupWindow.dismiss();
                if (listener != null) {
                    listener.qzoneClick();
                }

                break;

            case R.id.wechat_btn:
                popupWindow.dismiss();
                if (listener != null) {
                    listener.wechatClick();
                }

                break;

            case R.id.wechat_moments_btn:
                popupWindow.dismiss();
                if (listener != null) {
                    listener.wechatMomentsClick();
                }
                break;

            default:
                break;
        }

    }

    public void showPopupWindow() {
        ColorDrawable cd = new ColorDrawable(0x000000);
        popupWindow.setBackgroundDrawable(cd);
        WindowManager.LayoutParams lp = ((Activity) mContext).getWindow().getAttributes();
        lp.alpha = 0.4f;
        ((Activity) mContext).getWindow().setAttributes(lp);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(R.style.popupwindow_from_bottom_style);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = ((Activity) mContext).getWindow().getAttributes();
                lp.alpha = 1f;
                ((Activity) mContext).getWindow().setAttributes(lp);
            }
        });

        popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);

    }

    public interface onPlatformClickListener {
        void qqClick();

        void qzoneClick();

        void wechatClick();

        void wechatMomentsClick();
    }

    public void setOnPlatformClickListener(onPlatformClickListener listener) {
        this.listener = listener;
    }


    private void startAnimation() {
        buttonAnimation(wechatMomentsBtn, 100);
        buttonAnimation(wechatBtn, 200);
        buttonAnimation(qzoneBtn, 300);
        buttonAnimation(qqBtn, 400);
    }

    private void buttonAnimation(RelativeLayout button, int delayTime) {
        button.setTranslationY(button.getY() + mPopHeight);
        button.animate()
                .translationY(0)
                .setDuration(400)
                .setInterpolator(new OvershootInterpolator(1))
                .setStartDelay(delayTime);
    }
}
