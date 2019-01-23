package cloud.antelope.lingmou.mvp.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.amap.api.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.List;

import cloud.antelope.lingmou.app.utils.map.ClusterItem;
import cloud.antelope.lingmou.common.Constants;

/**
 * 作者：陈新明
 * 创建日期：2018/05/08
 * 邮箱：chenxinming@antelop.cloud
 * 描述：TODO
 */
public class OrgCameraEntity extends DataSupport implements ClusterItem ,SearchBean,Parcelable{

    /**
     * id : 72057600538379377
     * deviceName : 安徽马鞍山电信大门人行入口
     * deviceType : 100604
     * deviceManufacturer : null
     * manufacturerDeviceType : 103401
     * simpPy : ahmasdxdmrxrk
     * fullPy : anhuimaanshandianxindamenrenxingrukou
     * organizationIds : ["100101000443","100101000305"]
     * operationCenterIds : ["100100000227","100100000148"]
     * manufacturerDeviceId : 538379377
     * longitude : 118.50866585969925
     * latitude : 31.67873629208581
     * deviceState : 100505
     * sn : 137898605883
     * deviceData : 1
     * isIdleDeal : 0
     * "cover_url": "http://jxsr-api.antelopecloud.cn/v2/snapshots/538378251/cover?client_token=538378251_0_1533118660_2539fa7f517875c9f4b49da80a0c3b09"
     * "rtmp_url": "rtmp://da5f2426.server.antelopecloud.cn:1935/"
     * viewTimes:观看次数
     */

    @SerializedName("id")
    private String cameraId;
    private String state;

    public  OrgCameraEntity(){}
    protected OrgCameraEntity(Parcel in) {
        cameraId = in.readString();
        state = in.readString();
        deviceName = in.readString();
        if (in.readByte() == 0) {
            deviceType = null;
        } else {
            deviceType = in.readLong();
        }
        deviceTypeName = in.readString();
        if (in.readByte() == 0) {
            longitude = null;
        } else {
            longitude = in.readDouble();
        }
        if (in.readByte() == 0) {
            latitude = null;
        } else {
            latitude = in.readDouble();
        }
        if (in.readByte() == 0) {
            manufacturerDeviceId = null;
        } else {
            manufacturerDeviceId = in.readLong();
        }
        sn = in.readString();
        deviceData = in.readInt();
        if (in.readByte() == 0) {
            deviceState = null;
        } else {
            deviceState = in.readLong();
        }
        coverUrl = in.readString();
        playUrl = in.readString();
        installationLocationDetail = in.readString();
        if (in.readByte() == 0) {
            installationSite = null;
        } else {
            installationSite = in.readInt();
        }
        installationSiteName = in.readString();
        maintenancePhone = in.readString();
        if (in.readByte() == 0) {
            cameraOrientationCode = null;
        } else {
            cameraOrientationCode = in.readInt();
        }
        cameraOrientation = in.readString();
        installationHeigh = in.readString();
        maintenanceUnit = in.readString();
        installationTime = in.readString();
        organizationName = in.readString();
        mIsSelect = in.readByte() != 0;
        lineDistance = in.readFloat();
        viewTimes = in.readLong();
        timeStamps = in.readLong();
    }

    public static final Creator<OrgCameraEntity> CREATOR = new Creator<OrgCameraEntity>() {
        @Override
        public OrgCameraEntity createFromParcel(Parcel in) {
            return new OrgCameraEntity(in);
        }

        @Override
        public OrgCameraEntity[] newArray(int size) {
            return new OrgCameraEntity[size];
        }
    };

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    private String deviceName;
    private Long deviceType;
    private String deviceTypeName; // 设备类型名称
    private Double longitude;
    private Double latitude;
    private Long manufacturerDeviceId;
    private String sn;
    private int deviceData;
    private Long deviceState;
    private String coverUrl;
    private String playUrl;
    private String installationLocationDetail; //设备具体安装位置
    private Integer installationSite; // 设备安装场所类型code
    private String installationSiteName; // 设备安装场所类型名称
    private String maintenancePhone; // 联系电话
    private Integer cameraOrientationCode; // 设备朝向code
    private String cameraOrientation; // 设备朝向名称
    private String installationHeigh; // 设备安装高度
    private String maintenanceUnit; // 设备维护单位
    private String installationTime; // 设备安装时间
    private String organizationName; // 设备分组名称

    private boolean mIsSelect;
    private float lineDistance;
    private long viewTimes;

    private long timeStamps;

    public int getDeviceData() {
        return deviceData;
    }

    public void setDeviceData(int deviceData) {
        this.deviceData = deviceData;
    }

    public String getCameraId() {
        return cameraId;
    }

    public void setCameraId(String cameraId) {
        this.cameraId = cameraId;
    }

    public String getDeviceTypeName() {
        return deviceTypeName;
    }

    public void setDeviceTypeName(String deviceTypeName) {
        this.deviceTypeName = deviceTypeName;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public String getInstallationLocationDetail() {
        return installationLocationDetail;
    }

    public void setInstallationLocationDetail(String installationLocationDetail) {
        this.installationLocationDetail = installationLocationDetail;
    }

    public Integer getInstallationSite() {
        return installationSite;
    }

    public void setInstallationSite(Integer installationSite) {
        this.installationSite = installationSite;
    }

    public String getInstallationSiteName() {
        return installationSiteName;
    }

    public void setInstallationSiteName(String installationSiteName) {
        this.installationSiteName = installationSiteName;
    }

    public String getMaintenancePhone() {
        return maintenancePhone;
    }

    public void setMaintenancePhone(String maintenancePhone) {
        this.maintenancePhone = maintenancePhone;
    }

    public Integer getCameraOrientationCode() {
        return cameraOrientationCode;
    }

    public void setCameraOrientationCode(Integer cameraOrientationCode) {
        this.cameraOrientationCode = cameraOrientationCode;
    }

    public String getCameraOrientation() {
        return cameraOrientation;
    }

    public void setCameraOrientation(String cameraOrientation) {
        this.cameraOrientation = cameraOrientation;
    }

    public String getInstallationHeigh() {
        return installationHeigh;
    }

    public void setInstallationHeigh(String installationHeigh) {
        this.installationHeigh = installationHeigh;
    }

    public String getMaintenanceUnit() {
        return maintenanceUnit;
    }

    public void setMaintenanceUnit(String maintenanceUnit) {
        this.maintenanceUnit = maintenanceUnit;
    }

    public String getInstallationTime() {
        return installationTime;
    }

    public void setInstallationTime(String installationTime) {
        this.installationTime = installationTime;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }


    public long getTimeStamps() {
        return timeStamps;
    }

    public void setTimeStamps(long timeStamps) {
        this.timeStamps = timeStamps;
    }

    public long getViewTimes() {
        return viewTimes;
    }

    public void setViewTimes(long viewTimes) {
        this.viewTimes = viewTimes;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public float getLineDistance() {
        return lineDistance;
    }

    public void setLineDistance(float lineDistance) {
        this.lineDistance = lineDistance;
    }

    public boolean isSelect() {
        return mIsSelect;
    }

    public void setSelect(boolean select) {
        mIsSelect = select;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public Long getDeviceType() {
        if (null == deviceType) {
            return 100604L; //普通摄像机
        }
        return deviceType;
    }

    public Long getDeviceTypeReal() {
        return deviceType;
    }

    public void setDeviceType(Long deviceType) {
        this.deviceType = deviceType;
    }

    public Long getManufacturerDeviceId() {
        return manufacturerDeviceId;
    }

    public void setManufacturerDeviceId(Long manufacturerDeviceId) {
        this.manufacturerDeviceId = manufacturerDeviceId;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public int getDeviceState() {
       /* if (null == deviceState) {
            return 0L;
        }
        return deviceState == Constants.ORG_CAMERA_OFF_LINE ? 0L : 1L;*/
       return deviceData;
    }

    public Long getDeviceStateReal() {
        return deviceState;
    }

    public void setDeviceState(Long deviceState) {
        this.deviceState = deviceState;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    @Override
    public LatLng getPosition() {
        if (null == getLatitude() || null == getLongitude()) {
            return null;
        } else {
            return new LatLng(getLatitude(), getLongitude());
        }
    }

    @Override
    public int getItemType() {
        // Long deviceLongType = getDeviceType();
        // int deviceType = deviceLongType != null && deviceLongType == 100602 ? 1 : 0;
        // Long deviceLongState = getDeviceState();
        // int deviceState = deviceLongState != null && deviceLongState > 100502 ? 0 : 1;
        // return deviceType << 1 | deviceState;
        return -getDeviceType().intValue()<<1 | getDeviceState();
    }

    @Override
    public String getItemId() {
        return getManufacturerDeviceId()+"";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cameraId);
        dest.writeString(state);
        dest.writeString(deviceName);
        if (deviceType == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(deviceType);
        }
        dest.writeString(deviceTypeName);
        if (longitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(longitude);
        }
        if (latitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(latitude);
        }
        if (manufacturerDeviceId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(manufacturerDeviceId);
        }
        dest.writeString(sn);
        dest.writeInt(deviceData);
        if (deviceState == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(deviceState);
        }
        dest.writeString(coverUrl);
        dest.writeString(playUrl);
        dest.writeString(installationLocationDetail);
        if (installationSite == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(installationSite);
        }
        dest.writeString(installationSiteName);
        dest.writeString(maintenancePhone);
        if (cameraOrientationCode == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(cameraOrientationCode);
        }
        dest.writeString(cameraOrientation);
        dest.writeString(installationHeigh);
        dest.writeString(maintenanceUnit);
        dest.writeString(installationTime);
        dest.writeString(organizationName);
        dest.writeByte((byte) (mIsSelect ? 1 : 0));
        dest.writeFloat(lineDistance);
        dest.writeLong(viewTimes);
        dest.writeLong(timeStamps);
    }
}
