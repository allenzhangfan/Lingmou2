package cloud.antelope.lingmou.mvp.model.entity;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 作者：安兴亚
 * 创建日期：2017/10/23
 * 邮箱：anxingya@lingdanet.com
 * 描述：TODO
 */

public class UpdateInfo extends DataSupport implements Serializable {

    /**
     * version_name : 朝阳群众v2.2.0
     * version_number : 23
     * version_description : 发现新版本，请立即更新体验！
     * force_update : false
     * package_url : https://case.netposa.com/static/bin/cyqz_v2.2.0.apk
     */

    @SerializedName("version_name")
    private String versionName;
    @SerializedName("version_number")
    private int versionNumber;
    @SerializedName("version_description")
    private String versionDescription;
    @SerializedName("force_update")
    private boolean forceUpdate;
    @SerializedName("package_url")
    private String packageUrl;
    @SerializedName("min_version")
    private int minVersion;
    @SerializedName("total_times")
    private int totalTimes = -1;
    @SerializedName("interval_times")
    private int intervalTimes;
    @SerializedName("update_json")
    private boolean updateJson;
    private long showTime = -1L;

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(int versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getVersionDescription() {
        return versionDescription;
    }

    public void setVersionDescription(String versionDescription) {
        this.versionDescription = versionDescription;
    }

    public boolean isForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public String getPackageUrl() {
        return packageUrl;
    }

    public void setPackageUrl(String packageUrl) {
        this.packageUrl = packageUrl;
    }

    public int getMinVersion() {
        return minVersion;
    }

    public void setMinVersion(int minVersion) {
        this.minVersion = minVersion;
    }

    public int getTotalTimes() {
        return totalTimes;
    }

    public void setTotalTimes(int totalTimes) {
        this.totalTimes = totalTimes;
    }

    public int getIntervalTimes() {
        return intervalTimes;
    }

    public void setIntervalTimes(int intervalTimes) {
        this.intervalTimes = intervalTimes;
    }

    public boolean isUpdateJson() {
        return updateJson;
    }

    public void setUpdateJson(boolean updateJson) {
        this.updateJson = updateJson;
    }

    public long getShowTime() {
        return showTime;
    }

    public void setShowTime(long showTime) {
        this.showTime = showTime;
    }
}
