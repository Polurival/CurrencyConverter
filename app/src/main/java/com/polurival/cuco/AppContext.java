package com.polurival.cuco;

import android.app.Application;
import android.content.Context;

/**
 * Created by Polurival
 * on 09.05.2016.
 */
public class AppContext extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }
}
