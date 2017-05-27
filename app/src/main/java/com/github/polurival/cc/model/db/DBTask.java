package com.github.polurival.cc.model.db;

import android.content.Context;
import android.os.AsyncTask;

import com.github.polurival.cc.AppContext;
import com.github.polurival.cc.RateUpdaterListener;

public abstract class DBTask extends AsyncTask<String, Void, Boolean> {

    Context appContext;
    RateUpdaterListener rateUpdaterListener;

    public void setRateUpdaterListener(RateUpdaterListener rateUpdaterListener) {
        this.rateUpdaterListener = rateUpdaterListener;
    }

    @Override
    protected void onPreExecute() {
        appContext = AppContext.getContext();
    }
}
