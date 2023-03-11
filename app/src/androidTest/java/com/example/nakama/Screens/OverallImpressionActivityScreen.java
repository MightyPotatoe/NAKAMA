package com.example.nakama.Screens;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;

import com.example.nakama.R;
import com.example.nakama.Utils.Action;
import com.example.nakama.Utils.Finder;

import org.hamcrest.Matcher;

public class OverallImpressionActivityScreen extends BaseScreen{

    public OverallImpressionActivityScreen(ActivityScenario<?> scenario) {
        super(scenario);
    }

    public static Matcher<View> addButton =  withId(R.id.addImpressionButton);
    public static Matcher<View> saveButton =  withId(R.id.saveImpressionsButton);
    public static Matcher<View> descriptionInputMatcher = withId(R.id.impressionTextInput);
    public static Matcher<View> scoreInputMatcher = withId(R.id.impressionsScoreTextInput);

    public static Matcher<View> skipButton = withId(R.id.skipImpressionsButton);

    public void setDescription(int index, String description){
        onView(Finder.getElementFromMatchAtPosition(descriptionInputMatcher, index)).perform(typeText(description));
        Espresso.closeSoftKeyboard();
    }

    public void setScore(int index, String points){
        onView(Finder.getElementFromMatchAtPosition(scoreInputMatcher, index)).perform(typeText(points));
        Espresso.closeSoftKeyboard();
    }

    public void clickAddButton(){
        Action.clickOnView(addButton);
    }

    public RingSummaryActivityScreen clickSaveButton(){
        Action.clickOnView(saveButton);
        return new RingSummaryActivityScreen(scenario);
    }

    public RingSummaryActivityScreen clickSkipButton(){
        Action.clickOnView(skipButton);
        return new RingSummaryActivityScreen(scenario);
    }
}