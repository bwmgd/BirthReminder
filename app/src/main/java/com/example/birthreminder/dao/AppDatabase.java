package com.example.birthreminder.dao;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.birthreminder.entity.BirthDate;
import com.example.birthreminder.entity.People;
import com.example.birthreminder.entity.Reminder;
import com.example.birthreminder.entity.SMS;

@Database(entities = {People.class, BirthDate.class, Reminder.class, SMS.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DB_NAME = "BirthReminder.db";
    private static volatile AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) instance = create(context);
        return instance;
    }

    private static AppDatabase create(final Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, DB_NAME).build();
    }

    public abstract PeopleDao getPeopleDao();

    public abstract BirthDateDao getBirthDateDao();

    public abstract ReminderDao getReminderDao();

    public abstract SMSDao getSMSDao();
}
