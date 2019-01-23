/*
 * Copyright (C) 2017 LingDaNet.Co.Ltd. All Rights Reserved.
 */

package cloud.antelope.lingmou.app.utils.loader;

import android.content.Context;
import android.widget.ImageView;

import com.jess.arms.http.imageloader.glide.GlideArms;
import com.lingdanet.safeguard.common.utils.SizeUtils;
import com.lingdanet.safeguard.common.utils.Utils;
import com.youth.banner.loader.ImageLoader;

import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.AttachmentUtils;
import cloud.antelope.lingmou.app.utils.UrlUtils;
import cloud.antelope.lingmou.mvp.model.entity.AttachmentBean;
import cloud.antelope.lingmou.mvp.model.entity.BannerItemEntity;

/**
 * 作者：安兴亚
 * 创建日期：2017/08/29
 * 邮箱：anxingya@lingdanet.com
 * 描述：告警Banner图片加载器
 */

public class CaseBannerImageLoader extends ImageLoader {

    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        String url = null;
        if (null != path) {
            List<AttachmentBean> attachments = ((BannerItemEntity) path).getImgInfoJson();
            List<String> imgsWithToken = AttachmentUtils.getImgsWithToken(attachments);
            if (imgsWithToken.size() > 0) {
                url = UrlUtils.getAbbrUrl(imgsWithToken.get(0), SizeUtils.getScreenWidth(),
                        (int) Utils.getContext().getResources()
                                .getDimension(R.dimen.case_banner_iv_height));
            }
        }
        if (null != url) {
            GlideArms.with(context)                              //配置上下文
                    .load(url)                               //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                    .error(R.drawable.place_holder_big_bitmap)           //设置错误图片
                    .placeholder(R.drawable.place_holder_big_bitmap)     //设置占位图片
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.place_holder_big_bitmap);
        }
    }
}
