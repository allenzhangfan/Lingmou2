package cloud.antelope.lingmou.app.utils;

/**
 * 作者：陈新明
 * 创建日期：2018/08/09
 * 邮箱：chenxinming@antelop.cloud
 * 描述：TODO
 */
public class PublicUtils {
    private static final long SECONDS = 60L;
    private static final long MINUTE = 60* 60L;
    private static final long HOUR = 24 * 60 * 60L;
    private static final long DAY = 30 * 24 * 60 * 60L;
    private static final long MONTH = 12 * 30 * 24 * 60 * 60L;

    public static String caculateTimes(long timeStamps) {
        long time = timeStamps / 1000;
        if (time <= SECONDS) {
            return time + "秒前";
        } else if (time <= MINUTE) {
            return (time / SECONDS) + "分钟前";
        } else if (time <= HOUR) {
            return (time / MINUTE) + "小时前";
        } else if (time < DAY) {
            return (time / HOUR) + "天前";
        } else if (time < MONTH) {
            return (time / DAY) + "月前";
        } else {
            return (time / MONTH) + "年前";
        }
    }
}
