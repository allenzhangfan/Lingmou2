package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 作者：安兴亚
 * 创建日期：2017/07/26
 * 邮箱：anxingya@lingdanet.com
 * 描述：TODO
 */

public class CommentItemEntity implements Serializable {


    /**
     * id : 593079d455db4b969a7fbd4594f568be
     * reply : 593079d455db4b969a7fbd4594f568be
     * columnId : 593079d455db4b969a7fbd4594f568be
     * contentId : 1
     * operationCenterId : 593079d455db4b969a7fbd4594f568be
     * createUserId : 593079d455db4b969a7fbd4594f568be
     * createUserType : "5" //回复人的type 5是管理员
     * reUserType ： "5" // 被回复人的type 5是管理员
     * createUserName : createUserName
     * createUserNickName : createUserNickName
     * createUserMobile : 1
     * createTime : 593079d455db4b969a7fbd4594f568be
     * replyAuditState : 593079d455db4b969a7fbd4594f568be
     * replyAuditResult : 593079d455db4b969a7fbd4594f568be
     * replyAuditTime : 1
     * replyAuditDesc :
     * deleteState : 1
     * deleteTime : 1
     * hadAnswered : 593079d455db4b969a7fbd4594f568be
     * answerUserId : 593079d455db4b969a7fbd4594f568be
     * answerUserName : 593079d455db4b969a7fbd4594f568be
     * answer : 1
     * answerTime : 1
     * createUserAvatar :
     */

    private String id;
    private String reply;
    private String columnId;
    private String contentId;
    private String operationCenterId;
    private String createUserId;
    private String createUserType;
    private String reUserType;
    private String createUserName;
    private String createUserNickName;
    private String createUserMobile;
    private long createTime;
    private String replyAuditState;
    private String replyAuditResult;
    private String replyAuditUserName;
    private long replyAuditTime;
    private String replyAuditDesc;
    private String deleteState;
    private String deleteTime;
    private String hadAnswered;
    private String answerUserId;
    private String answerUserName;
    private String answer;
    private String answerTime;
    private String createUserAvatar;
    private List<AnswerItemEntity> answerList;

    private String type;
    private String topReplyId;
    private String upReplyId;
    private String reUserName;
    private String reUserId;

    public String getReUserType() {
        return reUserType;
    }

    public void setReUserType(String reUserType) {
        this.reUserType = reUserType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getOperationCenterId() {
        return operationCenterId;
    }

    public void setOperationCenterId(String operationCenterId) {
        this.operationCenterId = operationCenterId;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public String getCreateUserType() {
        return createUserType;
    }

    public void setCreateUserType(String createUserType) {
        this.createUserType = createUserType;
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

    public String getCreateUserMobile() {
        return createUserMobile;
    }

    public void setCreateUserMobile(String createUserMobile) {
        this.createUserMobile = createUserMobile;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getReplyAuditState() {
        return replyAuditState;
    }

    public void setReplyAuditState(String replyAuditState) {
        this.replyAuditState = replyAuditState;
    }

    public String getReplyAuditResult() {
        return replyAuditResult;
    }

    public void setReplyAuditResult(String replyAuditResult) {
        this.replyAuditResult = replyAuditResult;
    }

    public long getReplyAuditTime() {
        return replyAuditTime;
    }

    public void setReplyAuditTime(long replyAuditTime) {
        this.replyAuditTime = replyAuditTime;
    }

    public String getReplyAuditUserName() {
        return replyAuditUserName;
    }

    public void setReplyAuditUserName(String replyAuditUserName) {
        this.replyAuditUserName = replyAuditUserName;
    }

    public String getReplyAuditDesc() {
        return replyAuditDesc;
    }

    public void setReplyAuditDesc(String replyAuditDesc) {
        this.replyAuditDesc = replyAuditDesc;
    }

    public String getDeleteState() {
        return deleteState;
    }

    public void setDeleteState(String deleteState) {
        this.deleteState = deleteState;
    }

    public String getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(String deleteTime) {
        this.deleteTime = deleteTime;
    }

    public String getHadAnswered() {
        return hadAnswered;
    }

    public void setHadAnswered(String hadAnswered) {
        this.hadAnswered = hadAnswered;
    }

    public String getAnswerUserId() {
        return answerUserId;
    }

    public void setAnswerUserId(String answerUserId) {
        this.answerUserId = answerUserId;
    }

    public String getAnswerUserName() {
        return answerUserName;
    }

    public void setAnswerUserName(String answerUserName) {
        this.answerUserName = answerUserName;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswerTime() {
        return answerTime;
    }

    public void setAnswerTime(String answerTime) {
        this.answerTime = answerTime;
    }

    public String getCreateUserAvatar() {
        return createUserAvatar;
    }

    public void setCreateUserAvatar(String createUserAvatar) {
        this.createUserAvatar = createUserAvatar;
    }

    public List<AnswerItemEntity> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(List<AnswerItemEntity> answerList) {
        this.answerList = answerList;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTopReplyId() {
        return topReplyId;
    }

    public void setTopReplyId(String topReplyId) {
        this.topReplyId = topReplyId;
    }

    public String getUpReplyId() {
        return upReplyId;
    }

    public void setUpReplyId(String upReplyId) {
        this.upReplyId = upReplyId;
    }

    public String getReUserName() {
        return reUserName;
    }

    public void setReUserName(String reUserName) {
        this.reUserName = reUserName;
    }

    public String getReUserId() {
        return reUserId;
    }

    public void setReUserId(String reUserId) {
        this.reUserId = reUserId;
    }
}
