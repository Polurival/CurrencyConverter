package com.github.polurival.cc.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.github.polurival.cc.R;
import com.github.polurival.cc.model.CharCode;

/**
 * Created by Polurival
 * on 28.05.2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "converter";
    private static final int DB_VERSION = 1;

    public static final String TABLE_NAME = "currency";
    public static final String COLUMN_NAME_ID = "_id";
    public static final String COLUMN_NAME_CHAR_CODE = "char_code";
    public static final String COLUMN_NAME_NOMINAL = "nominal";
    public static final String COLUMN_NAME_VALUE = "value";
    public static final String COLUMN_NAME_CUSTOM_VALUE = "custom_value";
    public static final String COLUMN_NAME_NAME_RESOURCE_ID = "name_resource_id";
    public static final String COLUMN_NAME_FLAG_RESOURCE_ID = "flag_resource_id";
    public static final String COLUMN_NAME_CB_RF_SOURCE = "cb_rf";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateDatabase(db, 0, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateDatabase(db, oldVersion, newVersion);
    }

    private void updateDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 1) {
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME_CHAR_CODE + " TEXT, " +
                    COLUMN_NAME_NOMINAL + " INTEGER, " +
                    COLUMN_NAME_VALUE + " REAL, " +
                    COLUMN_NAME_CUSTOM_VALUE + " REAL, " +
                    COLUMN_NAME_NAME_RESOURCE_ID + " INTEGER, " +
                    COLUMN_NAME_FLAG_RESOURCE_ID + " INTEGER, " +
                    COLUMN_NAME_CB_RF_SOURCE + " INTEGER);");

            insertCurrency(db, CharCode.AUD, 1, 47.6818, 47.6818, R.string.aud, R.drawable.aud, 1);
            insertCurrency(db, CharCode.AMD, 100, 13.8162, 13.8162, R.string.amd, R.drawable.amd, 1);
            insertCurrency(db, CharCode.AZN, 1, 44.3498, 44.3498, R.string.azn, R.drawable.azn, 1);
            insertCurrency(db, CharCode.BGN, 1, 37.7487, 37.7487, R.string.bgn, R.drawable.bgn, 1);
            insertCurrency(db, CharCode.BRL, 1, 18.4344, 18.4344, R.string.brl, R.drawable.brl, 1);
            insertCurrency(db, CharCode.BYR, 10000, 33.5235, 33.5235, R.string.byr, R.drawable.byr, 1);
            insertCurrency(db, CharCode.CAD, 1, 50.6918, 50.6918, R.string.cad, R.drawable.cad, 1);
            insertCurrency(db, CharCode.CHF, 1, 66.7151, 66.7151, R.string.chf, R.drawable.chf, 1);
            insertCurrency(db, CharCode.CNY, 1, 10.0677, 10.0677, R.string.cny, R.drawable.cny, 1);
            insertCurrency(db, CharCode.CZK, 10, 27.3214, 27.3214, R.string.czk, R.drawable.czk, 1);
            insertCurrency(db, CharCode.DKK, 10, 99.3043, 99.3043, R.string.dkk, R.drawable.dkk, 1);
            insertCurrency(db, CharCode.EUR, 1, 73.8474, 73.8474, R.string.eur, R.drawable.eur, 1);
            insertCurrency(db, CharCode.GBP, 1, 96.8298, 96.8298, R.string.gbp, R.drawable.gbp, 1);
            insertCurrency(db, CharCode.HUF, 100, 23.4914, 23.4914, R.string.huf, R.drawable.huf, 1);
            insertCurrency(db, CharCode.INR, 100, 98.5470, 98.5470, R.string.inr, R.drawable.inr, 1);
            insertCurrency(db, CharCode.JPY, 100, 60.1770, 60.1770, R.string.jpy, R.drawable.jpy, 1);
            insertCurrency(db, CharCode.KGS, 100, 96.6682, 96.6682, R.string.kgs, R.drawable.kgs, 1);
            insertCurrency(db, CharCode.KRW, 1000, 55.9857, 55.9857, R.string.krw, R.drawable.krw, 1);
            insertCurrency(db, CharCode.KZT, 100, 19.6853, 19.6853, R.string.kzt, R.drawable.kzt, 1);
            insertCurrency(db, CharCode.MDL, 10, 32.9877, 32.9877, R.string.mdl, R.drawable.mdl, 1);
            insertCurrency(db, CharCode.NOK, 10, 79.5582, 79.5582, R.string.nok, R.drawable.nok, 1);
            insertCurrency(db, CharCode.PLN, 1, 16.7431, 16.7431, R.string.pln, R.drawable.pln, 1);
            insertCurrency(db, CharCode.RON, 1, 16.3781, 16.3781, R.string.ron, R.drawable.ron, 1);
            insertCurrency(db, CharCode.RUB, 1, 1.0, 1.0, R.string.rub, R.drawable.rub, 1);
            insertCurrency(db, CharCode.SEK, 10, 79.6427, 79.6427, R.string.sek, R.drawable.sek, 1);
            insertCurrency(db, CharCode.SGD, 1, 48.0231, 48.0231, R.string.sgd, R.drawable.sgd, 1);
            insertCurrency(db, CharCode.TJS, 10, 83.9686, 83.9686, R.string.tjs, R.drawable.tjs, 1);
            insertCurrency(db, CharCode.TMT, 1, 19.5794, 19.5794, R.string.tmt, R.drawable.tmt, 1);
            insertCurrency(db, CharCode.TRY, 1, 22.4401, 22.4401, R.string.try_, R.drawable.try_, 1);
            insertCurrency(db, CharCode.UAH, 10, 26.2746, 26.2746, R.string.uah, R.drawable.uah, 1);
            insertCurrency(db, CharCode.USD, 1, 66.0413, 66.0413, R.string.usd, R.drawable.usd, 1);
            insertCurrency(db, CharCode.UZS, 1000, 22.2361, 22.2361, R.string.uzs, R.drawable.uzs, 1);
            insertCurrency(db, CharCode.XDR, 1, 92.8541, 92.8541, R.string.xdr, 0, 1);
            insertCurrency(db, CharCode.ZAR, 10, 42.4580, 42.4580, R.string.zar, R.drawable.zar, 1);
        }
    }

    private static void insertCurrency(SQLiteDatabase db,
                                       Enum charCode, int nominal, double value,
                                       double customValue, int nameResourceId,
                                       int flagResourceId, int cbRfProvides) {

        ContentValues currencyValues = new ContentValues();
        currencyValues.put(COLUMN_NAME_CHAR_CODE, charCode.toString());
        currencyValues.put(COLUMN_NAME_NOMINAL, nominal);
        currencyValues.put(COLUMN_NAME_VALUE, value);
        currencyValues.put(COLUMN_NAME_CUSTOM_VALUE, customValue);
        currencyValues.put(COLUMN_NAME_NAME_RESOURCE_ID, nameResourceId);
        currencyValues.put(COLUMN_NAME_FLAG_RESOURCE_ID, flagResourceId);
        currencyValues.put(COLUMN_NAME_CB_RF_SOURCE, cbRfProvides);

        db.insert(TABLE_NAME, null, currencyValues);
    }
}
