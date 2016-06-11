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

    private static DBHelper instance;

    private static final String DB_NAME = "converter";
    private static final int DB_VERSION = 1;

    public static final String TABLE_NAME = "currency";

    public static final String COLUMN_NAME_ID = "_id";
    public static final String COLUMN_NAME_CHAR_CODE = "char_code";

    public static final String COLUMN_NAME_CB_RF_NOMINAL = "cb_rf_nominal";
    public static final String COLUMN_NAME_YAHOO_NOMINAL = "yahoo_nominal";
    public static final String COLUMN_NAME_CUSTOM_NOMINAL = "custom_nominal";

    public static final String COLUMN_NAME_CB_RF_VALUE = "cb_rf_value";
    public static final String COLUMN_NAME_YAHOO_VALUE = "yahoo_value";
    public static final String COLUMN_NAME_CUSTOM_VALUE = "custom_value";

    public static final String COLUMN_NAME_NAME_RESOURCE_ID = "name_resource_id";
    public static final String COLUMN_NAME_FLAG_RESOURCE_ID = "flag_resource_id";

    public static final String COLUMN_NAME_CB_RF_SOURCE = "cb_rf";
    public static final String COLUMN_NAME_YAHOO_SOURCE = "yahoo";

    public static final String CUSTOM_SOURCE_MOCK = "custom_source";

    //http://www.androiddesignpatterns.com/2012/05/correctly-managing-your-sqlite-database.html
    public static synchronized DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DBHelper(Context context) {
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
                    COLUMN_NAME_CB_RF_NOMINAL + " INTEGER, " +
                    COLUMN_NAME_YAHOO_NOMINAL + " INTEGER, " +
                    COLUMN_NAME_CUSTOM_NOMINAL + " INTEGER, " +
                    COLUMN_NAME_CB_RF_VALUE + " REAL, " +
                    COLUMN_NAME_YAHOO_VALUE + " REAL, " +
                    COLUMN_NAME_CUSTOM_VALUE + " REAL, " +
                    COLUMN_NAME_NAME_RESOURCE_ID + " INTEGER, " +
                    COLUMN_NAME_FLAG_RESOURCE_ID + " INTEGER, " +
                    COLUMN_NAME_CB_RF_SOURCE + " INTEGER, " +
                    COLUMN_NAME_YAHOO_SOURCE + " INTEGER);");

            insertCurrency(db, CharCode.AMD, 100, 1, 1, 13.8162, 0, 0, R.string.amd, R.drawable.amd, 1, 1);
            insertCurrency(db, CharCode.AUD, 1, 1, 1, 47.6818, 0, 0, R.string.aud, R.drawable.aud, 1, 1);
            insertCurrency(db, CharCode.AZN, 1, 1, 1, 44.3498, 0, 0, R.string.azn, R.drawable.azn, 1, 1);
            insertCurrency(db, CharCode.BGN, 1, 1, 1, 37.7487, 0, 0, R.string.bgn, R.drawable.bgn, 1, 1);
            insertCurrency(db, CharCode.BRL, 1, 1, 1, 18.4344, 0, 0, R.string.brl, R.drawable.brl, 1, 1);
            insertCurrency(db, CharCode.BYR, 10000, 1, 1, 33.5235, 0, 0, R.string.byr, R.drawable.byr, 1, 1);
            insertCurrency(db, CharCode.CAD, 1, 1, 1, 50.6918, 0, 0, R.string.cad, R.drawable.cad, 1, 1);
            insertCurrency(db, CharCode.CHF, 1, 1, 1, 66.7151, 0, 0, R.string.chf, R.drawable.chf, 1, 1);
            insertCurrency(db, CharCode.CNY, 1, 1, 1, 10.0677, 0, 0, R.string.cny, R.drawable.cny, 1, 1);
            insertCurrency(db, CharCode.CZK, 10, 1, 1, 27.3214, 0, 0, R.string.czk, R.drawable.czk, 1, 1);
            insertCurrency(db, CharCode.DKK, 10, 1, 1, 99.3043, 0, 0, R.string.dkk, R.drawable.dkk, 1, 1);
            insertCurrency(db, CharCode.EUR, 1, 1, 1, 73.8474, 0, 0, R.string.eur, R.drawable.eur, 1, 1);
            insertCurrency(db, CharCode.GBP, 1, 1, 1, 96.8298, 0, 0, R.string.gbp, R.drawable.gbp, 1, 1);
            insertCurrency(db, CharCode.HUF, 100, 1, 1, 23.4914, 0, 0, R.string.huf, R.drawable.huf, 1, 1);
            insertCurrency(db, CharCode.INR, 100, 1, 1, 98.5470, 0, 0, R.string.inr, R.drawable.inr, 1, 1);
            insertCurrency(db, CharCode.JPY, 100, 1, 1, 60.1770, 0, 0, R.string.jpy, R.drawable.jpy, 1, 1);
            insertCurrency(db, CharCode.KGS, 100, 1, 1, 96.6682, 0, 0, R.string.kgs, R.drawable.kgs, 1, 1);
            insertCurrency(db, CharCode.KRW, 1000, 1, 1, 55.9857, 1171.880005, 1171.880005, R.string.krw, R.drawable.krw, 1, 1);
            insertCurrency(db, CharCode.KZT, 100, 1, 1, 19.6853, 0, 0, R.string.kzt, R.drawable.kzt, 1, 1);
            insertCurrency(db, CharCode.MDL, 10, 1, 1, 32.9877, 0, 0, R.string.mdl, R.drawable.mdl, 1, 1);
            insertCurrency(db, CharCode.NOK, 10, 1, 1, 79.5582, 0, 0, R.string.nok, R.drawable.nok, 1, 1);
            insertCurrency(db, CharCode.PLN, 1, 1, 1, 16.7431, 0, 0, R.string.pln, R.drawable.pln, 1, 1);
            insertCurrency(db, CharCode.RON, 1, 1, 1, 16.3781, 0, 0, R.string.ron, R.drawable.ron, 1, 1);
            insertCurrency(db, CharCode.RUB, 1, 1, 1, 1.0, 0, 0, R.string.rub, R.drawable.rub, 1, 1);
            insertCurrency(db, CharCode.SEK, 10, 1, 1, 79.6427, 0, 0, R.string.sek, R.drawable.sek, 1, 1);
            insertCurrency(db, CharCode.SGD, 1, 1, 1, 48.0231, 0, 0, R.string.sgd, R.drawable.sgd, 1, 1);
            insertCurrency(db, CharCode.TJS, 10, 1, 1, 83.9686, 0, 0, R.string.tjs, R.drawable.tjs, 1, 1);
            insertCurrency(db, CharCode.TMT, 1, 1, 1, 19.5794, 0, 0, R.string.tmt, R.drawable.tmt, 1, 1);
            insertCurrency(db, CharCode.TRY, 1, 1, 1, 22.4401, 0, 0, R.string.try_, R.drawable.try_, 1, 1);
            insertCurrency(db, CharCode.UAH, 10, 1, 1, 26.2746, 0, 0, R.string.uah, R.drawable.uah, 1, 1);
            insertCurrency(db, CharCode.USD, 1, 1, 1, 66.0413, 0, 0, R.string.usd, R.drawable.usd, 1, 1);
            insertCurrency(db, CharCode.UZS, 1000, 1, 1, 22.2361, 0, 0, R.string.uzs, R.drawable.uzs, 1, 1);
            insertCurrency(db, CharCode.XAG, 1, 100, 100, 0, 5.7753, 5.7753, R.string.xdr, 0, 0, 1);
            insertCurrency(db, CharCode.XDR, 1, 1, 1, 92.8541, 0, 0, R.string.xdr, 0, 1, 1);
            insertCurrency(db, CharCode.ZAR, 10, 1, 1, 42.4580, 0, 0, R.string.zar, R.drawable.zar, 1, 1);
        }
    }

    private static void insertCurrency(SQLiteDatabase db, Enum charCode,
                                       int cbRfNominal,int yahooNominal, int customNominal,
                                       double cbRfValue, double yahooValue, double customValue,
                                       int nameResourceId, int flagResourceId,
                                       int cbRfProvides, int yahooProvides) {

        ContentValues currencyValues = new ContentValues();
        currencyValues.put(COLUMN_NAME_CHAR_CODE, charCode.toString());

        currencyValues.put(COLUMN_NAME_CB_RF_NOMINAL, cbRfNominal);
        currencyValues.put(COLUMN_NAME_YAHOO_NOMINAL, yahooNominal);
        currencyValues.put(COLUMN_NAME_CUSTOM_NOMINAL, customNominal);

        currencyValues.put(COLUMN_NAME_CB_RF_VALUE, cbRfValue);
        currencyValues.put(COLUMN_NAME_YAHOO_VALUE, yahooValue);
        currencyValues.put(COLUMN_NAME_CUSTOM_VALUE, customValue);

        currencyValues.put(COLUMN_NAME_NAME_RESOURCE_ID, nameResourceId);
        currencyValues.put(COLUMN_NAME_FLAG_RESOURCE_ID, flagResourceId);

        currencyValues.put(COLUMN_NAME_CB_RF_SOURCE, cbRfProvides);
        currencyValues.put(COLUMN_NAME_YAHOO_SOURCE, yahooProvides);

        db.insert(TABLE_NAME, null, currencyValues);
    }
}
