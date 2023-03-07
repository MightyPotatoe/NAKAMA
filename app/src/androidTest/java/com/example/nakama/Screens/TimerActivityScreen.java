package com.example.nakama.Screens;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.isNotEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.IsNot.not;

import android.content.Context;
import android.view.View;

import androidx.test.core.app.ActivityScenario;

import com.example.nakama.Activities.TimerActivity.TimerActivity;
import com.example.nakama.DataBase.Entities.UserScores.UserScore;
import com.example.nakama.DataBase.Entities.Users.Users;
import com.example.nakama.R;
import com.example.nakama.SharedPreferences.AppPreferences;
import com.example.nakama.Utils.Action;
import com.example.nakama.Utils.Converter;
import com.example.nakama.Utils.Validate;

import org.hamcrest.Matcher;
import org.junit.Assert;

public class TimerActivityScreen extends BaseScreen{

    public static Matcher<View> timerTextView =  withId(R.id.timeTextView);
    public static Matcher<View> usernameTextView =  withId(R.id.timerUserDetailsTextView);
    public static Matcher<View> difficultyTextView =  withId(R.id.timerDifficultyTextView);
    public static Matcher<View> ringTextView =  withId(R.id.timerRingDetailsTextView);
    public static Matcher<View> scoreTextView = withId(R.id.timerScore);
    public static Matcher<View> falseAlarmsTextView =  withId(R.id.timerFalseAlarmCounter);
    public static Matcher<View> samplesFoundTextView =  withId(R.id.timerSamplesFound);
    public static Matcher<View> defecationTextView =  withId(R.id.timerDefecationCounter);
    public static Matcher<View> droppedTreatTextView =  withId(R.id.timerDroppedTreatsCounter);
    private final Matcher<View> progressBar =  withId(R.id.timeProgressBar);

    //----Action Buttons---
    public static Matcher<View> falseAlarmButton =  withId(R.id.timerFalseAlarmButton);
    public static Matcher<View> positiveAlarmButton =  withId(R.id.timerPositiveAlarmButton);
    public static Matcher<View> droppedTreatButton =  withId(R.id.timerDroppedTreatButton);
    public static Matcher<View> defecationButton =  withId(R.id.timerDefecationButton);
    public static Matcher<View> disqualifiedButton =  withId(R.id.timerDisqualificationButton);

    //----Timer Buttons---
    public static Matcher<View> playButton =  withId(R.id.playButton);
    public static Matcher<View> pauseButton =  withId(R.id.pauseButton);
    public static Matcher<View> resetButton =  withId(R.id.resetButton);
    public static Matcher<View> doneButton =  withId(R.id.doneButton);

    //---Dialog ---
    public static Matcher<View> reasonEditText =  withId(R.id.textInput);

    public String getDifficultyTopBarText(){
        return Action.getText(difficultyTextView);
    }
    public String getUsernameTopBarText(){
        return Action.getText(usernameTextView);
    }
    public String gerRingTopBarText(){
        return Action.getText(ringTextView);
    }

    public TimerActivityScreen(ActivityScenario<?> scenario) {
        super(scenario);
        scenario.onActivity(activity -> appPreferences.setPollingFrequency(500));
    }
    public TimerActivityScreen(ActivityScenario<?> scenario, int time) {
        super(scenario);
        scenario.onActivity(activity -> {
            appPreferences.setPollingFrequency(500);
            appPreferences.setRingTime(time);
        });
    }

    public TimerActivityScreen(int timer, int pollingFrequency){
        super(ActivityScenario.launch(TimerActivity.class));
        scenario.onActivity(activity -> {
            appPreferences = new AppPreferences(activity.getSharedPreferences(AppPreferences.NAME, Context.MODE_PRIVATE));
            appPreferences.setPollingFrequency(pollingFrequency);
            appPreferences.setRingTime(timer);
            Users defaultUser = new Users("Tomasz", "Szymaniak", "Nala");
            int userId = db.getUserId(defaultUser);
            db.clearUserScore(userId, appPreferences.getDifficulty(), appPreferences.getActiveRing());
        });
        scenario.recreate();
    }

    public void clickPlayButton() {
        Action.clickOnView(playButton);
    }

    public void clickPauseButton() {
        Action.clickOnView(pauseButton);
    }

    public void clickResetButtonButton() {
        Action.clickOnView(resetButton);
    }

    public void clickFalseAlarm() {
        Action.clickOnView(falseAlarmButton);
    }

    public void clickPositiveAlarm() {
        Action.clickOnView(positiveAlarmButton);
    }

    public void clickTreatDroppedButton() {
        Action.clickOnView(droppedTreatButton);
    }
    public void clickDefecationButton() {
        Action.clickOnView(defecationButton);
    }
    public void clickDisqualificationButton() {
        Action.clickOnView(disqualifiedButton);
    }
    public void confirmDisqualification() {
        validateDisqualificationConfirmationDialog();
        Action.clickByText(R.string.dialog_positive_yes_button);
    }

    public RingResultActivityScreen provideReasonAndDismissDisqualificationDialog(String reason){
        validateDisqualificationReasonDialog();
        Action.sendKeys(reasonEditText, reason);
        Action.clickByText(R.string.dialog_positive_ok_button);
        return new RingResultActivityScreen(scenario);
    }

    public void confirmFalseAlarm() {
        validateFalseAlarmConfirmationDialog();
        Action.clickByText(R.string.dialog_positive_yes_button);
    }

    public void confirmPositiveAlarm(){
        validatePositiveAlarmConfirmationDialog();
        Action.clickByText(R.string.dialog_positive_yes_button);
    }
    public void confirmTreatDropped(){
        validateTreatDroppedConfirmationDialog();
        Action.clickByText(R.string.dialog_positive_yes_button);
    }
    public void confirmDefecation(){
        validateDefecationConfirmationDialog();
        Action.clickByText(R.string.dialog_positive_yes_button);
    }

    public void confirmReset(){
        validateConfirmResetDialog();
        Action.clickByText(R.string.dialog_positive_yes_button);
    }

    public void cancelReset(){
        validateConfirmResetDialog();
        Action.clickByText(R.string.dialog_negative_button);
    }
    public void dismissFalseAlarmLimitReachedDialog(){
        validateFalseAlarmLimitReachedDialog();
        Action.clickByText(R.string.dialog_positive_ok_button);
    }
    public void dismissAllSamplesFoundDialog(){
        validateAllSamplesFoundDialog();
        Action.clickByText(R.string.dialog_positive_ok_button);
    }
    public void dismissTreatDroppedLimitReachedDialog(){
        validateTreatDroppedLimitReachedDialog();
        Action.clickByText(R.string.dialog_positive_ok_button);
    }

    public void dismissTimeoutDialog(){
        validateTimeoutDialog();
        Action.clickByText(R.string.dialog_positive_ok_button);
    }

    public RingResultActivityScreen dismissDisqualificationDialog(){
        validateDisqualificationDialog();
        Action.clickByText(R.string.dialog_positive_ok_button);
        return new RingResultActivityScreen(scenario);
    }

    public void validateScores(int score, int falseAlarms, int defecation, int droppedTreats, int samplesFound){
        UserScore userScore = db.userScoresDao().getUserScore(appPreferences.getUserId(), appPreferences.getDifficulty(), appPreferences.getActiveRing());

        Assert.assertEquals(score, userScore.score);
        Assert.assertEquals(String.format("%d pkt", score), Action.getText(scoreTextView));

        Assert.assertEquals(String.valueOf(falseAlarms), Action.getText(falseAlarmsTextView));
        Assert.assertEquals(falseAlarms, userScore.falseAlarms);

        if(defecation == 1){
            Assert.assertTrue(userScore.defecation);
            Assert.assertEquals("1", Action.getText(defecationTextView));
        }
        else if(defecation == 0){
            Assert.assertFalse(userScore.defecation);
            Assert.assertEquals("0", Action.getText(defecationTextView));
        }

        Assert.assertEquals(String.valueOf(droppedTreats), Action.getText(droppedTreatTextView));
        Assert.assertEquals(droppedTreats, userScore.treatDrop);

        Assert.assertEquals(String.valueOf(samplesFound), Action.getText(samplesFoundTextView));
        Assert.assertEquals(samplesFound, userScore.samplesFound);
    }

    public void validateTimer(String timerValue, int progressBarProgress){
        Assert.assertEquals(timerValue, Action.getText(timerTextView));
        Assert.assertEquals(Integer.valueOf(progressBarProgress), Action.getProgress(progressBar));
    }

    public void validateFalseAlarmConfirmationDialog(){
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.confirm_dialog_title));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.confirm_false_alarm_dialog_message));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.dialog_positive_yes_button));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.dialog_negative_button));
    }

    public void validatePositiveAlarmConfirmationDialog(){
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.confirm_dialog_title));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.confirm_positive_alarm_dialog_message));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.dialog_positive_yes_button));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.dialog_negative_button));
    }

    public void validateFalseAlarmLimitReachedDialog(){
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.false_alarms_reached_dialog_title));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.false_alarms_reached_dialog_message));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.dialog_positive_ok_button));
    }
    public void validateAllSamplesFoundDialog(){
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.all_samples_found_dialog_title));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.all_samples_found_dialog_message));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.dialog_positive_ok_button));
    }
    public void validateTreatDroppedConfirmationDialog(){
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.confirm_dialog_title));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.confirm_treat_dropped_dialog_message));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.dialog_positive_yes_button));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.dialog_negative_button));
    }
    public void validateDefecationConfirmationDialog(){
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.confirm_dialog_title));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.confirm_defecation_dialog_message));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.dialog_positive_yes_button));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.dialog_negative_button));
    }
    public void validateDisqualificationConfirmationDialog(){
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.confirm_dialog_title));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.confirm_disqualification_dialog_message));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.dialog_positive_yes_button));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.dialog_negative_button));
    }
    public void validateDisqualificationReasonDialog(){
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.disqualification_dialog_title));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.provide_disqualification_reason_dialog_message));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.dialog_positive_ok_button));
    }
    public void validateTreatDroppedLimitReachedDialog(){
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.tread_dropped_limit_reached_dialog_title));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.tread_dropped_limit_reached_dialog_message));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.dialog_positive_ok_button));
    }
    public void validateConfirmResetDialog(){
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.confirm_dialog_title));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.confirm_reset_dialog_message));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.dialog_positive_yes_button));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.dialog_negative_button));
    }
    public void validateTimeoutDialog(){
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.timeout_dialog_title));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.timeout_dialog_message));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.dialog_positive_ok_button));
    }
    public void validateDisqualificationDialog(){
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.disqualification_dialog_title));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.defecation_disqualification_reason));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.dialog_positive_ok_button));
    }

    public String getTimerText(){
        return Action.getText(timerTextView);
    }

    public void validateDefaultStateTimerButtons(){
        onView(playButton).check(matches(isDisplayed()));
        onView(pauseButton).check(matches(not(isDisplayed())));
        onView(resetButton).check(matches(not(isDisplayed())));
        onView(doneButton).check(matches(not(isDisplayed())));
    }

    public void validateInProgressStateTimerButtons(){
        onView(playButton).check(matches(not(isDisplayed())));
        onView(pauseButton).check(matches(isDisplayed()));
        onView(resetButton).check(matches(not(isDisplayed())));
        onView(doneButton).check(matches(not(isDisplayed())));
    }

    public void validateInPausedStateTimerButtons(){
        onView(playButton).check(matches(isDisplayed()));
        onView(pauseButton).check(matches(not(isDisplayed())));
        onView(resetButton).check(matches(isDisplayed()));
        onView(doneButton).check(matches(isDisplayed()));
    }

    public void validateFinishedButtons(){
        onView(playButton).check(matches(not(isDisplayed())));
        onView(pauseButton).check(matches(not(isDisplayed())));
        onView(resetButton).check(matches(isDisplayed()));
        onView(doneButton).check(matches(isDisplayed()));
    }
    public void validateTimer(int expectedTime){
        Assert.assertEquals(Integer.valueOf(expectedTime), Action.getProgress(progressBar));
        Assert.assertEquals(Converter.millisToString(expectedTime), Action.getText(timerTextView));
    }

    public void validateIfActionButtonsAreDisabled(){
        onView(falseAlarmButton).check(matches(isNotEnabled()));
        onView(positiveAlarmButton).check(matches(isNotEnabled()));
        onView(droppedTreatButton).check(matches(isNotEnabled()));
        onView(defecationButton).check(matches(isNotEnabled()));
        onView(disqualifiedButton).check(matches(isNotEnabled()));
    }

    public void validateIfActionButtonsAreEnabled(){
        onView(falseAlarmButton).check(matches(isEnabled()));
        onView(positiveAlarmButton).check(matches(isEnabled()));
        onView(droppedTreatButton).check(matches(isEnabled()));
        onView(defecationButton).check(matches(isEnabled()));
        onView(disqualifiedButton).check(matches(isEnabled()));
    }
}