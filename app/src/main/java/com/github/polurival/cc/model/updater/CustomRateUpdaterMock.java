package com.github.polurival.cc.model.updater;

import android.content.ContentValues;
import android.content.Context;

import com.github.polurival.cc.R;
import com.github.polurival.cc.model.dto.CurrenciesRelations;
import com.github.polurival.cc.model.dto.Currency;
import com.github.polurival.cc.model.db.DBOperations;
import com.github.polurival.cc.model.db.DBReaderTask;
import com.github.polurival.cc.model.dto.SpinnersPositions;
import com.github.polurival.cc.util.AppPreferences;
import com.github.polurival.cc.util.Toaster;

import org.joda.time.LocalDateTime;

import java.math.BigDecimal;

public class CustomRateUpdaterMock extends CommonRateUpdater {

    @Override
    protected void onPreExecute() {
        Toaster.showToast(appContext.getString(R.string.custom_updating_info));
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        rateUpdaterListener.stopRefresh();
        rateUpdaterListener.setMenuState(null);

    }

    @Override
    public void saveSelectedCurrencySpinnersPositions(Context context,
                                                      int fromSpinnerSelectedPos,
                                                      int toSpinnerSelectedPos) {
        AppPreferences.saveMainActivityCustomRateUpdaterSpinnersPositions(context, fromSpinnerSelectedPos, toSpinnerSelectedPos);
    }

    @Override
    public void saveUpDateTime(Context context, LocalDateTime upDateTime) {
        //do nothing
    }

    @Override
    public void readDataFromDB(DBReaderTask dbReaderTask) {
        dbReaderTask.execute(DBOperations.getColumnsForReadForCustomSource());
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
    public void fillContentValuesForUpdatingColumns(ContentValues contentValues,
                                                    Currency currency) {
        //do nothing
    }

    @Override
    public BigDecimal calculateConversionResult(CurrenciesRelations currenciesRelations,
                                                BigDecimal enteredAmountOfMoney) {
        return currenciesRelations.calculateConversionResultByDefault(enteredAmountOfMoney);
    }

    @Override
    public boolean isUpdateFromNetworkUnavailable() {
        return true;
    }

    @Override
    public String getDescription() {
        return appContext.getString(R.string.custom);
    }
}
