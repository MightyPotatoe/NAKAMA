package com.example.nakama;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.nakama.Activities.MainActivity;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TimerActivityTests {

    TextView textView;
    CircularProgressIndicator circularProgressIndicator;
    ImageButton playButton;
    ImageButton pauseButton;
    ImageButton restartButton;
    ImageButton doneButton;

    /**
     * Given user is on MainActivity
     * And timer is set to 10 seconds
     * Then following elements are displayed:
     * | ELEMENT           | VISIBILITY | TEXT     |MAX   | PROGRESS |
     * | Time Progress Bar | VISIBLE    |          |10000 | 10000    |
     * | Play button       | VISIBLE    |          |      |          |
     * | Pause button      | INVISIBLE  |          |      |          |
     * | Reset button      | INVISIBLE  |          |      |          |
     * | Done button       | INVISIBLE  |          |      |          |
     * | Time TextView     | VISIBLE    | 00:10:00 |      |          |
     * When user press play button
     * Then following elements are displayed:
     * | ELEMENT           | VISIBILITY | TEXT        |MAX     | PROGRESS  |
     * | Time Progress Bar | VISIBLE    |             | 10000  | !=10000 |
     * | Play button       | INVISIBLE  |             |        |           |
     * | Pause button      | VISIBLE    |             |        |           |
     * | Reset button      | INVISIBLE  |             |        |           |
     * | Done button       | INVISIBLE  |             |        |           |
     * | Time TextView     | VISIBLE    | != 00:00:00 |        |           |
     * Then After timer is finished following elements are displayed:
     * | ELEMENT           | VISIBILITY | TEXT     |MAX   | PROGRESS |
     * | Time Progress Bar | VISIBLE    |          |10000 | 0        |
     * | Play button       | INVISIBLE  |          |      |          |
     * | Pause button      | INVISIBLE  |          |      |          |
     * | Reset button      | VISIBLE    |          |      |          |
     * | Done button       | VISIBLE    |          |      |          |
     * | Time TextView     | VISIBLE    | 00:00:00 |      |          |
     */
    @Test
    public void timer_activity_should_display_play_button_on_launch() throws InterruptedException {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        initializeView(scenario);
        Assert.assertEquals(View.VISIBLE, circularProgressIndicator.getVisibility());
        Assert.assertEquals(10000, circularProgressIndicator.getMax());
        Assert.assertEquals(10000, circularProgressIndicator.getProgress());
        Assert.assertEquals(View.VISIBLE, playButton.getVisibility());
        Assert.assertEquals(View.INVISIBLE, pauseButton.getVisibility());
        Assert.assertEquals(View.INVISIBLE, restartButton.getVisibility());
        Assert.assertEquals(View.INVISIBLE, doneButton.getVisibility());
        Assert.assertEquals(View.VISIBLE, textView.getVisibility());
        Assert.assertEquals("00:10:00", textView.getText());

        onView(withId(R.id.playButton)).perform(click());
        Assert.assertEquals(View.INVISIBLE, playButton.getVisibility());
        Assert.assertEquals(View.VISIBLE, pauseButton.getVisibility());
        Assert.assertEquals(View.INVISIBLE, restartButton.getVisibility());
        Assert.assertEquals(View.INVISIBLE, doneButton.getVisibility());
        Assert.assertEquals(View.VISIBLE, textView.getVisibility());
        Assert.assertNotEquals("00:10:00", textView.getText());
        Assert.assertEquals(10000, circularProgressIndicator.getMax());
        Assert.assertNotEquals(10000, circularProgressIndicator.getProgress());

        Thread.sleep(10000);
        Assert.assertEquals(View.INVISIBLE, playButton.getVisibility());
        Assert.assertEquals(View.INVISIBLE, pauseButton.getVisibility());
        Assert.assertEquals(View.VISIBLE, restartButton.getVisibility());
        Assert.assertEquals(View.VISIBLE, doneButton.getVisibility());
        Assert.assertEquals(View.VISIBLE, textView.getVisibility());
        Assert.assertEquals("00:00:00", textView.getText());
        Assert.assertEquals(10000, circularProgressIndicator.getMax());
        Assert.assertEquals(0, circularProgressIndicator.getProgress());
        scenario.close();
    }

    private <T extends Activity> void initializeView(ActivityScenario<T> scenario) {
        scenario.onActivity(activity -> {
            textView = activity.findViewById(R.id.timeTextView);
            circularProgressIndicator = activity.findViewById(R.id.timeProgressBar);
            playButton = activity.findViewById(R.id.playButton);
            pauseButton = activity.findViewById(R.id.pauseButton);
            restartButton = activity.findViewById(R.id.resetButton);
            doneButton = activity.findViewById(R.id.doneButton);
        });

    }
}
