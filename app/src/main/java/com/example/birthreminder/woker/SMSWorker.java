package com.example.birthreminder.woker;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.example.birthreminder.entity.SMS;
import com.example.birthreminder.util.BirthUtil;
import org.jetbrains.annotations.NotNull;

public class SMSWorker extends Worker {
    Context context;

    public SMSWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NotNull
    @Override
    public Result doWork() {
        AppDatabase appDatabase = AppDatabase.getInstance(context);
        PeopleDao peopleDao = appDatabase.getPeopleDao();
        for (People.PeopleWithBirthDates peopleWithBirthDates : peopleDao.getPeopleWithBirthDates()) {
            for (BirthDate birthDate : peopleWithBirthDates.birthDates) {
                SMS sms = appDatabase.getSMSDao().getSMSFromDate(birthDate.getId());
                if (BirthUtil.isToday(birthDate.getYear(), birthDate.getMonth(), birthDate.getDay()))
                    sendSMS(peopleWithBirthDates.people, sms);
            }
        }
        return Result.success();
    }

    private void sendSMS(People people, SMS sms) {

        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + sms.getPhone()));
        intent.putExtra("sms_body", sms.getMassage());
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, BirthApplication.CHANNEL_SMS_ID)
                .setSmallIcon(R.mipmap.ic_calendar)
                .setContentTitle(people.getName() + "的" +
                        BirthUtil.getAge(people.getYear(), people.getMonth(), people.getDay()) +
                        "岁生日到了,点击此处发送送短信来祝福TA吧")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(sms.getMassage()))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify((int) sms.getId(), builder.build());
    }
}