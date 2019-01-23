package cloud.antelope.lingmou.mvp.model.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.lingdanet.safeguard.common.utils.LogUtils;
import com.lingdanet.safeguard.common.utils.TimeUtils;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.List;

/**
 * 作者：安兴亚
 * 创建日期：2017/07/10
 * 邮箱：anxingya@lingdanet.com
 * 描述：TODO
 */

public class NewsItemEntity extends DataSupport implements MultiItemEntity, Serializable {

    public static final int NO_IMAGE_CASE = 1;
    public static final int ONE_IMAGE_CASE = 2;
    public static final int MULTI_IMAGE_CASE = 3;
    public static final int IMPORT_CASE = 4;

    private int itemType;

    /**
     * publishState : 1
     * caseCode : caseCode1
     * hadBuildHtml : 0
     * createUserId : 9B459BFCC46948A9B7315E9F34D35FC0
     * createUserMobile : 13900139000
     * columnId : 2a554eca6936483f8e314fa811266d22
     * caseStatus : 01
     * createYear : 2017
     * isBlackUser : 0
     * createUserName : p5QqzDvFUZWC8ezjyRJpDg==
     * title : 案件发布定向
     * auditResult : 1
     * type : 1
     * content : 这里有人发定向案件
     * auditState : 1
     * caseType3 :
     * replyNeedAudit : 0
     * caseType : 01
     * caseType2 :
     * imgInfoJson :
     * caseLevel : 02
     * createUserType : 3
     * caseId : 3b201eb7b0514abf9b2d145f5b71e5e6
     * objPoint : 案件概要
     * operationCenterId : test
     * adminHadRead : 0
     * isAllowReply : 1
     * deleteState : 0
     * publishTime : 353816932
     * adminReadTime :
     * objTime : 1234566
     * bonusesFlag : 0
     * openOrientation : 1
     * htmlUrl :
     * createDay : 20170704
     * hasSyncEs : 0
     * updateTime :
     * fromAttentionUser : 0
     * createMonth : 201707
     * caseMoney :
     * createTime : 353816932
     * deleteTime :
     * auditTime : 353816932
     * isTop : 0
     * location :
     * _id : {"timestamp":1499137676,"machineIdentifier":12942189,"processIdentifier":-4520,"counter":2311805,"timeSecond":1499137676,"date":1499137676000,"time":1499137676000}
     */

    private String publishState;
    private String caseCode;
    private String hadBuildHtml;
    private String createUserId;
    private String createUserMobile;
    private String columnId;
    private String caseStatus;
    private String createYear;
    private String isBlackUser;
    private String createUserName;
    private String createUserNickName;
    private String title;
    private String auditResult;
    private String type;
    private String content;
    private String auditState;
    private String caseType3;
    private String replyNeedAudit;
    private String caseType;
    private String caseType2;
    private String imgInfoJson;
    private String caseLevel;
    private String createUserType;
    @SerializedName("id")
    private String caseId;
    private String objPoint;
    private String operationCenterId;
    private String adminHadRead;
    private String isAllowReply;
    private String deleteState;
    private String publishTime;
    private String adminReadTime;
    private String objTime;
    private String bonusesFlag;
    private String openOrientation;
    private String htmlUrl;
    private String createDay;
    private String hasSyncEs;
    private String updateTime;
    private String fromAttentionUser;
    private String createMonth;
    private String caseMoney;
    private String createTime;
    private String deleteTime;
    private String auditTime;
    private String isTop;
    private String location;
    private long oldCreateTime;

    public String getPublishState() {
        return publishState;
    }

    public void setPublishState(String publishState) {
        this.publishState = publishState;
    }

    public String getCaseCode() {
        return caseCode;
    }

    public void setCaseCode(String caseCode) {
        this.caseCode = caseCode;
    }

    public String getHadBuildHtml() {
        return hadBuildHtml;
    }

    public void setHadBuildHtml(String hadBuildHtml) {
        this.hadBuildHtml = hadBuildHtml;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public String getCreateUserMobile() {
        return createUserMobile;
    }

    public void setCreateUserMobile(String createUserMobile) {
        this.createUserMobile = createUserMobile;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public String getCaseStatus() {
        return caseStatus;
    }

    public void setCaseStatus(String caseStatus) {
        this.caseStatus = caseStatus;
    }

    public String getCreateYear() {
        return createYear;
    }

    public void setCreateYear(String createYear) {
        this.createYear = createYear;
    }

    public String getIsBlackUser() {
        return isBlackUser;
    }

    public void setIsBlackUser(String isBlackUser) {
        this.isBlackUser = isBlackUser;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getCreateUserNickName() {
        return createUserNickName;
    }

    public void setCreateUserNickName(String createUserNickName) {
        this.createUserNickName = createUserNickName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuditResult() {
        return auditResult;
    }

    public void setAuditResult(String auditResult) {
        this.auditResult = auditResult;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuditState() {
        return auditState;
    }

    public void setAuditState(String auditState) {
        this.auditState = auditState;
    }

    public String getCaseType3() {
        return caseType3;
    }

    public void setCaseType3(String caseType3) {
        this.caseType3 = caseType3;
    }

    public String getReplyNeedAudit() {
        return replyNeedAudit;
    }

    public void setReplyNeedAudit(String replyNeedAudit) {
        this.replyNeedAudit = replyNeedAudit;
    }

    public String getCaseType() {
        return caseType;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }

    public String getCaseType2() {
        return caseType2;
    }

    public void setCaseType2(String caseType2) {
        this.caseType2 = caseType2;
    }

    public List<AttachmentBean> getImgInfoJson() {
        try {
            return new Gson().fromJson(imgInfoJson, new TypeToken<List<AttachmentBean>>() {
            }.getType());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            LogUtils.e("JsonSyntaxException for imgInfoJson of NewsItemEntity: " + imgInfoJson.toString());
        }
        return null;
    }

    public void setImgInfoJson(String imgInfoJson) {
        this.imgInfoJson = imgInfoJson;
    }

    public String getCaseLevel() {
        return caseLevel;
    }

    public void setCaseLevel(String caseLevel) {
        this.caseLevel = caseLevel;
    }

    public String getCreateUserType() {
        return createUserType;
    }

    public void setCreateUserType(String createUserType) {
        this.createUserType = createUserType;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getObjPoint() {
        return objPoint;
    }

    public void setObjPoint(String objPoint) {
        this.objPoint = objPoint;
    }

    public String getOperationCenterId() {
        return operationCenterId;
    }

    public void setOperationCenterId(String operationCenterId) {
        this.operationCenterId = operationCenterId;
    }

    public String getAdminHadRead() {
        return adminHadRead;
    }

    public void setAdminHadRead(String adminHadRead) {
        this.adminHadRead = adminHadRead;
    }

    public String getIsAllowReply() {
        return isAllowReply;
    }

    public void setIsAllowReply(String isAllowReply) {
        this.isAllowReply = isAllowReply;
    }

    public String getDeleteState() {
        return deleteState;
    }

    public void setDeleteState(String deleteState) {
        this.deleteState = deleteState;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getAdminReadTime() {
        return adminReadTime;
    }

    public void setAdminReadTime(String adminReadTime) {
        this.adminReadTime = adminReadTime;
    }

    public String getObjTime() {
        return objTime;
    }

    public void setObjTime(String objTime) {
        this.objTime = objTime;
    }

    public String getBonusesFlag() {
        return bonusesFlag;
    }

    public void setBonusesFlag(String bonusesFlag) {
        this.bonusesFlag = bonusesFlag;
    }

    public String getOpenOrientation() {
        return openOrientation;
    }

    public void setOpenOrientation(String openOrientation) {
        this.openOrientation = openOrientation;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public String getCreateDay() {
        return createDay;
    }

    public void setCreateDay(String createDay) {
        this.createDay = createDay;
    }

    public String getHasSyncEs() {
        return hasSyncEs;
    }

    public void setHasSyncEs(String hasSyncEs) {
        this.hasSyncEs = hasSyncEs;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getFromAttentionUser() {
        return fromAttentionUser;
    }

    public void setFromAttentionUser(String fromAttentionUser) {
        this.fromAttentionUser = fromAttentionUser;
    }

    public String getCreateMonth() {
        return createMonth;
    }

    public void setCreateMonth(String createMonth) {
        this.createMonth = createMonth;
    }

    public String getCaseMoney() {
        return caseMoney;
    }

    public void setCaseMoney(String caseMoney) {
        this.caseMoney = caseMoney;
    }

    public String getCreateTime() {
        return TimeUtils.millis2String(Long.valueOf(createTime), "yyyy.MM.dd");
    }

    public long getOldCreateTime() {
        return oldCreateTime;
    }

    public void setOldCreateTime(long oldCreateTime) {
        this.oldCreateTime = oldCreateTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(String deleteTime) {
        this.deleteTime = deleteTime;
    }

    public String getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(String auditTime) {
        this.auditTime = auditTime;
    }

    public String getIsTop() {
        return isTop;
    }

    public void setIsTop(String isTop) {
        this.isTop = isTop;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}
