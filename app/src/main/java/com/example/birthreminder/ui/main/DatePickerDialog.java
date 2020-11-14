package com.example.birthreminder.ui.main;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import cn.carbs.android.gregorianlunarcalendar.library.data.ChineseCalendar;
import cn.carbs.android.gregorianlunarcalendar.library.view.GregorianLunarCalendarView;
import cn.carbs.android.indicatorview.library.IndicatorView;
import com.example.birthreminder.R;
import com.haibin.calendarview.LunarUtil;

import java.util.Arrays;
import java.util.Calendar;

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
            if (isLunar = newSelectedIndex == 1) mCalendarView.toLunarMode();
            else mCalendarView.toGregorianMode();
        });

        this.findViewById(R.id.button_get_data).setOnClickListener(v -> {
            GregorianLunarCalendarView.CalendarData calendarData = mCalendarView.getCalendarData();
            Calendar calendar = calendarData.getCalendar();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int lunarYear = calendar.get(ChineseCalendar.CHINESE_YEAR);
            int lunarMonth = calendar.get(ChineseCalendar.CHINESE_MONTH);
            int lunarDay = calendar.get(ChineseCalendar.CHINESE_DATE);
            String showStr = "Gregorian : " + year + "-" + month + "-" + day + "\n"
                    + "Lunar     : " + lunarYear + "-" + lunarMonth + "-" + lunarDay;
            Log.v("Date", showStr);
            Log.v("lunarDate", Arrays.toString(
                    LunarUtil.lunarToSolar(lunarYear, lunarMonth, lunarDay, lunarMonth < 0)));
            if (onDatePickListener != null) {
                onDatePickListener.onDatePick(year, month, day, lunarYear, lunarMonth, lunarDay, isLunar);
            }
        });
        this.setCanceledOnTouchOutside(true);
    }

    public void init(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        mCalendarView.init(calendar);
        if (isLunar) mCalendarView.toLunarMode();
        else mCalendarView.toGregorianMode();
    }

    public void setLunar(boolean lunar) {
        isLunar = lunar;
    }

    /**
     * 获取日期后回调接口
     */
    public interface OnDatePickListener {
        void onDatePick(int year, int month, int day, int lunarYear, int lunarMonth, int lunarDay, boolean isLunar);
    }
}