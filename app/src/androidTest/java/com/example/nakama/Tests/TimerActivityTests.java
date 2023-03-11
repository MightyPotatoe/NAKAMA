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
     * When attempt is failed due to false alarm limits it should go directly to summary and all scores should be set to "failed"
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
    }

    /**
     * When defecation is reported and there was no other accident on other rings - Contestant ends this ring with 0 points and maximum time.
     */
    @Test
    public void defecation_should_increase_counter_and_stop_timer_with_zero_score() throws InterruptedException {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        MainActivityScreen mainActivityScreen = new MainActivityScreen(scenario);
        Users user = new Users("Tomasz", "Szymaniak", "Nala");
        mainActivityScreen.db.addUser(user);
        int userId = mainActivityScreen.db.getUserId(user);
        mainActivityScreen.db.addUserScoreForAllRingsIfNotExists(userId, Dictionary.Difficulty.Advanced.NAME);
        mainActivityScreen.db.clearUserScore(userId, Dictionary.Difficulty.Advanced.NAME, Dictionary.Rings.RING_4);

        mainActivityScreen.clickStartAdvancedModeButton();
        TimerActivityScreen timerActivityScreen = mainActivityScreen.confirmUserOverride();

        timerActivityScreen.clickPlayButton();
        Thread.sleep(1000);
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
    }

    /**
     * When defecation was already reported on other rings - Contestant should be disqualified attemp is over and user is processed directly to sumuary screen for all rings.
     */
    @Test
    public void defecation_should_disqualified_contestant_and_he_should_have_max_time_an_all_rings() throws InterruptedException {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        MainActivityScreen mainActivityScreen = new MainActivityScreen(scenario);
        Users user = new Users("Tomasz", "Szymaniak", "Nala");
        int userId = mainActivityScreen.db.getUserId(user);
        mainActivityScreen.db.addUserScoreForAllRingsIfNotExists(userId, Dictionary.Difficulty.Advanced.NAME);
        mainActivityScreen.db.userScoresDao().updateUserScoresDefecation(userId, Dictionary.Difficulty.Advanced.NAME, Dictionary.Rings.RING_4, true);

        mainActivityScreen.clickStartAdvancedModeButton();
        TimerActivityScreen timerActivityScreen = mainActivityScreen.confirmUserOverride();

        timerActivityScreen.clickPlayButton();
        Thread.sleep(1000);
        timerActivityScreen.clickDefecationButton();
        timerActivityScreen.confirmDefecation();
        RingSummaryActivityScreen ringSummaryActivityScreen = timerActivityScreen.dismissDisqualificationDialog();

        // TODO: 07.03.2023 This shoud finally lead to summary of all 4 rings! 
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
    }

    /**
     * When user press disqualify button - confirmation is prompt. If accepted timer is stopped and user is asked to provide reason.
     * Then this reason should be saved in DB.
     */
    @Test
    public void disqualification_button_test() throws InterruptedException {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        MainActivityScreen mainActivityScreen = new MainActivityScreen(scenario);
        Users user = new Users("Tomasz", "Szymaniak", "Nala");
        int userId = mainActivityScreen.db.getUserId(user);
        mainActivityScreen.db.addUserScoreForAllRingsIfNotExists(userId, Dictionary.Difficulty.Advanced.NAME);

        mainActivityScreen.clickStartAdvancedModeButton();
        TimerActivityScreen timerActivityScreen = mainActivityScreen.confirmUserOverride();

        timerActivityScreen.clickPlayButton();
        Thread.sleep(1000);
        timerActivityScreen.clickDisqualificationButton();
        timerActivityScreen.confirmDisqualification();
        RingSummaryActivityScreen ringSummaryActivityScreen = timerActivityScreen.provideReasonAndDismissDisqualificationDialog("Bo jest dupkiem.");

        // TODO: 07.03.2023 This shoud finally lead to summary of all 4 rings!
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
    }

    // TODO: 08.03.2023 Done button should not need to be confirmed when user is disqualified

}
