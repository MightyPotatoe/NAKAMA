package com.example.nakama.Activities.TimerActivity;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nakama.R;
import com.example.nakama.Utils.Converter;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class TimerActivityViewManager {

    public TimerActivityViewManager(AppCompatActivity activity) {
        this.timerTextView = activity.findViewById(R.id.timeTextView);
        timerProgressBar = activity.findViewById(R.id.timeProgressBar);
        playButton = activity.findViewById(R.id.playButton);
        pauseButton = activity.findViewById(R.id.pauseButton);
        resetButton = activity.findViewById(R.id.resetButton);
        doneButton = activity.findViewById(R.id.doneButton);
    }

    private final TextView timerTextView;
    private final CircularProgressIndicator timerProgressBar;
    private final ImageButton playButton;
    private final ImageButton pauseButton;
    private final ImageButton resetButton;
    private final ImageButton doneButton;

    public TextView getTimerTextView() {
        return timerTextView;
    }
    public void setViewToDefaultState(int timeInMillis){
        //Initialize default view
        timerProgressBar.setMax(timeInMillis);
        setTimerCurrentTime(timeInMillis);
        playButton.setVisibility(View.VISIBLE);
        pauseButton.setVisibility(View.INVISIBLE);
        resetButton.setVisibility(View.INVISIBLE);
        doneButton.setVisibility(View.INVISIBLE);
    }

    public void setTimerCurrentTime(long timeRemain){
        timerProgressBar.setProgress((int)timeRemain);
        timerTextView.setText(Converter.millisToString(timeRemain));
    }

    public void setViewToPlayedState(){
        playButton.setVisibility(View.INVISIBLE);
        pauseButton.setVisibility(View.VISIBLE);
        resetButton.setVisibility(View.INVISIBLE);
        doneButton.setVisibility(View.INVISIBLE);
    }

    public void setViewToPausedState(){
        playButton.setVisibility(View.VISIBLE);
        pauseButton.setVisibility(View.INVISIBLE);
        resetButton.setVisibility(View.VISIBLE);
        doneButton.setVisibility(View.VISIBLE);
    }

    public void setViewToFinishedState(){
        playButton.setVisibility(View.INVISIBLE);
        pauseButton.setVisibility(View.INVISIBLE);
        resetButton.setVisibility(View.VISIBLE);
        doneButton.setVisibility(View.VISIBLE);
    }
}
