package cloud.antelope.lingmou.mvp.model.entity;

/**
 * 作者：陈新明
 * 创建日期：2017/7/12
 * 邮箱：
 * 描述：TODO
 */

public class CameraRecordRequest {

    /**
     * cameraId : 53701535654654654651654654654a2
     * startTime : 1441676854
     * endTime : 1441680454
     */

    private String cameraId;
    private long startTime;
    private long endTime;

    public String getCameraId() {
        return cameraId;
    }

    public void setCameraId(String cameraId) {
        this.cameraId = cameraId;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
