package cloud.antelope.lingmou.mvp.ui.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jess.arms.http.imageloader.glide.GlideArms;
import com.lingdanet.safeguard.common.utils.TimeUtils;
import com.lingdanet.safeguard.common.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.model.entity.CameraItem;
import cloud.antelope.lingmou.mvp.model.entity.FaceNewEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;
import cloud.antelope.lingmou.mvp.ui.widget.RectImageView;

/**
 * Created by ChenXinming on 2018/1/22.
 * description:
 */

public class FaceDepotDetailAdapter extends BaseQuickAdapter<FaceNewEntity, BaseViewHolder> {
    private Context mContext;

    public FaceDepotDetailAdapter(Context context, @Nullable List<FaceNewEntity> data) {
        super(R.layout.item_face_depot_detail, data);
        this.mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, FaceNewEntity item) {
        RectImageView iv = helper.getView(R.id.iv);
        ImageView cb = helper.getView(R.id.cb);
        if (!TextUtils.isEmpty(item.facePath)) {
            GlideArms.with(mContext).load(item.facePath).centerCrop().placeholder(R.drawable.about_stroke_rect_gray).into(iv);
        } else {
            GlideArms.with(mContext).load(item.bodyPath).placeholder(R.drawable.about_stroke_rect_gray).into(iv);
        }
        helper.setVisible(R.id.cb,item.checkable);
        cb.setSelected(item.mIsChecked);
        helper.setText(R.id.tv_similarity, (int) item.score + "%");
        helper.setText(R.id.tv_name, item.cameraName);
        String timeDate = TimeUtils.millis2String(Long.parseLong(item.captureTime), "MM-dd HH:mm:ss");
        helper.setText(R.id.tv_time, timeDate);
    }
}
