package com.github.polurival.cc.model.updater;

import android.content.ContentValues;
import android.content.Context;

import com.github.polurival.cc.RateUpdaterListener;
import com.github.polurival.cc.model.Currency;
import com.github.polurival.cc.model.db.DBReaderTask;
import com.github.polurival.cc.model.dto.SpinnersPositions;

import org.joda.time.LocalDateTime;

import java.io.IOException;
import java.io.InputStream;

public interface RateUpdater {

    /**
     * See <a href="http://stackoverflow.com/a/24788257/5349748">Source</a>
     */
    void execute();

    InputStream getDataInputStream(String url) throws IOException;

    /**
     * Set Activity that get data from RateUpdater
     */
    void setRateUpdaterListener(RateUpdaterListener rateUpdaterListener);

    /**
     * return RateUpdater source name
     */
    String getDescription();

    void saveSelectedCurrencySpinnersPositions(Context context,
                                               int fromSpinnerSelectedPos,
                                               int toSpinnerSelectedPos);

    void saveUpDateTime(Context context, LocalDateTime upDateTime);

    void readDataFromDB(DBReaderTask dbReaderTask);

    LocalDateTime loadUpDateTime(Context context);

    int getDecimalScale();

    SpinnersPositions loadSpinnersPositions(Context context);

    void fillContentValuesForUpdatingColumns(ContentValues contentValues, Currency currency);
}
