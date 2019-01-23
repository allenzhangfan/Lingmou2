package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;

/**
 * 作者：陈新明
 * 创建日期：2018/05/08
 * 邮箱：chenxinming@antelop.cloud
 * 描述：TODO
 */
public class OrgCameraRequest implements Serializable {

    /**
     * page : 1
     * pageSize : 10000
     * orgIds : [100101001741]
     */

    private int page;
    private int pageSize;
    private String[] orgIds;

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

    public String[] getOrgIds() {
        return orgIds;
    }

    public void setOrgIds(String[] orgIds) {
        this.orgIds = orgIds;
    }
}
