package com.example.nakama.Activities.AttemptSummaryActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nakama.Activities.TopBar.TopBarViewManager;
import com.example.nakama.DataBase.AppDatabase;
import com.example.nakama.DataBase.Entities.UserScores.UserScore;
import com.example.nakama.DataBase.Entities.Users.Users;
import com.example.nakama.R;
import com.example.nakama.SharedPreferences.AppPreferences;
import com.example.nakama.Utils.Dictionary;

import java.util.Locale;

public class RingSummaryActivityViewManager {
    AppPreferences appPreferences;
    UserScore userScore;
    AppCompatActivity activity;
    TextView timeTextView;
    TextView userScoreTextView;
    TextView samplesFoundTextView;
    TextView falseAlarmsCounter;
    TextView falseAlarmsScoreImpact;
    TextView defecationCounter;
    TextView defecationScoreImpact;
    TextView droppedTreatCounter;
    TextView droppedTreatScoreImpact;
    TextView overallImpressionLabel;
    LinearLayout overallImpressionContainer;
    TextView disqualificationReasonLabel;
    TextView disqualificationReason;
    boolean failedAttemptFlag = false;

    public RingSummaryActivityViewManager(AppCompatActivity activity, Users user, String difficulty, String ring) {
        this.activity = activity;
        appPreferences = new AppPreferences(activity.getSharedPreferences(AppPreferences.NAME, Context.MODE_PRIVATE));
        AppDatabase db = AppDatabase.getInstance(activity);
        userScore = db.userScoresDao().getUserScore(appPreferences.getUserId(), appPreferences.getDifficulty(), appPreferences.getActiveRing());

        timeTextView = activity.findViewById(R.id.summaryTimeValue);
        userScoreTextView = activity.findViewById(R.id.summaryPointsValue);
        samplesFoundTextView = activity.findViewById(R.id.samplesFoundValue);
        falseAlarmsCounter = activity.findViewById(R.id.falseAlarmsValue);
        falseAlarmsScoreImpact = activity.findViewById(R.id.falseAlarmsScoreImpact);
        defecationCounter = activity.findViewById(R.id.summaryDefecationValue);
        defecationScoreImpact = activity.findViewById(R.id.summaryDefecationScoreImpact);
        droppedTreatCounter = activity.findViewById(R.id.summaryDroppedTreatsValue);
        droppedTreatScoreImpact = activity.findViewById(R.id.summaryDroppedTreatsScoreImpact);
        overallImpressionLabel = activity.findViewById(R.id.summaryOverallImpressionLabel);
        overallImpressionContainer = activity.findViewById(R.id.overallImpressionContainer);
        disqualificationReasonLabel = activity.findViewById(R.id.disqualificationReasonLabel);
        disqualificationReason = activity.findViewById(R.id.disqualificationReasonText);

        new TopBarViewManager(activity, user, difficulty, ring);
    }

    public void setUserScore(){
        timeTextView.setText(userScore.attemptTime);
        userScoreTextView.setText(String.format(Locale.ENGLISH, "%d pkt.",userScore.score));
        setSamplesFoundTextView(userScore.samplesFound);
        setFalseAlarms();
        setDefecation();
        setTreatDropped();
        setOverallImpression();
        showDisqualificationReason();
        if(failedAttemptFlag || (userScore.disqualification_reason != null)){
            overrideScoresImpactWithFailed();
        }

    }
    public void setSamplesFoundTextView(int samplesFound){
        int samplesToBeFound;
        if(appPreferences.getDifficulty().equals(Dictionary.Difficulty.Basic.NAME)){
            samplesToBeFound = Dictionary.Difficulty.Basic.SAMPLES_TO_FIND;
        }
        else {
            samplesToBeFound = Dictionary.Difficulty.Advanced.SAMPLES_TO_FIND;
        }
        samplesFoundTextView.setText(String.format(Locale.ENGLISH, "%d/%d",samplesFound, samplesToBeFound));
    }

    public void setFalseAlarms(){
        int falseAlarmsLimit = Dictionary.getFalseAlarmsLimit(appPreferences.getDifficulty());
        if(userScore.falseAlarms > falseAlarmsLimit){
            falseAlarmsScoreImpact.setTextColor(activity.getColor(R.color.advanced_theme_light_error));
            failedAttemptFlag = true;
        }
        falseAlarmsCounter.setText(String.valueOf(userScore.falseAlarms));
        int scoreImpact = userScore.falseAlarms * 50;
        if(scoreImpact != 0){
            falseAlarmsScoreImpact.setText(String.format(Locale.ENGLISH, "-%d pkt.", scoreImpact));
        }
        else {
            falseAlarmsScoreImpact.setText("-");
        }
    }

    public void setDefecation(){
        if(userScore.defecation){
            defecationCounter.setText("1");
            defecationScoreImpact.setTextColor(activity.getColor(R.color.advanced_theme_light_error));
            failedAttemptFlag = true;
        }
        else {
            defecationCounter.setText("0");
            defecationScoreImpact.setText("-");
        }
    }

    public void setTreatDropped(){
        if(userScore.treatDrop >= 2){
            droppedTreatScoreImpact.setTextColor(activity.getColor(R.color.advanced_theme_light_error));
            failedAttemptFlag = true;
        }
        droppedTreatCounter.setText(String.valueOf(userScore.treatDrop));
        int treatDropScoreModifier = userScore.treatDrop * 30;
        if(treatDropScoreModifier != 0){
            droppedTreatScoreImpact.setText(String.format(Locale.ENGLISH, "-%d pkt.", treatDropScoreModifier));
        }
        else {
            droppedTreatScoreImpact.setText("-");
        }
    }

    public void setOverallImpression(){
        if(userScore.overview != null){
            String[] overviewStrings = userScore.overview.replace("{","").replace("}","").split(",");
            overallImpressionLabel.setVisibility(View.VISIBLE);

            for(String pair : overviewStrings){
                View view = LayoutInflater.from(activity).inflate(R.layout.overall_impression_displayes, overallImpressionContainer, false);
                overallImpressionContainer.addView(view);
                TextView description = view.findViewById(R.id.overallImpressionDescription);
                String[] dividedPair = pair.split("=");
                description.setText(dividedPair[0].trim());
                TextView score = view.findViewById(R.id.overallImpressionScore);
                score.setText(String.format(Locale.ENGLISH, "-%s pkt.",dividedPair[1]));
            }
        }
    }

    public void showDisqualificationReason(){
        if(userScore.disqualification_reason != null){
            disqualificationReasonLabel.setVisibility(View.VISIBLE);
            disqualificationReason.setVisibility(View.VISIBLE);
            disqualificationReason.setText(userScore.disqualification_reason);
        }
    }

    public void overrideScoresImpactWithFailed(){
        falseAlarmsScoreImpact.setText(R.string.failed_attempt);
        defecationScoreImpact.setText(R.string.failed_attempt);
        droppedTreatScoreImpact.setText(R.string.failed_attempt);
    }

}