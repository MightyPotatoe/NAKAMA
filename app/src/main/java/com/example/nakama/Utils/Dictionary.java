package com.example.nakama.Utils;

public abstract class Dictionary {
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
    }
}