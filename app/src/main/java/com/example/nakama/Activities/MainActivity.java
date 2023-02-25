package com.example.nakama.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nakama.R;
import com.example.nakama.Services.TimerService;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private final int DEFAULT_TIMER_VALUE = 10000;
    private final int POLLING_FREQUENCY = 1000;
    TextView timerTextView;
    CircularProgressIndicator circularProgressIndicator;
    private Intent timerServiceIntent;
    PowerManager.WakeLock wakeLock;
    PowerManager mgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        //Registering Broadcast Receiver for BrewingServiceMessages
        timerServiceIntent = new Intent(this, TimerService.class);
        registerReceiver(broadcastReceiver, new IntentFilter(TimerService.TIMER_ACTION)); //<----Register

        //Setting up power management
        mgr = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
        wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "myapp:mywakelog");
        wakeLock.acquire(15*60*1000L /*15 minutes*/);

        //Initialize default view
        timerTextView = findViewById(R.id.timeTextView);
        circularProgressIndicator = findViewById(R.id.timeProgressBar);
        circularProgressIndicator.setMax(DEFAULT_TIMER_VALUE);
        updateTimer(DEFAULT_TIMER_VALUE);
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


    /**
     * On receive broadcast from brewingService
     */
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //get BROADCAST_TIMER_VALUE from broadcast and process it
            long timeLeft = intent.getLongExtra(TimerService.BROADCAST_TIMER_VALUE, DEFAULT_TIMER_VALUE);
            updateTimer(timeLeft);
            if(timeLeft == 0){
                findViewById(R.id.pauseButton).setVisibility(View.INVISIBLE);
                findViewById(R.id.resetButton).setVisibility(View.VISIBLE);
                findViewById(R.id.doneButton).setVisibility(View.VISIBLE);
            }
        }
    };

    public void updateTimer(long timeRemain){
        circularProgressIndicator.setProgress((int)timeRemain);
        String timeLeft = DurationFormatUtils.formatDuration(timeRemain, "mm:ss:SS", true);
        circularProgressIndicator.setProgress((int)timeRemain);
        Pattern pattern = Pattern.compile("\\d{2}:\\d{2}:\\d{2}");
        Matcher matcher = pattern.matcher(timeLeft);
        if (matcher.find()) {timerTextView.setText(matcher.group(0));}
    }

    //----------------------------------------------------BUTTONS ACTIONS--------------------------------------------------------------------
    public void onPlayButtonClick(View view) {
        Log.v("ACTIVITY DEBUG:", "Play button pressed");
        if(!TimerService.isServiceRunning){
            Log.v("ACTIVITY DEBUG:", "Attempting to start service");
            timerServiceIntent.putExtra(TimerService.TIME, DEFAULT_TIMER_VALUE);
            timerServiceIntent.putExtra(TimerService.POLLING_FREQUENCY, POLLING_FREQUENCY);
            this.startService(timerServiceIntent);
        }
        findViewById(R.id.playButton).setVisibility(View.INVISIBLE);
        findViewById(R.id.pauseButton).setVisibility(View.VISIBLE);
    }

    public void onPauseButtonClick(View view) {
        Log.v("ACTIVITY DEBUG:", "Pause button pressed");
    }
}