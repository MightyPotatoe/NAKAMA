package com.example.nakama.Utils;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.NoMatchingViewException;

public class Validate {

    public static boolean isElementDisplayed(int byStringResourceId){
        try{
            onView(withText(byStringResourceId)).check(matches(isDisplayed()));
            return true;
        }
        catch (NoMatchingViewException e){
            return false;
        }
    }
}