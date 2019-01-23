package cloud.antelope.lingmou.mvp.ui.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.utils.TimeUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.di.component.DaggerFaceFilterComponent;
import cloud.antelope.lingmou.di.module.FaceFilterModule;
import cloud.antelope.lingmou.mvp.contract.FaceFilterContract;
import cloud.antelope.lingmou.mvp.presenter.FaceFilterPresenter;
import cloud.antelope.lingmou.mvp.ui.widget.LmDatePickDialog;
import cloud.antelope.lingmou.mvp.ui.widget.LmTimePickerDialog;
import cloud.antelope.lingmou.mvp.ui.widget.SimilaritySeekBar;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class FaceFilterActivity extends BaseActivity<FaceFilterPresenter> implements FaceFilterContract.View {


    private static final long THIRTY_DAY_STAMP = 3 * 24 * 60 * 60 * 1000L;
    @BindView(R.id.head_left_iv)
    ImageButton mHeadLeftIv;
    @BindView(R.id.title_tv)
    TextView mTitleTv;
    @BindView(R.id.start_time_tv)
    TextView mStartTimeTv;
    @BindView(R.id.end_time_tv)
    TextView mEndTimeTv;
    @BindView(R.id.seekbar)
    SimilaritySeekBar seekbar;
    @BindView(R.id.head_right_iv)
    ImageView headRightIv;
    @BindView(R.id.head_right_tv)
    TextView headRightTv;
    @BindView(R.id.tv_3)
    TextView tv3;
    @BindView(R.id.tv_7)
    TextView tv7;
    @BindView(R.id.tv_15)
    TextView tv15;
    @BindView(R.id.tv_30)
    TextView tv30;
    @BindView(R.id.tv_start_time_choice)
    TextView tvStartTimeChoice;
    @BindView(R.id.start_time_ll)
    LinearLayout startTimeLl;
    @BindView(R.id.tv_end_time_choice)
    TextView tvEndTimeChoice;
    @BindView(R.id.end_time_ll)
    LinearLayout endTimeLl;
    @BindView(R.id.clean_tv)
    TextView cleanTv;
    @BindView(R.id.cofirm_tv)
    TextView cofirmTv;

    private LmDatePickDialog mStartDateDialog, mEndDateDialog;
    private LmTimePickerDialog mStartTimeDialog, mEndTimeDialog;

    private int mStartYear, mStartMonth, mStartDay, mStartHour, mStartMinute;
    private int mEndYear, mEndMonth, mEndDay, mEndHour, mEndMinute;

    private long mStartTimeStamp;
    private long mEndTimeStamp;
    private int score;
    private int rangeTime;
    private int originalRangeTime;
    private boolean daySelected;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerFaceFilterComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .faceFilterModule(new FaceFilterModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_face_filter; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mTitleTv.setText(R.string.filter_term);
        mStartTimeStamp = getIntent().getLongExtra("startTime", -1);
        mEndTimeStamp = getIntent().getLongExtra("endTime", -1);
        rangeTime = originalRangeTime = getIntent().getIntExtra("rangeTime", -1);
        score = getIntent().getIntExtra("score", 85);
        seekbar.setCurrentValue(score);
        select(rangeTime);
        if (rangeTime != -1) {
            tvStartTimeChoice.setVisibility(View.VISIBLE);
            tvEndTimeChoice.setVisibility(View.VISIBLE);
        } else {
            if (-1 == mEndTimeStamp) {
                mEndTimeStamp = System.currentTimeMillis();
            }
            if (-1 == mStartTimeStamp) {
                mStartTimeStamp = System.currentTimeMillis() - THIRTY_DAY_STAMP;
            }
        }
        String entTimeStr = TimeUtils.millis2String(mEndTimeStamp, "yyyy.MM.dd HH:mm");
        mEndTimeTv.setText(entTimeStr);

        String startTimeStr = TimeUtils.millis2String(mStartTimeStamp, "yyyy.MM.dd HH:mm");
        mStartTimeTv.setText(startTimeStr);

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
        mStartDateDialog.getDatePicker().updateDate(mStartYear, mStartMonth, mStartDay);
    }

    private void select(int day) {
        tv3.setSelected(false);
        tv7.setSelected(false);
        tv15.setSelected(false);
        tv30.setSelected(false);
        tvStartTimeChoice.setVisibility(View.VISIBLE);
        tvEndTimeChoice.setVisibility(View.VISIBLE);
        rangeTime = day;
        daySelected = true;
        switch (day) {
            case 3:
                tv3.setSelected(true);
                mEndTimeStamp = System.currentTimeMillis();
                mStartTimeStamp = System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000L;
                break;
            case 7:
                tv7.setSelected(true);
                mEndTimeStamp = System.currentTimeMillis();
                mStartTimeStamp = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L;
                break;
            case 15:
                tv15.setSelected(true);
                mEndTimeStamp = System.currentTimeMillis();
                mStartTimeStamp = System.currentTimeMillis() - 15 * 24 * 60 * 60 * 1000L;
                break;
            case 30:
                tv30.setSelected(true);
                mEndTimeStamp = System.currentTimeMillis();
                mStartTimeStamp = System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000L;
                break;
            default:
                rangeTime = -1;
                daySelected = false;
                tvStartTimeChoice.setVisibility(View.GONE);
                tvEndTimeChoice.setVisibility(View.GONE);
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
                // if (mStartTimeStamp != -1 && (endTime - mStartTimeStamp > THREE_DAY_STAMP)) {
                //     ToastUtils.showShort(R.string.hint_not_more_three_days);
                // } else {
                mEndTimeStamp = endTime;
                mEndTimeTv.setText(timeStr);
                mEndDateDialog.dismiss();
                tvEndTimeChoice.setVisibility(View.GONE);
                if (tvStartTimeChoice.getVisibility() == View.GONE && tvEndTimeChoice.getVisibility() == View.GONE) {
                    select(0);
                }
                // }
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
                // if (mEndTimeStamp != -1 && (mEndTimeStamp - startTime > THREE_DAY_STAMP)) {
                //     ToastUtils.showShort(R.string.hint_not_more_three_days);
                // } else {
                mStartTimeStamp = startTime;
                mStartTimeTv.setText(timeStr);
                tvStartTimeChoice.setVisibility(View.GONE);
                mStartDateDialog.dismiss();
                if (tvStartTimeChoice.getVisibility() == View.GONE && tvEndTimeChoice.getVisibility() == View.GONE) {
                    select(0);
                }
                // }
            }
        }, mStartHour == 0 ? Calendar.getInstance().get(Calendar.HOUR_OF_DAY) : mStartHour,
                mStartMinute == 0 ? Calendar.getInstance().get(Calendar.MINUTE) : mStartMinute, true);
        mStartTimeDialog.show();
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

    @OnClick({R.id.head_left_iv, R.id.title_tv, R.id.start_time_ll, R.id.end_time_ll, R.id.clean_tv
            , R.id.cofirm_tv, R.id.tv_start_time, R.id.tv_end_time_choice, R.id.tv_3, R.id.tv_7, R.id.tv_15, R.id.tv_30,})
    public void onViewClicked(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.tv_3:
                select(3);
                break;
            case R.id.tv_7:
                select(7);
                break;
            case R.id.tv_15:
                select(15);
                break;
            case R.id.tv_30:
                select(30);
                break;
            case R.id.head_left_iv:
                finish();
                break;
            case R.id.start_time_ll:
            case R.id.tv_start_time:
                initStartTime();
                mStartDateDialog.show();
                break;
            case R.id.end_time_ll:
            case R.id.tv_end_time_choice:
                initEndTime();
                mEndDateDialog.show();
                break;
            case R.id.clean_tv:
                mEndTimeStamp = System.currentTimeMillis();
                mStartTimeStamp = mEndTimeStamp - THIRTY_DAY_STAMP;
                //将参数带回
                intent.putExtra("startTime", mStartTimeStamp);
                intent.putExtra("endTime", mEndTimeStamp);
                intent.putExtra("score", seekbar.getCurrentValue());
                intent.putExtra("rangeTime", originalRangeTime);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.cofirm_tv:
                if (daySelected) {
                    intent.putExtra("rangeTime", rangeTime);
                    intent.putExtra("score", seekbar.getCurrentValue());
                    setResult(RESULT_OK, intent);
                    finish();
                } else if (-1 == mStartTimeStamp) {
                    ToastUtils.showShort("请选择开始时间");
                } else if (mEndTimeStamp == -1) {
                    ToastUtils.showShort("请选择结束时间");
                } else if (mEndTimeStamp <= mStartTimeStamp) {
                    ToastUtils.showShort(R.string.hint_end_day_less_start_day);
                }
                // else if(mEndTimeStamp - mStartTimeStamp > THREE_DAY_STAMP){
                //     ToastUtils.showShort(R.string.hint_not_more_three_days);
                // }
                else {
                    //将参数带回
                    intent.putExtra("startTime", mStartTimeStamp);
                    intent.putExtra("endTime", mEndTimeStamp);
                    intent.putExtra("score", seekbar.getCurrentValue());
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
        }
    }

    private void initEndTime() {
        String endTimeStamp = TimeUtils.millis2String(mEndTimeStamp, "yyyy-MM-dd");
        if (!TextUtils.isEmpty(endTimeStamp)) {
            String[] split = endTimeStamp.split("-");
            mEndDateDialog.getDatePicker().updateDate(Integer.parseInt(split[0]), Integer.parseInt(split[1]) - 1, Integer.parseInt(split[2]));
        }
    }

    private void initStartTime() {
        String startTimeStamp = TimeUtils.millis2String(mStartTimeStamp, "yyyy-MM-dd");
        if (!TextUtils.isEmpty(startTimeStamp)) {
            String[] split = startTimeStamp.split("-");
            mStartDateDialog.getDatePicker().updateDate(Integer.parseInt(split[0]), Integer.parseInt(split[1]) - 1, Integer.parseInt(split[2]));
        }
    }

}
