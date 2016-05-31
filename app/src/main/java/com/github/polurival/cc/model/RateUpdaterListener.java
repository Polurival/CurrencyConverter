package com.github.polurival.cc.model;

import org.joda.time.LocalDateTime;

import java.util.EnumMap;

/**
 * Created by Polurival
 * on 28.05.2016.
 */
public interface RateUpdaterListener {

    void setCurrencyMap(EnumMap<CharCode, Currency> currencyMap);

    void setUpDateTime(LocalDateTime upDateTime);

    void initSpinners();

    void loadSpinnerProperties();

    void saveDateProperties();

    void tvDateTimeSetText();

    RateUpdater getRateUpdater();

    void readDataFromDB();
}
