package cloud.antelope.lingmou.mvp.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.google.gson.Gson;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.TimeUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.umeng.analytics.MobclickAgent;

import org.litepal.crud.DataSupport;
import org.simple.eventbus.Subscriber;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.ResultHolder;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.di.component.DaggerFaceDepotComponent;
import cloud.antelope.lingmou.di.module.FaceDepotModule;
import cloud.antelope.lingmou.mvp.contract.FaceDepotContract;
import cloud.antelope.lingmou.mvp.model.entity.CollectCameraEntity;
import cloud.antelope.lingmou.mvp.model.entity.DailyFilterItemEntity;
import cloud.antelope.lingmou.mvp.model.entity.DetailCommonEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceDepotDetailEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceDepotEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceNewEntity;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreBaseEntity;
import cloud.antelope.lingmou.mvp.model.entity.GetKeyStoreEntity;
import cloud.antelope.lingmou.mvp.model.entity.OrgCameraEntity;
import cloud.antelope.lingmou.mvp.model.entity.PictureCollectEvent;
import cloud.antelope.lingmou.mvp.model.entity.SetKeyStoreRequest;
import cloud.antelope.lingmou.mvp.presenter.FaceDepotPresenter;
import cloud.antelope.lingmou.mvp.ui.adapter.DailyFilterAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.FaceDepotAdapter;
import cloud.antelope.lingmou.mvp.ui.fragment.CollectionListFragment;
import cloud.antelope.lingmou.mvp.ui.widget.LmDatePickDialog;
import cloud.antelope.lingmou.mvp.ui.widget.LmTimePickerDialog;
import cloud.antelope.lingmou.mvp.ui.widget.MenuView;
import cloud.antelope.lingmou.mvp.ui.widget.RightMenu;


public class FaceDepotActivity extends BaseActivity<FaceDepotPresenter>
        implements FaceDepotContract.View,
        RadioGroup.OnCheckedChangeListener,
        View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        BaseQuickAdapter.RequestLoadMoreListener {

    private static final int TYPE_SELECT_CAMERA = 0x01;
    @BindView(R.id.back_ib)
    ImageButton mBackIb;
    @BindView(R.id.face_title)
    TextView mFaceTitle;
    @BindView(R.id.title_rl)
    RelativeLayout mTitleRl;
    @BindView(R.id.face_depot_rclv)
    RecyclerView mFaceDepotRclv;
    @BindView(R.id.face_depot_srfl)
    SwipeRefreshLayout mFaceDepotSrfl;
    @BindView(R.id.rl_empty)
    RelativeLayout rlEmpty;
    @BindView(R.id.empty_no_network_tv)
    TextView mEmptyNoNetworkTv;
    @BindView(R.id.search_ib)
    ImageButton mSearchIb;
    @BindView(R.id.filter_ll)
    LinearLayout mFilterLl;
    @BindView(R.id.filter_rclv)
    RecyclerView mFilterRclv;
    @BindView(R.id.filter_range_tv)
    TextView mFilterRangeTv;
    @BindView(R.id.filter_gender_tv)
    TextView mFilterGenderTv;
    @BindView(R.id.filter_address_tv)
    TextView mFilterAddressTv;
    //    @BindView(R.id.shaixuan_ll)
//    LinearLayout mShaixuanLl;
    @BindView(R.id.filter_tv)
    TextView mFilterTv;
    RadioGroup mRangeRg;
    @BindView(R.id.address_rg_three)
    RadioGroup mAddressRgThree;
    @BindView(R.id.address_rg_four)
    RadioGroup addressRgFour;
    @BindView(R.id.ll_filter)
    LinearLayout llFilter;
    @BindView(R.id.ll_address)
    LinearLayout llAddress;
    LinearLayout llEange;
    @BindView(R.id.view_divider)
    View viewDivider;
    @BindView(R.id.filter_ll1)
    LinearLayout filterLl1;
    ScrollView scrollView;
    @BindView(R.id.tv_search)
    TextView tvSearch;
    @BindView(R.id.ctl_search)
    CollapsingToolbarLayout ctlSearch;
    @BindView(R.id.rl_search)
    RelativeLayout rlSearch;
    RelativeLayout llRight;
    @Inject
    @Named("gridlayout")
    RecyclerView.LayoutManager mGridLayoutManager;
    @Inject
    @Named("linearlayout")
    RecyclerView.LayoutManager mLinearLayoutManager;
    @Inject
    RecyclerView.ItemDecoration mItemDecoration;
    @Inject
    @Named("gridAnimator")
    RecyclerView.ItemAnimator mItemAnimator;
    @Inject
    @Named("linearAnimator")
    RecyclerView.ItemAnimator mLinearItemAnimator;
    @Inject
    FaceDepotAdapter mFaceDepotAdapter;
    @Inject
    LoadMoreView mLoadMoreView;
    @Inject
    DailyFilterAdapter mDailyFilterAdapter;

    private RelativeLayout mPopLl;
    private TextView mConfirmTv;
    private TextView mCleanTv;
    private RadioGroup mAddressRgOne, mAddressRgTwo, mGenderRg, mBodyRg, mAreaRg;
    private LinearLayout mStartTimeLl, mEndTimeLl;
    private TextView mStartTimeTv, mEndTimeTv;
    private LmDatePickDialog mStartDateDialog, mEndDateDialog;
    private LmTimePickerDialog mStartTimeDialog, mEndTimeDialog;
    private int mStartYear, mStartMonth, mStartDay, mStartHour, mStartMinute;
    private int mEndYear, mEndMonth, mEndDay, mEndHour, mEndMinute;
    private Activity mActivity;
    private Long mStartTimeStamp;
    private Long mEndTimeStamp;

    private static int SIZE = 15;
    private List<String> mCameraTags = null; //默认null，是不限
    private List<String> noCameraTags = null; //默认null，是不限
    private List<String> mFaceTags = null; //默认null，是不限
    private List<String> mSexTags = null; //默认null，是不限
    private List<String> mEmptyTags = null; //默认null，是不限
    private List<String> mCameraIds = null;
    private String mCameraIdsType = "";
    private String mTempCameraIdsType = "";
    private String mCameraType = "";
    private String mTempCameraType = "";
    private Long mTempStartTimeStamp;
    private Long mTempEndTimeStamp;
    private String mSex = "";
    private String mTempSex = "";
    private String mEyeGlass = "";
    private String mTempEyeGlass = "";
    private boolean mIsEnterEndTime;
    private String mMinId;
    private String mMinCaptureTime;
    private String mPageType = "0";

    private static final long SEVEN_DAY_STAMP = 7 * 24 * 60 * 60 * 1000;
    private boolean fromNewDeployMissionActivity;

    private List<DailyFilterItemEntity> mRangeEntities;
    private List<DailyFilterItemEntity> mGenderEntities;
    private List<DailyFilterItemEntity> mAddressEntities;

    private int mPosition;

    private int mPreRangePosition;
    private int mPreGenderPosition;
    private int mPreAddressPosition;
    private int mTempPreRangePosition;
    private int mTempPreGenderPosition;
    private int mTempPreAddressPosition;

    private Drawable mFilterArrowDownYellowDrawable, mFilterArrowDownBlackDrawable,
            mFilterArrowUpYellowDrawable, mFilterArrowUpBlackDrawable,
            mFilterBlackDrawable, mFilterYellowDrawable;
    //    private Animation mTopOut;
//    private Animation mTopIn;
    private Animation mRightIn;
    private Animation mRightOut;
    private boolean mTimeChanged;
    List<OrgCameraEntity> mOrgCameraEntities;
    private MenuView menuView;
    private RightMenu rightMenu;
    private float[] mFeature;
    private FaceDepotDetailEntity clickedEntity;

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        DaggerFaceDepotComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .faceDepotModule(new FaceDepotModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_face_depot; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mActivity = this;
        initFilterData();
        long cameraId = getIntent().getLongExtra("cameraId", -1);
        fromNewDeployMissionActivity = getIntent().getBooleanExtra("fromNewDeployMissionActivity", false);
        if (cameraId != -1) {
            mCameraIds = new ArrayList<>();
            mCameraIds.add(String.valueOf(cameraId));
        }
        mOrgCameraEntities = new ArrayList<>();

//        mTopOut = AnimationUtils.loadAnimation(getActivity(), R.anim.top_out);
//        mTopIn = AnimationUtils.loadAnimation(getActivity(), R.anim.top_in);
        mRightIn = AnimationUtils.loadAnimation(getActivity(), R.anim.right_in);
        mRightOut = AnimationUtils.loadAnimation(getActivity(), R.anim.right_out);
        mFilterArrowDownBlackDrawable = getResources().getDrawable(R.drawable.arrow_black_triangle_down);
        mFilterArrowUpBlackDrawable = getResources().getDrawable(R.drawable.arrow_black_triangle_up);
        mFilterArrowDownYellowDrawable = getResources().getDrawable(R.drawable.arrow_yellow_triangle_down);
        mFilterArrowUpYellowDrawable = getResources().getDrawable(R.drawable.arrow_yellow_triangle_up);
        mFilterBlackDrawable = getResources().getDrawable(R.drawable.filter_black_small);
        mFilterYellowDrawable = getResources().getDrawable(R.drawable.filter_orange_small);
        mFilterArrowDownBlackDrawable.setBounds(0, 0, mFilterArrowDownBlackDrawable.getMinimumWidth(), mFilterArrowDownBlackDrawable.getMinimumHeight());
        mFilterArrowUpBlackDrawable.setBounds(0, 0, mFilterArrowUpBlackDrawable.getMinimumWidth(), mFilterArrowUpBlackDrawable.getMinimumHeight());
        mFilterArrowDownYellowDrawable.setBounds(0, 0, mFilterArrowDownYellowDrawable.getMinimumWidth(), mFilterArrowDownYellowDrawable.getMinimumHeight());
        mFilterArrowUpYellowDrawable.setBounds(0, 0, mFilterArrowUpYellowDrawable.getMinimumWidth(), mFilterArrowUpYellowDrawable.getMinimumHeight());
        mFilterBlackDrawable.setBounds(0, 0, mFilterBlackDrawable.getMinimumWidth(), mFilterBlackDrawable.getMinimumHeight());
        mFilterYellowDrawable.setBounds(0, 0, mFilterYellowDrawable.getMinimumWidth(), mFilterYellowDrawable.getMinimumHeight());


        mFaceDepotSrfl.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        mFaceDepotSrfl.setOnRefreshListener(this);
        mFaceDepotRclv.setHasFixedSize(true);
        mFaceDepotRclv.setLayoutManager(mGridLayoutManager);
        mFaceDepotRclv.addItemDecoration(mItemDecoration);
        mFaceDepotRclv.setItemAnimator(mItemAnimator);
        mFaceDepotAdapter.setOnLoadMoreListener(this, mFaceDepotRclv);
        mFaceDepotAdapter.setLoadMoreView(mLoadMoreView);
        mFaceDepotRclv.setAdapter(mFaceDepotAdapter);


        mFilterRclv.setLayoutManager(new GridLayoutManager(FaceDepotActivity.this, 3));
        mFilterRclv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int position = parent.getChildLayoutPosition(view);
                outRect.left = position % 3 == 0 ? getResources().getDimensionPixelSize(R.dimen.dp16) : getResources().getDimensionPixelSize(R.dimen.dp8);
                outRect.right = position % 3 == 2 ? getResources().getDimensionPixelSize(R.dimen.dp16) : getResources().getDimensionPixelSize(R.dimen.dp8);
                outRect.top = position < 3 ? getResources().getDimensionPixelSize(R.dimen.dp16) : 0;
                outRect.bottom = getResources().getDimensionPixelSize(R.dimen.dp16);
            }
        });
        mFilterRclv.setAdapter(mDailyFilterAdapter);


        mEndTimeStamp = System.currentTimeMillis();
        mTempEndTimeStamp = mEndTimeStamp;
        mEndTimeTv.setText(TimeUtils.millis2String(mEndTimeStamp, "yyyy.MM.dd HH:mm"));


        mStartTimeStamp = System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000;
        mTempStartTimeStamp = mStartTimeStamp;
        mEndTimeTv.setText(TimeUtils.millis2String(mEndTimeStamp, "yyyy.MM.dd HH:mm"));

        mStartDateDialog = new LmDatePickDialog(this, (view, year, month, dayOfMonth) -> {
            mStartYear = year;
            mStartMonth = month + 1;
            mStartDay = dayOfMonth;
            initStartTimeDialog();
        }, Calendar.getInstance(), mStartYear == 0 ? Calendar.getInstance().get(Calendar.YEAR) : mStartYear,
                mStartMonth == 0 ? Calendar.getInstance().get(Calendar.MONTH) : mStartMonth,
                mStartDay == 0 ? Calendar.getInstance().get(Calendar.DAY_OF_MONTH) : mStartDay);
        mStartDateDialog.getDatePicker().setMinDate(System.currentTimeMillis()-30L*24*60*60*1000);

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

        mEndDateDialog.getDatePicker().setMinDate(System.currentTimeMillis()-30L*24*60*60*1000);
        mStartDateDialog.getDatePicker().setMaxDate(System.currentTimeMillis() + (1000 * 60 * 60));
        mEndDateDialog.getDatePicker().setMaxDate(System.currentTimeMillis() + (1000 * 60 * 60));
        onRefresh();
        mFaceDepotSrfl.post(new Runnable() {
            @Override
            public void run() {
                mFaceDepotSrfl.setRefreshing(true);
            }
        });
        mPresenter.getCollections();
        initListener();
    }

    private void initFilterData() {
        llRight = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.popwindow_face_depot, null);
        mPopLl = llRight.findViewById(R.id.pop_root);
        mCleanTv = mPopLl.findViewById(R.id.clean_tv);
        mConfirmTv = mPopLl.findViewById(R.id.cofirm_tv);
        mAddressRgOne = mPopLl.findViewById(R.id.address_rg_one);
        mAddressRgTwo = mPopLl.findViewById(R.id.address_rg_two);
        mStartTimeLl = mPopLl.findViewById(R.id.start_time_ll);
        mEndTimeLl = mPopLl.findViewById(R.id.end_time_ll);
        mStartTimeTv = mPopLl.findViewById(R.id.start_time_tv);
        mEndTimeTv = mPopLl.findViewById(R.id.end_time_tv);
        mGenderRg = mPopLl.findViewById(R.id.gender_rg);
        mBodyRg = mPopLl.findViewById(R.id.body_rg);
        mAreaRg = mPopLl.findViewById(R.id.area_rg);
        scrollView = llRight.findViewById(R.id.scrollview);
        llEange = llRight.findViewById(R.id.ll_range);
        mRangeRg = llRight.findViewById(R.id.range_rg);


        mRangeEntities = new ArrayList<>();
        mGenderEntities = new ArrayList<>();
        mAddressEntities = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            DailyFilterItemEntity entity = new DailyFilterItemEntity();
            if (i == 0) {
                entity.setSelect(true);
                entity.setName("全部");
            } else {
                entity.setSelect(false);
                entity.setName("我关注的");
            }
            mRangeEntities.add(entity);
        }

        for (int i = 0; i < 3; i++) {
            DailyFilterItemEntity entity = new DailyFilterItemEntity();
            if (i == 0) {
                entity.setSelect(true);
                entity.setName("全部");
            } else if (i == 1) {
                entity.setSelect(false);
                entity.setName("男");
            } else {
                entity.setSelect(false);
                entity.setName("女");
            }
            mGenderEntities.add(entity);
        }
        mAddressEntities.add(new DailyFilterItemEntity("不限", true));
        mAddressEntities.add(new DailyFilterItemEntity("社区", false));
        mAddressEntities.add(new DailyFilterItemEntity("网吧", false));
        mAddressEntities.add(new DailyFilterItemEntity("酒店", false));
        mAddressEntities.add(new DailyFilterItemEntity("餐厅", false));
        mAddressEntities.add(new DailyFilterItemEntity("商场", false));
        mAddressEntities.add(new DailyFilterItemEntity("重点区域", false));
        mAddressEntities.add(new DailyFilterItemEntity("重点商铺", false));
        mAddressEntities.add(new DailyFilterItemEntity("停车棚", false));
        mAddressEntities.add(new DailyFilterItemEntity("其他", false));
        if (getIntent() != null) {
            boolean fromDevice = getIntent().getBooleanExtra("fromDevice", false);
            if (getIntent().getBooleanExtra("fromDevice", false)) {
                mFaceTitle.setText(R.string.face_record);
                rlSearch.setVisibility(View.GONE);
            }
            if (fromDevice) {
                llAddress.setVisibility(View.GONE);
                llEange.setVisibility(View.GONE);
                viewDivider.setVisibility(View.GONE);
                llFilter.setVisibility(View.GONE);
                filterLl1.setVisibility(View.VISIBLE);
                mSearchIb.setVisibility(View.GONE);
            }
        }


    }

    private void initEndTimeDialog() {
        mEndTimeDialog = new LmTimePickerDialog(this, 0, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mEndHour = hourOfDay;
                mEndMinute = minute;
                Long endTime = TimeUtils.string2Millis(mEndYear + "-" + mEndMonth + "-" + mEndDay + " " + mEndHour + ":" + mEndMinute, new SimpleDateFormat("yyyy-MM-dd HH:mm"));
                String timeStr = TimeUtils.millis2String(endTime, "yyyy.MM.dd HH:mm");
                if (mTempStartTimeStamp != null && (endTime - mTempStartTimeStamp > SEVEN_DAY_STAMP)) {
                    ToastUtils.showShort(R.string.hint_not_more_seven_days);
                } else {
                    mTempEndTimeStamp = endTime;
                    mEndTimeTv.setText(timeStr);
                    mEndDateDialog.dismiss();
                    mTimeChanged = true;
                }
            }
        }, mEndHour == 0 ? Calendar.getInstance().get(Calendar.HOUR_OF_DAY) : mEndHour,
                mEndMinute == 0 ? Calendar.getInstance().get(Calendar.MINUTE) : mEndMinute, true);
        mEndTimeDialog.show();
    }

    private void initStartTimeDialog() {
        mStartTimeDialog = new LmTimePickerDialog(this, 0, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mStartHour = hourOfDay;
                mStartMinute = minute;
                Long startTime = TimeUtils.string2Millis(mStartYear + "-" + mStartMonth + "-" + mStartDay + " " + mStartHour + ":" + mStartMinute, new SimpleDateFormat("yyyy-MM-dd HH:mm"));
                String timeStr = TimeUtils.millis2String(startTime, "yyyy.MM.dd HH:mm");
//                if (mTempEndTimeStamp != null && (mTempEndTimeStamp - startTime > SEVEN_DAY_STAMP)) {
//                    ToastUtils.showShort(R.string.hint_not_more_seven_days);
//                } else {
                mTempStartTimeStamp = startTime;
                mStartTimeTv.setText(timeStr);
                mStartDateDialog.dismiss();
                mTimeChanged = true;
//                }
            }
        }, mStartHour == 0 ? Calendar.getInstance().get(Calendar.HOUR_OF_DAY) : mStartHour,
                mStartMinute == 0 ? Calendar.getInstance().get(Calendar.MINUTE) : mStartMinute, true);
        mStartTimeDialog.show();
    }

    private void setPopDismiss() {
        rightMenu.dismiss();
        rightMenu.setOnDismissListener(new RightMenu.OnDismissListener() {
            @Override
            public void onDismiss() {
                showArrowDown(mPosition);
            }
        });
    }

    private void initListener() {
        mRangeRg.setOnCheckedChangeListener(this);
        mAddressRgOne.setOnCheckedChangeListener(this);
        mAddressRgTwo.setOnCheckedChangeListener(this);
        mAddressRgThree.setOnCheckedChangeListener(this);
        addressRgFour.setOnCheckedChangeListener(this);
        mGenderRg.setOnCheckedChangeListener(this);
        mBodyRg.setOnCheckedChangeListener(this);
        mAreaRg.setOnCheckedChangeListener(this);
        mConfirmTv.setOnClickListener(this);
        mCleanTv.setOnClickListener(this);
        mStartTimeLl.setOnClickListener(this);
        mEndTimeLl.setOnClickListener(this);

        mDailyFilterAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (mPosition == 0) {
                    //证明当前是第一个筛选
                    mRangeEntities.get(mPreRangePosition).setSelect(false);
                    mPreRangePosition = position;
                    mRangeEntities.get(position).setSelect(true);
                    showArrowDown(0);
                    switch (position) {
                        case 0:
                            mCameraIdsType = "";
                            mCameraIds = null;
                            break;
                        case 1:
                            //mCameraIds = 收藏的所有摄像机
                            mCameraIdsType = "1";
                            if (null == mCameraIds) {
                                mCameraIds = new ArrayList<>();
                            }
                            mCameraIds.clear();
                            for (OrgCameraEntity entity : mOrgCameraEntities) {
                                mCameraIds.add(String.valueOf(entity.getManufacturerDeviceId()));
                            }
                            break;
                    }
                } else if (mPosition == 1) {
                    mGenderEntities.get(mPreGenderPosition).setSelect(false);
                    mPreGenderPosition = position;
                    mGenderEntities.get(position).setSelect(true);
                    showArrowDown(1);
                    switch (position) {
                        case 0:
                            mSex = "";
                            break;
                        case 1:
                            mSex = "100001";
                            break;
                        case 2:
                            mSex = "100002";
                            break;
                    }
                } else if (mPosition == 2) {
                    mAddressEntities.get(mPreAddressPosition).setSelect(false);
                    mPreAddressPosition = position;
                    mAddressEntities.get(position).setSelect(true);
                    showArrowDown(2);
                    switch (position) {
                        case 0:
                            mCameraType = "";
                            break;
                        case 1:
                            mCameraType = "102401";
                            break;
                        case 2:
                            mCameraType = "102402";
                            break;
                        case 3:
                            mCameraType = "102403";
                            break;
                        case 4:
                            mCameraType = "102404";
                            break;
                        case 5:
                            mCameraType = "102405";
                            break;
                        case 6:
                            mCameraType = "102406";
                            break;
                        case 7:
                            mCameraType = "102407";
                            break;
                        case 8:
                            mCameraType = "102408";
                            break;
                        case 9:
                            mCameraType = null;
                            break;
                    }
                }
                initNetParams();
               /* mShaixuanLl.setVisibility(View.GONE);
                mShaixuanLl.startAnimation(mTopOut);*/
                menuView.dismiss();
                mPageType = "0";
                mFaceDepotSrfl.setRefreshing(true);
                onRefresh();
            }
        });

        mFaceDepotAdapter.setOnItemClickListener((adapter, view, position) -> {
            Intent intent = new Intent();
            intent.putExtra("position", position);
            ArrayList<FaceDepotDetailEntity> detailEntities = (ArrayList<FaceDepotDetailEntity>) adapter.getData();
            ArrayList<DetailCommonEntity> changeData = getChangeData(detailEntities);
            if (fromNewDeployMissionActivity) {
                MobclickAgent.onEvent(FaceDepotActivity.this, "face_toLinkong");
                FaceDepotDetailEntity entity = detailEntities.get(position);
                intent.putExtra("facePath", entity.facePath);
                setResult(RESULT_OK, intent);
                finish();
            } else if (changeData != null) {
                boolean hasPermission = SPUtils.getInstance().getBoolean(Constants.PERMISSION_HAS_HYJJ, false);
                if (hasPermission) {
                    intent.setClass(FaceDepotActivity.this, FaceDepotDetailActivity.class);
                    intent.putExtra("face_Entity", detailEntities.get(position));
                    startActivity(intent);
                } else {
                    intent.setClass(FaceDepotActivity.this, PictureDetailActivity.class);
                    intent.putParcelableArrayListExtra("bean", changeData);
                    startActivity(intent);
                }
            }
        });
    }

    private void initNetParams() {
        if (mCameraType == null) {
            mCameraTags = null;
            noCameraTags = Arrays.asList("102401", "102402", "102403", "102404", "102405", "102406", "102407", "102408");
        } else if (!TextUtils.isEmpty(mCameraType)) {
            if (null == mCameraTags) {
                mCameraTags = new ArrayList<>();
            }
            mCameraTags.clear();
            mCameraTags.add(mCameraType);
            noCameraTags = null;
        } else {
            mCameraTags = null;
            noCameraTags = null;
        }
        if (!TextUtils.isEmpty(mSex)) {
            if (null == mSexTags) {
                mSexTags = new ArrayList<>();
            }
            mSexTags.clear();
            mSexTags.add(mSex);
        } else {
            if (null != mSexTags) {
                mSexTags.clear();
            }
            mFaceTags = null;
        }

        if (!TextUtils.isEmpty(mEyeGlass)) {
            if (null == mFaceTags) {
                mFaceTags = new ArrayList<>();
            }
            mFaceTags.clear();
            if (null != mSexTags) {
                mFaceTags.addAll(mSexTags);
            }
            if ("112801".equals(mEyeGlass)) {
                mFaceTags.add(mEyeGlass);
                mEmptyTags = null;
            } else {
                if (mEmptyTags == null) {
                    mEmptyTags = new ArrayList<>();
                }
                mEmptyTags.clear();
                mEmptyTags.add("112801");
            }
        } else {
            mEmptyTags = null;
            if (null != mSexTags) {
                if (null == mFaceTags) {
                    mFaceTags = new ArrayList<>();
                }
                mFaceTags.clear();
                mFaceTags.addAll(mSexTags);
            }
        }
    }

    private void showArrowDown(int typePosition) {
        switch (typePosition) {
            case 0:
                //range
                if (mPreRangePosition == 0) {
                    //第一个，全部，则颜色变回黑色
                    mFilterRangeTv.setText(R.string.range);
                    mFilterRangeTv.setTextColor(getResources().getColor(R.color.gray_212121));
                    mFilterRangeTv.setCompoundDrawables(null, null, mFilterArrowDownBlackDrawable, null);
                } else {
                    mFilterRangeTv.setText(mRangeEntities.get(mPreRangePosition).getName());
                    mFilterRangeTv.setTextColor(getResources().getColor(R.color.yellow_ff8f00));
                    mFilterRangeTv.setCompoundDrawables(null, null, mFilterArrowDownYellowDrawable, null);
                }
                break;
            case 1:
                //type
                if (mPreGenderPosition == 0) {
                    //第一个，全部，则颜色变回黑色
                    mFilterGenderTv.setText(R.string.gender);
                    mFilterGenderTv.setTextColor(getResources().getColor(R.color.gray_212121));
                    mFilterGenderTv.setCompoundDrawables(null, null, mFilterArrowDownBlackDrawable, null);
                } else {
                    mFilterGenderTv.setText(mGenderEntities.get(mPreGenderPosition).getName());
                    mFilterGenderTv.setTextColor(getResources().getColor(R.color.yellow_ff8f00));
                    mFilterGenderTv.setCompoundDrawables(null, null, mFilterArrowDownYellowDrawable, null);
                }
                break;
            case 2:
                //state
                if (mPreAddressPosition == 0) {
                    //第一个，全部，则颜色变回黑色
                    mFilterAddressTv.setText(R.string.alarm_address);
                    mFilterAddressTv.setTextColor(getResources().getColor(R.color.gray_212121));
                    mFilterAddressTv.setCompoundDrawables(null, null, mFilterArrowDownBlackDrawable, null);
                } else {
                    mFilterAddressTv.setText(mAddressEntities.get(mPreAddressPosition).getName());
                    mFilterAddressTv.setTextColor(getResources().getColor(R.color.yellow_ff8f00));
                    mFilterAddressTv.setCompoundDrawables(null, null, mFilterArrowDownYellowDrawable, null);
                }
                break;
        }
    }

    private void showFilterArrow() {
        if (mTimeChanged || !TextUtils.isEmpty(mEyeGlass)) {
            mFilterTv.setTextColor(getResources().getColor(R.color.yellow_ff8f00));
            mFilterTv.setCompoundDrawables(mFilterYellowDrawable, null, null, null);
        } else {
            mFilterTv.setTextColor(getResources().getColor(R.color.gray_212121));
            mFilterTv.setCompoundDrawables(mFilterBlackDrawable, null, null, null);
        }
    }

    private ArrayList<DetailCommonEntity> getChangeData(ArrayList<FaceDepotDetailEntity> entities) {
        if (null != entities) {
            ArrayList<DetailCommonEntity> list = new ArrayList<>();
            for (FaceDepotDetailEntity entity : entities) {
                DetailCommonEntity commonEntity = new DetailCommonEntity();
                commonEntity.deviceName = entity.cameraName;
                commonEntity.endTime = Long.parseLong(entity.captureTime);
                commonEntity.point = entity.faceRect;
                commonEntity.srcImg = entity.scenePath;
                commonEntity.collectId = entity.collectId;
                commonEntity.id = entity.id;
                commonEntity.favoritesType = CollectionListFragment.FACE;
                commonEntity.cameraId = entity.cameraId;
                commonEntity.cameraName = entity.cameraName;
                commonEntity.faceImg = entity.facePath;
                commonEntity.captureTime = Long.parseLong(entity.captureTime);
                commonEntity.collected = "1".equals(entity.isCollect);
                list.add(commonEntity);
            }
            return list;
        }
        return null;
    }

    @Override
    public void onBackPressedSupport() {
        if (null != rightMenu && rightMenu.getStatus() == RightMenu.SHOW) {
            setPopDismiss();
        } else {
            super.onBackPressedSupport();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    private void showArrowUp(int typePosition) {
        switch (typePosition) {
            case 0:
                //range
                if (mPreRangePosition == 0) {
                    mFilterRangeTv.setCompoundDrawables(null, null, mFilterArrowUpBlackDrawable, null);
                } else {
                    mFilterRangeTv.setCompoundDrawables(null, null, mFilterArrowUpYellowDrawable, null);
                }
                break;
            case 1:
                //type
                if (mPreGenderPosition == 0) {
                    mFilterGenderTv.setCompoundDrawables(null, null, mFilterArrowUpBlackDrawable, null);
                } else {
                    mFilterGenderTv.setCompoundDrawables(null, null, mFilterArrowUpYellowDrawable, null);
                }
                break;
            case 2:
                //state
                if (mPreAddressPosition == 0) {
                    mFilterAddressTv.setCompoundDrawables(null, null, mFilterArrowUpBlackDrawable, null);
                } else {
                    mFilterAddressTv.setCompoundDrawables(null, null, mFilterArrowUpYellowDrawable, null);
                }
                break;
        }
    }

    @OnClick({R.id.back_ib, R.id.filter_range_ll, R.id.filter_gender_ll, R.id.filter_address_ll,
            R.id.filter_ll, R.id.filter_ll1, R.id.search_ib, R.id.tv_search})
    public void onViewClicked(View view) {
        boolean rightOpen = false;
        if (rightMenu != null && rightMenu.getStatus() == RightMenu.SHOW) {
            rightOpen = true;
        }
        int filterVisibility = mPopLl.getVisibility();
        switch (view.getId()) {
            case R.id.back_ib:
                if (rightOpen) {
                    rightMenu.dismiss();
                    rightMenu.setOnDismissListener(new RightMenu.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            showArrowDown(mPosition);
                        }
                    });
                } else {
                    finish();
                }
                break;
            case R.id.filter_range_ll:
//                mPopLl.setVisibility(View.GONE);
                mFilterRclv.setVisibility(View.VISIBLE);
                mDailyFilterAdapter.setNewData(mRangeEntities);
                menuView = new MenuView(getActivity(), mFilterRclv, getViewHeight(mRangeEntities));
                menuView.show(llFilter);
                menuView.setOnDismissListener(new MenuView.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        showArrowDown(mPosition);
                    }
                });
                if (rightOpen) {
                    if (mPosition != 0) {
                        showArrowUp(0);
                        showArrowDown(mPosition);
                    } else {
                        showArrowDown(0);
                    }
                } else {
                    showArrowUp(0);
                }
                mPosition = 0;

                /*if (visibility == View.VISIBLE) {
                    mPopLl.setVisibility(View.GONE);
                    mFilterRclv.setVisibility(View.VISIBLE);
                    if (mPosition != 0) {
                        showArrowUp(0);
                        mDailyFilterAdapter.setNewData(mRangeEntities);
                        showArrowDown(mPosition);

                    } else {
                        mShaixuanLl.startAnimation(mTopOut);
                        mShaixuanLl.setVisibility(View.GONE);
                        showArrowDown(0);
                    }
                } else {
                    mPopLl.setVisibility(View.GONE);
                    mFilterRclv.setVisibility(View.VISIBLE);
                    mDailyFilterAdapter.setNewData(mRangeEntities);
                    mShaixuanLl.setVisibility(View.VISIBLE);
                    mShaixuanLl.startAnimation(mTopIn);
                    showArrowUp(0);
                }
                mPosition = 0;*/
                break;
            case R.id.filter_gender_ll:
//                mPopLl.setVisibility(View.GONE);
                mFilterRclv.setVisibility(View.VISIBLE);
                mDailyFilterAdapter.setNewData(mGenderEntities);
                menuView = new MenuView(getActivity(), mFilterRclv, getViewHeight(mGenderEntities));
                menuView.show(llFilter);
                menuView.setOnDismissListener(new MenuView.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        showArrowDown(mPosition);
                    }
                });
                if (rightOpen) {
                    if (mPosition != 1) {
                        showArrowUp(1);
                        showArrowDown(mPosition);
                    } else {
                        showArrowDown(1);
                    }
                } else {
                    showArrowUp(1);
                }
                mPosition = 1;
               /* if (visibility == View.VISIBLE) {
                    mPopLl.setVisibility(View.GONE);
                    mFilterRclv.setVisibility(View.VISIBLE);
                    if (mPosition != 1) {
                        showArrowUp(1);
                        mDailyFilterAdapter.setNewData(mGenderEntities);
                        showArrowDown(mPosition);
                    } else {
                        mShaixuanLl.startAnimation(mTopOut);
                        mShaixuanLl.setVisibility(View.GONE);
                        showArrowDown(1);
                    }
                } else {
                    mPopLl.setVisibility(View.GONE);
                    mFilterRclv.setVisibility(View.VISIBLE);
                    mDailyFilterAdapter.setNewData(mGenderEntities);
                    mShaixuanLl.setVisibility(View.VISIBLE);
                    mShaixuanLl.startAnimation(mTopIn);
                    showArrowUp(1);
                }
                mPosition = 1;*/
                break;
            case R.id.filter_address_ll:
//                mPopLl.setVisibility(View.GONE);
                mFilterRclv.setVisibility(View.VISIBLE);
                mDailyFilterAdapter.setNewData(mAddressEntities);
                menuView = new MenuView(getActivity(), mFilterRclv, getViewHeight(mAddressEntities));
                menuView.show(llFilter);
                menuView.setOnDismissListener(new MenuView.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        showArrowDown(mPosition);
                    }
                });
                if (rightOpen) {
                    if (mPosition != 2) {
                        showArrowUp(2);
                        showArrowDown(mPosition);
                    } else {
                        showArrowDown(2);
                    }
                } else {
                    showArrowUp(2);
                }
                mPosition = 2;
               /* if (visibility == View.VISIBLE) {
                    mPopLl.setVisibility(View.GONE);
                    mFilterRclv.setVisibility(View.VISIBLE);
                    if (mPosition != 2) {
                        showArrowUp(2);
                        mDailyFilterAdapter.setNewData(mAddressEntities);
                        showArrowDown(mPosition);
                    } else {
                        mShaixuanLl.startAnimation(mTopOut);
                        mShaixuanLl.setVisibility(View.GONE);
                        showArrowDown(2);
                    }
                } else {
                    mPopLl.setVisibility(View.GONE);
                    mFilterRclv.setVisibility(View.VISIBLE);
                    mDailyFilterAdapter.setNewData(mAddressEntities);
                    mShaixuanLl.startAnimation(mTopIn);
                    mShaixuanLl.setVisibility(View.VISIBLE);
                    showArrowUp(2);
                }
                mPosition = 2;*/
                break;
            case R.id.filter_ll:
            case R.id.filter_ll1:
                //显示过滤条件popWindow
                if (rightOpen) {
                    if (filterVisibility == View.VISIBLE) {
                        rightMenu.dismiss();
                        rightMenu.setOnDismissListener(new RightMenu.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                mPopLl.setVisibility(View.GONE);
                            }
                        });
                    } else {
                        mFilterRclv.setVisibility(View.GONE);
                        restoreData();
                        mPopLl.setVisibility(View.VISIBLE);
                        showArrowDown(mPosition);
                    }
                } else {
                    showRight();
                }
                mPosition = 3;
                break;
            case R.id.search_ib:
            case R.id.tv_search:
                //进入搜索界面
                if (rightOpen) {
                    setPopDismiss();
                }

                ActivityOptions activityOptions = null;
                Intent intent = new Intent(FaceDepotActivity.this, SearchCameraActivity.class);
                intent.putExtra("fromDepot", true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    activityOptions = ActivityOptions.makeSceneTransitionAnimation(FaceDepotActivity.this, tvSearch, "searchText");
                    startActivityForResult(intent, TYPE_SELECT_CAMERA, activityOptions.toBundle());
                } else {
                    startActivityForResult(intent, TYPE_SELECT_CAMERA);
                }
                break;
        }
    }

    private void showRight() {
        if (rightMenu == null) {
            rightMenu = new RightMenu(FaceDepotActivity.this, llRight);
            scrollView.scrollTo(0, 0);
            mFilterRclv.setVisibility(View.GONE);
            restoreData();
            mPopLl.setVisibility(View.VISIBLE);

        }
        rightMenu.show(llFilter);
    }

    private int getViewHeight(List list) {
        return list.size() * getResources().getDimensionPixelSize(R.dimen.dp40) + list.size() - 1;
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
            if (!mIsEnterEndTime) {
                mEndTimeStamp = System.currentTimeMillis();
                mTempEndTimeStamp = mEndTimeStamp;
                mEndTimeTv.setText(TimeUtils.millis2String(mEndTimeStamp, "yyyy.MM.dd HH:mm"));
            }
            //开始时间不为空，则表示已选择，恢复到取消前
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
        if (mCameraType == null) {
            setRadioButtonChecked(3, R.id.address_other_rb);
        } else if (TextUtils.isEmpty(mCameraType)) {
            setRadioButtonChecked(0, R.id.address_none_rb);
        } else {
            switch (mCameraType) {
                case "102401":
                    setRadioButtonChecked(0, R.id.address_shequ_rb);
                    break;
                case "102402":
                    setRadioButtonChecked(0, R.id.address_wangba_rb);
                    break;
                case "102403":
                    setRadioButtonChecked(1, R.id.address_hotel_rb);
                    break;
                case "102404":
                    setRadioButtonChecked(1, R.id.address_canting_rb);
                    break;
                case "102405":
                    setRadioButtonChecked(1, R.id.address_shop_rb);
                    break;
                case "102406":
                    setRadioButtonChecked(2, R.id.address_key_area_rb);
                    break;
                case "102407":
                    setRadioButtonChecked(2, R.id.address_key_shop_rb);
                    break;
                case "102408":
                    setRadioButtonChecked(2, R.id.address_parking_rb);
                    break;
            }
        }
        switch (mSex) {
            case "":
                mGenderRg.check(R.id.gender_none_rb);
                break;
            case "100001":
                mGenderRg.check(R.id.gender_male_rb);
                break;
            case "100002":
                mGenderRg.check(R.id.gender_female_rb);
                break;
        }

        switch (mEyeGlass) {
            case "":
                mBodyRg.check(R.id.body_none_rb);
                break;
            case "112801":
                mBodyRg.check(R.id.body_glass_rb);
                break;
            case "-112801":
                mBodyRg.check(R.id.body_noglass_rb);
                break;

        }

        switch (mCameraIdsType) {
            case "":
                mRangeRg.check(R.id.range_all_rb);
                break;
            case "1":
                mRangeRg.check(R.id.range_fav_rb);
                break;
        }

    }

    private void setRadioButtonChecked(int position, int id) {
        switch (position) {
            case 0:
                mAddressRgTwo.clearCheck();
                mAddressRgThree.clearCheck();
                addressRgFour.clearCheck();
                mAddressRgOne.check(id);
                break;
            case 1:
                mAddressRgOne.clearCheck();
                mAddressRgThree.clearCheck();
                addressRgFour.clearCheck();
                mAddressRgTwo.check(id);
                break;
            case 2:
                mAddressRgOne.clearCheck();
                mAddressRgTwo.clearCheck();
                addressRgFour.clearCheck();
                mAddressRgThree.check(id);
                break;
            case 3:
                mAddressRgOne.clearCheck();
                mAddressRgTwo.clearCheck();
                mAddressRgThree.clearCheck();
                addressRgFour.check(id);
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
    public void showMessage(@NonNull String message) {
        ToastUtils.showShort(message);
        if (null != mFaceDepotSrfl && mFaceDepotSrfl.isRefreshing()) {
            mFaceDepotSrfl.setRefreshing(false);
        }
        if (getString(R.string.network_disconnect).equals(message)) {
            if ("0".equals(mPageType)) {
                if (mFaceDepotAdapter.getData().isEmpty()) {
                    showNoNetworkView();
                }
                mFaceDepotAdapter.setEnableLoadMore(false);
                mFaceDepotSrfl.setEnabled(true);
            } else {
                mFaceDepotSrfl.setEnabled(true);
                mFaceDepotAdapter.setEnableLoadMore(true);
                mFaceDepotAdapter.loadMoreFail();
            }
        }
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
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
        mPageType = "0";
        mFaceDepotAdapter.setEnableLoadMore(false);
        mMinId = null;
        mMinCaptureTime = null;
        getFaceDepotData();
    }

    /**
     * 获取人脸图库数据
     */
    private void getFaceDepotData() {
        mPresenter.getFaceDepot(mStartTimeStamp, mEndTimeStamp, SIZE, mCameraTags, noCameraTags, mFaceTags, mEmptyTags, mCameraIds, mMinId, mMinCaptureTime, mPageType);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_time_ll:
                mStartDateDialog.show();
                break;
            case R.id.end_time_ll:
                mEndDateDialog.show();
                break;
            case R.id.clean_tv:
                mAddressRgTwo.clearCheck();
                mAddressRgOne.check(R.id.address_none_rb);
                mEyeGlass = "";
                mTempEyeGlass = "";
                mBodyRg.check(R.id.body_none_rb);
                mSex = "";
                mTempSex = "";
                mGenderRg.check(R.id.gender_none_rb);
                mAreaRg.check(R.id.area_none_rb);

                mStartTimeStamp = System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000;
                mTempStartTimeStamp = mStartTimeStamp;
                mEndTimeTv.setText(TimeUtils.millis2String(mEndTimeStamp, "yyyy.MM.dd HH:mm"));

                mEndTimeStamp = System.currentTimeMillis();
                mTempEndTimeStamp = mEndTimeStamp;
                mEndTimeTv.setText(TimeUtils.millis2String(mEndTimeStamp, "yyyy.MM.dd HH:mm"));
                setPopDismiss();
                mPageType = "0";
                mCameraType = "";
                mIsEnterEndTime = false;
                mFaceDepotSrfl.setRefreshing(true);
                mFaceTitle.setText(R.string.face_depot_title);
                mCameraIds = null;
                mCameraTags = null;
                mFaceTags = null;
                mSexTags = null;
                mEmptyTags = null;
                mTimeChanged = false;
                mCameraIdsType = "";
                mTempCameraIdsType = "";
                mTempPreRangePosition = 0;
                mTempPreGenderPosition = 0;
                mTempPreAddressPosition = 0;
                mRangeEntities.get(mPreRangePosition).setSelect(false);
                mPreRangePosition = mTempPreRangePosition;
                mRangeEntities.get(mPreRangePosition).setSelect(true);
                mGenderEntities.get(mTempPreGenderPosition).setSelect(false);
                mPreGenderPosition = mTempPreGenderPosition;
                mGenderEntities.get(mPreGenderPosition).setSelect(true);
                mAddressEntities.get(mPreAddressPosition).setSelect(false);
                mPreAddressPosition = mTempPreAddressPosition;
                mAddressEntities.get(mPreAddressPosition).setSelect(true);
                showArrowDown(0);
                showArrowDown(1);
                showArrowDown(2);

                rightMenu.dismiss();

                mPageType = "0";
                mFaceDepotSrfl.setRefreshing(true);
                onRefresh();
                break;
            case R.id.cofirm_tv:
                //重新根据条件 获取数据
                //                KeyboardUtils.hideSoftInput(mActivity, mSearchEt);
                if (mTempStartTimeStamp != null && mTempEndTimeStamp != null && mTempStartTimeStamp >= mTempEndTimeStamp) {
                    ToastUtils.showShort("开始时间必须小于结束时间");
                    return;
                }
                setPopDismiss();
                mStartTimeStamp = mTempStartTimeStamp;
                mEndTimeStamp = mTempEndTimeStamp;
                mCameraType = mTempCameraType;
                mSex = mTempSex;
                mEyeGlass = mTempEyeGlass;
                mCameraIdsType = mTempCameraIdsType;
                initNetParams();
                rightMenu.dismiss();
                mStartTimeStamp = mTempStartTimeStamp;
                mEndTimeStamp = mTempEndTimeStamp;
                mRangeEntities.get(mPreRangePosition).setSelect(false);
                mPreRangePosition = mTempPreRangePosition;
                mRangeEntities.get(mPreRangePosition).setSelect(true);
                mGenderEntities.get(mPreGenderPosition).setSelect(false);
                mPreGenderPosition = mTempPreGenderPosition;
                mGenderEntities.get(mPreGenderPosition).setSelect(true);
                mAddressEntities.get(mPreAddressPosition).setSelect(false);
                mPreAddressPosition = mTempPreAddressPosition;
                mAddressEntities.get(mPreAddressPosition).setSelect(true);
                /*if (!TextUtils.isEmpty(mCameraType)) {
                    if (null == mCameraTags) {
                        mCameraTags = new ArrayList<>();
                    }
                    mCameraTags.clear();
                    mCameraTags.add(mCameraType);
                } else {
                    mCameraTags = null;
                }
                if (!TextUtils.isEmpty(mSex)) {
                    if (null == mSexTags) {
                        mSexTags = new ArrayList<>();
                    }
                    mSexTags.clear();
                    mSexTags.add(mSex);
                } else {
                    if (null != mSexTags) {
                        mSexTags.clear();
                    }
                    mFaceTags = null;
                }*/

                showArrowDown(0);
                showArrowDown(1);
                showArrowDown(2);
                showFilterArrow();

                mPageType = "0";
                mIsEnterEndTime = true;
                mFaceDepotSrfl.setRefreshing(true);
                onRefresh();
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.range_all_rb:
                //所有的摄像机
                mTempCameraIdsType = "";
                mTempPreRangePosition = 0;
                break;
            case R.id.range_fav_rb:
                //我关注的摄像机
                mTempCameraIdsType = "1";
                mTempPreRangePosition = 1;
                break;
            case R.id.address_none_rb:
                setRadioButtonChecked(0, R.id.address_none_rb);
                mTempCameraType = "";
                mTempPreAddressPosition = 0;
                break;
            case R.id.address_shequ_rb:
                setRadioButtonChecked(0, R.id.address_shequ_rb);
                mTempCameraType = "102401";
                mTempPreAddressPosition = 1;
                break;
            case R.id.address_wangba_rb:
                setRadioButtonChecked(0, R.id.address_wangba_rb);
                mTempCameraType = "102402";
                mTempPreAddressPosition = 2;
                break;
            case R.id.address_hotel_rb:
                setRadioButtonChecked(1, R.id.address_hotel_rb);
                mTempCameraType = "102403";
                mTempPreAddressPosition = 3;
                break;
            case R.id.address_canting_rb:
                setRadioButtonChecked(1, R.id.address_canting_rb);
                mTempCameraType = "102404";
                mTempPreAddressPosition = 4;
                break;
            case R.id.address_shop_rb:
                setRadioButtonChecked(1, R.id.address_shop_rb);
                mTempCameraType = "102405";
                mTempPreAddressPosition = 5;
                break;
            case R.id.address_key_area_rb:
                setRadioButtonChecked(2, R.id.address_key_area_rb);
                mTempCameraType = "102406";
                mTempPreAddressPosition = 6;
                break;
            case R.id.address_key_shop_rb:
                setRadioButtonChecked(2, R.id.address_key_shop_rb);
                mTempCameraType = "102407";
                mTempPreAddressPosition = 7;
                break;
            case R.id.address_parking_rb:
                setRadioButtonChecked(2, R.id.address_parking_rb);
                mTempCameraType = "102408";
                mTempPreAddressPosition = 8;
                break;
            case R.id.address_other_rb:
                setRadioButtonChecked(3, R.id.address_other_rb);
                mTempCameraType = null;
                mTempPreAddressPosition = 9;
                break;
            case R.id.gender_none_rb:
                //从这里开始是性别
                mTempSex = "";
                mTempPreGenderPosition = 0;
                break;
            case R.id.gender_male_rb:
                mTempSex = "100001";
                mTempPreGenderPosition = 1;
                break;
            case R.id.gender_female_rb:
                mTempSex = "100002";
                mTempPreGenderPosition = 2;
                break;
            case R.id.body_none_rb:
                //人体
                mTempEyeGlass = "";
                break;
            case R.id.body_glass_rb:
                mTempEyeGlass = "112801";
                break;
            case R.id.body_noglass_rb:
                mTempEyeGlass = "-112801";
                break;
        }
    }

    @Override
    public void onLoadMoreRequested() {
        mPageType = "2";
        mFaceDepotSrfl.setEnabled(false);
        getFaceDepotData();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void getFaceDepotSuccess(FaceDepotEntity faceDepotEntity) {
        mFaceDepotSrfl.setRefreshing(false);
        mFaceDepotAdapter.setEnableLoadMore(true);
        List<FaceDepotDetailEntity> list = faceDepotEntity.face;
        if (null != list) {
            //            mFaceDepotAdapter.setSelectText(mSearchEt.getText().toString());
            if ("0".equals(mPageType)) {
                if (list.size() == 0) {
                    showNoDataView();
                } else {
                    showDataView();
                }
                mFaceDepotAdapter.setNewData(list);
                if (list.size() < SIZE) {
                    mFaceDepotAdapter.disableLoadMoreIfNotFullPage(mFaceDepotRclv);
                }
                if (list.size() != 0) {
                    mFaceDepotRclv.scrollToPosition(0);
                }

            } else {
                mFaceDepotSrfl.setEnabled(true);
                mFaceDepotAdapter.addData(list);
                if (list.size() < SIZE) {
                    mFaceDepotAdapter.loadMoreEnd(false);
                } else {
                    mFaceDepotAdapter.loadMoreComplete();
                }
            }
            if (list.size() > 0) {
                FaceDepotDetailEntity minEntity = list.get(list.size() - 1);
                mMinId = minEntity.id;
                mMinCaptureTime = minEntity.captureTime;
            }
        } else {
            if ("0".equals(mPageType)) {
                showNoDataView();
            }
        }
    }

    @Override
    public void getFaceError() {
        mFaceDepotSrfl.setRefreshing(false);
        if ("0".equals(mPageType)) {
            if (mFaceDepotAdapter.getData().isEmpty()) {
                showNoNetworkView();
            }
            mFaceDepotAdapter.setEnableLoadMore(false);
            mFaceDepotSrfl.setEnabled(true);
        } else {
            mFaceDepotSrfl.setEnabled(true);
            mFaceDepotAdapter.loadMoreFail();
            mFaceDepotAdapter.setEnableLoadMore(true);
        }
    }

    @Override
    public void getNoPermission() {
        mFaceDepotSrfl.setRefreshing(false);
        mFaceDepotSrfl.setEnabled(true);
        showNoPermissonView();
    }

    @Override
    public void getCollectionsSuccess(GetKeyStoreBaseEntity organizationEntity) {
        if (null != organizationEntity) {
            GetKeyStoreEntity userKvStroe = organizationEntity.getUserKvStroe();
            if (null != userKvStroe) {
                String storeValue = userKvStroe.getStoreValue();
                if (!TextUtils.isEmpty(storeValue)) {
                    Gson gson = new Gson();
                    SetKeyStoreRequest keyStore = gson.fromJson(storeValue, SetKeyStoreRequest.class);
                    List<String> storeSets = keyStore.getSets();
                    List<CollectCameraEntity> collectCameraList = new ArrayList<>();
                    for (String str : storeSets) {
                        CollectCameraEntity entity = new CollectCameraEntity();
                        String[] group_one = str.split(":");
                        entity.setGroup(group_one[0]);
                        String[] group_two = group_one[1].split("/");
                        entity.setCid(group_two[0]);
                        entity.setCameraName(group_two[1]);
                        collectCameraList.add(entity);
                    }
                    int size = collectCameraList.size();
                    List<CollectCameraEntity> repeatIndexs = new ArrayList<>();
                    for (int i = 0; i < size; i++) {
                        CollectCameraEntity iEntity = collectCameraList.get(i);
                        String iCid = iEntity.getCid();
                        if (i == size - 1) {
                            break;
                        }
                        for (int j = i + 1; j < size; j++) {
                            CollectCameraEntity jEntity = collectCameraList.get(j);
                            if (iCid.equals(jEntity.getCid())) {
                                repeatIndexs.add(jEntity);
                            }
                        }
                    }
                    for (CollectCameraEntity indexEntity : repeatIndexs) {
                        collectCameraList.remove(indexEntity);
                    }

                    mOrgCameraEntities.clear();
                    for (CollectCameraEntity entity : collectCameraList) {
                        List<OrgCameraEntity> cursorEntities = DataSupport.where("manufacturerdeviceid = ?", entity.getCid()).find(OrgCameraEntity.class);
                        if (cursorEntities != null && !cursorEntities.isEmpty()) {
                            OrgCameraEntity cameraEntity = cursorEntities.get(0);
                            mOrgCameraEntities.add(cameraEntity);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void getCollectionsFail() {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TYPE_SELECT_CAMERA:
                    //选择了Camera，或者"",重新刷新
                    String cameraIds = data.getStringExtra("cameraIds");
                    if (!TextUtils.isEmpty(cameraIds)) {
                        String[] split = cameraIds.split(",");
                        mCameraIds = Arrays.asList(split);
                    } else {
                        mCameraIds = null;
                    }
                    mFaceDepotSrfl.setRefreshing(true);
                    onRefresh();
                    break;
            }
        }
    }

    private void showDataView() {
        if (null != mEmptyNoNetworkTv) {
            mEmptyNoNetworkTv.setVisibility(View.GONE);
        }
        if (null != rlEmpty) {
            rlEmpty.setVisibility(View.GONE);
        }
        if (null != mFaceDepotRclv) {
            mFaceDepotRclv.setVisibility(View.VISIBLE);
        }
    }

    private void showNoDataView() {
        mEmptyNoNetworkTv.setVisibility(View.GONE);
        rlEmpty.setVisibility(View.VISIBLE);
        mFaceDepotRclv.setVisibility(View.GONE);
    }

    private void showNoNetworkView() {
        mEmptyNoNetworkTv.setVisibility(View.VISIBLE);
        rlEmpty.setVisibility(View.GONE);
        mFaceDepotRclv.setVisibility(View.GONE);
    }

    private void showNoPermissonView() {
        mEmptyNoNetworkTv.setVisibility(View.GONE);
        rlEmpty.setVisibility(View.GONE);
        mFaceDepotRclv.setVisibility(View.GONE);
    }

    @Subscriber()
    public void modifyList(PictureCollectEvent event) {
        List<FaceDepotDetailEntity> data = mFaceDepotAdapter.getData();
        try {
            data.get(event.position).isCollect = event.isCollected ? "1" : "0";
            data.get(event.position).collectId = event.colleteId;
            mFaceDepotAdapter.notifyItemChanged(event.position);
        } catch (Exception e) {

        }
    }
}
