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

    /**
     * 按任务id删除日期
     *
     * @param peopleId 人物id
     */
    @Query("delete from birthDates where peopleId = :peopleId")
    void deleteByPeopleId(long peopleId);

    /**
     * @param peopleId 人物id
     * @return 生日在今日之前的日期数目
     */
    @Query("select count(*) from birthDates where peopleId = :peopleId " +
            "and year < cast(strftime('%Y',date('now')) as int)")
    int getCount(long peopleId);
}
