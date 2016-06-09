package com.github.polurival.cc.model;

/**
 * Created by Polurival
 * on 10.04.2016.
 */
public class Currency {
    private int nominal;
    private double value;

    public Currency(int nominal, double value) {
        this.nominal = nominal;
        this.value = value;
    }

    public int getNominal() {
        return nominal;
    }

    public double getValue() {
        return value;
    }
}
