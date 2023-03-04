package com.example.nakama;

import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;

import com.example.nakama.Activities.AttemptSummaryActivity.AttemptSummaryActivity;
import com.example.nakama.Activities.TimerActivity.TimerActivity;
import com.example.nakama.SharedPreferences.AppPreferences;
import com.example.nakama.Utils.Action;
import com.example.nakama.Utils.Converter;
import com.example.nakama.Utils.Validate;

import org.junit.Assert;
import org.junit.Test;

public class AttemptSummaryActivityTests {

    public ActivityScenario<TimerActivity> setTimerActivityScenario(){
        ActivityScenario<TimerActivity> scenario = ActivityScenario.launch(TimerActivity.class);
        scenario.onActivity(activity -> {
            AppPreferences appPreferences = new AppPreferences(activity.getSharedPreferences(AppPreferences.NAME, Context.MODE_PRIVATE));
            appPreferences.setPollingFrequency(500);
            appPreferences.setRingTime(10000);
        });
        scenario.recreate();
        return scenario;
    }

    /**
     * When user starts timer (value 10000)
     * And user pauses timer after 2 sec
     * And user clicks done button
     * Then dialog with confirmation is displayed
     * When user confirm choice
     * Then summary screen is loaded with values
     *  | Zdobyte punkty | 200 pkt. |
     * And pozostały czas + czas użytkownika = czas timera (10000)
     */
    @Test
    public void max_score_should_be_obtained_when_timer_is_not_exceed() throws InterruptedException {
        ActivityScenario<TimerActivity> scenario = setTimerActivityScenario();
        Action.clickById(R.id.playButton);
        Thread.sleep(2000);
        Action.clickById(R.id.pauseButton);
        int timeLeftValue = Converter.stringToMillis(Action.getText(withId(R.id.timeTextView)));

        Action.clickById(R.id.doneButton);
        Action.clickByText(R.string.dialog_positive_yes_button);

        int attemptTime = Converter.stringToMillis(Action.getText(withId(R.id.summaryTimeValue)));
        Assert.assertEquals(10000, timeLeftValue + attemptTime);
        Assert.assertEquals("200 pkt.", Action.getText(withId(R.id.summaryPointsValue)));
        scenario.close();
    }

    /**
     * When user starts timer (value 10000)
     * And user waits until timer is finished
     * And user clicks done button
     * Then dialog with confirmation is displayed
     * When user confirm choice
     * Then summary screen is loaded with values
     *  | Zdobyte punkty | 0 pkt.   |
     *  | Czas           | 00:10:00 |
     * And pozostały czas
     */
    @Test
    public void zero_score_should_be_obtained_when_timer_is_exceed() throws InterruptedException {
        ActivityScenario<TimerActivity> scenario = setTimerActivityScenario();
        Action.clickById(R.id.playButton);
        Thread.sleep(10000);

        Action.clickByText(R.string.dialog_positive_ok_button);
        Action.clickById(R.id.doneButton);
        Action.clickByText(R.string.dialog_positive_yes_button);

        Assert.assertEquals("00:10:00", Action.getText(withId(R.id.summaryTimeValue)));
        Assert.assertEquals("0 pkt.", Action.getText(withId(R.id.summaryPointsValue)));
        scenario.close();
    }

    /**
     * Given user is on Summary Activity
     * When user clicks 'nastepny uczestnik'
     * Then confirm dialog is displayed
     * When user confirms dialog
     * Then TimerActivity is displayed
     */
    @Test
    public void clicking_next_butto_should_start_new_timer() {
        ActivityScenario<AttemptSummaryActivity> scenario = ActivityScenario.launch(AttemptSummaryActivity.class);
        Action.clickById(R.id.difficultySelectionCardButton);
        Assert.assertTrue(Validate.isElementDisplayedByText(R.string.confirm_new_attempt_dialog_message));
        Action.clickByText(R.string.dialog_positive_yes_button);
        Assert.assertTrue(Validate.isElementDisplayedById(R.id.timeProgressBar));
        scenario.close();
    }

    /**
     * Given user is on Summary Activity
     * When user clicks 'nastepny uczestnik'
     * Then confirm dialog is displayed
     * When user confirms dialog
     * Then TimerActivity is displayed
     */
    @Test
    public void clicking_cancel_on_confirm_new_attempt() {
        ActivityScenario<AttemptSummaryActivity> scenario = ActivityScenario.launch(AttemptSummaryActivity.class);
        Action.clickById(R.id.difficultySelectionCardButton);
        Assert.assertTrue(Validate.isElementDisplayedByText(R.string.confirm_new_attempt_dialog_message));
        Action.clickByText(R.string.dialog_negative_button);
        Assert.assertTrue(Validate.isElementDisplayedById(R.id.summaryPointsValue));
        scenario.close();
    }
}
