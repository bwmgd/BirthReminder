package com.example.birthreminder.ui.main;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.birthreminder.R;
import com.example.birthreminder.application.BirthApplication;
import com.example.birthreminder.dao.AppDatabase;
import com.example.birthreminder.dao.BirthDateDao;
import com.example.birthreminder.dao.PeopleDao;
import com.example.birthreminder.entity.BirthDate;
import com.example.birthreminder.entity.People;
import com.example.birthreminder.entity.Reminder;
import com.example.birthreminder.ui.birth.BirthActivity;
import com.example.birthreminder.util.BirthUtil;
import com.example.birthreminder.util.BirthUtil.SPECIAL_BIRTHDAY;
import top.defaults.colorpicker.ColorPickerPopup;

import java.util.ArrayList;
import java.util.List;

public class AddPeopleDialog extends Dialog implements
        DatePickerDialog.OnDatePickListener {
    private DatePickerDialog datePicker;
    private EditText editTextPersonName;
    private EditText editTextDate;
    private EditText editTextPhone;
    private View imageView;

    private int year;
    private int month;
    private int day;
    private boolean isLunar;
    private String name;
    private String phone;
    private int color;
    private int birthCode = SPECIAL_BIRTHDAY.NONE;

    private AddPeopleListener addPeopleListener;
    private UpdateListener updateListener;

    public AddPeopleDialog(@NonNull Context context) {
        super(context, R.style.dialog);
    }

    /**
     * 通过人物获取后续生日日期
     *
     * @param people 人物
     * @return 日期列表
     */
    private static List<BirthDate> getBirthDates(People people) {
        List<BirthDate> list = new ArrayList<>();
        for (int[] date : BirthUtil.calculateBirthday(
                people.getYear(), people.getMonth(), people.getDay(), people.getBirthCode())) {
            BirthDate birthDate = new BirthDate();
            birthDate.setYear(date[0]);
            birthDate.setMonth(date[1]);
            birthDate.setDay(date[2]);
            birthDate.setPeopleId(people.getId());
            list.add(birthDate);
        }
        return list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_brith_dialog);
        initView();
    }

    private void initView() {
        editTextPersonName = findViewById(R.id.editTextPersonName);
        editTextDate = findViewById(R.id.editTextDate);
        editTextDate.setFocusable(false);
        editTextPhone = findViewById(R.id.editTextPhone);
        color = BirthUtil.getRandomColor();
        imageView = findViewById(R.id.colorImageView);
        imageView.setBackgroundColor(color);
        imageView.setOnClickListener(this::getColor);
        findViewById(R.id.getPeopleButton).setOnClickListener(v -> getPeople());
        editTextDate.setOnClickListener(v -> getDate());
        findViewById(R.id.getDateButton).setOnClickListener(v -> getDate());
        findViewById(R.id.saveButton).setOnClickListener(v -> save());
    }

    private void save() { //人物保存
        name = editTextPersonName.getText().toString().trim();
        phone = editTextPhone.getText().toString().trim();

        if (name.equals("")) {
            Toast.makeText(getContext(), "要有名字哦~", Toast.LENGTH_LONG).show();
            return;
        }

        if (month < 0) getLunarLeapDialog(); //当生日为特殊生日时进行特殊生日弹窗
        else if (isLunar) getLunarDialog();
        else if (month == 2 && day == 29) getLeapDialog();
        else newPeople();
    }

    /**
     * 公历2月29生日弹窗
     */
    private void getLeapDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.leap_dialog);
        dialog.findViewById(R.id.button_2_28).setOnClickListener(v -> {
            birthCode = SPECIAL_BIRTHDAY.BEFORE;
            newPeople();
            dialog.dismiss();
        });
        dialog.findViewById(R.id.button_2_29).setOnClickListener(v -> {
            birthCode = SPECIAL_BIRTHDAY.ONLY;
            newPeople();
            dialog.dismiss();
        });
        dialog.findViewById(R.id.button_3_1).setOnClickListener(v -> {
            birthCode = SPECIAL_BIRTHDAY.AFTER;
            newPeople();
            dialog.dismiss();
        });
        dialog.show();
    }

    /**
     * 农历生日弹窗
     */
    private void getLunarDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.lunar_dialog);
        dialog.findViewById(R.id.button_only).setOnClickListener(v -> {
            birthCode = SPECIAL_BIRTHDAY.LUNAR;
            newPeople();
            dialog.dismiss();
        });
        dialog.findViewById(R.id.button_leap_normal).setOnClickListener(v -> {
            birthCode = SPECIAL_BIRTHDAY.LEAP_AND_NORMAL;
            newPeople();
            dialog.dismiss();
        });
        dialog.show();
    }

    /**
     * 农历闰月生日弹窗
     */
    private void getLunarLeapDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.lunar_leap_dialog);
        dialog.findViewById(R.id.button_only_leap).setOnClickListener(v -> {
            birthCode = SPECIAL_BIRTHDAY.ONLY_LEAP;
            newPeople();
            dialog.dismiss();
        });
        dialog.findViewById(R.id.button_leap_normal).setOnClickListener(v -> {
            birthCode = SPECIAL_BIRTHDAY.LEAP_AND_NORMAL;
            newPeople();
            dialog.dismiss();
        });
        dialog.findViewById(R.id.button_normal).setOnClickListener(v -> {
            birthCode = SPECIAL_BIRTHDAY.NORMAL;
            newPeople();
            dialog.dismiss();
        });
        dialog.show();
    }

    /**
     * 新人物插入
     */
    private void newPeople() {
        People people = new People();
        people.setName(name);
        people.setYear(year);
        people.setMonth(month);
        people.setDay(day);
        people.setPhone(phone);
        people.setColor(color);
        people.setBirthCode(birthCode);
        AppDatabase instance = AppDatabase.getInstance(getContext());
        if (updateListener == null) {
            new Thread(() -> {
                PeopleDao peopleDao = instance.getPeopleDao();
                people.setId(peopleDao.insert(people));
                newBirthDates(people, instance);
            }).start();
        }
        else {
            long[] longs = updateListener.OnUpdateListener(people);
            if (longs[0] == UpdateListener.NOT_BIRTH) {
                new Thread(() -> {
                    people.setId(longs[1]);
                    PeopleDao peopleDao = instance.getPeopleDao();
                    peopleDao.update(people);
                    Intent intent = new Intent(getContext(), BirthActivity.class);
                    intent.putExtra("people", people);
                    getOwnerActivity().startActivity(intent);
                }).start();
            }
            else if (longs[0] == UpdateListener.BIRTH) {
                new Thread(() -> {
                    people.setId(longs[1]);
                    instance.getReminderDao().deleteByPeopleId(people.getId());
                    instance.getSMSDao().deleteByPeopleId(people.getId());
                    BirthDateDao birthDateDao = instance.getBirthDateDao();
                    birthDateDao.deleteByPeopleId(people.getId());
                    PeopleDao peopleDao = instance.getPeopleDao();
                    peopleDao.update(people);
                    newBirthDates(people, instance);
                }).start();
            }
        }
        dismiss();
    }

    /**
     * 新生日日期插入
     *
     * @param people   人物
     * @param database 数据库
     */
    private void newBirthDates(People people, AppDatabase database) {
        BirthDateDao birthDateDao = database.getBirthDateDao();
        for (long i : birthDateDao.insert(getBirthDates(people))) {
            Reminder reminder = new Reminder();
            reminder.setPeopleId(people.getId());
            reminder.setBirthDateId(i);
            reminder.setContent(people.getName() + "的生日到了,记得祝福TA哦");
            database.getReminderDao().insert(reminder);
        }
        Intent intent = new Intent(getContext(), BirthActivity.class);
        intent.putExtra(BirthApplication.PEOPLE, people);
        getOwnerActivity().startActivity(intent);
    }

    /**
     * 获取颜色弹窗
     *
     * @param v 弹窗按钮
     */
    private void getColor(View v) {
        new ColorPickerPopup.Builder(getContext())
                .initialColor(color) // Set initial color
                .enableBrightness(true) // Enable brightness slider or not
                .enableAlpha(true) // Enable alpha slider or not
                .okTitle("选择")
                .cancelTitle("取消")
                .showIndicator(true)
                .showValue(true)
                .build()
                .show(v, new ColorPickerPopup.ColorPickerObserver() {
                    @Override
                    public void onColorPicked(int color) {
                        v.setBackgroundColor(color);
                        AddPeopleDialog.this.color = color;
                    }
                });
    }

    /**
     * 从联系人中获取人物
     */
    private void getPeople() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getOwnerActivity(),
                    new String[]{Manifest.permission.READ_CONTACTS}, 1);
        }
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        getOwnerActivity().startActivityForResult(intent, BirthApplication.CONTACT_REQUEST_CODE);
    }

    /**
     * 用日期选择弹窗获取日期
     */
    private void getDate() {
        if (datePicker == null) {
            datePicker = new DatePickerDialog(getContext());
            datePicker.setDatePickListener(this);
        }
        datePicker.show();
        datePicker.setLunar(isLunar);
        datePicker.init(year, month - 1, day);
    }

    /**
     * 日期选择事件回调
     *
     * @param year       公历年
     * @param month      公历月
     * @param day        公历日
     * @param lunarYear  农历年
     * @param lunarMonth 农历月
     * @param lunarDay   农历日
     * @param isLunar    是否为农历
     */
    @Override
    public void onDatePick(int year, int month, int day, int lunarYear, int lunarMonth, int lunarDay, boolean isLunar) {
        if (isLunar) setDate(lunarYear, lunarMonth, lunarDay, true);
        else setDate(year, month, day, false);
        if (addPeopleListener != null) addPeopleListener.onDatePicking(year, month, day);
        datePicker.cancel();
    }

    public void setDate(int year, int month, int day, boolean isLunar) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.isLunar = isLunar;
        editTextDate.setText(BirthUtil.formatDate(year, month, day, isLunar));
    }

    public void setName(String name) {
        this.name = name;
        editTextPersonName.setText(name);
    }

    public void setPhone(String phone) {
        this.phone = phone;
        editTextPhone.setText(phone);
    }

    public void setColor(int color) {
        this.color = color;
        imageView.setBackgroundColor(color);
    }

    public void setDatePickerListener(AddPeopleListener addPeopleListener) {
        this.addPeopleListener = addPeopleListener;
    }

    public void setUpdateListener(UpdateListener updateListener) {
        this.updateListener = updateListener;
    }

    /**
     * 添加人物接口
     */
    public interface AddPeopleListener {
        void onDatePicking(int year, int month, int day);
    }

    /**
     * 更新人物接口
     */
    public interface UpdateListener {
        long NONE = -1; //不更新
        long NOT_BIRTH = 0; //不更新生日
        long BIRTH = 1; //更新全部

        long[] OnUpdateListener(People people);
    }
}
