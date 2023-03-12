package com.example.nakama.SharedPreferences;

import android.content.SharedPreferences;

public class AppPreferences extends SharedPreferencesEditor{

    public static final String NAME = "APPLICATION_SETTINGS";
    private final String POLLING_FREQUENCY = "POLLING_FREQUENCY";
    private final String USER_ID = "USER_ID";
    private final String ACTIVE_RING = "RING";
    private final String DIFFICULTY = "DIFFICULTY";

    public AppPreferences(SharedPreferences sharedPreferences) {
        super(sharedPreferences);
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

    public void clearPreferences(){
        setUserId(-1);
        setDifficulty("");
        setActiveRing("");
    }
}