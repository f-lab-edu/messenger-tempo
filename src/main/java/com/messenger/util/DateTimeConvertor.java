package com.messenger.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeConvertor {

    private DateTimeConvertor() {}

    public static String convertTimestampMillis2String(long timestampMs) {
        return convertTimestampMillis2String(timestampMs, "yyyy-MM-dd kk:mm:ss.SSS");
    }

    public static String convertTimestampMillis2String(long timestampMs, String pattern) {
        Date date = new Date(timestampMs);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
    }

    public static String convertTimestamp2String(Timestamp timestamp) {
        long timestampMs = timestamp.getTime();
        return convertTimestampMillis2String(timestampMs);
    }

    public static String convertTimestamp2String(Timestamp timestamp, String pattern) {
        long timestampMs = timestamp.getTime();
        return convertTimestampMillis2String(timestampMs, pattern);
    }
}
