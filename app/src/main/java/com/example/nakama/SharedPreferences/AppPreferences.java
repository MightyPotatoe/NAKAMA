package com.example.nakama.SharedPreferences;

import android.content.SharedPreferences;

public class AppPreferences extends SharedPreferencesEditor{

    public static final String NAME = "APPLICATION_SETTINGS";

    private final String RING_TIME = "RING_TIME";
    private final String POLLING_FREQUENCY = "POLLING_FREQUENCY";

    public AppPreferences(SharedPreferences sharedPreferences) {
        super(sharedPreferences);
    }

    public int getRingTime(){
        return sharedPreferences.getInt(RING_TIME, 120000);
    }
    public void setRingTime(int value){
        putInt(RING_TIME, value);
    }

    public int getPollingFrequency(){
        return sharedPreferences.getInt(POLLING_FREQUENCY, 50);
    }
    public void setPollingFrequency(int value){
        putInt(POLLING_FREQUENCY, value);
    }
}