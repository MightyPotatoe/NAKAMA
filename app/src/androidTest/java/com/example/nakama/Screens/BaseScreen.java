package com.example.nakama.Screens;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;

import com.example.nakama.DataBase.AppDatabase;
import com.example.nakama.SharedPreferences.AppPreferences;

public class BaseScreen {

    public AppDatabase db;
    public AppPreferences appPreferences;

    ActivityScenario<?> scenario;

    public BaseScreen(ActivityScenario<?> scenario) {
        this.scenario = scenario;
        this.scenario.onActivity(activity -> {
            db = AppDatabase.getInstance(activity.getApplicationContext());
            if(appPreferences == null){
                appPreferences = new AppPreferences(activity.getSharedPreferences(AppPreferences.NAME, Context.MODE_PRIVATE));
            }
        });
    }
}