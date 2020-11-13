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

    @Query("delete from sms where peopleId = :peopleId")
    void deleteByPeopleId(long peopleId);

    @Query("select * from sms where birthDateId = :birthDateId")
    SMS getSMSFromDate(long birthDateId);
}
