package com.github.polurival.cc;

import android.app.Application;
import android.content.Context;

import net.danlew.android.joda.JodaTimeAndroid;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Polurival
 * on 09.05.2016.
 */
public class AppContext extends Application {

    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;

        //https://github.com/chrisjenx/Calligraphy#inject-into-context
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("font/Roboto-Regular.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        //https://github.com/dlew/joda-time-android
        JodaTimeAndroid.init(this);
    }

    public static Context getContext() {
        return appContext;
    }
}
