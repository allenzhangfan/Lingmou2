package cloud.antelope.lingmou.mvp.model.entity;

public class CollectRequest {

    /**
     * id : 101000000002
     * deviceId : 72057602147499526
     * deviceName : 前台大厅-15楼
     * favoritesType : 1
     * imageUrl : https://jxsr-oss1.antelopecloud.cn/files?obj_id=5b921b37800040020430388c&access_token=2147500034_3356491776_1567837843_3fecc90d249f768c1ccbf080adbe6ffe
     * faceImgUrl :
     */

    private Long id;
    private Long deviceId;
    private String deviceName;
    private Integer favoritesType;//1：截图<br />2：人脸<br />3：人体
    private String imageUrl;
    private String faceImgUrl;
    private String faceRect;
    private String featureId;

    public String getFeatureId() {
        return featureId;
    }

    public void setFeatureId(String featureId) {
        this.featureId = featureId;
    }

    public String getFaceRect() {
        return faceRect;
    }

    public void setFaceRect(String faceRect) {
        this.faceRect = faceRect;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getFavoritesType() {
        return favoritesType;
    }

    public void setFavoritesType(int favoritesType) {
        this.favoritesType = favoritesType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getFaceImgUrl() {
        return faceImgUrl;
    }

    public void setFaceImgUrl(String faceImgUrl) {
        this.faceImgUrl = faceImgUrl;
    }
}
