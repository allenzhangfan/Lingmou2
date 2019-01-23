package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;

/**
 * 作者：陈新明
 * 创建日期：2018/05/18
 * 邮箱：chenxinming@antelop.cloud
 * 描述：TODO
 */
public class GetKeyStoreEntity implements Serializable {

    /**
     * id : 100100000236
     * userId : 101000000248
     * storeKey : MY_GROUP
     * storeValue : {"groups":["bbb","ccc"],"sets":["bbb:538379377/安徽马鞍山电信大门人行入口","ccc:538447261/北郊工业大道278号龙鼎商务宾馆","ccc:538447002/北郊工业大道88号红湾KTV会所1"]}
     * lastUpdateTime : 1526006908000
     */

    private String id;
    private String userId;
    private String storeKey;
    private String storeValue;
    private String lastUpdateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStoreKey() {
        return storeKey;
    }

    public void setStoreKey(String storeKey) {
        this.storeKey = storeKey;
    }

    public String getStoreValue() {
        return storeValue;
    }

    public void setStoreValue(String storeValue) {
        this.storeValue = storeValue;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
