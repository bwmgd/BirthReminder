package com.example.birthreminder.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.birthreminder.entity.Reminder;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ReminderDao {
    @Insert(onConflict = REPLACE)
    long insert(Reminder reminder);

    @Delete
    void delete(Reminder reminder);

    @Delete
    void delete(List<Reminder> reminders);

    /**
     * 按人物id删除提醒
     *
     * @param peopleId 人物id
     */
    @Query("delete from reminders where peopleId = :peopleId")
    void deleteByPeopleId(long peopleId);

    /**
     * 按生日id获取提醒
     *
     * @param birthDateId 生日id
     * @return 提醒列表
     */
    @Query("select * from reminders where birthDateId = :birthDateId")
    List<Reminder> getRemindersFromDate(long birthDateId);
}
