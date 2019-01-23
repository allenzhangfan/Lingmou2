package cloud.antelope.lingmou.app.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.jess.arms.http.imageloader.glide.GlideArms;
import com.lingdanet.safeguard.common.utils.Configuration;
import com.lingdanet.safeguard.common.utils.EncryptUtils;
import com.lingdanet.safeguard.common.utils.FileUtils;
import com.lingdanet.safeguard.common.utils.ImageUtils;
import com.lingdanet.safeguard.common.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.model.entity.AttachmentBean;

/**
 * 作者：安兴亚
 * 创建日期：2017/07/25
 * 邮箱：anxingya@lingdanet.com
 * 描述：附件工具类，主要用来解析Url
 */

public class AttachmentUtils {

    /**
     * 附件集合中是否包含视频.
     *
     * @param attachments 事件上传的附件集合
     * @return 视频的截图Url地址，不包含access_token
     */
    public static String hasVideo(List<AttachmentBean> attachments) {
        if (null != attachments) {
            for (AttachmentBean attachment : attachments) {
                AttachmentBean.MetadataBean metadata = attachment.getMetadata();
                if (null != metadata
                        && Constants.EVENT_ATTACHMENT_IS_THUMBNAIL.equals(metadata.isThumbnail())) {
                    return UrlUtils.getEventObjectUrl(attachment.getUrl());
                }
            }
        }
        return null;
    }

    /**
     * 获取视频的Url地址.
     *
     * @param attachments 事件上传的附件集合
     * @return 视频的Url地址，不包含access_token
     */
    public static String getVideoPath(List<AttachmentBean> attachments) {
        if (null != attachments) {
            for (AttachmentBean attachment : attachments) {
                AttachmentBean.MetadataBean metadata = attachment.getMetadata();
                if (null != metadata && metadata.getMimeType().startsWith("video")) {
                    return UrlUtils.getEventObjectUrl(attachment.getUrl());
                }
            }
        }
        return null;
    }

    /**
     * 获取非视频缩略图的图片集合.
     *
     * @param attachments 事件上传中的附件集合
     * @return 包含图片Url的集合，不包含access_token
     */
    public static List<String> getRealImgs(List<AttachmentBean> attachments) {
        List<String> list = new ArrayList<>();
        if (null != attachments) {
            for (AttachmentBean attachment : attachments) {
                AttachmentBean.MetadataBean metadata = attachment.getMetadata();
                if (null != metadata && metadata.getMimeType().startsWith("image")
                        && !Constants.EVENT_ATTACHMENT_IS_THUMBNAIL.equals(metadata.isThumbnail())) {
                    list.add(UrlUtils.getEventObjectUrl(attachment.getUrl()));
                }
            }
        }
        return list;
    }

    /**
     * 获取包含视频缩略图的图片集合.
     *
     * @param attachments 事件上传中的附件集合
     * @return 包含图片Url的集合，不包含access_token
     */
    public static List<String> getImgs(List<AttachmentBean> attachments) {
        List<String> list = new ArrayList<>();
        if (null != attachments) {
            for (AttachmentBean attachment : attachments) {
                AttachmentBean.MetadataBean metadata = attachment.getMetadata();
                if (null != metadata && metadata.getMimeType().startsWith("image")) {
                    list.add(UrlUtils.getEventObjectUrl(attachment.getUrl()));
                }
            }
        }
        return list;
    }

    /**
     * 获取包含视频缩略图的图片集合.
     *
     * @param attachments 事件上传中的附件集合
     * @return 包含图片Url的集合，包含access_token
     */
    public static List<String> getImgsWithToken(List<AttachmentBean> attachments) {
        List<String> list = new ArrayList<>();
        if (null != attachments) {
            for (AttachmentBean attachment : attachments) {
                AttachmentBean.MetadataBean metadata = attachment.getMetadata();
                if (null != metadata && metadata.getMimeType().startsWith("image")) {
                    list.add(attachment.getUrl());
                }
            }
        }
        return list;
    }

    /**
     * 显示图片.
     *
     * @param imageUrl  图片的url路径，不包含access_token
     * @param imageView 显示的ImageView
     */
    public static void showImgUrl(@NonNull String imageUrl, final ImageView imageView) {
        String picPath = Configuration.getPictureDirectoryPath()
                + EncryptUtils.encryptMD5ToString(imageUrl) + Constants.DEFAULT_IMAGE_SUFFIX;
        final String cachePath = Configuration.getAppCacheDirectoryPath()
                + EncryptUtils.encryptMD5ToString(imageUrl) + Constants.DEFAULT_IMAGE_SUFFIX;

        Uri uri = null;
        if (FileUtils.isFile(picPath)) {
            uri = Uri.fromFile(new File(picPath));
        } else if (FileUtils.isFile(cachePath)) {
            uri = Uri.fromFile(new File(cachePath));
        }
        if (null != uri) {
            GlideArms.with(Utils.getContext())
                    .load(uri)
                    // .crossFade()
                    .placeholder(R.drawable.place_holder_big_bitmap)
                    .error(R.drawable.place_holder_big_bitmap)
                    .into(imageView);
            return;
        }

        uri = Uri.parse(UrlUtils.getEventObjectFullUrl(imageUrl));
        GlideArms.with(Utils.getContext())
                .load(uri)
                // .asBitmap()
                .thumbnail(0.1f)
                .placeholder(R.drawable.place_holder_big_bitmap)
                .error(R.drawable.place_holder_big_bitmap)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        imageView.setImageDrawable(resource);
                        ImageUtils.save(ImageUtils.drawable2Bitmap(resource), cachePath, Bitmap.CompressFormat.JPEG);
                    }
                });


    }
}
