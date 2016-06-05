package com.github.polurival.cc.model.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
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
public class DBUpdaterTask extends AsyncTask<Void, Void, EnumMap<CharCode, Currency>> {

    private SQLiteOpenHelper dbHelper;
    private RateUpdaterListener rateUpdaterListener;
    private ContentValues contentValues;
    private EnumMap<CharCode, Currency> currencyMap;
    private RateUpdater rateUpdater;

    public void setCurrencyMap(EnumMap<CharCode, Currency> currencyMap) {
        this.currencyMap = currencyMap;
    }

    public void setRateUpdaterListener(RateUpdaterListener rateUpdaterListener) {
        this.rateUpdaterListener = rateUpdaterListener;
    }

    @Override
    protected void onPreExecute() {
        dbHelper = DBHelper.getInstance(AppContext.getContext());
        contentValues = new ContentValues();
        rateUpdater = rateUpdaterListener.getRateUpdater();
    }

    @Override
    protected EnumMap<CharCode, Currency> doInBackground(Void... params) {
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            for (EnumMap.Entry<CharCode, Currency> entry : currencyMap.entrySet()) {
                contentValues.put(DBHelper.COLUMN_NAME_NOMINAL, entry.getValue().getNominal());
                if (rateUpdater instanceof CBRateUpdaterTask) {
                    contentValues.put(DBHelper.COLUMN_NAME_VALUE, entry.getValue().getValue());
                }
                db.update(DBHelper.TABLE_NAME,
                        contentValues,
                        DBHelper.COLUMN_NAME_CHAR_CODE + " = ?",
                        new String[]{entry.getKey().toString()});
            }
            db.close();
        } catch (SQLiteException e) {
            Toast.makeText(AppContext.getContext(),
                    AppContext.getContext().getString(R.string.db_error),
                    Toast.LENGTH_LONG).show();
        }
        return currencyMap;
    }

    @Override
    protected void onPostExecute(EnumMap<CharCode, Currency> result) {
        super.onPostExecute(result);
        if (currencyMap.size() != 0) {
            Toast.makeText(AppContext.getContext(),
                    AppContext.getContext().getString(R.string.db_update_success),
                    Toast.LENGTH_LONG).show();
        }

        rateUpdaterListener.stopRefresh();
        rateUpdaterListener.setUpDateTime(DateUtil.getCurrentDateTime());
        rateUpdaterListener.saveDateProperties();
        rateUpdaterListener.readDataFromDB();

    }
}
