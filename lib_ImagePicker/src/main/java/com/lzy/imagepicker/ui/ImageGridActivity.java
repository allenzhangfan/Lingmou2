package com.lzy.imagepicker.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import com.cjt2325.cameralibrary.CaptureActivity;
import com.cjt2325.cameralibrary.JCameraView;
import com.lzy.imagepicker.DataHolder;
import com.lzy.imagepicker.ImageDataSource;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.R;
import com.lzy.imagepicker.adapter.ImageFolderAdapter;
import com.lzy.imagepicker.adapter.ImageRecyclerAdapter;
import com.lzy.imagepicker.adapter.ImageRecyclerAdapter.OnImageItemClickListener;
import com.lzy.imagepicker.bean.MediaFolder;
import com.lzy.imagepicker.bean.MediaItem;
import com.lzy.imagepicker.view.CropImageView;
import com.lzy.imagepicker.view.FolderPopUpWindow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.cjt2325.cameralibrary.CaptureActivity.EXTRA_ALBUM;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧 Github地址：https://github.com/jeasonlzy0216
 * 版    本：1.0
 * 创建日期：2016/5/19
 * 描    述：
 * 修订历史：
 * <p>
 * 2017-03-17
 *
 * @author nanchen
 *         新增可直接传递是否裁剪参数，以及直接拍照
 *         <p>
 *         <p>
 *         ================================================
 */
public class ImageGridActivity extends ImageBaseActivity implements ImageDataSource.OnImagesLoadedListener, OnImageItemClickListener, ImagePicker.OnImageSelectedListener, View.OnClickListener {

    public static final int REQUEST_PERMISSION_STORAGE = 0x01;
    public static final int REQUEST_PERMISSION_CAMERA = 0x02;
    public static final String EXTRAS_TAKE_PICKERS = "TAKE";
    public static final String EXTRAS_IMAGES = "IMAGES";
    public static final String EXTRAS_VIDEOS = "VIDEOS";
    public static final String EXTRAS_LOAD_TYPE = "LOAD_TYPE";

    private ImagePicker imagePicker;

    private boolean isOrigin = false;  //是否选中原图
    private GridView mGridView;  //图片展示控件
    private View mFooterBar;     //底部栏
    private Button mBtnOk;       //确定按钮
    private Button mBtnDir;      //文件夹切换按钮
    private Button mBtnPre;      //预览按钮
    private ImageFolderAdapter mImageFolderAdapter;    //图片文件夹的适配器
    private FolderPopUpWindow mFolderPopupWindow;  //ImageSet的PopupWindow
    private List<MediaFolder> mMediaFolders;   //所有的图片文件夹
    //    private ImageGridAdapter mImageGridAdapter;  //图片九宫格展示的适配器
    private boolean directPhoto = false; // 默认不是直接调取相机
    private int typeCapture = JCameraView.BUTTON_STATE_BOTH;
    private RecyclerView mRecyclerView;
    private ImageRecyclerAdapter mRecyclerAdapter;


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        directPhoto = savedInstanceState.getBoolean(EXTRAS_TAKE_PICKERS, false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(EXTRAS_TAKE_PICKERS, directPhoto);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_grid);

        imagePicker = ImagePicker.getInstance();
        imagePicker.clear();
        imagePicker.addOnImageSelectedListener(this);

        Intent data = getIntent();
        int loadType = ImageDataSource.LOADER_ALL;
        // 新增可直接拍照
        if (data != null && data.getExtras() != null) {
            directPhoto = data.getBooleanExtra(EXTRAS_TAKE_PICKERS, false); // 默认不是直接打开相机
            typeCapture = data.getIntExtra(JCameraView.TYPE_CAPTURE, JCameraView.BUTTON_STATE_BOTH); // 是否只用相机的拍照功能
            if (directPhoto) {
                if (!(checkPermission(Manifest.permission.CAMERA))) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, ImageGridActivity.REQUEST_PERMISSION_CAMERA);
                } else {
                    imagePicker.takePicture(this, ImagePicker.REQUEST_CODE_TAKE, typeCapture);
                }
            }
            ArrayList<MediaItem> images = (ArrayList<MediaItem>) data.getSerializableExtra(EXTRAS_IMAGES);
            ArrayList<MediaItem> videos = (ArrayList<MediaItem>) data.getSerializableExtra(EXTRAS_VIDEOS);
            imagePicker.setSelectedMedias(images);
            imagePicker.setSelectedVideos(videos);
            loadType = data.getIntExtra(EXTRAS_LOAD_TYPE, ImageDataSource.LOADER_ALL);
        }


        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);


        findViewById(R.id.btn_back).setOnClickListener(this);
        mBtnOk = (Button) findViewById(R.id.btn_ok);
        mBtnOk.setOnClickListener(this);
        mBtnDir = (Button) findViewById(R.id.btn_dir);
        mBtnDir.setOnClickListener(this);
        mBtnPre = (Button) findViewById(R.id.btn_preview);
        mBtnPre.setOnClickListener(this);
        mGridView = (GridView) findViewById(R.id.gridview);
        mFooterBar = findViewById(R.id.footer_bar);
        if (imagePicker.isMultiMode()) {
            mBtnOk.setVisibility(View.VISIBLE);
            mBtnPre.setVisibility(View.VISIBLE);
        } else {
            mBtnOk.setVisibility(View.GONE);
            mBtnPre.setVisibility(View.GONE);
        }

        //        mImageGridAdapter = new ImageGridAdapter(this, null);
        mImageFolderAdapter = new ImageFolderAdapter(this, null);
        mRecyclerAdapter = new ImageRecyclerAdapter(this, null);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setAdapter(mRecyclerAdapter);
        onImageSelected(0, null, false);
        loadImages(loadType);
    }

    private void loadImages(int loadType) {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new ImageDataSource(this, null, this, loadType);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_STORAGE);
            }
        } else {
            new ImageDataSource(this, null, this, loadType);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new ImageDataSource(this, null, this, ImageDataSource.LOADER_ALL);
            } else {
                showToast("权限被禁止，无法选择本地图片");
            }
        } else if (requestCode == REQUEST_PERMISSION_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                imagePicker.takePicture(this, ImagePicker.REQUEST_CODE_TAKE, typeCapture);
            } else {
                showToast("权限被禁止，无法打开相机");
            }
        }
    }

    @Override
    protected void onDestroy() {
        imagePicker.removeOnImageSelectedListener(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_ok) {
            Intent intent = new Intent();
            intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, imagePicker.getSelectedMedias());
            intent.putExtra(ImagePicker.EXTRA_RESULT_VIDEOS, imagePicker.getSelectedVideos());
            setResult(ImagePicker.RESULT_CODE_ITEMS, intent);  //多选不允许裁剪裁剪，返回数据
            finish();
        } else if (id == R.id.btn_dir) {
            if (mMediaFolders == null) {
                return;
            }
            //点击文件夹按钮
            createPopupFolderList();
            mImageFolderAdapter.refreshData(mMediaFolders);  //刷新数据
            if (mFolderPopupWindow.isShowing()) {
                mFolderPopupWindow.dismiss();
            } else {
                mFolderPopupWindow.showAtLocation(mFooterBar, Gravity.NO_GRAVITY, 0, 0);
                //默认选择当前选择的上一个，当目录很多时，直接定位到已选中的条目
                int index = mImageFolderAdapter.getSelectIndex();
                index = index == 0 ? index : index - 1;
                mFolderPopupWindow.setSelection(index);
            }
        } else if (id == R.id.btn_preview) {
            Intent intent = new Intent(ImageGridActivity.this, ImagePreviewActivity.class);
            intent.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, 0);
            intent.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, imagePicker.getSelectedMedias());
            intent.putExtra(ImagePreviewActivity.ISORIGIN, isOrigin);
            intent.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
            startActivityForResult(intent, ImagePicker.REQUEST_CODE_PREVIEW);
        } else if (id == R.id.btn_back) {
            if (imagePicker.getStyle() == CropImageView.Style.FACE) {
                imagePicker.takePicture(this, ImagePicker.REQUEST_CODE_TAKE, typeCapture);
            } else {
                //点击返回按钮
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (imagePicker.getStyle() == CropImageView.Style.FACE) {
            imagePicker.takePicture(this, ImagePicker.REQUEST_CODE_TAKE, typeCapture);
        } else {
            //点击返回按钮
            finish();
        }
    }

    /**
     * 创建弹出的ListView
     */
    private void createPopupFolderList() {
        mFolderPopupWindow = new FolderPopUpWindow(this, mImageFolderAdapter);
        mFolderPopupWindow.setOnItemClickListener(new FolderPopUpWindow.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mImageFolderAdapter.setSelectIndex(position);
                imagePicker.setCurrentImageFolderPosition(position);
                mFolderPopupWindow.dismiss();
                MediaFolder mediaFolder = (MediaFolder) adapterView.getAdapter().getItem(position);
                if (null != mediaFolder) {
                    //                    mImageGridAdapter.refreshData(mediaFolder.images);
                    mRecyclerAdapter.refreshData(mediaFolder.images);
                    mBtnDir.setText(mediaFolder.name);
                }
                mGridView.smoothScrollToPosition(0);//滑动到顶部
            }
        });
        mFolderPopupWindow.setMargin(mFooterBar.getHeight());
    }

    @Override
    public void onImagesLoaded(List<MediaFolder> mediaFolders) {
        this.mMediaFolders = mediaFolders;
        imagePicker.setImageFolders(mediaFolders);
        if (mediaFolders.size() == 0) {
            //            mImageGridAdapter.refreshData(null);
            mRecyclerAdapter.refreshData(null);
        } else {
            //            mImageGridAdapter.refreshData(mediaFolders.get(0).images);
            mRecyclerAdapter.refreshData(mediaFolders.get(0).images);
            mBtnDir.setText(mediaFolders.get(0).name);
        }
        //        mImageGridAdapter.setOnImageItemClickListener(this);
        //        mGridView.setAdapter(mImageGridAdapter);
        mRecyclerAdapter.setOnImageItemClickListener(this);
        mImageFolderAdapter.refreshData(mediaFolders);
    }

    @Override
    public void onImageItemClick(View view, MediaItem mediaItem, int position) {
        //根据是否有相机按钮确定位置
        position = imagePicker.isShowCamera() ? position - 1 : position;
        if (imagePicker.isMultiMode()) {
            Intent intent = new Intent(ImageGridActivity.this, ImagePreviewActivity.class);
            intent.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);

            /**
             * 2017-03-20
             *
             * 依然采用弱引用进行解决，采用单例加锁方式处理
             */

            // 据说这样会导致大量图片的时候崩溃
            //            intent.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, imagePicker.getCurrentImageFolderItems());

            // 但采用弱引用会导致预览弱引用直接返回空指针
            DataHolder.getInstance().save(DataHolder.DH_CURRENT_IMAGE_FOLDER_ITEMS, imagePicker.getCurrentImageFolderItems());
            intent.putExtra(ImagePreviewActivity.ISORIGIN, isOrigin);
            startActivityForResult(intent, ImagePicker.REQUEST_CODE_PREVIEW);  //如果是多选，点击图片进入预览界面
        } else {
            imagePicker.clearSelectedMedias();
            imagePicker.addSelectedMediaItem(position, imagePicker.getCurrentImageFolderItems().get(position), true);
            if (imagePicker.isCrop()) {
                Intent intent = new Intent(ImageGridActivity.this, ImageCropActivity.class);
                intent.putExtra("isCapture", false);
                startActivityForResult(intent, ImagePicker.REQUEST_CODE_CROP);  //单选需要裁剪，进入裁剪界面
            } else {
                Intent intent = new Intent();
                intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, imagePicker.getSelectedMedias());
                intent.putExtra(ImagePicker.EXTRA_RESULT_VIDEOS, imagePicker.getSelectedVideos());
                setResult(ImagePicker.RESULT_CODE_ITEMS, intent);   //单选不需要裁剪，返回数据
                finish();
            }
        }
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public void onImageSelected(int position, MediaItem item, boolean isAdd) {
        if (imagePicker.getSelectMediaCount() > 0) {
            mBtnOk.setText(getString(R.string.select_complete, imagePicker.getSelectMediaCount(), imagePicker.getMediaLimit()));
            mBtnOk.setEnabled(true);
            mBtnPre.setEnabled(true);
        } else {
            mBtnOk.setText(getString(R.string.complete));
            mBtnOk.setEnabled(false);
            mBtnPre.setEnabled(false);
        }
        mBtnPre.setText(getResources().getString(R.string.preview_count, imagePicker.getSelectMediaCount()));
        //        mImageGridAdapter.notifyDataSetChanged();
        //        mRecyclerAdapter.notifyItemChanged(position); // 17/4/21 fix the position while click img to preview
        //        mRecyclerAdapter.notifyItemChanged(position + (imagePicker.isShowCamera() ? 1 : 0));// 17/4/24  fix the position while click right bottom preview button
        for (int i = imagePicker.isShowCamera() ? 1 : 0; i < mRecyclerAdapter.getItemCount(); i++) {
            if (mRecyclerAdapter.getItem(i).path != null && mRecyclerAdapter.getItem(i).path.equals(item.path)) {
                mRecyclerAdapter.notifyItemChanged(i);
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.getExtras() != null) {
            if (resultCode == ImagePicker.RESULT_CODE_BACK) {
                isOrigin = data.getBooleanExtra(ImagePreviewActivity.ISORIGIN, false);
            }
            //拍摄照片或视频返回
            else if (resultCode == RESULT_OK && requestCode == ImagePicker.REQUEST_CODE_TAKE) {
                boolean isEnterAlbum = data.getBooleanExtra(EXTRA_ALBUM, false);
                if (isEnterAlbum) {
                    //则瞬间显示所有的相册
                    loadImages(ImageDataSource.LOADER_ALL_IMAGE);
                } else {
                    int mediaWidth;
                    int mediaHeight;
                    long duration;
                    boolean isVideo = data.getBooleanExtra(CaptureActivity.EXTRA_IS_VIDEO, false);
                    String url = data.getStringExtra(CaptureActivity.EXTRA_CAPTURE_ITEM);
                    if (isVideo) {
                        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                        mmr.setDataSource(url);
                        try {
                            mediaWidth = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));//宽
                            mediaHeight = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));//高
                        } catch (NumberFormatException e) {
                            mediaWidth = 480;
                            mediaHeight = 640;
                            e.printStackTrace();
                        }
                        try {
                            duration = Long.parseLong(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)); // 播放时长单位为毫秒
                        } catch (NumberFormatException e) {
                            duration = 10000;
                            e.printStackTrace();
                        }
                    } else {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(url, options);
                        mediaWidth = options.outWidth;
                        mediaHeight = options.outHeight;
                        duration = -1;
                    }

                    //发送广播通知图片增加了
                    ImagePicker.galleryAddPic(this, new File(url));

                    /**
                     * 2017-03-21 对机型做旋转处理
                     */
                    //                String path = imagePicker.getTakeImageFile().getAbsolutePath();
                    //                int degree = BitmapUtil.getBitmapDegree(path);
                    //                if (degree != 0){
                    //                    Bitmap bitmap = BitmapUtil.rotateBitmapByDegree(path,degree);
                    //                    if (bitmap != null){
                    //                        File file = new File(path);
                    //                        try {
                    //                            FileOutputStream bos = new FileOutputStream(file);
                    //                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    //                            bos.flush();
                    //                            bos.close();
                    //                        } catch (IOException e) {
                    //                            e.printStackTrace();
                    //                        }
                    //                    }
                    //                }

                    MediaItem mediaItem = new MediaItem();
                    File file = new File(url);
                    mediaItem.path = url;
                    mediaItem.size = file.length();
                    mediaItem.name = file.getName();
                    mediaItem.width = mediaWidth;
                    mediaItem.height = mediaHeight;
                    mediaItem.addTime = new File(url).lastModified();
                    mediaItem.duration = duration;
                    if (isVideo) {
                        mediaItem.mimeType = "video/mp4";
                        imagePicker.getSelectedVideos().add(mediaItem);
                    } else {
                        mediaItem.mimeType = "image/jpeg";
                    }
                    imagePicker.getSelectedMedias().clear();
                    imagePicker.getSelectedMedias().add(mediaItem);
                    if (imagePicker.isCrop() && !isVideo) {
                        Intent intent = new Intent(ImageGridActivity.this, ImageCropActivity.class);
                        intent.putExtra("isCapture", true);
                        startActivityForResult(intent, ImagePicker.REQUEST_CODE_CROP);  //单选需要裁剪，进入裁剪界面
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, imagePicker.getSelectedMedias());
                        intent.putExtra(ImagePicker.EXTRA_RESULT_VIDEOS, imagePicker.getSelectedVideos());
                        setResult(ImagePicker.RESULT_CODE_ITEMS, intent);   //单选不需要裁剪，返回数据
                        finish();
                    }
                }

            }
            // 从预览页面过来的
            else if (resultCode == ImagePicker.RESULT_CODE_ITEMS && requestCode == ImagePicker.REQUEST_CODE_PREVIEW) {
                Intent intent = new Intent();
                intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, imagePicker.getSelectedMedias());
                intent.putExtra(ImagePicker.EXTRA_RESULT_VIDEOS, imagePicker.getSelectedVideos());
                setResult(ImagePicker.RESULT_CODE_ITEMS, intent);  //多选不允许裁剪裁剪，返回数据
                finish();
            } else {
                //从拍照界面返回
                //点击 X , 没有选择照片
                if (data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS) != null) {
                    //说明是从裁剪页面过来的数据
                    ArrayList<MediaItem> mediaItems = (ArrayList<MediaItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                    String userEntityJson = data.getStringExtra("userEntity");
                    ArrayList<MediaItem> selectedMedias = imagePicker.getSelectedMedias();
                    selectedMedias.clear();
                    selectedMedias.addAll(mediaItems);
                    Intent intent = new Intent();
                    intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, selectedMedias);
                    intent.putExtra("userEntity", userEntityJson);
                    setResult(ImagePicker.RESULT_CODE_ITEMS, intent);
                    finish();
                } else {
                    boolean iscaptrue = data.getBooleanExtra("isCapture", false);
                    if (imagePicker.getStyle() != CropImageView.Style.FACE) {
                        finish();
                    } else if(iscaptrue) {
                        imagePicker.takePicture(this, ImagePicker.REQUEST_CODE_TAKE, typeCapture);
                    }
                }

            }
        } else {
            if (directPhoto) {
                finish();
            }
        }
    }

}
