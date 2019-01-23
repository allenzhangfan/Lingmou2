package cloud.antelope.lingmou.mvp.model.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 作者：安兴亚
 * 创建日期：2017/07/07
 * 邮箱：anxingya@lingdanet.com
 * 描述：对象存储的实体类
 */

public class ObjectEntity implements Serializable {

    @SerializedName("obj_id")
    private String objId;
    private String name;
    @SerializedName("c_id")
    private long cid;
    @SerializedName("app_id")
    private String appId;
    @SerializedName("fileId")
    private String file_id;
    private int timestamp;
    @SerializedName("fileSize")
    private int file_size;
    private MetadataBean metadata;
    @SerializedName("expire_type")
    private int expireType;
    @SerializedName("areaId")
    private int area_id;

    public String getObjId() {
        return objId;
    }

    public void setObjId(String objId) {
        this.objId = objId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCid() {
        return cid;
    }

    public void setCid(long cid) {
        this.cid = cid;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getFile_id() {
        return file_id;
    }

    public void setFile_id(String file_id) {
        this.file_id = file_id;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public int getFile_size() {
        return file_size;
    }

    public void setFile_size(int file_size) {
        this.file_size = file_size;
    }

    public MetadataBean getMetadata() {
        return metadata;
    }

    public void setMetadata(MetadataBean metadata) {
        this.metadata = metadata;
    }

    public int getExpireType() {
        return expireType;
    }

    public void setExpireType(int expireType) {
        this.expireType = expireType;
    }

    public int getArea_id() {
        return area_id;
    }

    public void setArea_id(int area_id) {
        this.area_id = area_id;
    }

    public static class MetadataBean {
        /**
         * mimeType : image/jpeg
         */

        private String mimeType;

        public String getMimeType() {
            return mimeType;
        }

        public void setMimeType(String mimeType) {
            this.mimeType = mimeType;
        }
    }
}
