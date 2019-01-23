package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;

/**
 * Created by ChenXinming on 2017/11/20.
 * description:
 */

public class UpdatePswRequest implements Serializable {

    /**
     * userId : 123456
     * oldPwd : afrfafafd
     * newPwd : fasfafwer
     */

    private String oldPwd;
    private String pwd;

    public String getOldPwd() {
        return oldPwd;
    }

    public void setOldPwd(String oldPwd) {
        this.oldPwd = oldPwd;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}
