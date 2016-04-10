package com.polurival.cuco.strategies;

import org.w3c.dom.NodeList;

import java.util.EnumMap;

/**
 * Created by Polurival
 * on 10.04.2016.
 */
public interface RateUpdater {

    void fillValuteMap(NodeList descNodes);

    EnumMap<ValuteCharCode, Valute> getValuteMap();
}
