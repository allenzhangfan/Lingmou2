package cloud.antelope.lingmou.mvp.ui.widget.popup;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lingdanet.safeguard.common.utils.KeyboardUtils;
import com.lingdanet.safeguard.common.utils.LogUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.lingdanet.safeguard.common.utils.Utils;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.DefaultTextWatcher;

/**
 * @author RocXu
 * @brief 评论弹出框
 */
public class CommentPopWindow implements View.OnClickListener {
    private static final int MAX_TEXT_LENGTH = 60;
    private static final int MIN_TEXT_LENGTH = 10;

    private Context mContext;
    private LayoutInflater inflater;
    private int resourceId;
    private View parent;
    // 底部弹框
    private PopupWindow popupWindow;
    private TextView mCancelTv;
    private TextView mSendTv;
    private EditText mCommentEt;

    private String mCommentId;
    private String mTopReplyId;
    private String mUpReplyId;
    private String mReUserName;
    private String mReUserId;

    private onSendCommentClickListener listener;

    public CommentPopWindow(Context context, View parent, int resID, String commentId, String topReplyId,
                            String upReplyId, String reUserName, String reUserId) {
        this.mContext = context;
        this.resourceId = resID;
        this.parent = parent;
        mCommentId = commentId;
        mTopReplyId = topReplyId;
        mUpReplyId = upReplyId;
        mReUserName = reUserName;
        mReUserId = reUserId;

        initView();
        initListener();
    }


    private void initView() {
        inflater = LayoutInflater.from(mContext);
        View panelView = inflater.inflate(resourceId, null);

        popupWindow = new PopupWindow(panelView, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mCancelTv = (TextView) panelView.findViewById(R.id.cancel_tv);
        mSendTv = (TextView) panelView.findViewById(R.id.send_tv);
        mCommentEt = (EditText) panelView.findViewById(R.id.pop_comment_tv);
    }

    private void initListener() {
        mCancelTv.setOnClickListener(this);
        mSendTv.setOnClickListener(this);
        mCommentEt.addTextChangedListener(new DefaultTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString().trim())) {
                    mSendTv.setEnabled(true);
                    if (s.length() >= MAX_TEXT_LENGTH) {
                        ToastUtils.showShort(R.string.comment_max_length_limit, MAX_TEXT_LENGTH);
                    }
                } else {
                    mSendTv.setEnabled(false);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_tv:
                if (null != popupWindow && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                popupWindow = null;
                break;

            case R.id.send_tv:
                if (mCommentEt.getText().toString().trim().length() < 10) {
                    ToastUtils.showShort(Utils.getContext()
                            .getString(R.string.comment_min_length_limit), MIN_TEXT_LENGTH);
                    return;
                }
                if (null != popupWindow && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                popupWindow = null;
                if (listener != null) {
                    listener.sendComment(mCommentId, mTopReplyId, mUpReplyId, mReUserName, mReUserId, mCommentEt.getText().toString().trim());
                }
                break;

            default:
                break;
        }

    }

    private void setWindowAlpha(float alpha) {
        if (alpha < 0 || alpha > 1) return;
        WindowManager.LayoutParams windowLP = ((Activity) mContext).getWindow().getAttributes();
        windowLP.alpha = alpha;
        if (alpha == 1) {
            ((Activity) mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
        } else {
            ((Activity) mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug
        }
        ((Activity) mContext).getWindow().setAttributes(windowLP);
    }

    public void showPopupWindow() {
        ColorDrawable cd = new ColorDrawable(0x000000);
        popupWindow.setBackgroundDrawable(cd);
        // WindowManager.LayoutParams lp = ((Activity) mContext).getWindow().getAttributes();
        // lp.alpha = 0.4f;
        // ((Activity) mContext).getWindow().setAttributes(lp);
        setWindowAlpha(0.4f);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(R.style.popupwindow_from_bottom_style);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                /*WindowManager.LayoutParams lp = ((Activity) mContext).getWindow().getAttributes();
                lp.alpha = 1f;
                ((Activity) mContext).getWindow().setAttributes(lp);*/
                setWindowAlpha(1f);
                if (listener != null) {
                    listener.saveDraft(mCommentId, mCommentEt.getText().toString());
                }
            }
        });

        popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        mCommentEt.postDelayed(new Runnable() {
            @Override
            public void run() {
            KeyboardUtils.showSoftInput(mCommentEt);
            }
        },50);
    }

    public void setLocation(int gravity, int offsetX, int offsetY) {
        popupWindow.showAtLocation(parent, gravity, offsetX, offsetY);
    }

    public void setCommentDraft(String draft) {
        if (!TextUtils.isEmpty(draft)) {
            mCommentEt.setText(draft);
            mCommentEt.setSelection(draft.length());
        }
    }

    public void setCommentHint(String hint) {
        mCommentEt.setHint(hint);
    }

    public interface onSendCommentClickListener {
        /**
         * 发送评论
         *
         * @param commentId  评论的id
         * @param topReplyId 评论顶层条目的id
         * @param upReplyId  评论上一个条目id
         * @param reUserName 被评论的用户的名字
         * @param reUserId   被评论的用户的id
         * @param reply      评论的内容
         */
        void sendComment(String commentId, String topReplyId,
                         String upReplyId, String reUserName, String reUserId, String reply);

        /**
         * 保留草稿
         *
         * @param commentId 评论的id
         * @param draft     评论的草稿
         */
        void saveDraft(String commentId, String draft);
    }

    public void onSendCommentClickListener(onSendCommentClickListener listener) {
        this.listener = listener;
    }

}
