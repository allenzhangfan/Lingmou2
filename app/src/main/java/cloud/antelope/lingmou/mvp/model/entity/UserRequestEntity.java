/*
 * Copyright (C) 2016 LingDaNet.Co.Ltd. All Rights Reserved.
 */
package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;

/**
 * 作者：陈新明
 * 创建日期：2016/10/21
 * 邮箱：chenxinming@lingdanet.com
 * 描述：注册对象
 */

public class UserRequestEntity implements Serializable {
    //字符串的命名需要和服务器参数名一致，否则报错
    private String uid;
    private String username;
    private String verificationCode;
    private String password;
    private String newPassword;
    private String userType;
    private String ocId;
    private String phoneNumber;
    private String verifyType;
    private String oldPhoneNo;
    private String newPhoneNo;
    private String oldVerificationCode;
    private String newVerificationCode;


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getOcId() {
        return ocId;
    }

    public void setOcId(String ocId) {
        this.ocId = ocId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getVerifyType() {
        return verifyType;
    }

    public void setVerifyType(String verifyType) {
        this.verifyType = verifyType;
    }

    public String getOldPhoneNo() {
        return oldPhoneNo;
    }

    public void setOldPhoneNo(String oldPhoneNo) {
        this.oldPhoneNo = oldPhoneNo;
    }

    public String getNewPhoneNo() {
        return newPhoneNo;
    }

    public void setNewPhoneNo(String newPhoneNo) {
        this.newPhoneNo = newPhoneNo;
    }

    public String getOldVerificationCode() {
        return oldVerificationCode;
    }

    public void setOldVerificationCode(String oldVerificationCode) {
        this.oldVerificationCode = oldVerificationCode;
    }

    public String getNewVerificationCode() {
        return newVerificationCode;
    }

    public void setNewVerificationCode(String newVerificationCode) {
        this.newVerificationCode = newVerificationCode;
    }
}
