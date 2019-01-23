package cloud.antelope.lingmou.mvp.ui.adapter;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lingdanet.safeguard.common.utils.TimeUtils;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.model.entity.NewsItemEntity;

/**
 * 作者：陈新明
 * 创建日期：2018/04/09
 * 邮箱：chenxinming@antelop.cloud
 * 描述：TODO
 */
public class VictoryAdapter extends BaseQuickAdapter<NewsItemEntity, BaseViewHolder> {

    public VictoryAdapter() {
        super(R.layout.victory_item, null);
    }

    @Override
    protected void convert(BaseViewHolder helper, NewsItemEntity item) {
        // long time = TimeUtils.string2Millis(item.getObjTime(),new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()));
        if (!TextUtils.isEmpty(item.getObjTime())) {
            String createTime = TimeUtils.millis2String(Long.parseLong(item.getObjTime()), TimeUtils.FORMAT_FOUR);
            helper.setText(R.id.create_time_tv, createTime);
        } else {
            helper.setText(R.id.create_time_tv, "");
        }
        String publicTime = TimeUtils.millis2String(Long.parseLong(item.getPublishTime()), TimeUtils.FORMAT_FOUR + " " + TimeUtils.SHORT_TIME_FORMAT);
        helper.setText(R.id.publish_time_tv, publicTime);
        helper.setText(R.id.content_tv, item.getContent());
    }
}
