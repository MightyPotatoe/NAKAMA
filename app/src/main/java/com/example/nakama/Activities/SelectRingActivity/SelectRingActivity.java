package com.example.nakama.Activities.SelectRingActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nakama.Activities.TimerActivity.TimerActivity;
import com.example.nakama.DataBase.AppDatabase;
import com.example.nakama.DataBase.Entities.UserScores.UserScore;
import com.example.nakama.DataBase.Entities.Users.Users;
import com.example.nakama.R;
import com.example.nakama.SharedPreferences.AppPreferences;
import com.example.nakama.Utils.Dictionary;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SelectRingActivity extends AppCompatActivity {

    AppPreferences appPreferences;
    AppDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        appPreferences = new AppPreferences(getSharedPreferences(AppPreferences.NAME, Context.MODE_PRIVATE));
        switch (appPreferences.getDifficulty()){
            case Dictionary.Difficulty.Basic.NAME:
                setTheme(R.style.Theme_NAKAMA_BasicLevelTheme);
                break;
            case Dictionary.Difficulty.Advanced.NAME:
                setTheme(R.style.Theme_NAKAMA_AdvancedLevelTheme);
                break;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_ring);
        SelectRingActivityViewManager viewManager = new SelectRingActivityViewManager(this);
        db = AppDatabase.getInstance(this);
        viewManager.setViewToDefaultState(appPreferences.getDifficulty(), appPreferences.getActiveRing(), db.usersDao().getUser(appPreferences.getUserId()));
        viewManager.checkIfRingIsDone();
    }

    public void onSelectRing1ButtonClick(View view) {
        appPreferences.setActiveRing(Dictionary.Rings.RING_1);
        askIfOverrideAndStartTimerActivity();
    }
    public void onSelectRing2ButtonClick(View view) {
        appPreferences.setActiveRing(Dictionary.Rings.RING_2);
        askIfOverrideAndStartTimerActivity();
    }
    public void onSelectRing3ButtonClick(View view) {
        appPreferences.setActiveRing(Dictionary.Rings.RING_3);
        askIfOverrideAndStartTimerActivity();
    }
    public void onSelectRing4ButtonClick(View view) {
        appPreferences.setActiveRing(Dictionary.Rings.RING_4);
        askIfOverrideAndStartTimerActivity();
    }





    public void askIfOverrideAndStartTimerActivity(){
        if(!db.addUserScoreIfNotExists(appPreferences.getUserId(), appPreferences.getDifficulty(), appPreferences.getActiveRing())){
            showConfirmClearingAttemptDialog(appPreferences.getUserId(), appPreferences.getDifficulty(), appPreferences.getActiveRing());
        }
        else {
            Intent intent = new Intent(getApplicationContext(), TimerActivity.class);
            startActivity(intent);
        }
    }
    public void showConfirmClearingAttemptDialog(int userId, String difficulty, String ring) {
        Users user = db.getUser(userId);
        String message = String.format("Użytkownik '%s %s' startujący z psem '%s' posiada już wpis dla ringu '%s' na poziomie trudności '%s'.\n\nCzy jesteś pewien, że chcesz go nadpisać?\n\nWszystkie dotychczasowe dane zostaną utracone", user.userName, user.usersSurname, user.dogName, ring, difficulty);
        new MaterialAlertDialogBuilder(SelectRingActivity.this)
                .setTitle(R.string.confirm_dialog_title)
                .setMessage(message)
                .setPositiveButton(R.string.dialog_positive_yes_button, (dialogInterface, i) -> {
                    UserScore userScore = new UserScore(userId, difficulty, ring);
                    db.userScoresDao().updateScores(userScore);
                    Intent intent = new Intent(getApplicationContext(), TimerActivity.class);
                    startActivity(intent);
                })
                .setNegativeButton(R.string.dialog_negative_button, (dialogInterface, i) -> {})
                .show();
    }
}