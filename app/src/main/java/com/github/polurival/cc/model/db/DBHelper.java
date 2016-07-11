package com.github.polurival.cc.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.github.polurival.cc.R;
import com.github.polurival.cc.model.CharCode;
import com.github.polurival.cc.util.Toaster;

/**
 * Created by Polurival
 * on 28.05.2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static Context appContext;
    private static DBHelper instance;

    private static final String DB_NAME = "converter";
    private static final int DB_VERSION = 5;

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
            appContext = context;
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
        if (oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            createAndFillDatabase(db);
        }
    }

    public static Cursor getSearchCursor(String charCode, String rateUpdaterClassName) {
        Cursor searchCursor = null;

        boolean custom = false;
        String sourceColumnName = null;
        if (appContext.getString(R.string.cb_rf_rate_updater_class)
                .equals(rateUpdaterClassName)) {
            sourceColumnName = COLUMN_NAME_CB_RF_SOURCE;

        } else if (appContext.getString(R.string.yahoo_rate_updater_class)
                .equals(rateUpdaterClassName)) {
            sourceColumnName = COLUMN_NAME_YAHOO_SOURCE;

        } else {
            custom = true;
        }

        try {
            String sqlQuery;
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
                        TABLE_NAME, sourceColumnName, COLUMN_NAME_SWITCHING, COLUMN_NAME_CHAR_CODE)
                        + "'%" + charCode + "%';";
            }

            searchCursor = instance.getReadableDatabase().rawQuery(sqlQuery, null);
        } catch (SQLiteException e) {
            Toaster.showCenterToast(appContext.getString(R.string.db_reading_error));
        }
        return searchCursor;
    }

    private void createAndFillDatabase(SQLiteDatabase db) {
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

        Resources res = appContext.getResources();

        insertCurrency(db, CharCode.AED, 0, 1, 1, 0, 3.672950, 3.672950, res.getString(R.string.aed), String.valueOf(R.drawable.aed), 0, 1, 1);
        insertCurrency(db, CharCode.AFN, 0, 1, 1, 0, 69.279999, 69.279999, res.getString(R.string.afn), String.valueOf(R.drawable.afn), 0, 1, 1);
        insertCurrency(db, CharCode.ALL, 0, 1, 1, 0, 122.419502, 122.419502, res.getString(R.string.all), String.valueOf(R.drawable.all), 0, 1, 1);
        insertCurrency(db, CharCode.AMD, 100, 1, 1, 13.5089, 478.820007, 478.820007, res.getString(R.string.amd), String.valueOf(R.drawable.amd), 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.ANG, 0, 1, 1, 0, 1.790000, 1.790000, res.getString(R.string.ang), String.valueOf(R.drawable.ang), 0, 1, 1);
        insertCurrency(db, CharCode.AOA, 0, 1, 1, 0, 165.733002, 165.733002, res.getString(R.string.aoa), String.valueOf(R.drawable.aoa), 0, 1, 1);
        insertCurrency(db, CharCode.ARS, 0, 1, 1, 0, 13.815950, 13.815950, res.getString(R.string.ars), String.valueOf(R.drawable.ars), 0, 1, 1);
        insertCurrency(db, CharCode.AUD, 1, 1, 1, 47.9031, 1.355730, 1.355730, res.getString(R.string.aud), String.valueOf(R.drawable.aud), 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.AWG, 0, 1, 1, 0, 1.790000, 1.790000, res.getString(R.string.awg), String.valueOf(R.drawable.awg), 0, 1, 1);
        insertCurrency(db, CharCode.AZN, 1, 1, 1, 42.5989, 1.521000, 1.521000, res.getString(R.string.azn), String.valueOf(R.drawable.azn), 1, 1, 1); //cb_rf

        insertCurrency(db, CharCode.BAM, 0, 1, 1, 0, 1.738400, 1.738400, res.getString(R.string.bam), String.valueOf(R.drawable.bam), 0, 1, 1);
        insertCurrency(db, CharCode.BBD, 0, 1, 1, 0, 2.000000, 2.000000, res.getString(R.string.bbd), String.valueOf(R.drawable.bbd), 0, 1, 1);
        insertCurrency(db, CharCode.BDT, 0, 1, 1, 0, 78.879448, 78.879448, res.getString(R.string.bdt), String.valueOf(R.drawable.bdt), 0, 1, 1);
        insertCurrency(db, CharCode.BGN, 1, 1, 1, 37.4141, 1.737200, 1.737200, res.getString(R.string.bgn), String.valueOf(R.drawable.bgn), 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.BHD, 0, 10, 10, 0, 3.76945, 3.76945, res.getString(R.string.bhd), String.valueOf(R.drawable.bhd), 0, 1, 1);
        insertCurrency(db, CharCode.BIF, 0, 1, 1, 0, 1560.000000, 1560.000000, res.getString(R.string.bif), String.valueOf(R.drawable.bif), 0, 1, 1);
        insertCurrency(db, CharCode.BMD, 0, 1, 1, 0, 1.000050, 1.000050, res.getString(R.string.bmd), String.valueOf(R.drawable.bmd), 0, 1, 1);
        insertCurrency(db, CharCode.BND, 0, 1, 1, 0, 1.356850, 1.356850, res.getString(R.string.bnd), String.valueOf(R.drawable.bnd), 0, 1, 1);
        insertCurrency(db, CharCode.BOB, 0, 1, 1, 0, 6.875000, 6.875000, res.getString(R.string.bob), String.valueOf(R.drawable.bob), 0, 1, 1);
        insertCurrency(db, CharCode.BRL, 1, 1, 1, 19.0300, 3.419900, 3.419900, res.getString(R.string.brl), String.valueOf(R.drawable.brl), 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.BSD, 0, 1, 1, 0, 1.002965, 1.002965, res.getString(R.string.bsd), String.valueOf(R.drawable.bsd), 0, 1, 1);
        insertCurrency(db, CharCode.BTN, 0, 1, 1, 0, 66.792503, 66.792503, res.getString(R.string.btn), String.valueOf(R.drawable.btn), 0, 1, 1);
        insertCurrency(db, CharCode.BWP, 0, 1, 1, 0, 10.888950, 10.888950, res.getString(R.string.bwp), String.valueOf(R.drawable.bwp), 0, 1, 1);
        insertCurrency(db, CharCode.BYN, 1, 1, 1, 31.8884, 2.002000, 2.002000, res.getString(R.string.byn), String.valueOf(R.drawable.byn), 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.BZD, 0, 1, 1, 0, 1.995000, 1.995000, res.getString(R.string.bzd), String.valueOf(R.drawable.bzd), 0, 1, 1);

        insertCurrency(db, CharCode.CAD, 1, 1, 1, 50.8069, 1.277900, 1.277900, res.getString(R.string.cad), String.valueOf(R.drawable.cad), 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.CDF, 0, 1, 1, 0, 928.000000, 928.000000, res.getString(R.string.cdf), String.valueOf(R.drawable.cdf), 0, 1, 1);
        insertCurrency(db, CharCode.CHF, 1, 10, 10, 67.1590, 9.64255, 9.64255, res.getString(R.string.chf), String.valueOf(R.drawable.chf), 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.CLF, 0, 100, 100, 0, 2.4600, 2.4600, res.getString(R.string.clf), String.valueOf(R.drawable.clp), 0, 1, 1);
        insertCurrency(db, CharCode.CLP, 0, 1, 1, 0, 675.900024, 675.900024, res.getString(R.string.clp), String.valueOf(R.drawable.clp), 0, 1, 1);
        insertCurrency(db, CharCode.CNH, 0, 1, 1, 0, 6.603550, 6.603550, res.getString(R.string.cnh), String.valueOf(R.drawable.cny), 0, 1, 1);
        insertCurrency(db, CharCode.CNY, 10, 1, 1, 98.6097, 6.562650, 6.562650, res.getString(R.string.cny), String.valueOf(R.drawable.cny), 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.COP, 0, 1, 1, 0, 2971.449951, 2971.449951, res.getString(R.string.cop), String.valueOf(R.drawable.cop), 0, 1, 1);
        insertCurrency(db, CharCode.CRC, 0, 1, 1, 0, 541.419983, 541.419983, res.getString(R.string.crc), String.valueOf(R.drawable.crc), 0, 1, 1);
        insertCurrency(db, CharCode.CUC, 0, 1, 1, 0, 0.995000, 0.995000, res.getString(R.string.cuc), String.valueOf(R.drawable.cup), 0, 1, 1);
        insertCurrency(db, CharCode.CUP, 0, 1, 1, 0, 1.000000, 1.000000, res.getString(R.string.cup), String.valueOf(R.drawable.cup), 0, 1, 1);
        insertCurrency(db, CharCode.CVE, 0, 1, 1, 0, 97.494003, 97.494003, res.getString(R.string.cve), String.valueOf(R.drawable.cve), 0, 1, 1);
        insertCurrency(db, CharCode.CZK, 10, 1, 1, 27.0834, 24.021500, 24.021500, res.getString(R.string.czk), String.valueOf(R.drawable.czk), 1, 1, 1); //cb_rf

        insertCurrency(db, CharCode.DJF, 0, 1, 1, 0, 177.770004, 177.770004, res.getString(R.string.djf), String.valueOf(R.drawable.djf), 0, 1, 1);
        insertCurrency(db, CharCode.DKK, 10, 1, 1, 98.4372, 6.609550, 6.609550, res.getString(R.string.dkk), String.valueOf(R.drawable.dkk), 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.DOP, 0, 1, 1, 0, 46.090000, 46.090000, res.getString(R.string.dop), String.valueOf(R.drawable.dop), 0, 1, 1);
        insertCurrency(db, CharCode.DZD, 0, 1, 1, 0, 109.830002, 109.830002, res.getString(R.string.dzd), String.valueOf(R.drawable.dzd), 0, 1, 1);

        insertCurrency(db, CharCode.EGP, 0, 1, 1, 0, 8.881450, 8.881450, res.getString(R.string.egp), String.valueOf(R.drawable.egp), 0, 1, 1);
        insertCurrency(db, CharCode.ERN, 0, 1, 1, 0, 16.180000, 16.180000, res.getString(R.string.ern), String.valueOf(R.drawable.ern), 0, 1, 1);
        insertCurrency(db, CharCode.ETB, 0, 1, 1, 0, 21.730000, 21.730000, res.getString(R.string.etb), String.valueOf(R.drawable.etb), 0, 1, 1);
        insertCurrency(db, CharCode.EUR, 1, 10, 10, 73.1909, 8.88613, 8.88613, res.getString(R.string.eur), String.valueOf(R.drawable.eur), 1, 1, 1); //cb_rf

        insertCurrency(db, CharCode.FJD, 0, 1, 1, 0, 2.081600, 2.081600, res.getString(R.string.fjd), String.valueOf(R.drawable.fjd), 0, 1, 1);
        insertCurrency(db, CharCode.FKP, 0, 10, 10, 0, 6.59800, 6.59800, res.getString(R.string.fkp), String.valueOf(R.drawable.fkp), 0, 1, 1);

        insertCurrency(db, CharCode.GBP, 1, 10, 10, 93.4185, 7.01484, 7.01484, res.getString(R.string.gbp), String.valueOf(R.drawable.gbp), 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.GEL, 0, 1, 1, 0, 2.130000, 2.130000, res.getString(R.string.gel), String.valueOf(R.drawable.gel), 0, 1, 1);
        insertCurrency(db, CharCode.GHS, 0, 1, 1, 0, 3.860000, 3.860000, res.getString(R.string.ghs), String.valueOf(R.drawable.ghs), 0, 1, 1);
        insertCurrency(db, CharCode.GIP, 0, 10, 10, 0, 7.71000, 7.71000, res.getString(R.string.gip), String.valueOf(R.drawable.gip), 0, 1, 1);
        insertCurrency(db, CharCode.GMD, 0, 1, 1, 0, 42.820000, 42.820000, res.getString(R.string.gmd), String.valueOf(R.drawable.gmd), 0, 1, 1);
        insertCurrency(db, CharCode.GNF, 0, 1, 1, 0, 7353.750000, 7353.750000, res.getString(R.string.gnf), String.valueOf(R.drawable.gnf), 0, 1, 1);
        insertCurrency(db, CharCode.GTQ, 0, 1, 1, 0, 7.671150, 7.671150, res.getString(R.string.gtq), String.valueOf(R.drawable.gtq), 0, 1, 1);
        insertCurrency(db, CharCode.GYD, 0, 1, 1, 0, 206.789948, 206.789948, res.getString(R.string.gyd), String.valueOf(R.drawable.gyd), 0, 1, 1);

        insertCurrency(db, CharCode.HKD, 0, 1, 1, 0, 7.762850, 7.762850, res.getString(R.string.hkd), String.valueOf(R.drawable.hkd), 0, 1, 1);
        insertCurrency(db, CharCode.HNL, 0, 1, 1, 0, 22.635950, 22.635950, res.getString(R.string.hnl), String.valueOf(R.drawable.hnl), 0, 1, 1);
        insertCurrency(db, CharCode.HRK, 0, 1, 1, 0, 6.701300, 6.701300, res.getString(R.string.hrk), String.valueOf(R.drawable.hrk), 0, 1, 1);
        insertCurrency(db, CharCode.HTG, 0, 1, 1, 0, 63.053951, 63.053951, res.getString(R.string.htg), String.valueOf(R.drawable.htg), 0, 1, 1);
        insertCurrency(db, CharCode.HUF, 100, 1, 1, 23.4929, 277.630005, 277.630005, res.getString(R.string.huf), String.valueOf(R.drawable.huf), 1, 1, 1); //cb_rf

        insertCurrency(db, CharCode.IDR, 0, 1, 1, 0, 13347.500000, 13347.500000, res.getString(R.string.idr), String.valueOf(R.drawable.idr), 0, 1, 1);
        insertCurrency(db, CharCode.ILS, 0, 1, 1, 0, 3.864200, 3.864200, res.getString(R.string.ils), String.valueOf(R.drawable.ils), 0, 1, 1);
        insertCurrency(db, CharCode.INR, 100, 1, 1, 96.8388, 66.964951, 66.964951, res.getString(R.string.inr), String.valueOf(R.drawable.inr), 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.IQD, 0, 1, 1, 0, 1171.399902, 1171.399902, res.getString(R.string.iqd), String.valueOf(R.drawable.iqd), 0, 1, 1);
        insertCurrency(db, CharCode.IRR, 0, 1, 1, 0, 30485.000000, 30485.000000, res.getString(R.string.irr), String.valueOf(R.drawable.irr), 0, 1, 1);
        insertCurrency(db, CharCode.ISK, 0, 1, 1, 0, 123.500000, 123.500000, res.getString(R.string.isk), String.valueOf(R.drawable.isk), 0, 1, 1);

        insertCurrency(db, CharCode.JMD, 0, 1, 1, 0, 125.414948, 125.414948, res.getString(R.string.jmd), String.valueOf(R.drawable.jmd), 0, 1, 1);
        insertCurrency(db, CharCode.JOD, 0, 10, 10, 0, 7.08250, 7.08250, res.getString(R.string.jod), String.valueOf(R.drawable.jod), 0, 1, 1);
        insertCurrency(db, CharCode.JPY, 100, 1, 1, 60.4604, 106.940002, 106.940002, res.getString(R.string.jpy), String.valueOf(R.drawable.jpy), 1, 1, 1); //cb_rf

        insertCurrency(db, CharCode.KES, 0, 1, 1, 0, 101.081497, 101.081497, res.getString(R.string.kes), String.valueOf(R.drawable.kes), 0, 1, 1);
        insertCurrency(db, CharCode.KGS, 100, 1, 1, 95.1422, 68.011597, 68.011597, res.getString(R.string.kgs), String.valueOf(R.drawable.kgs), 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.KHR, 0, 1, 1, 0, 4096.500000, 4096.500000, res.getString(R.string.khr), String.valueOf(R.drawable.khr), 0, 1, 1);
        insertCurrency(db, CharCode.KMF, 0, 1, 1, 0, 434.999939, 434.999939, res.getString(R.string.kmf), String.valueOf(R.drawable.kmf), 0, 1, 1);
        insertCurrency(db, CharCode.KPW, 0, 1, 1, 0, 900.000000, 900.000000, res.getString(R.string.kpw), String.valueOf(R.drawable.kpw), 0, 1, 1);
        insertCurrency(db, CharCode.KRW, 1000, 1, 1, 55.4969, 1171.880005, 1171.880005, res.getString(R.string.krw), String.valueOf(R.drawable.krw), 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.KWD, 0, 10, 10, 0, 3.01220, 3.01220, res.getString(R.string.kwd), String.valueOf(R.drawable.kwd), 0, 1, 1);
        insertCurrency(db, CharCode.KYD, 0, 10, 10, 0, 8.20000, 8.20000, res.getString(R.string.kyd), String.valueOf(R.drawable.kyd), 0, 1, 1);
        insertCurrency(db, CharCode.KZT, 100, 1, 1, 19.3227, 333.024963, 333.024963, res.getString(R.string.kzt), String.valueOf(R.drawable.kzt), 1, 1, 1); //cb_rf

        insertCurrency(db, CharCode.LAK, 0, 1, 1, 0, 8127.299805, 8127.299805, res.getString(R.string.lak), String.valueOf(R.drawable.lak), 0, 1, 1);
        insertCurrency(db, CharCode.LBP, 0, 1, 1, 0, 1511.949951, 1511.949951, res.getString(R.string.lbp), String.valueOf(R.drawable.lbp), 0, 1, 1);
        insertCurrency(db, CharCode.LKR, 0, 1, 1, 0, 146.100006, 146.100006, res.getString(R.string.lkr), String.valueOf(R.drawable.lkr), 0, 1, 1);
        insertCurrency(db, CharCode.LRD, 0, 1, 1, 0, 84.669998, 84.669998, res.getString(R.string.lrd), String.valueOf(R.drawable.lrd), 0, 1, 1);
        insertCurrency(db, CharCode.LSL, 0, 1, 1, 0, 14.874950, 14.874950, res.getString(R.string.lsl), String.valueOf(R.drawable.lsl), 0, 1, 1);
        insertCurrency(db, CharCode.LYD, 0, 1, 1, 0, 1.369000, 1.369000, res.getString(R.string.lyd), String.valueOf(R.drawable.lyd), 0, 1, 1);

        insertCurrency(db, CharCode.MAD, 0, 1, 1, 0, 9.681550, 9.681550, res.getString(R.string.mad), String.valueOf(R.drawable.mad), 0, 1, 1);
        insertCurrency(db, CharCode.MDL, 10, 1, 1, 32.7800, 19.776449, 19.776449, res.getString(R.string.mdl), String.valueOf(R.drawable.mdl), 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.MGA, 0, 1, 1, 0, 3247.449951, 3247.449951, res.getString(R.string.mga), String.valueOf(R.drawable.mga), 0, 1, 1);
        insertCurrency(db, CharCode.MKD, 0, 1, 1, 0, 54.499001, 54.499001, res.getString(R.string.mkd), String.valueOf(R.drawable.mkd), 0, 1, 1);
        insertCurrency(db, CharCode.MMK, 0, 1, 1, 0, 1196.449951, 1196.449951, res.getString(R.string.mmk), String.valueOf(R.drawable.mmk), 0, 1, 1);
        insertCurrency(db, CharCode.MNT, 0, 1, 1, 0, 1981.000000, 1981.000000, res.getString(R.string.mnt), String.valueOf(R.drawable.mnt), 0, 1, 1);
        insertCurrency(db, CharCode.MOP, 0, 1, 1, 0, 7.995550, 7.995550, res.getString(R.string.mop), String.valueOf(R.drawable.mop), 0, 1, 1);
        insertCurrency(db, CharCode.MRO, 0, 1, 1, 0, 357.369995, 357.369995, res.getString(R.string.mro), String.valueOf(R.drawable.mro), 0, 1, 1);
        insertCurrency(db, CharCode.MUR, 0, 1, 1, 0, 35.363998, 35.363998, res.getString(R.string.mur), String.valueOf(R.drawable.mur), 0, 1, 1);
        insertCurrency(db, CharCode.MWK, 0, 1, 1, 0, 710.715027, 710.715027, res.getString(R.string.mwk), String.valueOf(R.drawable.mwk), 0, 1, 1);
        insertCurrency(db, CharCode.MVR, 0, 1, 1, 0, 15.350000, 15.350000, res.getString(R.string.mvr), String.valueOf(R.drawable.mvr), 0, 1, 1);
        insertCurrency(db, CharCode.MXN, 0, 1, 1, 0, 18.634251, 18.634251, res.getString(R.string.mxn), String.valueOf(R.drawable.mxn), 0, 1, 1);
        insertCurrency(db, CharCode.MXV, 0, 1, 1, 0, 3.439188, 3.439188, res.getString(R.string.mxv), String.valueOf(R.drawable.mxn), 0, 1, 1);
        insertCurrency(db, CharCode.MYR, 0, 1, 1, 0, 4.083000, 4.083000, res.getString(R.string.myr), String.valueOf(R.drawable.myr), 0, 1, 1);
        insertCurrency(db, CharCode.MZN, 0, 1, 1, 0, 60.209999, 60.209999, res.getString(R.string.mzn), String.valueOf(R.drawable.mzn), 0, 1, 1);

        insertCurrency(db, CharCode.NAD, 0, 1, 1, 0, 14.874450, 14.874450, res.getString(R.string.nad), String.valueOf(R.drawable.nad), 0, 1, 1);
        insertCurrency(db, CharCode.NGN, 0, 1, 1, 0, 199.625000, 199.625000, res.getString(R.string.ngn), String.valueOf(R.drawable.ngn), 0, 1, 1);
        insertCurrency(db, CharCode.NIO, 0, 1, 1, 0, 28.538000, 28.538000, res.getString(R.string.nio), String.valueOf(R.drawable.nio), 0, 1, 1);
        insertCurrency(db, CharCode.NOK, 10, 1, 1, 79.1735, 8.274100, 8.274100, res.getString(R.string.nok), String.valueOf(R.drawable.nok), 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.NPR, 0, 1, 1, 0, 106.867996, 106.867996, res.getString(R.string.npr), String.valueOf(R.drawable.npr), 0, 1, 1);
        insertCurrency(db, CharCode.NZD, 0, 1, 1, 0, 1.417677, 1.417677, res.getString(R.string.nzd), String.valueOf(R.drawable.nzd), 0, 1, 1);

        insertCurrency(db, CharCode.OMR, 0, 10, 10, 0, 3.85025, 3.85025, res.getString(R.string.omr), String.valueOf(R.drawable.omr), 0, 1, 1);

        insertCurrency(db, CharCode.PAB, 0, 1, 1, 0, 1.002835, 1.002835, res.getString(R.string.pab), String.valueOf(R.drawable.pab), 0, 1, 1);
        insertCurrency(db, CharCode.PEN, 0, 1, 1, 0, 3.331000, 3.331000, res.getString(R.string.pen), String.valueOf(R.drawable.pen), 0, 1, 1);
        insertCurrency(db, CharCode.PGK, 0, 1, 1, 0, 3.166350, 3.166350, res.getString(R.string.pgk), String.valueOf(R.drawable.pgk), 0, 1, 1);
        insertCurrency(db, CharCode.PHP, 0, 1, 1, 0, 46.110500, 46.110500, res.getString(R.string.php), String.valueOf(R.drawable.php), 0, 1, 1);
        insertCurrency(db, CharCode.PKR, 0, 1, 1, 0, 104.580002, 104.580002, res.getString(R.string.pkr), String.valueOf(R.drawable.pkr), 0, 1, 1);
        insertCurrency(db, CharCode.PLN, 1, 1, 1, 16.8168, 3.890450, 3.890450, res.getString(R.string.pln), String.valueOf(R.drawable.pln), 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.PYG, 0, 1, 1, 0, 5668.740234, 5668.740234, res.getString(R.string.pyg), String.valueOf(R.drawable.pyg), 0, 1, 1);

        insertCurrency(db, CharCode.QAR, 0, 1, 1, 0, 3.640000, 3.640000, res.getString(R.string.qar), String.valueOf(R.drawable.qar), 0, 1, 1);

        insertCurrency(db, CharCode.RON, 1, 1, 1, 16.2232, 4.012850, 4.012850, res.getString(R.string.ron), String.valueOf(R.drawable.ron), 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.RSD, 0, 1, 1, 0, 109.504997, 109.504997, res.getString(R.string.rsd), String.valueOf(R.drawable.rsd), 0, 1, 1);
        insertCurrency(db, CharCode.RUB, 1, 1, 1, 1.0, 65.365997, 65.365997, res.getString(R.string.rub), String.valueOf(R.drawable.rub), 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.RWF, 0, 1, 1, 0, 764.994995, 764.994995, res.getString(R.string.rwf), String.valueOf(R.drawable.rwf), 0, 1, 1);

        insertCurrency(db, CharCode.SAR, 0, 1, 1, 0, 3.750100, 3.750100, res.getString(R.string.sar), String.valueOf(R.drawable.sar), 0, 1, 1);
        insertCurrency(db, CharCode.SBD, 0, 1, 1, 0, 7.799350, 7.799350, res.getString(R.string.sbd), String.valueOf(R.drawable.sbd), 0, 1, 1);
        insertCurrency(db, CharCode.SCR, 0, 1, 1, 0, 12.990950, 12.990950, res.getString(R.string.scr), String.valueOf(R.drawable.scr), 0, 1, 1);
        insertCurrency(db, CharCode.SDG, 0, 1, 1, 0, 6.084700, 6.084700, res.getString(R.string.sdg), String.valueOf(R.drawable.sdg), 0, 1, 1);
        insertCurrency(db, CharCode.SEK, 10, 1, 1, 78.8224, 8.308450, 8.308450, res.getString(R.string.sek), String.valueOf(R.drawable.sek), 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.SGD, 1, 1, 1, 47.7407, 1.360250, 1.360250, res.getString(R.string.sgd), String.valueOf(R.drawable.sgd), 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.SHP, 0, 10, 10, 0, 6.93900, 6.93900, res.getString(R.string.shp), String.valueOf(R.drawable.shp), 0, 1, 1);
        insertCurrency(db, CharCode.SLL, 0, 1, 1, 0, 3945.000000, 3945.000000, res.getString(R.string.sll), String.valueOf(R.drawable.sll), 0, 1, 1);
        insertCurrency(db, CharCode.SOS, 0, 1, 1, 0, 590.150024, 590.150024, res.getString(R.string.sos), String.valueOf(R.drawable.sos), 0, 1, 1);
        insertCurrency(db, CharCode.SRD, 0, 1, 1, 0, 6.983500, 6.983500, res.getString(R.string.srd), String.valueOf(R.drawable.srd), 0, 1, 1);
        insertCurrency(db, CharCode.STD, 0, 1, 1, 0, 21663.500000, 21663.500000, res.getString(R.string.std), String.valueOf(R.drawable.std), 0, 1, 1);
        insertCurrency(db, CharCode.SVC, 0, 1, 1, 0, 8.774950, 8.774950, res.getString(R.string.svc), String.valueOf(R.drawable.svc), 0, 1, 1);
        insertCurrency(db, CharCode.SYP, 0, 1, 1, 0, 219.856995, 219.856995, res.getString(R.string.syp), String.valueOf(R.drawable.syp), 0, 1, 1);
        insertCurrency(db, CharCode.SZL, 0, 1, 1, 0, 14.880000, 14.880000, res.getString(R.string.szl), String.valueOf(R.drawable.szl), 0, 1, 1);

        insertCurrency(db, CharCode.THB, 0, 1, 1, 0, 35.263500, 35.263500, res.getString(R.string.thb), String.valueOf(R.drawable.thb), 0, 1, 1);
        insertCurrency(db, CharCode.TJS, 10, 1, 1, 82.5353, 7.869000, 7.869000, res.getString(R.string.tjs), String.valueOf(R.drawable.tjs), 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.TMT, 1, 1, 1, 19.2068, 3.499950, 3.499950, res.getString(R.string.tmt), String.valueOf(R.drawable.tmt), 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.TND, 0, 1, 1, 0, 2.158550, 2.158550, res.getString(R.string.tnd), String.valueOf(R.drawable.tnd), 0, 1, 1);
        insertCurrency(db, CharCode.TOP, 0, 1, 1, 0, 2.194522, 2.194522, res.getString(R.string.top), String.valueOf(R.drawable.top), 0, 1, 1);
        insertCurrency(db, CharCode.TRY, 1, 1, 1, 22.2417, 2.928950, 2.928950, res.getString(R.string.try_), String.valueOf(R.drawable.try_), 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.TTD, 0, 1, 1, 0, 6.679100, 6.679100, res.getString(R.string.ttd), String.valueOf(R.drawable.ttd), 0, 1, 1);
        insertCurrency(db, CharCode.TWD, 0, 1, 1, 0, 32.324501, 32.324501, res.getString(R.string.twd), String.valueOf(R.drawable.twd), 0, 1, 1);
        insertCurrency(db, CharCode.TZS, 0, 1, 1, 0, 2199.350098, 2199.350098, res.getString(R.string.tzs), String.valueOf(R.drawable.tzs), 0, 1, 1);

        insertCurrency(db, CharCode.UAH, 10, 1, 1, 25.8624, 25.066950, 25.066950, res.getString(R.string.uah), String.valueOf(R.drawable.uah), 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.UGX, 0, 1, 1, 0, 3359.449951, 3359.449951, res.getString(R.string.ugx), String.valueOf(R.drawable.ugx), 0, 1, 1);
        insertCurrency(db, CharCode.USD, 1, 1, 1, 64.7077, 1.000000, 1.000000, res.getString(R.string.usd), String.valueOf(R.drawable.usd), 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.UYU, 0, 1, 1, 0, 30.775000, 30.775000, res.getString(R.string.uyu), String.valueOf(R.drawable.uyu), 0, 1, 1);
        insertCurrency(db, CharCode.UZS, 1000, 1, 1, 22.0319, 2929.139893, 2929.139893, res.getString(R.string.uzs), String.valueOf(R.drawable.uzs), 1, 1, 1); //cb_rf

        insertCurrency(db, CharCode.VEF, 0, 1, 1, 0, 9.950000, 9.950000, res.getString(R.string.vef), String.valueOf(R.drawable.vef), 0, 1, 1);
        insertCurrency(db, CharCode.VND, 0, 1, 1, 0, 22330.000000, 22330.000000, res.getString(R.string.vnd), String.valueOf(R.drawable.vnd), 0, 1, 1);
        insertCurrency(db, CharCode.VUV, 0, 1, 1, 0, 112.260002, 112.260002, res.getString(R.string.vuv), String.valueOf(R.drawable.vuv), 0, 1, 1);

        insertCurrency(db, CharCode.WST, 0, 1, 1, 0, 2.529086, 2.529086, res.getString(R.string.wst), String.valueOf(R.drawable.wst), 0, 1, 1);

        insertCurrency(db, CharCode.XAF, 0, 1, 1, 0, 583.021057, 583.021057, res.getString(R.string.xaf), "0", 0, 1, 1);
        insertCurrency(db, CharCode.XAG, 0, 100, 100, 0, 5.7753, 5.7753, res.getString(R.string.xag), String.valueOf(R.drawable.xag), 0, 1, 1);
        insertCurrency(db, CharCode.XAU, 0, 10000, 10000, 0, 7.85, 7.85, res.getString(R.string.xau), String.valueOf(R.drawable.xau), 0, 1, 1);
        insertCurrency(db, CharCode.XCD, 0, 1, 1, 0, 2.700000, 2.700000, res.getString(R.string.xcd), "0", 0, 1, 1);
        insertCurrency(db, CharCode.XCP, 0, 10, 10, 0, 4.92247, 4.92247, res.getString(R.string.xcp), String.valueOf(R.drawable.xcp), 0, 1, 1);
        insertCurrency(db, CharCode.XDR, 1, 10, 10, 91.4947, 7.10600, 7.10600, res.getString(R.string.xdr), "0", 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.XOF, 0, 1, 1, 0, 583.021057, 583.021057, res.getString(R.string.xof), "0", 0, 1, 1);
        insertCurrency(db, CharCode.XPD, 0, 1000, 1000, 0, 1.828, 1.828, res.getString(R.string.xpd), String.valueOf(R.drawable.xpd), 0, 1, 1);
        insertCurrency(db, CharCode.XPF, 0, 1, 1, 0, 106.063202, 106.063202, res.getString(R.string.xpf), "0", 0, 1, 1);
        insertCurrency(db, CharCode.XPT, 0, 1000, 1000, 0, 1.006, 1.006, res.getString(R.string.xpt), String.valueOf(R.drawable.xpt), 0, 1, 1);

        insertCurrency(db, CharCode.YER, 0, 1, 1, 0, 250.100006, 250.100006, res.getString(R.string.yer), String.valueOf(R.drawable.yer), 0, 1, 1);

        insertCurrency(db, CharCode.ZAR, 10, 1, 1, 43.3567, 15.231200, 15.231200, res.getString(R.string.zar), String.valueOf(R.drawable.zar), 1, 1, 1); //cb_rf
        insertCurrency(db, CharCode.ZMW, 0, 1, 1, 0, 10.732950, 10.732950, res.getString(R.string.zmw), String.valueOf(R.drawable.zmw), 0, 1, 1);
        insertCurrency(db, CharCode.ZWL, 0, 1, 1, 0, 322.355011, 322.355011, res.getString(R.string.zwl), String.valueOf(R.drawable.zwl), 0, 1, 1);
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
