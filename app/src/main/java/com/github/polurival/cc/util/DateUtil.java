package com.github.polurival.cc.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Polurival
 * on 11.05.2016.
 */
public class DateUtil {

    private static DateFormat df =
            new SimpleDateFormat("EEE, d MMM yyyy, HH:mm", Locale.getDefault());

    public static Calendar getCurrentDateTime() {
        return Calendar.getInstance();
    }

    public static String getCurrentDateTimeStr() {
        return df.format(getCurrentDateTime().getTime());
    }

    public static Calendar getUpDateTime(String upDateTime) {
        Calendar calendar = getCurrentDateTime();
        try {
            calendar.setTime(df.parse(upDateTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    public static String getUpDateTimeStr(Calendar upDateTime) {
        return df.format(upDateTime.getTime());
    }
}
