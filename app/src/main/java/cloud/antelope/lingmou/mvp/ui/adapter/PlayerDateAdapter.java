package cloud.antelope.lingmou.mvp.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lingdanet.safeguard.common.utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import cloud.antelope.lingmou.R;

/**
 * 作者：陈新明
 * 创建日期：2018/06/27
 * 邮箱：chenxinming@antelop.cloud
 * 描述：TODO
 */
public class PlayerDateAdapter extends BaseQuickAdapter<Date, BaseViewHolder> {
    public PlayerDateAdapter() {
        super(R.layout.player_date_item, null);
    }

    @Override
    protected void convert(BaseViewHolder helper, Date item) {
        String date2String = TimeUtils.date2String(item, new SimpleDateFormat("MM/dd"));
        helper.setText(R.id.date_tv, date2String);
    }

}
