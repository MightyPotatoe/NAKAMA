package com.example.nakama.Tests;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.nakama.Activities.MainActivity.MainActivity;
import com.example.nakama.DataBase.Entities.UserScores.UserScore;
import com.example.nakama.Screens.MainActivityScreen;
import com.example.nakama.Screens.OverallImpressionScreen;
import com.example.nakama.Screens.RingResultActivityScreen;
import com.example.nakama.Screens.TimerActivityScreen;

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
        MainActivityScreen mainActivityScreen = new MainActivityScreen(ActivityScenario.launch(MainActivity.class));
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
        OverallImpressionScreen overallImpressionScreen = timerActivityScreen.confirmFinish();

        overallImpressionScreen.setDescription(0,"Opis 1");
        overallImpressionScreen.setScore(0,"10");
        overallImpressionScreen.clickAddButton();
        overallImpressionScreen.setDescription(1,"Opis 2");
        overallImpressionScreen.setScore(1,"5");
        RingResultActivityScreen ringResultActivityScreen = overallImpressionScreen.clickSaveButton();

        Assert.assertEquals("105 pkt.", ringResultActivityScreen.getSummaryPoints());
        userScore = timerActivityScreen.db.userScoresDao().getUserScore(timerActivityScreen.appPreferences.getUserId(), timerActivityScreen.appPreferences.getDifficulty(), timerActivityScreen.appPreferences.getActiveRing());
        Assert.assertTrue(userScore.overview.contains("Opis 1"));
        Assert.assertTrue(userScore.overview.contains("Opis 2"));
    }
}
