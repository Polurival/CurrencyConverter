package com.polurival.cuco.util;

import com.polurival.cuco.MainActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Polurival
 * on 11.05.2016.
 */
public class DateUtil {
    public static String getCurrentDateTime() {
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm", Locale.getDefault());
        return MainActivity.getInstance().getRateUpdater().getName()
                + df.format(Calendar.getInstance().getTime());
    }
}
