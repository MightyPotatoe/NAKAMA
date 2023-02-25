package com.example.nakama.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.nakama.R;
import com.example.nakama.Services.TimerService;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.sql.Time;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    TextView timerTextView;
    CircularProgressIndicator circularProgressIndicator;
    private Intent timerServiceIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerTextView = findViewById(R.id.timeTextView);
        circularProgressIndicator = findViewById(R.id.timeProgressBar);

        //Registering Broadcast Receiver for BrewingServiceMessages
        timerServiceIntent = new Intent(this, TimerService.class);
        registerReceiver(broadcastReceiver, new IntentFilter(TimerService.TIMER_ACTION)); //<----Register
        updateTimer(72138);
    }


    /**
     * On receive broadcast from brewingService
     */
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //get BROADCAST_TIMER_VALUE from broadcast and process it
            long timeLeft = intent.getLongExtra(TimerService.BROADCAST_TIMER_VALUE, -1);
            if(timeLeft != -1){
                updateTimer(timeLeft);
            }
        }
    };

    public void updateTimer(long timeRemain){
        String timeLeft = DurationFormatUtils.formatDuration(timeRemain, "mm:ss:SS", true);
        circularProgressIndicator.setProgress((int)timeRemain);
        Pattern pattern = Pattern.compile("\\d{2}:\\d{2}:\\d{2}");
        Matcher matcher = pattern.matcher(timeLeft);
        if (matcher.find()) {timerTextView.setText(matcher.group(0));}
    }

    public void onPlayButtonClick(View view) {
        Log.v("ACTIVITY DEBUG:", "Play buttoin pressed");
        if(!TimerService.isServiceRunning){
            Log.v("ACTIVITY DEBUG:", "Attempting to start service");
            timerServiceIntent.putExtra(TimerService.TIME, 120000);
            this.startService(timerServiceIntent);
        }
    }
}