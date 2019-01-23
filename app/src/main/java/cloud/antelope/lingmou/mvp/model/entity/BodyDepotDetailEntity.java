package cloud.antelope.lingmou.mvp.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ChenXinming on 2018/1/23.
 * description:
 */

public class BodyDepotDetailEntity implements Parcelable ,SearchBean{


    /**
     * id : 5afbfc45000200232017019c
     * cameraId : 538378652
     * cameraName : 贵溪东门阳光豪庭北区南门Z
     * captureTime : 1526463570000
     * longitude : 117.223220825195312
     * latitide : 28.2842693328857422
     * scenePath : https://jxsr-oss1.antelopecloud.cn/files2/538378652/5afbfc512017019c0425e960?access_token=538378652_0_1557999555_ecb7847f2ea7f04fd5d302e4a83c53c0&key=%2Fiermu%2Fai%2F137898529163_538378652_1526463570128_1526463569542340495.jpg
     * createTime : 1526463574872
     * bodyPath : https://jxsr-oss1.antelopecloud.cn/files?access_token=2147500032_0_1553226734_100c6f794e3ac9c7037f7e569489c16c&obj_id=5afbfc568000400004395b41
     * bodyRect : 1003,277,89,247
     */

    public String id;
    public String cameraId;
    public String cameraName;
    public String captureTime;
    public String longitude;
    public String latitide;
    public String scenePath;
    public String createTime;
    public String bodyPath;
    public String bodyRect;
    public String isCollect;
    public String collectId;

    public BodyDepotDetailEntity() {
    }

    protected BodyDepotDetailEntity(Parcel in) {
        id = in.readString();
        cameraId = in.readString();
        cameraName = in.readString();
        captureTime = in.readString();
        longitude = in.readString();
        latitide = in.readString();
        scenePath = in.readString();
        createTime = in.readString();
        bodyPath = in.readString();
        bodyRect = in.readString();
        isCollect = in.readString();
        collectId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(cameraId);
        dest.writeString(cameraName);
        dest.writeString(captureTime);
        dest.writeString(longitude);
        dest.writeString(latitide);
        dest.writeString(scenePath);
        dest.writeString(createTime);
        dest.writeString(bodyPath);
        dest.writeString(bodyRect);
        dest.writeString(isCollect);
        dest.writeString(collectId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BodyDepotDetailEntity> CREATOR = new Creator<BodyDepotDetailEntity>() {
        @Override
        public BodyDepotDetailEntity createFromParcel(Parcel in) {
            return new BodyDepotDetailEntity(in);
        }

        @Override
        public BodyDepotDetailEntity[] newArray(int size) {
            return new BodyDepotDetailEntity[size];
        }
    };
}
