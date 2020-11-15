package com.example.birthreminder.application;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import com.example.birthreminder.woker.RemindWorker;
import com.example.birthreminder.woker.SMSWorker;

public class BirthApplication extends Application {
    public static final int CONTACT_REQUEST_CODE = 1; //联系人请求码

    public static final String CHANNEL_REMIND_ID = "remind"; //通知频道
    public static final String CHANNEL_SMS_ID = "sms"; //通知频道

    public static final String PEOPLE = "people"; //intent传递码
    public static final String BIRTH = "birth";

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        createNotificationChannel();
        createWork();
    }

    private void createNotificationChannel() { //创建通知频道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "生日提醒器";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel remindChannel = new NotificationChannel(CHANNEL_REMIND_ID, name, importance);
            remindChannel.setDescription("生日备忘提醒通知");
            NotificationChannel smsChannel = new NotificationChannel(CHANNEL_SMS_ID, name, importance);
            smsChannel.setDescription("祝福短信发送通知");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(remindChannel);
            notificationManager.createNotificationChannel(smsChannel);
        }
    }

    private void createWork() { //创建发送通知和发送短信的work
        WorkManager workManager = WorkManager.getInstance(context);
        workManager.beginUniqueWork("senNot", ExistingWorkPolicy.KEEP,
                OneTimeWorkRequest.from(RemindWorker.class)).enqueue();
        workManager.beginUniqueWork("sms", ExistingWorkPolicy.KEEP,
                OneTimeWorkRequest.from(SMSWorker.class)).enqueue();
    }
}
