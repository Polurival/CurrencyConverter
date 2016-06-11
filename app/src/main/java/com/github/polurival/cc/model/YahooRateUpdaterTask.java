package com.github.polurival.cc.model;

import android.os.AsyncTask;

/**
 * Created by Polurival
 * on 11.06.2016.
 */
public class YahooRateUpdaterTask extends AsyncTask<Void, Void, Boolean> implements RateUpdater {

    @Override
    public void setRateUpdaterListener(RateUpdaterListener rateUpdaterListener) {

    }

    @Override
    protected Boolean doInBackground(Void... params) {
        return null;
    }

    @Override
    public <T> void fillCurrencyMapFromSource(T doc) {

    }

    @Override
    public String getDescription() {
        return null;
    }
}
