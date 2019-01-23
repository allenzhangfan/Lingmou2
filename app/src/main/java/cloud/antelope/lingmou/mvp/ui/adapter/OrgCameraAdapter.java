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

import org.litepal.crud.DataSupport;

import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;

/**
 * Created by Administrator on 2017/11/13.
 */

public class OrgCameraAdapter extends BaseQuickAdapter<OrgCameraEntity, BaseViewHolder> {

    private String mSelectStr;
    private String mOrgPath;

    public OrgCameraAdapter(@Nullable List<OrgCameraEntity> data) {
        super(R.layout.collection_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, OrgCameraEntity item) {
        /*if (item.getLongitude() != null
                && item.getLatitude() != null
                && item.getLatitude() != 0.0
                && item.getLongitude() != 0.0) {
            helper.setVisible(R.id.map_ib, true);
            helper.addOnClickListener(R.id.map_ib);
        } else {
            helper.setVisible(R.id.map_ib, false);
        }*/
        String deviceName = item.getDeviceName();
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

        helper.setText(R.id.collect_address_tv, mOrgPath);
        // Boolean solos = item.getSolos();
        // if (null != solos && solos) {
        //     //则是单兵设备
        //     if (item.getCamera_status() == Constants.CAMERA_ONLINE) {
        //         helper.setImageResource(R.id.collect_camera_iv, R.drawable.camera_phone_online);
        //     } else {
        //         helper.setImageResource(R.id.collect_camera_iv, R.drawable.camera_phone_offline);
        //     }
        // } else {
        int deviceState = item.getDeviceState();
        Long deviceType = item.getDeviceType();
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

    public void setOrgPath(String orgPath) {
        this.mOrgPath = orgPath;
    }

    public void setSelectText(String selectText) {
        mSelectStr = selectText;
        notifyDataSetChanged();
    }
}
