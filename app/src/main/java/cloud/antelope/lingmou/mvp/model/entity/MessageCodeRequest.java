package cloud.antelope.lingmou.mvp.model.entity;

public class MessageCodeRequest {
    private String loginName;
    private String phoneNum;

    public MessageCodeRequest(String loginName,String phoneNum) {
        this.loginName = loginName;
        this.phoneNum = phoneNum;
    }
}
