package cloud.antelope.lingmou.mvp.model.entity;

/**
 * 作者：陈新明
 * 创建日期：2018/09/17
 * 邮箱：chenxinming@antelop.cloud
 * 描述：TODO
 */
public class CameraDevicesRequest {

    /**
     * deviceType : 100602
     * deviceState : 2
     * pageNo : 1
     * pageSize : 20
     */

    private Integer deviceType;
    private String deviceName;
    private Integer deviceState;
    private int page;
    private int pageSize;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public Integer getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(Integer deviceType) {
        this.deviceType = deviceType;
    }

    public Integer getDeviceState() {
        return deviceState;
    }

    public void setDeviceState(Integer deviceState) {
        this.deviceState = deviceState;
    }

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
}
