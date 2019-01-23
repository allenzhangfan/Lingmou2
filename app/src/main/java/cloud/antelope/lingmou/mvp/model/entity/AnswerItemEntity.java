package cloud.antelope.lingmou.mvp.model.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;

/**
 * 作者：安兴亚
 * 创建日期：2018/04/11
 * 邮箱：anxingya@lingdanet.com
 * 描述：TODO
 */
public class AnswerItemEntity implements MultiItemEntity, Serializable {
    /**
     * createUserName :
     * reUserName :
     * createTime : 1523254191810
     * topReplyId : 0edff7ad6c4e4c9996cfa1b42c90e136
     * replyId : 96dcb90a3ac644b586f2d989d4934e36
     * upReplyId :
     * reply :
     * contentId :
     * columnId :
     "reUserType": "4",
     "createUserType": "5",
     * operationCenterId : aabbaabbaabbaabbaabbaabbaabbaabb
     * createUserAvatar : user/v1/cyrest/getAvatarUrlByuid/BC27722E85AE4214995090BDF43AD254
     * reUserAvatar : user/v1/cyrest/getAvatarUrlByuid/BC27722E85AE4214995090BDF43AD254
     */

    private int itemType;

    private String createUserName;
    private String createUserNickName;
    private String createUserId;
    private String reUserId;
    private String reUserName;
    private long createTime;
    private String topReplyId;
    private String replyId;
    private String upReplyId;
    private String reply;
    private String contentId;
    private String columnId;
    private String operationCenterId;
    private String createUserAvatar;
    private String reUserAvatar;
    private String reUserType;
    private String createUserType;

    public String getReUserType() {
        return reUserType;
    }

    public void setReUserType(String reUserType) {
        this.reUserType = reUserType;
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

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
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

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getTopReplyId() {
        return topReplyId;
    }

    public void setTopReplyId(String topReplyId) {
        this.topReplyId = topReplyId;
    }

    public String getReplyId() {
        return replyId;
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }

    public String getUpReplyId() {
        return upReplyId;
    }

    public void setUpReplyId(String upReplyId) {
        this.upReplyId = upReplyId;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public String getOperationCenterId() {
        return operationCenterId;
    }

    public void setOperationCenterId(String operationCenterId) {
        this.operationCenterId = operationCenterId;
    }

    public String getCreateUserAvatar() {
        return createUserAvatar;
    }

    public void setCreateUserAvatar(String createUserAvatar) {
        this.createUserAvatar = createUserAvatar;
    }

    public String getReUserAvatar() {
        return reUserAvatar;
    }

    public void setReUserAvatar(String reUserAvatar) {
        this.reUserAvatar = reUserAvatar;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }
}
