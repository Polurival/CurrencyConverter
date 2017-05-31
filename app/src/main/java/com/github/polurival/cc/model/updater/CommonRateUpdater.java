package com.github.polurival.cc.model.updater;

import android.content.Context;
import android.os.AsyncTask;

import com.github.polurival.cc.AppContext;
import com.github.polurival.cc.R;
import com.github.polurival.cc.RateUpdaterListener;
import com.github.polurival.cc.model.CharCode;
import com.github.polurival.cc.model.dto.Currency;
import com.github.polurival.cc.model.db.DBUpdaterTask;
import com.github.polurival.cc.util.Logger;
import com.github.polurival.cc.util.Toaster;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.EnumMap;

public abstract class CommonRateUpdater extends AsyncTask<Void, Void, Boolean> implements RateUpdater {

    Context appContext;
    EnumMap<CharCode, Currency> currencyMap;
    protected RateUpdaterListener rateUpdaterListener;

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
        Logger.logD(Logger.getTag(), "onPostExecute " + result);

        if (result) {
            rateUpdaterListener.checkAsyncTaskStatusAndSetNewInstance();

            DBUpdaterTask dbUpdaterTask = new DBUpdaterTask();
            dbUpdaterTask.setRateUpdaterListener(rateUpdaterListener);
            dbUpdaterTask.setCurrencyMap(currencyMap);
            dbUpdaterTask.execute();
        } else {
            Toaster.showToast(appContext.getString(R.string.update_error));

            rateUpdaterListener.stopRefresh();
            rateUpdaterListener.setMenuState(null);
        }
    }

    @Override
    public void execute() {
        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public InputStream getDataInputStream(String url) throws IOException {
        Logger.logD(Logger.getTag(), "download data from: " + url);

        final URL sourceUrl = new URL(url);
        URLConnection connection = sourceUrl.openConnection();
        return connection.getInputStream();
    }
}
