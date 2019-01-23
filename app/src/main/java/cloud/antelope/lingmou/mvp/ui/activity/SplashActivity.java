package cloud.antelope.lingmou.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;

import com.lingdanet.safeguard.common.constant.CommonConstant;
import com.lingdanet.safeguard.common.utils.LogUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;

import javax.inject.Inject;

import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.common.UrlConstant;
import cloud.antelope.lingmou.mvp.presenter.AppUpdatePresenter;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;

import static me.jessyan.retrofiturlmanager.RetrofitUrlManager.DOMAIN_NAME_HEADER;

/**
 * 程序启动界面.
 */
public class SplashActivity extends AppCompatActivity {

    // 启动图显示延迟
    private static final int APP_START_DELAY_TIME = 1500;

    private long mStartTime;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash_layout);

        mStartTime = System.currentTimeMillis();
        // RetrofitUrlManager.getInstance().putDomain(DOMAIN_NAME_HEADER, UrlConstant.LY_URL);
    }

    /**
     * 跳转到下一个页面.
     */
    public void gotoNextUI() {
        if (isFirstLaunch()) {
            gotoGuidePage();
        } else {
            LogUtils.w("cxm", "session = " + SPUtils.getInstance().getString(CommonConstant.SESSION) + ", uid = " + SPUtils.getInstance().getString(CommonConstant.UID));
            if (TextUtils.isEmpty(SPUtils.getInstance().getString(Constants.ZHYY_TOKEN))
                    || TextUtils.isEmpty(SPUtils.getInstance().getString(CommonConstant.UID))) {
                SPUtils.getInstance().put(Constants.HAS_LOGIN, false);
            }
            gotoMainPage();
            //        }
        }
    }

        /**
         * 是否是首次启动.
         *
         * @return boolean true:是首次启动客户端，false:非首次启动客户端
         */
        public boolean isFirstLaunch() {
            return SPUtils.getInstance().getBoolean(Constants.CONFIG_FIRST_START_APP, true);
        }

        /**
         * 跳转到主页面.
         */
        private void gotoMainPage () {
            long endTime = System.currentTimeMillis();
            int interval = (int) (endTime - mStartTime);
            if (interval < APP_START_DELAY_TIME) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startHomeActivity();
                    }
                }, APP_START_DELAY_TIME - interval);
            } else {
                startHomeActivity();
            }
        }

        /**
         * 启动HomeActivity.
         */
        private void startHomeActivity () {
            Intent intent = new Intent();
            if (SPUtils.getInstance().getBoolean(Constants.HAS_LOGIN, false)) {
                intent.setClass(SplashActivity.this, NewMainActivity.class);
                //            intent.setClass(SplashActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                intent.setClass(SplashActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }

        /**
         * 跳转到引导页.
         */
        private void gotoGuidePage () {
            long endTime = System.currentTimeMillis();
            int interval = (int) (endTime - mStartTime);
            if (interval < APP_START_DELAY_TIME) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startGuideActivity();
                    }
                }, APP_START_DELAY_TIME - interval);
            } else {
                startGuideActivity();
            }
        }

        /**
         * 启动引导页.
         */
        private void startGuideActivity () {
            Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
            startActivity(intent);
            finish();
        }
    }
