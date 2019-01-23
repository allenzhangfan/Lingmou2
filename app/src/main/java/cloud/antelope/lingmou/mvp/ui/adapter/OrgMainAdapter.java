package cloud.antelope.lingmou.mvp.ui.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.model.entity.OrgMainEntity;
import java.util.List;

/**
 * 作者：陈新明
 * 创建日期：2018/05/08
 * 邮箱：chenxinming@antelop.cloud
 * 描述：Org V2.0 Adapter
 */
public class OrgMainAdapter extends BaseQuickAdapter<OrgMainEntity, BaseViewHolder> {
    public OrgMainAdapter(@Nullable List<OrgMainEntity> data) {
        super(R.layout.organization_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, OrgMainEntity item) {
        helper.setText(R.id.org_name, item.mIsRootOrg ? item.mAliasName : item.organizationName);
    }
}
