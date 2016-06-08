package com.github.polurival.cc.model;

import android.database.Cursor;

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

    //void tvDateTimeSetText();

    RateUpdater getRateUpdater();

    void readDataFromDB();

    void stopRefresh();

    void setCursor(Cursor cursor);

    void initTvDateTime();

    void setPropertiesLoaded(boolean isLoaded);
}
