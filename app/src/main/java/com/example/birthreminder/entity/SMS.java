package com.example.birthreminder.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class SMS extends Reminder implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private String phone;
}
