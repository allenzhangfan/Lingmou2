package cloud.antelope.lingmou.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.utils.KeyboardUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.di.component.DaggerCarDepotSearchComponent;
import cloud.antelope.lingmou.di.module.CarDepotSearchModule;
import cloud.antelope.lingmou.mvp.contract.CarDepotSearchContract;
import cloud.antelope.lingmou.mvp.model.entity.SearchFragment;
import cloud.antelope.lingmou.mvp.presenter.CarDepotSearchPresenter;
import cloud.antelope.lingmou.mvp.ui.adapter.CarDepotSearchPagerAdapter;
import cloud.antelope.lingmou.mvp.ui.fragment.CarDepotSearchDeviceFragment;
import cloud.antelope.lingmou.mvp.ui.fragment.CarDepotSearchPlateFragment;
import cloud.antelope.lingmou.mvp.ui.widget.CustomViewPager;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class CarDepotSearchActivity extends BaseActivity<CarDepotSearchPresenter> implements CarDepotSearchContract.View {

    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.iv_clear)
    ImageView ivClear;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.stl)
    SlidingTabLayout stl;
    @BindView(R.id.vp)
    CustomViewPager vp;

    @Inject
    CarDepotSearchPagerAdapter mAdapter;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerCarDepotSearchComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .carDepotSearchModule(new CarDepotSearchModule(this, getSupportFragmentManager()))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_car_depot_search; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @OnClick({R.id.tv_cancel, R.id.iv_clear})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                onBackPressedSupport();
                break;
            case R.id.iv_clear:
                etSearch.setText("");
                break;
        }
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
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
                    KeyboardUtils.hideSoftInput(CarDepotSearchActivity.this);
                    etSearch.clearFocus();
                    Message message = new Message();
                    message.obj = etSearch.getText().toString();
                    CarDepotSearchPlateFragment.getInstance().setData(message);
                    CarDepotSearchDeviceFragment.getInstance().setData(message);
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

    @Override
    protected void onPause() {
        super.onPause();
        KeyboardUtils.hideSoftInput(this);
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
        finish();
    }

}
