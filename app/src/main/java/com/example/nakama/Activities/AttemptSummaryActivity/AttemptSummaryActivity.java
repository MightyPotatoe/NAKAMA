package com.example.nakama.Activities.AttemptSummaryActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nakama.Activities.MainActivity.MainActivity;
import com.example.nakama.R;
import com.example.nakama.Utils.Converter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class AttemptSummaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attempt_summary);
        AttemptSummaryActivityViewManager viewManager = new AttemptSummaryActivityViewManager(this);
        int attemptTime = getIntent().getIntExtra(MainActivity.INTENT_EXTRA_TIME, 0);
        viewManager.getTimeTextView().setText(Converter.millisToString(attemptTime));
        viewManager.setUserScore(getIntent().getIntExtra(MainActivity.INTENT_EXTRA_SCORE, 0));
    }

    @Override
    public void onBackPressed() {
        showConfirmNewAttemptDialog();
    }
    public void onNextAttemptButtonCLick(View view) {
        showConfirmNewAttemptDialog();
    }

    public void showConfirmNewAttemptDialog() {
        new MaterialAlertDialogBuilder(AttemptSummaryActivity.this)
                .setTitle(R.string.confirm_dialog_title)
                .setMessage(R.string.confirm_new_attempt_dialog_message)
                .setPositiveButton(R.string.dialog_positive_yes_button, (dialogInterface, i) -> {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                })
                .setNegativeButton(R.string.dialog_negative_button, (dialogInterface, i) -> {})
                .show();
    }
}