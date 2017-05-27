package com.github.polurival.cc.model.updater;

import android.content.Context;
import android.os.AsyncTask;

import com.github.polurival.cc.R;
import com.github.polurival.cc.model.CharCode;
import com.github.polurival.cc.model.Currency;
import com.github.polurival.cc.model.db.DBHelper;
import com.github.polurival.cc.model.db.DBReaderTask;
import com.github.polurival.cc.model.dto.SpinnersPositions;
import com.github.polurival.cc.util.AppPreferences;
import com.github.polurival.cc.util.Constants;
import com.github.polurival.cc.util.Logger;

import org.apache.commons.io.IOUtils;
import org.joda.time.LocalDateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.nio.charset.Charset;

public class YahooRateUpdaterTask extends CommonRateUpdater {

    @Override
    protected Boolean doInBackground(Void... params) {
        Logger.logD(Logger.getTag(), "doInBackground");

        try {
            String jsonStr =
                    IOUtils.toString(new URL(Constants.YAHOO_URL), Charset.forName("UTF-8"));

            if (jsonStr != null) {
                fillCurrencyMapFromSource(jsonStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.logD(Logger.getTag(), "changes in source! handle it");
            return false;
        }
        return true;
    }

    @Override
    public void execute() {
        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public <T> void fillCurrencyMapFromSource(T doc) throws JSONException {
        Logger.logD(Logger.getTag(), "fillCurrencyMapFromSource");

        JSONObject yahooAllCurrencies = new JSONObject((String) doc);
        JSONObject list = yahooAllCurrencies.getJSONObject(Constants.LIST_OBJECT);
        JSONArray resources = list.getJSONArray(Constants.RESOURCES_ARRAY);

        for (int i = 0; i < resources.length(); i++) {
            JSONObject resource =
                    resources.getJSONObject(i).getJSONObject(Constants.RESOURCE_OBJECT);
            JSONObject fields = resource.getJSONObject(Constants.FIELDS_OBJECT);

            String code;
            double rate;

            if (fields.isNull(Constants.SYMBOL_KEY) || fields.isNull(Constants.PRICE_KEY)) {
                continue;
            } else {
                code = fields.getString(Constants.SYMBOL_KEY).substring(0, 3);
                rate = fields.getDouble(Constants.PRICE_KEY);
            }

            CharCode charCode;
            try {
                charCode = CharCode.valueOf(code);
            } catch (IllegalArgumentException e) {
                continue;
            }

            int nominal = 1;
            if (rate < 1) {
                int j = 0;
                while (rate < 1) {
                    rate *= 10;
                    j++;
                }
                nominal = (int) Math.pow(10, j);
            }

            currencyMap.put(charCode, new Currency(nominal, rate));
        }

    }

    @Override
    public String getDescription() {
        return appContext.getString(R.string.yahoo);
    }

    @Override
    public void saveSelectedCurrencySpinnersPositions(Context context, int fromSpinnerSelectedPos, int toSpinnerSelectedPos) {
        AppPreferences.saveMainActivityYahooRateUpdaterSpinnersPositions(context, fromSpinnerSelectedPos, toSpinnerSelectedPos);
    }

    @Override
    public void saveUpDateTime(Context context, LocalDateTime upDateTime) {
        AppPreferences.saveYahooRateUpdaterUpDateTime(context, upDateTime);
    }

    @Override
    public void readDataFromDB(DBReaderTask dbReaderTask) {
        dbReaderTask.execute(DBHelper.COLUMN_NAME_YAHOO_SOURCE,
                DBHelper.COLUMN_NAME_YAHOO_NOMINAL,
                DBHelper.COLUMN_NAME_YAHOO_RATE);
    }

    @Override
    public LocalDateTime loadUpDateTime(Context context) {
        return AppPreferences.loadYahooRateUpdaterUpDateTime(context);
    }

    @Override
    public int getDecimalScale() {
        return 6;
    }

    @Override
    public SpinnersPositions loadSpinnersPositions(Context context) {
        return AppPreferences.loadMainActivityYahooRateUpdaterSpinnersPositions(context);
    }
}
