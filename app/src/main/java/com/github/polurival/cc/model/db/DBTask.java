package com.github.polurival.cc.model.db;

import android.content.Context;
import android.os.AsyncTask;

import com.github.polurival.cc.AppContext;
import com.github.polurival.cc.RateUpdaterListener;

/**
 * Created by Polurival
 * on 21.06.2016.
 */
public abstract class DBTask extends AsyncTask<String, Void, Boolean> {

    protected Context appContext;
    protected RateUpdaterListener rateUpdaterListener;

    public void setRateUpdaterListener(RateUpdaterListener rateUpdaterListener) {
        this.rateUpdaterListener = rateUpdaterListener;
    }

    @Override
    protected void onPreExecute() {
        appContext = AppContext.getContext();
    }
}
