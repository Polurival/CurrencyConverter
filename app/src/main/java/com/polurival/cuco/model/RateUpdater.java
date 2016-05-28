package com.polurival.cuco.model;

import org.w3c.dom.Document;

import java.util.EnumMap;

/**
 * Created by Polurival
 * on 10.04.2016.
 */
public interface RateUpdater {

    void fillCurrencyMap(Document doc);

    EnumMap<CurrencyCharCode, Currency> getCurrencyMap();

    String getName();
}
