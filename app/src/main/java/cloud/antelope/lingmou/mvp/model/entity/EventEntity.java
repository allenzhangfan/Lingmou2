package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 作者：安兴亚
 * 创建日期：2017/07/05
 * 邮箱：anxingya@lingdanet.com
 * 描述：事件存储的实体类
 */

public class EventEntity implements Serializable {

    private int channel_id;
    private String request_id;
    private String subject;
    private BodyBean body;
    private List<AttachmentBean> attachments;

    public int getChannelId() {
        return channel_id;
    }

    public void setChannelId(int channelId) {
        this.channel_id = channelId;
    }

    public String getRequestId() {
        return request_id;
    }

    public void setRequestId(String requestId) {
        this.request_id = requestId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public BodyBean getBody() {
        return body;
    }

    public void setBody(BodyBean body) {
        this.body = body;
    }

    public List<AttachmentBean> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentBean> attachments) {
        this.attachments = attachments;
    }

    public static class BodyBean {
        /**
         * columnId :
         * caseId :
         * isTop :
         * isNeedAudit : 0
         * content :
         * "token": "fafadfasfadsfadf",
         * "upLoadToken": "4143124141432243"
         * "tipType": "11",
         * isAllowReply :
         * uuid: "fafadsfadsfqfsdfa"
         */

        private String columnId;
        private String caseId;
        private String isTop;
        private String isNeedAudit;
        private String isAllowReply;
        private String content;
        private String token;
        private String upLoadToken;
        private String tipType;
        private String gpsAddr;
        private String gpsX;
        private String gpsY;
        private String uuid;

        public String getColumnId() {
            return columnId;
        }

        public void setColumnId(String columnId) {
            this.columnId = columnId;
        }

        public String getCaseId() {
            return caseId;
        }

        public void setCaseId(String caseId) {
            this.caseId = caseId;
        }

        public String getIsTop() {
            return isTop;
        }

        public void setIsTop(String isTop) {
            this.isTop = isTop;
        }

        public String getIsNeedAudit() {
            return isNeedAudit;
        }

        public void setIsNeedAudit(String isNeedAudit) {
            this.isNeedAudit = isNeedAudit;
        }

        public String getIsAllowReply() {
            return isAllowReply;
        }

        public void setIsAllowReply(String isAllowReply) {
            this.isAllowReply = isAllowReply;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getUpLoadToken() {
            return upLoadToken;
        }

        public void setUpLoadToken(String upLoadToken) {
            this.upLoadToken = upLoadToken;
        }

        public String getTipType() {
            return tipType;
        }

        public void setTipType(String tipType) {
            this.tipType = tipType;
        }

        public String getGpsAddr() {
            return gpsAddr;
        }

        public void setGpsAddr(String gpsAddr) {
            this.gpsAddr = gpsAddr;
        }

        public String getGpsX() {
            return gpsX;
        }

        public void setGpsX(String gpsX) {
            this.gpsX = gpsX;
        }

        public String getGpsY() {
            return gpsY;
        }

        public void setGpsY(String gpsY) {
            this.gpsY = gpsY;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }
    }
}
