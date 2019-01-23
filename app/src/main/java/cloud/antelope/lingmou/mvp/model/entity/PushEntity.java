package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;

/**
 * 作者：陈新明
 * 创建日期：2017/7/17
 * 邮箱：
 * 描述：TODO
 */

public class PushEntity implements Serializable {


    /**
     * id : 6CFCB2D5EB4D4DB7B6C6EEE2AB335E21
     * userId : 11FB5608D9D24788A0392963725E55F0
     * msgKey : cyqz_clue
     * msgValue : 1
     * createTime : 1501497499937
     */

    private String id;
    private String userId;
    private String msgKey;
    private String msgValue;
    private long createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMsgKey() {
        return msgKey;
    }

    public void setMsgKey(String msgKey) {
        this.msgKey = msgKey;
    }

    public String getMsgValue() {
        return msgValue;
    }

    public void setMsgValue(String msgValue) {
        this.msgValue = msgValue;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
