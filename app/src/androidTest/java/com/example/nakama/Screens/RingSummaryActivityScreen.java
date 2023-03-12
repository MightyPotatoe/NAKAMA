package com.example.nakama.Screens;

import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.view.View;

import androidx.test.core.app.ActivityScenario;

import com.example.nakama.R;
import com.example.nakama.Utils.Action;
import com.example.nakama.Utils.Converter;
import com.example.nakama.Utils.Finder;
import com.example.nakama.Utils.Validate;

import org.hamcrest.Matcher;
import org.junit.Assert;

public class RingSummaryActivityScreen extends BaseScreen{
    private final Matcher<View> summaryPointsTextView =  withId(R.id.summaryPointsValue);
    private final Matcher<View> summaryTime =  withId(R.id.summaryTimeValue);
    private final Matcher<View> samplesFound =  withId(R.id.samplesFoundValue);
    private final Matcher<View> falseAlarmsCount =  withId(R.id.falseAlarmsValue);
    private final Matcher<View> defecationCount =  withId(R.id.summaryDefecationValue);
    private final Matcher<View> droppedTreatCount =  withId(R.id.summaryDroppedTreatsValue);
    private final Matcher<View> defecationScoreImpact =  withId(R.id.summaryDefecationScoreImpact);
    private final Matcher<View> falseAlarmsScoreImpact =  withId(R.id.falseAlarmsScoreImpact);
    private final Matcher<View> droppedTreatScoreImpact =  withId(R.id.summaryDroppedTreatsScoreImpact);

    private final Matcher<View> impressionsLabel =  withId(R.id.summaryOverallImpressionLabel);
    private final Matcher<View> impressionDescription =  withId(R.id.overallImpressionDescription);
    private final Matcher<View> impressionScore =  withId(R.id.overallImpressionScore);
    private final Matcher<View> disqualificationReasonLabel =  withId(R.id.disqualificationReasonLabel);
    private final Matcher<View> disqualificationReason =  withId(R.id.disqualificationReasonText);
    private final Matcher<View> nextButton =  withId(R.id.ringSummaryNextButton);

    public RingSummaryActivityScreen(ActivityScenario<?> scenario) {
        super(scenario);
    }

    public String getSummaryPoints(){
        return Action.getText(summaryPointsTextView);
    }
    public String getSummaryTime(){
        return Action.getText(summaryTime);
    }
    public String getSamplesFound(){
        return Action.getText(samplesFound);
    }
    public String getFalseAlarmsCounter(){
        return Action.getText(falseAlarmsCount);
    }
    public String getFalseAlarmsScoreImpact(){
        return Action.getText(falseAlarmsScoreImpact);
    }
    public String getDefecationCounter(){
        return Action.getText(defecationCount);
    }
    public String getDefecationScoreImpact(){
        return Action.getText(defecationScoreImpact);
    }
    public String getTreatDroppedScoreImpact(){
        return Action.getText(droppedTreatScoreImpact);
    }
    public String getTreatDroppedCounter(){
        return Action.getText(droppedTreatCount);
    }
    public void validateTimerResult(int attemptTime, String timeLeftOnAttempt){
        int timeSpent = Converter.stringToMillis(timeLeftOnAttempt);
        int timeSpentOnAttempt = attemptTime - timeSpent;
        Assert.assertEquals(Converter.millisToString(timeSpentOnAttempt), Action.getText(summaryTime));
    }

    public Matcher<View> getDisqualificationReasonLabelElement(){
        return disqualificationReasonLabel;
    }

    public Matcher<View> getDisqualificationReasonElement(){
        return disqualificationReason;
    }

    public Matcher<View> getImpressionsLabelElement(){
        return impressionsLabel;
    }

    public String getDescriptionAtPosition(int index){
        return Action.getText(Finder.getElementFromMatchAtPosition(impressionDescription, index));
    }

    public String getDescriptionScoreAtPosition(int index){
        return Action.getText(Finder.getElementFromMatchAtPosition(impressionScore, index));
    }

    public void clickNextButton(){
        Action.clickOnView(nextButton);
    }

    public MainActivityScreen confirmNexAttempt(){
        validateConfirmNextAttemptDialog();
        Action.clickByText(R.string.dialog_positive_yes_button);
        return new MainActivityScreen(scenario);
    }

    public void cancelNexAttempt(){
        validateConfirmNextAttemptDialog();
        Action.clickByText(R.string.dialog_negative_button);
    }

    public void validateConfirmNextAttemptDialog(){
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.confirm_dialog_title));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.confirm_new_attempt_dialog_message));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.dialog_positive_yes_button));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.dialog_negative_button));
    }
}
