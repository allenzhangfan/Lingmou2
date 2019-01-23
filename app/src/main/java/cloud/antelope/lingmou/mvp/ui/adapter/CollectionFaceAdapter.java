package cloud.antelope.lingmou.mvp.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.http.imageloader.glide.ImageConfigImpl;
import com.jess.arms.utils.ArmsUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.FormatUtils;
import cloud.antelope.lingmou.mvp.model.entity.CollectionListBean;
import cloud.antelope.lingmou.mvp.model.entity.DetailCommonEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceDepotDetailEntity;
import cloud.antelope.lingmou.mvp.ui.activity.PictureDetailActivity;

import static cloud.antelope.lingmou.mvp.ui.fragment.CollectionListFragment.FACE;

public class CollectionFaceAdapter extends BaseQuickAdapter<CollectionListBean, BaseViewHolder> {

    private final ImageLoader mImageLoader;
    private final Activity mActivity;
    private Calendar calendar;

    public CollectionFaceAdapter(Activity activity, @Nullable List<CollectionListBean> data) {
        super(R.layout.item_search_face, data);
        mActivity = activity;
        mImageLoader = ArmsUtils.obtainAppComponentFromContext(activity).imageLoader();
        calendar = Calendar.getInstance();
    }

    @Override
    protected void convert(BaseViewHolder helper, CollectionListBean item) {
        mImageLoader.loadImage(mActivity, ImageConfigImpl
                .builder()
                .url(getUrl(item))
                .placeholder(R.drawable.about_stroke_rect_gray)
                .errorPic(R.drawable.about_stroke_rect_gray)
                .cacheStrategy(0)
                .imageView(helper.getView(R.id.iv_pic))
                .build());
        helper.setText(R.id.tv_name, item.getDeviceName());
        calendar.setTimeInMillis(Long.valueOf(item.getCreateTime()));
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        helper.setText(R.id.tv_time, year + "-" + FormatUtils.twoNumber(month) + "-" + FormatUtils.twoNumber(day) + " " + FormatUtils.twoNumber(hour) + ":" + FormatUtils.twoNumber(minute) + ":" + FormatUtils.twoNumber(second));
    }

    private String getUrl(CollectionListBean item) {
        switch (item.getFavoritesType()) {
            case FACE:
                return item.getFaceImgUrl();
            default:
                return item.getImageUrl();
        }
    }
}
