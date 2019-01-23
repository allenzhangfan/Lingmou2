package cloud.antelope.lingmou.mvp.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.http.imageloader.glide.ImageConfigImpl;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.utils.SizeUtils;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;
import timber.log.Timber;

/**
 * Created by Administrator on 2017/11/13.
 */

public class CollectCameraNewAdapter extends BaseQuickAdapter<OrgCameraEntity, BaseViewHolder> {

    private AppComponent mAppComponent;
    private ImageLoader mImageLoader;
    private Context mContext;
    //    private int mItemHeight;
//    private int mItemCountTvHeight;
    private boolean mIsAllVideoFrom;

    public CollectCameraNewAdapter(Context context) {
        super(R.layout.item_collection_new, null);
        mAppComponent = ArmsUtils.obtainAppComponentFromContext(context);
        mImageLoader = mAppComponent.imageLoader();
        mContext = context;
//        int screenWidthHalf = (SizeUtils.getScreenWidth() - SizeUtils.dp2px(32)) / 2;
//        mItemHeight = screenWidthHalf * 9 / 16;
//        mItemCountTvHeight = mItemHeight * 40 / 96;
    }

    public void setIsFromAllVideo(boolean isAllVideoFrom) {
        mIsAllVideoFrom = isAllVideoFrom;
    }

    @Override
    protected void convert(BaseViewHolder helper, OrgCameraEntity item) {
//        LinearLayout view = helper.getView(R.id.ll_root);
//        RecyclerView.LayoutParams rvLp = (RecyclerView.LayoutParams) view.getLayoutParams();
//        if (helper.getPosition() == 0 || helper.getPosition() == 1) {
//            rvLp.setMargins(mContext.getResources().getDimensionPixelSize(R.dimen.dp8),
//                    0,
//                    mContext.getResources().getDimensionPixelSize(R.dimen.dp8), 0);
//        } else {
//            rvLp.setMargins(mContext.getResources().getDimensionPixelSize(R.dimen.dp8),
//                    mContext.getResources().getDimensionPixelSize(R.dimen.dp16),
//                    mContext.getResources().getDimensionPixelSize(R.dimen.dp8),
//                    0);
//        }
//        view.setLayoutParams(rvLp);
//        RelativeLayout itemRl = helper.getView(R.id.item_rl);
//        TextView itemCountTv = helper.getView(R.id.count_tv);
//        ViewGroup.LayoutParams layoutParams = itemRl.getLayoutParams();
//        layoutParams.height = mItemHeight;
//        itemRl.setLayoutParams(layoutParams);
//        ViewGroup.LayoutParams tvLayoutParams = itemCountTv.getLayoutParams();
//        tvLayoutParams.height = mItemCountTvHeight;
//        itemCountTv.setLayoutParams(tvLayoutParams);
        helper.setVisible(R.id.view_divider, helper.getAdapterPosition() != getData().size() - 1);
        String coverUrl = item.getCoverUrl();
        String deviceName = item.getDeviceName();
        long viewTimes = item.getViewTimes();
        if (!TextUtils.isEmpty(coverUrl)) {
            /*GlideArms.with(mContext)
                    .load(coverUrl)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder_item_small)
                    .error(R.drawable.placeholder_item_small)
                    .into((ImageView) helper.getView(R.id.cover_iv));*/
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
        if (mIsAllVideoFrom) {
            String state = item.getDeviceData() == 1 ? "在线" : "离线";
            helper.setText(R.id.tv_state, state);
            if (item.getDeviceData() != 1) {
                helper.setVisible(R.id.offline_rl, true);
            } else {
                helper.setVisible(R.id.offline_rl, false);
            }
        }else {
            if(!TextUtils.isEmpty(item.getState())){
                helper.setText(R.id.tv_state,"4".equals(item.getState())?"在线":"离线");
                helper.setVisible(R.id.offline_rl,!"4".equals(item.getState()));
                helper.setVisible(R.id.tv_state,true);
            }else {
                helper.setVisible(R.id.tv_state,false);
            }
        }
    }
}
