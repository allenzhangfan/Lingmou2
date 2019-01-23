package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;

/**
 * Created by ChenXinming on 2017/11/20.
 * description:
 */

public class FeedbackRequest implements Serializable {

    /**
     * userId : 123456
     * phoneNo : 13300004444
     * content : 反馈
     */

    private String userId;
    private String phoneNo;
    private String content;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
