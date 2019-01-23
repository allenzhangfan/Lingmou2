package cloud.antelope.lingmou.mvp.model.entity;

public class CarDepotEntity {

    /**
     * id : 58883
     * deviceId : 538452919
     * deviceName : 车辆抓拍－禾市镇政府门口
     * picUrl : {"nearSceneUrl":"https://jxsr-oss1.antelopecloud.cn/files2/538452919/5bfe476b201823b70410fd44?access_token=538452919_0_1574930939_a0b638d0a20d0c69f7ba49f2732f0d0d&key=5bfe476b201823b70410fd44","smallPicUrl":"https://jxsr-oss1.antelopecloud.cn/files?access_token=2147500036_3356491776_1546271999_90d889b7a4a12c0f0e3fa7bb8816b2ce&key=%2f1543391083000002_1","bigPicUrl":"https://jxsr-oss1.antelopecloud.cn/files2/538452919/5bfe476b201823b70410fd44?access_token=538452919_0_1574930939_a0b638d0a20d0c69f7ba49f2732f0d0d&key=5bfe476b201823b70410fd44"}
     * vehicleRect : {"leftTopX":9,"leftTopY":652,"rightBtmX":516,"rightBtmY":1061,"width":507,"height":409}
     * plateNoRect : null
     * plateNo : null
     * plateColor : null
     * vehicleColor : 117702
     * vehicleBrand : 117101
     * passTime : 1543391083000
     * vehicleClass : 117666
     * longitude : 114.691
     * latitude : 26.8565
     */

    public int id;
    public int deviceId;
    public String deviceName;
    public PicUrlBean picUrl;
    public CarRect vehicleRect;
    public CarRect plateNoRect;
    public String plateNo;
    public Long plateColor;
    public int vehicleColor;
    public int vehicleBrand;
    public Long passTime;
    public int vehicleClass;
    public double longitude;
    public double latitude;

    public boolean selected;

    public static class PicUrlBean {
        /**
         * nearSceneUrl : https://jxsr-oss1.antelopecloud.cn/files2/538452919/5bfe476b201823b70410fd44?access_token=538452919_0_1574930939_a0b638d0a20d0c69f7ba49f2732f0d0d&key=5bfe476b201823b70410fd44
         * smallPicUrl : https://jxsr-oss1.antelopecloud.cn/files?access_token=2147500036_3356491776_1546271999_90d889b7a4a12c0f0e3fa7bb8816b2ce&key=%2f1543391083000002_1
         * bigPicUrl : https://jxsr-oss1.antelopecloud.cn/files2/538452919/5bfe476b201823b70410fd44?access_token=538452919_0_1574930939_a0b638d0a20d0c69f7ba49f2732f0d0d&key=5bfe476b201823b70410fd44
         */

        public String nearSceneUrl;
        public String smallPicUrl;
        public String bigPicUrl;

    }

}
