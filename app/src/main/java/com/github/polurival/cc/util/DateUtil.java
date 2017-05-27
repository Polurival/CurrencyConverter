package com.github.polurival.cc.util;

import android.support.annotation.NonNull;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;

public class DateUtil {

    private static DateTimeFormatter formatter =
            DateTimeFormat.forPattern("EEE, d MMM yyyy, HH:mm")
                    .withLocale(Locale.getDefault());

    @NonNull
    public static LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }

    public static String getUpDateTimeStr(LocalDateTime upDateTime) {
        return upDateTime.toString(formatter);
    }

    public static long getDefaultDateTimeInSeconds() {
        LocalDateTime localDate = new LocalDateTime(2016, 6, 11, 0, 0, 0);
        return localDate.toDate().getTime();
    }

    @NonNull
    public static LocalDateTime getUpDateTime(long upDateTimeInSeconds) {
        return new LocalDateTime(upDateTimeInSeconds);
    }

    public static long getUpDateTimeInSeconds(LocalDateTime upDateTime) {
        return upDateTime.toDate().getTime();
    }

    public static boolean isUpDateTimeLessThenCurrentDateTime(LocalDateTime upDateTime) {
        LocalDate upDate = upDateTime.toLocalDate();
        return upDate.compareTo(LocalDate.now()) < 0;
    }
}
