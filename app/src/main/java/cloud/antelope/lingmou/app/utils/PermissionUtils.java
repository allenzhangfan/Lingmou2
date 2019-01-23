package cloud.antelope.lingmou.app.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;

import com.lingdanet.safeguard.common.utils.DeviceUtil;
import com.lingdanet.safeguard.common.utils.Utils;

import java.util.ArrayList;

import cloud.antelope.lingmou.R;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by liucheng on 16/6/2.
 */
public class PermissionUtils {

    //权限
    public static final int RC_REBOOT_PERM = 121;
    public static final int RC_CAMERA_PERM = 123;
    public static final int RC_WRITE_SDCARD_PERM = 124;
    public static final int RC_READ_SDCARD_PERM = 125;
    public static final int RC_PHONE_STATE_PERM = 126;
    public static final int RC_LOCATION_PERM = 127;
    public static final int RC_RECORD_AUDIO_PERM = 128;


    /**
     * 开机请求权限
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static void requestPermission(@NonNull Activity activity) {
        if (DeviceUtil.getSDKVersion() >= Build.VERSION_CODES.M) {
            ArrayList<String> array = new ArrayList<>();
            if (!(activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
                array.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (!(activity.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)) {
                array.add(Manifest.permission.CAMERA);
            }
            if (!(activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                array.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            int size = array.size();
            if (size > 0) {
                activity.requestPermissions(array.toArray(new String[size]), RC_REBOOT_PERM);
            }
        }
    }

    /**
     * 获取定位信息
     *
     * @param hasPermission
     */
    @AfterPermissionGranted(RC_LOCATION_PERM)
    public static void locationTask(HasPermission hasPermission) {
        if (EasyPermissions.hasPermissions(Utils.getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            hasPermission.doNext(RC_LOCATION_PERM);
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(hasPermission, Utils.getContext().getResources().getString(R.string.location_permission),
                    RC_LOCATION_PERM, Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    /**
     * 调用摄像头
     *
     * @param hasPermission
     */
    @AfterPermissionGranted(RC_CAMERA_PERM)
    public static void cameraTask(HasPermission hasPermission) {
        if (EasyPermissions.hasPermissions(Utils.getContext(),
                Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            hasPermission.doNext(RC_CAMERA_PERM);
        } else {
            ArrayList<String> array = new ArrayList<>();
            if (!EasyPermissions.hasPermissions(Utils.getContext(),
                    Manifest.permission.CAMERA)) {
                array.add(Manifest.permission.CAMERA);
            }
            if (!EasyPermissions.hasPermissions(Utils.getContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                array.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            int size = array.size();
            if (size > 0) {
                EasyPermissions.requestPermissions(hasPermission,
                        Utils.getContext().getResources().getString(R.string.camera_permission),
                        RC_CAMERA_PERM, array.toArray(new String[size]));
            }

        }
    }


    /**
     * 请求视频录制
     *
     * @param hasPermission
     */
    @AfterPermissionGranted(RC_RECORD_AUDIO_PERM)
    public static void audioTask(HasPermission hasPermission) {
        if (EasyPermissions.hasPermissions(Utils.getContext(),
                Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION)) {
            hasPermission.doNext(RC_RECORD_AUDIO_PERM);
        } else {
            ArrayList<String> array = new ArrayList<>();
            if (!EasyPermissions.hasPermissions(Utils.getContext(),
                    Manifest.permission.CAMERA)) {
                array.add(Manifest.permission.CAMERA);
            }
            if (!EasyPermissions.hasPermissions(Utils.getContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                array.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (!EasyPermissions.hasPermissions(Utils.getContext(),
                    Manifest.permission.RECORD_AUDIO)) {
                array.add(Manifest.permission.RECORD_AUDIO);
            }
            if (!EasyPermissions.hasPermissions(Utils.getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                array.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            int size = array.size();
            if (size > 0) {
                EasyPermissions.requestPermissions(hasPermission,
                        Utils.getContext().getResources().getString(R.string.audio_permission),
                        RC_RECORD_AUDIO_PERM, array.toArray(new String[size]));
            }
        }
    }

    /**
     * 获取sd读卡存储
     */
    @AfterPermissionGranted(RC_READ_SDCARD_PERM)
    public static void readSdCardTask(HasPermission hasPermission) {
        if (EasyPermissions.hasPermissions(Utils.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // Have permission, do the thing!
            hasPermission.doNext(RC_READ_SDCARD_PERM);
        } else {
            // Request one permission
            EasyPermissions.requestPermissions(hasPermission, Utils.getContext().getString(R.string.sdcard_write_permission),
                    RC_READ_SDCARD_PERM, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }


    /**
     * 获取手机状态
     */
    @AfterPermissionGranted(RC_PHONE_STATE_PERM)
    public static void readPhoneState(HasPermission hasPermission) {
        if (EasyPermissions.hasPermissions(Utils.getContext(), Manifest.permission.READ_PHONE_STATE)) {
            // Have permission, do the thing!
            hasPermission.doNext(RC_PHONE_STATE_PERM);
        } else {
            // Request one permission
            EasyPermissions.requestPermissions(hasPermission, Utils.getContext().getString(R.string.sdcard_write_permission),
                    RC_PHONE_STATE_PERM, Manifest.permission.READ_PHONE_STATE);
        }
    }


    public interface HasPermission {
        void doNext(int permId);
    }
}
