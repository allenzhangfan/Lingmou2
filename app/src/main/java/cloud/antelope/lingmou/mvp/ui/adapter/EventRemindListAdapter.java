package cloud.antelope.lingmou.mvp.ui.adapter;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.http.imageloader.glide.ImageConfigImpl;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.utils.TimeUtils;
import com.lingdanet.safeguard.common.utils.Utils;

import java.util.Calendar;
import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.FormatUtils;
import cloud.antelope.lingmou.mvp.model.entity.DailyPoliceAlarmEntity;
import cloud.antelope.lingmou.mvp.model.entity.DeployResponse;
import cloud.antelope.lingmou.mvp.model.entity.EventRemindEntity;


/**
 * Created by ChenXinming on 2017/12/28.
 * description:
 */

public class EventRemindListAdapter extends BaseQuickAdapter<DailyPoliceAlarmEntity, BaseViewHolder> {

    private ImageLoader mImageLoader;
    private Activity mActivity;
    private Calendar calendar;

    public EventRemindListAdapter(Activity activity, @Nullable List<DailyPoliceAlarmEntity> data) {
        super(R.layout.item_event_remind, data);
        mActivity = activity;
        mImageLoader = ArmsUtils.obtainAppComponentFromContext(activity).imageLoader();
        calendar = Calendar.getInstance();
    }

    @Override
    protected void convert(BaseViewHolder helper, DailyPoliceAlarmEntity item) {
        String taskTypeName = "";
        Integer isEffective = item.getIsEffective();
        Integer isHandle = item.getIsHandle();
        mImageLoader.loadImage(mContext, ImageConfigImpl
                .builder()
                .url(item.getImageUrl())
                .cacheStrategy(0)
                .placeholder(R.drawable.about_stroke_rect_gray)
                .errorPic(R.drawable.about_stroke_rect_gray)
                .imageView((ImageView) (helper.getView(R.id.iv_pic))).build());
        if (null != item.getTaskType()) {
            if (1060403 == item.getTaskType()) {
                taskTypeName = Utils.getContext().getString(R.string.outside_person);
            } else if (1061201 == item.getTaskType()) {
                taskTypeName = Utils.getContext().getString(R.string.private_network_suite);
            }
            helper.setText(R.id.tv_alarm_type, taskTypeName);
        }
        if (null != isHandle && 1 == isHandle) {
            if (1 == isEffective) {
                //有效
                helper.setText(R.id.tv_type, R.string.valid_alarm);
                helper.setTextColor(R.id.tv_type, Utils.getContext().getResources().getColor(R.color.color_valid_alarm));
            } else {
                //无效
                helper.setText(R.id.tv_type, R.string.invalid_alarm);
                helper.setTextColor(R.id.tv_type, Utils.getContext().getResources().getColor(R.color.gray_777));
            }
        } else {
            //未处理
            helper.setText(R.id.tv_type, R.string.undo_alarm);
            helper.setTextColor(R.id.tv_type, Utils.getContext().getResources().getColor(R.color.color_undo_alarm));
        }
        helper.setText(R.id.tv_name,item.getTaskName());
        helper.setText(R.id.tv_time, TimeUtils.millis2String(Long.parseLong(item.getCaptureTime())));
        helper.setText(R.id.tv_device, item.getCameraName());

    }


}
