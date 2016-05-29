package com.github.polurival.cc.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.widget.Toast;

import com.github.polurival.cc.AppContext;
import com.github.polurival.cc.R;

import java.util.EnumMap;

/**
 * Created by Polurival
 * on 29.05.2016.
 */
public class DBReaderTask extends AsyncTask<String, Void, EnumMap<CharCode, Currency>> {

    private RateUpdaterListener rateUpdaterListener;
    private SQLiteOpenHelper dbHelper;
    private EnumMap<CharCode, Currency> currencyMap;

    public void setRateUpdaterListener(RateUpdaterListener rateUpdaterListener) {
        this.rateUpdaterListener = rateUpdaterListener;
    }

    @Override
    protected void onPreExecute() {
        dbHelper = new DBHelper(AppContext.getContext());
        currencyMap = new EnumMap<>(CharCode.class);
    }

    @Override
    protected EnumMap<CharCode, Currency> doInBackground(String... params) {
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query(DBHelper.TABLE_NAME,
                    new String[]{DBHelper.COLUMN_NAME_CHAR_CODE,
                            DBHelper.COLUMN_NAME_NOMINAL,
                            params[1],
                            DBHelper.COLUMN_NAME_NAME_RESOURCE_ID,
                            DBHelper.COLUMN_NAME_FLAG_RESOURCE_ID},
                    params[0] + " = 1",
                    null, null, null, null);

            while (cursor.moveToNext()) {
                currencyMap.put(CharCode.valueOf(cursor.getString(0)),
                        new Currency(cursor.getInt(1),
                                cursor.getDouble(2),
                                cursor.getInt(3),
                                cursor.getInt(4)));
            }

            cursor.close();
            db.close();
        } catch (SQLiteException e) {
            Toast.makeText(AppContext.getContext(),
                    AppContext.getContext().getString(R.string.database_error),
                    Toast.LENGTH_LONG).show();
        }
        return currencyMap;
    }

    @Override
    protected void onPostExecute(EnumMap<CharCode, Currency> result) {
        super.onPostExecute(result);

        rateUpdaterListener.setCurrencyMap(result);
        rateUpdaterListener.initSpinners();
        rateUpdaterListener.loadSpinnerProperties();
    }
}
