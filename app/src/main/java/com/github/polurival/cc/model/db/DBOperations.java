package com.github.polurival.cc.model.db;

import android.content.ContentValues;

import com.github.polurival.cc.model.Currency;

public class DBOperations {

    public static void fillContentValuesForUpdatingCbRfColumns(ContentValues contentValues, Currency currency) {
        contentValues.put(DBHelper.COLUMN_NAME_CB_RF_NOMINAL, currency.getNominal());
        contentValues.put(DBHelper.COLUMN_NAME_CB_RF_RATE, currency.getRate());
    }

    public static void fillContentValuesForUpdatingYahooColumns(ContentValues contentValues, Currency currency) {
        contentValues.put(DBHelper.COLUMN_NAME_YAHOO_NOMINAL, currency.getNominal());
        contentValues.put(DBHelper.COLUMN_NAME_YAHOO_RATE, currency.getRate());
    }
}
