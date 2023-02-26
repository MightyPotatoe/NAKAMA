package com.example.nakama.Utils;

public class Converter {
    private Converter() {}
    public static int getMillisFromString(String value){
        String[] timerTextSplit = value.split(":");
        int timerInMillis = Integer.parseInt(timerTextSplit[0]) * 60 * 1000;
        timerInMillis += Integer.parseInt(timerTextSplit[1]) * 1000;
        timerInMillis += Integer.parseInt(timerTextSplit[2]) * 10;
        return timerInMillis;
    }
}
