package cloud.antelope.lingmou.mvp.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.http.imageloader.glide.ImageConfigImpl;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.utils.TimeUtils;
import com.lingdanet.safeguard.common.utils.Utils;

import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.model.entity.DailyPoliceAlarmEntity;
import cloud.antelope.lingmou.mvp.ui.activity.DailyPoliceDetailActivity;
import cloud.antelope.lingmou.mvp.ui.widget.CircleProgressNewView;

public class SearchAlertAdapter extends BaseQuickAdapter<DailyPoliceAlarmEntity, BaseViewHolder> {

    private AppComponent mAppComponent;
    private ImageLoader mImageLoader;
    private Activity mActivity;

    public SearchAlertAdapter(Activity activity, @Nullable List<DailyPoliceAlarmEntity> data) {
        super(R.layout.item_daily_normal, data);
        mAppComponent = ArmsUtils.obtainAppComponentFromContext(activity);
        mImageLoader = mAppComponent.imageLoader();
        mActivity = activity;
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
                if (item.getFacePath() != null) {
                    mImageLoader.loadImage(mContext, ImageConfigImpl
                            .builder()
                            .url(item.getFacePath())
                            .cacheStrategy(0)
                            .placeholder(R.drawable.about_stroke_rect_gray)
                            .errorPic(R.drawable.about_stroke_rect_gray)
                            .imageView((ImageView) (helper.getView(R.id.alarm_zhuapai_iv))).build());
                }

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
                if (item.getImageUrl() != null) {
                    mImageLoader.loadImage(mContext, ImageConfigImpl
                            .builder()
                            .url(item.getImageUrl())
                            .cacheStrategy(0)
                            .placeholder(R.drawable.about_stroke_rect_gray)
                            .errorPic(R.drawable.about_stroke_rect_gray)
                            .imageView((ImageView) (helper.getView(R.id.alarm_bukong_iv))).build());
                }
                CircleProgressNewView circleProgressNewView = helper.getView(R.id.alarm_similar_progresview);
                circleProgressNewView.setScore((float) item.getSimilarity(), false);
                break;
            default:
                helper.setVisible(R.id.ll_one, true);
                helper.setVisible(R.id.ll_two, false);
                if (item.getFacePath() != null) {
                    mImageLoader.loadImage(mContext, ImageConfigImpl
                            .builder()
                            .url(item.getFacePath())
                            .cacheStrategy(0)
                            .placeholder(R.drawable.about_stroke_rect_gray)
                            .errorPic(R.drawable.about_stroke_rect_gray)
                            .imageView((ImageView) (helper.getView(R.id.iv_pic))).build());
                }
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
                helper.setText(R.id.tv_name, item.getTaskName());
                helper.setText(R.id.tv_time, TimeUtils.millis2String(Long.parseLong(item.getCaptureTime())));
                helper.setText(R.id.tv_device, item.getCameraName());
                break;
        }
        helper.setOnClickListener(R.id.ll_root, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, DailyPoliceDetailActivity.class);
                intent.putExtra("entity", item);
                intent.putExtra("position", helper.getPosition());
                mActivity.startActivity(intent);
            }
        });
    }
}
