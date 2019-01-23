package cloud.antelope.lingmou.mvp.model.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.lingdanet.safeguard.common.utils.LogUtils;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.List;

/**
 * 作者：安兴亚
 * 创建日期：2017/07/10
 * 邮箱：anxingya@lingdanet.com
 * 描述：TODO
 */

public class ClueItemEntity extends DataSupport implements MultiItemEntity, Serializable {

    private int itemType;

    private String publishState;
    private String caseCode;
    private String createUserId;
    private String createUserMobile;
    private String columnId;
    private String createYear;
    private String isBlackUser;
    private String createUserNickName;
    private String createUserName;
    private String title;
    private String auditResult;
    private String content;
    private String auditState;
    private String replyNeedAudit;
    private String gpsAddr;
    private String imgInfoJson;
    private String caseId;
    private String createUserType;
    @SerializedName("id")
    private String clueId;
    private String state;
    private String isFrozen;
    private String operationCenterId;
    private String adminHadRead;
    private String gpsX;
    private String isAllowReply;
    private String deleteState;
    private String gpsY;
    private String publishTime;
    private String isOrientation;
    private String adminReadTime;
    private String tipType;
    private String createDay;
    private String hasSyncEs;
    private String updateTime;
    private String fromAttentionUser;
    private String createMonth;
    private String tipType2;
    private String tipType3;
    private String createTime;
    private String deleteTime;
    private String auditTime;
    private String auditDesc;
    private String isTop;
    private String tipValued;
    private String location;
    private List<AnswerItemEntity> answerList;

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

    public String getReplyNeedAudit() {
        return replyNeedAudit;
    }

    public void setReplyNeedAudit(String replyNeedAudit) {
        this.replyNeedAudit = replyNeedAudit;
    }

    public String getGpsAddr() {
        return gpsAddr;
    }

    public void setGpsAddr(String gpsAddr) {
        this.gpsAddr = gpsAddr;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getCreateUserType() {
        return createUserType;
    }

    public void setCreateUserType(String createUserType) {
        this.createUserType = createUserType;
    }

    public String getClueId() {
        return clueId;
    }

    public void setClueId(String clueId) {
        this.clueId = clueId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getIsFrozen() {
        return isFrozen;
    }

    public void setIsFrozen(String isFrozen) {
        this.isFrozen = isFrozen;
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

    public String getGpsX() {
        return gpsX;
    }

    public void setGpsX(String gpsX) {
        this.gpsX = gpsX;
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

    public String getGpsY() {
        return gpsY;
    }

    public void setGpsY(String gpsY) {
        this.gpsY = gpsY;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getIsOrientation() {
        return isOrientation;
    }

    public void setIsOrientation(String isOrientation) {
        this.isOrientation = isOrientation;
    }

    public String getAdminReadTime() {
        return adminReadTime;
    }

    public void setAdminReadTime(String adminReadTime) {
        this.adminReadTime = adminReadTime;
    }

    public String getTipType() {
        return tipType;
    }

    public void setTipType(String tipType) {
        this.tipType = tipType;
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

    public String getTipType2() {
        return tipType2;
    }

    public void setTipType2(String tipType2) {
        this.tipType2 = tipType2;
    }

    public String getTipType3() {
        return tipType3;
    }

    public void setTipType3(String tipType3) {
        this.tipType3 = tipType3;
    }

    public String getCreateTime() {
        return createTime;
        // return TimeUtils.millis2String(Long.valueOf(createTime), "yyyy.MM.dd");
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

    public String getTipValued() {
        return tipValued;
    }

    public void setTipValued(String tipValued) {
        this.tipValued = tipValued;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<AttachmentBean> getImgInfoJson() {
        try {
            return new Gson().fromJson(imgInfoJson, new TypeToken<List<AttachmentBean>>() {
            }.getType());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            LogUtils.e("JsonSyntaxException for imgInfoJson of ClueItemEntity: " + imgInfoJson.toString());
        }
        return null;
    }

    public void setImgInfoJson(String imgInfoJson) {
        this.imgInfoJson = imgInfoJson;
    }

    public String getAuditDesc() {
        return auditDesc;
    }

    public void setAuditDesc(String auditDesc) {
        this.auditDesc = auditDesc;
    }

    public List<AnswerItemEntity> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(List<AnswerItemEntity> answerList) {
        this.answerList = answerList;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }
}
