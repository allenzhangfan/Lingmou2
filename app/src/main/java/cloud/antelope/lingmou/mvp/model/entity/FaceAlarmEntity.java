package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ChenXinming on 2017/12/26.
 * description:
 */

public class FaceAlarmEntity implements Serializable {

    /**
     * currentPage : 1
     * pageSize : 72
     * pageTotal : 149
     * list : [{"vid":"be2db16ebb384d9081e45d8fb6a7a07e","isCollection":null,"orgId":"50","alarmId":"be2db16ebb384d9081e45d8fb6a7a07e","cameraId":"538378299","longitude":114.40512,"latitude":30.494516,"cameraName":"武汉灵达","captureImg":"PFSB:/vdtimg/bimg/data/20171226/18_0/f5ab0fe7bfb12478212a9b52c22150fa","alarmLevel":0,"alarmType":2,"alarmStatus":1,"absTime":1514254178000,"creator":"-1","detail":{"personId":845,"createTime":"2017-12-22 17:00:35","idcard":"","name":"xy","sex":"1","libId":891277,"libName":"武汉布控库","targetImage":"PFSB:/vdtimg/bimg/data/20171223/00_0/33ca6a40c930f4da88be5d7078b411b1","score":87,"nation":"未知"},"createTime":1514254220393,"isEffective":-1,"common":null,"cameraType":"0","installType":"1","srcUrl":"PFSB:/vdtimg/bimg/data/20171226/18_0/8acb4cefdc9a5853f42923ca1ef4186c","isOnlineStatus":null}]
     */

    private int currentPage;
    private int pageSize;
    private int pageTotal;
    private List<FaceAlarmDetailBean> list;

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageTotal() {
        return pageTotal;
    }

    public void setPageTotal(int pageTotal) {
        this.pageTotal = pageTotal;
    }

    public List<FaceAlarmDetailBean> getList() {
        return list;
    }

    public void setList(List<FaceAlarmDetailBean> list) {
        this.list = list;
    }

}
