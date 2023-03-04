package com.example.nakama.Activities.TimerActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nakama.Activities.AttemptSummaryActivity.AttemptSummaryActivity;
import com.example.nakama.R;
import com.example.nakama.Services.TimerService;
import com.example.nakama.SharedPreferences.AppPreferences;
import com.example.nakama.Utils.Converter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class TimerActivity extends AppCompatActivity {

    public static final String INTENT_EXTRA_TIME = "TIME";
    public static final String INTENT_EXTRA_SCORE = "SCORE";
    TimerActivityViewManager viewManager;
    private Intent timerServiceIntent;
    PowerManager.WakeLock wakeLock;
    PowerManager powerManager;
    AppPreferences appPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_timer);
        viewManager = new TimerActivityViewManager(TimerActivity.this);
        appPreferences = new AppPreferences(getSharedPreferences(AppPreferences.NAME, Context.MODE_PRIVATE));

        //Registering Broadcast Receiver for BrewingServiceMessages
        timerServiceIntent = new Intent(this, TimerService.class);
        registerReceiver(broadcastReceiver, new IntentFilter(TimerService.TIMER_ACTION)); //<----Register

        //Setting up power management
        powerManager = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "myapp:mywakelog");
        wakeLock.acquire(15*60*1000L /*15 minutes*/);
        //Initialize default view
        viewManager.setViewToDefaultState(appPreferences.getRingTime());
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(TimerService.TIMER_ACTION)); //<----Register
    }
    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver); //<-- Unregister broadcast receiver to avoid memory leak
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(timerServiceIntent);
        wakeLock.release();
    }

    //----------------------------------------------------BROADCAST RECEIVER--------------------------------------------------------------------

    /**
     * On receive broadcast from brewingService
     */
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //get BROADCAST_TIMER_VALUE from broadcast and process it
            long timeLeft = intent.getLongExtra(TimerService.BROADCAST_TIMER_VALUE, appPreferences.getRingTime());
            viewManager.setTimerCurrentTime(timeLeft);
            if(timeLeft == 0){
                viewManager.setViewToFinishedState();
                showTimeUpDialog();
            }
        }
    };

    //----------------------------------------------------BUTTONS ACTIONS--------------------------------------------------------------------
    public void onPlayButtonClick(View view) {
        Log.v("ACTIVITY DEBUG:", "Play button pressed");
        if(!TimerService.isServiceRunning){
            Log.v("ACTIVITY DEBUG:", "Attempting to start service");
            //Get time from textField
            timerServiceIntent.putExtra(TimerService.TIME, Converter.stringToMillis((String) viewManager.getTimerTextView().getText()));
            timerServiceIntent.putExtra(TimerService.POLLING_FREQUENCY, appPreferences.getPollingFrequency());
            this.startService(timerServiceIntent);
        }
        viewManager.setViewToPlayedState();
    }

    public void onPauseButtonClick(View view) {
        Log.v("ACTIVITY DEBUG:", "Pause button pressed");
        TimerService.pauseTimer();
        this.stopService(timerServiceIntent);
        viewManager.setViewToPausedState();
    }

    public void onResetButtonClick(View view) {
        Log.v("ACTIVITY DEBUG:", "Reset button pressed");
        showConfirmResetDialog();
    }

    public void onDoneButtonClick(View view) {
        Log.v("ACTIVITY DEBUG:", "Done button pressed");
        showConfirmAttemptDialog();
    }

    public void showTimeUpDialog() {
        new MaterialAlertDialogBuilder(TimerActivity.this)
                .setTitle(R.string.timeout_dialog_title)
                .setMessage(R.string.timeout_dialog_message)
                .setPositiveButton(R.string.dialog_positive_ok_button, (dialogInterface, i) -> {
                })
                .show();
    }

    public void showConfirmResetDialog() {
        new MaterialAlertDialogBuilder(TimerActivity.this)
                .setTitle(R.string.confirm_dialog_title)
                .setMessage(R.string.confirm_reset_dialog_message)
                .setPositiveButton(R.string.dialog_positive_yes_button, (dialogInterface, i) -> viewManager.setViewToDefaultState(appPreferences.getRingTime()))
                .setNegativeButton(R.string.dialog_negative_button, (dialogInterface, i) -> {})
                .show();
    }

    public void showConfirmAttemptDialog() {
        new MaterialAlertDialogBuilder(TimerActivity.this)
                .setTitle(R.string.confirm_dialog_title)
                .setMessage(R.string.confirm_attempt_dialog_message)
                .setPositiveButton(R.string.dialog_positive_yes_button, (dialogInterface, i) -> {
                    //getting duration of this run
                    int timeLeft = Converter.stringToMillis((String) viewManager.getTimerTextView().getText());
                    int runTimeInMillis = appPreferences.getRingTime() - timeLeft;
                    Intent intent = new Intent(this, AttemptSummaryActivity.class);
                    intent.putExtra(INTENT_EXTRA_TIME, runTimeInMillis);
                    intent.putExtra(INTENT_EXTRA_SCORE, calculateUserScore());
                    startActivity(intent);
                })
                .setNegativeButton(R.string.dialog_negative_button, (dialogInterface, i) -> {})
                .show();
    }

    public int calculateUserScore(){
        int baseScore = 200;
        int timeLeft = Converter.stringToMillis((String) viewManager.getTimerTextView().getText());
        if(timeLeft == 0){
            baseScore = 0;
        }
        return baseScore;
    }
}