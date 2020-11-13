package com.example.birthreminder.application;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

public class BirthApplication extends Application {
    public static final int CONTACT_REQUEST_CODE = 1;

    public static final String PEOPLE = "people"; //intent传递码
    public static final String BIRTH = "birth";
    public static final String PHONE = "phone";
    public static final String MESSAGE = "message";
    public static final String DATE = "date";
    public static final int PEOPLE_REQUEST_CODE = 2;

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}
