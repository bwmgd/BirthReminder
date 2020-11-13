package com.example.birthreminder.ui.scheme;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.example.birthreminder.R;
import com.example.birthreminder.application.BirthApplication;
import com.example.birthreminder.dao.AppDatabase;
import com.example.birthreminder.entity.BirthDate;
import com.example.birthreminder.entity.People;
import com.example.birthreminder.entity.Reminder;
import com.example.birthreminder.entity.SMS;
import com.example.birthreminder.ui.main.MainActivity;
import com.example.birthreminder.util.BirthUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SchemeActivity extends AppCompatActivity implements DialogInterface.OnDismissListener {

    ReminderAdapter adapter;
    List<Reminder> reminderList;
    AppDatabase appDatabase;
    private People people;
    private BirthDate birthDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheme);

        people = (People) getIntent().getSerializableExtra(BirthApplication.PEOPLE);
        birthDate = (BirthDate) getIntent().getSerializableExtra(BirthApplication.BIRTH);

        initView();
        initData();
    }

    private void initView() {
        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        TextView nameTextView = findViewById(R.id.nameTextView);
        nameTextView.setText(people.getName());
        nameTextView.setTextColor(people.getColor());
        int year = birthDate.getYear();
        int month = birthDate.getMonth();
        int day = birthDate.getDay();
        ((TextView) findViewById(R.id.ageTextView)).setText(String.valueOf(
                BirthUtil.diff(people.getYear(), people.getMonth(), people.getDay(),
                        year, month, day)[0]));
        int[] param = BirthUtil.diff(year, month, day);
        ((TextView) findViewById(R.id.alsoTextView)).setText(param[3] < 0 ? "过了" : "还有");
        ((TextView) findViewById(R.id.yearTextView)).setText(String.valueOf(param[0]));
        ((TextView) findViewById(R.id.monthTextView)).setText(String.valueOf(param[1]));
        ((TextView) findViewById(R.id.dayTextView)).setText(String.valueOf(param[2]));
        ((TextView) findViewById(R.id.dateTextView)).setText("公历" + BirthUtil.formatDate(year, month, day));
        ((TextView) findViewById(R.id.lunarTextView)).setText("农历" + BirthUtil.formatToLunarDate(year, month, day));
        findViewById(R.id.addFab).setOnClickListener(v -> adapter.addVoidItem());
    }


    private void initData() {
        appDatabase = AppDatabase.getInstance(this);
        new Thread(() -> {
            getSMS();
            reminderList = appDatabase.getReminderDao().getRemindersFromDate(birthDate.getId());
            runOnUiThread(() -> {
                RecyclerView recyclerView = findViewById(R.id.recyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.addItemDecoration(new DividerItemDecoration(this, OrientationHelper.VERTICAL));
                adapter = new ReminderAdapter(reminderList, people.getId(), birthDate.getId());
                recyclerView.setAdapter(adapter);
            });
        }).start();
    }

    private void getSMS() {
        SMS sms = appDatabase.getSMSDao().getSMSFromDate(birthDate.getId());
        this.runOnUiThread(() -> ((ImageView) findViewById(R.id.imageView)).setImageDrawable(ContextCompat.getDrawable(
                this, sms == null ? android.R.drawable.sym_action_email : android.R.drawable.sym_action_chat)));
        findViewById(R.id.bt_sms).setOnClickListener(v ->
        {
            SMSDialog dialog = new SMSDialog(this, people, sms, birthDate.getId());
            dialog.setOnDismissListener(this);
            dialog.show();
        });
    }

    @Override
    protected void onStop() {
        List<Reminder> reminderList = new ArrayList<>(this.reminderList);
        new Thread(() -> {
            for (Reminder reminder : reminderList)
                if (reminder.getContent().equals("") || reminder.getContent() == null)
                    appDatabase.getReminderDao().delete(reminder);
        }).start();
        super.onStop();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        new Thread(this::getSMS).start();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(this, MainActivity.class));
        }
        return false;
    }
}