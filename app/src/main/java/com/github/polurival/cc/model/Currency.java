package com.github.polurival.cc.model;

/**
 * Created by Polurival
 * on 10.04.2016.
 */
public class Currency {
    private int nominal;
    private double rate;

    public Currency(int nominal, double rate) {
        this.nominal = nominal;
        this.rate = rate;
    }

    public int getNominal() {
        return nominal;
    }

    public double getRate() {
        return rate;
    }
}
