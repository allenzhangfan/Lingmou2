package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;

/**
 * 作者：安兴亚
 * 创建日期：2017/07/26
 * 邮箱：anxingya@lingdanet.com
 * 描述：TODO
 */

public class QueryReplyPageEntity implements Serializable {
    private String operationCenterId;
    private String columnId;
    private String contentId;
    private int page;
    private int pageSize;
    private String replyAuditState;
    private String replyAuditResult;
    private String type;
    private String replyId;

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

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getReplyAuditState() {
        return replyAuditState;
    }

    public void setReplyAuditState(String replyAuditState) {
        this.replyAuditState = replyAuditState;
    }

    public String getReplyAuditResult() {
        return replyAuditResult;
    }

    public void setReplyAuditResult(String replyAuditResult) {
        this.replyAuditResult = replyAuditResult;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReplyId() {
        return replyId;
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }
}
