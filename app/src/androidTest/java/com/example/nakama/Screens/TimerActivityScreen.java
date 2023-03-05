package com.example.nakama.Screens;

import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.view.View;

import com.example.nakama.R;

import org.hamcrest.Matcher;

public class TimerActivityScreen {
    public static Matcher<View> timerTextView =  withId(R.id.timeTextView);
    public static Matcher<View> usernameTextView =  withId(R.id.timerUserDetailsTextView);
    public static Matcher<View> difficultyTextView =  withId(R.id.timerDifficultyTextView);
    public static Matcher<View> ringTextView =  withId(R.id.timerRingDetailsTextView);
}