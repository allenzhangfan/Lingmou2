package cloud.antelope.lingmou.app.utils.map;

import com.amap.api.maps.model.LatLng;

/**
 * Created by yiyi.qi on 16/10/10.
 */

public interface ClusterItem {

    /**
     * 返回聚合元素的地理位置
     *
     * @return
     */
    LatLng getPosition();

    int getItemType();

    String getItemId();



}
