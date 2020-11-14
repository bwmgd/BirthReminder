package com.example.birthreminder.woker;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.birthreminder.R;
import com.example.birthreminder.application.BirthApplication;
import com.example.birthreminder.dao.AppDatabase;
import com.example.birthreminder.dao.PeopleDao;
import com.example.birthreminder.entity.BirthDate;
import com.example.birthreminder.entity.People;
import com.example.birthreminder.entity.Reminder;
import com.example.birthreminder.entity.SMS;
import com.example.birthreminder.ui.scheme.SchemeActivity;
import com.example.birthreminder.util.BirthUtil;
import org.jetbrains.annotations.NotNull;

public class MyWorker extends Worker {
    Context context;

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NotNull
    @Override
    public Result doWork() {
        AppDatabase appDatabase = AppDatabase.getInstance(context);
        PeopleDao peopleDao = appDatabase.getPeopleDao();
        for (People.PeopleWithBirthDates peopleWithBirthDates : peopleDao.getPeopleWithBirthDates()) {
            People people = peopleWithBirthDates.people;
            for (BirthDate birthDate : peopleWithBirthDates.birthDates) {
                SMS sms = appDatabase.getSMSDao().getSMSFromDate(birthDate.getId());
                if (BirthUtil.isToday(birthDate.getYear(), birthDate.getMonth(), birthDate.getDay()))
                    sendSMSMessage(sms.getPhone(), sms.getMassage());

                for (Reminder reminder : appDatabase.getReminderDao().getRemindersFromDate(birthDate.getId()))
                    if (BirthUtil.beforeDayIsToday(
                            birthDate.getYear(), birthDate.getMonth(), birthDate.getDay(), reminder.getBeforeDay()))
                        sendNotification(people, birthDate, reminder);
            }
        }
        return Result.success();
    }

    private void sendSMSMessage(String phone, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            for (String s : smsManager.divideMessage(message))
                smsManager.sendTextMessage(phone, null, s, null, null);
            Toast.makeText(getApplicationContext(), "短信已发送", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "短信发送失败", Toast.LENGTH_LONG).show();
        }
    }

    private void sendNotification(People people, BirthDate birthDate, Reminder reminder) {
        Intent intent = new Intent(context, SchemeActivity.class);
        intent.putExtra(BirthApplication.PEOPLE, people);
        intent.putExtra(BirthApplication.BIRTH, birthDate);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, BirthApplication.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_calendar)
                .setContentTitle(people.getName() + "的生日快到了")
                .setContentText(BirthUtil.getAge(people.getYear(), people.getMonth(), people.getDay()) +
                        "还有" + reminder.getBeforeDay() + "天")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(reminder.getContent()))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify((int) reminder.getId(), builder.build());
    }
}