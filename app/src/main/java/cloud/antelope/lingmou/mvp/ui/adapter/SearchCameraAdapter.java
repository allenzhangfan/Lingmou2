package cloud.antelope.lingmou.mvp.ui.adapter;

import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lingdanet.safeguard.common.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.model.entity.CameraItem;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;

/**
 * Created by ChenXinming on 2018/1/22.
 * description:
 */

public class SearchCameraAdapter extends BaseQuickAdapter<OrgCameraEntity, BaseViewHolder> {

    private List<CameraItem> mCheckedCameras;
    private String mSelectStr;

    public SearchCameraAdapter(@Nullable List<OrgCameraEntity> data) {
        super(R.layout.item_search_camera, data);
        mCheckedCameras = new ArrayList<>();
    }

    @Override
    protected void convert(BaseViewHolder helper, OrgCameraEntity item) {
        TextView textView = helper.getView(R.id.camera_name_tv);
        ImageView selectImageView = helper.getView(R.id.camera_cb);
        String deviceName = item.getDeviceName();
        if (!TextUtils.isEmpty(deviceName) && !TextUtils.isEmpty(mSelectStr) && deviceName.contains(mSelectStr)) {
            SpannableStringBuilder builder = new SpannableStringBuilder(deviceName);
            int from = deviceName.indexOf(mSelectStr);
            ForegroundColorSpan blueSpan = new ForegroundColorSpan(Utils.getContext().getResources().getColor(R.color.yellow_ff8f00));
            builder.setSpan(blueSpan, from, from + mSelectStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(builder);
        } else {
            textView.setText(deviceName);
        }
        if (item.isSelect()) {
            selectImageView.setImageResource(R.drawable.choosed_selected);
        } else {
            selectImageView.setImageResource(R.drawable.search_unchecked);
        }
    }

    public void setSelectCameras(List<CameraItem> cameras) {
        mCheckedCameras.clear();
        mCheckedCameras.addAll(cameras);
        notifyDataSetChanged();
    }


    public void setSelectText(String selectText) {
        mSelectStr = selectText;
        notifyDataSetChanged();
    }
}
