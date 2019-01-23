package cloud.antelope.lingmou.mvp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.utils.LogUtils;
import com.lingdanet.safeguard.common.utils.SizeUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.di.component.DaggerCloudEyeComponent;
import cloud.antelope.lingmou.di.module.CloudEyeModule;
import cloud.antelope.lingmou.mvp.contract.CloudEyeContract;
import cloud.antelope.lingmou.mvp.presenter.CloudEyePresenter;
import cloud.antelope.lingmou.mvp.ui.adapter.CloudEyeFragmentAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.IndicatorView;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class CloudEyeFragment extends BaseFragment<CloudEyePresenter> implements CloudEyeContract.View {


    @BindView(R.id.favorite_tv)
    TextView mFavoriteTv;
    @BindView(R.id.history_tv)
    TextView mHistoryTv;
    @BindView(R.id.cloud_viewpager)
    ViewPager mCloudViewpager;
    @BindView(R.id.indicatorView)
    IndicatorView mIndicatorView;

    @Inject
    CloudEyeFragmentAdapter mCloudEyeFragmentAdapter;

    @Inject
    CloudHistoryFragment mCloudHistoryFragment;
    @Inject
    CollectionFragment mCollectionFragment;
    @Inject
    List<Fragment> mFragmentList;

    private static float mMinSp = 18F;
    private static float mMaxSp = 28F;
    private static int DIVIDER_WIDTH = SizeUtils.dp2px(25F);
    private static int DIVIDER_MAX_WIDTH = SizeUtils.dp2px(75F);
    private static int DIVIDER_MARGIN_ONE_LEFT = SizeUtils.dp2px(37.5F);
    private static int DIVIDER_MARGIN_TWO_LEFT = SizeUtils.dp2px(97.5F);
    private static int TEXTVIEW_WIDTH = SizeUtils.dp2px(60F);
    private static CloudEyeFragment fragment;

    public static CloudEyeFragment newInstance() {
        fragment = new CloudEyeFragment();
        return fragment;
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerCloudEyeComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .cloudEyeModule(new CloudEyeModule(this, getFragmentManager()))
                .build()
                .inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cloud_eye, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mFragmentList.add(mCollectionFragment);
        mFragmentList.add(mCloudHistoryFragment);
        mCloudViewpager.setAdapter(mCloudEyeFragmentAdapter);
        mIndicatorView.setViewPager(mCloudViewpager);
        mCloudViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                LogUtils.i("cxm", "position = "+position + ",positionOffset = " + positionOffset + ", offSetPix = " + positionOffsetPixels);
                float currentMinSize = mMinSp + 10F * positionOffset;
                float currentMaxSize = mMaxSp - 10F * positionOffset;
                if (position == 0) {
                    mHistoryTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, currentMinSize);
                    mFavoriteTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, currentMaxSize);
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mFavoriteTv.setTextColor(getResources().getColor(R.color.color_cloud_eye_deep));
                    mHistoryTv.setTextColor(getResources().getColor(R.color.default_text_color));
                } else {
                    mFavoriteTv.setTextColor(getResources().getColor(R.color.default_text_color));
                    mHistoryTv.setTextColor(getResources().getColor(R.color.color_cloud_eye_deep));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void setData(@Nullable Object data) {

    }

    @Override
    public void showLoading(String message) {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ArmsUtils.snackbarText(message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @OnClick({R.id.favorite_tv, R.id.history_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.favorite_tv:
                mCloudViewpager.setCurrentItem(0);
                break;
            case R.id.history_tv:
                mCloudViewpager.setCurrentItem(1);
                break;
        }
    }
}
