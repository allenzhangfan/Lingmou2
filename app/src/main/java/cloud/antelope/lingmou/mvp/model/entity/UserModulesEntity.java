package cloud.antelope.lingmou.mvp.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class UserModulesEntity implements Parcelable{

    /**
     * menu_name : 实时视频
     * menu_id : 100001000235
     */

    private String menu_name;
    private String menu_id;

    protected UserModulesEntity(Parcel in) {
        menu_name = in.readString();
        menu_id = in.readString();
    }

    public static final Creator<UserModulesEntity> CREATOR = new Creator<UserModulesEntity>() {
        @Override
        public UserModulesEntity createFromParcel(Parcel in) {
            return new UserModulesEntity(in);
        }

        @Override
        public UserModulesEntity[] newArray(int size) {
            return new UserModulesEntity[size];
        }
    };

    public String getMenu_name() {
        return menu_name;
    }

    public void setMenu_name(String menu_name) {
        this.menu_name = menu_name;
    }

    public String getMenu_id() {
        return menu_id;
    }

    public void setMenu_id(String menu_id) {
        this.menu_id = menu_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(menu_name);
        dest.writeString(menu_id);
    }
}
