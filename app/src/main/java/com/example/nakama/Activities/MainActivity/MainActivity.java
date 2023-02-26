package com.example.nakama.Activities.MainActivity;

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

import com.example.nakama.R;
import com.example.nakama.Services.TimerService;
import com.example.nakama.Utils.Converter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MainActivity extends AppCompatActivity {

    private final int DEFAULT_TIMER_VALUE = 10000;

    MainActivityViewManager viewManager;
    private Intent timerServiceIntent;
    PowerManager.WakeLock wakeLock;
    PowerManager powerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        viewManager = new MainActivityViewManager(MainActivity.this);

        //Registering Broadcast Receiver for BrewingServiceMessages
        timerServiceIntent = new Intent(this, TimerService.class);
        registerReceiver(broadcastReceiver, new IntentFilter(TimerService.TIMER_ACTION)); //<----Register

        //Setting up power management
        powerManager = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "myapp:mywakelog");
        wakeLock.acquire(15*60*1000L /*15 minutes*/);
        //Initialize default view
        viewManager.setViewToDefaultState(DEFAULT_TIMER_VALUE);
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
            long timeLeft = intent.getLongExtra(TimerService.BROADCAST_TIMER_VALUE, DEFAULT_TIMER_VALUE);
            viewManager.setTimerCurrentTime(timeLeft);
            if(timeLeft == 0){
                viewManager.setViewToFinishedState();
                showTimeUpDialog();
            }
        }
    };

    //----------------------------------------------------BUTTONS ACTIONS--------------------------------------------------------------------
    public void onPlayButtonClick(View view) {
        int POLLING_FREQUENCY = 500;

        Log.v("ACTIVITY DEBUG:", "Play button pressed");
        if(!TimerService.isServiceRunning){
            Log.v("ACTIVITY DEBUG:", "Attempting to start service");
            //Get time from textField
            timerServiceIntent.putExtra(TimerService.TIME, Converter.getMillisFromString((String) viewManager.getTimerTextView().getText()));
            timerServiceIntent.putExtra(TimerService.POLLING_FREQUENCY, POLLING_FREQUENCY);
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
        //Initialize default view
        viewManager.setViewToDefaultState(DEFAULT_TIMER_VALUE);
    }

    public void showTimeUpDialog() {
        new MaterialAlertDialogBuilder(MainActivity.this)
                .setTitle(R.string.timeout_dialog_title)
                .setMessage(R.string.timeout_dialog_message)
                .setPositiveButton(R.string.timeout_dialog_positive_button, (dialogInterface, i) -> {
                })
                .show();
    }
}