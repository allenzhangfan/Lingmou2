package cloud.antelope.lingmou.mvp.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lingdanet.safeguard.common.utils.SizeUtils;
import com.lingdanet.safeguard.common.utils.Utils;
import com.lingdanet.safeguard.common.utils.WaterMarkUtils;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.MediaItem;

import java.util.ArrayList;
import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.mvp.ui.activity.ReportEditActivity;

/**
 * ================================================
 * 作    者：ikkong （ikkong@163.com），修改 jeasonlzy（廖子尧）
 * 版    本：1.0
 * 创建日期：2016/5/19
 * 描    述：
 * 修订历史：微信图片选择的Adapter, 感谢 ikkong 的提交
 * ================================================
 */
public class ImagePickerAdapter extends RecyclerView.Adapter<ImagePickerAdapter.SelectedPicViewHolder> {
    private int maxImgCount;
    private Context mContext;
    private List<MediaItem> mData;
    private LayoutInflater mInflater;
    private OnRecyclerViewItemClickListener listener;
    private boolean isAdded;   //是否额外添加了最后一个图片
    private Bitmap mAddImg;

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.listener = listener;
    }

    public void setImages(List<MediaItem> data) {
        mData = new ArrayList<>(data);
        if (getItemCount() < maxImgCount) {
            mData.add(new MediaItem());
            isAdded = true;
        } else {
            isAdded = false;
        }
        notifyDataSetChanged();
    }

    public List<MediaItem> getImages() {
        //由于图片未选满时，最后一张显示添加图片，因此这个方法返回真正的已选图片
        if (isAdded) return new ArrayList<>(mData.subList(0, mData.size() - 1));
        else return mData;
    }

    public ImagePickerAdapter(Context mContext, List<MediaItem> data, int maxImgCount) {
        View view = LayoutInflater.from(Utils.getContext()).inflate(R.layout.layout_picture_add, null);
        WaterMarkUtils.layoutView(view, SizeUtils.dp2px(90), SizeUtils.dp2px(90));
        mAddImg = WaterMarkUtils.loadBitmapFromView(view);
        this.mContext = mContext;
        this.maxImgCount = maxImgCount;
        this.mInflater = LayoutInflater.from(mContext);
        setImages(data);
    }

    @Override
    public SelectedPicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SelectedPicViewHolder(mInflater.inflate(R.layout.list_item_image, parent, false));
    }

    @Override
    public void onBindViewHolder(SelectedPicViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class SelectedPicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView iv_img;
        private ImageView iv_play_icon;
        private int clickPosition;

        public SelectedPicViewHolder(View itemView) {
            super(itemView);
            iv_img = (ImageView) itemView.findViewById(R.id.iv_img);
            iv_play_icon = (ImageView) itemView.findViewById(R.id.iv_play_icon);
        }

        public void bind(int position) {
            //设置条目的点击事件
            itemView.setOnClickListener(this);
            //根据条目位置设置图片
            MediaItem item = mData.get(position);
            String mimeType = item.mimeType;
            if (!TextUtils.isEmpty(mimeType) && mimeType.startsWith("video")) {
                iv_play_icon.setVisibility(View.VISIBLE);
            } else {
                iv_play_icon.setVisibility(View.GONE);
            }
            if (isAdded && position == getItemCount() - 1) {
                iv_img.setImageBitmap(mAddImg);
                iv_img.setBackgroundResource(0);
                clickPosition = ReportEditActivity.IMAGE_ITEM_ADD;
            } else {
                iv_img.setBackgroundResource(R.drawable.img_bg_round);
                ImagePicker.getInstance().getImageLoader().displayImage((Activity) mContext, item.path, iv_img, 0, 0, null);
                clickPosition = position;
            }
        }

        @Override
        public void onClick(View v) {
            if (listener != null) listener.onItemClick(v, clickPosition);
        }
    }
}
