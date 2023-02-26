package com.example.nakama.Utils;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Converter {
    private Converter() {}
    public static int stringToMillis(String value){
        String[] timerTextSplit = value.split(":");
        int timerInMillis = Integer.parseInt(timerTextSplit[0]) * 60 * 1000;
        timerInMillis += Integer.parseInt(timerTextSplit[1]) * 1000;
        timerInMillis += Integer.parseInt(timerTextSplit[2]) * 10;
        return timerInMillis;
    }

    public static String millisToString(long timeInMillis){
        String timeLeft = DurationFormatUtils.formatDuration(timeInMillis, "mm:ss:SS", true);
        Pattern pattern = Pattern.compile("\\d{2}:\\d{2}:\\d{2}");
        Matcher matcher = pattern.matcher(timeLeft);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return timeLeft;
    }
}
