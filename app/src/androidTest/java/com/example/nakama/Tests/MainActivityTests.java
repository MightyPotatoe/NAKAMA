package com.example.nakama.Tests;

import androidx.test.core.app.ActivityScenario;

import com.example.nakama.Activities.MainActivity.MainActivity;
import com.example.nakama.DataBase.Entities.UserScores.UserScore;
import com.example.nakama.DataBase.Entities.Users.Users;
import com.example.nakama.Screens.MainActivityScreen;
import com.example.nakama.Utils.Dictionary;

import org.junit.Assert;
import org.junit.Test;

public class MainActivityTests {
    @Test
    public void clear_user_data_button_should_remove_all_records_in_user_score_table(){
        MainActivityScreen mainActivityScreen = new MainActivityScreen(ActivityScenario.launch(MainActivity.class));
        Users user = new Users("Tomasz", "Szymaniak", "Nala");
        mainActivityScreen.db.userScoresDao().updateScores(new UserScore(user.uid, Dictionary.Difficulty.Advanced.NAME, Dictionary.Rings.RING_1, 75, "01:00:00", 1, true, 1,  0, "Ok", null));
        mainActivityScreen.db.userScoresDao().updateScores(new UserScore(user.uid, Dictionary.Difficulty.Advanced.NAME, Dictionary.Rings.RING_2, 75, "01:00:00", 1, true, 1,  0, "Ok", null));
        mainActivityScreen.db.userScoresDao().updateScores(new UserScore(user.uid, Dictionary.Difficulty.Advanced.NAME, Dictionary.Rings.RING_3, 75, "01:00:00", 1, true, 1,  0, "Ok", null));
        mainActivityScreen.db.userScoresDao().updateScores(new UserScore(user.uid, Dictionary.Difficulty.Advanced.NAME, Dictionary.Rings.RING_4, 75, "01:00:00", 1, true, 1,  0, "Ok", null));

        Users user2 = new Users("Ziomeczek", "Ziomeczkowski", "Ziomek");
        mainActivityScreen.db.userScoresDao().updateScores(new UserScore(user2.uid, Dictionary.Difficulty.Basic.NAME, Dictionary.Rings.RING_1, 75, "01:00:00", 1, true, 1,  0, "Ok", null));
        mainActivityScreen.db.userScoresDao().updateScores(new UserScore(user2.uid, Dictionary.Difficulty.Basic.NAME, Dictionary.Rings.RING_2, 75, "01:00:00", 1, true, 1,  0, "Ok", null));
        mainActivityScreen.db.userScoresDao().updateScores(new UserScore(user2.uid, Dictionary.Difficulty.Basic.NAME, Dictionary.Rings.RING_3, 75, "01:00:00", 1, true, 1,  0, "Ok", null));
        mainActivityScreen.db.userScoresDao().updateScores(new UserScore(user2.uid, Dictionary.Difficulty.Basic.NAME, Dictionary.Rings.RING_4, 75, "01:00:00", 1, true, 1,  0, "Ok", null));

        mainActivityScreen.clickClearUserDataButton();
        mainActivityScreen.confirmClearingData();

        Assert.assertNull(mainActivityScreen.db.userScoresDao().getUserScore(user.uid, Dictionary.Difficulty.Advanced.NAME, Dictionary.Rings.RING_1));
        Assert.assertNull(mainActivityScreen.db.userScoresDao().getUserScore(user.uid, Dictionary.Difficulty.Advanced.NAME, Dictionary.Rings.RING_2));
        Assert.assertNull(mainActivityScreen.db.userScoresDao().getUserScore(user.uid, Dictionary.Difficulty.Advanced.NAME, Dictionary.Rings.RING_3));
        Assert.assertNull(mainActivityScreen.db.userScoresDao().getUserScore(user.uid, Dictionary.Difficulty.Advanced.NAME, Dictionary.Rings.RING_4));

        Assert.assertNull(mainActivityScreen.db.userScoresDao().getUserScore(user2.uid, Dictionary.Difficulty.Basic.NAME, Dictionary.Rings.RING_1));
        Assert.assertNull(mainActivityScreen.db.userScoresDao().getUserScore(user2.uid, Dictionary.Difficulty.Basic.NAME, Dictionary.Rings.RING_2));
        Assert.assertNull(mainActivityScreen.db.userScoresDao().getUserScore(user2.uid, Dictionary.Difficulty.Basic.NAME, Dictionary.Rings.RING_3));
        Assert.assertNull(mainActivityScreen.db.userScoresDao().getUserScore(user2.uid, Dictionary.Difficulty.Basic.NAME, Dictionary.Rings.RING_4));
        mainActivityScreen.closeScenario();
    }
}