package cloud.antelope.lingmou.mvp.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 作者：安兴亚
 * 创建日期：2018/01/25
 * 邮箱：anxingya@lingdanet.com
 * 描述：TODO
 */

public class DetailCommonEntity implements Parcelable {
    public String point;
    public String srcImg;
    public String faceImg;
    public String deviceName;
    public long endTime;
    public String id;
    public String collectId;
    public String cameraId;
    public String cameraName;
    public long captureTime;
    public int favoritesType;//收藏图片类型
    public boolean collected;
    public CarRect carRect;

    public DetailCommonEntity() {
    }

    protected DetailCommonEntity(Parcel in) {
        point = in.readString();
        srcImg = in.readString();
        faceImg = in.readString();
        deviceName = in.readString();
        endTime = in.readLong();
        id = in.readString();
        collectId = in.readString();
        cameraId = in.readString();
        cameraName = in.readString();
        captureTime = in.readLong();
        favoritesType = in.readInt();
        collected = in.readByte() != 0;
        carRect = in.readParcelable(CarRect.class.getClassLoader());
    }

    public static final Creator<DetailCommonEntity> CREATOR = new Creator<DetailCommonEntity>() {
        @Override
        public DetailCommonEntity createFromParcel(Parcel in) {
            return new DetailCommonEntity(in);
        }

        @Override
        public DetailCommonEntity[] newArray(int size) {
            return new DetailCommonEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(point);
        dest.writeString(srcImg);
        dest.writeString(faceImg);
        dest.writeString(deviceName);
        dest.writeLong(endTime);
        dest.writeString(id);
        dest.writeString(collectId);
        dest.writeString(cameraId);
        dest.writeString(cameraName);
        dest.writeLong(captureTime);
        dest.writeInt(favoritesType);
        dest.writeByte((byte) (collected ? 1 : 0));
        dest.writeParcelable(carRect, flags);
    }
}
