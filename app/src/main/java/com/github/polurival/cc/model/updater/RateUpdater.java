package com.github.polurival.cc.model.updater;

import com.github.polurival.cc.RateUpdaterListener;

/**
 * Created by Polurival
 * on 10.04.2016.
 */
public interface RateUpdater {

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

}
