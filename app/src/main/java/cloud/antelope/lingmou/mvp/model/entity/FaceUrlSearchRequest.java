package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;

/**
 * 作者：陈新明
 * 创建日期：2018/05/17
 * 邮箱：chenxinming@antelop.cloud
 * 描述：TODO
 */
public class FaceUrlSearchRequest implements Serializable{
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
