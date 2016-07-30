package com.github.polurival.cc;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import net.danlew.android.joda.JodaTimeAndroid;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Polurival
 * on 09.05.2016.
 */
public class AppContext extends Application {

    private static Context appContext;
    private static boolean isActivityVisible;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("font/Roboto-Regular.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        JodaTimeAndroid.init(this);
    }

    public static Context getContext() {
        return appContext;
    }

    public static boolean isActivityVisible() {
        return isActivityVisible;
    }

    public static void activityResumed() {
        isActivityVisible = true;
    }

    public static void activityPaused() {
        isActivityVisible = false;
    }
}
