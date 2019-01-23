package cloud.antelope.lingmou.app;

/**
 * 作者：陈新明
 * 创建日期：2018/01/27
 * 邮箱：chenxinming@antelop.cloud
 * 描述：返回200，但是返回的data是null的情况
 */

public class SuccessNullException extends Exception {
    public SuccessNullException(String message) {
        super(message);
    }

}
