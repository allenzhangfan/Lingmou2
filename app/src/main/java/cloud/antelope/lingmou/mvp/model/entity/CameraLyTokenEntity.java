package cloud.antelope.lingmou.mvp.model.entity;

/**
 * 作者：陈新明
 * 创建日期：2017/7/12
 * 邮箱：
 * 描述：TODO
 */

public class CameraLyTokenEntity extends LmBaseData{

    /**
     * cameraId : A5A99AF534A414CCE3BC7D37F627A006
     * token : 1003555_0_1492313854_2adb4e7c5d44ac9f130df89deb022b71
     */

    private String cameraId;
    private String token;

    public String getCameraId() {
        return cameraId;
    }

    public void setCameraId(String cameraId) {
        this.cameraId = cameraId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
