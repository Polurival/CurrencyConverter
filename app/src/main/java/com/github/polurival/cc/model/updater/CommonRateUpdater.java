package com.github.polurival.cc.model.updater;

import android.content.Context;
import android.os.AsyncTask;

import com.github.polurival.cc.AppContext;
import com.github.polurival.cc.R;
import com.github.polurival.cc.RateUpdaterListener;
import com.github.polurival.cc.model.CharCode;
import com.github.polurival.cc.model.Currency;
import com.github.polurival.cc.model.db.DBUpdaterTask;
import com.github.polurival.cc.util.Logger;
import com.github.polurival.cc.util.Toaster;

import java.util.EnumMap;

abstract class CommonRateUpdater
        extends AsyncTask<Void, Void, Boolean> implements RateUpdater {

    Context appContext;
    EnumMap<CharCode, Currency> currencyMap;
    private RateUpdaterListener rateUpdaterListener;

    CommonRateUpdater() {
        this.appContext = AppContext.getContext();
    }

    @Override
    public void setRateUpdaterListener(RateUpdaterListener rateUpdaterListener) {
        this.rateUpdaterListener = rateUpdaterListener;
    }

    @Override
    protected void onPreExecute() {
        Logger.logD(Logger.getTag(), "onPreExecute");

        currencyMap = new EnumMap<>(CharCode.class);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        Logger.logD(Logger.getTag(), "onPostExecute " + result.toString());

        if (result) {
            rateUpdaterListener.checkAsyncTaskStatusAndSetNewInstance();

            DBUpdaterTask dbUpdaterTask = new DBUpdaterTask();
            dbUpdaterTask.setRateUpdaterListener(rateUpdaterListener);
            dbUpdaterTask.setCurrencyMap(currencyMap);
            dbUpdaterTask.execute();
        } else {
            Toaster.showBottomToast(appContext.getString(R.string.update_error));

            rateUpdaterListener.stopRefresh();
            rateUpdaterListener.setMenuState(null);
        }
    }
}
