package com.github.polurival.cc.model;

import org.w3c.dom.Document;

import java.util.EnumMap;

/**
 * Created by Polurival
 * on 10.04.2016.
 */
public interface RateUpdater {

    void setRateUpdaterListener(RateUpdaterListener rateUpdaterListener);

    void fillCurrencyMap(Document doc);

    EnumMap<CurrencyCharCode, Currency> getCurrencyMap();

    String getDescription();

}
