package com.example.nakama.Activities.SelectRingActivity;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.example.nakama.Activities.TopBar.TopBarViewManager;
import com.example.nakama.DataBase.AppDatabase;
import com.example.nakama.DataBase.Entities.Users.Users;
import com.example.nakama.R;
import com.example.nakama.SharedPreferences.AppPreferences;
import com.example.nakama.Utils.Dictionary;
import com.google.android.material.button.MaterialButton;

public class SelectRingActivityViewManager {


    AppCompatActivity activity;
    MaterialButton ring1Button;
    MaterialButton ring2Button;
    MaterialButton ring3Button;
    MaterialButton ring4Button;

    public SelectRingActivityViewManager(AppCompatActivity activity) {
        this.activity = activity;
        ring1Button = activity.findViewById(R.id.ring1Button);
        ring2Button = activity.findViewById(R.id.ring2Button);
        ring3Button = activity.findViewById(R.id.ring3Button);
        ring4Button = activity.findViewById(R.id.ring4Button);
    }

    public void setViewToDefaultState(String difficulty, String ring, Users user){
        new TopBarViewManager(activity, user, difficulty, ring);
    }

    public void checkIfRingIsDone(){
        AppDatabase db = AppDatabase.getInstance(activity);
        AppPreferences appPreferences = new AppPreferences(activity.getSharedPreferences(AppPreferences.NAME, Context.MODE_PRIVATE));
        if(db.userScoresDao().getUserScore(appPreferences.getUserId(),appPreferences.getDifficulty(), Dictionary.Rings.RING_1) == null){
            ring1Button.setIcon(null);
        }
        else{
            ring1Button.setIcon(AppCompatResources.getDrawable(activity, R.drawable.baseline_check_circle_24));
        }

        if(db.userScoresDao().getUserScore(appPreferences.getUserId(),appPreferences.getDifficulty(), Dictionary.Rings.RING_2) == null){
            ring2Button.setIcon(null);
        }
        else{
            ring2Button.setIcon(AppCompatResources.getDrawable(activity, R.drawable.baseline_check_circle_24));
        }

        if(db.userScoresDao().getUserScore(appPreferences.getUserId(),appPreferences.getDifficulty(), Dictionary.Rings.RING_3) == null){
            ring3Button.setIcon(null);
        }
        else{
            ring3Button.setIcon(AppCompatResources.getDrawable(activity, R.drawable.baseline_check_circle_24));
        }

        if(db.userScoresDao().getUserScore(appPreferences.getUserId(),appPreferences.getDifficulty(), Dictionary.Rings.RING_4) == null){
            ring4Button.setIcon(null);
        }
        else{
            ring4Button.setIcon(AppCompatResources.getDrawable(activity, R.drawable.baseline_check_circle_24));
        }

    }

}
