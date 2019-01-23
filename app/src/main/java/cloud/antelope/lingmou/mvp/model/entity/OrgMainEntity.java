package cloud.antelope.lingmou.mvp.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * 作者：陈新明
 * 创建日期：2018/05/08
 * 邮箱：chenxinming@antelop.cloud
 * 描述：TODO
 */
public class OrgMainEntity implements Parcelable {

    /**
     * id : 100101001743
     * organizationName : 二级部门Test
     * parentId : 100101001741
     * centerId : 100100000603
     * organizationDesc : 二级部门Test描述
     * organizationType : 1008000
     * treeDesc : CxmTest/一级部门Test/
     * orgSort : 3
     * createTime : 1525678093991
     */

    public String id;
    public String organizationName;
    public String parentId;
    public String centerId;
    public String organizationDesc;
    public int organizationType;
    public String treeDesc;
    public int orgSort;
    public String createTime;
    public boolean mIsRootOrg;
    public String mAliasName;

    public boolean level1;

    public OrgMainEntity (OrgMainEntity entity){
        this.id = entity.id;
        this.organizationName = entity.organizationName;
        this.parentId = entity.parentId;
        this.centerId = entity.centerId;
        this.organizationDesc = entity.organizationDesc;
        this.organizationType = entity.organizationType;
        this.treeDesc = entity.treeDesc;
        this.orgSort = entity.orgSort;
        this.createTime = entity.createTime;
        this.mIsRootOrg = entity.mIsRootOrg;
        this.mAliasName = entity.mAliasName;
        this.level1 = entity.level1;
    }

    protected OrgMainEntity(Parcel in) {
        id = in.readString();
        organizationName = in.readString();
        parentId = in.readString();
        centerId = in.readString();
        organizationDesc = in.readString();
        organizationType = in.readInt();
        treeDesc = in.readString();
        orgSort = in.readInt();
        createTime = in.readString();
        mIsRootOrg = in.readByte() != 0;
        mAliasName = in.readString();
        level1 = in.readByte() != 0;
    }

    public static final Creator<OrgMainEntity> CREATOR = new Creator<OrgMainEntity>() {
        @Override
        public OrgMainEntity createFromParcel(Parcel in) {
            return new OrgMainEntity(in);
        }

        @Override
        public OrgMainEntity[] newArray(int size) {
            return new OrgMainEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(organizationName);
        dest.writeString(parentId);
        dest.writeString(centerId);
        dest.writeString(organizationDesc);
        dest.writeInt(organizationType);
        dest.writeString(treeDesc);
        dest.writeInt(orgSort);
        dest.writeString(createTime);
        dest.writeByte((byte) (mIsRootOrg ? 1 : 0));
        dest.writeString(mAliasName);
        dest.writeByte((byte) (level1 ? 1 : 0));
    }
}
