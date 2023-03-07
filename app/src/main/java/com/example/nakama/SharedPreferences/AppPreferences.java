package com.example.nakama.SharedPreferences;

import android.content.SharedPreferences;

public class AppPreferences extends SharedPreferencesEditor{

    public static final String NAME = "APPLICATION_SETTINGS";

    private final String RING_TIME = "RING_TIME";
    private final String POLLING_FREQUENCY = "POLLING_FREQUENCY";
    private final String USER_ID = "USER_ID";
    private final String ACTIVE_RING = "RING";
    private final String DIFFICULTY = "DIFFICULTY";
    private final String FALSE_ALARMS_LIMIT = "FALSE_ALARMS_LIMIT";
    private final String SAMPLES_IN_RUN = "SAMPLES_IN_RUN";

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

    public void setUserId(int id){
        putInt(USER_ID, id);
    }
    public int getUserId(){
        return sharedPreferences.getInt(USER_ID, -1);
    }

    public void setActiveRing(String activeRing){
        putString(ACTIVE_RING, activeRing);
    }

    public String getActiveRing(){
        return sharedPreferences.getString(ACTIVE_RING, null);
    }

    public void setDifficulty(String difficulty){
        putString(DIFFICULTY, difficulty);
    }

    public String getDifficulty(){
        return sharedPreferences.getString(DIFFICULTY, null);
    }

    public void setFalseAlarmsLimit(int limit){
        putInt(FALSE_ALARMS_LIMIT, limit);
    }

    public int getFalseAlarmsLimit(){
        return sharedPreferences.getInt(FALSE_ALARMS_LIMIT, 1);
    }

    public void setSamplesInRun(int samples){
        putInt(SAMPLES_IN_RUN, samples);
    }

    public int getSamplesInRun(){
        return sharedPreferences.getInt(SAMPLES_IN_RUN, 1);
    }
}