/*
 * Copyright (C) 2017 LingDaNet.Co.Ltd. All Rights Reserved.
 */

package cloud.antelope.lingmou.app.utils.loader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.jess.arms.http.imageloader.glide.GlideArms;
import com.lingdanet.safeguard.common.utils.Configuration;
import com.lingdanet.safeguard.common.utils.EncryptUtils;
import com.lingdanet.safeguard.common.utils.FileUtils;
import com.lingdanet.safeguard.common.utils.ImageUtils;
import com.lzy.imagepicker.bean.LoadErrorEntity;
import com.lzy.imagepicker.listener.ImageLoadListener;
import com.lzy.imagepicker.loader.ImageLoader;

import java.io.File;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.common.Constants;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧 Github地址：https://github.com/jeasonlzy0216
 * 版    本：1.0
 * 创建日期：2016/5/19
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class GlideDownloadImageLoader implements ImageLoader {

    private String mSaveDir;

    public GlideDownloadImageLoader(String saveDir) {
        mSaveDir = saveDir;
    }

    @Override
    public void displayImage(Activity activity, final String path, final ImageView imageView,
                             int width, int height, final ImageLoadListener listener) {

        /* 1. 如果指定的保存路径不为空，则从指定路径中查找文件
           2. 如果指定的保存路径为空，则从缓存中查找文件
           3. 最后再从下载目录(/sdcard/lingmou/download/)中查找一次文件，如果都不存在，则从网络上面下载到缓存中
         */
        Uri uri = null;
        if (!TextUtils.isEmpty(mSaveDir) && FileUtils.isDir(mSaveDir)) {
            String filePath = mSaveDir + EncryptUtils.encryptMD5ToString(path)
                    + Constants.DEFAULT_IMAGE_SUFFIX;
            if (FileUtils.isFile(filePath)) {
                uri = Uri.fromFile(new File(filePath));
            }
        }
        if (null == uri) {
            String cachePath = Configuration.getAppCacheDirectoryPath()
                    + EncryptUtils.encryptMD5ToString(path)
                    + Constants.DEFAULT_IMAGE_SUFFIX;
            if (FileUtils.isFile(cachePath)) {
                uri = Uri.fromFile(new File(cachePath));
            } else {
                String downloadPath = Configuration.getDownloadDirectoryPath()
                        + EncryptUtils.encryptMD5ToString(path)
                        + Constants.DEFAULT_IMAGE_SUFFIX;
                if (FileUtils.isFile(downloadPath)) {
                    uri = Uri.fromFile(new File(downloadPath));
                }
            }
        }
        if (null != uri) {
            final String imageUri = uri.toString();
            if (null != listener) {
                listener.onLoadingStarted(imageUri, imageView);
            }
            GlideArms.with(activity)               //配置上下文
                    .load(uri)                 //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                    // .asBitmap()
                    .error(R.drawable.place_holder_big_bitmap)           //设置错误图片
                    .placeholder(R.drawable.place_holder_big_bitmap)     //设置占位图片
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                            if (null != listener) {
                                listener.onLoadingComplete(imageUri, imageView, resource);
                            }
                            if (resource instanceof GifDrawable) {
                                GifDrawable gifDrawable = (GifDrawable) resource;
                                imageView.setImageDrawable(gifDrawable);
                                gifDrawable.startFromFirstFrame();
                            } else {
                                imageView.setImageDrawable(resource);
                            }
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            if (null != listener) {
                                listener.onLoadingFailed(imageUri, imageView, new LoadErrorEntity(0, "加载图片失败"));
                            }
                            imageView.setImageResource(R.drawable.place_holder_big_bitmap);
                            super.onLoadFailed(errorDrawable);
                        }
                    });
            return;
        }

        uri = Uri.parse(path);
        final String imageUri = uri.toString();
        if (null != listener) {
            listener.onLoadingStarted(imageUri, imageView);
        }
        GlideArms.with(activity)                        //配置上下文
                .load(uri)                          //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                // .asBitmap()
                .error(R.drawable.place_holder_big_bitmap)           //设置错误图片
                .placeholder(R.drawable.place_holder_big_bitmap)     //设置占位图片
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        if (null != listener) {
                            listener.onLoadingComplete(imageUri, imageView, resource);
                        }
                        if (resource instanceof GifDrawable) {
                            GifDrawable gifDrawable = (GifDrawable) resource;
                            imageView.setImageDrawable(gifDrawable);
                            gifDrawable.startFromFirstFrame();
                            String cachePath = Configuration.getAppCacheDirectoryPath()
                                    + EncryptUtils.encryptMD5ToString(path)
                                    + Constants.DEFAULT_IMAGE_GIF_SUFFIX;
                            ImageUtils.save(ImageUtils.drawable2Bitmap(resource), cachePath, Bitmap.CompressFormat.JPEG);
                        } else {
                            imageView.setImageDrawable(resource);
                            String cachePath = Configuration.getAppCacheDirectoryPath()
                                    + EncryptUtils.encryptMD5ToString(path)
                                    + Constants.DEFAULT_IMAGE_SUFFIX;
                            ImageUtils.save(ImageUtils.drawable2Bitmap(resource), cachePath, Bitmap.CompressFormat.JPEG);
                        }
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        if (null != listener) {
                            listener.onLoadingFailed(imageUri, imageView, new LoadErrorEntity(0, "加载图片失败"));
                        }
                        imageView.setImageResource(R.drawable.place_holder_big_bitmap);
                        super.onLoadFailed(errorDrawable);
                    }
                });
    }

    @Override
    public void clearMemoryCache() {
    }
}
