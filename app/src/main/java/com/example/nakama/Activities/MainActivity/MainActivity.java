package com.example.nakama.Activities.MainActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nakama.Activities.SelectRingActivity.SelectRingActivity;
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
        Dictionary.DEBUG_MODE = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appPreferences = new AppPreferences(getSharedPreferences(AppPreferences.NAME, Context.MODE_PRIVATE));
        appPreferences.clearPreferences();
        db = AppDatabase.getInstance(this);
        // TODO: 05.03.2023 This is temporary to add one user will be replace wit user selection later on
        Users user = new Users("Tomasz", "Szymaniak", "Nala");
        db.addUser(user);
        appPreferences.setUserId(db.getUserId(user));

        MainActivityViewManager viewManager = new MainActivityViewManager(this);
        viewManager.setCardsDefaultContent();
        viewManager.getBasicLevelButton().setOnClickListener(view ->{
            appPreferences.setDifficulty(Dictionary.Difficulty.Basic.NAME);
            Intent intent = new Intent(getApplicationContext(), SelectRingActivity.class);
            startActivity(intent);
        });

        viewManager.getAdvancedLevelButton().setOnClickListener(view ->{
            appPreferences.setDifficulty(Dictionary.Difficulty.Advanced.NAME);
            Intent intent = new Intent(getApplicationContext(), SelectRingActivity.class);
            startActivity(intent);
        });
    }

    public void onClearUsersData(View view) {
        showConfirmClearingAllUserDataDialog();
    }

    public void showConfirmClearingAllUserDataDialog() {
        new MaterialAlertDialogBuilder(MainActivity.this)
                .setTitle(R.string.confirm_dialog_title)
                .setMessage(R.string.confirm_clearing_data_message)
                .setPositiveButton(R.string.dialog_positive_yes_button, (dialogInterface, i) -> {
                    db.userScoresDao().delete();
                    showDataDeletionConfirmationDialog();
                })
                .setNegativeButton(R.string.dialog_negative_button, (dialogInterface, i) -> {})
                .show();
    }

    public void showDataDeletionConfirmationDialog() {
        new MaterialAlertDialogBuilder(MainActivity.this)
                .setTitle(R.string.confirm_dialog_title)
                .setMessage(R.string.data_cleared_confirmation_message)
                .setNeutralButton(R.string.dialog_positive_ok_button, (dialogInterface, i) -> {})
                .show();
    }
}