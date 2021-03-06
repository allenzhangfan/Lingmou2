package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 作者：陈新明
 * 创建日期：2018/05/10
 * 邮箱：chenxinming@antelop.cloud
 * 描述：TODO
 */
public class DailyPoliceAlarmEntity implements Serializable,SearchBean {

    /**
     * id : 5F806175DFBF44B095253627C8FD7F6A
     * alarmLogId : 014534065C684B3C9BBC79835A1E321F
     * infoId : 71
     * structuredInfoJson : {"attr":{"female":0,"male":1,"nation":0,"age":57,"pose":null},"quality":0,"rect":{"left":273,"top":370,"width":577,"height":577}}
     * objectMainJson : {"name":"444"}
     * cameraName : 武汉研发中心走廊11
     * libId : 101000000069
     * libName : 南昌测试库
     * taskId : 0755D57C71994D80A971F64C1F39E8C0
     * taskName : 任务1
     * scenePath : https://jxsr-oss1.antelopecloud.cn/files2/538378354/5aea87bb20170072042a265c?access_token=538378354_0_1556855596_ed74521b586a0076ab6ada6dd82dde85&key=%2Fiermu%2Fai%2F137898526635_538378354_1525319610128_1525319611286243826
     * faceRect : 1128,581,109,108
     * similarity : 73.57386779785156
     * captureTime : 1525319542209
     * alarmTime : 1525319611000
     * oid : ["100101000651","100101000395","100101001579","100101001707","100101000362","100101001711","100101001454","100101000236","100101001708","100101001603","100101000256","100101000440","100101001053","100101000595","100101000311"]
     * cid : ["100100000570","100100000190","100100000318","100100000125","100100000189","100100000349","100100000540","100100000147","100100000116","100100000586","100100000585","100100000589","100100000226","100100000576","100100000197"]
     * isEffective : 1
     * isHandle : 1
     * geoAddress : 102401
     * facePath : https://jxsr-oss1.antelopecloud.cn/files?access_token=2147500032_0_1553226734_100c6f794e3ac9c7037f7e569489c16c&key=%2f5aea87af0009002620170072
     * operationDetail : 456
     * imageUrl : https://jxsr-oss1.antelopecloud.cn/files?obj_id=5ae2da08800040020428b104&access_token=2147500034_3356491776_1556351069_ae832b5488e1b3749514d9221c003f9f
     * cameraId : 538378354
     */

    private String id;
    private ObjectMainJsonBean objectMainJson;
    private String cameraName;
    private String libId;
    private String libName;
    private String taskId;
    private String taskName;
    private String scenePath;
    private Double similarity;
    private String captureTime;
    private String alarmTime;
    private Integer isEffective;
    private Integer isHandle;
    private String facePath;
    private String operationDetail;
    private String imageUrl;
    private Integer taskType;
    private Long cameraId;
    private String faceRect;
    private List<Long> alarmNotifyUsers;
    private Integer collectStatus;
    private Long collectId;
    private Long latitide;
    private Long longitude;

    public Long getLatitide() {
        return latitide;
    }

    public void setLatitide(Long latitide) {
        this.latitide = latitide;
    }

    public Long getLongitude() {
        return longitude;
    }

    public void setLongitude(Long longitude) {
        this.longitude = longitude;
    }

    public Integer getCollectStatus() {
        return collectStatus;
    }

    public void setCollectStatus(Integer collectStatus) {
        this.collectStatus = collectStatus;
    }

    public Long getCollectId() {
        return collectId;
    }

    public void setCollectId(Long collectId) {
        this.collectId = collectId;
    }

    public List<Long> getAlarmNotifyUsers() {
        return alarmNotifyUsers;
    }

    public void setAlarmNotifyUsers(List<Long> alarmNotifyUsers) {
        this.alarmNotifyUsers = alarmNotifyUsers;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public ObjectMainJsonBean getObjectMainJson() {
        return objectMainJson;
    }

    public void setObjectMainJson(ObjectMainJsonBean objectMainJson) {
        this.objectMainJson = objectMainJson;
    }

    public String getCameraName() {
        return cameraName;
    }

    public void setCameraName(String cameraName) {
        this.cameraName = cameraName;
    }

    public String getLibId() {
        return libId;
    }

    public void setLibId(String libId) {
        this.libId = libId;
    }

    public String getLibName() {
        return libName;
    }

    public void setLibName(String libName) {
        this.libName = libName;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getScenePath() {
        return scenePath;
    }

    public void setScenePath(String scenePath) {
        this.scenePath = scenePath;
    }

    public String getFaceRect() {
        return faceRect;
    }

    public void setFaceRect(String faceRect) {
        this.faceRect = faceRect;
    }

    public double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    public String getCaptureTime() {
        return captureTime;
    }

    public void setCaptureTime(String captureTime) {
        this.captureTime = captureTime;
    }

    public String getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }

    public Integer getIsEffective() {
        return isEffective;
    }

    public void setIsEffective(int isEffective) {
        this.isEffective = isEffective;
    }

    public Integer getIsHandle() {
        return isHandle;
    }

    public void setIsHandle(int isHandle) {
        this.isHandle = isHandle;
    }

    public String getFacePath() {
        return facePath;
    }

    public void setFacePath(String facePath) {
        this.facePath = facePath;
    }

    public String getOperationDetail() {
        return operationDetail;
    }

    public void setOperationDetail(String operationDetail) {
        this.operationDetail = operationDetail;
    }

    public void setSimilarity(Double similarity) {
        this.similarity = similarity;
    }

    public void setIsEffective(Integer isEffective) {
        this.isEffective = isEffective;
    }

    public void setIsHandle(Integer isHandle) {
        this.isHandle = isHandle;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public Long getCameraId() {
        return cameraId;
    }

    public void setCameraId(Long cameraId) {
        this.cameraId = cameraId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public static class ObjectMainJsonBean implements Serializable {
        /**
         * name : 444
         */

        private String name;
        private String birthday;
        private String nationality;
        private String gender;
        private String identityNumber;
        private String mobile;

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getNationality() {
            return nationality;
        }

        public void setNationality(String nationality) {
            this.nationality = nationality;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getIdentityNumber() {
            return identityNumber;
        }

        public void setIdentityNumber(String identityNumber) {
            this.identityNumber = identityNumber;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
