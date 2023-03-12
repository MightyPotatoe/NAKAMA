package com.example.nakama.Tests;

import androidx.test.core.app.ActivityScenario;

import com.example.nakama.Activities.MainActivity.MainActivity;
import com.example.nakama.DataBase.Entities.UserScores.UserScore;
import com.example.nakama.Screens.MainActivityScreen;
import com.example.nakama.Screens.SelectRingActivityScreen;
import com.example.nakama.Screens.TimerActivityScreen;
import com.example.nakama.Utils.Dictionary;

import org.junit.Assert;
import org.junit.Test;

public class SelectRingActivityTests {

    /**
     * Given user is on Main Activity
     * And db contain records in UserScores table for selected user
     * And user selects basic level
     * Then confirmation screen is displayed
     * Then after confirming timer is open and time is set to 2 minutes
     */
    @Test
    public void basic_difficulty_timer_should_be_launched_when_ring1_is_selected_and_data_for_this_ring_already_exists() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        Dictionary.DEBUG_MODE = true;
        MainActivityScreen mainActivityScreen = new MainActivityScreen(scenario);
        mainActivityScreen.db.addUserScoreIfNotExists(mainActivityScreen.appPreferences.getUserId(), Dictionary.Difficulty.Basic.NAME, Dictionary.Rings.RING_1);
        mainActivityScreen.db.userScoresDao().updateScores(new UserScore(mainActivityScreen.appPreferences.getUserId(), Dictionary.Difficulty.Basic.NAME, Dictionary.Rings.RING_1, 75, "01:00:00", 1, true, 1,  0, "Ok", null));
        UserScore userScore = mainActivityScreen.db.userScoresDao().getUserScore(mainActivityScreen.appPreferences.getUserId(), Dictionary.Difficulty.Basic.NAME, Dictionary.Rings.RING_1);
        Assert.assertEquals(userScore.attemptTime, "01:00:00");

        SelectRingActivityScreen selectRingActivityScreen = mainActivityScreen.clickStartBasicModeButton();
        selectRingActivityScreen.clickRing1Button();
        selectRingActivityScreen.dismissUserOverride();

        userScore = mainActivityScreen.db.userScoresDao().getUserScore(mainActivityScreen.appPreferences.getUserId(), Dictionary.Difficulty.Basic.NAME, Dictionary.Rings.RING_1);
        Assert.assertEquals("01:00:00", userScore.attemptTime);
        Assert.assertEquals(1, userScore.falseAlarms);
        Assert.assertEquals(1, userScore.treatDrop);
        Assert.assertTrue(userScore.defecation);
        Assert.assertEquals(75, userScore.score);
        Assert.assertEquals("Ok", userScore.overview);


        selectRingActivityScreen.clickRing1Button();
        TimerActivityScreen timerActivityScreen = selectRingActivityScreen.confirmUserOverride();
        Assert.assertEquals("00:05:00", timerActivityScreen.getTimerText());

        userScore = mainActivityScreen.db.userScoresDao().getUserScore(mainActivityScreen.appPreferences.getUserId(), Dictionary.Difficulty.Basic.NAME, Dictionary.Rings.RING_1);
        Assert.assertNull(userScore.attemptTime);
        Assert.assertEquals(0, userScore.falseAlarms);
        Assert.assertEquals(0, userScore.treatDrop);
        Assert.assertFalse(userScore.defecation);
        Assert.assertEquals(200, userScore.score);
        Assert.assertNull(userScore.overview);

        Assert.assertEquals("Poziom Podstawowy",timerActivityScreen.getDifficultyTopBarText());
        Assert.assertEquals("Ring 1",timerActivityScreen.gerRingTopBarText());
        Assert.assertEquals("Tomasz Szymaniak i Nala",timerActivityScreen.getUsernameTopBarText());
        timerActivityScreen.closeScenario();
    }

    /**
     * Given user is on Main Activity
     * And db contain records in UserScores table for selected user
     * And user selects advanced level
     * Then confirmation screen is displayed
     * Then after confirming timer is open and time is set to 4 minutes
     */
    @Test
    public void advanced_difficulty_timer_should_be_launched_when_ring1_is_selected_and_data_for_this_ring_already_exists() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        MainActivityScreen mainActivityScreen = new MainActivityScreen(scenario);
        mainActivityScreen.db.addUserScoreIfNotExists(mainActivityScreen.appPreferences.getUserId(), Dictionary.Difficulty.Advanced.NAME, Dictionary.Rings.RING_1);
        mainActivityScreen.db.userScoresDao().updateScores(new UserScore(mainActivityScreen.appPreferences.getUserId(), Dictionary.Difficulty.Advanced.NAME, Dictionary.Rings.RING_1, 75, "01:00:00", 1, true, 1,  0, "Ok", null));
        UserScore userScore = mainActivityScreen.db.userScoresDao().getUserScore(mainActivityScreen.appPreferences.getUserId(), Dictionary.Difficulty.Advanced.NAME, Dictionary.Rings.RING_1);
        Assert.assertEquals(userScore.attemptTime, "01:00:00");

        SelectRingActivityScreen selectRingActivityScreen = mainActivityScreen.clickStartAdvancedModeButton();
        selectRingActivityScreen.clickRing1Button();
        selectRingActivityScreen.dismissUserOverride();

        userScore = mainActivityScreen.db.userScoresDao().getUserScore(mainActivityScreen.appPreferences.getUserId(), Dictionary.Difficulty.Advanced.NAME, Dictionary.Rings.RING_1);
        Assert.assertEquals("01:00:00", userScore.attemptTime);
        Assert.assertEquals(1, userScore.falseAlarms);
        Assert.assertEquals(1, userScore.treatDrop);
        Assert.assertTrue(userScore.defecation);
        Assert.assertEquals(75, userScore.score);
        Assert.assertEquals("Ok", userScore.overview);

        selectRingActivityScreen.clickRing1Button();
        TimerActivityScreen timerActivityScreen = selectRingActivityScreen.confirmUserOverride();
        Assert.assertEquals("04:00:00", timerActivityScreen.getTimerText());

        userScore = mainActivityScreen.db.userScoresDao().getUserScore(mainActivityScreen.appPreferences.getUserId(), Dictionary.Difficulty.Advanced.NAME, Dictionary.Rings.RING_1);
        Assert.assertNull(userScore.attemptTime);
        Assert.assertEquals(0, userScore.falseAlarms);
        Assert.assertEquals(0, userScore.treatDrop);
        Assert.assertFalse(userScore.defecation);
        Assert.assertEquals(200, userScore.score);
        Assert.assertNull(userScore.overview);

        Assert.assertEquals("Poziom Zaawansowany",timerActivityScreen.getDifficultyTopBarText());
        Assert.assertEquals("Ring 1",timerActivityScreen.gerRingTopBarText());
        Assert.assertEquals("Tomasz Szymaniak i Nala",timerActivityScreen.getUsernameTopBarText());
        timerActivityScreen.closeScenario();
    }


    /**
     * Given user is on Main Activity
     * And db does not contain any records in UserScores table
     * And user selects basic level
     * Then Timer is open and time is set to 2 minutes
     */
    @Test
    public void basic_difficulty_timer_should_be_launched_when_ring_2_selected_and_no_user_score_in_db() {
        MainActivityScreen mainActivityScreen = new MainActivityScreen(ActivityScenario.launch(MainActivity.class));
        mainActivityScreen.db.userScoresDao().delete();

        SelectRingActivityScreen selectRingActivityScreen = mainActivityScreen.clickStartBasicModeButton();
        TimerActivityScreen timerActivityScreen = selectRingActivityScreen.clickRing2Button();
        Assert.assertEquals("02:00:00", timerActivityScreen.getTimerText());
        Assert.assertEquals("Poziom Podstawowy",timerActivityScreen.getDifficultyTopBarText());
        Assert.assertEquals("Ring 2",timerActivityScreen.gerRingTopBarText());
        Assert.assertEquals("Tomasz Szymaniak i Nala",timerActivityScreen.getUsernameTopBarText());
        timerActivityScreen.closeScenario();
    }

    /**
     * Given user is on Main Activity
     * And db does not contain any records in UserScores table
     * And user selects advanced level
     * Then Timer is open and time is set to 4 minutes
     */
    @Test
    public void advanced_difficulty_timer_should_be_launched_when_selected_and_no_user_score_in_db() {
        MainActivityScreen mainActivityScreen = new MainActivityScreen(ActivityScenario.launch(MainActivity.class));
        mainActivityScreen.db.userScoresDao().delete();

        SelectRingActivityScreen selectRingActivityScreen = mainActivityScreen.clickStartAdvancedModeButton();
        TimerActivityScreen timerActivityScreen = selectRingActivityScreen.clickRing2Button();
        Assert.assertEquals("04:00:00", timerActivityScreen.getTimerText());
        Assert.assertEquals("Poziom Zaawansowany",timerActivityScreen.getDifficultyTopBarText());
        Assert.assertEquals("Ring 2",timerActivityScreen.gerRingTopBarText());
        Assert.assertEquals("Tomasz Szymaniak i Nala",timerActivityScreen.getUsernameTopBarText());
        timerActivityScreen.closeScenario();
    }

}
