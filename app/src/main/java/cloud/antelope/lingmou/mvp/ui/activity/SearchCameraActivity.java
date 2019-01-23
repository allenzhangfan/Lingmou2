package cloud.antelope.lingmou.mvp.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.modle.PermissionDialog;
import com.lingdanet.safeguard.common.utils.KeyboardUtils;
import com.lingdanet.safeguard.common.utils.LocationUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.SizeUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;

import org.litepal.crud.DataSupport;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.PermissionUtils;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.di.component.DaggerSearchCameraComponent;
import cloud.antelope.lingmou.di.module.SearchCameraModule;
import cloud.antelope.lingmou.mvp.contract.SearchCameraContract;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrganizationEntity;
import cloud.antelope.lingmou.mvp.model.entity.SearchTextEntity;
import cloud.antelope.lingmou.mvp.presenter.SearchCameraPresenter;
import cloud.antelope.lingmou.mvp.ui.adapter.OrgCameraAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.SearchCameraAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.SearchHistoryAdapter;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class SearchCameraActivity extends BaseActivity<SearchCameraPresenter>
        implements SearchCameraContract.View,
        PermissionUtils.HasPermission,
        EasyPermissions.PermissionCallbacks {

    private static final int HISTORY_PAGE_SIZE = 10;
    private static final int CAMERA_PAGE_SIZE = 500;

    @BindView(R.id.root)
    LinearLayout mRoot;
    @BindView(R.id.search_sv)
    SearchView mSearchView;
//    @BindView(R.id.latest_search_label)
//    TextView mLatestSearchLabel;
//    @BindView(R.id.clear_search_history_tv)
//    TextView mClearHistoryTv;
    @BindView(R.id.search_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.cancel_search_tv)
    TextView mCancelSearchTv;
//    @BindView(R.id.top_divider_view)
//    View mTopDividerView;
    AutoCompleteTextView mSearchText;
//    @BindView(R.id.clear_divide_top_line)
//    View mClearDivideTopLine;
//    @BindView(R.id.no_result_tv)
//    TextView mNoResultTv;
    //    @BindView(R.id.back_ib)
//    ImageButton mBackIb;
    @BindView(R.id.confirm_search_tv)
    TextView mConfirmSearchTv;
    @BindView(R.id.no_result_iv)
    ImageView mNoResultIv;

    @Inject
    SearchCameraAdapter mSearchCameraAdapter;
    @Inject
    OrgCameraAdapter mCameraAdapter;
    @Inject
    SearchHistoryAdapter mHistoryAdapter;
    @Inject
    @Named("HistoryItemDecoration")
    RecyclerView.ItemDecoration mHistoryDecoration;
    @Inject
    @Named("CameraItemDecoration")
    RecyclerView.ItemDecoration mResultDecoration;
    @Inject
    RecyclerView.LayoutManager mLayoutManager;
    @Inject
    RecyclerView.ItemAnimator mItemAnimator;
    @BindView(R.id.tv_choice)
    TextView tvChoice;
    @BindView(R.id.tv_size)
    TextView tvSize;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    @BindView(R.id.ll_bottom)
    LinearLayout llBottom;


    private boolean mHasAddHistoryDecoration;
    private boolean mHasAddResultDecoration;
    private OrgCameraEntity mClickCamera;
    private String mSearchStr;
    private boolean mIsDepotIn;
    private List<OrgCameraEntity> mSelectCameraItems;

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        DaggerSearchCameraComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .searchCameraModule(new SearchCameraModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_search; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mIsDepotIn = getIntent().getBooleanExtra("fromDepot", false);
        if (mIsDepotIn) {
//            mBackIb.setVisibility(View.VISIBLE);
//            mConfirmSearchTv.setVisibility(View.VISIBLE);
//            mCancelSearchTv.setVisibility(View.GONE);
        }
        mSelectCameraItems = new ArrayList<>();
        initSearchView();

        mRecyclerView.setLayoutManager(mLayoutManager);
        // mRecyclerView.setBackgroundColor(getResources().getColor(R.color.white));
        mRecyclerView.setItemAnimator(mItemAnimator);
        mRecyclerView.addItemDecoration(mHistoryDecoration);
        mHasAddHistoryDecoration = true;
        mRecyclerView.setNestedScrollingEnabled(false);

        // 第一次进入查询所有的历史记录
//        queryHistoryData("");

        initListener();
    }

    private void initSearchView() {
        int id = getResources().getIdentifier("android:id/search_plate", null, getPackageName());
        LinearLayout searchPlate = (LinearLayout) mSearchView.findViewById(id);
        if (null != searchPlate) {
            searchPlate.setBackgroundResource(R.drawable.search_text_bg);
            int searchTextId = getResources()
                    .getIdentifier("android:id/search_src_text", null, getPackageName());
            mSearchText = (AutoCompleteTextView) searchPlate.findViewById(searchTextId);
            mSearchText.setTextColor(getResources().getColor(R.color.default_text_color));
            mSearchText.setHintTextColor(getResources().getColor(R.color.edit_text_hint_color));
            mSearchText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            mSearchText.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mSearchText.getLayoutParams();
            params.height = SizeUtils.dp2px(24);
            mSearchText.setLayoutParams(params);
            int closeBtnId = getResources()
                    .getIdentifier("android:id/search_close_btn", null, getPackageName());
            ImageView mCloseButton = (ImageView) findViewById(closeBtnId);
            mCloseButton.setImageResource(R.drawable.search_clear);

            //光标颜色
            try {
                Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
                mCursorDrawableRes.setAccessible(true);
                mCursorDrawableRes.set(mSearchText, R.drawable.search_cursor);
            } catch (Exception e) {

            }
        }
        int magIconId = getResources().getIdentifier("android:id/search_mag_icon", null, getPackageName());
        ImageView mMagIcon = (ImageView) mSearchView.findViewById(magIconId);
        if (null != mMagIcon) {
            mMagIcon.setImageResource(R.drawable.search_icon);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mMagIcon.getLayoutParams();
            params.setMargins(0, 0, 0, 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                params.setMarginStart(0);
            }
            mMagIcon.setLayoutParams(params);
        }
    }

    private void queryHistoryData(String s) {
        mRecyclerView.setAdapter(null);
        removeDecoration();
        List<SearchTextEntity> histories = DataSupport.where("name like ?", "%" + s + "%")
                .order("id desc").limit(HISTORY_PAGE_SIZE).find(SearchTextEntity.class);
        if (histories.size() > 0) {
            mRecyclerView.addItemDecoration(mHistoryDecoration);
            mHasAddHistoryDecoration = true;
            showClearItem();
            mHistoryAdapter.setNewData(histories);
            mRecyclerView.setAdapter(mHistoryAdapter);
            mNoResultIv.setVisibility(View.GONE);
//            mNoResultTv.setVisibility(View.GONE);
        } else {
            dismissClearItem();
            dismissTopLabel();
            mNoResultIv.setVisibility(View.INVISIBLE);
//            mNoResultTv.setVisibility(View.VISIBLE);
//            mNoResultTv.setText(R.string.no_history_search);
        }
    }

    private void initListener() {
        // 设置搜索文本监听
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                KeyboardUtils.hideSoftInput(SearchCameraActivity.this);
                // 按完搜索键后将当前查询的关键字保存起来
                insertData(query);
                dismissTopLabel();
                mSearchStr = query;
                queryCollectionData(query);
                return false;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String s) {
//                mNoResultTv.setVisibility(View.GONE);
                mNoResultIv.setVisibility(View.GONE);
                if (TextUtils.isEmpty(s)) {
                    showTopLabel();
                } else {
                    dismissTopLabel();
                }
                mSelectCameraItems.clear();
                showClearItem();
                // 根据tempName去模糊查询数据库中有没有数据
//                queryHistoryData(s);
                return false;
            }
        });
        mHistoryAdapter.setOnItemClickListener((adapter, view, position) -> {
            SearchTextEntity item = (SearchTextEntity) adapter.getItem(position);
            mSearchText.setText(item.getName());
            mSearchText.setSelection(item.getName().length());
            mSearchStr = item.getName();
            queryCollectionData(item.getName());
            dismissTopLabel();
        });
        mCameraAdapter.setOnItemClickListener((adapter, view, position) -> {
            // CameraNewEntity cameraNewEntity = new CameraNewEntity();
            // cameraNewEntity.cameraId = cameraItem.getCameraId();
            // cameraNewEntity.cameraAlias = cameraItem.getName();
            // cameraNewEntity.status = cameraItem.getCamera_status();
            boolean hasVideoLive = SPUtils.getInstance().getBoolean(Constants.PERMISSION_HAS_VIDEO_LIVE, false);
            if (hasVideoLive) {
                OrgCameraEntity cameraItem = (OrgCameraEntity) adapter.getItem(position);
                /*Intent intent = new Intent(SearchCameraActivity.this, PlayerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", cameraItem);
                intent.putExtras(bundle);
                startActivity(intent);*/
                Intent intent = new Intent(this, VideoPlayActivity.class);
                intent.putExtra("cameraId", cameraItem.getManufacturerDeviceId());
                intent.putExtra("cameraName", cameraItem.getDeviceName());
                intent.putExtra("cameraSn", cameraItem.getSn());
                startActivity(intent);
            } else {
                ToastUtils.showShort(R.string.hint_no_permission);
            }

        });
        mCameraAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            mClickCamera = (OrgCameraEntity) adapter.getItem(position);
            checkLocationPerm();
            if (!LocationUtils.isGpsEnabled()) {
                LocationUtils.openGpsSettings();
            }
        });

        mSearchCameraAdapter.setOnItemClickListener((adapter, view, position) -> {
            OrgCameraEntity cameraItem = (OrgCameraEntity) adapter.getItem(position);
            if (mSelectCameraItems.contains(cameraItem)) {
                mSelectCameraItems.remove(cameraItem);
                cameraItem.setSelect(false);
            } else {
                mSelectCameraItems.add(cameraItem);
                cameraItem.setSelect(true);
            }
            mSearchCameraAdapter.notifyItemChanged(position);
            notifySelectCount();
        });
    }

    private void notifySelectCount() {
        List<OrgCameraEntity> data = mSearchCameraAdapter.getData();
        int count = 0;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).isSelect()) {
                count++;
            }
        }
        llBottom.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
        tvSize.setText(String.format("已选择%d张", count));
        tvChoice.setText(count == data.size() ? "取消选择" : "全选");
    }

    private void queryCollectionData(String s) {
        // mPresenter.searchCamera(s, "camera", String.valueOf(CAMERA_PAGE_SIZE), String.valueOf(0), null);
        //通过关键字搜索摄像机
        Cursor cursor = DataSupport.findBySQL("select * from orgcameraentity where deviceName like '%" + s + "%'");
        if (cursor != null) {
            List<OrgCameraEntity> orgCameraList = new ArrayList<>();
            while (cursor.moveToNext()) {
                OrgCameraEntity entity = new OrgCameraEntity();
                String deviceName = cursor.getString(cursor.getColumnIndex("devicename"));
                Long deviceType = cursor.getLong(cursor.getColumnIndex("devicetype"));
                String sn = cursor.getString(cursor.getColumnIndex("sn"));
                Long manufacturerDeviceId = cursor.getLong(cursor.getColumnIndex("manufacturerdeviceid"));
                Long deviceState = cursor.getLong(cursor.getColumnIndex("devicestate"));
                Double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
                Double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));

                entity.setDeviceName(deviceName);
                entity.setDeviceType(deviceType);
                entity.setSn(sn);
                entity.setManufacturerDeviceId(manufacturerDeviceId);
                entity.setDeviceState(deviceState);
                entity.setLongitude(longitude);
                entity.setLatitude(latitude);
                orgCameraList.add(entity);
            }
            mRecyclerView.setAdapter(null);
            removeDecoration();
            if (orgCameraList != null && !orgCameraList.isEmpty()) {
                mRecyclerView.addItemDecoration(mResultDecoration);
                mHasAddResultDecoration = true;
//                mNoResultTv.setVisibility(View.GONE);
                mNoResultIv.setVisibility(View.GONE);
                if (!mIsDepotIn) {
                    mCameraAdapter.setNewData(orgCameraList);
                    mRecyclerView.setAdapter(mCameraAdapter);
                    mCameraAdapter.setSelectText(mSearchStr);
                } else {
                    //从图库进来的，则显示我们自己的逻辑
                    mSearchCameraAdapter.setNewData(orgCameraList);
                    mRecyclerView.setAdapter(mSearchCameraAdapter);
                    mSearchCameraAdapter.setSelectText(mSearchStr);
                }
            } else {
//                mNoResultTv.setVisibility(View.VISIBLE);
                mNoResultIv.setVisibility(View.VISIBLE);
//                mNoResultTv.setText(R.string.no_search_result);
            }
            dismissClearItem();
            mSearchView.clearFocus(); // 不获取焦点
        }
    }

    /**
     * 插入数据
     */
    private void insertData(String tempName) {
        List<SearchTextEntity> histories = DataSupport.where("name = ?", tempName).find(SearchTextEntity.class);
        if (histories.size() > 0) {
            SearchTextEntity oldOne = histories.get(0);
            oldOne.setSearchTimes(oldOne.getSearchTimes() + 1);
            oldOne.save();
        } else {
            SearchTextEntity newOne = new SearchTextEntity();
            newOne.setSearchTimes(1);
            newOne.setName(tempName);
            newOne.save();
        }
    }

    @OnClick({R.id.cancel_search_tv, R.id.clear_search_history_tv, /*R.id.back_ib,*/ R.id.confirm_search_tv, R.id.tv_choice, R.id.tv_confirm})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.clear_search_history_tv:
//                deleteData();
//                queryHistoryData("");
                break;
            case R.id.cancel_search_tv:
                KeyboardUtils.hideSoftInput(SearchCameraActivity.this);
                onBackPressedSupport();
                break;
//            case R.id.back_ib:
//                KeyboardUtils.hideSoftInput(SearchCameraActivity.this);
//                finish();
//                break;
            case R.id.tv_confirm:
            case R.id.confirm_search_tv:
                //获取已经选择的Item
                StringBuilder stringBuilder = new StringBuilder();
                for (OrgCameraEntity cameraItem : mSelectCameraItems) {
                    stringBuilder.append(cameraItem.getManufacturerDeviceId() + ",");
                }
                String cameraNos = stringBuilder.toString();
                if (!TextUtils.isEmpty(cameraNos)) {
                    cameraNos = cameraNos.substring(0, cameraNos.length() - 1);
                }
                Intent intent = new Intent();
                intent.putExtra("cameraIds", cameraNos);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.tv_choice:
                if (TextUtils.equals(getString(R.string.select_all), tvChoice.getText().toString())) {
                    mSelectCameraItems.clear();
                    for (OrgCameraEntity entity : mSearchCameraAdapter.getData()) {
                        entity.setSelect(true);
                        mSelectCameraItems.add(entity);
                    }
                } else {
                    mSelectCameraItems.clear();
                    for (OrgCameraEntity entity : mSearchCameraAdapter.getData()) {
                        entity.setSelect(false);
                    }
                }
                mSearchCameraAdapter.notifyDataSetChanged();
                notifySelectCount();
                break;

        }
    }

    /**
     * 移除RecyclerView的ItemDecoration
     */
    private void removeDecoration() {
        if (mHasAddHistoryDecoration) {
            mRecyclerView.removeItemDecoration(mHistoryDecoration);
            mHasAddHistoryDecoration = false;
        }
        if (mHasAddResultDecoration) {
            mRecyclerView.removeItemDecoration(mResultDecoration);
            mHasAddResultDecoration = false;
        }
    }

    /**
     * 清空数据
     */
    private void deleteData() {
        DataSupport.deleteAll(SearchTextEntity.class);
    }

    private void dismissTopLabel() {
//        mTopDividerView.setVisibility(View.GONE);
//        mLatestSearchLabel.setVisibility(View.GONE);
    }

    private void showTopLabel() {
//        mTopDividerView.setVisibility(View.VISIBLE);
//        mLatestSearchLabel.setVisibility(View.VISIBLE);
    }

    private void dismissClearItem() {
//        mClearDivideTopLine.setVisibility(View.GONE);
//        mClearHistoryTv.setVisibility(View.GONE);
    }

    private void showClearItem() {
//        mClearDivideTopLine.setVisibility(View.VISIBLE);
//        mClearHistoryTv.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSearchCameraSuccess(OrganizationEntity entity) {

    }

    @Override
    public void onSearchCameraError() {
        mSearchView.clearFocus(); // 不获取焦点
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
        ArmsUtils.toastText(message);
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

    @AfterPermissionGranted(PermissionUtils.RC_LOCATION_PERM)
    public void checkLocationPerm() {
        PermissionUtils.locationTask(this);
    }

    @Override
    public void doNext(int permId) {
        if (PermissionUtils.RC_LOCATION_PERM == permId) {
            Intent intent = new Intent(getActivity(), CameraMapActivity.class);
            intent.putExtra(Constants.SELECT_CAMERA, mClickCamera);
            startActivity(intent);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            if (perms.contains(Manifest.permission.ACCESS_FINE_LOCATION)) {
                final PermissionDialog dialog = new PermissionDialog(getActivity());
                dialog.setTitle(getString(R.string.request_permission_tag));
                dialog.setMessage(getString(R.string.need_location_permission_tips));
                dialog.setNegativeListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        ToastUtils.showShort(R.string.can_not_get_location);
                    }
                });
                dialog.show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public Activity getActivity() {
        return this;
    }

}
