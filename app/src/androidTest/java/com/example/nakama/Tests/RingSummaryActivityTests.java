package com.example.nakama.Tests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.hamcrest.core.IsNot.not;

import androidx.test.core.app.ActivityScenario;

import com.example.nakama.Activities.AttemptSummaryActivity.RingSummaryActivity;
import com.example.nakama.Activities.MainActivity.MainActivity;
import com.example.nakama.R;
import com.example.nakama.Screens.MainActivityScreen;
import com.example.nakama.Screens.OverallImpressionActivityScreen;
import com.example.nakama.Screens.RingSummaryActivityScreen;
import com.example.nakama.Screens.TimerActivityScreen;
import com.example.nakama.Utils.Action;
import com.example.nakama.Utils.Validate;

import org.junit.Assert;
import org.junit.Test;

public class RingSummaryActivityTests {

    /**
     * Advanced mode (200pkt):
     * - Dropeed treat -30pkt
     * - False alarm - 2x -50pkt
     * - Positive alarm - 2x
     * Overal impressions:
     *  - Szarpanie za smycz - 10pkt
     *  - Uzytkownik zjadł wszystkie kopytka - 5pkt
     *
     * Result:
     *  Score: 55pkt
     *  Time: x
     *  Samples found: 2/2
     *  False alarms: 2 -100pkt
     *  Defecations: 0
     *  Dropped treats: 1
     *  Overall Impression label - displayed
     *  Overall Impression[0] - Szarpanie za smycz - 10pkt
     *  Overall Impression[1] - Uzytkownik zjadł wszystkie kopytka - 5pkt
     *  Disqualification  label and reason - gone
     */
    @Test
    public void score_should_be_displayed_correctly_and_with_all_details() throws InterruptedException {
        MainActivityScreen mainActivityScreen = new MainActivityScreen(ActivityScenario.launch(MainActivity.class));
        mainActivityScreen.clickStartAdvancedModeButton();
        TimerActivityScreen timerActivityScreen = mainActivityScreen.confirmUserOverride();

        timerActivityScreen.clickPlayButton();
        Thread.sleep(1000);
        timerActivityScreen.clickTreatDroppedButton();
        timerActivityScreen.confirmTreatDropped();
        Thread.sleep(1000);
        timerActivityScreen.clickFalseAlarm();
        timerActivityScreen.confirmFalseAlarm();
        Thread.sleep(1000);
        timerActivityScreen.clickPositiveAlarm();
        timerActivityScreen.confirmPositiveAlarm();
        Thread.sleep(1000);
        timerActivityScreen.clickFalseAlarm();
        timerActivityScreen.confirmFalseAlarm();
        Thread.sleep(1000);
        timerActivityScreen.clickPositiveAlarm();
        timerActivityScreen.confirmPositiveAlarm();
        timerActivityScreen.dismissAllSamplesFoundDialog();
        String timeLeftAfterAttempt = timerActivityScreen.getTimerText();
        timerActivityScreen.clickDoneButton();
        OverallImpressionActivityScreen overallImpressionActivityScreen = timerActivityScreen.confirmFinish();

        overallImpressionActivityScreen.setDescription(0,"Szarpanie za smycz");
        overallImpressionActivityScreen.setScore(0,"10");
        overallImpressionActivityScreen.clickAddButton();
        overallImpressionActivityScreen.setDescription(1,"Uzytkownik zjadl wszystkie kopytka");
        overallImpressionActivityScreen.setScore(1,"5");
        RingSummaryActivityScreen ringSummaryActivityScreen = overallImpressionActivityScreen.clickSaveButton();

        Assert.assertEquals("55 pkt.", ringSummaryActivityScreen.getSummaryPoints());
        ringSummaryActivityScreen.validateTimerResult(240000, timeLeftAfterAttempt);
        Assert.assertEquals("2/2", ringSummaryActivityScreen.getSamplesFound());
        Assert.assertEquals("2", ringSummaryActivityScreen.getFalseAlarmsCounter());
        Assert.assertEquals("-100 pkt.", ringSummaryActivityScreen.getFalseAlarmsScoreImpact());
        Assert.assertEquals("0", ringSummaryActivityScreen.getDefecationCounter());
        Assert.assertEquals("-", ringSummaryActivityScreen.getDefecationScoreImpact());
        Assert.assertEquals("1", ringSummaryActivityScreen.getTreatDroppedCounter());
        Assert.assertEquals("-30 pkt.", ringSummaryActivityScreen.getTreatDroppedScoreImpact());

        Assert.assertEquals("Uzytkownik zjadl wszystkie kopytka",ringSummaryActivityScreen.getDescriptionAtPosition(0));
        Assert.assertEquals("-5 pkt.",ringSummaryActivityScreen.getDescriptionScoreAtPosition(0));

        Assert.assertEquals("Szarpanie za smycz",ringSummaryActivityScreen.getDescriptionAtPosition(1));
        Assert.assertEquals("-10 pkt.",ringSummaryActivityScreen.getDescriptionScoreAtPosition(1));

        onView(ringSummaryActivityScreen.getDisqualificationReasonLabelElement()).check(matches(not(isDisplayed())));
        onView(ringSummaryActivityScreen.getDisqualificationReasonElement()).check(matches(not(isDisplayed())));
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
        TimerActivityScreen timerActivityScreen = new TimerActivityScreen(2000, 500);
        timerActivityScreen.clickPlayButton();
        Thread.sleep(500);
        timerActivityScreen.clickPauseButton();
        timerActivityScreen.clickDoneButton();
        OverallImpressionActivityScreen overallImpressionActivityScreen = timerActivityScreen.confirmFinish();

        RingSummaryActivityScreen ringSummaryActivityScreen = overallImpressionActivityScreen.clickSkipButton();
        Assert.assertEquals("200 pkt.", ringSummaryActivityScreen.getSummaryPoints());
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
        TimerActivityScreen timerActivityScreen = new TimerActivityScreen(2000, 500);
        timerActivityScreen.clickPlayButton();
        Thread.sleep(2500);

        timerActivityScreen.validateTimeoutDialog();
        timerActivityScreen.dismissTimeoutDialog();
        timerActivityScreen.clickDoneButton();
        OverallImpressionActivityScreen overallImpressionActivityScreen = timerActivityScreen.confirmFinish();

        RingSummaryActivityScreen ringSummaryActivityScreen = overallImpressionActivityScreen.clickSkipButton();
        Assert.assertEquals("00:02:00", ringSummaryActivityScreen.getSummaryTime());
        Assert.assertEquals("0 pkt.", ringSummaryActivityScreen.getSummaryPoints());
    }

    /**
     * Given user is on Summary Activity
     * When user clicks 'nastepny uczestnik'
     * Then confirm dialog is displayed
     * When user confirms dialog
     * Then TimerActivity is displayed
     */
    @Test
    public void clicking_next_button_should_start_new_timer() {
        ActivityScenario<RingSummaryActivity> scenario = ActivityScenario.launch(RingSummaryActivity.class);
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
        ActivityScenario<RingSummaryActivity> scenario = ActivityScenario.launch(RingSummaryActivity.class);
        Action.clickById(R.id.difficultySelectionCardButton);
        Assert.assertTrue(Validate.isElementDisplayedByText(R.string.confirm_new_attempt_dialog_message));
        Action.clickByText(R.string.dialog_negative_button);
        Assert.assertTrue(Validate.isElementDisplayedById(R.id.summaryPointsValue));
        scenario.close();
    }
}
