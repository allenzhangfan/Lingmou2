package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;

public class NewDeployMissionRequest implements Serializable {
    private String id;
    private String name;
    private String startTime;
    private String endTime;
    private String alarmThreshold;
    private String describe;
    private String imageUrl;
    private String importAttribute;
    public NewDeployMissionRequest(String name, String startTime, String endTime, String alarmThreshold, String describe, String imageUrl,String importAttribute) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.alarmThreshold = alarmThreshold;
        this.describe = describe;
        this.imageUrl = imageUrl;
        this.importAttribute = importAttribute;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImportAttribute() {
        return importAttribute;
    }

    public void setImportAttribute(String importAttribute) {
        this.importAttribute = importAttribute;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getAlarmThreshold() {
        return alarmThreshold;
    }

    public void setAlarmThreshold(String alarmThreshold) {
        this.alarmThreshold = alarmThreshold;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
