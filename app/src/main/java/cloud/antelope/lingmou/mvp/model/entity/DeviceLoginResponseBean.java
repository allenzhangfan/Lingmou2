package cloud.antelope.lingmou.mvp.model.entity;

import java.io.Serializable;

/**
 * Created by liaolei on 2017/11/14.
 */
public class DeviceLoginResponseBean implements Serializable {
    public BroadcastBean data;
    public String message;
    public String request_id;

    public class BroadcastBean implements Serializable {
        public boolean is_bind; //设备是否已绑定
        public String token; //羚羊云设备推流及调用接口用的token，存在有效期，过去需重新登录
        public BroadcastConfigBean config; //设备配置信息
        public String push_url; //直播推流地址
        public String init_string; //设备登录羚羊云的初始化配置串
    }

    public class BroadcastConfigBean implements Serializable {
        public String osd; //摄像机名称，OSD内容
        public String video; //视频编码类型，mainstream：主流码，substream：子流码
        public int bitrate; //码率 单位Kbps
        public int bitlevel; //图像质量：1最低，2较低，3低，4中等，5较高，6最高
        public int alarm_push; //是否启用移动侦测（即报警），0否，1是
        public int alarm_interval; //图片上传最小时间间隔，单位秒
    }

}
