package com.lzy.imagepicker.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.R;
import com.lzy.imagepicker.bean.LoadErrorEntity;
import com.lzy.imagepicker.bean.MediaItem;
import com.lzy.imagepicker.listener.ImageLoadListener;
import com.lzy.imagepicker.ui.PlayVideoActivity;
import com.lzy.imagepicker.util.Utils;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧 Github地址：https://github.com/jeasonlzy0216
 * 版    本：1.0
 * 创建日期：2016/5/19
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class ImagePageAdapter extends PagerAdapter {

    private int screenWidth;
    private int screenHeight;
    private ImagePicker imagePicker;
    private ArrayList<MediaItem> images = new ArrayList<>();
    private Activity mActivity;
    private PhotoViewClickListener listener;

    public ImagePageAdapter(Activity activity, ArrayList<MediaItem> images) {
        this.mActivity = activity;
        this.images = images;

        DisplayMetrics dm = Utils.getScreenPix();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        imagePicker = ImagePicker.getInstance();
    }

    public void setData(ArrayList<MediaItem> images) {
        this.images = images;
    }

    public void setPhotoViewClickListener(PhotoViewClickListener listener) {
        this.listener = listener;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = newView(position);
        container.addView(view);
        return view;
    }

    private View newView(int position) {
        View view = View.inflate(com.lingdanet.safeguard.common.utils.Utils.getContext(),
                R.layout.view_photo_view, null);
        PhotoView photoView = (PhotoView) view.findViewById(R.id.photo_view_pv);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.loading_pb);
        ImageView imageView = (ImageView) view.findViewById(R.id.play_icon);
        final MediaItem mediaItem = images.get(position);
        String mimeType = mediaItem.mimeType;
        if (mimeType != null && mimeType.startsWith("video")) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity, PlayVideoActivity.class);
                    intent.putExtra(PlayVideoActivity.TYPE_VIDEO_PATH, mediaItem.path);
                    intent.putExtra(PlayVideoActivity.TYPE_AUTO_FINISH, true);
                    mActivity.startActivity(intent);
                }
            });
        } else {
            imageView.setVisibility(View.GONE);
        }
        imagePicker.getImageLoader().displayImage(mActivity, mediaItem.path, photoView, screenWidth,
                screenHeight, new ImageLoadListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        if (imageUri.startsWith("http")) {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, LoadErrorEntity errorEntity) {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Drawable drawable) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
        photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                if (listener != null) listener.OnPhotoTapListener(view, x, y);
            }
        });
        return view;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public interface PhotoViewClickListener {
        void OnPhotoTapListener(View view, float v, float v1);
    }
}
