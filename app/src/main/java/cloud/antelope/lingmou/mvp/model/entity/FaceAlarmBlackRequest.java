package cloud.antelope.lingmou.mvp.model.entity;

import java.util.ArrayList;

/**
 * 作者：陈新明
 * 创建日期：2018/09/18
 * 邮箱：chenxinming@antelop.cloud
 * 描述：TODO
 */
public class FaceAlarmBlackRequest {

    /**
     * pageNo : 1
     * pageSize : 20
     * keyword : 支持姓名/任务名称/设备名称模糊检索
     * taskType : 101501
     * alarmOperationType : 1
     * startTime : 1536573600000
     * endTime : 1536573600000
     * alarmScope : 1
     */

    public Integer pageNo;
    public Integer pageSize;
    public String keyword;
    public Integer taskType;
    public ArrayList<Long> taskTypeList = new ArrayList<>();
    public Integer alarmOperationType;
    public Long startTime;
    public Long endTime;
    public Integer alarmScope;

}
