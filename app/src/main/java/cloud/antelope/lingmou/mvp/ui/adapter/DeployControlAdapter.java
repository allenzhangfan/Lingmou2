package cloud.antelope.lingmou.mvp.ui.adapter;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
import cloud.antelope.lingmou.mvp.model.entity.DeployResponse;


/**
 * Created by ChenXinming on 2017/12/28.
 * description:
 */

public class DeployControlAdapter extends BaseQuickAdapter<DeployResponse.ListBean, BaseViewHolder> {

    private ImageLoader mImageLoader;
    private Activity mActivity;
    private OnClickListener onItemClickListener;
    private Calendar calendar;

    public void setOnClickListener(OnClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public DeployControlAdapter(Activity activity, @Nullable List<DeployResponse.ListBean> data) {
        super(R.layout.item_deploy_control, data);
        mActivity = activity;
        mImageLoader = ArmsUtils.obtainAppComponentFromContext(activity).imageLoader();
        calendar = Calendar.getInstance();
    }

    @Override
    protected void convert(BaseViewHolder helper, DeployResponse.ListBean item) {
        helper.setText(R.id.tv_name, item.getName());
        helper.setText(R.id.tv_statue, "进行中");
        switch (item.getTaskStatus()) {
            case 0:
                helper.setText(R.id.tv_pause, mActivity.getString(R.string.start_mission));
                helper.setText(R.id.tv_statue, mActivity.getString(R.string.paused));
                helper.setTextColor(R.id.tv_statue, mActivity.getResources().getColor(R.color.blue_gray_78909c));
                break;
            case 1:
                helper.setText(R.id.tv_pause, mActivity.getString(R.string.pause_mission));
                helper.setText(R.id.tv_statue, mActivity.getString(R.string.underway));
                helper.setTextColor(R.id.tv_statue, mActivity.getResources().getColor(R.color.yellow_ffc107));
                break;
            case 2:
                helper.setText(R.id.tv_pause, mActivity.getString(R.string.rebuild_mission));
                helper.setText(R.id.tv_statue, mActivity.getString(R.string.not_start));
                helper.setTextColor(R.id.tv_statue, mActivity.getResources().getColor(R.color.purple_ab47bc));
                break;
            case 3:
                helper.setText(R.id.tv_pause, mActivity.getString(R.string.rebuild_mission));
                helper.setText(R.id.tv_statue, mActivity.getString(R.string.expired));
                helper.setTextColor(R.id.tv_statue, mActivity.getResources().getColor(R.color.red_ec407a));
                break;
            default:
                helper.setText(R.id.tv_statue, "");
                break;
        }
        mImageLoader.loadImage(mContext, ImageConfigImpl
                .builder()
                .url(item.getImageUrl())
                .cacheStrategy(0)
                .placeholder(R.drawable.about_stroke_rect_gray)
                .errorPic(R.drawable.about_stroke_rect_gray)
                .imageView((ImageView) (helper.getView(R.id.iv_pic))).build());

        calendar.setTimeInMillis(Long.valueOf(item.getStartTime()));
        int startYear = calendar.get(Calendar.YEAR);
        int startMonth = calendar.get(Calendar.MONTH)+1;
        int startDay = calendar.get(Calendar.DAY_OF_MONTH);
        int startHour = calendar.get(Calendar.HOUR_OF_DAY);
        int startMinute = calendar.get(Calendar.MINUTE);
        helper.setText(R.id.tv_start_time, startYear + "-" + FormatUtils.twoNumber(startMonth) + "-" + FormatUtils.twoNumber(startDay) + " " + FormatUtils.twoNumber(startHour) + ":" + FormatUtils.twoNumber(startMinute));
        calendar.setTimeInMillis(Long.valueOf(item.getEndTime()));
        int endYear = calendar.get(Calendar.YEAR);
        int endMonth = calendar.get(Calendar.MONTH)+1;
        int endDay = calendar.get(Calendar.DAY_OF_MONTH);
        int endHour = calendar.get(Calendar.HOUR_OF_DAY);
        int endMinute = calendar.get(Calendar.MINUTE);
        helper.setText(R.id.tv_end_time, endYear + "-" + FormatUtils.twoNumber(endMonth) + "-" + FormatUtils.twoNumber(endDay) + " " + FormatUtils.twoNumber(endHour) + ":" + FormatUtils.twoNumber(endMinute));
        helper.setText(R.id.tv_remark, item.getDescribe());

        helper.setOnClickListener(R.id.ll_root, v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onClickItem(item);
            }
        });
        helper.setOnClickListener(R.id.tv_pause, v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onClickLeft(item,helper.getPosition());
            }
        });
        helper.setOnClickListener(R.id.tv_delete, v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onClickRight(item,helper.getPosition());
            }
        });

    }

    public interface OnClickListener {
        void onClickItem(DeployResponse.ListBean entity);

        void onClickLeft(DeployResponse.ListBean entity, int position);

        void onClickRight(DeployResponse.ListBean entity,int position);
    }

}
