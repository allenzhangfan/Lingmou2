package cloud.antelope.lingmou.mvp.model.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 作者：安兴亚
 * 创建日期：2017/07/10
 * 邮箱：anxingya@lingdanet.com
 * 描述：事件上传的图片或视频实体
 */

public class AttachmentBean implements Serializable {

    /**
     * form_field : first_object
     * key : /1/upload.txt
     * area_id : 0
     * metadata : {"mimeType":"vidoe/mp4","size":"142324"}
     * url :
     * file_name :
     * expireType : 1
     */
    private String form_field;
    private String key;
    @SerializedName("area_id")
    private int area_id;
    private AttachmentBean.MetadataBean metadata;
    private String url;
    @SerializedName("file_name")
    private String file_name;
    @SerializedName("expiretype")
    private int expireType;

    public String getFormField() {
        return form_field;
    }

    public void setFormField(String formField) {
        this.form_field = formField;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getAreaId() {
        return area_id;
    }

    public void setAreaId(int areaId) {
        this.area_id = areaId;
    }

    public MetadataBean getMetadata() {
        return metadata;
    }

    public void setMetadata(MetadataBean metadata) {
        this.metadata = metadata;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return file_name;
    }

    public void setFileName(String fileName) {
        this.file_name = fileName;
    }

    public int getExpireType() {
        return expireType;
    }

    public void setExpireType(int expireType) {
        this.expireType = expireType;
    }

    public static class MetadataBean {
        /**
         * mimeType : vidoe/mp4
         * size : 142324
         * isThumbnail : "1"/"0"
         */

        private String mimeType;
        private String size;
        private String isThumbnail;
        private String filePath;
        private String isFrontCover;

        public String getMimeType() {
            return mimeType;
        }

        public void setMimeType(String mimeType) {
            this.mimeType = mimeType;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String isThumbnail() {
            return isThumbnail;
        }

        public void setThumbnail(String thumbnail) {
            isThumbnail = thumbnail;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public String isFrontCover() {
            return isFrontCover;
        }

        public void setFrontCover(String isFrontCover) {
            this.isFrontCover = isFrontCover;
        }
    }
}
