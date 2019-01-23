package cloud.antelope.lingmou.mvp.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.di.component.DaggerDeviceListComponent;
import cloud.antelope.lingmou.di.module.DeviceListModule;
import cloud.antelope.lingmou.mvp.contract.DeviceListContract;
import cloud.antelope.lingmou.mvp.model.entity.DeviceActivitiesCloseEvent;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrgMainEntity;
import cloud.antelope.lingmou.mvp.presenter.DeviceListPresenter;
import cloud.antelope.lingmou.mvp.ui.fragment.DevicesFragment;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class DeviceListActivity extends BaseActivity<DeviceListPresenter> implements DeviceListContract.View {

    private DevicesFragment fragment;
    @BindView(R.id.ib_close)
    ImageButton ibClose;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ib_map)
    ImageButton ibMap;
    private static DeviceListActivity instance;
    private ArrayList<OrgMainEntity> mOrgMainEntities;

    public static DeviceListActivity getInstance() {
        return instance;
    }

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerDeviceListComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .deviceListModule(new DeviceListModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_device_list; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        instance = this;
        Bundle bundle = new Bundle();
        OrgMainEntity entity = getIntent().getParcelableExtra("OrgMainEntity");
        mOrgMainEntities = getIntent().getParcelableArrayListExtra("mOrgMainEntities");
        bundle.putParcelable("OrgMainEntity", entity);
        bundle.putParcelableArrayList("mOrgMainEntities", mOrgMainEntities);
        bundle.putParcelableArrayList("OrgCameraEntities", getIntent().getParcelableArrayListExtra("OrgCameraEntities"));
        fragment = new DevicesFragment();
        fragment.setArguments(bundle);
        loadRootFragment(R.id.fl_container, fragment);
        tvTitle.setText(entity.organizationName);
        List<OrgMainEntity> parentEntities = getParentIdOrgs(entity.id);
        if (parentEntities.isEmpty() || entity.mIsRootOrg) {
            ibClose.setVisibility(View.VISIBLE);
        }
    }

    //获取父id，获取所有父id为parentOrgId的子组织
    private List<OrgMainEntity> getParentIdOrgs(String parentOrgId) {
        List<OrgMainEntity> newList = new ArrayList<>();
        for (OrgMainEntity entity : mOrgMainEntities) {
            if (null != entity) {
                Object parentId = entity.parentId;
                if (parentId instanceof Double) {
                    if ((Double) parentId == Double.parseDouble(parentOrgId)) {
                        newList.add(entity);
                    }
                } else if (parentId instanceof String) {
                    if (parentOrgId.equals(parentId)) {
                        newList.add(entity);
                    }
                }
            }

        }
        return newList;
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
    public void onBackPressedSupport() {
        if (fragment.onPressBack()) {
            return;
        }
        super.onBackPressedSupport();
    }

    public void setIbCloseVisibility(boolean visible) {
        ibClose.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @OnClick({R.id.head_left_iv, R.id.ib_close,R.id.ib_map})
    public void onCLick(View view) {
        switch (view.getId()) {
            case R.id.ib_close:
                EventBus.getDefault().post(new DeviceActivitiesCloseEvent());
                break;
            case R.id.head_left_iv:
                onBackPressedSupport();
                break;
            case R.id.ib_map:
                startActivity(new Intent(DeviceListActivity.this, DeviceMapActivity.class));
                break;
        }
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

    @Subscriber
    public void onClose(DeviceActivitiesCloseEvent event){
        finish();
    }

}
