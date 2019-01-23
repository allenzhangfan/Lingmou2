package cloud.antelope.lingmou.mvp.model.entity;

public class DeployDetailEntity {

    /**
     * id : 4835BD4F4DEB4D0FBE0D3EE6336762EA
     * name : 第10089个布控任务
     * creator : 101000001029
     * type : 1
     * startTime : 1536573603400
     * endTime : 1536578900557
     * describe : 修改描述了了了了了了
     * taskStatus : 3
     * alarmThreshold : 80
     * taskType : 101504
     * imageUrl:""
     */

    private String id;
    private String name;
    private String creator;
    private String type;
    private String startTime;
    private String endTime;
    private String describe;
    private int taskStatus;
    private String alarmThreshold;
    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    private int taskType;

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

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}
