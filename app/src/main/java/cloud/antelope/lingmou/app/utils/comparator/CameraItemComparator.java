package cloud.antelope.lingmou.app.utils.comparator;


import java.util.Comparator;

import cloud.antelope.lingmou.mvp.model.entity.CameraItem;

/**
 * 作者：安兴亚
 * 创建日期：2017/12/11
 * 邮箱：anxingya@lingdanet.com
 * 描述：TODO
 */

public class CameraItemComparator implements Comparator<CameraItem> {

    @Override
    public int compare(CameraItem o1, CameraItem o2) {
        return o1.getCamera_status() - o2.getCamera_status();
    }
}
