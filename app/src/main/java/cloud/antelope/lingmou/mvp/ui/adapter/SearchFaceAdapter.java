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
import cloud.antelope.lingmou.mvp.model.entity.DetailCommonEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceDepotDetailEntity;
import cloud.antelope.lingmou.mvp.ui.activity.PictureDetailActivity;

public class SearchFaceAdapter extends BaseQuickAdapter<FaceDepotDetailEntity, BaseViewHolder> {

    private final ImageLoader mImageLoader;
    private final Activity mActivity;
    private Calendar calendar;
    public SearchFaceAdapter(Activity activity, @Nullable List<FaceDepotDetailEntity> data) {
        super(R.layout.item_search_face, data);
        mActivity = activity;
        mImageLoader = ArmsUtils.obtainAppComponentFromContext(activity).imageLoader();
        calendar = Calendar.getInstance();
    }

    @Override
    protected void convert(BaseViewHolder helper, FaceDepotDetailEntity item) {
        mImageLoader.loadImage(mActivity, ImageConfigImpl
                .builder()
                .url(item.facePath)
                .placeholder(R.drawable.about_stroke_rect_gray)
                .errorPic(R.drawable.about_stroke_rect_gray)
                .cacheStrategy(0)
                .imageView(helper.getView(R.id.iv_pic))
                .build());
        helper.setText(R.id.tv_name, item.cameraName);
        calendar.setTimeInMillis(Long.valueOf(item.captureTime));
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        helper.setText(R.id.tv_time, year+"-"+FormatUtils.twoNumber(month)+"-"+FormatUtils.twoNumber(day)+" "+FormatUtils.twoNumber(hour)+":"+FormatUtils.twoNumber(minute)+":"+FormatUtils.twoNumber(second));
        helper.setOnClickListener(R.id.cv_root, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, PictureDetailActivity.class);
                intent.putExtra("position", helper.getPosition());
                ArrayList<FaceDepotDetailEntity> detailEntities = (ArrayList<FaceDepotDetailEntity>) getData();
                ArrayList<DetailCommonEntity> changeData = getChangeData(detailEntities);
                if (changeData != null) {
                    intent.putParcelableArrayListExtra("bean", changeData);
                    mActivity.startActivity(intent);
                }
            }
        });
    }
    private ArrayList<DetailCommonEntity> getChangeData(ArrayList<FaceDepotDetailEntity> entities) {
        if (null != entities) {
            ArrayList<DetailCommonEntity> list = new ArrayList<>();
            for (FaceDepotDetailEntity entity : entities) {
                DetailCommonEntity commonEntity = new DetailCommonEntity();
                commonEntity.deviceName = entity.cameraName;
                commonEntity.endTime = Long.parseLong(entity.captureTime);
                commonEntity.point = entity.faceRect;
                commonEntity.srcImg = entity.scenePath;
                commonEntity.id = entity.id;
                commonEntity.cameraId = entity.cameraId;
                commonEntity.cameraName = entity.cameraName;
                commonEntity.faceImg = entity.facePath;
                commonEntity.favoritesType=2;
                commonEntity.captureTime = Long.parseLong(entity.captureTime);
                list.add(commonEntity);
            }
            return list;
        }
        return null;
    }
}
