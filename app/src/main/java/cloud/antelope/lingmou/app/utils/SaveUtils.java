package cloud.antelope.lingmou.app.utils;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;

import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.jess.arms.http.imageloader.glide.GlideArms;
import com.lingdanet.safeguard.common.constant.CommonConstant;
import com.lingdanet.safeguard.common.utils.FileUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.Utils;
import com.umeng.analytics.MobclickAgent;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.List;

import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.mvp.model.entity.BannerItemEntity;
import cloud.antelope.lingmou.mvp.model.entity.CameraItem;
import cloud.antelope.lingmou.mvp.model.entity.ClueItemEntity;
import cloud.antelope.lingmou.mvp.model.entity.NewsItemEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;
import cloud.antelope.lingmou.mvp.model.entity.SearchTextEntity;
import cloud.antelope.lingmou.mvp.model.entity.UserInfoBean;
import cloud.antelope.lingmou.mvp.model.entity.UserInfoEntity;
import cloud.antelope.lingmou.mvp.model.entity.UserInfoPrivilegeEntity;
import cloud.antelope.lingmou.mvp.model.entity.UserModulesEntity;
import cloud.antelope.lingmou.mvp.model.entity.VictoryItemEntity;
import cloud.antelope.lingmou.mvp.model.entity.WebPrivilege;
import cloud.antelope.lingmou.mvp.ui.activity.AccountActivity;

/**
 * 作者：安兴亚
 * 创建日期：2018/01/05
 * 邮箱：anxingya@lingdanet.com
 * 描述：TODO
 */

public class SaveUtils {

    public static boolean isInLogin = false;

    /**
     * 保存最后一次登录的用户信息.
     *
     * @param userEntity
     */
    public static void saveLastLoginInfo(UserInfoEntity userEntity) {
        // MobclickAgent.onProfileSignIn(String.valueOf(userInfo.getId()));
        UserInfoBean userInfo = userEntity.userInfo;
        SPUtils.getInstance().put(Constants.HAS_LOGIN, true);
        SPUtils.getInstance().put(CommonConstant.UID, String.valueOf(userInfo.id));
        // SPUtils.getInstance().put(Constants.CONFIG_SOLO_CID, userInfo.cid);
        // SPUtils.getInstance().put(Constants.CONFIG_SOLO_SN, userInfo.sn);
        // SPUtils.getInstance().put(Constants.CONFIG_SOLO_BRAND, userInfo.brand);
        SPUtils.getInstance().put(Constants.CONFIG_IS_SOLO, userInfo.isSolos);
        SPUtils.getInstance().put(Constants.CONFIG_LAST_USER_LOGIN_NAME, userInfo.loginName);
        SPUtils.getInstance().put(Constants.CONFIG_LAST_USER_LOGIN_PHONE, userInfo.phoneNum);
        SPUtils.getInstance().put(Constants.CONFIG_LAST_USER_REAL_NAME, userInfo.realName);
        SPUtils.getInstance().put(Constants.CONFIG_OPERATION_ID, userInfo.optCenterId);
        SPUtils.getInstance().put(Constants.CONFIG_ORGANIZATION_ID, userInfo.organizationId);
        if (!TextUtils.isEmpty(userInfo.userAvatar)) {
            SPUtils.getInstance().put(Constants.CONFIG_LAST_USER_AVATAR, userInfo.userAvatar);
        }
    }
    /**
     * 保存最后一次登录的用户权限.
     *
     * @param userEntity
     */
    public static void savePrivileges(UserInfoEntity userEntity) {
        List<UserInfoPrivilegeEntity> privileges = userEntity.privileges;
        List<WebPrivilege> webPrivileges = userEntity.webPrivileges;
        if (null != privileges) {
            SPUtils.getInstance().put(Constants.PERMISSION_HAS_HYJJ, false);
            SPUtils.getInstance().put(Constants.PERMISSION_HAS_FACE, false);
            SPUtils.getInstance().put(Constants.PERMISSION_HAS_BODY, false);
            SPUtils.getInstance().put(Constants.PERMISSION_HAS_VIDEO_LIVE, false);
            SPUtils.getInstance().put(Constants.PERMISSION_HAS_PERSON_TRACKING, false);
            SPUtils.getInstance().put(Constants.PERMISSION_HAS_BROADCAST, false);
            SPUtils.getInstance().put(Constants.PERMISSION_HAS_CAR, false);
            for (UserInfoPrivilegeEntity entity : privileges) {
                if (Constants.TYPE_PERMISSION_HYJJ_CODE==entity.privilegeCode) {
                    SPUtils.getInstance().put(Constants.PERMISSION_HAS_HYJJ, true);
                } else if (Constants.TYPE_PERMISSION_FACE_CODE==entity.privilegeCode) {
                    SPUtils.getInstance().put(Constants.PERMISSION_HAS_FACE, true);
                } else if (Constants.TYPE_PERMISSION_BODY_CODE==entity.privilegeCode) {
                    SPUtils.getInstance().put(Constants.PERMISSION_HAS_BODY, true);
                } else if (Constants.TYPE_PERMISSION_VIDEO_LIVE_CODE==entity.privilegeCode) {
                    SPUtils.getInstance().put(Constants.PERMISSION_HAS_VIDEO_LIVE, true);
                } else if (Constants.TYPE_PERMISSION_TRACKING_CODE==entity.privilegeCode) {
                    SPUtils.getInstance().put(Constants.PERMISSION_HAS_PERSON_TRACKING, true);
                }else if(Constants.TYPE_PERMISSION_BROADCAST_CODE==entity.privilegeCode){
                    SPUtils.getInstance().put(Constants.PERMISSION_HAS_BROADCAST, true);
                }else if(Constants.TYPE_PERMISSION_CAR_CODE==entity.privilegeCode){
                    SPUtils.getInstance().put(Constants.PERMISSION_HAS_CAR, true);
                }
            }
        }
        if (webPrivileges != null) {
            SPUtils.getInstance().put(Constants.PERMISSION_OUTSIDE_PERSON, false);
            SPUtils.getInstance().put(Constants.PERMISSION_PRIVATE_NETWORK_SUITE, false);
            SPUtils.getInstance().put(Constants.PERMISSION_EVENT_REMIND, false);
            SPUtils.getInstance().put(Constants.PERMISSION_KEY_PERSON, false);
            for (WebPrivilege entity : webPrivileges) {
                if (Constants.TYPE_PERMISSION_OUTSIDE_PERSON_CODE== entity.privilegeCode) {
                    SPUtils.getInstance().put(Constants.PERMISSION_OUTSIDE_PERSON, true);
                } else if (Constants.TYPE_PERMISSION_PRIVATE_NETWORK_SUITE_CODE== entity.privilegeCode) {
                    SPUtils.getInstance().put(Constants.PERMISSION_PRIVATE_NETWORK_SUITE, true);
                }else if (Constants.TYPE_PERMISSION_EVENT_REMIND_CODE== entity.privilegeCode) {
                    SPUtils.getInstance().put(Constants.PERMISSION_EVENT_REMIND, true);
                }else if(Constants.TYPE_PERMISSION_KEY_PERSON_CODE==entity.privilegeCode){
                    SPUtils.getInstance().put(Constants.PERMISSION_KEY_PERSON, true);
                }
            }
        }
    }


    public static void clear() {
        MobclickAgent.onProfileSignOff();  // 清除友盟的信息
        DataSupport.deleteAll(SearchTextEntity.class);
        DataSupport.deleteAll(BannerItemEntity.class);
        DataSupport.deleteAll(VictoryItemEntity.class);
        DataSupport.deleteAll(OrgCameraEntity.class);
        DataSupport.deleteAll(CameraItem.class);
        DataSupport.deleteAll(ClueItemEntity.class);
        DataSupport.deleteAll(NewsItemEntity.class);
        SPUtils.getInstance().put(Constants.HAS_LOGIN, false);
        SPUtils.getInstance().remove(Constants.CONFIG_MESSAGE_SWITCH);
        SPUtils.getInstance().remove(CommonConstant.UID);
        // SPUtils.getInstance().remove(CommonConstant.SESSION);
        SPUtils.getInstance().remove(Constants.CONFIG_SOLO_CID);
        SPUtils.getInstance().remove(Constants.CONFIG_SOLO_ID);
        SPUtils.getInstance().remove(Constants.CONFIG_SOLO_SN);
        SPUtils.getInstance().remove(Constants.CONFIG_SOLO_BRAND);
        SPUtils.getInstance().remove(Constants.CONFIG_IS_SOLO);
        SPUtils.getInstance().remove(Constants.CONFIG_LAST_USER_LOGIN_NAME);
        SPUtils.getInstance().remove(Constants.CONFIG_LAST_USER_LOGIN_PHONE);
        SPUtils.getInstance().remove(Constants.CONFIG_LAST_USER_REAL_NAME);
        SPUtils.getInstance().put(Constants.CONFIG_LAST_USER_AVATAR, "");
        SPUtils.getInstance().put(Constants.CONFIG_CASE_ID, "");
        SPUtils.getInstance().put(Constants.CONFIG_OPERATION_ID, "");
        SPUtils.getInstance().put(Constants.CONFIG_CLUE_ID, "");
        SPUtils.getInstance().put(Constants.COLLECT_JSON, "");
        SPUtils.getInstance().put(Constants.AFTER_LOGIN_SET_JPUSH_ALIAS_SUCCESS, false);
        clearPermissions();
    }

    public static void clearPermissions() {
        SPUtils.getInstance().put(Constants.PERMISSION_HAS_HYJJ, false);
        SPUtils.getInstance().put(Constants.PERMISSION_HAS_FACE, false);
        SPUtils.getInstance().put(Constants.PERMISSION_HAS_BODY, false);
        SPUtils.getInstance().put(Constants.PERMISSION_HAS_ALARM_LIST, false);
        SPUtils.getInstance().put(Constants.PERMISSION_HAS_ALARM_DETAIL, false);
        SPUtils.getInstance().put(Constants.PERMISSION_HAS_VIDEO_LIVE, false);
        SPUtils.getInstance().put(Constants.PERMISSION_HAS_VIDEO_HISTORY, false);
        SPUtils.getInstance().put(Constants.PERMISSION_HAS_VIDEO_CAPTURE, false);
        SPUtils.getInstance().put(Constants.PERMISSION_HAS_PERSON_TRACKING, false);
        SPUtils.getInstance().put(Constants.PERMISSION_OUTSIDE_PERSON, false);
        SPUtils.getInstance().put(Constants.PERMISSION_PRIVATE_NETWORK_SUITE, false);
        SPUtils.getInstance().put(Constants.PERMISSION_EVENT_REMIND, false);
        SPUtils.getInstance().put(Constants.PERMISSION_HAS_BROADCAST, false);
        SPUtils.getInstance().put(Constants.PERMISSION_HAS_SOLO, false);
        SPUtils.getInstance().put(Constants.PERMISSION_HAS_CAR, false);
        SPUtils.getInstance().put(Constants.PERMISSION_KEY_PERSON, false);
    }

    public static void clearCacheInfo() {
        clearImageAllCache(Utils.getContext());
    }

    /**
     * 清除图片磁盘缓存
     */
    public static void clearImageDiskCache(Context context) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // File fileCache = new File(Utils.getContext().getCacheDir(), "picasso-cache");
                        // FileUtils.deleteDir(fileCache);
                        GlideArms.get(context).clearDiskCache();
                    }
                }).start();
            } else {
                GlideArms.get(context).clearDiskCache();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清除图片内存缓存
     */
    public static void clearImageMemoryCache(Context context) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) { //只能在主线程执行
                GlideArms.get(context).clearMemory();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 清除图片所有缓存
     */
    public static void clearImageAllCache(Context context) {
        clearImageDiskCache(context);
        clearImageMemoryCache(context);
        String ImageExternalCatchDir = context.getExternalCacheDir() + ExternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR;
        FileUtils.deleteDir(ImageExternalCatchDir);
    }

}
