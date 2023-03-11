package com.example.nakama.Tests;

import androidx.test.core.app.ActivityScenario;

import com.example.nakama.Activities.MainActivity.MainActivity;
import com.example.nakama.DataBase.Entities.UserScores.UserScore;
import com.example.nakama.DataBase.Entities.Users.Users;
import com.example.nakama.Screens.MainActivityScreen;
import com.example.nakama.Screens.TimerActivityScreen;
import com.example.nakama.Utils.Dictionary;

import org.junit.Assert;
import org.junit.Test;

public class MainActivityTests {

    /**
     * Given user is on Main Activity
     * And db does not contain any records in UserScores table
     * And user selects basic level
     * Then Timer is open and time is set to 2 minutes
     */
    @Test
    public void basic_difficulty_timer_should_be_launched_when_selected_and_no_user_score_in_db() {
        MainActivityScreen mainActivityScreen = new MainActivityScreen(ActivityScenario.launch(MainActivity.class));
        mainActivityScreen.db.userScoresDao().delete();

        TimerActivityScreen timerActivityScreen = mainActivityScreen.clickStartBasicModeButton();
        Assert.assertEquals("02:00:00", timerActivityScreen.getTimerText());
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

        TimerActivityScreen timerActivityScreen = mainActivityScreen.clickStartAdvancedModeButton();
        Assert.assertEquals("04:00:00", timerActivityScreen.getTimerText());
    }

    /**
     * Given user is on Main Activity
     * And db contain records in UserScores table for selected user
     * And user selects basic level
     * Then confirmation screen is displayed
     * Then after confirming timer is open and time is set to 4 minutes
     */
    @Test
    public void basic_difficulty_timer_should_be_launched_when_selected() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        MainActivityScreen mainActivityScreen = new MainActivityScreen(scenario);
        Users user = new Users("Tomasz", "Szymaniak", "Nala");
        int userId = mainActivityScreen.db.getUserId(user);
        mainActivityScreen.db.addUserScoreIfNotExists(userId, Dictionary.Difficulty.Basic.NAME, Dictionary.Rings.RING_1);
        mainActivityScreen.db.userScoresDao().updateScores(new UserScore(userId, Dictionary.Difficulty.Basic.NAME, Dictionary.Rings.RING_1, 75, "01:00:00", 1, true, 1,  0, "Ok", null));
        UserScore userScore = mainActivityScreen.db.userScoresDao().getUserScore(userId, Dictionary.Difficulty.Basic.NAME, Dictionary.Rings.RING_1);
        Assert.assertEquals(userScore.attemptTime, "01:00:00");

        mainActivityScreen.clickStartBasicModeButton();
        mainActivityScreen.dismissUserOverride();

        userScore = mainActivityScreen.db.userScoresDao().getUserScore(userId, Dictionary.Difficulty.Basic.NAME, Dictionary.Rings.RING_1);
        Assert.assertEquals("01:00:00", userScore.attemptTime);
        Assert.assertEquals(1, userScore.falseAlarms);
        Assert.assertEquals(1, userScore.treatDrop);
        Assert.assertTrue(userScore.defecation);
        Assert.assertEquals(75, userScore.score);
        Assert.assertEquals("Ok", userScore.overview);

        mainActivityScreen.clickStartBasicModeButton();
        TimerActivityScreen timerActivityScreen = mainActivityScreen.confirmUserOverride();
        Assert.assertEquals("02:00:00", timerActivityScreen.getTimerText());

        userScore = mainActivityScreen.db.userScoresDao().getUserScore(userId, Dictionary.Difficulty.Basic.NAME, Dictionary.Rings.RING_1);
        Assert.assertNull(userScore.attemptTime);
        Assert.assertEquals(0, userScore.falseAlarms);
        Assert.assertEquals(0, userScore.treatDrop);
        Assert.assertFalse(userScore.defecation);
        Assert.assertEquals(200, userScore.score);
        Assert.assertNull(userScore.overview);

        Assert.assertEquals("Poziom Podstawowy",timerActivityScreen.getDifficultyTopBarText());
        Assert.assertEquals("Ring 1",timerActivityScreen.gerRingTopBarText());
        Assert.assertEquals("Tomasz Szymaniak i Nala",timerActivityScreen.getUsernameTopBarText());
    }

    /**
     * Given user is on Main Activity
     * And db contain records in UserScores table for selected user
     * And user selects advanced level
     * Then confirmation screen is displayed
     * Then after confirming timer is open and time is set to 4 minutes
     */
    @Test
    public void advanced_difficulty_timer_should_be_launched_when_selected() {
        MainActivityScreen mainActivityScreen = new MainActivityScreen(ActivityScenario.launch(MainActivity.class));
        Users user = new Users("Tomasz", "Szymaniak", "Nala");
        int userId = mainActivityScreen.db.getUserId(user);
        mainActivityScreen.db.addUserScoreIfNotExists(userId, Dictionary.Difficulty.Advanced.NAME, Dictionary.Rings.RING_1);
        mainActivityScreen.db.userScoresDao().updateScores(new UserScore(userId, Dictionary.Difficulty.Advanced.NAME, Dictionary.Rings.RING_1, 75, "01:00:00", 1, true, 1,  0, "Ok", null));
        UserScore userScore = mainActivityScreen.db.userScoresDao().getUserScore(userId, Dictionary.Difficulty.Advanced.NAME, Dictionary.Rings.RING_1);
        Assert.assertEquals(userScore.attemptTime, "01:00:00");

        mainActivityScreen.clickStartAdvancedModeButton();
        mainActivityScreen.dismissUserOverride();

        userScore = mainActivityScreen.db.userScoresDao().getUserScore(userId, Dictionary.Difficulty.Advanced.NAME, Dictionary.Rings.RING_1);
        Assert.assertEquals("01:00:00", userScore.attemptTime);
        Assert.assertEquals(1, userScore.falseAlarms);
        Assert.assertEquals(1, userScore.treatDrop);
        Assert.assertTrue(userScore.defecation);
        Assert.assertEquals(75, userScore.score);
        Assert.assertEquals("Ok", userScore.overview);

        mainActivityScreen.clickStartAdvancedModeButton();
        TimerActivityScreen timerActivityScreen = mainActivityScreen.confirmUserOverride();
        Assert.assertEquals("04:00:00", timerActivityScreen.getTimerText());

        userScore = mainActivityScreen.db.userScoresDao().getUserScore(userId, Dictionary.Difficulty.Advanced.NAME, Dictionary.Rings.RING_1);
        Assert.assertNull(userScore.attemptTime);
        Assert.assertEquals(0, userScore.falseAlarms);
        Assert.assertEquals(0, userScore.treatDrop);
        Assert.assertFalse(userScore.defecation);
        Assert.assertEquals(200, userScore.score);
        Assert.assertNull(userScore.overview);

        Assert.assertEquals("Poziom Zaawansowany",timerActivityScreen.getDifficultyTopBarText());
        Assert.assertEquals("Ring 1",timerActivityScreen.gerRingTopBarText());
        Assert.assertEquals("Tomasz Szymaniak i Nala",timerActivityScreen.getUsernameTopBarText());
    }
}