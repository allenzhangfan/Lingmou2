package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 录像时间轴
 * 时间碎片 （一次请求的时间碎片）
 *  */
public class RecordSection extends LmBaseData{

    /**
     * historyVideoTime : {"cid":538378302,"videos":[{"from":1511280000,"to":1511335410,"server_index":0}],"events":[],"eventinfo":[{"event_time":1511291279,"event_flag":1,"event_type":5}],"servers":[{"ip":"218.95.36.50","port":80}],"request_id":"07b192cd12ec47e6b08e58b02a31cab7"}
     */

    private HistoryVideoTimeBean historyVideoTime;

    public HistoryVideoTimeBean getHistoryVideoTime() {
        return historyVideoTime;
    }

    public void setHistoryVideoTime(HistoryVideoTimeBean historyVideoTime) {
        this.historyVideoTime = historyVideoTime;
    }

    public static class HistoryVideoTimeBean implements Serializable {
        /**
         * cid : 538378302
         * videos : [{"from":1511280000,"to":1511335410,"server_index":0}]
         * events : []
         * eventinfo : [{"event_time":1511291279,"event_flag":1,"event_type":5}]
         * servers : [{"ip":"218.95.36.50","port":80}]
         * request_id : 07b192cd12ec47e6b08e58b02a31cab7
         */

        private long cid;
        private String request_id;
        private List<VideosBean> videos;
        private List<Event> events;
        private List<EventinfoBean> eventinfo;
        private List<ServersBean> servers;

        public static class Event implements Serializable {
            public Event() {}
            public long begin;
            public long end;
            public String url;
            private Event(long begin,long end,String url){
                this.begin=begin;
                this.end = end;
                this.url = url;
            }
        }
        public long getCid() {
            return cid;
        }

        public void setCid(long cid) {
            this.cid = cid;
        }

        public String getRequest_id() {
            return request_id;
        }

        public void setRequest_id(String request_id) {
            this.request_id = request_id;
        }

        public List<VideosBean> getVideos() {
            return videos;
        }

        public void setVideos(List<VideosBean> videos) {
            this.videos = videos;
        }

        public List<?> getEvents() {
            return events;
        }

        public void setEvents(List<Event> events) {
            this.events = events;
        }

        public List<EventinfoBean> getEventinfo() {
            return eventinfo;
        }

        public void setEventinfo(List<EventinfoBean> eventinfo) {
            this.eventinfo = eventinfo;
        }

        public List<ServersBean> getServers() {
            return servers;
        }

        public void setServers(List<ServersBean> servers) {
            this.servers = servers;
        }

        public static class VideosBean implements Serializable {
            public VideosBean(){}

            /**
             * from : 1511280000
             * to : 1511335410
             * server_index : 0
             */
            private VideosBean(long from, long to, int serverIndex) {
                this.from = from;
                this.to = to;
                this.server_index = server_index;
            }
            public VideosBean getNewSection(long from, long to) {
                return new VideosBean(from, to, server_index);
            }

            private long from;
            private long to;
            private Byte server_index;

            public long getFrom() {
                return from;
            }

            public void setFrom(long from) {
                this.from = from;
            }

            public long getTo() {
                return to;
            }

            public void setTo(long to) {
                this.to = to;
            }

            public Byte getServer_index() {
                return server_index;
            }

            public void setServer_index(Byte server_index) {
                this.server_index = server_index;
            }
        }

        public static class EventinfoBean implements Serializable {
            /**
             * event_time : 1511291279
             * event_flag : 1
             * event_type : 5
             */

            private int event_time;
            private int event_flag;
            private int event_type;

            public int getEvent_time() {
                return event_time;
            }

            public void setEvent_time(int event_time) {
                this.event_time = event_time;
            }

            public int getEvent_flag() {
                return event_flag;
            }

            public void setEvent_flag(int event_flag) {
                this.event_flag = event_flag;
            }

            public int getEvent_type() {
                return event_type;
            }

            public void setEvent_type(int event_type) {
                this.event_type = event_type;
            }
        }

        public static class ServersBean implements Serializable {
            /**
             * ip : 218.95.36.50
             * port : 80
             */

            private String ip;
            private int port;

            public String getIp() {
                return ip;
            }

            public void setIp(String ip) {
                this.ip = ip;
            }

            public int getPort() {
                return port;
            }

            public void setPort(int port) {
                this.port = port;
            }
        }
    }
}
