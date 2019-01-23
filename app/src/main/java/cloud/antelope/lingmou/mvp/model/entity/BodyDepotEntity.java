package cloud.antelope.lingmou.mvp.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**
 * 作者：安兴亚
 * 创建日期：2018/01/24
 * 邮箱：anxingya@lingdanet.com
 * 描述：TODO
 */

public class BodyDepotEntity implements Parcelable {
    /**
     "total": 170016,
     "firstResult": 0,
     "maxResults": 72
     "currentPage": 1,
     "pageSize": 72,
     "pageTotal": 2362,
     "list":[BodyDepotDetailEntity]
     */
    public int total;
    public List<BodyDepotDetailEntity> body;


    public BodyDepotEntity() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.total);
        dest.writeTypedList(this.body);
    }

    protected BodyDepotEntity(Parcel in) {
        this.total = in.readInt();
        this.body = in.createTypedArrayList(BodyDepotDetailEntity.CREATOR);
    }

    public static final Creator<BodyDepotEntity> CREATOR = new Creator<BodyDepotEntity>() {
        @Override
        public BodyDepotEntity createFromParcel(Parcel source) {
            return new BodyDepotEntity(source);
        }

        @Override
        public BodyDepotEntity[] newArray(int size) {
            return new BodyDepotEntity[size];
        }
    };
}
