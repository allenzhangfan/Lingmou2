package cloud.antelope.lingmou.mvp.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ChenXinming on 2017/12/28.
 * description:
 */

public class FaceDepotEntity implements Parcelable {
    public int total;
    public List<FaceDepotDetailEntity> face;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.total);
        dest.writeTypedList(this.face);
    }

    public FaceDepotEntity() {
    }

    protected FaceDepotEntity(Parcel in) {
        this.total = in.readInt();
        this.face = in.createTypedArrayList(FaceDepotDetailEntity.CREATOR);
    }

    public static final Parcelable.Creator<FaceDepotEntity> CREATOR = new Parcelable.Creator<FaceDepotEntity>() {
        @Override
        public FaceDepotEntity createFromParcel(Parcel source) {
            return new FaceDepotEntity(source);
        }

        @Override
        public FaceDepotEntity[] newArray(int size) {
            return new FaceDepotEntity[size];
        }
    };
}
