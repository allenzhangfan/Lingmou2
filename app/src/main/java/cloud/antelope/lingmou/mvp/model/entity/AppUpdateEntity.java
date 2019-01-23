package cloud.antelope.lingmou.mvp.model.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 作者：安兴亚
 * 创建日期：2017/08/09
 * 邮箱：anxingya@lingdanet.com
 * 描述：APP更新信息响应实体
 */

public class AppUpdateEntity implements Serializable {


    /**
     * android : {"version_name":"朝阳群众v2.2.0","version_number":23,"version_description":"发现新版本，请立即更新体验！","force_update":false,"package_url":"https://case.netposa.com/static/bin/cyqz_v2.2.0.apk \n\n"}
     * ios : {"version_name":"朝阳群众v2.2.0","version_number":23,"version_description":"发现新版本，请立即更新体验！","force_update":false,"package_url":"https://itunes.apple.com/app/id1136541548"}
     */
    @SerializedName("android")
    private UpdateInfo android;

    public UpdateInfo getUpdateInfo() {
        return android;
    }

    public void setUpdateInfo(UpdateInfo updateInfo) {
        this.android = updateInfo;
    }
}
