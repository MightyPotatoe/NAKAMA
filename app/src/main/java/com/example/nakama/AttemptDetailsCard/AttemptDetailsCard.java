package com.example.nakama.AttemptDetailsCard;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.nakama.DataBase.Entities.UserScores.UserScore;
import com.example.nakama.R;
import com.example.nakama.Utils.Dictionary;
import com.google.android.material.textview.MaterialTextView;

import java.util.Locale;

public class AttemptDetailsCard {

    private final AppCompatActivity activity;
    private final String DIFFICULTY;
    private final MaterialTextView ringAndScoreLabel;
    private final MaterialTextView summaryTimeValue;
    private final MaterialTextView samplesFoundValue;
    private final LinearLayout moreDetailsLayoutBar;
    private final ConstraintLayout showMoreLayout;
    private final TableLayout scoreDetailsContainer;
    private final MaterialTextView showDetails;
    private final ImageView showDetailsIcon;
    private final MaterialTextView falseAlarmsValue;
    private final MaterialTextView falseAlarmsScoreImpact;
    private final MaterialTextView summaryDefecationValue;
    private final MaterialTextView summaryDefecationScoreImpact;
    private final MaterialTextView summaryDroppedTreatsValue;
    private final MaterialTextView summaryDroppedTreatsScoreImpact;
    private final MaterialTextView summaryOverallImpressionLabel;
    private final LinearLayout overallImpressionContainer;
    private final MaterialTextView disqualificationReasonLabel;
    private final MaterialTextView disqualificationReasonText;
    private final MaterialTextView summaryTimeLabel;
    private final MaterialTextView samplesFoundLabel;
    private final TableLayout ringSummaryTable;

    boolean failedAttemptFlag = false;




    public AttemptDetailsCard(View view, AppCompatActivity activity, String difficulty) {
        this.activity = activity;
        DIFFICULTY = difficulty;

        ringAndScoreLabel = view.findViewById(R.id.ringLabel);
        summaryTimeValue = view.findViewById(R.id.summaryTimeValue);
        samplesFoundValue = view.findViewById(R.id.samplesFoundValue);
        moreDetailsLayoutBar = view.findViewById(R.id.moreDetailsLayoutBar);
        scoreDetailsContainer = view.findViewById(R.id.scoreDetailsContainer);
        showDetails = view.findViewById(R.id.showDetails);
        showDetailsIcon = view.findViewById(R.id.showDetailsIcon);
        falseAlarmsValue = view.findViewById(R.id.falseAlarmsValue);
        falseAlarmsScoreImpact = view.findViewById(R.id.falseAlarmsScoreImpact);
        summaryDefecationValue = view.findViewById(R.id.summaryDefecationValue);
        summaryDefecationScoreImpact = view.findViewById(R.id.summaryDefecationScoreImpact);
        summaryDroppedTreatsValue = view.findViewById(R.id.summaryDroppedTreatsValue);
        summaryDroppedTreatsScoreImpact = view.findViewById(R.id.summaryDroppedTreatsScoreImpact);
        summaryOverallImpressionLabel = view.findViewById(R.id.summaryOverallImpressionLabel);
        overallImpressionContainer = view.findViewById(R.id.overallImpressionContainer);
        disqualificationReasonLabel = view.findViewById(R.id.disqualificationReasonLabel);
        disqualificationReasonText = view.findViewById(R.id.disqualificationReasonText);
        summaryTimeLabel = view.findViewById(R.id.summaryTimeLabel);
        samplesFoundLabel = view.findViewById(R.id.samplesFoundLabel);
        ringSummaryTable = view.findViewById(R.id.ringSummaryTable);

        showMoreLayout = view.findViewById(R.id.showMoreLayout);
        showMoreLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(scoreDetailsContainer.getVisibility() == View.GONE){
                    scoreDetailsContainer.setVisibility(View.VISIBLE);
                    showDetails.setText(R.string.hide_more);
                    showDetailsIcon.setImageResource(R.drawable.baseline_expand_less_24);
                    overallImpressionContainer.setVisibility(View.GONE);
                    overallImpressionContainer.setVisibility(View.VISIBLE);

                }
                else {
                    scoreDetailsContainer.setVisibility(View.GONE);
                    showDetails.setText(R.string.show_more);
                    showDetailsIcon.setImageResource(R.drawable.baseline_expand_more_24);
                    overallImpressionContainer.setVisibility(View.GONE);
                }
            }
        });

    }

    public void setCardContent(String ringName, UserScore userScore, String disqualificationReason) {

        if(disqualificationReason != null){
            ringAndScoreLabel.setText(String.format(Locale.ENGLISH,"%s - Zdyskwalifikowany.", ringName));
            ringSummaryTable.setVisibility(View.GONE);
            moreDetailsLayoutBar.setVisibility(View.GONE);
            showDisqualificationReason(disqualificationReason);
        }
        else if(userScore == null){
            ringAndScoreLabel.setText(ringName);
            summaryTimeValue.setText(R.string.no_record);
            samplesFoundValue.setVisibility(View.GONE);
            moreDetailsLayoutBar.setVisibility(View.GONE);
        }
        else {
            if(userScore.attemptTime == null || userScore.attemptTime.isEmpty()){
                ringAndScoreLabel.setText(String.format(Locale.ENGLISH,"%s", ringName));
                summaryTimeValue.setText(R.string.disrupted_attempt);
                samplesFoundValue.setVisibility(View.GONE);
                moreDetailsLayoutBar.setVisibility(View.GONE);
            }
            else {
                ringAndScoreLabel.setText(String.format(Locale.ENGLISH,"%s - %d pkt.", ringName, userScore.score));
                summaryTimeValue.setText(userScore.attemptTime);
                setSamplesFoundTextView(userScore.samplesFound);
                setFalseAlarms(userScore.falseAlarms);
                falseAlarmsValue.setText(String.valueOf(userScore.falseAlarms));
                setDefecation(userScore.defecation);
                setTreatDropped(userScore.treatDrop);
                setOverallImpression(userScore.overview);
                if(failedAttemptFlag || (userScore.disqualification_reason != null)){
                    overrideScoresImpactWithFailed();
                    String ringAndScore = ringAndScoreLabel.getText().toString() + " - Niezaliczone";
                    ringAndScoreLabel.setText(ringAndScore);
                }
            }
        }
    }

    public void setSamplesFoundTextView(int samplesFound){
        int samplesToBeFound;
        if(DIFFICULTY.equals(Dictionary.Difficulty.Basic.NAME)){
            samplesToBeFound = Dictionary.Difficulty.Basic.SAMPLES_TO_FIND;
        }
        else {
            samplesToBeFound = Dictionary.Difficulty.Advanced.SAMPLES_TO_FIND;
        }
        samplesFoundValue.setText(String.format(Locale.ENGLISH, "%d/%d",samplesFound, samplesToBeFound));
    }

    public void setFalseAlarms(int falseAlarms){
        int falseAlarmsLimit = Dictionary.getFalseAlarmsLimit(DIFFICULTY);
        if(falseAlarms > falseAlarmsLimit){
            falseAlarmsScoreImpact.setTextColor(activity.getColor(R.color.advanced_theme_light_error));
            failedAttemptFlag = true;
        }
        falseAlarmsValue.setText(String.valueOf(falseAlarms));
        int scoreImpact = falseAlarms * 50;
        if(scoreImpact != 0){
            falseAlarmsScoreImpact.setText(String.format(Locale.ENGLISH, "-%d pkt.", scoreImpact));
        }
        else {
            falseAlarmsScoreImpact.setText("-");
        }
    }

    public void setDefecation(boolean defecation){
        if(defecation){
            summaryDefecationValue.setText("1");
            summaryDefecationScoreImpact.setTextColor(activity.getColor(R.color.advanced_theme_light_error));
            failedAttemptFlag = true;
        }
        else {
            summaryDefecationValue.setText("0");
            summaryDefecationScoreImpact.setText("-");
        }
    }

    public void setTreatDropped(int treatDrop){
        if(treatDrop >= 2){
            summaryDroppedTreatsScoreImpact.setTextColor(activity.getColor(R.color.advanced_theme_light_error));
            failedAttemptFlag = true;
        }
        summaryDroppedTreatsValue.setText(String.valueOf(treatDrop));
        int treatDropScoreModifier = treatDrop * 30;
        if(treatDropScoreModifier != 0){
            summaryDroppedTreatsScoreImpact.setText(String.format(Locale.ENGLISH, "-%d pkt.", treatDropScoreModifier));
        }
        else {
            summaryDroppedTreatsScoreImpact.setText("-");
        }
    }

    public void setOverallImpression(String overview){
        if(overview != null){
            String[] overviewStrings = overview.replace("{","").replace("}","").split(",");
            summaryOverallImpressionLabel.setVisibility(View.VISIBLE);

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

    public void showDisqualificationReason(String disqualification_reason){
        if(disqualification_reason != null){
            disqualificationReasonLabel.setVisibility(View.VISIBLE);
            disqualificationReasonText.setVisibility(View.VISIBLE);
            disqualificationReasonText.setText(disqualification_reason);
        }
    }

    public void overrideScoresImpactWithFailed(){
        falseAlarmsScoreImpact.setText(R.string.failed_attempt);
        summaryDefecationScoreImpact.setText(R.string.failed_attempt);
        summaryDroppedTreatsScoreImpact.setText(R.string.failed_attempt);
    }
}
