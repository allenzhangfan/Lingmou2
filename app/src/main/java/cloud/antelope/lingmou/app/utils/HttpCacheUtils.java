package cloud.antelope.lingmou.app.utils;

import com.danikula.videocache.HttpProxyCacheServer;
import com.danikula.videocache.file.FileNameGenerator;
import com.lingdanet.safeguard.common.utils.Configuration;
import com.lingdanet.safeguard.common.utils.EncryptUtils;
import com.lingdanet.safeguard.common.utils.Utils;

import java.io.File;

import cloud.antelope.lingmou.common.Constants;

/**
 * 作者：安兴亚
 * 创建日期：2018/01/23
 * 邮箱：anxingya@lingdanet.com
 * 描述：TODO
 */

public class HttpCacheUtils {

    // 用户VideoView缓存视频用的代理服务器
    private static HttpProxyCacheServer mProxy;

    public static HttpProxyCacheServer getProxy() {
        if (null == mProxy) {
            synchronized (HttpCacheUtils.class) {
                if (null == mProxy) {
                    mProxy = newProxy();
                    return mProxy;
                }
            }
        }
        return mProxy;
    }

    private static HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(Utils.getContext())
                .maxCacheSize(512 * 1024 * 1024)       // 512Mb for cache
                .cacheDirectory(new File(Configuration.getVideoDirectoryPath())) // 设置缓存的目录
                .fileNameGenerator(new FileNameGenerator() {  // 设置缓存的文件名生成器
                    @Override
                    public String generate(String url) {
                        String imgUrl = UrlUtils.getEventObjectUrl(url);
                        return EncryptUtils.encryptMD5ToString(imgUrl) + Constants.DEFAULT_VIDEO_SUFFIX;
                    }
                }).build();
    }
}
