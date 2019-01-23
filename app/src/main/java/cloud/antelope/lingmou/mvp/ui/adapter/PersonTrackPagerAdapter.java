package cloud.antelope.lingmou.mvp.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jess.arms.http.imageloader.glide.GlideArms;
import com.lingdanet.safeguard.common.modle.RoundCornersTransformation;
import com.lingdanet.safeguard.common.utils.SizeUtils;
import com.lingdanet.safeguard.common.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.common.UrlConstant;
import cloud.antelope.lingmou.mvp.model.entity.FaceNewEntity;

public class PersonTrackPagerAdapter extends PagerAdapter implements CardAdapter {

    private List<CardView> mViews;
    private List<FaceNewEntity> mData;
    private float mBaseElevation;

    private Context mContext;
    private PagerAdapter mAdapter;

    private OnItemChildClickListener mOnItemChildClickListener;

    public void setOnItemChildClickListener(OnItemChildClickListener onItemChildClickListener) {
        mOnItemChildClickListener = onItemChildClickListener;
    }

    public PersonTrackPagerAdapter(Context context) {
        mContext = context;
        mAdapter = this;
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
    }

    public void setNewData(List<FaceNewEntity> data) {
        mData.clear();
        mViews.clear();
        mData = data;
        if (null != data && data.size() > 0) {
            for (int i = 0; i < data.size(); i++) {
                mViews.add(null);
            }
        }
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.item_person_track, container, false);
        container.addView(view);
        bind(mData.get(position), view, position);
        CardView cardView = (CardView) view.findViewById(R.id.cardView);

        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }

        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        mViews.set(position, cardView);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }

    private void bind(FaceNewEntity item, View view, int position) {
        TextView positionTv = (TextView) view.findViewById(R.id.position_tv);
        positionTv.setText(String.valueOf(position + 1));
        ImageView personFaceIv = (ImageView) view.findViewById(R.id.person_face_iv);
        RoundCornersTransformation transformation = new RoundCornersTransformation(SizeUtils.dp2px(4), RoundCornersTransformation.CornerType.ALL);
        String coverUrl = TextUtils.isEmpty(item.facePath) ? item.bodyPath : item.facePath;
        GlideArms.with(mContext)
                .asBitmap()
                .load(coverUrl)
                .transform(transformation)
                .placeholder(R.drawable.placeholder_face_list)
                .into(personFaceIv);
        TextView nameTv = (TextView) view.findViewById(R.id.name_tv);
        TextView dateTimeTv = (TextView) view.findViewById(R.id.date_time_tv);
        nameTv.setText(item.cameraName);
        String dateTime = TimeUtils.millis2String(Long.parseLong(item.captureTime), "yyyy-MM-dd HH:mm:ss");
        dateTimeTv.setText(dateTime);
        personFaceIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnItemChildClickListener) {
                    mOnItemChildClickListener.onItemChildClick(mAdapter, v, position);
                }
            }
        });
    }

    public interface OnItemChildClickListener {
        public void onItemChildClick(PagerAdapter adapter, View view, int position);
    }
}
