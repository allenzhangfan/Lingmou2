package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 作者：陈新明
 * 创建日期：2018/05/17
 * 邮箱：chenxinming@antelop.cloud
 * 描述：TODO
 */
public class FaceUrlSearchBaseEntity<T> implements Serializable {
    private List<T> imgsList;

    public List<T> getImgsList() {
        return imgsList;
    }

    public void setImgsList(List<T> imgsList) {
        this.imgsList = imgsList;
    }
}
