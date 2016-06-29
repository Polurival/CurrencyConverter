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

/**
 * Created by Polurival
 * on 19.06.2016.
 */
public abstract class CommonRateUpdater
        extends AsyncTask<Void, Void, Boolean> implements RateUpdater {

    protected Context appContext;
    protected EnumMap<CharCode, Currency> currencyMap;
    private RateUpdaterListener rateUpdaterListener;

    public CommonRateUpdater() {
        this.appContext = AppContext.getContext();
    }

    @Override
    public void setRateUpdaterListener(RateUpdaterListener rateUpdaterListener) {
        this.rateUpdaterListener = rateUpdaterListener;
    }

    @Override
    protected void onPreExecute() {
        currencyMap = new EnumMap<>(CharCode.class);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        Logger.logD(Logger.getTag(), "onPostExecute " + result.toString());

        if (result) {
            rateUpdaterListener.checkAsyncTaskStatusAndSetNewInstance();

            DBUpdaterTask dbUpdaterTask = new DBUpdaterTask();
            /*rateUpdaterListener.setOnBackPressedListener(dbUpdaterTask);*/
            dbUpdaterTask.setRateUpdaterListener(rateUpdaterListener);
            dbUpdaterTask.setCurrencyMap(currencyMap);
            dbUpdaterTask.execute();
        } else {
            Toaster.showCenterToast(appContext.getString(R.string.update_error));

            rateUpdaterListener.stopRefresh();
            rateUpdaterListener.setMenuState(null);
        }
    }
}
