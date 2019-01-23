package cloud.antelope.lingmou.mvp.model.entity;

public class CollectionListBean {

    /**
     * id : 101000000005
     * deviceId : 72057602147499526
     * deviceName : 前台大厅-15楼
     * userId : 101000000245
     * imageUrl : https://jxsr-oss1.antelopecloud.cn/files?obj_id=5b921b37800040020430388c&access_token=2147500034_3356491776_1567837843_3fecc90d249f768c1ccbf080adbe6ff8
     * faceImgUrl : https://jxsr-oss1.antelopecloud.cn/files?obj_id=5b921b37800040020430388c&access_token=2147500034_3356491776_1567837843_3fecc90d249f768c1ccbf080adbe6ffe
     * favoritesType : 1
     * createTime : 1537150316142
     */

    private String id;
    private String deviceId;
    private String deviceName;
    private String userId;
    private String imageUrl;
    private String faceImgUrl;
    private int favoritesType;
    private String createTime;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public int getFavoritesType() {
        return favoritesType;
    }

    public void setFavoritesType(int favoritesType) {
        this.favoritesType = favoritesType;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
