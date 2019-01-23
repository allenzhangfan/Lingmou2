package cloud.antelope.lingmou.mvp.ui.adapter;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.http.imageloader.glide.GlideArms;
import com.jess.arms.http.imageloader.glide.ImageConfigImpl;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.utils.SizeUtils;
import com.lingdanet.safeguard.common.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.common.UrlConstant;
import cloud.antelope.lingmou.mvp.model.entity.BodyDepotDetailEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceDepotDetailEntity;

/**
 * Created by ChenXinming on 2017/12/28.
 * description:
 */

public class BodyDepotAdapter extends BaseQuickAdapter<BodyDepotDetailEntity, BaseViewHolder> {

    private Activity mActivity;

    private int mItemWidth;

    private ImageLoader mImageLoader;

    public BodyDepotAdapter(Activity activity, @Nullable List<BodyDepotDetailEntity> data) {
        super(R.layout.item_body_depot, data);
        mActivity = activity;
        mItemWidth = (SizeUtils.getScreenWidth() - SizeUtils.dp2px(23)) / 2;
        mImageLoader = ArmsUtils.obtainAppComponentFromContext(mActivity).imageLoader();
    }

    @Override
    protected void convert(BaseViewHolder helper, BodyDepotDetailEntity item) {
//        RelativeLayout cardView = helper.getView(R.id.body_card_view);
//        ViewGroup.LayoutParams layoutParams = cardView.getLayoutParams();
//        layoutParams.height = mItemWidth;
//        cardView.setLayoutParams(layoutParams);
        ImageView faceIv = helper.getView(R.id.face_iv);
        if (!TextUtils.isEmpty(item.bodyPath)) {
            GlideArms.with(mActivity).asBitmap().load(item.bodyPath)
                    .placeholder(R.drawable.about_stroke_rect_gray)
                    .diskCacheStrategy(DiskCacheStrategy.NONE).into(faceIv);
        }
        TextView nameTv = helper.getView(R.id.name_tv);
        String deviceName = item.cameraName;
        nameTv.setText(deviceName);
        String timeDate = TimeUtils.millis2String(Long.parseLong(item.captureTime));
        helper.setText(R.id.date_time_tv, timeDate);
    }

}
