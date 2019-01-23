package com.lzy.imagepicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;

import com.cjt2325.cameralibrary.CaptureActivity;
import com.cjt2325.cameralibrary.JCameraView;
import com.lzy.imagepicker.bean.MediaFolder;
import com.lzy.imagepicker.bean.MediaItem;
import com.lzy.imagepicker.loader.ImageLoader;
import com.lzy.imagepicker.view.CropImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧 Github地址：https://github.com/jeasonlzy0216
 * 版    本：1.0
 * 创建日期：2016/5/19
 * 描    述：图片选择的入口类
 * 修订历史：
 * <p>
 * 2017-03-20
 *
 * @author nanchen
 *         采用单例和弱引用解决Intent传值限制导致的异常
 *         ================================================
 */
public class ImagePicker {

    public static final String TAG = ImagePicker.class.getSimpleName();
    public static final int REQUEST_CODE_TAKE = 1001;
    public static final int REQUEST_CODE_CROP = 1002;
    public static final int REQUEST_CODE_PREVIEW = 1003;
    public static final int RESULT_CODE_ITEMS = 1004;
    public static final int RESULT_CODE_BACK = 1005;

    public static final String EXTRA_RESULT_ITEMS = "extra_result_items";
    public static final String EXTRA_RESULT_VIDEOS = "extra_result_videos";
    public static final String EXTRA_SELECTED_IMAGE_POSITION = "selected_image_position";
    public static final String EXTRA_IMAGE_ITEMS = "extra_image_items";
    public static final String EXTRA_VIDEO_ITEMS = "extra_video_items";
    public static final String EXTRA_FROM_ITEMS = "extra_from_items";

    private boolean multiMode = true;    //图片选择模式
    private int mediaLimit = 9;         //最大选择图片数量
    private int videoLimit = 3;          //最大选择视频数量
    private boolean crop = true;         //裁剪
    private boolean showCamera = true;   //显示相机
    private boolean isSaveRectangle = false;  //裁剪后的图片是否是矩形，否者跟随裁剪框的形状
    private int outPutX = 800;           //裁剪保存宽度
    private int outPutY = 800;           //裁剪保存高度
    private int focusWidth = 280;         //焦点框的宽度
    private int focusHeight = 280;        //焦点框的高度
    private ImageLoader imageLoader;     //图片加载器
    private CropImageView.Style style = CropImageView.Style.RECTANGLE; //裁剪框的形状
    private File cropCacheFolder;
    private File takeImageFile;
    public Bitmap cropBitmap;
    private boolean isSelectVideo;

    private ArrayList<MediaItem> mSelectedMedias = new ArrayList<>();   //选中的媒体文件集合
    private ArrayList<MediaItem> mSelectedVideos = new ArrayList<>();   //选中的视频集合
    private List<MediaFolder> mMediaFolders;      //所有的图片文件夹
    private int mCurrentImageFolderPosition = 0;  //当前选中的文件夹位置 0表示所有图片
    private List<OnImageSelectedListener> mImageSelectedListeners;          // 图片选中的监听回调

    private static ImagePicker mInstance;

    private ImagePicker() {
    }

    public static ImagePicker getInstance() {
        if (mInstance == null) {
            synchronized (ImagePicker.class) {
                if (mInstance == null) {
                    mInstance = new ImagePicker();
                }
            }
        }
        return mInstance;
    }

    public boolean isMultiMode() {
        return multiMode;
    }

    public void setMultiMode(boolean multiMode) {
        this.multiMode = multiMode;
    }

    public int getMediaLimit() {
        return mediaLimit;
    }

    public void setMediaLimit(int mediaLimit) {
        this.mediaLimit = mediaLimit;
    }

    public int getVideoLimit() {
        return videoLimit;
    }

    public void setVideoLimit(int videoLimit) {
        this.videoLimit = videoLimit;
    }

    public boolean isCrop() {
        return crop;
    }

    public void setCrop(boolean crop) {
        this.crop = crop;
    }

    public boolean isShowCamera() {
        return showCamera;
    }

    public void setShowCamera(boolean showCamera) {
        this.showCamera = showCamera;
    }

    public boolean isSaveRectangle() {
        return isSaveRectangle;
    }

    public void setSaveRectangle(boolean isSaveRectangle) {
        this.isSaveRectangle = isSaveRectangle;
    }

    public boolean isSelectVideo() {
        return isSelectVideo;
    }

    public void setSelectVideo(boolean selectVideo) {
        isSelectVideo = selectVideo;
    }

    public int getOutPutX() {
        return outPutX;
    }

    public void setOutPutX(int outPutX) {
        this.outPutX = outPutX;
    }

    public int getOutPutY() {
        return outPutY;
    }

    public void setOutPutY(int outPutY) {
        this.outPutY = outPutY;
    }

    public int getFocusWidth() {
        return focusWidth;
    }

    public void setFocusWidth(int focusWidth) {
        this.focusWidth = focusWidth;
    }

    public int getFocusHeight() {
        return focusHeight;
    }

    public void setFocusHeight(int focusHeight) {
        this.focusHeight = focusHeight;
    }

    public File getTakeImageFile() {
        return takeImageFile;
    }

    public File getCropCacheFolder(Context context) {
        if (cropCacheFolder == null) {
            cropCacheFolder = new File(context.getCacheDir() + "/ImagePicker/cropTemp/");
        }
        return cropCacheFolder;
    }

    public void setCropCacheFolder(File cropCacheFolder) {
        this.cropCacheFolder = cropCacheFolder;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public void setImageLoader(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }

    public CropImageView.Style getStyle() {
        return style;
    }

    public void setStyle(CropImageView.Style style) {
        this.style = style;
    }

    public List<MediaFolder> getImageFolders() {
        return mMediaFolders;
    }

    public void setImageFolders(List<MediaFolder> mediaFolders) {
        mMediaFolders = mediaFolders;
    }

    public int getCurrentImageFolderPosition() {
        return mCurrentImageFolderPosition;
    }

    public void setCurrentImageFolderPosition(int mCurrentSelectedImageSetPosition) {
        mCurrentImageFolderPosition = mCurrentSelectedImageSetPosition;
    }

    public ArrayList<MediaItem> getCurrentImageFolderItems() {
        return mMediaFolders.get(mCurrentImageFolderPosition).images;
    }

    public boolean isSelectMedia(MediaItem item) {
        return mSelectedMedias.contains(item);
    }

    public boolean isSelectVideo(MediaItem item) {
        return mSelectedVideos.contains(item);
    }

    public int getSelectMediaCount() {
        if (mSelectedMedias == null) {
            return 0;
        }
        return mSelectedMedias.size();
    }

    public int getSelectVideoCount() {
        if (mSelectedVideos == null) {
            return 0;
        }
        return mSelectedVideos.size();
    }

    public ArrayList<MediaItem> getSelectedMedias() {
        return mSelectedMedias;
    }

    public ArrayList<MediaItem> getSelectedVideos() {
        return mSelectedVideos;
    }

    public void clearSelectedMedias() {
        if (mSelectedMedias != null) mSelectedMedias.clear();
        if (mSelectedVideos != null) mSelectedVideos.clear();
    }

    public void clear() {
        if (mImageSelectedListeners != null) {
            mImageSelectedListeners.clear();
            mImageSelectedListeners = null;
        }
        if (mMediaFolders != null) {
            mMediaFolders.clear();
            mMediaFolders = null;
        }
        if (mSelectedMedias != null) {
            mSelectedMedias.clear();
        }
        if (mSelectedVideos != null) {
            mSelectedVideos.clear();
        }
        mCurrentImageFolderPosition = 0;
    }

    /**
     * 拍照的方法
     */
    public void takePicture(Activity activity, int requestCode, int typeCapture) {
        Intent takePictureIntent = new Intent(activity, CaptureActivity.class);
        takePictureIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        takePictureIntent.putExtra(JCameraView.TYPE_CAPTURE, typeCapture);
        if (getStyle() == CropImageView.Style.FACE) {
            takePictureIntent.putExtra("styleExtra", 1);
        }
        activity.startActivityForResult(takePictureIntent, requestCode);
        //        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
        //            if (Utils.existSDCard())
        //                takeImageFile = new File(Environment.getExternalStorageDirectory(), "/DCIM/camera/");
        //            else takeImageFile = Environment.getDataDirectory();
        //            takeImageFile = createFile(takeImageFile, "IMG_", ".jpg");
        //            if (takeImageFile != null) {
        //                // 默认情况下，即不需要指定intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        //                // 照相机有自己默认的存储路径，拍摄的照片将返回一个缩略图。如果想访问原始图片，
        //                // 可以通过dat extra能够得到原始图片位置。即，如果指定了目标uri，data就没有数据，
        //                // 如果没有指定uri，则data就返回有数据！
        //
        //                Uri uri;
        //                if (VERSION.SDK_INT <= VERSION_CODES.M) {
        //                    uri = Uri.fromFile(takeImageFile);
        //                } else {
        //
        //
        //                    /**
        //                     * 7.0 调用系统相机拍照不再允许使用Uri方式，应该替换为FileProvider
        //                     * 并且这样可以解决MIUI系统上拍照返回size为0的情况
        //                     */
        //                    uri = FileProvider.getUriForFile(activity, ProviderUtil.getFileProviderName(activity), takeImageFile);
        //                    //加入uri权限 要不三星手机不能拍照
        //                    List<ResolveInfo> resInfoList = activity.getPackageManager().queryIntentActivities
        //                            (takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
        //                    for (ResolveInfo resolveInfo : resInfoList) {
        //                        String packageName = resolveInfo.activityInfo.packageName;
        //                        activity.grantUriPermission(packageName, uri, Intent
        //                                .FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //                    }
        //                }
        //
        //                LogUtils.e("nanchen", ProviderUtil.getFileProviderName(activity));
        //                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        //            }
        //        }
        //        activity.startActivityForResult(takePictureIntent, requestCode);
    }

    /**
     * 根据系统时间、前缀、后缀产生一个文件
     */
    public static File createFile(File folder, String prefix, String suffix) {
        if (!folder.exists() || !folder.isDirectory()) folder.mkdirs();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
        String filename = prefix + dateFormat.format(new Date(System.currentTimeMillis())) + suffix;
        return new File(folder, filename);
    }

    /**
     * 扫描图片
     */
    public static void galleryAddPic(Context context, File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    /**
     * 图片选中的监听
     */
    public interface OnImageSelectedListener {
        void onImageSelected(int position, MediaItem item, boolean isAdd);
    }

    public void addOnImageSelectedListener(OnImageSelectedListener l) {
        if (mImageSelectedListeners == null) mImageSelectedListeners = new ArrayList<>();
        mImageSelectedListeners.add(l);
    }

    public void removeOnImageSelectedListener(OnImageSelectedListener l) {
        if (mImageSelectedListeners == null) return;
        mImageSelectedListeners.remove(l);
    }

    public void addSelectedMediaItem(int position, MediaItem item, boolean isAdd) {
        if (isAdd) mSelectedMedias.add(item);
        else mSelectedMedias.remove(item);
        notifyImageSelectedChanged(position, item, isAdd);
    }

    public void addSelectedVideoItem(int position, MediaItem item, boolean isAdd) {
        if (isAdd) mSelectedVideos.add(item);
        else mSelectedVideos.remove(item);
    }

    public void setSelectedMedias(ArrayList<MediaItem> selectedImages) {
        if (selectedImages == null) {
            return;
        }
        this.mSelectedMedias = selectedImages;
    }

    public void setSelectedVideos(ArrayList<MediaItem> selectedVideos) {
        if (selectedVideos == null) {
            return;
        }
        this.mSelectedVideos = selectedVideos;
    }

    private void notifyImageSelectedChanged(int position, MediaItem item, boolean isAdd) {
        if (mImageSelectedListeners == null) return;
        for (OnImageSelectedListener l : mImageSelectedListeners) {
            l.onImageSelected(position, item, isAdd);
        }
    }

    /**
     * 是否是支持的图片格式
     *
     * @param mediaItem
     * @return
     */
    public boolean isSupportImage(MediaItem mediaItem) {
        Map<String, String> map = new HashMap<>();
        map.put("jpeg", "image/jpeg");
        map.put("jpg", "image/jpeg");
        map.put("png", "image/png");
        String extension = mediaItem.path.substring(mediaItem.path.lastIndexOf(".") + 1);
        String value = map.get(extension);
        if (!TextUtils.isEmpty(value)) {
            if (value.equals(mediaItem.mimeType)) {
                return true;
            }
        }
        return false;
    }

}