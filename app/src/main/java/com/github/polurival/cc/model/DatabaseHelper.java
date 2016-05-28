package com.github.polurival.cc.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.github.polurival.cc.R;

/**
 * Created by Polurival
 * on 28.05.2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "converter";
    private static final int DB_VERSION = 1;

    private static final String TABLE_NAME = "currency";
    private static final String COLUMN_NAME_ID = "_id";
    private static final String COLUMN_NAME_CHAR_CODE = "char_code";
    private static final String COLUMN_NAME_NOMINAL = "nominal";
    private static final String COLUMN_NAME_VALUE = "value";
    private static final String COLUMN_NAME_CUSTOM_VALUE = "custom_value";
    private static final String COLUMN_NAME_NAME_RESOURCE_ID = "name_resource_id";
    private static final String COLUMN_NAME_FLAG_RESOURCE_ID = "flag_resource_id";
    private static final String COLUMN_NAME_CB_RF = "cb_rf";

    public DatabaseHelper(Context context) {
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
                    COLUMN_NAME_CB_RF + " INTEGER);");

            insertCurrency(db, CharCode.RUB, 1, 1.0, 1.0, R.string.rub, R.drawable.rub, 1);
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
        currencyValues.put(COLUMN_NAME_CB_RF, cbRfProvides);

        db.insert(TABLE_NAME, null, currencyValues);
    }
}
