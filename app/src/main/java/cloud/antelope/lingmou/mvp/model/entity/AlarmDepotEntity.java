package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ChenXinming on 2018/1/20.
 * description:
 */

public class AlarmDepotEntity implements Serializable {


    /**
     * id : 101000000083
     * name : cxmTest01
     * threshold : 61
     * status : 1
     * creator : 101000000289
     * oid : 100101000636
     * cid : 100100000267
     * createTime : 1525932054528
     * updateTime : 1525932054528
     * userId : ["101000000289"]
     * key : 101000000083
     * creatorName : 未知创建者
     * orgName : CxmTest
     * userIdNames : ["未知管理者"]
     */

    private String id;
    private String name;
    private String threshold;
    private int status;
    private String creator;
    private String oid;
    private String cid;
    private String createTime;
    private String updateTime;
    private String key;
    private String creatorName;
    private String orgName;
    private List<String> userId;
    private List<String> userIdNames;

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

    public String getThreshold() {
        return threshold;
    }

    public void setThreshold(String threshold) {
        this.threshold = threshold;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public List<String> getUserId() {
        return userId;
    }

    public void setUserId(List<String> userId) {
        this.userId = userId;
    }

    public List<String> getUserIdNames() {
        return userIdNames;
    }

    public void setUserIdNames(List<String> userIdNames) {
        this.userIdNames = userIdNames;
    }
}
