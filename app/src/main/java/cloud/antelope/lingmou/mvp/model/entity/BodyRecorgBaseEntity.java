package cloud.antelope.lingmou.mvp.model.entity;

import java.util.List;

/**
 * 作者：陈新明
 * 创建日期：2018/05/17
 * 邮箱：chenxinming@antelop.cloud
 * 描述：TODO
 */
public class BodyRecorgBaseEntity<T> {
    private List<T> body;

    public List<T> getBody() {
        return body;
    }

    public void setBody(List<T> body) {
        this.body = body;
    }
}
