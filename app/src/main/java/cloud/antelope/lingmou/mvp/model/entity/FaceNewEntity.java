package cloud.antelope.lingmou.mvp.model.entity;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ChenXinming on 2017/11/21.
 * description:
 */

public class FaceNewEntity implements Parcelable {

    public String id;
    public String cameraId;
    public String cameraName;
    public String captureTime;
    public String longitude;
    public String latitide;
    public String scenePath;
    public String createTime;
    public String updateTime;
    public Integer age;
    public String facePath;
    public String faceRect;
    public String bodyPath;
    public String bodyRect;
    public boolean isSelect;
    public boolean mIsChecked;
    public int position;
    public float score;
    public boolean checkable;


    public FaceNewEntity() {
    }

    protected FaceNewEntity(Parcel in) {
        id = in.readString();
        cameraId = in.readString();
        cameraName = in.readString();
        captureTime = in.readString();
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
        facePath = in.readString();
        faceRect = in.readString();
        bodyPath = in.readString();
        bodyRect = in.readString();
        isSelect = in.readByte() != 0;
        mIsChecked = in.readByte() != 0;
        position = in.readInt();
        score = in.readFloat();
        checkable = in.readByte() != 0;
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
        dest.writeString(updateTime);
        if (age == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(age);
        }
        dest.writeString(facePath);
        dest.writeString(faceRect);
        dest.writeString(bodyPath);
        dest.writeString(bodyRect);
        dest.writeByte((byte) (isSelect ? 1 : 0));
        dest.writeByte((byte) (mIsChecked ? 1 : 0));
        dest.writeInt(position);
        dest.writeFloat(score);
        dest.writeByte((byte) (checkable ? 1 : 0));
    }

    public static final Creator<FaceNewEntity> CREATOR = new Creator<FaceNewEntity>() {
        @Override
        public FaceNewEntity createFromParcel(Parcel in) {
            return new FaceNewEntity(in);
        }

        @Override
        public FaceNewEntity[] newArray(int size) {
            return new FaceNewEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

}
