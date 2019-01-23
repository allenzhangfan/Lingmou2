package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 作者：安兴亚
 * 创建日期：2017/07/10
 * 邮箱：anxingya@lingdanet.com
 * 描述：TODO
 */

public class ContentListEntity<I> implements Serializable {

    private int total;
    private int pageSize;
    private int currentPage;
//    private List<ColumnFieldEntity> columnFields;
    private List<I> list;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

//    public List<ColumnFieldEntity> getColumnFields() {
//        return columnFields;
//    }
//
//    public void setColumnFields(List<ColumnFieldEntity> columnFields) {
//        this.columnFields = columnFields;
//    }

    public List<I> getList() {
        return list;
    }

    public void setList(List<I> list) {
        this.list = list;
    }
}
