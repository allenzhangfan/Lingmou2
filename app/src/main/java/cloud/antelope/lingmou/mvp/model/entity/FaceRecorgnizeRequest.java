package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;

/**
 * Created by ChenXinming on 2017/11/20.
 * description: 辨脸传的参数
 */

public class FaceRecorgnizeRequest implements Serializable {

    private Long startTime;
    private Long endTime;
    private int score;
    private Object faceFeature;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Object getFaceFeatures() {
        return faceFeature;
    }

    public void setFaceFeatures(Object faceFeatures) {
        this.faceFeature = faceFeatures;
    }
}
