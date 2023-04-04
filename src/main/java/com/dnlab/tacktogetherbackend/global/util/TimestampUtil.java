package com.dnlab.tacktogetherbackend.global.util;

import java.sql.Timestamp;

public class TimestampUtil {
    private TimestampUtil() {}

    public static Timestamp getCurrentTime() {
        return new Timestamp(System.currentTimeMillis());
    }
}
