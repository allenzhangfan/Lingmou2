package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;

/**
 * Created by ChenXinming on 2017/11/17.
 * description:登录返回的Bean
 */

public class LoginEntity extends LmBaseData implements Serializable {


    /**
     * modifyTime : 1525675879775
     * userId : 101000123640
     * token : eyJhbGciOiJIUzI1NiJ9.eyJvcmdhbml6YXRpb25JZCI6MTAwMTAxMDAxNzQwLCJleHQiOjE1NTcyMTcwNjcyOTAsInVpZCI6MTAxMDAwMTIzNjQwLCJ2YWxpZFN0YXRlIjoxMDQ0MDYsInJvbGVJZCI6MTAwMDAwMTExMDI5LCJ2YWxpZFRpbWUiOjE2MjAzNjk0NjA0MjAsIm9wdENlbnRlcklkIjoxMDAxMDAwMDA2MDMsImlhdCI6MTUyNTY4MTA2NzI5MH0.PT_aIbWz6jNF71p9mauUAiTbtqsNoF3fqLaHv_W-Lpk
     */

    private String token;
    private String phoneNum;

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
