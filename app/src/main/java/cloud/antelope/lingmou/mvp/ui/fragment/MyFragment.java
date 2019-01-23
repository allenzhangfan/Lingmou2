package cloud.antelope.lingmou.mvp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.DataHelper;
import com.kyleduo.switchbutton.SwitchButton;
import com.lingdanet.safeguard.common.constant.CommonConstant;
import com.lingdanet.safeguard.common.utils.FileUtils;
import com.lingdanet.safeguard.common.utils.LogUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.lingdanet.safeguard.common.utils.Utils;

import java.io.File;
import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.JPushUtils;
import cloud.antelope.lingmou.app.utils.LoadAvatarUtils;
import cloud.antelope.lingmou.app.utils.SaveUtils;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.di.component.DaggerMyComponent;
import cloud.antelope.lingmou.di.module.MyModule;
import cloud.antelope.lingmou.mvp.contract.MyContract;
import cloud.antelope.lingmou.mvp.model.entity.UserInfoBean;
import cloud.antelope.lingmou.mvp.model.entity.UserInfoEntity;
import cloud.antelope.lingmou.mvp.presenter.MyPresenter;
import cloud.antelope.lingmou.mvp.ui.activity.AboutActivity;
import cloud.antelope.lingmou.mvp.ui.activity.AccountActivity;
import cloud.antelope.lingmou.mvp.ui.activity.KFeedbackActivity;
import cloud.antelope.lingmou.mvp.ui.activity.MyReportActivity;


public class MyFragment extends BaseFragment<MyPresenter> implements MyContract.View {

    private static final int TYPE_ACCOUNT = 0x01;

    @BindView(R.id.header_iv)
    ImageView mHeaderIv;
    @BindView(R.id.username_tv)
    TextView mUsernameTv;
    @BindView(R.id.arrow_iv)
    ImageView mArrowIv;
    @BindView(R.id.cache_size_tv)
    TextView mCacheSizeTv;
    @BindView(R.id.clean_cache_rl)
    RelativeLayout mCleanCacheRl;
    @BindView(R.id.feedback_tv)
    TextView mFeedbackTv;
    @BindView(R.id.about_tv)
    TextView mAboutTv;
    @BindView(R.id.root)
    LinearLayout mRoot;
    @BindView(R.id.message_receive_sb)
    SwitchButton mMessageReceiveSb;
    @BindView(R.id.my_clue_tv)
    TextView mMyClueTv;
    @BindView(R.id.local_address_tv)
    TextView mLocalAddressTv;

    // private UserInfoEntity mUserInfoEntity;

    private String mLoginName;

    public static MyFragment newInstance() {
        MyFragment fragment = new MyFragment();
        return fragment;
    }

    @Override
    public void setupFragmentComponent(AppComponent appComponent) {
        DaggerMyComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .myModule(new MyModule(this))
                .build()
                .inject(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        LogUtils.i("cxm", "isVisibleToUser = " + isVisibleToUser);
        if (isVisibleToUser) {
            if (TextUtils.isEmpty(mLoginName)) {
                mPresenter.getUserInfo();
            }
        }
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my, container, false);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mLoginName = SPUtils.getInstance().getString(Constants.CONFIG_LAST_USER_LOGIN_NAME);
        mUsernameTv.setText(mLoginName);
        LoadAvatarUtils.updateAvatar(mHeaderIv);
        mLocalAddressTv.setText(SPUtils.getInstance().getString(Constants.URL_PLATFORM_NAME));
        if (SPUtils.getInstance()
                .getBoolean(Constants.CONFIG_MESSAGE_SWITCH, true)) {
            mMessageReceiveSb.setChecked(true);
            mMessageReceiveSb.setBackColorRes(R.color.primary);
        } else {
            mMessageReceiveSb.setChecked(false);
            mMessageReceiveSb.setBackColorRes(R.color.switch_button_unchecked_bg);
        }
        //重新获取下缓存大小
        mCacheSizeTv.setText(getCacheSize());
        initListener();
        // mPresenter.getStorageToken();
        mPresenter.getUserInfo();
    }

    private void initListener() {
        mMessageReceiveSb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SPUtils.getInstance().put(Constants.CONFIG_MESSAGE_SWITCH, isChecked);
                    mMessageReceiveSb.setBackColorRes(R.color.primary);
                    JPushUtils.resumeJPush();
                    // mPresenter.setPushConfig(Constants.PUSH);
                    String uid = SPUtils.getInstance().getString(CommonConstant.UID);
                    String loginName = SPUtils.getInstance().getString(Constants.CONFIG_LAST_USER_LOGIN_NAME, "");
                    String alias = uid + "_" + loginName;
                    if (!TextUtils.isEmpty(uid) && !TextUtils.isEmpty(loginName)) {
                        JPushUtils.setAlias(alias);
                    }
                } else {
                    SPUtils.getInstance().put(Constants.CONFIG_MESSAGE_SWITCH, isChecked);
                    mMessageReceiveSb.setBackColorRes(R.color.switch_button_unchecked_bg);
                    // mPresenter.setPushConfig(Constants.NOT_PUSH);
                    JPushUtils.stopJpush();
                    // JPushUtils.deleteAlias();
                }
            }
        });
    }

    private Handler mCacheHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ToastUtils.showShort("清除完成");
            mCacheSizeTv.setText("0M");
        }
    };

    @OnClick({R.id.ll_account, R.id.my_clue_tv, R.id.clean_cache_rl, R.id.feedback_tv, R.id.about_tv})
    public void onViewClicked(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.ll_account:
                intent.setClass(getActivity(), AccountActivity.class);
                startActivityForResult(intent, TYPE_ACCOUNT);
                break;
            case R.id.my_clue_tv:
                startActivity(new Intent(getActivity(), MyReportActivity.class));
                break;
            case R.id.clean_cache_rl:
                //点击清除缓存
                if (TextUtils.equals(mCacheSizeTv.getText(), "0M")) {
                    ToastUtils.showShort("当前没有缓存");
                } else {
                    ToastUtils.showShort("正在清除缓存...");
                    SaveUtils.clearCacheInfo();
                    mCacheHandler.sendEmptyMessageDelayed(1, 1000);
                }
                break;
            case R.id.feedback_tv:
                //意见反馈
                startActivity(new Intent(getContext(), KFeedbackActivity.class));
                break;
            case R.id.about_tv:
                startActivity(new Intent(getContext(), AboutActivity.class));
                break;
        }
    }

    /**
     * 获取Glide的缓存大小
     *
     * @return
     */
    private String getCacheSize() {
        File cacheDir = DataHelper.makeDirs(new File(DataHelper.getCacheFile(Utils.getContext()), "Glide"));
        if (cacheDir.exists()) {
            long glideLength = FileUtils.getDirLength(cacheDir);
            return bytes2kb(glideLength);
        } else {
            return "0.0M";
        }
    }

    /**
     * byte(字节)根据长度转成kb(千字节)和mb(兆字节)
     *
     * @param bytes
     * @return
     */
    public String bytes2kb(long bytes) {
        BigDecimal filesize = new BigDecimal(bytes);
        BigDecimal megabyte = new BigDecimal(1024 * 1024);
        float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP)
                .floatValue();
        LogUtils.i("cxm", "cacheSize = " + returnValue);
        return returnValue + "M";
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == TYPE_ACCOUNT) {
                //表示改了头像
                LoadAvatarUtils.updateAvatar(mHeaderIv);
            }
        }
    }

    /**
     * 此方法是让外部调用使fragment做一些操作的,比如说外部的activity想让fragment对象执行一些方法,
     * 建议在有多个需要让外界调用的方法时,统一传Message,通过what字段,来区分不同的方法,在setData
     * 方法中就可以switch做不同的操作,这样就可以用统一的入口方法做不同的事
     * <p>
     * 使用此方法时请注意调用时fragment的生命周期,如果调用此setData方法时onCreate还没执行
     * setData里却调用了presenter的方法时,是会报空的,因为dagger注入是在onCreated方法中执行的,然后才创建的presenter
     * 如果要做一些初始化操作,可以不必让外部调setData,在initData中初始化就可以了
     *
     * @param data
     */

    @Override
    public void setData(Object data) {

    }


    @Override
    public void showLoading(String message) {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(@NonNull String message) {
        ToastUtils.showShort(message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
    }

    @Override
    public void killMyself() {

    }

    @Override
    public void getUserInfoSuccess(UserInfoEntity userInfoEntity) {
        // mUserInfoEntity = userInfoEntity;
        updateUserInfo(userInfoEntity);
    }

    @Override
    public void getTokenSuccess() {
        LoadAvatarUtils.updateAvatar(mHeaderIv);
    }

    private void updateUserInfo(UserInfoEntity userInfo) {
        UserInfoBean entity = userInfo.userInfo;
        if (null != entity) {
            String avatarUrl = entity.userAvatar;
            if (!TextUtils.isEmpty(avatarUrl)) {
                SPUtils.getInstance().put(Constants.CONFIG_LAST_USER_AVATAR, avatarUrl);
                LoadAvatarUtils.updateAvatar(mHeaderIv);
            }
            mUsernameTv.setText(entity.loginName);
        }
    }

}
