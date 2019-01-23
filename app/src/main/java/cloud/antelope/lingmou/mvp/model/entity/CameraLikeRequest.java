package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;

/**
 * 作者：安兴亚
 * 创建日期：2017/11/21
 * 邮箱：anxingya@lingdanet.com
 * 描述：TODO
 */

public class CameraLikeRequest implements Serializable {

    /**
     * userId : 123456
     * cameraId : 330
     * isLike : 1
     */

    private String userId;
    private Long cameraId;
    private String isLike;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getCameraId() {
        return cameraId;
    }

    public void setCameraId(Long cameraId) {
        this.cameraId = cameraId;
    }

    public String getIsLike() {
        return isLike;
    }

    public void setIsLike(String isLike) {
        this.isLike = isLike;
    }
}
