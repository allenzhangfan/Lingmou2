package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 作者：陈新明
 * 创建日期：2018/05/14
 * 邮箱：chenxinming@antelop.cloud
 * 描述：TODO
 */
public class RecordLyCameraBean implements Serializable{

    /**
     * cid : 538447238
     * eventinfo : []
     * events : []
     * request_id : 8dbd2fb36e8642138e04dc8b0ea3f0e9
     * servers : [{"ip":"218.95.36.50","port":80}]
     * videos : [{"from":1526286411,"server_index":0,"to":1526289960}]
     */

    // private int cid;
    private String request_id;
    private List<ServersBean> eventinfo;
    private List<VideosBean> videos;

    // public int getCid() {
    //     return cid;
    // }
    //
    // public void setCid(int cid) {
    //     this.cid = cid;
    // }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    // public List<ServersBean> getServers() {
    //     return servers;
    // }
    //
    // public void setServers(List<ServersBean> servers) {
    //     this.servers = servers;
    // }

    public List<VideosBean> getVideos() {
        return videos;
    }

    public void setVideos(List<VideosBean> videos) {
        this.videos = videos;
    }

    public List<ServersBean> getEventinfo() {
        return eventinfo;
    }

    public void setEventinfo(List<ServersBean> eventinfo) {
        this.eventinfo = eventinfo;
    }

    public static class ServersBean implements Serializable{
        /**
         * ip : 218.95.36.50
         * port : 80
         */

        private long event_time;
        private int event_flag;
        private int event_type;

        public int getEvent_type() {
            return event_type;
        }

        public void setEvent_type(int event_type) {
            this.event_type = event_type;
        }

        public long getEvent_time() {
            return event_time;
        }

        public void setEvent_time(long event_time) {
            this.event_time = event_time;
        }

        public int getEvent_flag() {
            return event_flag;
        }

        public void setEvent_flag(int event_flag) {
            this.event_flag = event_flag;
        }
    }

    @Override
    public String toString() {
        return "RecordLyCameraBean{" +
                // "cid=" + cid +
                ", request_id='" + request_id + '\'' +
                // ", servers=" + servers +
                ", videos=" + videos +
                '}';
    }
}
