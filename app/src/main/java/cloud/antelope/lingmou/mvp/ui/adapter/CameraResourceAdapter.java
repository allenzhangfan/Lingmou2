package cloud.antelope.lingmou.mvp.ui.adapter;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

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
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;
import cloud.antelope.lingmou.mvp.ui.widget.swipemenulib.SwipeMenuLayout;

/**
 * Created by ChenXinming on 2017/12/28.
 * description:
 */

public class CameraResourceAdapter extends BaseQuickAdapter<OrgCameraEntity, BaseViewHolder> {

    private Activity mActivity;
    private OnClickListener onClickListener;
    private boolean allClose = true;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public CameraResourceAdapter(Activity activity, @Nullable List<OrgCameraEntity> data) {
        super(R.layout.item_camera_resource, data);
        mActivity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, OrgCameraEntity item) {
        ImageView ivPic = helper.getView(R.id.iv_pic);
        if (!mActivity.isDestroyed()) {
            if (!TextUtils.isEmpty(item.getCoverUrl())) {
                ArmsUtils.obtainAppComponentFromContext(mContext).imageLoader().loadImage(mContext, ImageConfigImpl
                        .builder()
                        .url(item.getCoverUrl())
                        .cacheStrategy(0)
                        // .isCropCenter(true)
                        .placeholder(R.drawable.about_stroke_rect_gray)
                        .errorPic(R.drawable.about_stroke_rect_gray)
                        .imageView(ivPic).build());

            } else {
                helper.setImageResource(R.id.iv_pic, R.drawable.about_stroke_rect_gray);
            }

        }
        if (!TextUtils.isEmpty(item.getState())) {
            helper.setText(R.id.tv_state, "4".equals(item.getState()) ? "在线" : "离线");
            helper.setVisible(R.id.tv_state, true);
        } else {
            helper.setVisible(R.id.tv_state, false);
        }
        helper.setVisible(R.id.view_divider, helper.getAdapterPosition() != getData().size() - 1);
        helper.setText(R.id.tv_name, item.getDeviceName());
//        helper.setText(R.id.tv_focus, item.isSelect() ? mActivity.getString(R.string.cancel) : mActivity.getString(R.string.favorite_text));
//        helper.setTextColor(R.id.tv_focus, item.isSelect() ?mActivity.getResources().getColor(R.color.gray_888):mActivity.getResources().getColor(R.color.yellow_ff8f00));
        ImageView ivFocus = helper.getView(R.id.iv_favorite);
        ivFocus.setBackgroundResource(item.isSelect() ? R.drawable.following : R.drawable.following_grey);
        helper.getView(R.id.rl_root).setOnClickListener(v -> {
            if (onClickListener != null) {
                onClickListener.onItemClick(item);
            }
        });
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
        } else {
            helper.setText(R.id.tv_address, mContext.getString(R.string.unknown_location));
        }
        SwipeMenuLayout sml = helper.getView(R.id.sml);
        helper.getView(R.id.iv_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sml.smoothExpand();
                allClose=false;
            }
        });
        sml.setOnStateChangedListener(new SwipeMenuLayout.OnStateChangedListener() {
            @Override
            public void onnStateChanged(boolean isExpand) {
                ImageView ivMenu = helper.getView(R.id.iv_menu);
                ivMenu.setImageResource(isExpand ? R.drawable.menu_yellow : R.drawable.menu);
            }
        });
        helper.getView(R.id.rl_favorite).setOnClickListener(v -> {
            if (onClickListener != null) {
                onClickListener.onCollectionClick(item);
            }
        });
        helper.getView(R.id.rl_setting).setOnClickListener(v -> {
            if (onClickListener != null) {
                onClickListener.onSettingClick(item);
            }
        });
        if (allClose) {
            sml.quickClose();
        }
    }

    public interface OnClickListener {
        void onItemClick(OrgCameraEntity item);

        void onCollectionClick(OrgCameraEntity item);

        void onSettingClick(OrgCameraEntity item);
    }

    public void close() {
        allClose = true;
        notifyDataSetChanged();
    }
}
