package cloud.antelope.lingmou.mvp.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.modle.LoadingDialog;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.di.component.DaggerOrganizationNewComponent;
import cloud.antelope.lingmou.di.module.OrganizationNewModule;
import cloud.antelope.lingmou.mvp.contract.OrganizationNewContract;
import cloud.antelope.lingmou.mvp.model.entity.CameraItem;
import cloud.antelope.lingmou.mvp.model.entity.OrganizationEntity;
import cloud.antelope.lingmou.mvp.presenter.OrganizationNewPresenter;
import cloud.antelope.lingmou.mvp.ui.adapter.OrganizationAdapter;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class OrganizationNewFragment extends BaseFragment<OrganizationNewPresenter>
        implements OrganizationNewContract.View,
        SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.organization_new_rclv)
    RecyclerView mOrganizationRclv;
    @BindView(R.id.organization_new_srfl)
    SwipeRefreshLayout mOrganizationSrfl;
    @BindView(R.id.empty_no_network_tv)
    TextView mEmptyNoNetworkTv;

    @Inject
    RecyclerView.LayoutManager mLayoutManager;
    @Inject
    OrganizationAdapter mOrganizationAdapter;
    @Inject
    RecyclerView.ItemAnimator mItemAnimator;
    @Inject
    RecyclerView.ItemDecoration mItemDecoration;
    @Inject
    LoadingDialog mLoadingDialog;

    private boolean mIsRootFragment = true;
    private String mTitle;
    private OrganizationEntity mOrgItem;
    private String mOrgName;

    private boolean mIsRefresh;
    private boolean mIsItemClick;
    private String mItemName;

    public static OrganizationNewFragment newInstance(boolean isRoot, String title, OrganizationEntity orgItem, String orgName) {
        OrganizationNewFragment fragment = new OrganizationNewFragment();
        Bundle args = new Bundle();
        args.putBoolean("isRoot", isRoot);
        if (!isRoot) {
            args.putString("title", title);
            args.putSerializable("item", orgItem);
            args.putString("orgName", orgName);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setupFragmentComponent(AppComponent appComponent) {
        DaggerOrganizationNewComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .organizationNewModule(new OrganizationNewModule(this))
                .build()
                .inject(this);
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.organization_new_layout, container, false);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        mIsRootFragment = arguments.getBoolean("isRoot");
        if (!mIsRootFragment) {
            mTitle = arguments.getString("title");
            mOrgItem = (OrganizationEntity) arguments.getSerializable("item");
            mOrgName = arguments.getString("orgName");
        }

        mOrganizationSrfl.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        mOrganizationSrfl.setOnRefreshListener(this);
        mOrganizationRclv.setHasFixedSize(false);

        mOrganizationRclv.setLayoutManager(mLayoutManager);
        mOrganizationRclv.setItemAnimator(mItemAnimator);
        // mOrganizationRclv.addItemDecoration(mItemDecoration);
        mOrganizationRclv.setAdapter(mOrganizationAdapter);

        if (mIsRootFragment) {
            mOrganizationRclv.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (null != mOrganizationSrfl) {
                        mOrganizationSrfl.setRefreshing(true);
                    }
                    onRefresh();
                }
            }, 100);
        } else {
            mOrganizationSrfl.setEnabled(false);
            List<CameraItem> cameras = mOrgItem.getCameras();
            mOrganizationAdapter.setNewData(cameras);
        }
        initListener();
    }

    private void initListener() {
        mOrganizationAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                CameraItem entity = (CameraItem) adapter.getItem(position);
                String org_id = entity.getCameraId();
                mIsItemClick = true;
                mItemName = entity.getName();
                mPresenter.getOrgCameras("1", org_id, "org", true);
            }
        });
    }

    @Override
    public void onRefresh() {
        mIsRefresh = true;
        if (mIsRootFragment) {
            mPresenter.getOrgCameras("1", "", "org", false);
        }
    }

    private void showNoNetwork() {
        mEmptyNoNetworkTv.setVisibility(View.VISIBLE);
        mOrganizationRclv.setVisibility(View.GONE);
    }

    private void showOrganization() {
        mEmptyNoNetworkTv.setVisibility(View.GONE);
        mOrganizationRclv.setVisibility(View.VISIBLE);
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
        mLoadingDialog.show();
    }

    @Override
    public void hideLoading() {
        mLoadingDialog.dismiss();
    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ArmsUtils.toastText(message);
        if (null != mOrganizationSrfl && mOrganizationSrfl.isRefreshing()) {
            mOrganizationSrfl.setRefreshing(false);
        }
        showNoNetwork();
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
    public Activity getFragmentActivity() {
        return this.getActivity();
    }

    @Override
    public void onOrgCameraSuccess(OrganizationEntity organizationEntity) {
        mOrganizationSrfl.setRefreshing(false);
        showOrganization();
        List<CameraItem> cameras = organizationEntity.getCameras();
        if (mIsRefresh) {
            if (null != cameras && !cameras.isEmpty()) {
                CameraItem camerasBean = cameras.get(0);
                mOrgName = camerasBean.getName();
                mPresenter.getOrgCameras("1", camerasBean.getCameraId(), "org", false);
            }
            mIsRefresh = false;
            mIsItemClick = false;
        } else if (!mIsItemClick) {
            //第二次调用，则开始显示所有的组织结构
            if (null != cameras && !cameras.isEmpty()) {
                mOrganizationAdapter.setNewData(cameras);
                if (mOrgItemClickListener != null) {
                    mOrgItemClickListener.onItemLoad(true, mOrgName, null != cameras && !cameras.isEmpty(), organizationEntity, mOrgName);
                }
            }
            mIsRefresh = false;
            mIsItemClick = false;
        } else {
            //item点击，进入到下一个页面
            mIsItemClick = false;
            if (mOrgItemClickListener != null) {
                mOrgItemClickListener.onItemLoad(false, mItemName, null != cameras && !cameras.isEmpty(), organizationEntity, mOrgName + "/" + mItemName);
            }
        }
    }

    @Override
    public void onOrgCameraError() {
        if (null != mOrganizationSrfl) {
            mOrganizationSrfl.setRefreshing(false);
        }
        showNoNetwork();
    }

    public interface OrgItemClickListener {
        void onItemLoad(boolean isRoot, String title, boolean isHasChildren, OrganizationEntity entity, String orgName);
    }

    private OrgItemClickListener mOrgItemClickListener;

    public void setOrgItemClickListener(OrgItemClickListener listener) {
        mOrgItemClickListener = listener;
    }
}
