package cloud.antelope.lingmou.mvp.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * 作者：陈新明
 * 创建日期：2018/07/10
 * 邮箱：chenxinming@antelop.cloud
 * 描述：首页选择的BaseUrl
 */
public class UrlEntity implements Parcelable {

    /**
     * platformName : 江西
     * serverUrl : http://jxsr-eye.antelopecloud.cn
     * objectStorageUrl : https://jxsr-oss1.antelopecloud.cn
     * eventStorageUrl : https://osstest.topvdn.com
     * soldierLiveUrl : http://jxsr-api.antelopecloud.cn
     * recordUrl : https://jxsr-api.antelopecloud.cn
     * recordUrl : topvdn://dgdx-api.antelopecloud.cn
     */

    private String platformName;
    private String serverUrl;
    private String objectStorageUrl;
    private String eventStorageUrl;
    private String soldierLiveUrl;
    private String recordUrl;
    private String recordPlayUrl;

    protected UrlEntity(Parcel in) {
        platformName = in.readString();
        serverUrl = in.readString();
        objectStorageUrl = in.readString();
        eventStorageUrl = in.readString();
        soldierLiveUrl = in.readString();
        recordUrl = in.readString();
        recordPlayUrl = in.readString();
    }

    public static final Creator<UrlEntity> CREATOR = new Creator<UrlEntity>() {
        @Override
        public UrlEntity createFromParcel(Parcel in) {
            return new UrlEntity(in);
        }

        @Override
        public UrlEntity[] newArray(int size) {
            return new UrlEntity[size];
        }
    };

    public String getRecordPlayUrl() {
        return recordPlayUrl;
    }

    public void setRecordPlayUrl(String recordPlayUrl) {
        this.recordPlayUrl = recordPlayUrl;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getObjectStorageUrl() {
        return objectStorageUrl;
    }

    public void setObjectStorageUrl(String objectStorageUrl) {
        this.objectStorageUrl = objectStorageUrl;
    }

    public String getEventStorageUrl() {
        return eventStorageUrl;
    }

    public void setEventStorageUrl(String eventStorageUrl) {
        this.eventStorageUrl = eventStorageUrl;
    }

    public String getSoldierLiveUrl() {
        return soldierLiveUrl;
    }

    public void setSoldierLiveUrl(String soldierLiveUrl) {
        this.soldierLiveUrl = soldierLiveUrl;
    }

    public String getRecordUrl() {
        return recordUrl;
    }

    public void setRecordUrl(String recordUrl) {
        this.recordUrl = recordUrl;
    }

    @Override
    public String toString() {
        return platformName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(platformName);
        dest.writeString(serverUrl);
        dest.writeString(objectStorageUrl);
        dest.writeString(eventStorageUrl);
        dest.writeString(soldierLiveUrl);
        dest.writeString(recordUrl);
        dest.writeString(recordPlayUrl);
    }
}
