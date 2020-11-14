package com.example.birthreminder.dao;

import androidx.room.*;
import com.example.birthreminder.entity.SMS;

@Dao
public interface SMSDao {
    @Insert
    long insert(SMS sms);

    @Update
    void update(SMS sms);

    @Delete
    void delete(SMS sms);

    /**
     * 按人物id删除短信
     *
     * @param peopleId 人物id
     */
    @Query("delete from sms where peopleId = :peopleId")
    void deleteByPeopleId(long peopleId);

    /**
     * 按日期获取短信提醒
     *
     * @param birthDateId 日期id
     * @return 短信
     */
    @Query("select * from sms where birthDateId = :birthDateId")
    SMS getSMSFromDate(long birthDateId);
}
