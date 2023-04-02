package com.example.nakama.Utils;

public abstract class Dictionary {

    public static boolean DEBUG_MODE = false;
    public static class Difficulty{
        public static final class Basic{
            public static final String NAME = "Podstawowy";
            public static final int ATTEMPT_TIME = 120000;
            public static final int SAMPLES_TO_FIND = 1;
            public static final int FALSE_ALARMS_LIMIT = 1;
        }
        public static final class Advanced{
            public static final String NAME = "Zaawansowany";
            public static final int ATTEMPT_TIME = 240000;
            public static final int SAMPLES_TO_FIND = 2;
            public static final int FALSE_ALARMS_LIMIT = 2;
        }
    }

    public static class Rings{
        public static final String RING_1 = "Ring 1";
        public static final String RING_2 = "Ring 2";
        public static final String RING_3 = "Ring 3";
        public static final String RING_4 = "Ring 4";

        public static final String[] ringsList = {RING_1, RING_2, RING_3, RING_4};
    }

    public static int getAttemptTime(String difficulty){
        if(difficulty.equals(Difficulty.Basic.NAME)){
            if(Dictionary.DEBUG_MODE){
                return 5000;
            }
            return Difficulty.Basic.ATTEMPT_TIME;
        }
        if(Dictionary.DEBUG_MODE){
            return 7000;
        }
        return Difficulty.Advanced.ATTEMPT_TIME;
    }

    public static int getFalseAlarmsLimit(String difficulty){
        if(difficulty.equals(Difficulty.Basic.NAME)){
            return Difficulty.Basic.FALSE_ALARMS_LIMIT;
        }
        return Difficulty.Advanced.FALSE_ALARMS_LIMIT;
    }

    public static int getSamplesOnRing(String difficulty){
        if(difficulty.equals(Difficulty.Basic.NAME)){
            return Difficulty.Basic.SAMPLES_TO_FIND;
        }
        return Difficulty.Advanced.SAMPLES_TO_FIND;
    }
}