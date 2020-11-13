package com.example.birthreminder.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.birthreminder.entity.BirthDate;

import java.util.List;

@Dao
public interface BirthDateDao {
    @Insert
    long insert(BirthDate birthDate);

    @Insert
    long[] insert(List<BirthDate> birthDates);

    @Delete
    void delete(BirthDate birthDate);

    @Query("delete from birthDates where peopleId = :peopleId")
    void deleteByPeopleId(long peopleId);

    @Query("select * from birthDates where peopleId = :peopleId")
    List<BirthDate> getBirthById(long peopleId);

    @Query("select count(*) from birthDates where peopleId = :peopleId " +
            "and year < cast(strftime('%Y',date('now')) as int)")
    int getCount(long peopleId);
}
