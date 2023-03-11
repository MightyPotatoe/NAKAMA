package com.example.nakama.Activities.AttemptSummaryActivity;

import android.content.Context;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nakama.DataBase.AppDatabase;
import com.example.nakama.DataBase.Entities.UserScores.UserScore;
import com.example.nakama.R;
import com.example.nakama.SharedPreferences.AppPreferences;

import java.util.Locale;

public class AttemptSummaryActivityViewManager {

    AppCompatActivity activity;
    TextView timeTextView;
    TextView userScoreTextView;

    public AttemptSummaryActivityViewManager(AppCompatActivity activity) {
        this.activity = activity;
        timeTextView = activity.findViewById(R.id.summaryTimeValue);
        userScoreTextView = activity.findViewById(R.id.summaryPointsValue);
    }

    public void setUserScore(){
        AppDatabase db = AppDatabase.getInstance(activity);
        AppPreferences appPreferences = new AppPreferences(activity.getSharedPreferences(AppPreferences.NAME, Context.MODE_PRIVATE));
        UserScore userScore = db.userScoresDao().getUserScore(appPreferences.getUserId(), appPreferences.getDifficulty(), appPreferences.getActiveRing());
        timeTextView.setText(userScore.attemptTime);
        userScoreTextView.setText(String.format(Locale.ENGLISH, "%d pkt.",userScore.score));
    }
}