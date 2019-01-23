package cloud.antelope.lingmou.app.utils.loader;

import android.app.Activity;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jess.arms.http.imageloader.glide.GlideAppliesOptions;
import com.jess.arms.http.imageloader.glide.GlideArms;
import com.lzy.imagepicker.listener.ImageLoadListener;
import com.lzy.imagepicker.loader.ImageLoader;

import java.io.File;

import cloud.antelope.lingmou.R;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧 Github地址：https://github.com/jeasonlzy0216
 * 版    本：1.0
 * 创建日期：2016/5/19
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class GlideImageLoader implements ImageLoader {

    @Override
    public void displayImage(Activity activity, String path, ImageView imageView, int width,
                             int height, ImageLoadListener listener) {

        Uri uri;
        if (path != null && path.startsWith("http")) {
            uri = Uri.parse(path);
        } else {
            uri = Uri.fromFile(new File(path));
        }
        GlideArms.with(activity)                             //配置上下文
                .load(uri)                               //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                .error(R.drawable.default_image)           //设置错误图片
                .placeholder(R.drawable.default_image)     //设置占位图片
                .into(imageView);
    }

    @Override
    public void clearMemoryCache() {
    }
}
