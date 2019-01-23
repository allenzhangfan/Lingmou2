package cloud.antelope.lingmou.mvp.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.http.imageloader.glide.GlideArms;
import com.jess.arms.http.imageloader.glide.GlideRequest;
import com.jess.arms.http.imageloader.glide.ImageConfigImpl;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.utils.TimeUtils;
import com.lingdanet.safeguard.common.utils.Utils;

import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.model.entity.DailyPoliceAlarmEntity;
import cloud.antelope.lingmou.mvp.ui.widget.CircleProgressNewView;

/**
 * 作者：陈新明
 * 创建日期：2018/09/07
 * 邮箱：chenxinming@antelop.cloud
 * 描述：新的告警列表页面
 */
public class DailyPoliceAdapter extends BaseQuickAdapter<DailyPoliceAlarmEntity, BaseViewHolder> {

    private AppComponent mAppComponent;
    private ImageLoader mImageLoader;

    public DailyPoliceAdapter(List<DailyPoliceAlarmEntity> data, Context context) {
        super(R.layout.item_daily_normal, data);
        mAppComponent = ArmsUtils.obtainAppComponentFromContext(context);
        mImageLoader = mAppComponent.imageLoader();
    }

    @Override
    protected void convert(BaseViewHolder helper, DailyPoliceAlarmEntity item) {
        String taskTypeName = "";
        Integer isEffective = item.getIsEffective();
        Integer isHandle = item.getIsHandle();
        switch (item.getTaskType()) {
            case 101501:
            case 101505:
                helper.setVisible(R.id.ll_two, true);
                helper.setVisible(R.id.ll_one, false);
                helper.setText(R.id.alarm_name_tv, item.getTaskName());
                String captureTime = TimeUtils.millis2String(Long.parseLong(item.getCaptureTime()));
                helper.setText(R.id.alarm_capture_time_tv, captureTime);
                helper.setText(R.id.alarm_device_tv, item.getCameraName());
                mImageLoader.loadImage(mContext, ImageConfigImpl
                        .builder()
                        .url(item.getFacePath())
                        .cacheStrategy(0)
                        .placeholder(R.drawable.about_stroke_rect_gray)
                        .errorPic(R.drawable.about_stroke_rect_gray)
                        .imageView((ImageView) (helper.getView(R.id.alarm_zhuapai_iv))).build());

                if (null != isHandle && 1 == isHandle) {
                    if (1 == isEffective) {
                        //有效
                        helper.setText(R.id.alarm_state_tv, R.string.valid_alarm);
                        helper.setTextColor(R.id.alarm_state_tv, Utils.getContext().getResources().getColor(R.color.color_valid_alarm));
                    } else {
                        //无效
                        helper.setText(R.id.alarm_state_tv, R.string.invalid_alarm);
                        helper.setTextColor(R.id.alarm_state_tv, Utils.getContext().getResources().getColor(R.color.gray_777));
                    }
                } else {
                    //未处理
                    helper.setText(R.id.alarm_state_tv, R.string.undo_alarm);
                    helper.setTextColor(R.id.alarm_state_tv, Utils.getContext().getResources().getColor(R.color.color_undo_alarm));
                }
                Integer taskType = item.getTaskType();

                if (null != taskType) {
                    if (101501 == taskType) {
                        taskTypeName = Utils.getContext().getString(R.string.control_key_person);
                    } else if (101505 == taskType) {
                        taskTypeName = Utils.getContext().getString(R.string.person_tracking);
                    }
                }
                helper.setText(R.id.alarm_type_tv, taskTypeName);
                mImageLoader.loadImage(mContext, ImageConfigImpl
                        .builder()
                        .url(item.getImageUrl())
                        .cacheStrategy(0)
                        .placeholder(R.drawable.about_stroke_rect_gray)
                        .errorPic(R.drawable.about_stroke_rect_gray)
                        .imageView((ImageView) (helper.getView(R.id.alarm_bukong_iv))).build());
                CircleProgressNewView circleProgressNewView = helper.getView(R.id.alarm_similar_progresview);
                circleProgressNewView.setScore((float) item.getSimilarity(), false);
                break;
            default:
                helper.setVisible(R.id.ll_one, true);
                helper.setVisible(R.id.ll_two, false);
                if (item.getFacePath() != null) {
                    GlideRequest<Drawable> placeholder = GlideArms.with(mContext)
                            .load(item.getFacePath())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.about_stroke_rect_gray);
                    if (item.getTaskType() == 101503) {
                        placeholder.into((ImageView) helper.getView(R.id.iv_pic));
                    } else {
                        placeholder.centerCrop().into((ImageView) helper.getView(R.id.iv_pic));
                    }
                }
                // 101501:重点人员布控  101502:外来人员布控  101503:魅影报警  101504:一体机报警  101505:临控报警
                if (null != item.getTaskType()) {
                    if (101502 == item.getTaskType()) {
                        taskTypeName = Utils.getContext().getString(R.string.outside_person);
                    } else if (101504 == item.getTaskType()) {
                        taskTypeName = Utils.getContext().getString(R.string.private_network_suite);
                    } else if (101503 == item.getTaskType()) {
                        taskTypeName = Utils.getContext().getString(R.string.event_remind);
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
                helper.setText(R.id.tv_name, item.getTaskName());
                helper.setText(R.id.tv_time, TimeUtils.millis2String(Long.parseLong(item.getCaptureTime())));
                helper.setText(R.id.tv_device, item.getCameraName());
                break;
        }
    }
}
