package com.example.nakama.Activities.UserRingsResultsActivity;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nakama.DataBase.AppDatabase;
import com.example.nakama.DataBase.Entities.Users.Users;
import com.example.nakama.R;
import com.example.nakama.SharedPreferences.AppPreferences;
import com.example.nakama.Utils.Dictionary;

public class UserRingsResultsActivity extends AppCompatActivity {

    UserRingsResultViewManager viewManager;
    AppPreferences appPreferences;
    AppDatabase db;
    Users user;

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
        setContentView(R.layout.activity_user_rings_results);
        viewManager = new UserRingsResultViewManager(this);

        //Get current user from db
        db = AppDatabase.getInstance(this);
        user = db.getUser(appPreferences.getUserId());
        viewManager.setViewToDefaultState(
                appPreferences.getDifficulty(),
                "Wyniki zawodnika",
                user);
    }
}