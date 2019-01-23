package cloud.antelope.lingmou.mvp.ui.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.TimeUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.lingdanet.safeguard.common.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.di.component.DaggerClothControlAlarmComponent;
import cloud.antelope.lingmou.di.module.ClothControlAlarmModule;
import cloud.antelope.lingmou.mvp.contract.ClothControlAlarmContract;
import cloud.antelope.lingmou.mvp.model.entity.AlarmDepotEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceAlarmBlackEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceAlarmDetailBean;
import cloud.antelope.lingmou.mvp.model.entity.FaceAlarmEntity;
import cloud.antelope.lingmou.mvp.model.entity.ListBaseBlackLibEntity;
import cloud.antelope.lingmou.mvp.model.entity.ListBaseEntity;
import cloud.antelope.lingmou.mvp.presenter.ClothControlAlarmPresenter;
import cloud.antelope.lingmou.mvp.ui.adapter.DepotAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.FaceAlarmAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.LmDatePickDialog;
import cloud.antelope.lingmou.mvp.ui.widget.LmTimePickerDialog;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class ClothControlAlarmActivity extends BaseActivity<ClothControlAlarmPresenter>
        implements ClothControlAlarmContract.View,
        RadioGroup.OnCheckedChangeListener,
        SwipeRefreshLayout.OnRefreshListener,
        BaseQuickAdapter.RequestLoadMoreListener, View.OnClickListener {

    private static final int TYPE_INTO_FACES = 0x01;
    private static final int TYPE_SELECT_CAMERA = 0x02;

    @BindView(R.id.back_ib)
    ImageButton mBackIb;
    @BindView(R.id.filter_ib)
    ImageButton mFilterIb;
    @BindView(R.id.cloth_rclv)
    RecyclerView mClothRclv;
    @BindView(R.id.cloth_swiperl)
    SwipeRefreshLayout mClothSwiperl;
    @BindView(R.id.title_rl)
    RelativeLayout mTitleRl;
    @BindView(R.id.title_split_view)
    View mTitleSplitView;
    @BindView(R.id.empty_tv)
    TextView mEmptyTv;
    @BindView(R.id.empty_no_network_tv)
    TextView mEmptyNoNetworkTv;
    @BindView(R.id.search_ib)
    ImageButton mSearchIb;
    @BindView(R.id.no_permission_rl)
    RelativeLayout mNoPermissionRl;

    @Inject
    FaceAlarmAdapter mFaceAlarmAdapter;
    @Inject
    @Named("LinearManager")
    RecyclerView.LayoutManager mLayoutManager;
    @Inject
    RecyclerView.ItemAnimator mItemAnimator;
    @Inject
    LoadMoreView mLoadMoreView;
    @Inject
    RecyclerView.ItemDecoration mGridItemDecoration;
    @Inject
    @Named("GridManager")
    RecyclerView.LayoutManager mGridLayoutManager;
    @Inject
    DepotAdapter mDepotAdapter;


    // private PopupWindow mPopupWindow;
    private RelativeLayout mPopRelaView;
    private RecyclerView mDepotRecyView;
    private RadioGroup mAlarmNewRg;
    private LmDatePickDialog mStartDateDialog;
    private LmDatePickDialog mEndDateDialog;
    private LmTimePickerDialog mStartTimeDialog, mEndTimeDialog;
    private LinearLayout mStartTimeLl, mEndTimeLl;
    private TextView mStartTimeTv, mEndTimeTv;
    private TextView mCleanTv, mConfirmTv;

    private int mStartYear, mStartMonth, mStartDay, mStartHour, mStartMinute;
    private int mEndYear, mEndMonth, mEndDay, mEndHour, mEndMinute;
    private Long mEndTimeStamp = null;
    private Long mStartTimeStamp = null;
    private Long mTempStartTimeStamp;
    private Long mTempEndTimeStamp;
    private String mEffective = "2";
    private String mTempEffective = "2";
    private int mFrom = 1;
    private static int SIZE = 10;
    private List<AlarmDepotEntity> mAlarmDepotEntities;
    private List<AlarmDepotEntity> mSelectEntities;
    private List<AlarmDepotEntity> mTempSelectEntities;
    private AlarmDepotEntity mBaseDepotEntity;
    private String mLibIds;
    private String mCameraIds;
    private RadioGroup mAreaRg;
    private boolean mIsEnterEndTime;
    private String mGeoAddress;
    private FaceAlarmBlackEntity mClickAlarmDetailBean;
    private int mClickPosition;
    private boolean mHasDetailPermission;
    private static final long SEVEN_DAY_STAMP = 7 * 24 * 60 * 60 * 1000;

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        DaggerClothControlAlarmComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .clothControlAlarmModule(new ClothControlAlarmModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_cloth_control_alarm; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mPopRelaView = findViewById(R.id.cloth_popup);

        mAlarmNewRg = mPopRelaView.findViewById(R.id.alarm_new_rg);
        mDepotRecyView = mPopRelaView.findViewById(R.id.depot_rclv);
        mStartTimeLl = mPopRelaView.findViewById(R.id.start_time_ll);
        mEndTimeLl = mPopRelaView.findViewById(R.id.end_time_ll);
        mStartTimeTv = mPopRelaView.findViewById(R.id.start_time_tv);
        mEndTimeTv = mPopRelaView.findViewById(R.id.end_time_tv);
        mCleanTv = mPopRelaView.findViewById(R.id.clean_tv);
        mConfirmTv = mPopRelaView.findViewById(R.id.confirm_tv);
        mAreaRg = mPopRelaView.findViewById(R.id.area_rg);

        mDepotRecyView.setNestedScrollingEnabled(false);
        mDepotRecyView.setLayoutManager(mGridLayoutManager);
        mDepotRecyView.addItemDecoration(mGridItemDecoration);
        mDepotRecyView.setAdapter(mDepotAdapter);
        mAlarmNewRg.setOnCheckedChangeListener(this);
        mAreaRg.setOnCheckedChangeListener(this);
        mStartTimeLl.setOnClickListener(this);
        mEndTimeLl.setOnClickListener(this);
        mCleanTv.setOnClickListener(this);
        mConfirmTv.setOnClickListener(this);

        mClothSwiperl.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        mClothSwiperl.setOnRefreshListener(this);
        mClothRclv.setHasFixedSize(false);
        mClothRclv.setLayoutManager(mLayoutManager);
        mClothRclv.setItemAnimator(mItemAnimator);
        mFaceAlarmAdapter.setOnLoadMoreListener(this, mClothRclv);
        mFaceAlarmAdapter.setLoadMoreView(mLoadMoreView);
        mClothRclv.setAdapter(mFaceAlarmAdapter);

        mClothRclv.postDelayed(() -> {
            mClothSwiperl.setRefreshing(true);
            onRefresh();
        }, 100);


        mEndTimeStamp = System.currentTimeMillis();
        mTempEndTimeStamp = mEndTimeStamp;
        mEndTimeTv.setText(TimeUtils.millis2String(mEndTimeStamp, "yyyy.MM.dd HH:mm"));

        mStartTimeStamp = System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000 + 1000;
        mTempStartTimeStamp = mStartTimeStamp;
        mEndTimeTv.setText(TimeUtils.millis2String(mEndTimeStamp, "yyyy.MM.dd HH:mm"));

        mStartDateDialog = new LmDatePickDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mStartYear = year;
                mStartMonth = month + 1;
                mStartDay = dayOfMonth;
                initStartTimeDialog();
            }

        }, Calendar.getInstance(), mStartYear == 0 ? Calendar.getInstance().get(Calendar.YEAR) : mStartYear,
                mStartMonth == 0 ? Calendar.getInstance().get(Calendar.MONTH) : mStartMonth,
                mStartDay == 0 ? Calendar.getInstance().get(Calendar.DAY_OF_MONTH) : mStartDay);

        mEndDateDialog = new LmDatePickDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                mEndYear = year;
                mEndMonth = month + 1;
                mEndDay = dayOfMonth;
                initEndTimeDialog();
            }
        }, Calendar.getInstance(), mEndYear == 0 ? Calendar.getInstance().get(Calendar.YEAR) : mEndYear,
                mEndMonth == 0 ? Calendar.getInstance().get(Calendar.MONTH) : mEndMonth,
                mEndDay == 0 ? Calendar.getInstance().get(Calendar.DAY_OF_MONTH) : mEndDay);
        mStartDateDialog.getDatePicker().setMaxDate(System.currentTimeMillis() + (1000 * 60 * 60));
        mEndDateDialog.getDatePicker().setMaxDate(System.currentTimeMillis() + (1000 * 60 * 60));

        mBaseDepotEntity = new AlarmDepotEntity();
        mBaseDepotEntity.setId("-1");
        mBaseDepotEntity.setName("全部\n布控库");
        mAlarmDepotEntities = new ArrayList<>();
        mSelectEntities = new ArrayList<>();
        mTempSelectEntities = new ArrayList<>();
        mSelectEntities.add(mBaseDepotEntity);
        mTempSelectEntities.add(mBaseDepotEntity);
        mAlarmDepotEntities.add(mBaseDepotEntity);
        mDepotAdapter.setNewData(mAlarmDepotEntities);
        mDepotAdapter.setSelectDepots(mSelectEntities);
        mPresenter.getDepots();

        mHasDetailPermission = SPUtils.getInstance().getBoolean(Constants.PERMISSION_HAS_ALARM_DETAIL, false);
        initListener();
    }

    @Override
    public void onBackPressedSupport() {
        if (mPopRelaView.getVisibility() == View.VISIBLE) {
            setPopDismiss();
        } else {
            super.onBackPressedSupport();
        }
    }

    private void setPopDismiss() {
        mPopRelaView.setVisibility(View.GONE);
        mFilterIb.setImageResource(R.drawable.cloth_control_filter);
    }

    private void initListener() {

        mDepotAdapter.setOnItemClickListener((adapter, view, position) -> {
            AlarmDepotEntity depotEntity = (AlarmDepotEntity) adapter.getItem(position);
            if (mTempSelectEntities.contains(mBaseDepotEntity) && "-1".equals(depotEntity.getId())) {
                //证明只有一个，就是base，则不动
                return;
            } else {
                //则标明获取到了布控库
                String id = depotEntity.getId();
                if ("-1".equals(id)) {
                    //标明变成了选第一个，全选
                    mTempSelectEntities.clear();
                    mTempSelectEntities.add(mBaseDepotEntity);
                    mDepotAdapter.setSelectDepots(mTempSelectEntities);
                } else if (mTempSelectEntities.contains(depotEntity)) {
                    //已选中，则清除
                    mTempSelectEntities.remove(depotEntity);
                    if (mTempSelectEntities.isEmpty()) {
                        mTempSelectEntities.add(mBaseDepotEntity);
                    }
                    mDepotAdapter.setSelectDepots(mTempSelectEntities);
                } else {
                    if (mTempSelectEntities.contains(mBaseDepotEntity)) {
                        mTempSelectEntities.remove(mBaseDepotEntity);
                    }
                    mTempSelectEntities.add(depotEntity);
                    mDepotAdapter.setSelectDepots(mTempSelectEntities);
                }
            }
        });

        mFaceAlarmAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (mHasDetailPermission) {
                    FaceAlarmBlackEntity item = (FaceAlarmBlackEntity) adapter.getItem(position);
                    Intent intent = new Intent(Utils.getContext(), FaceAlarmDetailActivity.class);
                    intent.putExtra("bean", item);
                    intent.putExtra("position", position);
                    startActivityForResult(intent, TYPE_INTO_FACES);
                } else {
                    getDetailNoPermission();
                }
                // mClickPosition = position;
                // mPresenter.getDetailPermission(mClickAlarmDetailBean.getId());
            }
        });
    }

    private void initEndTimeDialog() {
        mEndTimeDialog = new LmTimePickerDialog(this, 0, (view, hourOfDay, minute) -> {
            mEndHour = hourOfDay;
            mEndMinute = minute;
            Long entTimeStamp = TimeUtils.string2Millis(mEndYear + "-" + mEndMonth + "-" + mEndDay + " " + mEndHour + ":" + mEndMinute, new SimpleDateFormat("yyyy-MM-dd HH:mm"));
            String timeStr = TimeUtils.millis2String(entTimeStamp, "yyyy.MM.dd HH:mm");
            //                mClothEndTimeTv.setText(mEndYear + "." + mEndMonth + "." + mEndDay + " " + mEndHour + ":" + mEndMinute);
            if (mTempStartTimeStamp != null && (entTimeStamp - mTempStartTimeStamp > SEVEN_DAY_STAMP)) {
                ToastUtils.showShort(R.string.hint_not_more_seven_days);
            } else {
                mTempEndTimeStamp = entTimeStamp;
                mEndTimeTv.setText(timeStr);
                mEndDateDialog.dismiss();

            }
        }, mEndHour == 0 ? Calendar.getInstance().get(Calendar.HOUR_OF_DAY) : mEndHour,
                mEndMinute == 0 ? Calendar.getInstance().get(Calendar.MINUTE) : mEndMinute, true);
        mEndTimeDialog.show();
    }


    private void initStartTimeDialog() {
        mStartTimeDialog = new LmTimePickerDialog(this, 0, (view, hourOfDay, minute) -> {
            mStartHour = hourOfDay;
            mStartMinute = minute;
            Long startTime = TimeUtils.string2Millis(mStartYear + "-" + mStartMonth + "-" + mStartDay + " " + mStartHour + ":" + mStartMinute, new SimpleDateFormat("yyyy-MM-dd HH:mm"));
            String timeStr = TimeUtils.millis2String(startTime, "yyyy.MM.dd HH:mm");
            //                mClothStartTimeTv.setText(mStartYear + "." + mStartMonth + "." + mStartDay + " " + mStartHour + ":" + mStartMinute);
            if (mTempEndTimeStamp != null && (mTempEndTimeStamp - startTime) > SEVEN_DAY_STAMP) {
                ToastUtils.showShort(R.string.hint_not_more_seven_days);
            } else {
                mTempStartTimeStamp = startTime;
                mStartTimeTv.setText(timeStr);
                mStartDateDialog.dismiss();
            }
        }, mStartHour == 0 ? Calendar.getInstance().get(Calendar.HOUR_OF_DAY) : mStartHour,
                mStartMinute == 0 ? Calendar.getInstance().get(Calendar.MINUTE) : mStartMinute, true);
        mStartTimeDialog.show();
    }


    /**
     * 恢复数据到取消前
     */
    private void restoreData() {
        if (mStartTimeStamp != null) {
            //开始时间不为空，则表示已选择，恢复到取消前
            String startTimeStamp = TimeUtils.millis2String(mStartTimeStamp, "yyyy-MM-dd");
            String startTimeStr = TimeUtils.millis2String(mStartTimeStamp, "yyyy-MM-dd HH:mm");
            if (!TextUtils.isEmpty(startTimeStamp)) {
                String[] split = startTimeStamp.split("-");
                mStartDateDialog.getDatePicker().updateDate(Integer.parseInt(split[0]), Integer.parseInt(split[1]) - 1, Integer.parseInt(split[2]));
            }
            mStartTimeTv.setText(startTimeStr);
        } else {
            mStartTimeTv.setText("请选择开始时间");
        }
        if (mEndTimeStamp != null) {
            //开始时间不为空，则表示已选择，恢复到取消前
            if (!mIsEnterEndTime) {
                mEndTimeStamp = System.currentTimeMillis();
                mTempEndTimeStamp = mEndTimeStamp;
                mEndTimeTv.setText(TimeUtils.millis2String(mEndTimeStamp, "yyyy.MM.dd HH:mm"));
            }
            String endTimeStamp = TimeUtils.millis2String(mEndTimeStamp, "yyyy-MM-dd");
            String endTimeStr = TimeUtils.millis2String(mEndTimeStamp, "yyyy-MM-dd HH:mm");
            if (!TextUtils.isEmpty(endTimeStamp)) {
                String[] split = endTimeStamp.split("-");
                mEndDateDialog.getDatePicker().updateDate(Integer.parseInt(split[0]), Integer.parseInt(split[1]) - 1, Integer.parseInt(split[2]));
            }
            mEndTimeTv.setText(endTimeStr);
        } else {
            mEndTimeTv.setText("请选择结束时间");
        }

        if (TextUtils.isEmpty(mEffective)) {
            mAlarmNewRg.check(R.id.alarm_new_none_rb);
        } else if ("2".equals(mEffective)) {
            mAlarmNewRg.check(R.id.alarm_new_undo_rb);
        } else if ("3".equals(mEffective)) {
            mAlarmNewRg.check(R.id.alarm_new_valid_rb);
        } else if ("4".equals(mEffective)) {
            mAlarmNewRg.check(R.id.alarm_new_invalid_rb);
        }
        mDepotAdapter.setSelectDepots(mSelectEntities);

    }

    private void getNewData() {
        //获取新的数据
        mFrom = 1;
        mFaceAlarmAdapter.setEnableLoadMore(false);

        mPresenter.getFaceAlarm(mStartTimeStamp, mEndTimeStamp, mFrom, mEffective, SIZE, mLibIds, mCameraIds, mGeoAddress);
    }

    @Override
    public void showLoading(String message) {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(@NonNull String message) {
        ToastUtils.showShort(message);
        if (null != mClothSwiperl && mClothSwiperl.isRefreshing()) {
            mClothSwiperl.setRefreshing(false);
        }
        if (mFrom == 1) {
            if (mFaceAlarmAdapter.getData().isEmpty()) {
                mEmptyTv.setVisibility(View.GONE);
                mEmptyNoNetworkTv.setVisibility(View.VISIBLE);
            }
            mFaceAlarmAdapter.setEnableLoadMore(false);
            mClothSwiperl.setEnabled(true);
        } else {
            mClothSwiperl.setEnabled(true);
            mFaceAlarmAdapter.loadMoreFail();
            mFaceAlarmAdapter.setEnableLoadMore(true);
            mFrom--;
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


    @Override
    public void onRefresh() {
        if (!mIsEnterEndTime) {
            mEndTimeStamp = System.currentTimeMillis();
            mTempEndTimeStamp = mEndTimeStamp;
            mEndTimeTv.setText(TimeUtils.millis2String(mEndTimeStamp, "yyyy.MM.dd HH:mm"));
        }
        getNewData();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.alarm_new_none_rb:
                //全部
                mTempEffective = null;
                break;
            case R.id.alarm_new_undo_rb:
                //未处理
                mTempEffective = "2";
                break;
            case R.id.alarm_new_valid_rb:
                //有效
                mTempEffective = "3";
                break;
            case R.id.alarm_new_invalid_rb:
                //无效
                mTempEffective = "4";
                break;
            case R.id.area_none_rb:
                //抓拍地点
                // mTempIsOnlyAttention = "0";
                break;
            case R.id.area_collect_rb:
                // mTempIsOnlyAttention = "1";
                break;
        }
    }

    @Override
    public void onLoadMoreRequested() {
        mFrom++;
        mClothSwiperl.setEnabled(false);
        mPresenter.getFaceAlarm(mStartTimeStamp, mEndTimeStamp, mFrom, mEffective, SIZE, mLibIds, mCameraIds, null);
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void getAlarmSuccess(ListBaseEntity<List<FaceAlarmBlackEntity>> faceAlarmEntity) {
        mClothSwiperl.setRefreshing(false);
        mFaceAlarmAdapter.setEnableLoadMore(true);
        if (null == faceAlarmEntity) {
            mClothSwiperl.setEnabled(true);
            if (mFrom == 1) {
                showNoDataView();
            } else {
                mFaceAlarmAdapter.loadMoreEnd(false);
            }
            return;
        }
        List<FaceAlarmBlackEntity> list = faceAlarmEntity.getData();
        if (null != list) {
            if (mFrom == 1) {
                if (list.size() == 0) {
                    showNoDataView();
                } else {
                    showDataView();
                }
                mFaceAlarmAdapter.setNewData(list);
                if (list.size() < SIZE) {
                    mFaceAlarmAdapter.disableLoadMoreIfNotFullPage(mClothRclv);
                    //                    mFaceAlarmAdapter.loadMoreEnd(false);
                }
                /*if (list.size() != 0 ) {
                    mClothRclv.smoothScrollToPosition(0);
                }*/

            } else {
                mClothSwiperl.setEnabled(true);
                mFaceAlarmAdapter.addData(list);
                if (list.size() < SIZE) {
                    //                    mFaceAlarmAdapter.disableLoadMoreIfNotFullPage(mClothRclv);
                    mFaceAlarmAdapter.loadMoreEnd(false);
                } else {
                    mFaceAlarmAdapter.loadMoreComplete();
                }
            }

        } else {
            mClothSwiperl.setEnabled(true);
            if (mFrom == 1) {
                showNoDataView();
            } else {
                mFaceAlarmAdapter.loadMoreEnd(false);
            }
        }
    }

    private void showDataView() {
        mNoPermissionRl.setVisibility(View.GONE);
        mEmptyNoNetworkTv.setVisibility(View.GONE);
        mEmptyTv.setVisibility(View.GONE);
        mDepotRecyView.setVisibility(View.VISIBLE);
    }

    private void showNoDataView() {
        mNoPermissionRl.setVisibility(View.GONE);
        mEmptyNoNetworkTv.setVisibility(View.GONE);
        mEmptyTv.setVisibility(View.VISIBLE);
        mDepotRecyView.setVisibility(View.VISIBLE);
    }

    private void showNoNetworkView() {
        mNoPermissionRl.setVisibility(View.GONE);
        mEmptyNoNetworkTv.setVisibility(View.VISIBLE);
        mEmptyTv.setVisibility(View.GONE);
        mDepotRecyView.setVisibility(View.GONE);
    }

    private void showNoPermissonView() {
        mNoPermissionRl.setVisibility(View.VISIBLE);
        mEmptyNoNetworkTv.setVisibility(View.GONE);
        mEmptyTv.setVisibility(View.GONE);
        mDepotRecyView.setVisibility(View.GONE);
    }

    @Override
    public void getAlarmError() {
        mClothSwiperl.setRefreshing(false);
        if (mFrom == 1) {
            if (mFaceAlarmAdapter.getData().isEmpty()) {
                showNoNetworkView();
            }
            mFaceAlarmAdapter.setEnableLoadMore(false);
            mClothSwiperl.setEnabled(true);
        } else {
            mClothSwiperl.setEnabled(true);
            mFaceAlarmAdapter.loadMoreFail();
            mFaceAlarmAdapter.setEnableLoadMore(true);
            mFrom--;
        }
    }

    @Override
    public void getDepots(ListBaseBlackLibEntity<List<AlarmDepotEntity>> list) {
        //获取了所有的布控库
        mAlarmDepotEntities.clear();
        if (null != list) {
            mAlarmDepotEntities.addAll(list.getResultList());
        }
        mAlarmDepotEntities.add(0, mBaseDepotEntity);
        mDepotAdapter.setNewData(mAlarmDepotEntities);
        mDepotAdapter.setSelectDepots(mSelectEntities);
    }

    @Override
    public void getNoPermission() {
        mClothSwiperl.setRefreshing(false);
        mClothSwiperl.setEnabled(true);
        showNoPermissonView();
    }

    @Override
    public void getDetailNoPermission() {
        ToastUtils.showShort(R.string.hint_no_permission);
    }

    @Override
    public void getDetailSuccess() {
        Intent intent = new Intent(Utils.getContext(), FaceAlarmDetailActivity.class);
        intent.putExtra("bean", mClickAlarmDetailBean);
        intent.putExtra("position", mClickPosition);
        startActivityForResult(intent, TYPE_INTO_FACES);
    }

    @OnClick({R.id.back_ib, R.id.filter_ib, R.id.search_ib})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_ib:
                if (mPopRelaView.getVisibility() == View.VISIBLE) {
                    setPopDismiss();
                } else {
                    finish();
                }
                break;
            case R.id.filter_ib:
                //显示过滤条件popWindow
                if (mPopRelaView.getVisibility() == View.VISIBLE) {
                    setPopDismiss();
                } else {
                    restoreData();
                    mPopRelaView.setVisibility(View.VISIBLE);
                    mFilterIb.setImageResource(R.drawable.cloth_control_filter_click);
                }
                break;
            case R.id.search_ib:
                if (mPopRelaView.getVisibility() == View.VISIBLE) {
                    setPopDismiss();
                }
                //进入搜索界面
                Intent intent = new Intent(ClothControlAlarmActivity.this, SearchCameraActivity.class);
                intent.putExtra("fromDepot", true);
                startActivityForResult(intent, TYPE_SELECT_CAMERA);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_time_ll:
                //点击开始时间按钮
                mStartDateDialog.show();
                break;
            case R.id.end_time_ll:
                //点击结束时间按钮
                mEndDateDialog.show();
                break;
            case R.id.clean_tv:
                //重置
                setPopDismiss();
                mTempEffective = null;
                mEffective = null;
                mGeoAddress = null;
                // mTempStartTimeStamp = null;
                // mStartTimeStamp = null;

                mStartTimeStamp = System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000 + 1000;
                mTempStartTimeStamp = mStartTimeStamp;
                // mEndTimeTv.setText(TimeUtils.millis2String(mEndTimeStamp, "yyyy.MM.dd HH:mm"));

                mEndTimeStamp = System.currentTimeMillis();
                mTempEndTimeStamp = mEndTimeStamp;
                mTempSelectEntities.clear();
                mSelectEntities.clear();
                mTempSelectEntities.add(mBaseDepotEntity);
                mSelectEntities.add(mBaseDepotEntity);
                mClothSwiperl.setRefreshing(true);
                mLibIds = null;
                mCameraIds = null;
                mIsEnterEndTime = false;
                getNewData();
                break;
            case R.id.confirm_tv:
                //确定
                if (mTempStartTimeStamp != null && mTempEndTimeStamp != null && mTempStartTimeStamp >= mTempEndTimeStamp) {
                    ToastUtils.showShort("开始时间必须小于结束时间");
                    return;
                }
                setPopDismiss();
                mStartTimeStamp = mTempStartTimeStamp;
                mEndTimeStamp = mTempEndTimeStamp;
                mEffective = mTempEffective;
                mClothSwiperl.setRefreshing(true);
                mSelectEntities.clear();
                mSelectEntities.addAll(mTempSelectEntities);
                if (!mSelectEntities.contains(mBaseDepotEntity)) {
                    //在单选，循环读取ids
                    StringBuilder stringBuilder = new StringBuilder();
                    for (AlarmDepotEntity entity : mSelectEntities) {
                        stringBuilder.append(entity.getId() + ",");
                    }
                    mLibIds = stringBuilder.toString();
                    mLibIds = mLibIds.substring(0, mLibIds.length() - 1);
                } else {
                    mLibIds = null;
                }
                mIsEnterEndTime = true;
                mClothSwiperl.setRefreshing(true);
                getNewData();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TYPE_INTO_FACES:
                    if (TextUtils.isEmpty(mEffective)) {
                        mClothSwiperl.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //表示是全部，则需要更新数据
                                mClothSwiperl.setRefreshing(true);
                                onRefresh();
                            }
                        }, 150);
                    } else {
                        int position = data.getIntExtra("position", -1);
                        if (-1 != position) {
                            mFaceAlarmAdapter.remove(position);
                            mFaceAlarmAdapter.notifyItemRemoved(position);
                        }
                    }
                    break;
                case TYPE_SELECT_CAMERA:
                    mCameraIds = data.getStringExtra("cameraIds");
                    mClothSwiperl.setRefreshing(true);
                    getNewData();
                    break;
            }

        }

    }
}
