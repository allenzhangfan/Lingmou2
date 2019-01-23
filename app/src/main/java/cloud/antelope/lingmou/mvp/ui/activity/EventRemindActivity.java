package cloud.antelope.lingmou.mvp.ui.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;

import java.util.Calendar;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.FormatUtils;
import cloud.antelope.lingmou.di.component.DaggerEventRemindComponent;
import cloud.antelope.lingmou.di.module.EventRemindModule;
import cloud.antelope.lingmou.mvp.contract.EventRemindContract;
import cloud.antelope.lingmou.mvp.presenter.EventRemindPresenter;
import cloud.antelope.lingmou.mvp.ui.adapter.EventRemindPagerAdapter;
import cloud.antelope.lingmou.mvp.ui.fragment.EventRemindListFragment;
import cloud.antelope.lingmou.mvp.ui.widget.LmDatePickDialog;
import cloud.antelope.lingmou.mvp.ui.widget.LmTimePickerDialog;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class EventRemindActivity extends BaseActivity<EventRemindPresenter> implements EventRemindContract.View {
    public static final int START_TIME = 0x010;
    public static final int END_TIME = 0x011;
    @BindView(R.id.head_left_iv)
    ImageButton headLeftIv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.stl)
    SlidingTabLayout stl;
    @BindView(R.id.tv_filter)
    TextView tvFilter;
    @BindView(R.id.ll_filter)
    LinearLayout llFilter;
    @BindView(R.id.tv_search)
    TextView tvSearch;
    @BindView(R.id.vp)
    ViewPager vp;
    @BindView(R.id.rl_filter)
    RelativeLayout rlFilter;
    @Inject
    EventRemindPagerAdapter mAdapter;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_start_time)
    TextView tvStartTime;
    @BindView(R.id.ll_start_time)
    LinearLayout llStartTime;
    @BindView(R.id.tv_start_time_choice)
    TextView tvStartTimeChoice;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;
    @BindView(R.id.ll_end_time)
    LinearLayout llEndTime;
    @BindView(R.id.tv_end_time_choice)
    TextView tvEndTimeChoice;
    @BindView(R.id.clean_tv)
    TextView cleanTv;
    @BindView(R.id.cofirm_tv)
    TextView cofirmTv;
    @BindView(R.id.bottom_ll)
    LinearLayout bottomLl;
    private Drawable mFilterBlackDrawable, mFilterYellowDrawable;
    private Animation mRightIn, mRightOut;
    private LmDatePickDialog lmDatePickDialog;
    Calendar calendar;
    int year, month, day, hour, minute,
            setStartYear, setStartMonthOfYear, setStartDayOfMonth, setStartHour, setStartMinute,
            setEndYear, setEndMonthOfYear, setEndDayOfMonth, setEndHour, setEndMinute,
            tempStartYear, tempStartMonthOfYear, tempStartDayOfMonth, tempStartHour,
            tempStartMinute, tempEndYear, tempEndMonthOfYear, tempEndDayOfMonth, tempEndHour, tempEndMinute;
    Long startTime = -1L;
    Long endTime = -1L;
    private boolean timeSet;//时间设置过
    private boolean timeModify;//时间选择过（界面上）
    private boolean rightOpen;//右侧开启
    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerEventRemindComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .eventRemindModule(new EventRemindModule(this, getSupportFragmentManager()))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_event_remind; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        titleTv.setText(getString(R.string.event_remind));
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
        mFilterBlackDrawable = getResources().getDrawable(R.drawable.filter_black_small);
        mFilterYellowDrawable = getResources().getDrawable(R.drawable.filter_orange_small);
        mFilterBlackDrawable.setBounds(0, 0, mFilterBlackDrawable.getMinimumWidth(), mFilterBlackDrawable.getMinimumHeight());
        mFilterYellowDrawable.setBounds(0, 0, mFilterYellowDrawable.getMinimumWidth(), mFilterYellowDrawable.getMinimumHeight());
        mRightIn = AnimationUtils.loadAnimation(this, R.anim.right_in);
        mRightOut = AnimationUtils.loadAnimation(this, R.anim.right_out);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        setStartYear = year;
        setStartMonthOfYear = month + 1;
        setStartDayOfMonth = day - 3;
        setStartHour = hour;
        setStartMinute = minute;
        setEndYear = year;
        setEndMonthOfYear = month + 1;
        setEndDayOfMonth = day;
        setEndHour = hour;
        setEndMinute = minute;

        resetTime();
        calendar.set(setStartYear, setStartMonthOfYear - 1, setStartDayOfMonth, setStartHour, setStartMinute);
        startTime = calendar.getTimeInMillis();
        calendar.set(setEndYear, setEndMonthOfYear - 1, setEndDayOfMonth, setEndHour, setEndMinute);
        endTime = calendar.getTimeInMillis();
        timeSet = true;
    }

    private void resetTime() {
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        tempStartYear = year;
        tempStartMonthOfYear = month + 1;
        tempStartDayOfMonth = day - 3;
        tempStartHour =  hour;
        tempStartMinute = minute;
        tempEndYear = year;
        tempEndMonthOfYear = month + 1;
        tempEndDayOfMonth = day;
        tempEndHour = hour;
        tempEndMinute = minute;
        timeModify = true;
    }

    @OnClick({R.id.head_left_iv, R.id.ll_filter, R.id.tv_start_time_choice, R.id.ll_start_time
            , R.id.tv_end_time_choice, R.id.ll_end_time, R.id.cofirm_tv, R.id.clean_tv,R.id.tv_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_search:
                Intent intent = new Intent(EventRemindActivity.this, CloudSearchActivity.class);
                intent.putExtra("type", "alarm");
                intent.putExtra("taskType", 1100101);
                ActivityOptions activityOptions = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    activityOptions = ActivityOptions.makeSceneTransitionAnimation(EventRemindActivity.this, tvSearch, "searchText");
                    startActivity(intent, activityOptions.toBundle());
                } else {
                    startActivity(intent);
                }
                break;
            case R.id.head_left_iv:
                onBackPressedSupport();
                break;
            case R.id.ll_filter:
                if (rightOpen) {
                    hideFilter();
                } else {
                    showFilter();
                }
                break;
            case R.id.tv_start_time_choice:
            case R.id.ll_start_time:
                lmDatePickDialog = new LmDatePickDialog(EventRemindActivity.this, (view12, year, month, dayOfMonth) -> {
                    tempStartYear = year;
                    tempStartMonthOfYear = month + 1;
                    tempStartDayOfMonth = dayOfMonth;
                    lmDatePickDialog.dismiss();
                    showTimePickerDialog(START_TIME);
                }, null, setStartYear, setStartMonthOfYear - 1, setStartDayOfMonth);
                lmDatePickDialog.show();
                break;
            case R.id.tv_end_time_choice:
            case R.id.ll_end_time:
                lmDatePickDialog = new LmDatePickDialog(EventRemindActivity.this, (view12, year, month, dayOfMonth) -> {
                    tempEndYear = year;
                    tempEndMonthOfYear = month + 1;
                    tempEndDayOfMonth = dayOfMonth;
                    lmDatePickDialog.dismiss();
                    showTimePickerDialog(END_TIME);
                }, null, tempEndYear, tempEndMonthOfYear - 1, tempEndDayOfMonth);
                lmDatePickDialog.show();
                break;
            case R.id.clean_tv:
                resetTime();
            case R.id.cofirm_tv:
                if (tvStartTimeChoice.getVisibility() == View.VISIBLE && tvEndTimeChoice.getVisibility() == View.GONE) {
                    ToastUtils.showShort("请选择开始时间");
                    return;
                }
                if (tvEndTimeChoice.getVisibility() == View.VISIBLE && tvStartTimeChoice.getVisibility() == View.GONE) {
                    ToastUtils.showShort("请选择结束时间");
                    return;
                }
                timeSet = timeModify;
                setStartYear = tempStartYear;
                setStartMonthOfYear = tempStartMonthOfYear;
                setStartDayOfMonth = tempStartDayOfMonth;
                setStartHour = tempStartHour;
                setStartMinute = tempStartMinute;
                setEndYear = tempEndYear;
                setEndMonthOfYear = tempEndMonthOfYear;
                setEndDayOfMonth = tempEndDayOfMonth;
                setEndHour = tempEndHour;
                setEndMinute = tempEndMinute;
                calendar.set(setStartYear, setStartMonthOfYear - 1, setStartDayOfMonth, setStartHour, setStartMinute);
                startTime = calendar.getTimeInMillis();
                calendar.set(setEndYear, setEndMonthOfYear - 1, setEndDayOfMonth, setEndHour, setEndMinute);
                endTime = calendar.getTimeInMillis();
                setTime();
                hideFilter();
                break;
        }
    }

    private void setTime() {
        for (EventRemindListFragment fragment : mAdapter.fragments) {
            fragment.setTime(startTime, endTime);
        }
    }

    private void showTimePickerDialog(int type) {
        new LmTimePickerDialog(EventRemindActivity.this, 0, (view, hourOfDay, minute) -> {
            if (type == START_TIME) {
                tempStartHour = hourOfDay;
                tempStartMinute = minute;
                tvStartTimeChoice.setVisibility(View.GONE);
                tvStartTime.setText(tempStartYear + "-" + FormatUtils.twoNumber(tempStartMonthOfYear) + "-" + FormatUtils.twoNumber(tempStartDayOfMonth) + " " + FormatUtils.twoNumber(tempStartHour) + ":" + FormatUtils.twoNumber(tempStartMinute));
            } else {
                tempEndHour = hourOfDay;
                tempEndMinute = minute;
                tvEndTimeChoice.setVisibility(View.GONE);
                tvEndTime.setText(tempEndYear + "-" + FormatUtils.twoNumber(tempEndMonthOfYear) + "-" + FormatUtils.twoNumber(tempEndDayOfMonth) + " " + FormatUtils.twoNumber(tempEndHour) + ":" + FormatUtils.twoNumber(tempEndMinute));
            }
            timeModify = true;
        }, type == START_TIME ? tempStartHour : tempEndHour, type == START_TIME ? tempStartMinute : tempEndMinute, true).show();
    }

    private void hideFilter() {
        if (!rightOpen) return;
        tvFilter.setTextColor(getResources().getColor(R.color.gray_212121));
        tvFilter.setCompoundDrawables(mFilterBlackDrawable, null, null, null);
        rlFilter.startAnimation(mRightOut);
        rightOpen = false;
        mRightOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rlFilter.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void showFilter() {
        if (rightOpen) return;
        if (timeSet) {
            tvStartTimeChoice.setVisibility(View.GONE);
            tvStartTime.setText(setStartYear + "-" + FormatUtils.twoNumber(setStartMonthOfYear) + "-" + FormatUtils.twoNumber(setStartDayOfMonth) + " " + FormatUtils.twoNumber(setStartHour) + ":" + FormatUtils.twoNumber(setStartMinute));
            tvEndTimeChoice.setVisibility(View.GONE);
            tvEndTime.setText(setEndYear + "-" + FormatUtils.twoNumber(setEndMonthOfYear) + "-" + FormatUtils.twoNumber(setEndDayOfMonth) + " " + FormatUtils.twoNumber(setEndHour) + ":" + FormatUtils.twoNumber(setEndMinute));
        } else {
            tvStartTimeChoice.setVisibility(View.VISIBLE);
            tvEndTimeChoice.setVisibility(View.VISIBLE);
        }
        tvFilter.setTextColor(getResources().getColor(R.color.yellow_ff8f00));
        tvFilter.setCompoundDrawables(mFilterYellowDrawable, null, null, null);
        rlFilter.setVisibility(View.VISIBLE);
        rlFilter.startAnimation(mRightIn);
        rightOpen = true;
        mRightIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                rlFilter.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
    @Override
    public void onBackPressedSupport() {
        if (rightOpen) {
            hideFilter();
            return;
        }
        super.onBackPressedSupport();
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
