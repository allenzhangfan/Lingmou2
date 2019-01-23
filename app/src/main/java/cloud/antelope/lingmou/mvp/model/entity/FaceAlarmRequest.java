package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;

/**
 * Created by ChenXinming on 2017/12/26.
 * description:
 */

public class FaceAlarmRequest implements Serializable {

    /**
     * threshold : 50.0
     * libIds :
     * alarmOperationType : 3
     * geoAddress : 102401
     * startTime : 1523330448311
     * endTime : 1525922448311
     * cameraIds : 538379152,538378355
     * sortType : 1
     * page : 1
     * pageSize : 12
     */

    private String libIds;
    private String alarmOperationType;
    // private String geoAddress;
    private Long startTime;
    private Long endTime;
    private String cameraIds;
    private int sortType;
    private int page;
    private int pageSize;

    public String getLibIds() {
        return libIds;
    }

    public void setLibIds(String libIds) {
        this.libIds = libIds;
    }

    public String getAlarmOperationType() {
        return alarmOperationType;
    }

    public void setAlarmOperationType(String alarmOperationType) {
        this.alarmOperationType = alarmOperationType;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getCameraIds() {
        return cameraIds;
    }

    public void setCameraIds(String cameraIds) {
        this.cameraIds = cameraIds;
    }

    public int getSortType() {
        return sortType;
    }

    public void setSortType(int sortType) {
        this.sortType = sortType;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
