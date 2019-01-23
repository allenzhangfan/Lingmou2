package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;

/**
 * 作者：陈新明
 * 创建日期：2017/7/17
 * 邮箱：
 * 描述：TODO
 */

public class PushRequest implements Serializable {
    private String key;
    private String value; //1：通知；2：静默

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
