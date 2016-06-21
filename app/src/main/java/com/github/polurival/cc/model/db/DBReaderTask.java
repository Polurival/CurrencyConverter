package com.github.polurival.cc.model.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;

import com.github.polurival.cc.R;

/**
 * Created by Polurival
 * on 29.05.2016.
 */
public class DBReaderTask extends DBTask {

    private Cursor cursor;

    @Override
    protected Boolean doInBackground(String... params) {
        String nominal = params[1];
        String rate = params[2];

        String switchClause = DBHelper.COLUMN_NAME_SWITCHING + " = 1";
        String where = DBHelper.CUSTOM_SOURCE_MOCK.equals(params[0]) ?
                switchClause :
                switchClause + " AND " + params[0] + " = 1";

        try {
            SQLiteDatabase db =
                    DBHelper.getInstance(appContext).getReadableDatabase();
            cursor = db.query(DBHelper.TABLE_NAME,
                    new String[]{DBHelper.COLUMN_NAME_ID,
                            DBHelper.COLUMN_NAME_CHAR_CODE,
                            nominal,
                            rate,
                            DBHelper.COLUMN_NAME_NAME_RESOURCE_ID,
                            DBHelper.COLUMN_NAME_FLAG_RESOURCE_ID},
                    where,
                    null, null, null, null);
        } catch (SQLiteException e) {
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            if (rateUpdaterListener != null) {
                rateUpdaterListener.setCursor(cursor);

                rateUpdaterListener.setMenuState(null);

                rateUpdaterListener.initSpinners();
                rateUpdaterListener.loadSpinnerProperties();
                rateUpdaterListener.initTvDateTime();

                rateUpdaterListener.setPropertiesLoaded(true);
            }
        } else {
            Toast.makeText(appContext, appContext.getString(R.string.db_reading_error),
                    Toast.LENGTH_SHORT)
                    .show();
        }

        assert rateUpdaterListener != null;
        rateUpdaterListener.setOnBackPressedListener(null);
    }
}
