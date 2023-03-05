package com.example.nakama;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;

import android.app.Activity;
import android.content.Context;

import androidx.test.core.app.ActivityScenario;

import com.example.nakama.Activities.MainActivity.MainActivity;
import com.example.nakama.DataBase.AppDatabase;
import com.example.nakama.DataBase.Entities.UserScores.UserScores;
import com.example.nakama.DataBase.Entities.Users.Users;
import com.example.nakama.Screens.MainActivityScreen;
import com.example.nakama.Screens.TimerActivityScreen;
import com.example.nakama.SharedPreferences.AppPreferences;
import com.example.nakama.Utils.Action;
import com.example.nakama.Utils.Dictionary;
import com.example.nakama.Utils.Validate;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

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
        connectDb(scenario);
        db.userScoresDao().delete();

        onView(MainActivityScreen.startBasicModeButton).perform(click());
        Assert.assertEquals("02:00:00", Action.getText(TimerActivityScreen.timerTextView));
        scenario.close();
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
        connectDb(scenario);
        connectToSharedPreferences(scenario);
        db.userScoresDao().delete();
        onView(MainActivityScreen.startAdvancedModeButton).perform(click());
        Assert.assertEquals("04:00:00", Action.getText(TimerActivityScreen.timerTextView));
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
        connectDb(scenario);
        Users user = new Users("Tomasz", "Szymaniak", "Nala");
        int userId = db.getUserId(user);
        db.addUserScore(userId, Dictionary.Difficulty.BASIC, Dictionary.Rings.RING_1);
        db.userScoresDao().updateUserScores(userId, Dictionary.Difficulty.BASIC, Dictionary.Rings.RING_1, "01:00:00", 1, 1, true, 75, "Ok");
        List<UserScores> userScores = db.userScoresDao().getUserScores(userId, Dictionary.Difficulty.BASIC, Dictionary.Rings.RING_1);
        Assert.assertEquals(userScores.get(0).attemptTime, "01:00:00");

        onView(MainActivityScreen.startBasicModeButton).perform(click());
        validateConfirmOverrideDialog();
        Action.clickByText(R.string.dialog_negative_button);
        userScores = db.userScoresDao().getUserScores(userId, Dictionary.Difficulty.BASIC, Dictionary.Rings.RING_1);
        Assert.assertEquals("01:00:00", userScores.get(0).attemptTime);
        Assert.assertEquals(1, userScores.get(0).falseAlarms);
        Assert.assertEquals(1, userScores.get(0).treatDrop);
        Assert.assertTrue(userScores.get(0).defecation);
        Assert.assertEquals(75, userScores.get(0).score);
        Assert.assertEquals("Ok", userScores.get(0).overview);

        Assert.assertTrue(Validate.isElementDisplayedById(R.id.card_view));

        onView(MainActivityScreen.startBasicModeButton).perform(click());
        validateConfirmOverrideDialog();
        Action.clickByText(R.string.dialog_positive_yes_button);
        Assert.assertEquals("02:00:00", Action.getText(TimerActivityScreen.timerTextView));

        userScores = db.userScoresDao().getUserScores(userId, Dictionary.Difficulty.BASIC, Dictionary.Rings.RING_1);
        Assert.assertNull(userScores.get(0).attemptTime);
        Assert.assertEquals(0, userScores.get(0).falseAlarms);
        Assert.assertEquals(0, userScores.get(0).treatDrop);
        Assert.assertFalse(userScores.get(0).defecation);
        Assert.assertEquals(200, userScores.get(0).score);
        Assert.assertNull(userScores.get(0).overview);
        Assert.assertEquals("Poziom Podstawowy",Action.getText(TimerActivityScreen.difficultyTextView));
        Assert.assertEquals("Ring 1",Action.getText(TimerActivityScreen.ringTextView));
        Assert.assertEquals("Tomasz Szymaniak i Nala",Action.getText(TimerActivityScreen.usernameTextView));
        scenario.close();
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
        db.addUserScore(userId, Dictionary.Difficulty.ADVANCED, Dictionary.Rings.RING_1);
        db.userScoresDao().updateUserScores(userId, Dictionary.Difficulty.ADVANCED, Dictionary.Rings.RING_1, "01:00:00", 1, 1, true, 75, "Ok");
        List<UserScores> userScores = db.userScoresDao().getUserScores(userId, Dictionary.Difficulty.ADVANCED, Dictionary.Rings.RING_1);
        Assert.assertEquals(userScores.get(0).attemptTime, "01:00:00");

        onView(MainActivityScreen.startAdvancedModeButton).perform(click());
        validateConfirmOverrideDialog();
        Action.clickByText(R.string.dialog_negative_button);
        userScores = db.userScoresDao().getUserScores(userId, Dictionary.Difficulty.ADVANCED, Dictionary.Rings.RING_1);
        Assert.assertEquals("01:00:00", userScores.get(0).attemptTime);
        Assert.assertEquals(1, userScores.get(0).falseAlarms);
        Assert.assertEquals(1, userScores.get(0).treatDrop);
        Assert.assertTrue(userScores.get(0).defecation);
        Assert.assertEquals(75, userScores.get(0).score);
        Assert.assertEquals("Ok", userScores.get(0).overview);

        Assert.assertTrue(Validate.isElementDisplayedById(R.id.card_view));

        onView(MainActivityScreen.startAdvancedModeButton).perform(click());
        validateConfirmOverrideDialog();
        Action.clickByText(R.string.dialog_positive_yes_button);
        Assert.assertEquals("04:00:00", Action.getText(TimerActivityScreen.timerTextView));

        userScores = db.userScoresDao().getUserScores(userId, Dictionary.Difficulty.ADVANCED, Dictionary.Rings.RING_1);
        Assert.assertNull(userScores.get(0).attemptTime);
        Assert.assertEquals(0, userScores.get(0).falseAlarms);
        Assert.assertEquals(0, userScores.get(0).treatDrop);
        Assert.assertFalse(userScores.get(0).defecation);
        Assert.assertEquals(200, userScores.get(0).score);
        Assert.assertNull(userScores.get(0).overview);
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

    private <T extends Activity> void connectToSharedPreferences(ActivityScenario<T> scenario) {
        scenario.onActivity(activity -> appPreferences = new AppPreferences(activity.getSharedPreferences(AppPreferences.NAME, Context.MODE_PRIVATE)));
    }
}