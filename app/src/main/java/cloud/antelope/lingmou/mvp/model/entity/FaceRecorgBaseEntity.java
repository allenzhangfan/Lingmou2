package cloud.antelope.lingmou.mvp.model.entity;

import java.util.List;

/**
 * 作者：陈新明
 * 创建日期：2018/05/17
 * 邮箱：chenxinming@antelop.cloud
 * 描述：TODO
 */
public class FaceRecorgBaseEntity<T> {
    private List<T> face;

    public List<T> getFace() {
        return face;
    }

    public void setFace(List<T> face) {
        this.face = face;
    }
}
