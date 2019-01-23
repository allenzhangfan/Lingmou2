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
import cloud.antelope.lingmou.mvp.model.entity.FaceDepotDetailEntity;

/**
 * Created by ChenXinming on 2017/12/28.
 * description:
 */

public class FaceDepotAdapter extends BaseQuickAdapter<FaceDepotDetailEntity, BaseViewHolder> {

    private Activity mActivity;
    private String mSelectStr;
    private int mItemWidth;
    private ImageLoader mImageLoader;

    public FaceDepotAdapter(Activity activity, @Nullable List<FaceDepotDetailEntity> data) {
        super(R.layout.item_face_depot, data);
        mActivity = activity;
        mItemWidth = (SizeUtils.getScreenWidth() - SizeUtils.dp2px(23)) / 2;
        mImageLoader = ArmsUtils.obtainAppComponentFromContext(mActivity).imageLoader();
    }

    @Override
    protected void convert(BaseViewHolder helper, FaceDepotDetailEntity item) {
        CardView cardView = helper.getView(R.id.item_card_view);
        ViewGroup.LayoutParams cardViewLayoutParams = cardView.getLayoutParams();
        cardViewLayoutParams.height = mItemWidth;
        cardView.setLayoutParams(cardViewLayoutParams);
        ImageView faceIv = helper.getView(R.id.face_iv);
        if (!TextUtils.isEmpty(item.facePath)) {
            mImageLoader.loadImage(mActivity, ImageConfigImpl
                    .builder()
                    .url(item.facePath)
                    .placeholder(R.drawable.about_stroke_rect_gray)
                    .errorPic(R.drawable.about_stroke_rect_gray)
                    .cacheStrategy(0)
                    .imageView(faceIv)
                    .build());
        }
        TextView nameTv = helper.getView(R.id.name_tv);
        String deviceName = item.cameraName;
        if (!TextUtils.isEmpty(deviceName) && !TextUtils.isEmpty(mSelectStr)) {
            char[] chars = mSelectStr.toCharArray();
            List<Integer> fromEnd = new ArrayList<>();
            int size = chars.length;
            for (int i = 0; i < size; i++) {
                String str = String.valueOf(chars[i]);
                if (deviceName.contains(str)) {
                    int from = deviceName.indexOf(str);
                    fromEnd.add(from);
                }
            }
            SpannableStringBuilder builder = new SpannableStringBuilder(deviceName);
//            ForegroundColorSpan blueSpan = new ForegroundColorSpan(mActivity.getResources().getColor(R.color.home_text_select));
            if (fromEnd.size() > 0) {
                for(int j = 0; j<fromEnd.size(); j++) {
                    builder.setSpan(new ForegroundColorSpan(mActivity.getResources().getColor(R.color.yellow_ffb300)), fromEnd.get(j), fromEnd.get(j)+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                nameTv.setText(builder);
            }
        } else {
            nameTv.setText(item.cameraName);
        }
        String timeDate = TimeUtils.millis2String(Long.parseLong(item.captureTime));
        helper.setText(R.id.date_time_tv, timeDate);
    }


    public void setSelectText(String selectText) {
        mSelectStr = selectText;
        notifyDataSetChanged();
    }
}
