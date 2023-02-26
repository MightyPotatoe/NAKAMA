package com.example.nakama;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.nakama.Activities.MainActivity.MainActivity;
import com.example.nakama.Utils.Validate;
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
        Thread.sleep(1000);
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

    /**
     * Given user is on MainActivity
     * And timer is set to 10 seconds
     * When user press 'play' button and waits 2s
     * And user press 'pause'
     * Then following elements are displayed:
     * | ELEMENT           | VISIBILITY |
     * | Time Progress Bar | VISIBLE    |
     * | Play button       | VISIBLE    |
     * | Pause button      | INVISIBLE  |
     * | Reset button      | VISIBLE    |
     * | Done button       | VISIBLE    |
     * | Time TextView     | VISIBLE    |
     * When user waits 2s
     * Then values on timer and progress bar are not changed
     * When user press 'play' and waits 1s
     * Then following elements are displayed:
     * | ELEMENT           | VISIBILITY |
     * | Time Progress Bar | VISIBLE    |
     * | Play button       | INVISIBLE  |
     * | Pause button      | VISIBLE    |
     * | Reset button      | INVISIBLE  |
     * | Done button       | INVISIBLE  |
     * | Time TextView     | VISIBLE    |
     * When user press 'pause'
     * Then following elements are displayed:
     * | ELEMENT           | VISIBILITY |
     * | Time Progress Bar | VISIBLE    |
     * | Play button       | VISIBLE    |
     * | Pause button      | INVISIBLE  |
     * | Reset button      | VISIBLE    |
     * | Done button       | VISIBLE    |
     * | Time TextView     | VISIBLE    |
     * And timer value and progress bar progress is lesser than on previous pause
     * When user waits 2s
     * Then values on timer and progress bar are not changed
     * When user press 'play' and waits for timer to finish
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
    public void timer_activity_should_pause_and_continue() throws InterruptedException {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        initializeView(scenario);
        //Click start and wait 2s
        onView(withId(R.id.playButton)).perform(click());
        Thread.sleep(2000);

        //Click pause
        onView(withId(R.id.pauseButton)).perform(click());
        Assert.assertEquals(View.VISIBLE, playButton.getVisibility());
        Assert.assertEquals(View.INVISIBLE, pauseButton.getVisibility());
        Assert.assertEquals(View.VISIBLE, restartButton.getVisibility());
        Assert.assertEquals(View.VISIBLE, doneButton.getVisibility());
        Assert.assertEquals(View.VISIBLE, textView.getVisibility());
        Assert.assertNotEquals("00:10:00", textView.getText());
        Assert.assertEquals(10000, circularProgressIndicator.getMax());
        Assert.assertNotEquals(10000, circularProgressIndicator.getProgress());
        String timerValueOnPause = (String) textView.getText();
        int progressBarMaxOnPause = circularProgressIndicator.getMax();
        int progressBarProgressOnPause = circularProgressIndicator.getProgress();

        //Wait 2s to check if timer is actually stopped
        Thread.sleep(2000);
        Assert.assertEquals(timerValueOnPause, textView.getText());
        Assert.assertEquals(progressBarMaxOnPause, circularProgressIndicator.getMax());
        Assert.assertEquals(progressBarProgressOnPause, circularProgressIndicator.getProgress());

        //Click play and wait 1s
        onView(withId(R.id.playButton)).perform(click());
        Assert.assertEquals(View.INVISIBLE, playButton.getVisibility());
        Assert.assertEquals(View.VISIBLE, pauseButton.getVisibility());
        Assert.assertEquals(View.INVISIBLE, restartButton.getVisibility());
        Assert.assertEquals(View.INVISIBLE, doneButton.getVisibility());
        Assert.assertEquals(View.VISIBLE, textView.getVisibility());
        Assert.assertNotEquals("00:10:00", textView.getText());
        Assert.assertEquals(10000, circularProgressIndicator.getMax());
        Assert.assertNotEquals(10000, circularProgressIndicator.getProgress());
        Thread.sleep(1000);

        //Click pause again
        onView(withId(R.id.pauseButton)).perform(click());
        Assert.assertEquals(View.VISIBLE, playButton.getVisibility());
        Assert.assertEquals(View.INVISIBLE, pauseButton.getVisibility());
        Assert.assertEquals(View.VISIBLE, restartButton.getVisibility());
        Assert.assertEquals(View.VISIBLE, doneButton.getVisibility());
        Assert.assertEquals(View.VISIBLE, textView.getVisibility());
        Assert.assertNotEquals("00:10:00", textView.getText());
        Assert.assertEquals(10000, circularProgressIndicator.getMax());
        Assert.assertNotEquals(10000, circularProgressIndicator.getProgress());
        String timerValueOnSecondPause = (String) textView.getText();
        int progressBarMaxOnSecondPause = circularProgressIndicator.getMax();
        int progressBarProgressOnSecondPause = circularProgressIndicator.getProgress();
        Assert.assertTrue(progressBarProgressOnSecondPause < progressBarProgressOnPause);

        //Wait 1s to check if timer is actually stopped
        Thread.sleep(2000);
        Assert.assertEquals(timerValueOnSecondPause, textView.getText());
        Assert.assertEquals(progressBarMaxOnSecondPause, circularProgressIndicator.getMax());
        Assert.assertEquals(progressBarProgressOnSecondPause, circularProgressIndicator.getProgress());

        //Cllick play and wait for timer to be finished
        onView(withId(R.id.playButton)).perform(click());
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

    /**
     * Given user is on MainActivity
     * And timer is set to 10 seconds
     * When user press 'play' button and waits 2s
     * And user press 'pause'
     * And user press 'reset'
     * Then following elements are displayed:
     * | ELEMENT           | VISIBILITY | TEXT     |MAX   | PROGRESS |
     * | Time Progress Bar | VISIBLE    |          |10000 | 10000    |
     * | Play button       | VISIBLE    |          |      |          |
     * | Pause button      | INVISIBLE  |          |      |          |
     * | Reset button      | INVISIBLE  |          |      |          |
     * | Done button       | INVISIBLE  |          |      |          |
     * | Time TextView     | VISIBLE    | 00:10:00 |      |          |
     * When user press 'play' button and until timer is finished
     * And user dismiss timeout dialog
     * And user press 'reset'
     * Then following elements are displayed:
     * | ELEMENT           | VISIBILITY | TEXT     |MAX   | PROGRESS |
     * | Time Progress Bar | VISIBLE    |          |10000 | 10000    |
     * | Play button       | VISIBLE    |          |      |          |
     * | Pause button      | INVISIBLE  |          |      |          |
     * | Reset button      | INVISIBLE  |          |      |          |
     * | Done button       | INVISIBLE  |          |      |          |
     * | Time TextView     | VISIBLE    | 00:10:00 |      |          |
     */
    @Test
    public void timer_should_be_reset_when_reset_button_is_pressed() throws InterruptedException {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        initializeView(scenario);
        //Click start and wait 2s
        onView(withId(R.id.playButton)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.pauseButton)).perform(click());

        //Click reset when paused
        onView(withId(R.id.resetButton)).perform(click());
        Assert.assertEquals(View.VISIBLE, circularProgressIndicator.getVisibility());
        Assert.assertEquals(10000, circularProgressIndicator.getMax());
        Assert.assertEquals(10000, circularProgressIndicator.getProgress());
        Assert.assertEquals(View.VISIBLE, playButton.getVisibility());
        Assert.assertEquals(View.INVISIBLE, pauseButton.getVisibility());
        Assert.assertEquals(View.INVISIBLE, restartButton.getVisibility());
        Assert.assertEquals(View.INVISIBLE, doneButton.getVisibility());
        Assert.assertEquals(View.VISIBLE, textView.getVisibility());
        Assert.assertEquals("00:10:00", textView.getText());

        //Click play and wait for timer to be finished
        onView(withId(R.id.playButton)).perform(click());
        Thread.sleep(10000);

        //Close timeout dialog
        onView(withText(R.string.timeout_dialog_positive_button)).perform(click());
        //Click reset when finished
        onView(withId(R.id.resetButton)).perform(click());
        Assert.assertEquals(View.VISIBLE, circularProgressIndicator.getVisibility());
        Assert.assertEquals(10000, circularProgressIndicator.getMax());
        Assert.assertEquals(10000, circularProgressIndicator.getProgress());
        Assert.assertEquals(View.VISIBLE, playButton.getVisibility());
        Assert.assertEquals(View.INVISIBLE, pauseButton.getVisibility());
        Assert.assertEquals(View.INVISIBLE, restartButton.getVisibility());
        Assert.assertEquals(View.INVISIBLE, doneButton.getVisibility());
        Assert.assertEquals(View.VISIBLE, textView.getVisibility());
        Assert.assertEquals("00:10:00", textView.getText());
    }

    /**
     * Given user is on MainActivity
     * And timer is set to 10 seconds
     * When user press 'play' button and waits until timer is finished
     * Then timeout dialog is displayed
     * When user clicks dialog accept button
     * Then timeout dialog is closed displayed
     */
    @Test
    public void dialogShouldBeDisplayedWhenTimerIsFinished() throws InterruptedException {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        initializeView(scenario);
        onView(withId(R.id.playButton)).perform(click());
        Thread.sleep(10000);
        Assert.assertTrue(Validate.isElementDisplayed(R.string.timeout_dialog_title));
        Assert.assertTrue(Validate.isElementDisplayed(R.string.timeout_dialog_message));
        Assert.assertTrue(Validate.isElementDisplayed(R.string.timeout_dialog_positive_button));
        Assert.assertTrue(Validate.isElementDisplayed(R.string.timeout_dialog_title));
        onView(withText(R.string.timeout_dialog_positive_button)).perform(click());

        Assert.assertFalse(Validate.isElementDisplayed(R.string.timeout_dialog_title));
        Assert.assertFalse(Validate.isElementDisplayed(R.string.timeout_dialog_message));
        Assert.assertFalse(Validate.isElementDisplayed(R.string.timeout_dialog_positive_button));
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
