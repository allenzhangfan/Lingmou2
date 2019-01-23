package cloud.antelope.lingmou.mvp.model.entity;

public class TrackingPersonRequest {
    private Integer pageNo;
    private Integer pageSize;

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }
}
