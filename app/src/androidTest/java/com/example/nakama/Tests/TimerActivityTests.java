package com.example.nakama.Tests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.hamcrest.core.IsNot.not;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.nakama.Activities.MainActivity.MainActivity;
import com.example.nakama.DataBase.Entities.UserScores.UserScore;
import com.example.nakama.DataBase.Entities.Users.Users;
import com.example.nakama.Screens.MainActivityScreen;
import com.example.nakama.Screens.RingSummaryActivityScreen;
import com.example.nakama.Screens.SelectRingActivityScreen;
import com.example.nakama.Screens.TimerActivityScreen;
import com.example.nakama.Utils.Converter;
import com.example.nakama.Utils.Dictionary;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TimerActivityTests {

    /**
     * Check default activity state when entering base mode
     */
    @Test
    public void validate_starting_activity_state(){
        MainActivityScreen mainActivityScreen = new MainActivityScreen(ActivityScenario.launch(MainActivity.class));
        mainActivityScreen.db.userScoresDao().delete();
        SelectRingActivityScreen selectRingActivityScreen = mainActivityScreen.clickStartBasicModeButton();

        TimerActivityScreen timerActivityScreen = selectRingActivityScreen.clickRing1Button();

        timerActivityScreen.validateTimer(120000);
        timerActivityScreen.validateDefaultStateTimerButtons();
        timerActivityScreen.validateIfActionButtonsAreDisabled();
        timerActivityScreen.validateScores(200,0,0,0,0);
        timerActivityScreen.closeScenario();
    }

    /**
     * Check if activity is paused and continued later.
     */
    @Test
    public void timer_activity_should_pause_and_continue() throws InterruptedException {
        MainActivityScreen mainActivityScreen = new MainActivityScreen(ActivityScenario.launch(MainActivity.class));
        SelectRingActivityScreen selectRingActivityScreen = mainActivityScreen.clickStartBasicModeButton();
        selectRingActivityScreen.clickRing1Button();
        TimerActivityScreen timerActivityScreen = selectRingActivityScreen.confirmUserOverride();

        String beginningTime = timerActivityScreen.getTimerText();
        Assert.assertEquals("02:00:00", beginningTime);

        timerActivityScreen.clickPlayButton();
        Assert.assertNotEquals(beginningTime, timerActivityScreen.getTimerText());
        timerActivityScreen.validateIfActionButtonsAreEnabled();
        timerActivityScreen.validateInProgressStateTimerButtons();

        timerActivityScreen.clickPauseButton();
        timerActivityScreen.validateIfActionButtonsAreEnabled();
        timerActivityScreen.validateInPausedStateTimerButtons();
        String currentTime = timerActivityScreen.getTimerText();
        Assert.assertEquals(currentTime, timerActivityScreen.getTimerText());

        timerActivityScreen.clickPlayButton();
        Thread.sleep(1000);
        Assert.assertNotEquals(currentTime, timerActivityScreen.getTimerText());
        timerActivityScreen.validateIfActionButtonsAreEnabled();
        timerActivityScreen.validateInProgressStateTimerButtons();
        timerActivityScreen.closeScenario();
    }

    /**
     * When timer is paused or finished pressing reset button should reset activity to default state.
     * Covers: On Finish state
     */
    @Test
    public void timer_should_be_reset_when_reset_button_is_pressed_and_choice_is_confirmed() throws InterruptedException {
        MainActivityScreen mainActivityScreen = new MainActivityScreen(ActivityScenario.launch(MainActivity.class));
        Dictionary.DEBUG_MODE = true;
        SelectRingActivityScreen selectRingActivityScreen = mainActivityScreen.clickStartAdvancedModeButton();
        selectRingActivityScreen.clickRing1Button();
        TimerActivityScreen timerActivityScreen = selectRingActivityScreen.confirmUserOverride();

        String beginningTime = timerActivityScreen.getTimerText();
        timerActivityScreen.validateTimer(7000);

        timerActivityScreen.clickPlayButton();
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
        timerActivityScreen.validateTimer(7000);

        //Click play and wait for timer to be finished
        timerActivityScreen.clickPlayButton();
        timerActivityScreen.clickFalseAlarm();
        timerActivityScreen.confirmFalseAlarm();
        timerActivityScreen.clickPositiveAlarm();
        timerActivityScreen.confirmPositiveAlarm();
        timerActivityScreen.clickTreatDroppedButton();
        timerActivityScreen.confirmTreatDropped();
        timerActivityScreen.validateScores(120, 1, 0, 1, 1);
        Thread.sleep(7000);
        timerActivityScreen.dismissTimeoutDialog();
        timerActivityScreen.validateTimer(0);
        timerActivityScreen.validateFinishedButtons();
        //Reset
        timerActivityScreen.clickResetButtonButton();
        timerActivityScreen.confirmReset();
        timerActivityScreen.validateScores(200, 0, 0, 0, 0);
        timerActivityScreen.validateDefaultStateTimerButtons();
        timerActivityScreen.validateIfActionButtonsAreDisabled();
        timerActivityScreen.validateTimer(7000);
        timerActivityScreen.closeScenario();
    }

    /**
     * When timer is paused and pressing reset button is pressed and confirmation is canceled timer should not be reseted.
     */
    @Test
    public void reset_dialog_should_be_dismissed_when_no_is_clicked_and_timer_should_not_be_reset() throws InterruptedException {
        MainActivityScreen mainActivityScreen = new MainActivityScreen(ActivityScenario.launch(MainActivity.class));
        SelectRingActivityScreen selectRingActivityScreen = mainActivityScreen.clickStartAdvancedModeButton();
        selectRingActivityScreen.clickRing1Button();
        TimerActivityScreen timerActivityScreen = selectRingActivityScreen.confirmUserOverride();

        String beginningTime = timerActivityScreen.getTimerText();
        timerActivityScreen.validateTimer(240000);

        timerActivityScreen.clickPlayButton();
        Thread.sleep(500);
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
        timerActivityScreen.closeScenario();
    }


    /**
     * False alarms counter should be incremented. When limit is reached it timer and score should be set to zero.
     * 50pts should be subtract on each false alarm
     * When attempt is failed due to false alarm limits it should go directly to summary and all scores should be set to "failed"
     */
    @Test
    public void false_alarms_button_should_increase_counter_and_stop_when_limit_reached(){
        MainActivityScreen mainActivityScreen = new MainActivityScreen(ActivityScenario.launch(MainActivity.class));
        SelectRingActivityScreen selectRingActivityScreen = mainActivityScreen.clickStartAdvancedModeButton();
        selectRingActivityScreen.clickRing1Button();
        TimerActivityScreen timerActivityScreen = selectRingActivityScreen.confirmUserOverride();

        timerActivityScreen.clickPlayButton();
        timerActivityScreen.clickFalseAlarm();
        timerActivityScreen.confirmFalseAlarm();
        timerActivityScreen.validateScores(150, 1, 0, 0, 0);

        timerActivityScreen.clickFalseAlarm();
        timerActivityScreen.confirmFalseAlarm();
        timerActivityScreen.validateScores(100, 2, 0, 0, 0);

        timerActivityScreen.clickFalseAlarm();
        timerActivityScreen.confirmFalseAlarm();
        RingSummaryActivityScreen ringSummaryActivityScreen = timerActivityScreen.dismissFalseAlarmLimitReachedDialog();

        Assert.assertEquals("0 pkt.", ringSummaryActivityScreen.getSummaryPoints());
        ringSummaryActivityScreen.validateTimerResult(240000, "00:00:00");
        Assert.assertEquals("0/2", ringSummaryActivityScreen.getSamplesFound());
        Assert.assertEquals("3", ringSummaryActivityScreen.getFalseAlarmsCounter());
        Assert.assertEquals("Niezaliczone", ringSummaryActivityScreen.getFalseAlarmsScoreImpact());
        Assert.assertEquals("Niezaliczone", ringSummaryActivityScreen.getDefecationScoreImpact());
        Assert.assertEquals("Niezaliczone", ringSummaryActivityScreen.getTreatDroppedScoreImpact());

        onView(ringSummaryActivityScreen.getImpressionsLabelElement()).check(matches(not(isDisplayed())));
        onView(ringSummaryActivityScreen.getDisqualificationReasonLabelElement()).check(matches(not(isDisplayed())));
        onView(ringSummaryActivityScreen.getDisqualificationReasonElement()).check(matches(not(isDisplayed())));
        ringSummaryActivityScreen.closeScenario();
    }

    /**
     * Samples Found counter should be incremented. When all samples are found timer should be stopped.
     */
    @Test
    public void positive_alarms_button_should_increase_counter_and_stop_when_limit_reached(){
        MainActivityScreen mainActivityScreen = new MainActivityScreen(ActivityScenario.launch(MainActivity.class));
        SelectRingActivityScreen selectRingActivityScreen = mainActivityScreen.clickStartAdvancedModeButton();
        selectRingActivityScreen.clickRing1Button();
        TimerActivityScreen timerActivityScreen = selectRingActivityScreen.confirmUserOverride();

        timerActivityScreen.clickPlayButton();
        timerActivityScreen.clickPositiveAlarm();
        timerActivityScreen.confirmPositiveAlarm();
        timerActivityScreen.validateScores(200, 0, 0, 0, 1);

        timerActivityScreen.clickPositiveAlarm();
        timerActivityScreen.confirmPositiveAlarm();
        timerActivityScreen.dismissAllSamplesFoundDialog();
        timerActivityScreen.validateScores(200, 0, 0, 0, 2);
        String currentTime = timerActivityScreen.getTimerText();
        Assert.assertNotEquals("04:00:00", currentTime);
        String timeAfterWait = timerActivityScreen.getTimerText();
        Assert.assertEquals(timeAfterWait, currentTime);
        timerActivityScreen.closeScenario();
    }

    /**
     * Treat dropped counter should be incremented. When limit is reached timer should be set to 0 and 0 score should be applied. - Basic difficulty
     */
    @Test
    public void treat_dropped_button_should_increase_counter_and_stop_when_limit_reached_with_zero_score_basic(){
        MainActivityScreen mainActivityScreen = new MainActivityScreen(ActivityScenario.launch(MainActivity.class));
        SelectRingActivityScreen selectRingActivityScreen = mainActivityScreen.clickStartBasicModeButton();
        selectRingActivityScreen.clickRing1Button();
        TimerActivityScreen timerActivityScreen = selectRingActivityScreen.confirmUserOverride();

        timerActivityScreen.clickPlayButton();
        timerActivityScreen.clickTreatDroppedButton();
        timerActivityScreen.confirmTreatDropped();
        timerActivityScreen.validateScores(170, 0, 0, 1, 0);

        timerActivityScreen.clickTreatDroppedButton();
        timerActivityScreen.confirmTreatDropped();
        RingSummaryActivityScreen ringSummaryActivityScreen = timerActivityScreen.dismissTreatDroppedLimitReachedDialog();

        Assert.assertEquals("0 pkt.", ringSummaryActivityScreen.getSummaryPoints());
        ringSummaryActivityScreen.validateTimerResult(120000, "00:00:00");
        Assert.assertEquals("0/1", ringSummaryActivityScreen.getSamplesFound());
        Assert.assertEquals("0", ringSummaryActivityScreen.getFalseAlarmsCounter());
        Assert.assertEquals("0", ringSummaryActivityScreen.getDefecationCounter());
        Assert.assertEquals("2", ringSummaryActivityScreen.getTreatDroppedCounter());

        Assert.assertEquals("Niezaliczone", ringSummaryActivityScreen.getFalseAlarmsScoreImpact());
        Assert.assertEquals("Niezaliczone", ringSummaryActivityScreen.getDefecationScoreImpact());
        Assert.assertEquals("Niezaliczone", ringSummaryActivityScreen.getTreatDroppedScoreImpact());

        onView(ringSummaryActivityScreen.getImpressionsLabelElement()).check(matches(not(isDisplayed())));
        onView(ringSummaryActivityScreen.getDisqualificationReasonLabelElement()).check(matches(not(isDisplayed())));
        onView(ringSummaryActivityScreen.getDisqualificationReasonElement()).check(matches(not(isDisplayed())));
        ringSummaryActivityScreen.closeScenario();
    }

    /**
     * Treat dropped counter should be incremented. When limit is reached timer should be set to 0 and 0 score should be applied. - Advanced difficulty
     */
    @Test
    public void treat_dropped_button_should_increase_counter_and_stop_when_limit_reached_with_zero_score_advanced(){
        MainActivityScreen mainActivityScreen = new MainActivityScreen(ActivityScenario.launch(MainActivity.class));
        SelectRingActivityScreen selectRingActivityScreen = mainActivityScreen.clickStartAdvancedModeButton();
        selectRingActivityScreen.clickRing1Button();
        TimerActivityScreen timerActivityScreen = selectRingActivityScreen.confirmUserOverride();

        timerActivityScreen.clickPlayButton();

        timerActivityScreen.clickTreatDroppedButton();
        timerActivityScreen.confirmTreatDropped();
        timerActivityScreen.validateScores(170, 0, 0, 1, 0);

        timerActivityScreen.clickTreatDroppedButton();
        timerActivityScreen.confirmTreatDropped();
        RingSummaryActivityScreen ringSummaryActivityScreen = timerActivityScreen.dismissTreatDroppedLimitReachedDialog();

        Assert.assertEquals("0 pkt.", ringSummaryActivityScreen.getSummaryPoints());
        ringSummaryActivityScreen.validateTimerResult(240000, "00:00:00");
        Assert.assertEquals("0/2", ringSummaryActivityScreen.getSamplesFound());
        Assert.assertEquals("0", ringSummaryActivityScreen.getFalseAlarmsCounter());
        Assert.assertEquals("0", ringSummaryActivityScreen.getDefecationCounter());
        Assert.assertEquals("2", ringSummaryActivityScreen.getTreatDroppedCounter());

        Assert.assertEquals("Niezaliczone", ringSummaryActivityScreen.getFalseAlarmsScoreImpact());
        Assert.assertEquals("Niezaliczone", ringSummaryActivityScreen.getDefecationScoreImpact());
        Assert.assertEquals("Niezaliczone", ringSummaryActivityScreen.getTreatDroppedScoreImpact());

        onView(ringSummaryActivityScreen.getImpressionsLabelElement()).check(matches(not(isDisplayed())));
        onView(ringSummaryActivityScreen.getDisqualificationReasonLabelElement()).check(matches(not(isDisplayed())));
        onView(ringSummaryActivityScreen.getDisqualificationReasonElement()).check(matches(not(isDisplayed())));
        ringSummaryActivityScreen.closeScenario();
    }

    /**
     * When defecation is reported and there was no other accident on other rings - Contestant ends this ring with 0 points and maximum time.
     */
    @Test
    public void defecation_should_increase_counter_and_stop_timer_with_zero_score(){
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        MainActivityScreen mainActivityScreen = new MainActivityScreen(scenario);
        Users user = new Users("Tomasz", "Szymaniak", "Nala");
        mainActivityScreen.db.addUser(user);
        int userId = mainActivityScreen.db.getUserId(user);
        mainActivityScreen.db.addUserScoreForAllRingsIfNotExists(userId, Dictionary.Difficulty.Advanced.NAME);
        mainActivityScreen.db.clearUserScore(userId, Dictionary.Difficulty.Advanced.NAME, Dictionary.Rings.RING_4);

        SelectRingActivityScreen selectRingActivityScreen = mainActivityScreen.clickStartAdvancedModeButton();
        selectRingActivityScreen.clickRing1Button();
        TimerActivityScreen timerActivityScreen = selectRingActivityScreen.confirmUserOverride();

        timerActivityScreen.clickPlayButton();
        timerActivityScreen.clickDefecationButton();
        RingSummaryActivityScreen ringSummaryActivityScreen = timerActivityScreen.confirmDefecation();

        Assert.assertEquals("0 pkt.", ringSummaryActivityScreen.getSummaryPoints());
        ringSummaryActivityScreen.validateTimerResult(240000, "00:00:00");
        Assert.assertEquals("0/2", ringSummaryActivityScreen.getSamplesFound());
        Assert.assertEquals("0", ringSummaryActivityScreen.getFalseAlarmsCounter());
        Assert.assertEquals("1", ringSummaryActivityScreen.getDefecationCounter());
        Assert.assertEquals("Niezaliczone", ringSummaryActivityScreen.getFalseAlarmsScoreImpact());
        Assert.assertEquals("Niezaliczone", ringSummaryActivityScreen.getDefecationScoreImpact());
        Assert.assertEquals("Niezaliczone", ringSummaryActivityScreen.getTreatDroppedScoreImpact());

        onView(ringSummaryActivityScreen.getImpressionsLabelElement()).check(matches(not(isDisplayed())));
        onView(ringSummaryActivityScreen.getDisqualificationReasonLabelElement()).check(matches(not(isDisplayed())));
        onView(ringSummaryActivityScreen.getDisqualificationReasonElement()).check(matches(not(isDisplayed())));
        ringSummaryActivityScreen.closeScenario();
    }

    /**
     * When defecation was already reported on other rings - Contestant should be disqualified attemp is over and user is processed directly to sumuary screen for all rings.
     */
    @Test
    public void defecation_should_disqualified_contestant_and_he_should_have_max_time_an_all_rings(){
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        MainActivityScreen mainActivityScreen = new MainActivityScreen(scenario);
        Users user = new Users("Tomasz", "Szymaniak", "Nala");
        int userId = mainActivityScreen.db.getUserId(user);
        mainActivityScreen.db.addUserScoreForAllRingsIfNotExists(userId, Dictionary.Difficulty.Advanced.NAME);
        mainActivityScreen.db.userScoresDao().updateUserScoresDefecation(userId, Dictionary.Difficulty.Advanced.NAME, Dictionary.Rings.RING_4, true);

        SelectRingActivityScreen selectRingActivityScreen = mainActivityScreen.clickStartAdvancedModeButton();
        selectRingActivityScreen.clickRing1Button();
        TimerActivityScreen timerActivityScreen = selectRingActivityScreen.confirmUserOverride();

        timerActivityScreen.clickPlayButton();
        timerActivityScreen.clickDefecationButton();
        timerActivityScreen.confirmDefecation();
        RingSummaryActivityScreen ringSummaryActivityScreen = timerActivityScreen.dismissDisqualificationDialog();

        Assert.assertEquals("0 pkt.", ringSummaryActivityScreen.getSummaryPoints());
        UserScore userScore1 = mainActivityScreen.db.userScoresDao().getUserScore(userId, Dictionary.Difficulty.Advanced.NAME, Dictionary.Rings.RING_1);
        UserScore userScore2 = mainActivityScreen.db.userScoresDao().getUserScore(userId, Dictionary.Difficulty.Advanced.NAME, Dictionary.Rings.RING_2);
        UserScore userScore3 = mainActivityScreen.db.userScoresDao().getUserScore(userId, Dictionary.Difficulty.Advanced.NAME, Dictionary.Rings.RING_3);
        UserScore userScore4 = mainActivityScreen.db.userScoresDao().getUserScore(userId, Dictionary.Difficulty.Advanced.NAME, Dictionary.Rings.RING_4);
        Assert.assertEquals(0, userScore1.score);
        Assert.assertEquals(0, userScore2.score);
        Assert.assertEquals(0, userScore3.score);
        Assert.assertEquals(0, userScore4.score);
        Assert.assertEquals(Converter.millisToString(240000), userScore1.attemptTime);
        Assert.assertEquals(Converter.millisToString(240000), userScore2.attemptTime);
        Assert.assertEquals(Converter.millisToString(240000), userScore3.attemptTime);
        Assert.assertEquals(Converter.millisToString(240000), userScore4.attemptTime);
        Assert.assertEquals("Załatwianie potrzeb fizjologicznych na więcej niż jednym ringu.", userScore1.disqualification_reason);
        mainActivityScreen.db.userScoresDao().delete(userId, Dictionary.Difficulty.Advanced.NAME, Dictionary.Rings.RING_2);
        mainActivityScreen.db.userScoresDao().delete(userId, Dictionary.Difficulty.Advanced.NAME, Dictionary.Rings.RING_3);
        mainActivityScreen.db.userScoresDao().delete(userId, Dictionary.Difficulty.Advanced.NAME, Dictionary.Rings.RING_4);
        mainActivityScreen.closeScenario();
    }

    /**
     * When user press disqualify button - confirmation is prompt. If accepted timer is stopped and user is asked to provide reason.
     * Then this reason should be saved in DB.
     */
    @Test
    public void disqualification_button_test(){
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        MainActivityScreen mainActivityScreen = new MainActivityScreen(scenario);
        Users user = new Users("Tomasz", "Szymaniak", "Nala");
        int userId = mainActivityScreen.db.getUserId(user);
        mainActivityScreen.db.addUserScoreForAllRingsIfNotExists(userId, Dictionary.Difficulty.Advanced.NAME);

        SelectRingActivityScreen selectRingActivityScreen = mainActivityScreen.clickStartAdvancedModeButton();
        selectRingActivityScreen.clickRing1Button();
        TimerActivityScreen timerActivityScreen = selectRingActivityScreen.confirmUserOverride();

        timerActivityScreen.clickPlayButton();
        timerActivityScreen.clickDisqualificationButton();
        timerActivityScreen.confirmDisqualification();
        RingSummaryActivityScreen ringSummaryActivityScreen = timerActivityScreen.provideReasonAndDismissDisqualificationDialog("Bo jest dupkiem.");

        Assert.assertEquals("0 pkt.", ringSummaryActivityScreen.getSummaryPoints());
        UserScore userScore1 = mainActivityScreen.db.userScoresDao().getUserScore(userId, Dictionary.Difficulty.Advanced.NAME, Dictionary.Rings.RING_1);
        UserScore userScore2 = mainActivityScreen.db.userScoresDao().getUserScore(userId, Dictionary.Difficulty.Advanced.NAME, Dictionary.Rings.RING_2);
        UserScore userScore3 = mainActivityScreen.db.userScoresDao().getUserScore(userId, Dictionary.Difficulty.Advanced.NAME, Dictionary.Rings.RING_3);
        UserScore userScore4 = mainActivityScreen.db.userScoresDao().getUserScore(userId, Dictionary.Difficulty.Advanced.NAME, Dictionary.Rings.RING_4);
        Assert.assertEquals(0, userScore1.score);
        Assert.assertEquals(0, userScore2.score);
        Assert.assertEquals(0, userScore3.score);
        Assert.assertEquals(0, userScore4.score);
        Assert.assertEquals(Converter.millisToString(240000), userScore1.attemptTime);
        Assert.assertEquals(Converter.millisToString(240000), userScore2.attemptTime);
        Assert.assertEquals(Converter.millisToString(240000), userScore3.attemptTime);
        Assert.assertEquals(Converter.millisToString(240000), userScore4.attemptTime);
        Assert.assertEquals("Bo jest dupkiem.", userScore1.disqualification_reason);
        ringSummaryActivityScreen.closeScenario();
    }
}
