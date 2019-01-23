package cloud.antelope.lingmou.mvp.model.entity;

import java.util.List;

/**
 * 作者：陈新明
 * 创建日期：2018/09/17
 * 邮箱：chenxinming@antelop.cloud
 * 描述：TODO
 */
public class BaseListEntity<T> {
    private int pageNum;
    private int pageSize;
    private int total;
    private int pages;
    private List<T> list;

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
