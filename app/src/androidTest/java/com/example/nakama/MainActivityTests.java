package com.example.nakama;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;

import androidx.test.core.app.ActivityScenario;

import com.example.nakama.Activities.MainActivity.MainActivity;
import com.example.nakama.Screens.MainActivityScreen;
import com.example.nakama.Screens.TimerActivityScreen;
import com.example.nakama.Utils.Action;

import org.junit.Assert;
import org.junit.Test;

public class MainActivityTests {

    /**
     * Given user is on Main Activity
     * And user selects basic level
     * Then Timer is open and time is set to 2 minutes
     */
    @Test
    public void basic_difficulty_timer_should_be_launched_when_selected() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        onView(MainActivityScreen.startBasicModeButton).perform(click());
        Assert.assertEquals("02:00:00", Action.getText(TimerActivityScreen.timerTextView));
        scenario.close();
    }

    /**
     * Given user is on Main Activity
     * And user selects advanced level
     * Then Timer is open and time is set to 2 minutes
     */
    @Test
    public void advanced_difficulty_timer_should_be_launched_when_selected() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        onView(MainActivityScreen.startAdvancedModeButton).perform(click());
        Assert.assertEquals("04:00:00", Action.getText(TimerActivityScreen.timerTextView));
        scenario.close();
    }
}