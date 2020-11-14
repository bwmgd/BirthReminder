package com.example.birthreminder.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity(tableName = "reminders")
public class Reminder implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private long id; //id

    private long birthDateId; //日期id
    private long peopleId; //人物id
    private String content = ""; //提醒内容
    private int beforeDay = 0; //提前天数
}
