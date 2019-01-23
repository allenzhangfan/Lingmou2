package cloud.antelope.lingmou.mvp.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class CarRect implements Parcelable{
    public int leftTopX;
    public int leftTopY;
    public int rightBtmX;
    public int rightBtmY;
    public int width;
    public int height;

    protected CarRect(Parcel in) {
        leftTopX = in.readInt();
        leftTopY = in.readInt();
        rightBtmX = in.readInt();
        rightBtmY = in.readInt();
        width = in.readInt();
        height = in.readInt();
    }

    public static final Creator<CarRect> CREATOR = new Creator<CarRect>() {
        @Override
        public CarRect createFromParcel(Parcel in) {
            return new CarRect(in);
        }

        @Override
        public CarRect[] newArray(int size) {
            return new CarRect[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(leftTopX);
        dest.writeInt(leftTopY);
        dest.writeInt(rightBtmX);
        dest.writeInt(rightBtmY);
        dest.writeInt(width);
        dest.writeInt(height);
    }
}
