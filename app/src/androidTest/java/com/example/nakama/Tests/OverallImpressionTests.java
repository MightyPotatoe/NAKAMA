package com.example.nakama.Tests;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.nakama.Activities.MainActivity.MainActivity;
import com.example.nakama.DataBase.Entities.UserScores.UserScore;
import com.example.nakama.DataBase.Entities.Users.Users;
import com.example.nakama.Screens.MainActivityScreen;
import com.example.nakama.Screens.OverallImpressionActivityScreen;
import com.example.nakama.Screens.RingSummaryActivityScreen;
import com.example.nakama.Screens.TimerActivityScreen;
import com.example.nakama.Utils.Dictionary;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class OverallImpressionTests {

    /**
     * User should be able to add 2 impressions and the score should be accounted for the final reult
     */
    @Test
    public void overall_impressions_should_be_accounted_for_final_result(){
        Users user = new Users("Tomasz", "Szymaniak", "Nala");
        MainActivityScreen mainActivityScreen = new MainActivityScreen(ActivityScenario.launch(MainActivity.class));
        int userId = mainActivityScreen.db.getUserId(user);
        mainActivityScreen.db.addUserScoreIfNotExists(userId, Dictionary.Difficulty.Basic.NAME, Dictionary.Rings.RING_1);
        mainActivityScreen.db.userScoresDao().updateScores(new UserScore(userId, Dictionary.Difficulty.Basic.NAME, Dictionary.Rings.RING_1, 75, "01:00:00", 1, true, 1,  0, "Ok", null));

        mainActivityScreen.clickStartBasicModeButton();
        TimerActivityScreen timerActivityScreen = mainActivityScreen.confirmUserOverride();
        timerActivityScreen.clickPlayButton();
        UserScore userScore = timerActivityScreen.db.userScoresDao().getUserScore(timerActivityScreen.appPreferences.getUserId(), timerActivityScreen.appPreferences.getDifficulty(), timerActivityScreen.appPreferences.getActiveRing());
        Assert.assertEquals(200, userScore.score);

        timerActivityScreen.clickTreatDroppedButton();
        timerActivityScreen.confirmTreatDropped();
        timerActivityScreen.clickFalseAlarm();
        timerActivityScreen.confirmFalseAlarm();
        userScore = timerActivityScreen.db.userScoresDao().getUserScore(timerActivityScreen.appPreferences.getUserId(), timerActivityScreen.appPreferences.getDifficulty(), timerActivityScreen.appPreferences.getActiveRing());
        Assert.assertEquals(120, userScore.score);
        timerActivityScreen.clickPositiveAlarm();
        timerActivityScreen.confirmPositiveAlarm();
        timerActivityScreen.dismissAllSamplesFoundDialog();
        timerActivityScreen.clickDoneButton();
        timerActivityScreen.validateConfirmFinishDialog();
        OverallImpressionActivityScreen overallImpressionActivityScreen = timerActivityScreen.confirmFinish();

        overallImpressionActivityScreen.setDescription(0,"Opis 1");
        overallImpressionActivityScreen.setScore(0,"10");
        overallImpressionActivityScreen.clickAddButton();
        overallImpressionActivityScreen.setDescription(1,"Opis 2");
        overallImpressionActivityScreen.setScore(1,"5");
        RingSummaryActivityScreen ringSummaryActivityScreen = overallImpressionActivityScreen.clickSaveButton();

        Assert.assertEquals("105 pkt.", ringSummaryActivityScreen.getSummaryPoints());
        userScore = timerActivityScreen.db.userScoresDao().getUserScore(timerActivityScreen.appPreferences.getUserId(), timerActivityScreen.appPreferences.getDifficulty(), timerActivityScreen.appPreferences.getActiveRing());
        Assert.assertTrue(userScore.overview.contains("Opis 1"));
        Assert.assertTrue(userScore.overview.contains("Opis 2"));
    }
}
