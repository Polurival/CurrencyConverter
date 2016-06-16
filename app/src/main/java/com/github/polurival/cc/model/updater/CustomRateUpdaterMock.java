package com.github.polurival.cc.model.updater;

import android.os.AsyncTask;

import com.github.polurival.cc.AppContext;
import com.github.polurival.cc.R;
import com.github.polurival.cc.RateUpdaterListener;
import com.github.polurival.cc.model.CharCode;
import com.github.polurival.cc.model.Currency;

import java.util.EnumMap;

/**
 * Created by Polurival
 * on 01.06.2016.
 */
public class CustomRateUpdaterMock
        extends AsyncTask<Void, Void, EnumMap<CharCode, Currency>>
        implements RateUpdater {

    @Override
    public void setRateUpdaterListener(RateUpdaterListener rateUpdaterListener) {
        //do nothing
    }

    @Override
    public <T> void fillCurrencyMapFromSource(T doc) {
        //do nothing
    }

    @Override
    public String getDescription() {
        return AppContext.getContext().getString(R.string.custom);
    }

    @Override
    protected EnumMap<CharCode, Currency> doInBackground(Void... params) {
        return null;
    }
}
