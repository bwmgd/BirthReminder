package com.example.birthreminder.dao;

import androidx.room.*;
import com.example.birthreminder.entity.People;

import java.util.List;

@Dao
public interface PeopleDao {
    @Insert
    long insert(People people);

    @Update
    void update(People people);

    @Delete
    void delete(People people);

    /**
     * @return 全部人物及人物生日列表的列表
     */
    @Transaction
    @Query("select * from peoples")
    List<People.PeopleWithBirthDates> getPeopleWithBirthDates();

    /**
     * @param peopleId 人物id
     * @return 人物及人物生日列表
     */
    @Transaction
    @Query("select * from peoples where id = :peopleId")
    People.PeopleWithBirthDates getBirthDatesById(long peopleId);
}
