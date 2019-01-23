package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;

/**
 * 作者：陈新明
 * 创建日期：2018/05/09
 * 邮箱：chenxinming@antelop.cloud
 * 描述：TODO
 */
public class CameraNewStreamRequest implements Serializable {
    private Long[] cids;

    public Long[] getCids() {
        return cids;
    }

    public void setCids(Long[] cids) {
        this.cids = cids;
    }
}
