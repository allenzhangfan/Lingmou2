package cloud.antelope.lingmou.mvp.ui.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.model.entity.WorkbenchBean;

/**
 * 作者：陈新明
 * 创建日期：2018/09/20
 * 邮箱：chenxinming@antelop.cloud
 * 描述：TODO
 */
public class WorkbenchAdapter extends BaseQuickAdapter<WorkbenchBean, BaseViewHolder> {

    public WorkbenchAdapter(@Nullable List<WorkbenchBean> data) {
        super(R.layout.item_workbench, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, WorkbenchBean item) {
        helper.setText(R.id.item_workbench_tv, item.getName());
        helper.setBackgroundRes(R.id.item_workbench_bg_rv, item.getBgDrawable());
        helper.setImageResource(R.id.item_workbench_iv, item.getIvDrawable());
        helper.setTextColor(R.id.item_workbench_tv,item.getTextColor());
    }
}
