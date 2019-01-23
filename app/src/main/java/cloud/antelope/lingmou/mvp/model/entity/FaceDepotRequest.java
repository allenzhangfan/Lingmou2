package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ChenXinming on 2017/12/28.
 * description:
 */

public class FaceDepotRequest implements Serializable {
    private List<String> cameraIds;
    private List<String> cameraTags;
    private List<String> noCameraTags;
    private List<String> faceTags;
    private List<String> emptyTags;
    private Long startTime;
    private Long endTime;
    private int pageSize;
    private String minCaptureTime;
    private String minId;
    private String pageType;

    public List<String> getNoCameraTags() {
        return noCameraTags;
    }

    public void setNoCameraTags(List<String> noCameraTags) {
        this.noCameraTags = noCameraTags;
    }

    public String getPageType() {
        return pageType;
    }

    public void setPageType(String pageType) {
        this.pageType = pageType;
    }

    public String getMinCaptureTime() {
        return minCaptureTime;
    }

    public void setMinCaptureTime(String minCaptureTime) {
        this.minCaptureTime = minCaptureTime;
    }

    public String getMinId() {
        return minId;
    }

    public void setMinId(String minId) {
        this.minId = minId;
    }

    public List<String> getCameraIds() {
        return cameraIds;
    }

    public void setCameraIds(List<String> cameraIds) {
        this.cameraIds = cameraIds;
    }

    public List<String> getCameraTags() {
        return cameraTags;
    }

    public void setCameraTags(List<String> cameraTags) {
        this.cameraTags = cameraTags;
    }

    public List<String> getFaceTags() {
        return faceTags;
    }

    public void setFaceTags(List<String> faceTags) {
        this.faceTags = faceTags;
    }

    public List<String> getEmptyTags() {
        return emptyTags;
    }

    public void setEmptyTags(List<String> emptyTags) {
        this.emptyTags = emptyTags;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
