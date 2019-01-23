package cloud.antelope.lingmou.mvp.ui.adapter;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jess.arms.http.imageloader.glide.GlideArms;
import com.lingdanet.safeguard.common.utils.TimeUtils;

import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.common.UrlConstant;
import cloud.antelope.lingmou.mvp.model.entity.FaceAlarmBlackEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceAlarmDetailBean;
import cloud.antelope.lingmou.mvp.ui.widget.AlarmProgressView;

/**
 * Created by ChenXinming on 2017/12/26.
 * description:
 */

public class FaceAlarmAdapter extends BaseQuickAdapter<FaceAlarmBlackEntity, BaseViewHolder> {
    private Activity mActivity;

    public FaceAlarmAdapter(Activity activity, @Nullable List<FaceAlarmBlackEntity> data) {
        super(R.layout.face_alarm_item, data);
        mActivity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, FaceAlarmBlackEntity item) {
        helper.setText(R.id.title_name, item.getCameraName());
        ImageView bukongIv = helper.getView(R.id.beibukong_iv);
        AlarmProgressView alarmProgressView = helper.getView(R.id.progress_alarm);
        helper.setTag(R.id.progress_alarm, item);
        ImageView tagIv = helper.getView(R.id.check_iv);
        if (null != item) {
            if (!mActivity.isDestroyed()) {
                GlideArms.with(mActivity).load(item.getImageUrl()).placeholder(R.drawable.placeholder_alarm_list).centerCrop().into(tagIv);
            }
            helper.setText(R.id.score_tv, (int)(item.getSimilarity())+"%");
            if (alarmProgressView.getTag() == item) {
                alarmProgressView.setTotalScore(item, item.getSimilarity());
            }
        }if (!mActivity.isDestroyed()) {
            GlideArms.with(mActivity).load(item.getFacePath()).placeholder(R.drawable.placeholder_alarm_list).centerCrop().into(bukongIv);
        }
        helper.setText(R.id.create_time_tv, TimeUtils.millis2String(Long.parseLong(item.getCaptureTime()), "yyyy.MM.dd"));
        TextView statusTv = helper.getView(R.id.status_tv);
        statusTv.setText(getEffective(item.getIsHandle(), item.getIsEffective()));
        setDiffBg(item.getIsHandle(), item.getIsEffective(), statusTv);
    }

    /**
     *
     * @param isHandle 0 未处理；1 已处理
     * @param isEffective
     * @param statusTv
     */
    private void setDiffBg(int isHandle, int isEffective, TextView statusTv) {
        if (isHandle == 0) {
            statusTv.setBackgroundResource(R.drawable.undo_shape_gradient);
        }else{
            if (isEffective == 0) {
                statusTv.setBackgroundResource(R.drawable.invalid_shape_gradient);
            } else if (isEffective == 1) {
                statusTv.setBackgroundResource(R.drawable.valid_shape_gradient);
            }
        }
    }

    private String getEffective(int isHandle, int isEffective) {
        if (isHandle == 0) {
            return "未处理";
        } else {
            if (isEffective == 0) {
                return "无效";
            } else if (isEffective == 1) {
                return "有效";
            }
        }
        return "";
    }
}
