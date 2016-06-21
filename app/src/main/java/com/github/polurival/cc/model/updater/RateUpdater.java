package com.github.polurival.cc.model.updater;

import com.github.polurival.cc.RateUpdaterListener;

/**
 * Created by Polurival
 * on 10.04.2016.
 */
public interface RateUpdater {


    void setRateUpdaterListener(RateUpdaterListener rateUpdaterListener);


    <T> void fillCurrencyMapFromSource(T doc) throws Exception;


    String getDescription();

}
