/*
 * Copyright (C) 2017 LingDaNet.Co.Ltd. All Rights Reserved.
 */

package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;

/**
 * 作者：陈新明
 * 创建日期：2017/6/26
 * 邮箱：
 * 描述：TODO
 */

public class OperationEntity implements Serializable {

    /**
     * caseColumnId : 828a2a826f07490ea5ebf36784b2c998
     * operationCenterId : aabbaabbaabbaabbaabbaabbaabbaabb
     * tipColumnId : 39e0fd622a314c6bbbba7f6bdcf4ab7f
     */

    /**
     * 告警的栏目Id.
     */
    private String caseColumnId;
    /**
     * 运营中心的Id.
     */
    private String operationCenterId;
    /**
     * 线索的栏目Id.
     */
    private String tipColumnId;

    public String getCaseColumnId() {
        return caseColumnId;
    }

    public void setCaseColumnId(String caseColumnId) {
        this.caseColumnId = caseColumnId;
    }

    public String getOperationCenterId() {
        return operationCenterId;
    }

    public void setOperationCenterId(String operationCenterId) {
        this.operationCenterId = operationCenterId;
    }

    public String getTipColumnId() {
        return tipColumnId;
    }

    public void setTipColumnId(String tipColumnId) {
        this.tipColumnId = tipColumnId;
    }
}
