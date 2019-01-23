package cloud.antelope.lingmou.mvp.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 作者：陈新明
 * 创建日期：2018/05/10
 * 邮箱：chenxinming@antelop.cloud
 * 描述：TODO
 */
public class UserInfoBean implements Parcelable {
    public String id;
    public String loginName;
    public String realName;
    public Long userType;
    public String organizationId;
    public String optCenterId;
    public String phoneNum;
    public String idCard;
    public String userAvatar;
    public String cid;
    public String sn;
    public String brand;
    public String isSolos;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.loginName);
        dest.writeString(this.realName);
        dest.writeValue(this.userType);
        dest.writeString(this.organizationId);
        dest.writeString(this.optCenterId);
        dest.writeString(this.phoneNum);
        dest.writeString(this.idCard);
        dest.writeString(this.userAvatar);
        dest.writeString(this.cid);
        dest.writeString(this.sn);
        dest.writeString(this.brand);
        dest.writeString(this.isSolos);
    }

    public UserInfoBean() {
    }

    protected UserInfoBean(Parcel in) {
        this.id = in.readString();
        this.loginName = in.readString();
        this.realName = in.readString();
        this.userType = (Long) in.readValue(Long.class.getClassLoader());
        this.organizationId = in.readString();
        this.optCenterId = in.readString();
        this.phoneNum = in.readString();
        this.idCard = in.readString();
        this.userAvatar = in.readString();
        this.cid = in.readString();
        this.sn = in.readString();
        this.brand = in.readString();
        this.isSolos = in.readString();
    }

    public static final Parcelable.Creator<UserInfoBean> CREATOR = new Parcelable.Creator<UserInfoBean>() {
        @Override
        public UserInfoBean createFromParcel(Parcel source) {
            return new UserInfoBean(source);
        }

        @Override
        public UserInfoBean[] newArray(int size) {
            return new UserInfoBean[size];
        }
    };
}
