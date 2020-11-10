package com.example.birthreminder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.example.birthreminder.calendar.AddPeopleDialog;
import com.example.birthreminder.calendar.DatePickerDialog;
import com.example.birthreminder.dao.AppDatabase;
import com.example.birthreminder.dao.BirthDateDao;
import com.example.birthreminder.dao.PeopleDao;
import com.example.birthreminder.entity.BirthDate;
import com.example.birthreminder.entity.People;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements
        CalendarView.OnCalendarSelectListener,
        CalendarView.OnCalendarLongClickListener,
        DatePickerDialog.OnDatePickListener,
        AddPeopleDialog.AddPeopleListener {

    TextView mTextMonthDay;
    TextView mTextYear;
    TextView mTextLunar;
    TextView mTextCurrentDay;
    CalendarView mCalendarView;
    RelativeLayout mRelativeTool;
    private DatePickerDialog datePicker;
    private AddPeopleDialog addPeopleDialog;
    private int year;
    private int month;
    private int day;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
    }


    @SuppressLint("SetTextI18n")
    protected void initView() {
        mTextMonthDay = findViewById(R.id.tv_month_day);
        mTextYear = findViewById(R.id.tv_year);
        mTextLunar = findViewById(R.id.tv_lunar);
        mRelativeTool = findViewById(R.id.rl_tool);
        mCalendarView = findViewById(R.id.calendarView);
        mTextCurrentDay = findViewById(R.id.tv_current_day);
        mTextMonthDay.setOnClickListener(v -> {
            if (datePicker == null) {
                datePicker = new DatePickerDialog(this);
                datePicker.setDatePickListener(this);
            }
            datePicker.show();
            datePicker.init(year, month - 1, day);
        });
        findViewById(R.id.fl_current).setOnClickListener(v -> mCalendarView.scrollToCurrent());
        mCalendarView.setOnCalendarSelectListener(this);
        mCalendarView.setOnCalendarLongClickListener(this);
        year = mCalendarView.getCurYear();
        month = mCalendarView.getCurMonth();
        mTextYear.setText(String.valueOf(year));
        day = mCalendarView.getCurDay();
        mTextMonthDay.setText(month + "月" + day + "日");
        mTextCurrentDay.setText(String.valueOf(mCalendarView.getCurDay()));
        mCalendarView.setRange(1901, 1, 1, 2100, 12, 30);
    }

    private void initData() {
        new Thread(() -> {
            PeopleDao peopleDao = AppDatabase.getInstance(MainActivity.this).getPeopleDao();
            List<People.PeopleWithBirthDates> peoples = peopleDao.getPeopleWithBirthDates();

            List<People> allPeople = new ArrayList<>();
            List<Map<String, Object>> list = new ArrayList<>();
            Map<String, Calendar> map = new HashMap<>();

            for (People.PeopleWithBirthDates peopleWithBirthDates : peoples) {
                People people = peopleWithBirthDates.people;
                allPeople.add(people);
                for (BirthDate birthDate : peopleWithBirthDates.birthDates) {
                    Calendar calendar = new Calendar();
                    calendar.setYear(birthDate.getYear());
                    calendar.setMonth(birthDate.getMonth());
                    calendar.setDay(birthDate.getDay());
                    calendar.setSchemeColor(people.getColor());
                    calendar.setScheme(people.getName().substring(0, 1));
                    map.put(calendar.toString(), calendar);
                    if (birthDate.getYear() == year && birthDate.getMonth() == month) {
                        Map<String, Object> objectMap = new HashMap<>();
                        objectMap.put("people", people);
                        objectMap.put("birth", birthDate);
                        list.add(objectMap);
                    }
                }
            }
            RecyclerView recyclerView = findViewById(R.id.peopleList);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new PeopleAdapter(allPeople, this));
            recyclerView.addItemDecoration(new DividerItemDecoration(this, OrientationHelper.VERTICAL));

            mCalendarView.setSchemeDate(map);
            RecyclerView birthRecyclerView = findViewById(R.id.birthRecyclerView);
            birthRecyclerView.setAdapter(new BirthAdapter(list, MainActivity.this));
            birthRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            birthRecyclerView.addItemDecoration(new DividerItemDecoration(this, OrientationHelper.VERTICAL));
        }).start();
    }

    @Override
    public void onCalendarOutOfRange(Calendar calendar) {
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        year = calendar.getYear();
        if (month != calendar.getMonth()) {
            month = calendar.getMonth();
            new Thread(() -> {
                List<Map<String, Object>> list = new ArrayList<>();
                BirthDateDao birthDateDao = AppDatabase.getInstance(this).getBirthDateDao();
                PeopleDao peopleDao = AppDatabase.getInstance(this).getPeopleDao();
                for (BirthDate birthDate : birthDateDao.getBirthDatesByMonth(year, month)) {
                    Map<String, Object> objectMap = new HashMap<>();
                    objectMap.put("people", peopleDao.getPeopleById(birthDate.getPeopleId()));
                    objectMap.put("birth", birthDate);
                    list.add(objectMap);
                }
                RecyclerView birthRecyclerView = findViewById(R.id.birthRecyclerView);
                birthRecyclerView.setAdapter(new BirthAdapter(list, MainActivity.this));
            }).start();
        }
        day = calendar.getDay();
        mTextMonthDay.setText(month + "月" + day + "日");
        mTextYear.setText(String.valueOf(year));
        mTextLunar.setText(calendar.getLunar());
        Log.v("onClick", String.valueOf(mCalendarView.getSelectedCalendar().getDay()));
    }

    @Override
    public void onCalendarLongClickOutOfRange(Calendar calendar) {

    }

    @Override
    public void onCalendarLongClick(Calendar calendar) {
        onCalendarSelect(calendar, false);
        addPeopleDialog = new AddPeopleDialog(this);
        addPeopleDialog.setOwnerActivity(this);
        addPeopleDialog.setDatePickerListener(this);
        addPeopleDialog.show();
        addPeopleDialog.setDate(year, month, day, false);
    }

    @Override
    public void onDatePick(int year, int month, int day, int lunarYear, int lunarMonth, int lunarDay, boolean isLunar) {
        mCalendarView.scrollToCalendar(year, month, day);
        datePicker.cancel();
    }

    @Override
    public void onDatePicking(int year, int month, int day) {
        mCalendarView.scrollToCalendar(year, month, day);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("main", "res");
        if (data == null) return;
        if (resultCode == Activity.RESULT_OK) {
            try {//选择通讯录联系人返回
                if (data.getData() != null) {
                    Cursor cursor = this.getContentResolver()
                            .query(data.getData(),
                                    new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME},
                                    null, null, null);
                    while (cursor.moveToNext()) {
                        //取出该条数据的联系人姓名
                        String name = cursor.getString(1).replaceAll(" ", "");
                        //取出该条数据的联系人的手机号
                        String number = cursor.getString(0).replaceAll(" ", "").replaceAll("-", "");
                        if (number.length() > 11) number = number.substring(number.length() - 11);
                        Log.v("CONTENT", name + ", " + number);
                        if (addPeopleDialog != null) {
                            addPeopleDialog.setName(name);
                            addPeopleDialog.setPhone(number);
                        }
                    }
                    cursor.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}