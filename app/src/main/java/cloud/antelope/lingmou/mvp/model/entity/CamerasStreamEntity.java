package cloud.antelope.lingmou.mvp.model.entity;

import java.util.List;

/**
 * Created by ChenXinming on 2017/11/21.
 * description:
 */

public class CamerasStreamEntity extends LmBaseData {

    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * cameraId : 351
         * cameraNo : 538378299
         * rtmpUrl : rtmp://da5f2430.server.antelopecloud.cn:1935/
         * hls : http://218.95.36.48/live/538378299/index.m3u8
         * cameraStatus : 1
         * bitlevel :
         * scene :
         * nightMode :
         * exposeMode :
         */

        private String cameraId;
        private String cameraNo;
        private String rtmpUrl;
        private String hls;
        private int cameraStatus;
        private String bitlevel;
        private String scene;
        private String nightMode;
        private String exposeMode;

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

        public int getCameraStatus() {
            return cameraStatus;
        }

        public void setCameraStatus(int cameraStatus) {
            this.cameraStatus = cameraStatus;
        }

        public String getBitlevel() {
            return bitlevel;
        }

        public void setBitlevel(String bitlevel) {
            this.bitlevel = bitlevel;
        }

        public String getScene() {
            return scene;
        }

        public void setScene(String scene) {
            this.scene = scene;
        }

        public String getNightMode() {
            return nightMode;
        }

        public void setNightMode(String nightMode) {
            this.nightMode = nightMode;
        }

        public String getExposeMode() {
            return exposeMode;
        }

        public void setExposeMode(String exposeMode) {
            this.exposeMode = exposeMode;
        }
    }
}
