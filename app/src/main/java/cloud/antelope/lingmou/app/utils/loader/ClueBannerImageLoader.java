/*
 * Copyright (C) 2017 LingDaNet.Co.Ltd. All Rights Reserved.
 */

package cloud.antelope.lingmou.app.utils.loader;

import android.content.Context;
import android.widget.ImageView;

import com.youth.banner.loader.ImageLoader;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.AttachmentUtils;
import cloud.antelope.lingmou.mvp.ui.activity.ClueDetailActivity;

/**
 * 作者：安兴亚
 * 创建日期：2017/08/29
 * 邮箱：anxingya@lingdanet.com
 * 描述：告警Banner图片加载器
 */

public class ClueBannerImageLoader extends ImageLoader {

    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        if (null != path) {
            String imgUrl = ((ClueDetailActivity.ClueBannerItem) path).getImgUrl();
            AttachmentUtils.showImgUrl(imgUrl, imageView);
        } else {
            imageView.setImageResource(R.drawable.place_holder_big_bitmap);
        }
    }
}
