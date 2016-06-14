package com.github.polurival.cc.model;

import android.database.Cursor;

import com.github.polurival.cc.model.db.OnBackPressedListener;

import org.joda.time.LocalDateTime;

/**
 * Created by Polurival
 * on 28.05.2016.
 */
public interface RateUpdaterListener {

    void setOnBackPressedListener(OnBackPressedListener onBackPressedListener);

    void setMenuState(String menuState);

    void setCursor(Cursor cursor);

    void setPropertiesLoaded(boolean isLoaded);

    void setUpDateTime(LocalDateTime upDateTime);

    void initSpinners();

    void initTvDateTime();

    void loadSpinnerProperties();

    void saveDateProperties();

    void readDataFromDB();

    void stopRefresh();

    RateUpdater getRateUpdater();
}
