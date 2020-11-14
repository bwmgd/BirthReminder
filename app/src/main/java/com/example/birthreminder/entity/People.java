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
    private long id; //id

    private String name; //姓名
    private String phone; //电话
    private int year; //生日年
    private int month; //生日月
    private int day; //生日日
    private int birthCode; //特殊生日代码
    private int color; //日历显示颜色

    public static class PeopleWithBirthDates { //人物日期关系
        @Embedded
        public People people;
        @Relation(
                parentColumn = "id",
                entityColumn = "peopleId"
        )
        public List<BirthDate> birthDates;
    }
}

