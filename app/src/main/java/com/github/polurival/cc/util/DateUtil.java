package com.github.polurival.cc.util;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;

/**
 * Created by Polurival
 * on 11.05.2016.
 */
public class DateUtil {

    private static DateTimeFormatter formatter =
            DateTimeFormat.forPattern("EEE, d MMM yyyy, HH:mm")
                    .withLocale(Locale.getDefault());

    public static LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }

    public static LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    public static String getCurrentDateTimeStr() {
        return getCurrentDateTime().toString(formatter);
    }

    public static String getDefaultDateTimeStr() {
        LocalDateTime localDate = new LocalDateTime(2016, 5, 29, 0, 0, 0);
        return localDate.toString(formatter);
    }
    public static long getDefaultDateTimeInSeconds() {
        LocalDateTime localDate = new LocalDateTime(2016, 5, 29, 0, 0, 0);
        return localDate.toDate().getTime();
    }

    public static LocalDateTime getUpDateTime(String upDateTime) {
        return formatter.parseLocalDateTime(upDateTime);
    }

    public static LocalDateTime getUpDateTime(long upDateTimeInSeconds) {
        return new LocalDateTime(upDateTimeInSeconds);
    }

    public static String getUpDateTimeStr(LocalDateTime upDateTime) {
        return upDateTime.toString(formatter);
    }

    public static String getUpDateTimeStr(long upDateTimeInSeconds) {
        LocalDateTime upDateTime = new LocalDateTime(upDateTimeInSeconds);
        return upDateTime.toString(formatter);
    }

    public static long getUpDateTimeInSeconds(LocalDateTime upDateTime) {
        return upDateTime.toDate().getTime();
    }

    public static boolean compareUpDateWithCurrentDate(LocalDateTime upDateTime) {
        LocalDate upDate = upDateTime.toLocalDate();
        LocalDate currentDate = getCurrentDate();

        return upDate.compareTo(currentDate) < 0;
    }
}
