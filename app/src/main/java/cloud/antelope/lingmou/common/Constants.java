package cloud.antelope.lingmou.common;

import android.text.TextUtils;

import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.Utils;

import cloud.antelope.lingmou.R;

/**
 * @class Constants
 * @brief 定义客户端全局常量
 */
public final class Constants {

    /**
     * 后门Key
     */
    public static final String BACKDOOR_KEY = "backdoor_key";

    private static String getBaseUrl() {
        return SPUtils.getInstance().getString(BACKDOOR_KEY);
    }

    /**
     * 是否显示后门，允许用户修改服务器地址
     */
    public static final boolean SHOW_BACKDOOR = false;
    /**
     * 是否弹出过更新提示
     */
    public static  boolean SHOW_UPDATE_DIALOG = false;
    /**
     * 当前的versionCode
     */
    public static final String VERSION_CODE = "version_Code";

    /**
     * BASE_URL.
     */
    public static String LD_SERVER =
            TextUtils.isEmpty(getBaseUrl()) ? UrlConstant.BASE_URL + "service/" : getBaseUrl() + "service/";

    // 羚羊云对象存储的队列Id，看家护院使用1，朝阳群众使用0
    public static final int CHANNEL_ID = 0;

    // // 对象存储的主题
    public static final String OBJECT_STORAGE_SUBJECT =
            Utils.getContext().getResources().getString(R.string.storage_subject);

    /**
     * 事件存储的附件是否是缩略图，"0"，不是缩略图，"1"，是缩略图
     */
    public static final String EVENT_ATTACHMENT_NOT_THUMBNAIL = "0";

    /**
     * 事件存储的附件是否是缩略图，"0"，不是缩略图，"1"，是缩略图
     */
    public static final String EVENT_ATTACHMENT_IS_THUMBNAIL = "1";

    /**
     * 手机号码的长度PHONE_NUMBER_LENGHT
     */
    public static final int PHONE_NUMBER_LENGHT = 11;


    /**
     * 消息推送的类型，线索类型
     */
    public static final String PUSH_TYPE_CLUE = "cyqz_clue";

    /**
     * 消息推送
     */
    public static final String PUSH = "1";

    /**
     * 极光推送注册id
     */
    public static  String REGISTRATION_ID = "";
    /**
     * 消息静默推送
     */
    public static final String NOT_PUSH = "2";

    // 用户类型
    public static final String USER_TYPE = "4";

    // 验证码类型，注册
    public static final String SMS_CODE_REGISTER = "1";

    // 验证码类型，修改密码
    public static final String SMS_CODE_CHANGE_PWD = "2";

    public static final String CONFIG_OPERATION_ID = "config_operation_id";

    public static final String CONFIG_ORGANIZATION_ID = "config_organization_id";

    /// 首次启动标志键名(值为boolean类型）
    public static final String CONFIG_FIRST_START_APP = "first_start_app";

    public static final String CONFIG_LAST_LY_TOKEN = "last_ly_token";

    public static final String CONFIG_LAST_LOGIN_USER_ACCOUNT = "config_last_login_user_account";
    //头像
    public static final String CONFIG_LAST_USER_AVATAR = "config_last_user_avatar";
    //昵称
    public static final String CONFIG_LAST_USER_NICKNAME = "config_last_user_nickname";
    // 登录名
    public static final String CONFIG_LAST_USER_LOGIN_NAME = "config_last_user_login_name";
    // 登录名
    public static final String CONFIG_LAST_USER_LOGIN_PHONE = "config_last_user_login_phone";
    // 登录名
    public static final String CONFIG_LAST_USER_REAL_NAME = "config_last_user_real_name";

    // 是否登陆 标志位
    public static final String HAS_LOGIN = "has_login";

    //  消息开关
    public static final String CONFIG_MESSAGE_SWITCH = "config_message_switch";
    //  非WiFi播放开关
    public static final String CONFIG_NOT_WIFI_SWITCH = "config_not_wifi_switch";

    public static final String GOTO_MAIN = "goto_main";

    public static final String DEFAULT_IMAGE_SUFFIX = ".jpeg";
    public static final String DEFAULT_VIDEO_SUFFIX = ".mp4";
    public static final String DEFAULT_IMAGE_GIF_SUFFIX = ".gif";

    public static final String DEFAULT_VIDEO_MIME_TYPE = "video/mp4";
    public static final String DEFAULT_PICTURE_MIME_TYPE = "image/jpeg";

    // 申请的腾讯bugly appID正式版
    public static final String TECENT_BUGLY_APPID =
            Utils.getContext().getResources().getString(R.string.bugly_id);

    /**
     * shareSDK的APPID正式版
     */
    public static final String SHARE_SDK_APPID =
            Utils.getContext().getResources().getString(R.string.share_sdk_appid);

    /**
     * shareSDK的APPKEY正式版
     */
    public static final String SHARE_SDK_APP_KEY =
            Utils.getContext().getResources().getString(R.string.share_sdk_appkey);

    /**
     * 告警分享
     */
    public static final String CASE_SHARE = "1";

    /**
     * APP分享
     */
    public static final String APP_SHARE = "2";


    /**
     * 通知栏的通知id
     */
    public static final String NOTIFICATION_ID = "notification_id";

    /**
     * Token失效的字符串
     */
    public static final CharSequence TOKEN_INVALIDATE = "notification.token_invalid.login";

    public static final String EXTRA_NICKNAME = "extra_nickname";

    /**
     * 直播录像的录制存储路径
     */
    public static final String RECORD_PATH = "records/";

    /**
     * 直播录像的截图存储路径
     */
    public static final String SNAP_PATH = "snaps/";

    /**
     * 服务器时间
     */
    public static final String SERVER_DATE = "server_date";

    /**
     * 抓拍机
     */
    public static final int CAMERA_QIANG_JI = 100603;

    /**
     * 球机
     */
    public static final int CAMERA_QIU_JI = 100602;

    /**
     * 手机
     */
    public static final int CAMERA_SHOU_JI = 100605;

    /**
     * 普通摄像机
     */
    public static final int CAMERA_NORMAL_JI = 100604;

    /**
     * 摄像机在线
     */
    public static final int CAMERA_ONLINE = 1;

    /**
     * 摄像机离线
     */
    public static final int CAMERA_OFFLINE = 0;

    /**
     * 新的抓拍机
     */
    public static final int ORG_CAMERA_ZHUAPAIJI = 100603;

    /**
     * 新的手机
     */
    public static final int ORG_CAMERA_PHONE = 100605;

    /**
     * 新的普通摄像机
     */
    public static final int ORG_CAMERA_NORMAL = 100604;

    /**
     * 新的球机
     */
    public static final int ORG_CAMERA_QIUJI = 100602;

    /**
     * 大于此，为在线
     */
    public static final int ORG_CAMERA_OFF_LINE = 100501;


    /**
     * 收藏摄像机
     */
    public static final String CAMERA_FAVORITE = "1";

    /**
     * 取消收藏摄像机
     */
    public static final String CAMERA_NOT_FAVORITE = "0";

    /**
     * 单兵功能的cid
     */
    public static final String CONFIG_SOLO_CID = "config_solo_cid";

    /**
     * 单兵功能的sn
     */
    public static final String CONFIG_SOLO_SN = "config_solo_sn";
    /**
     * 单兵功能的id
     */
    public static final String CONFIG_SOLO_ID = "config_solo_id";

    /**
     * 单兵功能的brand
     */
    public static final String CONFIG_SOLO_BRAND = "config_solo_brand";

    /**
     * 是否支持单兵功能
     */
    public static final String CONFIG_IS_SOLO = "config_is_solo";

    /**
     * 支持单兵功能
     */
    public static final int CONFIG_SUPPORT_SOLO = 1;

    /**
     * 不支持单兵功能
     */
    public static final int CONFIG_NOT_SUPPORT_SOLO = 0;

    public static final String SELECT_CAMERA = "selectCamera";

    public static final String CONFIG_CASE_ID = "config_case_id";

    public static final String CONFIG_CLUE_ID = "config_clue_id";

    /**
     * 本地存储的案件的最近更新时间
     */
    public static final String CONFIG_LAST_CASE_TIME = "config_last_case_time";
    /**
     * 本地存储的线索的最近更新时间
     */
    public static final String CONFIG_LAST_CLUE_TIME = "config_last_clue_time";

    /**
     * 重大告警级别
     */
    public static final String CASE_LEVEL_IMPORT = "02";

    /**
     * 是否是封面图片.
     */
    public static final String IS_FRONT_COVER = "1";

    /**
     * 是否已回复评论，0 未回复，1 已回复
     */
    public static final String REPLY_HAS_ANSWERED = "1";

    /**
     * 是否已审核，0 未审核 1已审核
     */
    public static final String HAS_AUDIT = "1";

    /**
     * 是否已审核通过，0 审核未通过，1审核通过
     */
    public static final String HAS_AUDIT_PASS = "1";

    /**
     * 点赞
     */
    public static final String LIKE_IT = "1";

    /**
     * 取消点赞
     */
    public static final String UNLIKE_IT = "0";

    /**
     * 是否允许评论，0 不允许，1 允许，默认是1
     */
    public static final String IS_ALLOW_REPLY = "1";

    /**
     * 是否允许提交告警关联线索，0 不允许，1 允许，默认是1
     */
    public static final String IS_ALLOW_CASE_REPORT = "1";

    public static final String CASE_ID = "case_id";
    //举报类型
    public static final String EXTRA_REPORT_TYPE = "extra_report_type";

    /**
     * 是否置顶，0 不置顶，1 置顶，默认是0
     */
    public static final String OBJECT_STORAGE_IS_TOP = "0";

    /**
     * 是否需要审核，0 不需要，1 需要，默认是0
     */
    public static final String IS_ALLOW_AUDIT = "0";

    /**
     * 是否是有价值的线索
     */
    public static final String IS_VALUED_REPORT = "02";


    public static final String EXTRA_ADDR = "extra_location";
    public static final String EXTRA_LAT = "extra_lat";
    public static final String EXTRA_LON = "extra_lon";

    /**
     * 朝阳群众的token
     */
    public static final String CONFIG_CYQZ_TOKEN = "config_cyqz_token";

    /**
     * 智慧云眼token
     */
    public static final String ZHYY_TOKEN = "zhyy_token";
    /**
     * 设备的IMEI码
     */
    public static final String DEVICE_IMEI = "device_imei";

    /**
     * 羚羊事件上传的token
     */
    public static final String CONFIG_CYQZ_LY_TOKEN = "config_cyqz_ly_token";

    public static final String INTENT_SHOW_SUCCESS_DIALOG = "intent_show_success_dialog";

    /**
     * 线索的类型.
     * "11": "一般违法线索",
     * "12": "严重违法线索",
     * "13": "各类安全隐患",
     * "14": "其它"
     */
    public static final int TYPE_1 = 11;
    public static final int TYPE_2 = 12;
    public static final int TYPE_3 = 13;
    public static final int TYPE_4 = 14;

    public static final String EXTRA_START_FROM_CLUE = "extra_start_from_clue";

    /**
     * 火眼金睛截图后目录
     */
    public static final String CROP_PIC_PATH = "crop_img/";

    public static final String AFTER_LOGIN_SET_JPUSH_ALIAS_SUCCESS = "after_login_set_jpush_alias";

    /**
     * 收藏的JSON串
     */
    public static final String COLLECT_JSON = "collect_json";

    /**
     * 评论类型，1.关注人提交线索 2.新闻评论 3.新闻回复  4.线索回复
     */
    public static final String REPLY_TYPE_NEWS_COMMENT = "2";

    public static final String CONFIG_REPLY_ID = "config_reply_id";

    public static final String CONFIG_CONTENT_ID = "config_content_id";


    /**
     * 评论类型，1.关注人提交线索 2.新闻评论 3.新闻回复  4.线索回复
     */
    public static final String REPLY_TYPE_COMMENT_REPLY = "3";


    /**
     * 评论类型，1.关注人提交线索 2.新闻评论 3.新闻回复  4.线索回复
     */
    public static final String REPLY_TYPE_CLUE_REPLY = "4";

    /**
     * 是否有火眼金睛
     */
    public static final String PERMISSION_HAS_HYJJ = "permission_hyjj";
    /**
     * 是否有单兵
     */
    public static final String PERMISSION_HAS_SOLO = "permission_has_solo";
    /**
     * 是否有报警列表
     */
    public static final String PERMISSION_HAS_ALARM_LIST = "permission_alarm_list";
    /**
     * 是否有报警详情
     */
    public static final String PERMISSION_HAS_ALARM_DETAIL = "permission_alarm_detail";
    /**
     * 是否有人脸
     */
    public static final String PERMISSION_HAS_FACE = "permission_face";
    /**
     * 是否有人体
     */
    public static final String PERMISSION_HAS_BODY = "permission_body";
    /**
     * 是否有车辆
     */
    public static final String PERMISSION_HAS_CAR = "permission_car";
    /**
     * 是否有实时视频
     */
    public static final String PERMISSION_HAS_VIDEO_LIVE = "permission_video_live";
    /**
     * 是否有历史视频
     */
    public static final String PERMISSION_HAS_VIDEO_HISTORY = "permission_video_history";
    /**
     * 是否有截图
     */
    public static final String PERMISSION_HAS_VIDEO_CAPTURE = "permission_video_capture";
    /**
     * 是否有人员追踪
     */
    public static final String PERMISSION_HAS_PERSON_TRACKING = "permission_person_tracking";
    /**
     * 是否有广播
     */
    public static final String PERMISSION_HAS_BROADCAST = "permission_broadcast";
    /**
     * 是否有外来人员
     */
    public static final String PERMISSION_OUTSIDE_PERSON = "permission_outside_person";
    /**
     * 是否有专网套件
     */
    public static final String PERMISSION_PRIVATE_NETWORK_SUITE = "permission_private_network_suite";
    /**
     * 是否有事件提醒
     */
    public static final String PERMISSION_EVENT_REMIND = "permission_event_remind";
    /**
     * 是否有重点人员
     */
    public static final String PERMISSION_KEY_PERSON = "permission_key_person";

    /**
     * 广播权限code码
     */
    public static final int TYPE_PERMISSION_BROADCAST_CODE = 2010701;
    /**
     * 以图搜图权限code码
     */
    public static final int TYPE_PERMISSION_HYJJ_CODE = 2010301;

    /**
     * 报警列表code码
     */
    public static final int TYPE_PERMISSION_ALARM_LIST_CODE = 1060101;

    /**
     * 报警详情code码
     */
    public static final int TYPE_PERMISSION_ALARM_DETAIL_CODE = 1060102;

    /**
     * 人脸图库code码
     */
    public static final int TYPE_PERMISSION_FACE_CODE =2010501;
    /**
     * 人员追踪code码
     */
    public static final int TYPE_PERMISSION_TRACKING_CODE =2010401;

    /**
     * 人体图库code码
     */
    public static final int TYPE_PERMISSION_BODY_CODE = 2010601;

    /**
     * 实时视频Code码
     */
    public static final int TYPE_PERMISSION_VIDEO_LIVE_CODE = 2010201;

    /**
     * 历史视频code码
     */
    public static final int TYPE_PERMISSION_VIDEO_HISTORY_CODE = 1010102;

    /**
     * 视频截屏
     */
    public static final int TYPE_PERMISSION_VIDEO_CAPTURE_CODE = 1010105;
    /**
     * 外来人员
     */
    public static final int TYPE_PERMISSION_OUTSIDE_PERSON_CODE = 1060403;
    /**
     * 专网套件
     */
    public static final int TYPE_PERMISSION_PRIVATE_NETWORK_SUITE_CODE = 1061201;
    /**
     * 事件提醒
     */
    public static final int TYPE_PERMISSION_EVENT_REMIND_CODE = 1100101;
    /**
     * 车辆图库
     */
    public static final int TYPE_PERMISSION_CAR_CODE = 2010801;
    /**
     * 重点人员
     */
    public static final int TYPE_PERMISSION_KEY_PERSON_CODE = 1060103;

    /**
     * BASE URL
     */
    public static final String URL_BASE = "url_base";


    /**
     * 对象上传
     */
    public static final String URL_OBJECT_STORAGE = "url_object_storage";

    /**
     * 事件上传
     */
    public static final String URL_EVENT_STORAGE = "url_event_storage";

    /**
     * 单兵直播
     */
    public static final String URL_SOLDIER_LIVE = "url_soldier_live";

    /**
     * 获取录像数据
     */
    public static final String URL_RECORD = "url_record";

    /**
     * 录像播放的头Url
     */
    public static final String URL_RECORD_PLAY = "url_record_play";

    /**
     * 平台的名字
     */
    public static final String URL_PLATFORM_NAME = "url_platform_name";

    /**
     * Baseurl 的key
     */
    public static final String KEY_BASE_URL = "baseUrl";

    /**
     * 地图样式的文件
     */
    public static final String MAP_STYLE_FILE = "map_style/";

}

