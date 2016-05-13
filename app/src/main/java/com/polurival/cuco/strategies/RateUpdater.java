package com.polurival.cuco.strategies;

import org.w3c.dom.Document;

import java.util.EnumMap;

/**
 * Created by Polurival
 * on 10.04.2016.
 */
public interface RateUpdater {

    void fillValuteMap(Document doc);

    EnumMap<ValuteCharCode, Valute> getValuteMap();

    String getName();
}
