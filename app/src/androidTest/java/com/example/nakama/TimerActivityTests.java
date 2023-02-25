package com.example.nakama;

import android.view.View;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.nakama.Activities.MainActivity;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TimerActivityTests {


    /**
     * Given user is on MainActivity
     * Then following elements are displayed:
     * | Element           | Visibility |
     * | Time Progress Bar | VISIBLE    |
     * | Play button       | VISIBLE    |
     * | Pause button      | INVISIBLE  |
     * | Reset button      | INVISIBLE  |
     * | Done button       | INVISIBLE  |
     * And 'Time' TextView should be set to starting value (2 min)
     */
    @Test
    public void timer_activity_should_display_play_button_on_launch(){
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.onActivity(activity -> {
            Assert.assertEquals(View.VISIBLE, activity.findViewById(R.id.timeProgressBar).getVisibility());
            Assert.assertEquals(View.VISIBLE, activity.findViewById(R.id.playButton).getVisibility());
            Assert.assertEquals(View.INVISIBLE, activity.findViewById(R.id.pauseButton).getVisibility());
            Assert.assertEquals(View.INVISIBLE, activity.findViewById(R.id.resetButton).getVisibility());
            Assert.assertEquals(View.INVISIBLE, activity.findViewById(R.id.doneButton).getVisibility());
            Assert.assertEquals(View.VISIBLE, activity.findViewById(R.id.timeTextView).getVisibility());
            TextView textView = activity.findViewById(R.id.timeTextView);
            Assert.assertEquals("02:00:00", textView.getText());
        });
        scenario.close();
    }
}
