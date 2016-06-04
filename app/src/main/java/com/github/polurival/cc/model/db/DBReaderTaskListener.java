package com.github.polurival.cc.model.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Polurival
 * on 02.06.2016.
 */
public interface DBReaderTaskListener {

    void setCursorAndDB(Cursor cursor, SQLiteDatabase db);

    void initCustomSpinner();

    void readEditCurrencyDataFromDB();

}
