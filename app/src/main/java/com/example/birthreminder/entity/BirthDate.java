package com.example.birthreminder.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity(tableName = "birthDates")
public class BirthDate implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private long id; //id

    private int year; //日期年
    private int month; //日期月
    private int day; //日期日
    private long peopleId; //人物id
}
