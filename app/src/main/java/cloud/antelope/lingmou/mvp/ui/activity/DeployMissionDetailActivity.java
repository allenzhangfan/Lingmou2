package cloud.antelope.lingmou.mvp.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.http.imageloader.glide.ImageConfigImpl;
import com.jess.arms.utils.ArmsUtils;
import com.lingdanet.safeguard.common.modle.LoadingDialog;
import com.lingdanet.safeguard.common.modle.SweetDialog;
import com.lingdanet.safeguard.common.utils.KeyboardUtils;
import com.lingdanet.safeguard.common.utils.ToastUtils;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cloud.antelope.lingmou.R;
import cloud.antelope.lingmou.app.utils.FormatUtils;
import cloud.antelope.lingmou.app.utils.LogUploadUtil;
import cloud.antelope.lingmou.di.component.DaggerDeployMissionDetailComponent;
import cloud.antelope.lingmou.di.module.DeployMissionDetailModule;
import cloud.antelope.lingmou.mvp.contract.DeployMissionDetailContract;
import cloud.antelope.lingmou.mvp.model.entity.DeployDetailEntity;
import cloud.antelope.lingmou.mvp.model.entity.LogUploadRequest;
import cloud.antelope.lingmou.mvp.model.entity.ModifyDeployListEvent;
import cloud.antelope.lingmou.mvp.model.entity.NewDeployMissionRequest;
import cloud.antelope.lingmou.mvp.model.entity.StartOrPauseDeployMissionRequest;
import cloud.antelope.lingmou.mvp.presenter.DeployMissionDetailPresenter;
import cloud.antelope.lingmou.mvp.ui.fragment.DeployListFragment;
import cloud.antelope.lingmou.mvp.ui.widget.LmDatePickDialog;
import cloud.antelope.lingmou.mvp.ui.widget.LmTimePickerDialog;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class DeployMissionDetailActivity extends BaseActivity<DeployMissionDetailPresenter> implements DeployMissionDetailContract.View {
    private static final int PAUSED = 0;
    private static final int UNDERWAY = 1;
    private static final int NOT_RUN = 2;
    private static final int EXPIRED = 3;
    public static final int START_TIME = 0x010;
    public static final int END_TIME = 0x011;
    public static final int MODIFY = 0x100;
    public static final int START = 0x101;
    public static final int PAUSE = 0x102;
    @BindView(R.id.head_left_iv)
    ImageButton headLeftIv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.head_right_iv)
    ImageView headRightIv;
    @BindView(R.id.head_right_tv)
    TextView headRightTv;
    @BindView(R.id.iv_pic)
    ImageView ivPic;
    @BindView(R.id.tv_mission_detail)
    TextView tvMissionDetail;
    @BindView(R.id.tv_mission_name)
    TextView tvMissionName;
    @BindView(R.id.tv_mission_status)
    TextView tvMissionStatus;
    @BindView(R.id.tv_start_time)
    TextView tvStartTime;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;
    @BindView(R.id.ll_fold)
    LinearLayout llFold;
    @BindView(R.id.et_mission_remark)
    EditText etMissionRemark;
    @BindView(R.id.tv_mission_operation)
    TextView tvMissionOperation;
    @BindView(R.id.tv_save)
    TextView tvSave;
    @BindView(R.id.tv_threshold)
    TextView tvThreshold;
    @BindView(R.id.fl_overlay)
    FrameLayout flOverlay;
    @Inject
    LoadingDialog mLoadingDialog;
    @Inject
    ImageLoader mImageLoader;
    boolean fold;
    boolean animating;
    private int foldHeight;
    private String id;
    private int startYear;
    private int startMonth;
    private int startDay;
    private int startHour;
    private int startMinute;
    private int endYear;
    private int endMonth;
    private int endDay;
    private int endHour;
    private int endMinute;
    private ArrayList<String> list;
    private int selectedPosition;
    private LmDatePickDialog lmDatePickDialog;
    DeployDetailEntity entity;
    Calendar mCalendar;
    private int state;
    private TimerTask timerTask;
    private boolean alreadyShown;
    private String startTimeStr, endTimeStr;
    private boolean controlAgain;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerDeployMissionDetailComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .deployMissionDetailModule(new DeployMissionDetailModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_deploy_mission_detail; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        titleTv.setText(getString(R.string.mission_detail));
        llFold.measure(0, 0);
        foldHeight = llFold.getMeasuredHeight();
        headRightIv.setVisibility(View.VISIBLE);
        headRightIv.setImageResource(R.drawable.delete);
        Intent intent = getIntent();
        if (intent != null) {
            id = intent.getStringExtra("mission_id");
            state = intent.getIntExtra("state", -1);
        }
        mCalendar = Calendar.getInstance();
        list = new ArrayList<>();
        for (int i = 80; i <= 100; i++) {
            list.add(String.valueOf(i));
        }
        mPresenter.getData(id);
    }

    @OnClick({R.id.head_left_iv, R.id.tv_mission_detail, R.id.head_right_iv, R.id.tv_start_time, R.id.tv_end_time, R.id.tv_threshold, R.id.tv_save, R.id.tv_mission_operation})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.head_left_iv:
                finish();
                break;
            case R.id.head_right_iv:
                SweetDialog dialog = new SweetDialog(DeployMissionDetailActivity.this);
                dialog.setTitle(getString(R.string.sure_delete_title));
                dialog.setPositiveListener((v) -> {
                    dialog.dismiss();
                    mPresenter.deleteMission(id);
                });
                dialog.setNegativeListener((v) -> {
                    dialog.dismiss();
                });
                dialog.show();
                break;
            case R.id.tv_mission_detail:
                fold();
                break;
            case R.id.tv_start_time:
                lmDatePickDialog = new LmDatePickDialog(DeployMissionDetailActivity.this, (view12, year, month, dayOfMonth) -> {
                    startYear = year;
                    startMonth = month + 1;
                    startDay = dayOfMonth;
                    lmDatePickDialog.dismiss();
                    showTimePickerDialog(START_TIME);
                }, null, startYear, startMonth - 1, startDay);
                lmDatePickDialog.show();
                break;
            case R.id.tv_end_time:
                lmDatePickDialog = new LmDatePickDialog(DeployMissionDetailActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view12, int year, int month, int dayOfMonth) {
                        endYear = year;
                        endMonth = month + 1;
                        endDay = dayOfMonth;
                        lmDatePickDialog.dismiss();
                        DeployMissionDetailActivity.this.showTimePickerDialog(END_TIME);
                    }
                }, null, endYear, endMonth - 1, endDay);
                lmDatePickDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        controlAgain = false;
                    }
                });
                lmDatePickDialog.show();
                break;
            case R.id.tv_threshold:
                KeyboardUtils.hideSoftInput(this);
                OptionsPickerView pickerView = new OptionsPickerView.Builder(this, (options1, options2, options3, v) -> {
                    selectedPosition = options1;
                    tvThreshold.setText(list.get(options1));
                    checkChange();
                }).setContentTextSize(20)//设置滚轮文字大小
                        .setDividerColor(Color.LTGRAY)//设置分割线的颜色
                        .setTextColorCenter(Color.LTGRAY)
                        .setDecorView((ViewGroup) getWindow().getDecorView())
                        .build();
                pickerView.setPicker(list);
                pickerView.setSelectOptions(selectedPosition);
                pickerView.show();
                break;
            case R.id.tv_mission_operation:
                switch (entity.getTaskStatus()) {
                    case PAUSED:
                        mPresenter.startOrPauseMission(new StartOrPauseDeployMissionRequest(id, "1"));
                        break;
                    case UNDERWAY:
                        mPresenter.startOrPauseMission(new StartOrPauseDeployMissionRequest(id, "0"));
                        break;
                    case NOT_RUN:
                        controlAgain = true;
                        onClick(tvStartTime);
                        break;
                    case EXPIRED:
                        controlAgain = true;
                        onClick(tvEndTime);
                        break;
                }
                break;
            case R.id.tv_save:
                mCalendar.set(startYear, startMonth - 1, startDay, startHour, startMinute);
                String start = String.valueOf(mCalendar.getTimeInMillis());
                mCalendar.set(endYear, endMonth - 1, endDay, endHour, endMinute);
                String end = String.valueOf(mCalendar.getTimeInMillis());

                if (checkTime(true)) {
                    NewDeployMissionRequest request = new NewDeployMissionRequest(entity.getName(), start, end, tvThreshold.getText().toString(), etMissionRemark.getText().toString(), entity.getImageUrl(), null);
                    request.setId(id);
                    mPresenter.modifyMission(request);
                } else {
                    ToastUtils.showLong(getText(R.string.time_set_error_tip));
                }
                break;
        }
    }

    private void showTimePickerDialog(int type) {
        LmTimePickerDialog dialog = new LmTimePickerDialog(DeployMissionDetailActivity.this, 0, (view, hourOfDay, minute) -> {
            if (type == START_TIME) {
                startHour = hourOfDay;
                startMinute = minute;
                if (checkTime(false)) {
                    tvStartTime.setTextColor(getResources().getColor(R.color.gray_212121));
                    tvStartTime.setText(startYear + "-" + FormatUtils.twoNumber(startMonth) + "-" + FormatUtils.twoNumber(startDay) + " " + FormatUtils.twoNumber(startHour) + ":" + FormatUtils.twoNumber(startMinute));
                    checkChange();
                } else {
                    ToastUtils.showLong(getText(R.string.time_set_error_tip));
                    restoreTime(tvStartTime);
                }
            } else {
                endHour = hourOfDay;
                endMinute = minute;
                if (checkTime(false)) {
                    tvEndTime.setTextColor(getResources().getColor(R.color.gray_212121));
                    tvEndTime.setText(endYear + "-" + FormatUtils.twoNumber(endMonth) + "-" + FormatUtils.twoNumber(endDay) + " " + FormatUtils.twoNumber(endHour) + ":" + FormatUtils.twoNumber(endMinute));
                    checkChange();
                } else {
                    ToastUtils.showLong(getText(R.string.time_set_error_tip));
                    restoreTime(tvEndTime);
                }
            }
            if (controlAgain) {
                onClick(tvSave);
            }
        }, type == START_TIME ? startHour : endHour, type == START_TIME ? startMinute : endMinute, true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                controlAgain = false;
            }
        });
        dialog.show();
    }

    private void restoreTime(TextView textView) {
        if (textView.getId() == R.id.tv_start_time) {
            if (TextUtils.isEmpty(textView.getText().toString())) {
                mCalendar.setTimeInMillis(Long.valueOf(entity.getStartTime()));
                startYear = mCalendar.get(Calendar.YEAR);
                startMonth = mCalendar.get(Calendar.MONTH) + 1;
                startDay = mCalendar.get(Calendar.DAY_OF_MONTH);
                startHour = mCalendar.get(Calendar.HOUR_OF_DAY);
                startMinute = mCalendar.get(Calendar.MINUTE);
            } else {
                String[] s1 = textView.getText().toString().split(" ");
                String[] s2 = s1[0].split("-");
                String[] s3 = s1[1].split(":");
                startYear = Integer.valueOf(s2[0]);
                startMonth = Integer.valueOf(s2[1]);
                startDay = Integer.valueOf(s2[2]);
                startHour = Integer.valueOf(s3[0]);
                startMinute = Integer.valueOf(s3[1]);
                Timber.e(startYear + " " + startMonth + " " + startDay + " " + startHour + " " + startMinute);
            }
        } else {
            if (TextUtils.isEmpty(textView.getText().toString())) {
                mCalendar.setTimeInMillis(Long.valueOf(entity.getEndTime()));
                endYear = mCalendar.get(Calendar.YEAR);
                endMonth = mCalendar.get(Calendar.MONTH) + 1;
                endDay = mCalendar.get(Calendar.DAY_OF_MONTH);
                endHour = mCalendar.get(Calendar.HOUR_OF_DAY);
                endMinute = mCalendar.get(Calendar.MINUTE);
            } else {
                String[] s1 = textView.getText().toString().split(" ");
                String[] s2 = s1[0].split("-");
                String[] s3 = s1[1].split(":");
                endYear = Integer.valueOf(s2[0]);
                endMonth = Integer.valueOf(s2[1]);
                endDay = Integer.valueOf(s2[2]);
                endHour = Integer.valueOf(s3[0]);
                endMinute = Integer.valueOf(s3[1]);

            }
        }
    }

    /**
     * 检查时间设置
     *
     * @return 没问题返回true
     */
    private boolean checkTime(boolean atLast) {
        if (TextUtils.isEmpty(tvStartTime.getText().toString()) && TextUtils.isEmpty(tvEndTime.getText().toString())) {
            return true;
        }
        mCalendar.set(startYear, startMonth - 1, startDay, startHour, startMinute);
        long start = mCalendar.getTimeInMillis();
        mCalendar.set(endYear, endMonth - 1, endDay, endHour, endMinute);
        long end = mCalendar.getTimeInMillis();
        return atLast ? end > start : end >= start;
    }

    private void fold() {
        if (animating) return;
        AnimatorSet set = new AnimatorSet();
        ValueAnimator alphaAnim, heightAnim;
        if (fold) {
            alphaAnim = ValueAnimator.ofFloat(0, 1.0f);
            heightAnim = ValueAnimator.ofFloat(foldHeight, 0f);
        } else {
            alphaAnim = ValueAnimator.ofFloat(1.0f, 0);
            heightAnim = ValueAnimator.ofFloat(0, foldHeight);
        }
        alphaAnim.setDuration(200);
        alphaAnim.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            llFold.setAlpha(value);
        });
        heightAnim.setDuration(300);
        heightAnim.addUpdateListener(animation -> {
            float value = -(float) animation.getAnimatedValue();
            llFold.setPadding(0, (int) value, 0, 0);
        });
        if (fold) {
            set.play(heightAnim).before(alphaAnim);
            alphaAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    Drawable drawable = getResources().getDrawable(R.drawable.arrow_down);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    tvMissionDetail.setCompoundDrawables(null, null, drawable, null);
                    fold = !fold;
                    animating = false;
                }
            });
        } else {
            set.play(alphaAnim).before(heightAnim);
            heightAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    Drawable drawable = getResources().getDrawable(R.drawable.arrow_up);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    tvMissionDetail.setCompoundDrawables(null, null, drawable, null);
                    fold = !fold;
                    animating = false;
                }
            });
        }
        set.start();
        animating = true;
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
    public void showData(DeployDetailEntity entity) {
        this.entity = entity;
        flOverlay.setVisibility(View.GONE);
        mImageLoader.loadImage(this, ImageConfigImpl
                .builder()
                .url(entity.getImageUrl())
                .cacheStrategy(0)
                .placeholder(R.drawable.placeholder_face_compare)
                .errorPic(R.drawable.placeholder_face_compare)
                .imageView(ivPic).build());
        tvMissionName.setText(entity.getName());
        String status = "";
        int color = 0;
        switch (entity.getTaskStatus()) {
            case PAUSED:
                status = getString(R.string.paused);
                color = getResources().getColor(R.color.blue_gray_78909c);
                tvMissionOperation.setText(getString(R.string.start_mission));
                tvMissionOperation.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                break;
            case UNDERWAY:
                status = getString(R.string.underway);
                color = getResources().getColor(R.color.yellow_ffc107);
                tvMissionOperation.setText(getString(R.string.pause_mission));
                tvMissionOperation.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                break;
            case NOT_RUN:
                status = getString(R.string.not_start);
                color = getResources().getColor(R.color.purple_ab47bc);
                tvMissionOperation.setText(getString(R.string.not_start_mission));
                tvMissionOperation.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                tvStartTime.setTextColor(getResources().getColor(R.color.red_ed0f2a));
                break;
            case EXPIRED:
                status = getString(R.string.expired);
                color = getResources().getColor(R.color.red_ec407a);
                tvMissionOperation.setText(getString(R.string.mission_expired));
                tvMissionOperation.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                tvEndTime.setTextColor(getResources().getColor(R.color.red_ed0f2a));
                break;
        }
        tvMissionStatus.setText(status);
        tvMissionStatus.setTextColor(color);
        tvThreshold.setText(entity.getAlarmThreshold());
        selectedPosition = list.indexOf(entity.getAlarmThreshold());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.valueOf(entity.getStartTime()));
        startYear = calendar.get(Calendar.YEAR);
        startMonth = calendar.get(Calendar.MONTH) + 1;
        startDay = calendar.get(Calendar.DAY_OF_MONTH);
        startHour = calendar.get(Calendar.HOUR_OF_DAY);
        startMinute = calendar.get(Calendar.MINUTE);
        startTimeStr = startYear + "-" + FormatUtils.twoNumber(startMonth) + "-" + FormatUtils.twoNumber(startDay) + " " + FormatUtils.twoNumber(startHour) + ":" + FormatUtils.twoNumber(startMinute);
        tvStartTime.setText(startTimeStr);
        calendar.setTimeInMillis(Long.valueOf(entity.getEndTime()));
        endYear = calendar.get(Calendar.YEAR);
        endMonth = calendar.get(Calendar.MONTH) + 1;
        endDay = calendar.get(Calendar.DAY_OF_MONTH);
        endHour = calendar.get(Calendar.HOUR_OF_DAY);
        endMinute = calendar.get(Calendar.MINUTE);
        endTimeStr = endYear + "-" + FormatUtils.twoNumber(endMonth) + "-" + FormatUtils.twoNumber(endDay) + " " + FormatUtils.twoNumber(endHour) + ":" + FormatUtils.twoNumber(endMinute);
        tvEndTime.setText(endTimeStr);
        etMissionRemark.setText(entity.getDescribe());
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (state == DeployListFragment.NOT_RUN) {
                    runOnUiThread(() -> {
                        controlAgain = true;
                        onClick(tvStartTime);
                    });
                } else if (state == DeployListFragment.EXPIRED) {
                    runOnUiThread(() -> {
                        controlAgain = true;
                        onClick(tvEndTime);
                    });
                }
            }
        };
        etMissionRemark.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkChange();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        tvSave.setEnabled(false);
        if (!alreadyShown) {
            new Timer().schedule(timerTask, 500);
            alreadyShown = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        KeyboardUtils.hideSoftInput(this);
    }

    @Override
    public void onDeleteSuccess() {
        EventBus.getDefault().post(new ModifyDeployListEvent());
        LogUploadUtil.uploadLog(new LogUploadRequest(107500, 107502, String.format("删除人员追踪任务【%s】", entity.getName())));
        finish();
    }

    @Override
    public void onModifySuccess(int type) {
        if (type == MODIFY) {
            ToastUtils.showShort("保存成功！");
            if (controlAgain) {
                controlAgain = false;
                LogUploadUtil.uploadLog(new LogUploadRequest(107500, 107504, String.format("重新布控人员追踪任务【%s】", entity.getName())));
            } else {
                LogUploadUtil.uploadLog(new LogUploadRequest(107500, 107505, String.format("编辑布控人员追踪任务【%s】", entity.getName())));
            }
        } else {
            LogUploadUtil.uploadLog(new LogUploadRequest(107500, 107503, String.format("开启/暂停人员追踪任务【%s】", entity.getName())));
            ToastUtils.showShort("操作成功！");
        }
        EventBus.getDefault().post(new ModifyDeployListEvent());
        mPresenter.getData(id);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timerTask != null) {
            timerTask.cancel();
        }
    }

    private void checkChange() {
        boolean changed = false;
        changed |= !TextUtils.equals(tvThreshold.getText(), entity.getAlarmThreshold());
        changed |= !TextUtils.equals(tvStartTime.getText(), startTimeStr);
        changed |= !TextUtils.equals(tvEndTime.getText(), endTimeStr);
        changed |= !TextUtils.equals(etMissionRemark.getText(), entity.getDescribe());
        tvSave.setEnabled(changed);
    }
}
