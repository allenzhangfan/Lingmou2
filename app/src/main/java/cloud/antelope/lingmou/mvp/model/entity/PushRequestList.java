package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 作者：陈新明
 * 创建日期：2017/7/17
 * 邮箱：
 * 描述：TODO
 */

public class PushRequestList implements Serializable {
    private List<PushRequest> keyValues;

    public List<PushRequest> getKeyValues() {
        return keyValues;
    }

    public void setKeyValues(List<PushRequest> keyValues) {
        this.keyValues = keyValues;
    }
}
