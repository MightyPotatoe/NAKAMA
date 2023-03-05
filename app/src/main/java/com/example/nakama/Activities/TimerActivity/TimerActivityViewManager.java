package com.example.nakama.Activities.TimerActivity;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.nakama.DataBase.Entities.Users.Users;
import com.example.nakama.R;
import com.example.nakama.Utils.Converter;
import com.example.nakama.Utils.Dictionary;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class TimerActivityViewManager {

    public TimerActivityViewManager(AppCompatActivity activity) {
        this.timerTextView = activity.findViewById(R.id.timeTextView);
        timerProgressBar = activity.findViewById(R.id.timeProgressBar);
        playButton = activity.findViewById(R.id.playButton);
        pauseButton = activity.findViewById(R.id.pauseButton);
        resetButton = activity.findViewById(R.id.resetButton);
        doneButton = activity.findViewById(R.id.doneButton);
        difficultyImageView = activity.findViewById(R.id.timerDifficultyImage);
        difficultyTextView = activity.findViewById(R.id.timerDifficultyTextView);
        ringDetailsTextView = activity.findViewById(R.id.timerRingDetailsTextView);
        userDetailsTextView = activity.findViewById(R.id.timerUserDetailsTextView);
        topBarLayout = activity.findViewById(R.id.timerTopBarLayout);
    }
    private final TextView timerTextView;
    private final CircularProgressIndicator timerProgressBar;
    private final ImageButton playButton;
    private final ImageButton pauseButton;
    private final ImageButton resetButton;
    private final ImageButton doneButton;
    private final ShapeableImageView difficultyImageView;
    private final TextView difficultyTextView;
    private final TextView ringDetailsTextView;
    private final TextView userDetailsTextView;
    private final ConstraintLayout topBarLayout;

    public TextView getTimerTextView() {
        return timerTextView;
    }
    public void setViewToDefaultState(int timeInMillis, String difficulty, String ring, Users user){
        //Initialize default view
        timerProgressBar.setMax(timeInMillis);
        setTimerCurrentTime(timeInMillis);
        playButton.setVisibility(View.VISIBLE);
        pauseButton.setVisibility(View.INVISIBLE);
        resetButton.setVisibility(View.INVISIBLE);
        doneButton.setVisibility(View.INVISIBLE);
        String displayedDifficulty = String.format("Poziom %s", difficulty);
        difficultyTextView.setText(displayedDifficulty);
        ringDetailsTextView.setText(ring);
        String displayedUserName = String.format("%s %s i %s", user.userName, user.usersSurname, user.dogName);
        userDetailsTextView.setText(displayedUserName);

        switch (difficulty){
            case Dictionary.Difficulty.BASIC:
                difficultyImageView.setImageResource(R.drawable.basic_level_icon);
                changeLayoutColors(R.color.basic_difficulty_color);
                break;
            case Dictionary.Difficulty.ADVANCED:
                difficultyImageView.setImageResource(R.drawable.advanced_level_icon);
                changeLayoutColors(R.color.advanced_difficulty_color);
                break;
        }
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

    public void changeLayoutColors(int colorId){
        topBarLayout.setBackgroundResource(colorId);
    }
}
