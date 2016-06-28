package com.github.polurival.cc.model.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.github.polurival.cc.R;
import com.github.polurival.cc.model.updater.CBRateUpdaterTask;
import com.github.polurival.cc.model.CharCode;
import com.github.polurival.cc.model.Currency;
import com.github.polurival.cc.model.updater.RateUpdater;
import com.github.polurival.cc.model.updater.YahooRateUpdaterTask;
import com.github.polurival.cc.util.DateUtil;
import com.github.polurival.cc.util.Toaster;

import java.util.EnumMap;

/**
 * Created by Polurival
 * on 29.05.2016.
 */
public class DBUpdaterTask extends DBTask {

    private EnumMap<CharCode, Currency> currencyMap;

    public void setCurrencyMap(EnumMap<CharCode, Currency> currencyMap) {
        this.currencyMap = currencyMap;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            RateUpdater rateUpdater = rateUpdaterListener.getRateUpdater();
            SQLiteDatabase db = DBHelper.getInstance(appContext).getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            for (EnumMap.Entry<CharCode, Currency> entry : currencyMap.entrySet()) {
                int nominal = entry.getValue().getNominal();
                double rate = entry.getValue().getRate();

                if (rateUpdater instanceof CBRateUpdaterTask) {
                    contentValues.put(DBHelper.COLUMN_NAME_CB_RF_NOMINAL, nominal);
                    contentValues.put(DBHelper.COLUMN_NAME_CB_RF_RATE, rate);
                } else if (rateUpdater instanceof YahooRateUpdaterTask) {
                    contentValues.put(DBHelper.COLUMN_NAME_YAHOO_NOMINAL, nominal);
                    contentValues.put(DBHelper.COLUMN_NAME_YAHOO_RATE, rate);
                }

                db.update(DBHelper.TABLE_NAME,
                        contentValues,
                        DBHelper.COLUMN_NAME_CHAR_CODE + " = ?",
                        new String[]{entry.getKey().toString()});
            }
            db.close();
        } catch (SQLiteException e) {
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            Toaster.showCenterToast(appContext.getString(R.string.db_update_success));

            rateUpdaterListener.stopRefresh();
            rateUpdaterListener.setMenuState(null);

            rateUpdaterListener.setUpDateTime(DateUtil.getCurrentDateTime());
            rateUpdaterListener.saveDateProperties();

            rateUpdaterListener.readDataFromDB();
        } else {
            Toaster.showCenterToast(appContext.getString(R.string.db_writing_error));
        }

        rateUpdaterListener.setOnBackPressedListener(null);
    }
}
