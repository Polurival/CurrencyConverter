package com.github.polurival.cc.util;

/**
 * Created by Polurival
 * on 01.06.2016.
 */
public interface Constants {

    String LOG = "com.github.polurival.cc";

    String MENU_HIDE = "menuHide";

    /**
     * See <a href="http://www.cbr.ru/scripts/XML_daily.asp">source</a>
     */
    String CBR_URL = "http://www.cbr.ru/scripts/XML_daily.asp";
    String CURRENCY_NODE_LIST = "Valute";
    String CHAR_CODE_NODE = "CharCode";
    String NOMINAL_NODE = "Nominal";
    String RATE_NODE = "Value";

    /**
     * See <a href="http://finance.yahoo.com/webservice/v1/symbols/allcurrencies/quote?format=json">source</a>
     */
    String YAHOO_URL =
            "http://finance.yahoo.com/webservice/v1/symbols/allcurrencies/quote?format=json";
    String LIST_OBJECT = "list";
    String RESOURCES_ARRAY = "resources";
    String RESOURCE_OBJECT = "resource";
    String FIELDS_OBJECT = "fields";
    String SYMBOL_KEY = "symbol";
    String PRICE_KEY = "price";

    /**
     * For enable or disable all or one currency
     */
    String MULTIPLE = "multiple";
    String SINGLE = "single";

}
