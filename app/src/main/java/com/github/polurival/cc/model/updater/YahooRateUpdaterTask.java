package com.github.polurival.cc.model.updater;

import android.content.ContentValues;
import android.content.Context;

import com.github.polurival.cc.R;
import com.github.polurival.cc.model.CharCode;
import com.github.polurival.cc.model.Currency;
import com.github.polurival.cc.model.db.DBHelper;
import com.github.polurival.cc.model.db.DBOperations;
import com.github.polurival.cc.model.db.DBReaderTask;
import com.github.polurival.cc.model.dto.SpinnersPositions;
import com.github.polurival.cc.util.AppPreferences;
import com.github.polurival.cc.util.CurrencyUtil;
import com.github.polurival.cc.util.Logger;

import org.joda.time.LocalDateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class YahooRateUpdaterTask extends CommonRateUpdater {

    /**
     * See <a href="http://finance.yahoo.com/webservice/v1/symbols/allcurrencies/quote?format=json">source</a>
     */
    private static final String YAHOO_URL =
            "http://finance.yahoo.com/webservice/v1/symbols/allcurrencies/quote?format=json";
    private static final String LIST_OBJECT = "list";
    private static final String RESOURCES_ARRAY = "resources";
    private static final String RESOURCE_OBJECT = "resource";
    private static final String FIELDS_OBJECT = "fields";
    private static final String SYMBOL_KEY = "symbol";
    private static final String PRICE_KEY = "price";

    @Override
    protected Boolean doInBackground(Void... params) {
        Logger.logD(Logger.getTag(), "doInBackground");

        try {
            InputStream inputStream = downloadData(YAHOO_URL);
            if (inputStream != null) {
                String json = parseDataToJsonString(inputStream);
                inputStream.close();
                if (json != null) {
                    Logger.logD(Logger.getTag(), json);
                    return fillCurrencyMap(json);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Logger.logD(Logger.getTag(), "can't connect or read from source!");
        }
        return false;
    }

    private String parseDataToJsonString(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, length);
        }
        return byteArrayOutputStream.toString("UTF-8");
    }

    private boolean fillCurrencyMap(String json) {
        Logger.logD(Logger.getTag(), "fillCurrencyMap");
        try {
            JSONObject yahooAllCurrencies = new JSONObject(json);
            JSONObject list = yahooAllCurrencies.getJSONObject(LIST_OBJECT);
            JSONArray resources = list.getJSONArray(RESOURCES_ARRAY);

            for (int i = 0; i < resources.length(); i++) {
                JSONObject resource =
                        resources.getJSONObject(i).getJSONObject(RESOURCE_OBJECT);
                JSONObject fields = resource.getJSONObject(FIELDS_OBJECT);

                String code;
                double rate;

                if (fields.isNull(SYMBOL_KEY) || fields.isNull(PRICE_KEY)) {
                    continue;
                } else {
                    code = fields.getString(SYMBOL_KEY).substring(0, 3);
                    rate = fields.getDouble(PRICE_KEY);
                }

                CharCode charCode;
                try {
                    charCode = CharCode.valueOf(code);
                } catch (IllegalArgumentException e) {
                    // ignore unsupported currencies
                    continue;
                }

                final Currency currency = CurrencyUtil.getCurrency(rate);
                currencyMap.put(charCode, currency);
            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            Logger.logD(Logger.getTag(), "can't parse - changes in source! handle it");
            return false;
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

    @Override
    public void fillContentValuesForUpdatingColumns(ContentValues contentValues, Currency currency) {
        DBOperations.fillContentValuesForUpdatingYahooColumns(contentValues, currency);
    }
}
