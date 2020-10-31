package com.example.birthreminder.dao;

import androidx.room.*;
import com.example.birthreminder.entity.Reminder;

import java.util.List;

@Dao
public interface ReminderDao {
    @Insert
    void insert(Reminder reminder);

    @Update
    void update(Reminder reminder);

    @Delete
    void delete(Reminder reminder);

    @Query("select * from reminders where birthDateId = :birthDateId")
    List<Reminder> getRemindersFromDate(long birthDateId);
}
