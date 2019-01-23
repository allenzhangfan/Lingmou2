package cloud.antelope.lingmou.mvp.ui.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.model.entity.OrgMainEntity;

/**
 * Created by ChenXinming on 2018/1/22.
 * description:
 */

public class DeviceParentAdapter extends BaseQuickAdapter<OrgMainEntity, BaseViewHolder> {
    public DeviceParentAdapter(@Nullable List data) {
        super(R.layout.item_device_parent, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, OrgMainEntity item) {
        helper.setText(R.id.tv_name, item.mIsRootOrg ? item.mAliasName : item.organizationName);
        helper.setBackgroundRes(R.id.iv,item.level1?R.drawable.tree_bitmap_1:R.drawable.tree_bitmap_2);
    }
}
