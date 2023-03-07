package com.example.nakama;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.nakama.Activities.MainActivity.MainActivity;
import com.example.nakama.Activities.TimerActivity.TimerActivity;
import com.example.nakama.DataBase.AppDatabase;
import com.example.nakama.Screens.MainActivityScreen;
import com.example.nakama.Screens.TimerActivityScreen;
import com.example.nakama.SharedPreferences.AppPreferences;
import com.example.nakama.Utils.Validate;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TimerActivityTests {

    TextView textView;
    CircularProgressIndicator circularProgressIndicator;
    ImageButton playButton;
    ImageButton pauseButton;
    ImageButton restartButton;
    ImageButton doneButton;
    ActivityScenario<TimerActivity> scenario;

    AppPreferences appPreferences;

    AppDatabase db;
    void initializeSettings(){
        scenario = ActivityScenario.launch(TimerActivity.class);
        scenario.onActivity(activity -> {
            appPreferences = new AppPreferences(activity.getSharedPreferences(AppPreferences.NAME, Context.MODE_PRIVATE));
            appPreferences.setPollingFrequency(500);
            appPreferences.setRingTime(10000);
        });
        scenario.recreate();
        initializeView(scenario);
    }

    /**
     * Check default activity state when entering base mode
     */
    @Test
    public void validate_starting_activity_state(){
        MainActivityScreen mainActivityScreen = new MainActivityScreen(ActivityScenario.launch(MainActivity.class));
        mainActivityScreen.clickStartBasicModeButton();
        TimerActivityScreen timerActivityScreen = mainActivityScreen.confirmUserOverride();

        timerActivityScreen.validateTimer(120000);
        timerActivityScreen.validateDefaultStateTimerButtons();
        timerActivityScreen.validateIfActionButtonsAreDisabled();
        timerActivityScreen.validateScores(200,0,0,0,0);
    }

    /**
     * Check if activity is paused and continued later.
     */
    @Test
    public void timer_activity_should_pause_and_continue() throws InterruptedException {
        TimerActivityScreen timerActivityScreen = new TimerActivityScreen(10000, 500);
        String beginningTime = timerActivityScreen.getTimerText();
        Assert.assertEquals("00:10:00", beginningTime);

        timerActivityScreen.clickPlayButton();
        Thread.sleep(1000);
        Assert.assertNotEquals(beginningTime, timerActivityScreen.getTimerText());
        timerActivityScreen.validateIfActionButtonsAreEnabled();
        timerActivityScreen.validateInProgressStateTimerButtons();

        timerActivityScreen.clickPauseButton();
        timerActivityScreen.validateIfActionButtonsAreEnabled();
        timerActivityScreen.validateInPausedStateTimerButtons();
        String currentTime = timerActivityScreen.getTimerText();
        Thread.sleep(1000);
        Assert.assertEquals(currentTime, timerActivityScreen.getTimerText());

        timerActivityScreen.clickPlayButton();
        Thread.sleep(1000);
        Assert.assertNotEquals(currentTime, timerActivityScreen.getTimerText());
        timerActivityScreen.validateIfActionButtonsAreEnabled();
        timerActivityScreen.validateInProgressStateTimerButtons();
    }

    /**
     * When timer is paused or finished pressing reset button should reset activity to default state.
     * Covers: On Finish state
     */
    @Test
    public void timer_should_be_reset_when_reset_button_is_pressed_and_choice_is_confirmed() throws InterruptedException {
        TimerActivityScreen timerActivityScreen = new TimerActivityScreen(5000, 500);
        String beginningTime = timerActivityScreen.getTimerText();
        timerActivityScreen.validateTimer(5000);

        timerActivityScreen.clickPlayButton();
        Thread.sleep(1000);
        Assert.assertNotEquals(beginningTime, timerActivityScreen.getTimerText());
        timerActivityScreen.clickFalseAlarm();
        timerActivityScreen.confirmFalseAlarm();
        timerActivityScreen.clickTreatDroppedButton();
        timerActivityScreen.confirmTreatDropped();
        timerActivityScreen.validateScores(120, 1, 0, 1, 0);

        timerActivityScreen.clickPauseButton();
        timerActivityScreen.validateInPausedStateTimerButtons();
        Assert.assertNotEquals(beginningTime, timerActivityScreen.getTimerText());

        timerActivityScreen.clickResetButtonButton();
        timerActivityScreen.confirmReset();
        timerActivityScreen.validateScores(200, 0, 0, 0, 0);
        timerActivityScreen.validateDefaultStateTimerButtons();
        timerActivityScreen.validateIfActionButtonsAreDisabled();
        timerActivityScreen.validateTimer(5000);

        //Click play and wait for timer to be finished
        timerActivityScreen.clickPlayButton();
        timerActivityScreen.clickFalseAlarm();
        timerActivityScreen.confirmFalseAlarm();
        timerActivityScreen.clickPositiveAlarm();
        timerActivityScreen.confirmPositiveAlarm();
        timerActivityScreen.clickTreatDroppedButton();
        timerActivityScreen.confirmTreatDropped();
        timerActivityScreen.validateScores(120, 1, 0, 1, 1);
        Thread.sleep(5000);
        timerActivityScreen.dismissTimeoutDialog();
        timerActivityScreen.validateTimer(0);
        timerActivityScreen.validateFinishedButtons();
        //Reset
        timerActivityScreen.clickResetButtonButton();
        timerActivityScreen.confirmReset();
        timerActivityScreen.validateScores(200, 0, 0, 0, 0);
        timerActivityScreen.validateDefaultStateTimerButtons();
        timerActivityScreen.validateIfActionButtonsAreDisabled();
        timerActivityScreen.validateTimer(5000);
    }

    /**
     * When timer is paused and pressing reset button is pressed and confirmation is canceled timer should not be reseted.
     */
    @Test
    public void reset_dialog_should_be_dismissed_when_no_is_clicked_and_timer_should_not_be_reset() throws InterruptedException {
        TimerActivityScreen timerActivityScreen = new TimerActivityScreen(5000, 500);
        String beginningTime = timerActivityScreen.getTimerText();
        timerActivityScreen.validateTimer(5000);

        timerActivityScreen.clickPlayButton();
        Thread.sleep(1000);
        Assert.assertNotEquals(beginningTime, timerActivityScreen.getTimerText());
        timerActivityScreen.clickFalseAlarm();
        timerActivityScreen.confirmFalseAlarm();
        timerActivityScreen.clickPositiveAlarm();
        timerActivityScreen.confirmPositiveAlarm();
        timerActivityScreen.clickTreatDroppedButton();
        timerActivityScreen.confirmTreatDropped();

        timerActivityScreen.clickPauseButton();
        timerActivityScreen.validateScores(120, 1, 0, 1, 1);
        timerActivityScreen.validateInPausedStateTimerButtons();
        Assert.assertNotEquals(beginningTime, timerActivityScreen.getTimerText());
        String currentTime = timerActivityScreen.getTimerText();

        timerActivityScreen.clickResetButtonButton();
        timerActivityScreen.cancelReset();
        timerActivityScreen.validateScores(120, 1, 0, 1, 1);
        timerActivityScreen.validateInPausedStateTimerButtons();
        Assert.assertNotEquals(beginningTime, timerActivityScreen.getTimerText());
        Assert.assertEquals(currentTime, timerActivityScreen.getTimerText());
    }


    /**
     * False alarms counter should be incremented. When limit is reached it timer and score should be set to zero.
     * 50pts should be subtract on each false alarm
     */
    @Test
    public void false_alarms_button_should_increase_counter_and_stop_when_limit_reached() throws InterruptedException {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        MainActivityScreen mainActivityScreen = new MainActivityScreen(scenario);
        mainActivityScreen.clickStartAdvancedModeButton();
        TimerActivityScreen timerActivityScreen = mainActivityScreen.confirmUserOverride();

        timerActivityScreen.clickPlayButton();

        Thread.sleep(1000);
        timerActivityScreen.clickFalseAlarm();
        timerActivityScreen.confirmFalseAlarm();
        timerActivityScreen.validateScores(150, 1, 0, 0, 0);

        Thread.sleep(1000);
        timerActivityScreen.clickFalseAlarm();
        timerActivityScreen.confirmFalseAlarm();
        timerActivityScreen.validateScores(100, 2, 0, 0, 0);

        Thread.sleep(1000);
        timerActivityScreen.clickFalseAlarm();
        timerActivityScreen.confirmFalseAlarm();
        timerActivityScreen.dismissFalseAlarmLimitReachedDialog();
        timerActivityScreen.validateScores(0, 3, 0, 0, 0);
        timerActivityScreen.validateTimer("00:00:00", 0);
    }

    /**
     * Samples Found counter should be incremented. When all samples are found timer should be stopped.
     */
    @Test
    public void positive_alarms_button_should_increase_counter_and_stop_when_limit_reached() throws InterruptedException {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        MainActivityScreen mainActivityScreen = new MainActivityScreen(scenario);
        mainActivityScreen.clickStartAdvancedModeButton();
        TimerActivityScreen timerActivityScreen = mainActivityScreen.confirmUserOverride();

        timerActivityScreen.clickPlayButton();

        Thread.sleep(1000);
        timerActivityScreen.clickPositiveAlarm();
        timerActivityScreen.confirmPositiveAlarm();
        timerActivityScreen.validateScores(200, 0, 0, 0, 1);

        Thread.sleep(1000);
        timerActivityScreen.clickPositiveAlarm();
        timerActivityScreen.confirmPositiveAlarm();
        timerActivityScreen.dismissAllSamplesFoundDialog();
        timerActivityScreen.validateScores(200, 0, 0, 0, 2);
        String currentTime = timerActivityScreen.getTimerText();
        Assert.assertNotEquals("04:00:00", currentTime);
        Thread.sleep(1000);
        String timeAfterWait = timerActivityScreen.getTimerText();
        Assert.assertEquals(timeAfterWait, currentTime);
    }

    /**
     * Treat dropped counter should be incremented. When limit is reached timer should be set to 0 and 0 score should be applied. - Basic difficulty
     */
    @Test
    public void treat_dropped_button_should_increase_counter_and_stop_when_limit_reached_with_zero_score_basic() throws InterruptedException {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        MainActivityScreen mainActivityScreen = new MainActivityScreen(scenario);
        mainActivityScreen.clickStartBasicModeButton();
        TimerActivityScreen timerActivityScreen = mainActivityScreen.confirmUserOverride();

        timerActivityScreen.clickPlayButton();

        Thread.sleep(1000);
        timerActivityScreen.clickTreatDroppedButton();
        timerActivityScreen.confirmTreatDropped();
        timerActivityScreen.validateScores(170, 0, 0, 1, 0);

        Thread.sleep(1000);
        timerActivityScreen.clickTreatDroppedButton();
        timerActivityScreen.confirmTreatDropped();
        timerActivityScreen.dismissTreatDroppedLimitReachedDialog();
        timerActivityScreen.validateScores(0, 0, 0, 2, 0);
        timerActivityScreen.validateTimer("00:00:00", 0);
    }

    /**
     * Treat dropped counter should be incremented. When limit is reached timer should be set to 0 and 0 score should be applied. - Advanced difficulty
     */
    @Test
    public void treat_dropped_button_should_increase_counter_and_stop_when_limit_reached_with_zero_score_advanced() throws InterruptedException {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        MainActivityScreen mainActivityScreen = new MainActivityScreen(scenario);
        mainActivityScreen.clickStartAdvancedModeButton();
        TimerActivityScreen timerActivityScreen = mainActivityScreen.confirmUserOverride();

        timerActivityScreen.clickPlayButton();

        Thread.sleep(1000);
        timerActivityScreen.clickTreatDroppedButton();
        timerActivityScreen.confirmTreatDropped();
        timerActivityScreen.validateScores(170, 0, 0, 1, 0);

        Thread.sleep(1000);
        timerActivityScreen.clickTreatDroppedButton();
        timerActivityScreen.confirmTreatDropped();
        timerActivityScreen.dismissTreatDroppedLimitReachedDialog();
        timerActivityScreen.validateScores(0, 0, 0, 2, 0);
        timerActivityScreen.validateTimer("00:00:00", 0);
    }

    //-----------------TEST UTILS---------------------------------------
    private <T extends Activity> void initializeView(ActivityScenario<T> scenario) {
        scenario.onActivity(activity -> {
            textView = activity.findViewById(R.id.timeTextView);
            circularProgressIndicator = activity.findViewById(R.id.timeProgressBar);
            playButton = activity.findViewById(R.id.playButton);
            pauseButton = activity.findViewById(R.id.pauseButton);
            restartButton = activity.findViewById(R.id.resetButton);
            doneButton = activity.findViewById(R.id.doneButton);
        });
    }

    public void validateDefaultActivityState() throws InterruptedException {
        Thread.sleep(1000);
        Assert.assertEquals(View.VISIBLE, circularProgressIndicator.getVisibility());
        Assert.assertEquals(10000, circularProgressIndicator.getMax());
        Assert.assertEquals(10000, circularProgressIndicator.getProgress());
        Assert.assertEquals(View.VISIBLE, playButton.getVisibility());
        Assert.assertEquals(View.INVISIBLE, pauseButton.getVisibility());
        Assert.assertEquals(View.INVISIBLE, restartButton.getVisibility());
        Assert.assertEquals(View.INVISIBLE, doneButton.getVisibility());
        Assert.assertEquals(View.VISIBLE, textView.getVisibility());
        Assert.assertEquals("00:10:00", textView.getText());
    }

    public void validateInProgressActivityState(){
        Assert.assertEquals(View.INVISIBLE, playButton.getVisibility());
        Assert.assertEquals(View.VISIBLE, pauseButton.getVisibility());
        Assert.assertEquals(View.INVISIBLE, restartButton.getVisibility());
        Assert.assertEquals(View.INVISIBLE, doneButton.getVisibility());
        Assert.assertEquals(View.VISIBLE, textView.getVisibility());
        Assert.assertNotEquals("00:10:00", textView.getText());
        Assert.assertEquals(10000, circularProgressIndicator.getMax());
        Assert.assertNotEquals(10000, circularProgressIndicator.getProgress());
    }

    public void validateFinishedActivityState(){
        Assert.assertEquals(View.INVISIBLE, playButton.getVisibility());
        Assert.assertEquals(View.INVISIBLE, pauseButton.getVisibility());
        Assert.assertEquals(View.VISIBLE, restartButton.getVisibility());
        Assert.assertEquals(View.VISIBLE, doneButton.getVisibility());
        Assert.assertEquals(View.VISIBLE, textView.getVisibility());
        Assert.assertEquals("00:00:00", textView.getText());
        Assert.assertEquals(10000, circularProgressIndicator.getMax());
        Assert.assertEquals(0, circularProgressIndicator.getProgress());
    }

    public void validatePausedActivityState(){
        Assert.assertEquals(View.VISIBLE, playButton.getVisibility());
        Assert.assertEquals(View.INVISIBLE, pauseButton.getVisibility());
        Assert.assertEquals(View.VISIBLE, restartButton.getVisibility());
        Assert.assertEquals(View.VISIBLE, doneButton.getVisibility());
        Assert.assertEquals(View.VISIBLE, textView.getVisibility());
        Assert.assertNotEquals("00:10:00", textView.getText());
        Assert.assertEquals(10000, circularProgressIndicator.getMax());
        Assert.assertNotEquals(10000, circularProgressIndicator.getProgress());
    }

    public void validateConfirmResetDialog(){
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.confirm_dialog_title));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.confirm_reset_dialog_message));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.dialog_positive_yes_button));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.dialog_negative_button));
    }

    public void validateConfirmResetDialogIsGone(){
        Assert.assertFalse(Validate.isElementInDialogDisplayedByText(R.string.confirm_dialog_title));
        Assert.assertFalse(Validate.isElementInDialogDisplayedByText(R.string.confirm_reset_dialog_message));
        Assert.assertFalse(Validate.isElementInDialogDisplayedByText(R.string.dialog_positive_yes_button));
        Assert.assertFalse(Validate.isElementInDialogDisplayedByText(R.string.dialog_negative_button));
    }

    public void validateTimeoutDialog(){
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.timeout_dialog_title));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.timeout_dialog_message));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.dialog_positive_ok_button));
    }

    public void validateTimeoutDialogIsGone(){
        Assert.assertFalse(Validate.isElementInDialogDisplayedByText(R.string.timeout_dialog_title));
        Assert.assertFalse(Validate.isElementInDialogDisplayedByText(R.string.timeout_dialog_message));
        Assert.assertFalse(Validate.isElementInDialogDisplayedByText(R.string.dialog_positive_ok_button));
    }

    public void validateConfirmAttemptDialogIsGone(){
        Assert.assertFalse(Validate.isElementInDialogDisplayedByText(R.string.confirm_dialog_title));
        Assert.assertFalse(Validate.isElementInDialogDisplayedByText(R.string.confirm_attempt_dialog_message));
        Assert.assertFalse(Validate.isElementInDialogDisplayedByText(R.string.dialog_positive_yes_button));
        Assert.assertFalse(Validate.isElementInDialogDisplayedByText(R.string.dialog_negative_button));
    }
}
