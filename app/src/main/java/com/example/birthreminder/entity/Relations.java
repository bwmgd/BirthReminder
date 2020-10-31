package com.example.birthreminder.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class Relations {
    public static class PeopleWithBirthDate {
        @Embedded
        public People people;
        @Relation(
                parentColumn = "id",
                entityColumn = "peopleId"
        )
        public List<BirthDate> birthDates;
    }

    public static class BirthDateWithReminders {
        @Embedded
        public BirthDate birthDate;
        @Relation(
                parentColumn = "id",
                entityColumn = "birthDateId"
        )
        public List<Reminder> reminders;
    }
}
