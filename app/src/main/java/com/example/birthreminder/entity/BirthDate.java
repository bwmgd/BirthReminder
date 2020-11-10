package com.example.birthreminder.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity(tableName = "birthDates")
public class BirthDate implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private int year;
    private int month;
    private int day;
    private long peopleId;
}
