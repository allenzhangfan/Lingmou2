package cloud.antelope.lingmou.mvp.ui.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jess.arms.http.imageloader.glide.GlideArms;
import com.lingdanet.safeguard.common.utils.SizeUtils;
import com.lingdanet.safeguard.common.utils.TimeUtils;

import cloud.antelope.lingmou.app.utils.ConfidenceUtil;
import cloud.antelope.lingmou.mvp.model.entity.FaceNewEntity;

import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.ui.widget.RectImageView;

/**
 * Created by ChenXinming on 2017/11/21.
 * description:
 */

public class FaceRecognizeAdapter extends BaseQuickAdapter<FaceNewEntity, BaseViewHolder> {

    private Context mContext;
    private boolean mIsTrackStatus;
    private String mFeature;
    private int mItemHeight;

    public FaceRecognizeAdapter(Context context, @Nullable List<FaceNewEntity> data) {
        super(R.layout.face_item, data);
        mContext = context;
        int screenWidth = SizeUtils.getScreenWidth();
        mItemHeight = (screenWidth - SizeUtils.dp2px(20 + 36)) / 3;
    }

    @Override
    protected void convert(BaseViewHolder helper, FaceNewEntity item) {
        RelativeLayout imgParentRl = helper.getView(R.id.img_parent_rl);
        ViewGroup.LayoutParams layoutParams = imgParentRl.getLayoutParams();
        layoutParams.height = mItemHeight;
        imgParentRl.setLayoutParams(layoutParams);
        RectImageView civ = helper.getView(R.id.face_civ);
        ImageView itemCheckImg = helper.getView(R.id.item_select_img);
        View view = helper.getView(R.id.no_lat_lng_view);
        if (!TextUtils.isEmpty(item.facePath)) {
            GlideArms.with(mContext).load(item.facePath).centerCrop().placeholder(R.drawable.about_stroke_rect_gray).into(civ);
        } else {
            GlideArms.with(mContext).load(item.bodyPath).placeholder(R.drawable.about_stroke_rect_gray).into(civ);
        }
        if (!mIsTrackStatus) {
            civ.setIsSelect(item.isSelect);
            view.setVisibility(View.GONE);
            itemCheckImg.setVisibility(View.GONE);
        } else {
            civ.setIsSelect(false);
            if (TextUtils.isEmpty(item.latitide) || TextUtils.isEmpty(item.longitude)) {
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.GONE);
            }
            boolean checked = item.mIsChecked;
            if (checked) {
                itemCheckImg.setVisibility(View.VISIBLE);
                itemCheckImg.setSelected(true);
            } else {
                itemCheckImg.setVisibility(View.VISIBLE);
                itemCheckImg.setSelected(false);
            }
        }
        helper.setText(R.id.score_tv,(int) item.score+"%");
        helper.setText(R.id.face_name_tv, item.cameraName);
        String timeDate = TimeUtils.millis2String(Long.parseLong(item.captureTime), "MM-dd HH:mm:ss");
        helper.setText(R.id.face_time_tv, timeDate);

    }

    public void setTrackStatus(boolean isTrackStatus){
        mIsTrackStatus = isTrackStatus;
    }

    public void setFaceFeature(String feature) {
        mFeature = feature;
    }
}
