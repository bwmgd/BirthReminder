package com.example.birthreminder.application;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import com.example.birthreminder.R;
import com.example.birthreminder.woker.MyWorker;

import java.util.concurrent.TimeUnit;

public class BirthApplication extends Application {
    public static final int CONTACT_REQUEST_CODE = 1; //联系人请求码

    public static final String CHANNEL_ID = "debug"; //通知频道

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
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createWork() { //创建发送通知和发送短信的work
        PeriodicWorkRequest remindRequest = new PeriodicWorkRequest.Builder(MyWorker.class, 1, TimeUnit.DAYS).build();
        WorkManager workManager = WorkManager.getInstance(context);
        workManager.enqueueUniquePeriodicWork("remind", ExistingPeriodicWorkPolicy.KEEP, remindRequest);
    }
}
