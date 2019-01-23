package cloud.antelope.lingmou.mvp.model.entity;

/**
 * 作者：陈新明
 * 创建日期：2017/7/12
 * 邮箱：
 * 描述：TODO
 */

public class CameraStreamEntity {

    /**
     * cameraId : 0b4e7a0e5fe84ad35fb5f95b9ceeac79
     * cameraNo : 135693698758
     * cameraStatus : 4
     * rtmpUrl : rtmp://rtmp0-9.public.topvdn.cn/
     * hls : http://hls0-
     9.public.topvdn.cn/hls/<cid>/index.m3u8
     */

    private String cameraId;
    private String cameraNo;
    private int cameraStatus;
    private String rtmpUrl;
    private String hls;
    private String bitlevel;

    public String getBitlevel() {
        return bitlevel;
    }

    public void setBitlevel(String bitlevel) {
        this.bitlevel = bitlevel;
    }

    public String getCameraId() {
        return cameraId;
    }

    public void setCameraId(String cameraId) {
        this.cameraId = cameraId;
    }

    public String getCameraNo() {
        return cameraNo;
    }

    public void setCameraNo(String cameraNo) {
        this.cameraNo = cameraNo;
    }

    public int getCameraStatus() {
        return cameraStatus;
    }

    public void setCameraStatus(int cameraStatus) {
        this.cameraStatus = cameraStatus;
    }

    public String getRtmpUrl() {
        return rtmpUrl;
    }

    public void setRtmpUrl(String rtmpUrl) {
        this.rtmpUrl = rtmpUrl;
    }

    public String getHls() {
        return hls;
    }

    public void setHls(String hls) {
        this.hls = hls;
    }
}
