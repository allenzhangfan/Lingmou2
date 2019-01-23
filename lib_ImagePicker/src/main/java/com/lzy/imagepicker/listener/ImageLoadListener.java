package com.lzy.imagepicker.listener;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.lzy.imagepicker.bean.LoadErrorEntity;

/**
 * 作者：安兴亚
 * 创建日期：2017/11/02
 * 邮箱：anxingya@lingdanet.com
 * 描述：TODO
 */

public interface ImageLoadListener {

    void onLoadingStarted(String imageUri, View view);

    void onLoadingFailed(String imageUri, View view, LoadErrorEntity errorEntity);

    void onLoadingComplete(String imageUri, View view, Drawable drawable);

}
