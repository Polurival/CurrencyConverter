package com.github.polurival.cc;

import android.app.Application;
import android.content.Context;

import com.github.polurival.cc.model.db.DBHelper;

import net.danlew.android.joda.JodaTimeAndroid;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class AppContext extends Application {

    private static Context appContext;
    private static boolean isActivityVisible;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;

        initDatabase();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/Roboto-ThinItalic.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        JodaTimeAndroid.init(this);
    }

    private void initDatabase() {
        DBHelper.getInstance(this);
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
