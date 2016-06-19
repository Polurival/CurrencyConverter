package com.github.polurival.cc.model.updater;

import com.github.polurival.cc.R;
import com.github.polurival.cc.RateUpdaterListener;

/**
 * Created by Polurival
 * on 01.06.2016.
 */
public class CustomRateUpdaterMock extends CommonRateUpdater {

    @Override
    public void setRateUpdaterListener(RateUpdaterListener rateUpdaterListener) {
        //do nothing
    }
    @Override
    protected void onPreExecute() {
        //do nothing
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        return null;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        //do nothing
    }

    @Override
    public <T> void fillCurrencyMapFromSource(T doc) {
        //do nothing
    }

    @Override
    public String getDescription() {
        return appContext.getString(R.string.custom);
    }
}
