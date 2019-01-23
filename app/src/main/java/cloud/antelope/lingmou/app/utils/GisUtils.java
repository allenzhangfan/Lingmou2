package cloud.antelope.lingmou.app.utils;

import android.content.Context;
import android.text.TextUtils;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.lingdanet.safeguard.common.utils.LogUtils;
import com.lingdanet.safeguard.common.utils.Utils;

import cloud.antelope.lingmou.R;

/**
 * 作者：安兴亚
 * 创建日期：2017/12/26
 * 邮箱：anxingya@lingdanet.com
 * 描述：TODO
 */

public class GisUtils {

    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    private AMapLocationClientOption mLocationOption = null;

    private AMapLocation mLocation;

    private int mInterval;

    private AMapLocationListener mMyLocationListener;
    private OnLocateActionListener mLocateListener;

    public void setLocateListener(OnLocateActionListener locateListener) {
        mLocateListener = locateListener;
    }

    public AMapLocation getLocation() {
        return mLocation;
    }

    /**
     * @param context
     * @param interval 定位的时间间隔，如果传入0，则只在第一定进行定位
     */
    public GisUtils(Context context, int interval) {
        mInterval = interval;
        mLocationClient = new AMapLocationClient(context.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        //设置定位回调监听
        mLocationClient.setLocationListener(mMyLocationListener);
        initLocation();
    }

    private void initLocation() {
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(false);
        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        // mLocationOption.setOnceLocationLatest(true);
        if (mInterval > 0) {
            mLocationOption.setInterval(mInterval);
        }
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
    }

    public void start() {
        // 启动定位
        mLocationClient.startLocation();
        if (mLocateListener != null) {
            mLocateListener.onLocateStart();
        }
    }

    public void stop() {
        if (mLocationClient.isStarted()) {
            mLocationClient.stopLocation();
        }
    }

    public void destory() {
        if (null != mLocationClient) {
            mLocationClient.unRegisterLocationListener(mMyLocationListener);
            mLocationClient.onDestroy();
            mMyLocationListener = null;
            mLocationClient = null;
        }
    }

    /**
     * 获取定位地址.
     *
     * @param location
     * @return
     */
    public static String getAbbrAddr(AMapLocation location) {
        if (null == location) {
            return Utils.getContext().getString(R.string.unknow_text);
        } else {
            return location.getCity() + location.getDistrict() + location.getStreet();
        }
    }

    /**
     * 获取指定格式的地址，只需要市区街道的信息
     * @param fullAddr
     * @return
     */
    public static String getAbbrAddr(String fullAddr) {
        if (TextUtils.isEmpty(fullAddr)) {
            return fullAddr;
        }
        int proIndex = fullAddr.indexOf("省");
        if (proIndex > 0 && proIndex <= 3) {
            return fullAddr.substring(proIndex + 1);
        } else {
            if (fullAddr.startsWith("北京") || fullAddr.startsWith("重庆")
                    || fullAddr.startsWith("天津") || fullAddr.startsWith("上海")) {
                return fullAddr;
            } else {
                int disIndex = fullAddr.indexOf("区");
                return fullAddr.substring(disIndex + 1);
            }
        }
    }

    public interface OnLocateActionListener {
        void onLocateStart();

        void onLocateOK(AMapLocation location);

        void onLocateFail(int errCode);
    }

    /**
     * 实现实时位置回调监听
     */
    public class MyLocationListener implements AMapLocationListener {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            // 显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
            LogUtils.e("AmapError", "location Error, "
                    + "ErrCode:" + aMapLocation.getErrorCode()
                    + ", ErrInfo:" + aMapLocation.getErrorInfo());
            if (aMapLocation.getErrorCode() == AMapLocation.LOCATION_SUCCESS) {
                // 定位成功回调信息，设置相关消息
                mLocation = aMapLocation;
                LogUtils.d("mCurrentLatLng = " +
                        aMapLocation.getLatitude() + ", " +
                        aMapLocation.getLongitude() +
                        ",\nAddress = " + aMapLocation.getAddress() +
                        ",\nDescription = " + aMapLocation.getDescription() +
                        ",\nCountry = " + aMapLocation.getCountry() +
                        ",\nProvince = " + aMapLocation.getProvince() +
                        ",\nCity = " + aMapLocation.getCity() +
                        ",\nStreet = " + aMapLocation.getStreet() +
                        ",\nProvider = " + aMapLocation.getProvider() +
                        ",\nFloor = " + aMapLocation.getFloor()
                );
                if (mLocateListener != null) {
                    mLocateListener.onLocateOK(aMapLocation);
                }
            } else {
                if (mLocateListener != null) {
                    mLocateListener.onLocateFail(aMapLocation.getErrorCode());
                }
            }
        }
    }
}
