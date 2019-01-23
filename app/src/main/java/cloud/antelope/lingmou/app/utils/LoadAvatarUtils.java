package cloud.antelope.lingmou.app.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.jess.arms.http.imageloader.glide.GlideArms;
import com.lingdanet.safeguard.common.modle.GlideCircleTransform;
import com.lingdanet.safeguard.common.utils.Configuration;
import com.lingdanet.safeguard.common.utils.EncryptUtils;
import com.lingdanet.safeguard.common.utils.FileUtils;
import com.lingdanet.safeguard.common.utils.ImageUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.Utils;

import java.io.File;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.common.UrlConstant;

/**
 * 作者：安兴亚
 * 创建日期：2017/08/08
 * 邮箱：anxingya@lingdanet.com
 * 描述：加载用户头像的工具类
 */

public class LoadAvatarUtils {
    /**
     * 显式圆头像
     * @param avatarImg
     */
    public static void updateAvatar(final ImageView avatarImg) {
        String cidObjIdPath = SPUtils.getInstance().getString(Constants.CONFIG_LAST_USER_AVATAR);
        // final String path = UrlConstant.LY_URL + "files2/" + cidObjIdPath + "?access_token=";
        final String path = cidObjIdPath;
        final String destPath = Configuration.getAvatarDirectoryPath()
                + EncryptUtils.encryptMD5ToString(path) + Constants.DEFAULT_IMAGE_SUFFIX;
        final Uri uri;
        if (FileUtils.isFile(destPath)) {
            uri = Uri.fromFile(new File(destPath));
            GlideArms.with(Utils.getContext())
                    .load(uri)
                    .transform(new GlideCircleTransform(Utils.getContext()))
                    .placeholder(R.drawable.user_header_default)
                    .error(R.drawable.user_header_default)
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                            if (null != avatarImg) {
                                avatarImg.setImageDrawable(resource);
                            }
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                            FileUtils.deleteFile(destPath);
                            if (null != avatarImg) {
                                getAvatarFromServer(avatarImg, path, destPath);
                            }
                        }
                    });
        } else {
            getAvatarFromServer(avatarImg, path, destPath);
        }
    }
    /**
     * 从服务器获取用户的头像（显示圆头像）.
     *
     * @param avatarImg 展示用户头像的ImageView
     * @param path      用户头像的Url，不包括羚羊token
     * @param destPath  头像本地保存的路径
     */
    private static void getAvatarFromServer(final ImageView avatarImg, String path, final String destPath) {
        final String imgUrl = path;
        Uri uri = Uri.parse(imgUrl);
        GlideArms.with(Utils.getContext())
                .load(uri)
                .transform(new GlideCircleTransform(Utils.getContext()))
                .placeholder(R.drawable.user_header_default)
                .error(R.drawable.user_header_default)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource,
                                                Transition<? super Drawable> glideAnimation) {
                        if (null != avatarImg) {
                            avatarImg.setImageDrawable(resource);
                        }
                        if (resource != null) {
                            Bitmap bitmap;
                            if (resource instanceof BitmapDrawable) {
                                bitmap = ((BitmapDrawable)resource).getBitmap();
                                ImageUtils.save(bitmap, destPath, Bitmap.CompressFormat.JPEG);
                            } else if (resource instanceof GifDrawable) {
                                bitmap = ((GifDrawable)resource).getFirstFrame();
                                ImageUtils.save(bitmap, destPath, Bitmap.CompressFormat.JPEG);
                            }
                        }
                    }
                });
    }

}
