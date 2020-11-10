package com.example.birthreminder.calendar;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import cn.carbs.android.gregorianlunarcalendar.library.util.Util;
import com.example.birthreminder.BirthApplication;
import com.example.birthreminder.BirthUtil;
import com.example.birthreminder.BirthUtil.SPECIAL_BIRTHDAY;
import com.example.birthreminder.PeopleActivity;
import com.example.birthreminder.R;
import com.example.birthreminder.dao.AppDatabase;
import com.example.birthreminder.dao.BirthDateDao;
import com.example.birthreminder.dao.PeopleDao;
import com.example.birthreminder.entity.BirthDate;
import com.example.birthreminder.entity.People;
import top.defaults.colorpicker.ColorPickerPopup;

import java.util.ArrayList;
import java.util.List;

public class AddPeopleDialog extends Dialog implements
        DatePickerDialog.OnDatePickListener,
        LeapDialog.SpecialBirthdayListener {
    private DatePickerDialog datePicker;
    private EditText editTextPersonName;
    private EditText editTextDate;
    private EditText editTextPhone;

    private int year;
    private int month;
    private int day;
    private boolean isLunar;
    private String name;
    private String phone;
    private int color;
    private int birthCode = SPECIAL_BIRTHDAY.NONE;

    private AddPeopleListener addPeopleListener;

    public AddPeopleDialog(@NonNull Context context) {
        super(context, R.style.dialog);
    }

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
        editTextPersonName = findViewById(R.id.editTextPersonName);
        editTextDate = findViewById(R.id.editTextDate);
        editTextDate.setFocusable(false);
        editTextPhone = findViewById(R.id.editTextPhone);
        color = BirthUtil.getRandomColor();
        View imageView = findViewById(R.id.colorImageView);
        imageView.setBackgroundColor(color);
        imageView.setOnClickListener(this::getColor);
        findViewById(R.id.getPeopleButton).setOnClickListener(v -> getPeople());
        editTextDate.setOnClickListener(v -> getDate());
        findViewById(R.id.getDateButton).setOnClickListener(v -> getDate());
        findViewById(R.id.button_save).setOnClickListener(v -> save());
    }

    private void save() {
        name = editTextPersonName.getText().toString();
        phone = editTextPhone.getText().toString();
        if (name.equals("")) {
            Toast.makeText(getContext(), "要有名字哦~", Toast.LENGTH_LONG).show();
            return;
        }
        if (isLunar) {
            LeapDialog leapDialog = new LeapDialog(getContext());
            leapDialog.setContentView(month < 0 ? R.layout.lunar_leap_dialog : R.layout.lunar_dialog);
            leapDialog.setListener(this);
            leapDialog.show();
        }
        else if (month == 2 && day == 29) {
            LeapDialog leapDialog = new LeapDialog(getContext());
            leapDialog.setContentView(R.layout.leap_dialog);
            leapDialog.setListener(this);
            leapDialog.show();
        }
        else newPeople();
    }

    private void newPeople() {
        new Thread(() -> {
            PeopleDao peopleDao = AppDatabase.getInstance(getContext()).getPeopleDao();
            People people = new People();
            people.setName(name);
            people.setYear(year);
            people.setMonth(month);
            people.setDay(day);
            people.setPhone(phone);
            people.setColor(color);
            people.setBirthCode(birthCode);
            people.setId(peopleDao.insert(people));
            BirthDateDao birthDateDao = AppDatabase.getInstance(getContext()).getBirthDateDao();
            birthDateDao.insert(getBirthDates(people));
            Log.v("database", "peopleInsert");
            Intent intent = new Intent(getContext(), PeopleActivity.class);
            intent.putExtra("people", people);
            getOwnerActivity().startActivity(intent);
        }).start();
        dismiss();
    }

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

    private void getDate() {
        if (datePicker == null) {
            datePicker = new DatePickerDialog(getContext());
            datePicker.setDatePickListener(this);
        }
        datePicker.show();
        datePicker.setLunar(isLunar);
        datePicker.init(year, month - 1, day);
    }

    @Override
    public void onDatePick(int year, int month, int day, int lunarYear, int lunarMonth, int lunarDay, boolean isLunar) {
        if (isLunar) setDate(lunarYear, lunarMonth, lunarDay, true);
        else setDate(year, month, day, false);
        if (addPeopleListener != null) addPeopleListener.onDatePicking(year, month, day);
        datePicker.cancel();
    }

    private String formatDate() {
        if (isLunar) return Util.getLunarNameOfYear(year) + "年"
                + (month < 0 ? "闰" : "") + Util.getLunarNameOfMonth(Math.abs(month)) + "月"
                + Util.getLunarNameOfDay(day) + "日";
        return year + "-" + month + "-" + day;
    }

    public void setDate(int year, int month, int day, boolean isLunar) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.isLunar = isLunar;
        editTextDate.setText(formatDate());
    }

    public void setName(String name) {
        this.name = name;
        editTextPersonName.setText(name);
    }

    public void setPhone(String phone) {
        this.phone = phone;
        editTextPhone.setText(phone);
    }

    public void setDatePickerListener(AddPeopleListener addPeopleListener) {
        this.addPeopleListener = addPeopleListener;
    }

    @Override
    public void onSpecialChoose(View v) {
        switch (v.getId()) {
            case R.id.button_2_28:
                birthCode = SPECIAL_BIRTHDAY.BEFORE;
                break;
            case R.id.button_2_29:
                birthCode = SPECIAL_BIRTHDAY.ONLY;
                break;
            case R.id.button_3_1:
                birthCode = SPECIAL_BIRTHDAY.AFTER;
                break;
            case R.id.button_only:
                birthCode = SPECIAL_BIRTHDAY.LUNAR;
                break;
            case R.id.button_only_leap:
                birthCode = SPECIAL_BIRTHDAY.ONLY_LEAP;
                break;
            case R.id.button_leap_normal:
                birthCode = SPECIAL_BIRTHDAY.LEAP_AND_NORMAL;
                break;
            case R.id.button_normal:
                birthCode = SPECIAL_BIRTHDAY.NORMAL;
                break;
        }
        newPeople();
    }

    public interface AddPeopleListener {
        void onDatePicking(int year, int month, int day);
    }
}
