package com.example.birthreminder.calendar;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import cn.carbs.android.gregorianlunarcalendar.library.data.ChineseCalendar;
import cn.carbs.android.gregorianlunarcalendar.library.view.GregorianLunarCalendarView;
import cn.carbs.android.indicatorview.library.IndicatorView;
import com.example.birthreminder.R;

import java.util.Calendar;

/**
 * Created by carbs on 2016/7/12.
 */

public class DatePickerDialog extends Dialog {

    private GregorianLunarCalendarView mCalendarView;
    private boolean isLunar = false;
    private OnDatePickListener onDatePickListener;

    public DatePickerDialog(Context context) {
        super(context, R.style.dialog);
    }

    public void setDatePickListener(OnDatePickListener onDatePickListener) {
        this.onDatePickListener = onDatePickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_picker_dialog);
        mCalendarView = this.findViewById(R.id.calendar_view);
        IndicatorView mIndicatorView = this.findViewById(R.id.indicator_view);
        mIndicatorView.setOnIndicatorChangedListener((oldSelectedIndex, newSelectedIndex) -> {
            isLunar = newSelectedIndex == 1;
            if (newSelectedIndex == 0) toGregorianMode();
            else toLunarMode();
        });

        this.findViewById(R.id.button_get_data).setOnClickListener(v -> {

            GregorianLunarCalendarView.CalendarData calendarData = mCalendarView.getCalendarData();
            Calendar calendar = calendarData.getCalendar();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            String showStr = "Gregorian : " + year + "-"
                    + month + "-" + day + "\n"
                    + "Lunar     : " + calendar.get(ChineseCalendar.CHINESE_YEAR) + "-"
                    + (calendar.get(ChineseCalendar.CHINESE_MONTH)) + "-"
                    + calendar.get(ChineseCalendar.CHINESE_DATE);
            Log.v("Date", showStr);
            if (onDatePickListener != null) onDatePickListener.onButtonClick(year, month, day, isLunar);
        });
        mCalendarView.init();
        if (isLunar) toLunarMode();
        else toGregorianMode();
        this.setCanceledOnTouchOutside(true);
    }

    private void toGregorianMode() {
        mCalendarView.toGregorianMode();
    }

    private void toLunarMode() {
        mCalendarView.toLunarMode();
    }

    public interface OnDatePickListener {
        void onButtonClick(int year, int month, int day, boolean isLunar);
    }
}