package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;

/**
 * 作者：安兴亚
 * 创建日期：2017/07/04
 * 邮箱：anxingya@lingdanet.com
 * 描述：内容列表请求实体
 */

public class ContentRequestEntity implements Serializable {

    /**
     * 栏目编号
     */
    private String columnId;

    /**
     * 页码号
     */
    private int page;

    /**
     * 每页数量
     */
    private int pageSize;

    /**
     * 运营中心编号
     */
    private String operationCenterId;

    /**
     * 获取新闻类型，06表战果，不传为其余新闻
     */

    /**
     * 内容编号
     */
    private String contentId;

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    private String caseType;

    public String getCaseType() {
        return caseType;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
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

    public String getOperationCenterId() {
        return operationCenterId;
    }

    public void setOperationCenterId(String operationCenterId) {
        this.operationCenterId = operationCenterId;
    }
}
