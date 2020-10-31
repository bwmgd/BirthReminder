package com.example.birthreminder.dao;

import androidx.room.*;
import com.example.birthreminder.entity.People;
import com.example.birthreminder.entity.Relations;

import java.util.List;

@Dao
public interface PeopleDao {
    @Insert
    void insert(People people);

    @Update
    void update(People people);

    @Delete
    void delete(People people);

    @Transaction
    @Query("select * from peoples")
    List<Relations.PeopleWithBirthDate> getPeopleWithBirthDates();
}
