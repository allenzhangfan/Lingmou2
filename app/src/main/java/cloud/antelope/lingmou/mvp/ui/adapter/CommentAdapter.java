package cloud.antelope.lingmou.mvp.ui.adapter;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jess.arms.http.imageloader.glide.GlideArms;
import com.lingdanet.safeguard.common.constant.CommonConstant;
import com.lingdanet.safeguard.common.constant.TimeConstants;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.SizeUtils;
import com.lingdanet.safeguard.common.utils.SpanUtils;
import com.lingdanet.safeguard.common.utils.TimeUtils;
import com.lingdanet.safeguard.common.utils.Utils;

import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.common.UrlConstant;
import cloud.antelope.lingmou.mvp.model.entity.AnswerItemEntity;
import cloud.antelope.lingmou.mvp.model.entity.CommentItemEntity;
import cloud.antelope.lingmou.mvp.ui.widget.FlexibleDividerDecoration;

/**
 * 作者：安兴亚
 * 创建日期：2017/07/26
 * 邮箱：anxingya@lingdanet.com
 * 描述：告警评论的适配器
 */

public class CommentAdapter extends BaseQuickAdapter<CommentItemEntity, BaseViewHolder>
        implements FlexibleDividerDecoration.VisibilityProvider {


    public CommentAdapter(List<CommentItemEntity> list) {
        super(R.layout.item_case_comment, list);
    }

    @Override
    protected void convert(final BaseViewHolder helper, CommentItemEntity item) {
        String basrUrl = SPUtils.getInstance().getString(Constants.URL_BASE);
        String url = basrUrl + "/api/" + item.getCreateUserAvatar() + "?time=" + System.currentTimeMillis();
        GlideArms.with(Utils.getContext())                      //配置上下文
                .load(url)                                  //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                // .asBitmap()                                 //设置图片为bitmap
                .placeholder(R.drawable.default_avatar)     //设置占位图片
                .error(R.drawable.default_avatar)           //设置错误图片
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        helper.setImageDrawable(R.id.user_avatar_cv, resource);
                    }
                });

        helper.setText(R.id.commit_time_tv, getFormatTime(item.getCreateTime()));
        helper.setText(R.id.user_name_tv,"5".equals(item.getCreateUserType()) ? "管理员" : item.getCreateUserNickName());
        helper.setText(R.id.user_reply_tv, item.getReply());
        List<AnswerItemEntity> answerList = item.getAnswerList();
        if (null != answerList && !answerList.isEmpty()) {
            helper.setVisible(R.id.reply_ll, true);
            LinearLayout replyList = (LinearLayout) helper.getView(R.id.reply_ll);
            replyList.removeAllViews();
            for (int i = 0; i < answerList.size(); i++) {
                if (i < 4) {
                    AnswerItemEntity answerItem = answerList.get(i);
                    AppCompatTextView textView = new AppCompatTextView(Utils.getContext());
                    SpannableStringBuilder replyText =
                            new SpanUtils().append("5".equals(answerItem.getCreateUserType()) ? "管理员" : answerItem.getCreateUserNickName())
                                    .setForegroundColor(Utils.getContext().getResources().getColor(R.color.color_msg_admin))
                                    .append(" 回复 ")
                                    .setForegroundColor(Utils.getContext().getResources().getColor(R.color.mine_text_color))
                                    .append("5".equals(answerItem.getReUserType()) ? "管理员 ：" : answerItem.getReUserName() + " ：")
                                    .setForegroundColor(Utils.getContext().getResources().getColor(R.color.color_msg_admin))
                                    .append(answerItem.getReply())
                                    .setForegroundColor(Utils.getContext().getResources().getColor(R.color.mine_text_color))
                                    .setFlag(SpannableString.SPAN_INCLUSIVE_EXCLUSIVE)
                                    .create();
                    textView.setText(replyText);
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12.0f);
                    textView.setGravity(Gravity.TOP | Gravity.LEFT);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    // lp.setMargins(SizeUtils.dp2px(8), SizeUtils.dp2px(8), SizeUtils.dp2px(8), SizeUtils.dp2px(8));
                    textView.setLayoutParams(lp);
                    // textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD)); // 设置粗体
                    textView.setPadding(SizeUtils.dp2px(8), SizeUtils.dp2px(8), SizeUtils.dp2px(8), SizeUtils.dp2px(8));
                    // if (null != mOnAnswerItemClickListener) {
                    //     textView.setOnClickListener(new View.OnClickListener() {
                    //         @Override
                    //         public void onClick(View v) {
                    //             mOnAnswerItemClickListener.onAnswerItemClick(answerItem);
                    //         }
                    //     });
                    // }
                    replyList.addView(textView);
                } else {
                    AppCompatTextView textView = new AppCompatTextView(Utils.getContext());
                    SpannableStringBuilder seeAllText =
                            new SpanUtils().append("查看全部 > ")
                                    .setForegroundColor(Utils.getContext().getResources().getColor(R.color.color_msg_admin))
                                    .setFlag(SpannableString.SPAN_INCLUSIVE_EXCLUSIVE)
                                    .create();
                    textView.setText(seeAllText);
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12.0f);
                    textView.setGravity(Gravity.TOP | Gravity.LEFT);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(SizeUtils.dp2px(8), SizeUtils.dp2px(8), SizeUtils.dp2px(8), SizeUtils.dp2px(8));
                    textView.setLayoutParams(lp);
                    // textView.setPadding(SizeUtils.dp2px(8), SizeUtils.dp2px(8), SizeUtils.dp2px(8), SizeUtils.dp2px(8));
                    // if (null != mOnAnswerItemClickListener) {
                    //     textView.setOnClickListener(new View.OnClickListener() {
                    //         @Override
                    //         public void onClick(View v) {
                    //             mOnAnswerItemClickListener.onSeeAllItemClick(answerList);
                    //         }
                    //     });
                    // }
                    replyList.addView(textView);
                    break;
                }
            }
        } else {
            helper.setVisible(R.id.reply_ll, false);
        }
    }

    private String getFormatTime(long convertTime) {
        long serverTime = SPUtils.getInstance().getLong(CommonConstant.SERVER_DATE);
        long interval = Math.abs(serverTime - convertTime) / 1000;
        if (interval < 60) {                                    //1分钟内
            return String.valueOf(TimeUtils.getTimeSpan(serverTime, convertTime, TimeConstants.SEC)) + "秒前";
        } else if (interval >= 60 && interval < 3600) {         //1小时内
            return String.valueOf(TimeUtils.getTimeSpan(serverTime, convertTime, TimeConstants.MIN)) + "分钟前";
        } else if (interval >= 3600 && interval < 86400) {      // 1天内
            return String.valueOf(TimeUtils.getTimeSpan(serverTime, convertTime, TimeConstants.HOUR) + "小时前");
        } else if (interval >= 86400 && interval < 604800) {    // 7天内
            return String.valueOf(TimeUtils.getTimeSpan(serverTime, convertTime, TimeConstants.DAY) + "天前");
        } else if (interval >= 604800 &&
                TimeUtils.millis2String(serverTime, "yyyy").equals(TimeUtils.millis2String(convertTime, "yyyy"))) {
            return TimeUtils.millis2String(convertTime, "M月d日");
        } else if (interval >= 604800 &&
                !TimeUtils.millis2String(serverTime, "yyyy").equals(TimeUtils.millis2String(convertTime, "yyyy"))) {
            return TimeUtils.millis2String(convertTime, "yyyy年M月d日");
        }

        return null;
    }

    @Override
    public boolean shouldHideDivider(int position, RecyclerView parent) {
        if (position == 0) {
            return true;
        } else {
            return false;
        }
    }
}
