package cloud.antelope.lingmou.mvp.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.modle.LoadingDialog;
import com.lingdanet.safeguard.common.utils.LocationUtils;
import com.lingdanet.safeguard.common.utils.LogUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.SizeUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.PermissionUtils;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.di.component.DaggerOrganizationStructureComponent;
import cloud.antelope.lingmou.di.module.OrganizationStructureModule;
import cloud.antelope.lingmou.mvp.contract.OrganizationStructureContract;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrgMainEntity;
import cloud.antelope.lingmou.mvp.presenter.OrganizationStructurePresenter;
import cloud.antelope.lingmou.mvp.ui.adapter.OrgCameraAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.OrgMainAdapter;
import pub.devrel.easypermissions.AfterPermissionGranted;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class OrganizationStructureActivity extends BaseActivity<OrganizationStructurePresenter>
        implements OrganizationStructureContract.View, SwipeRefreshLayout.OnRefreshListener,
        PermissionUtils.HasPermission {

    @BindView(R.id.org_main_ll)
    LinearLayout mOrgLl;
    @BindView(R.id.org_content_layout)
    FrameLayout mContentLayout;
    @BindView(R.id.parent_main_hz_scrollview)
    HorizontalScrollView mHorizontalScrollView;
    @BindView(R.id.org_recyview)
    RecyclerView mOrgReclyView;
    @BindView(R.id.camera_recyview)
    RecyclerView mCameraReclyView;
    @BindView(R.id.org_new_srfl)
    SwipeRefreshLayout mOrgNewSrfl;
    @BindView(R.id.no_permission_rl)
    RelativeLayout mNoPermissionRl;
    // @BindView(R.id.search_ib)
    // ImageButton mSearchIb;
    @BindView(R.id.location_ib)
    ImageButton mLocationIb;
    @BindView(R.id.back_iv)
    ImageButton mBackIv;
    @BindView(R.id.ib_close)
    ImageButton ibClose;

    OrgCameraAdapter mOrgCameraAdapter;
    OrgMainAdapter mOrgMainAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.LayoutManager mCameraLayoutManager;
    RecyclerView.ItemAnimator mItemAnimator;
    RecyclerView.ItemDecoration mItemDecoration;
    LoadingDialog mLoadingDialog;

    private List<OrgMainEntity> mOrgMainEntities;
    private Drawable mLeftDrawable;
    private int page = 1;
    private int pageSize = 3000;

    private String mCurrentOrgPath;

    private OrgMainEntity mCurrentClickOrgEntity;
    private OrgCameraEntity mClickCamera;

    private String mOrgId;
    private String mOrgRootId = "0";

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerOrganizationStructureComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .organizationStructureModule(new OrganizationStructureModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_organization_structure; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mOrgId = SPUtils.getInstance().getString(Constants.CONFIG_ORGANIZATION_ID, "0");
        mOrgCameraAdapter = new OrgCameraAdapter(null);
        mOrgMainAdapter = new OrgMainAdapter(null);
        mLayoutManager = new LinearLayoutManager(this);
        mCameraLayoutManager = new LinearLayoutManager(this);
        mItemAnimator = new DefaultItemAnimator();
        mItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        mLoadingDialog = new LoadingDialog(this);

        mLeftDrawable = getResources().getDrawable(R.drawable.org_right_arrow);
        mLeftDrawable.setBounds(0, 0, mLeftDrawable.getMinimumWidth(), mLeftDrawable.getMinimumHeight());

        mOrgMainEntities = new ArrayList<>();
        mOrgNewSrfl.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);

        mOrgReclyView.setHasFixedSize(false);
        mOrgReclyView.setLayoutManager(mLayoutManager);
        mOrgReclyView.setItemAnimator(mItemAnimator);
        mOrgReclyView.setAdapter(mOrgMainAdapter);

        mCameraReclyView.setHasFixedSize(false);
        mCameraReclyView.setLayoutManager(mCameraLayoutManager);
        mCameraReclyView.setItemAnimator(mItemAnimator);
        mCameraReclyView.setAdapter(mOrgCameraAdapter);

        mOrgNewSrfl.setOnRefreshListener(this);
        mOrgReclyView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (null != mOrgNewSrfl) {
                    mOrgNewSrfl.setRefreshing(true);
                }
                onRefresh();
            }
        }, 100);

        //响应点击事件
        mOrgMainAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                OrgMainEntity newEntity = (OrgMainEntity) adapter.getItem(position);
                OrgMainEntity entity = new OrgMainEntity(newEntity);
                mCurrentClickOrgEntity = entity;
                //查看是否有子元素，没有的话，证明就是最小组织，点击获取摄像机
                List<OrgMainEntity> parentEntities = getParentIdOrgs(entity.id);
                if (parentEntities.isEmpty() || entity.mIsRootOrg) {
                    //空，进行Camera请求
                    mPresenter.getMainCameras(page, pageSize, new String[]{entity.id});
                    mCurrentOrgPath = newEntity.treeDesc + newEntity.organizationName;
                } else {
                    createAddTextView(false, entity);
                    //非空，进入下个
                    mOrgNewSrfl.setEnabled(false);
                    if (!entity.mIsRootOrg) {
                        entity.mIsRootOrg=true;
                        entity.mAliasName=entity.organizationName /*+ "(本部)"*/;
                    }
                    parentEntities.add(0, entity);
                    mOrgMainAdapter.setNewData(parentEntities);
                }
            }
        });

        mOrgCameraAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                boolean hasVideoLive = SPUtils.getInstance().getBoolean(Constants.PERMISSION_HAS_VIDEO_LIVE, false);
                if (hasVideoLive) {
                    OrgCameraEntity entity = (OrgCameraEntity) adapter.getItem(position);
                    //进入直播界面
                    Intent intent = new Intent(view.getContext(), VideoDetailActivity.class);
                    intent.putExtra("cid", String.valueOf(entity.getManufacturerDeviceId()));
                    intent.putExtra("cameraName", entity.getDeviceName());
                    intent.putExtra("cameraSn", entity.getSn());
                    intent.putExtra("isFromOrgMap", true);
                    startActivity(intent);
                } else {
                    ToastUtils.showShort(R.string.hint_no_permission);
                }

            }
        });


        mOrgCameraAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            mClickCamera = (OrgCameraEntity) adapter.getItem(position);
            checkLocationPerm();
            if (!LocationUtils.isGpsEnabled()) {
                LocationUtils.openGpsSettings();
            }
        });
    }

    private void checkHierarchy() {
        if (null != mOrgLl) {
            int childSize = mOrgLl.getChildCount();
            if (childSize > 1) {
                ibClose.setVisibility(View.VISIBLE);
                return;
            }
        }
        ibClose.setVisibility(View.GONE);
    }

    @OnClick({R.id.back_iv, R.id.location_ib, R.id.ib_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                // startActivity(new Intent(this, SearchCameraActivity.class));
                onBackPressedSupport();
                break;
            case R.id.location_ib:
                mClickCamera = null;
                checkLocationPerm();
                if (!LocationUtils.isGpsEnabled()) {
                    LocationUtils.openGpsSettings();
                }
                break;
            case R.id.ib_close:
                finish();
                break;
        }
    }

    @AfterPermissionGranted(PermissionUtils.RC_LOCATION_PERM)
    public void checkLocationPerm() {
        PermissionUtils.locationTask(this);
    }

    @Override
    public void showLoading(String message) {
        mLoadingDialog.show();
    }

    @Override
    public void hideLoading() {
        mLoadingDialog.dismiss();
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

    @Override
    public void getMainOrgsSuccess(List<OrgMainEntity> entityList) {
        //进行CreateText和List展示
        mOrgNewSrfl.setRefreshing(false);
        mOrgMainEntities.clear();
        mOrgMainEntities.addAll(entityList);
        showOrgView();
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
            mOrgLl.removeAllViews();
            createAddTextView(false, rootOrg);
            String rootOrgId = rootOrg.id;
            List<OrgMainEntity> orgMainEntityList = getParentIdOrgs(rootOrgId);
            rootOrg.mAliasName=rootOrg.organizationName /*+ "(本部)"*/;
            rootOrg.mIsRootOrg=true;
            orgMainEntityList.add(0, rootOrg);
            mOrgMainAdapter.setNewData(orgMainEntityList);
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
        mOrgNewSrfl.setEnabled(true);
        mOrgNewSrfl.setRefreshing(false);
    }

    @Override
    public void getMainOrgCamerasSuccess(List<OrgCameraEntity> entityList) {
        if (null == entityList || entityList.isEmpty()) {
            ToastUtils.showShort(R.string.hint_no_org_videos);
        } else {
            mOrgNewSrfl.setEnabled(false);
            createAddTextView(false, mCurrentClickOrgEntity);
            mOrgMainAdapter.setNewData(null);
            showCameraView();
            mOrgCameraAdapter.setOrgPath(mCurrentOrgPath);
            Collections.sort(entityList, new Comparator<OrgCameraEntity>() {
                @Override
                public int compare(OrgCameraEntity o1, OrgCameraEntity o2) {

                    return (int) (o2.getDeviceState() - o1.getDeviceState());
                }
            });
            mOrgCameraAdapter.setNewData(entityList);
        }
    }

    @Override
    public void getMainOrgCameraFail() {
        mCurrentClickOrgEntity = null;
    }

    @Override
    public void getOrgNoPermission() {
        mOrgNewSrfl.setEnabled(true);
        mOrgNewSrfl.setRefreshing(false);
        showNoPermissionView();
    }

    @Override
    public void getCameraNoPermission() {
        mCurrentClickOrgEntity = null;
        ToastUtils.showShort(R.string.hint_no_permission);
    }

    private void createAddTextView(boolean isRootClick, OrgMainEntity entity) {
        final TextView textView = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.rightMargin = SizeUtils.dp2px(11);
        textView.setTextSize(14);
        textView.setTextColor(Color.parseColor("#999999"));
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(params);
        textView.setMaxLines(1);
        // textView.setEllipsize(TextUtils.TruncateAt.END);
        if (isRootClick) {
            textView.setText(entity.organizationName);
        } else {
            textView.setText(TextUtils.isEmpty(entity.mAliasName) ? entity.organizationName : entity.mAliasName);
        }
        textView.setCompoundDrawables(null, null, mLeftDrawable, null);
        textView.setCompoundDrawablePadding(SizeUtils.dp2px(11));
        textView.setTag(entity);
        mOrgLl.addView(textView);
        mHorizontalScrollView.post(new Runnable() {
            @Override
            public void run() {
                if(mHorizontalScrollView!=null)
                mHorizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        });
        //循环，然后让最后一个TextView变蓝
        int childCount = mOrgLl.getChildCount();
        for (int i = 0; i < childCount; i++) {
            TextView tv = (TextView) mOrgLl.getChildAt(i);
            if (i == childCount - 1) {
                //最后一个，颜色变蓝
                tv.setTextColor(Color.parseColor("#36A8FF"));
            } else {
                tv.setTextColor(Color.parseColor("#999999"));
            }
        }
        checkHierarchy();
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int childCount = mOrgLl.getChildCount();
                OrgMainEntity tagEntity = (OrgMainEntity) textView.getTag();
                for (int i = childCount - 1; i > -1; i--) {
                    TextView childTv = (TextView) mOrgLl.getChildAt(i);
                    if (null != childTv && childTv.getTag() == tagEntity) {
                        //证明是最后一个
                        if (i == childCount - 1) {
                            return;
                        } else {
                            //干掉i+1之后的所有view和fragment
                            for (int j = mOrgLl.getChildCount() - 1; j > i; j--) {
                                TextView orgTv = (TextView) mOrgLl.getChildAt(j);
                                LogUtils.i("cxm", "orgTv -- i = " + i + ",orgTv = " + orgTv);
                                if (null != orgTv) {
                                    LogUtils.i("cxm", "orgTv -- i = " + i + ",orgTv.text = " + orgTv.getText());
                                    mOrgLl.removeView(orgTv);
                                    ((TextView) (mOrgLl.getChildAt(mOrgLl.getChildCount() - 1))).setTextColor(Color.parseColor("#36A8FF"));
                                }
                            }
                            if (i == 0) {
                                //重置根目录下的元素
                                List<OrgMainEntity> rootOrgs = getParentIdOrgs(mOrgRootId);
                                if (!rootOrgs.isEmpty()) {
                                    OrgMainEntity rootOrg = rootOrgs.get(0);
                                    mOrgLl.removeAllViews();
                                    createAddTextView(true, rootOrg);
                                    mOrgCameraAdapter.setNewData(null);
                                    showOrgView();
                                    String rootOrgId = rootOrg.id;
                                    List<OrgMainEntity> orgMainEntityList = getParentIdOrgs(rootOrgId);

                                    if (!entity.mIsRootOrg) {
                                        rootOrg.mAliasName=rootOrg.organizationName/* + "(本部)"*/;
                                        rootOrg.mIsRootOrg=true;
                                    }
                                    orgMainEntityList.add(0, rootOrg);
                                    mOrgMainAdapter.setNewData(orgMainEntityList);
                                    mOrgNewSrfl.setEnabled(true);
                                }
                            } else {
                                mOrgCameraAdapter.setNewData(null);
                                showOrgView();
                                List<OrgMainEntity> orgParentEntities = getParentIdOrgs(tagEntity.id);
                                mOrgNewSrfl.setEnabled(false);
                                if (!entity.mIsRootOrg) {
                                    entity.mAliasName=entity.organizationName /*+ "(本部)"*/;
                                    entity.mIsRootOrg=true;
                                }
                                orgParentEntities.add(0, entity);
                                mOrgMainAdapter.setNewData(orgParentEntities);
                            }
                        }
                    }
                }
                checkHierarchy();
            }
        });
    }

    @Override
    public void onBackPressedSupport() {
        if (null != mOrgLl) {
            int childSize = mOrgLl.getChildCount();
            if (childSize > 1) {
                //表明点了二级结构
                TextView textView = (TextView) mOrgLl.getChildAt(childSize - 2);
                TextView currentTextView = (TextView) mOrgLl.getChildAt(childSize - 1);
                mOrgLl.removeView(currentTextView);
                textView.setTextColor(Color.parseColor("#36A8FF"));
                OrgMainEntity tagEntity = (OrgMainEntity) textView.getTag();
                mOrgCameraAdapter.setNewData(null);
                showOrgView();
                List<OrgMainEntity> orgParentEntities = getParentIdOrgs(tagEntity.id);
                mOrgNewSrfl.setEnabled(childSize == 2);
                if (!tagEntity.mIsRootOrg) {
                    tagEntity.mAliasName=tagEntity.organizationName /*+ "(本部)"*/;
                    tagEntity.mIsRootOrg=true;
                }
                orgParentEntities.add(0, tagEntity);
                mOrgMainAdapter.setNewData(orgParentEntities);
                checkHierarchy();
                return;
            }
        }
        super.onBackPressedSupport();
    }

    @Override
    public void onRefresh() {
        if (null != mPresenter) {
            mPresenter.getMainOrgs();
        }
    }

    @Override
    public void doNext(int permId) {
        if (PermissionUtils.RC_LOCATION_PERM == permId) {
            Intent intent = new Intent(this, CameraMapActivity.class);
            if (mClickCamera != null) {
                intent.putExtra(Constants.SELECT_CAMERA, mClickCamera);
            }
            startActivity(intent);
        }
    }

    private void showCameraView() {
        mOrgReclyView.setVisibility(View.GONE);
        mCameraReclyView.setVisibility(View.VISIBLE);
        mNoPermissionRl.setVisibility(View.GONE);
    }

    private void showOrgView() {
        mOrgReclyView.setVisibility(View.VISIBLE);
        mCameraReclyView.setVisibility(View.GONE);
        mNoPermissionRl.setVisibility(View.GONE);
    }

    private void showNoPermissionView() {
        mOrgReclyView.setVisibility(View.GONE);
        mCameraReclyView.setVisibility(View.GONE);
        mNoPermissionRl.setVisibility(View.VISIBLE);
    }
}
