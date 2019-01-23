package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;

/**
 * 作者：陈新明
 * 创建日期：2018/05/18
 * 邮箱：chenxinming@antelop.cloud
 * 描述：TODO
 */
public class GetKeyStoreRequest implements Serializable{

    /**
     * userId : 101000000248
     * storeKey : MY_GROUP
     */

    private String userId;
    private String storeKey;

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
}
