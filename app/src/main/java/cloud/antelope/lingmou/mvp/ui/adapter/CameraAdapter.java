package cloud.antelope.lingmou.mvp.ui.adapter;

import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lingdanet.safeguard.common.utils.Utils;

import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.model.entity.CameraItem;

/**
 * Created by Administrator on 2017/11/13.
 */

public class CameraAdapter extends BaseQuickAdapter<CameraItem, BaseViewHolder> {

    private String mOrgName;
    private String mSelectStr;

    public CameraAdapter(@Nullable List<CameraItem> data, String orgName) {
        super(R.layout.collection_item, data);
        mOrgName = orgName;
    }

    public void setOrgName(String orgName) {
        mOrgName = orgName;
        notifyDataSetChanged();
    }

    @Override
    protected void convert(BaseViewHolder helper, CameraItem item) {
        if (TextUtils.isEmpty(item.getLatitude())
                || TextUtils.isEmpty(item.getLongitude())
                || item.getLatitude().equals("0.0")
                || item.getLongitude().equals("0.0")) {
            helper.setVisible(R.id.map_ib, false);
        } else {
            helper.setVisible(R.id.map_ib, true);
            helper.addOnClickListener(R.id.map_ib);
        }
        String deviceName = item.getName();
        TextView nameTv = helper.getView(R.id.collect_name_tv);
        if (!TextUtils.isEmpty(deviceName) && !TextUtils.isEmpty(mSelectStr) && deviceName.contains(mSelectStr)) {
            SpannableStringBuilder builder = new SpannableStringBuilder(deviceName);
            int from = deviceName.indexOf(mSelectStr);
            ForegroundColorSpan blueSpan = new ForegroundColorSpan(Utils.getContext().getResources().getColor(R.color.home_text_select));
            builder.setSpan(blueSpan, from, from + mSelectStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            nameTv.setText(builder);
        } else {
            nameTv.setText(deviceName);
        }

        helper.setText(R.id.collect_address_tv, mOrgName == null ? item.getOrgPath() : mOrgName);
        Boolean solos = item.getSolos();
        if (null != solos && solos) {
            //则是单兵设备
            if (item.getCamera_status() == Constants.CAMERA_ONLINE) {
                helper.setImageResource(R.id.collect_camera_iv, R.drawable.phone_offline);
            } else {
                helper.setImageResource(R.id.collect_camera_iv, R.drawable.camera_phone_offline);
            }
        } else {
            switch (item.getCamera_type()) {
                case Constants.CAMERA_QIANG_JI:
                    if (item.getCamera_status() == Constants.CAMERA_ONLINE) {
                        helper.setImageResource(R.id.collect_camera_iv, R.drawable.camera_gunlock);
                    } else {
                        helper.setImageResource(R.id.collect_camera_iv, R.drawable.camera_gunlock_offline);
                    }
                    break;
                case Constants.CAMERA_QIU_JI:
                    if (item.getCamera_status() == Constants.CAMERA_ONLINE) {
                        helper.setImageResource(R.id.collect_camera_iv, R.drawable.camera_ball);
                    } else {
                        helper.setImageResource(R.id.collect_camera_iv, R.drawable.camera_ball_offline);
                    }
                    break;
                default:
                    if (item.getCamera_status() == Constants.CAMERA_ONLINE) {
                        helper.setImageResource(R.id.collect_camera_iv, R.drawable.camera_gunlock);
                    } else {
                        helper.setImageResource(R.id.collect_camera_iv, R.drawable.camera_gunlock_offline);
                    }
                    break;
            }
        }
    }

    public void setSelectText(String selectText) {
        mSelectStr = selectText;
        notifyDataSetChanged();
    }
}
