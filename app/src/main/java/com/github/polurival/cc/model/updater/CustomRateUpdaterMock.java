package com.github.polurival.cc.model.updater;

import android.content.Context;

import com.github.polurival.cc.R;
import com.github.polurival.cc.RateUpdaterListener;
import com.github.polurival.cc.model.db.DBHelper;
import com.github.polurival.cc.model.db.DBReaderTask;
import com.github.polurival.cc.model.dto.SpinnersPositions;
import com.github.polurival.cc.util.AppPreferences;

import org.joda.time.LocalDateTime;

public class CustomRateUpdaterMock extends CommonRateUpdater {

    @Override
    public void execute() {
        //do nothing
    }

    @Override
    public void setRateUpdaterListener(RateUpdaterListener rateUpdaterListener) {
        //do nothing
    }
    @Override
    protected void onPreExecute() {
        //do nothing
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        return null;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        //do nothing
    }

    @Override
    public void saveSelectedCurrencySpinnersPositions(Context context, int fromSpinnerSelectedPos, int toSpinnerSelectedPos) {
        AppPreferences.saveMainActivityCustomRateUpdaterSpinnersPositions(context, fromSpinnerSelectedPos, toSpinnerSelectedPos);
    }

    @Override
    public void saveUpDateTime(Context context, LocalDateTime upDateTime) {
        //do nothing
    }

    @Override
    public void readDataFromDB(DBReaderTask dbReaderTask) {
        dbReaderTask.execute(DBHelper.CUSTOM_SOURCE_MOCK,
                DBHelper.COLUMN_NAME_CUSTOM_NOMINAL,
                DBHelper.COLUMN_NAME_CUSTOM_RATE);
    }

    @Override
    public LocalDateTime loadUpDateTime(Context context) {
        return AppPreferences.loadCustomRateUpdaterUpDateTime(context);
    }

    @Override
    public int getDecimalScale() {
        return 6;
    }

    @Override
    public SpinnersPositions loadSpinnersPositions(Context context) {
        return AppPreferences.loadMainActivityCustomRateUpdaterSpinnersPositions(context);
    }

    @Override
    public <T> void fillCurrencyMapFromSource(T doc) {
        //do nothing
    }

    @Override
    public String getDescription() {
        return appContext.getString(R.string.custom);
    }
}
