package cloud.antelope.lingmou.mvp.ui.adapter;

import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jess.arms.http.imageloader.glide.ImageConfigImpl;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.utils.Utils;

import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;

/**
 * Created by ChenXinming on 2018/1/22.
 * description:
 */

public class DeviceCameraAdapter extends BaseQuickAdapter<OrgCameraEntity, BaseViewHolder> {
    private String mSelectStr;
    private String mOrgPath;
    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public DeviceCameraAdapter(@Nullable List data) {
        super(R.layout.item_device_camera, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, OrgCameraEntity item) {
        String deviceName = item.getDeviceName();
        TextView nameTv = helper.getView(R.id.tv_name);
//        if (!TextUtils.isEmpty(deviceName) && !TextUtils.isEmpty(mSelectStr) && deviceName.contains(mSelectStr)) {
//            SpannableStringBuilder builder = new SpannableStringBuilder(deviceName);
//            int from = deviceName.indexOf(mSelectStr);
//            ForegroundColorSpan blueSpan = new ForegroundColorSpan(Utils.getContext().getResources().getColor(R.color.home_text_select));
//            builder.setSpan(blueSpan, from, from + mSelectStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            nameTv.setText(builder);
//        } else {
        nameTv.setText(deviceName);
//        }
        long deviceState = item.getDeviceState();
        Long deviceType = item.getDeviceType();
//        int resId = R.drawable.box_camera_offline;
        TextView tvState = helper.getView(R.id.tv_state);
        if (null != deviceType) {
            if (deviceType == Constants.ORG_CAMERA_ZHUAPAIJI) {
                //抓拍机
                tvState.setText("抓拍机");
//                resId = deviceState == 1 ? R.drawable.capture_camera_online : R.drawable.capture_camera_offline;
            } else if (deviceType == Constants.ORG_CAMERA_QIUJI) {
                //球机
                tvState.setText("球机");
//                resId = deviceState == 1 ? R.drawable.dome_camera_online : R.drawable.dome_camera_offline;
            } else if (deviceType == Constants.ORG_CAMERA_PHONE) {
                //手机
                tvState.setText("单兵");
//                resId = deviceState == 1 ? R.drawable.mobile_camera_online : R.drawable.mobile_camera_offline;
            } else {
                //普通摄像机
                tvState.setText("枪机");
//                resId = deviceState == 1 ? R.drawable.box_camera_online : R.drawable.box_camera_offline;
            }
            helper.setBackgroundRes(R.id.view_state, deviceState == 1 ? R.drawable.shape_oval_green : R.drawable.shape_oval_red);
        }
        helper.setVisible(R.id.offline_rl,deviceState != 1);
        if (item.getCoverUrl() != null) {
            ArmsUtils
                    .obtainAppComponentFromContext(mContext)
                    .imageLoader()
                    .loadImage(mContext, ImageConfigImpl
                            .builder()
                            .url(item.getCoverUrl() + "&width=256.0&height=144.0")
                            .cacheStrategy(0)
                            .placeholder(R.drawable.about_stroke_rect_gray)
                            .errorPic(R.drawable.about_stroke_rect_gray)
                            .imageView(helper.getView(R.id.iv_pic)).build());
        }
        if (item.getLatitude() != null && item.getLongitude() != null) {
            helper.setText(R.id.tv_address, "");
            LatLonPoint point = new LatLonPoint(item.getLatitude(), item.getLongitude());
            RegeocodeQuery query = new RegeocodeQuery(point, 500, GeocodeSearch.AMAP);
            GeocodeSearch geocodeSearch = new GeocodeSearch(Utils.getContext());
            geocodeSearch.getFromLocationAsyn(query);
            geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
                @Override
                public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                    if (i == 1000) {
                        RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
                        String formatAddress = regeocodeAddress.getFormatAddress();
                        helper.setText(R.id.tv_address, formatAddress);
                    }
                }

                @Override
                public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

                }
            });
        }else {
            helper.setText(R.id.tv_address, mContext.getString(R.string.unknown_location));
        }
        helper.getView(R.id.ll_root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null) {
                    onClickListener.onClickItem(item);
                }
            }
        });
        helper.getView(R.id.iv_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null) {
                    onClickListener.onClickMenu(item);
                }
            }
        });

    }

    public void setOrgPath(String orgPath) {
        this.mOrgPath = orgPath;
    }

    public void setSelectText(String selectText) {
        mSelectStr = selectText;
        notifyDataSetChanged();
    }

    public interface OnClickListener {
        void onClickMenu(OrgCameraEntity item);

        void onClickItem(OrgCameraEntity item);
    }
}
