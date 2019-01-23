package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 作者：陈新明
 * 创建日期：2018/05/08
 * 邮箱：chenxinming@antelop.cloud
 * 描述：TODO
 */
public class OrgCameraParentEntity implements Serializable {
    private int resultSize;
    private List<OrgCameraEntity> resultList;

    public int getResultSize() {
        return resultSize;
    }

    public void setResultSize(int resultSize) {
        this.resultSize = resultSize;
    }

    public List<OrgCameraEntity> getResultList() {
        return resultList;
    }

    public void setResultList(List<OrgCameraEntity> resultList) {
        this.resultList = resultList;
    }
}
