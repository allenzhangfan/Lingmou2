package com.lzy.imagepicker.bean;

/**
 * 作者：安兴亚
 * 创建日期：2017/11/02
 * 邮箱：anxingya@lingdanet.com
 * 描述：TODO
 */

public class LoadErrorEntity {

    public int errCode;
    public String errMsg;

    public LoadErrorEntity(int errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
