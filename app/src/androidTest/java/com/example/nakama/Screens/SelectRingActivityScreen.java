package com.example.nakama.Screens;

import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.view.View;

import androidx.test.core.app.ActivityScenario;

import com.example.nakama.R;
import com.example.nakama.Utils.Action;
import com.example.nakama.Utils.Validate;

import org.hamcrest.Matcher;
import org.junit.Assert;

public class SelectRingActivityScreen extends BaseScreen{

    //----Ring Buttons---
    public static Matcher<View> ring1Button =  withId(R.id.ring1Button);
    public static Matcher<View> ring2Button =  withId(R.id.ring2Button);
    public static Matcher<View> ring3Button =  withId(R.id.ring3Button);
    public static Matcher<View> ring4Button =  withId(R.id.ring4Button);

    public SelectRingActivityScreen(ActivityScenario<?> scenario) {
        super(scenario);
    }

    public TimerActivityScreen clickRing1Button(){
        Action.clickOnView(ring1Button);
        return new TimerActivityScreen(scenario);
    }
    public TimerActivityScreen clickRing2Button(){
        Action.clickOnView(ring2Button);
        return new TimerActivityScreen(scenario);
    }

    public void dismissUserOverride(){
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.confirm_dialog_title));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.dialog_positive_yes_button));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.dialog_negative_button));
        Action.clickByText(R.string.dialog_negative_button);
    }
    public TimerActivityScreen confirmUserOverride(){
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.confirm_dialog_title));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.dialog_positive_yes_button));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.dialog_negative_button));
        Action.clickByText(R.string.dialog_positive_yes_button);
        return new TimerActivityScreen(scenario);
    }

}
