package com.github.polurival.cc.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.github.polurival.cc.AppContext;
import com.github.polurival.cc.R;
import com.github.polurival.cc.model.CharCode;
import com.github.polurival.cc.model.Currency;
import com.github.polurival.cc.util.Toaster;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper instance;

    private boolean isUpgrade;

    private static final String DB_NAME = "converter";
    private static final int DB_VERSION = 6;

    public static final String TABLE_NAME = "currency";

    public static final String COLUMN_NAME_ID = "_id";
    public static final String COLUMN_NAME_CHAR_CODE = "char_code";

    public static final String COLUMN_NAME_CB_RF_NOMINAL = "cb_rf_nominal";
    public static final String COLUMN_NAME_YAHOO_NOMINAL = "yahoo_nominal";
    public static final String COLUMN_NAME_CUSTOM_NOMINAL = "custom_nominal";

    public static final String COLUMN_NAME_CB_RF_RATE = "cb_rf_rate";
    public static final String COLUMN_NAME_YAHOO_RATE = "yahoo_rate";
    public static final String COLUMN_NAME_CUSTOM_RATE = "custom_rate";

    public static final String COLUMN_NAME_CURRENCY_NAME = "currency_name";
    public static final String COLUMN_NAME_FLAG_ID = "flag_id";

    public static final String COLUMN_NAME_CB_RF_SOURCE = "cb_rf";
    public static final String COLUMN_NAME_YAHOO_SOURCE = "yahoo";

    public static final String COLUMN_NAME_SWITCHING = "switching";

    public static final String CUSTOM_SOURCE_MOCK = "custom_source";

    /**
     * See <a href="http://www.androiddesignpatterns.com/2012/05/correctly-managing-your-sqlite-database.html">source</a>
     */
    public static synchronized DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context);
        }
        return instance;
    }

    private DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        isUpgrade = false;
        updateDatabase(db, 0, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        isUpgrade = true;
        updateDatabase(db, oldVersion, newVersion);
    }

    private void updateDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 6) {
            List<Currency> userData = null;
            if (isUpgrade) {
                userData = copyUserDataToList(db);
            }

            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            createAndFillTable(db);

            if (isUpgrade && null != userData) {
                copyUserDataToTable(db, userData);
            }
        }
    }

    private List<Currency> copyUserDataToList(SQLiteDatabase db) {
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COLUMN_NAME_ID, COLUMN_NAME_CHAR_CODE,
                        COLUMN_NAME_CUSTOM_NOMINAL, COLUMN_NAME_CUSTOM_RATE, COLUMN_NAME_SWITCHING},
                null, null, null, null, null);

        List<Currency> userData = new ArrayList<>();
        if (null != cursor) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                Currency currency = new Currency(cursor.getString(1), cursor.getInt(2),
                        cursor.getDouble(3), cursor.getInt(4));
                userData.add(currency);
            }
            cursor.close();
        }
        return userData;
    }

    private void copyUserDataToTable(SQLiteDatabase db, List<Currency> userData) {
        ContentValues contentValues = new ContentValues();
        db.beginTransaction();
        try {
            for (Currency currency : userData) {
                contentValues.put(COLUMN_NAME_CUSTOM_NOMINAL, currency.getNominal());
                contentValues.put(COLUMN_NAME_CUSTOM_RATE, currency.getRate());
                contentValues.put(COLUMN_NAME_SWITCHING, currency.getSwitching());

                db.update(TABLE_NAME, contentValues,
                        COLUMN_NAME_CHAR_CODE + " = ?", new String[]{currency.getCharCode()});
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * @param withSwitching provides search for off currencies
     */
    public static Cursor getSearchCursor(String charCode, String rateUpdaterClassName,
                                         boolean withSwitching) {
        Cursor searchCursor = null;

        boolean custom = false;
        String sourceColumnName = null;
        if (AppContext.getContext().getString(R.string.cb_rf_rate_updater_class)
                .equals(rateUpdaterClassName)) {
            sourceColumnName = COLUMN_NAME_CB_RF_SOURCE;

        } else if (AppContext.getContext().getString(R.string.yahoo_rate_updater_class)
                .equals(rateUpdaterClassName)) {
            sourceColumnName = COLUMN_NAME_YAHOO_SOURCE;

        } else {
            custom = true;
        }

        try {
            String sqlQuery;
            if (withSwitching) {
                if (custom) {
                    sqlQuery = String.format(
                            "SELECT %s, %s, %s FROM %s WHERE %s = 1 AND %s LIKE ",
                            COLUMN_NAME_ID, COLUMN_NAME_CHAR_CODE, COLUMN_NAME_CURRENCY_NAME,
                            TABLE_NAME, COLUMN_NAME_SWITCHING, COLUMN_NAME_CHAR_CODE)
                            + "'%" + charCode + "%';";
                } else {
                    sqlQuery = String.format(
                            "SELECT %s, %s, %s FROM %s WHERE %s = 1 AND %s = 1 AND %s LIKE ",
                            COLUMN_NAME_ID, COLUMN_NAME_CHAR_CODE, COLUMN_NAME_CURRENCY_NAME,
                            TABLE_NAME, sourceColumnName, COLUMN_NAME_SWITCHING,
                            COLUMN_NAME_CHAR_CODE) + "'%" + charCode + "%';";
                }
            } else {
                if (custom) {
                    sqlQuery = String.format(
                            "SELECT %s, %s, %s FROM %s WHERE %s LIKE ",
                            COLUMN_NAME_ID, COLUMN_NAME_CHAR_CODE, COLUMN_NAME_CURRENCY_NAME,
                            TABLE_NAME, COLUMN_NAME_CHAR_CODE)
                            + "'%" + charCode + "%';";
                } else {
                    sqlQuery = String.format(
                            "SELECT %s, %s, %s FROM %s WHERE %s = 1 AND %s LIKE ",
                            COLUMN_NAME_ID, COLUMN_NAME_CHAR_CODE, COLUMN_NAME_CURRENCY_NAME,
                            TABLE_NAME, sourceColumnName, COLUMN_NAME_CHAR_CODE)
                            + "'%" + charCode + "%';";
                }
            }

            searchCursor = instance.getReadableDatabase().rawQuery(sqlQuery, null);
        } catch (SQLiteException e) {
            Toaster.showToast(AppContext.getContext().getString(R.string.db_reading_error));
        }
        return searchCursor;
    }

    private void createAndFillTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME_CHAR_CODE + " TEXT, " +
                COLUMN_NAME_CB_RF_NOMINAL + " INTEGER, " +
                COLUMN_NAME_YAHOO_NOMINAL + " INTEGER, " +
                COLUMN_NAME_CUSTOM_NOMINAL + " INTEGER, " +
                COLUMN_NAME_CB_RF_RATE + " REAL, " +
                COLUMN_NAME_YAHOO_RATE + " REAL, " +
                COLUMN_NAME_CUSTOM_RATE + " REAL, " +
                COLUMN_NAME_CURRENCY_NAME + " TEXT, " +
                COLUMN_NAME_FLAG_ID + " TEXT, " +
                COLUMN_NAME_CB_RF_SOURCE + " INTEGER, " +
                COLUMN_NAME_YAHOO_SOURCE + " INTEGER, " +
                COLUMN_NAME_SWITCHING + " INTEGER);");

        insertCurrency(db, CharCode.AED, 0, 1, 1, 0, 3.672950, 3.672950, "aed", "aed", 0, 1, 1);
        insertCurrency(db, CharCode.AFN, 0, 1, 1, 0, 69.279999, 69.279999, "afn", "afn", 0, 1, 1);
        insertCurrency(db, CharCode.ALL, 0, 1, 1, 0, 122.419502, 122.419502, "all", "all", 0, 1, 1);
        insertCurrency(db, CharCode.AMD, 100, 1, 1, 13.5089, 478.820007, 478.820007, "amd", "amd", 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.ANG, 0, 1, 1, 0, 1.790000, 1.790000, "ang", "ang", 0, 1, 1);
        insertCurrency(db, CharCode.AOA, 0, 1, 1, 0, 165.733002, 165.733002, "aoa", "aoa", 0, 1, 1);
        insertCurrency(db, CharCode.ARS, 0, 1, 1, 0, 13.815950, 13.815950, "ars", "ars", 0, 1, 1);
        insertCurrency(db, CharCode.AUD, 1, 1, 1, 47.9031, 1.355730, 1.355730, "aud", "aud", 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.AWG, 0, 1, 1, 0, 1.790000, 1.790000, "awg", "awg", 0, 1, 1);
        insertCurrency(db, CharCode.AZN, 1, 1, 1, 42.5989, 1.521000, 1.521000, "azn", "azn", 1, 1, 1); //cb_rf

        insertCurrency(db, CharCode.BAM, 0, 1, 1, 0, 1.738400, 1.738400, "bam", "bam", 0, 1, 1);
        insertCurrency(db, CharCode.BBD, 0, 1, 1, 0, 2.000000, 2.000000, "bbd", "bbd", 0, 1, 1);
        insertCurrency(db, CharCode.BDT, 0, 1, 1, 0, 78.879448, 78.879448, "bdt", "bdt", 0, 1, 1);
        insertCurrency(db, CharCode.BGN, 1, 1, 1, 37.4141, 1.737200, 1.737200, "bgn", "bgn", 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.BHD, 0, 10, 10, 0, 3.76945, 3.76945, "bhd", "bhd", 0, 1, 1);
        insertCurrency(db, CharCode.BIF, 0, 1, 1, 0, 1560.000000, 1560.000000, "bif", "bif", 0, 1, 1);
        insertCurrency(db, CharCode.BMD, 0, 1, 1, 0, 1.000050, 1.000050, "bmd", "bmd", 0, 1, 1);
        insertCurrency(db, CharCode.BND, 0, 1, 1, 0, 1.356850, 1.356850, "bnd", "bnd", 0, 1, 1);
        insertCurrency(db, CharCode.BOB, 0, 1, 1, 0, 6.875000, 6.875000, "bob", "bob", 0, 1, 1);
        insertCurrency(db, CharCode.BRL, 1, 1, 1, 19.0300, 3.419900, 3.419900, "brl", "brl", 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.BSD, 0, 1, 1, 0, 1.002965, 1.002965, "bsd", "bsd", 0, 1, 1);
        insertCurrency(db, CharCode.BTN, 0, 1, 1, 0, 66.792503, 66.792503, "btn", "btn", 0, 1, 1);
        insertCurrency(db, CharCode.BWP, 0, 1, 1, 0, 10.888950, 10.888950, "bwp", "bwp", 0, 1, 1);
        insertCurrency(db, CharCode.BYN, 1, 1, 1, 31.8884, 2.002000, 2.002000, "byn", "byn", 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.BZD, 0, 1, 1, 0, 1.995000, 1.995000, "bzd", "bzd", 0, 1, 1);

        insertCurrency(db, CharCode.CAD, 1, 1, 1, 50.8069, 1.277900, 1.277900, "cad", "cad", 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.CDF, 0, 1, 1, 0, 928.000000, 928.000000, "cdf", "cdf", 0, 1, 1);
        insertCurrency(db, CharCode.CHF, 1, 10, 10, 67.1590, 9.64255, 9.64255, "chf", "chf", 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.CLF, 0, 100, 100, 0, 2.4600, 2.4600, "clf", "clp", 0, 1, 1);
        insertCurrency(db, CharCode.CLP, 0, 1, 1, 0, 675.900024, 675.900024, "clp", "clp", 0, 1, 1);
        insertCurrency(db, CharCode.CNH, 0, 1, 1, 0, 6.603550, 6.603550, "cnh", "cny", 0, 1, 1);
        insertCurrency(db, CharCode.CNY, 10, 1, 1, 98.6097, 6.562650, 6.562650, "cny", "cny", 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.COP, 0, 1, 1, 0, 2971.449951, 2971.449951, "cop", "cop", 0, 1, 1);
        insertCurrency(db, CharCode.CRC, 0, 1, 1, 0, 541.419983, 541.419983, "crc", "crc", 0, 1, 1);
        insertCurrency(db, CharCode.CUC, 0, 1, 1, 0, 0.995000, 0.995000, "cuc", "cup", 0, 1, 1);
        insertCurrency(db, CharCode.CUP, 0, 1, 1, 0, 1.000000, 1.000000, "cup", "cup", 0, 1, 1);
        insertCurrency(db, CharCode.CVE, 0, 1, 1, 0, 97.494003, 97.494003, "cve", "cve", 0, 1, 1);
        insertCurrency(db, CharCode.CZK, 10, 1, 1, 27.0834, 24.021500, 24.021500, "czk", "czk", 1, 1, 1); //cb_rf

        insertCurrency(db, CharCode.DJF, 0, 1, 1, 0, 177.770004, 177.770004, "djf", "djf", 0, 1, 1);
        insertCurrency(db, CharCode.DKK, 10, 1, 1, 98.4372, 6.609550, 6.609550, "dkk", "dkk", 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.DOP, 0, 1, 1, 0, 46.090000, 46.090000, "dop", "dop", 0, 1, 1);
        insertCurrency(db, CharCode.DZD, 0, 1, 1, 0, 109.830002, 109.830002, "dzd", "dzd", 0, 1, 1);

        insertCurrency(db, CharCode.EGP, 0, 1, 1, 0, 8.881450, 8.881450, "egp", "egp", 0, 1, 1);
        insertCurrency(db, CharCode.ERN, 0, 1, 1, 0, 16.180000, 16.180000, "ern", "ern", 0, 1, 1);
        insertCurrency(db, CharCode.ETB, 0, 1, 1, 0, 21.730000, 21.730000, "etb", "etb", 0, 1, 1);
        insertCurrency(db, CharCode.EUR, 1, 10, 10, 73.1909, 8.88613, 8.88613, "eur", "eur", 1, 1, 1); //cb_rf

        insertCurrency(db, CharCode.FJD, 0, 1, 1, 0, 2.081600, 2.081600, "fjd", "fjd", 0, 1, 1);
        insertCurrency(db, CharCode.FKP, 0, 10, 10, 0, 6.59800, 6.59800, "fkp", "fkp", 0, 1, 1);

        insertCurrency(db, CharCode.GBP, 1, 10, 10, 93.4185, 7.01484, 7.01484, "gbp", "gbp", 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.GEL, 0, 1, 1, 0, 2.130000, 2.130000, "gel", "gel", 0, 1, 1);
        insertCurrency(db, CharCode.GHS, 0, 1, 1, 0, 3.860000, 3.860000, "ghs", "ghs", 0, 1, 1);
        insertCurrency(db, CharCode.GIP, 0, 10, 10, 0, 7.71000, 7.71000, "gip", "gip", 0, 1, 1);
        insertCurrency(db, CharCode.GMD, 0, 1, 1, 0, 42.820000, 42.820000, "gmd", "gmd", 0, 1, 1);
        insertCurrency(db, CharCode.GNF, 0, 1, 1, 0, 7353.750000, 7353.750000, "gnf", "gnf", 0, 1, 1);
        insertCurrency(db, CharCode.GTQ, 0, 1, 1, 0, 7.671150, 7.671150, "gtq", "gtq", 0, 1, 1);
        insertCurrency(db, CharCode.GYD, 0, 1, 1, 0, 206.789948, 206.789948, "gyd", "gyd", 0, 1, 1);

        insertCurrency(db, CharCode.HKD, 0, 1, 1, 0, 7.762850, 7.762850, "hkd", "hkd", 0, 1, 1);
        insertCurrency(db, CharCode.HNL, 0, 1, 1, 0, 22.635950, 22.635950, "hnl", "hnl", 0, 1, 1);
        insertCurrency(db, CharCode.HRK, 0, 1, 1, 0, 6.701300, 6.701300, "hrk", "hrk", 0, 1, 1);
        insertCurrency(db, CharCode.HTG, 0, 1, 1, 0, 63.053951, 63.053951, "htg", "htg", 0, 1, 1);
        insertCurrency(db, CharCode.HUF, 100, 1, 1, 23.4929, 277.630005, 277.630005, "huf", "huf", 1, 1, 1); //cb_rf

        insertCurrency(db, CharCode.IDR, 0, 1, 1, 0, 13347.500000, 13347.500000, "idr", "idr", 0, 1, 1);
        insertCurrency(db, CharCode.ILS, 0, 1, 1, 0, 3.864200, 3.864200, "ils", "ils", 0, 1, 1);
        insertCurrency(db, CharCode.INR, 100, 1, 1, 96.8388, 66.964951, 66.964951, "inr", "inr", 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.IQD, 0, 1, 1, 0, 1171.399902, 1171.399902, "iqd", "iqd", 0, 1, 1);
        insertCurrency(db, CharCode.IRR, 0, 1, 1, 0, 30485.000000, 30485.000000, "irr", "irr", 0, 1, 1);
        insertCurrency(db, CharCode.ISK, 0, 1, 1, 0, 123.500000, 123.500000, "isk", "isk", 0, 1, 1);

        insertCurrency(db, CharCode.JMD, 0, 1, 1, 0, 125.414948, 125.414948, "jmd", "jmd", 0, 1, 1);
        insertCurrency(db, CharCode.JOD, 0, 10, 10, 0, 7.08250, 7.08250, "jod", "jod", 0, 1, 1);
        insertCurrency(db, CharCode.JPY, 100, 1, 1, 60.4604, 106.940002, 106.940002, "jpy", "jpy", 1, 1, 1); //cb_rf

        insertCurrency(db, CharCode.KES, 0, 1, 1, 0, 101.081497, 101.081497, "kes", "kes", 0, 1, 1);
        insertCurrency(db, CharCode.KGS, 100, 1, 1, 95.1422, 68.011597, 68.011597, "kgs", "kgs", 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.KHR, 0, 1, 1, 0, 4096.500000, 4096.500000, "khr", "khr", 0, 1, 1);
        insertCurrency(db, CharCode.KMF, 0, 1, 1, 0, 434.999939, 434.999939, "kmf", "kmf", 0, 1, 1);
        insertCurrency(db, CharCode.KPW, 0, 1, 1, 0, 900.000000, 900.000000, "kpw", "kpw", 0, 1, 1);
        insertCurrency(db, CharCode.KRW, 1000, 1, 1, 55.4969, 1171.880005, 1171.880005, "krw", "krw", 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.KWD, 0, 10, 10, 0, 3.01220, 3.01220, "kwd", "kwd", 0, 1, 1);
        insertCurrency(db, CharCode.KYD, 0, 10, 10, 0, 8.20000, 8.20000, "kyd", "kyd", 0, 1, 1);
        insertCurrency(db, CharCode.KZT, 100, 1, 1, 19.3227, 333.024963, 333.024963, "kzt", "kzt", 1, 1, 1); //cb_rf

        insertCurrency(db, CharCode.LAK, 0, 1, 1, 0, 8127.299805, 8127.299805, "lak", "lak", 0, 1, 1);
        insertCurrency(db, CharCode.LBP, 0, 1, 1, 0, 1511.949951, 1511.949951, "lbp", "lbp", 0, 1, 1);
        insertCurrency(db, CharCode.LKR, 0, 1, 1, 0, 146.100006, 146.100006, "lkr", "lkr", 0, 1, 1);
        insertCurrency(db, CharCode.LRD, 0, 1, 1, 0, 84.669998, 84.669998, "lrd", "lrd", 0, 1, 1);
        insertCurrency(db, CharCode.LSL, 0, 1, 1, 0, 14.874950, 14.874950, "lsl", "lsl", 0, 1, 1);
        insertCurrency(db, CharCode.LYD, 0, 1, 1, 0, 1.369000, 1.369000, "lyd", "lyd", 0, 1, 1);

        insertCurrency(db, CharCode.MAD, 0, 1, 1, 0, 9.681550, 9.681550, "mad", "mad", 0, 1, 1);
        insertCurrency(db, CharCode.MDL, 10, 1, 1, 32.7800, 19.776449, 19.776449, "mdl", "mdl", 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.MGA, 0, 1, 1, 0, 3247.449951, 3247.449951, "mga", "mga", 0, 1, 1);
        insertCurrency(db, CharCode.MKD, 0, 1, 1, 0, 54.499001, 54.499001, "mkd", "mkd", 0, 1, 1);
        insertCurrency(db, CharCode.MMK, 0, 1, 1, 0, 1196.449951, 1196.449951, "mmk", "mmk", 0, 1, 1);
        insertCurrency(db, CharCode.MNT, 0, 1, 1, 0, 1981.000000, 1981.000000, "mnt", "mnt", 0, 1, 1);
        insertCurrency(db, CharCode.MOP, 0, 1, 1, 0, 7.995550, 7.995550, "mop", "mop", 0, 1, 1);
        insertCurrency(db, CharCode.MRO, 0, 1, 1, 0, 357.369995, 357.369995, "mro", "mro", 0, 1, 1);
        insertCurrency(db, CharCode.MUR, 0, 1, 1, 0, 35.363998, 35.363998, "mur", "mur", 0, 1, 1);
        insertCurrency(db, CharCode.MWK, 0, 1, 1, 0, 710.715027, 710.715027, "mwk", "mwk", 0, 1, 1);
        insertCurrency(db, CharCode.MVR, 0, 1, 1, 0, 15.350000, 15.350000, "mvr", "mvr", 0, 1, 1);
        insertCurrency(db, CharCode.MXN, 0, 1, 1, 0, 18.634251, 18.634251, "mxn", "mxn", 0, 1, 1);
        insertCurrency(db, CharCode.MXV, 0, 1, 1, 0, 3.439188, 3.439188, "mxv", "mxn", 0, 1, 1);
        insertCurrency(db, CharCode.MYR, 0, 1, 1, 0, 4.083000, 4.083000, "myr", "myr", 0, 1, 1);
        insertCurrency(db, CharCode.MZN, 0, 1, 1, 0, 60.209999, 60.209999, "mzn", "mzn", 0, 1, 1);

        insertCurrency(db, CharCode.NAD, 0, 1, 1, 0, 14.874450, 14.874450, "nad", "nad", 0, 1, 1);
        insertCurrency(db, CharCode.NGN, 0, 1, 1, 0, 199.625000, 199.625000, "ngn", "ngn", 0, 1, 1);
        insertCurrency(db, CharCode.NIO, 0, 1, 1, 0, 28.538000, 28.538000, "nio", "nio", 0, 1, 1);
        insertCurrency(db, CharCode.NOK, 10, 1, 1, 79.1735, 8.274100, 8.274100, "nok", "nok", 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.NPR, 0, 1, 1, 0, 106.867996, 106.867996, "npr", "npr", 0, 1, 1);
        insertCurrency(db, CharCode.NZD, 0, 1, 1, 0, 1.417677, 1.417677, "nzd", "nzd", 0, 1, 1);

        insertCurrency(db, CharCode.OMR, 0, 10, 10, 0, 3.85025, 3.85025, "omr", "omr", 0, 1, 1);

        insertCurrency(db, CharCode.PAB, 0, 1, 1, 0, 1.002835, 1.002835, "pab", "pab", 0, 1, 1);
        insertCurrency(db, CharCode.PEN, 0, 1, 1, 0, 3.331000, 3.331000, "pen", "pen", 0, 1, 1);
        insertCurrency(db, CharCode.PGK, 0, 1, 1, 0, 3.166350, 3.166350, "pgk", "pgk", 0, 1, 1);
        insertCurrency(db, CharCode.PHP, 0, 1, 1, 0, 46.110500, 46.110500, "php", "php", 0, 1, 1);
        insertCurrency(db, CharCode.PKR, 0, 1, 1, 0, 104.580002, 104.580002, "pkr", "pkr", 0, 1, 1);
        insertCurrency(db, CharCode.PLN, 1, 1, 1, 16.8168, 3.890450, 3.890450, "pln", "pln", 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.PYG, 0, 1, 1, 0, 5668.740234, 5668.740234, "pyg", "pyg", 0, 1, 1);

        insertCurrency(db, CharCode.QAR, 0, 1, 1, 0, 3.640000, 3.640000, "qar", "qar", 0, 1, 1);

        insertCurrency(db, CharCode.RON, 1, 1, 1, 16.2232, 4.012850, 4.012850, "ron", "ron", 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.RSD, 0, 1, 1, 0, 109.504997, 109.504997, "rsd", "rsd", 0, 1, 1);
        insertCurrency(db, CharCode.RUB, 1, 1, 1, 1.0, 65.365997, 65.365997, "rub", "rub", 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.RWF, 0, 1, 1, 0, 764.994995, 764.994995, "rwf", "rwf", 0, 1, 1);

        insertCurrency(db, CharCode.SAR, 0, 1, 1, 0, 3.750100, 3.750100, "sar", "sar", 0, 1, 1);
        insertCurrency(db, CharCode.SBD, 0, 1, 1, 0, 7.799350, 7.799350, "sbd", "sbd", 0, 1, 1);
        insertCurrency(db, CharCode.SCR, 0, 1, 1, 0, 12.990950, 12.990950, "scr", "scr", 0, 1, 1);
        insertCurrency(db, CharCode.SDG, 0, 1, 1, 0, 6.084700, 6.084700, "sdg", "sdg", 0, 1, 1);
        insertCurrency(db, CharCode.SEK, 10, 1, 1, 78.8224, 8.308450, 8.308450, "sek", "sek", 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.SGD, 1, 1, 1, 47.7407, 1.360250, 1.360250, "sgd", "sgd", 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.SHP, 0, 10, 10, 0, 6.93900, 6.93900, "shp", "shp", 0, 1, 1);
        insertCurrency(db, CharCode.SLL, 0, 1, 1, 0, 3945.000000, 3945.000000, "sll", "sll", 0, 1, 1);
        insertCurrency(db, CharCode.SOS, 0, 1, 1, 0, 590.150024, 590.150024, "sos", "sos", 0, 1, 1);
        insertCurrency(db, CharCode.SRD, 0, 1, 1, 0, 6.983500, 6.983500, "srd", "srd", 0, 1, 1);
        insertCurrency(db, CharCode.STD, 0, 1, 1, 0, 21663.500000, 21663.500000, "std", "std", 0, 1, 1);
        insertCurrency(db, CharCode.SVC, 0, 1, 1, 0, 8.774950, 8.774950, "svc", "svc", 0, 1, 1);
        insertCurrency(db, CharCode.SYP, 0, 1, 1, 0, 219.856995, 219.856995, "syp", "syp", 0, 1, 1);
        insertCurrency(db, CharCode.SZL, 0, 1, 1, 0, 14.880000, 14.880000, "szl", "szl", 0, 1, 1);

        insertCurrency(db, CharCode.THB, 0, 1, 1, 0, 35.263500, 35.263500, "thb", "thb", 0, 1, 1);
        insertCurrency(db, CharCode.TJS, 10, 1, 1, 82.5353, 7.869000, 7.869000, "tjs", "tjs", 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.TMT, 1, 1, 1, 19.2068, 3.499950, 3.499950, "tmt", "tmt", 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.TND, 0, 1, 1, 0, 2.158550, 2.158550, "tnd", "tnd", 0, 1, 1);
        insertCurrency(db, CharCode.TOP, 0, 1, 1, 0, 2.194522, 2.194522, "top", "top", 0, 1, 1);
        insertCurrency(db, CharCode.TRY, 1, 1, 1, 22.2417, 2.928950, 2.928950, "try_", "try_", 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.TTD, 0, 1, 1, 0, 6.679100, 6.679100, "ttd", "ttd", 0, 1, 1);
        insertCurrency(db, CharCode.TWD, 0, 1, 1, 0, 32.324501, 32.324501, "twd", "twd", 0, 1, 1);
        insertCurrency(db, CharCode.TZS, 0, 1, 1, 0, 2199.350098, 2199.350098, "tzs", "tzs", 0, 1, 1);

        insertCurrency(db, CharCode.UAH, 10, 1, 1, 25.8624, 25.066950, 25.066950, "uah", "uah", 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.UGX, 0, 1, 1, 0, 3359.449951, 3359.449951, "ugx", "ugx", 0, 1, 1);
        insertCurrency(db, CharCode.USD, 1, 1, 1, 64.7077, 1.000000, 1.000000, "usd", "usd", 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.UYU, 0, 1, 1, 0, 30.775000, 30.775000, "uyu", "uyu", 0, 1, 1);
        insertCurrency(db, CharCode.UZS, 1000, 1, 1, 22.0319, 2929.139893, 2929.139893, "uzs", "uzs", 1, 1, 1); //cb_rf

        insertCurrency(db, CharCode.VEF, 0, 1, 1, 0, 9.950000, 9.950000, "vef", "vef", 0, 1, 1);
        insertCurrency(db, CharCode.VND, 0, 1, 1, 0, 22330.000000, 22330.000000, "vnd", "vnd", 0, 1, 1);
        insertCurrency(db, CharCode.VUV, 0, 1, 1, 0, 112.260002, 112.260002, "vuv", "vuv", 0, 1, 1);

        insertCurrency(db, CharCode.WST, 0, 1, 1, 0, 2.529086, 2.529086, "wst", "wst", 0, 1, 1);

        insertCurrency(db, CharCode.XAF, 0, 1, 1, 0, 583.021057, 583.021057, "xaf", "0", 0, 1, 1);
        insertCurrency(db, CharCode.XAG, 0, 100, 100, 0, 5.7753, 5.7753, "xag", "xag", 0, 1, 1);
        insertCurrency(db, CharCode.XAU, 0, 10000, 10000, 0, 7.85, 7.85, "xau", "xau", 0, 1, 1);
        insertCurrency(db, CharCode.XCD, 0, 1, 1, 0, 2.700000, 2.700000, "xcd", "0", 0, 1, 1);
        insertCurrency(db, CharCode.XCP, 0, 10, 10, 0, 4.92247, 4.92247, "xcp", "xcp", 0, 1, 1);
        insertCurrency(db, CharCode.XDR, 1, 10, 10, 91.4947, 7.10600, 7.10600, "xdr", "0", 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.XOF, 0, 1, 1, 0, 583.021057, 583.021057, "xof", "0", 0, 1, 1);
        insertCurrency(db, CharCode.XPD, 0, 1000, 1000, 0, 1.828, 1.828, "xpd", "xpd", 0, 1, 1);
        insertCurrency(db, CharCode.XPF, 0, 1, 1, 0, 106.063202, 106.063202, "xpf", "0", 0, 1, 1);
        insertCurrency(db, CharCode.XPT, 0, 1000, 1000, 0, 1.006, 1.006, "xpt", "xpt", 0, 1, 1);

        insertCurrency(db, CharCode.YER, 0, 1, 1, 0, 250.100006, 250.100006, "yer", "yer", 0, 1, 1);

        insertCurrency(db, CharCode.ZAR, 10, 1, 1, 43.3567, 15.231200, 15.231200, "zar", "zar", 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.ZMW, 0, 1, 1, 0, 10.732950, 10.732950, "zmw", "zmw", 0, 1, 1);
        insertCurrency(db, CharCode.ZWL, 0, 1, 1, 0, 322.355011, 322.355011, "zwl", "zwl", 0, 1, 1);
    }

    private static void insertCurrency(SQLiteDatabase db, Enum charCode,
                                       int cbRfNominal, int yahooNominal, int customNominal,
                                       double cbRfRate, double yahooRate, double customRate,
                                       String currencyName, String flagId,
                                       int cbRfProvides, int yahooProvides, int switching) {

        ContentValues currencyValues = new ContentValues();
        currencyValues.put(COLUMN_NAME_CHAR_CODE, charCode.toString());

        currencyValues.put(COLUMN_NAME_CB_RF_NOMINAL, cbRfNominal);
        currencyValues.put(COLUMN_NAME_YAHOO_NOMINAL, yahooNominal);
        currencyValues.put(COLUMN_NAME_CUSTOM_NOMINAL, customNominal);

        currencyValues.put(COLUMN_NAME_CB_RF_RATE, cbRfRate);
        currencyValues.put(COLUMN_NAME_YAHOO_RATE, yahooRate);
        currencyValues.put(COLUMN_NAME_CUSTOM_RATE, customRate);

        currencyValues.put(COLUMN_NAME_CURRENCY_NAME, currencyName);
        currencyValues.put(COLUMN_NAME_FLAG_ID, flagId);

        currencyValues.put(COLUMN_NAME_CB_RF_SOURCE, cbRfProvides);
        currencyValues.put(COLUMN_NAME_YAHOO_SOURCE, yahooProvides);

        currencyValues.put(COLUMN_NAME_SWITCHING, switching);

        db.insert(TABLE_NAME, null, currencyValues);
    }
}
