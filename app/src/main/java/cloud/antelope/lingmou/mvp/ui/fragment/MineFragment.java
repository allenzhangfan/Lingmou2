package cloud.antelope.lingmou.mvp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.umeng.analytics.MobclickAgent;

import org.simple.eventbus.Subscriber;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.BarViewUtils;
import cloud.antelope.lingmou.app.utils.JPushUtils;
import cloud.antelope.lingmou.app.utils.LoadAvatarUtils;
import cloud.antelope.lingmou.app.utils.SaveUtils;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.di.component.DaggerMineComponent;
import cloud.antelope.lingmou.di.module.MineModule;
import cloud.antelope.lingmou.mvp.contract.MineContract;
import cloud.antelope.lingmou.mvp.model.entity.FaceDepotDetailEntity;
import cloud.antelope.lingmou.mvp.model.entity.NotWifiNoticeChangedEvent;
import cloud.antelope.lingmou.mvp.model.entity.PictureCollectEvent;
import cloud.antelope.lingmou.mvp.model.entity.UserInfoBean;
import cloud.antelope.lingmou.mvp.model.entity.UserInfoEntity;
import cloud.antelope.lingmou.mvp.presenter.MinePresenter;
import cloud.antelope.lingmou.mvp.ui.activity.AboutActivity;
import cloud.antelope.lingmou.mvp.ui.activity.AccountActivity;
import cloud.antelope.lingmou.mvp.ui.activity.CollectedAlertActivity;
import cloud.antelope.lingmou.mvp.ui.activity.CollectedPictureActivity;
import cloud.antelope.lingmou.mvp.ui.activity.DeployControlActivity;
import cloud.antelope.lingmou.mvp.ui.activity.FocusedDevicesActivity;
import cloud.antelope.lingmou.mvp.ui.activity.HistoryWatchActivity;
import cloud.antelope.lingmou.mvp.ui.activity.KFeedbackActivity;
import cloud.antelope.lingmou.mvp.ui.activity.PlayerActivity;
import cloud.antelope.lingmou.mvp.ui.activity.TrackingPersonActivity;

public class MineFragment extends BaseFragment<MinePresenter> implements MineContract.View {
    private static final int TYPE_ACCOUNT = 0x01;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.rl_top)
    RelativeLayout rlTop;
    @BindView(R.id.header_iv)
    ImageView headerIv;
    @BindView(R.id.cache_size_tv)
    TextView cacheSizeTv;
    @BindView(R.id.message_receive_sb)
    SwitchButton mMessageReceiveSb;
    @BindView(R.id.not_wifi_notice_sb)
    SwitchButton notWifiNoticeSb;
    @BindView(R.id.top_view)
    View topView;
    String mLoginName;
    @BindView(R.id.iv_arrow)
    ImageView ivArrow;
    @BindView(R.id.ll_focused_devices)
    LinearLayout llFocusedDevices;
    @BindView(R.id.ll_collected_picture)
    LinearLayout llCollectedPicture;
    @BindView(R.id.ll_collected_alert)
    LinearLayout llCollectedAlert;
    @BindView(R.id.iv_tracking_task)
    ImageView ivTrackingTask;
    @BindView(R.id.ll_tracking_task)
    LinearLayout llTrackingTask;
    @BindView(R.id.iv_tracking_people)
    ImageView ivTrackingPeople;
    @BindView(R.id.ll_tracking_people)
    LinearLayout llTrackingPeople;
    @BindView(R.id.ll_watch_history)
    LinearLayout llWatchHistory;
    @BindView(R.id.arrow_iv)
    ImageView arrowIv;
    @BindView(R.id.clean_cache_rl)
    RelativeLayout cleanCacheRl;
    @BindView(R.id.feedback_tv)
    TextView feedbackTv;
    @BindView(R.id.about_tv)
    TextView aboutTv;
    private Handler mCacheHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ToastUtils.showShort("清除完成");
            cacheSizeTv.setText("0M");
        }
    };

    @Override
    public void setupFragmentComponent(AppComponent appComponent) {
        DaggerMineComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .mineModule(new MineModule(this))
                .build()
                .inject(this);
    }

    public static MineFragment newInstance() {
        Bundle args = new Bundle();
        MineFragment fragment = new MineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mine, container, false);
    }

    @OnClick({R.id.ll_focused_devices, R.id.rl_top, R.id.clean_cache_rl, R.id.feedback_tv, R.id.about_tv
            , R.id.ll_collected_picture, R.id.ll_collected_alert, R.id.ll_tracking_task, R.id.ll_tracking_people, R.id.ll_watch_history})
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.rl_top:
                intent.setClass(getActivity(), AccountActivity.class);
                startActivityForResult(intent, TYPE_ACCOUNT);
                break;
            case R.id.ll_focused_devices:
                MobclickAgent.onEvent(getContext(), "mine_favoriteDevices");
                startActivity(new Intent(getActivity(), FocusedDevicesActivity.class));
                break;
            case R.id.ll_collected_picture:
                MobclickAgent.onEvent(getContext(), "mine_favoritePictures");
                startActivity(new Intent(getActivity(), CollectedPictureActivity.class));
                break;
            case R.id.ll_collected_alert:
                MobclickAgent.onEvent(getContext(), "mine_favoriteAlarms");
                intent.setClass(getActivity(), CollectedAlertActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_tracking_task:
                if (SPUtils.getInstance().getBoolean(Constants.PERMISSION_HAS_PERSON_TRACKING)) {
                    MobclickAgent.onEvent(getContext(), "mine_linkongTasks");
                    intent.setClass(getActivity(), DeployControlActivity.class);
                    intent.putExtra("fromMine", true);
                    startActivity(intent);
                } else {
                    ToastUtils.showShort(R.string.hint_no_permission);
                }
                break;
            case R.id.ll_tracking_people:
                if (SPUtils.getInstance().getBoolean(Constants.PERMISSION_HAS_PERSON_TRACKING)) {
                    MobclickAgent.onEvent(getContext(), "mine_linkongPerson");
                    intent.setClass(getActivity(), TrackingPersonActivity.class);
                    startActivity(intent);
                } else {
                    ToastUtils.showShort(R.string.hint_no_permission);
                }
                break;
            case R.id.ll_watch_history:
                MobclickAgent.onEvent(getContext(), "mine_watchHistory");
                startActivity(new Intent(getActivity(), HistoryWatchActivity.class));
                break;
            case R.id.clean_cache_rl:
                //点击清除缓存
                if (TextUtils.equals(cacheSizeTv.getText(), "0M")) {
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

    @Override
    public void onSupportVisible() {
        if (TextUtils.isEmpty(mLoginName)) {
            mPresenter.getUserInfo();
        }

    }

    @Subscriber()
    public void modifyNotWifiNoticeSb(NotWifiNoticeChangedEvent event) {
        if (notWifiNoticeSb == null) return;
        if (SPUtils.getInstance()
                .getBoolean(Constants.CONFIG_NOT_WIFI_SWITCH, false)) {
            notWifiNoticeSb.setChecked(true);
            notWifiNoticeSb.setBackColorRes(R.color.yellow_ffa000);
        } else {
            notWifiNoticeSb.setChecked(false);
            notWifiNoticeSb.setBackColorRes(R.color.switch_button_unchecked_bg);
        }
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        topView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, BarViewUtils.getStatusBarHeight()));
        mLoginName = SPUtils.getInstance().getString(Constants.CONFIG_LAST_USER_LOGIN_NAME);
        tvName.setText(mLoginName);
        LoadAvatarUtils.updateAvatar(headerIv);
        if (SPUtils.getInstance()
                .getBoolean(Constants.CONFIG_MESSAGE_SWITCH, true)) {
            mMessageReceiveSb.setChecked(true);
            mMessageReceiveSb.setBackColorRes(R.color.yellow_ffa000);
        } else {
            mMessageReceiveSb.setChecked(false);
            mMessageReceiveSb.setBackColorRes(R.color.switch_button_unchecked_bg);
        }
        if (SPUtils.getInstance()
                .getBoolean(Constants.CONFIG_NOT_WIFI_SWITCH, false)) {
            notWifiNoticeSb.setChecked(true);
            notWifiNoticeSb.setBackColorRes(R.color.yellow_ffa000);
        } else {
            notWifiNoticeSb.setChecked(false);
            notWifiNoticeSb.setBackColorRes(R.color.switch_button_unchecked_bg);
        }
        if (!SPUtils.getInstance().getBoolean(Constants.PERMISSION_HAS_PERSON_TRACKING)) {
            ivTrackingPeople.setImageResource(R.drawable.track_person_no_permission);
            ivTrackingTask.setImageResource(R.drawable.track_task_no_permission);
        }
        //重新获取下缓存大小
        cacheSizeTv.setText(getCacheSize());
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
                    mMessageReceiveSb.setBackColorRes(R.color.yellow_ffa000);
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
        notWifiNoticeSb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPUtils.getInstance().put(Constants.CONFIG_NOT_WIFI_SWITCH, isChecked);
                notWifiNoticeSb.setBackColorRes(isChecked ? R.color.yellow_ffa000 : R.color.switch_button_unchecked_bg);
            }
        });
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
                LoadAvatarUtils.updateAvatar(headerIv);
            }
        }
    }

    @Override
    public void getUserInfoSuccess(UserInfoEntity userInfoEntity) {
        // mUserInfoEntity = userInfoEntity;
        updateUserInfo(userInfoEntity);
    }

    @Override
    public void getTokenSuccess() {
        LoadAvatarUtils.updateAvatar(headerIv);
    }

    private void updateUserInfo(UserInfoEntity userInfo) {
        UserInfoBean entity = userInfo.userInfo;
        if (null != entity) {
            String avatarUrl = entity.userAvatar;
            if (!TextUtils.isEmpty(avatarUrl)) {
                SPUtils.getInstance().put(Constants.CONFIG_LAST_USER_AVATAR, avatarUrl);
                LoadAvatarUtils.updateAvatar(headerIv);
            }
            tvName.setText(entity.loginName);
        }
    }

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
    public void showMessage(String message) {
        ToastUtils.showShort(message);
    }

    @Override
    public void launchActivity(Intent intent) {

    }

    @Override
    public void killMyself() {

    }

}
