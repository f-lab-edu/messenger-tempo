package com.messenger.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeConvertor {

    public static String convertTimestampMillis2String(long timestamp_ms) {
        return convertTimestampMillis2String(timestamp_ms, "yyyy-MM-dd kk:mm:ss.SSS");
    }

    public static String convertTimestampMillis2String(long timestamp_ms, String pattern) {
        Date date = new Date(timestamp_ms);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
    }

}
