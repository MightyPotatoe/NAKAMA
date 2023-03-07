package com.example.nakama.Services;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;


public class TimerService extends Service {

    public static boolean isServiceRunning = false;
    private BroadcastSender broadcastSender;
    public static final String TIME = "TIME";
    public static final String POLLING_FREQUENCY = "POLLING_FREQUENCY";

    //---TIMER----
    public static CountDownTimer countDownTimer;
    public static String TIMER_ACTION = "TIMER_ACTION";
    public static String BROADCAST_TIMER_VALUE = "TIMER_VALUE";

    public static boolean forcedFinish = false;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //-----------------------------SERVICE METHODS--------------------------------------------------
    @Override
    public void onCreate() {
        Log.v("SERVICE DEBUG:", "Timer service created");
        this.broadcastSender = new BroadcastSender(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isServiceRunning = true;
        forcedFinish = false;
        Log.v("SERVICE DEBUG:", "Timer service started");
        //Assigning data passed by intent
        int currentStepTime = intent.getIntExtra(TIME, 120);
        int pollingFrequency = intent.getIntExtra(POLLING_FREQUENCY, 1000);
        //Creating and starting countdown Timer
        initializeCountDownTimer(currentStepTime, pollingFrequency);
        countDownTimer.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        //Canceling timer
        countDownTimer.cancel();
        Log.v("SERVICE DEBUG:", "Service Destroyed");
        isServiceRunning = false;
        super.onDestroy();
    }

    //----------------COUNTDOWN TIMER-----------------------------------------------
    //---Definition
    public void initializeCountDownTimer(int timerValueInMilis, int pollingFrequency) {
        Log.v("TIMER DEBUG:", "Initializing countdown timer");
        //Creating countdownTimer
        countDownTimer = new CountDownTimer(timerValueInMilis, pollingFrequency) {
            public void onTick(long millisUntilFinished) {
                broadcastSender.sendBroadcast(TIMER_ACTION, BROADCAST_TIMER_VALUE, millisUntilFinished);
            }
            public void onFinish() {
                broadcastSender.sendBroadcast(TIMER_ACTION, BROADCAST_TIMER_VALUE, 0L);
                onDestroy();
            }
        };
    }

    public static void pauseTimer(){
        countDownTimer.cancel();
    }
}
