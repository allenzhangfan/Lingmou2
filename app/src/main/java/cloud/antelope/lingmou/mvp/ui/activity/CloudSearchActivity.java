package cloud.antelope.lingmou.mvp.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.utils.KeyboardUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.di.component.DaggerCloudSearchComponent;
import cloud.antelope.lingmou.di.module.CloudSearchModule;
import cloud.antelope.lingmou.mvp.contract.CloudSearchContract;
import cloud.antelope.lingmou.mvp.model.entity.SearchFragment;
import cloud.antelope.lingmou.mvp.presenter.CloudSearchPresenter;
import cloud.antelope.lingmou.mvp.ui.adapter.SearchPagerAdapter;
import cloud.antelope.lingmou.mvp.ui.fragment.DeployListFragment;
import cloud.antelope.lingmou.mvp.ui.fragment.SearchListFragment;
import cloud.antelope.lingmou.mvp.ui.widget.CustomViewPager;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class CloudSearchActivity extends BaseActivity<CloudSearchPresenter> implements CloudSearchContract.View {

    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.stl)
    SlidingTabLayout stl;
    @BindView(R.id.vp)
    CustomViewPager vp;
    @BindView(R.id.iv_clear)
    ImageView ivClear;
    @Inject
    SearchPagerAdapter mAdapter;

    private String mType;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerCloudSearchComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .cloudSearchModule(new CloudSearchModule(this, getSupportFragmentManager()))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_cloud_search; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        //alarm : 告警搜索
        //device : 设备搜索
        //deploy : 临控搜索
        //face:人脸搜索
        //body:人体搜索
        mType = getIntent().getStringExtra("type");
        if (!TextUtils.isEmpty(mType)) {
            stl.setVisibility(View.GONE);
            ArrayList<Fragment> fragmentList = new ArrayList<>();
            switch (mType) {
                case "face":
                    SearchListFragment faceFragment = new SearchListFragment();
                    faceFragment.setStatusType(SearchListFragment.FACE);
                    fragmentList.add(faceFragment);
                    break;
                case "body":
                    SearchListFragment bodyFragment = new SearchListFragment();
                    bodyFragment.setStatusType(SearchListFragment.BODY);
                    fragmentList.add(bodyFragment);
                    break;
                case "alarm":
                    etSearch.setHint(R.string.police_search_hint);
                    SearchListFragment alarmFragment = new SearchListFragment();
                    alarmFragment.setStatusType(SearchListFragment.ALERT);
                    int taskType = getIntent().getIntExtra("taskType", -1);
                    Bundle bundle = new Bundle();
                    bundle.putInt("taskType",taskType);
                    alarmFragment.setArguments(bundle);
                    fragmentList.add(alarmFragment);
                    break;
                case "device":
                    etSearch.setHint(R.string.device_search_hint);
                    SearchListFragment deviceFragment = new SearchListFragment();
                    deviceFragment.setStatusType(SearchListFragment.VIDEO);
                    fragmentList.add(deviceFragment);
                    break;
                case "deploy":
                    etSearch.setHint(R.string.deploy_search_hint);
                    DeployListFragment deployFragment = new DeployListFragment();
                    Bundle bundle0 = new Bundle();
                    bundle0.putInt("statusType", DeployListFragment.ALL);
                    bundle0.putBoolean("fromSearch", true);
                    deployFragment.setArguments(bundle0);
                    fragmentList.add(deployFragment);
                    break;
            }
            mAdapter.setFragmentList(fragmentList);

        } else {
            etSearch.setHint(R.string.cloud_search_hint);
            ArrayList<Fragment> fragmentList = new ArrayList<>();
            SearchListFragment fragment0 = new SearchListFragment();
            fragment0.setStatusType(SearchListFragment.FACE);
            fragmentList.add(fragment0);

            SearchListFragment fragment1 = new SearchListFragment();
            fragment1.setStatusType(SearchListFragment.BODY);
            fragmentList.add(fragment1);

            SearchListFragment fragment2 = new SearchListFragment();
            fragment2.setStatusType(SearchListFragment.ALERT);
            fragmentList.add(fragment2);

            SearchListFragment fragment3 = new SearchListFragment();
            fragment3.setStatusType(SearchListFragment.VIDEO);
            fragmentList.add(fragment3);
            mAdapter.setFragmentList(fragmentList);
            vp.setIsPagingEnabled(true);
        }
        vp.setAdapter(mAdapter);
        stl.setViewPager(vp);
        stl.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                vp.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH && !TextUtils.isEmpty(etSearch.getText().toString())) {
                    for (int i = 0; i < mAdapter.getFragmentList().size(); i++) {
                        SearchFragment fragment = (SearchFragment) mAdapter.getFragmentList().get(i);
                        fragment.onSearch(etSearch.getText().toString().trim());
                        if (TextUtils.isEmpty(mType)) {
                            stl.setVisibility(View.VISIBLE);
                        }
                    }
                    KeyboardUtils.hideSoftInput(CloudSearchActivity.this);
                    etSearch.clearFocus();
                }
                return true;
            }
        });
        etSearch.postDelayed(() -> {
            if (etSearch != null)
                KeyboardUtils.showSoftInput(etSearch);
        }, 700);
    }

    @OnClick({R.id.tv_cancel,R.id.iv_clear})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_cancel:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                } else {
                    finish();
                }
                break;
            case R.id.iv_clear:
                etSearch.setText("");
                break;
        }

    }

    @Override
    public void showLoading(String message) {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        KeyboardUtils.hideSoftInput(this);
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
        finish();
    }

}
