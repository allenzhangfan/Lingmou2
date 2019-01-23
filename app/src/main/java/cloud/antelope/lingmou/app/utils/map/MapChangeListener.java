package cloud.antelope.lingmou.app.utils.map;

import com.amap.api.maps.model.CameraPosition;

/**
 * 作者：安兴亚
 * 创建日期：2017/11/23
 * 邮箱：anxingya@lingdanet.com
 * 描述：TODO
 */

public interface MapChangeListener {

    void onCameraChange(CameraPosition var1);

    void onCameraChangeFinish(CameraPosition var1);
}
