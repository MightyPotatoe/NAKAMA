package com.example.nakama.Activities.AttemptSummaryActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nakama.Activities.MainActivity.MainActivity;
import com.example.nakama.DataBase.AppDatabase;
import com.example.nakama.DataBase.Entities.Users.Users;
import com.example.nakama.R;
import com.example.nakama.SharedPreferences.AppPreferences;
import com.example.nakama.Utils.Dictionary;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class RingSummaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppPreferences appPreferences = new AppPreferences(getSharedPreferences(AppPreferences.NAME, MODE_PRIVATE));
        switch (appPreferences.getDifficulty()){
            case Dictionary.Difficulty.Basic.NAME:
                setTheme(R.style.Theme_NAKAMA_BasicLevelTheme);
                break;
            case Dictionary.Difficulty.Advanced.NAME:
                setTheme(R.style.Theme_NAKAMA_AdvancedLevelTheme);
                break;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring_summary);
        AppDatabase db = AppDatabase.getInstance(this);
        Users users = db.getUser(appPreferences.getUserId());
        RingSummaryActivityViewManager viewManager = new RingSummaryActivityViewManager(this, users, appPreferences.getDifficulty(), appPreferences.getActiveRing());
        viewManager.setUserScore();
    }

    @Override
    public void onBackPressed() {}
    public void onNextAttemptButtonCLick(View view) {
        showConfirmNewAttemptDialog();
    }

    public void showConfirmNewAttemptDialog() {
        new MaterialAlertDialogBuilder(RingSummaryActivity.this)
                .setTitle(R.string.confirm_dialog_title)
                .setMessage(R.string.confirm_new_attempt_dialog_message)
                .setPositiveButton(R.string.dialog_positive_yes_button, (dialogInterface, i) -> {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                })
                .setNegativeButton(R.string.dialog_negative_button, (dialogInterface, i) -> {})
                .show();
    }
}