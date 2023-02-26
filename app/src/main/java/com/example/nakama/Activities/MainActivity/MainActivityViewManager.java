package com.example.nakama.Activities.MainActivity;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nakama.R;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivityViewManager {

    private final TextView timerTextView;
    private final CircularProgressIndicator timerProgressBar;
    private final ImageButton playButton;
    private final ImageButton pauseButton;
    private final ImageButton resetButton;
    private final ImageButton doneButton;

    public TextView getTimerTextView() {
        return timerTextView;
    }

    public MainActivityViewManager(AppCompatActivity activity) {
        this.timerTextView = activity.findViewById(R.id.timeTextView);
        timerProgressBar = activity.findViewById(R.id.timeProgressBar);
        playButton = activity.findViewById(R.id.playButton);
        pauseButton = activity.findViewById(R.id.pauseButton);
        resetButton = activity.findViewById(R.id.resetButton);
        doneButton = activity.findViewById(R.id.doneButton);
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
        String timeLeft = DurationFormatUtils.formatDuration(timeRemain, "mm:ss:SS", true);
        Pattern pattern = Pattern.compile("\\d{2}:\\d{2}:\\d{2}");
        Matcher matcher = pattern.matcher(timeLeft);
        if (matcher.find()) {timerTextView.setText(matcher.group(0));}
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
