package cloud.antelope.lingmou.mvp.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.http.imageloader.glide.GlideArms;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.FormatUtils;
import cloud.antelope.lingmou.mvp.model.entity.BodyDepotDetailEntity;
import cloud.antelope.lingmou.mvp.model.entity.CarDepotEntity;
import cloud.antelope.lingmou.mvp.model.entity.DetailCommonEntity;
import cloud.antelope.lingmou.mvp.ui.activity.PictureDetailActivity;

public class CarDepotAdapter extends BaseQuickAdapter<CarDepotEntity, BaseViewHolder> {

    private final Activity mActivity;

    public CarDepotAdapter(Activity activity, @Nullable List<CarDepotEntity> data) {
        super(R.layout.item_car_depot, data);
        mActivity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, CarDepotEntity item) {
        GlideArms.with(mActivity).asBitmap().load(item.picUrl.smallPicUrl)
                .placeholder(R.drawable.about_stroke_rect_gray).diskCacheStrategy(DiskCacheStrategy.NONE).into((ImageView) helper.getView(R.id.iv_pic));
        helper.setText(R.id.tv_car_number, item.plateNo);
        helper.setText(R.id.tv_address, item.deviceName);
        helper.setText(R.id.tv_time, TimeUtils.millis2String(item.passTime));
    }
}
