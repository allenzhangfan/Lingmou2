package cloud.antelope.lingmou.mvp.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.DataSupport;

public class CarBrandBean extends DataSupport implements Parcelable{
    /**
     * id : 15
     * name : 理念
     * code : 117364
     * simpleSpelling : ln
     * fullSpelling : linian
     * remark :
     * isDelete : 0
     * pic : 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
     */
   /* public String label;
    public String brandPath;
    public Long value;
    public String letter;*/

    public boolean firstOfLetter;
    public boolean selected;
    public int id;
    public String name;
    public Long code;
    public String letter;
    public String simpleSpelling;
    public String fullSpelling;
    public String remark;
    public int isDelete;
    public String pic;

    protected CarBrandBean(Parcel in) {
        firstOfLetter = in.readByte() != 0;
        selected = in.readByte() != 0;
        id = in.readInt();
        name = in.readString();
        if (in.readByte() == 0) {
            code = null;
        } else {
            code = in.readLong();
        }
        letter = in.readString();
        simpleSpelling = in.readString();
        fullSpelling = in.readString();
        remark = in.readString();
        isDelete = in.readInt();
        pic = in.readString();
    }

    public static final Creator<CarBrandBean> CREATOR = new Creator<CarBrandBean>() {
        @Override
        public CarBrandBean createFromParcel(Parcel in) {
            return new CarBrandBean(in);
        }

        @Override
        public CarBrandBean[] newArray(int size) {
            return new CarBrandBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (firstOfLetter ? 1 : 0));
        dest.writeByte((byte) (selected ? 1 : 0));
        dest.writeInt(id);
        dest.writeString(name);
        if (code == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(code);
        }
        dest.writeString(letter);
        dest.writeString(simpleSpelling);
        dest.writeString(fullSpelling);
        dest.writeString(remark);
        dest.writeInt(isDelete);
        dest.writeString(pic);
    }
}