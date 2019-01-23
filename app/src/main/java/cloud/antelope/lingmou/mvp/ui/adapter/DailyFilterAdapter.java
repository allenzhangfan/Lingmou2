package cloud.antelope.lingmou.mvp.ui.adapter;

import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lingdanet.safeguard.common.utils.Utils;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.model.entity.DailyFilterItemEntity;

/**
 * 作者：陈新明
 * 创建日期：2018/09/07
 * 邮箱：chenxinming@antelop.cloud
 * 描述：filter的Adapter
 */
public class DailyFilterAdapter extends BaseQuickAdapter<DailyFilterItemEntity, BaseViewHolder> {

    public DailyFilterAdapter() {
        super(R.layout.item_daily_filter, null);
    }

    @Override
    protected void convert(BaseViewHolder helper, DailyFilterItemEntity item) {
        LinearLayout llRoot = helper.getView(R.id.ll_root);
        llRoot.setBackgroundResource(!item.isSelect() ? R.drawable.shape_rect_corner_2_f7f7f7 : R.drawable.shape_rect_corner_2_fff8e1);
        helper.setText(R.id.item_filter_name_tv, item.getName());
        helper.setTextColor(R.id.item_filter_name_tv, item.isSelect() ? Utils.getContext().getResources().getColor(R.color.yellow_ff8f00) : Utils.getContext().getResources().getColor(R.color.gray_212121));
//        helper.setVisible(R.id.item_filter_select_iv, item.isSelect());
    }
}
