package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;

/**
 * 作者：陈新明
 * 创建日期：2018/05/09
 * 邮箱：chenxinming@antelop.cloud
 * 描述：TODO
 */
public class PlayEntity implements Serializable {
    private String token;
    private CameraNewStreamEntity mStreamEntity;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public CameraNewStreamEntity getStreamEntity() {
        return mStreamEntity;
    }

    public void setStreamEntity(CameraNewStreamEntity streamEntity) {
        mStreamEntity = streamEntity;
    }
}
