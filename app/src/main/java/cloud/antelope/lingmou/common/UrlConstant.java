package cloud.antelope.lingmou.common;

/**
 * 作者：安兴亚
 * 创建日期：2017/08/09
 * 邮箱：anxingya@lingdanet.com
 * 描述：Url地址常量
 */

public class UrlConstant {

    /**
     * BaseUrl.
     */

//    public static String BASE_URL = "http://th76fn.natappfree.cc/";
//     public static String BASE_URL = "http://192.168.14.46:9091/";
    public static String BASE_URL = " http://lm-test04.lingda.com/";
    //     public static String BASE_URL = "http://lm-test01.lingda.com/";
    // public static String BASE_URL = "http://lm-dev.lingda.com/";
//     public static String SERVER_BASE_URL = "https://api.topvdn.com/route/appPlatform";
//     public static String SERVER_BASE_URL = "http://m.topvdn.com/appPlatform.json";
//     public static String SERVER_BASE_URL = "http://192.168.14.33/appPlatform.json";
//     public static String SERVER_BASE_URL = "http://192.168.152.150:3000/users/getServer";

    public static String SERVER = "http://192.168.100.3/static";
//    public static String SERVER = "http://m.topvdn.com";
    public static String SERVER_BASE_URL = SERVER + "/appPlatform.json";
    /**
     * 更新
     */
    public static final String URL_UPDATE = SERVER+"/updateApp.json";

    /**
     * 对象存储上传
     */
    public static String LY_URL = "https://jxsr-oss1.antelopecloud.cn/";

    /**
     * 事件上传地址
     */
    public static String CYQZ_LY_URL = "https://osstest.topvdn.com/";

    /**
     * 单兵的登录Url
     */
    public static String SOLDIER_URL = "https://jxsr-api.antelopecloud.cn/";

    /**
     * 羚羊录像列表的url
     */
    public static String LY_CAMERA_URL = "https://jxsr-api.antelopecloud.cn/";

    /**
     * 灵达包含nginx路径的Url.
     */
    public static String LD_SERVER = BASE_URL + "api/";
//    public static String LD_SERVER = BASE_URL ;

    /**
     * App分享的logo在服务器端的地址.
     */
    public static final String SHARE_LOGO = "static/imgs/app_icon_share.png";

}
