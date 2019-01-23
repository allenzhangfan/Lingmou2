package cloud.antelope.lingmou.mvp.model.entity;

public class DeployListRequest {
    private Integer statusType;// 任务状态<br />0：暂停<br />1：运行中<br />2：未运行<br />3：已过期    4：全部                                             |
    private Integer pageSize;// 单页条数（默认20）
    private Integer pageNo;// 页码（默认1）
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStatusType() {
        return statusType;
    }

    public void setStatusType(Integer statusType) {
        this.statusType = statusType;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }
}
