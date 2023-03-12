package com.example.nakama.Screens;

import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

import android.view.View;

import androidx.test.core.app.ActivityScenario;

import com.example.nakama.Activities.MainActivity.MainActivity;
import com.example.nakama.R;
import com.example.nakama.Utils.Action;
import com.example.nakama.Utils.Validate;

import org.hamcrest.Matcher;
import org.junit.Assert;

public class MainActivityScreen extends BaseScreen{
    public static Matcher<View> startBasicModeButton = allOf(withId(R.id.difficultySelectionCardButton), isDescendantOfA(withId(R.id.card_view)));
    public static Matcher<View> startAdvancedModeButton = allOf(withId(R.id.difficultySelectionCardButton), isDescendantOfA(withId(R.id.card_view2)));

    public static Matcher<View> clearDataButton = withId(R.id.clearUsersData);

    public MainActivityScreen(ActivityScenario<MainActivity> scenario) {
        super(scenario);
    }
    public TimerActivityScreen clickStartBasicModeButton(){
        Action.clickOnView(startBasicModeButton);
        return new TimerActivityScreen(scenario);
    }

    public TimerActivityScreen clickStartAdvancedModeButton(){
        Action.clickOnView(startAdvancedModeButton);
        return new TimerActivityScreen(scenario);
    }
    public void clickClearUserDataButton(){
        Action.clickOnView(clearDataButton);
    }
    public void confirmClearingData(){
        validateConfirmDataClearDialog();
        Action.clickByText(R.string.dialog_positive_yes_button);
        validateDataClearedConfirmationDialog();
        Action.clickByText(R.string.dialog_positive_ok_button);
    }


    public TimerActivityScreen confirmUserOverride(int time){
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.confirm_dialog_title));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.dialog_positive_yes_button));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.dialog_negative_button));
        Action.clickByText(R.string.dialog_positive_yes_button);
        return new TimerActivityScreen(scenario, time);
    }

    public TimerActivityScreen confirmUserOverride(){
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.confirm_dialog_title));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.dialog_positive_yes_button));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.dialog_negative_button));
        Action.clickByText(R.string.dialog_positive_yes_button);
        return new TimerActivityScreen(scenario);
    }

    public void dismissUserOverride(){
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.confirm_dialog_title));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.dialog_positive_yes_button));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.dialog_negative_button));
        Action.clickByText(R.string.dialog_negative_button);
    }

    public void validateConfirmDataClearDialog(){
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.confirm_dialog_title));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.confirm_clearing_data_message));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.dialog_positive_yes_button));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.dialog_negative_button));
    }

    public void validateDataClearedConfirmationDialog(){
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.confirm_dialog_title));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.data_cleared_confirmation_message));
        Assert.assertTrue(Validate.isElementInDialogDisplayedByText(R.string.dialog_positive_ok_button));
    }
}