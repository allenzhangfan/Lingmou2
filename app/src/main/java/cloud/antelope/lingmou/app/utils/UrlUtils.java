package cloud.antelope.lingmou.app.utils;

import android.text.TextUtils;

import com.danikula.videocache.HttpProxyCacheServer;
import com.danikula.videocache.file.FileNameGenerator;
import com.lingdanet.safeguard.common.utils.Configuration;
import com.lingdanet.safeguard.common.utils.EncryptUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.Utils;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cloud.antelope.lingmou.common.Constants;

/**
 * Created by liucheng on 16/5/26.
 */
public class UrlUtils {

    /**
     * 获取事件传输附件的Url.
     * <p>
     * 这里的算法：
     * 先以"access_token="进行切分：
     * 如果切分出来的数组长度为1，说明没有"access_token="参数，则整个url都是key。
     * 如果切分出来的数组长度大于1，则将"access_token="后面的部分使用"&"再次进行切分：
     * 如果切分出来的数组长度为1，说明"access_token="后面没有其他参数了，
     * 则保留"access_token="前面的部分为key，此时需要移除该key的最后一个字符，"?" 或者 "&"
     * 如果切分出来的数组长度大于1，说明"access_token="后面还有其他参数，
     * 则将后面的参数拼接到"access_token="前面的部分，注意"&"符号的处理
     *
     * @param url 事件传输附件的url，如：
     *            https://osstest.topvdn.com/files2/2147489027/59911f3d8000150305001266
     *            https://osstest.topvdn.com/files2/2147489027/59911f3d8000150305001266?access_token=2147489027_3221225472_1533979590_a5aeb5d939b2ee9a6e81c57be66aefdf
     *            https://osstest.topvdn.com/files2/2147489027/59911f3d8000150305001266?access_token=2147489027_3221225472_1533979590_a5aeb5d939b2ee9a6e81c57be66aefdf&size=234423&size=234423
     *            https://osstest.topvdn.com/files2/2147489027/59911f3d8000150305001266?keyid=safdasdfafafda&access_token=2147489027_3221225472_1533979590_a5aeb5d939b2ee9a6e81c57be66aefdf
     *            https://osstest.topvdn.com/files2/2147489027/59911f3d8000150305001266?keyid=safdasdfafafda&access_token=2147489027_3221225472_1533979590_a5aeb5d939b2ee9a6e81c57be66aefdf&size=231234
     *            https://osstest.topvdn.com/files2/2147489027/59911f3d8000150305001266?keyid=safdasdfafafda&size=231234
     * @return 返回不包含access_token的url
     */
    public static String getEventObjectUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return url;
        }
        StringBuilder sb = new StringBuilder();
        String[] splits = url.split("access_token=");
        if (splits.length == 1) {
            sb.append(splits[0]);
        } else if (splits.length > 1) {
            String[] params = splits[1].split("&");
            if (params.length == 1) {
                sb.append(splits[0].substring(0, splits[0].length() - 1));
            } else if (params.length > 1) {
                sb.append(splits[0]);
                for (int i = 1; i < params.length; i++) {
                    sb.append(params[i]);
                    if (i != params.length - 1) {
                        sb.append("&");
                    }
                }
            }
        }
        return sb.toString();
    }

    /**
     * 获取事件传输附件包含最新access_token的Url.
     *
     * @param url 不包含access_token的Url
     * @return 包含access_token的Url
     */
    public static String getEventObjectFullUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return url;
        }
        if (url.contains("?")) {
            return url + "&access_token="
                    + SPUtils.getInstance().getString(Constants.CONFIG_CYQZ_LY_TOKEN);
        } else {
            return url + "?access_token="
                    + SPUtils.getInstance().getString(Constants.CONFIG_CYQZ_LY_TOKEN);
        }
    }

    /**
     * 获取Url路径最后的文件扩展名.
     *
     * @param imgUrl 事件传输附件的Url
     * @return 事件传输附件的文件扩展名，如：".jpeg"
     */
    public static String getUrlExtension(String imgUrl) {
        if (TextUtils.isEmpty(imgUrl)) {
            return "";
        } else {
            int lastPoi = imgUrl.lastIndexOf('.');
            int lastSep = imgUrl.lastIndexOf('/');
            if (lastPoi == -1 || lastSep >= lastPoi) {
                return "";
            }
            return imgUrl.substring(lastPoi);
        }
    }

    /**
     * 获取缩略图格式的Url.传入的宽高为最大限制值
     *
     * @param url    图片的Url路径
     * @param width  图片希望的最大宽度
     * @param height 图片希望的最大高度
     * @return 包含 &width=xxx&height=xxx 的Url链接
     */
    public static String getAbbrUrl(String url, int width, int height) {
        if (TextUtils.isEmpty(url)) {
            return url;
        }
        Pattern pattern = Pattern.compile("&width=\\d+&height=\\d+",
                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return url;
        }
        return url + "&width=" + width + "&height=" + height;
    }

    /**
     * 组装用户头像的Url.
     *
     * @param url   用户头像的Url
     * @param cid   羚羊云返回的cid
     * @param objId 羚羊云返回的objId
     * @return 组装好的用户头像Url
     */
    public static String assembleAvatarUrl(String url, long cid, String objId) {
        if (TextUtils.isEmpty(url) || cid < 0 || TextUtils.isEmpty(objId)) {
            return "";
        }
        return url + "files2/" + cid + "/" + objId + "?access_token=";
    }



    // 用户VideoView缓存视频用的代理服务器
    private static HttpProxyCacheServer mProxy;

    public static HttpProxyCacheServer getProxy() {
        return mProxy == null ? (mProxy = newProxy()) : mProxy;
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
