package com.github.polurival.cc.model;

/**
 * Created by Polurival
 * on 10.04.2016.
 */
public final class Currency {
    private String charCode;
    private int nominal;
    private double rate;
    private int switching;

    public Currency(int nominal, double rate) {
        this.nominal = nominal;
        this.rate = rate;
    }

    public Currency(String charCode, int nominal, double rate, int switching) {
        this.charCode = charCode;
        this.nominal = nominal;
        this.rate = rate;
        this.switching = switching;
    }

    public int getNominal() {
        return nominal;
    }

    public double getRate() {
        return rate;
    }

    public int getSwitching() {
        return switching;
    }

    public String getCharCode() {
        return charCode;
    }
}
