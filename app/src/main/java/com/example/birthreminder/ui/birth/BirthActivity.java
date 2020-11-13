package com.example.birthreminder.ui.birth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.example.birthreminder.R;
import com.example.birthreminder.application.BirthApplication;
import com.example.birthreminder.dao.AppDatabase;
import com.example.birthreminder.dao.BirthDateDao;
import com.example.birthreminder.dao.PeopleDao;
import com.example.birthreminder.entity.People;
import com.example.birthreminder.ui.main.AddPeopleDialog;
import com.example.birthreminder.ui.main.MainActivity;
import com.example.birthreminder.util.BirthUtil;

import java.util.Objects;

public class BirthActivity extends AppCompatActivity implements AddPeopleDialog.UpdateListener {
    People people;
    AppDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birth);

        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        people = (People) getIntent().getSerializableExtra(BirthApplication.PEOPLE);
        Log.v("people", people.toString());

        TextView nameTextView = findViewById(R.id.nameTextView);
        nameTextView.setText(people.getName());
        nameTextView.setTextColor(people.getColor());
        ((TextView) findViewById(R.id.lunarTextView)).setText(
                people.getBirthCode() < BirthUtil.SPECIAL_BIRTHDAY.LUNAR ? "公历" : "农历");
        ((TextView) findViewById(R.id.yearTextView)).setText(String.valueOf(people.getYear()));
        ((TextView) findViewById(R.id.monthTextView)).setText(String.valueOf(people.getMonth()));
        ((TextView) findViewById(R.id.dayTextView)).setText(String.valueOf(people.getDay()));
        ((TextView) findViewById(R.id.phoneTextView)).setText(people.getPhone());

        new Thread(() -> {
            database = AppDatabase.getInstance(this);
            PeopleDao peopleDao = database.getPeopleDao();
            BirthDateDao birthDateDao = database.getBirthDateDao();
            int position = birthDateDao.getCount(people.getId());
            People.PeopleWithBirthDates peopleWithBirthDates = peopleDao.getBirthDatesById(people.getId());

            runOnUiThread(() -> {
                BirthAdapter birthAdapter = new BirthAdapter(peopleWithBirthDates, this);
                RecyclerView recyclerView = findViewById(R.id.recyclerView);
                LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(birthAdapter);
                recyclerView.addItemDecoration(new DividerItemDecoration(BirthActivity.this, OrientationHelper.VERTICAL));
                recyclerView.scrollToPosition(position);
                layoutManager.scrollToPositionWithOffset(position, 0);
            });
        }).start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.v("menu", menu.toString());
        getMenuInflater().inflate(R.menu.people, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.modify:
                AddPeopleDialog dialog = new AddPeopleDialog(this);
                dialog.show();
                dialog.setOwnerActivity(this);
                dialog.setDate(people.getYear(), people.getMonth(), people.getDay(),
                        people.getBirthCode() >= BirthUtil.SPECIAL_BIRTHDAY.LUNAR);
                dialog.setName(people.getName());
                dialog.setPhone(people.getPhone());
                dialog.setColor(people.getColor());
                dialog.setUpdateListener(this);
                break;
            case R.id.delete:
                new Thread(() -> {
                    BirthDateDao birthDateDao = database.getBirthDateDao();
                    birthDateDao.deleteByPeopleId(people.getId());
                    PeopleDao peopleDao = database.getPeopleDao();
                    peopleDao.delete(people);
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }).start();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public long[] OnUpdateListener(People people) {
        if (this.people.getYear() == people.getYear() &&
                this.people.getMonth() == people.getMonth() &&
                this.people.getDay() == people.getDay()) {
            if (this.people.getName().equals(people.getName()) && this.people.getPhone().equals(people.getPhone())) {
                return new long[]{AddPeopleDialog.UpdateListener.NONE, -1};
            }
            else return new long[]{AddPeopleDialog.UpdateListener.NOT_BIRTH, this.people.getId()};
        }
        else return new long[]{AddPeopleDialog.UpdateListener.BIRTH, this.people.getId()};
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
            startActivity(new Intent(this, MainActivity.class));
        return super.onKeyDown(keyCode, event);
    }
}