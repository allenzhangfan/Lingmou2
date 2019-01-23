package cloud.antelope.lingmou.mvp.ui.adapter;

import android.text.TextUtils;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jess.arms.http.imageloader.glide.GlideArms;
import com.lingdanet.safeguard.common.modle.RoundCornersTransformation;
import com.lingdanet.safeguard.common.utils.SizeUtils;
import com.lingdanet.safeguard.common.utils.TimeUtils;

import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.common.UrlConstant;
import cloud.antelope.lingmou.mvp.model.entity.FaceNewEntity;

/**
 * Created by liucheng on 16/6/12.
 * 告警列表适配器
 */
public class PersonTrackAdapter extends BaseQuickAdapter<FaceNewEntity, BaseViewHolder> {

    public PersonTrackAdapter(List<FaceNewEntity> list) {
        super(R.layout.item_list_person_track, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, FaceNewEntity item) {
        helper.getAdapterPosition();
        helper.setText(R.id.position_tv, String.valueOf(item.position));
        helper.setText(R.id.name_tv, item.cameraName);
        String dateTime = TimeUtils.millis2String(Long.parseLong(item.captureTime), "yyyy-MM-dd HH:mm:ss");
        helper.setText(R.id.date_time_tv, dateTime);
        ImageView personFaceIv = helper.getView(R.id.person_face_iv);
        RoundCornersTransformation transformation = new RoundCornersTransformation(SizeUtils.dp2px(4), RoundCornersTransformation.CornerType.ALL);
        String coverUrl = TextUtils.isEmpty(item.facePath) ? item.bodyPath : item.facePath;
        GlideArms.with(mContext)
                .asBitmap()
                .load(coverUrl)
                .transform(transformation)
                .placeholder(R.drawable.placeholder_face_list)
                .into(personFaceIv);
        helper.addOnClickListener(R.id.person_face_iv);
    }

}
