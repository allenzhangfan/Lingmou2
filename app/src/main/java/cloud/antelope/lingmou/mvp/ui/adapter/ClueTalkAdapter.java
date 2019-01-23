package cloud.antelope.lingmou.mvp.ui.adapter;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jess.arms.http.imageloader.glide.GlideArms;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.TimeUtils;
import com.lingdanet.safeguard.common.utils.Utils;

import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.common.UrlConstant;
import cloud.antelope.lingmou.mvp.model.entity.AnswerItemEntity;
import cloud.antelope.lingmou.mvp.ui.widget.CircleImageView;


/**
 * Created by liucheng on 16/6/12.
 * 告警列表适配器
 */
public class ClueTalkAdapter extends BaseMultiItemQuickAdapter<AnswerItemEntity, BaseViewHolder> {

    public static final int TYPE_TALK_LEFT = 1;
    public static final int TYPE_TALK_RIGHT = 2;
    private String mNickName;

    public ClueTalkAdapter(List<AnswerItemEntity> list) {
        super(list);
        addItemType(TYPE_TALK_LEFT, R.layout.item_clue_talk_left);
        addItemType(TYPE_TALK_RIGHT, R.layout.item_clue_talk_right);
        mNickName = SPUtils.getInstance().getString(Constants.CONFIG_LAST_USER_LOGIN_NAME, "");
    }

    @Override
    protected void convert(BaseViewHolder helper, AnswerItemEntity item) {
        helper.setText(R.id.talk_time_tv, TimeUtils.millis2String(item.getCreateTime(), TimeUtils.FORMAT_TWO));
        helper.setText(R.id.talk_content_tv, item.getReply());
        View view = helper.getView(R.id.re_user_tv);
        CircleImageView leftView = helper.getView(R.id.user_avatar_cv_left);
        CircleImageView rightView = helper.getView(R.id.user_avatar_cv_right);
        if (null != view) {
            ((TextView)view).setText(mNickName);
        }
        String basrUrl = SPUtils.getInstance().getString(Constants.URL_BASE);
        String url = basrUrl + "/api/" + item.getCreateUserAvatar() + "?time=" + System.currentTimeMillis();
        if (null != leftView) {
            GlideArms.with(Utils.getContext())                      //配置上下文
                    .load(url)                                  //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                    // .asBitmap()                                 //设置图片为bitmap
                    .placeholder(R.drawable.default_avatar)     //设置占位图片
                    .error(R.drawable.default_avatar)           //设置错误图片
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                            helper.setImageDrawable(R.id.user_avatar_cv_left, resource);
                        }
                    });
        }
        if (null != rightView) {
            String userAvatar = SPUtils.getInstance().getString(Constants.CONFIG_LAST_USER_AVATAR);
            GlideArms.with(Utils.getContext())                      //配置上下文
                    .load(userAvatar)                                  //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                    // .asBitmap()                                 //设置图片为bitmap
                    .placeholder(R.drawable.default_avatar)     //设置占位图片
                    .error(R.drawable.default_avatar)           //设置错误图片
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                            helper.setImageDrawable(R.id.user_avatar_cv_right, resource);
                        }
                    });
        }
    }
}
