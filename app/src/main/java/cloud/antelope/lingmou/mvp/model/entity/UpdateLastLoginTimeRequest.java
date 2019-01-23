package cloud.antelope.lingmou.mvp.model.entity;

public class UpdateLastLoginTimeRequest {

    /**
     * deviceUniqueId : F630936E-A333-4287-BC86-70C95E0BC830
     */

    private String deviceUniqueId;

    public UpdateLastLoginTimeRequest(String deviceUniqueId) {
        this.deviceUniqueId = deviceUniqueId;
    }

    public String getDeviceUniqueId() {
        return deviceUniqueId;
    }

    public void setDeviceUniqueId(String deviceUniqueId) {
        this.deviceUniqueId = deviceUniqueId;
    }
}
