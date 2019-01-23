package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;

/**
 * Created by ChenXinming on 2017/12/26.
 * description:
 */

public class FaceAlarmDetailBean implements Serializable {

    /**
     * point : "1427_421_136_135_"
     * vid : be2db16ebb384d9081e45d8fb6a7a07e
     * isCollection : null
     * orgId : 50
     * alarmId : be2db16ebb384d9081e45d8fb6a7a07e
     * cameraId : 538378299
     * longitude : 114.40512
     * latitude : 30.494516
     * cameraName : 武汉灵达
     * captureImg : PFSB:/vdtimg/bimg/data/20171226/18_0/f5ab0fe7bfb12478212a9b52c22150fa
     * alarmLevel : 0
     * alarmType : 2
     * alarmStatus : 1
     * absTime : 1514254178000
     * creator : -1
     * detail : {"personId":845,"createTime":"2017-12-22 17:00:35","idcard":"","name":"xy","sex":"1","libId":891277,"libName":"武汉布控库","targetImage":"PFSB:/vdtimg/bimg/data/20171223/00_0/33ca6a40c930f4da88be5d7078b411b1","score":87,"nation":"未知"}
     * createTime : 1514254220393
     * isEffective : -1
     * common : null
     * cameraType : 0
     * installType : 1
     * srcUrl : PFSB:/vdtimg/bimg/data/20171226/18_0/8acb4cefdc9a5853f42923ca1ef4186c
     * isOnlineStatus : null
     */

    private String point;
    private String vid;
    private String orgId;
    private String alarmId;
    private String cameraId;
    private double longitude;
    private double latitude;
    private String cameraName;
    private String captureImg;
    private int alarmLevel;
    private int alarmType;
    private int alarmStatus;
    private long absTime;
    private String creator;
    private DetailBean detail;
    private long createTime;
    private int isEffective = -2;
    private String common;
    private String cameraType;
    private String installType;
    private String srcUrl;

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(String alarmId) {
        this.alarmId = alarmId;
    }

    public String getCameraId() {
        return cameraId;
    }

    public void setCameraId(String cameraId) {
        this.cameraId = cameraId;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getCameraName() {
        return cameraName;
    }

    public void setCameraName(String cameraName) {
        this.cameraName = cameraName;
    }

    public String getCaptureImg() {
        return captureImg;
    }

    public void setCaptureImg(String captureImg) {
        this.captureImg = captureImg;
    }

    public int getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(int alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public int getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(int alarmType) {
        this.alarmType = alarmType;
    }

    public int getAlarmStatus() {
        return alarmStatus;
    }

    public void setAlarmStatus(int alarmStatus) {
        this.alarmStatus = alarmStatus;
    }

    public long getAbsTime() {
        return absTime;
    }

    public void setAbsTime(long absTime) {
        this.absTime = absTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public DetailBean getDetail() {
        return detail;
    }

    public void setDetail(DetailBean detail) {
        this.detail = detail;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getIsEffective() {
        return isEffective;
    }

    public void setIsEffective(int isEffective) {
        this.isEffective = isEffective;
    }

    public String getCommon() {
        return common;
    }

    public void setCommon(String common) {
        this.common = common;
    }

    public String getCameraType() {
        return cameraType;
    }

    public void setCameraType(String cameraType) {
        this.cameraType = cameraType;
    }

    public String getInstallType() {
        return installType;
    }

    public void setInstallType(String installType) {
        this.installType = installType;
    }

    public String getSrcUrl() {
        return srcUrl;
    }

    public void setSrcUrl(String srcUrl) {
        this.srcUrl = srcUrl;
    }

    public static class DetailBean implements Serializable {
        /**
         * personId : 845.0
         * createTime : 2017-12-22 17:00:35
         * idcard :
         * name : xy
         * sex : 1
         * libId : 891277.0
         * libName : 武汉布控库
         * targetImage : PFSB:/vdtimg/bimg/data/20171223/00_0/33ca6a40c930f4da88be5d7078b411b1
         * score : 87.0
         * nation : 未知
         * birthday : 生日
         */

        private double personId;
        private String createTime;
        private String idcard;
        private String name;
        private String sex;
        private double libId;
        private String libName;
        private String targetImage;
        private double score;
        private String nation;
        private String birthday;

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public double getPersonId() {
            return personId;
        }

        public void setPersonId(double personId) {
            this.personId = personId;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getIdcard() {
            return idcard;
        }

        public void setIdcard(String idcard) {
            this.idcard = idcard;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public double getLibId() {
            return libId;
        }

        public void setLibId(double libId) {
            this.libId = libId;
        }

        public String getLibName() {
            return libName;
        }

        public void setLibName(String libName) {
            this.libName = libName;
        }

        public String getTargetImage() {
            return targetImage;
        }

        public void setTargetImage(String targetImage) {
            this.targetImage = targetImage;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public String getNation() {
            return nation;
        }

        public void setNation(String nation) {
            this.nation = nation;
        }
    }
}
