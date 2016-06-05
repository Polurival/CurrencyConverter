package com.github.polurival.cc.util;

import android.util.Log;

import com.github.polurival.cc.BuildConfig;

/**
 * Created by Polurival
 * on 01.06.2016.
 */
public class Logger {

    public static void logD(String log) {
        if (BuildConfig.DEBUG) {
            Log.d(Constants.LOG, log);
        }
    }

    public static void logD(String className, String log) {
        if (BuildConfig.DEBUG) {
            Log.d(className, log);
        }
    }
}
