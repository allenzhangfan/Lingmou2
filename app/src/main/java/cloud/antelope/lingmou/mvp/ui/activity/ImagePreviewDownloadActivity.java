/*
 * Copyright (C) 2017 LingDaNet.Co.Ltd. All Rights Reserved.
 */

package cloud.antelope.lingmou.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.lingdanet.safeguard.common.utils.Configuration;
import com.lingdanet.safeguard.common.utils.DeviceUtil;
import com.lingdanet.safeguard.common.utils.EncryptUtils;
import com.lingdanet.safeguard.common.utils.FileUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.MediaItem;
import com.lzy.imagepicker.ui.ImagePreviewBaseActivity;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.loader.GlideDownloadImageLoader;
import cloud.antelope.lingmou.common.Constants;

/**
 * @brief 图片预览
 */
public class ImagePreviewDownloadActivity extends ImagePreviewBaseActivity
        implements View.OnClickListener {

    public static final String EXTRA_SHOW_DOWNLOAD = "extra_show_download";
    public static final String EXTRA_SAVE_DIR = "extra_save_dir";

    private String mSaveDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSaveDir = getIntent().getStringExtra(EXTRA_SAVE_DIR);
        super.onCreate(savedInstanceState);

        ImageView btnDownload = (ImageView) findViewById(com.lzy.imagepicker.R.id.btn_download);
        boolean showDownload = getIntent().getBooleanExtra(EXTRA_SHOW_DOWNLOAD, true);

        btnDownload.setOnClickListener(this);
        if (showDownload) {
            btnDownload.setVisibility(View.VISIBLE);
        } else {
            btnDownload.setVisibility(View.GONE);
        }
        topBar.findViewById(com.lzy.imagepicker.R.id.btn_back).setOnClickListener(this);

        mTitleCount.setText(getString(com.lzy.imagepicker.R.string.preview_image_count, mCurrentPosition + 1, mMediaItems.size()));
        //滑动ViewPager的时候，根据外界的数据改变当前的选中状态和当前的图片的位置描述文本
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
                mTitleCount.setText(getString(com.lzy.imagepicker.R.string.preview_image_count, mCurrentPosition + 1, mMediaItems.size()));
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == com.lzy.imagepicker.R.id.btn_download) {
            MediaItem item = mMediaItems.get(mCurrentPosition);
            String savePath;
            // 如果有保存的路径，则使用保存的路径，如果没有保存的路径，则使用 /sdcard/lingmou/download/
            if (!TextUtils.isEmpty(mSaveDir)) {
                savePath = mSaveDir + EncryptUtils.encryptMD5ToString(item.path)
                        + Constants.DEFAULT_IMAGE_SUFFIX;
            } else {
                savePath = Configuration.getDownloadDirectoryPath()
                        + EncryptUtils.encryptMD5ToString(item.path)
                        + Constants.DEFAULT_IMAGE_SUFFIX;
            }

            // 如果最终的文件已经存在，则直接返回，如果不存在，则从缓存中取
            if (FileUtils.isFileExists(savePath)) {
                ToastUtils.showShort(R.string.image_save_to, savePath);
                return;
            }
            String srcPath = Configuration.getAppCacheDirectoryPath()
                    + EncryptUtils.encryptMD5ToString(item.path) + Constants.DEFAULT_IMAGE_SUFFIX;
            if (FileUtils.copyFile(srcPath, savePath)) {
                ToastUtils.showShort(R.string.image_save_to, savePath);
                DeviceUtil.galleryAddMedia(savePath);
            } else {
                ToastUtils.showShort(R.string.image_save_fail);
            }
        } else if (id == com.lzy.imagepicker.R.id.btn_back) {
            onBackPressed();
        }
    }

    @Override
    public void initImagePicker() {
        imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideDownloadImageLoader(mSaveDir));   //设置图片加载器
    }

    /**
     * 单击时，隐藏头和尾
     */
    @Override
    public void onImageSingleTap() {
        if (topBar.getVisibility() == View.VISIBLE) {
            topBar.setAnimation(AnimationUtils.loadAnimation(this, com.lzy.imagepicker.R.anim.top_out));
            topBar.setVisibility(View.GONE);
            tintManager.setStatusBarTintResource(com.lzy.imagepicker.R.color.transparent);//通知栏所需颜色
            //给最外层布局加上这个属性表示，Activity全屏显示，且状态栏被隐藏覆盖掉。
            //            if (Build.VERSION.SDK_INT >= 16) content.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        } else {
            topBar.setAnimation(AnimationUtils.loadAnimation(this, com.lzy.imagepicker.R.anim.top_in));
            topBar.setVisibility(View.VISIBLE);
            tintManager.setStatusBarTintResource(com.lzy.imagepicker.R.color.status_bar);//通知栏所需颜色
            // Activity全屏显示，但状态栏不会被隐藏覆盖，状态栏依然可见，Activity顶端布局部分会被状态遮住
            // if (Build.VERSION.SDK_INT >= 16) content.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }
}
