package com.example.birthreminder;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.example.birthreminder.dao.AppDatabase;
import com.example.birthreminder.dao.PeopleDao;
import com.example.birthreminder.entity.BirthDate;
import com.example.birthreminder.entity.People;

import java.util.Objects;

public class PeopleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);
        setSupportActionBar(findViewById(R.id.toolbar));

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);  //菜单栏返回按钮
        getSupportActionBar().setHomeButtonEnabled(true);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        People people = (People) getIntent().getSerializableExtra("people");
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
            PeopleDao peopleDao = AppDatabase.getInstance(this).getPeopleDao();
            People.PeopleWithBirthDates peopleWithBirthDates = peopleDao.getBirthDatesById(people.getId());
            BirthAdapter birthAdapter = new BirthAdapter(peopleWithBirthDates, this);
            for (BirthDate birthDate : peopleWithBirthDates.birthDates) {
                Log.v("birth", birthDate.toString());
            }
            recyclerView.setAdapter(birthAdapter);
            recyclerView.addItemDecoration(new DividerItemDecoration(PeopleActivity.this, OrientationHelper.VERTICAL));
        }).start();
    }
}