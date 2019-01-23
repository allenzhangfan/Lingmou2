package cloud.antelope.lingmou.mvp.ui.fragment;

import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.utils.SPUtils;
import com.lingdanet.safeguard.common.utils.TimeUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;
import com.umeng.analytics.MobclickAgent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.common.Constants;
import cloud.antelope.lingmou.di.component.DaggerDailyPoliceComponent;
import cloud.antelope.lingmou.di.module.DailyPoliceModule;
import cloud.antelope.lingmou.mvp.contract.DailyPoliceContract;
import cloud.antelope.lingmou.mvp.model.entity.DailyFilterItemEntity;
import cloud.antelope.lingmou.mvp.model.entity.DailyPoliceAlarmEntity;
import cloud.antelope.lingmou.mvp.model.entity.FaceAlarmBlackRequest;
import cloud.antelope.lingmou.mvp.presenter.DailyPolicePresenter;
import cloud.antelope.lingmou.mvp.ui.activity.BodyDepotActivity;
import cloud.antelope.lingmou.mvp.ui.activity.CloudSearchActivity;
import cloud.antelope.lingmou.mvp.ui.activity.DailyPoliceDetailActivity;
import cloud.antelope.lingmou.mvp.ui.activity.NewMainActivity;
import cloud.antelope.lingmou.mvp.ui.adapter.DailyFilterAdapter;
import cloud.antelope.lingmou.mvp.ui.adapter.DailyPoliceAdapter;
import cloud.antelope.lingmou.mvp.ui.widget.LmDatePickDialog;
import cloud.antelope.lingmou.mvp.ui.widget.LmTimePickerDialog;
import cloud.antelope.lingmou.mvp.ui.widget.MenuView;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class DailyPoliceFragment extends BaseFragment<DailyPolicePresenter> implements DailyPoliceContract.View,
        SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener,
        RadioGroup.OnCheckedChangeListener {

    private static final long SEVEN_DAY_STAMP = 7 * 24 * 60 * 60 * 1000;

    private static final int ITEM_CLICK_POSITION = 0x01;

    @BindView(R.id.daily_police_rclv)
    RecyclerView mDailyPoliceRclv;
    @BindView(R.id.daily_police_srfl)
    SwipeRefreshLayout mDailyPoliceSrfl;
    @BindView(R.id.shaixuan_ll)
    LinearLayout mShaixuanLl;
    @BindView(R.id.filter_ll)
    LinearLayout mFilterLl;
    @BindView(R.id.filter_rclv)
    RecyclerView mFilterRclv;
    @BindView(R.id.filter_range_ll)
    LinearLayout mFilterRangeLl;
    @BindView(R.id.filter_type_ll)
    LinearLayout mFilterTypeLl;
    @BindView(R.id.filter_state_ll)
    LinearLayout mFilterStateLl;

    @BindView(R.id.range_all_rb)
    RadioButton mRangeAllRb;
    @BindView(R.id.range_collect_rb)
    RadioButton mRangeCollectRb;
    @BindView(R.id.range_radio_group)
    RadioGroup mRangeRadioGroup;
    //    @BindView(R.id.type_all_rb)
//    RadioButton mTypeAllRb;
//    @BindView(R.id.type_control_rb)
//    RadioButton mTypeControlRb;
//    @BindView(R.id.type_keyperson_rb)
//    RadioButton mTypeKeypersonRb;
//    @BindView(R.id.type_radio_group_one)
//    RadioGroup mTypeRadioGroupOne;
    @BindView(R.id.state_all_rb)
    RadioButton mStateAllRb;
    @BindView(R.id.state_undo_rb)
    RadioButton mStateUndoRb;
    @BindView(R.id.state_valid_rb)
    RadioButton mStateValidRb;
    @BindView(R.id.state_radio_group_one)
    RadioGroup mStateRadioGroupOne;
    @BindView(R.id.state_invalid_rb)
    RadioButton mStateInvalidRb;
    @BindView(R.id.state_radio_group_two)
    RadioGroup mStateRadioGroupTwo;
    @BindView(R.id.alarm_time_start_tv)
    TextView mAlarmTimeStartTv;
    @BindView(R.id.alarm_time_end_tv)
    TextView mAlarmTimeEndTv;
    @BindView(R.id.reset_tv)
    TextView mResetTv;
    @BindView(R.id.save_tv)
    TextView mSaveTv;
    @BindView(R.id.root_filter_fl)
    FrameLayout mRootFilterFl;
    @BindView(R.id.filter_tv)
    TextView mFilterTv;
    @BindView(R.id.title_alarm_range_tv)
    TextView mTitleAlarmRangeTv;
    @BindView(R.id.title_alarm_type_tv)
    TextView mTitleAlarmTypeTv;
    @BindView(R.id.title_alarm_state_tv)
    TextView mTitleAlarmStateTv;
    @BindView(R.id.empty_tv)
    TextView mEmptyTv;
    @BindView(R.id.tv_search)
    TextView tvSearch;
    @BindView(R.id.rv_type)
    RecyclerView rvType;

    @Inject
    DailyFilterAdapter mDailyFilterAdapter;
    @Inject
    DailyPoliceAdapter mDailyPoliceAdapter;
    @Inject
    @Named("PoliceItemAnimator")
    RecyclerView.ItemAnimator mItemAnimator;
    @Inject
    @Named("FilterItemAnimator")
    RecyclerView.ItemAnimator mFilterItemAnimator;
    @Inject
    LoadMoreView mLoadMoreView;

    private LmDatePickDialog mStartDateDialog;
    private LmDatePickDialog mEndDateDialog;
    private LmTimePickerDialog mStartTimeDialog, mEndTimeDialog;

    private List<DailyFilterItemEntity> mRangeEntities;
    private List<DailyFilterItemEntity> mTypeEntities;
    private List<DailyFilterItemEntity> mStateEntities;

    private int mStartYear, mStartMonth, mStartDay, mStartHour, mStartMinute;
    private int mEndYear, mEndMonth, mEndDay, mEndHour, mEndMinute;
    private Long mEndTimeStamp = null;
    private Long mStartTimeStamp = null;
    private Long mTempStartTimeStamp = null;
    private Long mTempEndTimeStamp = null;
    private Integer mAlarmScope = 1;
    private Integer mTempAlarmScope = 1;
    private Integer mAlarmTaskType = -1;
    private Integer mTempAlarmTaskType = -1;
    private Integer mAlarmOperationType = -1;
    private Integer mTempAlarmOperationState = -1;

    private int mPosition;

    private int mPreRangePosition;
    private int mPreTypePosition;
    private int mPreStatePosition;
    private int mTempPreRangePosition;
    private int mTempPreTypePosition;
    private int mTempPreStatePosition;

    private Drawable mFilterArrowDownYellowDrawable, mFilterArrowDownBlackDrawable,
            mFilterArrowUpYellowDrawable, mFilterArrowUpBlackDrawable,
            mFilterBlackDrawable, mFilterYellowDrawable;

    private boolean mTimeChanged;

    private static DailyPoliceFragment mPoliceFragment;
    private static DailyPoliceFragment instance;
    //    private Animation mTopOut;
//    private Animation mTopIn;
    private Animation mRightIn;
    private Animation mRightOut;

    private int mPageNo = 1;
    private final static int PAGE_SIZE = 10;
    private MenuView menuView;

    private boolean mIsEnterEndTime;
    private ArrayList<DailyFilterItemEntity> tempList;


    public static DailyPoliceFragment newInstance() {
        mPoliceFragment = new DailyPoliceFragment();
        return mPoliceFragment;
    }

    public static DailyPoliceFragment getInstance() {
        return instance;
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerDailyPoliceComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .dailyPoliceModule(new DailyPoliceModule(this))
                .build()
                .inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_daily_police, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        instance = this;


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

        mDailyPoliceSrfl.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        mDailyPoliceSrfl.setOnRefreshListener(this);

        mDailyPoliceRclv.setAdapter(mDailyPoliceAdapter);
        mDailyPoliceRclv.setHasFixedSize(false);
        mDailyPoliceRclv.setLayoutManager(new LinearLayoutManager(getActivity()));
        mDailyPoliceRclv.setItemAnimator(mItemAnimator);


        mFilterRclv.setLayoutManager(new GridLayoutManager(getActivity(), 3));
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

        mDailyPoliceAdapter.setOnLoadMoreListener(this, mDailyPoliceRclv);
        mDailyPoliceAdapter.setLoadMoreView(mLoadMoreView);

        initFilterData();
        // testData();

        initListener();
        mEndTimeStamp = System.currentTimeMillis();
        mTempEndTimeStamp = mEndTimeStamp;

        mStartTimeStamp = System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000L;
        mTempStartTimeStamp = mStartTimeStamp;

        mStartDateDialog = new LmDatePickDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
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

        mEndDateDialog = new LmDatePickDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
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

        mDailyPoliceRclv.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDailyPoliceSrfl.setRefreshing(true);
                onRefresh();
            }
        }, 150);
    }

    private void setDateStr(String str, boolean isStartTv) {
        String[] dateTime = str.split(" ");
        String[] dateSplit = dateTime[0].split("-");
        if (!isStartTv) {
            mEndYear = Integer.parseInt(dateSplit[0]);
            mEndMonth = Integer.parseInt(dateSplit[1]) - 1;
            mEndDay = Integer.parseInt(dateSplit[2]);
            mEndDateDialog.updateDate(mEndYear, mEndMonth, mEndDay);
            mAlarmTimeEndTv.setText(Html.fromHtml("<font color = \"#424242\">结束时间</font><br><font color = \"#FF8F00\">" + str.substring(0, str.length() - 3) + "</font>"));
        } else {
            mStartYear = Integer.parseInt(dateSplit[0]);
            mStartMonth = Integer.parseInt(dateSplit[1]) - 1;
            mStartDay = Integer.parseInt(dateSplit[2]);
            mStartDateDialog.updateDate(mStartYear, mStartMonth, mStartDay);
            mAlarmTimeStartTv.setText(Html.fromHtml("<font color = \"#424242\">开始时间</font><br><font color = \"#FF8F00\">" + str.substring(0, str.length() - 3) + "</font>"));
        }
    }

    private void initEndTimeDialog() {
        mEndTimeDialog = new LmTimePickerDialog(getActivity(), 0, (view, hourOfDay, minute) -> {
            mEndHour = hourOfDay;
            mEndMinute = minute;
            Long entTimeStamp = TimeUtils.string2Millis(mEndYear + "-" + mEndMonth + "-" + mEndDay + " " + mEndHour + ":" + mEndMinute, new SimpleDateFormat("yyyy-MM-dd HH:mm"));
            String timeStr = TimeUtils.millis2String(entTimeStamp);
            //                mClothEndTimeTv.setText(mEndYear + "." + mEndMonth + "." + mEndDay + " " + mEndHour + ":" + mEndMinute);
            if (entTimeStamp - mTempStartTimeStamp <= 0) {
                ToastUtils.showShort(R.string.hint_end_day_less_start_day);
            }
            /*else if (entTimeStamp - mTempStartTimeStamp > SEVEN_DAY_STAMP) {
                ToastUtils.showShort(R.string.hint_not_more_seven_days);
            }*/
            else {
                mTempEndTimeStamp = entTimeStamp;
                setDateStr(timeStr, false);
                mEndDateDialog.dismiss();
                mAlarmTimeEndTv.setBackground(getResources().getDrawable(R.drawable.shape_rect_corner_5_fff8e1));
                mTimeChanged = true;
            }
        }, mEndHour == 0 ? Calendar.getInstance().get(Calendar.HOUR_OF_DAY) : mEndHour,
                mEndMinute == 0 ? Calendar.getInstance().get(Calendar.MINUTE) : mEndMinute, true);
        mEndTimeDialog.show();
    }


    private void initStartTimeDialog() {
        mStartTimeDialog = new LmTimePickerDialog(getActivity(), 0, (view, hourOfDay, minute) -> {
            mStartHour = hourOfDay;
            mStartMinute = minute;
            Long startTime = TimeUtils.string2Millis(mStartYear + "-" + mStartMonth + "-" + mStartDay + " " + mStartHour + ":" + mStartMinute, new SimpleDateFormat("yyyy-MM-dd HH:mm"));
            String timeStr = TimeUtils.millis2String(startTime);
            if (mTempEndTimeStamp - startTime <= 0) {
                ToastUtils.showShort(R.string.hint_end_day_less_start_day);
            }
            /*else if (mTempEndTimeStamp - startTime > SEVEN_DAY_STAMP) {
                ToastUtils.showShort(R.string.hint_not_more_seven_days);
            } */
            else {
                mTempStartTimeStamp = startTime;
                setDateStr(timeStr, true);
                mStartDateDialog.dismiss();
                mAlarmTimeStartTv.setBackground(getResources().getDrawable(R.drawable.shape_rect_corner_5_fff8e1));
                mTimeChanged = true;
            }
        }, mStartHour == 0 ? Calendar.getInstance().get(Calendar.HOUR_OF_DAY) : mStartHour,
                mStartMinute == 0 ? Calendar.getInstance().get(Calendar.MINUTE) : mStartMinute, true);
        mStartTimeDialog.show();
    }

    private void initListener() {
        mRangeRadioGroup.setOnCheckedChangeListener(this);
        mStateRadioGroupOne.setOnCheckedChangeListener(this);
        mStateRadioGroupTwo.setOnCheckedChangeListener(this);
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
                            mAlarmScope = 1;
                            break;
                        case 1:
                            mAlarmScope = 2;
                            break;
                    }
                } else if (mPosition == 1) {
                    mTypeEntities.get(mPreTypePosition).setSelect(false);
                    mPreTypePosition = position;
                    mTypeEntities.get(position).setSelect(true);
                    showArrowDown(1);

                    // 101501:重点人员布控  101502:外来人员布控  101503:魅影报警  101504:一体机报警  101505:临控报警
                    if (TextUtils.equals(getString(R.string.all), mTypeEntities.get(position).getName())) {
                        mAlarmTaskType = -1;
                    } else if (TextUtils.equals(getString(R.string.person_tracking), mTypeEntities.get(position).getName())) {
                        mAlarmTaskType = 101505;
                    } else if (TextUtils.equals(getString(R.string.control_key_person), mTypeEntities.get(position).getName())) {
                        mAlarmTaskType = 101501;
                    } else if (TextUtils.equals(getString(R.string.outside_person), mTypeEntities.get(position).getName())) {
                        mAlarmTaskType = 101502;
                    } else if (TextUtils.equals(getString(R.string.private_network_suite), mTypeEntities.get(position).getName())) {
                        mAlarmTaskType = 101504;
                    }
                } else if (mPosition == 2) {
                    mStateEntities.get(mPreStatePosition).setSelect(false);
                    mPreStatePosition = position;
                    mStateEntities.get(position).setSelect(true);
                    showArrowDown(2);
                    switch (position) {
                        case 0:
                            mAlarmOperationType = -1;
                            break;
                        case 1:
                            mAlarmOperationType = 1;
                            break;
                        case 2:
                            mAlarmOperationType = 2;
                            break;
                        case 3:
                            mAlarmOperationType = 3;
                            break;
                    }
                }
                menuView.dismiss();
//                mShaixuanLl.setVisibility(View.GONE);
//                mShaixuanLl.startAnimation(mTopOut);
//                NewMainActivity newMainActivity = (NewMainActivity) getActivity();
//                newMainActivity.setBottomVisible(true);
                mDailyPoliceSrfl.setRefreshing(true);
                onRefresh();
            }
        });

        mDailyPoliceAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MobclickAgent.onEvent(getContext(), "alarmDetail");
                DailyPoliceAlarmEntity item = (DailyPoliceAlarmEntity) adapter.getItem(position);
                Intent intent = new Intent(getActivity(), DailyPoliceDetailActivity.class);
                intent.putExtra("entity", item);
                intent.putExtra("position", position);
                startActivityForResult(intent, ITEM_CLICK_POSITION);
            }
        });
    }

    private void initFilterData() {
        mRangeEntities = new ArrayList<>();
        mTypeEntities = new ArrayList<>();
        mStateEntities = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            DailyFilterItemEntity entity = new DailyFilterItemEntity();
            if (i == 0) {
                entity.setSelect(true);
                entity.setName("全部");
            } else {
                entity.setSelect(false);
                entity.setName("收藏");
            }
            mRangeEntities.add(entity);
        }
        SPUtils utils = SPUtils.getInstance();
        mTypeEntities.add(new DailyFilterItemEntity(getString(R.string.all), true));
        mTypeEntities.add(new DailyFilterItemEntity(getString(R.string.person_tracking), false));
        if (utils.getBoolean(Constants.PERMISSION_KEY_PERSON)) {
            mTypeEntities.add(new DailyFilterItemEntity(getString(R.string.control_key_person), false));
        }
        if (utils.getBoolean(Constants.PERMISSION_OUTSIDE_PERSON)) {
            mTypeEntities.add(new DailyFilterItemEntity(getString(R.string.outside_person), false));
        }
        if (utils.getBoolean(Constants.PERMISSION_PRIVATE_NETWORK_SUITE)) {
            mTypeEntities.add(new DailyFilterItemEntity(getString(R.string.private_network_suite), false));
        }
        rvType.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        tempList = new ArrayList<>();
        tempList.addAll(mTypeEntities);
        rvType.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int position = parent.getChildLayoutPosition(view);
                outRect.left = position % 3 == 0 ? getResources().getDimensionPixelSize(R.dimen.dp16) : getResources().getDimensionPixelSize(R.dimen.dp8);
                outRect.right = position % 3 == 2 ? getResources().getDimensionPixelSize(R.dimen.dp16) : getResources().getDimensionPixelSize(R.dimen.dp8);
                outRect.bottom = position < 3 ? getResources().getDimensionPixelSize(R.dimen.dp16) : 0;
            }
        });
        rvType.setAdapter(new BaseQuickAdapter<DailyFilterItemEntity, BaseViewHolder>(R.layout.item_daily_filter_right, tempList) {
            @Override
            protected void convert(BaseViewHolder helper, DailyFilterItemEntity item) {
                TextView tv = helper.getView(R.id.tv);
                tv.setText(item.getName());
                tv.setSelected(item.isSelect());
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 101501:重点人员布控  101502:外来人员布控  101503:魅影报警  101504:一体机报警  101505:临控报警
                        if (TextUtils.equals(getString(R.string.all), item.getName())) {
                            mTempAlarmTaskType = -1;


                            mTempPreTypePosition = 0;
                        } else if (TextUtils.equals(getString(R.string.person_tracking), item.getName())) {
                            mTempAlarmTaskType = 101505;
                            mTempPreTypePosition = 1;
                        } else if (TextUtils.equals(getString(R.string.control_key_person), item.getName())) {
                            mTempAlarmTaskType = 101501;
                            mTempPreTypePosition = 2;
                        } else if (TextUtils.equals(getString(R.string.outside_person), item.getName())) {
                            mTempAlarmTaskType = 101502;
                            mTempPreTypePosition = 3;
                        } else if (TextUtils.equals(getString(R.string.private_network_suite), item.getName())) {
                            mTempAlarmTaskType = 101504;
                            mTempPreTypePosition = 4;
                        }
                        for (int i = 0; i < getData().size(); i++) {
                            if (i == helper.getAdapterPosition()) {
                                getData().get(i).setSelect(true);
                            } else {
                                getData().get(i).setSelect(false);
                            }
                        }
                        notifyDataSetChanged();
                    }
                });
            }
        });

        for (int i = 0; i < 4; i++) {
            DailyFilterItemEntity entity = new DailyFilterItemEntity();
            if (i == 0) {
                entity.setSelect(true);
                entity.setName("全部");
            } else if (i == 1) {
                entity.setSelect(false);
                entity.setName("待处理");
            } else if (i == 2) {
                entity.setSelect(false);
                entity.setName("有效");
            } else {
                entity.setSelect(false);
                entity.setName("无效");
            }
            mStateEntities.add(entity);
        }
    }

    private void testData() {
        List<DailyPoliceAlarmEntity> alarmEntityList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            DailyPoliceAlarmEntity entity = new DailyPoliceAlarmEntity();
            entity.setCaptureTime(String.valueOf(new Random(10000).nextLong() + System.currentTimeMillis() / 1000));
            entity.setFacePath("http://pic1.16pic.com/00/38/47/16pic_3847497_b.jpg");
            entity.setImageUrl("http://img1.imgtn.bdimg.com/it/u=2166549022,2948101365&fm=27&gp=0.jpg");
            entity.setCameraName("保利国际中心" + i);
            entity.setIsHandle(i / 2);
            entity.setIsEffective(i / 2);
            entity.setSimilarity((i + 1) * 9.9d);
            entity.setTaskName("这是布控任务" + i);
            alarmEntityList.add(entity);
        }
        mDailyPoliceAdapter.setNewData(alarmEntityList);
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
    public Fragment getFragmentContext() {
        return this;
    }

    @Override
    public void getAlarmsSuccess(List<DailyPoliceAlarmEntity> list) {
        mDailyPoliceSrfl.setRefreshing(false);
        mDailyPoliceAdapter.setEnableLoadMore(true);
        if (list != null) {
            if (1 == mPageNo) {
                if (list.size() == 0) {
                    //展示无数据页面
                    mEmptyTv.setVisibility(View.VISIBLE);
                    mDailyPoliceRclv.setVisibility(View.GONE);
                } else {
                    //展示数据页面
                    mEmptyTv.setVisibility(View.GONE);
                    mDailyPoliceRclv.setVisibility(View.VISIBLE);
                }
                mDailyPoliceAdapter.setNewData(list);
                if (list.size() < PAGE_SIZE) {
                    mDailyPoliceAdapter.disableLoadMoreIfNotFullPage(mDailyPoliceRclv);
                }
                if (list.size() != 0) {
                    mDailyPoliceRclv.scrollToPosition(0);
                }
            } else {
                mDailyPoliceSrfl.setEnabled(true);
                mDailyPoliceAdapter.addData(list);
                if (list.size() < PAGE_SIZE) {
                    mDailyPoliceAdapter.loadMoreEnd(false);
                } else {
                    mDailyPoliceAdapter.loadMoreComplete();
                }
            }
        } else {
            if (1 == mPageNo) {
                //展示无数据页
                mEmptyTv.setVisibility(View.VISIBLE);
                mDailyPoliceRclv.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void getAlarmFail() {
        if (1 == mPageNo) {
            //展示无数据页
            mEmptyTv.setVisibility(View.VISIBLE);
            mDailyPoliceRclv.setVisibility(View.GONE);
            mDailyPoliceSrfl.setRefreshing(false);
        }
    }

    @Override
    public void onRefresh() {
        if (!mIsEnterEndTime) {
            mEndTimeStamp = System.currentTimeMillis();
            mTempEndTimeStamp = mEndTimeStamp;
            setDateStr(TimeUtils.millis2String(mEndTimeStamp), false);
        }
        mDailyPoliceAdapter.setEnableLoadMore(false);
        mPageNo = 1;

        FaceAlarmBlackRequest request = new FaceAlarmBlackRequest();
        request.pageNo = mPageNo;
        request.pageSize = PAGE_SIZE;
        request.alarmScope = mAlarmScope;
        if (mAlarmTaskType != null && -1 != mAlarmTaskType) {
            request.taskType = mAlarmTaskType;
            request.taskTypeList.add(Long.valueOf(mAlarmTaskType));
        } else {
            SPUtils utils = SPUtils.getInstance();
            if (utils.getBoolean(Constants.PERMISSION_KEY_PERSON)) {
                request.taskTypeList.add(101501L);
            }
            request.taskTypeList.add(101502L);
            if (utils.getBoolean(Constants.PERMISSION_OUTSIDE_PERSON)) {
                request.taskTypeList.add(101505L);
            }
            if (utils.getBoolean(Constants.PERMISSION_PRIVATE_NETWORK_SUITE)) {
                request.taskTypeList.add(101504L);
            }
        }
        if (-1 != mAlarmOperationType) {
            request.alarmOperationType = mAlarmOperationType;
        }
        if (null != mStartTimeStamp) {
            request.startTime = mStartTimeStamp;
        }
        if (null != mEndTimeStamp) {
            request.endTime = mEndTimeStamp;
        }


        mPresenter.getAlarms(request);
    }

    @Override
    public void onLoadMoreRequested() {
        mPageNo++;
        mDailyPoliceRclv.setEnabled(false);
        FaceAlarmBlackRequest request = new FaceAlarmBlackRequest();
        request.pageNo = mPageNo;
        request.pageSize = PAGE_SIZE;
        request.alarmScope = mAlarmScope;
        if (mAlarmTaskType != null && -1 != mAlarmTaskType) {
            request.taskType = mAlarmTaskType;
            request.taskTypeList.add(Long.valueOf(mAlarmTaskType));
        } else {
            SPUtils utils = SPUtils.getInstance();
            if (utils.getBoolean(Constants.PERMISSION_KEY_PERSON)) {
                request.taskTypeList.add(101501L);
            }
            request.taskTypeList.add(101502L);
            if (utils.getBoolean(Constants.PERMISSION_OUTSIDE_PERSON)) {
                request.taskTypeList.add(101505L);
            }
            if (utils.getBoolean(Constants.PERMISSION_PRIVATE_NETWORK_SUITE)) {
                request.taskTypeList.add(101504L);
            }
        }
        if (-1 != mAlarmOperationType) {
            request.alarmOperationType = mAlarmOperationType;
        }
        if (null != mStartTimeStamp) {
            request.startTime = mStartTimeStamp;
        }
        if (null != mEndTimeStamp) {
            request.endTime = mEndTimeStamp;
        }
        mPresenter.getAlarms(request);
    }

    private void showArrowUp(int typePosition) {
        switch (typePosition) {
            case 0:
                //range
                if (mPreRangePosition == 0) {
                    mTitleAlarmRangeTv.setCompoundDrawables(null, null, mFilterArrowUpBlackDrawable, null);
                } else {
                    mTitleAlarmRangeTv.setCompoundDrawables(null, null, mFilterArrowUpYellowDrawable, null);
                }
                break;
            case 1:
                //type
                if (mPreTypePosition == 0) {
                    mTitleAlarmTypeTv.setCompoundDrawables(null, null, mFilterArrowUpBlackDrawable, null);
                } else {
                    mTitleAlarmTypeTv.setCompoundDrawables(null, null, mFilterArrowUpYellowDrawable, null);
                }
                break;
            case 2:
                //state
                if (mPreStatePosition == 0) {
                    mTitleAlarmStateTv.setCompoundDrawables(null, null, mFilterArrowUpBlackDrawable, null);
                } else {
                    mTitleAlarmStateTv.setCompoundDrawables(null, null, mFilterArrowUpYellowDrawable, null);
                }
                break;
        }
    }


    private void showArrowDown(int typePosition) {
        switch (typePosition) {
            case 0:
                //range
                if (mPreRangePosition == 0) {
                    //第一个，全部，则颜色变回黑色
                    mTitleAlarmRangeTv.setText(R.string.alarm_range);
                    mTitleAlarmRangeTv.setTextColor(getResources().getColor(R.color.gray_212121));
                    mTitleAlarmRangeTv.setCompoundDrawables(null, null, mFilterArrowDownBlackDrawable, null);
                } else {
                    mTitleAlarmRangeTv.setText(mRangeEntities.get(mPreRangePosition).getName());
                    mTitleAlarmRangeTv.setTextColor(getResources().getColor(R.color.yellow_ff8f00));
                    mTitleAlarmRangeTv.setCompoundDrawables(null, null, mFilterArrowDownYellowDrawable, null);
                }
                break;
            case 1:
                //type
                if (mPreTypePosition == 0) {
                    //第一个，全部，则颜色变回黑色
                    mTitleAlarmTypeTv.setText(R.string.alarm_type);
                    mTitleAlarmTypeTv.setTextColor(getResources().getColor(R.color.gray_212121));
                    mTitleAlarmTypeTv.setCompoundDrawables(null, null, mFilterArrowDownBlackDrawable, null);
                } else {
                    mTitleAlarmTypeTv.setText(mTypeEntities.get(mPreTypePosition).getName());
                    mTitleAlarmTypeTv.setTextColor(getResources().getColor(R.color.yellow_ff8f00));
                    mTitleAlarmTypeTv.setCompoundDrawables(null, null, mFilterArrowDownYellowDrawable, null);
                }
                break;
            case 2:
                //state
                if (mPreStatePosition == 0) {
                    //第一个，全部，则颜色变回黑色
                    mTitleAlarmStateTv.setText(R.string.alarm_state);
                    mTitleAlarmStateTv.setTextColor(getResources().getColor(R.color.gray_212121));
                    mTitleAlarmStateTv.setCompoundDrawables(null, null, mFilterArrowDownBlackDrawable, null);
                } else {
                    mTitleAlarmStateTv.setText(mStateEntities.get(mPreStatePosition).getName());
                    mTitleAlarmStateTv.setTextColor(getResources().getColor(R.color.yellow_ff8f00));
                    mTitleAlarmStateTv.setCompoundDrawables(null, null, mFilterArrowDownYellowDrawable, null);
                }
                break;
        }
    }

    private void showFilterArrow() {
        if (mTimeChanged) {
            mFilterTv.setTextColor(getResources().getColor(R.color.yellow_ff8f00));
            mFilterTv.setCompoundDrawables(mFilterYellowDrawable, null, null, null);
        } else {
            mFilterTv.setTextColor(getResources().getColor(R.color.gray_212121));
            mFilterTv.setCompoundDrawables(mFilterBlackDrawable, null, null, null);
        }
    }

    @OnClick({R.id.tv_search, R.id.filter_range_ll, R.id.filter_type_ll, R.id.filter_state_ll, R.id.filter_ll,
            R.id.alarm_time_start_tv, R.id.alarm_time_end_tv, R.id.reset_tv, R.id.save_tv, R.id.shaixuan_ll})
    public void onViewClicked(View view) {
        int visibility = mShaixuanLl.getVisibility();
        int filterVisibility = mRootFilterFl.getVisibility();
        NewMainActivity newMainActivity = (NewMainActivity) getActivity();
        switch (view.getId()) {
            case R.id.tv_search:
                if (isShaixuanVisible()) {
                    NewMainActivity mainActivity = (NewMainActivity) getActivity();
                    mainActivity.setBottomVisible(true);
                    setShaixuanGone();
                }
                Intent intent = new Intent(getActivity(), CloudSearchActivity.class);
                intent.putExtra("type", "alarm");
                ActivityOptions activityOptions = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    activityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity(), tvSearch, "searchText");
                    startActivity(intent, activityOptions.toBundle());
                } else {
                    startActivity(intent);
                }

                break;
            case R.id.filter_range_ll:
                mRootFilterFl.setVisibility(View.GONE);
                mFilterRclv.setVisibility(View.VISIBLE);
                mDailyFilterAdapter.setNewData(mRangeEntities);
                menuView = new MenuView(getActivity(), mFilterRclv, getViewHeight(mRangeEntities));
                menuView.show(mFilterRangeLl);
                menuView.setOnDismissListener(new MenuView.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        mFilterRclv.setVisibility(View.INVISIBLE);
                        mFilterRclv.setPadding(0, 0, 0, 0);
                        showArrowDown(mPosition);
                    }
                });
                if (visibility == View.VISIBLE) {
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
                    mRootFilterFl.setVisibility(View.GONE);
                    mFilterRclv.setVisibility(View.VISIBLE);
                    if (mPosition != 0) {
                        showArrowUp(0);
                        mDailyFilterAdapter.setNewData(mRangeEntities);
                        showArrowDown(mPosition);

                    } else {
                        newMainActivity.setBottomVisible(true);
                        mShaixuanLl.startAnimation(mTopOut);
                        mShaixuanLl.setVisibility(View.GONE);
                        showArrowDown(0);
                    }
                } else {
                    mRootFilterFl.setVisibility(View.GONE);
                    mFilterRclv.setVisibility(View.VISIBLE);
                    mDailyFilterAdapter.setNewData(mRangeEntities);
                    newMainActivity.setBottomVisible(false);
                    mShaixuanLl.setVisibility(View.VISIBLE);
                    mShaixuanLl.startAnimation(mTopIn);
                    showArrowUp(0);
                }
                mPosition = 0;*/
                break;
            case R.id.filter_type_ll:
                mRootFilterFl.setVisibility(View.GONE);
                mFilterRclv.setVisibility(View.VISIBLE);
                mDailyFilterAdapter.setNewData(mTypeEntities);
                menuView = new MenuView(getActivity(), mFilterRclv, getViewHeight(mTypeEntities));
                menuView.show(mFilterRangeLl);
                menuView.setOnDismissListener(new MenuView.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        mFilterRclv.setVisibility(View.INVISIBLE);
                        mFilterRclv.setPadding(0, 0, 0, 0);
                        showArrowDown(mPosition);
                    }
                });
                if (visibility == View.VISIBLE) {
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

                /*if (visibility == View.VISIBLE) {
                    mRootFilterFl.setVisibility(View.GONE);
                    mFilterRclv.setVisibility(View.VISIBLE);
                    if (mPosition != 1) {
                        showArrowUp(1);
                        mDailyFilterAdapter.setNewData(mTypeEntities);
                        showArrowDown(mPosition);
                    } else {
                        newMainActivity.setBottomVisible(true);
                        mShaixuanLl.startAnimation(mTopOut);
                        mShaixuanLl.setVisibility(View.GONE);
                        showArrowDown(1);
                    }
                } else {
                    mRootFilterFl.setVisibility(View.GONE);
                    mFilterRclv.setVisibility(View.VISIBLE);
                    mDailyFilterAdapter.setNewData(mTypeEntities);
                    newMainActivity.setBottomVisible(false);
                    mShaixuanLl.setVisibility(View.VISIBLE);
                    mShaixuanLl.startAnimation(mTopIn);
                    showArrowUp(1);
                }
                mPosition = 1;*/
                break;
            case R.id.filter_state_ll:
                mRootFilterFl.setVisibility(View.GONE);
                mFilterRclv.setVisibility(View.VISIBLE);
                mDailyFilterAdapter.setNewData(mStateEntities);
                menuView = new MenuView(getActivity(), mFilterRclv, getViewHeight(mStateEntities));
                menuView.setOnDismissListener(new MenuView.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        showArrowDown(mPosition);
                    }
                });
                menuView.show(mFilterRangeLl);
                if (visibility == View.VISIBLE) {
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

                /*if (visibility == View.VISIBLE) {
                    mRootFilterFl.setVisibility(View.GONE);
                    mFilterRclv.setVisibility(View.VISIBLE);
                    if (mPosition != 2) {
                        showArrowUp(2);
                        mDailyFilterAdapter.setNewData(mStateEntities);
                        showArrowDown(mPosition);
                    } else {
                        newMainActivity.setBottomVisible(true);
                        mShaixuanLl.startAnimation(mTopOut);
                        mShaixuanLl.setVisibility(View.GONE);
                        showArrowDown(2);
                    }
                } else {
                    mRootFilterFl.setVisibility(View.GONE);
                    mFilterRclv.setVisibility(View.VISIBLE);
                    mDailyFilterAdapter.setNewData(mStateEntities);
                    newMainActivity.setBottomVisible(false);
                    mShaixuanLl.startAnimation(mTopIn);
                    mShaixuanLl.setVisibility(View.VISIBLE);
                    showArrowUp(2);
                }
                mPosition = 2;*/
                break;
            case R.id.filter_ll:
                if (visibility == View.VISIBLE) {
                    if (filterVisibility == View.VISIBLE) {
                        mShaixuanLl.startAnimation(mRightOut);
                        mRightOut.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                mShaixuanLl.setVisibility(View.GONE);
                                newMainActivity.setBottomVisible(true);
                                mRootFilterFl.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    } else {
                        mFilterRclv.setVisibility(View.GONE);
                        restoreFilterData();
                        mRootFilterFl.setVisibility(View.VISIBLE);
                        showArrowDown(mPosition);
                    }
                } else {
                    mShaixuanLl.startAnimation(mRightIn);
                    newMainActivity.setBottomVisible(false);
                    mShaixuanLl.setVisibility(View.VISIBLE);
                    mFilterRclv.setVisibility(View.GONE);
                    restoreFilterData();
                    mRootFilterFl.setVisibility(View.VISIBLE);
                }
                mPosition = 3;
                break;
            case R.id.alarm_time_start_tv:
                mStartDateDialog.show();
                break;
            case R.id.alarm_time_end_tv:
                mEndDateDialog.show();
                break;
            case R.id.reset_tv:
                mShaixuanLl.startAnimation(mRightOut);
                mRightOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        newMainActivity.setBottomVisible(true);
                        mShaixuanLl.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                mAlarmScope = 1;
                mAlarmTaskType = -1;
                mAlarmOperationType = -1;
                mEndTimeStamp = System.currentTimeMillis();
                mStartTimeStamp = mEndTimeStamp - 30 * 24 * 60 * 60 * 1000L;
                mTempPreRangePosition = 0;
                mTempPreTypePosition = 0;
                mTempPreStatePosition = 0;
                mRangeEntities.get(mPreRangePosition).setSelect(false);
                mPreRangePosition = mTempPreRangePosition;
                mRangeEntities.get(mPreRangePosition).setSelect(true);
                mTypeEntities.get(mPreTypePosition).setSelect(false);
                mPreTypePosition = mTempPreTypePosition;
                mTypeEntities.get(mPreTypePosition).setSelect(true);
                mStateEntities.get(mPreStatePosition).setSelect(false);
                mPreStatePosition = mTempPreStatePosition;
                mStateEntities.get(mPreStatePosition).setSelect(true);
                showArrowDown(0);
                showArrowDown(1);
                showArrowDown(2);
                mTimeChanged = false;
                mIsEnterEndTime = false;
                showFilterArrow();
                mDailyPoliceSrfl.setRefreshing(true);
                onRefresh();
                break;
            case R.id.save_tv:
                mShaixuanLl.startAnimation(mRightOut);
                mRightOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        newMainActivity.setBottomVisible(true);
                        mShaixuanLl.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                mAlarmScope = mTempAlarmScope;
                mAlarmTaskType = mTempAlarmTaskType;
                mAlarmOperationType = mTempAlarmOperationState;
                mStartTimeStamp = mTempStartTimeStamp;
                mEndTimeStamp = mTempEndTimeStamp;
                mRangeEntities.get(mPreRangePosition).setSelect(false);
                mPreRangePosition = mTempPreRangePosition;
                mRangeEntities.get(mPreRangePosition).setSelect(true);
                mTypeEntities.get(mPreTypePosition).setSelect(false);
                mPreTypePosition = mTempPreTypePosition;
                mTypeEntities.get(mPreTypePosition).setSelect(true);
                mStateEntities.get(mPreStatePosition).setSelect(false);
                mPreStatePosition = mTempPreStatePosition;
                mStateEntities.get(mPreStatePosition).setSelect(true);
                showArrowDown(0);
                showArrowDown(1);
                showArrowDown(2);
                mIsEnterEndTime = true;
                showFilterArrow();
                mDailyPoliceSrfl.setRefreshing(true);
                onRefresh();
                break;
            case R.id.shaixuan_ll:
                mShaixuanLl.startAnimation(mRightOut);
                mRightOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        newMainActivity.setBottomVisible(true);
                        mShaixuanLl.setVisibility(View.GONE);
                        showArrowDown(mPosition);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                break;
        }
    }

    private int getViewHeight(List list) {
        return list.size() * getResources().getDimensionPixelSize(R.dimen.dp40) + list.size() - 1;
    }

    /**
     * 点击了按钮，但是没确认，需要恢复到原状态
     */
    private void restoreFilterData() {
        switch (mAlarmScope) {
            case 1:
                mRangeRadioGroup.check(R.id.range_all_rb);
                break;
            case 2:
                mRangeRadioGroup.check(R.id.range_collect_rb);
                break;
        }
        //101501:重点人员布控  101502:外来人员布控  101503:魅影报警  101504:一体机报警  101505:临控报警
        switch (mAlarmTaskType) {
            case 101501:
                for (int i = 0; i < tempList.size(); i++) {
                    if (getString(R.string.control_key_person).equals(tempList.get(i).getName())) {
                        tempList.get(i).setSelect(true);
                    } else {
                        tempList.get(i).setSelect(false);
                    }
                }
                rvType.getAdapter().notifyDataSetChanged();
                break;
            case 101505:
                for (int i = 0; i < tempList.size(); i++) {
                    if (getString(R.string.person_tracking).equals(tempList.get(i).getName())) {
                        tempList.get(i).setSelect(true);
                    } else {
                        tempList.get(i).setSelect(false);
                    }
                }
                rvType.getAdapter().notifyDataSetChanged();
                break;
            case 101502:
                for (int i = 0; i < tempList.size(); i++) {
                    if (getString(R.string.outside_person).equals(tempList.get(i).getName())) {
                        tempList.get(i).setSelect(true);
                    } else {
                        tempList.get(i).setSelect(false);
                    }
                }
                rvType.getAdapter().notifyDataSetChanged();
                break;
            case 101504:
                for (int i = 0; i < tempList.size(); i++) {
                    if (getString(R.string.private_network_suite).equals(tempList.get(i).getName())) {
                        tempList.get(i).setSelect(true);
                    } else {
                        tempList.get(i).setSelect(false);
                    }
                }
                rvType.getAdapter().notifyDataSetChanged();
                break;
            default:
                for (int i = 0; i < tempList.size(); i++) {
                    if (getString(R.string.all).equals(tempList.get(i).getName())) {
                        tempList.get(i).setSelect(true);
                    } else {
                        tempList.get(i).setSelect(false);
                    }
                }
                rvType.getAdapter().notifyDataSetChanged();
                break;
        }
        switch (mAlarmOperationType) {
            case 1:
                setStateRadioChecked(true, R.id.state_undo_rb);
                break;
            case 2:
                setStateRadioChecked(true, R.id.state_valid_rb);
                break;
            case 3:
                setStateRadioChecked(false, R.id.state_invalid_rb);
                break;
            default:
                setStateRadioChecked(true, R.id.state_all_rb);
                break;
        }
        mAlarmTimeEndTv.setBackground(getResources().getDrawable(R.drawable.shape_rect_corner_5_f7f7f7));
        mAlarmTimeStartTv.setBackground(getResources().getDrawable(R.drawable.shape_rect_corner_5_f7f7f7));
        if (!mIsEnterEndTime) {
            mEndTimeStamp = System.currentTimeMillis();
            mTempEndTimeStamp = mEndTimeStamp;
        }
        String startTimeStr = TimeUtils.millis2String(mStartTimeStamp);
        setDateStr(startTimeStr, true);
        String endTimeStr = TimeUtils.millis2String(mEndTimeStamp);
        setDateStr(endTimeStr, false);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        // 布控任务类型 |  101501:重点人员布控  101502:外来人员布控  101503:魅影报警  101504:一体机报警  101505:临控报警
        switch (checkedId) {
            case R.id.range_all_rb:
                mTempAlarmScope = 1;
                mTempPreRangePosition = 0;
                break;
            case R.id.range_collect_rb:
                mTempAlarmScope = 2;
                mTempPreRangePosition = 1;
                break;
//            case R.id.type_all_rb:
//                mTempAlarmTaskType = -1;
//                mTempPreTypePosition = 0;
//                break;
//            case R.id.type_control_rb:
//                mTempAlarmTaskType = 101501;
//                mTempPreTypePosition = 2;
//                break;
//            case R.id.type_keyperson_rb:
//                mTempAlarmTaskType = 101505;
//                mTempPreTypePosition = 1;
//                break;
            case R.id.state_all_rb:
                setStateRadioChecked(true, R.id.state_all_rb);
                mTempAlarmOperationState = -1;
                mTempPreStatePosition = 0;
                break;
            case R.id.state_undo_rb:
                setStateRadioChecked(true, R.id.state_undo_rb);
                mTempAlarmOperationState = 1;
                mTempPreStatePosition = 1;
                break;
            case R.id.state_valid_rb:
                setStateRadioChecked(true, R.id.state_valid_rb);
                mTempAlarmOperationState = 2;
                mTempPreStatePosition = 2;
                break;
            case R.id.state_invalid_rb:
                setStateRadioChecked(false, R.id.state_invalid_rb);
                mTempAlarmOperationState = 3;
                mTempPreStatePosition = 3;
                break;
            default:
                break;
        }
    }


    private void setStateRadioChecked(boolean isOne, int id) {
        if (isOne) {
            mStateRadioGroupTwo.clearCheck();
            mStateRadioGroupOne.check(id);
        } else {
            mStateRadioGroupOne.clearCheck();
            mStateRadioGroupTwo.check(id);
        }
    }

    public boolean isShaixuanVisible() {
        return mShaixuanLl != null && mShaixuanLl.getVisibility() == View.VISIBLE;
    }

    public void setShaixuanGone() {
        mShaixuanLl.startAnimation(mRightOut);
        mRightOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mShaixuanLl.setVisibility(View.GONE);
                showArrowDown(mPosition);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == ITEM_CLICK_POSITION) {
                int position = data.getIntExtra("position", -1);
                if (-1 == position) {
                    //证明是收藏相关的
                    int collect_position = data.getIntExtra("collect_position", -1);
                    boolean collect = data.getBooleanExtra("collect", false);
                    if (mAlarmScope == 2) {
                        mDailyPoliceSrfl.setRefreshing(true);
                        onRefresh();
                    } else {
                        mDailyPoliceAdapter.getItem(collect_position).setCollectStatus(collect ? 1 : 0);
                    }
                } else {
                    if (-1 == mAlarmOperationType) {
                        mDailyPoliceRclv.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //表示是全部，则需要更新数据
                                mDailyPoliceSrfl.setRefreshing(true);
                                onRefresh();
                            }
                        }, 150);
                    } else {
                        mDailyPoliceAdapter.remove(position);
                        mDailyPoliceAdapter.notifyItemRemoved(position);
                    }
                }

            }
        }
    }
}
