package com.example.nakama.Activities.TimerActivity;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nakama.Activities.TopBar.TopBarViewManager;
import com.example.nakama.DataBase.AppDatabase;
import com.example.nakama.DataBase.Entities.UserScores.UserScore;
import com.example.nakama.DataBase.Entities.Users.Users;
import com.example.nakama.R;
import com.example.nakama.Utils.Converter;
import com.example.nakama.Utils.Dictionary;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.Locale;

public class TimerActivityViewManager {
    private final AppCompatActivity activity;
    private final TextView timerTextView;
    private final CircularProgressIndicator timerProgressBar;
    private final ImageButton playButton;
    private final ImageButton pauseButton;
    private final ImageButton resetButton;
    private final ImageButton doneButton;
    private final TextView actualScoreTextView;
    private final TextView falseAlarmsTextView;
    private final TextView defecationTextView;
    private final TextView droppedTreatsTextView;
    private final TextView samplesFoundTextView;
    private final FloatingActionButton falseAlarmsButton;
    private final FloatingActionButton positiveAlarmsButton;
    private final Button defecationButton;
    private final Button droppedTreatsButton;
    private final Button disqualifiedButton;

    public TimerActivityViewManager(AppCompatActivity activity) {
        this.activity = activity;
        this.timerTextView = activity.findViewById(R.id.timeTextView);
        timerProgressBar = activity.findViewById(R.id.timeProgressBar);
        playButton = activity.findViewById(R.id.playButton);
        pauseButton = activity.findViewById(R.id.pauseButton);
        resetButton = activity.findViewById(R.id.resetButton);
        doneButton = activity.findViewById(R.id.doneButton);
        actualScoreTextView = activity.findViewById(R.id.timerScore);
        falseAlarmsTextView = activity.findViewById(R.id.timerFalseAlarmCounter);
        defecationTextView = activity.findViewById(R.id.timerDefecationCounter);
        droppedTreatsTextView = activity.findViewById(R.id.timerDroppedTreatsCounter);
        falseAlarmsButton = activity.findViewById(R.id.timerFalseAlarmButton);
        positiveAlarmsButton = activity.findViewById(R.id.timerPositiveAlarmButton);
        defecationButton = activity.findViewById(R.id.timerDefecationButton);
        droppedTreatsButton = activity.findViewById(R.id.timerDroppedTreatButton);
        disqualifiedButton = activity.findViewById(R.id.timerDisqualificationButton);
        samplesFoundTextView = activity.findViewById(R.id.timerSamplesFound);
    }
    public CircularProgressIndicator getTimerProgressBar() {
        return timerProgressBar;
    }
    public TextView getTimerTextView() {
        return timerTextView;
    }
    public void setViewToDefaultState(String difficulty, String ring, Users user){
        timerProgressBar.setMax(Dictionary.getAttemptTime(difficulty));
        setTimerCurrentTime(Dictionary.getAttemptTime(difficulty));

        playButton.setVisibility(View.VISIBLE);
        pauseButton.setVisibility(View.INVISIBLE);
        resetButton.setVisibility(View.INVISIBLE);
        doneButton.setVisibility(View.INVISIBLE);
        new TopBarViewManager(activity, user, difficulty, ring);
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
        setFunctionalButtonsEnabled(true);
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
    public void updateScores(AppDatabase db, Users user, String difficulty, String ring){
        UserScore userScore = db.userScoresDao().getUserScore(user.uid, difficulty, ring);
        actualScoreTextView.setText(String.format(Locale.ENGLISH,"%d pkt", userScore.score));
        falseAlarmsTextView.setText(String.valueOf(userScore.falseAlarms));
        droppedTreatsTextView.setText(String.valueOf(userScore.treatDrop));
        samplesFoundTextView.setText(String.valueOf(userScore.samplesFound));
        if(userScore.defecation){
            defecationTextView.setText("1");
        }
        else {
            defecationTextView.setText("0");
        }
    }

    public void setFunctionalButtonsEnabled(boolean enabled){
        falseAlarmsButton.setEnabled(enabled);
        positiveAlarmsButton.setEnabled(enabled);
        defecationButton.setEnabled(enabled);
        droppedTreatsButton.setEnabled(enabled);
        disqualifiedButton.setEnabled(enabled);
        if(enabled){
            disqualifiedButton.setBackgroundColor(activity.getColor(R.color.md_theme_light_errorContainer));
            disqualifiedButton.setAlpha(1f);
            falseAlarmsButton.setBackgroundColor(activity.getColor(R.color.md_theme_light_errorContainer));
            falseAlarmsButton.setAlpha(1f);
        }
        else {
            disqualifiedButton.setBackgroundColor(activity.getColor(com.google.android.material.R.color.switch_thumb_disabled_material_light));
            disqualifiedButton.setAlpha(.5f);
            falseAlarmsButton.setBackgroundColor(activity.getColor(com.google.android.material.R.color.switch_thumb_disabled_material_light));
            falseAlarmsButton.setAlpha(.5f);
        }
    }
}
