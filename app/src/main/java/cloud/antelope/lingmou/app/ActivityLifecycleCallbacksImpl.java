/**
 * Copyright 2017 JessYan
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cloud.antelope.lingmou.app;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.https.OkHttpUtils;
import com.lingdanet.safeguard.common.utils.AppUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.WaterMarkUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.Density;
import cloud.antelope.lingmou.app.utils.ViewUtils;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.common.UrlConstant;
import cloud.antelope.lingmou.mvp.model.entity.AppUpdateEntity;
import cloud.antelope.lingmou.mvp.model.entity.UpdateInfo;
import cloud.antelope.lingmou.mvp.model.entity.UpdateLastLoginTimeRequest;
import cloud.antelope.lingmou.mvp.ui.activity.PictureDetailActivity;
import cloud.antelope.lingmou.mvp.ui.activity.FaceDetailActivity;
import cloud.antelope.lingmou.mvp.ui.activity.PlayerActivity;
import cloud.antelope.lingmou.mvp.ui.activity.PlayerNewActivity;
import cloud.antelope.lingmou.mvp.ui.activity.RecordActivity;
import cloud.antelope.lingmou.mvp.ui.activity.Splash1Activity;
import cloud.antelope.lingmou.mvp.ui.activity.VideoPlayActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import timber.log.Timber;

/**
 * ================================================
 * 展示 {@link Application.ActivityLifecycleCallbacks} 的用法
 * <p>
 * Created by JessYan on 04/09/2017 17:14
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public class ActivityLifecycleCallbacksImpl implements Application.ActivityLifecycleCallbacks {

    private Bitmap mMarkerBitmap;
    private Bitmap mLandscapeMarkerBitmap;
    //打开的Activity数量统计
    private int activityStartCount = 0;
    AppUpdateEntity appUpdateEntity = null;
    private Dialog mUpdateDialog;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Timber.w(activity + " - onActivityCreated");
    }

    @Override
    public void onActivityStarted(Activity activity) {
        activityStartCount++;
        //数值从0变到1说明是从后台切到前台
        if (activityStartCount == 1) {
            //从后台切到前台
            updateLastLoginTime();
            if (activity instanceof Splash1Activity) return;
            callUpdate(activity);
        }
        if (!activity.getIntent().getBooleanExtra("isInitToolbar", false)) {
            //由于加强框架的兼容性,故将 setContentView 放到 onActivityCreated 之后,onActivityStarted 之前执行
            //而 findViewById 必须在 Activity setContentView() 后才有效,所以将以下代码从之前的 onActivityCreated 中移动到 onActivityStarted 中执行
            activity.getIntent().putExtra("isInitToolbar", true);
            //这里全局给Activity设置toolbar和title,你想象力有多丰富,这里就有多强大,以前放到BaseActivity的操作都可以放到这里
            if (activity.findViewById(R.id.toolbar) != null) {
                if (activity instanceof AppCompatActivity) {
                    ((AppCompatActivity) activity).setSupportActionBar((Toolbar) activity.findViewById(R.id.toolbar));
                    ((AppCompatActivity) activity).getSupportActionBar().setDisplayShowTitleEnabled(false);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        activity.setActionBar((android.widget.Toolbar) activity.findViewById(R.id.toolbar));
                        activity.getActionBar().setDisplayShowTitleEnabled(false);
                    }
                }
            }
            if (activity.findViewById(R.id.toolbar_title) != null) {
                ((TextView) activity.findViewById(R.id.toolbar_title)).setText(activity.getTitle());
            }
            if (activity.findViewById(R.id.toolbar_back) != null) {
                activity.findViewById(R.id.toolbar_back).setOnClickListener(v -> {
                    activity.onBackPressed();
                });
            }
        }

        if ((activity instanceof PlayerActivity) || (activity instanceof RecordActivity) || (activity instanceof PlayerNewActivity)|| (activity instanceof VideoPlayActivity)) {
            ViewGroup rootView = getRootView(activity);
            ImageView waterMarkView = (ImageView) LayoutInflater.from(activity).inflate(R.layout.layout_water_mark, null, false);
            if (null == mLandscapeMarkerBitmap) {
                String loginName = SPUtils.getInstance().getString(Constants.CONFIG_LAST_USER_REAL_NAME);
                String loginPhone = SPUtils.getInstance().getString(Constants.CONFIG_LAST_USER_LOGIN_PHONE);
                mLandscapeMarkerBitmap = WaterMarkUtils.getLandscapeMarkerBitmapMultiLine(loginName , 1920, 1080);
            }
            waterMarkView.setImageBitmap(mLandscapeMarkerBitmap);
            rootView.addView(waterMarkView);
            if((activity instanceof PlayerActivity) || (activity instanceof RecordActivity) || (activity instanceof PlayerNewActivity))
            Density.setOrientation(activity, Density.HEIGHT);
            return;
        }
        if ((activity instanceof PictureDetailActivity) || (activity instanceof FaceDetailActivity)) {
            ViewGroup rootView = getRootView(activity);
            ImageView waterMarkView = (ImageView) LayoutInflater.from(activity).inflate(R.layout.layout_water_mark, null, false);
            if (null == mMarkerBitmap) {
                String loginName = SPUtils.getInstance().getString(Constants.CONFIG_LAST_USER_REAL_NAME);
                String loginPhone = SPUtils.getInstance().getString(Constants.CONFIG_LAST_USER_LOGIN_PHONE);
                String time=new SimpleDateFormat("yyyyMMdd").format(new Date())+"T"+new SimpleDateFormat("HHmmss").format(new Date());
                mMarkerBitmap = WaterMarkUtils.getMarkerBitmapMultiLine(loginName + "\n" + time);
            }
            waterMarkView.setImageBitmap(mMarkerBitmap);
            rootView.addView(waterMarkView);
            return;
        } else {
            mMarkerBitmap = null;
            mLandscapeMarkerBitmap = null;
        }
        Density.setDefault(activity);
    }

    private void updateLastLoginTime() {
        if (TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.URL_BASE)) || TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.ZHYY_TOKEN)))
            return;
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json"), new Gson().toJson(new UpdateLastLoginTimeRequest(SPUtils.getInstance().getString(Constants.DEVICE_IMEI))));
        Request request = new Request.Builder()
                .url(SPUtils.getInstance().getString(Constants.URL_BASE) + "/api/app/user/updateLastLoginTime")
                .addHeader("Authorization", SPUtils.getInstance().getString(Constants.ZHYY_TOKEN))
                .addHeader("Accept","*/*")
                .addHeader("Content-type","application/json")
                .post(requestBody)
                .build();
        OkHttpUtils.getInstance().getOkHttpClient().newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                    }
                });
    }

    private void callUpdate(Activity activity) {
        Request request = new Request.Builder()
                .url(UrlConstant.URL_UPDATE)
                .addHeader("Authorization", SPUtils.getInstance().getString(Constants.ZHYY_TOKEN))
                .build();
        OkHttpUtils.getInstance().getOkHttpClient().newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        ResponseBody responseBody = response.body();
                        String body = responseBody.string();
                        Timber.i(body);
                        if (!TextUtils.isEmpty(body)) {
                            Gson gson = ArmsUtils.obtainAppComponentFromContext(activity).gson();
                            try {
                                appUpdateEntity = gson.fromJson(body, new TypeToken<AppUpdateEntity>() {
                                }.getType());
                            } catch (JsonSyntaxException e) {
                                e.printStackTrace();
                            }
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (appUpdateEntity == null) return;
                                    UpdateInfo updateInfo = appUpdateEntity.getUpdateInfo();
                                    int currentVersion = AppUtils.getAppVersionCode();
                                    if (null != updateInfo) {
                                        int updateVersion = updateInfo.getVersionNumber();
                                        boolean forceUpdate = updateInfo.isForceUpdate();
                                        String versionDescription = updateInfo.getVersionDescription();
                                        String packageUrl = updateInfo.getPackageUrl();
                                        int minVersion = updateInfo.getMinVersion();
                                        if (updateVersion > currentVersion) {
                                            if (currentVersion < minVersion) {
                                                showUpdateDialog(true, versionDescription, packageUrl);
                                            } else {
                                                showUpdateDialog(forceUpdate, versionDescription, packageUrl);
                                            }
                                        }
                                    }
                                }

                                private void showUpdateDialog(boolean isForce, String versionDescription, String mDownloadUrl) {
                                    if (mUpdateDialog != null) return;
                                    if(!isForce &&Constants.SHOW_UPDATE_DIALOG) return;
                                    mUpdateDialog = ViewUtils.showUpgradeDialog(isForce, versionDescription, activity
                                            , v -> {
                                                AppUtils.gotoDownloadApk(mDownloadUrl);
                                                if (!isForce) {
                                                    if (mUpdateDialog != null && mUpdateDialog.isShowing()) {
                                                        mUpdateDialog.dismiss();
                                                        mUpdateDialog = null;
                                                    }
                                                }
                                            }
                                            , v -> {
                                                if (!isForce) {
                                                    if (mUpdateDialog != null && mUpdateDialog.isShowing()) {
                                                        mUpdateDialog.dismiss();
                                                        mUpdateDialog = null;
                                                    }
                                                }
                                            });
                                    Constants.SHOW_UPDATE_DIALOG =true;
                                }
                            });
                        }
                    }
                });
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Timber.w(activity + " - onActivityResumed");
        MobclickAgent.onResume(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Timber.w(activity + " - onActivityPaused");
        MobclickAgent.onPause(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        activityStartCount--;
        //数值从1到0说明是从前台切到后台
        if (activityStartCount == 0) {
            if (mUpdateDialog != null) {
                mUpdateDialog.dismiss();
                mUpdateDialog = null;
            }
        }

        Timber.w(activity + " - onActivityStopped");
        if (activity instanceof PictureDetailActivity
                || activity instanceof FaceDetailActivity
                || activity instanceof PlayerActivity
                || activity instanceof RecordActivity
                || activity instanceof PlayerNewActivity) {

            ViewGroup rootView = getRootView(activity);
            rootView.removeViewAt(rootView.getChildCount() - 1);
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Timber.w(activity + " - onActivitySaveInstanceState");
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Timber.w(activity + " - onActivityDestroyed");
        //横竖屏切换或配置改变时, Activity 会被重新创建实例, 但 Bundle 中的基础数据会被保存下来,移除该数据是为了保证重新创建的实例可以正常工作
        activity.getIntent().removeExtra("isInitToolbar");
    }

    //查找布局的底层
    protected static ViewGroup getRootView(Activity context) {
        return (ViewGroup) context.findViewById(android.R.id.content);
    }
}
