package com.example.birthreminder.entity;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Relation;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Entity(tableName = "peoples")
public class People implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;
    private String phone;
    private int year;
    private int month;
    private int day;
    private int birthCode;
    private int color;

    public static class PeopleWithBirthDates {
        @Embedded
        public People people;
        @Relation(
                parentColumn = "id",
                entityColumn = "peopleId"
        )
        public List<BirthDate> birthDates;
    }
}

