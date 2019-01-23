package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;

/**
 * 作者：安兴亚
 * 创建日期：2017/07/26
 * 邮箱：anxingya@lingdanet.com
 * 描述：TODO
 */

public class CommentLikeEntity implements Serializable {

    private String columnId;
    private String contentId;
    private String addOrRemove;

    private String operationCenterId;


    public String getOperationCenterId() {
        return operationCenterId;
    }

    public void setOperationCenterId(String operationCenterId) {
        this.operationCenterId = operationCenterId;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getAddOrRemove() {
        return addOrRemove;
    }

    public void setAddOrRemove(String addOrRemove) {
        this.addOrRemove = addOrRemove;
    }
}
