package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;

/**
 * 作者：安兴亚
 * 创建日期：2017/09/26
 * 邮箱：anxingya@lingdanet.com
 * 描述：TODO
 */

public class ShareEntity implements Serializable {


    /**
     * contentId : 10
     * type : 0
     */

    private String contentId;
    private String type;

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
