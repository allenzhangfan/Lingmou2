package cloud.antelope.lingmou.mvp.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by ChenXinming on 2017/11/20.
 * description:
 */

public class UserInfoEntity implements Parcelable {

    public UserInfoBean userInfo;

    public List<UserInfoPrivilegeEntity> privileges;

    public List<UserModulesEntity> modules;

    public List<WebPrivilege> webPrivileges;


    public UserInfoEntity() {
    }


    protected UserInfoEntity(Parcel in) {
        userInfo = in.readParcelable(UserInfoBean.class.getClassLoader());
        privileges = in.createTypedArrayList(UserInfoPrivilegeEntity.CREATOR);
        modules = in.createTypedArrayList(UserModulesEntity.CREATOR);
        webPrivileges = in.createTypedArrayList(WebPrivilege.CREATOR);
    }

    public static final Creator<UserInfoEntity> CREATOR = new Creator<UserInfoEntity>() {
        @Override
        public UserInfoEntity createFromParcel(Parcel in) {
            return new UserInfoEntity(in);
        }

        @Override
        public UserInfoEntity[] newArray(int size) {
            return new UserInfoEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(userInfo, flags);
        dest.writeTypedList(privileges);
        dest.writeTypedList(modules);
        dest.writeTypedList(webPrivileges);
    }
}
