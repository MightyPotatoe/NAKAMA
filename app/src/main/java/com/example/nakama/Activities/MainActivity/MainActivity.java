package com.example.nakama.Activities.MainActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nakama.Activities.TimerActivity.TimerActivity;
import com.example.nakama.R;
import com.example.nakama.SharedPreferences.AppPreferences;

public class MainActivity extends AppCompatActivity {

    AppPreferences appPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appPreferences = new AppPreferences(getSharedPreferences(AppPreferences.NAME, Context.MODE_PRIVATE));

        MainActivityViewManager viewManager = new MainActivityViewManager(this);
        viewManager.setCardsDefaultContent();
        viewManager.getBasicLevelButton().setOnClickListener(view -> {
            appPreferences.setRingTime(120000);
            appPreferences.setPollingFrequency(50);
            Intent intent = new Intent(getApplicationContext(), TimerActivity.class);
            startActivity(intent);
        });

        viewManager.getAdvancedLevelButton().setOnClickListener(view -> {
            appPreferences.setRingTime(240000);
            appPreferences.setPollingFrequency(50);
            Intent intent = new Intent(getApplicationContext(), TimerActivity.class);
            startActivity(intent);
        });
    }
}