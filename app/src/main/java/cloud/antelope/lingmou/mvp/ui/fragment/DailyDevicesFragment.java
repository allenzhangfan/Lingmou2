package cloud.antelope.lingmou.mvp.ui.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.di.component.DaggerDailyDevicesComponent;
import cloud.antelope.lingmou.di.module.DailyDevicesModule;
import cloud.antelope.lingmou.mvp.contract.DailyDevicesContract;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrgMainEntity;
import cloud.antelope.lingmou.mvp.presenter.DailyDevicesPresenter;
import cloud.antelope.lingmou.mvp.ui.activity.CloudSearchActivity;
import cloud.antelope.lingmou.mvp.ui.activity.DeviceListActivity;
import cloud.antelope.lingmou.mvp.ui.activity.DeviceMapActivity;
import cloud.antelope.lingmou.mvp.ui.activity.NewMainActivity;
import cloud.antelope.lingmou.mvp.ui.adapter.DeviceParentAdapter;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class DailyDevicesFragment extends BaseFragment<DailyDevicesPresenter> implements DailyDevicesContract.View, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.tv_search)
    TextView tvSearch;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.srl)
    SwipeRefreshLayout srl;
    @BindView(R.id.ib_map)
    ImageButton ibMap;
    @BindView(R.id.no_permission_rl)
    RelativeLayout mNoPermissionRl;
    @Inject
    DeviceParentAdapter mDeviceParentAdapter;
    private ArrayList<OrgMainEntity> mOrgMainEntities;
    private boolean hasData;
    private String mOrgId;
    private String mOrgRootId = "0";
    private int page = 1;
    private int pageSize = 3000;
    private OrgMainEntity newEntity;

    public static DailyDevicesFragment newInstance() {
        DailyDevicesFragment fragment = new DailyDevicesFragment();
        return fragment;
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerDailyDevicesComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .dailyDevicesModule(new DailyDevicesModule(this))
                .build()
                .inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_daily_devices, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mOrgId = SPUtils.getInstance().getString(Constants.CONFIG_ORGANIZATION_ID, "0");
        srl.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        srl.setOnRefreshListener(this);
        mOrgMainEntities = new ArrayList<>();
        rv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int position = parent.getChildAdapterPosition(view);
                outRect.top = getResources().getDimensionPixelSize(R.dimen.dp16);
                outRect.left = position % 2 == 0 ? getResources().getDimensionPixelSize(R.dimen.dp16) : getResources().getDimensionPixelSize(R.dimen.dp8);
                outRect.right = position % 2 == 0 ? getResources().getDimensionPixelSize(R.dimen.dp8) : getResources().getDimensionPixelSize(R.dimen.dp16);
            }
        });
        rv.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rv.setAdapter(mDeviceParentAdapter);
        mDeviceParentAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                newEntity = (OrgMainEntity) adapter.getItem(position);

                List<OrgMainEntity> parentEntities = getParentIdOrgs(newEntity.id);
                if (parentEntities.isEmpty() || newEntity.mIsRootOrg) {
                    //空，进行Camera请求
                    mPresenter.getMainCameras(page, pageSize, new String[]{newEntity.id});
                } else {
                    Intent intent = new Intent(getActivity(), DeviceListActivity.class);
                    intent.putExtra("OrgMainEntity", newEntity);
                    intent.putParcelableArrayListExtra("mOrgMainEntities", mOrgMainEntities);
                    startActivity(intent);
                }
            }
        });
        mPresenter.getMainOrgs();
    }

    @Override
    public void setData(@Nullable Object data) {

    }

    @Override
    public void showLoading(String s) {

    }

    @Override
    public void hideLoading() {

    }

    @OnClick({R.id.tv_search, R.id.ib_map})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_search:
                ActivityOptions activityOptions = null;
                Intent searchIntent = new Intent(getActivity(), CloudSearchActivity.class);
                searchIntent.putExtra("type", "device");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    activityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity(), tvSearch, "searchText");
                    startActivity(searchIntent, activityOptions.toBundle());
                } else {
                    startActivity(searchIntent);
                }
                break;
            case R.id.ib_map:
                startActivity(new Intent(getActivity(), DeviceMapActivity.class));
                break;
        }
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
    public void getMainOrgsSuccess(List<OrgMainEntity> entityList) {
        hasData = true;
        //进行CreateText和List展示
        srl.setRefreshing(false);
        mOrgMainEntities.clear();
        mOrgMainEntities.addAll(entityList);
        if (entityList != null) {
            for (OrgMainEntity entity : entityList) {
                String id = entity.id;
                if (id.equals(mOrgId)) {
                    Object parentId = entity.parentId;
                    mOrgRootId = String.valueOf(parentId);
                    break;
                }
            }
        }
        List<OrgMainEntity> rootOrgs = getParentIdOrgs(mOrgRootId);
        if (!rootOrgs.isEmpty()) {
            OrgMainEntity rootOrg = rootOrgs.get(0);
            String rootOrgId = rootOrg.id;
            List<OrgMainEntity> orgMainEntityList = getParentIdOrgs(rootOrgId);
            for (OrgMainEntity entity : orgMainEntityList) {
                entity.level1 = true;
            }
            rootOrg.mAliasName = rootOrg.organizationName/* + "(本部)"*/;
            rootOrg.mIsRootOrg = true;
            rootOrg.level1 = true;
            orgMainEntityList.add(0, rootOrg);
            mDeviceParentAdapter.setNewData(orgMainEntityList);
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
    public void getMainOrgsFail() {
        srl.setEnabled(true);
        srl.setRefreshing(false);
    }

    @Override
    public void getOrgNoPermission() {
        srl.setEnabled(true);
        srl.setRefreshing(false);
        showNoPermissionView();
    }

    @Override
    public void getMainOrgCamerasSuccess(List<OrgCameraEntity> entityList) {
        if (null == entityList || entityList.isEmpty()) {
            ToastUtils.showShort(R.string.hint_no_org_videos);
        } else {
            Intent intent = new Intent(getActivity(), DeviceListActivity.class);
            intent.putParcelableArrayListExtra("OrgCameraEntities", (ArrayList<? extends Parcelable>) entityList);
            intent.putExtra("OrgMainEntity", newEntity);
            intent.putParcelableArrayListExtra("mOrgMainEntities", mOrgMainEntities);
            startActivity(intent);
        }
    }

    @Override
    public void getMainOrgCameraFail() {

    }

    @Override
    public void getCameraNoPermission() {

    }

    @Override
    public void onRefresh() {
        mPresenter.getMainOrgs();
    }

    private void showNoPermissionView() {
        rv.setVisibility(View.GONE);
        mNoPermissionRl.setVisibility(View.VISIBLE);
    }
}
