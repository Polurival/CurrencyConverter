package com.github.polurival.cc.model.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.github.polurival.cc.R;
import com.github.polurival.cc.model.CharCode;
import com.github.polurival.cc.model.dto.Currency;
import com.github.polurival.cc.model.updater.RateUpdater;
import com.github.polurival.cc.util.DateUtil;
import com.github.polurival.cc.util.Logger;
import com.github.polurival.cc.util.Toaster;

import java.util.EnumMap;

public class DBUpdaterTask extends DBTask {

    private EnumMap<CharCode, Currency> currencyMap;

    public void setCurrencyMap(EnumMap<CharCode, Currency> currencyMap) {
        this.currencyMap = currencyMap;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        Logger.logD(Logger.getTag(), "doInBackground");

        try {
            RateUpdater rateUpdater = rateUpdaterListener.getRateUpdater();
            SQLiteDatabase db = DBHelper.getInstance(appContext).getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            db.beginTransaction();
            try {
                for (EnumMap.Entry<CharCode, Currency> entry : currencyMap.entrySet()) {

                    rateUpdater.fillContentValuesForUpdatingColumns(contentValues, entry.getValue());

                    db.update(DBHelper.TABLE_NAME,
                            contentValues,
                            DBHelper.COLUMN_NAME_CHAR_CODE + " = ?",
                            new String[]{entry.getKey().toString()});
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteException e) {
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        Logger.logD(Logger.getTag(), "onPostExecute " + result.toString());

        if (result) {
            rateUpdaterListener.stopRefresh();
            rateUpdaterListener.setMenuState(null);

            rateUpdaterListener.setUpDateTime(DateUtil.getCurrentDateTime());
            rateUpdaterListener.saveUpDateTimeProperty();

            rateUpdaterListener.readDataFromDB();
        } else {
            Toaster.showToast(appContext.getString(R.string.db_writing_error));
        }
    }
}
