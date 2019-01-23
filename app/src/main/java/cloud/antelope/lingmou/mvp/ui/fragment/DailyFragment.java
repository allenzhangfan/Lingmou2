package cloud.antelope.lingmou.mvp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.lingdanet.safeguard.common.utils.LogUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.VirtualButtonBarUtil;
import cloud.antelope.lingmou.di.component.DaggerDailyComponent;
import cloud.antelope.lingmou.di.module.DailyModule;
import cloud.antelope.lingmou.mvp.contract.DailyContract;
import cloud.antelope.lingmou.mvp.presenter.DailyPresenter;
import cloud.antelope.lingmou.mvp.ui.activity.CloudSearchActivity;
import cloud.antelope.lingmou.mvp.ui.activity.NewMainActivity;
import cloud.antelope.lingmou.mvp.ui.adapter.CloudEyeFragmentAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.IndicatorView;
import me.yokeyword.fragmentation.ISupportFragment;
import timber.log.Timber;

public class DailyFragment extends BaseFragment<DailyPresenter> implements DailyContract.View {
    @BindView(R.id.daily_police_tv)
    TextView mDailyPoliceTv;
    @BindView(R.id.daily_device_tv)
    TextView mDailyDeviceTv;
    @BindView(R.id.vp)
    ViewPager vp;

//    @BindView(R.id.indicatorView)
//    IndicatorView mIndicatorView;

    CloudEyeFragmentAdapter mCloudEyeFragmentAdapter;
    List<Fragment> mFragmentList;
    private int mSelectPosition = 0;
    private int prePosition = 0;
    private static float mMinSp = 17F;
    private static float mMaxSp = 22F;
//    private ISupportFragment[] mFragments = new ISupportFragment[2];
    private static DailyFragment instance;

    public static DailyFragment getInstance() {
        return instance;
    }

    @Override
    public void setupFragmentComponent(AppComponent appComponent) {
        DaggerDailyComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .dailyModule(new DailyModule(this))
                .build()
                .inject(this);
    }

    public static DailyFragment newInstance() {
        Bundle args = new Bundle();
        DailyFragment fragment = new DailyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_daily, container, false);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        instance = this;
//        adaptiveVirtualButtonBar();
        initFragment();
        mDailyDeviceTv.setSelected(true);
    }

    /**
     * 适配部分华为虚拟按键bug
     */
    private void adaptiveVirtualButtonBar() {
        if (VirtualButtonBarUtil.checkDeviceHasNavigationBar(getActivity())) {
            VirtualButtonBarUtil.assistActivity(getView(), getActivity());
        }
    }

    private void initFragment() {
        /*ISupportFragment firstFragment = findChildFragment(CloudMapFragment.class);
        if (firstFragment == null) {
            mFragments[0] = CloudMapFragment.newInstance();
            mFragments[1] = DailyPoliceFragment.newInstance();

            loadMultipleRootFragment(R.id.fl_container, 0,
                    mFragments[0],
                    mFragments[1]);
        } else {
            mFragments[0] = firstFragment;
            mFragments[1] = findChildFragment(DailyPoliceFragment.class);
        }*/
        mFragmentList = new ArrayList<>();
        mFragmentList.add(DailyDevicesFragment.newInstance());
        mFragmentList.add(DailyPoliceFragment.newInstance());
        mCloudEyeFragmentAdapter = new CloudEyeFragmentAdapter(getFragmentManager(), mFragmentList);
        vp.setAdapter(mCloudEyeFragmentAdapter);
//        mIndicatorView.setViewPager(vp);
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                LogUtils.i("cxm", "position = " + position + ",positionOffset = " + positionOffset + ", offSetPix = " + positionOffsetPixels);
                float currentMinSize = mMinSp + 5F * positionOffset;
                float currentMaxSize = mMaxSp - 5F * positionOffset;
                if (position == 0) {
                    Timber.e("min: " + currentMinSize + " max: " + currentMaxSize);
                    mDailyPoliceTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, currentMinSize);
                    mDailyDeviceTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, currentMaxSize);
                }
            }

            @Override
            public void onPageSelected(int position) {
                mDailyDeviceTv.setSelected(position==0);
                mDailyPoliceTv.setSelected(position==1);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void switchFragment(int position) {
//        showHideFragment(mFragments[position], mFragments[prePosition]);
        vp.setCurrentItem(position);
        prePosition = position;
        switch (position) {
            case 0:
                MobclickAgent.onEvent(getContext(), "device");
//                mSearchIv.setVisibility(View.INVISIBLE);
                selectTitleTv(mDailyDeviceTv, mDailyPoliceTv);
                break;
            case 1:
                MobclickAgent.onEvent(getContext(), "alarm");
//                mSearchIv.setVisibility(View.VISIBLE);
                selectTitleTv(mDailyPoliceTv, mDailyDeviceTv);
                DailyDevicesFragment cloudMapFragments = (DailyDevicesFragment) mFragmentList.get(0);
                DailyPoliceFragment policeFragments = (DailyPoliceFragment) mFragmentList.get(1);
                if (policeFragments.isShaixuanVisible()) {
                    NewMainActivity mainActivity = (NewMainActivity) getActivity();
                    mainActivity.setBottomVisible(true);
                    policeFragments.setShaixuanGone();
                }
                break;
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

    @OnClick({R.id.daily_police_tv, R.id.daily_device_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.daily_device_tv:
                if (mSelectPosition == 0) {
                    return;
                }
                vp.setCurrentItem(0);
                mSelectPosition = 0;
                break;
            case R.id.daily_police_tv:
                if (mSelectPosition == 1) {
                    return;
                }
                vp.setCurrentItem(1);
                mSelectPosition = 1;
                break;
        }
    }

    private void selectTitleTv(TextView selected, TextView unselected) {
        selected.setSelected(true);
        selected.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.sp17));
        unselected.setSelected(false);
        unselected.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.sp15));
    }
}
