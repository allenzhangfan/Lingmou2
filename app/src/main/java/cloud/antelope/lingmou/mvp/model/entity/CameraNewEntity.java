package cloud.antelope.lingmou.mvp.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 作者：陈新明
 * 创建日期：2017/6/15
 * 邮箱：
 * 描述：TODO
 */

public class CameraNewEntity implements Parcelable {
    public String cameraId;
    public String cameraNo;
    public String cameraAlias;
    public int status;
    public String coverUrl;
    public boolean mHasAlarm;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cameraId);
        dest.writeString(this.cameraNo);
        dest.writeString(this.cameraAlias);
        dest.writeInt(this.status);
        dest.writeString(this.coverUrl);
        dest.writeByte(this.mHasAlarm ? (byte) 1 : (byte) 0);
    }

    public CameraNewEntity() {
    }

    protected CameraNewEntity(Parcel in) {
        this.cameraId = in.readString();
        this.cameraNo = in.readString();
        this.cameraAlias = in.readString();
        this.status = in.readInt();
        this.coverUrl = in.readString();
        this.mHasAlarm = in.readByte() != 0;
    }

    public static final Creator<CameraNewEntity> CREATOR = new Creator<CameraNewEntity>() {
        @Override
        public CameraNewEntity createFromParcel(Parcel source) {
            return new CameraNewEntity(source);
        }

        @Override
        public CameraNewEntity[] newArray(int size) {
            return new CameraNewEntity[size];
        }
    };


    @Override
    public String toString() {
        return "CameraNewEntity{" +
                "cameraId='" + cameraId + '\'' +
                ", cameraNo='" + cameraNo + '\'' +
                ", cameraAlias='" + cameraAlias + '\'' +
                ", status=" + status +
                ", coverUrl='" + coverUrl + '\'' +
                ", mHasAlarm='" + mHasAlarm + '\'' +
                '}';
    }
}
