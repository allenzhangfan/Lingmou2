package cloud.antelope.lingmou.mvp.ui.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.model.entity.CameraItem;


/**
 * Created by Administrator on 2017/11/13.
 */

public class OrganizationAdapter extends BaseQuickAdapter<CameraItem, BaseViewHolder> {
    public OrganizationAdapter(@Nullable List<CameraItem> data) {
        super(R.layout.organization_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CameraItem item) {
        helper.setText(R.id.org_name, item.getName());
    }
}
