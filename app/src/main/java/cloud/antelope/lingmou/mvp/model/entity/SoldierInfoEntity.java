package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;

/**
 * 作者：陈新明
 * 创建日期：2018/05/22
 * 邮箱：chenxinming@antelop.cloud
 * 描述：TODO
 */
public class SoldierInfoEntity implements Serializable{
    private Long manufacturerDeviceId;
    private String sn;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getManufacturerDeviceId() {
        return manufacturerDeviceId;
    }

    public void setManufacturerDeviceId(Long manufacturerDeviceId) {
        this.manufacturerDeviceId = manufacturerDeviceId;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }
}
