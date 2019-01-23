package cloud.antelope.lingmou.mvp.model.entity;

public class CollectionListRequest {
    private Integer favoritesType;// 1：截图2：人脸3：人体
    private Integer pageSize;// 单页条数（默认20）
    private Integer pageNo;// 页码（默认1）

    public CollectionListRequest(){  }

    public CollectionListRequest(Integer favoritesType, Integer pageSize, Integer pageNo) {
        this.favoritesType = favoritesType;
        this.pageSize = pageSize;
        this.pageNo = pageNo;
    }

    public Integer getFavoritesType() {
        return favoritesType;
    }

    public void setFavoritesType(Integer favoritesType) {
        this.favoritesType = favoritesType;
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
