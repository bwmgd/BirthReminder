package com.example.birthreminder.woker;

import android.content.Context;
import android.telephony.SmsManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.birthreminder.application.BirthApplication;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

public class SMSWorker extends Worker {
    public SMSWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NotNull
    @Override
    public Result doWork() {
        Data data = getInputData();
        int[] date = data.getIntArray(BirthApplication.DATE);
        assert date != null;
        if (LocalDate.of(date[0], date[1], date[2]).compareTo(LocalDate.now()) == 0) {
            try {
                String phone = data.getString(BirthApplication.PHONE);
                String message = data.getString(BirthApplication.MESSAGE);
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phone, null, message, null, null);
                Toast.makeText(getApplicationContext(), "SMS sent.",
                        Toast.LENGTH_LONG).show();
                return Result.success();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),
                        "SMS failed, please try again.",
                        Toast.LENGTH_LONG).show();
                e.printStackTrace();
                return Result.failure();
            }
        }
        return Result.retry();
    }
}