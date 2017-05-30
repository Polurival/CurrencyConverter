package com.github.polurival.cc.model.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.github.polurival.cc.R;
import com.github.polurival.cc.util.Logger;
import com.github.polurival.cc.util.Toaster;

public class DBReaderTask extends DBTask {

    private Cursor cursor;

    @Override
    protected Boolean doInBackground(String... params) {
        Logger.logD(Logger.getTag(),
                String.format("doInBackground %s %s %s", params[0], params[1], params[2]));

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
                            DBHelper.COLUMN_NAME_CURRENCY_NAME,
                            DBHelper.COLUMN_NAME_FLAG_ID},
                    where,
                    null, null, null, null);
        } catch (SQLiteException e) {
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        Logger.logD(Logger.getTag(), "onPostExecute " + result.toString());

        if (result) {
            if (rateUpdaterListener != null) {
                rateUpdaterListener.setCommonCursor(cursor);

                rateUpdaterListener.initSpinners();
                rateUpdaterListener.loadSpinnersProperties();
                rateUpdaterListener.initTvDateTime();

                rateUpdaterListener.setPropertiesLoaded(true);
            }
        } else {
            Toaster.showToast(appContext.getString(R.string.db_reading_error));
        }
    }
}
