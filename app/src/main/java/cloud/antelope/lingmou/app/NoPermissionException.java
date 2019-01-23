package cloud.antelope.lingmou.app;

/**
 * 作者：陈新明
 * 创建日期：2018/01/27
 * 邮箱：chenxinming@antelop.cloud
 * 描述：没有权限的Exception
 */

public class NoPermissionException extends Exception {
    public NoPermissionException(String message) {
        super(message);
    }

}
