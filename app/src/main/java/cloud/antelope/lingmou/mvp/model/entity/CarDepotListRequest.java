package cloud.antelope.lingmou.mvp.model.entity;

import java.util.List;

public class CarDepotListRequest {

    /**
     * devices : []
     * endTime : 1543198149074
     * page : 1
     * pageSize : 15
     * plateColor : 117754
     * plateNo : D86665
     * startTime : 1542988800000
     * vehicleBrands : [117111]
     * vehicleClasses : [117672]
     * vehicleColor : 117702
     */

    public Long endTime;
    public int page;
    public int pageSize;
    public Long plateColor;
    public Long vehicleClass;
    public Long vehicleBrand;
    public String plateNo;
    public Long startTime;
    public Long vehicleColor;
    public List<String> devices;
    public List<Long> vehicleBrands;
    public List<Long> vehicleClasses;
    public List<String> plateNos;

}
