package cloud.antelope.lingmou.mvp.ui.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lingdanet.safeguard.common.utils.SizeUtils;

import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.model.entity.AppBean;

/**
 * Created by ChenXinming on 2017/12/27.
 * description:
 */

public class AppAdapter extends BaseQuickAdapter<AppBean, BaseViewHolder> {
    public AppAdapter(@Nullable List<AppBean> data) {
        super(R.layout.item_app_fragment, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AppBean item) {
        TextView appNameTv = helper.getView(R.id.app_name_tv);
        ImageView appIv = helper.getView(R.id.app_iv);
        appNameTv.setText(item.getName());
        appIv.setImageDrawable(item.getTopDrawable());
    }
}
