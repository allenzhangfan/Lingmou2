package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;

/**
 * 作者：安兴亚
 * 创建日期：2017/07/26
 * 邮箱：anxingya@lingdanet.com
 * 描述：TODO
 */

public class LikeCountEntity implements Serializable {

    private long count;
    private String userIds;

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }


    public String getUserIds() {
        return userIds;
    }

    public void setUserIds(String userIds) {
        this.userIds = userIds;
    }
}
