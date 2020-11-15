package com.example.birthreminder.woker;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.*;
import com.example.birthreminder.R;
import com.example.birthreminder.application.BirthApplication;
import com.example.birthreminder.dao.AppDatabase;
import com.example.birthreminder.dao.PeopleDao;
import com.example.birthreminder.entity.BirthDate;
import com.example.birthreminder.entity.People;
import com.example.birthreminder.entity.Reminder;
import com.example.birthreminder.ui.scheme.SchemeActivity;
import com.example.birthreminder.util.BirthUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class RemindWorker extends Worker {
    Context context;

    public RemindWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NotNull
    @Override
    public Result doWork() {
        try {
            AppDatabase appDatabase = AppDatabase.getInstance(context);
            PeopleDao peopleDao = appDatabase.getPeopleDao();
            for (People.PeopleWithBirthDates peopleWithBirthDates : peopleDao.getPeopleWithBirthDates())
                for (BirthDate birthDate : peopleWithBirthDates.birthDates)
                    for (Reminder reminder : appDatabase.getReminderDao().getRemindersFromDate(birthDate.getId()))
                        if (BirthUtil.beforeDayIsToday(
                                birthDate.getYear(), birthDate.getMonth(), birthDate.getDay(), reminder.getBeforeDay()))
                            sendNotification(peopleWithBirthDates.people, birthDate, reminder);
            return Result.success();
        } catch (Exception e) {
            return Result.failure();
        } finally {
            Calendar currentDate = Calendar.getInstance(); //每日0点进行通知
            Calendar dueDate = Calendar.getInstance();
            dueDate.set(Calendar.HOUR_OF_DAY, 0);
            dueDate.set(Calendar.MINUTE, 0);
            dueDate.set(Calendar.SECOND, 0);
            if (dueDate.before(currentDate)) dueDate.add(Calendar.HOUR_OF_DAY, 24);
            long timeDiff = dueDate.getTimeInMillis() - currentDate.getTimeInMillis();

            OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(RemindWorker.class)
                    .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS).build();
            WorkManager.getInstance(context).enqueueUniqueWork("senNot", ExistingWorkPolicy.REPLACE, request);
        }
    }

    private void sendNotification(People people, BirthDate birthDate, Reminder reminder) {
        Intent intent = new Intent(context, SchemeActivity.class);
        intent.putExtra(BirthApplication.PEOPLE, people);
        intent.putExtra(BirthApplication.BIRTH, birthDate);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, BirthApplication.CHANNEL_REMIND_ID)
                .setSmallIcon(R.mipmap.ic_calendar)
                .setContentTitle(people.getName() + "的生日快到了")
                .setContentText(BirthUtil.getAge(people.getYear(), people.getMonth(), people.getDay()) +
                        "还有" + reminder.getBeforeDay() + "天")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(reminder.getContent()))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify((int) reminder.getId(), builder.build());
    }
}