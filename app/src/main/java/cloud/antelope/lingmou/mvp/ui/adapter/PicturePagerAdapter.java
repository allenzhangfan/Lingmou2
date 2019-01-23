package cloud.antelope.lingmou.mvp.ui.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.jess.arms.http.imageloader.glide.GlideArms;
import com.lingdanet.safeguard.common.utils.SizeUtils;

import java.util.ArrayList;
import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.model.entity.DetailCommonEntity;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by ChenXinming on 2018/1/4.
 * description:
 */

public class PicturePagerAdapter extends PagerAdapter {

    private final Paint mPaint;
    private Activity mContext;
    private List<DetailCommonEntity> mDetailEntities;
    private LayoutInflater mLayoutInflater;

    public PicturePagerAdapter(Activity context, List<DetailCommonEntity> list) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mDetailEntities = new ArrayList<>();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(SizeUtils.dp2px(2));
    }

    @Override
    public int getCount() {
        return mDetailEntities.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mLayoutInflater.inflate(R.layout.item_picture_detail, null);
        final PhotoView photoView = view.findViewById(R.id.photo_view);
        final ProgressBar progressBar = view.findViewById(R.id.progress_bar);
        final TextView failTextView = view.findViewById(R.id.fail_text);

        DetailCommonEntity detailEntity = mDetailEntities.get(position);
        String point = detailEntity.point;
        GlideArms.with(mContext).asBitmap().load(detailEntity.srcImg).diskCacheStrategy(DiskCacheStrategy.NONE).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                if (null != resource) {
                    //对bitmap进行操作
                    final Bitmap bitmap = Bitmap.createBitmap(resource.getWidth(), resource.getHeight(), Bitmap.Config.RGB_565);
                    bitmap.setDensity(0);
                    Canvas canvas = new Canvas(bitmap);
                    canvas.drawBitmap(resource, 0, 0, null);
                    if (point != null) {
                        final String[] split = point.split(",");
                        if (split.length == 4) {
                            final int[] coorFaces = {Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3])};
                            canvas.drawRect(coorFaces[0], coorFaces[1], coorFaces[0] + coorFaces[2], coorFaces[1] + coorFaces[3], mPaint);
                        }
                    }
                    if(detailEntity.carRect!=null){
                        canvas.drawRect(detailEntity.carRect.leftTopX,detailEntity.carRect.leftTopY,detailEntity.carRect.rightBtmX,detailEntity.carRect.rightBtmY,mPaint);
                    }
                    progressBar.setVisibility(View.GONE);
                    photoView.setImageBitmap(bitmap);
                }
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                progressBar.setVisibility(View.GONE);
                failTextView.setVisibility(View.VISIBLE);
                // ToastUtils.showShort(R.string.fail_load_picture);
            }
        });
        photoView.setOnPhotoTapListener((view1, x, y) -> {
            if (mOnTapClicklistener != null) {
                mOnTapClicklistener.onTapClick();
            }
        });
        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public void setNewData(List<DetailCommonEntity> list) {
        if (list != null) {
            mDetailEntities.clear();
            mDetailEntities.addAll(list);
        }
        notifyDataSetChanged();
    }

    public OnTapClicklistener mOnTapClicklistener;

    public void setOnTapClicklistener(OnTapClicklistener onTapClicklistener) {
        mOnTapClicklistener = onTapClicklistener;
    }

    public interface OnTapClicklistener {
        void onTapClick();
    }
}
