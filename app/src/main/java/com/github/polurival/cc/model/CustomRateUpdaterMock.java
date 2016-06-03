package com.github.polurival.cc.model;

import android.os.AsyncTask;

import com.github.polurival.cc.AppContext;
import com.github.polurival.cc.R;

import org.w3c.dom.Document;

import java.util.EnumMap;

/**
 * Created by Polurival
 * on 01.06.2016.
 */
public class CustomRateUpdaterMock
        extends AsyncTask<Void, Void, EnumMap<CharCode, Currency>>
        implements RateUpdater {

    private RateUpdaterListener rateUpdaterListener;

    @Override
    public void setRateUpdaterListener(RateUpdaterListener rateUpdaterListener) {
        this.rateUpdaterListener = rateUpdaterListener;
    }

    @Override
    public void fillCurrencyMap(Document doc) {
        //do nothing
    }

    @Override
    public EnumMap<CharCode, Currency> getCurrencyMap() {
        return null;
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
