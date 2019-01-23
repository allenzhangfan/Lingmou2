package cloud.antelope.lingmou.mvp.model.entity;

import java.util.List;

/**
 * 作者：陈新明
 * 创建日期：2017/7/12
 * 邮箱：
 * 描述：TODO
 */

public class CameraStreamRequest {

    private List<String> cameraIds;

    public List<String> getCameraIds() {
        return cameraIds;
    }

    public void setCameraIds(List<String> cameraIds) {
        this.cameraIds = cameraIds;
    }
}
