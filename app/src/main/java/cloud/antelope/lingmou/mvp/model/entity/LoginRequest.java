package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;

/**
 * 作者：陈新明
 * 创建日期：2018/05/07
 * 邮箱：chenxinming@antelop.cloud
 * 描述：TODO
 */
public class LoginRequest implements Serializable{


    /**
     * loginName : liumin
     * userPwd : a123456
     * loginType : 1
     * userInfoExtVo : {"deviceUniqueId":"F9B11BBC-4C5C-48BB-945D-99313D1D04C1","deviceType":2,"registrationId":"171976fa8aaae521517"}
     */

    private String phoneNum;
    private String identifyCode;
    private String loginName;
    private String userPwd;
    private int loginType;
    private UserInfoExtVoBean userInfoExtVo;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }

    public int getLoginType() {
        return loginType;
    }

    public void setLoginType(int loginType) {
        this.loginType = loginType;
    }

    public UserInfoExtVoBean getUserInfoExtVo() {
        return userInfoExtVo;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getIdentifyCode() {
        return identifyCode;
    }

    public void setIdentifyCode(String identifyCode) {
        this.identifyCode = identifyCode;
    }

    public void setUserInfoExtVo(UserInfoExtVoBean userInfoExtVo) {
        this.userInfoExtVo = userInfoExtVo;
    }

    public static class UserInfoExtVoBean {
        /**
         * deviceUniqueId : F9B11BBC-4C5C-48BB-945D-99313D1D04C1
         * deviceType : 2
         * registrationId : 171976fa8aaae521517
         */

        private String deviceUniqueId;
        private int deviceType;
        private String registrationId;

        public String getDeviceUniqueId() {
            return deviceUniqueId;
        }

        public void setDeviceUniqueId(String deviceUniqueId) {
            this.deviceUniqueId = deviceUniqueId;
        }

        public int getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(int deviceType) {
            this.deviceType = deviceType;
        }

        public String getRegistrationId() {
            return registrationId;
        }

        public void setRegistrationId(String registrationId) {
            this.registrationId = registrationId;
        }
    }
}
