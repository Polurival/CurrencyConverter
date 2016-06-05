package com.github.polurival.cc.model.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.widget.Toast;

import com.github.polurival.cc.AppContext;
import com.github.polurival.cc.R;
import com.github.polurival.cc.model.CharCode;
import com.github.polurival.cc.model.Currency;
import com.github.polurival.cc.model.RateUpdaterListener;

import java.util.EnumMap;

/**
 * Created by Polurival
 * on 29.05.2016.
 */
public class DBReaderTask extends AsyncTask<String, Void, EnumMap<CharCode, Currency>> {

    private RateUpdaterListener rateUpdaterListener;
    private SQLiteOpenHelper dbHelper;
    private EnumMap<CharCode, Currency> currencyMap;

    private SQLiteDatabase db;
    private Cursor cursor;

    public void setRateUpdaterListener(RateUpdaterListener rateUpdaterListener) {
        this.rateUpdaterListener = rateUpdaterListener;
    }

    @Override
    protected void onPreExecute() {
        dbHelper = DBHelper.getInstance(AppContext.getContext());
        currencyMap = new EnumMap<>(CharCode.class);
    }

    @Override
    protected EnumMap<CharCode, Currency> doInBackground(String... params) {
        String nominal = params[1];
        String value = params[2];
        String where = DBHelper.CUSTOM_SOURCE_MOCK.equals(params[0]) ?
                null :(params[0] + " = 1");
        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(DBHelper.TABLE_NAME,
                    new String[]{DBHelper.COLUMN_NAME_ID,
                            DBHelper.COLUMN_NAME_CHAR_CODE,
                            nominal,
                            value,
                            DBHelper.COLUMN_NAME_NAME_RESOURCE_ID,
                            DBHelper.COLUMN_NAME_FLAG_RESOURCE_ID},
                    where,
                    null, null, null, null);

            while (cursor.moveToNext()) {
                currencyMap.put(CharCode.valueOf(cursor.getString(1)),
                        new Currency(cursor.getInt(2),
                                cursor.getDouble(3),
                                cursor.getInt(4),
                                cursor.getInt(5)));
            }
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

        if (rateUpdaterListener != null) {
            rateUpdaterListener.setCurrencyMap(result);
            rateUpdaterListener.initSpinners();
            rateUpdaterListener.loadSpinnerProperties();
            rateUpdaterListener.tvDateTimeSetText();

            cursor.close();
            db.close();
        }
        //TODO remove currencyMap, work with Cursor and CursorAdapter
    }
}
