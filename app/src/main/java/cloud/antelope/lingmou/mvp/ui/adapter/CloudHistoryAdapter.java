package cloud.antelope.lingmou.mvp.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.http.imageloader.glide.ImageConfigImpl;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.utils.SizeUtils;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.PublicUtils;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;

/**
 * Created by Administrator on 2017/11/13.
 */

public class CloudHistoryAdapter extends BaseQuickAdapter<OrgCameraEntity, BaseViewHolder> {

    private AppComponent mAppComponent;
    private ImageLoader mImageLoader;
    private Context mContext;

    public CloudHistoryAdapter(Context context) {
        super(R.layout.item_cloud_history, null);
        mAppComponent = ArmsUtils.obtainAppComponentFromContext(context);
        mImageLoader = mAppComponent.imageLoader();
        mContext = context;
//        mItemHeight = (SizeUtils.getScreenWidth() - SizeUtils.dp2px(16)) * 9 / 16;
//        mItemNameTvHeight = mItemHeight * 48 / 193;
//        mItemTimeTvHeight = mItemHeight * 40 / 193;
    }

    @Override
    protected void convert(BaseViewHolder helper, OrgCameraEntity item) {
        helper.setVisible(R.id.view_divider, helper.getAdapterPosition() != getData().size() - 1);
        String coverUrl = item.getCoverUrl();
        String deviceName = item.getDeviceName();
        long viewTimes = item.getViewTimes();
        if (!TextUtils.isEmpty(coverUrl)) {
            mImageLoader.loadImage(mContext, ImageConfigImpl
                    .builder()
                    .url(coverUrl)
                    .cacheStrategy(0)
                    // .isCropCenter(true)
                    .placeholder(R.drawable.about_stroke_rect_gray)
                    .errorPic(R.drawable.about_stroke_rect_gray)
                    .imageView((ImageView) helper.getView(R.id.iv_pic)).build());

        } else {
            helper.setImageResource(R.id.iv_pic, R.drawable.about_stroke_rect_gray);
        }
        helper.setText(R.id.tv_name, deviceName);
        long timeStamps = System.currentTimeMillis() - item.getTimeStamps();
        helper.setText(R.id.tv_state, PublicUtils.caculateTimes(timeStamps));











       /* LinearLayout view = helper.getView(R.id.ll_root);
        RecyclerView.LayoutParams rvLp = (RecyclerView.LayoutParams) view.getLayoutParams();
        if (helper.getPosition() == 0) {
            rvLp.setMargins(0,0,0,0);
        } else {
            rvLp.setMargins(0,mContext.getResources().getDimensionPixelSize(R.dimen.dp16),0,0);
        }
        view.setLayoutParams(rvLp);
        RelativeLayout itemRl = helper.getView(R.id.history_item_rl);
        ViewGroup.LayoutParams layoutParams = itemRl.getLayoutParams();
        layoutParams.height = mItemHeight;
        itemRl.setLayoutParams(layoutParams);
        View vb = helper.getView(R.id.view_b);
        ViewGroup.LayoutParams nameTVLayoutParams = vb.getLayoutParams();
        nameTVLayoutParams.height = mItemNameTvHeight;
        vb.setLayoutParams(nameTVLayoutParams);
        View vt = helper.getView(R.id.view_t);
        ViewGroup.LayoutParams timeTVLayoutParams = vt.getLayoutParams();
        timeTVLayoutParams.height = mItemTimeTvHeight;
        vt.setLayoutParams(timeTVLayoutParams);
        String coverUrl = item.getCoverUrl();
        String deviceName = item.getDeviceName();
        long timeStamps = System.currentTimeMillis() - item.getTimeStamps();
        if (!TextUtils.isEmpty(coverUrl)) {
            *//*GlideArms.with(mContext)
                    .load(coverUrl)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder_item_big)
                    .error(R.drawable.placeholder_item_big)
                    .into((ImageView) helper.getView(R.id.capture_iv));*//*
            mImageLoader.loadImage(mContext, ImageConfigImpl
                    .builder()
                    .url(coverUrl)
                    .cacheStrategy(1)
                    // .isCropCenter(true)
                    .placeholder(R.drawable.about_stroke_rect_gray)
                    .errorPic(R.drawable.about_stroke_rect_gray)
                    .imageView((ImageView) helper.getView(R.id.capture_iv)).build());

        } else {
            helper.setImageResource(R.id.capture_iv, R.drawable.about_stroke_rect_gray);
        }
        helper.setText(R.id.name_tv, deviceName);
        helper.setText(R.id.time_tv, PublicUtils.caculateTimes(timeStamps));*/
    }
}
