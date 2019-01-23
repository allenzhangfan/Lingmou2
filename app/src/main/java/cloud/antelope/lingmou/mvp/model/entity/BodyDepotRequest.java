package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ChenXinming on 2018/1/23.
 * description: 人体库
 */

public class BodyDepotRequest implements Serializable {


    /**
     * startTime : 1526354533700
     * endTime : 1526613733700
     * confidence : 0.8
     * cameraIds : ["538378708","538378652"]
     * cameraTags : ["102401"]
     * currentPage : 1
     * pageSize : 50
     * bodyTags : ["100001","112401","112502","112001"]
     */

    private Long startTime;
    private Long endTime;
    private int pageSize;
    private List<String> cameraIds;
    private List<String> cameraTags;
    private List<String> noCameraTags;
    private List<String> bodyTags;
    private String minCaptureTime;
    private String minId;
    private String pageType;

    public List<String> getNoCameraTags() {
        return noCameraTags;
    }

    public void setNoCameraTags(List<String> noCameraTags) {
        this.noCameraTags = noCameraTags;
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

    public String getPageType() {
        return pageType;
    }

    public void setPageType(String pageType) {
        this.pageType = pageType;
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

    public List<String> getBodyTags() {
        return bodyTags;
    }

    public void setBodyTags(List<String> bodyTags) {
        this.bodyTags = bodyTags;
    }
}
