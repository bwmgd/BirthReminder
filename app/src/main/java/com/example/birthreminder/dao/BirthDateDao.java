package com.example.birthreminder.dao;

import androidx.room.*;
import com.example.birthreminder.entity.BirthDate;
import com.example.birthreminder.entity.Relations;

import java.util.List;

@Dao
public interface BirthDateDao {
    @Insert
    void insert(BirthDate birthDate);

    @Query("select last_insert_rowid() as MaxID from birthDates")
    int getMaxId();

    @Transaction
    default int insertBirthDate(BirthDate birthDate) {
        insert(birthDate);
        return getMaxId();
    }

    @Update
    void update(BirthDate birthDate);

    @Delete
    void delete(BirthDate birthDate);

    @Query("update birthDates set color = :color where id = :id")
    void updateColor(long id, int color);

    @Query("update birthDates set text = :text where id = :id")
    void updateText(long id, String text);

    @Query("delete from birthDates where nameId = :nameId")
    void deleteWithNameId(long nameId);

    @Query("select * from birthDates")
    List<BirthDate> getAllBirthDates();

    @Query("select * from birthDates where date>=date(:date,'start of month','+0 month','-0 day') " +
            "AND date < date(:date,'start of month','+1 month','0 day')")
    List<BirthDate> getBirthDatesFromMonth(String date);

    @Query("select * from birthDates where date<date('now')")
    List<BirthDate> getBirthDatesBeforeNow();

    @Query("select * from birthDates where date>=date('now')")
    List<BirthDate> getBirthAfterBeforeNow();

    @Transaction
    @Query("SELECT * FROM birthDates")
    List<Relations.BirthDateWithReminders> getBirthDateWithReminders();
}
