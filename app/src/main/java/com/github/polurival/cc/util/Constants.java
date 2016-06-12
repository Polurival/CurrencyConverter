package com.github.polurival.cc.util;

import com.github.polurival.cc.model.CBRateUpdaterTask;
import com.github.polurival.cc.model.CustomRateUpdaterMock;
import com.github.polurival.cc.model.YahooRateUpdaterTask;

/**
 * Created by Polurival
 * on 01.06.2016.
 */
public interface Constants {
    String LOG = "com.github.polurival.cc";

    String CBR_URL = "http://www.cbr.ru/scripts/XML_daily.asp";
    String YAHOO_URL =
            "http://finance.yahoo.com/webservice/v1/symbols/allcurrencies/quote?format=json";

    String CB_RF_RATE_UPDATER = CBRateUpdaterTask.class.getName();
    String YAHOO_RATE_UPDATER = YahooRateUpdaterTask.class.getName();
    String CUSTOM_RATE_UPDATER = CustomRateUpdaterMock.class.getName();
}
