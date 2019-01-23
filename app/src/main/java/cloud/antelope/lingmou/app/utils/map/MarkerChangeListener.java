package cloud.antelope.lingmou.app.utils.map;

import com.amap.api.maps.model.Marker;

/**
 * 作者：安兴亚
 * 创建日期：2017/11/18
 * 邮箱：anxingya@lingdanet.com
 * 描述：TODO
 */

public interface MarkerChangeListener {

    /**
     * 当选中的Marker变化时
     *
     * @param marker
     */
    void onSelectMarkerChange(Marker marker);

    /**
     * 当Marker被聚合了
     *
     * @param fromConstructor 聚合来自构造函数
     */
    void onMarkerClustered(boolean fromConstructor);

    /**
     * 当缩放到最大级别了，Marker还在聚合之中
     *
     */
    void onMarkerInMaxZoom();

    /**
     * 当缩放到最大级别了，Marker不在聚合之中
     *
     */
    void onMarkerNotInMaxZoom();
}
