package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 作者：安兴亚
 * 创建日期：2017/07/26
 * 邮箱：anxingya@lingdanet.com
 * 描述：TODO
 */

public class CommentListEntity implements Serializable {


    /**
     * pageNum : 1
     * pageSize : 10
     * total : 3
     * list : [{"id":"593079d455db4b969a7fbd4594f568be","reply":"593079d455db4b969a7fbd4594f568be","columnId":"593079d455db4b969a7fbd4594f568be","contentId":"1","operationCenterId":"593079d455db4b969a7fbd4594f568be","replyUserId":"593079d455db4b969a7fbd4594f568be","replyUserName":"593079d455db4b969a7fbd4594f568be","replyUserMobile":"1","replyTime":"593079d455db4b969a7fbd4594f568be","replyAuditState":"593079d455db4b969a7fbd4594f568be","replyAuditResult":"593079d455db4b969a7fbd4594f568be","replyAuditTime":"1","deleteState":"1","deleteTime":"1","hadAnswered":"593079d455db4b969a7fbd4594f568be","answerUserId":"593079d455db4b969a7fbd4594f568be","answerUserName":"593079d455db4b969a7fbd4594f568be","answer":"1","answerTime":"1"}]
     */

    private int pageNum;
    private int pageSize;
    private int total;
    private List<CommentItemEntity> list;

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<CommentItemEntity> getList() {
        return list;
    }

    public void setList(List<CommentItemEntity> list) {
        this.list = list;
    }

}
