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

    public static final String COLUMN_NAME_CB_RF_RATE = "cb_rf_rate";
    public static final String COLUMN_NAME_YAHOO_RATE = "yahoo_rate";
    public static final String COLUMN_NAME_CUSTOM_RATE = "custom_rate";

    public static final String COLUMN_NAME_NAME_RESOURCE_ID = "name_resource_id";
    public static final String COLUMN_NAME_FLAG_RESOURCE_ID = "flag_resource_id";

    public static final String COLUMN_NAME_CB_RF_SOURCE = "cb_rf";
    public static final String COLUMN_NAME_YAHOO_SOURCE = "yahoo";

    public static final String COLUMN_NAME_SWITCHING = "switching";

    public static final String CUSTOM_SOURCE_MOCK = "custom_source";

    /**
     * See <a href="http://www.androiddesignpatterns.com/2012/05/correctly-managing-your-sqlite-database.html">source</a>
     */
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
                    COLUMN_NAME_CB_RF_RATE + " REAL, " +
                    COLUMN_NAME_YAHOO_RATE + " REAL, " +
                    COLUMN_NAME_CUSTOM_RATE + " REAL, " +
                    COLUMN_NAME_NAME_RESOURCE_ID + " INTEGER, " +
                    COLUMN_NAME_FLAG_RESOURCE_ID + " INTEGER, " +
                    COLUMN_NAME_CB_RF_SOURCE + " INTEGER, " +
                    COLUMN_NAME_YAHOO_SOURCE + " INTEGER, " +
                    COLUMN_NAME_SWITCHING + " INTEGER);");

            insertCurrency(db, CharCode.AED, 0, 1, 1, 0, 3.672950, 3.672950, R.string.aed, R.drawable.aed, 0, 1, 1);
            insertCurrency(db, CharCode.AFN, 0, 1, 1, 0, 69.279999, 69.279999, R.string.afn, R.drawable.afn, 0, 1, 1);
            insertCurrency(db, CharCode.ALL, 0, 1, 1, 0, 122.419502, 122.419502, R.string.all, R.drawable.all, 0, 1, 1);
            insertCurrency(db, CharCode.AMD, 100, 1, 1, 13.5089, 478.820007, 478.820007, R.string.amd, R.drawable.amd, 1, 1, 1); //cb_rf
            insertCurrency(db, CharCode.ANG, 0, 1, 1, 0, 1.790000, 1.790000, R.string.ang, 0, 0, 1, 1);
            insertCurrency(db, CharCode.AOA, 0, 1, 1, 0, 165.733002, 165.733002, R.string.aoa, R.drawable.aoa, 0, 1, 1);
            insertCurrency(db, CharCode.ARS, 0, 1, 1, 0, 13.815950, 13.815950, R.string.ars, R.drawable.ars, 0, 1, 1);
            insertCurrency(db, CharCode.AUD, 1, 1, 1, 47.9031, 1.355730, 1.355730, R.string.aud, R.drawable.aud, 1, 1, 1); //cb_rf
            insertCurrency(db, CharCode.AWG, 0, 1, 1, 0, 1.790000, 1.790000, R.string.awg, R.drawable.awg, 0, 1, 1);
            insertCurrency(db, CharCode.AZN, 1, 1, 1, 42.5989, 1.521000, 1.521000, R.string.azn, R.drawable.azn, 1, 1, 1); //cb_rf

            insertCurrency(db, CharCode.BAM, 0, 1, 1, 0, 1.738400, 1.738400, R.string.bam, R.drawable.bam, 0, 1, 1);
            insertCurrency(db, CharCode.BBD, 0, 1, 1, 0, 2.000000, 2.000000, R.string.bbd, R.drawable.bbd, 0, 1, 1);
            insertCurrency(db, CharCode.BDT, 0, 1, 1, 0, 78.879448, 78.879448, R.string.bdt, R.drawable.bdt, 0, 1, 1);
            insertCurrency(db, CharCode.BGN, 1, 1, 1, 37.4141, 1.737200, 1.737200, R.string.bgn, R.drawable.bgn, 1, 1, 1); //cb_rf
            insertCurrency(db, CharCode.BHD, 0, 10, 10, 0, 3.76945, 3.76945, R.string.bhd, R.drawable.bhd, 0, 1, 1);
            insertCurrency(db, CharCode.BIF, 0, 1, 1, 0, 1560.000000, 1560.000000, R.string.bif, R.drawable.bif, 0, 1, 1);
            insertCurrency(db, CharCode.BMD, 0, 1, 1, 0, 1.000050, 1.000050, R.string.bmd, R.drawable.bmd, 0, 1, 1);
            insertCurrency(db, CharCode.BND, 0, 1, 1, 0, 1.356850, 1.356850, R.string.bnd, R.drawable.bnd, 0, 1, 1);
            insertCurrency(db, CharCode.BOB, 0, 1, 1, 0, 6.875000, 6.875000, R.string.bob, R.drawable.bob, 0, 1, 1);
            insertCurrency(db, CharCode.BRL, 1, 1, 1, 19.0300, 3.419900, 3.419900, R.string.brl, R.drawable.brl, 1, 1, 1); //cb_rf
            insertCurrency(db, CharCode.BSD, 0, 1, 1, 0, 1.002965, 1.002965, R.string.bsd, R.drawable.bsd, 0, 1, 1);
            insertCurrency(db, CharCode.BTN, 0, 1, 1, 0, 66.792503, 66.792503, R.string.btn, R.drawable.btn, 0, 1, 1);
            insertCurrency(db, CharCode.BWP, 0, 1, 1, 0, 10.888950, 10.888950, R.string.bwp, R.drawable.bwp, 0, 1, 1);
            //insertCurrency(db, CharCode.BYN, 0, 0, 0, 0, 0, 0, R.string.byr, R.drawable.byr, 0, 0); c июля 2016
            insertCurrency(db, CharCode.BYR, 10000, 1, 1, 32.6600, 19800.000000, 19800.000000, R.string.byr, R.drawable.byr, 1, 1, 1); //cb_rf
            insertCurrency(db, CharCode.BZD, 0, 1, 1, 0, 1.995000, 1.995000, R.string.bzd, R.drawable.bzd, 0, 1, 1);

            insertCurrency(db, CharCode.CAD, 1, 1, 1, 50.8069, 1.277900, 1.277900, R.string.cad, R.drawable.cad, 1, 1, 1); //cb_rf
            insertCurrency(db, CharCode.CDF, 0, 1, 1, 0, 928.000000, 928.000000, R.string.cdf, R.drawable.cdf, 0, 1, 1);
            insertCurrency(db, CharCode.CHF, 1, 10, 10, 67.1590, 9.64255, 9.64255, R.string.chf, R.drawable.chf, 1, 1, 1); //cb_rf
            insertCurrency(db, CharCode.CLF, 0, 100, 100, 0, 2.4600, 2.4600, R.string.clf, R.drawable.clp, 0, 1, 1);
            insertCurrency(db, CharCode.CLP, 0, 1, 1, 0, 675.900024, 675.900024, R.string.clp, R.drawable.clp, 0, 1, 1);
            insertCurrency(db, CharCode.CNH, 0, 1, 1, 0, 6.603550, 6.603550, R.string.cnh, R.drawable.cny, 0, 1, 1);
            insertCurrency(db, CharCode.CNY, 10, 1, 1, 98.6097, 6.562650, 6.562650, R.string.cny, R.drawable.cny, 1, 1, 1); //cb_rf
            insertCurrency(db, CharCode.COP, 0, 1, 1, 0, 2971.449951, 2971.449951, R.string.cop, R.drawable.cop, 0, 1, 1);
            insertCurrency(db, CharCode.CRC, 0, 1, 1, 0, 541.419983, 541.419983, R.string.crc, R.drawable.crc, 0, 1, 1);
            insertCurrency(db, CharCode.CUP, 0, 1, 1, 0, 1.000000, 1.000000, R.string.cup, R.drawable.cup, 0, 1, 1);
            insertCurrency(db, CharCode.CVE, 0, 1, 1, 0, 97.494003, 97.494003, R.string.cve, R.drawable.cve, 0, 1, 1);
            insertCurrency(db, CharCode.CZK, 10, 1, 1, 27.0834, 24.021500, 24.021500, R.string.czk, R.drawable.czk, 1, 1, 1); //cb_rf

            insertCurrency(db, CharCode.DJF, 0, 1, 1, 0, 177.770004, 177.770004, R.string.djf, R.drawable.djf, 0, 1, 1);
            insertCurrency(db, CharCode.DKK, 10, 1, 1, 98.4372, 6.609550, 6.609550, R.string.dkk, R.drawable.dkk, 1, 1, 1); //cb_rf
            insertCurrency(db, CharCode.DOP, 0, 1, 1, 0, 46.090000, 46.090000, R.string.dop, R.drawable.dop, 0, 1, 1);
            insertCurrency(db, CharCode.DZD, 0, 1, 1, 0, 109.830002, 109.830002, R.string.dzd, R.drawable.dzd, 0, 1, 1);

            insertCurrency(db, CharCode.EGP, 0, 1, 1, 0, 8.881450, 8.881450, R.string.egp, R.drawable.egp, 0, 1, 1);
            insertCurrency(db, CharCode.ERN, 0, 1, 1, 0, 16.180000, 16.180000, R.string.ern, R.drawable.ern, 0, 1, 1);
            insertCurrency(db, CharCode.ETB, 0, 1, 1, 0, 21.730000, 21.730000, R.string.etb, R.drawable.etb, 0, 1, 1);
            insertCurrency(db, CharCode.EUR, 1, 10, 10, 73.1909, 8.88613, 8.88613, R.string.eur, R.drawable.eur, 1, 1, 1); //cb_rf

            insertCurrency(db, CharCode.FJD, 0, 1, 1, 0, 2.081600, 2.081600, R.string.fjd, R.drawable.fjd, 0, 1, 1);
            insertCurrency(db, CharCode.FKP, 0, 10, 10, 0, 6.59800, 6.59800, R.string.fkp, R.drawable.fkp, 0, 1, 1);

            insertCurrency(db, CharCode.GBP, 1, 10, 10, 93.4185, 7.01484, 7.01484, R.string.gbp, R.drawable.gbp, 1, 1 , 1); //cb_rf
            insertCurrency(db, CharCode.GEL, 0, 1, 1, 0, 2.130000, 2.130000, R.string.gel, R.drawable.gel, 0, 1, 1);
            insertCurrency(db, CharCode.GHS, 0, 1, 1, 0, 3.860000, 3.860000, R.string.ghs, R.drawable.ghs, 0, 1, 1);
            insertCurrency(db, CharCode.GIP, 0, 10, 10, 0, 7.71000, 7.71000, R.string.gip, R.drawable.gip, 0, 1, 1);
            insertCurrency(db, CharCode.GMD, 0, 1, 1, 0, 42.820000, 42.820000, R.string.gmd, R.drawable.gmd, 0, 1, 1);
            insertCurrency(db, CharCode.GNF, 0, 1, 1, 0, 7353.750000, 7353.750000, R.string.gnf, R.drawable.gnf, 0, 1, 1);
            insertCurrency(db, CharCode.GTQ, 0, 1, 1, 0, 7.671150, 7.671150, R.string.gtq, R.drawable.gtq, 0, 1, 1);
            insertCurrency(db, CharCode.GYD, 0, 1, 1, 0, 206.789948, 206.789948, R.string.gyd, R.drawable.gyd, 0, 1, 1);

            insertCurrency(db, CharCode.HKD, 0, 1, 1, 0, 7.762850, 7.762850, R.string.hkd, R.drawable.hkd, 0, 1, 1);
            insertCurrency(db, CharCode.HNL, 0, 1, 1, 0, 22.635950, 22.635950, R.string.hnl, R.drawable.hnl, 0, 1, 1);
            insertCurrency(db, CharCode.HRK, 0, 1, 1, 0, 6.701300, 6.701300, R.string.hrk, R.drawable.hrk, 0, 1, 1);
            insertCurrency(db, CharCode.HTG, 0, 1, 1, 0, 63.053951, 63.053951, R.string.htg, R.drawable.htg, 0, 1, 1);
            insertCurrency(db, CharCode.HUF, 100, 1, 1, 23.4929, 277.630005, 277.630005, R.string.huf, R.drawable.huf, 1, 1, 1); //cb_rf

            insertCurrency(db, CharCode.IDR, 0, 1, 1, 0, 13347.500000, 13347.500000, R.string.idr, R.drawable.idr, 0, 1, 1);
            insertCurrency(db, CharCode.ILS, 0, 1, 1, 0, 3.864200, 3.864200, R.string.ils, R.drawable.ils, 0, 1, 1);
            insertCurrency(db, CharCode.INR, 100, 1, 1, 96.8388, 66.964951, 66.964951, R.string.inr, R.drawable.inr, 1, 1, 1); //cb_rf
            insertCurrency(db, CharCode.IQD, 0, 1, 1, 0, 1171.399902, 1171.399902, R.string.iqd, R.drawable.iqd, 0, 1, 1);
            insertCurrency(db, CharCode.IRR, 0, 1, 1, 0, 30485.000000, 30485.000000, R.string.irr, R.drawable.irr, 0, 1, 1);
            insertCurrency(db, CharCode.ISK, 0, 1, 1, 0, 123.500000, 123.500000, R.string.isk, R.drawable.isk, 0, 1, 1);

            insertCurrency(db, CharCode.JMD, 0, 1, 1, 0, 125.414948, 125.414948, R.string.jmd, R.drawable.jmd, 0, 1, 1);
            insertCurrency(db, CharCode.JOD, 0, 10, 10, 0, 7.08250, 7.08250, R.string.jod, R.drawable.jod, 0, 1, 1);
            insertCurrency(db, CharCode.JPY, 100, 1, 1, 60.4604, 106.940002, 106.940002, R.string.jpy, R.drawable.jpy, 1, 1, 1); //cb_rf

            insertCurrency(db, CharCode.KES, 0, 1, 1, 0, 101.081497, 101.081497, R.string.kes, R.drawable.kes, 0, 1, 1);
            insertCurrency(db, CharCode.KGS, 100, 1, 1, 95.1422, 68.011597, 68.011597, R.string.kgs, R.drawable.kgs, 1, 1, 1); //cb_rf
            insertCurrency(db, CharCode.KHR, 0, 1, 1, 0, 4096.500000, 4096.500000, R.string.khr, R.drawable.khr, 0, 1, 1);
            insertCurrency(db, CharCode.KMF, 0, 1, 1, 0, 434.999939, 434.999939, R.string.kmf, R.drawable.kmf, 0, 1, 1);
            insertCurrency(db, CharCode.KPW, 0, 1, 1, 0, 900.000000, 900.000000, R.string.kpw, R.drawable.kpw, 0, 1, 1);
            insertCurrency(db, CharCode.KRW, 1000, 1, 1, 55.4969, 1171.880005, 1171.880005, R.string.krw, R.drawable.krw, 1, 1, 1); //cb_rf
            insertCurrency(db, CharCode.KWD, 0, 10, 10, 0, 3.01220, 3.01220, R.string.kwd, R.drawable.kwd, 0, 1, 1);
            insertCurrency(db, CharCode.KYD, 0, 10, 10, 0, 8.20000, 8.20000, R.string.kyd, R.drawable.kyd, 0, 1, 1);
            insertCurrency(db, CharCode.KZT, 100, 1, 1, 19.3227, 333.024963, 333.024963, R.string.kzt, R.drawable.kzt, 1, 1, 1); //cb_rf

            insertCurrency(db, CharCode.LAK, 0, 1, 1, 0, 8127.299805, 8127.299805, R.string.lak, R.drawable.lak, 0, 1, 1);
            insertCurrency(db, CharCode.LBP, 0, 1, 1, 0, 1511.949951, 1511.949951, R.string.lbp, R.drawable.lbp, 0, 1, 1);
            insertCurrency(db, CharCode.LKR, 0, 1, 1, 0, 146.100006, 146.100006, R.string.lkr, R.drawable.lkr, 0, 1, 1);
            insertCurrency(db, CharCode.LRD, 0, 1, 1, 0, 84.669998, 84.669998, R.string.lrd, R.drawable.lrd, 0, 1, 1);
            insertCurrency(db, CharCode.LSL, 0, 1, 1, 0, 14.874950, 14.874950, R.string.lsl, R.drawable.lsl, 0, 1, 1);
            insertCurrency(db, CharCode.LYD, 0, 1, 1, 0, 1.369000, 1.369000, R.string.lyd, R.drawable.lyd, 0, 1, 1);

            insertCurrency(db, CharCode.MAD, 0, 1, 1, 0, 9.681550, 9.681550, R.string.mad, R.drawable.mad, 0, 1, 1);
            insertCurrency(db, CharCode.MDL, 10, 1, 1, 32.7800, 19.776449, 19.776449, R.string.mdl, R.drawable.mdl, 1, 1, 1); //cb_rf
            insertCurrency(db, CharCode.MGA, 0, 1, 1, 0, 3247.449951, 3247.449951, R.string.mga, R.drawable.mga, 0, 1, 1);
            insertCurrency(db, CharCode.MKD, 0, 1, 1, 0, 54.499001, 54.499001, R.string.mkd, R.drawable.mkd, 0, 1, 1);
            insertCurrency(db, CharCode.MMK, 0, 1, 1, 0, 1196.449951, 1196.449951, R.string.mmk, R.drawable.mmk, 0, 1, 1);
            insertCurrency(db, CharCode.MNT, 0, 1, 1, 0, 1981.000000, 1981.000000, R.string.mnt, R.drawable.mnt, 0, 1, 1);
            insertCurrency(db, CharCode.MOP, 0, 1, 1, 0, 7.995550, 7.995550, R.string.mop, R.drawable.mop, 0, 1, 1);
            insertCurrency(db, CharCode.MRO, 0, 1, 1, 0, 357.369995, 357.369995, R.string.mro, R.drawable.mro, 0, 1, 1);
            insertCurrency(db, CharCode.MUR, 0, 1, 1, 0, 35.363998, 35.363998, R.string.mur, R.drawable.mur, 0, 1, 1);
            insertCurrency(db, CharCode.MWK, 0, 1, 1, 0, 710.715027, 710.715027, R.string.mwk, R.drawable.mwk, 0, 1, 1);
            insertCurrency(db, CharCode.MVR, 0, 1, 1, 0, 15.350000, 15.350000, R.string.mvr, R.drawable.mvr, 0, 1, 1);
            insertCurrency(db, CharCode.MXN, 0, 1, 1, 0, 18.634251, 18.634251, R.string.mxn, R.drawable.mxn, 0, 1, 1);
            insertCurrency(db, CharCode.MXV, 0, 1, 1, 0, 3.439188, 3.439188, R.string.mxv, R.drawable.mxn, 0, 1, 1);
            insertCurrency(db, CharCode.MYR, 0, 1, 1, 0, 4.083000, 4.083000, R.string.myr, R.drawable.myr, 0, 1, 1);
            insertCurrency(db, CharCode.MZN, 0, 1, 1, 0, 60.209999, 60.209999, R.string.mzn, R.drawable.mzn, 0, 1, 1);

            insertCurrency(db, CharCode.NAD, 0, 1, 1, 0, 14.874450, 14.874450, R.string.nad, R.drawable.nad, 0, 1, 1);
            insertCurrency(db, CharCode.NGN, 0, 1, 1, 0, 199.625000, 199.625000, R.string.ngn, R.drawable.ngn, 0, 1, 1);
            insertCurrency(db, CharCode.NIO, 0, 1, 1, 0, 28.538000, 28.538000, R.string.nio, R.drawable.nio, 0, 1, 1);
            insertCurrency(db, CharCode.NOK, 10, 1, 1, 79.1735, 8.274100, 8.274100, R.string.nok, R.drawable.nok, 1, 1, 1); //cb_rf
            insertCurrency(db, CharCode.NPR, 0, 1, 1, 0, 106.867996, 106.867996, R.string.npr, R.drawable.npr, 0, 1, 1);
            insertCurrency(db, CharCode.NZD, 0, 1, 1, 0, 1.417677, 1.417677, R.string.nzd, R.drawable.nzd, 0, 1, 1);

            insertCurrency(db, CharCode.OMR, 0, 10, 10, 0, 3.85025, 3.85025, R.string.omr, R.drawable.omr, 0, 1, 1);

            insertCurrency(db, CharCode.PAB, 0, 1, 1, 0, 1.002835, 1.002835, R.string.pab, R.drawable.pab, 0, 1, 1);
            insertCurrency(db, CharCode.PEN, 0, 1, 1, 0, 3.331000, 3.331000, R.string.pen, R.drawable.pen, 0, 1, 1);
            insertCurrency(db, CharCode.PGK, 0, 1, 1, 0, 3.166350, 3.166350, R.string.pgk, R.drawable.pgk, 0, 1, 1);
            insertCurrency(db, CharCode.PHP, 0, 1, 1, 0, 46.110500, 46.110500, R.string.php, R.drawable.php, 0, 1, 1);
            insertCurrency(db, CharCode.PKR, 0, 1, 1, 0, 104.580002, 104.580002, R.string.pkr, R.drawable.pkr, 0, 1, 1);
            insertCurrency(db, CharCode.PLN, 1, 1, 1, 16.8168, 3.890450, 3.890450, R.string.pln, R.drawable.pln, 1, 1, 1); //cb_rf
            insertCurrency(db, CharCode.PYG, 0, 1, 1, 0, 5668.740234, 5668.740234, R.string.pyg, R.drawable.pyg, 0, 1, 1);

            insertCurrency(db, CharCode.QAR, 0, 1, 1, 0, 3.640000, 3.640000, R.string.qar, R.drawable.qar, 0, 1, 1);

            insertCurrency(db, CharCode.RON, 1, 1, 1, 16.2232, 4.012850, 4.012850, R.string.ron, R.drawable.ron, 1, 1, 1); //cb_rf
            insertCurrency(db, CharCode.RSD, 0, 1, 1, 0, 109.504997, 109.504997, R.string.rsd, R.drawable.rsd, 0, 1, 1);
            insertCurrency(db, CharCode.RUB, 1, 1, 1, 1.0, 65.365997, 65.365997, R.string.rub, R.drawable.rub, 1, 1, 1); //cb_rf
            insertCurrency(db, CharCode.RWF, 0, 1, 1, 0, 764.994995, 764.994995, R.string.rwf, R.drawable.rwf, 0, 1, 1);

            insertCurrency(db, CharCode.SAR, 0, 1, 1, 0, 3.750100, 3.750100, R.string.sar, R.drawable.sar, 0, 1, 1);
            insertCurrency(db, CharCode.SBD, 0, 1, 1, 0, 7.799350, 7.799350, R.string.sbd, R.drawable.sbd, 0, 1, 1);
            insertCurrency(db, CharCode.SCR, 0, 1, 1, 0, 12.990950, 12.990950, R.string.scr, R.drawable.scr, 0, 1, 1);
            insertCurrency(db, CharCode.SDG, 0, 1, 1, 0, 6.084700, 6.084700, R.string.sdg, R.drawable.sdg, 0, 1, 1);
            insertCurrency(db, CharCode.SEK, 10, 1, 1, 78.8224, 8.308450, 8.308450, R.string.sek, R.drawable.sek, 1, 1, 1); //cb_rf
            insertCurrency(db, CharCode.SGD, 1, 1, 1, 47.7407, 1.360250, 1.360250, R.string.sgd, R.drawable.sgd, 1, 1, 1); //cb_rf
            insertCurrency(db, CharCode.SHP, 0, 10, 10, 0, 6.93900, 6.93900, R.string.shp, R.drawable.shp, 0, 1, 1);
            insertCurrency(db, CharCode.SLL, 0, 1, 1, 0, 3945.000000, 3945.000000, R.string.sll, R.drawable.sll, 0, 1, 1);
            insertCurrency(db, CharCode.SOS, 0, 1, 1, 0, 590.150024, 590.150024, R.string.sos, R.drawable.sos, 0, 1, 1);
            insertCurrency(db, CharCode.SRD, 0, 1, 1, 0, 6.983500, 6.983500, R.string.srd, R.drawable.srd, 0, 1, 1);
            insertCurrency(db, CharCode.STD, 0, 1, 1, 0, 21663.500000, 21663.500000, R.string.std, R.drawable.std, 0, 1, 1);
            insertCurrency(db, CharCode.SVC, 0, 1, 1, 0, 8.774950, 8.774950, R.string.svc, R.drawable.svc, 0, 1, 1);
            insertCurrency(db, CharCode.SYP, 0, 1, 1, 0, 219.856995, 219.856995, R.string.syp, R.drawable.syp, 0, 1, 1);
            insertCurrency(db, CharCode.SZL, 0, 1, 1, 0, 14.880000, 14.880000, R.string.szl, R.drawable.szl, 0, 1, 1);

            insertCurrency(db, CharCode.THB, 0, 1, 1, 0, 35.263500, 35.263500, R.string.thb, R.drawable.thb, 0, 1, 1);
            insertCurrency(db, CharCode.TJS, 10, 1, 1, 82.5353, 7.869000, 7.869000, R.string.tjs, R.drawable.tjs, 1, 1, 1); //cb_rf
            insertCurrency(db, CharCode.TMT, 1, 1, 1, 19.2068, 3.499950, 3.499950, R.string.tmt, R.drawable.tmt, 1, 1, 1); //cb_rf
            insertCurrency(db, CharCode.TND, 0, 1, 1, 0, 2.158550, 2.158550, R.string.tnd, R.drawable.tnd, 0, 1, 1);
            insertCurrency(db, CharCode.TOP, 0, 1, 1, 0, 2.194522, 2.194522, R.string.top, R.drawable.top, 0, 1, 1);
            insertCurrency(db, CharCode.TRY, 1, 1, 1, 22.2417, 2.928950, 2.928950, R.string.try_, R.drawable.try_, 1, 1, 1); //cb_rf
            insertCurrency(db, CharCode.TTD, 0, 1, 1, 0, 6.679100, 6.679100, R.string.ttd, R.drawable.ttd, 0, 1, 1);
            insertCurrency(db, CharCode.TWD, 0, 1, 1, 0, 32.324501, 32.324501, R.string.twd, R.drawable.twd, 0, 1, 1);
            insertCurrency(db, CharCode.TZS, 0, 1, 1, 0, 2199.350098, 2199.350098, R.string.tzs, R.drawable.tzs, 0, 1, 1);

            insertCurrency(db, CharCode.UAH, 10, 1, 1, 25.8624, 25.066950, 25.066950, R.string.uah, R.drawable.uah, 1, 1, 1); //cb_rf
            insertCurrency(db, CharCode.UGX, 0, 1, 1, 0, 3359.449951, 3359.449951, R.string.ugx, R.drawable.ugx, 0, 1, 1);
            insertCurrency(db, CharCode.USD, 1, 1, 1, 64.7077, 1.000000, 1.000000, R.string.usd, R.drawable.usd, 1, 1, 1); //cb_rf
            insertCurrency(db, CharCode.UYU, 0, 1, 1, 0, 30.775000, 30.775000, R.string.uyu, R.drawable.uyu, 0, 1, 1);
            insertCurrency(db, CharCode.UZS, 1000, 1, 1, 22.0319, 2929.139893, 2929.139893, R.string.uzs, R.drawable.uzs, 1, 1, 1); //cb_rf

            insertCurrency(db, CharCode.VEF, 0, 1, 1, 0, 9.950000, 9.950000, R.string.vef, R.drawable.vef, 0, 1, 1);
            insertCurrency(db, CharCode.VND, 0, 1, 1, 0, 22330.000000, 22330.000000, R.string.vnd, R.drawable.vnd, 0, 1, 1);
            insertCurrency(db, CharCode.VUV, 0, 1, 1, 0, 112.260002, 112.260002, R.string.vuv, R.drawable.vuv, 0, 1, 1);

            insertCurrency(db, CharCode.WST, 0, 1, 1, 0, 2.529086, 2.529086, R.string.wst, R.drawable.wst, 0, 1, 1);

            insertCurrency(db, CharCode.XAF, 0, 1, 1, 0, 583.021057, 583.021057, R.string.xaf, 0, 0, 1, 1);
            insertCurrency(db, CharCode.XAG, 0, 100, 100, 0, 5.7753, 5.7753, R.string.xag, 0, 0, 1, 1);
            insertCurrency(db, CharCode.XAU, 0, 10000, 10000, 0, 7.85, 7.85, R.string.xau, 0, 0, 1, 1);
            insertCurrency(db, CharCode.XCD, 0, 1, 1, 0, 2.700000, 2.700000, R.string.xcd, 0, 0, 1, 1);
            insertCurrency(db, CharCode.XCP, 0, 10, 10, 0, 4.92247, 4.92247, R.string.xcp, 0, 0, 1, 1);
            insertCurrency(db, CharCode.XDR, 1, 10, 10, 91.4947, 7.10600, 7.10600, R.string.xdr, 0, 1, 1, 1); //cb_rf
            insertCurrency(db, CharCode.XOF, 0, 1, 1, 0, 583.021057, 583.021057, R.string.xof, 0, 0, 1, 1);
            insertCurrency(db, CharCode.XPD, 0, 1000, 1000, 0, 1.828, 1.828, R.string.xpd, 0, 0, 1, 1);
            insertCurrency(db, CharCode.XPF, 0, 1, 1, 0, 106.063202, 106.063202, R.string.xpf, 0, 0, 1, 1);
            insertCurrency(db, CharCode.XPT, 0, 1000, 1000, 0, 1.006, 1.006, R.string.xpt, 0, 0, 1, 1);

            insertCurrency(db, CharCode.YER, 0, 1, 1, 0, 250.100006, 250.100006, R.string.yer, R.drawable.yer, 0, 1, 1);

            insertCurrency(db, CharCode.ZAR, 10, 1, 1, 43.3567, 15.231200, 15.231200, R.string.zar, R.drawable.zar, 1, 1, 1); //cb_rf
            insertCurrency(db, CharCode.ZMW, 0, 1, 1, 0, 10.732950, 10.732950, R.string.zmw, R.drawable.zmw, 0, 1, 1);
            insertCurrency(db, CharCode.ZWL, 0, 1, 1, 0, 322.355011, 322.355011, R.string.zwl, R.drawable.zwl, 0, 1, 1);
        }
    }

    private static void insertCurrency(SQLiteDatabase db, Enum charCode,
                                       int cbRfNominal,int yahooNominal, int customNominal,
                                       double cbRfRate, double yahooRate, double customRate,
                                       int nameResourceId, int flagResourceId,
                                       int cbRfProvides, int yahooProvides, int switching) {

        ContentValues currencyValues = new ContentValues();
        currencyValues.put(COLUMN_NAME_CHAR_CODE, charCode.toString());

        currencyValues.put(COLUMN_NAME_CB_RF_NOMINAL, cbRfNominal);
        currencyValues.put(COLUMN_NAME_YAHOO_NOMINAL, yahooNominal);
        currencyValues.put(COLUMN_NAME_CUSTOM_NOMINAL, customNominal);

        currencyValues.put(COLUMN_NAME_CB_RF_RATE, cbRfRate);
        currencyValues.put(COLUMN_NAME_YAHOO_RATE, yahooRate);
        currencyValues.put(COLUMN_NAME_CUSTOM_RATE, customRate);

        currencyValues.put(COLUMN_NAME_NAME_RESOURCE_ID, nameResourceId);
        currencyValues.put(COLUMN_NAME_FLAG_RESOURCE_ID, flagResourceId);

        currencyValues.put(COLUMN_NAME_CB_RF_SOURCE, cbRfProvides);
        currencyValues.put(COLUMN_NAME_YAHOO_SOURCE, yahooProvides);

        currencyValues.put(COLUMN_NAME_SWITCHING, switching);

        db.insert(TABLE_NAME, null, currencyValues);
    }
}
