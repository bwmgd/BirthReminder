package com.example.birthreminder.dao;

import androidx.room.*;
import com.example.birthreminder.entity.BirthDate;

import java.util.List;

@Dao
public interface BirthDateDao {
    @Insert
    long insert(BirthDate birthDate);

    @Insert
    long[] insert(List<BirthDate> birthDates);

    @Update
    void update(BirthDate birthDate);

    @Delete
    void delete(BirthDate birthDate);

    @Query("delete from birthDates where peopleId = :peopleId")
    void deleteByPeopleId(long peopleId);

    @Query("select count(*) from birthDates where peopleId = :peopleId and " +
            "date(cast(year as text) ||'-'||cast(month as text)||'-'||cast(day as text)) < date('now')")
    int getBirthDatesBeforeNowCountById(long peopleId);

    @Query("select * from birthDates where peopleId = :peopleId")
    List<BirthDate> getBirthById(long peopleId);

    @Query("select * from birthDates where year = :year and month = :month")
    List<BirthDate> getBirthDatesByMonth(int year, int month);
}
