package com.example.nakama.Screens;

import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

import android.view.View;

import com.example.nakama.R;

import org.hamcrest.Matcher;

public class MainActivityScreen {
    public static Matcher<View> startBasicModeButton = allOf(withId(R.id.difficultySelectionCardButton), isDescendantOfA(withId(R.id.card_view)));
    public static Matcher<View> startAdvancedModeButton = allOf(withId(R.id.difficultySelectionCardButton), isDescendantOfA(withId(R.id.card_view2)));
}