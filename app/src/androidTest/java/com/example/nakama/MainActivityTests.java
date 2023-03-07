package com.example.nakama;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;

import android.app.Activity;

import androidx.test.core.app.ActivityScenario;

import com.example.nakama.Activities.MainActivity.MainActivity;
import com.example.nakama.DataBase.AppDatabase;
import com.example.nakama.DataBase.Entities.UserScores.UserScore;
import com.example.nakama.DataBase.Entities.Users.Users;
import com.example.nakama.Screens.MainActivityScreen;
import com.example.nakama.Screens.TimerActivityScreen;
import com.example.nakama.SharedPreferences.AppPreferences;
import com.example.nakama.Utils.Action;
import com.example.nakama.Utils.Dictionary;
import com.example.nakama.Utils.Validate;

import org.junit.Assert;
import org.junit.Test;

public class MainActivityTests {

    AppDatabase db;
    AppPreferences appPreferences;

    /**
     * Given user is on Main Activity
     * And db does not contain any records in UserScores table
     * And user selects basic level
     * Then Timer is open and time is set to 2 minutes
     */
    @Test
    public void basic_difficulty_timer_should_be_launched_when_selected_and_no_user_score_in_db() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        MainActivityScreen mainActivityScreen = new MainActivityScreen(scenario);
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
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        MainActivityScreen mainActivityScreen = new MainActivityScreen(scenario);
        mainActivityScreen.db.userScoresDao().delete();

        TimerActivityScreen timerActivityScreen = mainActivityScreen.clickStartAdvancedModeButton();
        Assert.assertEquals("04:00:00", timerActivityScreen.getTimerText());
        scenario.close();
    }

    /**
     * Given user is on Main Activity
     * And db contains record in UserScores table
     * | UserId = Tomasz Szymaniak Nala | Difficulty = BASIC | Ring = RING 1 | Time = 01:00:00 |
     * And user selects basic level
     * Then confirm resetting attempt dialog should be displayed
     * When user press no
     * Then user is still on MainActivity
     * And time is not changed for this attempt
     * And user selects basic level
     * Then confirm resetting attempt dialog should be displayed
     * When user press yes
     * Then Timer is open and time is set to 2 minutes and time is null in db
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

        userScore = db.userScoresDao().getUserScore(userId, Dictionary.Difficulty.Basic.NAME, Dictionary.Rings.RING_1);
        Assert.assertEquals("01:00:00", userScore.attemptTime);
        Assert.assertEquals(1, userScore.falseAlarms);
        Assert.assertEquals(1, userScore.treatDrop);
        Assert.assertTrue(userScore.defecation);
        Assert.assertEquals(75, userScore.score);
        Assert.assertEquals("Ok", userScore.overview);

        mainActivityScreen.clickStartBasicModeButton();
        TimerActivityScreen timerActivityScreen = mainActivityScreen.confirmUserOverride();
        Assert.assertEquals("02:00:00", timerActivityScreen.getTimerText());

        userScore = db.userScoresDao().getUserScore(userId, Dictionary.Difficulty.Basic.NAME, Dictionary.Rings.RING_1);
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
     * And db contains record in UserScores table
     * | UserId = Tomasz Szymaniak Nala | Difficulty = BASIC | Ring = RING 1 | Time = 01:00:00 |
     * And user selects advanced level
     * Then confirm resetting attempt dialog should be displayed
     * When user press no
     * Then user is still on MainActivity
     * And time is not changed for this attempt
     * And user selects advanced level
     * Then confirm resetting attempt dialog should be displayed
     * When user press yes
     * Then Timer is open and time is set to 2 minutes and time is null in db
     */
    @Test
    public void advanced_difficulty_timer_should_be_launched_when_selected() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        connectDb(scenario);
        Users user = new Users("Tomasz", "Szymaniak", "Nala");
        int userId = db.getUserId(user);
        db.addUserScoreIfNotExists(userId, Dictionary.Difficulty.Advanced.NAME, Dictionary.Rings.RING_1);
        db.userScoresDao().updateScores(new UserScore(userId, Dictionary.Difficulty.Advanced.NAME, Dictionary.Rings.RING_1, 75, "01:00:00", 1, true, 1,  0, "Ok", null));
        UserScore userScore = db.userScoresDao().getUserScore(userId, Dictionary.Difficulty.Advanced.NAME, Dictionary.Rings.RING_1);
        Assert.assertEquals(userScore.attemptTime, "01:00:00");

        onView(MainActivityScreen.startAdvancedModeButton).perform(click());
        validateConfirmOverrideDialog();
        Action.clickByText(R.string.dialog_negative_button);
        userScore = db.userScoresDao().getUserScore(userId, Dictionary.Difficulty.Advanced.NAME, Dictionary.Rings.RING_1);
        Assert.assertEquals("01:00:00", userScore.attemptTime);
        Assert.assertEquals(1, userScore.falseAlarms);
        Assert.assertEquals(1, userScore.treatDrop);
        Assert.assertTrue(userScore.defecation);
        Assert.assertEquals(75, userScore.score);
        Assert.assertEquals("Ok", userScore.overview);

        Assert.assertTrue(Validate.isElementDisplayedById(R.id.card_view));

        onView(MainActivityScreen.startAdvancedModeButton).perform(click());
        validateConfirmOverrideDialog();
        Action.clickByText(R.string.dialog_positive_yes_button);
        Assert.assertEquals("04:00:00", Action.getText(TimerActivityScreen.timerTextView));

        userScore = db.userScoresDao().getUserScore(userId, Dictionary.Difficulty.Advanced.NAME, Dictionary.Rings.RING_1);
        Assert.assertNull(userScore.attemptTime);
        Assert.assertEquals(0, userScore.falseAlarms);
        Assert.assertEquals(0, userScore.treatDrop);
        Assert.assertFalse(userScore.defecation);
        Assert.assertEquals(200, userScore.score);
        Assert.assertNull(userScore.overview);
        Assert.assertEquals("Poziom Zaawansowany",Action.getText(TimerActivityScreen.difficultyTextView));
        Assert.assertEquals("Ring 1",Action.getText(TimerActivityScreen.ringTextView));
        Assert.assertEquals("Tomasz Szymaniak i Nala",Action.getText(TimerActivityScreen.usernameTextView));
        scenario.close();
    }

    private <T extends Activity> void connectDb(ActivityScenario<T> scenario) {
        scenario.onActivity(activity -> db = AppDatabase.getInstance(activity.getApplicationContext()));
    }

    public void validateConfirmOverrideDialog(){
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.confirm_dialog_title));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.dialog_positive_yes_button));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.dialog_negative_button));
    }
}