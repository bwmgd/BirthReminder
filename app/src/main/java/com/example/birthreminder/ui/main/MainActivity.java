package com.example.birthreminder.ui.main;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.example.birthreminder.R;
import com.example.birthreminder.application.BirthApplication;
import com.example.birthreminder.dao.AppDatabase;
import com.example.birthreminder.dao.PeopleDao;
import com.example.birthreminder.entity.BirthDate;
import com.example.birthreminder.entity.People;
import com.example.birthreminder.ui.birth.BirthAdapter;
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

    List<People.PeopleWithBirthDates> peoples;
    RecyclerView birthRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendSMS();
        initView();
        initData();
    }

    private void sendSMS() {

        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + "17600736674"));
        intent.putExtra("sms_body", "测试用户生日快乐");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, BirthApplication.CHANNEL_SMS_ID)
                .setSmallIcon(R.mipmap.ic_calendar)
                .setContentTitle("测试用户的18岁生日到了,点击此处发送送短信来祝福TA吧")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("测试用户生日快乐"))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(100000, builder.build());
    }


    protected void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_my_calendar);
        DrawerLayout drawerLayout = findViewById(R.id.nav_view);
        //DrawerLayout监听器
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.app_name,
                R.string.app_name
        );
        drawerLayout.addDrawerListener(toggle); //添加侧边抽屉菜单按钮
        toggle.syncState();


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

        birthRecyclerView = findViewById(R.id.birthRecyclerView);
        birthRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        birthRecyclerView.addItemDecoration(new DividerItemDecoration(this, OrientationHelper.VERTICAL));
    }

    private void initData() {
        new Thread(() -> {
            PeopleDao peopleDao = AppDatabase.getInstance(MainActivity.this).getPeopleDao();
            peoples = peopleDao.getPeopleWithBirthDates();

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
                    getBirthMonthList(list, people, birthDate);
                }
            }
            runOnUiThread(() -> {
                RecyclerView recyclerView = findViewById(R.id.peopleList);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(new PeopleAdapter(allPeople, this));
                recyclerView.addItemDecoration(new DividerItemDecoration(this, OrientationHelper.VERTICAL));

                mCalendarView.setSchemeDate(map);
                birthRecyclerView.setAdapter(new BirthAdapter(list, MainActivity.this));
            });
        }).start();
    }

    @Override
    public void onCalendarOutOfRange(Calendar calendar) {
    }

    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        year = calendar.getYear();
        if (month != calendar.getMonth()) {
            month = calendar.getMonth();
            if (peoples != null) {
                List<Map<String, Object>> list = new ArrayList<>();
                for (People.PeopleWithBirthDates peopleWithBirthDates : peoples) {
                    People people = peopleWithBirthDates.people;
                    for (BirthDate birthDate : peopleWithBirthDates.birthDates) {
                        getBirthMonthList(list, people, birthDate);
                    }
                }
                birthRecyclerView.setAdapter(new BirthAdapter(list, MainActivity.this));
            }
        }
        day = calendar.getDay();
        mTextMonthDay.setText(month + "月" + day + "日");
        mTextYear.setText(String.valueOf(year));
        mTextLunar.setText(calendar.getLunar());
        Log.v("onClick", String.valueOf(mCalendarView.getSelectedCalendar().getDay()));
    }

    /**
     * 获取本月过生日的信息
     *
     * @param list      人与日期的 HasMap列表
     * @param people    人物
     * @param birthDate 日期
     */
    private void getBirthMonthList(List<Map<String, Object>> list, People people, BirthDate birthDate) {
        if (birthDate.getYear() == year && birthDate.getMonth() == month) {
            Map<String, Object> objectMap = new HashMap<>();
            objectMap.put(BirthApplication.PEOPLE, people);
            objectMap.put(BirthApplication.BIRTH, birthDate);
            list.add(objectMap);
        }
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

    /**
     * 日期选择器选择日期后改变日历界面回调
     */
    @Override
    public void onDatePick(int year, int month, int day, int lunarYear, int lunarMonth, int lunarDay, boolean isLunar) {
        mCalendarView.scrollToCalendar(year, month, day);
        datePicker.cancel();
    }

    /**
     * 添加人物弹窗的跳转到生日位置回调
     */
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