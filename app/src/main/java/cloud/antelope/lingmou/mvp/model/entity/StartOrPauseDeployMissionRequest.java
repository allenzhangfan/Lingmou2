package cloud.antelope.lingmou.mvp.model.entity;

public class StartOrPauseDeployMissionRequest {
    private String id;
    private String type;//任务操作状态<br />1：启用 <br />0：停用

    public StartOrPauseDeployMissionRequest(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
