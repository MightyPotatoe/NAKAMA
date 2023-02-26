package com.example.nakama.Activities.AttemptSummaryActivity;

import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nakama.R;

public class AttemptSummaryActivityViewManager {

    TextView timeTextView;
    TextView userScoreTextView;

    public AttemptSummaryActivityViewManager(AppCompatActivity activity) {
        timeTextView = activity.findViewById(R.id.summaryTimeValue);
        userScoreTextView = activity.findViewById(R.id.summaryPointsValue);
    }

    public TextView getTimeTextView() {
        return timeTextView;
    }

    public TextView getUserScoreTextView() {
        return userScoreTextView;
    }

    public void setUserScore(int score) {
        String scoreText = score + " pkt.";
        userScoreTextView.setText(scoreText);
    }
}