package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 作者：陈新明
 * 创建日期：2018/05/09
 * 邮箱：chenxinming@antelop.cloud
 * 描述：TODO
 */
public class CameraNewStreamEntity implements Serializable {

    /**
     * init_string : [Config]
     IsDebug=0
     LocalBasePort=8200
     IsCaptureDev=1
     IsPlayDev=1
     UdpSendInterval=2
     [Tracker]
     Count=3
     IP1=218.95.36.43
     Port1=8000
     IP2=218.95.36.34
     Port2=80
     IP3=218.95.36.13
     Port3=443
     [LogServer]
     Count=1
     IP1=42.51.12.137
     Port1=80

     * devices : [{"cid":538379152,"state":4,"config_type":3,"tracker_ip":"218.95.36.43","tracker_port":80,"public_ip":"59.175.197.202","public_port":12942,"local_ip":"192.168.1.153","local_port":8200,"conn_key":0,"relay_ip":"da5f2413.server.antelopecloud.cn","relay_port":1935,"cover_url":"http://jxsr-api.antelopecloud.cn/v2/snapshots/538379152/cover?client_token=538379152_0_1525945299_b0edae2e823a2189df28aee22c0f587c","rtmp_url":"rtmp://da5f2413.server.antelopecloud.cn:1935/","hls":"http://218.95.36.19/live/538379152/index.m3u8"}]
     * total : 1
     * size : 1
     * page : 1
     * request_id : 81651e4421fd4983a6ce1c5e891e4b05
     */

    private String init_string;
    private int total;
    private int size;
    private int page;
    private String request_id;
    private List<DevicesBean> devices;

    public String getInit_string() {
        return init_string;
    }

    public void setInit_string(String init_string) {
        this.init_string = init_string;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public List<DevicesBean> getDevices() {
        return devices;
    }

    public void setDevices(List<DevicesBean> devices) {
        this.devices = devices;
    }

    public static class DevicesBean {
        /**
         * cid : 538379152
         * state : 4
         * config_type : 3
         * tracker_ip : 218.95.36.43
         * tracker_port : 80
         * public_ip : 59.175.197.202
         * public_port : 12942
         * local_ip : 192.168.1.153
         * local_port : 8200
         * conn_key : 0
         * relay_ip : da5f2413.server.antelopecloud.cn
         * relay_port : 1935
         * cover_url : http://jxsr-api.antelopecloud.cn/v2/snapshots/538379152/cover?client_token=538379152_0_1525945299_b0edae2e823a2189df28aee22c0f587c
         * rtmp_url : rtmp://da5f2413.server.antelopecloud.cn:1935/
         * hls : http://218.95.36.19/live/538379152/index.m3u8
         */
        private String cover_url;
        private String rtmp_url;
        private String hls;
        private String state;
        private Long cid;

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public Long getCid() {
            return cid;
        }

        public void setCid(Long cid) {
            this.cid = cid;
        }

        public String getCover_url() {
            return cover_url;
        }

        public void setCover_url(String cover_url) {
            this.cover_url = cover_url;
        }

        public String getRtmp_url() {
            return rtmp_url;
        }

        public void setRtmp_url(String rtmp_url) {
            this.rtmp_url = rtmp_url;
        }

        public String getHls() {
            return hls;
        }

        public void setHls(String hls) {
            this.hls = hls;
        }
    }
}
