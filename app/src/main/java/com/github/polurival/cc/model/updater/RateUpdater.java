package com.github.polurival.cc.model.updater;

import android.content.Context;

import com.github.polurival.cc.RateUpdaterListener;
import com.github.polurival.cc.model.db.DBReaderTask;
import com.github.polurival.cc.model.dto.SpinnersPositions;

import org.joda.time.LocalDateTime;

public interface RateUpdater {

    /**
     * See <a href="http://stackoverflow.com/a/24788257/5349748">Source</a>
     */
    void execute();

    /**
     * Set Activity that get data from RateUpdater
     */
    void setRateUpdaterListener(RateUpdaterListener rateUpdaterListener);

    /**
     * Map used for writing data to the database
     */
    <T> void fillCurrencyMapFromSource(T doc) throws Exception;

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
}
