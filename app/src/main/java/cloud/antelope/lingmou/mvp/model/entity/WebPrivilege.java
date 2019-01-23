package cloud.antelope.lingmou.mvp.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class WebPrivilege implements Parcelable{

    /**
     * id : 100001000141
     * privilegeName : 管理重点人员库
     * privilegeCode : 1060305
     * menuId : 100001010069
     * urlIds : null
     * privilegeType : 1
     */

    public String id;
    public String privilegeName;
    public int privilegeCode;
    public String menuId;
    public int privilegeType;

    protected WebPrivilege(Parcel in) {
        id = in.readString();
        privilegeName = in.readString();
        privilegeCode = in.readInt();
        menuId = in.readString();
        privilegeType = in.readInt();
    }

    public static final Creator<WebPrivilege> CREATOR = new Creator<WebPrivilege>() {
        @Override
        public WebPrivilege createFromParcel(Parcel in) {
            return new WebPrivilege(in);
        }

        @Override
        public WebPrivilege[] newArray(int size) {
            return new WebPrivilege[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(privilegeName);
        dest.writeInt(privilegeCode);
        dest.writeString(menuId);
        dest.writeInt(privilegeType);
    }
}
