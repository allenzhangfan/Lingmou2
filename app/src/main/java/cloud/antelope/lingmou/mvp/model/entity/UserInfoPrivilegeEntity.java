package cloud.antelope.lingmou.mvp.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 作者：陈新明
 * 创建日期：2018/06/27
 * 邮箱：chenxinming@antelop.cloud
 * 描述：TODO
 */
public class UserInfoPrivilegeEntity implements Parcelable {

    /**
     * id : 100001000238
     * privilegeName : 人脸图库
     * privilegeCode : 2010501
     * menuId : 100001010079
     */

    public String id;
    public String privilegeName;
    public int privilegeCode;
    public String menuId;

    protected UserInfoPrivilegeEntity(Parcel in) {
        id = in.readString();
        privilegeName = in.readString();
        privilegeCode = in.readInt();
        menuId = in.readString();
    }

    public static final Creator<UserInfoPrivilegeEntity> CREATOR = new Creator<UserInfoPrivilegeEntity>() {
        @Override
        public UserInfoPrivilegeEntity createFromParcel(Parcel in) {
            return new UserInfoPrivilegeEntity(in);
        }

        @Override
        public UserInfoPrivilegeEntity[] newArray(int size) {
            return new UserInfoPrivilegeEntity[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrivilegeName() {
        return privilegeName;
    }

    public void setPrivilegeName(String privilegeName) {
        this.privilegeName = privilegeName;
    }

    public int getPrivilegeCode() {
        return privilegeCode;
    }

    public void setPrivilegeCode(int privilegeCode) {
        this.privilegeCode = privilegeCode;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

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
    }
}
