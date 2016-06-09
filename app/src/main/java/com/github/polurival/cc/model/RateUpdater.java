package com.github.polurival.cc.model;

import org.w3c.dom.Document;

/**
 * Created by Polurival
 * on 10.04.2016.
 */
public interface RateUpdater {

    void setRateUpdaterListener(RateUpdaterListener rateUpdaterListener);

    void fillCurrencyMapFromSource(Document doc);

    String getDescription();

}
