package com.example.nakama.Activities.MainActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nakama.Activities.TimerActivity.TimerActivity;
import com.example.nakama.DataBase.AppDatabase;
import com.example.nakama.DataBase.Entities.Users.Users;
import com.example.nakama.R;
import com.example.nakama.SharedPreferences.AppPreferences;
import com.example.nakama.Utils.Dictionary;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MainActivity extends AppCompatActivity {

    AppPreferences appPreferences;
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appPreferences = new AppPreferences(getSharedPreferences(AppPreferences.NAME, Context.MODE_PRIVATE));
        db = AppDatabase.getInstance(this);
        // TODO: 05.03.2023 This is temporary to add one user will be replace wit user selection later on
        Users user = new Users("Tomasz", "Szymaniak", "Nala");
        db.addUser(user);

        MainActivityViewManager viewManager = new MainActivityViewManager(this);
        viewManager.setCardsDefaultContent();
        viewManager.getBasicLevelButton().setOnClickListener(view ->{
            appPreferences.setFalseAlarmsLimit(1);
            appPreferences.setSamplesInRun(1);
            openTimerActivity(120000, Dictionary.Difficulty.BASIC, Dictionary.Rings.RING_1, user);
        });

        viewManager.getAdvancedLevelButton().setOnClickListener(view ->{
            appPreferences.setFalseAlarmsLimit(2);
            appPreferences.setSamplesInRun(2);
            openTimerActivity(240000, Dictionary.Difficulty.ADVANCED, Dictionary.Rings.RING_1, user);
        });
    }

    public void showConfirmClearingAttemptDialog(int userId, String difficulty, String ring) {
        Users user = db.getUser(userId);
        String message = String.format("Użytkownik '%s %s' startujący z pesem '%s' posiada już wpis dla ringu '%s' na poziomie trudności '%s'.\n\nCzy jesteś pewien, że chcesz go nadpisać?\n\nWszystkie dotychczasowe dane zostaną utracone", user.userName, user.usersSurname, user.dogName, ring, difficulty);
        new MaterialAlertDialogBuilder(MainActivity.this)
                .setTitle(R.string.confirm_dialog_title)
                .setMessage(message)
                .setPositiveButton(R.string.dialog_positive_yes_button, (dialogInterface, i) -> {
                    db.clearUserScore(userId, difficulty, ring);
                    Intent intent = new Intent(getApplicationContext(), TimerActivity.class);
                    startActivity(intent);
                })
                .setNegativeButton(R.string.dialog_negative_button, (dialogInterface, i) -> {})
                .show();
    }

    public void openTimerActivity(int time, String difficulty, String ring, Users user){
        appPreferences.setRingTime(time);
        appPreferences.setPollingFrequency(50);
        appPreferences.setDifficulty(difficulty);
        appPreferences.setActiveRing(ring);
        appPreferences.setUserId(db.getUserId(user));
        if(!db.addUserScore(db.getUserId(user), difficulty, ring)){
            showConfirmClearingAttemptDialog(db.getUserId(user), difficulty, ring);
        }
        else {
            Intent intent = new Intent(getApplicationContext(), TimerActivity.class);
            startActivity(intent);
        }
    }
}