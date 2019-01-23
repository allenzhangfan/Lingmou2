package cloud.antelope.lingmou.mvp.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ChenXinming on 2017/12/28.
 * description:
 */

public class FaceDepotDetailEntity implements Parcelable ,SearchBean{


    public String id;
    public String cameraId;
    public String cameraName;
    public String captureTime;
    public String address;
    public String longitude;
    public String latitide;
    public String scenePath;
    public String createTime;
    public String updateTime;
    public Integer age;
    public Double confidence;
    public String facePath;
    public String faceRect;
    public String isCollect;
    public String collectId;


    public FaceDepotDetailEntity() {
    }

    protected FaceDepotDetailEntity(Parcel in) {
        id = in.readString();
        cameraId = in.readString();
        cameraName = in.readString();
        captureTime = in.readString();
        address = in.readString();
        longitude = in.readString();
        latitide = in.readString();
        scenePath = in.readString();
        createTime = in.readString();
        updateTime = in.readString();
        if (in.readByte() == 0) {
            age = null;
        } else {
            age = in.readInt();
        }
        if (in.readByte() == 0) {
            confidence = null;
        } else {
            confidence = in.readDouble();
        }
        facePath = in.readString();
        faceRect = in.readString();
        isCollect = in.readString();
        collectId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(cameraId);
        dest.writeString(cameraName);
        dest.writeString(captureTime);
        dest.writeString(address);
        dest.writeString(longitude);
        dest.writeString(latitide);
        dest.writeString(scenePath);
        dest.writeString(createTime);
        dest.writeString(updateTime);
        if (age == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(age);
        }
        if (confidence == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(confidence);
        }
        dest.writeString(facePath);
        dest.writeString(faceRect);
        dest.writeString(isCollect);
        dest.writeString(collectId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FaceDepotDetailEntity> CREATOR = new Creator<FaceDepotDetailEntity>() {
        @Override
        public FaceDepotDetailEntity createFromParcel(Parcel in) {
            return new FaceDepotDetailEntity(in);
        }

        @Override
        public FaceDepotDetailEntity[] newArray(int size) {
            return new FaceDepotDetailEntity[size];
        }
    };
}
