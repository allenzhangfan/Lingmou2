package cloud.antelope.lingmou.mvp.model.entity;

import java.util.List;

public class DeployResponse {

    /**
     * pageNum : 1
     * pageSize : 20
     * total : 2
     * pages : 1
     * list : [{"id":"F38AA7B026484D9EBE7906A459FF60E9","name":"第100801个布控任务","startTime":"1536573600000","endTime":"1536578900000","describe":"修改描述了了了了了了","taskStatus":3,"alarmThreshold":"98","taskType":10504,"imageUrl":"https://jxsr-oss1.antelopecloud.cn/files?obj_id=5b9643f380004002042002f2&access_token=2147500034_3356491776_1568110424_1e1dfd4f5789ccb445c96bce1ac44baf"}]
     */

    private int pageNum;
    private int pageSize;
    private int total;
    private int pages;
    private List<ListBean> list;

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean implements SearchBean{
        /**
         * id : F38AA7B026484D9EBE7906A459FF60E9
         * name : 第100801个布控任务
         * startTime : 1536573600000
         * endTime : 1536578900000
         * describe : 修改描述了了了了了了
         * taskStatus : 3
         * alarmThreshold : 98
         * taskType : 10504
         * imageUrl : https://jxsr-oss1.antelopecloud.cn/files?obj_id=5b9643f380004002042002f2&access_token=2147500034_3356491776_1568110424_1e1dfd4f5789ccb445c96bce1ac44baf
         */

        private String id;
        private String name;
        private String startTime;
        private String endTime;
        private String describe;
        private int taskStatus;
        private String alarmThreshold;
        private int taskType;
        private String imageUrl;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

        public String getDescribe() {
            return describe;
        }

        public void setDescribe(String describe) {
            this.describe = describe;
        }

        public int getTaskStatus() {
            return taskStatus;
        }

        public void setTaskStatus(int taskStatus) {
            this.taskStatus = taskStatus;
        }

        public String getAlarmThreshold() {
            return alarmThreshold;
        }

        public void setAlarmThreshold(String alarmThreshold) {
            this.alarmThreshold = alarmThreshold;
        }

        public int getTaskType() {
            return taskType;
        }

        public void setTaskType(int taskType) {
            this.taskType = taskType;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }
}
