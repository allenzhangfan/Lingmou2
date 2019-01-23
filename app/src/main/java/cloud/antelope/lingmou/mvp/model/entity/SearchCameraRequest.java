package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;

/**
 * 作者：安兴亚
 * 创建日期：2017/11/21
 * 邮箱：anxingya@lingdanet.com
 * 描述：TODO
 */

public class SearchCameraRequest implements Serializable {

    /**
     * key : 羚羊
     * type : camera
     * count : 30
     * offset : 0
     * isOnline : 1
     */

    private String key;
    private String type;
    private String count;
    private String offset;
    private String isOnline;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public String getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(String isOnline) {
        this.isOnline = isOnline;
    }
}
