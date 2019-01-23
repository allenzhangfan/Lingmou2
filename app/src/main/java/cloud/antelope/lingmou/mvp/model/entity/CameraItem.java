package cloud.antelope.lingmou.mvp.model.entity;

import android.text.TextUtils;

import com.amap.api.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

import cloud.antelope.lingmou.app.utils.map.ClusterItem;
import cloud.antelope.lingmou.common.Constants;

/**
 * 作者：安兴亚
 * 创建日期：2017/11/16
 * 邮箱：anxingya@lingdanet.com
 * 描述：TODO
 */

public class CameraItem extends DataSupport implements Serializable, ClusterItem {


    /**
     * id : 325
     * name : 本地羚羊2
     * type : camera
     * camera_status : 1
     * camera_type : 1
     * cameraNo : 本地羚羊2
     * latitude : null
     * longitude : null
     * angle : 0
     * cameraCode : null
     * installType : 3
     * installAddress : null
     * score : 0
     * isSynced : null
     * hd_channel : []
     * sd_channel : []
     * sort : null
     * orgPath : 组织1>组织11>组织113
     * "isSolos" : 是否是单兵
     */

    @SerializedName("id")
    private String cameraId;
    private String name;
    private String type;
    private int camera_status;
    private int camera_type;
    private String cameraNo;
    private String latitude;
    private String longitude;
    private String installAddress;
    private String orgPath;
    private Boolean isSolos;
    private boolean mIsSelect;

    public boolean isSelect() {
        return mIsSelect;
    }

    public void setSelect(boolean select) {
        mIsSelect = select;
    }

    public Boolean getSolos() {
        return isSolos;
    }

    public void setSolos(Boolean solos) {
        isSolos = solos;
    }

    public String getCameraId() {
        return cameraId;
    }

    public void setCameraId(String cameraId) {
        this.cameraId = cameraId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCamera_status() {
        return camera_status;
    }

    public void setCamera_status(int camera_status) {
        this.camera_status = camera_status;
    }

    public int getCamera_type() {
        return camera_type >= 0 ? camera_type : Constants.CAMERA_QIANG_JI;
    }

    public void setCamera_type(int camera_type) {
        this.camera_type = camera_type;
    }

    public String getCameraNo() {
        return cameraNo;
    }

    public void setCameraNo(String cameraNo) {
        this.cameraNo = cameraNo;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getInstallAddress() {
        return installAddress;
    }

    public void setInstallAddress(String installAddress) {
        this.installAddress = installAddress;
    }

    public String getOrgPath() {
        return orgPath;
    }

    public void setOrgPath(String orgPath) {
        this.orgPath = orgPath;
    }

    @Override
    public LatLng getPosition() {
        if (TextUtils.isEmpty(getLatitude()) || TextUtils.isEmpty(getLongitude())) {
            return null;
        } else {
            return new LatLng(Double.parseDouble(getLatitude()), Double.parseDouble(getLongitude()));
        }
    }

    /**
     * 这里使用了位运算，其中第一位表示的是摄像机的在线状态，0表示在线，1表示离线
     * 第二位表示的是摄像机的类型，0表示枪机，1表示球机
     *
     * @return
     */
    @Override
    public int getItemType() {
        return getCamera_type() << 1 | getCamera_status();
    }

    @Override
    public String getItemId() {
        return getCameraId();
    }

}
