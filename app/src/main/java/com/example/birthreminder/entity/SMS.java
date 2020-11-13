package com.example.birthreminder.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity(tableName = "SMS", indices = {@Index(value = {"birthDateId"}, unique = true)})
public class SMS implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private long birthDateId;
    private long peopleId;
    private String massage;
    private String phone;
}
