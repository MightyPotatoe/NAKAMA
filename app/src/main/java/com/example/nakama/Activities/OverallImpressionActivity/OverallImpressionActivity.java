package com.example.nakama.Activities.OverallImpressionActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nakama.Activities.AttemptSummaryActivity.RingSummaryActivity;
import com.example.nakama.DataBase.AppDatabase;
import com.example.nakama.R;
import com.example.nakama.SharedPreferences.AppPreferences;
import com.example.nakama.Utils.Dictionary;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;


public class OverallImpressionActivity extends AppCompatActivity{

    OverallImpressionViewManager viewManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppPreferences appPreferences = new AppPreferences(getSharedPreferences(AppPreferences.NAME, MODE_PRIVATE));
        switch (appPreferences.getDifficulty()){
            case Dictionary.Difficulty.Basic.NAME:
                setTheme(R.style.Theme_NAKAMA_BasicLevelTheme);
                break;
            case Dictionary.Difficulty.Advanced.NAME:
                setTheme(R.style.Theme_NAKAMA_AdvancedLevelTheme);
                break;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overall_impression);
        viewManager = new OverallImpressionViewManager(this);
        viewManager.addNewImpression();
        findViewById(R.id.addImpressionButton).setOnClickListener(view -> onAddButtonClick());
    }

    public void onSkipButtonClick(View view) {
        Intent intent = new Intent(this, RingSummaryActivity.class);
        startActivity(intent);
    }

    public void onSaveButtonClick(View view) {
        viewManager.resetErrors();
        int totalMinusScoreDistributed = 0;
        //Validating score distribution
        for(int i =0; i < viewManager.impressionsLayout.getChildCount(); i++){
            EditText numberEditText = viewManager.impressionsLayout.getChildAt(i).findViewById(R.id.impressionsScoreTextInput);
            String number = numberEditText.getText().toString();
            if(!number.isEmpty()){
                totalMinusScoreDistributed += Integer.parseInt(number);
            }
        }
        if(totalMinusScoreDistributed > 30){
            showWrongPointsDistributionDialog();
            return;
        }
        //Validating if there are empty descriptions
        for(int i =0; i < viewManager.impressionsLayout.getChildCount(); i++){
            EditText textEditText = viewManager.impressionsLayout.getChildAt(i).findViewById(R.id.impressionTextInput);
            EditText numberEditText = viewManager.impressionsLayout.getChildAt(i).findViewById(R.id.impressionsScoreTextInput);
            if(textEditText.getText().toString().isEmpty() || numberEditText.getText().toString().isEmpty()){
                TextInputLayout descriptionBox = viewManager.impressionsLayout.getChildAt(i).findViewById(R.id.impressionEditTextLayout);
                descriptionBox.setErrorEnabled(true);
                descriptionBox.setError("Uzupełnij opis i punkty");
                TextInputLayout scoreBox = viewManager.impressionsLayout.getChildAt(i).findViewById(R.id.minusScoreEditTextLayout);
                scoreBox.setErrorEnabled(true);
                scoreBox.setError(" ");
                return;
            }
        }
        HashMap<String, Integer> hashMap = new HashMap<>();
        for(int i =0; i < viewManager.impressionsLayout.getChildCount(); i++){
            EditText textEditText = viewManager.impressionsLayout.getChildAt(i).findViewById(R.id.impressionTextInput);
            EditText numberEditText = viewManager.impressionsLayout.getChildAt(i).findViewById(R.id.impressionsScoreTextInput);
            hashMap.put(textEditText.getText().toString(), Integer.parseInt(numberEditText.getText().toString()));
        }
        AppDatabase db = AppDatabase.getInstance(this);
        AppPreferences appPreferences = new AppPreferences(getSharedPreferences(AppPreferences.NAME, MODE_PRIVATE));
        db.userScoresDao().updateUserScoresOverallImpressions(appPreferences.getUserId(), appPreferences.getDifficulty(), appPreferences.getActiveRing(), hashMap.toString());
        int userScore = db.userScoresDao().getUserScoresScore(appPreferences.getUserId(), appPreferences.getDifficulty(), appPreferences.getActiveRing());
        db.userScoresDao().updateScorePoints(appPreferences.getUserId(), appPreferences.getDifficulty(), appPreferences.getActiveRing(), userScore - totalMinusScoreDistributed);
        //save to db and update score
        Intent intent = new Intent(this, RingSummaryActivity.class);
        startActivity(intent);
    }

    public void showWrongPointsDistributionDialog() {
        new MaterialAlertDialogBuilder(OverallImpressionActivity.this)
                .setTitle(R.string.wrong_score_distribution_dialog_title)
                .setMessage(R.string.wrong_score_distribution_dialog_message)
                .setPositiveButton(R.string.dialog_positive_ok_button, (dialogInterface, i) -> {
                })
                .show();
    }
    public void onAddButtonClick(){
        if(viewManager.impressionsLayout.getChildCount()<4){
            viewManager.addNewImpression();
        }
    }
}