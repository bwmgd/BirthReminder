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
    private long id; //id

    private long birthDateId; //日期id
    private long peopleId; //人物id
    private String massage; //信息内容
    private String phone; //电话
}
