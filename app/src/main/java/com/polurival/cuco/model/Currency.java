package com.polurival.cuco.model;

/**
 * Created by Polurival
 * on 10.04.2016.
 */
public class Currency {
    private String nominal;
    private String currencyToRubRate;

    public Currency(String nominal, String value) {
        this.nominal = nominal;
        this.currencyToRubRate = value;
    }

    public String getNominal() {
        return nominal;
    }

    public String getCurrencyToRubRate() {
        return currencyToRubRate;
    }
}
