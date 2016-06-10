package com.github.polurival.cc.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.widget.Toast;

import com.github.polurival.cc.AppContext;
import com.github.polurival.cc.R;
import com.github.polurival.cc.model.CBRateUpdaterTask;
import com.github.polurival.cc.model.CharCode;
import com.github.polurival.cc.model.Currency;
import com.github.polurival.cc.model.RateUpdater;
import com.github.polurival.cc.model.RateUpdaterListener;
import com.github.polurival.cc.util.DateUtil;

import java.util.EnumMap;

/**
 * Created by Polurival
 * on 29.05.2016.
 */
public class DBUpdaterTask extends AsyncTask<Void, Void, Boolean> {

    private Context appContext;
    private RateUpdaterListener rateUpdaterListener;
    private EnumMap<CharCode, Currency> currencyMap;

    public void setCurrencyMap(EnumMap<CharCode, Currency> currencyMap) {
        this.currencyMap = currencyMap;
    }

    public void setRateUpdaterListener(RateUpdaterListener rateUpdaterListener) {
        this.rateUpdaterListener = rateUpdaterListener;
    }

    @Override
    protected void onPreExecute() {
        appContext = AppContext.getContext();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            RateUpdater rateUpdater = rateUpdaterListener.getRateUpdater();
            SQLiteDatabase db = DBHelper.getInstance(appContext).getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            for (EnumMap.Entry<CharCode, Currency> entry : currencyMap.entrySet()) {
                if (rateUpdater instanceof CBRateUpdaterTask) {
                    contentValues.put(DBHelper.COLUMN_NAME_CB_RF_NOMINAL, entry.getValue().getNominal());
                    contentValues.put(DBHelper.COLUMN_NAME_CB_RF_VALUE, entry.getValue().getValue());
                } // TODO: 09.06.2016  add if else {...} for Yahoo
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
            Toast.makeText(appContext, appContext.getString(R.string.db_update_success),
                    Toast.LENGTH_SHORT)
                    .show();

            rateUpdaterListener.stopRefresh();
            rateUpdaterListener.setUpDateTime(DateUtil.getCurrentDateTime());
            rateUpdaterListener.saveDateProperties();
            rateUpdaterListener.readDataFromDB();
        } else {
            Toast.makeText(appContext, appContext.getString(R.string.db_writing_error),
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
