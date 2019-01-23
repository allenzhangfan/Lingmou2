package cloud.antelope.lingmou.mvp.model.entity;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 作者：安兴亚
 * 创建日期：2017/11/14
 * 邮箱：anxingya@lingdanet.com
 * 描述：TODO
 */

public class SearchTextEntity extends DataSupport implements Serializable {

    private String name;
    private int searchTimes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSearchTimes() {
        return searchTimes;
    }

    public void setSearchTimes(int searchTimes) {
        this.searchTimes = searchTimes;
    }
}
