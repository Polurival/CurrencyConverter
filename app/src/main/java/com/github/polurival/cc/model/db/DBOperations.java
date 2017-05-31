package com.github.polurival.cc.model.db;

import android.content.ContentValues;

import com.github.polurival.cc.model.dto.Currency;

public class DBOperations {

    public static String[] getColumnsForReadForCustomSource() {
        return new String[]{
                DBHelper.CUSTOM_SOURCE_MOCK,
                DBHelper.COLUMN_NAME_CUSTOM_NOMINAL,
                DBHelper.COLUMN_NAME_CUSTOM_RATE
        };
    }

    public static String[] getColumnsForReadForCBSource() {
        return new String[] {
                DBHelper.COLUMN_NAME_CB_RF_SOURCE,
                DBHelper.COLUMN_NAME_CB_RF_NOMINAL,
                DBHelper.COLUMN_NAME_CB_RF_RATE
        };
    }

    public static String[] getColumnsForReadForYahooSource() {
        return new String[] {
                DBHelper.COLUMN_NAME_YAHOO_SOURCE,
                DBHelper.COLUMN_NAME_YAHOO_NOMINAL,
                DBHelper.COLUMN_NAME_YAHOO_RATE
        };
    }

    public static void fillContentValuesForUpdatingCbRfColumns(ContentValues contentValues, Currency currency) {
        contentValues.put(DBHelper.COLUMN_NAME_CB_RF_NOMINAL, currency.getNominal());
        contentValues.put(DBHelper.COLUMN_NAME_CB_RF_RATE, currency.getRate());
    }

    public static void fillContentValuesForUpdatingYahooColumns(ContentValues contentValues, Currency currency) {
        contentValues.put(DBHelper.COLUMN_NAME_YAHOO_NOMINAL, currency.getNominal());
        contentValues.put(DBHelper.COLUMN_NAME_YAHOO_RATE, currency.getRate());
    }
}
