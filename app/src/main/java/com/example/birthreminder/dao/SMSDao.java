package com.example.birthreminder.dao;

import androidx.room.*;
import com.example.birthreminder.entity.Reminder;
import com.example.birthreminder.entity.SMS;

import java.util.List;

@Dao
public interface SMSDao {
    @Insert
    long insert(SMS sms);

    @Update
    void update(SMS sms);

    @Delete
    void delete(SMS sms);

    @Query("select * from reminders where birthDateId = :birthDateId")
    List<Reminder> getSMSsFromDate(long birthDateId);
}
