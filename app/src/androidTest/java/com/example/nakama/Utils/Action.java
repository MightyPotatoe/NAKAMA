package com.example.nakama.Utils;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;

import org.hamcrest.Matcher;

public class Action {

    public static void clickById(int id){
        try{
            onView(withId(id)).perform(click());
        }
        catch (Exception e){
            onView(withId(id)).inRoot(isDialog()).perform(click());
        }
    }
    public static void clickOnView(Matcher<View> view) {
        try{
            onView(view).perform(click());
        }
        catch (Exception e) {
                onView(view).inRoot(isDialog()).perform(click());
        }
    }

    public static void clickByText(int id){
        try{
            onView(withText(id)).perform(click());
        }
        catch (Exception e){
            onView(withText(id)).inRoot(isDialog()).perform(click());
        }
    }

    public static void sendKeys(Matcher<View> view, String text){
        onView(view).perform(typeText(text));
    }

    public static String getText(final Matcher<View> matcher) {
        final String[] stringHolder = { null };
        onView(matcher).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(TextView.class);
            }

            @Override
            public String getDescription() {
                return "getting text from a TextView";
            }

            @Override
            public void perform(UiController uiController, View view) {
                TextView tv = (TextView)view; //Save, because of check in getConstraints()
                stringHolder[0] = tv.getText().toString();
            }
        });
        return stringHolder[0];
    }

    public static Integer getProgress(final Matcher<View> matcher) {
        final Integer[] progress = {null};
        onView(matcher).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(ProgressBar.class);
            }

            @Override
            public String getDescription() {
                return "getting progress from a ProgressBar";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ProgressBar pb = (ProgressBar)view; //Save, because of check in getConstraints()
                progress[0] = pb.getProgress();
            }
        });
        return progress[0];
    }

    public static Integer getMax(final Matcher<View> matcher) {
        final Integer[] max = {null};
        onView(matcher).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(ProgressBar.class);
            }

            @Override
            public String getDescription() {
                return "getting progress from a ProgressBar";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ProgressBar pb = (ProgressBar)view; //Save, because of check in getConstraints()
                max[0] = pb.getMax();
            }
        });
        return max[0];
    }
}