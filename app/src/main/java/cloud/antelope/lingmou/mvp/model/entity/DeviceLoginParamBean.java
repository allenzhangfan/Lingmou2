package cloud.antelope.lingmou.mvp.model.entity;

import com.google.gson.Gson;

/**
 * Created by liaolei on 2017/11/14.
 */
public class DeviceLoginParamBean {
    int cid;//羚分配的设备CID
    String sn;//序列号
    String brand;//设备品牌
    String signature;//登录签名，具体待实现

    public DeviceLoginParamBean(int cid, String sn, String brand, String signature) {
        this.cid = cid;
        this.sn = sn;
        this.brand = brand;
        this.signature = signature;
    }

    public String getJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
