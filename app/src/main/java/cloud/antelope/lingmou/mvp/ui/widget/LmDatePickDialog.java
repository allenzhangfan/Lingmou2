package cloud.antelope.lingmou.mvp.ui.widget;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import java.lang.reflect.Field;
import java.util.Calendar;

import cloud.antelope.lingmou.R;

/**
 * 作者：陈新明
 * 创建日期：2018/01/29
 * 邮箱：chenxinming@antelop.cloud
 * 描述：TODO
 */

public class LmDatePickDialog extends AlertDialog implements View.OnClickListener,
        DatePicker.OnDateChangedListener {


    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String DAY = "day";


    private final DatePicker mDatePicker;

    private DatePickerDialog.OnDateSetListener mDateSetListener;


    public LmDatePickDialog(@NonNull Context context) {
        this(context, null, Calendar.getInstance(), -1, -1, -1);
    }

    public LmDatePickDialog(@NonNull Context context,
                            @Nullable DatePickerDialog.OnDateSetListener listener, @Nullable Calendar calendar, int year,
                            int monthOfYear, int dayOfMonth) {
        super(context, 0);

        final Context themeContext = getContext();
        final LayoutInflater inflater = LayoutInflater.from(themeContext);
        final View view = inflater.inflate(R.layout.lm_date_picker_diaog, null);
        final Button okBtn = view.findViewById(R.id.ok_btn);
        final Button cancelBtn = view.findViewById(R.id.cancel_btn);
        okBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        setView(view);


        if (calendar != null) {
            year = calendar.get(Calendar.YEAR);
            monthOfYear = calendar.get(Calendar.MONTH);
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        }

        mDatePicker = (DatePicker) view.findViewById(R.id.datePicker);
        mDatePicker.init(year, monthOfYear, dayOfMonth, this);
        mDateSetListener = listener;
        Resources system = Resources.getSystem();
        int dayId = system.getIdentifier("day", "id", "android");
        int monthId = system.getIdentifier("month", "id", "android");
        int yearId = system.getIdentifier("year", "id", "android");

        NumberPicker dayPicker = (NumberPicker) mDatePicker.findViewById(dayId);
        NumberPicker monthPicker = (NumberPicker) mDatePicker.findViewById(monthId);
        NumberPicker yearPicker = (NumberPicker) mDatePicker.findViewById(yearId);

        setNumberPickerDividerColour(dayPicker);
        setNumberPickerDividerColour(monthPicker);
        setNumberPickerDividerColour(yearPicker);
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

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mDatePicker.init(year, monthOfYear, dayOfMonth, this);
    }

    public void setOnDateSetListener(@Nullable DatePickerDialog.OnDateSetListener listener) {
        mDateSetListener = listener;
    }


    @NonNull
    public DatePicker getDatePicker() {
        return mDatePicker;
    }

    public void updateDate(int year, int month, int dayOfMonth) {
        mDatePicker.updateDate(year, month, dayOfMonth);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_btn:
                if (mDateSetListener != null) {
                    // Clearing focus forces the dialog to commit any pending
                    // changes, e.g. typed text in a NumberPicker.
                    mDatePicker.clearFocus();
                    mDateSetListener.onDateSet(mDatePicker, mDatePicker.getYear(),
                            mDatePicker.getMonth(), mDatePicker.getDayOfMonth());
                }
                break;
            case R.id.cancel_btn:
                cancel();
                break;
        }
    }
}
