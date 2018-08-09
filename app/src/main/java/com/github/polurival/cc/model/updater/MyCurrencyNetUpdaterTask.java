package com.github.polurival.cc.model.updater;

import android.content.ContentValues;
import android.content.Context;

import com.github.polurival.cc.R;
import com.github.polurival.cc.model.CharCode;
import com.github.polurival.cc.model.db.DBOperations;
import com.github.polurival.cc.model.db.DBReaderTask;
import com.github.polurival.cc.model.dto.CurrenciesRelations;
import com.github.polurival.cc.model.dto.Currency;
import com.github.polurival.cc.model.dto.SpinnersPositions;
import com.github.polurival.cc.util.AppPreferences;
import com.github.polurival.cc.util.Logger;

import org.joda.time.LocalDateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

/**
 * @author Polurival on 08.08.2018.
 */
public class MyCurrencyNetUpdaterTask extends CommonRateUpdater {

    /**
     * @see <a href="https://www.mycurrency.net/page/Free+Currency+Converter+API">source</a>
     */
    private static final String MY_CURRENCY_NET_URL = "https://www.mycurrency.net/service/rates";

    private static final String CURRENCY_CODE_KEY = "currency_code";
    private static final String RATE_KEY = "rate";

    @Override
    protected Boolean doInBackground(Void... voids) {
        Logger.logD(Logger.getTag(), "doInBackground");

        try {
            InputStream inputStream = getDataInputStream(MY_CURRENCY_NET_URL);
            if (inputStream != null) {
                String json = parseDataToJsonString(inputStream);
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

    @Override
    public String getDescription() {
        return appContext.getString(R.string.my_currency_net);
    }

    @Override
    public void saveSelectedCurrencySpinnersPositions(Context context, int fromSpinnerSelectedPos, int toSpinnerSelectedPos) {
        AppPreferences.saveMainActivityMyCurrencyNetRateUpdaterSpinnersPositions(context, fromSpinnerSelectedPos, toSpinnerSelectedPos);
    }

    @Override
    public void saveUpDateTime(Context context, LocalDateTime upDateTime) {
        AppPreferences.saveMyCurrencyNetRateUpdaterUpDateTime(context, upDateTime);
    }

    @Override
    public void readDataFromDB(DBReaderTask dbReaderTask) {
        dbReaderTask.execute(DBOperations.getColumnsForReadForMyCurrencyNetSource());
    }

    @Override
    public LocalDateTime loadUpDateTime(Context context) {
        return AppPreferences.loadMyCurrencyNetRateUpdaterUpDateTime(context);
    }

    @Override
    public int getDecimalScale() {
        return 6;
    }

    @Override
    public SpinnersPositions loadSpinnersPositions(Context context) {
        return AppPreferences.loadMainActivityMyCurrencyNetRateUpdaterSpinnersPositions(context);
    }

    @Override
    public void fillContentValuesForUpdatingColumns(ContentValues contentValues, Currency currency) {
        DBOperations.fillContentValuesForUpdatingMyCurrencyNetColumns(contentValues, currency);
    }

    @Override
    public BigDecimal calculateConversionResult(CurrenciesRelations currenciesRelations, BigDecimal enteredAmountOfMoney) {
        return currenciesRelations.calculateConversionResultByDefault(enteredAmountOfMoney);
    }

    @Override
    public boolean isUpdateFromNetworkUnavailable() {
        return false;
    }

    private String parseDataToJsonString(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, length);
        }
        inputStream.close();
        return byteArrayOutputStream.toString("UTF-8");
    }

    private boolean fillCurrencyMap(String json) {
        Logger.logD(Logger.getTag(), "fillCurrencyMap");
        try {
            JSONArray resources = new JSONArray(json);
            for (int i = 0; i < resources.length(); i++) {
                JSONObject resource = resources.getJSONObject(i);

                String currencyCode;
                double rate;

                if (resource.isNull(CURRENCY_CODE_KEY) || resource.isNull(RATE_KEY)) {
                    continue;
                } else {
                    currencyCode = resource.getString(CURRENCY_CODE_KEY).substring(0, 3);
                    rate = resource.getDouble(RATE_KEY);
                }

                CharCode charCode;
                try {
                    charCode = CharCode.valueOf(currencyCode);
                } catch (IllegalArgumentException e) {
                    // ignore unsupported currencies
                    continue;
                }

                final Currency currency = getNormalizedCurrency(rate);
                currencyMap.put(charCode, currency);
            }
            return true;

        } catch (JSONException e) {
            e.printStackTrace();
            Logger.logD(Logger.getTag(), "can't parse - changes in source! handle it");
            return false;
        }
    }
}
