package cloud.antelope.lingmou.mvp.ui.adapter;

import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lingdanet.safeguard.common.utils.Utils;

import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;

/**
 * Created by Administrator on 2017/11/13.
 */

public class CollectCameraAdapter extends BaseQuickAdapter<OrgCameraEntity, BaseViewHolder> {

    private String mOrgName;
    private String mSelectStr;

    public CollectCameraAdapter(@Nullable List<OrgCameraEntity> data, String orgName) {
        super(R.layout.collection_item, data);
        mOrgName = orgName;
    }

    public void setOrgName(String orgName) {
        mOrgName = orgName;
        notifyDataSetChanged();
    }

    @Override
    protected void convert(BaseViewHolder helper, OrgCameraEntity item) {
        String deviceName = item.getDeviceName();
        TextView nameTv = helper.getView(R.id.collect_name_tv);
        ImageButton mapIb = helper.getView(R.id.map_ib);
        if (!TextUtils.isEmpty(deviceName) && !TextUtils.isEmpty(mSelectStr) && deviceName.contains(mSelectStr)) {
            SpannableStringBuilder builder = new SpannableStringBuilder(deviceName);
            int from = deviceName.indexOf(mSelectStr);
            ForegroundColorSpan blueSpan = new ForegroundColorSpan(Utils.getContext().getResources().getColor(R.color.home_text_select));
            builder.setSpan(blueSpan, from, from + mSelectStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            nameTv.setText(builder);
        } else {
            nameTv.setText(deviceName);
        }
        if (null == item.getLongitude() || null == item.getLatitude() ) {
            mapIb.setVisibility(View.GONE);
        } else {
            mapIb.setVisibility(View.VISIBLE);
            helper.addOnClickListener(R.id.map_ib);
        }
        Long deviceType = item.getDeviceType();
        int deviceState = item.getDeviceState();
        int resId = R.drawable.camera_normal_offline;
        if (null != deviceType) {
            if (deviceType == Constants.ORG_CAMERA_ZHUAPAIJI) {
                //抓拍机
                resId = deviceState == 1 ? R.drawable.camera_zhuapai : R.drawable.camera_zhuapai_offline;
            } else if (deviceType == Constants.ORG_CAMERA_QIUJI) {
                //球机
                resId = deviceState == 1 ? R.drawable.camera_ball : R.drawable.camera_ball_offline;
            } else if(deviceType == Constants.ORG_CAMERA_PHONE) {
                //手机
                resId = deviceState == 1 ? R.drawable.camera_phone : R.drawable.camera_phone_offline;
            } else {
                //普通摄像机
                resId = deviceState == 1 ? R.drawable.camera_normal : R.drawable.camera_normal_offline;
            }
        }
        helper.setImageResource(R.id.collect_camera_iv, resId);

    }

    public void setSelectText(String selectText) {
        mSelectStr = selectText;
        notifyDataSetChanged();
    }
}
