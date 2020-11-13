package com.example.birthreminder.ui.scheme;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.example.birthreminder.R;
import com.example.birthreminder.dao.AppDatabase;
import com.example.birthreminder.entity.People;
import com.example.birthreminder.entity.SMS;

public class SMSDialog extends Dialog {
    private final People people;
    private final long birthId;
    private SMS sms;


    public SMSDialog(@NonNull Context context, People people, SMS sms, long birthId) {
        super(context);
        this.people = people;
        this.sms = sms;
        this.birthId = birthId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_dialog);
        ((TextView) findViewById(R.id.nameTextView)).setText(people.getName());
        EditText phoneTextView = findViewById(R.id.phoneText);
        EditText massageTextView = findViewById(R.id.contentText);
        phoneTextView.setText(people.getPhone());
        massageTextView.setText(sms == null ? "" : sms.getMassage());
        AppDatabase appDatabase = AppDatabase.getInstance(getContext());
        findViewById(R.id.saveButton).setOnClickListener(v -> {
            String phone = phoneTextView.getText().toString().trim();
            String massage = massageTextView.getText().toString().trim();
            if (phone.equals("") || massage.equals("")) {
                Toast.makeText(getContext(), "不能没有电话号码或信息内容", Toast.LENGTH_LONG).show();
                return;
            }
            if (sms == null) {
                sms = new SMS();
                sms.setBirthDateId(birthId);
                sms.setPeopleId(people.getId());
                sms.setMassage(massage);
                sms.setPhone(phone);
                new Thread(() -> appDatabase.getSMSDao().insert(sms)).start();
            }
            else if (!sms.getPhone().equals(phone) || sms.getMassage().equals(massage)) {
                sms.setPhone(phone);
                sms.setMassage(massage);
                new Thread(() -> appDatabase.getSMSDao().update(sms)).start();
            }
            dismiss();
        });

        findViewById(R.id.deleteButton).setOnClickListener(v -> {
            if (sms != null) {
                new Thread(() -> {
                    Log.v("sms", "del");
                    appDatabase.getSMSDao().delete(sms);
                    sms = null;
                }).start();
            }
            dismiss();
        });
        setCanceledOnTouchOutside(true);
    }
}
