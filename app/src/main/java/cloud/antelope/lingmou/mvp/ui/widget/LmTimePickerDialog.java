package cloud.antelope.lingmou.mvp.ui.widget;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import java.lang.reflect.Field;

import cloud.antelope.lingmou.R;

/**
 * 作者：陈新明
 * 创建日期：2018/01/29
 * 邮箱：chenxinming@antelop.cloud
 * 描述：TODO
 */

public class LmTimePickerDialog extends AlertDialog implements View.OnClickListener,
        TimePicker.OnTimeChangedListener {

    private static final String HOUR = "hour";
    private static final String MINUTE = "minute";
    private static final String IS_24_HOUR = "is24hour";

    private final TimePicker mTimePicker;
    private final TimePickerDialog.OnTimeSetListener mTimeSetListener;

    private final int mInitialHourOfDay;
    private final int mInitialMinute;
    private final boolean mIs24HourView;
    private final Button okBtn;

    protected LmTimePickerDialog(Context context) {
        this(context, 0, null, -1, -1, true);
    }


    public LmTimePickerDialog(Context context, int themeResId, TimePickerDialog.OnTimeSetListener listener,
                              int hourOfDay, int minute, boolean is24HourView) {
        super(context, 0);

        mTimeSetListener = listener;
        mInitialHourOfDay = hourOfDay;
        mInitialMinute = minute;
        mIs24HourView = is24HourView;

        final Context themeContext = getContext();
        final LayoutInflater inflater = LayoutInflater.from(themeContext);
        final View view = inflater.inflate(R.layout.lm_time_picker_diaog, null);
        setView(view);

        okBtn = view.findViewById(R.id.ok_btn);
        final Button cancelBtn = view.findViewById(R.id.cancel_btn);
        okBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        mTimePicker = (TimePicker) view.findViewById(R.id.timePicker);
        mTimePicker.setIs24HourView(mIs24HourView);
        mTimePicker.setCurrentHour(mInitialHourOfDay);
        mTimePicker.setCurrentMinute(mInitialMinute);
        mTimePicker.setOnTimeChangedListener(this);

        Resources system = Resources.getSystem();
        int hourNumberPickerId = system.getIdentifier("hour", "id", "android");
        int minuteNumberPickerId = system.getIdentifier("minute", "id", "android");
        int ampmNumberPickerId = system.getIdentifier("amPm", "id", "android");
        NumberPicker hourNumberPicker = (NumberPicker) mTimePicker.findViewById(hourNumberPickerId);
        NumberPicker minuteNumberPicker = (NumberPicker) mTimePicker.findViewById(minuteNumberPickerId);
        NumberPicker ampmNumberPicker = (NumberPicker) mTimePicker.findViewById(ampmNumberPickerId);
        setNumberPickerDividerColour(hourNumberPicker);
        setNumberPickerDividerColour(minuteNumberPicker);
        setNumberPickerDividerColour(ampmNumberPicker);
    }


    private void setNumberPickerDividerColour(NumberPicker number_picker){
        final int count = number_picker.getChildCount();

        for(int i = 0; i < count; i++){

            try{
                Field dividerField = number_picker.getClass().getDeclaredField("mSelectionDivider");
                dividerField.setAccessible(true);
                ColorDrawable colorDrawable = new ColorDrawable(getContext().getResources().getColor(R.color
                        .yellow_ff8f00));
                dividerField.set(number_picker,colorDrawable);

                number_picker.invalidate();
            }
            catch(NoSuchFieldException e){
            }
            catch(IllegalAccessException e){
            }
            catch(IllegalArgumentException e){
            }
        }
    }

    public TimePicker getTimePicker() {
        return mTimePicker;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_btn:
                if (mTimeSetListener != null) {
                    mTimeSetListener.onTimeSet(mTimePicker, mTimePicker.getCurrentHour(),
                            mTimePicker.getCurrentMinute());
                }
                dismiss();
                break;
            case R.id.cancel_btn:
                cancel();
                break;
        }
    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

    }

    public void updateTime(int hourOfDay, int minuteOfHour) {
        mTimePicker.setCurrentHour(hourOfDay);
        mTimePicker.setCurrentMinute(minuteOfHour);
    }

}
