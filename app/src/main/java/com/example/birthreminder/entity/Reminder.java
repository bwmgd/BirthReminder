package com.example.birthreminder.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity(tableName = "reminders")
public class Reminder implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private long birthDateId;
    private String content;
    private int beforeMonth;
    private int beforeDay;
    private int remindHour;
    private int remindMinute;
}
