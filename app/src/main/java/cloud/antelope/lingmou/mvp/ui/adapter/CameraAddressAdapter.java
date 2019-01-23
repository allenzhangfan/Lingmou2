package cloud.antelope.lingmou.mvp.ui.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lingdanet.safeguard.common.utils.Utils;

import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.model.entity.CameraItem;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;

/**
 * Created by Administrator on 2017/11/13.
 */

public class CameraAddressAdapter extends BaseQuickAdapter<OrgCameraEntity, BaseViewHolder> {
    public CameraAddressAdapter(@Nullable List<OrgCameraEntity> data) {
        super(R.layout.simple_text_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, OrgCameraEntity item) {
        helper.setText(R.id.simple_tv, TextUtils.isEmpty(item.getDeviceName())
                ? Utils.getContext().getString(R.string.unknow_camera) : item.getDeviceName());
    }
}
