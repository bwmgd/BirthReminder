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

    @Query("select * from peoples where id = :id")
    People getPeopleById(long id);

    @Query("select * from peoples")
    List<People> getAllPeople();

    @Transaction
    @Query("select * from peoples")
    List<People.PeopleWithBirthDates> getPeopleWithBirthDates();

    @Transaction
    @Query("select * from peoples where id = :peopleId")
    People.PeopleWithBirthDates getBirthDatesById(long peopleId);
}
