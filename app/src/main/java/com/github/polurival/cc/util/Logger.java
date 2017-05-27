package com.github.polurival.cc.util;

import android.util.Log;

import com.github.polurival.cc.BuildConfig;

public class Logger {

    public static void logD(String log) {
        if (BuildConfig.DEBUG) {
            Log.d(Constants.LOG, log);
        }
    }

    public static void logD(final String TAG, String log) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, log);
        }
    }

    /**
     * Makes TAG looks like 'MainActivity.java:136'
     * <p>See <a href="http://stackoverflow.com/a/29107315">source</a></p>
     */
    public static String getTag() {
        String tag = "";
        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        for (int i = 0; i < ste.length; i++) {
            if (ste[i].getMethodName().equals("getTag")) {
                tag = "(" + ste[i + 1].getFileName() + ":" + ste[i + 1].getLineNumber() + ")";
            }
        }
        return tag;
    }
}
