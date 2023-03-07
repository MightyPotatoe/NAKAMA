package com.example.nakama.Screens;

import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.view.View;

import androidx.test.core.app.ActivityScenario;

import com.example.nakama.R;
import com.example.nakama.Utils.Action;

import org.hamcrest.Matcher;

public class RingResultActivityScreen extends BaseScreen{
    private final Matcher<View> summaryPointsTextView =  withId(R.id.summaryPointsValue);
    public RingResultActivityScreen(ActivityScenario<?> scenario) {
        super(scenario);
    }

    public String getSummaryPoints(){
        return Action.getText(summaryPointsTextView);
    }
}
