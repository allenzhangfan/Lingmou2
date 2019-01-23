package cloud.antelope.lingmou.mvp.model.entity;


import java.util.List;

/**
 * Created by Administrator on 2017/11/13.
 */

public class OrganizationEntity extends LmBaseData {

    private List<CameraItem> cameras;

    public List<CameraItem> getCameras() {
        return cameras;
    }

    public void setCameras(List<CameraItem> cameras) {
        this.cameras = cameras;
    }

}
