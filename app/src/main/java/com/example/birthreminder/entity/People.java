package com.example.birthreminder.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity(tableName = "peoples")
public class People implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;
    private String phone;
    private Boolean isLunar;
    private int month;
    private int day;
}
