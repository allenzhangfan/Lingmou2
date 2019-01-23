package cloud.antelope.lingmou.mvp.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.http.imageloader.glide.ImageConfigImpl;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.utils.Utils;

import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;
import cloud.antelope.lingmou.mvp.ui.activity.VideoPlayActivity;

public class SearchVideoAdapter extends BaseQuickAdapter<OrgCameraEntity, BaseViewHolder> {

    private final ImageLoader mImageLoader;
    private final Activity mActivity;
    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public SearchVideoAdapter(Activity activity, @Nullable List<OrgCameraEntity> data) {
        super(R.layout.item_search_video, data);
        mActivity = activity;
        mImageLoader = ArmsUtils.obtainAppComponentFromContext(activity).imageLoader();
    }
    @Override
    protected void convert(BaseViewHolder helper, OrgCameraEntity item) {
        String coverUrl = item.getCoverUrl();
        String deviceName = item.getDeviceName();
        if (!TextUtils.isEmpty(coverUrl)) {
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

        } else {
            helper.setImageResource(R.id.iv_pic, R.drawable.about_stroke_rect_gray);
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
        helper.setText(R.id.tv_name,deviceName);
        long deviceState = item.getDeviceStateReal();
        Long deviceType = item.getDeviceType();
//        int resId = R.drawable.box_camera_offline;
        TextView tvState = helper.getView(R.id.tv_state);
        if (null != deviceType) {
            if (deviceType == Constants.ORG_CAMERA_ZHUAPAIJI) {
                //抓拍机
                tvState.setText("抓拍机");
            } else if (deviceType == Constants.ORG_CAMERA_QIUJI) {
                //球机
                tvState.setText("球机");
            } else if (deviceType == Constants.ORG_CAMERA_PHONE) {
                //手机
                tvState.setText("单兵");
            } else {
                //普通摄像机
                tvState.setText("枪机");
            }
            helper.setBackgroundRes(R.id.view_state, deviceState == 1 ? R.drawable.shape_oval_green : R.drawable.shape_oval_red);
        }
        helper.setVisible(R.id.offline_rl,deviceState != 1);
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

       /* helper.setOnClickListener(R.id.ll_root, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, VideoPlayActivity.class);
                intent.putExtra("cameraId", item.getManufacturerDeviceId());
                intent.putExtra("cameraName", item.getDeviceName());
                intent.putExtra("cameraSn", item.getSn());
                intent.putExtra("cover", item.getCoverUrl());
                intent.putExtra("entity", item);
                mActivity.startActivity(intent);
            }
        });*/
    }

    public interface OnClickListener {
        void onClickMenu(OrgCameraEntity item);

        void onClickItem(OrgCameraEntity item);
    }
}
