package cloud.antelope.lingmou.mvp.ui.adapter;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jess.arms.http.imageloader.glide.GlideArms;
import com.lingdanet.safeguard.common.constant.CommonConstant;
import com.lingdanet.safeguard.common.constant.TimeConstants;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.SpanUtils;
import com.lingdanet.safeguard.common.utils.TimeUtils;
import com.lingdanet.safeguard.common.utils.Utils;

import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.common.UrlConstant;
import cloud.antelope.lingmou.mvp.model.entity.AnswerItemEntity;
import cloud.antelope.lingmou.mvp.ui.widget.FlexibleDividerDecoration;


/**
 * 作者：安兴亚
 * 创建日期：2017/07/26
 * 邮箱：anxingya@lingdanet.com
 * 描述：告警评论详情的适配器
 */

public class CommentDetailAdapter extends BaseQuickAdapter<AnswerItemEntity, BaseViewHolder>
        implements FlexibleDividerDecoration.VisibilityProvider {

    public CommentDetailAdapter(List<AnswerItemEntity> list) {
        super(R.layout.item_comment_detail, list);
    }

    @Override
    protected void convert(final BaseViewHolder helper, AnswerItemEntity item) {

        String basrUrl = SPUtils.getInstance().getString(Constants.URL_BASE);
        String url = basrUrl +"/api/" + item.getCreateUserAvatar() + "?time=" + System.currentTimeMillis();
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
        helper.setText(R.id.user_name_tv, "5".equals(item.getCreateUserType()) ? "管理员" : item.getCreateUserNickName());
        if (helper.getAdapterPosition() == 0) {
            helper.itemView.setBackgroundColor(Utils.getContext().getResources().getColor(R.color.white));
            helper.setText(R.id.user_reply_tv, item.getReply());
        } else {
            helper.itemView.setBackgroundColor(Utils.getContext().getResources().getColor(R.color.bg_app));
            SpannableStringBuilder text =
                    new SpanUtils().append("回复 ")
                            .setForegroundColor(Utils.getContext().getResources().getColor(R.color.mine_text_color))
                            .append("5".equals(item.getReUserType()) ? "管理员" : item.getReUserName())
                            .setForegroundColor(Utils.getContext().getResources().getColor(R.color.color_msg_admin))
                            .append("：" + item.getReply())
                            .setForegroundColor(Utils.getContext().getResources().getColor(R.color.mine_text_color))
                            .setFlag(SpannableString.SPAN_INCLUSIVE_EXCLUSIVE)
                            .create();
            helper.setText(R.id.user_reply_tv, text);
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
        if (position == 0 || position == 1) {
            return true;
        } else {
            return false;
        }
    }
}
