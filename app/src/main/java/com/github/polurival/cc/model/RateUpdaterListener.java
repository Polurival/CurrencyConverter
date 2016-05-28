package com.github.polurival.cc.model;

import java.util.EnumMap;

/**
 * Created by Polurival
 * on 28.05.2016.
 */
public interface RateUpdaterListener {

    void setCurrencyMap(EnumMap<CharCode, Currency> currencyMap);

    void initSpinners();

    void loadSpinnerProperties();

}
