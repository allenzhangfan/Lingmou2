package cloud.antelope.lingmou.mvp.ui.adapter;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.http.imageloader.glide.ImageConfigImpl;
import com.jess.arms.utils.ArmsUtils;

import java.util.Calendar;
import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.FormatUtils;
import cloud.antelope.lingmou.mvp.model.entity.CollectionListBean;
import cloud.antelope.lingmou.mvp.model.entity.TrackingPersonListBean;

import static cloud.antelope.lingmou.mvp.ui.fragment.CollectionListFragment.FACE;

public class TrackingPersonAdapter extends BaseQuickAdapter<TrackingPersonListBean, BaseViewHolder> {

    private final ImageLoader mImageLoader;
    private final Activity mActivity;
    private Calendar calendar;

    public TrackingPersonAdapter(Activity activity, @Nullable List<TrackingPersonListBean> data) {
        super(R.layout.item_tracking_person, data);
        mActivity = activity;
        mImageLoader = ArmsUtils.obtainAppComponentFromContext(activity).imageLoader();
        calendar = Calendar.getInstance();
    }

    @Override
    protected void convert(BaseViewHolder helper, TrackingPersonListBean item) {
        mImageLoader.loadImage(mContext, ImageConfigImpl
                .builder()
                .url(item.getImageurl())
                .cacheStrategy(0)
                .placeholder(R.drawable.placeholder_face_compare)
                .errorPic(R.drawable.placeholder_face_compare)
                .imageView((ImageView) (helper.getView(R.id.iv_pic))).build());
//        calendar.setTimeInMillis(Long.valueOf(item.getCreateTime()));
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH) + 1;
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
//        int hour = calendar.get(Calendar.HOUR_OF_DAY);
//        int minute = calendar.get(Calendar.MINUTE);
//        int second = calendar.get(Calendar.SECOND);
        String createTime = item.getCreateTime();
        String substring = createTime.substring(0, createTime.lastIndexOf("."));
        helper.setText(R.id.tv_time,substring);
    }
}
